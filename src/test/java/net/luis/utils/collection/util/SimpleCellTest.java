/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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
	void constructorWithNullRowKey() {
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, "columnKey", "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, "columnKey", null));
	}
	
	@Test
	void constructorWithNullColumnKey() {
		assertThrows(NullPointerException.class, () -> new SimpleCell<>("rowKey", null, "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>("rowKey", null, null));
	}
	
	@Test
	void constructorWithBothKeysNull() {
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, null, "value"));
		assertThrows(NullPointerException.class, () -> new SimpleCell<>(null, null, null));
	}
	
	@Test
	void constructorWithValidInputs() {
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", "value"));
		assertDoesNotThrow(() -> new SimpleCell<>("rowKey", "columnKey", null));
		assertDoesNotThrow(() -> new SimpleCell<>(1, 2, 3));
		assertDoesNotThrow(() -> new SimpleCell<>("", "", ""));
	}
	
	@Test
	void getRowKey() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("testRow", "testColumn", "testValue");
		assertEquals("testRow", cell.getRowKey());
		
		SimpleCell<Integer, String, String> intCell = new SimpleCell<>(42, "column", "value");
		assertEquals(42, intCell.getRowKey());
	}
	
	@Test
	void getColumnKey() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("testRow", "testColumn", "testValue");
		assertEquals("testColumn", cell.getColumnKey());
		
		SimpleCell<String, Integer, String> intCell = new SimpleCell<>("row", 42, "value");
		assertEquals(42, intCell.getColumnKey());
	}
	
	@Test
	void getValue() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "column", "testValue");
		assertEquals("testValue", cell.getValue());
		
		SimpleCell<String, String, Integer> intCell = new SimpleCell<>("row", "column", 42);
		assertEquals(42, intCell.getValue());
	}
	
	@Test
	void getValueWithNull() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "column", null);
		assertNull(cell.getValue());
	}
	
	@Test
	void toStringMethod() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "col", "val");
		assertEquals("(row,col)=val", cell.toString());
		
		SimpleCell<String, String, String> nullCell = new SimpleCell<>("row", "col", null);
		assertEquals("(row,col)=null", nullCell.toString());
		
		SimpleCell<Integer, Integer, String> intCell = new SimpleCell<>(1, 2, "value");
		assertEquals("(1,2)=value", intCell.toString());
	}
	
	@Test
	void equalsAndHashCodeSameObjects() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row", "col", "val");
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row", "col", "val");
		
		assertEquals(cell1, cell2);
		assertEquals(cell1.hashCode(), cell2.hashCode());
	}
	
	@Test
	void equalsSameInstance() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "col", "val");
		assertEquals(cell, cell);
	}
	
	@Test
	void equalsWithDifferentRowKey() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row1", "col", "val");
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row2", "col", "val");
		
		assertNotEquals(cell1, cell2);
	}
	
	@Test
	void equalsWithDifferentColumnKey() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row", "col1", "val");
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row", "col2", "val");
		
		assertNotEquals(cell1, cell2);
	}
	
	@Test
	void equalsWithDifferentValue() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row", "col", "val1");
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row", "col", "val2");
		
		assertNotEquals(cell1, cell2);
	}
	
	@Test
	void equalsWithNull() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "col", "val");
		assertNotEquals(cell, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		SimpleCell<String, String, String> cell = new SimpleCell<>("row", "col", "val");
		assertNotEquals(cell, "not a cell");
	}
	
	@Test
	void equalsWithNullValues() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row", "col", null);
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row", "col", null);
		
		assertEquals(cell1, cell2);
	}
	
	@Test
	void equalsWithMixedNullValues() {
		SimpleCell<String, String, String> cell1 = new SimpleCell<>("row", "col", null);
		SimpleCell<String, String, String> cell2 = new SimpleCell<>("row", "col", "value");
		
		assertNotEquals(cell1, cell2);
	}
	
	@Test
	void differentTypes() {
		SimpleCell<String, Integer, Boolean> mixedCell = new SimpleCell<>("row", 42, true);
		
		assertEquals("row", mixedCell.getRowKey());
		assertEquals(42, mixedCell.getColumnKey());
		assertEquals(true, mixedCell.getValue());
		assertEquals("(row,42)=true", mixedCell.toString());
	}
}
