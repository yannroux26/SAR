package dev;

public class QueueBroker {
	Broker broker;

	QueueBroker(Broker broker) {
		this.broker = broker;
	}

	String name() {
		return broker.getName();
	}

	MessageQueue accept(int port) {
		Channel c = broker.accept(port);
		return new MessageQueue(c);
	}

	MessageQueue connect(String name, int port) {
		Channel c = broker.connect(name, port);
		return new MessageQueue(c);
	}
}
