package dev;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Broker {
	BrokerManager bmanager;
	String name;
	Map<Integer, Rdv> rdvmap;

	public Broker(String name) throws IllegalAccessException {
		this.name = name;
		rdvmap = new HashMap<Integer, Rdv>();
		bmanager = BrokerManager.getSelf();
		bmanager.add(this);
	}

	//Récupère l'autre broker et attends qu'il créé le rdv
	public Channel connect(String name, int port) {
		Broker b = bmanager.get(name);
		if (b == null)
			return null;
		return b._connect(this, port);
	}

	// Créé le rdv et notify l'autre broker
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
		return ch;
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

}
