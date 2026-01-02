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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TimeFieldConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullHourThrows() {
		assertThrows(NullPointerException.class, () -> new TimeFieldConstraintConfig(null, Optional.empty(), Optional.empty(), Optional.empty()));
	}

	@Test
	void constructorNullMinuteThrows() {
		assertThrows(NullPointerException.class, () -> new TimeFieldConstraintConfig(Optional.empty(), null, Optional.empty(), Optional.empty()));
	}

	@Test
	void constructorNullSecondThrows() {
		assertThrows(NullPointerException.class, () -> new TimeFieldConstraintConfig(Optional.empty(), Optional.empty(), null, Optional.empty()));
	}

	@Test
	void constructorNullMillisecondThrows() {
		assertThrows(NullPointerException.class, () -> new TimeFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), null));
	}

	@Test
	void constructorValidParameters() {
		TimeFieldConstraintConfig config = new TimeFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		assertNotNull(config);
		assertEquals(Optional.empty(), config.hour());
		assertEquals(Optional.empty(), config.minute());
		assertEquals(Optional.empty(), config.second());
		assertEquals(Optional.empty(), config.millisecond());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(TimeFieldConstraintConfig.UNCONSTRAINED);
		assertEquals(Optional.empty(), TimeFieldConstraintConfig.UNCONSTRAINED.hour());
		assertEquals(Optional.empty(), TimeFieldConstraintConfig.UNCONSTRAINED.minute());
		assertEquals(Optional.empty(), TimeFieldConstraintConfig.UNCONSTRAINED.second());
		assertEquals(Optional.empty(), TimeFieldConstraintConfig.UNCONSTRAINED.millisecond());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(TimeFieldConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForEmpty() {
		TimeFieldConstraintConfig config = new TimeFieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithHourConstraint() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithMinuteConstraint() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMin(30, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSecondConstraint() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMax(45, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithMillisecondConstraint() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithUnconstrainedFieldConfig() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED);
		assertTrue(config.isUnconstrained());
	}

	// Builder Method Tests

	@Test
	void withHour() {
		FieldConstraintConfig hourConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(hourConfig);

		assertEquals(Optional.of(hourConfig), config.hour());
		assertEquals(Optional.empty(), config.minute());
		assertEquals(Optional.empty(), config.second());
		assertEquals(Optional.empty(), config.millisecond());
	}

	@Test
	void withMinute() {
		FieldConstraintConfig minuteConfig = FieldConstraintConfig.UNCONSTRAINED.withMax(30, true);
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);

		assertEquals(Optional.empty(), config.hour());
		assertEquals(Optional.of(minuteConfig), config.minute());
		assertEquals(Optional.empty(), config.second());
		assertEquals(Optional.empty(), config.millisecond());
	}

	@Test
	void withSecond() {
		FieldConstraintConfig secondConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(15, true);
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);

		assertEquals(Optional.empty(), config.hour());
		assertEquals(Optional.empty(), config.minute());
		assertEquals(Optional.of(secondConfig), config.second());
		assertEquals(Optional.empty(), config.millisecond());
	}

	@Test
	void withMillisecond() {
		FieldConstraintConfig millisecondConfig = FieldConstraintConfig.UNCONSTRAINED.withMax(999, true);
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);

		assertEquals(Optional.empty(), config.hour());
		assertEquals(Optional.empty(), config.minute());
		assertEquals(Optional.empty(), config.second());
		assertEquals(Optional.of(millisecondConfig), config.millisecond());
	}

	@Test
	void chainedBuilders() {
		FieldConstraintConfig hourConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		FieldConstraintConfig minuteConfig = FieldConstraintConfig.UNCONSTRAINED.withMax(30, true);

		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(hourConfig)
			.withMinute(minuteConfig);

		assertEquals(Optional.of(hourConfig), config.hour());
		assertEquals(Optional.of(minuteConfig), config.minute());
		assertEquals(Optional.empty(), config.second());
		assertEquals(Optional.empty(), config.millisecond());
	}

	// matches(LocalTime) Tests

	@Test
	void matchesLocalTimeUnconstrained() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(LocalTime.of(10, 30, 45, 500_000_000)).isSuccess());
		assertTrue(config.matches(LocalTime.of(0, 0, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(23, 59, 59, 999_000_000)).isSuccess());
	}

	@Test
	void matchesLocalTimeHourSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		assertTrue(config.matches(LocalTime.of(10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(15, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(23, 0, 0)).isSuccess());
	}

	@Test
	void matchesLocalTimeHourFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		Result<Void> result = config.matches(LocalTime.of(9, 0, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalTimeMinuteSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		assertTrue(config.matches(LocalTime.of(10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 15, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30, 0)).isSuccess());
	}

	@Test
	void matchesLocalTimeMinuteFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		Result<Void> result = config.matches(LocalTime.of(10, 31, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalTimeSecondSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true).withMax(45, true));

		assertTrue(config.matches(LocalTime.of(10, 30, 15)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30, 30)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30, 45)).isSuccess());
	}

	@Test
	void matchesLocalTimeSecondFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true).withMax(45, true));

		Result<Void> result = config.matches(LocalTime.of(10, 30, 50));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalTimeMillisecondSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));

		assertTrue(config.matches(LocalTime.of(10, 30, 45, 500_000_000)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30, 45, 750_000_000)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30, 45, 999_000_000)).isSuccess());
	}

	@Test
	void matchesLocalTimeMillisecondFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));

		Result<Void> result = config.matches(LocalTime.of(10, 30, 45, 499_000_000));
		assertTrue(result.isError());
	}

	@Test
	void matchesLocalTimeMultipleConstraintsSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		assertTrue(config.matches(LocalTime.of(10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(12, 15, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(15, 30, 0)).isSuccess());
	}

	@Test
	void matchesLocalTimeMultipleConstraintsFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		Result<Void> result = config.matches(LocalTime.of(16, 0, 0));
		assertTrue(result.isError());
	}

	// matches(LocalDateTime) Tests

	@Test
	void matchesLocalDateTimeSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 10, 0, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 15, 15, 0)).isSuccess());
		assertTrue(config.matches(LocalDateTime.of(2024, 6, 15, 23, 30, 0)).isSuccess());
	}

	@Test
	void matchesLocalDateTimeFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		Result<Void> result = config.matches(LocalDateTime.of(2024, 6, 15, 9, 0, 0));
		assertTrue(result.isError());
	}

	// matches(OffsetTime) Tests

	@Test
	void matchesOffsetTimeSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		ZoneOffset offset = ZoneOffset.ofHours(1);
		assertTrue(config.matches(OffsetTime.of(10, 0, 0, 0, offset)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(15, 30, 0, 0, offset)).isSuccess());
	}

	@Test
	void matchesOffsetTimeFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));

		ZoneOffset offset = ZoneOffset.ofHours(1);
		Result<Void> result = config.matches(OffsetTime.of(9, 0, 0, 0, offset));
		assertTrue(result.isError());
	}

	// matches(OffsetDateTime) Tests

	@Test
	void matchesOffsetDateTimeSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		ZoneOffset offset = ZoneOffset.ofHours(2);
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 10, 0, 0, 0, offset)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 15, 15, 0, 0, offset)).isSuccess());
		assertTrue(config.matches(OffsetDateTime.of(2024, 6, 15, 20, 30, 0, 0, offset)).isSuccess());
	}

	@Test
	void matchesOffsetDateTimeFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		ZoneOffset offset = ZoneOffset.ofHours(2);
		Result<Void> result = config.matches(OffsetDateTime.of(2024, 6, 15, 10, 31, 0, 0, offset));
		assertTrue(result.isError());
	}

	// matches(ZonedDateTime) Tests

	@Test
	void matchesZonedDateTimeSuccess() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true).withMax(45, true));

		ZoneId zone = ZoneId.of("Europe/Paris");
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 10, 30, 15, 0, zone)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 15, 30, 30, 0, zone)).isSuccess());
		assertTrue(config.matches(ZonedDateTime.of(2024, 6, 15, 20, 30, 45, 0, zone)).isSuccess());
	}

	@Test
	void matchesZonedDateTimeFailure() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true).withMax(45, true));

		ZoneId zone = ZoneId.of("Europe/Paris");
		Result<Void> result = config.matches(ZonedDateTime.of(2024, 6, 15, 10, 30, 50, 0, zone));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("TimeFieldConstraintConfig[unconstrained]", TimeFieldConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithHour() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
		assertTrue(str.contains("min=10"));
	}

	@Test
	void toStringWithMinute() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		String str = config.toString();
		assertTrue(str.contains("minute"));
		assertTrue(str.contains("max=30"));
	}

	@Test
	void toStringWithMultipleConstraints() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
		assertTrue(str.contains("minute"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		TimeFieldConstraintConfig config = TimeFieldConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		FieldConstraintConfig hourConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		TimeFieldConstraintConfig config1 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		TimeFieldConstraintConfig config2 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentHour() {
		TimeFieldConstraintConfig config1 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		TimeFieldConstraintConfig config2 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		FieldConstraintConfig hourConfig = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		TimeFieldConstraintConfig config1 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		TimeFieldConstraintConfig config2 = TimeFieldConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
