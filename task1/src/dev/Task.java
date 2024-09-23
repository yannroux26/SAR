package dev;

public class Task extends Thread {
	Broker broker;
	Runnable runnable;
	
	Task(Broker b, Runnable r){
		this.broker = b;
		this.runnable = r;
	}
	static Broker getBroker(){
		Task task =(Task) Thread.currentThread();
		return task.broker;
	}
}
