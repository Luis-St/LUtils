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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FieldCodec}.<br>
 *
 * @author Luis-St
 */
class FieldCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("test");
		
		assertThrows(NullPointerException.class, () -> fieldCodec.encode(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.encode(typeProvider, null, obj));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode component because the component can not be retrieved from a null object"));
	}
	
	@Test
	void encodeWithValidObject() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("John");
		
		JsonObject current = new JsonObject();
		JsonElement result = fieldCodec.encode(typeProvider, current, obj);
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleFields() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectWithAge> nameCodec = STRING.fieldOf("name", TestObjectWithAge::name);
		FieldCodec<Integer, TestObjectWithAge> ageCodec = INTEGER.fieldOf("age", TestObjectWithAge::age);
		TestObjectWithAge obj = new TestObjectWithAge("John", 25);
		
		JsonObject current = new JsonObject();
		nameCodec.encode(typeProvider, current, obj);
		ageCodec.encode(typeProvider, current, obj);
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		expected.add("age", new JsonPrimitive(25));
		assertEquals(expected, current);
	}
	
	@Test
	void encodeWithNullFieldValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectNullable> fieldCodec = STRING.fieldOf("name", TestObjectNullable::name);
		TestObjectNullable obj = new TestObjectNullable(null);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> fieldCodec.encode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Unable to encode named 'name'"));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		assertThrows(NullPointerException.class, () -> fieldCodec.decode(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.decode(typeProvider, null, obj));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode named 'name' null value"));
	}
	
	@Test
	void decodeWithValidObject() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void decodeWithMissingField() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Name 'name' not found"));
	}
	
	@Test
	void decodeWithAlias() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("username", new JsonPrimitive("John"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void decodeWithMultipleAliases() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user", "displayName"), TestObject::name);
		
		JsonObject obj1 = new JsonObject();
		obj1.add("username", new JsonPrimitive("John"));
		assertEquals("John", fieldCodec.decode(typeProvider, typeProvider.empty(), obj1));
		
		JsonObject obj2 = new JsonObject();
		obj2.add("user", new JsonPrimitive("Jane"));
		assertEquals("Jane", fieldCodec.decode(typeProvider, typeProvider.empty(), obj2));
		
		JsonObject obj3 = new JsonObject();
		obj3.add("displayName", new JsonPrimitive("Bob"));
		assertEquals("Bob", fieldCodec.decode(typeProvider, typeProvider.empty(), obj3));
	}
	
	@Test
	void decodeWithNoMatchingAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("other", new JsonPrimitive("John"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> fieldCodec.decode(typeProvider, typeProvider.empty(), obj));
		assertTrue(exception.getMessage().contains("Name and aliases 'name'"));
		assertTrue(exception.getMessage().contains("not found"));
	}
	
	@Test
	void decodePrefersPrimaryName() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		obj.add("username", new JsonPrimitive("Jane"));
		
		String result = fieldCodec.decode(typeProvider, typeProvider.empty(), obj);
		assertEquals("John", result);
	}
	
	@Test
	void equalsAndHashCode() {
		FieldCodec<String, TestObject> codec1 = STRING.fieldOf("name", TestObject::name);
		FieldCodec<String, TestObject> codec2 = STRING.fieldOf("name", TestObject::name);
		FieldCodec<String, TestObject> codec3 = STRING.fieldOf("other", TestObject::name);
		FieldCodec<String, TestObject> codec4 = STRING.fieldOf("name", "alias", TestObject::name);
		
		assertEquals(codec1, codec2);
		
		assertNotEquals(codec1, codec3);
		assertNotEquals(codec1, codec4);
	}
	
	@Test
	void toStringRepresentation() {
		FieldCodec<String, TestObject> codec1 = STRING.fieldOf("name", TestObject::name);
		String result1 = codec1.toString();
		assertTrue(result1.startsWith("NamedCodec['name'"));
		assertTrue(result1.contains("StringCodec"));
		
		FieldCodec<String, TestObject> codec2 = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		String result2 = codec2.toString();
		assertTrue(result2.startsWith("NamedCodec['name'"));
		assertTrue(result2.contains("[username, user]") || result2.contains("[user, username]"));
		assertTrue(result2.contains("StringCodec"));
	}
	
	private record TestObject(@NonNull String name) {}
	
	private record TestObjectWithAge(@NonNull String name, int age) {}
	
	private record TestObjectNullable(String name) {}
}
