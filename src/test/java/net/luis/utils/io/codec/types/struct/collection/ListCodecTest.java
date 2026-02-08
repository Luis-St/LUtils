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

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ListCodec}.<br>
 *
 * @author Luis-St
 */
class ListCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new ListCodec<>(null));
		assertDoesNotThrow(() -> new ListCodec<>(INTEGER));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), list));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, list));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as list"));
	}
	
	@Test
	void encodeWithValidList() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), list);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithEmptyList() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), list);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(STRING);
		List<String> list = List.of("hello");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), list);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<List<String>> stringCodec = new ListCodec<>(STRING);
		assertDoesNotThrow(() -> stringCodec.encode(typeProvider, typeProvider.empty(), List.of("a", "b")));
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(BOOLEAN);
		assertDoesNotThrow(() -> boolCodec.encode(typeProvider, typeProvider.empty(), List.of(true, false)));
	}
	
	@Test
	void encodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> listWithNull = Lists.newArrayList(1, null, 3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), listWithNull));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as list"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		List<Integer> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(List.of(1, 2, 3), result);
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		List<Integer> result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray());
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		List<String> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(List.of("hello"), result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
		assertTrue(exception.getMessage().contains("Json element '42' is not a json array"));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<List<String>> stringCodec = new ListCodec<>(STRING);
		List<String> stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), stringArray);
		assertEquals(List.of("a", "b"), stringResult);
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(BOOLEAN);
		List<Boolean> boolResult = boolCodec.decode(typeProvider, typeProvider.empty(), boolArray);
		assertEquals(List.of(true, false), boolResult);
	}
	
	@Test
	void decodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void toStringRepresentation() {
		ListCodec<Integer> codec = new ListCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ListCodec["));
		assertTrue(result.endsWith("]"));
	}
}
