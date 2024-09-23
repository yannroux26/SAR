package dev;

public class Channel {
	BooleanWrapper disconnected;
	private boolean islocaldisconnected;
	CircularBuffer inbuffer;
	CircularBuffer outbuffer;

	Channel(CircularBuffer inbuffer, CircularBuffer outbuffer, BooleanWrapper disconnected) {
		this.inbuffer = inbuffer;
		this.outbuffer = outbuffer;
		this.disconnected = disconnected;
		this.islocaldisconnected = false;
	}

	synchronized int read(byte[] bytes, int offset, int length) throws InterruptedException {
		int nb_bytes = 0;
		while (nb_bytes < length) {
			if (inbuffer.empty())
				wait();
			if (islocaldisconnected)
				throw new InterruptedException();
			else {
				bytes[offset + nb_bytes] = inbuffer.pull();
				notifyAll();
			}
			if(disconnected.value && inbuffer.empty())
				islocaldisconnected = true;
		}
		return nb_bytes;
	}

	int write(byte[] bytes, int offset, int length) throws InterruptedException {
		int nb_bytes = 0;
		while (nb_bytes < length) {
			if (inbuffer.full())
				wait();
			else if (islocaldisconnected)
				throw new InterruptedException();
			else {
				inbuffer.push(bytes[offset + nb_bytes]);
				notifyAll();
			}
		}
		return nb_bytes;
	}

	void disconnect() {
		this.disconnected.value = true;
		this.islocaldisconnected = true;
	}

	boolean disconnected() {
		return islocaldisconnected;
	}
}
