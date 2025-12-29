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

class LocalDateConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateConstraintConfig(TemporalConstraintConfig.unconstrained(), null, DateFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullDateFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalDateConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		LocalDateConstraintConfig config = new LocalDateConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertNotNull(config);
		assertNotNull(config.config());
		assertNotNull(config.spanConfig());
		assertNotNull(config.dateFieldConfig());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(LocalDateConstraintConfig.UNCONSTRAINED);
		assertTrue(LocalDateConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(LocalDateConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(LocalDateConstraintConfig.UNCONSTRAINED.dateFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(LocalDateConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		LocalDateConstraintConfig config = new LocalDateConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			DateFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMin(LocalDate.of(2024, 1, 1), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDateFieldConstraint() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		LocalDate max = LocalDate.of(2024, 12, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofDays(7));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofDays(30));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Date Field Constraint Builder Tests

	@Test
	void withDayOfWeek() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withDayOfMonth() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMonth() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withYear() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(LocalDate.of(2024, 6, 16));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(LocalDate.of(2023, 12, 31));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(LocalDate.of(2025, 1, 1));
		assertTrue(result.isError());
	}

	// matches Tests - Date Field Constraints

	@Test
	void matchesDayOfWeekSuccess() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 17, 2024 is Monday, June 21 is Friday
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 21)).isSuccess());
	}

	@Test
	void matchesDayOfWeekFailure() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY));
		// June 15, 2024 is Saturday
		Result<Void> result = config.matches(LocalDate.of(2024, 6, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesMonthSuccess() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 7, 15)).isSuccess());
	}

	@Test
	void matchesMonthFailure() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		Result<Void> result = config.matches(LocalDate.of(2024, 7, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesYearSuccess() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2025, 12, 31)).isSuccess());
	}

	@Test
	void matchesYearFailure() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		Result<Void> result = config.matches(LocalDate.of(2019, 6, 15));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTemporalAndFieldSuccess() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED
			.withMin(LocalDate.of(2024, 1, 1), true)
			.withMonth(Set.of(Month.JUNE, Month.JULY));

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 7, 20)).isSuccess());
	}

	@Test
	void matchesCombinedTemporalAndFieldFailure() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED
			.withMin(LocalDate.of(2024, 1, 1), true)
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(LocalDate.of(2024, 7, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesMultipleFieldConstraintsSuccess() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
	}

	@Test
	void matchesMultipleFieldConstraintsFailure() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));

		Result<Void> result = config.matches(LocalDate.of(2026, 6, 15));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("LocalDateConstraintConfig[unconstrained]", LocalDateConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("LocalDateConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithFieldConstraint() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		String str = config.toString();
		assertTrue(str.contains("months"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		LocalDateConstraintConfig config = LocalDateConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config1 = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalDateConstraintConfig config2 = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config1 = LocalDateConstraintConfig.UNCONSTRAINED.withMin(value, true);
		LocalDateConstraintConfig config2 = LocalDateConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		LocalDate value = LocalDate.of(2024, 6, 15);
		LocalDateConstraintConfig config1 = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalDateConstraintConfig config2 = LocalDateConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
