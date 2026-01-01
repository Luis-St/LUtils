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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonNull}.<br>
 *
 * @author Luis-St
 */
class JsonNullTest {
	
	@Test
	void singletonInstance() {
		assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE);
		
		JsonNull null1 = JsonNull.INSTANCE;
		JsonNull null2 = JsonNull.INSTANCE;
		assertSame(null1, null2);
	}
	
	@Test
	void jsonElementTypeChecks() {
		JsonNull jsonNull = JsonNull.INSTANCE;
		
		assertTrue(jsonNull.isJsonNull());
		assertFalse(jsonNull.isJsonObject());
		assertFalse(jsonNull.isJsonArray());
		assertFalse(jsonNull.isJsonPrimitive());
	}
	
	@Test
	void jsonElementConversions() {
		JsonNull jsonNull = JsonNull.INSTANCE;
		
		assertThrows(JsonTypeException.class, jsonNull::getAsJsonObject);
		assertThrows(JsonTypeException.class, jsonNull::getAsJsonArray);
		assertThrows(JsonTypeException.class, jsonNull::getAsJsonPrimitive);
	}
	
	@Test
	void jsonElementConversionExceptionMessages() {
		JsonNull jsonNull = JsonNull.INSTANCE;
		
		JsonTypeException objectException = assertThrows(JsonTypeException.class, jsonNull::getAsJsonObject);
		assertTrue(objectException.getMessage().contains("json object"));
		assertTrue(objectException.getMessage().contains("json null"));
		
		JsonTypeException arrayException = assertThrows(JsonTypeException.class, jsonNull::getAsJsonArray);
		assertTrue(arrayException.getMessage().contains("json array"));
		assertTrue(arrayException.getMessage().contains("json null"));
		
		JsonTypeException primitiveException = assertThrows(JsonTypeException.class, jsonNull::getAsJsonPrimitive);
		assertTrue(primitiveException.getMessage().contains("json primitive"));
		assertTrue(primitiveException.getMessage().contains("json null"));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("null", JsonNull.INSTANCE.toString());
	}
	
	@Test
	void toStringWithCustomConfigs() {
		JsonConfig config1 = new JsonConfig(false, false, "  ", false, 1, false, 1, StandardCharsets.UTF_8);
		JsonConfig config2 = new JsonConfig(true, true, "\t", true, 10, true, 5, StandardCharsets.UTF_16);
		
		assertEquals("null", JsonNull.INSTANCE.toString(config1));
		assertEquals("null", JsonNull.INSTANCE.toString(config2));
		
		assertEquals("null", JsonNull.INSTANCE.toString(null));
	}
	
	@Test
	void equalsAndHashCode() {
		JsonNull null1 = JsonNull.INSTANCE;
		JsonNull null2 = JsonNull.INSTANCE;
		
		assertEquals(null1, null2);
		assertEquals(null1.hashCode(), null2.hashCode());
		
		assertEquals(null1, null1);
		
		assertNotEquals(null1, null);
		assertNotEquals(null1, "null");
		assertNotEquals(null1, new JsonPrimitive("null"));
		assertNotEquals(null1, new JsonObject());
		assertNotEquals(null1, new JsonArray());
	}
	
	@Test
	void consistentBehaviorAcrossMultipleCalls() {
		for (int i = 0; i < 100; i++) {
			assertTrue(JsonNull.INSTANCE.isJsonNull());
			assertFalse(JsonNull.INSTANCE.isJsonObject());
			assertEquals("null", JsonNull.INSTANCE.toString());
			assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE);
		}
	}
	
	@Test
	void interactionWithOtherJsonElements() {
		JsonArray array = new JsonArray();
		array.add(JsonNull.INSTANCE);
		
		JsonObject object = new JsonObject();
		object.add("nullValue", JsonNull.INSTANCE);
		
		assertTrue(array.contains(JsonNull.INSTANCE));
		assertEquals(JsonNull.INSTANCE, array.get(0));
		
		assertTrue(object.containsValue(JsonNull.INSTANCE));
		assertEquals(JsonNull.INSTANCE, object.get("nullValue"));
	}
}
