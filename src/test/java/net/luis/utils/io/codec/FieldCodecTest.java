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

package net.luis.utils.io.codec;

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("test");
		
		assertThrows(NullPointerException.class, () -> fieldCodec.encodeStart(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.encodeStart(typeProvider, null, obj));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		Result<JsonElement> result = fieldCodec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode component because the component can not be retrieved from a null object"));
	}
	
	@Test
	void encodeStartWithValidObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		TestObject obj = new TestObject("John");
		
		JsonObject current = new JsonObject();
		Result<JsonElement> result = fieldCodec.encodeStart(typeProvider, current, obj);
		assertTrue(result.isSuccess());
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultipleFields() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectWithAge> nameCodec = STRING.fieldOf("name", TestObjectWithAge::name);
		FieldCodec<Integer, TestObjectWithAge> ageCodec = INTEGER.fieldOf("age", TestObjectWithAge::age);
		TestObjectWithAge obj = new TestObjectWithAge("John", 25);
		
		JsonObject current = new JsonObject();
		Result<JsonElement> nameResult = nameCodec.encodeStart(typeProvider, current, obj);
		assertTrue(nameResult.isSuccess());
		
		Result<JsonElement> ageResult = ageCodec.encodeStart(typeProvider, current, obj);
		assertTrue(ageResult.isSuccess());
		
		JsonObject expected = new JsonObject();
		expected.add("name", new JsonPrimitive("John"));
		expected.add("age", new JsonPrimitive(25));
		assertEquals(expected, current);
	}
	
	@Test
	void encodeStartWithNullFieldValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObjectNullable> fieldCodec = STRING.fieldOf("name", TestObjectNullable::name);
		TestObjectNullable obj = new TestObjectNullable(null);
		
		Result<JsonElement> result = fieldCodec.encodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode named 'name'"));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		assertThrows(NullPointerException.class, () -> fieldCodec.decodeStart(null, typeProvider.empty(), obj));
		assertThrows(NullPointerException.class, () -> fieldCodec.decodeStart(typeProvider, null, obj));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode named 'name' null value"));
	}
	
	@Test
	void decodeStartWithValidObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isSuccess());
		assertEquals("John", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMissingField() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", TestObject::name);
		
		JsonObject obj = new JsonObject();
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Name 'name' not found"));
	}
	
	@Test
	void decodeStartWithAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("username", new JsonPrimitive("John"));
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isSuccess());
		assertEquals("John", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleAliases() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user", "displayName"), TestObject::name);
		
		JsonObject obj1 = new JsonObject();
		obj1.add("username", new JsonPrimitive("John"));
		Result<String> result1 = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj1);
		assertTrue(result1.isSuccess());
		assertEquals("John", result1.resultOrThrow());
		
		JsonObject obj2 = new JsonObject();
		obj2.add("user", new JsonPrimitive("Jane"));
		Result<String> result2 = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj2);
		assertTrue(result2.isSuccess());
		assertEquals("Jane", result2.resultOrThrow());
		
		JsonObject obj3 = new JsonObject();
		obj3.add("displayName", new JsonPrimitive("Bob"));
		Result<String> result3 = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj3);
		assertTrue(result3.isSuccess());
		assertEquals("Bob", result3.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNoMatchingAlias() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", Set.of("username", "user"), TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("other", new JsonPrimitive("John"));
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Name and aliases 'name'"));
		assertTrue(result.errorOrThrow().contains("not found"));
	}
	
	@Test
	void decodeStartPrefersPrimaryName() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		FieldCodec<String, TestObject> fieldCodec = STRING.fieldOf("name", "username", TestObject::name);
		
		JsonObject obj = new JsonObject();
		obj.add("name", new JsonPrimitive("John"));
		obj.add("username", new JsonPrimitive("Jane"));
		
		Result<String> result = fieldCodec.decodeStart(typeProvider, typeProvider.empty(), obj);
		assertTrue(result.isSuccess());
		assertEquals("John", result.resultOrThrow());
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
	
	private record TestObject(@NotNull String name) {}
	
	private record TestObjectWithAge(@NotNull String name, int age) {}
	
	private record TestObjectNullable(String name) {}
}
