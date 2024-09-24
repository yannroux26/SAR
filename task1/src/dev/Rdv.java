package dev;

public class Rdv {
	Broker broker;
	int port;
	Channel channel;

	Rdv(Broker broker, int port) {// accept
		this.broker = broker;
		this.port = port;
		notifyAll();
	}

	public Channel accept() throws InterruptedException {
		while (channel == null)
			wait();
		return channel;
	}

	public synchronized Channel connect() throws InterruptedException {
		if (channel!=null)
			return null;
		
		CircularBuffer cb1 = new CircularBuffer(32);
		CircularBuffer cb2 = new CircularBuffer(32);
		BooleanWrapper disconnected = new BooleanWrapper(false);
		
		Channel achannel = new Channel(cb1, cb2,disconnected,port);
		Channel cchannel = new Channel(cb2, cb1,disconnected,port);
		
		this.channel = achannel;
		notifyAll();
		return cchannel;
	}
}
