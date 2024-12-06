/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonReader}.<br>
 *
 * @author Luis-St
 */
class JsonReaderTest {
	
	private static final JsonConfig DEFAULT_CONFIG = JsonConfig.DEFAULT;
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(false, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new JsonReader((String) null));
		assertDoesNotThrow(() -> new JsonReader("test"));
		
		assertThrows(NullPointerException.class, () -> new JsonReader((String) null, JsonConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new JsonReader("test", null));
		assertDoesNotThrow(() -> new JsonReader("test", JsonConfig.DEFAULT));
		
		assertThrows(NullPointerException.class, () -> new JsonReader((InputProvider) null));
		assertDoesNotThrow(() -> new JsonReader(new InputProvider(InputStream.nullInputStream())));
		
		assertThrows(NullPointerException.class, () -> new JsonReader((InputProvider) null, JsonConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new JsonReader(new InputProvider(InputStream.nullInputStream()), null));
		assertDoesNotThrow(() -> new JsonReader(new InputProvider(InputStream.nullInputStream()), JsonConfig.DEFAULT));
	}
	
	@Test
	void readJson() {
		JsonReader reader = new JsonReader("");
		assertThrows(JsonSyntaxException.class, reader::readJson);
	}
	
	@Test
	void readJsonArrayDefaultConfig() {
		JsonReader emptyArrayReader = new JsonReader("[]");
		JsonElement emptyArray = emptyArrayReader.readJson();
		
		assertInstanceOf(JsonArray.class, emptyArray);
		assertEquals(new JsonArray(), emptyArray);
		
		JsonReader arrayReader = new JsonReader("[1, 2, 3]");
		JsonElement array = arrayReader.readJson();
		
		assertInstanceOf(JsonArray.class, array);
		assertEquals(3, ((JsonArray) array).size());
		assertEquals(new JsonPrimitive(1L), ((JsonArray) array).get(0));
		assertEquals(new JsonPrimitive(2L), ((JsonArray) array).get(1));
		assertEquals(new JsonPrimitive(3L), ((JsonArray) array).get(2));
		
		// Not enough characters to be valid
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[").readJson());
		
		// Missing closing bracket
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[ ").readJson());
		assertDoesNotThrow(() -> new JsonReader("[ ]  ").readJson());
		
		// Trailing comma
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[1, 2, 3, ]").readJson());
	}
	
	@Test
	void readJsonNullDefaultConfig() {
		JsonReader reader = new JsonReader("null");
		JsonElement element = reader.readJson();
		
		assertInstanceOf(JsonNull.class, element);
		assertEquals(JsonNull.INSTANCE, element);
	}
	
	@Test
	void readJsonObjectDefaultConfig() {
		JsonReader emptyObjectReader = new JsonReader("{}");
		JsonElement emptyObject = emptyObjectReader.readJson();
		
		assertInstanceOf(JsonObject.class, emptyObject);
		assertEquals(new JsonObject(), emptyObject);
		
		JsonReader objectReader = new JsonReader("{\"key\": \"value\"}");
		JsonElement object = objectReader.readJson();
		
		assertInstanceOf(JsonObject.class, object);
		assertEquals(1, ((JsonObject) object).size());
		assertTrue(((JsonObject) object).containsKey("key"));
		assertEquals(new JsonPrimitive("value"), ((JsonObject) object).get("key"));
		
		// Not enough characters to be valid
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{").readJson());
		
		// Missing closing bracket
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{ ").readJson());
		assertDoesNotThrow(() -> new JsonReader("{ }  ").readJson());
		
		// Missing key value separator
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\" \"value\"}").readJson());
		
		// Trailing comma
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\": \"value\", }").readJson());
	}
	
	@Test
	void readJsonPrimitiveDefaultConfig() {
		JsonReader booleanReader = new JsonReader("true");
		JsonElement booleanElement = booleanReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, booleanElement);
		assertEquals(new JsonPrimitive(true), booleanElement);
		
		JsonReader numberReader = new JsonReader("42");
		JsonElement numberElement = numberReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, numberElement);
		assertEquals(new JsonPrimitive(42L), numberElement);
		
