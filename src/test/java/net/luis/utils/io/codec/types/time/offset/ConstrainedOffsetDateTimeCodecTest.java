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

package net.luis.utils.io.codec.types.time.offset;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OffsetDateTimeCodec} focusing on offset date time constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetDateTimeCodecTest {
	
	private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	//region Temporal Comparison Constraints - Encode
	@Test
	void encodeAfterSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.after(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeAfterFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.after(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeAfterOrEqualSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.afterOrEqual(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBeforeSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.before(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBeforeFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.before(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeBeforeOrEqualSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.beforeOrEqual(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBetweenSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.between(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBetweenFailureTooEarly() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.between(OffsetDateTime.of(2024, 6, 15, 0, 0, 0, 0, OFFSET), OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBetweenFailureTooLate() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.between(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), OffsetDateTime.of(2024, 6, 15, 23, 59, 59, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 12, 31, 0, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBetweenOrEqualSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.betweenOrEqual(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET), OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	//endregion
	
	//region Temporal Comparison Constraints - Decode
	@Test
	void decodeAfterSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.after(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isSuccess());
		assertEquals(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeAfterFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.after(OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-01-01T00:00:00+02:00"));
		assertTrue(result.isError());
	}

	@Test
	void decodeBeforeSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.before(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeBeforeFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.before(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isError());
	}

	@Test
	void decodeBetweenSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.between(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET), OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeBetweenFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.between(OffsetDateTime.of(2024, 6, 15, 0, 0, 0, 0, OFFSET), OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-01-01T00:00:00+02:00"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Time Field Constraints - Encode
	@Test
	void encodeHourConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeHourConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 20, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMinuteConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.minute(c -> c.equalTo(0));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMinuteConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.minute(c -> c.equalTo(0));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeSecondConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.second(c -> c.lessThan(30));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 15, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeSecondConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.second(c -> c.lessThan(30));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMillisecondConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.millisecond(c -> c.equalTo(0));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMillisecondConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.millisecond(c -> c.equalTo(0));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 500_000_000, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region Time Field Constraints - Decode
	@Test
	void decodeHourConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeHourConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T20:30:45+02:00"));
		assertTrue(result.isError());
	}

	@Test
	void decodeMinuteConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.minute(c -> c.equalTo(0));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:00:00+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeMinuteConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.minute(c -> c.equalTo(0));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:00+02:00"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Date Field Constraints - Encode
	@Test
	void encodeDayOfWeekConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 17, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfWeekConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeDayOfWeekInConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 17, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfWeekInConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMonthConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.month(Month.JUNE);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMonthConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.month(Month.DECEMBER);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMonthInConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMonthInConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.monthIn(Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeDayOfMonthConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 10, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfMonthConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 20, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeYearConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.year(c -> c.equalTo(2024));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeYearConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.year(c -> c.equalTo(2024));
		OffsetDateTime value = OffsetDateTime.of(2023, 6, 15, 12, 30, 45, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region Date Field Constraints - Decode
	@Test
	void decodeDayOfWeekConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-17T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeDayOfWeekConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isError());
	}

	@Test
	void decodeMonthConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.month(Month.JUNE);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeMonthConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.month(Month.DECEMBER);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00"));
		assertTrue(result.isError());
	}

	@Test
	void decodeDayOfMonthConstraintSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-10T12:30:45+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeDayOfMonthConstraintFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-20T12:30:45+02:00"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Combined Constraints
	@Test
	void encodeWithAllConstraintTypesSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.afterOrEqual(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 17, 12, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithAllConstraintTypesFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.afterOrEqual(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 0, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithAllConstraintTypesSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.afterOrEqual(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-17T12:00:00+02:00"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithAllConstraintTypesFailure() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.afterOrEqual(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetDateTime.of(2024, 12, 31, 23, 59, 59, 0, OFFSET))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		Result<OffsetDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:00:00+02:00"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Business Logic Scenarios
	@Test
	void encodeBusinessHoursOnWeekdaysSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 17, 14, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBusinessHoursOnWeekdaysFailureWeekend() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 14, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBusinessHoursOnWeekdaysFailureOutsideHours() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 17, 20, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeFirstQuarterSuccess() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		OffsetDateTime value = OffsetDateTime.of(2024, 2, 15, 12, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeFirstQuarterFailureWrongMonth() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		OffsetDateTime value = OffsetDateTime.of(2024, 6, 15, 12, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeFirstQuarterFailureWrongYear() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		OffsetDateTime value = OffsetDateTime.of(2023, 2, 15, 12, 30, 0, 0, OFFSET);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region ToString
	@Test
	void toStringWithoutConstraints() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME;
		String str = codec.toString();
		assertEquals("OffsetDateTimeCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<OffsetDateTime> codec = OFFSET_DATE_TIME.after(OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, OFFSET));
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedOffsetDateTimeCodec["));
		assertTrue(str.contains("constraints="));
	}
	//endregion
}
