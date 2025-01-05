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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

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
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), list));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, list));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), list));
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonArray.class, result.orThrow());
		assertEquals(new JsonArray(list.stream().map(JsonPrimitive::new).toList()), result.orThrow());
	}
	
	@Test
	void encodeStartSized() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 3, 3);
		List<Integer> list = List.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), list));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, list));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), List.of(1, 2)).isError());
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), List.of(1, 2, 3, 4)).isError());
		
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), list));
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonArray.class, result.orThrow());
		assertEquals(new JsonArray(list.stream().map(JsonPrimitive::new).toList()), result.orThrow());
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER);
		List<Integer> list = List.of(1, 2, 3);
		JsonArray array = new JsonArray(list.stream().map(JsonPrimitive::new).toList());
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, array));
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null)).isError());
		Result<List<Integer>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, array));
		assertTrue(result.isSuccess());
		assertIterableEquals(list, result.orThrow());
	}
	
	@Test
	void decodeStartSized() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<List<Integer>> codec = new ListCodec<>(Codec.INTEGER, 3, 3);
		List<Integer> list = List.of(1, 2, 3);
		JsonArray array = new JsonArray(list.stream().map(JsonPrimitive::new).toList());
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, array));
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null)).isError());
		assertTrue(codec.decodeStart(typeProvider, new JsonArray(Stream.of(1, 2).map(JsonPrimitive::new).toList())).isError());
		assertTrue(codec.decodeStart(typeProvider, new JsonArray(Stream.of(1, 2, 3, 4).map(JsonPrimitive::new).toList())).isError());
		
		Result<List<Integer>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, array));
		assertTrue(result.isSuccess());
		assertIterableEquals(list, result.orThrow());
	}
}
