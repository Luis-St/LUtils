package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DataFlowIssue")
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
		assertEquals("rowKey", new SimpleCell<>("rowKey", "columnKey", "value").getRowKey());
	}
	
	@Test
	void getColumnKey() {
		assertEquals("columnKey", new SimpleCell<>("rowKey", "columnKey", "value").getColumnKey());
	}
	
	@Test
	void getValue() {
		assertEquals("value", new SimpleCell<>("rowKey", "columnKey", "value").getValue());
	}
}