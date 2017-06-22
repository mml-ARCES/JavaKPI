package sofia_kp.subscriptions;

interface SubscritpionState {
	static final SubscritpionState EXIT = new ExitState();
	public  SubscritpionState handle(AutoSubscription context);
}

class ExitState implements SubscritpionState{

	@Override
	public SubscritpionState handle(AutoSubscription context) {
		return this;
	}
	
}
