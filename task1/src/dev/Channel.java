package dev;

public class Channel {
	BooleanWrapper disconnected;
	private boolean islocaldisconnected;
	CircularBuffer inbuffer, outbuffer;
	private int port;

	protected Channel(Broker broker, int port) {
		
	}
	
//	Channel(CircularBuffer inbuffer, CircularBuffer outbuffer, BooleanWrapper disconnected,int port) {
//		this.inbuffer = inbuffer;
//		this.outbuffer = outbuffer;
//		this.disconnected = disconnected;
//		this.islocaldisconnected = false;
//		this.port= port;
//	}

	public synchronized int read(byte[] bytes, int offset, int length) throws InterruptedException {
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
				disconnect();
		}
		return nb_bytes;
	}

	public int write(byte[] bytes, int offset, int length) throws InterruptedException {
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

	public void disconnect() {
		this.disconnected.value = true;
		this.islocaldisconnected = true;
		Broker broker =Task.getBroker();
		broker.usedPort.remove(port);
	}

	public boolean disconnected() {
		return islocaldisconnected;
	}
}
