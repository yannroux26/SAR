package tests;

import dev.Broker;
import dev.BrokerManager;
import dev.Task;

public class Main {

	public static void main(String[] args) {
		BrokerManager bmanager = new BrokerManager();
		Broker bserv = new Broker("serveur", bmanager);
		Broker bclient1 = new Broker("client1", bmanager);

		Runnable thserv = new ServeurRunnable(bserv, 8080);
		Runnable thclient1 = new ClientRunnable(bclient1, "serveur", 8080);
		
		Task taserv = new Task(bserv,thserv);
		Task taclient1 = new Task(bclient1,thclient1);
		

		taserv.start();
		taclient1.start();

		try {
			taclient1.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("That's all folks");
	}
}

/*
 * For the test, you will implement a simple echo server. An echo server accepts
 * the connect of any number of clients and echoes back anything a client sends.
 * 
 * A test client will loop over and over the following steps: - connect to the
 * server - send a sequence of bytes, representing the number from 1 to 255 -
 * test that these bytes are echoed properly by the server - disconnect.
 * 
 * You will test with one server and several clients.
 * 
 * At the end of the classwork, you will make sure to git-add and git-commit and
 * you will surrender your git repository as an archive (zip or tar). Then, as
 * homework, you will finish the given task. You will surrender your git before
 * the next classroom lecture, as an archive (zip or tar).
 */
