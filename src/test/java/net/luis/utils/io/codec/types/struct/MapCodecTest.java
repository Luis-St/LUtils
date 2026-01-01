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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MapCodec}.<br>
 *
 * @author Luis-St
 */
class MapCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new MapCodec<>(null, BOOLEAN));
		assertThrows(NullPointerException.class, () -> new MapCodec<>(INTEGER, null));
		assertDoesNotThrow(() -> new MapCodec<>(INTEGER, BOOLEAN));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), map));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, map));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as map"));
	}
	
	@Test
	void encodeStartWithValidMap() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), map);
		assertTrue(result.isSuccess());
		
		JsonObject expected = new JsonObject();
		expected.add("1", new JsonPrimitive(true));
		expected.add("2", new JsonPrimitive(false));
		
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyMap() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), map);
		assertTrue(result.isSuccess());
		assertEquals(new JsonObject(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleEntry() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER);
		Map<String, Integer> map = Map.of("key", 42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), map);
		assertTrue(result.isSuccess());
		
		JsonObject expected = new JsonObject();
		expected.add("key", new JsonPrimitive(42));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Map<String, String>> stringCodec = new MapCodec<>(STRING, STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), Map.of("a", "b"));
		assertTrue(stringResult.isSuccess());
		
		Codec<Map<Integer, Double>> numericCodec = new MapCodec<>(INTEGER, DOUBLE);
		Result<JsonElement> numericResult = numericCodec.encodeStart(typeProvider, typeProvider.empty(), Map.of(1, 3.14));
		assertTrue(numericResult.isSuccess());
	}
	
	@Test
	void encodeStartWithInvalidKeys() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Map.of(1, true));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, String>> codec = new MapCodec<>(INTEGER, STRING);
		
		Map<Integer, String> validMap = Map.of(1, "value");
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validMap);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonObject()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as map"));
	}
	
	@Test
	void decodeStartWithValidObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive(false));
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(Map.of(1, true, 2, false), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEmptyObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonObject());
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithSingleEntry() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER);
		
		JsonObject object = new JsonObject();
		object.add("key", new JsonPrimitive(42));
		
		Result<Map<String, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(Map.of("key", 42), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode map"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject stringObject = new JsonObject();
		stringObject.add("a", new JsonPrimitive("b"));
		stringObject.add("c", new JsonPrimitive("d"));
		
		Codec<Map<String, String>> stringCodec = new MapCodec<>(STRING, STRING);
		Result<Map<String, String>> stringResult = stringCodec.decodeStart(typeProvider, typeProvider.empty(), stringObject);
		assertTrue(stringResult.isSuccess());
		assertEquals(Map.of("a", "b", "c", "d"), stringResult.resultOrThrow());
		
		JsonObject numericObject = new JsonObject();
		numericObject.add("1", new JsonPrimitive(3.14));
		
		Codec<Map<Integer, Double>> numericCodec = new MapCodec<>(INTEGER, DOUBLE);
		Result<Map<Integer, Double>> numericResult = numericCodec.decodeStart(typeProvider, typeProvider.empty(), numericObject);
		assertTrue(numericResult.isSuccess());
		assertEquals(Map.of(1, 3.14), numericResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidKeys() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("not-a-number", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive(false));
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), object);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive("not-a-boolean"));
		
		Result<Map<Integer, Boolean>> result = codec.decodeStart(typeProvider, typeProvider.empty(), object);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Integer>> codec = new MapCodec<>(INTEGER, INTEGER);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(42));
		object.add("invalid-key", new JsonPrimitive(100));
		object.add("3", new JsonPrimitive("invalid-value"));
		
		Result<Map<Integer, Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), object);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void equalsAndHashCode() {
		MapCodec<Integer, Boolean> codec1 = new MapCodec<>(INTEGER, BOOLEAN);
		MapCodec<Integer, Boolean> codec2 = new MapCodec<>(INTEGER, BOOLEAN);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		MapCodec<Integer, Boolean> codec = new MapCodec<>(INTEGER, BOOLEAN);
		String result = codec.toString();
		
		assertTrue(result.startsWith("MapCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains(","));
	}
}
