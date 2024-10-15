package dev;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
	private static BrokerManager self;
	HashMap<String, Broker> brokerList;
	
	static BrokerManager getSelf() {
		return self;
	}
	
	static {
		self = new BrokerManager();
	}
	
	private BrokerManager(){
		this.brokerList= new HashMap<String,Broker>();
	}
	
	public synchronized void add(Broker broker) throws IllegalAccessException {
		String name = broker.getName();
		Broker b = brokerList.get(name);
		if (b!=null) {
			throw new IllegalAccessException("Broker already exists");
		}
		brokerList.put(name,broker);
	}
	
	public synchronized void remove(Broker broker) {
		String name = broker.getName();
		brokerList.remove(name,broker);
	}
	
	public synchronized Broker get(String name) {
		return brokerList.get(name);
	}
	
	
}
