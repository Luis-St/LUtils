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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedArrayCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().minLength(5);
		Integer[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(5);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(3);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(5);
		Integer[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().lengthBetween(2, 5);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<Integer[]> codec = INTEGER.array().lengthBetween(5, 10);
		Integer[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<Integer[]> codec = INTEGER.array().lengthBetween(1, 2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().empty();
		Integer[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().empty();
		Integer[] array = {1};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().notEmpty();
		Integer[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().notEmpty();
		Integer[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().empty();
		JsonArray array = new JsonArray();

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<Integer[]> codec = INTEGER.array().notEmpty();
		JsonArray array = new JsonArray();

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}


	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(2).maxLength(5);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<Integer[]> codec = INTEGER.array().minLength(5).maxLength(10);
		Integer[] array = {1, 2};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<Integer[]> codec = INTEGER.array().minLength(1).maxLength(2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(10).minLength(2);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(2).maxLength(5);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(5).exactLength(3);
		Integer[] array = {1, 2, 3};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<Integer[]> codec = INTEGER.array().minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<Integer[]> codec = INTEGER.array().minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<Integer[]> codec = INTEGER.array().exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Integer[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Integer[]> codec = INTEGER.array();
		String str = codec.toString();
		assertEquals("ArrayCodec[IntegerCodec]", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<Integer[]> codec = INTEGER.array().minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
