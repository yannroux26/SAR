package dev;

public class Rdv {
	Broker broker1;
	Broker broker2;
	int port;
	Channel channel;
	boolean accepting;

	Rdv(Broker broker, int port) {// accept
		this.broker1 = broker;
		this.port = port;
		this.accepting = false;
		notifyAll();
	}

	Rdv(Broker broker1, Broker broker2, int port) {// connect
		this.broker1 = broker1;
		this.broker2 = broker2;
		this.port = port;
		this.accepting = false;
		notifyAll();
	}

	public Channel accept() throws InterruptedException {
		this.accepting = true;
		notifyAll();
		while (channel == null)
			wait();
		return channel;
	}

	public Channel connect() throws InterruptedException {
		while ((!broker2.rdvmap.containsKey(port)) || (!broker2.rdvmap.get(port).accepting))
			wait();
		
		CircularBuffer cb1 = new CircularBuffer(32);
		CircularBuffer cb2 = new CircularBuffer(32);
		
		Channel achannel = new Channel(cb1, cb2);
		Channel cchannel = new Channel(cb2, cb1);
		
		broker2.rdvmap.get(port).channel = achannel;
		notifyAll();
		return cchannel;
	}
}
