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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyBuilder}.<br>
 *
 * @author Luis-St
 */
class PropertyBuilderTest {
	
	@Test
	void createEmptyObject() {
		PropertyObject object = PropertyBuilder.object().buildObject();
		
		assertTrue(object.isEmpty());
		assertEquals(0, object.size());
	}
	
	@Test
	void createEmptyArray() {
		PropertyArray array = PropertyBuilder.array().buildArray();
		
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
	}
	
	@Test
	void buildSimpleObject() {
		PropertyObject object = PropertyBuilder.object()
			.add("name", "John Doe")
			.add("age", 30)
			.add("active", true)
			.buildObject();
		
		assertEquals(3, object.size());
		assertEquals(new PropertyValue("John Doe"), object.get("name"));
		assertEquals(new PropertyValue(30), object.get("age"));
		assertEquals(new PropertyValue(true), object.get("active"));
	}
	
	@Test
	void buildSimpleArray() {
		PropertyArray array = PropertyBuilder.array()
			.add("first")
			.add("second")
			.add("third")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new PropertyValue("first"), array.get(0));
		assertEquals(new PropertyValue("second"), array.get(1));
		assertEquals(new PropertyValue("third"), array.get(2));
	}
	
	@Test
	void addAllPrimitiveTypesToObject() {
		PropertyObject object = PropertyBuilder.object()
			.add("string", "test")
			.add("nullString", (String) null)
			.add("boolean", true)
			.add("number", Integer.valueOf(42))
			.add("nullNumber", (Number) null)
			.add("byte", (byte) 1)
			.add("short", (short) 2)
			.add("int", 3)
			.add("long", 4L)
			.add("float", 5.5f)
			.add("double", 6.6)
			.buildObject();
		
		assertEquals(11, object.size());
		assertEquals(new PropertyValue("test"), object.get("string"));
		assertEquals(PropertyNull.INSTANCE, object.get("nullString"));
		assertEquals(new PropertyValue(true), object.get("boolean"));
		assertEquals(new PropertyValue(42), object.get("number"));
		assertEquals(PropertyNull.INSTANCE, object.get("nullNumber"));
		assertEquals(new PropertyValue((byte) 1), object.get("byte"));
		assertEquals(new PropertyValue((short) 2), object.get("short"));
		assertEquals(new PropertyValue(3), object.get("int"));
		assertEquals(new PropertyValue(4L), object.get("long"));
		assertEquals(new PropertyValue(5.5f), object.get("float"));
		assertEquals(new PropertyValue(6.6), object.get("double"));
	}
	
	@Test
	void addAllPrimitiveTypesToArray() {
		PropertyArray array = PropertyBuilder.array()
			.add("test")
			.add((String) null)
			.add(true)
			.add(Integer.valueOf(42))
			.add((Number) null)
			.add((byte) 1)
			.add((short) 2)
			.add(3)
			.add(4L)
			.add(5.5f)
			.add(6.6)
			.buildArray();
		
		assertEquals(11, array.size());
		assertEquals(new PropertyValue("test"), array.get(0));
		assertEquals(PropertyNull.INSTANCE, array.get(1));
		assertEquals(new PropertyValue(true), array.get(2));
		assertEquals(new PropertyValue(42), array.get(3));
		assertEquals(PropertyNull.INSTANCE, array.get(4));
		assertEquals(new PropertyValue((byte) 1), array.get(5));
		assertEquals(new PropertyValue((short) 2), array.get(6));
		assertEquals(new PropertyValue(3), array.get(7));
		assertEquals(new PropertyValue(4L), array.get(8));
		assertEquals(new PropertyValue(5.5f), array.get(9));
		assertEquals(new PropertyValue(6.6), array.get(10));
	}
	
	@Test
	void addPropertyElementsDirectly() {
		PropertyElement customElement = new PropertyValue("custom");
		
		PropertyObject object = PropertyBuilder.object()
			.add("element", customElement)
			.add("null", (PropertyElement) null)
			.buildObject();
		
		assertEquals(2, object.size());
		assertEquals(customElement, object.get("element"));
		assertEquals(PropertyNull.INSTANCE, object.get("null"));
		
		PropertyArray array = PropertyBuilder.array()
			.add(customElement)
			.add((PropertyElement) null)
			.buildArray();
		
		assertEquals(2, array.size());
		assertEquals(customElement, array.get(0));
		assertEquals(PropertyNull.INSTANCE, array.get(1));
	}
	
	@Test
	void addBuilderResultsToObjectAndArray() {
		PropertyBuilder nestedObjectBuilder = PropertyBuilder.object()
			.add("nested", "value");
		
		PropertyBuilder nestedArrayBuilder = PropertyBuilder.array()
			.add(1)
			.add(2);
		
		PropertyObject object = PropertyBuilder.object()
			.addObject("obj", nestedObjectBuilder)
			.addArray("arr", nestedArrayBuilder)
			.buildObject();
		
		assertEquals(2, object.size());
		PropertyObject nestedObj = object.getAsPropertyObject("obj");
		assertEquals(new PropertyValue("value"), nestedObj.get("nested"));
		
		PropertyArray nestedArr = object.getAsPropertyArray("arr");
		assertEquals(2, nestedArr.size());
		assertEquals(new PropertyValue(1), nestedArr.get(0));
		
		PropertyArray array = PropertyBuilder.array()
			.addObject(nestedObjectBuilder)
			.addArray(nestedArrayBuilder)
			.buildArray();
		
		assertEquals(2, array.size());
		assertInstanceOf(PropertyObject.class, array.get(0));
		assertInstanceOf(PropertyArray.class, array.get(1));
	}
	
	@Test
	void addAllArrayConvenienceMethods() {
		PropertyArray array = PropertyBuilder.array()
			.addAll("first", "second", "third")
			.addAll(1, 2, 3)
			.addAll(true, false, true)
			.buildArray();
		
		assertEquals(9, array.size());
		
		assertEquals(new PropertyValue("first"), array.get(0));
		assertEquals(new PropertyValue("second"), array.get(1));
		assertEquals(new PropertyValue("third"), array.get(2));
		
		assertEquals(new PropertyValue(1), array.get(3));
		assertEquals(new PropertyValue(2), array.get(4));
		assertEquals(new PropertyValue(3), array.get(5));
		
		assertEquals(new PropertyValue(true), array.get(6));
		assertEquals(new PropertyValue(false), array.get(7));
		assertEquals(new PropertyValue(true), array.get(8));
	}
	
	@Test
	void nestedObjectInObject() {
		PropertyObject object = PropertyBuilder.object()
			.add("name", "John")
			.startObject("address")
			.add("street", "123 Main St")
			.add("city", "Anytown")
			.add("zipcode", 12345)
			.endObject()
			.add("age", 30)
			.buildObject();
		
		assertEquals(3, object.size());
		assertEquals(new PropertyValue("John"), object.get("name"));
		assertEquals(new PropertyValue(30), object.get("age"));
		
		PropertyObject address = object.getAsPropertyObject("address");
		assertEquals(3, address.size());
		assertEquals(new PropertyValue("123 Main St"), address.get("street"));
		assertEquals(new PropertyValue("Anytown"), address.get("city"));
		assertEquals(new PropertyValue(12345), address.get("zipcode"));
	}
	
	@Test
	void nestedArrayInObject() {
		PropertyObject object = PropertyBuilder.object()
			.add("name", "John")
			.startArray("hobbies")
			.add("reading")
			.add("swimming")
			.add("coding")
			.endArray()
			.add("age", 30)
			.buildObject();
		
		assertEquals(3, object.size());
		
		PropertyArray hobbies = object.getAsPropertyArray("hobbies");
		assertEquals(3, hobbies.size());
		assertEquals(new PropertyValue("reading"), hobbies.get(0));
		assertEquals(new PropertyValue("swimming"), hobbies.get(1));
		assertEquals(new PropertyValue("coding"), hobbies.get(2));
	}
	
	@Test
	void nestedObjectInArray() {
		PropertyArray array = PropertyBuilder.array()
			.add("first")
			.startObject()
			.add("id", 1)
			.add("name", "Item 1")
			.endObject()
			.add("last")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new PropertyValue("first"), array.get(0));
		assertEquals(new PropertyValue("last"), array.get(2));
		
		PropertyObject item = array.getAsPropertyObject(1);
		assertEquals(2, item.size());
		assertEquals(new PropertyValue(1), item.get("id"));
		assertEquals(new PropertyValue("Item 1"), item.get("name"));
	}
	
	@Test
	void nestedArrayInArray() {
		PropertyArray array = PropertyBuilder.array()
			.add("outer")
			.startArray()
			.add("inner1")
			.add("inner2")
			.endArray()
			.add("final")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new PropertyValue("outer"), array.get(0));
		assertEquals(new PropertyValue("final"), array.get(2));
		
		PropertyArray inner = array.getAsPropertyArray(1);
		assertEquals(2, inner.size());
		assertEquals(new PropertyValue("inner1"), inner.get(0));
		assertEquals(new PropertyValue("inner2"), inner.get(1));
	}
	
	@Test
	void deeplyNestedStructure() {
		PropertyObject object = PropertyBuilder.object()
			.add("level", 1)
			.startObject("nested")
			.add("level", 2)
			.startArray("items")
			.startObject()
			.add("level", 3)
			.startObject("deep")
			.add("level", 4)
			.add("value", "deepest")
			.endObject()
			.endObject()
			.endArray()
			.endObject()
			.buildObject();
		
		assertEquals(2, object.size());
		assertEquals(new PropertyValue(1), object.get("level"));
		
		PropertyObject nested = object.getAsPropertyObject("nested");
		assertEquals(new PropertyValue(2), nested.get("level"));
		
		PropertyArray items = nested.getAsPropertyArray("items");
		assertEquals(1, items.size());
		
		PropertyObject item = items.getAsPropertyObject(0);
		assertEquals(new PropertyValue(3), item.get("level"));
		
		PropertyObject deep = item.getAsPropertyObject("deep");
		assertEquals(new PropertyValue(4), deep.get("level"));
		assertEquals(new PropertyValue("deepest"), deep.get("value"));
	}
	
	@Test
	void conditionalBuildingInObject() {
		boolean includeOptional = true;
		boolean excludeOptional = false;
		
		PropertyObject object = PropertyBuilder.object()
			.add("required", "always here")
			.addIf(includeOptional, "optional1", "included")
			.addIf(excludeOptional, "optional2", "excluded")
			.addIf(includeOptional, "optionalFlag", true)
			.addIf(excludeOptional, "excludedFlag", false)
			.addIf(includeOptional, "optionalNumber", 42)
			.addIf(excludeOptional, "excludedNumber", 99)
			.buildObject();
		
		assertEquals(4, object.size());
		assertEquals(new PropertyValue("always here"), object.get("required"));
		assertEquals(new PropertyValue("included"), object.get("optional1"));
		assertEquals(new PropertyValue(true), object.get("optionalFlag"));
		assertEquals(new PropertyValue(42), object.get("optionalNumber"));
		
		assertFalse(object.containsKey("optional2"));
		assertFalse(object.containsKey("excludedFlag"));
		assertFalse(object.containsKey("excludedNumber"));
	}
	
	@Test
	void conditionalBuildingInArray() {
		boolean includeOptional = true;
		boolean excludeOptional = false;
		
		PropertyArray array = PropertyBuilder.array()
			.add("always")
			.addIf(includeOptional, "included")
			.addIf(excludeOptional, "excluded")
			.add("final")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new PropertyValue("always"), array.get(0));
		assertEquals(new PropertyValue("included"), array.get(1));
		assertEquals(new PropertyValue("final"), array.get(2));
	}
	
	@Test
	void buildMultipleTimesReturnsConsistentResults() {
		PropertyBuilder builder = PropertyBuilder.object()
			.add("name", "John")
			.add("age", 30);
		
		PropertyElement first = builder.build();
		PropertyElement second = builder.build();
		PropertyObject firstObject = builder.buildObject();
		PropertyObject secondObject = builder.buildObject();
		
		assertEquals(first, second);
		assertEquals(firstObject, secondObject);
	}
	
	@Test
	void contextValidationInObjectMode() {
		PropertyBuilder builder = PropertyBuilder.object();
		
		assertDoesNotThrow(() -> builder.add("key", "value"));
		assertDoesNotThrow(() -> builder.startObject("nested"));
		assertDoesNotThrow(() -> builder.endObject());
		
		assertThrows(IllegalStateException.class, () -> builder.add("value"));
		assertThrows(IllegalStateException.class, () -> builder.add(42));
		assertThrows(IllegalStateException.class, () -> builder.startObject());
		assertThrows(IllegalStateException.class, () -> builder.startArray());
	}
	
	@Test
	void contextValidationInArrayMode() {
		PropertyBuilder builder = PropertyBuilder.array();
		
		assertDoesNotThrow(() -> builder.add("value"));
		assertDoesNotThrow(() -> builder.add(42));
		assertDoesNotThrow(() -> builder.startObject());
		assertDoesNotThrow(() -> builder.endObject());
		
		assertThrows(IllegalStateException.class, () -> builder.add("key", "value"));
		assertThrows(IllegalStateException.class, () -> builder.startObject("key"));
		assertThrows(IllegalStateException.class, () -> builder.startArray("key"));
	}
	
	@Test
	void nestingStateValidation() {
		PropertyBuilder builder = PropertyBuilder.object();
		
		assertThrows(IllegalStateException.class, builder::endObject);
		assertThrows(IllegalStateException.class, builder::endArray);
		
		builder.startObject("nested");
		assertThrows(IllegalStateException.class, builder::build);
		assertThrows(IllegalStateException.class, builder::buildObject);
		
		builder.endObject();
		assertDoesNotThrow(builder::build);
	}
	
	@Test
	void nullKeyValidation() {
		PropertyBuilder builder = PropertyBuilder.object();
		
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.startObject(null));
		assertThrows(NullPointerException.class, () -> builder.startArray(null));
		assertThrows(NullPointerException.class, () -> builder.addObject(null, PropertyBuilder.object()));
		assertThrows(NullPointerException.class, () -> builder.addArray(null, PropertyBuilder.array()));
	}
	
	@Test
	void nullBuilderValidation() {
		PropertyBuilder objectBuilder = PropertyBuilder.object();
		PropertyBuilder arrayBuilder = PropertyBuilder.array();
		
		assertThrows(NullPointerException.class, () -> objectBuilder.addObject("key", null));
		assertThrows(NullPointerException.class, () -> objectBuilder.addArray("key", null));
		assertThrows(NullPointerException.class, () -> arrayBuilder.addObject(null));
		assertThrows(NullPointerException.class, () -> arrayBuilder.addArray(null));
	}
	
	@Test
	void buildTypeValidation() {
		PropertyBuilder objectBuilder = PropertyBuilder.object().add("key", "value");
		PropertyBuilder arrayBuilder = PropertyBuilder.array().add("value");
		
		assertDoesNotThrow(objectBuilder::buildObject);
		assertThrows(IllegalStateException.class, objectBuilder::buildArray);
		
		assertDoesNotThrow(arrayBuilder::buildArray);
		assertThrows(IllegalStateException.class, arrayBuilder::buildObject);
	}
	
	@Test
	void utilityMethods() {
		PropertyBuilder builder = PropertyBuilder.object();
		
		assertEquals(1, builder.getNestingDepth());
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertTrue(builder.isAtRootLevel());
		
		builder.startObject("nested");
		assertEquals(2, builder.getNestingDepth());
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertFalse(builder.isAtRootLevel());
		
		builder.startArray("items");
		assertEquals(3, builder.getNestingDepth());
		assertFalse(builder.isInObjectContext());
		assertTrue(builder.isInArrayContext());
		assertFalse(builder.isAtRootLevel());
		
		builder.endArray();
		assertEquals(2, builder.getNestingDepth());
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertFalse(builder.isAtRootLevel());
		
		builder.endObject();
		assertEquals(1, builder.getNestingDepth());
		assertTrue(builder.isInObjectContext());
		assertFalse(builder.isInArrayContext());
		assertTrue(builder.isAtRootLevel());
	}
	
	@Test
	void toStringMethods() {
		PropertyBuilder builder = PropertyBuilder.object()
			.add("name", "John")
			.add("age", 30);
		
		String defaultString = builder.toString();
		assertNotNull(defaultString);
		assertTrue(defaultString.contains("name"));
		assertTrue(defaultString.contains("John"));
		assertTrue(defaultString.contains("age"));
		assertTrue(defaultString.contains("30"));
		
		assertThrows(NullPointerException.class, () -> builder.toString(null));
	}
	
	@Test
	void complexRealWorldExample() {
		PropertyObject result = PropertyBuilder.object()
			.add("apiVersion", "1.0")
			.add("timestamp", System.currentTimeMillis())
			.startObject("user")
			.add("id", 12345)
			.add("username", "john_doe")
			.add("email", "john@example.com")
			.add("verified", true)
			.startObject("profile")
			.add("firstName", "John")
			.add("lastName", "Doe")
			.add("age", 30)
			.startArray("interests")
			.add("programming")
			.add("photography")
			.add("travel")
			.endArray()
			.endObject()
			.startArray("permissions")
			.add("read")
			.add("write")
			.endArray()
			.endObject()
			.startArray("recentActivity")
			.startObject()
			.add("action", "login")
			.add("timestamp", System.currentTimeMillis() - 3600000)
			.add("ip", "192.168.1.1")
			.endObject()
			.startObject()
			.add("action", "update_profile")
			.add("timestamp", System.currentTimeMillis() - 1800000)
			.add("changes", PropertyBuilder.array()
				.add("email")
				.add("firstName")
				.buildArray())
			.endObject()
			.endArray()
			.add("status", "active")
			.buildObject();
		
		assertEquals(5, result.size());
		assertTrue(result.containsKey("apiVersion"));
		assertTrue(result.containsKey("timestamp"));
		assertTrue(result.containsKey("user"));
		assertTrue(result.containsKey("recentActivity"));
		assertTrue(result.containsKey("status"));
		
		PropertyObject user = result.getAsPropertyObject("user");
		assertEquals(6, user.size());
		assertEquals(new PropertyValue(12345), user.get("id"));
		assertEquals(new PropertyValue("john_doe"), user.get("username"));
		assertEquals(new PropertyValue(true), user.get("verified"));
		
		PropertyObject profile = user.getAsPropertyObject("profile");
		assertEquals(4, profile.size());
		assertEquals(new PropertyValue("John"), profile.get("firstName"));
		
		PropertyArray interests = profile.getAsPropertyArray("interests");
		assertEquals(3, interests.size());
		assertEquals(new PropertyValue("programming"), interests.get(0));
		
		PropertyArray permissions = user.getAsPropertyArray("permissions");
		assertEquals(2, permissions.size());
		assertEquals(new PropertyValue("read"), permissions.get(0));
		assertEquals(new PropertyValue("write"), permissions.get(1));
		
		PropertyArray activity = result.getAsPropertyArray("recentActivity");
		assertEquals(2, activity.size());
		
		PropertyObject firstActivity = activity.getAsPropertyObject(0);
		assertEquals(new PropertyValue("login"), firstActivity.get("action"));
		
		PropertyObject secondActivity = activity.getAsPropertyObject(1);
		assertEquals(new PropertyValue("update_profile"), secondActivity.get("action"));
		PropertyArray changes = secondActivity.getAsPropertyArray("changes");
		assertEquals(2, changes.size());
		assertEquals(new PropertyValue("email"), changes.get(0));
	}
	
	@Test
	void builderReuseAfterBuild() {
		PropertyBuilder builder = PropertyBuilder.object()
			.add("initial", "value");
		
		PropertyObject first = builder.buildObject();
		assertEquals(1, first.size());
		
		builder.add("additional", "value");
		PropertyObject second = builder.buildObject();
		assertEquals(2, second.size());
		
		assertEquals(1, first.size());
		assertFalse(first.containsKey("additional"));
	}
	
	@Test
	void methodChainingConsistency() {
		PropertyBuilder builder = PropertyBuilder.object();
		PropertyBuilder returned = builder.add("key", "value");
		assertSame(builder, returned);
		
		returned = builder.startObject("nested");
		assertSame(builder, returned);
		
		returned = builder.endObject();
		assertSame(builder, returned);
		
		returned = builder.addIf(true, "conditional", "value");
		assertSame(builder, returned);
	}
}
