package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-st
 *
 */

class SimpleEntryTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SimpleEntry<>(null, "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("key", "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("key", null));
	}
	
	@Test
	void getKey() {
		assertEquals(new SimpleEntry<>("key", "value").getKey(), "key");
	}
	
	@Test
	void getValue() {
		assertEquals(new SimpleEntry<>("key", "value").getValue(), "value");
	}
	
	@Test
	void setValue() {
		assertThrows(ConcurrentModificationException.class, () -> new SimpleEntry<>("key", "value").setValue("value"));
	}
}