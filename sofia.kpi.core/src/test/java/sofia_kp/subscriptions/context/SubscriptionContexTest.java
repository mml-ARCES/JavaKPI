package sofia_kp.subscriptions.context;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SubscriptionContexTest {

	private SubscriptionContext subscriptionContext;
	private ContextEntry entry1;
	private ContextEntry entry2;
	private ContextEntry entryTr;
	private ContextEntry entrySame1;
	private ContextEntry entrySame2;
	private ContextEntry entryOLD3;
	private ContextEntry entryOLD2;
	private ContextEntry entryOLD1;
	private ContextEntry entryOLD4;
	private ContextEntry entryNEW1;
	private ContextEntry entryNEW2;
	private ContextEntry entryNEW3;
	private ContextEntry entryNEW4;

	@Before
	public void setUp() throws Exception {
		subscriptionContext = new SubscriptionContext();
	 entry1 = new ContextEntry(new String[]{"test"});
	 entry2 = new ContextEntry(new String[]{"test","test2"});
	 entryTr = new ContextEntry(new String[]{"a","b","c"});
	 
	 entrySame1 = new ContextEntry(new String[]{"a","b","c","f","g"});
	 entrySame2 = new ContextEntry(new String[]{"a","b","c","f","g"});
	 
	 entryOLD1 = new ContextEntry(new String[]{"a","b","c"});
	 entryOLD2 = new ContextEntry(new String[]{"a","b","d"});
	 entryOLD3 = new ContextEntry(new String[]{"f","b","c"});
	 entryOLD4 = new ContextEntry(new String[]{"a","d","c"});
	 
	 entryNEW1 = new ContextEntry(new String[]{"a1","b1","c1"});
	 entryNEW2 = new ContextEntry(new String[]{"a1","b1","d1"});
	 entryNEW3 = new ContextEntry(new String[]{"f1","b1","c1"});
	 entryNEW4 = new ContextEntry(new String[]{"a1","d1","c1"});
	 
	}

	@Test
	public void testAdd() {
		subscriptionContext.add(entry1);
		assertEquals(1,(int)subscriptionContext.getContext().get(entry1));
	}

	@Test
	public void testAddSame() {
		subscriptionContext.add(entrySame1);
		subscriptionContext.add(entrySame2);
		assertEquals(2,(int)subscriptionContext.getContext().get(entrySame1));
	}
	@Test
	public void testRemove() {
		testAdd();
		subscriptionContext.remove(entry1);
		assertFalse(subscriptionContext.getContext().containsKey(entry1));
	}
	
	@Test
	public void testRemoveSame() {
		testAddSame();
		subscriptionContext.remove(entrySame2);
		assertEquals((int)subscriptionContext.getContext().get(entrySame2),1);
		subscriptionContext.remove(entrySame2);
		assertFalse(subscriptionContext.getContext().containsKey(entrySame2));
	}

	@Test
	public void testDiff() {
		subscriptionContext.add(entryOLD1);
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryNEW1);
		
		SubscriptionContext newCtx = new SubscriptionContext();
		
		newCtx.add(entryNEW1);
		newCtx.add(entryNEW2);
		newCtx.add(entryNEW3);
		newCtx.add(entryNEW4);
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		
		assertTrue(diff.getOldEntries().contains(entryOLD1));
		assertTrue(diff.getOldEntries().contains(entryOLD2));
		assertEquals(2, diff.getOldEntries().size());
		
		assertTrue(diff.getNewEntries().contains(entryNEW2));
		assertTrue(diff.getNewEntries().contains(entryNEW3));
		assertTrue(diff.getNewEntries().contains(entryNEW4));
		assertEquals(3, diff.getNewEntries().size());
		
	}

	@Test
	public void testDiffEmpty(){
		subscriptionContext.add(entryOLD1);
		subscriptionContext.add(entryOLD2);
		
		SubscriptionContext newCtx = new SubscriptionContext();
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		assertTrue(diff.getOldEntries().contains(entryOLD1));
		assertTrue(diff.getOldEntries().contains(entryOLD2));
		assertEquals(2, diff.getOldEntries().size());
		
		assertEquals(0, diff.getNewEntries().size());
		
	}
	
	@Test
	public void testDiffOldDuplicates(){
		subscriptionContext.add(entryOLD1);
		subscriptionContext.add(entryOLD1);
		
		SubscriptionContext newCtx = new SubscriptionContext();
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		assertTrue(diff.getOldEntries().contains(entryOLD1));
		assertEquals(2, diff.getOldEntries().size());
		
		assertEquals(0, diff.getNewEntries().size());
		
	}
	
	@Test
	public void testDiffNewDuplicates(){
		
		SubscriptionContext newCtx = new SubscriptionContext();
		newCtx.add(entryNEW1);
		newCtx.add(entryNEW1);
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		
		assertTrue(diff.getNewEntries().contains(entryNEW1));
		assertEquals(2, diff.getNewEntries().size());
		
		assertEquals(0, diff.getOldEntries().size());
		
	}
	
	@Test
	public void testDiffIntersectionDuplicates(){
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryOLD2);
		
		SubscriptionContext newCtx = new SubscriptionContext();
		newCtx.add(entryOLD2);
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		
		assertTrue(diff.getOldEntries().contains(entryOLD2));
		assertEquals(2, diff.getOldEntries().size());
		
		assertEquals(0, diff.getNewEntries().size());
		
	}
	
	@Test
	public void testDiffComplexDuplicates(){
		subscriptionContext.add(entryOLD1);
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryOLD2);
		subscriptionContext.add(entryOLD3);
		subscriptionContext.add(entryOLD3);
		subscriptionContext.add(entryNEW3);
		
		
		SubscriptionContext newCtx = new SubscriptionContext();
		newCtx.add(entryOLD3);
		newCtx.add(entryNEW3);
		newCtx.add(entryNEW1);
		newCtx.add(entryNEW2);
		newCtx.add(entryNEW2);
		
		
		DiffResult diff = subscriptionContext.diff(newCtx);
		
		
		long entryOld1Count = diff.getOldEntries().stream().filter(a ->{
			return a.equals(entryOLD1);
		}).count();
		
		long entryOld2Count = diff.getOldEntries().stream().filter(a ->{
			return a.equals(entryOLD2);
		}).count();
		
		long entryOld3Count = diff.getOldEntries().stream().filter(a ->{
			return a.equals(entryOLD3);
		}).count();
		
		long entryNew1Count = diff.getNewEntries().stream().filter(a ->{
			return a.equals(entryNEW1);
		}).count();
		
		long entryNew2Count = diff.getNewEntries().stream().filter(a ->{
			return a.equals(entryNEW2);
		}).count();
		
		long entryNew3Count = diff.getNewEntries().stream().filter(a ->{
			return a.equals(entryNEW3);
		}).count();
		
		assertEquals(1, entryOld1Count);
		assertEquals(3, entryOld2Count);
		assertEquals(1, entryOld3Count);
		assertEquals(5, diff.getOldEntries().size());
		
		assertEquals(1, entryNew1Count);
		assertEquals(2, entryNew2Count);
		assertEquals(0, entryNew3Count);
		
		assertEquals(3, diff.getNewEntries().size());
	}
	
	
	
}
