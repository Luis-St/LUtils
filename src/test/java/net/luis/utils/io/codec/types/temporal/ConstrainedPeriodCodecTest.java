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

package net.luis.utils.io.codec.types.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.temporal.PeriodConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Period;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PeriodCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedPeriodCodecTest {
	
	@Test
	void encodeStartWithValidGreaterThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(6);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withGreaterThan(threshold));
		Period value = Period.ofYears(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidLessThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(2);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withLessThan(threshold));
		Period value = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(1);
		Period max = Period.ofYears(2);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withBetween(min, max));
		Period value = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period target = Period.of(1, 2, 15);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period value1 = Period.ofMonths(1);
		Period value2 = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded = Period.ofMonths(1);
		Period value = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded1 = Period.ofMonths(1);
		Period excluded2 = Period.ofMonths(3);
		Period value = Period.ofMonths(6);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidPositiveConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withPositive);
		Period value = Period.ofMonths(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNonNegativeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withNonNegative);
		Period value = Period.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNonZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withNonZero);
		Period value = Period.ofDays(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withZero);
		Period value = Period.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidDayConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withDay(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(10)
		));
		Period value = Period.ofDays(15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withMonth(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 6)
		));
		Period value = Period.ofMonths(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(5)
		));
		Period value = Period.ofYears(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(1);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withGreaterThan(threshold));
		
		Result<Period> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1y"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withCustom(value -> {
			if (value.getDays() % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Period days must be divisible by 7");
		}));
		Period value = Period.ofDays(14);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(1);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withGreaterThan(threshold));
		Period value = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(6);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withLessThan(threshold));
		Period value = Period.ofYears(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(6);
		Period max = Period.ofYears(2);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withBetween(min, max));
		Period value = Period.ofMonths(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(1);
		Period max = Period.ofYears(1);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withBetween(min, max));
		Period value = Period.ofYears(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period target = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withEqualTo(target));
		Period differentValue = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period value1 = Period.ofMonths(1);
		Period value2 = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		Period notInSet = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded1 = Period.ofMonths(1);
		Period excluded2 = Period.ofMonths(3);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withPositive);
		Period value = Period.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withNonNegative);
		Period value = Period.ofDays(-1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withNonZero);
		Period value = Period.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(PeriodConstraintConfig::withZero);
		Period value = Period.ofDays(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartDayConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withDay(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(20)
		));
		Period value = Period.ofDays(10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withMonth(
			NumericConstraintConfig.UNCONSTRAINED.withLessThan(3)
		));
		Period value = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(2)
		));
		Period value = Period.ofYears(5);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(2);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withGreaterThan(threshold));
		
		Result<Period> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("6m"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withCustom(value -> {
			if (value.getDays() % 7 == 0) {
				return Result.success(null);
			}
			return Result.error("Period days must be divisible by 7");
		}));
		Period value = Period.ofDays(10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		Period threshold = Period.ofMonths(1);
		Codec<Period> codec = new PeriodCodec().apply(config -> config.withGreaterThan(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Period> codec = new PeriodCodec();
		
		assertEquals("PeriodCodec", codec.toString());
	}
}
