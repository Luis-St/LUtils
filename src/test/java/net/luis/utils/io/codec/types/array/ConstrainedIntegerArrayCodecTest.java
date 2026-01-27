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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for constrained {@link IntegerArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedIntegerArrayCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(1));
		int[] validArray = new int[] { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validArray);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(1));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new int[] { 1, 2 }, result.resultOrThrow());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(1));
		String str = codec.toString();
		assertTrue(str.contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.equalTo(new int[] { 1, 2, 3 });
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.equalTo(new int[] { 1, 2, 3 });
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 4, 5 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.equalTo(new int[] { 1, 2, 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.equalTo(new int[] { 1, 2, 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notEqualTo(new int[] { 1, 2, 3 });
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 4, 5, 6 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notEqualTo(new int[] { 1, 2, 3 });
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notEqualTo(new int[] { 1, 2, 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(4));
		array.add(new JsonPrimitive(5));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notEqualTo(new int[] { 1, 2, 3 });
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.in(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.in(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 5, 6 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.in(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.in(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notIn(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 5, 6 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notIn(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notIn(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(7));
		array.add(new JsonPrimitive(8));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.notIn(List.of(new int[] { 1, 2 }, new int[] { 3, 4 }));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(3));
		array.add(new JsonPrimitive(4));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMinLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.maxLength(5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.maxLength(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMaxLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.maxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartMaxLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.maxLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.exactLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.exactLength(3));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartExactLengthConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.exactLength(2));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartExactLengthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.exactLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.lengthBetween(2, 4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.lengthBetween(3, 5));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2 });
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLengthBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.lengthBetween(1, 2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLengthBetweenConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.lengthBetween(1, 3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartLengthBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.lengthBetween(3, 5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.custom(arr -> {
			if (Arrays.stream(arr).allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.custom(arr -> {
			if (Arrays.stream(arr).allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, -2, 3 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.custom(arr -> {
			if (Arrays.stream(arr).allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.custom(arr -> {
			if (Arrays.stream(arr).allMatch(i -> i > 0)) {
				return Result.success(null);
			}
			return Result.error("All elements must be positive");
		});
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(-2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(2).maxLength(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1, 2, 3 });
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(2).maxLength(4));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), new int[] { 1 });
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCombinedConstraintsSuccess() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(1).maxLength(3));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartCombinedConstraintsViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<int[]> codec = Codecs.INTEGER_ARRAY.length(builder -> builder.minLength(3).maxLength(5));
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<int[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
	}
}
