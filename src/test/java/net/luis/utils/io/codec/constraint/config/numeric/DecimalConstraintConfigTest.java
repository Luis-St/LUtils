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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DecimalConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class DecimalConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new DecimalConstraintConfig<>(Optional.empty(), Optional.of(true), Optional.empty(), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.of(true), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(true), Optional.empty()));
		assertDoesNotThrow(() -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(true)));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<>(null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<>(Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void unconstrainedMethod() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.numericConfig().isEmpty());
		assertTrue(config.finite().isEmpty());
		assertTrue(config.notNaN().isEmpty());
		assertTrue(config.integral().isEmpty());
		assertTrue(config.normalized().isEmpty());
	}
	
	@Test
	void isUnconstrained() {
		assertTrue(DecimalConstraintConfig.unconstrained().isUnconstrained());
		assertFalse(DecimalConstraintConfig.<Double>unconstrained().withFinite().isUnconstrained());
	}
	
	@Test
	void withNumericConfig() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNumericConfig(c -> c.withMin(0.0, true));
		
		assertTrue(config.numericConfig().isPresent());
		assertTrue(config.numericConfig().get().min().isPresent());
		assertEquals(0.0, config.numericConfig().get().min().get().getFirst());
	}
	
	@Test
	void withFinite() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		
		assertTrue(config.finite().isPresent());
		assertTrue(config.finite().get());
	}
	
	@Test
	void withNotNaN() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotNaN();
		
		assertTrue(config.notNaN().isPresent());
		assertTrue(config.notNaN().get());
	}
	
	@Test
	void withIntegral() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIntegral();
		
		assertTrue(config.integral().isPresent());
		assertTrue(config.integral().get());
	}
	
	@Test
	void withNormalized() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNormalized();
		
		assertTrue(config.normalized().isPresent());
		assertTrue(config.normalized().get());
	}
	
	@Test
	void matchesUnconstrainedDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.unconstrained();
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 1.5).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, -123.456).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, Double.MAX_VALUE).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, Double.MIN_VALUE).isSuccess());
	}
	
	@Test
	void matchesUnconstrainedFloat() {
		DecimalConstraintConfig<Float> config = DecimalConstraintConfig.unconstrained();
		
		assertTrue(config.matches(NumberType.FLOAT, 0.0f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, 1.5f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, -123.456f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, Float.MAX_VALUE).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, Float.MIN_VALUE).isSuccess());
	}
	
	@Test
	void matchesFiniteDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 123.456).isSuccess());
		
		Result<Void> resultInfinity = config.matches(NumberType.DOUBLE, Double.POSITIVE_INFINITY);
		assertTrue(resultInfinity.isError());
		assertTrue(resultInfinity.errorOrThrow().contains("Violated finite constraint"));
		
		Result<Void> resultNegInfinity = config.matches(NumberType.DOUBLE, Double.NEGATIVE_INFINITY);
		assertTrue(resultNegInfinity.isError());
		
		Result<Void> resultNaN = config.matches(NumberType.DOUBLE, Double.NaN);
		assertTrue(resultNaN.isError());
	}
	
	@Test
	void matchesFiniteFloat() {
		DecimalConstraintConfig<Float> config = DecimalConstraintConfig.<Float>unconstrained().withFinite();
		
		assertTrue(config.matches(NumberType.FLOAT, 0.0f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, 123.456f).isSuccess());
		
		Result<Void> resultInfinity = config.matches(NumberType.FLOAT, Float.POSITIVE_INFINITY);
		assertTrue(resultInfinity.isError());
		assertTrue(resultInfinity.errorOrThrow().contains("Violated finite constraint"));
		
		Result<Void> resultNaN = config.matches(NumberType.FLOAT, Float.NaN);
		assertTrue(resultNaN.isError());
	}
	
	@Test
	void matchesNotNaNDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotNaN();
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, Double.POSITIVE_INFINITY).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, Double.NEGATIVE_INFINITY).isSuccess());
		
		Result<Void> resultNaN = config.matches(NumberType.DOUBLE, Double.NaN);
		assertTrue(resultNaN.isError());
		assertTrue(resultNaN.errorOrThrow().contains("Violated not-NaN constraint"));
	}
	
	@Test
	void matchesNotNaNFloat() {
		DecimalConstraintConfig<Float> config = DecimalConstraintConfig.<Float>unconstrained().withNotNaN();
		
		assertTrue(config.matches(NumberType.FLOAT, 0.0f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, Float.POSITIVE_INFINITY).isSuccess());
		
		Result<Void> resultNaN = config.matches(NumberType.FLOAT, Float.NaN);
		assertTrue(resultNaN.isError());
		assertTrue(resultNaN.errorOrThrow().contains("Violated not-NaN constraint"));
	}
	
	@Test
	void matchesIntegralDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIntegral();
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 1.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 100.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, -5.0).isSuccess());
		
		Result<Void> resultFractional = config.matches(NumberType.DOUBLE, 5.5);
		assertTrue(resultFractional.isError());
		assertTrue(resultFractional.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void matchesIntegralFloat() {
		DecimalConstraintConfig<Float> config = DecimalConstraintConfig.<Float>unconstrained().withIntegral();
		
		assertTrue(config.matches(NumberType.FLOAT, 0.0f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, 1.0f).isSuccess());
		
		Result<Void> resultFractional = config.matches(NumberType.FLOAT, 5.5f);
		assertTrue(resultFractional.isError());
		assertTrue(resultFractional.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void matchesNormalizedDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNormalized();
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 0.5).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 1.0).isSuccess());
		
		Result<Void> resultNegative = config.matches(NumberType.DOUBLE, -0.1);
		assertTrue(resultNegative.isError());
		assertTrue(resultNegative.errorOrThrow().contains("Violated normalized constraint"));
		
		Result<Void> resultAboveOne = config.matches(NumberType.DOUBLE, 1.1);
		assertTrue(resultAboveOne.isError());
	}
	
	@Test
	void matchesNormalizedFloat() {
		DecimalConstraintConfig<Float> config = DecimalConstraintConfig.<Float>unconstrained().withNormalized();
		
		assertTrue(config.matches(NumberType.FLOAT, 0.0f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, 0.5f).isSuccess());
		assertTrue(config.matches(NumberType.FLOAT, 1.0f).isSuccess());
		
		Result<Void> resultNegative = config.matches(NumberType.FLOAT, -0.1f);
		assertTrue(resultNegative.isError());
		assertTrue(resultNegative.errorOrThrow().contains("Violated normalized constraint"));
		
		Result<Void> resultAboveOne = config.matches(NumberType.FLOAT, 1.1f);
		assertTrue(resultAboveOne.isError());
	}
	
	@Test
	void matchesWithNumericConfigDouble() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNumericConfig(c -> c.withRange(0.0, 10.0, true));
		
		Result<Void> resultBelow = config.matches(NumberType.DOUBLE, -1.0);
		assertTrue(resultBelow.isError());
		
		assertTrue(config.matches(NumberType.DOUBLE, 0.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 5.0).isSuccess());
		assertTrue(config.matches(NumberType.DOUBLE, 10.0).isSuccess());
		
		Result<Void> resultAbove = config.matches(NumberType.DOUBLE, 11.0);
		assertTrue(resultAbove.isError());
	}
	
	@Test
	void matchesInvalidTypeThrows() {
		DecimalConstraintConfig<Integer> config = DecimalConstraintConfig.unconstrained();
		assertThrows(IllegalArgumentException.class, () -> config.matches(NumberType.INTEGER, 5));
	}
	
	@Test
	void toStringUnconstrained() {
		String str = DecimalConstraintConfig.unconstrained().toString();
		assertEquals("DecimalConstraintConfig[unconstrained]", str);
	}
	
	@Test
	void toStringWithConstraints() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withFinite().withNotNaN().withIntegral();
		String str = config.toString();
		assertTrue(str.contains("DecimalConstraintConfig["));
		assertTrue(str.contains("finite"));
		assertTrue(str.contains("notNaN"));
		assertTrue(str.contains("integral"));
	}
	
	@Test
	void equality() {
		DecimalConstraintConfig<Double> config1 = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		DecimalConstraintConfig<Double> config2 = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		DecimalConstraintConfig<Double> config3 = DecimalConstraintConfig.<Double>unconstrained().withNotNaN();
		
		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}
	
	@Test
	void hashCodeConsistency() {
		DecimalConstraintConfig<Double> config1 = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		DecimalConstraintConfig<Double> config2 = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
