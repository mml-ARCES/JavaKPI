package sofia_kp.subscriptions;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import sofia_kp.SIBResponse;
import sofia_kp.iKPIC;
import sofia_kp.iKPIC_subscribeHandler2;
import sofia_kp.subscriptions.context.SubscriptionContextTracker;

public class AutoStateFullSubscription extends AutoSubscription {

	public AutoStateFullSubscription(Socket in_sock, iKPIC_subscribeHandler2 hand, SocketAddress address, String query,
			iKPIC api,SIBResponse initResponse) throws IOException {
		super(in_sock, new SubscriptionContextTracker(hand,initResponse), address, query, api);
	}
	
	@Override
	public void run() {
		while(!this.isInterrupted() || currentState.equals(SubscritpionState.EXIT)){
			currentState = currentState.handle(this);
		}
	}
	
	@Override
	SubscritpionState ReSubscribeState() {
		return new FullResubscribeState();
	}
	
}
