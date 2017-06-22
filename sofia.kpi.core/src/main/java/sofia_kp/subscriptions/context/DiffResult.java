package sofia_kp.subscriptions.context;

import java.util.Collection;

public class DiffResult {
	private final Collection<ContextEntry> newEntries;
	private final Collection<ContextEntry> oldEntries;

	public DiffResult(Collection<ContextEntry> newEntries, Collection<ContextEntry> oldEntries){
		this.newEntries = newEntries;
		this.oldEntries = oldEntries;
	}

	public Collection<ContextEntry> getNewEntries() {
		return newEntries;
	}

	public Collection<ContextEntry> getOldEntries() {
		return oldEntries;
	}
}
