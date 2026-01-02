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

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ListCodec} focusing on size constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedListCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinSizeConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void encodeWithMaxSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxSizeConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void encodeWithExactSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(3);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactSizeConstraintFailureTooSmall() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void encodeWithExactSizeConstraintFailureTooLarge() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void encodeWithSizeBetweenConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().sizeBetween(2, 5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithSizeBetweenConstraintFailureBelow() {
		Codec<List<Integer>> codec = INTEGER.list().sizeBetween(5, 10);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void encodeWithSizeBetweenConstraintFailureAbove() {
		Codec<List<Integer>> codec = INTEGER.list().sizeBetween(1, 2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().empty();
		List<Integer> list = List.of();
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().empty();
		List<Integer> list = List.of(1);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().notEmpty();
		List<Integer> list = List.of(1, 2);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().notEmpty();
		List<Integer> list = List.of();
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void encodeWithSingletonConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().singleton();
		List<Integer> list = List.of(42);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithSingletonConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().singleton();
		List<Integer> list = List.of(1, 2);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithPairConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().pair();
		List<Integer> list = List.of(1, 2);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPairConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().pair();
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithMinSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithMinSizeConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void decodeWithMaxSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithMaxSizeConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void decodeWithExactSizeConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithExactSizeConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void decodeWithSizeBetweenConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().sizeBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithSizeBetweenConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().sizeBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().empty();
		JsonArray array = new JsonArray();
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().notEmpty();
		JsonArray array = new JsonArray();
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void decodeWithSingletonConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().singleton();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().size());
		assertEquals(42, result.resultOrThrow().get(0));
	}
	
	@Test
	void decodeWithSingletonConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().singleton();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithPairConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().pair();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithPairConstraintFailure() {
		Codec<List<Integer>> codec = INTEGER.list().pair();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(2).maxSize(5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(5).maxSize(10);
		List<Integer> list = List.of(1, 2);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(1).maxSize(2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(10).minSize(2);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(2).maxSize(5);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(5).exactSize(3);
		List<Integer> list = List.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), list);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(2).maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(5).maxSize(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum size constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(1).maxSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum size constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(10).minSize(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().maxSize(2).maxSize(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<List<Integer>> codec = INTEGER.list().exactSize(5).exactSize(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<List<Integer>> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().size());
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<List<Integer>> codec = INTEGER.list();
		String str = codec.toString();
		assertEquals("ListCodec[IntegerCodec]", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<List<Integer>> codec = INTEGER.list().minSize(1).maxSize(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedListCodec["));
		assertTrue(str.contains("constraints="));
	}
}
