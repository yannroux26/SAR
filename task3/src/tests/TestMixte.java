package tests;

import java.util.concurrent.Semaphore;

import dev.MessageQueue;
import dev.MessageQueue.Listener;
import dev.QueueBroker;
import dev.QueueBroker.AcceptListener;
import dev.QueueBroker.ConnectListener;

public class TestMixte {
	public static void main(String[] args) throws InterruptedException {
		boolean t1 = test1();
		assert (t1);
		System.out.println("test 1 ok");
		boolean t2 = test2();
		assert (t2);
		System.out.println("test 2 ok");
//		boolean t3 = test3();
//		assert (t3);
		System.out.println("That's all folks");
	}

	/*
	 * test la connection
	 * 
	 * @return true si le test réussi
	 */
	public static boolean test1() throws InterruptedException {
		Semaphore sm = new Semaphore(-1);
		try {
			QueueBroker qbserv = new QueueBroker("serveur1");
			QueueBroker qbclient = new QueueBroker("client1");
			int port = 8080;

			qbserv.bind(port, new AcceptListener() {

				@Override
				public void accepted(MessageQueue queue) {
					System.out.println("connection accepted");
					sm.release();
				}
			});
			qbclient.connect("serveur1", port, new ConnectListener() {

				@Override
				public void refused() {
					System.out.println("Connection refused");
					sm.release();
				}

				@Override
				public void connected(MessageQueue queue) {
					System.out.println("connected");
					sm.release();
				}
			});
		} catch (IllegalAccessException e) {
			System.out.println("There is a problem in the broker creation");
			e.printStackTrace();
		}
		sm.acquire();
		return true;
	}

	public static boolean test2() throws InterruptedException {
		Semaphore sm = new Semaphore(-1);
		try {
			QueueBroker qbserv = new QueueBroker("serveur2");
			QueueBroker qbclient = new QueueBroker("client2");
			int port = 8081;

			qbclient.connect("serveur2", port, new ConnectListener() {

				@Override
				public void refused() {
					System.out.println("Connection refused");
				}

				@Override
				public void connected(MessageQueue queue) {
					System.out.println("connected");
					queue.setListener(new Listener() {

						@Override
						public void received(byte[] msg) {
							System.out.println("echo" + new String(msg));
							queue.close();
						}

						@Override
						public void closed() {
							System.out.println("Client'connection closed");
							sm.release();
						}
					});
					System.out.println("avant send");
					byte[] msg = "This sentence should be printed on the shell".getBytes();
					if (!queue.send(msg)) {
						System.out.println("listener pas instanciés");
					}
					System.out.println("après send");
				}
			});
			

			qbserv.bind(port, new AcceptListener() {

				@Override
				public void accepted(MessageQueue queue) {
					System.out.println("connection accepted");
					queue.setListener(new Listener() {

						@Override
						public void received(byte[] msg) {
							System.out.println("serv received");
							queue.send(msg);
							queue.close();
						}

						@Override
						public void closed() {
							System.out.println("server's connection closed");
							sm.release();
						}
					});
				}
			});


		} catch (IllegalAccessException e) {
			System.out.println("There is a problem in the broker creation");
			e.printStackTrace();
		}
		sm.acquire();

		return true;
	}
}
