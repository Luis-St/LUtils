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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.NoSuchPropertyException;
import net.luis.utils.io.data.property.exception.PropertyTypeException;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyObject}.<br>
 *
 * @author Luis-St
 */
class PropertyObjectTest {
	
	@Test
	void constructorWithEmptyMap() {
		PropertyObject object = new PropertyObject();
		assertTrue(object.isEmpty());
		assertEquals(0, object.size());
	}
	
	@Test
	void constructorWithElementsMap() {
		Map<String, PropertyElement> elements = Map.of(
			"key1", new PropertyValue("value1"),
			"key2", new PropertyValue(42),
			"key3", PropertyNull.INSTANCE
		);
		
		PropertyObject object = new PropertyObject(elements);
		assertEquals(3, object.size());
		assertEquals(new PropertyValue("value1"), object.get("key1"));
		assertEquals(new PropertyValue(42), object.get("key2"));
		assertEquals(PropertyNull.INSTANCE, object.get("key3"));
	}
	
	@Test
	void constructorWithNullMap() {
		assertThrows(NullPointerException.class, () -> new PropertyObject(null));
	}
	
	@Test
	void propertyElementTypeChecks() {
		PropertyObject object = new PropertyObject();
		
		assertFalse(object.isPropertyNull());
		assertTrue(object.isPropertyObject());
		assertFalse(object.isPropertyArray());
		assertFalse(object.isPropertyValue());
	}
	
	@Test
	void propertyElementConversions() {
		PropertyObject object = new PropertyObject();
		
		assertSame(object, object.getAsPropertyObject());
		assertThrows(PropertyTypeException.class, object::getAsPropertyArray);
		assertThrows(PropertyTypeException.class, object::getAsPropertyValue);
	}
	
