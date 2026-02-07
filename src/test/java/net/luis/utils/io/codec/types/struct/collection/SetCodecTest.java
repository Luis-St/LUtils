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

import com.google.common.collect.Sets;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SetCodec}.<br>
 *
 * @author Luis-St
 */
class SetCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new SetCodec<>(null));
		assertDoesNotThrow(() -> new SetCodec<>(INTEGER));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), set));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, set));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as set"));
	}
	
	@Test
	void encodeWithValidSet() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of(1, 2, 3);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), set);
		
		JsonArray array = result.getAsJsonArray();
		assertEquals(3, array.size());
		assertTrue(array.contains(new JsonPrimitive(1)));
		assertTrue(array.contains(new JsonPrimitive(2)));
		assertTrue(array.contains(new JsonPrimitive(3)));
	}
	
	@Test
	void encodeWithEmptySet() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), set);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<String>> codec = new SetCodec<>(STRING);
		Set<String> set = Set.of("hello");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), set);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Set<String>> stringCodec = new SetCodec<>(STRING);
		assertDoesNotThrow(() -> stringCodec.encode(typeProvider, typeProvider.empty(), Set.of("a", "b")));
		
		Codec<Set<Boolean>> boolCodec = new SetCodec<>(BOOLEAN);
		assertDoesNotThrow(() -> boolCodec.encode(typeProvider, typeProvider.empty(), Set.of(true, false)));
	}
	
	@Test
	void encodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> setWithNull = Sets.newHashSet(1, null, 3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), setWithNull));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as set"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Set<Integer> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(Set.of(1, 2, 3), result);
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		Set<Integer> result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray());
		assertTrue(result.isEmpty());
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<String>> codec = new SetCodec<>(STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		Set<String> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(Set.of("hello"), result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
		assertTrue(exception.getMessage().contains("Json element '42' is not a json array"));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<Set<String>> stringCodec = new SetCodec<>(STRING);
		Set<String> stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), stringArray);
		assertEquals(Set.of("a", "b"), stringResult);
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<Set<Boolean>> boolCodec = new SetCodec<>(BOOLEAN);
		Set<Boolean> boolResult = boolCodec.decode(typeProvider, typeProvider.empty(), boolArray);
		assertEquals(Set.of(true, false), boolResult);
	}
	
	@Test
	void decodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeWithDuplicates() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(3));
		
		Set<Integer> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(Set.of(1, 2, 3), result);
		assertEquals(3, result.size());
	}
	
	@Test
	void toStringRepresentation() {
		SetCodec<Integer> codec = new SetCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("SetCodec["));
		assertTrue(result.endsWith("]"));
	}
}
