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

package net.luis.utils.io.codec.types.temporal.zoned;

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
 * Test class for {@link ZonedDateTimeCodec} focusing on zoned date time constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedZonedDateTimeCodecTest {
	
	private static final ZoneId ZONE = ZoneId.of("Europe/Berlin");
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	//region Temporal Comparison Constraints - Encode
	@Test
	void encodeAfterSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.after(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeAfterFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.after(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeAfterOrEqualSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.afterOrEqual(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBeforeSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.before(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBeforeFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.before(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeBeforeOrEqualSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.beforeOrEqual(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBetweenSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.between(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBetweenFailureTooEarly() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.between(ZonedDateTime.of(2024, 6, 15, 0, 0, 0, 0, ZONE), ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBetweenFailureTooLate() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.between(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), ZonedDateTime.of(2024, 6, 15, 23, 59, 59, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 12, 31, 0, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBetweenOrEqualSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.betweenOrEqual(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE), ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	//endregion
	
	//region Temporal Comparison Constraints - Decode
	@Test
	void decodeAfterSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.after(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
		assertEquals(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE), result.resultOrThrow());
	}

	@Test
	void decodeAfterFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.after(ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-01-01T00:00:00+01:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}

	@Test
	void decodeBeforeSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.before(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeBeforeFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.before(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}

	@Test
	void decodeBetweenSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.between(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE), ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeBetweenFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.between(ZonedDateTime.of(2024, 6, 15, 0, 0, 0, 0, ZONE), ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-01-01T00:00:00+01:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Time Field Constraints - Encode
	@Test
	void encodeHourConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeHourConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 20, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMinuteConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.minute(c -> c.equalTo(0));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMinuteConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.minute(c -> c.equalTo(0));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeSecondConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.second(c -> c.lessThan(30));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 15, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeSecondConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.second(c -> c.lessThan(30));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMillisecondConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.millisecond(c -> c.equalTo(0));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMillisecondConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.millisecond(c -> c.equalTo(0));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 500_000_000, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region Time Field Constraints - Decode
	@Test
	void decodeHourConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeHourConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.hour(c -> c.betweenOrEqual(9, 17));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T20:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}

	@Test
	void decodeMinuteConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.minute(c -> c.equalTo(0));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:00:00+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeMinuteConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.minute(c -> c.equalTo(0));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:00+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Date Field Constraints - Encode
	@Test
	void encodeDayOfWeekConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 17, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfWeekConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeDayOfWeekInConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 17, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfWeekInConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMonthConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.month(Month.JUNE);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMonthConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.month(Month.DECEMBER);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeMonthInConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeMonthInConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.monthIn(Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeDayOfMonthConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 10, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeDayOfMonthConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 20, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeYearConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.year(c -> c.equalTo(2024));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeYearConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.year(c -> c.equalTo(2024));
		ZonedDateTime value = ZonedDateTime.of(2023, 6, 15, 12, 30, 45, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region Date Field Constraints - Decode
	@Test
	void decodeDayOfWeekConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-17T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeDayOfWeekConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfWeek(DayOfWeek.MONDAY);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}

	@Test
	void decodeMonthConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.month(Month.JUNE);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeMonthConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.month(Month.DECEMBER);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}

	@Test
	void decodeDayOfMonthConstraintSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-10T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeDayOfMonthConstraintFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.dayOfMonth(c -> c.betweenOrEqual(1, 15));
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-20T12:30:45+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Combined Constraints
	@Test
	void encodeWithAllConstraintTypesSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.afterOrEqual(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE))
			.beforeOrEqual(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 17, 12, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithAllConstraintTypesFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.afterOrEqual(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE))
			.beforeOrEqual(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithAllConstraintTypesSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.afterOrEqual(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE))
			.beforeOrEqual(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-17T12:00:00+02:00[Europe/Berlin]"));
		assertTrue(result.isSuccess());
	}

	@Test
	void decodeWithAllConstraintTypesFailure() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.afterOrEqual(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE))
			.beforeOrEqual(ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZONE))
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.dayOfWeek(DayOfWeek.MONDAY)
			.month(Month.JUNE);
		Result<ZonedDateTime> result = codec.decodeStart(this.provider, this.provider.empty(), new JsonPrimitive("2024-06-15T12:00:00+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}
	//endregion
	
	//region Business Logic Scenarios
	@Test
	void encodeBusinessHoursOnWeekdaysSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 17, 14, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeBusinessHoursOnWeekdaysFailureWeekend() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 14, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeBusinessHoursOnWeekdaysFailureOutsideHours() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.dayOfWeekIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 17, 20, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeFirstQuarterSuccess() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		ZonedDateTime value = ZonedDateTime.of(2024, 2, 15, 12, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeFirstQuarterFailureWrongMonth() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		ZonedDateTime value = ZonedDateTime.of(2024, 6, 15, 12, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeFirstQuarterFailureWrongYear() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME
			.monthIn(Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH))
			.year(c -> c.equalTo(2024));
		ZonedDateTime value = ZonedDateTime.of(2023, 2, 15, 12, 30, 0, 0, ZONE);
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	//endregion
	
	//region ToString
	@Test
	void toStringWithoutConstraints() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME;
		String str = codec.toString();
		assertEquals("ZonedDateTimeCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<ZonedDateTime> codec = ZONED_DATE_TIME.after(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZONE));
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedZonedDateTimeCodec["));
		assertTrue(str.contains("constraints="));
	}
	//endregion
}
