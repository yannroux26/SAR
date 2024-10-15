package dev;

import java.nio.ByteBuffer;

public class MessageQueue {
	Channel c;
	Listener listener;

	MessageQueue(Channel channel) {
		c = channel;
	}

	public interface Listener {
		void received(byte[] msg);
		void closed();
	}

	public void setListener(Listener l) {
		this.listener = l;
		receive();
	}

	public boolean send(byte[] bytes) {
		return send(bytes, 0, bytes.length);
	}

	boolean send(byte[] bytes, int offset, int length) {
		if (c.rch.broker==null)
			return false;
		byte[] bytescopy = bytes.clone();
		Runnable r = () -> {
			try {
				// We write the length of the message first
				byte[] len = ByteBuffer.allocate(4).putInt(length).array();

				int nbw = 0;
				while (nbw < 4)
					nbw += c.write(len, nbw, 4 - nbw);

				// then we write the message
				nbw = 0;
				while (nbw < length)
					nbw += c.write(bytescopy, offset + nbw, length - nbw);
			} catch (DisconnectedException e) {
				// nothing to do here
			}
		};
		new Task(c.broker, r);
		return true;
	}

	public void receive() {
		Runnable r = () -> {
			while (true) {
				try {
					// We read the length of the message first
					byte[] len = new byte[4];

					int nbr = 0;
					while (nbr < 4)
						nbr += c.read(len, nbr, 4 - nbr);

					int length = ByteBuffer.wrap(len).getInt();

					// then we read the message
					byte[] bytes = new byte[length];
					nbr = 0;
					while (nbr < length)
						nbr += c.read(bytes, nbr, length - nbr);

					Runnable rlistener = () -> {
						listener.received(bytes);
					};

					PompeLair.getSelf().post(new TaskEvent(rlistener));

				} catch (DisconnectedException e) {
					// We stop trying to read the message once we get a DisconnectedException
					break;
				}
			}
		};
		new Task(c.broker, r);
	}

	public void close() {
		Runnable rlistener = () -> {
			c.disconnect();
			listener.closed();
		};

		PompeLair.getSelf().post(new TaskEvent(rlistener));
	}

	public boolean closed() {
		return c.disconnected();
	}
}
