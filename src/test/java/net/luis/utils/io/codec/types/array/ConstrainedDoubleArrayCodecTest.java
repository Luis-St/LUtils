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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedDoubleArrayCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(5);
		double[] array = { 1.0, 2.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(5);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(3);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(5);
		double[] array = { 1.0, 2.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.lengthBetween(2, 5);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<double[]> codec = DOUBLE_ARRAY.lengthBetween(5, 10);
		double[] array = { 1.0, 2.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<double[]> codec = DOUBLE_ARRAY.lengthBetween(1, 2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.empty();
		double[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.empty();
		double[] array = { 1.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.notEmpty();
		double[] array = { 1.0, 2.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.notEmpty();
		double[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.empty();
		JsonArray array = new JsonArray();
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<double[]> codec = DOUBLE_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(2).maxLength(5);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(5).maxLength(10);
		double[] array = { 1.0, 2.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(1).maxLength(2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(10).minLength(2);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(2).maxLength(5);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(5).exactLength(3);
		double[] array = { 1.0, 2.0, 3.0 };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<double[]> codec = DOUBLE_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<double[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<double[]> codec = DOUBLE_ARRAY;
		String str = codec.toString();
		assertEquals("DoubleArrayCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<double[]> codec = DOUBLE_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedDoubleArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
