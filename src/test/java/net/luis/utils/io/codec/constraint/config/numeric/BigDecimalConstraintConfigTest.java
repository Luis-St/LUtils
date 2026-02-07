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

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigDecimalConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class BigDecimalConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPercentage() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIntegral() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNormalized() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullScaleMin() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullScaleMax() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPrecisionMin() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPrecisionMax() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(new BigDecimal("10"), new BigDecimal("5")));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withBetween(new BigDecimal("5"), new BigDecimal("5")));
	}
	
	@Test
	void constructWithNegativeScaleMin() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withMinScale(-1));
	}
	
	@Test
	void constructWithScaleMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withScaleBetween(5, 2));
	}
	
	@Test
	void constructWithEqualScaleMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(2, false)), Optional.of(Pair.of(2, true)),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNonPositivePrecisionMin() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withMinPrecision(0));
	}
	
	@Test
	void constructWithPrecisionMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> BigDecimalConstraintConfig.UNCONSTRAINED.withPrecisionBetween(5, 2));
	}
	
	@Test
	void constructWithEqualPrecisionMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new BigDecimalConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Pair.of(3, false)), Optional.of(Pair.of(3, true)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.percentage().isEmpty());
		assertTrue(config.integral().isEmpty());
		assertTrue(config.normalized().isEmpty());
		assertTrue(config.scaleMin().isEmpty());
		assertTrue(config.scaleMax().isEmpty());
		assertTrue(config.precisionMin().isEmpty());
		assertTrue(config.precisionMax().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(new BigDecimal("3.14")));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(BigDecimalConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPositive();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withEqualTo(new BigDecimal("3.14"));
		assertTrue(config.equalTo().isPresent());
		assertEquals(new BigDecimal("3.14"), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNotEqualTo(new BigDecimal("3.14"));
		assertTrue(config.equalTo().isPresent());
		assertEquals(new BigDecimal("3.14"), config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIn(List.of(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNotIn(List.of(new BigDecimal("4"), new BigDecimal("5")));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(new BigDecimal("4"), new BigDecimal("5")), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withGreaterThan() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withGreaterThan(new BigDecimal("5"));
		assertTrue(config.min().isPresent());
		assertEquals(new BigDecimal("5"), config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(new BigDecimal("5"));
		assertTrue(config.min().isPresent());
		assertEquals(new BigDecimal("5"), config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withLessThan(new BigDecimal("10"));
		assertTrue(config.max().isPresent());
		assertEquals(new BigDecimal("10"), config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(new BigDecimal("10"));
		assertTrue(config.max().isPresent());
		assertEquals(new BigDecimal("10"), config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withBetween(new BigDecimal("1"), new BigDecimal("10"));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(new BigDecimal("1"), config.min().get().getFirst());
		assertEquals(new BigDecimal("10"), config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(new BigDecimal("1"), new BigDecimal("10"));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(new BigDecimal("1"), config.min().get().getFirst());
		assertEquals(new BigDecimal("10"), config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withPositive() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}
	
	@Test
	void withNonPositive() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}
	
	@Test
	void withNegative() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}
	
	@Test
	void withNonNegative() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}
	
	@Test
	void withZero() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withNonZero() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}
	
	@Test
	void withPercentage() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPercentage();
		assertTrue(config.percentage().isPresent());
		assertEquals(Unit.INSTANCE, config.percentage().get());
	}
	
	@Test
	void withIntegral() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIntegral();
		assertTrue(config.integral().isPresent());
		assertEquals(Unit.INSTANCE, config.integral().get());
	}
	
	@Test
	void withNormalized() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNormalized();
		assertTrue(config.normalized().isPresent());
		assertEquals(Unit.INSTANCE, config.normalized().get());
	}
	
	@Test
	void withScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2);
		assertTrue(config.scaleMin().isPresent());
		assertTrue(config.scaleMax().isPresent());
		assertEquals(2, config.scaleMin().get().getFirst());
		assertEquals(2, config.scaleMax().get().getFirst());
		assertTrue(config.scaleMin().get().getSecond());
		assertTrue(config.scaleMax().get().getSecond());
	}
	
	@Test
	void withMinScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMinScale(2);
		assertTrue(config.scaleMin().isPresent());
		assertEquals(2, config.scaleMin().get().getFirst());
		assertTrue(config.scaleMin().get().getSecond());
	}
	
	@Test
	void withMaxScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMaxScale(4);
		assertTrue(config.scaleMax().isPresent());
		assertEquals(4, config.scaleMax().get().getFirst());
		assertTrue(config.scaleMax().get().getSecond());
	}
	
	@Test
	void withScaleBetween() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScaleBetween(2, 4);
		assertTrue(config.scaleMin().isPresent());
		assertTrue(config.scaleMax().isPresent());
		assertEquals(2, config.scaleMin().get().getFirst());
		assertEquals(4, config.scaleMax().get().getFirst());
	}
	
	@Test
	void withPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPrecision(5);
		assertTrue(config.precisionMin().isPresent());
		assertTrue(config.precisionMax().isPresent());
		assertEquals(5, config.precisionMin().get().getFirst());
		assertEquals(5, config.precisionMax().get().getFirst());
		assertTrue(config.precisionMin().get().getSecond());
		assertTrue(config.precisionMax().get().getSecond());
	}
	
	@Test
	void withMinPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMinPrecision(3);
		assertTrue(config.precisionMin().isPresent());
		assertEquals(3, config.precisionMin().get().getFirst());
		assertTrue(config.precisionMin().get().getSecond());
	}
	
	@Test
	void withMaxPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withMaxPrecision(10);
		assertTrue(config.precisionMax().isPresent());
		assertEquals(10, config.precisionMax().get().getFirst());
		assertTrue(config.precisionMax().get().getSecond());
	}
	
	@Test
	void withPrecisionBetween() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPrecisionBetween(3, 10);
		assertTrue(config.precisionMin().isPresent());
		assertTrue(config.precisionMax().isPresent());
		assertEquals(3, config.precisionMin().get().getFirst());
		assertEquals(10, config.precisionMax().get().getFirst());
	}
	
	@Test
	void validateWithEqualTo() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withEqualTo(new BigDecimal("3.14"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("3.14")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("3.15")));
	}
	
	@Test
	void validateWithNotEqualTo() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNotEqualTo(new BigDecimal("3.14"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("3.15")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("3.14")));
	}
	
	@Test
	void validateWithIn() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIn(List.of(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("2")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("4")));
	}
	
	@Test
	void validateWithNotIn() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNotIn(List.of(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("4")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1")));
	}
	
	@Test
	void validateWithGreaterThan() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withGreaterThan(new BigDecimal("5"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("5.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("5")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("4.9")));
	}
	
	@Test
	void validateWithGreaterThanOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(new BigDecimal("5"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("5")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("5.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("4.9")));
	}
	
	@Test
	void validateWithLessThan() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withLessThan(new BigDecimal("10"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("9.9")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("10")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("10.1")));
	}
	
	@Test
	void validateWithLessThanOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(new BigDecimal("10"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("10")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("9.9")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("10.1")));
	}
	
	@Test
	void validateWithBetween() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withBetween(new BigDecimal("1"), new BigDecimal("10"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("5")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("10")));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(new BigDecimal("1"), new BigDecimal("10"));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("10")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("5")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("0.9")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("10.1")));
	}
	
	@Test
	void validateWithPositive() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPositive();
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("100")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(BigDecimal.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-0.1")));
	}
	
	@Test
	void validateWithNonPositive() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertDoesNotThrow(() -> config.validate(BigDecimal.ZERO));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("-0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("0.1")));
	}
	
	@Test
	void validateWithNegative() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNegative();
		assertDoesNotThrow(() -> config.validate(new BigDecimal("-0.1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("-100")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(BigDecimal.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("0.1")));
	}
	
	@Test
	void validateWithNonNegative() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertDoesNotThrow(() -> config.validate(BigDecimal.ZERO));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-0.1")));
	}
	
	@Test
	void validateWithZero() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withZero();
		assertDoesNotThrow(() -> config.validate(BigDecimal.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-0.1")));
	}
	
	@Test
	void validateWithNonZero() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNonZero();
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("-0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(BigDecimal.ZERO));
	}
	
	@Test
	void validateWithPercentage() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPercentage();
		assertDoesNotThrow(() -> config.validate(BigDecimal.ZERO));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("50")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("100")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("100.1")));
	}
	
	@Test
	void validateWithIntegral() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withIntegral();
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("42")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("-5")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.00")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.5")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("3.14")));
	}
	
	@Test
	void validateWithNormalized() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withNormalized();
		assertDoesNotThrow(() -> config.validate(BigDecimal.ZERO));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.5")));
		assertDoesNotThrow(() -> config.validate(BigDecimal.ONE));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-0.1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.1")));
	}
	
	@Test
	void validateWithScale() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScale(2);
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.23")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.00")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.2")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.234")));
	}
	
	@Test
	void validateWithScaleRange() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withScaleBetween(2, 4);
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.23")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.234")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.2345")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.2")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.23456")));
	}
	
	@Test
	void validateWithPrecision() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPrecision(3);
		assertDoesNotThrow(() -> config.validate(new BigDecimal("123")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1.23")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("12")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1234")));
	}
	
	@Test
	void validateWithPrecisionRange() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED.withPrecisionBetween(2, 4);
		assertDoesNotThrow(() -> config.validate(new BigDecimal("12")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("123")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("1234")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("12345")));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED
			.withPositive()
			.withMaxScale(2)
			.withLessThanOrEqual(new BigDecimal("100"));
		
		assertDoesNotThrow(() -> config.validate(new BigDecimal("0.01")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("50.50")));
		assertDoesNotThrow(() -> config.validate(new BigDecimal("100")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(BigDecimal.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("-1")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("1.123")));
		assertThrows(ConstraintViolateException.class, () -> config.validate(new BigDecimal("100.01")));
	}
	
	@Test
	void validateWithNullValue() {
		BigDecimalConstraintConfig config = BigDecimalConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
