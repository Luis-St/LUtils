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

package net.luis.utils.io.codec.types.time.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateTimeCodec} focusing on local date time constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateTimeCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - Temporal Comparison Constraints

	@Test
	void encodeWithAfterConstraintSuccess() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.after(reference);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 13, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.after(reference);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 11, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBeforeConstraintSuccess() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.before(reference);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 11, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.before(reference);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 13, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.betweenOrEqual(min, max);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.betweenOrEqual(min, max);
		LocalDateTime value = LocalDateTime.of(2025, 1, 1, 0, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Temporal Comparison Constraints

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T11:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		LocalDateTime reference = LocalDateTime.of(2024, 6, 15, 12, 0);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T13:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		LocalDateTime min = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime max = LocalDateTime.of(2024, 12, 31, 23, 59);
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2025-01-01T00:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints

	@Test
	void encodeWithHourConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 18, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMinuteConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.minute(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinuteConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.minute(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 30);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithSecondConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.second(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 30, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSecondConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.second(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 30, 30);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMillisecondConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.millisecond(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 30, 45, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMillisecondConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.millisecond(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 30, 45, 500_000_000);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints

	@Test
	void decodeWithHourConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithHourConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T18:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMinuteConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMinuteConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:30:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Date Field Constraints

	@Test
	void encodeWithDayOfWeekConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 12, 0); // Monday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfWeekConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithDayOfWeekInConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 12, 0); // Monday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfWeekInConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithDayOfMonthConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		LocalDateTime value = LocalDateTime.of(2024, 6, 10, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDayOfMonthConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		LocalDateTime value = LocalDateTime.of(2024, 6, 20, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMonthConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMonthConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 7, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMonthInConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.monthIn(Set.of(Month.JUNE, Month.JULY));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMonthInConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.monthIn(Set.of(Month.JUNE, Month.JULY));
		LocalDateTime value = LocalDateTime.of(2024, 8, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithYearConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.year(c -> c.equalTo(2024));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithYearConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.year(c -> c.equalTo(2024));
		LocalDateTime value = LocalDateTime.of(2025, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Date Field Constraints

	@Test
	void decodeWithDayOfWeekConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		JsonPrimitive json = new JsonPrimitive("2024-06-17T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 17, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithDayOfWeekConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMonthConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMonthConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-07-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithYearConstraintSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithYearConstraintFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.year(c -> c.betweenOrEqual(2020, 2025));
		JsonPrimitive json = new JsonPrimitive("2026-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests - Temporal + Time Field

	@Test
	void encodeWithTemporalAndTimeFieldConstraintsSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithTemporalAndTimeFieldConstraintsFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 18, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests - Temporal + Date Field

	@Test
	void encodeWithTemporalAndDateFieldConstraintsSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithTemporalAndDateFieldConstraintsFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 7, 15, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests - Time Field + Date Field

	@Test
	void encodeWithTimeAndDateFieldConstraintsSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeek(DayOfWeek.MONDAY);
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 12, 0); // Monday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithTimeAndDateFieldConstraintsFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeek(DayOfWeek.MONDAY);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests - All Constraint Types Combined

	@Test
	void encodeWithAllConstraintTypesSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 12, 0); // Monday, June, 12:00

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAllConstraintTypesHourFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 18, 0); // Invalid hour

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAllConstraintTypesMinuteFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 12, 30); // Invalid minute

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAllConstraintTypesDayOfWeekFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0); // Invalid day (Saturday)

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAllConstraintTypesMonthFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.beforeOrEqual(LocalDateTime.of(2024, 12, 31, 23, 59))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		LocalDateTime value = LocalDateTime.of(2024, 7, 15, 12, 0); // Invalid month (not Monday in July)

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Chained Constraints

	@Test
	void decodeWithAllConstraintTypesSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.hour(c -> c.betweenOrEqual(9, 17))
			.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00");

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 15, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithAllConstraintTypesFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.afterOrEqual(LocalDateTime.of(2024, 1, 1, 0, 0))
			.hour(c -> c.betweenOrEqual(9, 17))
			.month(Month.JUNE);
		JsonPrimitive json = new JsonPrimitive("2024-07-15T12:00:00"); // Wrong month

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Complex Business Rule Tests

	@Test
	void encodeWithBusinessHoursWeekdaysConstraintSuccess() {
		// Business hours: Monday-Friday, 9 AM - 5 PM, on the hour
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 14, 0); // Monday, 2 PM

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBusinessHoursWeekdaysConstraintWeekendFailure() {
		// Business hours: Monday-Friday, 9 AM - 5 PM
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
			.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 14, 0); // Saturday

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBusinessHoursWeekdaysConstraintOutsideHoursFailure() {
		// Business hours: Monday-Friday, 9 AM - 5 PM
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
			.hour(c -> c.betweenOrEqual(9, 17));
		LocalDateTime value = LocalDateTime.of(2024, 6, 17, 18, 0); // Monday, but 6 PM

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Multiple Time Field Constraints

	@Test
	void encodeWithMultipleTimeFieldConstraintsSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMultipleTimeFieldConstraintsFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		LocalDateTime value = LocalDateTime.of(2024, 6, 15, 12, 0, 30); // Invalid second

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Multiple Date Field Constraints

	@Test
	void encodeWithMultipleDateFieldConstraintsSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.month(Month.JUNE)
			.dayOfMonth(c -> c.betweenOrEqual(1, 15))
			.year(c -> c.equalTo(2024));
		LocalDateTime value = LocalDateTime.of(2024, 6, 10, 12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMultipleDateFieldConstraintsFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.month(Month.JUNE)
			.dayOfMonth(c -> c.betweenOrEqual(1, 15))
			.year(c -> c.equalTo(2024));
		LocalDateTime value = LocalDateTime.of(2024, 6, 20, 12, 0); // Invalid day (20 > 15)

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Complex Combinations

	@Test
	void decodeWithComplexCombinationSuccess() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
			.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("2024-06-17T12:00:00"); // Monday in June at noon

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2024, 6, 17, 12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithComplexCombinationFailure() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME
			.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY))
			.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00"); // Saturday (wrong day)

		Result<LocalDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME;
		String str = codec.toString();
		assertEquals("LocalDateTimeCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<LocalDateTime> codec = LOCAL_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedLocalDateTimeCodec["));
		assertTrue(str.contains("constraints="));
	}
}
