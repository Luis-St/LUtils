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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedIntegerCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 42);
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		Integer result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		String result = codec.encodeKey(42);
		assertEquals("42", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		Integer result = codec.decodeKey("42");
		assertEquals(42, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Integer> codec = Codecs.INTEGER;
		assertEquals("IntegerCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertDoesNotThrow(() -> codec.encodeKey(42));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.equalTo(42);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertDoesNotThrow(() -> codec.encodeKey(100));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.notEqualTo(42);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 20));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(20)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.encodeKey(20));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.in(Set.of(10, 20, 30));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.encodeKey(42));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 20));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(20)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(20));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.notIn(Set.of(10, 20, 30));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 15));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(15)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertDoesNotThrow(() -> codec.encodeKey(15));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertDoesNotThrow(() -> codec.decodeKey("15"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(10));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThan(10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 10));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertDoesNotThrow(() -> codec.encodeKey(10));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 9));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(9)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(9));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.greaterThanOrEqual(10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("9"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertDoesNotThrow(() -> codec.encodeKey(50));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.lessThan(100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertDoesNotThrow(() -> codec.encodeKey(100));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 101));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(101)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(101));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.lessThanOrEqual(100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("101"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertDoesNotThrow(() -> codec.encodeKey(50));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.between(10, 100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 10));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertDoesNotThrow(() -> codec.encodeKey(50));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 9));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(101)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.betweenOrEqual(10, 100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 1));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(1));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(-5));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 1));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(5)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(1));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), -1));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-10)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(-5));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-100"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(5));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -1));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-5)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-1));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(0));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 1));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 1));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(-5));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(100));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 101));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(150));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertDoesNotThrow(() -> codec.encodeKey(-4));
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 41));
	}
	
	@Test
	void decodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-3));
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.even();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("99"));
	}
	
	@Test
	void encodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 41));
	}
	
	@Test
	void decodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.encodeKey(-3));
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertDoesNotThrow(() -> codec.decodeKey("99"));
	}
	
	@Test
	void encodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void decodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-4));
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.odd();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 25));
	}
	
	@Test
	void decodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encodeKey(-10));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 23));
	}
	
	@Test
	void decodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(7)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-11));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("101"));
	}
	
	@Test
	void encodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 27));
	}
	
	@Test
	void decodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(9));
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("81"));
	}
	
	@Test
	void encodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10));
	}
	
	@Test
	void decodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(15)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5));
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 16));
	}
	
	@Test
	void decodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encodeKey(64));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decodeKey("256"));
	}
	
	@Test
	void encodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 15));
	}
	
	@Test
	void decodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(63));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 21));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(49)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(14));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 20));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(15));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Integer> codec = Codecs.INTEGER.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
}
