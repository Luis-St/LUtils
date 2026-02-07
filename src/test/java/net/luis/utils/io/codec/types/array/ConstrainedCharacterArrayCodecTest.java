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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for constrained {@link CharacterArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedCharacterArrayCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(1));
		char[] validArray = { 'a', 'b', 'c' };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), validArray);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive('a'));
		expected.add(new JsonPrimitive('b'));
		expected.add(new JsonPrimitive('c'));
		assertEquals(expected, result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		char[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new char[] { 'a', 'b' }, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.equalTo(new char[] { 'a', 'b', 'c' });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.equalTo(new char[] { 'a', 'b', 'c' });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'd', 'e' }));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.equalTo(new char[] { 'a', 'b', 'c' });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.equalTo(new char[] { 'a', 'b', 'c' });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('d'));
		array.add(new JsonPrimitive('e'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notEqualTo(new char[] { 'a', 'b', 'c' });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'd', 'e', 'f' }));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notEqualTo(new char[] { 'a', 'b', 'c' });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notEqualTo(new char[] { 'a', 'b', 'c' });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('d'));
		array.add(new JsonPrimitive('e'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notEqualTo(new char[] { 'a', 'b', 'c' });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.in(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' }));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.in(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'e', 'f' }));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.in(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('c'));
		array.add(new JsonPrimitive('d'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.in(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('g'));
		array.add(new JsonPrimitive('h'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'e', 'f' }));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' }));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('g'));
		array.add(new JsonPrimitive('h'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.notIn(List.of(new char[] { 'a', 'b' }, new char[] { 'c', 'd' }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('c'));
		array.add(new JsonPrimitive('d'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' }));
	}
	
	@Test
	void decodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMaxLength(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMaxLength(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void decodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMaxLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		array.add(new JsonPrimitive('c'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withExactLength(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withExactLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' }));
	}
	
	@Test
	void decodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withExactLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withExactLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withLengthBetween(2, 4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withLengthBetween(3, 5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b' }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withLengthBetween(1, 2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void decodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withLengthBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeLengthBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withLengthBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.custom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					throw new ConstraintViolateException("All characters must be lowercase letters");
				}
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.custom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					throw new ConstraintViolateException("All characters must be lowercase letters");
				}
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'B', 'c' }));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.custom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					throw new ConstraintViolateException("All characters must be lowercase letters");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.custom(arr -> {
			for (char c : arr) {
				if (c < 'a' || c > 'z') {
					throw new ConstraintViolateException("All characters must be lowercase letters");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('B'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(2).withMaxLength(4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a', 'b', 'c' }));
	}
	
	@Test
	void encodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(2).withMaxLength(4));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new char[] { 'a' }));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(1).withMaxLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = Codecs.CHARACTER_ARRAY.apply(config -> config.withMinLength(3).withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive('a'));
		array.add(new JsonPrimitive('b'));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
}
