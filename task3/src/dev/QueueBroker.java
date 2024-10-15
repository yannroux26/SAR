package dev;

import java.util.ArrayList;

public class QueueBroker {
	Broker broker;
	ArrayList<Integer> usedport;

	public QueueBroker(String name) throws IllegalAccessException {
		this.broker = new Broker(name);
		this.usedport = new ArrayList<Integer>();
	}

	String name() {
		return broker.getName();
	}

	public interface AcceptListener {
		void accepted(MessageQueue queue);
	}

	public boolean bind(int port, AcceptListener listener) {
		// TODO S'OCCUPER DES RETOURS
		Runnable r = () -> {
			MessageQueue mq = new MessageQueue(broker.accept(port));
			Runnable rlistener = () -> {
				listener.accepted(mq);
			};
			PompeLair.getSelf().post(new TaskEvent(rlistener));
		};
		new Task(this, r);
		return true;
	};

	public boolean unbind(int port) {
		//TODO A CHECK
		Runnable r = () -> {
			synchronized (this.broker.rdvmap) {
				this.broker.rdvmap.remove(port);
			}
		};
		PompeLair.getSelf().post(r);
		return true;
	}

	public interface ConnectListener {
		void connected(MessageQueue queue);

		void refused();
	}

	public boolean connect(String name, int port, ConnectListener listener) {
		if (BrokerManager.getSelf().get(name) == null)
			return false;
		// TODO S OCCUPER DES RETOURS
		Runnable r = () -> {
				MessageQueue mq = new MessageQueue(broker.connect(name, port));
				TaskEvent rlistener = new TaskEvent(() -> {
				if (mq.c != null)
					listener.connected(mq);
				else
					listener.refused();
			});
				PompeLair.getSelf().post(rlistener);
		};

		new Task(this, r);
		return true;
	};
}
