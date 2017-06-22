package sofia_kp.subscriptions.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionContext {
	private HashMap<ContextEntry, Integer> context = new HashMap<>();
	private HashSet<ContextEntry> duplicates = new HashSet<>();
	private boolean checkDuplicate = false;

	public void add(ContextEntry entry) {
		Integer old = context.put(entry, 1);
		if (old != null) {
			context.put(entry, ++old);

			if (!checkDuplicate && old > 1) {
				checkDuplicate = true;
				duplicates.add(entry);
			}
		}
	}

	public void remove(ContextEntry entry) {
		if (context.containsKey(entry)) {
			Integer count = context.get(entry) - 1;
			if (count > 0) {
				context.put(entry, count);
			} else {
				context.remove(entry);
				if (checkDuplicate) {
					duplicates.remove(entry);
					checkDuplicate = !duplicates.isEmpty();
				}
			}
		}
	}

	public DiffResult diff(SubscriptionContext newContext) {
		HashSet<ContextEntry> intersection = new HashSet<>(context.keySet());

		Set<ContextEntry> newKeySet = newContext.context.keySet();

		intersection.retainAll(newKeySet);

		HashSet<ContextEntry> obsoleteEntries = new HashSet<>(context.keySet());
		HashSet<ContextEntry> newEntries = new HashSet<>(newContext.context.keySet());

		obsoleteEntries.removeAll(intersection);
		newEntries.removeAll(intersection);

		Collection<ContextEntry> newEntriesResult;
		Collection<ContextEntry> oldEntriesResult;

		if (checkDuplicate || newContext.checkDuplicate) {
			newEntriesResult = new ArrayList<>();
			oldEntriesResult = new ArrayList<>();

			processDuplicates(newContext, intersection, obsoleteEntries, newEntries, newEntriesResult,
					oldEntriesResult);
		} else {
			newEntriesResult = newEntries;
			oldEntriesResult = obsoleteEntries;
		}

		synchronizeStatus(newEntriesResult, oldEntriesResult);

		return new DiffResult(newEntriesResult, oldEntriesResult);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (ContextEntry entry : context.keySet()) {
			builder.append(entry);
			builder.append('\n');
		}
		return builder.toString();
	}

	private void synchronizeStatus(Collection<ContextEntry> newEntries, Collection<ContextEntry> obsoleteEntries) {
		for (ContextEntry e : obsoleteEntries) {
			remove(e);
		}

		for (ContextEntry e : newEntries) {
			add(e);
		}
	}

	private void processDuplicates(SubscriptionContext newContext, HashSet<ContextEntry> intersection,
			HashSet<ContextEntry> obsoleteEntries, HashSet<ContextEntry> newEntries,
			Collection<ContextEntry> newEntriesCollection, Collection<ContextEntry> oldEntriesCollection) {

		calculateOldDuplicates(obsoleteEntries, oldEntriesCollection);

		calculateNewDuplicates(newContext, newEntries, newEntriesCollection);

		checkIntersectionCount(newContext, intersection, newEntriesCollection, oldEntriesCollection);
	}

	private void calculateOldDuplicates(HashSet<ContextEntry> obsoleteEntries,
			Collection<ContextEntry> oldEntriesCollection) {
		for (final ContextEntry e : obsoleteEntries) {
			final Integer count = context.get(e);
			for (int i = 0; i < count; i++) {
				oldEntriesCollection.add(e);
			}
		}
	}

	private void calculateNewDuplicates(SubscriptionContext newContext, HashSet<ContextEntry> newEntries,
			Collection<ContextEntry> newEntriesCollection) {
		for (final ContextEntry e : newEntries) {
			final Integer count = newContext.context.get(e);
			for (int i = 0; i < count; i++) {
				newEntriesCollection.add(e);
			}
		}
	}

	private void checkIntersectionCount(SubscriptionContext newContext, HashSet<ContextEntry> intersection,
			Collection<ContextEntry> newEntriesCollection, Collection<ContextEntry> oldEntriesCollection) {
		for (ContextEntry e : intersection) {
			Integer oldCount = context.get(e);
			Integer newCount = newContext.context.get(e);
			int diff = oldCount - newCount;

			for (int i = 0; i < diff; i++) {
				oldEntriesCollection.add(e);
			}

			for (int i = 0; i < -diff; i++) {
				newEntriesCollection.add(e);
			}
		}
	}

	HashMap<ContextEntry, Integer> getContext() {
		return context;
	}

}
