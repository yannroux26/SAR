package dev;

import java.util.HashMap;

public class QueueBroker {
	Broker broker;
	HashMap<Integer, Task> usedport;

	public QueueBroker(String name) throws IllegalAccessException {
		this.broker = new Broker(name);
		this.usedport = new HashMap<Integer, Task>();
	}

	String name() {
		return broker.getName();
	}

	public interface AcceptListener {
		void accepted(MessageQueue queue);
	}

	public boolean bind(int port, AcceptListener listener) {
		if (usedport.containsKey(port))
			return false;
		Runnable r = () -> {
			MessageQueue mq = new MessageQueue(broker.accept(port));
			Runnable rlistener = () -> {
				unbind(port);
				listener.accepted(mq);
			};
			PompeLair.getSelf().post(new TaskEvent(rlistener));
		};
		Task threadedtask = new Task(this, r);
		usedport.put(port, threadedtask);
		return true;
	};

	public boolean unbind(int port) {
		if (!usedport.containsKey(port))
			return false;
		Runnable r = () -> {
			Task t = usedport.get(port);
			t.interrupt();
			usedport.remove(port);
		};
		new Task(this, r);
		return true;
	}

	public interface ConnectListener {
		void connected(MessageQueue queue);

		void refused();
	}

	public boolean connect(String name, int port, ConnectListener listener) {
		if (BrokerManager.getSelf().get(name) == null)
			return false;
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
