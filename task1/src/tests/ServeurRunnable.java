package tests;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.Broker;
import dev.Channel;
import dev.Task;

public class ServeurRunnable implements Runnable {
	int port;
	Broker broker;

	public ServeurRunnable(Broker b, int port) {
		this.broker = b;
		this.port = port;
	}

	@Override
	public void run() {
		for (int i = 1; i < 255; i++) {
			try {
				int length = ((byte[]) convertIntToByteArray(i)).length;
				Channel discord = broker.accept(port);
				byte[] tabRep = null;
				int nbbytesread = discord.read(tabRep, 0, length);
				
				System.out.println("serv a lu");
				
				assert (nbbytesread == length);

				int nbbyteswrite = discord.write(tabRep, 0, length);
				System.out.println("serv a ecrit");	
				
				assert (nbbyteswrite == length);

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