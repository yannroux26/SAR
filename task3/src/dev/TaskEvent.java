package dev;

public class TaskEvent extends Thread {
	Runnable r;
	boolean alive;

	public TaskEvent(Runnable r) {
		this.r = r;
		alive = true;
	}

	void post(Runnable r) {
		if (alive)
			PompeLair.getSelf().post(this);
	}

	static TaskEvent task() {
		TaskEvent t = (TaskEvent) Thread.currentThread();
		return t;
	}

	void kill() {
		alive = false;
	}

	boolean killed() {
		return !alive;
	}

	public void run() {
		r.run();
	}

}
