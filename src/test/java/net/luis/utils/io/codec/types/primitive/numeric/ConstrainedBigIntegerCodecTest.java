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
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100));
		assertEquals(new JsonPrimitive("100"), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		BigInteger result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100"));
		assertEquals(BigInteger.valueOf(100), result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		String result = codec.encodeKey(BigInteger.valueOf(100));
		assertEquals("100", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		BigInteger result = codec.decodeKey("100");
		assertEquals(BigInteger.valueOf(100), result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("BigIntegerCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.equalTo(BigInteger.valueOf(42));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notEqualTo(BigInteger.valueOf(42));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(20)));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("20")));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(20)));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.in(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(20)));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("20")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(20)));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.notIn(Set.of(BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30)));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThan(BigInteger.valueOf(50));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(49)));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("49")));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(49)));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.greaterThanOrEqual(BigInteger.valueOf(50));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("49"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThan(BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(101)));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("101")));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(101)));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.lessThanOrEqual(BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("101"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(10)));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(10)));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.between(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(10)));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(10)));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(9)));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("101")));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(9)));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.betweenOrEqual(BigInteger.valueOf(10), BigInteger.valueOf(100));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("101"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(1)));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1")));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(1)));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-5")));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("-5"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ONE));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1")));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.ONE));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(-1)));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-1")));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(-1)));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5")));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(-1)));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-1")));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(-1)));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ONE));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1")));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.ONE));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ONE));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1")));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.ONE));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.ZERO));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.ZERO));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0")));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(101)));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-1")));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(101)));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(43)));
	}
	
	@Test
	void decodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("43")));
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(43)));
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.even();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("43"));
	}
	
	@Test
	void encodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(43)));
	}
	
	@Test
	void decodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("43")));
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(43)));
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.decodeKey("43"));
	}
	
	@Test
	void encodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("42")));
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(42)));
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.odd();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("100")));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(100)));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(101)));
	}
	
	@Test
	void decodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("101")));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(101)));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("101"));
	}
	
	@Test
	void encodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(27)));
	}
	
	@Test
	void decodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("27")));
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(27)));
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("27"));
	}
	
	@Test
	void encodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(28)));
	}
	
	@Test
	void decodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("28")));
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(28)));
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("28"));
	}
	
	@Test
	void encodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(64)));
	}
	
	@Test
	void decodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("64")));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(64)));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decodeKey("64"));
	}
	
	@Test
	void encodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(65)));
	}
	
	@Test
	void decodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("65")));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(65)));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("65"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(49)));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("49")));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(BigInteger.valueOf(49)));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("49"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("50")));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(BigInteger.valueOf(50)));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<BigInteger> codec = Codecs.BIG_INTEGER.custom(value -> {
			if (!value.mod(BigInteger.valueOf(7)).equals(BigInteger.ZERO)) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
}
