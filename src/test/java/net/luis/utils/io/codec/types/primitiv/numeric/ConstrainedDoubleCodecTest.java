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

package net.luis.utils.io.codec.types.primitiv.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.DOUBLE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleCodec} focusing on decimal and numeric constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedDoubleCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Decimal-specific constraints

	@Test
	void encodeWithFiniteConstraintSuccess() {
		Codec<Double> codec = DOUBLE.finite();
		double value = 1.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithFiniteConstraintFailure() {
		Codec<Double> codec = DOUBLE.finite();
		double value = Double.POSITIVE_INFINITY;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated finite constraint"));
	}

	@Test
	void encodeWithNotNaNConstraintSuccess() {
		Codec<Double> codec = DOUBLE.notNaN();
		double value = 1.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNotNaNConstraintFailure() {
		Codec<Double> codec = DOUBLE.notNaN();
		double value = Double.NaN;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated not-NaN constraint"));
	}

	@Test
	void encodeWithIntegralConstraintSuccess() {
		Codec<Double> codec = DOUBLE.integral();
		double value = 5.0;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithIntegralConstraintFailure() {
		Codec<Double> codec = DOUBLE.integral();
		double value = 5.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}

	@Test
	void encodeWithNormalizedConstraintSuccess() {
		Codec<Double> codec = DOUBLE.normalized();
		double value = 0.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNormalizedConstraintFailure() {
		Codec<Double> codec = DOUBLE.normalized();
		double value = 1.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated normalized constraint"));
	}

	// Numeric constraints

	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<Double> codec = DOUBLE.positive();
		double value = 10.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<Double> codec = DOUBLE.positive();
		double value = -5.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<Double> codec = DOUBLE.negative();
		double value = -10.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<Double> codec = DOUBLE.negative();
		double value = 5.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<Double> codec = DOUBLE.nonNegative();
		double value = 0.0;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<Double> codec = DOUBLE.nonNegative();
		double value = -1.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeWithBetweenConstraintSuccess() {
		Codec<Double> codec = DOUBLE.between(0.0, 100.0);
		double value = 50.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenConstraintFailure() {
		Codec<Double> codec = DOUBLE.between(0.0, 100.0);
		double value = 127.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	// Decode tests

	@Test
	void decodeWithFiniteConstraintSuccess() {
		Codec<Double> codec = DOUBLE.finite();
		JsonPrimitive json = new JsonPrimitive(1.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(1.5, result.resultOrThrow());
	}

	@Test
	void decodeWithFiniteConstraintFailure() {
		Codec<Double> codec = DOUBLE.finite();
		JsonPrimitive json = new JsonPrimitive(Double.POSITIVE_INFINITY);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated finite constraint"));
	}

	@Test
	void decodeWithNotNaNConstraintSuccess() {
		Codec<Double> codec = DOUBLE.notNaN();
		JsonPrimitive json = new JsonPrimitive(1.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(1.5, result.resultOrThrow());
	}

	@Test
	void decodeWithNotNaNConstraintFailure() {
		Codec<Double> codec = DOUBLE.notNaN();
		JsonPrimitive json = new JsonPrimitive(Double.NaN);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated not-NaN constraint"));
	}

	@Test
	void decodeWithIntegralConstraintSuccess() {
		Codec<Double> codec = DOUBLE.integral();
		JsonPrimitive json = new JsonPrimitive(5.0);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(5.0, result.resultOrThrow());
	}

	@Test
	void decodeWithIntegralConstraintFailure() {
		Codec<Double> codec = DOUBLE.integral();
		JsonPrimitive json = new JsonPrimitive(5.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}

	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<Double> codec = DOUBLE.positive();
		JsonPrimitive json = new JsonPrimitive(10.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10.5, result.resultOrThrow());
	}

	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<Double> codec = DOUBLE.positive();
		JsonPrimitive json = new JsonPrimitive(-5.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained constraints

	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Double> codec = DOUBLE.finite().positive().between(0.0, 100.0);
		double value = 50.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<Double> codec = DOUBLE.finite().positive().integral();
		double value = 12.5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Double> codec = DOUBLE.notNaN().nonNegative();
		JsonPrimitive json = new JsonPrimitive(10.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10.5, result.resultOrThrow());
	}

	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<Double> codec = DOUBLE.finite().positive();
		JsonPrimitive json = new JsonPrimitive(-7.5);

		Result<Double> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Double> codec = DOUBLE;
		String str = codec.toString();
		assertEquals("DoubleCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<Double> codec = DOUBLE.finite().positive();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedDoubleCodec["));
		assertTrue(str.contains("constraints="));
	}
}
