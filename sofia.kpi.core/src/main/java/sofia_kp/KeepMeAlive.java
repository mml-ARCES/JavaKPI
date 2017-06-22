package sofia_kp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Vector;
/**
 * A thread that keeps the connection alive with the SIB. 
 * This functionality is used to check the present of this client. If SIB
 * loses connection then it assumes that this client is died an therefore execute
 * its last will.
 * @author Cristiano
 *
 */
public class KeepMeAlive extends Thread {

	private Socket in_sock;
	private BufferedReader ft_in;

	public KeepMeAlive(Socket in_sock) throws IOException {
		super("KeepMeAlive");
		this.in_sock = in_sock;
		InputStream reader = null;
		reader = in_sock.getInputStream();

		ft_in = new BufferedReader(  new InputStreamReader(reader));
	}
	
	@Override
	public void run() {

		int charRead =0;
		try
		{
			while(!this.isInterrupted()){
			while (  ( (charRead = ft_in.read()) != (-1))) 
			{
			}
			}
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
