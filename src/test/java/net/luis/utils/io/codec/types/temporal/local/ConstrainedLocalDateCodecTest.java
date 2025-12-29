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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateCodec} focusing on local date constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - Temporal Comparison Constraints (After)

	@Test
	void encodeWithAfterConstraintSuccess() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.after(reference);
		LocalDate value = LocalDate.of(2024, 6, 16);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.after(reference);
		LocalDate value = LocalDate.of(2024, 6, 14);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAfterConstraintBoundary() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.after(reference);
		LocalDate value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Before)

	@Test
	void encodeWithBeforeConstraintSuccess() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.before(reference);
		LocalDate value = LocalDate.of(2024, 6, 14);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.before(reference);
		LocalDate value = LocalDate.of(2024, 6, 16);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Between)

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMinBoundary() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		LocalDate value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMaxBoundary() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		LocalDate value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		LocalDate value = LocalDate.of(2025, 1, 1);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Temporal Comparison Constraints

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-14");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		LocalDate reference = LocalDate.of(2024, 6, 15);
		Codec<LocalDate> codec = LOCAL_DATE.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-16");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		LocalDate min = LocalDate.of(2024, 1, 1);
		LocalDate max = LocalDate.of(2024, 12, 31);
		Codec<LocalDate> codec = LOCAL_DATE.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2025-01-01");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Date Field Constraints (DayOfWeek)

	@Test
	void encodeWithDayOfWeekSingleConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeek(DayOfWeek.MONDAY);
		// June 17, 2024 is Monday
		LocalDate value = LocalDate.of(2024, 6, 17);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfWeekSingleConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeek(DayOfWeek.MONDAY);
		// June 15, 2024 is Saturday
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithDayOfWeekInConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 17, 2024 is Monday, June 21 is Friday
		LocalDate value = LocalDate.of(2024, 6, 17);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfWeekInConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		// June 15, 2024 is Saturday
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithWeekdaysConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(
			DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
		));
		LocalDate value = LocalDate.of(2024, 6, 17); // Monday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithWeekdaysConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(
			DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
		));
		LocalDate value = LocalDate.of(2024, 6, 15); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Date Field Constraints (DayOfWeek)

	@Test
	void decodeWithDayOfWeekConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeek(DayOfWeek.MONDAY);
		JsonPrimitive json = new JsonPrimitive("2024-06-17");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 17), result.resultOrThrow());
	}

	@Test
	void decodeWithDayOfWeekConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeek(DayOfWeek.MONDAY);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithDayOfWeekInConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		JsonPrimitive json = new JsonPrimitive("2024-06-21"); // Friday

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 21), result.resultOrThrow());
	}

	@Test
	void decodeWithDayOfWeekInConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		JsonPrimitive json = new JsonPrimitive("2024-06-15"); // Saturday

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Date Field Constraints (DayOfMonth)

	@Test
	void encodeWithDayOfMonthEqualToConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.equalTo(15));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfMonthEqualToConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.equalTo(15));
		LocalDate value = LocalDate.of(2024, 6, 16);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithDayOfMonthBetweenConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		LocalDate value = LocalDate.of(2024, 6, 10);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfMonthBetweenConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		LocalDate value = LocalDate.of(2024, 6, 20);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithDayOfMonthGreaterThanConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.greaterThan(15));
		LocalDate value = LocalDate.of(2024, 6, 20);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfMonthGreaterThanConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.greaterThan(15));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Date Field Constraints (DayOfMonth)

	@Test
	void decodeWithDayOfMonthEqualToConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.equalTo(15));
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithDayOfMonthEqualToConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.equalTo(15));
		JsonPrimitive json = new JsonPrimitive("2024-06-16");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithDayOfMonthBetweenConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		JsonPrimitive json = new JsonPrimitive("2024-06-10");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 10), result.resultOrThrow());
	}

	@Test
	void decodeWithDayOfMonthBetweenConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		JsonPrimitive json = new JsonPrimitive("2024-06-20");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Date Field Constraints (Month)

	@Test
	void encodeWithMonthSingleConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.month(Month.JUNE);
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMonthSingleConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.month(Month.JUNE);
		LocalDate value = LocalDate.of(2024, 7, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMonthInConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		LocalDate value = LocalDate.of(2024, 7, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMonthInConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		LocalDate value = LocalDate.of(2024, 9, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithSummerMonthsConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSummerMonthsConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		LocalDate value = LocalDate.of(2024, 12, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Date Field Constraints (Month)

	@Test
	void decodeWithMonthConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithMonthConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-07-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMonthInConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		JsonPrimitive json = new JsonPrimitive("2024-07-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 7, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithMonthInConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		JsonPrimitive json = new JsonPrimitive("2024-09-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Date Field Constraints (Year)

	@Test
	void encodeWithYearEqualToConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.equalTo(2024));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithYearEqualToConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.equalTo(2024));
		LocalDate value = LocalDate.of(2025, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithYearBetweenConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.betweenOrEqual(2020, 2025));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithYearBetweenConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.betweenOrEqual(2020, 2025));
		LocalDate value = LocalDate.of(2026, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithYearGreaterThanOrEqualConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.greaterThanOrEqual(2020));
		LocalDate value = LocalDate.of(2024, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithYearGreaterThanOrEqualConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.greaterThanOrEqual(2020));
		LocalDate value = LocalDate.of(2019, 6, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Date Field Constraints (Year)

	@Test
	void decodeWithYearEqualToConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.equalTo(2024));
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithYearEqualToConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.equalTo(2024));
		JsonPrimitive json = new JsonPrimitive("2025-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithYearBetweenConstraintSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithYearBetweenConstraintFailure() {
		Codec<LocalDate> codec = LOCAL_DATE.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2026-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE
			.afterOrEqual(LocalDate.of(2024, 1, 1))
			.beforeOrEqual(LocalDate.of(2024, 12, 31))
			.dayOfWeek(DayOfWeek.MONDAY);
		LocalDate value = LocalDate.of(2024, 6, 17); // Monday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<LocalDate> codec = LOCAL_DATE
			.afterOrEqual(LocalDate.of(2024, 1, 1))
			.beforeOrEqual(LocalDate.of(2024, 12, 31))
			.dayOfWeek(DayOfWeek.MONDAY);
		LocalDate value = LocalDate.of(2024, 6, 15); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE
			.afterOrEqual(LocalDate.of(2024, 1, 1))
			.beforeOrEqual(LocalDate.of(2024, 12, 31))
			.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<LocalDate> codec = LOCAL_DATE
			.afterOrEqual(LocalDate.of(2024, 1, 1))
			.beforeOrEqual(LocalDate.of(2024, 12, 31))
			.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2025-06-15");

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMultipleFieldConstraintsSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE
			.month(Month.JUNE)
			.dayOfMonth(c -> c.betweenOrEqual(1, 15))
			.year(c -> c.equalTo(2024));
		LocalDate value = LocalDate.of(2024, 6, 10);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMultipleFieldConstraintsFailure() {
		Codec<LocalDate> codec = LOCAL_DATE
			.month(Month.JUNE)
			.dayOfMonth(c -> c.betweenOrEqual(1, 15))
			.year(c -> c.equalTo(2024));
		LocalDate value = LocalDate.of(2024, 6, 20);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMultipleFieldConstraintsSuccess() {
		Codec<LocalDate> codec = LOCAL_DATE
			.monthIn(Set.of(Month.JUNE, Month.JULY))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
			.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2024-06-17"); // Monday

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2024, 6, 17), result.resultOrThrow());
	}

	@Test
	void decodeWithMultipleFieldConstraintsFailure() {
		Codec<LocalDate> codec = LOCAL_DATE
			.monthIn(Set.of(Month.JUNE, Month.JULY))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
			.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2024-06-15"); // Saturday

		Result<LocalDate> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDate> codec = LOCAL_DATE;
		String str = codec.toString();
		assertEquals("LocalDateCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<LocalDate> codec = LOCAL_DATE.month(Month.JUNE);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedLocalDateCodec["));
		assertTrue(str.contains("constraints="));
	}
}
