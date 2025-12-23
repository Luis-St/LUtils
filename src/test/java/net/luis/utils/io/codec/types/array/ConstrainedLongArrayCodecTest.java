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
 * Test class for {@link LongArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedLongArrayCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.minLength(2);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.minLength(5);
		long[] array = {1L, 2L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.maxLength(5);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.maxLength(2);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.exactLength(3);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<long[]> codec = LONG_ARRAY.exactLength(5);
		long[] array = {1L, 2L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<long[]> codec = LONG_ARRAY.exactLength(2);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.lengthBetween(2, 5);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<long[]> codec = LONG_ARRAY.lengthBetween(5, 10);
		long[] array = {1L, 2L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<long[]> codec = LONG_ARRAY.lengthBetween(1, 2);
		long[] array = {1L, 2L, 3L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.empty();
		long[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.empty();
		long[] array = {1L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.notEmpty();
		long[] array = {1L, 2L};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.notEmpty();
		long[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.empty();
		JsonArray array = new JsonArray();

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<long[]> codec = LONG_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<long[]> codec = LONG_ARRAY.notEmpty();
		JsonArray array = new JsonArray();

		Result<long[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<long[]> codec = LONG_ARRAY;
		String str = codec.toString();
		assertEquals("LongArrayCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<long[]> codec = LONG_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedLongArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
