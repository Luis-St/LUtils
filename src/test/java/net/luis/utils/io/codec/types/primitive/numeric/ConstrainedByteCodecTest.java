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
 * Test class for {@link ByteCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedByteCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (byte) 42);
		assertEquals(new JsonPrimitive((byte) 42), result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Byte result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42));
		assertEquals((byte) 42, result);
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() throws EncoderException {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		String result = codec.encodeKey((byte) 42);
		assertEquals("42", result);
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() throws DecoderException {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		Byte result = codec.decodeKey("42");
		assertEquals((byte) 42, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Byte> codec = Codecs.BYTE;
		assertFalse(codec.toString().contains("Constrained"));
		assertEquals("ByteCodec", codec.toString());
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 20));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20)));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 20));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.equalTo((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 20));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 20));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notEqualTo((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 20));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20)));
	}
	
	@Test
	void encodeKeyInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 20));
	}
	
	@Test
	void decodeKeyInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20, (byte) 30));
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 30));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30)));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 30));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.in(Set.of((byte) 10, (byte) 20));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("30"));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 30));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30)));
	}
	
	@Test
	void encodeKeyNotInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 30));
	}
	
	@Test
	void decodeKeyNotInConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertDoesNotThrow(() -> codec.decodeKey("30"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.notIn(Set.of((byte) 10, (byte) 20));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 20));
	}
	
	@Test
	void decodeGreaterThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 20)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 20));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertDoesNotThrow(() -> codec.decodeKey("20"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThan((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 5));
	}
	
	@Test
	void decodeGreaterThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5)));
	}
	
	@Test
	void encodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 5));
	}
	
	@Test
	void decodeKeyGreaterThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.greaterThanOrEqual((byte) 10);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 30));
	}
	
	@Test
	void decodeLessThanConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30)));
	}
	
	@Test
	void encodeKeyLessThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 30));
	}
	
	@Test
	void decodeKeyLessThanConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("30"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 50));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyLessThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 50));
	}
	
	@Test
	void decodeKeyLessThanConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThan((byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 50));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 50));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 60));
	}
	
	@Test
	void decodeLessThanOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 60)));
	}
	
	@Test
	void encodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 60));
	}
	
	@Test
	void decodeKeyLessThanOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.lessThanOrEqual((byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("60"));
	}
	
	@Test
	void encodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 30));
	}
	
	@Test
	void decodeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 30)));
	}
	
	@Test
	void encodeKeyBetweenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 30));
	}
	
	@Test
	void decodeKeyBetweenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("30"));
	}
	
	@Test
	void encodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.between((byte) 10, (byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 30));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertDoesNotThrow(() -> codec.decodeKey("30"));
	}
	
	@Test
	void encodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 5));
	}
	
	@Test
	void decodeBetweenOrEqualConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 60)));
	}
	
	@Test
	void encodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 5));
	}
	
	@Test
	void decodeKeyBetweenOrEqualConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.betweenOrEqual((byte) 10, (byte) 50);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("60"));
	}
	
	@Test
	void encodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 42));
	}
	
	@Test
	void decodePositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42)));
	}
	
	@Test
	void encodeKeyPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 42));
	}
	
	@Test
	void decodeKeyPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("42"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5)));
	}
	
	@Test
	void encodeKeyPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.positive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-5"));
	}
	
	@Test
	void encodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodeNonPositiveConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertDoesNotThrow(() -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 5));
	}
	
	@Test
	void decodeNonPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5)));
	}
	
	@Test
	void encodeKeyNonPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 5));
	}
	
	@Test
	void decodeKeyNonPositiveConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonPositive();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) -10));
	}
	
	@Test
	void decodeNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10)));
	}
	
	@Test
	void encodeKeyNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) -10));
	}
	
	@Test
	void decodeKeyNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 5)));
	}
	
	@Test
	void encodeKeyNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.negative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("5"));
	}
	
	@Test
	void encodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodeNonNegativeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 10)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertDoesNotThrow(() -> codec.decodeKey("10"));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) -5));
	}
	
	@Test
	void decodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5)));
	}
	
	@Test
	void encodeKeyNonNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) -5));
	}
	
	@Test
	void decodeKeyNonNegativeConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonNegative();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-5"));
	}
	
	@Test
	void encodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodeZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0)));
	}
	
	@Test
	void encodeKeyZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 5));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -5)));
	}
	
	@Test
	void encodeKeyZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 5));
	}
	
	@Test
	void decodeKeyZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.zero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-5"));
	}
	
	@Test
	void encodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 42));
	}
	
	@Test
	void decodeNonZeroConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -10)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 42));
	}
	
	@Test
	void decodeKeyNonZeroConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("-10"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 0));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0)));
	}
	
	@Test
	void encodeKeyNonZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyNonZeroConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 50));
	}
	
	@Test
	void decodePercentageConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 100)));
	}
	
	@Test
	void encodeKeyPercentageConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyPercentageConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertDoesNotThrow(() -> codec.decodeKey("75"));
	}
	
	@Test
	void encodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 101));
	}
	
	@Test
	void decodePercentageConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -1)));
	}
	
	@Test
	void encodeKeyPercentageConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 101));
	}
	
	@Test
	void decodeKeyPercentageConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.percentage();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("-1"));
	}
	
	@Test
	void encodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 42));
	}
	
	@Test
	void decodeEvenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0)));
	}
	
	@Test
	void encodeKeyEvenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) -10));
	}
	
	@Test
	void decodeKeyEvenConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertDoesNotThrow(() -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 41));
	}
	
	@Test
	void decodeEvenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 3)));
	}
	
	@Test
	void encodeKeyEvenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 41));
	}
	
	@Test
	void decodeKeyEvenConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.even();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("3"));
	}
	
	@Test
	void encodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 41));
	}
	
	@Test
	void decodeOddConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 3)));
	}
	
	@Test
	void encodeKeyOddConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) -11));
	}
	
	@Test
	void decodeKeyOddConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertDoesNotThrow(() -> codec.decodeKey("99"));
	}
	
	@Test
	void encodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 42));
	}
	
	@Test
	void decodeOddConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 0)));
	}
	
	@Test
	void encodeKeyOddConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 42));
	}
	
	@Test
	void decodeKeyOddConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.odd();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 25));
	}
	
	@Test
	void decodeDivisibleByConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 0));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertDoesNotThrow(() -> codec.decodeKey("-15"));
	}
	
	@Test
	void encodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 23));
	}
	
	@Test
	void decodeDivisibleByConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 17)));
	}
	
	@Test
	void encodeKeyDivisibleByConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 23));
	}
	
	@Test
	void decodeKeyDivisibleByConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.divisibleBy(5);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("17"));
	}
	
	@Test
	void encodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 27));
	}
	
	@Test
	void decodePowerOfConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 9)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 81));
	}
	
	@Test
	void decodeKeyPowerOfConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 10));
	}
	
	@Test
	void decodePowerOfConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 15)));
	}
	
	@Test
	void encodeKeyPowerOfConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 10));
	}
	
	@Test
	void decodeKeyPowerOfConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOf(3);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("15"));
	}
	
	@Test
	void encodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 64));
	}
	
	@Test
	void decodePowerOfTwoConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 32)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 16));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertDoesNotThrow(() -> codec.decodeKey("1"));
	}
	
	@Test
	void encodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 50));
	}
	
	@Test
	void decodePowerOfTwoConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 100)));
	}
	
	@Test
	void encodeKeyPowerOfTwoConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 50));
	}
	
	@Test
	void decodeKeyPowerOfTwoConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.powerOfTwo();
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("100"));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), (byte) 21));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 49)));
	}
	
	@Test
	void encodeKeyCustomConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.encodeKey((byte) 14));
	}
	
	@Test
	void decodeKeyCustomConstraintSuccess() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("0"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), (byte) 22));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 50)));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey((byte) 22));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<Byte> codec = Codecs.BYTE.custom(value -> {
			if (value % 7 != 0) {
				throw new ConstraintViolateException("Value must be divisible by 7");
			}
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("50"));
	}
	
}
