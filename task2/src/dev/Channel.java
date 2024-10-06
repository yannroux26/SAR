package dev;

public class Channel {
	Broker broker;
	int port;
	CircularBuffer inbuffer, outbuffer;
	Channel rch;
	String rname;
	private boolean disconnected;
	boolean remotedisconnected;

	protected Channel(Broker broker, int port) {
		this.broker = broker;
		this.port = port;
		this.inbuffer = new CircularBuffer(64);
	}

	void connect(Channel remotechannel, String name) {
		this.rch = remotechannel;
		remotechannel.rch = this;
		this.outbuffer = remotechannel.inbuffer;
		rch.outbuffer = this.inbuffer;
		this.rname = name;
	}

	public synchronized int read(byte[] bytes, int offset, int length)
			throws DisconnectedException {
		if (disconnected)
			throw new DisconnectedException();
		int nbytes = 0;
		try {
			while (nbytes == 0) {
				if (inbuffer.empty()) {
					synchronized (inbuffer) {
						while (inbuffer.empty()) {
							if (disconnected || remotedisconnected)
								throw new DisconnectedException();
							try {
								inbuffer.wait();
							} catch (InterruptedException e) {
								// nothing to do here
							}
						}
					}
				}
				while (nbytes < length && !inbuffer.empty()) {
					byte val = inbuffer.pull();
					bytes[offset + nbytes] = val;
					nbytes++;
				}
				if (nbytes != 0)
					synchronized (outbuffer) {
						outbuffer.notify();
					}
			}
		} catch (DisconnectedException e) {
			if (!disconnected) {
				disconnected = true;
				synchronized (outbuffer) {
					outbuffer.notifyAll();
				}
			}
			throw e;
		}
		return nbytes;
	}

	public synchronized int write(byte[] bytes, int offset, int length)
			throws DisconnectedException {
		if (disconnected)
			throw new DisconnectedException();
		int nbytes = 0;
		while (nbytes == 0) {
				if (outbuffer.full()) {
					synchronized (outbuffer) {
						while (outbuffer.full()) {
							if (disconnected)
								throw new DisconnectedException();
							if (remotedisconnected)
								return length;
							try {
								outbuffer.wait();
							} catch (InterruptedException e) {
								// nothing to do here
							}
						}
					}
				}
				while (nbytes < length && !outbuffer.full()) {
					byte val = bytes[offset + nbytes];
					outbuffer.push(val);
					nbytes++;
				}
				if (nbytes != 0)
					synchronized (outbuffer) {
						outbuffer.notify();
					}
			}
		return nbytes;
	}

	public void disconnect() {
		synchronized (this) {
			if (disconnected)
				return;
			disconnected = true;
		}
		rch.remotedisconnected = true;
		synchronized (outbuffer) {
			outbuffer.notifyAll();
		}
		synchronized (inbuffer) {
			inbuffer.notifyAll();
		}
	}

	public boolean disconnected() {
		return disconnected;
	}
}
