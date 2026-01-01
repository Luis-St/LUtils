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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.PropertyArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.property.exception.PropertyTypeException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyArray}.<br>
 *
 * @author Luis-St
 */
class PropertyArrayTest {
	
	@Test
	void constructorWithEmptyList() {
		PropertyArray array = new PropertyArray();
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
	}
	
	@Test
	void constructorWithElementsList() {
		List<PropertyElement> elements = List.of(PropertyNull.INSTANCE, new PropertyValue(42), new PropertyValue("test"));
		PropertyArray array = new PropertyArray(elements);
		
		assertEquals(3, array.size());
		assertEquals(PropertyNull.INSTANCE, array.get(0));
		assertEquals(new PropertyValue(42), array.get(1));
		assertEquals(new PropertyValue("test"), array.get(2));
	}
	
	@Test
	void constructorWithNullList() {
		assertThrows(NullPointerException.class, () -> new PropertyArray(null));
	}
	
	@Test
	void propertyElementTypeChecks() {
		PropertyArray array = new PropertyArray();
		
		assertFalse(array.isPropertyNull());
		assertFalse(array.isPropertyObject());
		assertTrue(array.isPropertyArray());
		assertFalse(array.isPropertyValue());
	}
	
	@Test
	void propertyElementConversions() {
		PropertyArray array = new PropertyArray();
		
		assertThrows(PropertyTypeException.class, array::getAsPropertyObject);
		assertSame(array, array.getAsPropertyArray());
		assertThrows(PropertyTypeException.class, array::getAsPropertyValue);
	}
	
	@Test
	void sizeOperations() {
		PropertyArray array = new PropertyArray();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
		
		array.add(PropertyNull.INSTANCE);
		assertEquals(1, array.size());
		assertFalse(array.isEmpty());
		
		array.add(new PropertyValue(10));
		assertEquals(2, array.size());
		
		array.remove(0);
		assertEquals(1, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void containsOperations() {
		PropertyArray array = new PropertyArray();
		PropertyElement element = new PropertyValue("test");
		
		assertFalse(array.contains(element));
		assertFalse(array.contains(null));
		
		array.add(element);
		assertTrue(array.contains(element));
		assertFalse(array.contains(new PropertyValue("different")));
		
		array.add((PropertyElement) null);
		assertTrue(array.contains(PropertyNull.INSTANCE));
	}
	
	@Test
	void iteratorAndCollectionViews() {
		PropertyArray array = new PropertyArray();
		array.add(new PropertyValue(1));
		array.add(new PropertyValue(2));
		array.add(new PropertyValue(3));
		
		int count = 0;
		for (PropertyElement element : array) {
			count++;
			assertInstanceOf(PropertyValue.class, element);
		}
		assertEquals(3, count);
		
		assertEquals(3, array.elements().size());
		assertTrue(array.elements().contains(new PropertyValue(1)));
	}
	
	@Test
	void setOperations() {
		PropertyArray array = new PropertyArray();
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.set(-1, PropertyNull.INSTANCE));
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.set(0, PropertyNull.INSTANCE));
		
		array.add(new PropertyValue(1));
		
		PropertyElement old = array.set(0, new PropertyValue(2));
		assertEquals(new PropertyValue(1), old);
		assertEquals(new PropertyValue(2), array.get(0));
		
		array.set(0, (PropertyElement) null);
		assertEquals(PropertyNull.INSTANCE, array.get(0));
		
		array.set(0, "string");
		assertEquals(new PropertyValue("string"), array.get(0));
		
		array.set(0, true);
		assertEquals(new PropertyValue(true), array.get(0));
		
		array.set(0, (Number) null);
		assertEquals(PropertyNull.INSTANCE, array.get(0));
		
		array.set(0, 42);
		assertEquals(new PropertyValue(42), array.get(0));
	}
	
	@Test
	void addOperations() {
		PropertyArray array = new PropertyArray();
		
		array.add((PropertyElement) null);
		assertEquals(PropertyNull.INSTANCE, array.get(0));
		
		array.add(new PropertyValue(42));
		assertEquals(new PropertyValue(42), array.get(1));
		
		array.add("string");
		assertEquals(new PropertyValue("string"), array.get(2));
		
		array.add((String) null);
		assertEquals(PropertyNull.INSTANCE, array.get(3));
		
		array.add(true);
		assertEquals(new PropertyValue(true), array.get(4));
		
		array.add((Number) null);
		assertEquals(PropertyNull.INSTANCE, array.get(5));
		
		array.add(123);
		assertEquals(new PropertyValue(123), array.get(6));
		
		array.add((byte) 1);
		assertEquals(new PropertyValue((byte) 1), array.get(7));
		
		array.add((short) 2);
		assertEquals(new PropertyValue((short) 2), array.get(8));
		
		array.add(3L);
		assertEquals(new PropertyValue(3L), array.get(9));
		
		array.add(4.5f);
		assertEquals(new PropertyValue(4.5f), array.get(10));
		
		array.add(5.7);
		assertEquals(new PropertyValue(5.7), array.get(11));
	}
	
	@Test
	void addAllOperations() {
		PropertyArray array = new PropertyArray();
		
		PropertyArray other = new PropertyArray();
		other.add(new PropertyValue(1));
		other.add(new PropertyValue(2));
		
		assertThrows(NullPointerException.class, () -> array.addAll((PropertyArray) null));
		
		array.addAll(other);
		assertEquals(2, array.size());
		assertEquals(new PropertyValue(1), array.get(0));
		assertEquals(new PropertyValue(2), array.get(1));
		
		assertThrows(NullPointerException.class, () -> array.addAll((List<PropertyElement>) null));
		
		array.addAll(Arrays.asList(new PropertyValue(3), new PropertyValue(4)));
		assertEquals(4, array.size());
		assertEquals(new PropertyValue(3), array.get(2));
		assertEquals(new PropertyValue(4), array.get(3));
	}
	
