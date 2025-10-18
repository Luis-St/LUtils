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

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonWriter}.<br>
 *
 * @author Luis-St
 */
class JsonWriterTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, false, "  ", false, 5, false, 2, StandardCharsets.UTF_16);
	
	@Test
	void constructorValidation() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertThrows(NullPointerException.class, () -> new JsonWriter(null));
		assertThrows(NullPointerException.class, () -> new JsonWriter(null, JsonConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new JsonWriter(provider, null));
		
		assertDoesNotThrow(() -> new JsonWriter(provider));
		assertDoesNotThrow(() -> new JsonWriter(provider, JsonConfig.DEFAULT));
		assertDoesNotThrow(() -> new JsonWriter(provider, CUSTOM_CONFIG));
	}
	
	@Test
	void writeJsonNull() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(JsonNull.INSTANCE);
		assertEquals("null", stream.toString());
		
		stream.reset();
		writer.writeJson(JsonNull.INSTANCE);
		writer.writeJson(JsonNull.INSTANCE);
		assertEquals("nullnull", stream.toString());
	}
	
	@Test
	void writeJsonNullValidation() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		assertThrows(NullPointerException.class, () -> writer.writeJson(null));
	}
	
	@Test
	void writeJsonPrimitiveBoolean() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(new JsonPrimitive(true));
		assertEquals("true", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(false));
		assertEquals("false", stream.toString());
	}
	
	@Test
	void writeJsonPrimitiveNumbers() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(new JsonPrimitive(42));
		assertEquals("42", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(3.14f));
		assertEquals("3.14", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(2.718));
		assertEquals("2.718", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(-42));
		assertEquals("-42", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(0));
		assertEquals("0", stream.toString());
	}
	
	@Test
	void writeJsonPrimitiveSpecialNumbers() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(Double.POSITIVE_INFINITY));
		assertEquals("Infinity", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(Double.NEGATIVE_INFINITY));
		assertEquals("-Infinity", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(Double.NaN));
		assertEquals("NaN", stream.toString());
	}
	
	@Test
	void writeJsonPrimitiveStrings() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(new JsonPrimitive("hello"));
		assertEquals("\"hello\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive(""));
		assertEquals("\"\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive("hello world"));
		assertEquals("\"hello world\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive("hello\nworld"));
		assertEquals("\"hello\nworld\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive("123"));
		assertEquals("\"123\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive("true"));
		assertEquals("\"true\"", stream.toString());
		
		stream.reset();
		writer.writeJson(new JsonPrimitive("not_a_number"));
		assertEquals("\"not_a_number\"", stream.toString());
	}
	
	@Test
	void writeJsonArrayEmpty() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray array = new JsonArray();
		writer.writeJson(array);
		assertEquals("[]", stream.toString());
	}
	
	@Test
	void writeJsonArraySingleElement() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		writer.writeJson(array);
		assertEquals("[42]", stream.toString());
	}
	
	@Test
	void writeJsonArrayMultipleElements() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		writer.writeJson(array);
		assertEquals("[1, 2, 3]", stream.toString());
	}
	
	@Test
	void writeJsonArrayMixedTypes() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(new JsonPrimitive("hello"));
		array.add(new JsonPrimitive(true));
		array.add(JsonNull.INSTANCE);
		writer.writeJson(array);
		assertEquals("[42, \"hello\", true, null]", stream.toString());
	}
	
	@Test
	void writeJsonArrayNested() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray innerArray1 = new JsonArray();
		innerArray1.add(new JsonPrimitive(1));
		innerArray1.add(new JsonPrimitive(2));
		
		JsonArray innerArray2 = new JsonArray();
		innerArray2.add(new JsonPrimitive(3));
		innerArray2.add(new JsonPrimitive(4));
		
		JsonArray outerArray = new JsonArray();
		outerArray.add(innerArray1);
		outerArray.add(innerArray2);
		
		writer.writeJson(outerArray);
		assertEquals("[[1, 2], [3, 4]]", stream.toString());
	}
	
	@Test
	void writeJsonObjectEmpty() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonObject object = new JsonObject();
		writer.writeJson(object);
		assertEquals("{}", stream.toString());
	}
	
	@Test
	void writeJsonObjectSingleProperty() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonObject object = new JsonObject();
		object.add("key", new JsonPrimitive("value"));
		writer.writeJson(object);
		assertEquals("{ \"key\": \"value\" }", stream.toString());
	}
	
	@Test
	void writeJsonObjectMultipleProperties() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream), CUSTOM_CONFIG);
		
		JsonObject object = new JsonObject();
		object.add("name", new JsonPrimitive("John"));
		object.add("age", new JsonPrimitive(30));
		object.add("active", new JsonPrimitive(true));
		
		writer.writeJson(object);
		String result = stream.toString();
		
		assertFalse(result.contains(System.lineSeparator()));
		assertTrue(result.contains("\"name\": \"John\""));
		assertTrue(result.contains("\"age\": 30"));
		assertTrue(result.contains("\"active\": true"));
	}
	
	@Test
	void writeJsonObjectNested() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonObject innerObject = new JsonObject();
		innerObject.add("city", new JsonPrimitive("New York"));
		innerObject.add("country", new JsonPrimitive("USA"));
		
		JsonObject outerObject = new JsonObject();
		outerObject.add("name", new JsonPrimitive("John"));
		outerObject.add("address", innerObject);
		
		writer.writeJson(outerObject);
		String result = stream.toString();
		
		assertTrue(result.contains("\"name\": \"John\""));
		assertTrue(result.contains("\"address\":"));
		assertTrue(result.contains("\"city\": \"New York\""));
		assertTrue(result.contains("\"country\": \"USA\""));
	}
	
	@Test
	void writeJsonObjectWithArrays() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray numbers = new JsonArray();
		numbers.add(new JsonPrimitive(1));
		numbers.add(new JsonPrimitive(2));
		numbers.add(new JsonPrimitive(3));
		
		JsonObject object = new JsonObject();
		object.add("name", new JsonPrimitive("Test"));
		object.add("numbers", numbers);
		
		writer.writeJson(object);
		String result = stream.toString();
		
		assertTrue(result.contains("\"name\": \"Test\""));
		assertTrue(result.contains("\"numbers\": [1, 2, 3]"));
	}
	
	@Test
	void writeJsonComplexStructure() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonObject profile = new JsonObject();
		profile.add("age", new JsonPrimitive(30));
		profile.add("email", new JsonPrimitive("john@example.com"));
		
		JsonObject user1 = new JsonObject();
		user1.add("id", new JsonPrimitive(1));
		user1.add("name", new JsonPrimitive("John Doe"));
		user1.add("active", new JsonPrimitive(true));
		user1.add("profile", profile);
		
		JsonArray users = new JsonArray();
		users.add(user1);
		
		JsonObject meta = new JsonObject();
		meta.add("total", new JsonPrimitive(1));
		meta.add("page", new JsonPrimitive(1));
		
		JsonObject root = new JsonObject();
		root.add("users", users);
		root.add("meta", meta);
		
		writer.writeJson(root);
		String result = stream.toString();
		
		assertTrue(result.contains("\"users\":"));
		assertTrue(result.contains("\"meta\":"));
		assertTrue(result.contains("\"id\": 1"));
		assertTrue(result.contains("\"name\": \"John Doe\""));
		assertTrue(result.contains("\"active\": true"));
		assertTrue(result.contains("\"email\": \"john@example.com\""));
		assertTrue(result.contains("\"total\": 1"));
	}
	
	@Test
	void writeJsonWithDifferentConfigs() {
		JsonObject object = new JsonObject();
		object.add("key1", new JsonPrimitive("value1"));
		object.add("key2", new JsonPrimitive(42));
		
		StringOutputStream stream1 = new StringOutputStream();
		JsonWriter writer1 = new JsonWriter(new OutputProvider(stream1));
		writer1.writeJson(object);
		String result1 = stream1.toString();
		assertTrue(result1.contains(System.lineSeparator()));
		
		StringOutputStream stream2 = new StringOutputStream();
		JsonWriter writer2 = new JsonWriter(new OutputProvider(stream2), CUSTOM_CONFIG);
		writer2.writeJson(object);
		String result2 = stream2.toString();
		assertFalse(result2.contains(System.lineSeparator()));
	}
	
	@Test
	void writeJsonWithDifferentCharsets() {
		JsonObject object = new JsonObject();
		object.add("message", new JsonPrimitive("Hello 世界"));
		
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		JsonWriter writer1 = new JsonWriter(new OutputProvider(os1), JsonConfig.DEFAULT);
		writer1.writeJson(object);
		String result1 = os1.toString(StandardCharsets.UTF_8);
		assertTrue(result1.contains("Hello 世界"));
		
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		JsonWriter writer2 = new JsonWriter(new OutputProvider(os2), CUSTOM_CONFIG);
		writer2.writeJson(object);
		String result2 = os2.toString(StandardCharsets.UTF_16);
		assertTrue(result2.contains("Hello 世界"));
	}
	
	@Test
	void writeMultipleJsonElements() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		writer.writeJson(new JsonPrimitive(42));
		writer.writeJson(new JsonPrimitive("hello"));
		writer.writeJson(JsonNull.INSTANCE);
		
		assertEquals("42\"hello\"null", stream.toString());
	}
	
	@Test
	void writeJsonLargeData() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray largeArray = new JsonArray();
		for (int i = 0; i < 1000; i++) {
			largeArray.add(new JsonPrimitive(i));
		}
		
		JsonObject object = new JsonObject();
		object.add("data", largeArray);
		
		assertDoesNotThrow(() -> writer.writeJson(object));
		
		String result = stream.toString();
		assertTrue(result.contains("\"data\":"));
		assertTrue(result.contains("0"));
		assertTrue(result.contains("999"));
	}
	
	@Test
	void writeJsonSpecialValues() {
		StringOutputStream stream = new StringOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(stream));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(Double.POSITIVE_INFINITY));
		array.add(new JsonPrimitive(Double.NEGATIVE_INFINITY));
		array.add(new JsonPrimitive(Double.NaN));
		array.add(new JsonPrimitive(Double.MAX_VALUE));
		array.add(new JsonPrimitive(Double.MIN_VALUE));
		
		writer.writeJson(array);
		String result = stream.toString();
		
		assertTrue(result.contains("Infinity"));
		assertTrue(result.contains("-Infinity"));
		assertTrue(result.contains("NaN"));
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new JsonWriter(new OutputProvider(OutputStream.nullOutputStream())).close());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(os));
		assertDoesNotThrow(writer::close);
		
		assertDoesNotThrow(writer::close);
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void flushingBehavior() {
		FlushTrackingOutputStream trackingStream = new FlushTrackingOutputStream();
		JsonWriter writer = new JsonWriter(new OutputProvider(trackingStream));
		
		writer.writeJson(new JsonPrimitive("test"));
		
		assertTrue(trackingStream.wasFlushCalled());
	}
	
	//region Helper Classes
	private static class StringOutputStream extends OutputStream {
		
		private final StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(int b) {
			if (b == 0) {
				return;
			}
			this.builder.append((char) b);
		}
		
		public void reset() {
			this.builder.setLength(0);
		}
		
		@Override
		public String toString() {
			return this.builder.toString();
		}
	}
	
	private static class FlushTrackingOutputStream extends OutputStream {
		
		private boolean flushCalled;
		
		@Override
		public void write(int b) {
			// Do nothing
		}
		
		@Override
		public void flush() {
			this.flushCalled = true;
		}
		
		public boolean wasFlushCalled() {
			return this.flushCalled;
		}
	}
	//endregion
}
