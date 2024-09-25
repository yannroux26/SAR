package dev;

public class Rdv {
	Channel ac;
	Channel cc;
	Broker ab;
	Broker cb;
	int port;

	private void _wait() {
		while (ac == null || cc == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// nothing to do here
			}
		}
	}

	synchronized Channel connect(Broker cb,int port) {
		this.cb = cb;
		cc = new Channel(cb, port);
		if (ac!=null) {
			ac.connect(cc,cb.getName());
			notify();
		}else 
			_wait();
		return cc;
	}

	synchronized Channel accept(Broker ab, int port) {
		this.cb = cb;
		cc = new Channel(cb, port);
		if (cc!=null) {
			ac.connect(cc,ac.getName());
		} else 
			_wait();
		return ac;
	}
	
//	Rdv(Broker broker, int port) {// acept
//		this.broker = broker;
//		this.port = port;
//	}
//
//	public synchronized Channel acept() throws InterruptedException {
//		System.out.println("AAAAAAAAAAAAAAA");
//		notifyAll();
//		while (channel == null)
//			wait();
//		System.out.println("AAAAAAAAAAAAAAAAAAA2");
//		return channel;
//	}
//
//	public synchronized Channel connect() throws InterruptedException {
//		System.out.println("BBBBBBBBBBBBBBBBBBBB");
//		if (channel!=null)
//			return null;
//		
//		CircularBuffer cb1 = new CircularBuffer(32);
//		CircularBuffer cb2 = new CircularBuffer(32);
//		BooleanWrapper disconnected = new BooleanWrapper(false);
//		
//		Channel achannel = new Channel(cb1, cb2,disconnected,port);
//		Channel cchannel = new Channel(cb2, cb1,disconnected,port);
//		
//		this.channel = achannel;
//		notifyAll();
//		return cchannel;
//	}
}