	@Test
	void removeOperations() {
		PropertyArray array = new PropertyArray();
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.remove(-1));
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.remove(0));
		
		array.add(new PropertyValue(1));
		array.add(new PropertyValue(2));
		array.add(new PropertyValue(3));
		
		PropertyElement removed = array.remove(1);
		assertEquals(new PropertyValue(2), removed);
		assertEquals(2, array.size());
		assertEquals(new PropertyValue(1), array.get(0));
		assertEquals(new PropertyValue(3), array.get(1));
		
		assertTrue(array.remove(new PropertyValue(1)));
		assertFalse(array.remove(new PropertyValue(999)));
		assertFalse(array.remove(null));
		
		array.add(new PropertyValue(10));
		array.add(new PropertyValue(20));
		assertEquals(3, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void getOperationsWithBoundsChecking() {
		PropertyArray array = new PropertyArray();
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.get(0));
		
		array.add(new PropertyObject());
		array.add(new PropertyArray());
		array.add(new PropertyValue("test"));
		array.add(new PropertyValue(42));
		array.add(new PropertyValue(true));
		array.add(new PropertyValue(3.14));
		
		assertEquals(new PropertyObject(), array.get(0));
		assertEquals(new PropertyArray(), array.get(1));
		assertEquals(new PropertyValue("test"), array.get(2));
	}
	
	@Test
	void getAsSpecificTypes() {
		PropertyArray array = new PropertyArray();
		array.add(new PropertyObject());
		array.add(new PropertyArray());
		array.add(new PropertyValue("test"));
		array.add(new PropertyValue(42));
		array.add(new PropertyValue(true));
		array.add(new PropertyValue(3.14));
		array.add(PropertyNull.INSTANCE);
		
		assertEquals(new PropertyObject(), array.getAsPropertyObject(0));
		assertEquals(new PropertyArray(), array.getAsPropertyArray(1));
		assertEquals(new PropertyValue("test"), array.getAsPropertyValue(2));
		
		assertEquals("test", array.getAsString(2));
		assertEquals(42, array.getAsNumber(3));
		assertEquals(42, array.getAsInteger(3));
		assertEquals(42L, array.getAsLong(3));
		assertEquals(42.0f, array.getAsFloat(3));
		assertEquals(42.0, array.getAsDouble(3));
		assertEquals((byte) 42, array.getAsByte(3));
		assertEquals((short) 42, array.getAsShort(3));
		assertTrue(array.getAsBoolean(4));
		assertEquals(3.14, array.getAsDouble(5));
		
		assertThrows(PropertyTypeException.class, () -> array.getAsPropertyObject(2));
		assertThrows(PropertyTypeException.class, () -> array.getAsPropertyArray(2));
		assertThrows(PropertyTypeException.class, () -> array.getAsPropertyValue(0));
		assertThrows(PropertyTypeException.class, () -> array.getAsString(6));
		assertThrows(PropertyTypeException.class, () -> array.getAsNumber(6));
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.getAsPropertyObject(-1));
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> array.getAsString(100));
	}
	
	@Test
	void equalsAndHashCode() {
		PropertyArray array1 = new PropertyArray();
		PropertyArray array2 = new PropertyArray();
		PropertyArray array3 = new PropertyArray();
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array1.add(new PropertyValue(42));
		array1.add(new PropertyValue("test"));
		
		array2.add(new PropertyValue(42));
		array2.add(new PropertyValue("test"));
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array3.add(new PropertyValue(43));
		array3.add(new PropertyValue("test"));
		
		assertNotEquals(array1, array3);
		assertNotEquals(array1, null);
		assertNotEquals(array1, "not an array");
		
		assertEquals(array1, array1);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		PropertyArray array = new PropertyArray();
		assertEquals("[]", array.toString());
		
		array.add(new PropertyValue(42));
		assertEquals("[42]", array.toString());
		
		array.add(new PropertyValue("test"));
		assertEquals("[42, test]", array.toString());
	}
	
	@Test
	void toStringWithNullConfig() {
		PropertyArray array = new PropertyArray();
		assertThrows(NullPointerException.class, () -> array.toString(null));
	}
	
	@Test
	void toStringWithNestedStructures() {
		PropertyArray array = new PropertyArray();
		PropertyObject nestedObject = new PropertyObject();
		nestedObject.add("key", new PropertyValue("value"));
		
		PropertyArray nestedArray = new PropertyArray();
		nestedArray.add(new PropertyValue(1));
		nestedArray.add(new PropertyValue(2));
		
		array.add(nestedObject);
		array.add(nestedArray);
		
		String result = array.toString();
		assertTrue(result.contains("[1, 2]"));
	}
	
	@Test
	void primitiveTypeChecks() {
		PropertyArray array = new PropertyArray();
		
		assertFalse(array.isBoolean());
		assertFalse(array.isNumber());
		assertFalse(array.isByte());
		assertFalse(array.isShort());
		assertFalse(array.isInteger());
		assertFalse(array.isLong());
		assertFalse(array.isFloat());
		assertFalse(array.isDouble());
		assertFalse(array.isString());
	}
}
