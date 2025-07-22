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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.json.exception.JsonSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonReader}.<br>
 *
 * @author Luis-St
 */
class JsonReaderTest {
	
	private static final JsonConfig STRICT_CONFIG = JsonConfig.DEFAULT;
	private static final JsonConfig NON_STRICT_CONFIG = new JsonConfig(false, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8);
	
	@Test
	void constructorWithString() {
		assertThrows(NullPointerException.class, () -> new JsonReader((String) null));
		assertThrows(NullPointerException.class, () -> new JsonReader((String) null, STRICT_CONFIG));
		assertThrows(NullPointerException.class, () -> new JsonReader("test", null));
		
		assertDoesNotThrow(() -> new JsonReader("{}"));
		assertDoesNotThrow(() -> new JsonReader("{}", STRICT_CONFIG));
		assertDoesNotThrow(() -> new JsonReader("[]", NON_STRICT_CONFIG));
	}
	
	@Test
	void constructorWithInputProvider() {
		assertThrows(NullPointerException.class, () -> new JsonReader((InputProvider) null));
		assertThrows(NullPointerException.class, () -> new JsonReader((InputProvider) null, STRICT_CONFIG));
		assertThrows(NullPointerException.class, () -> new JsonReader(new InputProvider(InputStream.nullInputStream()), null));
		
		assertDoesNotThrow(() -> new JsonReader(new InputProvider(InputStream.nullInputStream())));
		assertDoesNotThrow(() -> new JsonReader(new InputProvider(InputStream.nullInputStream()), STRICT_CONFIG));
	}
	
	@Test
	void readJsonEmptyInput() {
		JsonReader reader = new JsonReader("");
		assertThrows(JsonSyntaxException.class, reader::readJson);
		
		JsonReader whitespaceReader = new JsonReader("   \n\t  ");
		assertThrows(JsonSyntaxException.class, whitespaceReader::readJson);
	}
	
	@Test
	void readJsonNull() {
		JsonReader strictReader = new JsonReader("null", STRICT_CONFIG);
		JsonElement element = strictReader.readJson();
		assertInstanceOf(JsonNull.class, element);
		assertEquals(JsonNull.INSTANCE, element);
		
		JsonReader nonStrictReader = new JsonReader("NULL", NON_STRICT_CONFIG);
		element = nonStrictReader.readJson();
		assertEquals(JsonNull.INSTANCE, element);
		
		JsonReader mixedCaseReader = new JsonReader("nUlL", NON_STRICT_CONFIG);
		element = mixedCaseReader.readJson();
		assertEquals(JsonNull.INSTANCE, element);
		
		JsonReader invalidStrictReader = new JsonReader("NULL", STRICT_CONFIG);
		assertThrows(JsonSyntaxException.class, invalidStrictReader::readJson);
	}
	
