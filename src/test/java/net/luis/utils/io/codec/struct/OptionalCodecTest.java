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
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static net.luis.utils.io.codec.Codecs.*;
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
		assertDoesNotThrow(() -> new OptionalCodec<>(INTEGER));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), Optional.of(1)));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, Optional.of(1)));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertSame(current, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, Optional.empty());
		assertTrue(result.isSuccess());
		assertSame(current, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithPresentOptional() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, Optional.of(42));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject current = new JsonObject();
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, current, Optional.of("hello"));
		assertTrue(stringResult.isSuccess());
		assertEquals(new JsonPrimitive("hello"), stringResult.resultOrThrow());
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, current, Optional.of(true));
		assertTrue(boolResult.isSuccess());
		assertEquals(new JsonPrimitive(true), boolResult.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithEmpty() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isPresent());
		assertEquals(42, result.resultOrThrow().orElseThrow());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER);
		
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Optional<String>> stringCodec = new OptionalCodec<>(STRING);
		Result<Optional<String>> stringResult = stringCodec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(stringResult.isSuccess());
		assertTrue(stringResult.resultOrThrow().isPresent());
		assertEquals("hello", stringResult.resultOrThrow().orElseThrow());
		
		Codec<Optional<Boolean>> boolCodec = new OptionalCodec<>(BOOLEAN);
		Result<Optional<Boolean>> boolResult = boolCodec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isSuccess());
		assertTrue(boolResult.resultOrThrow().isPresent());
		assertTrue(boolResult.resultOrThrow().orElseThrow());
	}
	
	@Test
	void withDefaultWithValue() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).withDefault(Optional.of(99));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isPresent());
		assertEquals(99, result.resultOrThrow().orElseThrow());
	}
	
	@Test
	void orElseWithEmptyOptional() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).withDefault(Optional.empty());
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isEmpty());
	}
	
	@Test
	void withDefaultWithNull() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).withDefault(null);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void withDefaultGetNullChecks() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(INTEGER);
		assertThrows(NullPointerException.class, () -> codec.withDefaultGet(null));
	}
	
	@Test
	void withDefaultGetWithSupplier() {
		Codec<Optional<Integer>> codec = new OptionalCodec<>(INTEGER).withDefaultGet(() -> Optional.of(123));
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Optional<Integer>> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isPresent());
		assertEquals(123, result.resultOrThrow().orElseThrow());
	}
	
	@Test
	void orElseWithValue() {
		Codec<Integer> codec = new OptionalCodec<>(INTEGER).orElse(99);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertEquals(99, nullResult.resultOrThrow());
		
		Result<Integer> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(validResult.isSuccess());
		assertEquals(42, validResult.resultOrThrow());
	}
	
	@Test
	void orElseWithNull() {
		Codec<Integer> codec = new OptionalCodec<>(INTEGER).orElse(null);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void orElseGetNullChecks() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(INTEGER);
		assertThrows(NullPointerException.class, () -> codec.orElseGet(null));
	}
	
	@Test
	void orElseGetWithSupplier() {
		Codec<Integer> codec = new OptionalCodec<>(INTEGER).orElseGet(() -> 123);
		assertNotNull(codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertEquals(123, nullResult.resultOrThrow());
		
		Result<Integer> invalidResult = codec.decodeStart(typeProvider, new JsonPrimitive("invalid"));
		assertTrue(invalidResult.isSuccess());
		assertEquals(123, invalidResult.resultOrThrow());
		
		Result<Integer> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(validResult.isSuccess());
		assertEquals(42, validResult.resultOrThrow());
	}
	
	@Test
	void equalsAndHashCode() {
		OptionalCodec<Integer> codec1 = new OptionalCodec<>(INTEGER);
		OptionalCodec<Integer> codec2 = new OptionalCodec<>(INTEGER);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		OptionalCodec<Integer> codec = new OptionalCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("OptionalCodec["));
		assertTrue(result.endsWith("]"));
	}
}
