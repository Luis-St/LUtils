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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonArray;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SetCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedSetCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		Set<Integer> validSet = Set.of(1, 2, 3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validSet));
	}
	
	@Test
	void decodeWithValidConstrainedValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Set<Integer> result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertEquals(Set.of(1, 2), result);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(4, 5)));
	}
	
	@Test
	void decodeEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(4, 5, 6)));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void decodeNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2)));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(5, 6)));
	}
	
	@Test
	void decodeInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(5, 6)));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2)));
	}
	
	@Test
	void decodeNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2)));
	}
	
	@Test
	void decodeMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(5));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void decodeMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2)));
	}
	
	@Test
	void decodeExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(2, 4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeSizeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(3, 5));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2)));
	}
	
	@Test
	void encodeSizeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(1, 2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void decodeSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeSizeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (!set.stream().allMatch(i -> i > 0)) {
				throw new ConstraintViolateException("All elements must be positive");
			}
		}));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (!set.stream().allMatch(i -> i > 0)) {
				throw new ConstraintViolateException("All elements must be positive");
			}
		}));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, -2, 3)));
	}
	
	@Test
	void decodeCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (!set.stream().allMatch(i -> i > 0)) {
				throw new ConstraintViolateException("All elements must be positive");
			}
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (!set.stream().allMatch(i -> i > 0)) {
				throw new ConstraintViolateException("All elements must be positive");
			}
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(-2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void encodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2).withMaxSize(4));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1, 2, 3)));
	}
	
	@Test
	void encodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2).withMaxSize(4));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Set.of(1)));
	}
	
	@Test
	void decodeCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1).withMaxSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3).withMaxSize(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
}
