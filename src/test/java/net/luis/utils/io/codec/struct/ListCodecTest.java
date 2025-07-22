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

package net.luis.utils.io.codec.struct;

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

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
		assertThrows(NullPointerException.class, () -> new ListCodec<>(null, 0, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ListCodec<>(Codec.INTEGER, -1, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ListCodec<>(Codec.INTEGER, 1, 0));
		assertDoesNotThrow(() -> new ListCodec<>(Codec.INTEGER));
		assertDoesNotThrow(() -> new ListCodec<>(Codec.INTEGER, 0, 10));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), list));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, list));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as list"));
	}
	
	@Test
	void encodeStartWithValidList() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		
		assertEquals(expected, result.orThrow());
	}
	
	@Test
	void encodeStartWithEmptyList() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> list = List.of();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.orThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(Codec.STRING);
		List<String> list = List.of("hello");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result.orThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<List<String>> stringCodec = new ListCodec<>(Codec.STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), List.of("a", "b"));
		assertTrue(stringResult.isSuccess());
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(Codec.BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, typeProvider.empty(), List.of(true, false));
		assertTrue(boolResult.isSuccess());
	}
	
	@Test
	void encodeStartSizedExactMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 3, 3);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartSizedTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 3, 5);
		List<Integer> list = List.of(1, 2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("List size '2' is out of range: 3..5"));
	}
	
	@Test
	void encodeStartSizedTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 1, 3);
		List<Integer> list = List.of(1, 2, 3, 4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("List size '4' is out of range: 1..3"));
	}
	
	@Test
	void encodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> listWithNull = Lists.newArrayList(1, null, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), listWithNull);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode some elements"));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as list"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertEquals(List.of(1, 2, 3), result.orThrow());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, new JsonArray());
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(Codec.STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		Result<List<String>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertEquals(List.of("hello"), result.orThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode list"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<List<String>> stringCodec = new ListCodec<>(Codec.STRING);
		Result<List<String>> stringResult = stringCodec.decodeStart(typeProvider, stringArray);
		assertTrue(stringResult.isSuccess());
		assertEquals(List.of("a", "b"), stringResult.orThrow());
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(Codec.BOOLEAN);
		Result<List<Boolean>> boolResult = boolCodec.decodeStart(typeProvider, boolArray);
		assertTrue(boolResult.isSuccess());
		assertEquals(List.of(true, false), boolResult.orThrow());
	}
	
	@Test
	void decodeStartSizedExactMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 2, 2);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertEquals(List.of(1, 2), result.orThrow());
	}
	
	@Test
	void decodeStartSizedTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 3, 5);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("size '2' is out of range: 3..5"));
	}
	
	@Test
	void decodeStartSizedTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 1, 2);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("size '3' is out of range: 1..2"));
	}
	
	@Test
	void decodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode some elements"));
	}
	
	@Test
	void decodeStartWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(100));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
	}
	
	@Test
	void equalsAndHashCode() {
		ListCodec<Integer> codec1 = new ListCodec<>(Codec.INTEGER, 0, 10);
		ListCodec<Integer> codec2 = new ListCodec<>(Codec.INTEGER, 0, 10);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		ListCodec<Integer> codec = new ListCodec<>(Codec.INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ListCodec["));
		assertTrue(result.endsWith("]"));
	}
}
