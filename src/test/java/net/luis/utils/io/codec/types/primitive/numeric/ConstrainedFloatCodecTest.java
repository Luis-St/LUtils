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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FloatCodec} focusing on decimal and numeric constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedFloatCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	// Decimal-specific constraints
	
	@Test
	void encodeWithFiniteConstraintSuccess() {
		Codec<Float> codec = FLOAT.finite();
		float value = 1.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithFiniteConstraintFailure() {
		Codec<Float> codec = FLOAT.finite();
		float value = Float.POSITIVE_INFINITY;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated finite constraint"));
	}
	
	@Test
	void encodeWithNotNaNConstraintSuccess() {
		Codec<Float> codec = FLOAT.notNaN();
		float value = 1.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNotNaNConstraintFailure() {
		Codec<Float> codec = FLOAT.notNaN();
		float value = Float.NaN;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated not-NaN constraint"));
	}
	
	@Test
	void encodeWithIntegralConstraintSuccess() {
		Codec<Float> codec = FLOAT.integral();
		float value = 5.0f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithIntegralConstraintFailure() {
		Codec<Float> codec = FLOAT.integral();
		float value = 5.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void encodeWithNormalizedConstraintSuccess() {
		Codec<Float> codec = FLOAT.normalized();
		float value = 0.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNormalizedConstraintFailure() {
		Codec<Float> codec = FLOAT.normalized();
		float value = 1.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated normalized constraint"));
	}
	
	// Numeric constraints
	
	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<Float> codec = FLOAT.positive();
		float value = 10.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<Float> codec = FLOAT.positive();
		float value = -5.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<Float> codec = FLOAT.negative();
		float value = -10.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<Float> codec = FLOAT.negative();
		float value = 5.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<Float> codec = FLOAT.nonNegative();
		float value = 0.0f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<Float> codec = FLOAT.nonNegative();
		float value = -1.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Codec<Float> codec = FLOAT.betweenOrEqual(0.0f, 100.0f);
		float value = 50.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Codec<Float> codec = FLOAT.betweenOrEqual(0.0f, 100.0f);
		float value = 127.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	// Decode tests
	
	@Test
	void decodeWithFiniteConstraintSuccess() {
		Codec<Float> codec = FLOAT.finite();
		JsonPrimitive json = new JsonPrimitive(1.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(1.5f, result.resultOrThrow());
	}
	
	@Test
	void decodeWithFiniteConstraintFailure() {
		Codec<Float> codec = FLOAT.finite();
		JsonPrimitive json = new JsonPrimitive(Float.POSITIVE_INFINITY);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated finite constraint"));
	}
	
	@Test
	void decodeWithNotNaNConstraintSuccess() {
		Codec<Float> codec = FLOAT.notNaN();
		JsonPrimitive json = new JsonPrimitive(1.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(1.5f, result.resultOrThrow());
	}
	
	@Test
	void decodeWithNotNaNConstraintFailure() {
		Codec<Float> codec = FLOAT.notNaN();
		JsonPrimitive json = new JsonPrimitive(Float.NaN);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated not-NaN constraint"));
	}
	
	@Test
	void decodeWithIntegralConstraintSuccess() {
		Codec<Float> codec = FLOAT.integral();
		JsonPrimitive json = new JsonPrimitive(5.0f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(5.0f, result.resultOrThrow());
	}
	
	@Test
	void decodeWithIntegralConstraintFailure() {
		Codec<Float> codec = FLOAT.integral();
		JsonPrimitive json = new JsonPrimitive(5.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<Float> codec = FLOAT.positive();
		JsonPrimitive json = new JsonPrimitive(10.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10.5f, result.resultOrThrow());
	}
	
	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<Float> codec = FLOAT.positive();
		JsonPrimitive json = new JsonPrimitive(-5.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	// Chained constraints
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Float> codec = FLOAT.finite().positive().betweenOrEqual(0.0f, 100.0f);
		float value = 50.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<Float> codec = FLOAT.finite().positive().integral();
		float value = 12.5f;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Float> codec = FLOAT.notNaN().nonNegative();
		JsonPrimitive json = new JsonPrimitive(10.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10.5f, result.resultOrThrow());
	}
	
	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<Float> codec = FLOAT.finite().positive();
		JsonPrimitive json = new JsonPrimitive(-7.5f);
		
		Result<Float> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Float> codec = FLOAT;
		String str = codec.toString();
		assertEquals("FloatCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Float> codec = FLOAT.finite().positive();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedFloatCodec["));
		assertTrue(str.contains("constraints="));
	}
}
