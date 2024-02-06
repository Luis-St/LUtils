package net.luis.utils.util;

import net.luis.utils.exception.ModificationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SimpleEntry}.<br>
 *
 * @author Luis-St
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
		assertEquals("key", new SimpleEntry<>("key", "value").getKey());
	}
	
	@Test
	void getValue() {
		assertEquals("value", new SimpleEntry<>("key", "value").getValue());
		assertNull(new SimpleEntry<>("key", null).getValue());
	}
	
	@Test
	void setValue() {
		assertThrows(ModificationException.class, () -> new SimpleEntry<>("key", "value").setValue("value"));
	}
}