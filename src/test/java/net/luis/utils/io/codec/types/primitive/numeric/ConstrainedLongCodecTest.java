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
 * Test class for {@link LongCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLongCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 100L);
		assertEquals(new JsonPrimitive(100L), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		Long result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L));
		assertEquals(100L, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Long> codec = Codecs.LONG.positive();
		
		String result = codec.encodeKey(100L);
		assertEquals("100", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Long> codec = Codecs.LONG.positive();
		
		Long result = codec.decodeKey("100");
		assertEquals(100L, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Long> codec = Codecs.LONG.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Long> codec = Codecs.LONG;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("LongCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertDoesNotThrow(() -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100L));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.equalTo(42L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100L));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.notEqualTo(42L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 20L));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(20L)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.encodeKey(20L));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.in(Set.of(10L, 20L, 30L));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 20L));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(20L)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(20L));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.notIn(Set.of(10L, 20L, 30L));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100L));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(51L)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(25L)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.greaterThan(50L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("25"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 49L));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(25L)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(49L));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.greaterThanOrEqual(50L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("25"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 25L));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(49L)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertDoesNotThrow(() -> codec.encodeKey(25L));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertDoesNotThrow(() -> codec.decodeKey("25"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.lessThan(50L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(25L)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 51L));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(51L));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.lessThanOrEqual(50L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50L)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertDoesNotThrow(() -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10L));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5L));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.between(10L, 100L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("200"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 10L));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertDoesNotThrow(() -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 9L));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(101L)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5L));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.betweenOrEqual(10L, 100L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("200"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-42L));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-42L)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(-100L));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 1L));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), -42L));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(-100L));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-42"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -1L));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-42L)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-100L));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(0L));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 1L));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42L));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(-100L));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0L));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0L));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50L));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 101L));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-1L)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(200L));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-50"));
	}
	
	@Test
	void encodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.even();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.even();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.even();
		
		assertDoesNotThrow(() -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.even();
		
		assertDoesNotThrow(() -> codec.decodeKey("-2"));
	}
	
	@Test
	void encodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 41L));
	}
	
	@Test
	void decodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.even();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(99L));
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.even();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-3"));
	}
	
	@Test
	void encodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 41L));
	}
	
	@Test
	void decodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertDoesNotThrow(() -> codec.encodeKey(99L));
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertDoesNotThrow(() -> codec.decodeKey("-3"));
	}
	
	@Test
	void encodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.odd();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-2"));
	}
	
	@Test
	void encodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100L));
	}
	
	@Test
	void decodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0L)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertDoesNotThrow(() -> codec.encodeKey(25L));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertDoesNotThrow(() -> codec.decodeKey("-15"));
	}
	
	@Test
	void encodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(99L));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.divisibleBy(5L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-7"));
	}
	
	@Test
	void encodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 27L));
	}
	
	@Test
	void decodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encodeKey(81L));
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("9"));
	}
	
	@Test
	void encodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10L)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100L));
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 64L));
	}
	
	@Test
	void decodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1L)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encodeKey(256L));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decodeKey("1024"));
	}
	
	@Test
	void encodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("999"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(49L)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(77L));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 43L));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(50L));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Long> codec = Codecs.LONG.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("99"));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42L));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100L)));
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertDoesNotThrow(() -> codec.encodeKey(2L));
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive().even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -2L));
	}
	
	@Test
	void decodeCombinedConstraintsEvenViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = Codecs.LONG.positive().even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(41L)));
	}
	
	@Test
	void encodeKeyCombinedConstraintsRangeViolation() {
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(102L));
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<Long> codec = Codecs.LONG.positive().even().lessThanOrEqual(100L);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-99"));
	}
	
}
