package dev;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Broker {
	BrokerManager bmanager;
	String name;
	Map<Integer, Rdv> rdvmap;
	ArrayList<Integer> usedPort;

	public Broker(String name) throws IllegalAccessException {
		this.name = name;
		rdvmap = new HashMap<Integer, Rdv>();
		usedPort = new ArrayList<Integer>();
		bmanager = BrokerManager.getSelf();
		bmanager.add(this);
	}

	public Channel connect(String name, int port) {
		Broker b = bmanager.get(name);
		if (b == null)
			return null;
		return b._connect(this.port);
	}

	public Channel accept(int port) {
		Rdv rdv = null;
		synchronized (rdvmap) {
			rdv = rdvmap.get(port);
			if (rdv != null)
				throw new IllegalStateException("port " + port + " already used");
			rdv = new Rdv();
			rdvmap.put(port, rdv);
			rdvmap.notifyAll();
		}
		Channel ch;
		ch = rdv.accept(this, port);
	}

	private Channel _connect(Broker b, int port) {
		Rdv rdv = null;
		synchronized (rdvmap) {
			rdv = rdvmap.get(port);
			while (rdv == null) {
				try {
					rdv.wait();
				} catch (InterruptedException e) {
					// nothing to do here
				}
				rdv = rdvmap.get(port);
			}
			rdvmap.remove(rdv);
		}
		return rdv.connect(b,port);
	}

	public String getName() {
		return name;
	}

	//	public Channel accept(int port) throws InterruptedException {
//		if (!addport(port))
//			return null;
//		Rdv rdv = new Rdv(this, port);
//		rdvmap.put(port, rdv);
//		Channel c = rdv.accept();
//		rdvmap.remove(port, rdv);
//		return c;
//	}
//
//	public synchronized Channel connect(String name, int port) throws InterruptedException {
//		System.out.println("1");
//		if (!addport(port))
//			return null;
//		Broker destbroker = bmanager.brokerList.get(name);
//		if (destbroker == null)
//			return null;
//
//		System.out.println("2");
//		while (!destbroker.rdvmap.containsKey(port))
//			System.out.println("3");
//		wait();
//
//		System.out.println("4");
//		Rdv rdv = destbroker.rdvmap.get(port);
//		System.out.println("5");
//		return rdv.connect();
//	}
//
//	private synchronized boolean addport(int port) {
//		if (this.usedPort.contains(port))
//			return false;
//		usedPort.add(port);
//		return true;
//	}

}
