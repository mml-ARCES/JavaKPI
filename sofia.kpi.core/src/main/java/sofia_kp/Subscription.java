package sofia_kp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Vector;



public class Subscription extends Thread {
	
	
	protected BufferedReader ft_in;
	protected iKPIC_subscribeHandler2 f_eh;
	protected Socket ft_kpSocket;

	public Subscription(Socket in_sock, iKPIC_subscribeHandler2 hand) throws IOException
	{
		f_eh= hand;
		ft_kpSocket = in_sock;
		InputStream reader = null;
		reader = in_sock.getInputStream();
		ft_in = new BufferedReader(  new InputStreamReader(reader));
	}
	
	@Override
	public void run() {
		SSAP_XMLTools xmlTools=new SSAP_XMLTools(null,null,null);
		String msg_event="";   
		String restOfTheMessage="";

		int buffsize= 4 *1024;

		StringBuilder builder = new StringBuilder();

		char[] buffer = new char[buffsize];
		int charRead =0;
		try
		{
			while (  ( (charRead = ft_in.read(buffer, 0, buffer.length)) != (-1)) || (!restOfTheMessage.isEmpty())  ) 
			{
				if(!restOfTheMessage.equals(""))
				{
					builder.append(restOfTheMessage);
					restOfTheMessage = "";
				}
				
				if(charRead != -1)
				{
					builder.append(buffer, 0 , charRead);
				}

				msg_event = builder.toString();


				if(  msg_event.contains("<SSAP_message>") 
						&& msg_event.contains("</SSAP_message>"))
				{//One or more messages in the same notification
					
					//Trim subscription keep alive
					//TODO: this method is brute force
					int start_msg = msg_event.indexOf('<');
					msg_event = msg_event.substring(start_msg);
					
					int index = msg_event.indexOf("</SSAP_message>") + 15;
					restOfTheMessage = msg_event.substring(index);
					msg_event = msg_event.substring(0, index);
				//	System.out.println("indication: " + msg_event);
					String subID = xmlTools.getSubscriptionID(msg_event);

					// here it starts single message processing and it is possible to launch multiple threads for parallelization

					if(xmlTools.isUnSubscriptionConfirmed(msg_event))
					{
						f_eh.kpic_UnsubscribeEventHandler( subID  );
						return;
					}//if(xmlTools.isUnSubscriptionConfirmed(msg_event))
					else 
					{
						String indSequence = xmlTools.getSSAPmsgIndicationSequence(msg_event);
						if(xmlTools.isRDFNotification(msg_event))
						{
							Vector<Vector<String>> triples_n = new Vector<Vector<String>>();
							triples_n = xmlTools.getNewResultEventTriple(msg_event);
							Vector<Vector<String>> triples_o = new Vector<Vector<String>>();
							triples_o = xmlTools.getObsoleteResultEventTriple(msg_event);
							//int indSequence = xmlTools.getSSAPmsgIndicationSequence(msg_event);
							f_eh.kpic_RDFEventHandler(triples_n, triples_o, indSequence, subID);
						}
						else
						{
							//System.out.println("Notif. " + indSequence + " id = " + id +"\n");
							SSAP_sparql_response resp_new = xmlTools.get_SPARQL_indication_new_results(msg_event);
							SSAP_sparql_response resp_old = xmlTools.get_SPARQL_indication_obsolete_results(msg_event);
							
							f_eh.kpic_SPARQLEventHandler(resp_new, resp_old, indSequence, subID);
					
						}
						//	f_eh.kpic_SIBEventHandler( msg_event );
						//	deb_print("KpCore:EventHandlerThread:event message passed to event handler");   



						if(  restOfTheMessage.contains("<SSAP_message>") 
								&& restOfTheMessage.contains("</SSAP_message>"))//a complete message in the rest of the message
						{	
							//Trim subscription keep alive
							//TODO: this method is brute force
							
							start_msg = restOfTheMessage.indexOf('<');
							restOfTheMessage = restOfTheMessage.substring(start_msg);
							//									deb_print( "KpCore:EventHandlerThread:YES, UnSubscription Confirmed!\n"
							//											+"EVENT HANDLER THREAD:stop");
							//System.out.println( "Rest of the message = " + restOfTheMessage);
							String test = restOfTheMessage.substring(0, restOfTheMessage.indexOf("</SSAP_message>") +15);
							if (xmlTools.isUnSubscriptionConfirmed(test))
							{
								//System.out.println("I should never print this please check an unsubscription in the rest of the message");
								f_eh.kpic_UnsubscribeEventHandler( subID  );
								return;	
							}
						}


						buffer = new char[buffsize];
						charRead = 0;
						msg_event="";
						builder = new StringBuilder();


						//     builder= new StringBuilder();

					}//if(tmp_msg.startsWith("<SSAP_message>") && tmp_msg.endsWith("</SSAP_message>"))

					//else if(  msg_event.contains("</SSAP_message>")) deb_print("***NOT YET RECOGNIZED EVENT MESSAGE:_"+msg_event.replace("\n", "")+"_"); 

				}
			}
			try
			{
				System.out.println("I should not go here untili unsubscribe");
				ft_in.close();
				ft_kpSocket.close();
			}
			catch(Exception e)
			{
				//	err_print("KpCore:startEventHandlerThread:closing connection:Exception:\n"+e);
				e.printStackTrace();
				f_eh.kpic_ExceptionEventHandler(e);
				//this.ERR_EXC_CLOSE_CONN;
			}	
		}

		catch(Exception e)
		{
			//	err_print("KpCore:startEventHandlerThread:reading:Exception:\n"+e);
			e.printStackTrace();
			f_eh.kpic_ExceptionEventHandler(e);
		}
	}

	
}





