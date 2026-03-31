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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PeriodConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class PeriodConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDay() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithPositiveAndNegative() {
		assertThrows(IllegalArgumentException.class, () -> new PeriodConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.of(false), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.day().isEmpty());
		assertTrue(config.month().isEmpty());
		assertTrue(config.year().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(Period.ofDays(1)));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(PeriodConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withPositive();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withEqualTo(Period.ofDays(10));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Period.ofDays(10), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNotEqualTo(Period.ofDays(10));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Period.ofDays(10), config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withIn(List.of(Period.ofDays(1), Period.ofDays(2)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Period.ofDays(1), Period.ofDays(2)), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Period.ofDays(1), Period.ofDays(2)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Period.ofDays(1), Period.ofDays(2)), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withGreaterThan() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withGreaterThan(Period.ofDays(5));
		assertTrue(config.min().isPresent());
		assertEquals(Period.ofDays(5), config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(Period.ofDays(5));
		assertTrue(config.min().isPresent());
		assertEquals(Period.ofDays(5), config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withLessThan(Period.ofMonths(3));
		assertTrue(config.max().isPresent());
		assertEquals(Period.ofMonths(3), config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(Period.ofMonths(3));
		assertTrue(config.max().isPresent());
		assertEquals(Period.ofMonths(3), config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withBetween(Period.ofDays(1), Period.ofMonths(1));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Period.ofDays(1), config.min().get().getFirst());
		assertEquals(Period.ofMonths(1), config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Period.ofDays(1), Period.ofMonths(1));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Period.ofDays(1), config.min().get().getFirst());
		assertEquals(Period.ofMonths(1), config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withPositive() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}
	
	@Test
	void withNonPositive() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}
	
	@Test
	void withNegative() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}
	
	@Test
	void withNonNegative() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}
	
	@Test
	void withZero() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withNonZero() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}
	
	@Test
	void withDay() {
		NumericConstraintConfig dayConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 31);
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withDay(dayConfig);
		assertTrue(config.day().isPresent());
		assertEquals(dayConfig, config.day().get());
	}
	
	@Test
	void withMonth() {
		NumericConstraintConfig monthConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 12);
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.month().isPresent());
		assertEquals(monthConfig, config.month().get());
	}
	
	@Test
	void withYear() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}
	
	@Test
	void withCustom() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withCustom(p -> {
			if (p.getYears() >= 100) throw new ConstraintViolateException("Period must be less than 100 years");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void validateWithEqualTo() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withEqualTo(Period.ofDays(10));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(10)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(5)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNotEqualTo(Period.ofDays(10));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(5)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(10)));
	}
	
	@Test
	void validateWithIn() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withIn(List.of(Period.ofDays(1), Period.ofDays(2)));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(1)));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(3)));
	}
	
	@Test
	void validateWithNotIn() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Period.ofDays(1), Period.ofDays(2)));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(3)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(1)));
	}
	
	@Test
	void validateWithPositive() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withPositive();
		assertDoesNotThrow(() -> config.validate(Period.ofDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(-1)));
	}
	
	@Test
	void validateWithNonPositive() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertDoesNotThrow(() -> config.validate(Period.ZERO));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(1)));
	}
	
	@Test
	void validateWithNegative() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNegative();
		assertDoesNotThrow(() -> config.validate(Period.ofDays(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(1)));
	}
	
	@Test
	void validateWithNonNegative() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertDoesNotThrow(() -> config.validate(Period.ZERO));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(-1)));
	}
	
	@Test
	void validateWithZero() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withZero();
		assertDoesNotThrow(() -> config.validate(Period.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ofDays(-1)));
	}
	
	@Test
	void validateWithNonZero() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED.withNonZero();
		assertDoesNotThrow(() -> config.validate(Period.ofDays(1)));
		assertDoesNotThrow(() -> config.validate(Period.ofDays(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Period.ZERO));
	}
	
	@Test
	void validateWithNullValue() {
		PeriodConstraintConfig config = PeriodConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
