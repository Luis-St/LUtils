package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
class MutableEntryTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new MutableEntry<>(null, "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("key", "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("key", null));
	}
	
	@Test
	void setValue() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value0");
		assertDoesNotThrow(() -> {
			entry.setValue("value1");
		});
		assertEquals("value1", entry.getValue());
	}
}