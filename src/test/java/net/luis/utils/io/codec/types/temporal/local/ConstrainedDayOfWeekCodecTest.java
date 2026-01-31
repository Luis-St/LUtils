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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DayOfWeekCodec} with constraints.<br>
 * Note: DayOfWeekCodec uses EnumConstraintConfig which does not support key encoding/decoding.<br>
 *
 * @author Luis-St
 */
class ConstrainedDayOfWeekCodecTest {
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("MONDAY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notEqualTo(DayOfWeek.SUNDAY);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notIn(weekend);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void encodeStartWithWeekendConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekend);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("MONDAY"));
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.MONDAY, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.custom(value -> {
			if (value.getValue() <= 5) {
				return Result.success(null);
			}
			return Result.error("Day must be a weekday");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithAllDaysInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> allDays = Set.of(DayOfWeek.values());
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(allDays);
		
		for (DayOfWeek day : DayOfWeek.values()) {
			Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), day);
			assertTrue(result.isSuccess());
		}
	}
	
	@Test
	void encodeStartWithMidWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> midWeek = Set.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(midWeek);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.WEDNESDAY);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY);
		assertTrue(result2.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notEqualTo(DayOfWeek.SUNDAY);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notIn(weekend);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY);
		assertTrue(result2.isError());
	}
	
	@Test
	void encodeStartWeekendConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekend);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result2.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("SATURDAY"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.custom(value -> {
			if (value.getValue() <= 5) {
				return Result.success(null);
			}
			return Result.error("Day must be a weekday");
		});
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMidWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> midWeek = Set.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(midWeek);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY);
		assertTrue(result2.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK;
		
		assertEquals("DayOfWeekCodec", codec.toString());
	}
}
