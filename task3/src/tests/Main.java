package tests;

import java.nio.ByteBuffer;

import dev.Broker;
import dev.Channel;
import dev.DisconnectedException;
import dev.MessageQueue;
import dev.QueueBroker;
import dev.Task;

public class Main {

	public static void main(String[] args) {
		boolean t1 = test1();
		assert (t1);
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
		}
		boolean t2 = test2();
		assert (t2);
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
		}
		boolean t3 = test3();
		assert (t3);
		System.out.println("That's all folks");
	}

	/*
	 * test l envoi des nb de 0 à 256
	 * 
	 * @return true si le test réussi
	 */
	public static boolean test1() {
		try {
			Broker bserv = new Broker("serveur");
			QueueBroker qbserv = new QueueBroker(bserv);
			Broker bclient = new Broker("client1");
			QueueBroker qbclient = new QueueBroker(bclient);

			Task taserv = new Task(qbserv, () -> {

				MessageQueue msgq = qbserv.accept(8080);
				System.out.println("server is accepting");

				try {
					for (int i = 0; i < 256; i++) {
						// the server receive the message
						byte[] buffer;
						buffer = msgq.receive();

						// the server echo send back the message
						msgq.send(buffer, 0, buffer.length);
					}

					// disconnection
					msgq.close();
					System.out.println("server is disconnected");

				} catch (DisconnectedException e) {
					// the client isn't suppose to be disconnected during this
					e.printStackTrace();
				}
			});

			Task taclient1 = new Task(qbclient, () -> {
				MessageQueue msgq = qbclient.connect("serveur", 8080);
				System.out.println("client is connecting");

				try {
					for (int i = 0; i < 256; i++) {
						// we write the int
						byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
						msgq.send(bytes, 0, 4);

						// we read the int
						byte[] bytesr = msgq.receive();

						int repecho = ByteBuffer.wrap(bytesr).getInt();
						assert (repecho == i);
						System.out.println("echo " + repecho);
					}

					msgq.close();
					System.out.println("déconnection du client");

				} catch (DisconnectedException e3) {
					System.out.println("Le serveur s'est déconnecté");
				}
			});

			try {
				taclient1.join();
				taserv.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IllegalAccessException e1) {
			System.out.println("Broker's creation failed");
			e1.printStackTrace();
			return false;
		}

		System.out.println("Test1 OK");
		return true;
	}

	/*
	 * test l'envois d'une phrase plus courte que le circular buffer
	 */
	public static boolean test2() {
		String sentence = "this sentence should be print on the shell";

		try {
			Broker bserv = new Broker("serveur2");
			QueueBroker qbserv = new QueueBroker(bserv);
			Broker bclient = new Broker("client2");
			QueueBroker qbclient = new QueueBroker(bclient);

			Task taserv = new Task(qbserv, () -> {

				MessageQueue msgq = qbserv.accept(8081);
				System.out.println("server is accepting");

				try {
					// the server receive the message
					byte[] buffer;
					buffer = msgq.receive();

					// the server echo send back the message
					msgq.send(buffer, 0, buffer.length);

					// disconnection
					msgq.close();
					System.out.println("server is disconnected");

				} catch (DisconnectedException e) {
					// the client isn't suppose to be disconnected during this
					e.printStackTrace();
				}
			});

			Task taclient = new Task(qbclient, () -> {
				MessageQueue msgq = qbclient.connect("serveur2", 8081);
				System.out.println("client is connecting");

				try {
					// we write the sentence
					byte[] bytes = sentence.getBytes();
					msgq.send(bytes, 0, bytes.length);

					// we read the sentence
					byte[] bytesr = msgq.receive();

					String repecho = new String(bytesr);
					assert (repecho == sentence);
					System.out.println("echo " + repecho);

					msgq.close();
					System.out.println("déconnection du client");

				} catch (DisconnectedException e3) {
					System.out.println("Le serveur s'est déconnecté");
				}
			});

			try {
				taclient.join();
				taserv.join();
			} catch (InterruptedException e) {
				// the server shouldn't disconnect here
				e.printStackTrace();
			}

		} catch (IllegalAccessException e1) {
			System.out.println("Broker's creation failed");
			e1.printStackTrace();
			return false;
		}

		System.out.println("Test2 OK");
		return true;
	}

	/*
	 * test l'envois d'une phrase plus grande que le circular buffer
	 */
	public static boolean test3() {
		String sentence = "\n"
				+ "\n"
				+ "Quam ob rem circumspecta cautela observatum est deinceps et cum edita montium petere coeperint grassatores, loci iniquitati milites cedunt. ubi autem in planitie potuerint reperiri, quod contingit adsidue, nec exsertare lacertos nec crispare permissi tela, quae vehunt bina vel terna, pecudum ritu inertium trucidantur.\n"
				+ "\n"
				+ "Advenit post multos Scudilo Scutariorum tribunus velamento subagrestis ingenii persuasionis opifex callidus. qui eum adulabili sermone seriis admixto solus omnium proficisci pellexit vultu adsimulato saepius replicando quod flagrantibus votis eum videre frater cuperet patruelis, siquid per inprudentiam gestum est remissurus ut mitis et clemens, participemque eum suae maiestatis adscisceret, futurum laborum quoque socium, quos Arctoae provinciae diu fessae poscebant.\n"
				+ "\n"
				+ "Et quia Montius inter dilancinantium manus spiritum efflaturus Epigonum et Eusebium nec professionem nec dignitatem ostendens aliquotiens increpabat, qui sint hi magna quaerebatur industria, et nequid intepesceret, Epigonus e Lycia philosophus ducitur et Eusebius ab Emissa Pittacas cognomento, concitatus orator, cum quaestor non hos sed tribunos fabricarum insimulasset promittentes armorum si novas res agitari conperissent.";

		try {
			Broker bserv = new Broker("serveur3");
			QueueBroker qbserv = new QueueBroker(bserv);
			Broker bclient = new Broker("client3");
			QueueBroker qbclient = new QueueBroker(bclient);

			Task taserv = new Task(qbserv, () -> {

				MessageQueue msgq = qbserv.accept(8083);
				System.out.println("server is accepting");

				try {
					// the server receive the message
					byte[] buffer;
					buffer = msgq.receive();

					// the server echo send back the message
					msgq.send(buffer, 0, buffer.length);

					// disconnection
					msgq.close();
					System.out.println("server is disconnected");

				} catch (DisconnectedException e) {
					// the client isn't suppose to be disconnected during this
					e.printStackTrace();
				}
			});

			Task taclient = new Task(qbclient, () -> {
				MessageQueue msgq = qbclient.connect("serveur3", 8083);
				System.out.println("client is connecting");

				try {
					// we write the sentence
					byte[] bytes = sentence.getBytes();
					msgq.send(bytes, 0, bytes.length);

					// we read the sentence
					byte[] bytesr = msgq.receive();

					String repecho = new String(bytesr);
					assert (repecho == sentence);
					System.out.println("echo " + repecho);

					msgq.close();
					System.out.println("déconnection du client");

				} catch (DisconnectedException e3) {
					System.out.println("Le serveur s'est déconnecté");
				}
			});

			try {
				taclient.join();
				taserv.join();
			} catch (InterruptedException e) {
				// the server shouldn't disconnect here
				e.printStackTrace();
			}

		} catch (IllegalAccessException e1) {
			System.out.println("Broker's creation failed");
			e1.printStackTrace();
			return false;
		}

		System.out.println("Test3 OK");
		return true;
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
