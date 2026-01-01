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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), set));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, set));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as set"));
	}
	
	@Test
	void encodeStartWithValidSet() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), set);
		assertTrue(result.isSuccess());
		
		JsonArray array = result.resultOrThrow().getAsJsonArray();
		assertEquals(3, array.size());
		assertTrue(array.contains(new JsonPrimitive(1)));
		assertTrue(array.contains(new JsonPrimitive(2)));
		assertTrue(array.contains(new JsonPrimitive(3)));
	}
	
	@Test
	void encodeStartWithEmptySet() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> set = Set.of();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), set);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<String>> codec = new SetCodec<>(STRING);
		Set<String> set = Set.of("hello");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), set);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Set<String>> stringCodec = new SetCodec<>(STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), Set.of("a", "b"));
		assertTrue(stringResult.isSuccess());
		
		Codec<Set<Boolean>> boolCodec = new SetCodec<>(BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, typeProvider.empty(), Set.of(true, false));
		assertTrue(boolResult.isSuccess());
	}
	
	@Test
	void encodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		Set<Integer> setWithNull = Sets.newHashSet(1, null, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), setWithNull);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as set"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(Set.of(1, 2, 3), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray());
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<String>> codec = new SetCodec<>(STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		Result<Set<String>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(Set.of("hello"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode set"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<Set<String>> stringCodec = new SetCodec<>(STRING);
		Result<Set<String>> stringResult = stringCodec.decodeStart(typeProvider, typeProvider.empty(), stringArray);
		assertTrue(stringResult.isSuccess());
		assertEquals(Set.of("a", "b"), stringResult.resultOrThrow());
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<Set<Boolean>> boolCodec = new SetCodec<>(BOOLEAN);
		Result<Set<Boolean>> boolResult = boolCodec.decodeStart(typeProvider, typeProvider.empty(), boolArray);
		assertTrue(boolResult.isSuccess());
		assertEquals(Set.of(true, false), boolResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartWithDuplicates() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(Set.of(1, 2, 3), result.resultOrThrow());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void equalsAndHashCode() {
		SetCodec<Integer> codec1 = new SetCodec<>(INTEGER);
		SetCodec<Integer> codec2 = new SetCodec<>(INTEGER);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		SetCodec<Integer> codec = new SetCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("SetCodec["));
		assertTrue(result.endsWith("]"));
	}
}
