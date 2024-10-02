package tests;

import java.nio.ByteBuffer;

import dev.Broker;
import dev.Channel;
import dev.DisconnectedException;
import dev.Task;

public class Main {

	public static void main(String[] args) {
		test1();
		// test2();
		// test3();
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
			Broker bclient1 = new Broker("client1");

			Task taserv = new Task(bserv, () -> {
				try {
					Channel channel = bserv.accept(8080);

					byte[] buffer = new byte[256];
					int bytesRead;
					try {
						while ((bytesRead = channel.read(buffer, 0, buffer.length)) > 0) {
							channel.write(buffer, 0, bytesRead);
						}
					} catch (DisconnectedException e) {
						// nothing to do here
					}
					Thread.sleep(1000);

					channel.disconnect();
				} catch (Exception e) {
					System.err.println("Server error: " + e.getMessage());
				}
			});

			Task taclient1 = new Task(bclient1, () -> {
				Channel c = bclient1.connect("serveur", 8080);

				try {
					for (int i = 0; i < 256; i++) {
						// we write the int
						byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
						int nbw = 0;
						while (nbw < 4)
							nbw += c.write(bytes, nbw, bytes.length - nbw);

						// we read the int
						byte[] bytesr = new byte[4];
						int nbr = 0;
						while (nbr < 4)
							nbr += c.read(bytesr, nbr, bytes.length - nbr);

						int repecho = ByteBuffer.wrap(bytesr).getInt();

						assert (repecho == i);
						System.out.println("echo "+repecho);
					}
				} catch (DisconnectedException e3) {
					System.out.println("Le serveur s'est déconnecté");
				}

				c.disconnect();
				System.out.println("déconnection du client");

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
			Broker bserv = new Broker("serveur");
			Broker bclient = new Broker("client");

			Task taserv = new Task(bserv, () -> {
				Channel c = bserv.accept(8080);

				try {
					byte[] bytes = sentence.getBytes();
					int l = bytes.length;
					byte[] bytesr = new byte[l];

					int nbread = c.read(bytesr, 0, l);

					String rep = new String(bytesr);
					if (nbread != l) {
						System.out.println(nbread + " bytes has been read instead of " + l);
					}

					if (!rep.equals(sentence)) {
						System.out.println(
								"The sentence has been misread, read :\n" + rep + "\ninstead of:\n" + sentence);
						// return false;
					}

					System.out.println("serv read :" + rep);

					int nbw = c.write(bytesr, 0, l);

					if (nbw != l) {
						System.out.println(nbw + " bytes has been wrote instead of " + l);
					}

				} catch (DisconnectedException e3) {
					System.out.println("serv is in dandling mode");
				}

				c.disconnect();
				System.out.println("serveur's disconnection");

			});

			Task taclient = new Task(bclient, () -> {
				Channel c = bclient.connect("serveur", 8080);

				try {
					byte[] bytes = sentence.getBytes();
					int l = bytes.length;

					c.write(bytes, 0, l);

					byte[] bytesr = new byte[l];

					c.read(bytesr, 0, l);

					String rep = new String(bytesr);
					assert (rep.equals(sentence));
					System.out.println(rep + " est passé par l echo");

				} catch (DisconnectedException e3) {
					System.out.println("client is in dandling mode");
				}

				c.disconnect();
				System.out.println("client's disconnection");

			});

			try {
				taclient.join();
				taserv.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IllegalAccessException e1) {
			System.out.println("Broker's creation failed");
			e1.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		System.out.println("Test2 OK");
		return true;
	}

	/*
	 * test l'envois d'une phrase plus grande que le circular buffer
	 */
	public static boolean test3() {
		String paragraph = "Quis enim aut eum diligat quem metuat, aut eum a quo se metui putet? Coluntur tamen simulatione dumtaxat ad tempus. Quod si forte, ut fit plerumque, ceciderunt, tum intellegitur quam fuerint inopes amicorum. Quod Tarquinium dixisse ferunt, tum exsulantem se intellexisse quos fidos amicos habuisset, quos infidos, cum iam neutris gratiam referre posset.";

		try {
			Broker bserv = new Broker("serveur");
			Broker bclient = new Broker("client");

			Task taserv = new Task(bserv, () -> {
				Channel c = bserv.accept(8080);

				try {
					byte[] bytes = paragraph.getBytes();
					int l = bytes.length;
					System.out.println("nb bytes a écrire :" + l);
					byte[] bytesr = new byte[l];

					int nbpassage = l / 63;
					int nbr = 0;
					for (int i = 0; i < nbpassage; i++) {
						nbr += c.read(bytesr, i * 63, 63);
						System.out.println(nbr + " bytes lus par le serv");
					}
					if (l % 63 != 0) {
						nbr += c.read(bytesr, nbpassage * 63, l - 63 * nbpassage);
						System.out.println(nbr + " bytes lus par le serv");
					}

					String rep = new String(bytesr);

					if (!rep.equals(paragraph)) {
						System.out.println(
								"The paragraph has been misread, read :\n" + rep + "\ninstead of:\n" + paragraph);
						// return false;
					}
					System.out.println("serv read :" + rep);
					System.out.println("FIN LECTURE PAR LE SERVEUR");

					int nbpassagew = l / 63;
					int nbw = 0;
					for (int i = 0; i < nbpassagew; i++) {
						nbw += c.write(bytesr, i * 63, 63);
						System.out.println(nbw + " bytes écrits par le serv");
					}
					if (l % 63 != 0) {
						nbw += c.write(bytesr, nbpassagew * 63, l - 63 * nbpassagew);
						System.out.println(nbw + " bytes écrits par le serv");
					}

				} catch (DisconnectedException e3) {
					System.out.println("serv is in dandling mode");
				}

				c.disconnect();
				System.out.println("serveur's disconnection");

			});

			Task taclient = new Task(bclient, () -> {
				Channel c = bclient.connect("serveur", 8080);
				System.out.println("the client is connected to the serv");
				try {
					byte[] bytes = paragraph.getBytes();
					int l = bytes.length;

					int nbpassagew = l / 63;
					int nbw = 0;
					for (int i = 0; i < nbpassagew; i++) {
						nbw += c.write(bytes, i * 63, 63);
						System.out.println(nbw + " bytes écrits par le client");
					}
					if (l % 63 != 0)
						nbw += c.write(bytes, nbpassagew * 63, l - 63 * nbpassagew);
					System.out.println(nbw + " bytes écrits par le client");

					System.out.println("FIN ÉCRITURE PAR LE CLIENT");

					byte[] bytesr = new byte[l];

					int nbpassage = l / 63;
					int nbr = 0;
					for (int i = 0; i < nbpassage; i++) {
						nbr += c.read(bytesr, i * 63, 63);
						System.out.println(nbw + " bytes lus par le client");
					}
					if (l % 63 != 0) {
						nbr += c.read(bytesr, nbpassage * 63, l - 63 * nbpassage);
						System.out.println(nbw + " bytes lus par le client");
					}

					String rep = new String(bytesr);
					assert (rep.equals(paragraph));
					System.out.println(rep + " est passé par l echo");

					System.out.println("FIN LECTURE PAR LE CLIENT");

				} catch (DisconnectedException e3) {
					System.out.println("client is in dandling mode");
				}

				c.disconnect();
				System.out.println("client's disconnection");

			});

			try {
				taclient.join();
				taserv.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (IllegalAccessException e1) {
			System.out.println("Broker's creation failed");
			e1.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
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
