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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DateFieldConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullDaysOfWeekThrows() {
		assertThrows(NullPointerException.class, () -> new DateFieldConstraintConfig(null, Optional.empty(), Optional.empty(), Optional.empty()));
	}

	@Test
	void constructorNullDayOfMonthThrows() {
		assertThrows(NullPointerException.class, () -> new DateFieldConstraintConfig(Optional.empty(), null, Optional.empty(), Optional.empty()));
	}

	@Test
	void constructorNullMonthsThrows() {
		assertThrows(NullPointerException.class, () -> new DateFieldConstraintConfig(Optional.empty(), Optional.empty(), null, Optional.empty()));
	}

	@Test
	void constructorNullYearThrows() {
		assertThrows(NullPointerException.class, () -> new DateFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), null));
	}

	@Test
	void constructorEmptyDaysOfWeekSetThrows() {
		assertThrows(IllegalArgumentException.class, () -> new DateFieldConstraintConfig(Optional.of(Set.of()), Optional.empty(), Optional.empty(), Optional.empty()));
	}

	@Test
	void constructorEmptyMonthsSetThrows() {
		assertThrows(IllegalArgumentException.class, () -> new DateFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(Set.of()), Optional.empty()));
	}

	@Test
	void constructorValidParameters() {
		DateFieldConstraintConfig config = new DateFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		assertNotNull(config);
		assertEquals(Optional.empty(), config.daysOfWeek());
		assertEquals(Optional.empty(), config.dayOfMonth());
		assertEquals(Optional.empty(), config.months());
		assertEquals(Optional.empty(), config.year());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(DateFieldConstraintConfig.UNCONSTRAINED);
		assertEquals(Optional.empty(), DateFieldConstraintConfig.UNCONSTRAINED.daysOfWeek());
		assertEquals(Optional.empty(), DateFieldConstraintConfig.UNCONSTRAINED.dayOfMonth());
		assertEquals(Optional.empty(), DateFieldConstraintConfig.UNCONSTRAINED.months());
		assertEquals(Optional.empty(), DateFieldConstraintConfig.UNCONSTRAINED.year());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(DateFieldConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForEmpty() {
		DateFieldConstraintConfig config = new DateFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDaysOfWeekConstraint() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithDayOfMonthConstraint() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithMonthsConstraint() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithYearConstraint() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithUnconstrainedFieldConfig() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED);
		assertTrue(config.isUnconstrained());
	}

	// Builder Method Tests

	@Test
	void withDayOfWeek() {
		Set<DayOfWeek> days = Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfWeek(days);

		assertTrue(config.daysOfWeek().isPresent());
		assertEquals(days, config.daysOfWeek().get());
		assertEquals(Optional.empty(), config.dayOfMonth());
		assertEquals(Optional.empty(), config.months());
		assertEquals(Optional.empty(), config.year());
	}

	@Test
	void withDayOfMonth() {
		FieldConstraintConfig dayOfMonthConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(15, true);
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfMonth(dayOfMonthConfig);

		assertEquals(Optional.empty(), config.daysOfWeek());
		assertEquals(Optional.of(dayOfMonthConfig), config.dayOfMonth());
		assertEquals(Optional.empty(), config.months());
		assertEquals(Optional.empty(), config.year());
	}

	@Test
	void withMonth() {
		Set<Month> months = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(months);

		assertEquals(Optional.empty(), config.daysOfWeek());
		assertEquals(Optional.empty(), config.dayOfMonth());
		assertTrue(config.months().isPresent());
		assertEquals(months, config.months().get());
		assertEquals(Optional.empty(), config.year());
	}

	@Test
	void withYear() {
		FieldConstraintConfig yearConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true);
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withYear(yearConfig);

		assertEquals(Optional.empty(), config.daysOfWeek());
		assertEquals(Optional.empty(), config.dayOfMonth());
		assertEquals(Optional.empty(), config.months());
		assertEquals(Optional.of(yearConfig), config.year());
	}

	@Test
	void chainedBuilders() {
		Set<DayOfWeek> days = Set.of(DayOfWeek.MONDAY);
		Set<Month> months = Set.of(Month.JUNE);

		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withDayOfWeek(days)
			.withMonth(months);

		assertEquals(Optional.of(days), config.daysOfWeek());
		assertEquals(Optional.empty(), config.dayOfMonth());
		assertEquals(Optional.of(months), config.months());
		assertEquals(Optional.empty(), config.year());
	}

	// matches(LocalDate) Tests

	@Test
	void matchesLocalDateUnconstrained() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesLocalDateDayOfWeekSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

		// June 17, 2024 is Monday, June 21 is Friday
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 21)).isSuccess());
	}

	@Test
	void matchesLocalDateDayOfWeekFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

		// June 15, 2024 is Saturday
		Result<Void> result = config.matches(LocalDate.of(2024, 6, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalDateDayOfMonthSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true).withMax(20, true));

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 17)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 20)).isSuccess());
	}

	@Test
	void matchesLocalDateDayOfMonthFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 14));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalDateMonthSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 7, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 8, 15)).isSuccess());
	}

	@Test
	void matchesLocalDateMonthFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE, Month.JULY));

		Result<Void> result = config.matches(LocalDate.of(2024, 9, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalDateYearSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));

		assertTrue(config.matches(LocalDate.of(2020, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2022, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2025, 6, 15)).isSuccess());
	}

	@Test
	void matchesLocalDateYearFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));

		Result<Void> result = config.matches(LocalDate.of(2019, 6, 15));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalDateMultipleConstraintsSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true).withMax(2025, true));

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2022, 6, 1)).isSuccess());
	}

	@Test
	void matchesLocalDateMultipleConstraintsFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));

		Result<Void> result = config.matches(LocalDate.of(2024, 7, 15));
		assertTrue(result.isError());
	}

	// matches(LocalDateTime) Tests

	@Test
	void matchesLocalDateTimeSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 30)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 20, 14, 0)).isSuccess());
	}

	@Test
	void matchesLocalDateTimeFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE));

		Result<Void> result = config.matches(LocalDateTime.of(2024, 7, 15, 10, 30));
		assertTrue(result.isError());
	}

	// matches(OffsetDateTime) Tests

	@Test
	void matchesOffsetDateTimeSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));

		ZoneOffset offset = ZoneOffset.ofHours(2);
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 0, 0, 0, offset)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2025, 7, 15, 14, 30, 0, 0, offset)).isSuccess());
	}

	@Test
	void matchesOffsetDateTimeFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));

		ZoneOffset offset = ZoneOffset.ofHours(2);
		Result<Void> result = config.matches(OffsetDateTime.of(2019, 6, 15, 10, 0, 0, 0, offset));
		assertTrue(result.isError());
	}

	// matches(ZonedDateTime) Tests

	@Test
	void matchesZonedDateTimeSuccess() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(20, true));

		ZoneId zone = ZoneId.of("Europe/Paris");
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 10, 30, 0, 0, zone)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 7, 18, 14, 0, 0, 0, zone)).isSuccess());
	}

	@Test
	void matchesZonedDateTimeFailure() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withDayOfMonth(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		ZoneId zone = ZoneId.of("Europe/Paris");
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 9, 10, 30, 0, 0, zone));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("DateFieldConstraintConfig[unconstrained]", DateFieldConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithDaysOfWeek() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withDayOfWeek(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		String str = config.toString();
		assertTrue(str.contains("daysOfWeek"));
		assertTrue(str.contains("MONDAY") || str.contains("FRIDAY"));
	}

	@Test
	void toStringWithMonth() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		String str = config.toString();
		assertTrue(str.contains("months"));
		assertTrue(str.contains("JUNE"));
	}

	@Test
	void toStringWithMultipleConstraints() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED
			.withMonth(Set.of(Month.JUNE))
			.withYear(FieldConstraintConfig.UNCONSTRAINED.withMin(2020, true));
		String str = config.toString();
		assertTrue(str.contains("months"));
		assertTrue(str.contains("year"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		DateFieldConstraintConfig config = DateFieldConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		Set<Month> months = Set.of(Month.JUNE);
		DateFieldConstraintConfig config1 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(months);
		DateFieldConstraintConfig config2 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(months);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentMonth() {
		DateFieldConstraintConfig config1 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JUNE));
		DateFieldConstraintConfig config2 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(Set.of(Month.JULY));
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		Set<Month> months = Set.of(Month.JUNE);
		DateFieldConstraintConfig config1 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(months);
		DateFieldConstraintConfig config2 = DateFieldConstraintConfig.UNCONSTRAINED.withMonth(months);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
