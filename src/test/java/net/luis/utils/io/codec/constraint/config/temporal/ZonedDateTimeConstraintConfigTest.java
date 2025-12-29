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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ZonedDateTimeConstraintConfigTest {

	private static final ZoneId ZONE = ZoneId.of("Europe/Paris");

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new ZonedDateTimeConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new ZonedDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), null, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullTimeFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new ZonedDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullDateFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new ZonedDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		ZonedDateTimeConstraintConfig config = new ZonedDateTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertNotNull(config);
		assertNotNull(config.config());
		assertNotNull(config.spanConfig());
		assertNotNull(config.timeFieldConfig());
		assertNotNull(config.dateFieldConfig());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(ZonedDateTimeConstraintConfig.UNCONSTRAINED);
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.timeFieldConfig().isUnconstrained());
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.dateFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(ZonedDateTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		ZonedDateTimeConstraintConfig config = new ZonedDateTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTimeFieldConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDateFieldConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		ZonedDateTime max = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTime max = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Time Field Constraint Builder Tests

	@Test
	void withHour() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinute() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withSecond() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMillisecond() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).withMax(500, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Date Field Constraint Builder Tests

	@Test
	void withDayOfWeek() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withDayOfMonth() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMonth() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withYear() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2030, 12, 31, 23, 59, 59, 0, ZONE)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 12, 31, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(ZonedDateTime.of(2023, 12, 31, 23, 59, 59, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTime max = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		ZonedDateTime min = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		ZonedDateTime max = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	// matches Tests - Time Field Constraints

	@Test
	void matchesHourSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true));
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 9, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 17, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesHourFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true));
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 8, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinuteSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).withMax(30, true));
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 15, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesMinuteFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).withMax(30, true));
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 12, 45, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	// matches Tests - Date Field Constraints

	@Test
	void matchesDayOfWeekSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 17, 2024 is Monday, June 21 is Friday
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 17, 12, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesDayOfWeekFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY));
		// June 15, 2024 is Saturday
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesMonthSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesMonthFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesYearSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2025, 12, 31, 23, 59, 59, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesYearFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		Result<Void> result = config.matches(ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTemporalAndTimeFieldSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true));

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesCombinedTemporalAndTimeFieldFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true));

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 8, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesCombinedTemporalAndDateFieldSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withMonth(Set.of(Month.JUNE, Month.JULY));

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 7, 20, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesCombinedTemporalAndDateFieldFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesCombinedTimeFieldAndDateFieldSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE));

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesCombinedTimeFieldAndDateFieldFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	@Test
	void matchesCombinedAllConstraintTypesSuccess() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE, Month.JULY));

		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE)).isSuccess());
	}

	@Test
	void matchesCombinedAllConstraintTypesFailure() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(ZonedDateTime.of(2024, 7, 15, 12, 0, 0, 0, ZONE));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("ZonedDateTimeConstraintConfig[unconstrained]", ZonedDateTimeConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("ZonedDateTimeConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithTimeFieldConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
	}

	@Test
	void toStringWithDateFieldConstraint() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		String str = config.toString();
		assertTrue(str.contains("months"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		ZonedDateTimeConstraintConfig config = ZonedDateTimeConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config1 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		ZonedDateTimeConstraintConfig config2 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config1 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMin(value, true);
		ZonedDateTimeConstraintConfig config2 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		ZonedDateTimeConstraintConfig config1 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		ZonedDateTimeConstraintConfig config2 = ZonedDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
