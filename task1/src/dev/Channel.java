package dev;

public class Channel {
	boolean disconnected;
	CircularBuffer inbuffer;
	CircularBuffer outbuffer;

	Channel(CircularBuffer inbuffer, CircularBuffer outbuffer) {
		this.inbuffer = inbuffer;
		this.outbuffer = outbuffer;
		this.disconnected = false;
	}

	synchronized int read(byte[] bytes, int offset, int length) throws InterruptedException {
		int nb_bytes = 0;
		while (nb_bytes < length) {
			if (inbuffer.empty())
				wait();
			else if (disconnected)
				return -1;
			else {
				bytes[offset + nb_bytes] = inbuffer.pull();
				notifyAll();
			}
		}
		return nb_bytes;
	}

	int write(byte[] bytes, int offset, int length) throws InterruptedException {
		int nb_bytes = 0;
		while (nb_bytes < length) {
			if (inbuffer.full())
				wait();
			else if (disconnected)
				return -1;
			else {
				inbuffer.push(bytes[offset + nb_bytes]);
				notifyAll();
			}
		}
		return nb_bytes;
	}

	void disconnect() {
		this.disconnected = true;
	}

	boolean disconnected() {
		return disconnected;
	}
}
