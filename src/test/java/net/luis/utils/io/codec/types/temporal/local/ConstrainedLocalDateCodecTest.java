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
 * Test class for {@link LocalDateCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateCodecTest {
	
	@Test
	void encodeStartWithValidAfterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2023-06-15"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBefore(threshold));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBetween(after, before));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate target = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate value1 = LocalDate.of(2023, 6, 15);
		LocalDate value2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded = LocalDate.of(2023, 6, 15);
		LocalDate value = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded1 = LocalDate.of(2023, 6, 15);
		LocalDate excluded2 = LocalDate.of(2023, 7, 20);
		LocalDate value = LocalDate.of(2023, 8, 25);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfterOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBeforeOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidDayOfWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDate value = LocalDate.of(2023, 6, 16); // Friday
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidDayOfMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withDayOfMonth(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 15)
		));
		LocalDate value = LocalDate.of(2023, 6, 10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstraint() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("2023-06-15", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15"));
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2023, 6, 15), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstraint() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDate> result = codec.decodeKey("2023-06-15");
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.of(2023, 6, 15), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withCustom(value -> {
			if (value.getDayOfMonth() % 5 == 0) {
				return Result.success(null);
			}
			return Result.error("Day of month must be divisible by 5");
		}));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		LocalDate valueBefore = LocalDate.of(2019, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBefore(threshold));
		LocalDate valueAfter = LocalDate.of(2023, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueAfter);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBetween(after, before));
		LocalDate valueTooEarly = LocalDate.of(2019, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withBetween(after, before));
		LocalDate valueTooLate = LocalDate.of(2026, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate target = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withEqualTo(target));
		LocalDate differentValue = LocalDate.of(2023, 7, 20);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate value1 = LocalDate.of(2023, 6, 15);
		LocalDate value2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		LocalDate notInSet = LocalDate.of(2023, 8, 25);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded1 = LocalDate.of(2023, 6, 15);
		LocalDate excluded2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDayOfWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDate value = LocalDate.of(2023, 6, 17); // Saturday
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDayOfMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withDayOfMonth(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 15)
		));
		LocalDate value = LocalDate.of(2023, 6, 20);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDate value = LocalDate.of(2023, 1, 15); // January
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDate value = LocalDate.of(2015, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		LocalDate valueBefore = LocalDate.of(2019, 6, 15);
		
		Result<String> result = codec.encodeKey(valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		
		Result<LocalDate> result = codec.decodeKey("2019-06-15");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withCustom(value -> {
			if (value.getDayOfMonth() % 5 == 0) {
				return Result.success(null);
			}
			return Result.error("Day of month must be divisible by 5");
		}));
		LocalDate value = LocalDate.of(2023, 6, 17);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = new LocalDateCodec().apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDate> codec = new LocalDateCodec();
		
		assertEquals("LocalDateCodec", codec.toString());
	}
}
