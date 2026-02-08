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
 * Test class for {@link DoubleCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedDoubleCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 3.14);
		assertEquals(new JsonPrimitive(3.14), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Double result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14));
		assertEquals(3.14, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		String result = codec.encodeKey(3.14);
		assertEquals("3.14", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		Double result = codec.decodeKey("3.14");
		assertEquals(3.14, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Double> codec = Codecs.DOUBLE;
		assertEquals("DoubleCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.equalTo(3.14);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notEqualTo(42.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.in(Set.of(1.0, 2.0, 3.14));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notIn(Set.of(1.0, 2.0, 42.0));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(0.0);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThan(10.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.greaterThanOrEqual(10.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(10.0);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThan(3.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.14);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.lessThanOrEqual(3.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 10.0);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.between(0.0, 3.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(3.14, 10.0);
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.betweenOrEqual(5.0, 10.0);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -3.14));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-3.14));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-3.14"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), -3.14));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey(-3.14));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("-3.14"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), -3.14));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(-3.14));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-3.14"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), -3.14));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(-3.14));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-3.14"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.0));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(0.0));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 0.0));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.0)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(0.0));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0.0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 50.0));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(50.0)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey(50.0));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("50.0"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 150.0));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(150.0)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(150.0));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150.0"));
	}
	
	@Test
	void encodeFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeFiniteConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyFiniteConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyFiniteConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY));
	}
	
	@Test
	void decodeFiniteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.POSITIVE_INFINITY)));
	}
	
	@Test
	void encodeKeyFiniteConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(Double.POSITIVE_INFINITY));
	}
	
	@Test
	void decodeKeyFiniteConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.finite();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("Infinity"));
	}
	
	@Test
	void encodeNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNotNaNConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNotNaNConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertDoesNotThrow(() -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNotNaNConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertDoesNotThrow(() -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Double.NaN));
	}
	
	@Test
	void decodeNotNaNConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NaN)));
	}
	
	@Test
	void encodeKeyNotNaNConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(Double.NaN));
	}
	
	@Test
	void decodeKeyNotNaNConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.notNaN();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("NaN"));
	}
	
	@Test
	void encodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeIntegralConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyIntegralConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyIntegralConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeIntegralConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyIntegralConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyIntegralConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.integral();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 0.5));
	}
	
	@Test
	void decodeNormalizedConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0.5)));
	}
	
	@Test
	void encodeKeyNormalizedConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertDoesNotThrow(() -> codec.encodeKey(0.5));
	}
	
	@Test
	void decodeKeyNormalizedConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertDoesNotThrow(() -> codec.decodeKey("0.5"));
	}
	
	@Test
	void encodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void decodeNormalizedConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14)));
	}
	
	@Test
	void encodeKeyNormalizedConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.14));
	}
	
	@Test
	void decodeKeyNormalizedConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.normalized();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.14"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42.0));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertDoesNotThrow(() -> codec.encodeKey(42.0));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertDoesNotThrow(() -> codec.decodeKey("42.0"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), 3.0));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.0)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(3.0));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Double> codec = Codecs.DOUBLE.custom(
			value -> {
				if (value % 2.0 != 0.0) throw new ConstraintViolateException("Value must be even");
			}
		);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3.0"));
	}
	
}
