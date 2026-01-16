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

package net.luis.utils.io.codec.constraint_new.config.temporal;

import static org.junit.jupiter.api.Assertions.*;

import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

/**
 * Test class for {@link OffsetDateTimeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class OffsetDateTimeConstraintConfigTest {

	private static final OffsetDateTime DT_2024_01_15_10_30 = OffsetDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneOffset.UTC);
	private static final OffsetDateTime DT_2024_06_15_12_00 = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
	private static final OffsetDateTime DT_2024_12_25_14_30 = OffsetDateTime.of(2024, 12, 25, 14, 30, 0, 0, ZoneOffset.UTC);
	private static final OffsetDateTime DT_2025_01_01_00_00 = OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

	@Test
	void unconstrained() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED;
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
		assertTrue(config.offset().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}

	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullDayOfWeek() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullDayOfMonth() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullDayOfYear() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullWeekOfMonth() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullWeekOfYear() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullOffset() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}

	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}

	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void withEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(DT_2024_06_15_12_00, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}

	@Test
	void withNotEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(DT_2024_06_15_12_00, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}

	@Test
	void withIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withIn(null));
	}

	@Test
	void withNotIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}

	@Test
	void withAfter() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}

	@Test
	void withAfterNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfter(null));
	}

	@Test
	void withAfterOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(DT_2024_01_15_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}

	@Test
	void withAfterOrEqualNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(null));
	}

	@Test
	void withBefore() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBefore(DT_2025_01_01_00_00);
		assertTrue(config.before().isPresent());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}

	@Test
	void withBeforeNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBefore(null));
	}

	@Test
	void withBeforeOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(DT_2025_01_01_00_00);
		assertTrue(config.before().isPresent());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}

	@Test
	void withBeforeOrEqualNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(null));
	}

	@Test
	void withBetween() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}

	@Test
	void withBetweenNullAfter() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetween(null, DT_2025_01_01_00_00));
	}

	@Test
	void withBetweenNullBefore() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, null));
	}

	@Test
	void withBetweenOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}

	@Test
	void withBetweenOrEqualNullAfter() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(null, DT_2025_01_01_00_00));
	}

	@Test
	void withBetweenOrEqualNullBefore() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, null));
	}

	@Test
	void withWithinLast() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(30));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofDays(30), config.withinLast().get());
	}

	@Test
	void withWithinLastNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(null));
	}

	@Test
	void withWithinNext() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(7));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofDays(7), config.withinNext().get());
	}

	@Test
	void withWithinNextNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(null));
	}

	@Test
	void withDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dowConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(dowConfig);
		assertTrue(config.dayOfWeek().isPresent());
		assertEquals(dowConfig, config.dayOfWeek().get());
	}

	@Test
	void withDayOfWeekNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(null));
	}

	@Test
	void withDayOfMonth() {
		NumericFieldConstraintConfig domConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 15);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(domConfig);
		assertTrue(config.dayOfMonth().isPresent());
		assertEquals(domConfig, config.dayOfMonth().get());
	}

	@Test
	void withDayOfMonthNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(null));
	}

	@Test
	void withDayOfYear() {
		NumericFieldConstraintConfig doyConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 100);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfYear(doyConfig);
		assertTrue(config.dayOfYear().isPresent());
		assertEquals(doyConfig, config.dayOfYear().get());
	}

	@Test
	void withDayOfYearNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfYear(null));
	}

	@Test
	void withWeekOfMonth() {
		NumericFieldConstraintConfig womConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 4);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfMonth(womConfig);
		assertTrue(config.weekOfMonth().isPresent());
		assertEquals(womConfig, config.weekOfMonth().get());
	}

	@Test
	void withWeekOfMonthNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfMonth(null));
	}

	@Test
	void withWeekOfYear() {
		NumericFieldConstraintConfig woyConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 52);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfYear(woyConfig);
		assertTrue(config.weekOfYear().isPresent());
		assertEquals(woyConfig, config.weekOfYear().get());
	}

	@Test
	void withWeekOfYearNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfYear(null));
	}

	@Test
	void withMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JUNE, Month.JULY, Month.AUGUST));
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.month().isPresent());
		assertEquals(monthConfig, config.month().get());
	}

	@Test
	void withMonthNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(null));
	}

	@Test
	void withYear() {
		NumericFieldConstraintConfig yearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(2020, 2030);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}

	@Test
	void withYearNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(null));
	}

	@Test
	void withHour() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}

	@Test
	void withHourNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(null));
	}

	@Test
	void withMinute() {
		NumericFieldConstraintConfig minuteConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withIn(List.of(0, 15, 30, 45));
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}

	@Test
	void withMinuteNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMinute(null));
	}

	@Test
	void withSecond() {
		NumericFieldConstraintConfig secondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}

	@Test
	void withSecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withSecond(null));
	}

	@Test
	void withMillisecond() {
		NumericFieldConstraintConfig millisecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}

	@Test
	void withMillisecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(null));
	}

	@Test
	void withNanosecond() {
		NumericFieldConstraintConfig nanosecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}

	@Test
	void withNanosecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNanosecond(null));
	}

	@Test
	void withOffset() {
		ZoneOffsetConstraintConfig offsetConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withOffset(offsetConfig);
		assertTrue(config.offset().isPresent());
		assertEquals(offsetConfig, config.offset().get());
	}

	@Test
	void withOffsetNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withOffset(null));
	}

	@Test
	void withCustom() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withCustom(dt -> dt.getHour() < 12 ? Result.success() : Result.error("Time must be before noon"));
		assertTrue(config.custom().isPresent());
	}

	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withCustom(null));
	}

	@Test
	void matchesWithEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}

	@Test
	void matchesWithNotEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}

	@Test
	void matchesWithIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isError());
	}

	@Test
	void matchesWithNotIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}

	@Test
	void matchesWithAfter() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}

	@Test
	void matchesWithAfterOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(DT_2024_01_15_10_30);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}

	@Test
	void matchesWithBefore() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBefore(DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}

	@Test
	void matchesWithBeforeOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2025_01_01_00_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
	}

	@Test
	void matchesWithBetween() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}

	@Test
	void matchesWithBetweenOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}

	@Test
	void matchesWithMonthConstraint() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JUNE, Month.DECEMBER));
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}

	@Test
	void matchesWithYearConstraint() {
		NumericFieldConstraintConfig yearConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(2024);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}

	@Test
	void matchesWithHourConstraint() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 13);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isError());
	}

	@Test
	void matchesWithOffsetConstraint() {
		ZoneOffsetConstraintConfig offsetConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withOffset(offsetConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2))).isError());
	}

	@Test
	void matchesWithMultipleConstraints() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withAfterOrEqual(DT_2024_01_15_10_30)
			.withBeforeOrEqual(DT_2025_01_01_00_00)
			.withNotIn(List.of(DT_2024_06_15_12_00));

		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}

	@Test
	void matchesWithNullValue() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
