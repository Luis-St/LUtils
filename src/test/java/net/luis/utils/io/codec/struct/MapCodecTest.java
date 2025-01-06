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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MapCodec}.<br>
 *
 * @author Luis-St
 */
class MapCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new MapCodec<>(null, Codec.BOOLEAN));
		assertThrows(NullPointerException.class, () -> new MapCodec<>(Codec.INTEGER, null));
		assertThrows(NullPointerException.class, () -> new MapCodec<>(null, Codec.BOOLEAN, 0, Integer.MAX_VALUE));
		assertThrows(NullPointerException.class, () -> new MapCodec<>(Codec.INTEGER, null, 0, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN, -1, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN, 1, 0));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		JsonObject object = new JsonObject(Map.of(
			"1", new JsonPrimitive(true), "2", new JsonPrimitive(false)
		));
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), map));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, map));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), map));
		assertTrue(result.isSuccess());
		assertEquals(object, assertInstanceOf(JsonObject.class, result.orThrow()));
	}
	
	@Test
	void encodeStartSized() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN, 2, 2);
		Map<Integer, Boolean> map = Map.of(1, true, 2, false);
		JsonObject object = new JsonObject(Map.of(
			"1", new JsonPrimitive(true), "2", new JsonPrimitive(false)
		));
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), map));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, map));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), Map.of(1, true)).isError());
		assertTrue(codec.encodeStart(typeProvider, typeProvider.empty(), Map.of(1, true, 2, false, 3, true)).isError());
		
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), map));
		assertTrue(result.isSuccess());
		assertEquals(object, assertInstanceOf(JsonObject.class, result.orThrow()));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN);
		JsonObject object = new JsonObject(Map.of(
			"1", new JsonPrimitive(true), "2", new JsonPrimitive(false)
		));
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, object));
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null)).isError());
		Result<Map<Integer, Boolean>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, object));
		assertTrue(result.isSuccess());
		assertEquals(Map.of(1, true, 2, false), result.orThrow());
	}
	
	@Test
	void decodeStartSized() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Map<Integer, Boolean>> codec = new MapCodec<>(Codec.INTEGER, Codec.BOOLEAN, 2, 2);
		JsonObject object = new JsonObject(Map.of(
			"1", new JsonPrimitive(true), "2", new JsonPrimitive(false)
		));
		JsonObject objectSized1 = new JsonObject(Map.of(
			"1", new JsonPrimitive(true)
		));
		JsonObject objectSized3 = new JsonObject(Map.of(
			"1", new JsonPrimitive(true), "2", new JsonPrimitive(false), "3", new JsonPrimitive(true)
		));
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, object));
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null)).isError());
		assertTrue(codec.decodeStart(typeProvider, objectSized1).isError());
		assertTrue(codec.decodeStart(typeProvider, objectSized3).isError());
		Result<Map<Integer, Boolean>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, object));
		assertTrue(result.isSuccess());
		assertEquals(Map.of(1, true, 2, false), result.orThrow());
	}
}
