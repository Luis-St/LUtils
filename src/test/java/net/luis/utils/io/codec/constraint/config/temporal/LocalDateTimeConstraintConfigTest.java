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

import net.luis.utils.io.codec.constraint.config.temporal.core.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateTimeConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), null, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullTimeFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullDateFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		LocalDateTimeConstraintConfig config = new LocalDateTimeConstraintConfig(
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
		assertNotNull(LocalDateTimeConstraintConfig.UNCONSTRAINED);
		assertTrue(LocalDateTimeConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(LocalDateTimeConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(LocalDateTimeConstraintConfig.UNCONSTRAINED.timeFieldConfig().isUnconstrained());
		assertTrue(LocalDateTimeConstraintConfig.UNCONSTRAINED.dateFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(LocalDateTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		LocalDateTimeConstraintConfig config = new LocalDateTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMin(LocalDateTime.of(2024, 1, 1, 0, 0), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTimeFieldConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDateFieldConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Time Field Constraint Builder Tests

	@Test
	void withHour() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinute() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withSecond() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMillisecond() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Date Field Constraint Builder Tests

	@Test
	void withDayOfWeek() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withDayOfMonth() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMonth() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withYear() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2020, 1, 1, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2030, 12, 31, 23, 59)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 10, 31));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(LocalDateTime.of(2023, 12, 31, 23, 59));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(LocalDateTime.of(2025, 1, 1, 0, 0));
		assertTrue(result.isError());
	}

	// matches Tests - Time Field Constraints

	@Test
	void matchesHourSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true));
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 12, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 15, 0)).isSuccess());
	}

	@Test
	void matchesHourFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 9, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinuteSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
	}

	@Test
	void matchesMinuteFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 10, 31));
		assertTrue(result.isError());
	}

	// matches Tests - Date Field Constraints

	@Test
	void matchesDayOfWeekSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 17, 2024 is Monday, June 21 is Friday
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 17, 10, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 21, 14, 0)).isSuccess());
	}

	@Test
	void matchesDayOfWeekFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY));
		// June 15, 2024 is Saturday
		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 10, 30));
		assertTrue(result.isError());
	}

	@Test
	void matchesMonthSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 7, 15, 10, 30)).isSuccess());
	}

	@Test
	void matchesMonthFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		Result<Void> result = config.matches(LocalDateTime.of(2024, 7, 15, 10, 30));
		assertTrue(result.isError());
	}

	@Test
	void matchesYearSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2020, 1, 1, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2025, 12, 31, 23, 59)).isSuccess());
	}

	@Test
	void matchesYearFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		Result<Void> result = config.matches(LocalDateTime.of(2019, 6, 15, 10, 30));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTimeAndDateFieldsSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMonth(Set.of(Month.JUNE));

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 12, 0)).isSuccess());
	}

	@Test
	void matchesCombinedTimeAndDateFieldsFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(LocalDateTime.of(2024, 7, 15, 12, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesCombinedAllConstraintTypesSuccess() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(LocalDateTime.of(2024, 1, 1, 0, 0), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE, Month.JULY));

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 12, 0)).isSuccess());
	}

	@Test
	void matchesCombinedAllConstraintTypesFailure() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(LocalDateTime.of(2024, 1, 1, 0, 0), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 18, 0));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("LocalDateTimeConstraintConfig[unconstrained]", LocalDateTimeConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("LocalDateTimeConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithTimeFieldConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
	}

	@Test
	void toStringWithDateFieldConstraint() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		String str = config.toString();
		assertTrue(str.contains("months"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		LocalDateTimeConstraintConfig config = LocalDateTimeConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config1 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalDateTimeConstraintConfig config2 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config1 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMin(value, true);
		LocalDateTimeConstraintConfig config2 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 10, 30);
		LocalDateTimeConstraintConfig config1 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalDateTimeConstraintConfig config2 = LocalDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
