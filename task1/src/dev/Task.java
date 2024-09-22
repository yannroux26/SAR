package dev;

public class Task extends Thread {
	Broker broker;
	Runnable runnable;
	
	Task(Broker b, Runnable r){
		this.broker = b;
		this.runnable = r;
	}
	Broker getBroker(){//à mettre en static
		return this.broker;
	}
}
