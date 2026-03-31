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
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), VALUE_PI);
		assertEquals(new JsonPrimitive("3.14159"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159"));
		assertEquals(VALUE_PI, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		String result = codec.encodeKey(VALUE_PI);
		assertEquals("3.14159", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		BigDecimal result = codec.decodeKey("3.14159");
		assertEquals(VALUE_PI, result);
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
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_E));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_E));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.equalTo(VALUE_PI);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2.71828"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_E));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_E));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertDoesNotThrow(() -> codec.decodeKey("2.71828"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notEqualTo(VALUE_PI);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_E));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2.71828")));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_E));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E, VALUE_42));
		
		assertDoesNotThrow(() -> codec.decodeKey("2.71828"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.in(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.notIn(Set.of(VALUE_PI, VALUE_E));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.TEN));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigDecimal.TEN));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThan(BigDecimal.TEN);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.TEN));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10")));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.encodeKey(BigDecimal.TEN));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("9.99")));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("9.99")));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("9.99")));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.greaterThanOrEqual(BigDecimal.TEN);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("9.99"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_100));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_100));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThan(VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_100));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_100));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("100.01")));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100.01")));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("100.01")));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.lessThanOrEqual(VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100.01"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_100));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.between(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_100));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_NEGATIVE));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("100.01")));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, VALUE_100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100.01"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_NEGATIVE));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-5.5"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_NEGATIVE));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigDecimal.ZERO));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("-5.5"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_NEGATIVE));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5")));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_NEGATIVE));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-5.5"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_NEGATIVE));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-5.5")));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_NEGATIVE));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-5.5"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigDecimal.ZERO));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.00"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14159"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_NEGATIVE));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("-5.5"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ZERO));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigDecimal.ZERO));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.00"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("50")));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigDecimal.ZERO));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75.5"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("100.01")));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-1")));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("150")));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-0.01"));
	}
	
	@Test
	void encodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("100.00")));
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertDoesNotThrow(() -> codec.decodeKey("-100"));
	}
	
	@Test
	void encodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_HALF));
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.integral();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.5"));
	}
	
	@Test
	void encodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_HALF));
	}
	
	@Test
	void decodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigDecimal.ONE));
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.75"));
	}
	
	@Test
	void encodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("1.01")));
	}
	
	@Test
	void decodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-0.01")));
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_PI));
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2.0"));
	}
	
	@Test
	void encodeScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodeScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14")));
	}
	
	@Test
	void encodeKeyScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("99.99")));
	}
	
	@Test
	void decodeKeyScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertDoesNotThrow(() -> codec.decodeKey("99.99"));
	}
	
	@Test
	void encodeScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("3.1")));
	}
	
	@Test
	void decodeKeyScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scale(2);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.1"));
	}
	
	@Test
	void encodeMinScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeMinScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyMinScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("3.14")));
	}
	
	@Test
	void decodeKeyMinScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeMinScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.1")));
	}
	
	@Test
	void decodeMinScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1")));
	}
	
	@Test
	void encodeKeyMinScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyMinScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minScale(2);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeMaxScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodeMaxScaleConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1")));
	}
	
	@Test
	void encodeKeyMaxScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyMaxScaleConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeMaxScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeMaxScaleConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyMaxScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_SMALL));
	}
	
	@Test
	void decodeKeyMaxScaleConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxScale(2);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.001"));
	}
	
	@Test
	void encodeScaleBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodeScaleBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1415")));
	}
	
	@Test
	void encodeKeyScaleBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("3.141")));
	}
	
	@Test
	void decodeKeyScaleBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.141"));
	}
	
	@Test
	void encodeScaleBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.1")));
	}
	
	@Test
	void decodeScaleBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyScaleBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyScaleBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.scaleBetween(2, 4);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodePrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodePrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14")));
	}
	
	@Test
	void encodeKeyPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("100")));
	}
	
	@Test
	void decodeKeyPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodePrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodePrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precision(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeMinPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeMinPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyMinPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("100")));
	}
	
	@Test
	void decodeKeyMinPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeMinPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeMinPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyMinPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigDecimal.ONE));
	}
	
	@Test
	void decodeKeyMinPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.minPrecision(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeMaxPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodeMaxPrecisionConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.1")));
	}
	
	@Test
	void encodeKeyMaxPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyMaxPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeMaxPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_PI));
	}
	
	@Test
	void decodeMaxPrecisionConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyMaxPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("1000")));
	}
	
	@Test
	void decodeKeyMaxPrecisionConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.maxPrecision(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1000"));
	}
	
	@Test
	void encodePrecisionBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("3.14")));
	}
	
	@Test
	void decodePrecisionBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.141")));
	}
	
	@Test
	void encodeKeyPrecisionBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_42));
	}
	
	@Test
	void decodeKeyPrecisionBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodePrecisionBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigDecimal.ONE));
	}
	
	@Test
	void decodePrecisionBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("3.14159")));
	}
	
	@Test
	void encodeKeyPrecisionBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("10000")));
	}
	
	@Test
	void decodeKeyPrecisionBetweenConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.precisionBetween(2, 4);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), VALUE_42));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(VALUE_100));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("41")));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("41")));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("99")));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.custom(value -> {
			if (value.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) != 0) {
				throw new ConstraintViolateException("Value must be even");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("99"));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new BigDecimal("50.00")));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50.00")));
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertDoesNotThrow(() -> codec.encodeKey(new BigDecimal("99.99")));
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertDoesNotThrow(() -> codec.decodeKey("99.99"));
	}
	
	@Test
	void encodeCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), VALUE_NEGATIVE));
	}
	
	@Test
	void decodeCombinedConstraintsRangeViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("150.00")));
	}
	
	@Test
	void encodeKeyCombinedConstraintsScaleViolation() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(new BigDecimal("50.123")));
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<BigDecimal> codec = Codecs.BIG_DECIMAL.apply(config -> config
			.withPositive()
			.withLessThanOrEqual(VALUE_100)
			.withMaxScale(2));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-150.123"));
	}
	
}
