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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigDecimalCodec} focusing on BigDecimal-specific and numeric constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedBigDecimalCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	// Scale constraints
	
	@Test
	void encodeWithScaleConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scale(2);
		BigDecimal value = new BigDecimal("1.23");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithScaleConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scale(2);
		BigDecimal value = new BigDecimal("1.2");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated scale constraint"));
	}
	
	@Test
	void encodeWithMinScaleConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.minScale(2);
		BigDecimal value = new BigDecimal("1.234");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinScaleConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.minScale(2);
		BigDecimal value = new BigDecimal("1.2");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum scale constraint"));
	}
	
	@Test
	void encodeWithMaxScaleConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.maxScale(2);
		BigDecimal value = new BigDecimal("1.2");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxScaleConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.maxScale(2);
		BigDecimal value = new BigDecimal("1.234");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum scale constraint"));
	}
	
	@Test
	void encodeWithScaleBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scaleBetween(1, 3);
		BigDecimal value = new BigDecimal("1.23");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithScaleBetweenConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scaleBetween(1, 3);
		BigDecimal value = new BigDecimal("1.2345");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated scale range constraint"));
	}
	
	// Precision constraints
	
	@Test
	void encodeWithPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precision(3);
		BigDecimal value = new BigDecimal("1.23");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPrecisionConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precision(3);
		BigDecimal value = new BigDecimal("12.34");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated precision constraint"));
	}
	
	@Test
	void encodeWithMinPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.minPrecision(3);
		BigDecimal value = new BigDecimal("12.34");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMinPrecisionConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.minPrecision(3);
		BigDecimal value = new BigDecimal("1.2");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum precision constraint"));
	}
	
	@Test
	void encodeWithMaxPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.maxPrecision(4);
		BigDecimal value = new BigDecimal("12.3");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithMaxPrecisionConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.maxPrecision(4);
		BigDecimal value = new BigDecimal("12.345");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum precision constraint"));
	}
	
	@Test
	void encodeWithPrecisionBetweenConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precisionBetween(2, 4);
		BigDecimal value = new BigDecimal("1.23");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPrecisionBetweenConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precisionBetween(2, 4);
		BigDecimal value = new BigDecimal("12.345");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated precision range constraint"));
	}
	
	// Integral and normalized constraints
	
	@Test
	void encodeWithIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.integral();
		BigDecimal value = new BigDecimal("5.0");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithIntegralConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.integral();
		BigDecimal value = new BigDecimal("5.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void encodeWithNormalizedConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.normalized();
		BigDecimal value = new BigDecimal("0.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNormalizedConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.normalized();
		BigDecimal value = new BigDecimal("1.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated normalized constraint"));
	}
	
	// Numeric constraints
	
	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive();
		BigDecimal value = new BigDecimal("10.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive();
		BigDecimal value = new BigDecimal("-5.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.negative();
		BigDecimal value = new BigDecimal("-10.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.negative();
		BigDecimal value = new BigDecimal("5.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.nonNegative();
		BigDecimal value = BigDecimal.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.nonNegative();
		BigDecimal value = new BigDecimal("-1.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, new BigDecimal("100.0"));
		BigDecimal value = new BigDecimal("50.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.betweenOrEqual(BigDecimal.ZERO, new BigDecimal("100.0"));
		BigDecimal value = new BigDecimal("127.5");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	// Decode tests
	
	@Test
	void decodeWithScaleConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scale(2);
		JsonPrimitive json = new JsonPrimitive("1.23");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("1.23"), result.resultOrThrow());
	}
	
	@Test
	void decodeWithScaleConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.scale(2);
		JsonPrimitive json = new JsonPrimitive("1.2");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated scale constraint"));
	}
	
	@Test
	void decodeWithPrecisionConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precision(3);
		JsonPrimitive json = new JsonPrimitive("1.23");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("1.23"), result.resultOrThrow());
	}
	
	@Test
	void decodeWithPrecisionConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.precision(3);
		JsonPrimitive json = new JsonPrimitive("12.34");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated precision constraint"));
	}
	
	@Test
	void decodeWithIntegralConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.integral();
		JsonPrimitive json = new JsonPrimitive("5.0");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("5.0"), result.resultOrThrow());
	}
	
	@Test
	void decodeWithIntegralConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.integral();
		JsonPrimitive json = new JsonPrimitive("5.5");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive();
		JsonPrimitive json = new JsonPrimitive("10.5");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("10.5"), result.resultOrThrow());
	}
	
	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive();
		JsonPrimitive json = new JsonPrimitive("-5.5");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	// Chained constraints
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive().scale(2).precisionBetween(2, 4);
		BigDecimal value = new BigDecimal("1.23");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive().integral().scale(2);
		BigDecimal value = new BigDecimal("12.50");
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<BigDecimal> codec = BIG_DECIMAL.nonNegative().maxScale(3);
		JsonPrimitive json = new JsonPrimitive("10.5");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("10.5"), result.resultOrThrow());
	}
	
	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive().minScale(2);
		JsonPrimitive json = new JsonPrimitive("-7.5");
		
		Result<BigDecimal> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<BigDecimal> codec = BIG_DECIMAL;
		String str = codec.toString();
		assertEquals("BigDecimalCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<BigDecimal> codec = BIG_DECIMAL.positive().scale(2);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedBigDecimalCodec["));
		assertTrue(str.contains("constraints="));
	}
}
