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
 * Test class for {@link FloatArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedFloatArrayCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(5);
		float[] array = { 1.0f, 2.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(5);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(3);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(5);
		float[] array = { 1.0f, 2.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.lengthBetween(2, 5);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<float[]> codec = FLOAT_ARRAY.lengthBetween(5, 10);
		float[] array = { 1.0f, 2.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<float[]> codec = FLOAT_ARRAY.lengthBetween(1, 2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.empty();
		float[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.empty();
		float[] array = { 1.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.notEmpty();
		float[] array = { 1.0f, 2.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.notEmpty();
		float[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		array.add(new JsonPrimitive(3.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		array.add(new JsonPrimitive(2.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.empty();
		JsonArray array = new JsonArray();
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<float[]> codec = FLOAT_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(2).maxLength(5);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(5).maxLength(10);
		float[] array = { 1.0f, 2.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(1).maxLength(2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(10).minLength(2);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(2).maxLength(5);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(5).exactLength(3);
		float[] array = { 1.0f, 2.0f, 3.0f };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<float[]> codec = FLOAT_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1.0f));
		array.add(new JsonPrimitive(2.0f));
		array.add(new JsonPrimitive(3.0f));
		
		Result<float[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<float[]> codec = FLOAT_ARRAY;
		String str = codec.toString();
		assertEquals("FloatArrayCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<float[]> codec = FLOAT_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedFloatArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
