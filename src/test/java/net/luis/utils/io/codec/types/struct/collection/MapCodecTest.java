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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), map));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, map));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as map"));
	}
	
	@Test
	void encodeWithValidMap() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), map);
		
		JsonObject expected = new JsonObject();
		expected.add("1", new JsonPrimitive(true));
		expected.add("2", new JsonPrimitive(false));
		
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithEmptyMap() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		Map<Integer, Boolean> map = Map.of();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), map);
		assertEquals(new JsonObject(), result);
	}
	
	@Test
	void encodeWithSingleEntry() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER);
		Map<String, Integer> map = Map.of("key", 42);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), map);
		
		JsonObject expected = new JsonObject();
		expected.add("key", new JsonPrimitive(42));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Map<String, String>> stringCodec = new MapCodec<>(STRING, STRING);
		assertDoesNotThrow(() -> stringCodec.encode(typeProvider, typeProvider.empty(), Map.of("a", "b")));
		
		Codec<Map<Integer, Double>> numericCodec = new MapCodec<>(INTEGER, DOUBLE);
		assertDoesNotThrow(() -> numericCodec.encode(typeProvider, typeProvider.empty(), Map.of(1, 3.14)));
	}
	
	@Test
	void encodeWithInvalidKeys() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Map.of(1, true)));
	}
	
	@Test
	void encodeWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, String>> codec = new MapCodec<>(INTEGER, STRING);
		
		Map<Integer, String> validMap = Map.of(1, "value");
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validMap));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonObject()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as map"));
	}
	
	@Test
	void decodeWithValidObject() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive(false));
		
		Map<Integer, Boolean> result = codec.decode(typeProvider, typeProvider.empty(), object);
		assertEquals(Map.of(1, true, 2, false), result);
	}
	
	@Test
	void decodeWithEmptyObject() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		Map<Integer, Boolean> result = codec.decode(typeProvider, typeProvider.empty(), new JsonObject());
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithSingleEntry() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<String, Integer>> codec = new MapCodec<>(STRING, INTEGER);
		
		JsonObject object = new JsonObject();
		object.add("key", new JsonPrimitive(42));
		
		Map<String, Integer> result = codec.decode(typeProvider, typeProvider.empty(), object);
		assertEquals(Map.of("key", 42), result);
	}
	
	@Test
	void decodeWithNonObject() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
		assertTrue(exception.getMessage().contains("Unable to decode map"));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonObject stringObject = new JsonObject();
		stringObject.add("a", new JsonPrimitive("b"));
		stringObject.add("c", new JsonPrimitive("d"));
		
		Codec<Map<String, String>> stringCodec = new MapCodec<>(STRING, STRING);
		Map<String, String> stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), stringObject);
		assertEquals(Map.of("a", "b", "c", "d"), stringResult);
		
		JsonObject numericObject = new JsonObject();
		numericObject.add("1", new JsonPrimitive(3.14));
		
		Codec<Map<Integer, Double>> numericCodec = new MapCodec<>(INTEGER, DOUBLE);
		Map<Integer, Double> numericResult = numericCodec.decode(typeProvider, typeProvider.empty(), numericObject);
		assertEquals(Map.of(1, 3.14), numericResult);
	}
	
	@Test
	void decodeWithInvalidKeys() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("not-a-number", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive(false));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), object));
	}
	
	@Test
	void decodeWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(INTEGER, BOOLEAN);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(true));
		object.add("2", new JsonPrimitive("not-a-boolean"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), object));
	}
	
	@Test
	void decodeWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Integer>> codec = new MapCodec<>(INTEGER, INTEGER);
		
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive(42));
		object.add("invalid-key", new JsonPrimitive(100));
		object.add("3", new JsonPrimitive("invalid-value"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), object));
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