		JsonReader stringReader = new JsonReader("\"test\"");
		JsonElement stringElement = stringReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, stringElement);
		assertEquals(new JsonPrimitive("test"), stringElement);
		
		// Invalid string
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("test").readJson());
	}
	
	@Test
	void readJsonArrayCustomConfig() {
		JsonReader emptyArrayReader = new JsonReader("[]", CUSTOM_CONFIG);
		JsonElement emptyArray = emptyArrayReader.readJson();
		
		assertInstanceOf(JsonArray.class, emptyArray);
		assertEquals(new JsonArray(), emptyArray);
		
		JsonReader arrayReader = new JsonReader("[1, 2, 3]", CUSTOM_CONFIG);
		JsonElement array = arrayReader.readJson();
		
		assertInstanceOf(JsonArray.class, array);
		assertEquals(3, ((JsonArray) array).size());
		assertEquals(new JsonPrimitive(1L), ((JsonArray) array).get(0));
		assertEquals(new JsonPrimitive(2L), ((JsonArray) array).get(1));
		assertEquals(new JsonPrimitive(3L), ((JsonArray) array).get(2));
		
		// Not enough characters to be valid
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[", CUSTOM_CONFIG).readJson());
		
		// Missing closing bracket
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("[ ", CUSTOM_CONFIG).readJson());
		assertDoesNotThrow(() -> new JsonReader("[ ]  ", CUSTOM_CONFIG).readJson());
		
		// Trailing comma
		assertDoesNotThrow(() -> new JsonReader("[1, 2, 3, ]", CUSTOM_CONFIG).readJson());
	}
	
	@Test
	void readJsonNullCustomConfig() {
		JsonReader reader = new JsonReader("null", CUSTOM_CONFIG);
		JsonElement element = reader.readJson();
		
		assertInstanceOf(JsonNull.class, element);
		assertEquals(JsonNull.INSTANCE, element);
	}
	
	@Test
	void readJsonObjectCustomConfig() {
		JsonReader emptyObjectReader = new JsonReader("{}", CUSTOM_CONFIG);
		JsonElement emptyObject = emptyObjectReader.readJson();
		
		assertInstanceOf(JsonObject.class, emptyObject);
		assertEquals(new JsonObject(), emptyObject);
		
		JsonReader objectReader = new JsonReader("{\"key\": \"value\"}", CUSTOM_CONFIG);
		JsonElement object = objectReader.readJson();
		
		assertInstanceOf(JsonObject.class, object);
		assertEquals(1, ((JsonObject) object).size());
		assertTrue(((JsonObject) object).containsKey("key"));
		assertEquals(new JsonPrimitive("value"), ((JsonObject) object).get("key"));
		
		// Not enough characters to be valid
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{", CUSTOM_CONFIG).readJson());
		
		// Missing closing bracket
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{ ", CUSTOM_CONFIG).readJson());
		assertDoesNotThrow(() -> new JsonReader("{ }  ", CUSTOM_CONFIG).readJson());
		
		// Missing key value separator
		assertThrows(JsonSyntaxException.class, () -> new JsonReader("{\"key\" \"value\"}", CUSTOM_CONFIG).readJson());
		
		// Trailing comma
		assertDoesNotThrow(() -> new JsonReader("{\"key\": \"value\", }", CUSTOM_CONFIG).readJson());
	}
	
	@Test
	void readJsonPrimitiveCustomConfig() {
		JsonReader booleanReader = new JsonReader("true", CUSTOM_CONFIG);
		JsonElement booleanElement = booleanReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, booleanElement);
		assertEquals(new JsonPrimitive(true), booleanElement);
		
		JsonReader numberReader = new JsonReader("42", CUSTOM_CONFIG);
		JsonElement numberElement = numberReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, numberElement);
		assertEquals(new JsonPrimitive(42L), numberElement);
		
		JsonReader stringReader = new JsonReader("\"test\"", CUSTOM_CONFIG);
		JsonElement stringElement = stringReader.readJson();
		
		assertInstanceOf(JsonPrimitive.class, stringElement);
		assertEquals(new JsonPrimitive("test"), stringElement);
		
		// Invalid string
		assertDoesNotThrow(() -> new JsonReader("test", CUSTOM_CONFIG).readJson());
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new JsonReader("test").close());
	}
}
