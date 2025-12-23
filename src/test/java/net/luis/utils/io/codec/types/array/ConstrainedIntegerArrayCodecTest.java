/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedIntegerArrayCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.minLength(2);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.minLength(5);
		int[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.maxLength(5);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.maxLength(2);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.exactLength(3);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<int[]> codec = INTEGER_ARRAY.exactLength(5);
		int[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<int[]> codec = INTEGER_ARRAY.exactLength(2);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.lengthBetween(2, 5);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<int[]> codec = INTEGER_ARRAY.lengthBetween(5, 10);
		int[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<int[]> codec = INTEGER_ARRAY.lengthBetween(1, 2);
		int[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.empty();
		int[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.empty();
		int[] array = {1};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.notEmpty();
		int[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.notEmpty();
		int[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.empty();
		JsonArray array = new JsonArray();

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<int[]> codec = INTEGER_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<int[]> codec = INTEGER_ARRAY.notEmpty();
		JsonArray array = new JsonArray();

		Result<int[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<int[]> codec = INTEGER_ARRAY;
		String str = codec.toString();
		assertEquals("IntegerArrayCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<int[]> codec = INTEGER_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedIntegerArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
