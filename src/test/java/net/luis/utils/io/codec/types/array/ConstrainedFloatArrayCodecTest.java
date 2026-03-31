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
 * Test class for constrained {@link FloatArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedFloatArrayCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(1));
		float[] validArray = { 1.0f, 2.0f, 3.0f };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), validArray);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1.0f));
		expected.add(new JsonPrimitive(2.0f));
		expected.add(new JsonPrimitive(3.0f));
		assertEquals(expected, result);
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		float[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new float[] { 1.0f, 2.0f }, result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.equalTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.equalTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 4.0f, 5.0f }));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.equalTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.equalTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4.0f));
		array.add(new JsonPrimitive(5.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notEqualTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 4.0f, 5.0f, 6.0f }));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notEqualTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notEqualTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4.0f));
		array.add(new JsonPrimitive(5.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notEqualTo(new float[] { 1.0f, 2.0f, 3.0f });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.in(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f }));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.in(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 5.0f, 6.0f }));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.in(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3.0f));
		array.add(new JsonPrimitive(4.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.in(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7.0f));
		array.add(new JsonPrimitive(8.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notIn(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 5.0f, 6.0f }));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notIn(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f }));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notIn(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7.0f));
		array.add(new JsonPrimitive(8.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.notIn(List.of(new float[] { 1.0f, 2.0f }, new float[] { 3.0f, 4.0f }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3.0f));
		array.add(new JsonPrimitive(4.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f }));
	}
	
	@Test
	void decodeMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMaxLength(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMaxLength(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void decodeMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMaxLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withExactLength(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withExactLength(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f }));
	}
	
	@Test
	void decodeExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withExactLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withExactLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withLengthBetween(2, 4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withLengthBetween(3, 5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f }));
	}
	
	@Test
	void encodeLengthBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withLengthBetween(1, 2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void decodeLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withLengthBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeLengthBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withLengthBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.custom(arr -> {
			for (float f : arr) {
				if (f <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.custom(arr -> {
			for (float f : arr) {
				if (f <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, -2.0f, 3.0f }));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.custom(arr -> {
			for (float f : arr) {
				if (f <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.custom(arr -> {
			for (float f : arr) {
				if (f <= 0) {
					throw new ConstraintViolateException("All elements must be positive");
				}
			}
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(-2.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(2).withMaxLength(4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f, 2.0f, 3.0f }));
	}
	
	@Test
	void encodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(2).withMaxLength(4));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), new float[] { 1.0f }));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(1).withMaxLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<float[]> codec = Codecs.FLOAT_ARRAY.apply(config -> config.withMinLength(3).withMaxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
}
