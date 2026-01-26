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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LongCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLongCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100L);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(100L), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isSuccess());
		assertEquals(100L, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
		assertEquals("100", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
		assertEquals(100L, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Long> codec = new LongCodec();
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("LongCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<Long> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withEqualTo(42L));
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotEqualTo(42L));
		
		Result<Long> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 20L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(20L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<String> result = codec.encodeKey(20L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 20L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(20L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<String> result = codec.encodeKey(20L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withNotIn(Set.of(10L, 20L, 30L)));
		
		Result<Long> result = codec.decodeKey("20");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(51L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(25L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThan(50L));
		
		Result<Long> result = codec.decodeKey("25");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<Long> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 49L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(25L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<String> result = codec.encodeKey(49L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withGreaterThanOrEqual(50L));
		
		Result<Long> result = codec.decodeKey("25");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 25L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(49L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<String> result = codec.encodeKey(25L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<Long> result = codec.decodeKey("25");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThan(50L));
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(25L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<Long> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 51L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<String> result = codec.encodeKey(51L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withLessThanOrEqual(50L));
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<Long> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<String> result = codec.encodeKey(5L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetween(10L, 100L));
		
		Result<Long> result = codec.decodeKey("200");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<Long> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 9L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(101L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<String> result = codec.encodeKey(5L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withBetweenOrEqual(10L, 100L));
		
		Result<Long> result = codec.decodeKey("200");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(-42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-42L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(-100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Long> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(-100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Long> result = codec.decodeKey("-42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -1L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-42L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(-100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Long> result = codec.decodeKey("-1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(0L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Long> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(-100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Long> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(0L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Long> result = codec.decodeKey("75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 101L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(200L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Long> result = codec.decodeKey("-50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Long> result = codec.decodeKey("-2");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 41L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(99L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Long> result = codec.decodeKey("-3");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 41L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(99L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Long> result = codec.decodeKey("-3");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Long> result = codec.decodeKey("-2");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<String> result = codec.encodeKey(25L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<Long> result = codec.decodeKey("-15");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<String> result = codec.encodeKey(99L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withDivisibleBy(5L));
		
		Result<Long> result = codec.decodeKey("-7");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 27L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(81L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<Long> result = codec.decodeKey("9");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(10L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(100L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withPowerOf(3));
		
		Result<Long> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 64L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(256L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Long> result = codec.decodeKey("1024");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Long> result = codec.decodeKey("999");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(49L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(77L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Long> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 43L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(50L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Long> result = codec.decodeKey("99");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<String> result = codec.encodeKey(2L);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<Long> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven());
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -2L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsEvenViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven());
		
		Result<Long> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(41L));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCombinedConstraintsRangeViolation() {
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<String> result = codec.encodeKey(102L);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<Long> codec = new LongCodec().apply(config -> config
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100L));
		
		Result<Long> result = codec.decodeKey("-99");
		assertTrue(result.isError());
	}
	
}
