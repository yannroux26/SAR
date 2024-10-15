package dev;

public class Task extends Thread {
	protected Broker broker;
	protected QueueBroker qbroker;
	Runnable runnable;
	
	public Task(Broker b, Runnable r){
		this.broker = b;
		this.runnable = r;
		this.start();
	}
	
	public Task(QueueBroker qb, Runnable r){
		this.qbroker = qb;
		this.runnable = r;
		this.start();
	}
	
	@Override
	public void run(){
		runnable.run();
	}
	
	public static Broker getBroker(){
		return getTask().broker;
	}
	public static QueueBroker getQueueBroker(){
		return getTask().qbroker;
	}
	
	public static Task getTask(){
		return(Task) Thread.currentThread();
		}
}