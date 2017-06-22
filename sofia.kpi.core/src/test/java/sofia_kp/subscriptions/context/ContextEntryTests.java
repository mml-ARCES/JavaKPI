package sofia_kp.subscriptions.context;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ContextEntryTests {

	private ContextEntry contextEntry;
	private ContextEntry contextEntry2;
	private ContextEntry contextEntry3;
	private ContextEntry contextEntry4;
	private ContextEntry contextEntry5;
	private ContextEntry contextEntry6;
	private ContextEntry contextEntry7;
	private ContextEntry contextEntry8;

	@Before
	public void setUp() throws Exception {
		contextEntry = new ContextEntry(new String[]{"a"});
		contextEntry2 = new ContextEntry(new String[]{"a","b","c"});
		contextEntry3 = new ContextEntry(new String[]{"a","b","c1"});
		contextEntry4 = new ContextEntry(new String[]{"a","b1","c"});
		contextEntry5 = new ContextEntry(new String[]{"a1","b","c"});
		contextEntry6 = new ContextEntry(new String[]{"a1","b1","c1"});
		contextEntry7 = new ContextEntry(new String[]{"a","b","c","f","g"});
		contextEntry8 = new ContextEntry(new String[]{"a","b","c","f","g1"});
	}

	@Test
	public void testHashCode() {
		ContextEntry contextEntry9 = new ContextEntry(new String[]{"a","b","c"});
		assertEquals(contextEntry.hashCode(), contextEntry.hashCode());
		assertEquals(contextEntry2.hashCode(), contextEntry9.hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertEquals(contextEntry, contextEntry);
		assertNotEquals(contextEntry, contextEntry2);
		assertNotEquals(contextEntry, contextEntry3);
		assertNotEquals(contextEntry, contextEntry4);
		assertNotEquals(contextEntry, contextEntry5);
		assertNotEquals(contextEntry, contextEntry6);
		
		assertNotEquals(contextEntry2, contextEntry3);
		assertNotEquals(contextEntry2, contextEntry4);
		assertNotEquals(contextEntry2, contextEntry6);
		
		assertEquals(contextEntry8, contextEntry8);
		assertNotEquals(contextEntry7, contextEntry8);
		
		ContextEntry contextEntry9 = new ContextEntry(new String[]{"a","b","c"});
		
		assertEquals(contextEntry2, contextEntry9);
		
	}
	
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(ContextEntry.class).verify();
	}
}
