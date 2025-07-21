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
		assertDoesNotThrow(() -> new OptionalCodec<>(Codec.INTEGER));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), Optional.of(1)));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, Optional.of(1)));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertSame(current, result.orThrow());
	}
	
	@Test
	void encodeStartWithEmptyOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, Optional.empty());
		assertTrue(result.isSuccess());
		assertSame(current, result.orThrow());
	}
	
	@Test
	void encodeStartWithPresentOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, Optional.of(42));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject current = new JsonObject();
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(Codec.STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, current, Optional.of("hello"));
		assertTrue(stringResult.isSuccess());
		assertEquals(new JsonPrimitive("hello"), stringResult.orThrow());
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(Codec.BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, current, Optional.of(true));
		assertTrue(boolResult.isSuccess());
		assertEquals(new JsonPrimitive(true), boolResult.orThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithEmpty() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(42, result.orThrow().orElseThrow());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(Codec.STRING);
		Result<Optional<String>> stringResult = stringCodec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.orThrow().isPresent());
		assertEquals("hello", stringResult.orThrow().orElseThrow());
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(Codec.BOOLEAN);
		Result<Optional<Boolean>> boolResult = boolCodec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isSuccess());
		assertTrue(boolResult.orThrow().isPresent());
		assertTrue(boolResult.orThrow().orElseThrow());
	}
	
	@Test
	void orElseWithValue() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER).orElse(Optional.of(99));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(99, result.orThrow().orElseThrow());
	}
	
	@Test
	void orElseWithEmptyOptional() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER).orElse(Optional.empty());
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void orElseWithNull() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER).orElse(null);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void orElseGetNullChecks() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(Codec.INTEGER);
		assertThrows(NullPointerException.class, () -> codec.orElseGet(null));
	}
	
	@Test
	void orElseGetWithSupplier() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(Codec.INTEGER).orElseGet(() -> Optional.of(123));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isPresent());
		assertEquals(123, result.orThrow().orElseThrow());
	}
	
	@Test
	void orElseFlatWithValue() {
		Codec<Integer> codec = new OptionalCodec<>(Codec.INTEGER).orElseFlat(99);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertEquals(99, nullResult.orThrow());
		
		Result<Integer> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(validResult.isSuccess());
		assertEquals(42, validResult.orThrow());
	}
	
	@Test
	void orElseFlatWithNull() {
		Codec<Integer> codec = new OptionalCodec<>(Codec.INTEGER).orElseFlat(null);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void orElseGetFlatNullChecks() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(Codec.INTEGER);
		assertThrows(NullPointerException.class, () -> codec.orElseGetFlat(null));
	}
	
	@Test
	void orElseGetFlatWithSupplier() {
		Codec<Integer> codec = new OptionalCodec<>(Codec.INTEGER).orElseGetFlat(() -> 123);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertEquals(123, nullResult.orThrow());
		
		Result<Integer> invalidResult = codec.decodeStart(typeProvider, new JsonPrimitive("invalid"));
		assertTrue(invalidResult.isSuccess());
		assertEquals(123, invalidResult.orThrow());
		
		Result<Integer> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(validResult.isSuccess());
		assertEquals(42, validResult.orThrow());
	}
	
	@Test
	void equalsAndHashCode() {
		OptionalCodec<Integer> codec1 = new OptionalCodec<>(Codec.INTEGER);
		OptionalCodec<Integer> codec2 = new OptionalCodec<>(Codec.INTEGER);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(Codec.INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("OptionalCodec["));
		assertTrue(result.endsWith("]"));
	}
}
