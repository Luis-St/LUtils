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

package net.luis.utils.io.data.json;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonBuilder}.<br>
 *
 * @author Luis-St
 */
class JsonBuilderTest {
	
	private static final JsonConfig COMPACT_CONFIG = new JsonConfig(true, false, "", false, 0, false, 0, StandardCharsets.UTF_8);
	
	@Test
	void createEmptyObject() {
		JsonObject object = JsonBuilder.object().buildObject();
		
		assertTrue(object.isEmpty());
		assertEquals(0, object.size());
		assertEquals("{}", object.toString());
	}
	
	@Test
	void createEmptyArray() {
		JsonArray array = JsonBuilder.array().buildArray();
		
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
		assertEquals("[]", array.toString());
	}
	
	@Test
	void buildSimpleObject() {
		JsonObject object = JsonBuilder.object()
			.add("name", "John Doe")
			.add("age", 30)
			.add("active", true)
			.buildObject();
		
		assertEquals(3, object.size());
		assertEquals(new JsonPrimitive("John Doe"), object.get("name"));
		assertEquals(new JsonPrimitive(30), object.get("age"));
		assertEquals(new JsonPrimitive(true), object.get("active"));
	}
	
	@Test
	void buildSimpleArray() {
		JsonArray array = JsonBuilder.array()
			.add("first")
			.add("second")
			.add("third")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new JsonPrimitive("first"), array.get(0));
		assertEquals(new JsonPrimitive("second"), array.get(1));
		assertEquals(new JsonPrimitive("third"), array.get(2));
	}
	
	@Test
	void addAllPrimitiveTypesToObject() {
		JsonObject object = JsonBuilder.object()
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
		assertEquals(new JsonPrimitive("test"), object.get("string"));
		assertEquals(JsonNull.INSTANCE, object.get("nullString"));
		assertEquals(new JsonPrimitive(true), object.get("boolean"));
		assertEquals(new JsonPrimitive(42), object.get("number"));
		assertEquals(JsonNull.INSTANCE, object.get("nullNumber"));
		assertEquals(new JsonPrimitive((byte) 1), object.get("byte"));
		assertEquals(new JsonPrimitive((short) 2), object.get("short"));
		assertEquals(new JsonPrimitive(3), object.get("int"));
		assertEquals(new JsonPrimitive(4L), object.get("long"));
		assertEquals(new JsonPrimitive(5.5f), object.get("float"));
		assertEquals(new JsonPrimitive(6.6), object.get("double"));
	}
	
	@Test
	void addAllPrimitiveTypesToArray() {
		JsonArray array = JsonBuilder.array()
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
		assertEquals(new JsonPrimitive("test"), array.get(0));
		assertEquals(JsonNull.INSTANCE, array.get(1));
		assertEquals(new JsonPrimitive(true), array.get(2));
		assertEquals(new JsonPrimitive(42), array.get(3));
		assertEquals(JsonNull.INSTANCE, array.get(4));
		assertEquals(new JsonPrimitive((byte) 1), array.get(5));
		assertEquals(new JsonPrimitive((short) 2), array.get(6));
		assertEquals(new JsonPrimitive(3), array.get(7));
		assertEquals(new JsonPrimitive(4L), array.get(8));
		assertEquals(new JsonPrimitive(5.5f), array.get(9));
		assertEquals(new JsonPrimitive(6.6), array.get(10));
	}
	
	@Test
	void addJsonElementsDirectly() {
		JsonElement customElement = new JsonPrimitive("custom");
		
		JsonObject object = JsonBuilder.object()
			.add("element", customElement)
			.add("null", (JsonElement) null)
			.buildObject();
		
		assertEquals(2, object.size());
		assertEquals(customElement, object.get("element"));
		assertEquals(JsonNull.INSTANCE, object.get("null"));
		
		JsonArray array = JsonBuilder.array()
			.add(customElement)
			.add((JsonElement) null)
			.buildArray();
		
		assertEquals(2, array.size());
		assertEquals(customElement, array.get(0));
		assertEquals(JsonNull.INSTANCE, array.get(1));
	}
	
	@Test
	void addBuilderResultsToObjectAndArray() {
		JsonBuilder nestedObjectBuilder = JsonBuilder.object()
			.add("nested", "value");
		
		JsonBuilder nestedArrayBuilder = JsonBuilder.array()
			.add(1)
			.add(2);
		
		JsonObject object = JsonBuilder.object()
			.addObject("obj", nestedObjectBuilder)
			.addArray("arr", nestedArrayBuilder)
			.buildObject();
		
		assertEquals(2, object.size());
		JsonObject nestedObj = object.getAsJsonObject("obj");
		assertEquals(new JsonPrimitive("value"), nestedObj.get("nested"));
		
		JsonArray nestedArr = object.getAsJsonArray("arr");
		assertEquals(2, nestedArr.size());
		assertEquals(new JsonPrimitive(1), nestedArr.get(0));
		
		JsonArray array = JsonBuilder.array()
			.addObject(nestedObjectBuilder)
			.addArray(nestedArrayBuilder)
			.buildArray();
		
		assertEquals(2, array.size());
		assertTrue(array.get(0) instanceof JsonObject);
		assertTrue(array.get(1) instanceof JsonArray);
	}
	
	@Test
	void addAllArrayConvenienceMethods() {
		JsonArray array = JsonBuilder.array()
			.addAll("first", "second", "third")
			.addAll(1, 2, 3)
			.addAll(true, false, true)
			.buildArray();
		
		assertEquals(9, array.size());
		
		assertEquals(new JsonPrimitive("first"), array.get(0));
		assertEquals(new JsonPrimitive("second"), array.get(1));
		assertEquals(new JsonPrimitive("third"), array.get(2));
		
		assertEquals(new JsonPrimitive(1), array.get(3));
		assertEquals(new JsonPrimitive(2), array.get(4));
		assertEquals(new JsonPrimitive(3), array.get(5));
		
		assertEquals(new JsonPrimitive(true), array.get(6));
		assertEquals(new JsonPrimitive(false), array.get(7));
		assertEquals(new JsonPrimitive(true), array.get(8));
	}
	
	@Test
	void nestedObjectInObject() {
		JsonObject object = JsonBuilder.object()
			.add("name", "John")
			.startObject("address")
			.add("street", "123 Main St")
			.add("city", "Anytown")
			.add("zipcode", 12345)
			.endObject()
			.add("age", 30)
			.buildObject();
		
		assertEquals(3, object.size());
		assertEquals(new JsonPrimitive("John"), object.get("name"));
		assertEquals(new JsonPrimitive(30), object.get("age"));
		
		JsonObject address = object.getAsJsonObject("address");
		assertEquals(3, address.size());
		assertEquals(new JsonPrimitive("123 Main St"), address.get("street"));
		assertEquals(new JsonPrimitive("Anytown"), address.get("city"));
		assertEquals(new JsonPrimitive(12345), address.get("zipcode"));
	}
	
	@Test
	void nestedArrayInObject() {
		JsonObject object = JsonBuilder.object()
			.add("name", "John")
			.startArray("hobbies")
			.add("reading")
			.add("swimming")
			.add("coding")
			.endArray()
			.add("age", 30)
			.buildObject();
		
		assertEquals(3, object.size());
		
		JsonArray hobbies = object.getAsJsonArray("hobbies");
		assertEquals(3, hobbies.size());
		assertEquals(new JsonPrimitive("reading"), hobbies.get(0));
		assertEquals(new JsonPrimitive("swimming"), hobbies.get(1));
		assertEquals(new JsonPrimitive("coding"), hobbies.get(2));
	}
	
	@Test
	void nestedObjectInArray() {
		JsonArray array = JsonBuilder.array()
			.add("first")
			.startObject()
			.add("id", 1)
			.add("name", "Item 1")
			.endObject()
			.add("last")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new JsonPrimitive("first"), array.get(0));
		assertEquals(new JsonPrimitive("last"), array.get(2));
		
		JsonObject item = array.getAsJsonObject(1);
		assertEquals(2, item.size());
		assertEquals(new JsonPrimitive(1), item.get("id"));
		assertEquals(new JsonPrimitive("Item 1"), item.get("name"));
	}
	
	@Test
	void nestedArrayInArray() {
		JsonArray array = JsonBuilder.array()
			.add("outer")
			.startArray()
			.add("inner1")
			.add("inner2")
			.endArray()
			.add("final")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new JsonPrimitive("outer"), array.get(0));
		assertEquals(new JsonPrimitive("final"), array.get(2));
		
		JsonArray inner = array.getAsJsonArray(1);
		assertEquals(2, inner.size());
		assertEquals(new JsonPrimitive("inner1"), inner.get(0));
		assertEquals(new JsonPrimitive("inner2"), inner.get(1));
	}
	
	@Test
	void deeplyNestedStructure() {
		JsonObject object = JsonBuilder.object()
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
		assertEquals(new JsonPrimitive(1), object.get("level"));
		
		JsonObject nested = object.getAsJsonObject("nested");
		assertEquals(new JsonPrimitive(2), nested.get("level"));
		
		JsonArray items = nested.getAsJsonArray("items");
		assertEquals(1, items.size());
		
		JsonObject item = items.getAsJsonObject(0);
		assertEquals(new JsonPrimitive(3), item.get("level"));
		
		JsonObject deep = item.getAsJsonObject("deep");
		assertEquals(new JsonPrimitive(4), deep.get("level"));
		assertEquals(new JsonPrimitive("deepest"), deep.get("value"));
	}
	
	@Test
	void conditionalBuildingInObject() {
		boolean includeOptional = true;
		boolean excludeOptional = false;
		
		JsonObject object = JsonBuilder.object()
			.add("required", "always here")
			.addIf(includeOptional, "optional1", "included")
			.addIf(excludeOptional, "optional2", "excluded")
			.addIf(includeOptional, "optionalFlag", true)
			.addIf(excludeOptional, "excludedFlag", false)
			.addIf(includeOptional, "optionalNumber", 42)
			.addIf(excludeOptional, "excludedNumber", 99)
			.buildObject();
		
		assertEquals(4, object.size());
		assertEquals(new JsonPrimitive("always here"), object.get("required"));
		assertEquals(new JsonPrimitive("included"), object.get("optional1"));
		assertEquals(new JsonPrimitive(true), object.get("optionalFlag"));
		assertEquals(new JsonPrimitive(42), object.get("optionalNumber"));
		
		assertFalse(object.containsKey("optional2"));
		assertFalse(object.containsKey("excludedFlag"));
		assertFalse(object.containsKey("excludedNumber"));
	}
	
	@Test
	void conditionalBuildingInArray() {
		boolean includeOptional = true;
		boolean excludeOptional = false;
		
		JsonArray array = JsonBuilder.array()
			.add("always")
			.addIf(includeOptional, "included")
			.addIf(excludeOptional, "excluded")
			.add("final")
			.buildArray();
		
		assertEquals(3, array.size());
		assertEquals(new JsonPrimitive("always"), array.get(0));
		assertEquals(new JsonPrimitive("included"), array.get(1));
		assertEquals(new JsonPrimitive("final"), array.get(2));
	}
	
	@Test
	void buildMultipleTimesReturnsConsistentResults() {
		JsonBuilder builder = JsonBuilder.object()
			.add("name", "John")
			.add("age", 30);
		
		JsonElement first = builder.build();
		JsonElement second = builder.build();
		JsonObject firstObject = builder.buildObject();
		JsonObject secondObject = builder.buildObject();
		
		assertEquals(first, second);
		assertEquals(firstObject, secondObject);
		assertEquals(first, firstObject);
		
		builder.add("city", "New York");
		JsonObject modified = builder.buildObject();
		assertEquals(3, modified.size());
		assertEquals(new JsonPrimitive("New York"), modified.get("city"));
	}
	
	@Test
	void contextValidationInObjectMode() {
		JsonBuilder builder = JsonBuilder.object();
		
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
		JsonBuilder builder = JsonBuilder.array();
		
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
		JsonBuilder builder = JsonBuilder.object();
		
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
		JsonBuilder builder = JsonBuilder.object();
		
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.startObject(null));
		assertThrows(NullPointerException.class, () -> builder.startArray(null));
		assertThrows(NullPointerException.class, () -> builder.addObject(null, JsonBuilder.object()));
		assertThrows(NullPointerException.class, () -> builder.addArray(null, JsonBuilder.array()));
	}
	
	@Test
	void nullBuilderValidation() {
		JsonBuilder objectBuilder = JsonBuilder.object();
		JsonBuilder arrayBuilder = JsonBuilder.array();
		
		assertThrows(NullPointerException.class, () -> objectBuilder.addObject("key", null));
		assertThrows(NullPointerException.class, () -> objectBuilder.addArray("key", null));
		assertThrows(NullPointerException.class, () -> arrayBuilder.addObject(null));
		assertThrows(NullPointerException.class, () -> arrayBuilder.addArray(null));
	}
	
	@Test
	void buildTypeValidation() {
		JsonBuilder objectBuilder = JsonBuilder.object().add("key", "value");
		JsonBuilder arrayBuilder = JsonBuilder.array().add("value");
		
		assertDoesNotThrow(objectBuilder::buildObject);
		assertThrows(IllegalStateException.class, objectBuilder::buildArray);
		
		assertDoesNotThrow(arrayBuilder::buildArray);
		assertThrows(IllegalStateException.class, arrayBuilder::buildObject);
	}
	
	@Test
	void utilityMethods() {
		JsonBuilder builder = JsonBuilder.object();
		
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
		JsonBuilder builder = JsonBuilder.object()
			.add("name", "John")
			.add("age", 30);
		
		String defaultString = builder.toString();
		assertNotNull(defaultString);
		assertTrue(defaultString.contains("\"name\""));
		assertTrue(defaultString.contains("\"John\""));
		assertTrue(defaultString.contains("\"age\""));
		assertTrue(defaultString.contains("30"));
		
		String compactString = builder.toString(COMPACT_CONFIG);
		assertNotNull(compactString);
		assertFalse(compactString.contains(System.lineSeparator()));
		assertTrue(compactString.contains("\"name\": \"John\""));
		
		assertThrows(NullPointerException.class, () -> builder.toString(null));
	}
	
	@Test
	void complexRealWorldExample() {
		JsonObject result = JsonBuilder.object()
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
			.add("changes", JsonBuilder.array()
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
		
		JsonObject user = result.getAsJsonObject("user");
		assertEquals(6, user.size());
		assertEquals(new JsonPrimitive(12345), user.get("id"));
		assertEquals(new JsonPrimitive("john_doe"), user.get("username"));
		assertEquals(new JsonPrimitive(true), user.get("verified"));
		
		JsonObject profile = user.getAsJsonObject("profile");
		assertEquals(4, profile.size());
		assertEquals(new JsonPrimitive("John"), profile.get("firstName"));
		
		JsonArray interests = profile.getAsJsonArray("interests");
		assertEquals(3, interests.size());
		assertEquals(new JsonPrimitive("programming"), interests.get(0));
		
		JsonArray permissions = user.getAsJsonArray("permissions");
		assertEquals(2, permissions.size());
		assertEquals(new JsonPrimitive("read"), permissions.get(0));
		assertEquals(new JsonPrimitive("write"), permissions.get(1));
		
		JsonArray activity = result.getAsJsonArray("recentActivity");
		assertEquals(2, activity.size());
		
		JsonObject firstActivity = activity.getAsJsonObject(0);
		assertEquals(new JsonPrimitive("login"), firstActivity.get("action"));
		
		JsonObject secondActivity = activity.getAsJsonObject(1);
		assertEquals(new JsonPrimitive("update_profile"), secondActivity.get("action"));
		JsonArray changes = secondActivity.getAsJsonArray("changes");
		assertEquals(2, changes.size());
		assertEquals(new JsonPrimitive("email"), changes.get(0));
	}
	
	@Test
	void builderReuseAfterBuild() {
		JsonBuilder builder = JsonBuilder.object()
			.add("initial", "value");
		
		JsonObject first = builder.buildObject();
		assertEquals(1, first.size());
		
		builder.add("additional", "value");
		JsonObject second = builder.buildObject();
		assertEquals(2, second.size());
		
		assertEquals(1, first.size());
		assertFalse(first.containsKey("additional"));
	}
	
	@Test
	void methodChainingConsistency() {
		JsonBuilder builder = JsonBuilder.object();
		JsonBuilder returned = builder.add("key", "value");
		assertSame(builder, returned);
		
		returned = builder.startObject("nested");
		assertSame(builder, returned);
		
		returned = builder.endObject();
		assertSame(builder, returned);
		
		returned = builder.addIf(true, "conditional", "value");
		assertSame(builder, returned);
	}
}
