package dev;

public class Task extends Thread {
	protected Broker broker;
	Runnable runnable;
	
	public Task(Broker b, Runnable r){
		this.broker = b;
		this.runnable = r;
		this.start();
	}
	
	@Override
	public void run(){
		runnable.run();
	}
	
	public static Broker getBroker(){
		Task task =(Task) Thread.currentThread();
		return task.broker;
	}
}
