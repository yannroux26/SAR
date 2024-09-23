package dev;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BrokerManager {
	Map<String, Broker> brokerList;
	
	BrokerManager(){
		this.brokerList= new ConcurrentHashMap<String,Broker>();
	}
	
	
}
