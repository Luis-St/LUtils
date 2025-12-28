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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigDecimalConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class BigDecimalConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new BigDecimalConstraintConfig(Optional.empty(), Optional.of(true), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(true), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.of(2), OptionalInt.of(5), OptionalInt.empty(), OptionalInt.empty()));
		assertDoesNotThrow(() -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(1), OptionalInt.of(10)));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(null, Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), null, Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), null, OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), null, OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), null, OptionalInt.empty(), OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), null, OptionalInt.empty()));
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), null));
	}
	
	@Test
	void constructorMaxScaleLessThanMinScale() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.of(5), OptionalInt.of(2), OptionalInt.empty(), OptionalInt.empty()));
	}
	
	@Test
	void constructorNegativePrecision() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(-1), OptionalInt.empty()));
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(-1)));
	}
	
	@Test
	void constructorMaxPrecisionLessThanMinPrecision() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.of(10), OptionalInt.of(5)));
	}
	
	@Test
	void unconstrainedConstant() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.numericConfig().isEmpty());
		assertTrue(config.integral().isEmpty());
		assertTrue(config.normalized().isEmpty());
		assertTrue(config.minScale().isEmpty());
		assertTrue(config.maxScale().isEmpty());
		assertTrue(config.minPrecision().isEmpty());
		assertTrue(config.maxPrecision().isEmpty());
	}
	
	@Test
	void isUnconstrained() {
		assertTrue(BigDecimalConstraintConfig.UNCONSTRAINED.isUnconstrained());
		assertFalse(BigDecimalConstraintConfig.UNCONSTRAINED.withIntegral().isUnconstrained());
	}
	
	@Test
	void withNumericConfig() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNumericConfig(c -> c.withMin(BigDecimal.ZERO, true));
		
		assertTrue(config.numericConfig().isPresent());
		assertTrue(config.numericConfig().get().min().isPresent());
		assertEquals(BigDecimal.ZERO, config.numericConfig().get().min().get().getFirst());
	}
	
	@Test
	void withIntegral() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIntegral();
		
		assertTrue(config.integral().isPresent());
		assertTrue(config.integral().get());
	}
	
	@Test
	void withNormalized() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertTrue(config.normalized().isPresent());
		assertTrue(config.normalized().get());
	}
	
	@Test
	void withMinScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMinScale(2);
		
		assertTrue(config.minScale().isPresent());
		assertEquals(2, config.minScale().getAsInt());
		assertTrue(config.maxScale().isEmpty());
	}
	
	@Test
	void withMaxScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMaxScale(5);
		
		assertTrue(config.minScale().isEmpty());
		assertTrue(config.maxScale().isPresent());
		assertEquals(5, config.maxScale().getAsInt());
	}
	
	@Test
	void withScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5);
		
		assertTrue(config.minScale().isPresent());
		assertEquals(2, config.minScale().getAsInt());
		assertTrue(config.maxScale().isPresent());
		assertEquals(5, config.maxScale().getAsInt());
	}
	
	@Test
	void withMinPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMinPrecision(1);
		
		assertTrue(config.minPrecision().isPresent());
		assertEquals(1, config.minPrecision().getAsInt());
		assertTrue(config.maxPrecision().isEmpty());
	}
	
	@Test
	void withMaxPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMaxPrecision(10);
		
		assertTrue(config.minPrecision().isEmpty());
		assertTrue(config.maxPrecision().isPresent());
		assertEquals(10, config.maxPrecision().getAsInt());
	}
	
	@Test
	void withPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPrecision(1, 10);
		
		assertTrue(config.minPrecision().isPresent());
		assertEquals(1, config.minPrecision().getAsInt());
		assertTrue(config.maxPrecision().isPresent());
		assertEquals(10, config.maxPrecision().getAsInt());
	}
	
	@Test
	void matchesUnconstrained() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED;
		
		assertTrue(config.matches(BigDecimal.ZERO).isSuccess());
		assertTrue(config.matches(BigDecimal.ONE).isSuccess());
		assertTrue(config.matches(new BigDecimal("123.456")).isSuccess());
		assertTrue(config.matches(new BigDecimal("-999.999")).isSuccess());
	}
	
	@Test
	void matchesIntegral() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIntegral();
		
		assertTrue(config.matches(BigDecimal.ZERO).isSuccess());
		assertTrue(config.matches(BigDecimal.ONE).isSuccess());
		assertTrue(config.matches(new BigDecimal("100")).isSuccess());
		assertTrue(config.matches(new BigDecimal("5.0")).isSuccess());
		
		Result<Void> resultFractional = config.matches(new BigDecimal("5.5"));
		assertTrue(resultFractional.isError());
		assertTrue(resultFractional.errorOrThrow().contains("Violated integral constraint"));
	}
	
	@Test
	void matchesNormalized() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNormalized();
		
		assertTrue(config.matches(BigDecimal.ZERO).isSuccess());
		assertTrue(config.matches(new BigDecimal("0.5")).isSuccess());
		assertTrue(config.matches(BigDecimal.ONE).isSuccess());
		
		Result<Void> resultNegative = config.matches(new BigDecimal("-0.1"));
		assertTrue(resultNegative.isError());
		assertTrue(resultNegative.errorOrThrow().contains("Violated normalized constraint"));
		
		Result<Void> resultAboveOne = config.matches(new BigDecimal("1.1"));
		assertTrue(resultAboveOne.isError());
		assertTrue(resultAboveOne.errorOrThrow().contains("Violated normalized constraint"));
	}
	
	@Test
	void matchesWithNumericConfig() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNumericConfig(c -> c.withRange(BigDecimal.ZERO, BigDecimal.TEN, true));
		
		Result<Void> resultBelow = config.matches(new BigDecimal("-1"));
		assertTrue(resultBelow.isError());
		
		assertTrue(config.matches(BigDecimal.ZERO).isSuccess());
		assertTrue(config.matches(BigDecimal.valueOf(5)).isSuccess());
		assertTrue(config.matches(BigDecimal.TEN).isSuccess());
		
		Result<Void> resultAbove = config.matches(new BigDecimal("11"));
		assertTrue(resultAbove.isError());
	}
	
	@Test
	void toStringUnconstrained() {
		String str = BigDecimalConstraintConfig.UNCONSTRAINED.toString();
		assertEquals("BigDecimalConstraintConfig[unconstrained]", str);
	}
	
	@Test
	void toStringWithConstraints() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5).withPrecision(1, 10);
		String str = config.toString();
		assertTrue(str.contains("BigDecimalConstraintConfig["));
		assertTrue(str.contains("minScale=2"));
		assertTrue(str.contains("maxScale=5"));
		assertTrue(str.contains("minPrecision=1"));
		assertTrue(str.contains("maxPrecision=10"));
	}
	
	@Test
	void equality() {
		BigDecimalConstraintConfig config1 = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5);
		BigDecimalConstraintConfig config2 = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5);
		BigDecimalConstraintConfig config3 = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 6);
		
		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}
	
	@Test
	void hashCodeConsistency() {
		BigDecimalConstraintConfig config1 = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5);
		BigDecimalConstraintConfig config2 = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2, 5);
		
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
