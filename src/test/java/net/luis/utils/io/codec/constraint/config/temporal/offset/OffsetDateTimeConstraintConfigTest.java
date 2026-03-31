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

package net.luis.utils.io.codec.constraint.config.temporal.offset;

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertFalse(config.isUnconstrained());
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
		NumericConstraintConfig domConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 15);
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
		NumericConstraintConfig doyConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 100);
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
		NumericConstraintConfig womConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 4);
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
		NumericConstraintConfig woyConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 52);
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
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(2020, 2030);
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
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
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
		NumericConstraintConfig minuteConfig = NumericConstraintConfig.UNCONSTRAINED.withIn(List.of(0, 15, 30, 45));
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
		NumericConstraintConfig secondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
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
		NumericConstraintConfig millisecondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
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
		NumericConstraintConfig nanosecondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
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
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withCustom(dt -> {
			if (dt.getHour() >= 12) throw new ConstraintViolateException("Time must be before noon");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> OffsetDateTimeConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEqualTo(DT_2024_06_15_12_00);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_01_15_10_30));
	}
	
	@Test
	void validateWithNotEqualTo() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(DT_2024_06_15_12_00);
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void validateWithIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_12_25_14_30));
	}
	
	@Test
	void validateWithNotIn() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(DT_2024_01_15_10_30, DT_2024_06_15_12_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_01_15_10_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void validateWithAfter() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfter(DT_2024_01_15_10_30);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_01_15_10_30));
	}
	
	@Test
	void validateWithAfterOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(DT_2024_01_15_10_30);
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void validateWithBefore() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBefore(DT_2025_01_01_00_00);
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2025_01_01_00_00));
	}
	
	@Test
	void validateWithBeforeOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(DT_2025_01_01_00_00);
		assertDoesNotThrow(() -> config.validate(DT_2025_01_01_00_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
	}
	
	@Test
	void validateWithBetween() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetween(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_01_15_10_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2025_01_01_00_00));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(DT_2024_01_15_10_30, DT_2025_01_01_00_00);
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertDoesNotThrow(() -> config.validate(DT_2025_01_01_00_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void validateWithMonthConstraint() {
		EnumConstraintConfig<Month> monthConfig = EnumConstraintConfig.<Month>unconstrained().withIn(List.of(Month.JUNE, Month.DECEMBER));
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(monthConfig);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_01_15_10_30));
	}
	
	@Test
	void validateWithYearConstraint() {
		NumericConstraintConfig yearConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(2024);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(yearConfig);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2025_01_01_00_00));
	}
	
	@Test
	void validateWithHourConstraint() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 13);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_12_25_14_30));
	}
	
	@Test
	void validateWithOffsetConstraint() {
		ZoneOffsetConstraintConfig offsetConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withOffset(offsetConfig);
		assertDoesNotThrow(() -> config.validate(DT_2024_06_15_12_00));
		assertThrows(ConstraintViolateException.class, () -> config.validate(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2))));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withAfterOrEqual(DT_2024_01_15_10_30)
			.withBeforeOrEqual(DT_2025_01_01_00_00)
			.withNotIn(List.of(DT_2024_06_15_12_00));
		
		assertDoesNotThrow(() -> config.validate(DT_2024_01_15_10_30));
		assertDoesNotThrow(() -> config.validate(DT_2024_12_25_14_30));
		assertThrows(ConstraintViolateException.class, () -> config.validate(DT_2024_06_15_12_00));
	}
	
	@Test
	void validateWithNullValue() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
