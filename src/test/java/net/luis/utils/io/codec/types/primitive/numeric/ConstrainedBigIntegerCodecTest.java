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

import java.math.BigInteger;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigIntegerCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedBigIntegerCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("100"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(100), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
		assertEquals("100", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(100), result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<BigInteger> codec = new BigIntegerCodec();
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("BigIntegerCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotEqualTo(BigInteger.valueOf(42)));
		
		Result<BigInteger> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("20"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(20));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeKey("20");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(20));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("20"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(20));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withNotIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30))));
		
		Result<BigInteger> result = codec.decodeKey("20");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThan(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(49));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("49"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(49));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withGreaterThanOrEqual(BigInteger.valueOf(50)));
		
		Result<BigInteger> result = codec.decodeKey("49");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThan(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("101"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withLessThanOrEqual(BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("101");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(10));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(10));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetween(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(10));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(9));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("101"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(9));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withBetweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100)));
		
		Result<BigInteger> result = codec.decodeKey("101");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPositive);
		
		Result<BigInteger> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-5"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<BigInteger> result = codec.decodeKey("-5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<String> result = codec.encodeKey(BigInteger.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonPositive);
		
		Result<BigInteger> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(-1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-1"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(-1));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<BigInteger> result = codec.decodeKey("-1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNegative);
		
		Result<BigInteger> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("5"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<BigInteger> result = codec.decodeKey("5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(-1));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(-1));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonNegative);
		
		Result<BigInteger> result = codec.decodeKey("-1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<BigInteger> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<String> result = codec.encodeKey(BigInteger.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withZero);
		
		Result<BigInteger> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ONE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(BigInteger.ONE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<BigInteger> result = codec.decodeKey("1");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<String> result = codec.encodeKey(BigInteger.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withNonZero);
		
		Result<BigInteger> result = codec.decodeKey("0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPercentage);
		
		Result<BigInteger> result = codec.decodeKey("-1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(42));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<BigInteger> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(43));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("43"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(43));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withEven);
		
		Result<BigInteger> result = codec.decodeKey("43");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(43));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("43"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(43));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<BigInteger> result = codec.decodeKey("43");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withOdd);
		
		Result<BigInteger> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(100));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<BigInteger> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("101"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(101));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withDivisibleBy(5));
		
		Result<BigInteger> result = codec.decodeKey("101");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(27));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("27"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(27));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<BigInteger> result = codec.decodeKey("27");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(28));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("28"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(28));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withPowerOf(3));
		
		Result<BigInteger> result = codec.decodeKey("28");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(64));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("64"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(64));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<BigInteger> result = codec.decodeKey("64");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(65));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("65"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(65));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(IntegerConstraintConfig::withPowerOfTwo);
		
		Result<BigInteger> result = codec.decodeKey("65");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(49));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("49"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(49));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<BigInteger> result = codec.decodeKey("49");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<String> result = codec.encodeKey(BigInteger.valueOf(50));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<BigInteger> codec = new BigIntegerCodec().apply(config -> config.withCustom(value -> {
			if (value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				return Result.success();
			}
			return Result.error("Value must be divisible by 7");
		}));
		
		Result<BigInteger> result = codec.decodeKey("50");
		assertTrue(result.isError());
	}
	
}
