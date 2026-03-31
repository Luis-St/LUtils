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

package net.luis.utils.io.codec.constraint.config.temporal.local;

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class LocalDateConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfWeek() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.after().isEmpty());
		assertTrue(config.before().isEmpty());
		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isEmpty());
		assertTrue(config.dayOfWeek().isEmpty());
		assertTrue(config.dayOfMonth().isEmpty());
		assertTrue(config.dayOfYear().isEmpty());
		assertTrue(config.weekOfMonth().isEmpty());
		assertTrue(config.weekOfYear().isEmpty());
		assertTrue(config.month().isEmpty());
		assertTrue(config.year().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(LocalDate.now()));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(LocalDateConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfter(LocalDate.now());
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEqualTo(date);
		assertTrue(config.equalTo().isPresent());
		assertEquals(date, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotEqualTo(date);
		assertTrue(config.equalTo().isPresent());
		assertEquals(date, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		LocalDate date1 = LocalDate.now();
		LocalDate date2 = date1.plusDays(1);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withIn(List.of(date1, date2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(date1, date2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		LocalDate date1 = LocalDate.now();
		LocalDate date2 = date1.plusDays(1);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotIn(List.of(date1, date2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(date1, date2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withAfter() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfter(date);
		assertTrue(config.after().isPresent());
		assertEquals(date, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqual() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfterOrEqual(date);
		assertTrue(config.after().isPresent());
		assertEquals(date, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withBefore() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBefore(date);
		assertTrue(config.before().isPresent());
		assertEquals(date, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqual() {
		LocalDate date = LocalDate.now();
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(date);
		assertTrue(config.before().isPresent());
		assertEquals(date, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetween() {
		LocalDate after = LocalDate.now();
		LocalDate before = after.plusDays(30);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		LocalDate after = LocalDate.now();
		LocalDate before = after.plusDays(30);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withWithinLast() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(30));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofDays(30), config.withinLast().get());
	}
	
	@Test
	void withWithinNext() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofDays(30), config.withinNext().get());
	}
	
	@Test
	void withDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dayOfWeekConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(dayOfWeekConfig);
		assertTrue(config.dayOfWeek().isPresent());
		assertEquals(dayOfWeekConfig, config.dayOfWeek().get());
	}
	
	@Test
	void withDayOfMonth() {
		NumericConstraintConfig dayOfMonthConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfMonth(dayOfMonthConfig);
		assertTrue(config.dayOfMonth().isPresent());
		assertEquals(dayOfMonthConfig, config.dayOfMonth().get());
	}
	
	@Test
	void withDayOfYear() {
		NumericConstraintConfig dayOfYearConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 365);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfYear(dayOfYearConfig);
		assertTrue(config.dayOfYear().isPresent());
		assertEquals(dayOfYearConfig, config.dayOfYear().get());
	}
	
	@Test
	void withWeekOfMonth() {
		NumericConstraintConfig weekOfMonthConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 5);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWeekOfMonth(weekOfMonthConfig);
		assertTrue(config.weekOfMonth().isPresent());
		assertEquals(weekOfMonthConfig, config.weekOfMonth().get());
	}
	
	@Test
	void withWeekOfYear() {
		NumericConstraintConfig weekOfYearConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 53);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWeekOfYear(weekOfYearConfig);
		assertTrue(config.weekOfYear().isPresent());
		assertEquals(weekOfYearConfig, config.weekOfYear().get());
	}
	
	@Test
	void withMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JANUARY, Month.DECEMBER));
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.month().isPresent());
		assertEquals(monthConfig, config.month().get());
	}
	
	@Test
	void withYear() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2000);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}
	
	@Test
	void withCustom() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withCustom(d -> {
			if (d.getYear() <= 2000) throw new ConstraintViolateException("Year must be after 2000");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void validateWithEqualTo() {
		LocalDate date = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEqualTo(date);
		assertDoesNotThrow(() -> config.validate(date));
		assertThrows(ConstraintViolateException.class, () -> config.validate(date.plusDays(1)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		LocalDate date = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotEqualTo(date);
		assertDoesNotThrow(() -> config.validate(date.plusDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(date));
	}
	
	@Test
	void validateWithIn() {
		LocalDate date1 = LocalDate.of(2024, 6, 15);
		LocalDate date2 = LocalDate.of(2024, 6, 16);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withIn(List.of(date1, date2));
		assertDoesNotThrow(() -> config.validate(date1));
		assertDoesNotThrow(() -> config.validate(date2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalDate.of(2024, 6, 17)));
	}
	
	@Test
	void validateWithNotIn() {
		LocalDate date1 = LocalDate.of(2024, 6, 15);
		LocalDate date2 = LocalDate.of(2024, 6, 16);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotIn(List.of(date1, date2));
		assertDoesNotThrow(() -> config.validate(LocalDate.of(2024, 6, 17)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(date1));
	}
	
	@Test
	void validateWithAfter() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertDoesNotThrow(() -> config.validate(threshold.plusDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.minusDays(1)));
	}
	
	@Test
	void validateWithAfterOrEqual() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(threshold.plusDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.minusDays(1)));
	}
	
	@Test
	void validateWithBefore() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertDoesNotThrow(() -> config.validate(threshold.minusDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.plusDays(1)));
	}
	
	@Test
	void validateWithBeforeOrEqual() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(threshold.minusDays(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.plusDays(1)));
	}
	
	@Test
	void validateWithDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dayOfWeekConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withEqualTo(DayOfWeek.MONDAY);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(dayOfWeekConfig);
		assertDoesNotThrow(() -> config.validate(LocalDate.of(2024, 6, 17)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalDate.of(2024, 6, 18)));
	}
	
	@Test
	void validateWithMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withEqualTo(Month.JANUARY);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertDoesNotThrow(() -> config.validate(LocalDate.of(2024, 1, 15)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalDate.of(2024, 6, 15)));
	}
	
	@Test
	void validateWithYear() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertDoesNotThrow(() -> config.validate(LocalDate.of(2024, 6, 15)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalDate.of(2019, 6, 15)));
	}
	
	@Test
	void validateWithNullValue() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
