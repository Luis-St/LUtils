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
 * Test class for {@link BooleanArrayCodec} focusing on length constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedBooleanArrayCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithMinLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinLengthConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(5);
		boolean[] array = { true, false };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithMaxLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(5);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxLengthConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(3);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooSmall() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(5);
		boolean[] array = { true, false };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithExactLengthConstraintFailureTooLarge() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.lengthBetween(2, 5);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureBelow() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.lengthBetween(5, 10);
		boolean[] array = { true, false };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithLengthBetweenConstraintFailureAbove() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.lengthBetween(1, 2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithEmptyConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.empty();
		boolean[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEmptyConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.empty();
		boolean[] array = { true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithNotEmptyConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.notEmpty();
		boolean[] array = { true, false };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotEmptyConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.notEmpty();
		boolean[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMinLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMinLengthConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithMaxLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(2, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithMaxLengthConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithExactLengthConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithExactLengthConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithLengthBetweenConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.lengthBetween(2, 5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithLengthBetweenConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.lengthBetween(5, 10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithEmptyConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.empty();
		JsonArray array = new JsonArray();
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithEmptyConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.empty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithNotEmptyConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(1, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithNotEmptyConstraintFailure() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.notEmpty();
		JsonArray array = new JsonArray();
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(2).maxLength(5);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMin() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(5).maxLength(10);
		boolean[] array = { true, false };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsFailureMax() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(1).maxLength(2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void encodeWithOverwrittenMinConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(10).minLength(2);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenMaxConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(2).maxLength(5);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOverwrittenExactConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(5).exactLength(3);
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMin() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(5).maxLength(10);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("minimum length constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsFailureMax() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(1).maxLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("maximum length constraint"));
	}
	
	@Test
	void decodeWithOverwrittenMinConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(10).minLength(2);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenMaxConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.maxLength(2).maxLength(5);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void decodeWithOverwrittenExactConstraintSuccess() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.exactLength(5).exactLength(3);
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(false));
		array.add(new JsonPrimitive(true));
		
		Result<boolean[]> result = codec.decodeStart(this.provider, this.provider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(3, result.resultOrThrow().length);
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY;
		String str = codec.toString();
		assertEquals("BooleanArrayCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<boolean[]> codec = BOOLEAN_ARRAY.minLength(1).maxLength(10);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedBooleanArrayCodec["));
		assertTrue(str.contains("constraints="));
	}
}
