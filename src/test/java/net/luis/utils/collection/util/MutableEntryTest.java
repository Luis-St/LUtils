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
 * Test class for {@link MutableEntry}.<br>
 *
 * @author Luis-St
 */
class MutableEntryTest {
	
	@Test
	void constructorWithNullKey() {
		assertThrows(NullPointerException.class, () -> new MutableEntry<>(null, "value"));
		assertThrows(NullPointerException.class, () -> new MutableEntry<>(null, null));
	}
	
	@Test
	void constructorWithValidInputs() {
		assertDoesNotThrow(() -> new MutableEntry<>("key", "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("key", null));
		assertDoesNotThrow(() -> new MutableEntry<>(123, "value"));
		assertDoesNotThrow(() -> new MutableEntry<>("", ""));
	}
	
	@Test
	void getKey() {
		MutableEntry<String, String> entry = new MutableEntry<>("testKey", "value");
		assertEquals("testKey", entry.getKey());
		
		MutableEntry<Integer, String> intEntry = new MutableEntry<>(42, "value");
		assertEquals(42, intEntry.getKey());
	}
	
	@Test
	void getValueInitial() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "initialValue");
		assertEquals("initialValue", entry.getValue());
		
		MutableEntry<String, String> nullEntry = new MutableEntry<>("key", null);
		assertNull(nullEntry.getValue());
	}
	
	@Test
	void setValueReturnsOldValue() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "oldValue");
		
		String oldValue = entry.setValue("newValue");
		assertEquals("oldValue", oldValue);
		assertEquals("newValue", entry.getValue());
	}
	
	@Test
	void setValueWithNull() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value");
		
		String oldValue = entry.setValue(null);
		assertEquals("value", oldValue);
		assertNull(entry.getValue());
	}
	
	@Test
	void setValueFromNull() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", null);
		
		String oldValue = entry.setValue("newValue");
		assertNull(oldValue);
		assertEquals("newValue", entry.getValue());
	}
	
	@Test
	void setValueMultipleTimes() {
		MutableEntry<String, Integer> entry = new MutableEntry<>("key", 1);
		
		assertEquals(1, entry.setValue(2));
		assertEquals(2, entry.getValue());
		
		assertEquals(2, entry.setValue(3));
		assertEquals(3, entry.getValue());
		
		assertEquals(3, entry.setValue(null));
		assertNull(entry.getValue());
	}
	
	@Test
	void toStringMethod() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value");
		assertEquals("key=value", entry.toString());
		
		MutableEntry<String, String> nullValueEntry = new MutableEntry<>("key", null);
		assertEquals("key=null", nullValueEntry.toString());
		
		MutableEntry<Integer, String> intKeyEntry = new MutableEntry<>(42, "value");
		assertEquals("42=value", intKeyEntry.toString());
	}
	
	@Test
	void equalsAndHashCodeSameObjects() {
		MutableEntry<String, String> entry1 = new MutableEntry<>("key", "value");
		MutableEntry<String, String> entry2 = new MutableEntry<>("key", "value");
		
		assertEquals(entry1, entry2);
		assertEquals(entry1.hashCode(), entry2.hashCode());
	}
	
	@Test
	void equalsSameInstance() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value");
		assertEquals(entry, entry);
	}
	
	@Test
	void equalsWithDifferentValues() {
		MutableEntry<String, String> entry1 = new MutableEntry<>("key", "value1");
		MutableEntry<String, String> entry2 = new MutableEntry<>("key", "value2");
		
		assertNotEquals(entry1, entry2);
	}
	
	@Test
	void equalsWithDifferentKeys() {
		MutableEntry<String, String> entry1 = new MutableEntry<>("key1", "value");
		MutableEntry<String, String> entry2 = new MutableEntry<>("key2", "value");
		
		assertNotEquals(entry1, entry2);
	}
	
	@Test
	void equalsWithNull() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value");
		assertNotEquals(entry, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "value");
		assertNotEquals(entry, "not an entry");
	}
	
	@Test
	void equalsWithNullValues() {
		MutableEntry<String, String> entry1 = new MutableEntry<>("key", null);
		MutableEntry<String, String> entry2 = new MutableEntry<>("key", null);
		
		assertEquals(entry1, entry2);
	}
	
	@Test
	void mutabilityAfterCreation() {
		MutableEntry<String, String> entry = new MutableEntry<>("key", "initial");
		
		MutableEntry<String, String> other = new MutableEntry<>("key", "initial");
		assertEquals(entry, other);
		
		entry.setValue("changed");
		assertNotEquals(entry, other);
		
		other.setValue("changed");
		assertEquals(entry, other);
	}
}
