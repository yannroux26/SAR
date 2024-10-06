package dev;

import java.nio.ByteBuffer;

public class MessageQueue {
	Channel c;

	MessageQueue(Channel channel) {
		c = channel;
	}

	void send(byte[] bytes, int offset, int length) throws DisconnectedException {
			// We write the length of the message first
			byte[] len = ByteBuffer.allocate(4).putInt(length).array();

			int nbw = 0;
			while (nbw < 4)
				nbw += c.write(len, nbw, 4 - nbw);

			// then we write the message
			nbw = 0;
			while (nbw < length)
				nbw += c.write(bytes, offset + nbw, length - nbw);
	}

	byte[] receive() throws DisconnectedException {
			// We read the length of the message first
			byte[] len = new byte[4];

			int nbr = 0;
			while (nbr < 4)
				nbr += c.read(len, nbr, 4 - nbr);
			
			int length = ByteBuffer.wrap(len).getInt();
			
			// then we read the message
			byte[] bytes = new byte[4];
			nbr = 0;
			while (nbr < length)
				nbr += c.read(bytes, nbr, length - nbr);
			
			return bytes;
	}

	void close() {
		c.disconnect();
	}

	boolean closed() {
		return c.disconnected();
	}
}
