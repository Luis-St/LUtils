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
 * Test class for {@link LocalDateTimeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class LocalDateTimeConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfWeek() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED;
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
		assertTrue(config.hour().isEmpty());
		assertTrue(config.minute().isEmpty());
		assertTrue(config.second().isEmpty());
		assertTrue(config.millisecond().isEmpty());
		assertTrue(config.nanosecond().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(LocalDateTime.now()).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(dateTime);
		assertTrue(config.equalTo().isPresent());
		assertEquals(dateTime, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(dateTime);
		assertTrue(config.equalTo().isPresent());
		assertEquals(dateTime, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		LocalDateTime dateTime1 = LocalDateTime.now();
		LocalDateTime dateTime2 = dateTime1.plusHours(1);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(dateTime1, dateTime2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(dateTime1, dateTime2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		LocalDateTime dateTime1 = LocalDateTime.now();
		LocalDateTime dateTime2 = dateTime1.plusHours(1);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(dateTime1, dateTime2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(dateTime1, dateTime2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withAfter() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withAfter(dateTime);
		assertTrue(config.after().isPresent());
		assertEquals(dateTime, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqual() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(dateTime);
		assertTrue(config.after().isPresent());
		assertEquals(dateTime, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withBefore() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBefore(dateTime);
		assertTrue(config.before().isPresent());
		assertEquals(dateTime, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqual() {
		LocalDateTime dateTime = LocalDateTime.now();
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(dateTime);
		assertTrue(config.before().isPresent());
		assertEquals(dateTime, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetween() {
		LocalDateTime after = LocalDateTime.now();
		LocalDateTime before = after.plusHours(1);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		LocalDateTime after = LocalDateTime.now();
		LocalDateTime before = after.plusHours(1);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withWithinLast() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofHours(24), config.withinLast().get());
	}
	
	@Test
	void withWithinNext() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofHours(24), config.withinNext().get());
	}
	
	@Test
	void withDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dayOfWeekConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(dayOfWeekConfig);
		assertTrue(config.dayOfWeek().isPresent());
		assertEquals(dayOfWeekConfig, config.dayOfWeek().get());
	}
	
	@Test
	void withDayOfMonth() {
		NumericFieldConstraintConfig dayOfMonthConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 31);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(dayOfMonthConfig);
		assertTrue(config.dayOfMonth().isPresent());
		assertEquals(dayOfMonthConfig, config.dayOfMonth().get());
	}
	
	@Test
	void withDayOfYear() {
		NumericFieldConstraintConfig dayOfYearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 365);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfYear(dayOfYearConfig);
		assertTrue(config.dayOfYear().isPresent());
		assertEquals(dayOfYearConfig, config.dayOfYear().get());
	}
	
	@Test
	void withWeekOfMonth() {
		NumericFieldConstraintConfig weekOfMonthConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 5);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfMonth(weekOfMonthConfig);
		assertTrue(config.weekOfMonth().isPresent());
		assertEquals(weekOfMonthConfig, config.weekOfMonth().get());
	}
	
	@Test
	void withWeekOfYear() {
		NumericFieldConstraintConfig weekOfYearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 53);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfYear(weekOfYearConfig);
		assertTrue(config.weekOfYear().isPresent());
		assertEquals(weekOfYearConfig, config.weekOfYear().get());
	}
	
	@Test
	void withMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JANUARY, Month.DECEMBER));
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.month().isPresent());
		assertEquals(monthConfig, config.month().get());
	}
	
	@Test
	void withYear() {
		NumericFieldConstraintConfig yearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2000);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}
	
	@Test
	void withHour() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}
	
	@Test
	void withMinute() {
		NumericFieldConstraintConfig minuteConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}
	
	@Test
	void withSecond() {
		NumericFieldConstraintConfig secondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}
	
	@Test
	void withMillisecond() {
		NumericFieldConstraintConfig millisecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 999);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}
	
	@Test
	void withNanosecond() {
		NumericFieldConstraintConfig nanosecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}
	
	@Test
	void withCustom() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withCustom(dt -> dt.getYear() > 2000 ? Result.success() : Result.error("Year must be after 2000"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void matchesWithEqualTo() {
		LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(dateTime);
		assertTrue(config.matches(dateTime).isSuccess());
		assertTrue(config.matches(dateTime.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		LocalDateTime dateTime = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(dateTime);
		assertTrue(config.matches(dateTime.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(dateTime).isError());
	}
	
	@Test
	void matchesWithIn() {
		LocalDateTime dateTime1 = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTime dateTime2 = LocalDateTime.of(2024, 6, 16, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(dateTime1, dateTime2));
		assertTrue(config.matches(dateTime1).isSuccess());
		assertTrue(config.matches(dateTime2).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 17, 10, 30, 0)).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		LocalDateTime dateTime1 = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTime dateTime2 = LocalDateTime.of(2024, 6, 16, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(dateTime1, dateTime2));
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 17, 10, 30, 0)).isSuccess());
		assertTrue(config.matches(dateTime1).isError());
	}
	
	@Test
	void matchesWithAfter() {
		LocalDateTime threshold = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertTrue(config.matches(threshold.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.minusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		LocalDateTime threshold = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold.minusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithBefore() {
		LocalDateTime threshold = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertTrue(config.matches(threshold.minusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		LocalDateTime threshold = LocalDateTime.of(2024, 6, 15, 10, 30, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.minusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dayOfWeekConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withEqualTo(DayOfWeek.MONDAY);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(dayOfWeekConfig);
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 17, 10, 0, 0)).isSuccess()); // Monday
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 18, 10, 0, 0)).isError()); // Tuesday
	}
	
	@Test
	void matchesWithMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withEqualTo(Month.JANUARY);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.matches(LocalDateTime.of(2024, 1, 15, 10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 0, 0)).isError());
	}
	
	@Test
	void matchesWithHour() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 8, 0, 0)).isError());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 18, 0, 0)).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
