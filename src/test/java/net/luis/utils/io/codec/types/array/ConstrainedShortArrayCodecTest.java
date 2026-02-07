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
 * Test class for constrained {@link ShortArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedShortArrayCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(1));
		short[] validArray = { (short) 1, (short) 2, (short) 3 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), validArray);
		assertInstanceOf(JsonArray.class, result);
		assertEquals(3, ((JsonArray) result).size());
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		short[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new short[] { (short) 1, (short) 2 }, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.equalTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.equalTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 4, (short) 5 }));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.equalTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.equalTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notEqualTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 4, (short) 5, (short) 6 }));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notEqualTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notEqualTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notEqualTo(new short[] { (short) 1, (short) 2, (short) 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.in(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2 }));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.in(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 5, (short) 6 }));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.in(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.in(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notIn(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 5, (short) 6 }));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notIn(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2 }));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notIn(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.notIn(List.of(new short[] { (short) 1, (short) 2 }, new short[] { (short) 3, (short) 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2 }));
	}
	
	@Test
	void decodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.maxLength(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.maxLength(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void decodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.maxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.maxLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.exactLength(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.exactLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2 }));
	}
	
	@Test
	void decodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.exactLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.exactLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.lengthBetween(2, 4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.lengthBetween(3, 5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2 }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.lengthBetween(1, 2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void decodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.lengthBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeLengthBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.lengthBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.custom(arr -> {
			for (short s : arr) {
				if (s <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.custom(arr -> {
			for (short s : arr) {
				if (s <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) -2, (short) 3 }));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.custom(arr -> {
			for (short s : arr) {
				if (s <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.custom(arr -> {
			for (short s : arr) {
				if (s <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(-2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(2).maxLength(4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1, (short) 2, (short) 3 }));
	}
	
	@Test
	void encodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(2).maxLength(4));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new short[] { (short) 1 }));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(1).maxLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = Codecs.SHORT_ARRAY.length(builder -> builder.minLength(3).maxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
}
