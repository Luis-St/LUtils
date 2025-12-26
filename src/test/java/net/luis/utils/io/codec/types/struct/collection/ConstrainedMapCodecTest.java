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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MapCodec} focusing on size constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedMapCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithMinSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinSizeConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithMaxSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMaxSizeConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithExactSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(3);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithExactSizeConstraintFailureTooSmall() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithExactSizeConstraintFailureTooLarge() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithSizeBetweenConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).sizeBetween(2, 5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSizeBetweenConstraintFailureBelow() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).sizeBetween(5, 10);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithSizeBetweenConstraintFailureAbove() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).sizeBetween(1, 2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).empty();
		Map<Integer, String> map = Map.of();

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).empty();
		Map<Integer, String> map = Map.of(1, "a");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).notEmpty();
		Map<Integer, String> map = Map.of(1, "a", 2, "b");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).notEmpty();
		Map<Integer, String> map = Map.of();

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithSingletonConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).singleton();
		Map<Integer, String> map = Map.of(1, "value");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSingletonConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).singleton();
		Map<Integer, String> map = Map.of(1, "a", 2, "b");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
	}

	@Test
	void encodeWithPairConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).pair();
		Map<Integer, String> map = Map.of(1, "a", 2, "b");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPairConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).pair();
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithMinSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(2);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithMinSizeConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(5);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithMaxSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(5);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}

	@Test
	void decodeWithMaxSizeConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(2);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void decodeWithExactSizeConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(3);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithExactSizeConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(3);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithSizeBetweenConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).sizeBetween(2, 5);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithSizeBetweenConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).sizeBetween(5, 10);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).empty();
		JsonObject object = new JsonObject();

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().size());
	}

	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).empty();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).notEmpty();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
	}

	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).notEmpty();
		JsonObject object = new JsonObject();

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithSingletonConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).singleton();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("value"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
		assertEquals("value", result.resultOrThrow().get(1));
	}

	@Test
	void decodeWithSingletonConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).singleton();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithPairConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).pair();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}

	@Test
	void decodeWithPairConstraintFailure() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).pair();
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
	}


	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(2).maxSize(5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(5).maxSize(10);
		Map<Integer, String> map = Map.of(1, "a", 2, "b");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(1).maxSize(2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(10).minSize(2);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(2).maxSize(5);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(5).exactSize(3);
		Map<Integer, String> map = Map.of(1, "a", 2, "b", 3, "c");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), map);
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(2).maxSize(5);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(5).maxSize(10);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}

	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(1).maxSize(2);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}

	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(10).minSize(2);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).maxSize(2).maxSize(5);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).exactSize(5).exactSize(3);
		JsonObject object = new JsonObject();
		object.add("1", new JsonPrimitive("a"));
		object.add("2", new JsonPrimitive("b"));
		object.add("3", new JsonPrimitive("c"));

		Result<Map<Integer, String>> result = codec.decodeStart(this.provider, this.provider.empty(), object);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING);
		String str = codec.toString();
		assertTrue(str.contains("MapCodec["));
	}

	@Test
	void toStringWithConstraints() {
		Codec<Map<Integer, String>> codec = new MapCodec<>(Codecs.INTEGER, Codecs.STRING).minSize(1).maxSize(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedMapCodec["));
		assertTrue(str.contains("constraints="));
	}
}
