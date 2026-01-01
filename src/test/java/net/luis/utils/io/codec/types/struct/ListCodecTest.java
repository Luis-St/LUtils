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

import com.google.common.collect.Lists;
import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), list));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, list));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as list"));
	}
	
	@Test
	void encodeStartWithValidList() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyList() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> list = List.of();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(STRING);
		List<String> list = List.of("hello");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), list);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<List<String>> stringCodec = new ListCodec<>(STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), List.of("a", "b"));
		assertTrue(stringResult.isSuccess());
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, typeProvider.empty(), List.of(true, false));
		assertTrue(boolResult.isSuccess());
	}
	
	@Test
	void encodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		List<Integer> listWithNull = Lists.newArrayList(1, null, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), listWithNull);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as list"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(List.of(1, 2, 3), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray());
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<String>> codec = new ListCodec<>(STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		Result<List<String>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(List.of("hello"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode list"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<List<String>> stringCodec = new ListCodec<>(STRING);
		Result<List<String>> stringResult = stringCodec.decodeStart(typeProvider, typeProvider.empty(), stringArray);
		assertTrue(stringResult.isSuccess());
		assertEquals(List.of("a", "b"), stringResult.resultOrThrow());
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<List<Boolean>> boolCodec = new ListCodec<>(BOOLEAN);
		Result<List<Boolean>> boolResult = boolCodec.decodeStart(typeProvider, typeProvider.empty(), boolArray);
		assertTrue(boolResult.isSuccess());
		assertEquals(List.of(true, false), boolResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void decodeStartWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(100));
		
		Result<List<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isPartial());
		assertTrue(result.hasError());
		assertTrue(result.hasValue());
	}
	
	@Test
	void equalsAndHashCode() {
		ListCodec<Integer> codec1 = new ListCodec<>(INTEGER);
		ListCodec<Integer> codec2 = new ListCodec<>(INTEGER);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		ListCodec<Integer> codec = new ListCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ListCodec["));
		assertTrue(result.endsWith("]"));
	}
}
