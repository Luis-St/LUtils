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
 * Test class for {@link FloatCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedFloatCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 3.14f);
		assertEquals(new JsonPrimitive(3.14f), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		Float result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertEquals(3.14f, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		String result = codec.encodeKey(3.14f);
		assertEquals("3.14", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		Float result = codec.decodeKey("3.14");
		assertEquals(3.14f, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Float> codec = Codecs.FLOAT.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Float> codec = Codecs.FLOAT;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("FloatCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14f));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.equalTo(3.14f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14f));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notEqualTo(3.14f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f, 100.5f));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f, 100.5f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f, 100.5f));
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f, 100.5f));
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100.5f));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.5f)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100.5f));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.in(Set.of(3.14f, 42.0f));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100.5"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100.5f));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.5f)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertDoesNotThrow(() -> codec.encodeKey(100.5f));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertDoesNotThrow(() -> codec.decodeKey("100.5"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14f));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notIn(Set.of(3.14f, 42.0f));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10.0f));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(10.0f));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.greaterThan(10.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5.0"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 10.0f));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(10.0f));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 9.99f));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(9.99f));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.greaterThanOrEqual(10.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5.0"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100.0f));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0f)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100.0f));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.lessThan(100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150.0"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 100.0f));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(100.0f));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 100.01f));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0f)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(100.01f));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.lessThanOrEqual(100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150.0"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0f)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("50.0"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 10.0f));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5.0f));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.between(10.0f, 100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150.0"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 10.0f));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.encodeKey(50.0f));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertDoesNotThrow(() -> codec.decodeKey("100.0"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 9.99f));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.01f)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(5.0f));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.betweenOrEqual(10.0f, 100.0f);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150.0"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0f)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(0.001f));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-1.0f));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(-42.0f));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.001f));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1.0"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), -3.14f));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-42.0f)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(-0.001f));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-42.0"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(1.0f));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0f));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -0.001f));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-42.0f));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1.0"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(0.0f));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.001f));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-1.0f));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-42.0f)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(0.001f));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("-0.001"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.0f));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0.0f));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50.0f));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(100.0f));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75.5"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -1.0f));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.01f)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(150.0f));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-0.1"));
	}
	
	@Test
	void encodeFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MAX_VALUE)));
	}
	
	@Test
	void encodeKeyFiniteConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertDoesNotThrow(() -> codec.encodeKey(Float.MIN_VALUE));
	}
	
	@Test
	void decodeKeyFiniteConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Float.POSITIVE_INFINITY));
	}
	
	@Test
	void decodeFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.NEGATIVE_INFINITY)));
	}
	
	@Test
	void encodeKeyFiniteConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(Float.NaN));
	}
	
	@Test
	void decodeKeyFiniteConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.finite();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Infinity"));
	}
	
	@Test
	void encodeNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.POSITIVE_INFINITY)));
	}
	
	@Test
	void encodeKeyNotNaNConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertDoesNotThrow(() -> codec.encodeKey(Float.NEGATIVE_INFINITY));
	}
	
	@Test
	void decodeKeyNotNaNConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Float.NaN));
	}
	
	@Test
	void decodeNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.NaN)));
	}
	
	@Test
	void encodeKeyNotNaNConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(Float.NaN));
	}
	
	@Test
	void decodeKeyNotNaNConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.notNaN();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("NaN"));
	}
	
	@Test
	void encodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(100.0f)));
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertDoesNotThrow(() -> codec.encodeKey(-5.0f));
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14f));
	}
	
	@Test
	void decodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.5f)));
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(2.718f));
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.integral();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("1.5"));
	}
	
	@Test
	void encodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.5f));
	}
	
	@Test
	void decodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0f)));
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertDoesNotThrow(() -> codec.encodeKey(1.0f));
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.75"));
	}
	
	@Test
	void encodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -0.1f));
	}
	
	@Test
	void decodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1.1f)));
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(2.0f));
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-0.5"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 4.0f));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10.0f)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(6.0f));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("8.0"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.0f));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(5.0f)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(7.0f));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Float> codec = Codecs.FLOAT.custom(value -> {
			if (value % 2.0f != 0.0f) throw new ConstraintViolateException("Value must be even");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("9.0"));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0f));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0f)));
	}
	
	@Test
	void encodeKeyCombinedConstraintsSuccess() {
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withNonNegative()
			.withNormalized());
		
		assertDoesNotThrow(() -> codec.encodeKey(0.5f));
	}
	
	@Test
	void decodeKeyCombinedConstraintsSuccess() {
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withNonNegative()
			.withNormalized());
		
		assertDoesNotThrow(() -> codec.decodeKey("0.75"));
	}
	
	@Test
	void encodeCombinedConstraintsPositiveViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withLessThan(100.0f));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -5.0f));
	}
	
	@Test
	void decodeCombinedConstraintsFiniteViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withFinite());
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.POSITIVE_INFINITY)));
	}
	
	@Test
	void encodeKeyCombinedConstraintsRangeViolation() {
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withLessThan(100.0f));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(150.0f));
	}
	
	@Test
	void decodeKeyCombinedConstraintsMultipleViolations() {
		Codec<Float> codec = Codecs.FLOAT.apply(config -> config
			.withPositive()
			.withFinite()
			.withLessThan(100.0f));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-50.0"));
	}
	
}
