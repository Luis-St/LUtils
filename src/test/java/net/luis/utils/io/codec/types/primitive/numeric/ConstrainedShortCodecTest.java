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
 * Test class for {@link ShortCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedShortCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (short) 100);
		assertEquals(new JsonPrimitive((short) 100), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		Short result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100));
		assertEquals((short) 100, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		String result = codec.encodeKey((short) 100);
		assertEquals("100", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		Short result = codec.decodeKey("100");
		assertEquals((short) 100, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Short> codec = Codecs.SHORT.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Short> codec = Codecs.SHORT;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("ShortCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 42)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 42));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 100));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.equalTo((short) 42);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 100));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 42)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 42));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.notEqualTo((short) 42);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("42"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 20));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 20)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 20));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.in(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 20));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 20)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 20));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.notIn(Set.of((short) 10, (short) 20, (short) 30));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 100));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.greaterThan((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 49));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 49)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 49));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.greaterThanOrEqual((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("49"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 25));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 25)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 25));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("25"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.lessThan((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 25)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("25"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 51));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 51)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 51));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.lessThanOrEqual((short) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("51"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 10));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 5));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.between((short) 10, (short) 100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 10));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertDoesNotThrow(() -> codec.decodeKey("75"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 9));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 101)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 5));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.betweenOrEqual((short) 10, (short) 100);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("150"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("500"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -10)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) -5));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -50)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) -100));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 1));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 50));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) -42));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) -1));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-500"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 10)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 5));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) -1));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) -50));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 0));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 1));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -1)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-50"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -100)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 0));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 0));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 50));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 0)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 100));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) -1));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 101)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 150));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 0));
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertDoesNotThrow(() -> codec.decodeKey("-50"));
	}
	
	@Test
	void encodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 41));
	}
	
	@Test
	void decodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 99)));
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.even();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-51"));
	}
	
	@Test
	void encodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 41));
	}
	
	@Test
	void decodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 99)));
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertDoesNotThrow(() -> codec.decodeKey("-51"));
	}
	
	@Test
	void encodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 0));
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.odd();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-50"));
	}
	
	@Test
	void encodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 100));
	}
	
	@Test
	void decodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 0));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decodeKey("-25"));
	}
	
	@Test
	void encodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 51)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 7));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-26"));
	}
	
	@Test
	void encodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 27));
	}
	
	@Test
	void decodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 81)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("9"));
	}
	
	@Test
	void encodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 10));
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 64));
	}
	
	@Test
	void decodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 128)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 1));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decodeKey("256"));
	}
	
	@Test
	void encodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 100)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 3));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 49));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 14)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertDoesNotThrow(() -> codec.encodeKey((short) 21));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("7"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (short) 42));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 50)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((short) 10));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Short> codec = Codecs.SHORT.custom(value -> {
			if (value % 7 != 0) throw new ConstraintViolateException("Value must be divisible by 7");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
}
