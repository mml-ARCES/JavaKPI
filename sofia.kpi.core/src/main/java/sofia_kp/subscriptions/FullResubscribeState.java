package sofia_kp.subscriptions;

import sofia_kp.SIBResponse;
import sofia_kp.subscriptions.context.SubscriptionContextTracker;

public class FullResubscribeState extends ReSubscribeState {

	@Override
	protected SubscritpionState handleResponse(AutoSubscription context) {
		System.out.println("Stato risottoscrizione con ripristino stato");
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
						SubscriptionContextTracker tracker = (SubscriptionContextTracker)context.getHandler();
						SIBResponse newStateSubs = new SIBResponse(msg);
						
						if("sparql".equals(newStateSubs.queryType)){
							tracker.handleReSubscribeSPARQL(newStateSubs);
						}else{
							tracker.handleReSubscribeRDF(newStateSubs);
						}
						
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
