/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.luis.utils.collection.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SimpleCell}.<br>
 *
 * @author Luis-St
 */
class SimpleCellTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, null, "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, "columnKey", "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>("rowKey", null, "value"));
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", null));
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", "value"));
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