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

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DurationConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class DurationConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithPositiveAndNegative() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.of(false), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Duration.ofHours(-1)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.of(Duration.ZERO), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new DurationConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isEmpty());
		assertTrue(config.hour().isEmpty());
		assertTrue(config.minute().isEmpty());
		assertTrue(config.second().isEmpty());
		assertTrue(config.millisecond().isEmpty());
		assertTrue(config.nanosecond().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(DurationConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withPositive();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withEqualTo(Duration.ofHours(2));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Duration.ofHours(2), config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNotEqualTo(Duration.ofHours(2));
		assertTrue(config.equalTo().isPresent());
		assertEquals(Duration.ofHours(2), config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withIn(List.of(Duration.ofHours(1), Duration.ofHours(2)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Duration.ofHours(1), Duration.ofHours(2)), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Duration.ofHours(1), Duration.ofHours(2)));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(Duration.ofHours(1), Duration.ofHours(2)), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withGreaterThan() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withGreaterThan(Duration.ofHours(1));
		assertTrue(config.min().isPresent());
		assertEquals(Duration.ofHours(1), config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(Duration.ofHours(1));
		assertTrue(config.min().isPresent());
		assertEquals(Duration.ofHours(1), config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withLessThan(Duration.ofHours(10));
		assertTrue(config.max().isPresent());
		assertEquals(Duration.ofHours(10), config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(Duration.ofHours(10));
		assertTrue(config.max().isPresent());
		assertEquals(Duration.ofHours(10), config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withBetween(Duration.ofHours(1), Duration.ofHours(10));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Duration.ofHours(1), config.min().get().getFirst());
		assertEquals(Duration.ofHours(10), config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Duration.ofHours(1), Duration.ofHours(10));
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(Duration.ofHours(1), config.min().get().getFirst());
		assertEquals(Duration.ofHours(10), config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withPositive() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}
	
	@Test
	void withNonPositive() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}
	
	@Test
	void withNegative() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}
	
	@Test
	void withNonNegative() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}
	
	@Test
	void withZero() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withNonZero() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}
	
	@Test
	void withWithinLast() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofHours(24), config.withinLast().get());
	}
	
	@Test
	void withWithinNext() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofHours(24), config.withinNext().get());
	}
	
	@Test
	void withHour() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}
	
	@Test
	void withMinute() {
		NumericConstraintConfig minuteConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}
	
	@Test
	void withSecond() {
		NumericConstraintConfig secondConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}
	
	@Test
	void withMillisecond() {
		NumericConstraintConfig millisecondConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 999);
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}
	
	@Test
	void withNanosecond() {
		NumericConstraintConfig nanosecondConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}
	
	@Test
	void withCustom() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withCustom(d -> {
			if (d.toHours() >= 100) throw new ConstraintViolateException("Duration must be less than 100 hours");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void validateWithEqualTo() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withEqualTo(Duration.ofHours(2));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(3)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNotEqualTo(Duration.ofHours(2));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(2)));
	}
	
	@Test
	void validateWithIn() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withIn(List.of(Duration.ofHours(1), Duration.ofHours(2)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(3)));
	}
	
	@Test
	void validateWithNotIn() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNotIn(List.of(Duration.ofHours(1), Duration.ofHours(2)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(3)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(1)));
	}
	
	@Test
	void validateWithGreaterThan() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withGreaterThan(Duration.ofHours(5));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(6)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(5)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(4)));
	}
	
	@Test
	void validateWithGreaterThanOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(Duration.ofHours(5));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(5)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(6)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(4)));
	}
	
	@Test
	void validateWithLessThan() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withLessThan(Duration.ofHours(10));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(9)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(10)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(11)));
	}
	
	@Test
	void validateWithLessThanOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(Duration.ofHours(10));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(10)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(9)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(11)));
	}
	
	@Test
	void validateWithBetween() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withBetween(Duration.ofHours(1), Duration.ofHours(10));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(5)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(10)));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(Duration.ofHours(1), Duration.ofHours(10));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(10)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(5)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(11)));
	}
	
	@Test
	void validateWithPositive() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withPositive();
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(-1)));
	}
	
	@Test
	void validateWithNonPositive() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertDoesNotThrow(() -> config.validate(Duration.ZERO));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(1)));
	}
	
	@Test
	void validateWithNegative() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNegative();
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(1)));
	}
	
	@Test
	void validateWithNonNegative() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertDoesNotThrow(() -> config.validate(Duration.ZERO));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(-1)));
	}
	
	@Test
	void validateWithZero() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withZero();
		assertDoesNotThrow(() -> config.validate(Duration.ZERO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ofHours(-1)));
	}
	
	@Test
	void validateWithNonZero() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED.withNonZero();
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(1)));
		assertDoesNotThrow(() -> config.validate(Duration.ofHours(-1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Duration.ZERO));
	}
	
	@Test
	void validateWithNullValue() {
		DurationConstraintConfig config = DurationConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
