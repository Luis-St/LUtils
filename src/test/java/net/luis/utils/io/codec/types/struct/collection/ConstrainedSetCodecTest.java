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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		Set<Integer> validSet = Set.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validSet);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(Set.of(1, 2), result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(4, 5));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(4, 5, 6));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotEqualTo(Set.of(1, 2, 3)));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(5, 6));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(5, 6));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withNotIn(List.of(Set.of(1, 2), Set.of(3, 4))));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMaxSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExactSizeConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartExactSizeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withExactSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(2, 4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(3, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSizeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(1, 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartSizeBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartSizeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withSizeBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (set.stream().allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (set.stream().allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, -2, 3));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (set.stream().allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withCustom(set -> {
			if (set.stream().allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		}));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(-2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2).withMaxSize(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1, 2, 3));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(2).withMaxSize(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Set.of(1));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(1).withMaxSize(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Set<Integer>> codec = new SetCodec<>(INTEGER).apply(config -> config.withMinSize(3).withMaxSize(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Set<Integer>> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
}
