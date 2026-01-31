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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigDecimalCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedBigDecimalCodecTest {
	
	private static final BigDecimal VALUE_PI = new BigDecimal("3.14159");
	private static final BigDecimal VALUE_E = new BigDecimal("2.71828");
	private static final BigDecimal VALUE_42 = BigDecimal.valueOf(42);
	private static final BigDecimal VALUE_100 = BigDecimal.valueOf(100);
	private static final BigDecimal VALUE_NEGATIVE = new BigDecimal("-5.5");
	private static final BigDecimal VALUE_HALF = new BigDecimal("0.5");
	private static final BigDecimal VALUE_SMALL = new BigDecimal("0.001");
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("3.14159"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
		assertEquals(VALUE_PI, result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isSuccess());
		assertEquals("3.14159", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isSuccess());
		assertEquals(VALUE_PI, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("BigDecimalCodec", codec.toString());
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_E);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<String> result = codec.encodeKey(VALUE_E);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeKey("2.71828");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_E);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<String> result = codec.encodeKey(VALUE_E);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeKey("2.71828");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_E);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		Result<String> result = codec.encodeKey(VALUE_E);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		Result<BigDecimal> result = codec.decodeKey("2.71828");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.TEN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<String> result = codec.encodeKey(BigDecimal.TEN);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeKey("10");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.TEN);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<String> result = codec.encodeKey(BigDecimal.TEN);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeKey("10");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("9.99"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("9.99"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<String> result = codec.encodeKey(new BigDecimal("9.99"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		Result<BigDecimal> result = codec.decodeKey("9.99");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("100.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<String> result = codec.encodeKey(new BigDecimal("100.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("100.01");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_100);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<String> result = codec.encodeKey(VALUE_100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_NEGATIVE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<String> result = codec.encodeKey(new BigDecimal("100.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		Result<BigDecimal> result = codec.decodeKey("100.01");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<String> result = codec.encodeKey(VALUE_NEGATIVE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		Result<BigDecimal> result = codec.decodeKey("-5.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_NEGATIVE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<String> result = codec.encodeKey(BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<BigDecimal> result = codec.decodeKey("-5.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_NEGATIVE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<String> result = codec.encodeKey(VALUE_NEGATIVE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<BigDecimal> result = codec.decodeKey("-5.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<BigDecimal> result = codec.decodeKey("0");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_NEGATIVE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<String> result = codec.encodeKey(VALUE_NEGATIVE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		Result<BigDecimal> result = codec.decodeKey("-5.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<String> result = codec.encodeKey(BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<BigDecimal> result = codec.decodeKey("0.00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		Result<BigDecimal> result = codec.decodeKey("3.14159");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<String> result = codec.encodeKey(VALUE_NEGATIVE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<BigDecimal> result = codec.decodeKey("-5.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<String> result = codec.encodeKey(BigDecimal.ZERO);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		Result<BigDecimal> result = codec.decodeKey("0.00");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("50"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<String> result = codec.encodeKey(BigDecimal.ZERO);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<BigDecimal> result = codec.decodeKey("75.5");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("100.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<String> result = codec.encodeKey(new BigDecimal("150"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		Result<BigDecimal> result = codec.decodeKey("-0.01");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<String> result = codec.encodeKey(new BigDecimal("100.00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<BigDecimal> result = codec.decodeKey("-100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<String> result = codec.encodeKey(VALUE_HALF);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		Result<BigDecimal> result = codec.decodeKey("0.5");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_HALF);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<String> result = codec.encodeKey(BigDecimal.ONE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<BigDecimal> result = codec.decodeKey("0.75");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("1.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-0.01"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<String> result = codec.encodeKey(VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		Result<BigDecimal> result = codec.decodeKey("2.0");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<String> result = codec.encodeKey(new BigDecimal("99.99"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<BigDecimal> result = codec.decodeKey("99.99");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<String> result = codec.encodeKey(new BigDecimal("3.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		Result<BigDecimal> result = codec.decodeKey("3.1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMinScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<String> result = codec.encodeKey(new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMinScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<BigDecimal> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMinScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMinScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMaxScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMaxScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMaxScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<String> result = codec.encodeKey(VALUE_SMALL);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMaxScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		Result<BigDecimal> result = codec.decodeKey("0.001");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartScaleBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartScaleBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1415"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyScaleBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<String> result = codec.encodeKey(new BigDecimal("3.141"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyScaleBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeKey("3.141");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartScaleBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.1"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartScaleBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyScaleBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyScaleBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<String> result = codec.encodeKey(new BigDecimal("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMinPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<String> result = codec.encodeKey(new BigDecimal("100"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMinPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMinPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<String> result = codec.encodeKey(BigDecimal.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMinPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		Result<BigDecimal> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyMaxPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyMaxPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_PI);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMaxPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<String> result = codec.encodeKey(new BigDecimal("1000"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMaxPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		Result<BigDecimal> result = codec.decodeKey("1000");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPrecisionBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("3.14"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartPrecisionBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.141"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyPrecisionBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<String> result = codec.encodeKey(VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPrecisionBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPrecisionBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), BigDecimal.ONE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPrecisionBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPrecisionBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<String> result = codec.encodeKey(new BigDecimal("10000"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyPrecisionBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		Result<BigDecimal> result = codec.decodeKey("1");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("42"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<String> result = codec.encodeKey(VALUE_100);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<BigDecimal> result = codec.decodeKey("100");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("41"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("41"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<String> result = codec.encodeKey(new BigDecimal("99"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
				return Result.success(null);
			}
			return Result.error("Value must be even");
		});
		
		Result<BigDecimal> result = codec.decodeKey("99");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new BigDecimal("50.00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("50.00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<String> result = codec.encodeKey(new BigDecimal("99.99"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<BigDecimal> result = codec.decodeKey("99.99");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), VALUE_NEGATIVE);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsRangeViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("150.00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCombinedConstraintsScaleViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<String> result = codec.encodeKey(new BigDecimal("50.123"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		Result<BigDecimal> result = codec.decodeKey("-150.123");
		assertTrue(result.isError());
	}
	
}
