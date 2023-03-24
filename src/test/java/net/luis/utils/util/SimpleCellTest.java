package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class SimpleCellTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, "columnKey", "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>("rowKey", null, "value"));
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", "value"));
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", null));
	}
	
	@Test
	void getRowKey() {
		assertEquals(new SimpleCell<>("rowKey", "columnKey", "value").getRowKey(), "rowKey");
	}
	
	@Test
	void getColumnKey() {
		assertEquals(new SimpleCell<>("rowKey", "columnKey", "value").getColumnKey(), "columnKey");
	}
	
	@Test
	void getValue() {
		assertEquals(new SimpleCell<>("rowKey", "columnKey", "value").getValue(), "value");
	}
}