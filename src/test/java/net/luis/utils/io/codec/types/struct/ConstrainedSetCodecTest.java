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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SetCodec} focusing on size constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedSetCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinSizeConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithMaxSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxSizeConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithExactSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(3);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactSizeConstraintFailureTooSmall() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithExactSizeConstraintFailureTooLarge() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithSizeBetweenConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().sizeBetween(2, 5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSizeBetweenConstraintFailureBelow() {
		Codec<Set<Integer>> codec = INTEGER.set().sizeBetween(5, 10);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithSizeBetweenConstraintFailureAbove() {
		Codec<Set<Integer>> codec = INTEGER.set().sizeBetween(1, 2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().empty();
		Set<Integer> set = Set.of();

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().empty();
		Set<Integer> set = Set.of(1);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().notEmpty();
		Set<Integer> set = Set.of(1, 2);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().notEmpty();
		Set<Integer> set = Set.of();

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithSingletonConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().singleton();
		Set<Integer> set = Set.of(42);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSingletonConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().singleton();
		Set<Integer> set = Set.of(1, 2);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
	}

	@Test
	void encodeWithPairConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().pair();
		Set<Integer> set = Set.of(1, 2);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPairConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().pair();
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithMinSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithMinSizeConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithMaxSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}

	@Test
	void decodeWithMaxSizeConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void decodeWithExactSizeConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithExactSizeConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithSizeBetweenConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().sizeBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithSizeBetweenConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().sizeBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().empty();
		JsonArray array = new JsonArray();

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().size());
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().notEmpty();
		JsonArray array = new JsonArray();

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithSingletonConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().singleton();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
		assertTrue(result.resultOrThrow().contains(42));
	}

	@Test
	void decodeWithSingletonConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().singleton();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithPairConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().pair();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}

	@Test
	void decodeWithPairConstraintFailure() {
		Codec<Set<Integer>> codec = INTEGER.set().pair();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}


	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(2).maxSize(5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(5).maxSize(10);
		Set<Integer> set = Set.of(1, 2);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(1).maxSize(2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(10).minSize(2);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(2).maxSize(5);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(5).exactSize(3);
		Set<Integer> set = Set.of(1, 2, 3);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), set);
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(2).maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(5).maxSize(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(1).maxSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(10).minSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().maxSize(2).maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<Set<Integer>> codec = INTEGER.set().exactSize(5).exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));

		Result<Set<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Set<Integer>> codec = INTEGER.set();
		String str = codec.toString();
		assertEquals("SetCodec[IntegerCodec]", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<Set<Integer>> codec = INTEGER.set().minSize(1).maxSize(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedSetCodec["));
		assertTrue(str.contains("constraints="));
	}
}