	@Test
	void readJsonBoolean() {
		assertEquals(new JsonPrimitive(true), new JsonReader("true", STRICT_CONFIG).readJson());
		assertEquals(new JsonPrimitive(true), new JsonReader("TRUE", NON_STRICT_CONFIG).readJson());
		assertEquals(new JsonPrimitive(true), new JsonReader("True", NON_STRICT_CONFIG).readJson());
		
		assertEquals(new JsonPrimitive(false), new JsonReader("false", STRICT_CONFIG).readJson());
		assertEquals(new JsonPrimitive(false), new JsonReader("FALSE", NON_STRICT_CONFIG).readJson());
		assertEquals(new JsonPrimitive(false), new JsonReader("False", NON_STRICT_CONFIG).readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("TRUE", STRICT_CONFIG).readJson());
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("FALSE", STRICT_CONFIG).readJson());
	}
	
	@Test
	void readJsonNumbers() {
		assertEquals(new JsonPrimitive(0L), new JsonReader("0").readJson());
		assertEquals(new JsonPrimitive(42L), new JsonReader("42").readJson());
		assertEquals(new JsonPrimitive(-42L), new JsonReader("-42").readJson());
		assertEquals(new JsonPrimitive(123456789L), new JsonReader("123456789").readJson());
		
		assertEquals(new JsonPrimitive(3.14), new JsonReader("3.14").readJson());
		assertEquals(new JsonPrimitive(-3.14), new JsonReader("-3.14").readJson());
		assertEquals(new JsonPrimitive(0.0), new JsonReader("0.0").readJson());
	}
	
	@Test
	void readJsonStrings() {
		assertEquals(new JsonPrimitive("hello"), new JsonReader("\"hello\"").readJson());
		assertEquals(new JsonPrimitive(""), new JsonReader("\"\"").readJson());
		assertEquals(new JsonPrimitive("hello world"), new JsonReader("\"hello world\"").readJson());
		
		assertEquals(new JsonPrimitive("hello\nworld"), new JsonReader("\"hello\nworld\"").readJson());
		assertEquals(new JsonPrimitive("hello\tworld"), new JsonReader("\"hello\tworld\"").readJson());
		assertEquals(new JsonPrimitive("hello\\\"world"), new JsonReader("\"hello\\\"world\"").readJson());
		assertEquals(new JsonPrimitive("hello\\world"), new JsonReader("\"hello\\world\"").readJson());
		
		assertEquals(new JsonPrimitive("Hello 世界"), new JsonReader("\"Hello 世界\"").readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("hello").readJson());
	}
	
	@Test
	void readJsonArrayEmpty() {
		JsonElement element = new JsonReader("[]").readJson();
		assertTrue(assertInstanceOf(JsonArray.class, element).isEmpty());
		
		element = new JsonReader(" [ ] ").readJson();
		assertTrue(assertInstanceOf(JsonArray.class, element).isEmpty());
	}
	
	@Test
	void readJsonArraySingleElement() {
		JsonElement element = new JsonReader("[42]").readJson();
		JsonArray array = assertInstanceOf(JsonArray.class, element);
		assertEquals(1, array.size());
		assertEquals(new JsonPrimitive(42L), array.get(0));
		
		element = new JsonReader("[ 42 ]").readJson();
		array = assertInstanceOf(JsonArray.class, element);
		assertEquals(1, array.size());
		assertEquals(new JsonPrimitive(42L), array.get(0));
	}
	
	@Test
	void readJsonArrayMultipleElements() {
		JsonElement element = new JsonReader("[1, 2, 3]").readJson();
		JsonArray array = assertInstanceOf(JsonArray.class, element);
		assertEquals(3, array.size());
		assertEquals(new JsonPrimitive(1L), array.get(0));
		assertEquals(new JsonPrimitive(2L), array.get(1));
		assertEquals(new JsonPrimitive(3L), array.get(2));
		
		element = new JsonReader("[42, \"hello\", true, null]").readJson();
		array = assertInstanceOf(JsonArray.class, element);
		assertEquals(4, array.size());
		assertEquals(new JsonPrimitive(42L), array.get(0));
		assertEquals(new JsonPrimitive("hello"), array.get(1));
		assertEquals(new JsonPrimitive(true), array.get(2));
		assertEquals(JsonNull.INSTANCE, array.get(3));
	}
	
	@Test
	void readJsonArrayNested() {
		JsonElement element = new JsonReader("[[1, 2], [3, 4]]").readJson();
		JsonArray outerArray = assertInstanceOf(JsonArray.class, element);
		assertEquals(2, outerArray.size());
		
		JsonArray innerArray1 = outerArray.getAsJsonArray(0);
		assertEquals(2, innerArray1.size());
		assertEquals(new JsonPrimitive(1L), innerArray1.get(0));
		assertEquals(new JsonPrimitive(2L), innerArray1.get(1));
		
		JsonArray innerArray2 = outerArray.getAsJsonArray(1);
		assertEquals(2, innerArray2.size());
		assertEquals(new JsonPrimitive(3L), innerArray2.get(0));
		assertEquals(new JsonPrimitive(4L), innerArray2.get(1));
	}
	
	@Test
	void readJsonArrayInvalid() {
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[").readJson());
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[1, 2").readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[1, 2,]", STRICT_CONFIG).readJson());
		
		assertDoesNotThrow(() -> new JsonReader("[1, 2,]", NON_STRICT_CONFIG).readJson());
	}
	
	@Test
	void readJsonObjectEmpty() {
		JsonElement element = new JsonReader("{}").readJson();
		assertTrue(assertInstanceOf(JsonObject.class, element).isEmpty());
		
		element = new JsonReader(" { } ").readJson();
		assertTrue(assertInstanceOf(JsonObject.class, element).isEmpty());
	}
	
	@Test
	void readJsonObjectSingleEntry() {
		JsonElement element = new JsonReader("{\"key\": \"value\"}").readJson();
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		assertEquals(1, object.size());
		assertEquals(new JsonPrimitive("value"), object.get("key"));
		
		element = new JsonReader("{ \"key\" : \"value\" }", NON_STRICT_CONFIG).readJson();
		object = assertInstanceOf(JsonObject.class, element);
		assertEquals(1, object.size());
		assertEquals(new JsonPrimitive("value"), object.get("key"));
	}
	
	@Test
	void readJsonObjectMultipleEntries() {
		JsonElement element = new JsonReader("{\"key1\": \"value1\", \"key2\": 42, \"key3\": true}").readJson();
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		assertEquals(3, object.size());
		assertEquals(new JsonPrimitive("value1"), object.get("key1"));
		assertEquals(new JsonPrimitive(42L), object.get("key2"));
		assertEquals(new JsonPrimitive(true), object.get("key3"));
	}
	
	@Test
	void readJsonObjectNested() {
		JsonElement element = new JsonReader("{\"outer\": {\"inner\": \"value\"}}").readJson();
		JsonObject outerObject = assertInstanceOf(JsonObject.class, element);
		assertEquals(1, outerObject.size());
		
		JsonObject innerObject = outerObject.getAsJsonObject("outer");
		assertEquals(1, innerObject.size());
		assertEquals(new JsonPrimitive("value"), innerObject.get("inner"));
	}
	
	@Test
	void readJsonObjectWithArrays() {
		JsonElement element = new JsonReader("{\"numbers\": [1, 2, 3], \"booleans\": [true, false]}").readJson();
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		assertEquals(2, object.size());
		
		JsonArray numbers = object.getAsJsonArray("numbers");
		assertEquals(3, numbers.size());
		assertEquals(new JsonPrimitive(1L), numbers.get(0));
		
		JsonArray booleans = object.getAsJsonArray("booleans");
		assertEquals(2, booleans.size());
		assertEquals(new JsonPrimitive(true), booleans.get(0));
	}
	
	@Test
	void readJsonObjectStrictMode() {
		assertDoesNotThrow(() -> new JsonReader("{\"key\": \"value\"}", STRICT_CONFIG).readJson());
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{key: \"value\"}", STRICT_CONFIG).readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\": \"value\",}", STRICT_CONFIG).readJson());
	}
	
	@Test
	void readJsonObjectNonStrictMode() {
		assertDoesNotThrow(() -> new JsonReader("{\"key\": \"value\",}", NON_STRICT_CONFIG).readJson());
		
		JsonElement element = new JsonReader("{\"key\": \"value\",}", NON_STRICT_CONFIG).readJson();
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		assertEquals(new JsonPrimitive("value"), object.get("key"));
	}
	
	@Test
	void readJsonObjectInvalid() {
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{").readJson());
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\": \"value\"").readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\" \"value\"}").readJson());
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key1\": \"value1\" \"key2\": \"value2\"}").readJson());
	}
	
	@Test
	void readJsonComplexStructure() {
		String complexJson = """
			{
				"users": [
					{
						"id": 1,
						"name": "John Doe",
						"active": true,
						"profile": {
							"age": 30,
							"email": "john@example.com"
						}
					},
					{
						"id": 2,
						"name": "Jane Smith",
						"active": false,
						"profile": {
							"age": 25,
							"email": "jane@example.com"
						}
					}
				],
				"meta": {
					"total": 2,
					"page": 1
				}
			}
			""";
		
		JsonElement element = new JsonReader(complexJson).readJson();
		JsonObject root = assertInstanceOf(JsonObject.class, element);
		
		assertTrue(root.containsKey("users"));
		assertTrue(root.containsKey("meta"));
		
		JsonArray users = root.getAsJsonArray("users");
		assertEquals(2, users.size());
		
		JsonObject firstUser = users.getAsJsonObject(0);
		assertEquals(new JsonPrimitive(1L), firstUser.get("id"));
		assertEquals(new JsonPrimitive("John Doe"), firstUser.get("name"));
		assertEquals(new JsonPrimitive(true), firstUser.get("active"));
		
		JsonObject firstProfile = firstUser.getAsJsonObject("profile");
		assertEquals(new JsonPrimitive(30L), firstProfile.get("age"));
		assertEquals(new JsonPrimitive("john@example.com"), firstProfile.get("email"));
	}
	
	@Test
	void readJsonWithInputProvider() {
		String json = "{\"test\": \"value\"}";
		byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);
		InputStream inputStream = new ByteArrayInputStream(jsonBytes);
		InputProvider inputProvider = new InputProvider(inputStream);
		
		JsonReader reader = new JsonReader(inputProvider);
		JsonElement element = reader.readJson();
		
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		assertEquals(new JsonPrimitive("value"), object.get("test"));
	}
	
	@Test
	void readJsonStrictModeExtraContent() {
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{} extra", STRICT_CONFIG).readJson());
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("42 123", STRICT_CONFIG).readJson());
		
		assertDoesNotThrow(() -> new JsonReader("{} extra", NON_STRICT_CONFIG).readJson());
		assertDoesNotThrow(() -> new JsonReader("42 123", NON_STRICT_CONFIG).readJson());
	}
	
	@Test
	void readJsonWithWhitespace() {
		String jsonWithWhitespace = """
			  {
			    "key1" : "value1" ,
			    "key2" : [
			      1 ,
			      2 ,
			      3
			    ] ,
			    "key3" : {
			      "nested" : true
			    }
			  }
			""";
		
		assertThrows(JsonSyntaxException.class, () -> new JsonReader(jsonWithWhitespace, STRICT_CONFIG).readJson());
		
		JsonElement element = new JsonReader(jsonWithWhitespace, NON_STRICT_CONFIG).readJson();
		JsonObject object = assertInstanceOf(JsonObject.class, element);
		
		assertEquals(new JsonPrimitive("value1"), object.get("key1"));
		assertEquals(3, object.getAsJsonArray("key2").size());
		assertEquals(new JsonPrimitive(true), object.getAsJsonObject("key3").get("nested"));
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new JsonReader("{}").close());
		assertDoesNotThrow(() -> new JsonReader("[]").close());
		assertDoesNotThrow(() -> new JsonReader("null").close());
		
		InputProvider provider = new InputProvider(new ByteArrayInputStream("{}".getBytes()));
		JsonReader reader = new JsonReader(provider);
		assertDoesNotThrow(reader::close);
	}
	
	@Test
	void readJsonInvalidSyntax() {
		String[] invalidJsons = {
			"{",
			"}",
			"[",
			"]",
			"{\"key\":}",
			"{\"key\": \"value\", }",
			"[1, 2, 3, ]",
			"{\"key1\": \"value1\" \"key2\": \"value2\"}",
			"{'key': 'value'}",
			"{key: value}"
		};
		
		for (String invalidJson : invalidJsons) {
			assertThrows(JsonSyntaxException.class, () -> new JsonReader(invalidJson, STRICT_CONFIG).readJson());
		}
	}
}
