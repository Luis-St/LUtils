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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedYearCodecTest {
	
	@Test
	void encodeStartWithValidAfterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		Year value = Year.of(2023);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(2023), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2025);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBefore(threshold));
		Year value = Year.of(2023);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBetween(after, before));
		Year value = Year.of(2023);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year target = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year value1 = Year.of(2023);
		Year value2 = Year.of(2024);
		Codec<Year> codec = new YearCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded = Year.of(2023);
		Year value = Year.of(2024);
		Codec<Year> codec = new YearCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded1 = Year.of(2023);
		Year excluded2 = Year.of(2024);
		Year value = Year.of(2025);
		Codec<Year> codec = new YearCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfterOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBeforeOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year min = Year.of(2020);
		Year max = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBetweenOrEqual(min, max));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), min);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), max);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstraint() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		Year value = Year.of(2023);
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("2023", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(2023));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2023), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstraint() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		
		Result<Year> result = codec.decodeKey("2023");
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2023), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec().apply(config -> config.withCustom(value -> {
			if (value.getValue() % 4 == 0) {
				return Result.success(null);
			}
			return Result.error("Year must be divisible by 4 (leap year candidates)");
		}));
		Year value = Year.of(2024);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		Year valueBefore = Year.of(2019);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBefore(threshold));
		Year valueAfter = Year.of(2023);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueAfter);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBetween(after, before));
		Year valueTooEarly = Year.of(2019);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = new YearCodec().apply(config -> config.withBetween(after, before));
		Year valueTooLate = Year.of(2026);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year target = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withEqualTo(target));
		Year differentValue = Year.of(2024);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year value1 = Year.of(2023);
		Year value2 = Year.of(2024);
		Codec<Year> codec = new YearCodec().apply(config -> config.withIn(Set.of(value1, value2)));
		Year notInSet = Year.of(2025);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded = Year.of(2023);
		Codec<Year> codec = new YearCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded1 = Year.of(2023);
		Year excluded2 = Year.of(2024);
		Codec<Year> codec = new YearCodec().apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		Year valueBefore = Year.of(2019);
		
		Result<String> result = codec.encodeKey(valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(2019));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		
		Result<Year> result = codec.decodeKey("2019");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec().apply(config -> config.withCustom(value -> {
			if (value.getValue() % 4 == 0) {
				return Result.success(null);
			}
			return Result.error("Year must be divisible by 4 (leap year candidates)");
		}));
		Year value = Year.of(2023);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = new YearCodec().apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Year> codec = new YearCodec();
		
		assertEquals("YearCodec", codec.toString());
	}
}
