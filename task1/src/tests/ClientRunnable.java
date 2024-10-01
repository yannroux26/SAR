package tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.Broker;
import dev.Channel;
import dev.Task;

public class ClientRunnable implements Runnable {
	int port;
	String servname;
	Broker broker;
	
	public ClientRunnable(Broker b, String servname, int port) {
		this.broker = b;
		this.port = port;
		this.servname = servname;
	}

	@Override
	public void run() {
		for (int i = 1; i < 255; i++) {
			try {
				byte[] tab = convertIntToByteArray(i);
				Channel discord = broker.connect(servname, port);
				int n = discord.write(tab, 0, tab.length);

				System.out.println("Client a écrit");
				
				assert (n == tab.length);

				byte[] tabRep = null;
				int m = discord.read(tabRep, 0, tab.length);
				
				System.out.println("Client a lu");
				
				assert (m == tabRep.length);
				assert (tabRep == tab);

				discord.disconnect();

				assert (discord.disconnected());
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}

		}
		System.out.println("that's all folks");
	}
	
	private static byte[] convertIntToByteArray ( final int i ) throws IOException {      
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(bos);
	    dos.writeInt(i);
	    dos.flush();
	    return bos.toByteArray();
	}

}