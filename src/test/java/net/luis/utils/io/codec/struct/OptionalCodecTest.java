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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalCodec}.<br>
 *
 * @author Luis-St
 */
class OptionalCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new OptionalCodec<>(null));
	}
	
	@Test
	void encodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), Optional.of(1)));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, Optional.of(1)));
		
		Result<JsonElement> nullResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null));
		assertTrue(nullResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, nullResult.orThrow());
		
		Result<JsonElement> emptyResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), Optional.empty()));
		assertTrue(emptyResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, emptyResult.orThrow());
		
		Result<JsonElement> result = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), Optional.of(1)));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(1), assertInstanceOf(JsonPrimitive.class, result.orThrow()));
	}
	
	@Test
	void decodeStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, Optional.of(1)));
		
		Result<Optional<Integer>> nullResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null));
		assertTrue(nullResult.isSuccess());
		assertTrue(nullResult.orThrow().isEmpty());
		
		Result<Optional<Integer>> invalidResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive("test")));
		assertTrue(invalidResult.isSuccess());
		assertTrue(invalidResult.orThrow().isEmpty());
		
		Result<Optional<Integer>> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(1, result.orThrow().get());
	}
	
	@Test
	void decodeStartFlat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new OptionalCodec<>(Codec.INTEGER).orElseGetFlat(() -> 10);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, 1));
		
		Result<Integer> nullResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null));
		assertTrue(nullResult.isSuccess());
		assertEquals(10, nullResult.orThrow());
		
		Result<Integer> invalidResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive("test")));
		assertTrue(invalidResult.isSuccess());
		assertEquals(10, invalidResult.orThrow());
		
		Result<Integer> result = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(result.isSuccess());
		assertEquals(1, result.orThrow());
	}
	
	@Test
	void orElseFlat() {
		assertDoesNotThrow(() -> new OptionalCodec<>(Codec.INTEGER).orElseFlat(null));
		assertNotNull(new OptionalCodec<>(Codec.INTEGER).orElseFlat(1));
	}
	
	@Test
	void orGetDefault() {
		assertThrows(NullPointerException.class, () -> new OptionalCodec<>(Codec.INTEGER).orElseGetFlat(null));
		assertNotNull(new OptionalCodec<>(Codec.INTEGER).orElseGetFlat(() -> 1));
	}
}
