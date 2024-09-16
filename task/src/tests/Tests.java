package tests;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import dev.Broker;
import dev.Channel;

public class Tests {
	private static byte[] convertIntToByteArray ( final int i ) throws IOException {      
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    DataOutputStream dos = new DataOutputStream(bos);
	    dos.writeInt(i);
	    dos.flush();
	    return bos.toByteArray();
	}
	
	public static void main(String[] args) {
		Broker sBroker  = new Broker("sBroker");
		Broker cBroker  = new Broker("cBroker");
		
		
		for (int i=1;i<255;i++) {
			
			try {
				byte[] tab = convertIntToByteArray(i);
				Channel discord = cBroker.connect("sBroker", 8080);
				int n = discord.write(tab, 0, tab.length);
				
				assert(n==tab.length);
				
				byte[] tabRep = null;
				int m = discord.read(tabRep, 0, tab.length);
				
				assert(m==tabRep.length);			
				assert(tabRep==tab);
				
				discord.disconnect();
				
				assert(discord.disconnected());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 
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
