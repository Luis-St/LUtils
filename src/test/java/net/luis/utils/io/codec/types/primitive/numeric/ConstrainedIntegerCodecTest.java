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
 * Test class for {@link IntegerCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedIntegerCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(42);
		assertTrue(result.isSuccess());
		assertEquals("42", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Integer> codec = new IntegerCodec();
		assertEquals("IntegerCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<String> result = codec.encodeKey(42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<Integer> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<String> result = codec.encodeKey(100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withEqualTo(42));
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<String> result = codec.encodeKey(100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<String> result = codec.encodeKey(42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotEqualTo(42));
		
		Result<Integer> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<String> result = codec.encodeKey(20);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<String> result = codec.encodeKey(42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<String> result = codec.encodeKey(42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(20));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<String> result = codec.encodeKey(20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withNotIn(Set.of(10, 20, 30)));
		
		Result<Integer> result = codec.decodeKey("20");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 15);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(15));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<String> result = codec.encodeKey(15);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<Integer> result = codec.decodeKey("15");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(10));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<String> result = codec.encodeKey(10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThan(10));
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<String> result = codec.encodeKey(10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 9);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(9));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<String> result = codec.encodeKey(9);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withGreaterThanOrEqual(10));
		
		Result<Integer> result = codec.decodeKey("9");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<String> result = codec.encodeKey(50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<Integer> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<String> result = codec.encodeKey(100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThan(100));
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<String> result = codec.encodeKey(100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 101);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(101));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<String> result = codec.encodeKey(101);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withLessThanOrEqual(100));
		
		Result<Integer> result = codec.decodeKey("101");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<String> result = codec.encodeKey(50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<Integer> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<String> result = codec.encodeKey(5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetween(10, 100));
		
		Result<Integer> result = codec.decodeKey("150");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<String> result = codec.encodeKey(50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 9);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(101));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<String> result = codec.encodeKey(5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withBetweenOrEqual(10, 100));
		
		Result<Integer> result = codec.decodeKey("150");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<Integer> result = codec.decodeKey("-1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(-5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Integer> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(-5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Integer> result = codec.decodeKey("-100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<Integer> result = codec.decodeKey("5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Integer> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-5));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(-1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<Integer> result = codec.decodeKey("-10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(0);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Integer> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(-5);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Integer> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(0);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<Integer> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Integer> result = codec.decodeKey("75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 101);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(150);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<Integer> result = codec.decodeKey("-10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(-4);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 41);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(-3);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<Integer> result = codec.decodeKey("99");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 41);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(-3);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Integer> result = codec.decodeKey("99");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(-4);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 25);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey(-10);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 23);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(7));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey(-11);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<Integer> result = codec.decodeKey("101");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 27);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(9);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<Integer> result = codec.decodeKey("81");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 10);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(15));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(5);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<Integer> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 16);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(64);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Integer> result = codec.decodeKey("256");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 15);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(63);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<Integer> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 21);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(49));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Integer> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 20);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(50));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(15);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Integer> codec = new IntegerCodec().apply(config -> config.withCustom(value -> {
			if (value % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<Integer> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
}
