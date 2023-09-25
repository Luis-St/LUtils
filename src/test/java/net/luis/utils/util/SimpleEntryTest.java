package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
class SimpleEntryTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SimpleEntry<>(null, "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("key", "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("key", null));
	}
	
	@Test
	void getKey() {
		assertEquals("key", new SimpleEntry<>("key", "value").getKey());
	}
	
	@Test
	void getValue() {
		assertEquals("value", new SimpleEntry<>("key", "value").getValue());
	}
	
	@Test
	void setValue() {
		assertThrows(ConcurrentModificationException.class, () -> new SimpleEntry<>("key", "value").setValue("value"));
	}
}