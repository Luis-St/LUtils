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

package net.luis.utils.io.codec.constraint.config.temporal.zoned;

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZonedDateTimeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class ZonedDateTimeConstraintConfigTest {
	
	private static final ZonedDateTime DT_2024_01_15_10_30 = ZonedDateTime.of(2024, 1, 15, 10, 30, 0, 0, ZoneId.of("UTC"));
	private static final ZonedDateTime DT_2024_06_15_12_00 = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.of("UTC"));
	private static final ZonedDateTime DT_2024_12_25_14_30 = ZonedDateTime.of(2024, 12, 25, 14, 30, 0, 0, ZoneId.of("UTC"));
	private static final ZonedDateTime DT_2025_01_01_00_00 = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfWeek() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfMonth() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDayOfYear() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfMonth() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWeekOfYear() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMonth() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullYear() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullZone() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofDays(-1)),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new ZonedDateTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED;
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
		assertTrue(config.zone().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withPast();
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualTo() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(DT_2024_06_15_12_00, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(DT_2024_06_15_12_00, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withAfter() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfter(null));
	}
	
	@Test
	void withAfterOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(DT_2024_01_15_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqualNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(null));
	}
	
	@Test
	void withBefore() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBefore(DT_2025_01_01_00_00);
		assertTrue(config.before().isPresent());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBefore(null));
	}
	
	@Test
	void withBeforeOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(DT_2025_01_01_00_00);
		assertTrue(config.before().isPresent());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqualNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(null));
	}
	
	@Test
	void withBetween() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenNullAfter() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetween(null, DT_2025_01_01_00_00));
	}
	
	@Test
	void withBetweenNullBefore() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, null));
	}
	
	@Test
	void withBetweenOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(DT_2024_01_15_10_30, config.after().get().getFirst());
		assertEquals(DT_2025_01_01_00_00, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqualNullAfter() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(null, DT_2025_01_01_00_00));
	}
	
	@Test
	void withBetweenOrEqualNullBefore() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, null));
	}
	
	@Test
	void withWithinLast() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(30));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofDays(30), config.withinLast().get());
	}
	
	@Test
	void withWithinLastNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(null));
	}
	
	@Test
	void withWithinNext() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(7));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofDays(7), config.withinNext().get());
	}
	
	@Test
	void withWithinNextNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(null));
	}
	
	@Test
	void withDayOfWeek() {
		EnumConstraintConfig<DayOfWeek> dowConfig = EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(dowConfig);
		assertTrue(config.dayOfWeek().isPresent());
		assertEquals(dowConfig, config.dayOfWeek().get());
	}
	
	@Test
	void withDayOfWeekNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(null));
	}
	
	@Test
	void withDayOfMonth() {
		NumericConstraintConfig domConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 15);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(domConfig);
		assertTrue(config.dayOfMonth().isPresent());
		assertEquals(domConfig, config.dayOfMonth().get());
	}
	
	@Test
	void withDayOfMonthNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(null));
	}
	
	@Test
	void withDayOfYear() {
		NumericConstraintConfig doyConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 100);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfYear(doyConfig);
		assertTrue(config.dayOfYear().isPresent());
		assertEquals(doyConfig, config.dayOfYear().get());
	}
	
	@Test
	void withDayOfYearNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfYear(null));
	}
	
	@Test
	void withWeekOfMonth() {
		NumericConstraintConfig womConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 4);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfMonth(womConfig);
		assertTrue(config.weekOfMonth().isPresent());
		assertEquals(womConfig, config.weekOfMonth().get());
	}
	
	@Test
	void withWeekOfMonthNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfMonth(null));
	}
	
	@Test
	void withWeekOfYear() {
		NumericConstraintConfig woyConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 52);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfYear(woyConfig);
		assertTrue(config.weekOfYear().isPresent());
		assertEquals(woyConfig, config.weekOfYear().get());
	}
	
	@Test
	void withWeekOfYearNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWeekOfYear(null));
	}
	
	@Test
	void withMonth() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JUNE, Month.JULY, Month.AUGUST));
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.month().isPresent());
		assertEquals(monthConfig, config.month().get());
	}
	
	@Test
	void withMonthNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(null));
	}
	
	@Test
	void withYear() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(2020, 2030);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.year().isPresent());
		assertEquals(yearConfig, config.year().get());
	}
	
	@Test
	void withYearNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(null));
	}
	
	@Test
	void withHour() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}
	
	@Test
	void withHourNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(null));
	}
	
	@Test
	void withMinute() {
		NumericConstraintConfig minuteConfig = NumericConstraintConfig.UNCONSTRAINED.withIn(List.of(0, 15, 30, 45));
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}
	
	@Test
	void withMinuteNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMinute(null));
	}
	
	@Test
	void withSecond() {
		NumericConstraintConfig secondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}
	
	@Test
	void withSecondNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withSecond(null));
	}
	
	@Test
	void withMillisecond() {
		NumericConstraintConfig millisecondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}
	
	@Test
	void withMillisecondNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(null));
	}
	
	@Test
	void withNanosecond() {
		NumericConstraintConfig nanosecondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}
	
	@Test
	void withNanosecondNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNanosecond(null));
	}
	
	@Test
	void withZone() {
		ZoneIdConstraintConfig zoneConfig = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withZone(zoneConfig);
		assertTrue(config.zone().isPresent());
		assertEquals(zoneConfig, config.zone().get());
	}
	
	@Test
	void withZoneNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withZone(null));
	}
	
	@Test
	void withCustom() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withCustom(dt -> dt.getHour() < 12 ? Result.success() : Result.error("Time must be before noon"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> ZonedDateTimeConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(DT_2024_06_15_12_00);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}
	
	@Test
	void matchesWithIn() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}
	
	@Test
	void matchesWithAfter() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(DT_2024_01_15_10_30);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}
	
	@Test
	void matchesWithBefore() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBefore(DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2025_01_01_00_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
	}
	
	@Test
	void matchesWithBetween() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
	}
	
	@Test
	void matchesWithMonthConstraint() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JUNE, Month.DECEMBER));
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_01_15_10_30).isError());
	}
	
	@Test
	void matchesWithYearConstraint() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(2024);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2025_01_01_00_00).isError());
	}
	
	@Test
	void matchesWithHourConstraint() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 13);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isError());
	}
	
	@Test
	void matchesWithZoneConstraint() {
		ZoneIdConstraintConfig zoneConfig = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withZone(zoneConfig);
		assertTrue(config.matches(DT_2024_06_15_12_00).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Berlin"))).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withAfterOrEqual(DT_2024_01_15_10_30)
			.withBeforeOrEqual(DT_2025_01_01_00_00)
			.withNotIn(List.of(DT_2024_06_15_12_00));
		
		assertTrue(config.matches(DT_2024_01_15_10_30).isSuccess());
		assertTrue(config.matches(DT_2024_12_25_14_30).isSuccess());
		assertTrue(config.matches(DT_2024_06_15_12_00).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
