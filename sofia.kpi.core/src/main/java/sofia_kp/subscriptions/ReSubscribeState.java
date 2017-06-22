package sofia_kp.subscriptions;

import java.io.PrintWriter;

import sofia_kp.SSAP_XMLTools;

class ReSubscribeState implements SubscritpionState{

	protected SSAP_XMLTools xmlTools;

	ReSubscribeState() {
		xmlTools=new SSAP_XMLTools(null,null,null);
	}
	@Override
	public SubscritpionState handle(AutoSubscription context) {
		SubscritpionState nextState = this;
		
		PrintWriter printWriter = context.getPrintWriter();
		printWriter.println(context.getQuery());
		printWriter.flush();
		nextState = handleResponse(context);
		
		return nextState;
	}

	protected SubscritpionState handleResponse(AutoSubscription context) {
		System.out.println("Stato risottoscrizione");
		SubscritpionState nextState = this;
		int buffsize= 4 *1024;

		StringBuilder builder = new StringBuilder();


		char[] buffer = new char[buffsize];
		String msg = "";
		int charRead =0;

		try
		{
			while ((charRead = context.getReader().read(buffer, 0, buffer.length)) != (-1)) 
			{
				builder.append(buffer, 0 , charRead);

				msg = builder.toString();
				
				if(msg.contains("<SSAP_message>") && msg.contains(  "</SSAP_message>") )
				{	
					
					msg = msg.substring(msg.indexOf('<'));
					msg = msg.substring(0, msg.lastIndexOf('>')+1);
					if(this.xmlTools.isSubscriptionConfirmed(msg))
					{
						nextState = context.createConnectionState();
						break;
					}
					else 
					{ 
						System.out.println("[90] UNKNOW MESSAGE:"+msg);
						nextState = context.createJoiningState();
						break;
					}

				}
			}     

		}catch(Exception e)
		{
			nextState = context.createReconnectionState();
		}

		return nextState;
	}

}
