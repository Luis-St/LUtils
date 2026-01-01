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
	void constructorWithNullKey() {
		assertThrows(NullPointerException.class, () -> new SimpleEntry<>(null, "value"));
		assertThrows(NullPointerException.class, () -> new SimpleEntry<>(null, null));
	}
	
	@Test
	void constructorWithValidInputs() {
		assertDoesNotThrow(() -> new SimpleEntry<>("key", "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("key", null));
		assertDoesNotThrow(() -> new SimpleEntry<>(123, "value"));
		assertDoesNotThrow(() -> new SimpleEntry<>("", ""));
	}
	
	@Test
	void getKey() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("testKey", "value");
		assertEquals("testKey", entry.getKey());
		
		SimpleEntry<Integer, String> intEntry = new SimpleEntry<>(42, "value");
		assertEquals(42, intEntry.getKey());
	}
	
	@Test
	void getValue() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "testValue");
		assertEquals("testValue", entry.getValue());
		
		SimpleEntry<String, Integer> intEntry = new SimpleEntry<>("key", 42);
		assertEquals(42, intEntry.getValue());
	}
	
	@Test
	void getValueWithNull() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", null);
		assertNull(entry.getValue());
	}
	
	@Test
	void setValueThrowsModificationException() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		
		assertThrows(ModificationException.class, () -> entry.setValue("newValue"));
		assertThrows(ModificationException.class, () -> entry.setValue(null));
		assertThrows(ModificationException.class, () -> entry.setValue(""));
	}
	
	@Test
	void setValueExceptionMessage() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		
		ModificationException exception = assertThrows(ModificationException.class, () -> entry.setValue("newValue"));
		assertTrue(exception.getMessage().contains("newValue"));
		assertTrue(exception.getMessage().contains("muted"));
	}
	
	@Test
	void setValueDoesNotChangeOriginalValue() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "originalValue");
		
		assertThrows(ModificationException.class, () -> entry.setValue("attemptedNewValue"));
		
		assertEquals("originalValue", entry.getValue());
	}
	
	@Test
	void toStringMethod() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		assertEquals("key=value", entry.toString());
		
		SimpleEntry<String, String> nullValueEntry = new SimpleEntry<>("key", null);
		assertEquals("key=null", nullValueEntry.toString());
		
		SimpleEntry<Integer, String> intKeyEntry = new SimpleEntry<>(42, "value");
		assertEquals("42=value", intKeyEntry.toString());
	}
	
	@Test
	void equalsAndHashCodeSameObjects() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key", "value");
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key", "value");
		
		assertEquals(entry1, entry2);
		assertEquals(entry1.hashCode(), entry2.hashCode());
	}
	
	@Test
	void equalsSameInstance() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		assertEquals(entry, entry);
	}
	
	@Test
	void equalsWithDifferentValues() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key", "value1");
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key", "value2");
		
		assertNotEquals(entry1, entry2);
	}
	
	@Test
	void equalsWithDifferentKeys() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key1", "value");
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key2", "value");
		
		assertNotEquals(entry1, entry2);
	}
	
	@Test
	void equalsWithNull() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		assertNotEquals(entry, null);
	}
	
	@Test
	void equalsWithDifferentType() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		assertNotEquals(entry, "not an entry");
	}
	
	@Test
	void equalsWithNullValues() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key", null);
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key", null);
		
		assertEquals(entry1, entry2);
	}
	
	@Test
	void equalsWithMixedNullValues() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key", null);
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key", "value");
		
		assertNotEquals(entry1, entry2);
	}
	
	@Test
	void hashCodeConsistency() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		int hash1 = entry.hashCode();
		int hash2 = entry.hashCode();
		
		assertEquals(hash1, hash2);
	}
	
	@Test
	void hashCodeWithNullValue() {
		SimpleEntry<String, String> entry1 = new SimpleEntry<>("key", null);
		SimpleEntry<String, String> entry2 = new SimpleEntry<>("key", null);
		
		assertEquals(entry1.hashCode(), entry2.hashCode());
	}
	
	@Test
	void immutabilityGuarantee() {
		SimpleEntry<String, String> entry = new SimpleEntry<>("key", "value");
		
		assertEquals("key", entry.getKey());
		assertEquals("value", entry.getValue());
		
		assertThrows(ModificationException.class, () -> entry.setValue("different"));
		
		assertEquals("key", entry.getKey());
		assertEquals("value", entry.getValue());
	}
}