	@Test
	void sizeAndEmptyOperations() {
		PropertyObject object = new PropertyObject();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
		
		object.add("key1", new PropertyValue("value1"));
		assertEquals(1, object.size());
		assertFalse(object.isEmpty());
		
		object.add("key2", new PropertyValue(42));
		assertEquals(2, object.size());
		
		object.remove("key1");
		assertEquals(1, object.size());
		
		object.clear();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void containsOperations() {
		PropertyObject object = new PropertyObject();
		PropertyElement value = new PropertyValue("test");
		
		assertFalse(object.containsKey("key"));
		assertFalse(object.containsKey(null));
		assertFalse(object.containsValue(value));
		assertFalse(object.containsValue(null));
		
		object.add("key", value);
		assertTrue(object.containsKey("key"));
		assertTrue(object.containsValue(value));
		assertFalse(object.containsKey("other"));
		assertFalse(object.containsValue(new PropertyValue("different")));
		
		object.add("nullKey", (String) null);
		assertTrue(object.containsKey("nullKey"));
		assertTrue(object.containsValue(PropertyNull.INSTANCE));
	}
	
	@Test
	void keySetAndValuesOperations() {
		PropertyObject object = new PropertyObject();
		
		assertEquals(Set.of(), object.keySet());
		assertTrue(object.elements().isEmpty());
		assertTrue(object.entrySet().isEmpty());
		
		object.add("key1", new PropertyValue("value1"));
		object.add("key2", new PropertyValue(42));
		object.add("key3", PropertyNull.INSTANCE);
		
		Set<String> keys = object.keySet();
		assertEquals(Set.of("key1", "key2", "key3"), keys);
		
		assertEquals(3, object.elements().size());
		assertTrue(object.elements().contains(new PropertyValue("value1")));
		assertTrue(object.elements().contains(new PropertyValue(42)));
		assertTrue(object.elements().contains(PropertyNull.INSTANCE));
		
		assertEquals(3, object.entrySet().size());
		assertTrue(object.entrySet().contains(Map.entry("key1", new PropertyValue("value1"))));
	}
	
	@Test
	void forEachOperation() {
		PropertyObject object = new PropertyObject();
		
		assertThrows(NullPointerException.class, () -> object.forEach(null));
		
		object.forEach((key, value) -> fail("Should not be called for empty object"));
		
		object.add("key1", new PropertyValue("value1"));
		object.add("key2", new PropertyValue(42));
		
		AtomicInteger callCount = new AtomicInteger(0);
		object.forEach((key, value) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("key1", "key2").contains(key));
			assertNotNull(value);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addOperationsWithPropertyElement() {
		PropertyObject object = new PropertyObject();
		
		assertThrows(NullPointerException.class, () -> object.add(null, new PropertyValue("value")));
		
		PropertyElement previous = object.add("key1", (PropertyElement) null);
		assertNull(previous);
		assertEquals(PropertyNull.INSTANCE, object.get("key1"));
		
		previous = object.add("key1", new PropertyValue("newValue"));
		assertEquals(PropertyNull.INSTANCE, previous);
		assertEquals(new PropertyValue("newValue"), object.get("key1"));
	}
	
	@Test
	void addOperationsWithPrimitiveTypes() {
		PropertyObject object = new PropertyObject();
		
		object.add("stringKey", "testString");
		assertEquals(new PropertyValue("testString"), object.get("stringKey"));
		
		object.add("nullStringKey", (String) null);
		assertEquals(PropertyNull.INSTANCE, object.get("nullStringKey"));
		
		object.add("booleanKey", true);
		assertEquals(new PropertyValue(true), object.get("booleanKey"));
		
		object.add("numberKey", Integer.valueOf(42));
		assertEquals(new PropertyValue(42), object.get("numberKey"));
		
		object.add("nullNumberKey", (Number) null);
		assertEquals(PropertyNull.INSTANCE, object.get("nullNumberKey"));
		
		object.add("byteKey", (byte) 1);
		assertEquals(new PropertyValue((byte) 1), object.get("byteKey"));
		
		object.add("shortKey", (short) 2);
		assertEquals(new PropertyValue((short) 2), object.get("shortKey"));
		
		object.add("intKey", 3);
		assertEquals(new PropertyValue(3), object.get("intKey"));
		
		object.add("longKey", 4L);
		assertEquals(new PropertyValue(4L), object.get("longKey"));
		
		object.add("floatKey", 5.5f);
		assertEquals(new PropertyValue(5.5f), object.get("floatKey"));
		
		object.add("doubleKey", 6.6);
		assertEquals(new PropertyValue(6.6), object.get("doubleKey"));
	}
	
	@Test
	void addAllOperations() {
		PropertyObject object = new PropertyObject();
		
		PropertyObject other = new PropertyObject();
		other.add("key1", new PropertyValue("value1"));
		other.add("key2", new PropertyValue(42));
		
		assertThrows(NullPointerException.class, () -> object.addAll((PropertyObject) null));
		
		object.addAll(other);
		assertEquals(2, object.size());
		assertEquals(new PropertyValue("value1"), object.get("key1"));
		assertEquals(new PropertyValue(42), object.get("key2"));
		
		Map<String, PropertyElement> map = new HashMap<>();
		map.put("key3", new PropertyValue("value3"));
		map.put("key4", PropertyNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> object.addAll((Map<String, PropertyElement>) null));
		
		object.addAll(map);
		assertEquals(4, object.size());
		assertEquals(new PropertyValue("value3"), object.get("key3"));
		assertEquals(PropertyNull.INSTANCE, object.get("key4"));
	}
	
	@Test
	void removeOperations() {
		PropertyObject object = new PropertyObject();
		
		assertNull(object.remove("nonexistent"));
		assertNull(object.remove(null));
		
		object.add("key1", new PropertyValue("value1"));
		object.add("key2", new PropertyValue(42));
		object.add("key3", PropertyNull.INSTANCE);
		
		PropertyElement removed = object.remove("key1");
		assertEquals(new PropertyValue("value1"), removed);
		assertEquals(2, object.size());
		assertFalse(object.containsKey("key1"));
		
		assertNull(object.remove("nonexistent"));
		assertEquals(2, object.size());
		
		object.clear();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void replaceOperations() {
		PropertyObject object = new PropertyObject();
		
		assertThrows(NullPointerException.class, () -> object.replace(null, new PropertyValue("value")));
		
		PropertyElement result = object.replace("key", new PropertyValue("value"));
		assertNull(result);
		assertTrue(object.isEmpty());
		
		object.add("key1", new PropertyValue("oldValue"));
		
		result = object.replace("key1", new PropertyValue("newValue"));
		assertEquals(new PropertyValue("oldValue"), result);
		assertEquals(new PropertyValue("newValue"), object.get("key1"));
		
		result = object.replace("key1", null);
		assertEquals(new PropertyValue("newValue"), result);
		assertEquals(PropertyNull.INSTANCE, object.get("key1"));
		
		assertThrows(NullPointerException.class, () -> object.replace(null, PropertyNull.INSTANCE, new PropertyValue("value")));
		assertThrows(NullPointerException.class, () -> object.replace("key1", null, new PropertyValue("value")));
		
		boolean replaced = object.replace("key1", PropertyNull.INSTANCE, new PropertyValue("finalValue"));
		assertTrue(replaced);
		assertEquals(new PropertyValue("finalValue"), object.get("key1"));
		
		replaced = object.replace("key1", PropertyNull.INSTANCE, new PropertyValue("shouldNotReplace"));
		assertFalse(replaced);
		assertEquals(new PropertyValue("finalValue"), object.get("key1"));
	}
	
	@Test
	void getOperations() {
		PropertyObject object = new PropertyObject();
		
		assertThrows(NullPointerException.class, () -> object.get(null));
		assertNull(object.get("nonexistent"));
		
		object.add("stringKey", new PropertyValue("stringValue"));
		object.add("numberKey", new PropertyValue(42));
		object.add("booleanKey", new PropertyValue(true));
		object.add("objectKey", new PropertyObject());
		object.add("arrayKey", new PropertyArray());
		object.add("nullKey", PropertyNull.INSTANCE);
		
		assertEquals(new PropertyValue("stringValue"), object.get("stringKey"));
		assertEquals(new PropertyValue(42), object.get("numberKey"));
		assertEquals(new PropertyObject(), object.get("objectKey"));
		assertEquals(new PropertyArray(), object.get("arrayKey"));
		assertEquals(PropertyNull.INSTANCE, object.get("nullKey"));
	}
	
	@Test
	void getAsSpecificTypesSuccess() {
		PropertyObject object = new PropertyObject();
		object.add("objectKey", new PropertyObject());
		object.add("arrayKey", new PropertyArray());
		object.add("stringKey", new PropertyValue("stringValue"));
		object.add("numberKey", new PropertyValue(42));
		object.add("booleanKey", new PropertyValue(true));
		object.add("doubleKey", new PropertyValue(3.14));
		
		assertEquals(new PropertyObject(), object.getAsPropertyObject("objectKey"));
		assertEquals(new PropertyArray(), object.getAsPropertyArray("arrayKey"));
		assertEquals(new PropertyValue("stringValue"), object.getPropertyValue("stringKey"));
		
		assertEquals("stringValue", object.getAsString("stringKey"));
		assertEquals(42, object.getAsNumber("numberKey"));
		assertEquals(42, object.getAsInteger("numberKey"));
		assertEquals(42L, object.getAsLong("numberKey"));
		assertEquals(42.0f, object.getAsFloat("numberKey"));
		assertEquals(42.0, object.getAsDouble("numberKey"));
		assertEquals((byte) 42, object.getAsByte("numberKey"));
		assertEquals((short) 42, object.getAsShort("numberKey"));
		assertTrue(object.getAsBoolean("booleanKey"));
		assertEquals(3.14, object.getAsDouble("doubleKey"));
	}
	
	@Test
	void getAsSpecificTypesExceptions() {
		PropertyObject object = new PropertyObject();
		object.add("stringKey", new PropertyValue("stringValue"));
		object.add("nullKey", PropertyNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> object.getAsPropertyObject(null));
		assertThrows(NoSuchPropertyException.class, () -> object.getAsPropertyObject("nonexistent"));
		assertThrows(NoSuchPropertyException.class, () -> object.getAsPropertyArray("nonexistent"));
		assertThrows(NoSuchPropertyException.class, () -> object.getPropertyValue("nonexistent"));
		assertThrows(NoSuchPropertyException.class, () -> object.getAsString("nonexistent"));
		
		assertThrows(PropertyTypeException.class, () -> object.getAsPropertyObject("stringKey"));
		assertThrows(PropertyTypeException.class, () -> object.getAsPropertyArray("stringKey"));
		assertThrows(PropertyTypeException.class, () -> object.getPropertyValue("nullKey"));
		assertThrows(PropertyTypeException.class, () -> object.getAsString("nullKey"));
		assertThrows(PropertyTypeException.class, () -> object.getAsBoolean("nullKey"));
		assertThrows(PropertyTypeException.class, () -> object.getAsNumber("nullKey"));
	}
	
	@Test
	void hasGroup() {
		PropertyObject object = new PropertyObject();
		object.add("app.database.host", new PropertyValue("localhost"));
		object.add("app.database.port", new PropertyValue(5432));
		object.add("app.cache.enabled", new PropertyValue(true));
		object.add("other.key", new PropertyValue("value"));
		
		assertThrows(NullPointerException.class, () -> object.hasGroup(null));
		
		assertTrue(object.hasGroup("app"));
		assertTrue(object.hasGroup("app.database"));
		assertTrue(object.hasGroup("app.cache"));
		assertTrue(object.hasGroup("other"));
		assertFalse(object.hasGroup("nonexistent"));
		assertFalse(object.hasGroup("app.nonexistent"));
	}
	
	@Test
	void getSubgroup() {
		PropertyObject object = new PropertyObject();
		object.add("app.database.host", new PropertyValue("localhost"));
		object.add("app.database.port", new PropertyValue(5432));
		object.add("app.cache.enabled", new PropertyValue(true));
		object.add("other.key", new PropertyValue("value"));
		
		PropertyObject appSubgroup = object.getSubgroup("app");
		assertEquals(3, appSubgroup.size());
		assertEquals(new PropertyValue("localhost"), appSubgroup.get("database.host"));
		assertEquals(new PropertyValue(5432), appSubgroup.get("database.port"));
		assertEquals(new PropertyValue(true), appSubgroup.get("cache.enabled"));
		
		PropertyObject dbSubgroup = object.getSubgroup("app.database");
		assertEquals(2, dbSubgroup.size());
		assertEquals(new PropertyValue("localhost"), dbSubgroup.get("host"));
		assertEquals(new PropertyValue(5432), dbSubgroup.get("port"));
		
		PropertyObject emptySubgroup = object.getSubgroup("nonexistent");
		assertTrue(emptySubgroup.isEmpty());
		
		PropertyObject allSubgroup = object.getSubgroup(null);
		assertEquals(4, allSubgroup.size());
	}
	
	@Test
	void getChildGroups() {
		PropertyObject object = new PropertyObject();
		object.add("app.database.host", new PropertyValue("localhost"));
		object.add("app.database.port", new PropertyValue(5432));
		object.add("app.cache.enabled", new PropertyValue(true));
		object.add("app.logging.level", new PropertyValue("INFO"));
		object.add("other.key", new PropertyValue("value"));
		
		Set<String> appChildren = object.getChildGroups("app");
		assertEquals(Set.of("database", "cache", "logging"), appChildren);
		
		Set<String> rootChildren = object.getChildGroups(null);
		assertEquals(Set.of("app", "other"), rootChildren);
		
		Set<String> dbChildren = object.getChildGroups("app.database");
		assertTrue(dbChildren.isEmpty());
		
		Set<String> nonexistentChildren = object.getChildGroups("nonexistent");
		assertTrue(nonexistentChildren.isEmpty());
	}
	
	@Test
	void toNestedMap() {
		PropertyObject object = new PropertyObject();
		object.add("a.b.c", new PropertyValue(1));
		object.add("a.b.d", new PropertyValue(2));
		object.add("a.e", new PropertyValue("test"));
		
		Map<String, Object> nested = object.toNestedMap();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> a = (Map<String, Object>) nested.get("a");
		assertNotNull(a);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> b = (Map<String, Object>) a.get("b");
		assertNotNull(b);
		assertEquals(1, b.get("c"));
		assertEquals(2, b.get("d"));
		assertEquals("test", a.get("e"));
	}
	
	@Test
	void fromNestedMap() {
		Map<String, Object> nested = new LinkedHashMap<>();
		Map<String, Object> a = new LinkedHashMap<>();
		Map<String, Object> b = new LinkedHashMap<>();
		b.put("c", 1);
		b.put("d", 2);
		a.put("b", b);
		a.put("e", "test");
		nested.put("a", a);
		
		assertThrows(NullPointerException.class, () -> PropertyObject.fromNestedMap(null));
		
		PropertyObject object = PropertyObject.fromNestedMap(nested);
		assertEquals(new PropertyValue(1), object.get("a.b.c"));
		assertEquals(new PropertyValue(2), object.get("a.b.d"));
		assertEquals(new PropertyValue("test"), object.get("a.e"));
	}
	
	@Test
	void flatten() {
		PropertyObject nested = new PropertyObject();
		PropertyObject inner = new PropertyObject();
		inner.add("c", new PropertyValue(1));
		inner.add("d", new PropertyValue(2));
		nested.add("a", inner);
		nested.add("b", new PropertyValue("test"));
		
		PropertyObject flat = nested.flatten();
		assertEquals(new PropertyValue(1), flat.get("a.c"));
		assertEquals(new PropertyValue(2), flat.get("a.d"));
		assertEquals(new PropertyValue("test"), flat.get("b"));
	}
	
	@Test
	void expand() {
		PropertyObject flat = new PropertyObject();
		flat.add("a.b.c", new PropertyValue(1));
		flat.add("a.b.d", new PropertyValue(2));
		flat.add("a.e", new PropertyValue("test"));
		
		PropertyObject expanded = flat.expand();
		
		PropertyObject a = expanded.getAsPropertyObject("a");
		assertNotNull(a);
		
		PropertyObject b = a.getAsPropertyObject("b");
		assertNotNull(b);
		assertEquals(new PropertyValue(1), b.get("c"));
		assertEquals(new PropertyValue(2), b.get("d"));
		assertEquals(new PropertyValue("test"), a.get("e"));
	}
	
	@Test
	void equalsAndHashCode() {
		PropertyObject object1 = new PropertyObject();
		PropertyObject object2 = new PropertyObject();
		PropertyObject object3 = new PropertyObject();
		
		assertEquals(object1, object2);
		assertEquals(object1.hashCode(), object2.hashCode());
		
		object1.add("key1", new PropertyValue("value1"));
		object1.add("key2", new PropertyValue(42));
		
		object2.add("key1", new PropertyValue("value1"));
		object2.add("key2", new PropertyValue(42));
		
		assertEquals(object1, object2);
		assertEquals(object1.hashCode(), object2.hashCode());
		
		object3.add("key1", new PropertyValue("differentValue"));
		object3.add("key2", new PropertyValue(42));
		
		assertNotEquals(object1, object3);
		assertNotEquals(object1, null);
		assertNotEquals(object1, "not an object");
		
		assertEquals(object1, object1);
		
		PropertyObject object4 = new PropertyObject();
		object4.add("key1", new PropertyValue("value1"));
		assertNotEquals(object1, object4);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		PropertyObject object = new PropertyObject();
		assertEquals("", object.toString());
		
		object.add("key", new PropertyValue("value"));
		String result = object.toString();
		assertTrue(result.contains("key"));
		assertTrue(result.contains("="));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringWithNullConfig() {
		PropertyObject object = new PropertyObject();
		assertThrows(NullPointerException.class, () -> object.toString(null));
	}
	
	@Test
	void preservesInsertionOrder() {
		PropertyObject object = new PropertyObject();
		
		object.add("third", new PropertyValue(3));
		object.add("first", new PropertyValue(1));
		object.add("second", new PropertyValue(2));
		
		String[] expectedOrder = { "third", "first", "second" };
		String[] actualOrder = object.keySet().toArray(new String[0]);
		assertArrayEquals(expectedOrder, actualOrder);
	}
}
