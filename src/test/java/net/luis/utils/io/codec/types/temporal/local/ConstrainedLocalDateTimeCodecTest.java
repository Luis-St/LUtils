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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateTimeCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateTimeCodecTest {
	
	@Test
	void encodeStartWithValidAfterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2023-06-15T12:30"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBefore(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBetween(after, before));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime target = LocalDateTime.of(2023, 6, 15, 12, 30, 45);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime value1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime excluded2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		LocalDateTime value = LocalDateTime.of(2023, 8, 25, 18, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfterOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBeforeOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidDayOfWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 16, 12, 30); // Friday
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withHour(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withMinute(
			NumericConstraintConfig.UNCONSTRAINED.withIn(Set.of(0, 15, 30, 45))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstraint() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("2023-06-15T12:30", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30"));
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2023, 6, 15, 12, 30), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstraint() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDateTime> result = codec.decodeKey("2023-06-15T12:30");
		assertTrue(result.isSuccess());
		assertEquals(LocalDateTime.of(2023, 6, 15, 12, 30), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0 && value.getSecond() == 0) {
				return Result.success(null);
			}
			return Result.error("DateTime must be on quarter hour with zero seconds");
		}));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30, 0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		LocalDateTime valueBefore = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBefore(threshold));
		LocalDateTime valueAfter = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueAfter);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBetween(after, before));
		LocalDateTime valueTooEarly = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withBetween(after, before));
		LocalDateTime valueTooLate = LocalDateTime.of(2026, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime target = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withEqualTo(target));
		LocalDateTime differentValue = LocalDateTime.of(2023, 7, 20, 15, 45);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime value1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		LocalDateTime notInSet = LocalDateTime.of(2023, 8, 25, 18, 0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime excluded2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDayOfWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 17, 12, 30); // Saturday
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDateTime value = LocalDateTime.of(2023, 1, 15, 12, 30); // January
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDateTime value = LocalDateTime.of(2015, 6, 15, 12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withHour(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 20, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withMinute(
			NumericConstraintConfig.UNCONSTRAINED.withIn(Set.of(0, 15, 30, 45))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 22);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		LocalDateTime valueBefore = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		Result<String> result = codec.encodeKey(valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:30"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDateTime> result = codec.decodeKey("2019-06-15T12:30");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0 && value.getSecond() == 0) {
				return Result.success(null);
			}
			return Result.error("DateTime must be on quarter hour with zero seconds");
		}));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 22, 0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = new LocalDateTimeCodec().apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		assertEquals("LocalDateTimeCodec", codec.toString());
	}
}
