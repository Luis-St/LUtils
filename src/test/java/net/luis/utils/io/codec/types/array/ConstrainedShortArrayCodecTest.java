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
 * Test class for {@link ShortArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedShortArrayCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(5);
		short[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(5);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(3);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(5);
		short[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.lengthBetween(2, 5);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<short[]> codec = SHORT_ARRAY.lengthBetween(5, 10);
		short[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<short[]> codec = SHORT_ARRAY.lengthBetween(1, 2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.empty();
		short[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.empty();
		short[] array = { 1 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.notEmpty();
		short[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.notEmpty();
		short[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.empty();
		JsonArray array = new JsonArray();
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<short[]> codec = SHORT_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(2).maxLength(5);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(5).maxLength(10);
		short[] array = { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(1).maxLength(2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(10).minLength(2);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(2).maxLength(5);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(5).exactLength(3);
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<short[]> codec = SHORT_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<short[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<short[]> codec = SHORT_ARRAY;
		String str = codec.toString();
		assertEquals("ShortArrayCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<short[]> codec = SHORT_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedShortArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
