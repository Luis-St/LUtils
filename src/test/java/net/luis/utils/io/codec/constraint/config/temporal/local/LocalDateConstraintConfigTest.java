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

import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
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
		assertTrue(config.matches(LocalDate.now()).isSuccess());
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
		NumericFieldConstraintConfig dayOfMonthConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfMonth(dayOfMonthConfig);
		assertTrue(config.dayOfMonth().isPresent());
		assertEquals(dayOfMonthConfig, config.dayOfMonth().get());
	}
	
	@Test
	void withDayOfYear() {
		NumericFieldConstraintConfig dayOfYearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 365);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfYear(dayOfYearConfig);
		assertTrue(config.dayOfYear().isPresent());
		assertEquals(dayOfYearConfig, config.dayOfYear().get());
	}
	
	@Test
	void withWeekOfMonth() {
		NumericFieldConstraintConfig weekOfMonthConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 5);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWeekOfMonth(weekOfMonthConfig);
		assertTrue(config.weekOfMonth().isPresent());
		assertEquals(weekOfMonthConfig, config.weekOfMonth().get());
	}
	
	@Test
	void withWeekOfYear() {
		NumericFieldConstraintConfig weekOfYearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 53);
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
		NumericFieldConstraintConfig yearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2000);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}
	
	@Test
	void withCustom() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withCustom(d -> d.getYear() > 2000 ? Result.success() : Result.error("Year must be after 2000"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void matchesWithEqualTo() {
		LocalDate date = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEqualTo(date);
		assertTrue(config.matches(date).isSuccess());
		assertTrue(config.matches(date.plusDays(1)).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		LocalDate date = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotEqualTo(date);
		assertTrue(config.matches(date.plusDays(1)).isSuccess());
		assertTrue(config.matches(date).isError());
	}
	
	@Test
	void matchesWithIn() {
		LocalDate date1 = LocalDate.of(2024, 6, 15);
		LocalDate date2 = LocalDate.of(2024, 6, 16);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withIn(List.of(date1, date2));
		assertTrue(config.matches(date1).isSuccess());
		assertTrue(config.matches(date2).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		LocalDate date1 = LocalDate.of(2024, 6, 15);
		LocalDate date2 = LocalDate.of(2024, 6, 16);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withNotIn(List.of(date1, date2));
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isSuccess());
		assertTrue(config.matches(date1).isError());
	}
	
	@Test
	void matchesWithAfter() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertTrue(config.matches(threshold.plusDays(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.minusDays(1)).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.plusDays(1)).isSuccess());
		assertTrue(config.matches(threshold.minusDays(1)).isError());
	}
	
	@Test
	void matchesWithBefore() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertTrue(config.matches(threshold.minusDays(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.plusDays(1)).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		LocalDate threshold = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.minusDays(1)).isSuccess());
		assertTrue(config.matches(threshold.plusDays(1)).isError());
	}
	
	@Test
	void matchesWithDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dayOfWeekConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withEqualTo(DayOfWeek.MONDAY);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(dayOfWeekConfig);
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 18)).isError());
	}
	
	@Test
	void matchesWithMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withEqualTo(Month.JANUARY);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.matches(LocalDate.of(2024, 1, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isError());
	}
	
	@Test
	void matchesWithYear() {
		NumericFieldConstraintConfig yearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2019, 6, 15)).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
