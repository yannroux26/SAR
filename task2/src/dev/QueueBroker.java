package dev;

public class QueueBroker {
	Broker broker;

	public QueueBroker(Broker broker) {
		this.broker = broker;
	}

	String name() {
		return broker.getName();
	}

	public MessageQueue accept(int port) {
		Channel c = broker.accept(port);
		return new MessageQueue(c);
	}

	public MessageQueue connect(String name, int port) {
		Channel c = broker.connect(name, port);
		return new MessageQueue(c);
	}
}
