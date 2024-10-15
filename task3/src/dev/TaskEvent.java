package dev;

public class TaskEvent extends Thread{
	Runnable r;
	
	public TaskEvent(Runnable r) {
		this.r = r;
	}
	
	void post(Runnable r) {
		PompeLair.getSelf().post(this);
	}
	static TaskEvent task() {
		TaskEvent t = (TaskEvent) Thread.currentThread();
		return t;
	}
	void kill() {
		//TODO
	}
	boolean killed() {
		//TODO
		return true;
	}
	
	public void run() {
		r.run();
	}

}
