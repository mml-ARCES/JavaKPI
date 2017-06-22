package sofia_kp.subscriptions;

import sofia_kp.SIBResponse;

public class JoiningState implements SubscritpionState {

	@Override
	public SubscritpionState handle(AutoSubscription context) {
		SIBResponse join = context.getApi().join();
		return context.createReconnectionState();
	}

}
