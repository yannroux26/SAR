package dev;

import java.util.LinkedList;

class PompeLair extends Thread {
	LinkedList<Runnable> queue;
	private static PompeLair self;
	
	static PompeLair getSelf() {
		return self;
	}
	
	static {
		self = new PompeLair();
	}
	
	private PompeLair() {
		queue = new LinkedList<Runnable>();
		this.start();
	}

	public synchronized void run() {
		Runnable r;
		while (true) {
			r = queue.poll();
			while (r != null) {
				r.run();
				r = queue.poll();
			}
			sleep();
		}
	}

	public synchronized void post(Runnable r) {
		queue.addLast(r);
		notify();
	}

	private void sleep() {
		try {
			wait();
		} catch (InterruptedException ex) {
			// nothing to do here.
		}
	}
}
