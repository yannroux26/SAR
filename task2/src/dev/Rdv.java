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
		this.ab = ab;
		ac = new Channel(ab, port);
		if (cc!=null) {
			ac.connect(cc,ab.getName());
		} else 
			_wait();
		return ac;
	}
}
