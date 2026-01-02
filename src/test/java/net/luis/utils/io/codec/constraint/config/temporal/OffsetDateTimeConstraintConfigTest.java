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

class OffsetDateTimeConstraintConfigTest {

	private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetDateTimeConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), null, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullTimeFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullDateFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetDateTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		OffsetDateTimeConstraintConfig config = new OffsetDateTimeConstraintConfig(
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
		assertNotNull(OffsetDateTimeConstraintConfig.UNCONSTRAINED);
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.timeFieldConfig().isUnconstrained());
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.dateFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(OffsetDateTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		OffsetDateTimeConstraintConfig config = new OffsetDateTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMin(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTimeFieldConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDateFieldConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		OffsetDateTime max = OffsetDateTime.of(2024, 12, 31, 23, 59, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTime max = OffsetDateTime.of(2024, 12, 31, 23, 59, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Time Field Constraint Builder Tests

	@Test
	void withHour() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinute() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withSecond() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMillisecond() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Date Field Constraint Builder Tests

	@Test
	void withDayOfWeek() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withDayOfMonth() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMonth() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withYear() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2030, 12, 31, 23, 59, 0, 0, OFFSET)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 10, 31, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(OffsetDateTime.of(2023, 12, 31, 23, 59, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTime max = OffsetDateTime.of(2024, 12, 31, 23, 59, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		OffsetDateTime min = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		OffsetDateTime max = OffsetDateTime.of(2024, 12, 31, 23, 59, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(OffsetDateTime.of(2025, 1, 1, 0, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// matches Tests - Time Field Constraints

	@Test
	void matchesHourSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true));
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 12, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 15, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesHourFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 9, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinuteSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMinuteFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 10, 31, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// matches Tests - Date Field Constraints

	@Test
	void matchesDayOfWeekSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 17, 2024 is Monday, June 21 is Friday
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 17, 10, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 21, 14, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesDayOfWeekFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY));
		// June 15, 2024 is Saturday
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMonthSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 7, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMonthFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 7, 15, 10, 30, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesYearSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2025, 12, 31, 23, 59, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesYearFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		Result<Void> result = config.matches(OffsetDateTime.of(2019, 6, 15, 10, 30, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTimeAndDateFieldsSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMonth(Set.of(Month.JUNE));

		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesCombinedTimeAndDateFieldsFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(OffsetDateTime.of(2024, 7, 15, 12, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesCombinedAllConstraintTypesSuccess() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE, Month.JULY));

		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesCombinedAllConstraintTypesFailure() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED
			.withMin(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(9, true).withMax(17, true))
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 18, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("OffsetDateTimeConstraintConfig[unconstrained]", OffsetDateTimeConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("OffsetDateTimeConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithTimeFieldConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
	}

	@Test
	void toStringWithDateFieldConstraint() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		String str = config.toString();
		assertTrue(str.contains("months"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		OffsetDateTimeConstraintConfig config = OffsetDateTimeConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config1 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		OffsetDateTimeConstraintConfig config2 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config1 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMin(value, true);
		OffsetDateTimeConstraintConfig config2 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 10, 30, 0, 0, OFFSET);
		OffsetDateTimeConstraintConfig config1 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		OffsetDateTimeConstraintConfig config2 = OffsetDateTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
