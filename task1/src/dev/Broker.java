package dev;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Broker {
	BrokerManager bmanager;
	String name;
	Map<Integer, Rdv> rdvmap;
	ArrayList<Integer> usedPort;

	Broker(String name, BrokerManager brokermanager) {
		this.name = name;
		this.rdvmap = new HashMap<Integer, Rdv>();
		this.usedPort = new ArrayList<Integer>();
		this.bmanager = brokermanager;
		brokermanager.brokerList.put(name, this);
	}

	Channel accept(int port) throws InterruptedException {
		if (!addport(port))
			return null;
		Rdv rdv = new Rdv(this, port);
		rdvmap.put(port, rdv);
		Channel c = rdv.accept();
		rdvmap.remove(port, rdv);
		return c;
	}

	Channel connect(String name, int port) throws InterruptedException {
		if (!addport(port))
			return null;
		Broker destbroker = bmanager.brokerList.get(name);
		if (destbroker == null)
			return null;

		while (!destbroker.rdvmap.containsKey(port))
			wait();	
		
		Rdv rdv = destbroker.rdvmap.get(port);
		return rdv.connect();
	}

	private synchronized boolean addport(int port) {
		if (this.usedPort.contains(port))
			return false;
		usedPort.add(port);
		return true;
	}
}