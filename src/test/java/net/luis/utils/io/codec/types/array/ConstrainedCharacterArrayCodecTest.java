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
 * Test class for {@link CharacterArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedCharacterArrayCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(5);
		char[] array = {'a', 'b'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(5);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(3);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(5);
		char[] array = {'a', 'b'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.lengthBetween(2, 5);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<char[]> codec = CHARACTER_ARRAY.lengthBetween(5, 10);
		char[] array = {'a', 'b'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<char[]> codec = CHARACTER_ARRAY.lengthBetween(1, 2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.empty();
		char[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.empty();
		char[] array = {'a'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.notEmpty();
		char[] array = {'a', 'b'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.notEmpty();
		char[] array = {};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}

	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.empty();
		JsonArray array = new JsonArray();

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<char[]> codec = CHARACTER_ARRAY.notEmpty();
		JsonArray array = new JsonArray();

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}


	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(2).maxLength(5);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(5).maxLength(10);
		char[] array = {'a', 'b'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(1).maxLength(2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(10).minLength(2);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(2).maxLength(5);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(5).exactLength(3);
		char[] array = {'a', 'b', 'c'};

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}

	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}

	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<char[]> codec = CHARACTER_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("a"));
		array.add(new JsonPrimitive("b"));
		array.add(new JsonPrimitive("c"));

		Result<char[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<char[]> codec = CHARACTER_ARRAY;
		String str = codec.toString();
		assertEquals("CharacterArrayCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<char[]> codec = CHARACTER_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedCharacterArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
