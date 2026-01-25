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

import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MonthCodec} with constraints.<br>
 * Note: MonthCodec uses EnumConstraintConfig which does not support key encoding/decoding.<br>
 *
 * @author Luis-St
 */
class ConstrainedMonthCodecTest {
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withEqualTo(Month.JUNE));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("JUNE"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(summerMonths));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JULY);
		assertTrue(result2.isSuccess());
		
		Result<JsonElement> result3 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.AUGUST);
		assertTrue(result3.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withNotEqualTo(Month.JANUARY));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> winterMonths = Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withNotIn(winterMonths));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JULY);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(summerMonths));
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("JUNE"));
		assertTrue(result.isSuccess());
		assertEquals(Month.JUNE, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withCustom(value -> {
			if (value.getValue() >= 4 && value.getValue() <= 9) {
				return Result.success(null);
			}
			return Result.error("Month must be in warm season (April to September)");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithAllMonthsInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> allMonths = Set.of(Month.values());
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(allMonths));
		
		for (Month month : Month.values()) {
			Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), month);
			assertTrue(result.isSuccess());
		}
	}
	
	@Test
	void encodeStartWithFirstQuarterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> firstQuarter = Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(firstQuarter));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isSuccess());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.MARCH);
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withEqualTo(Month.JUNE));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(summerMonths));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.DECEMBER);
		assertTrue(result2.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withNotEqualTo(Month.JANUARY));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> winterMonths = Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withNotIn(winterMonths));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.DECEMBER);
		assertTrue(result2.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(summerMonths));
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("JANUARY"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec().apply(config -> config.withCustom(value -> {
			if (value.getValue() >= 4 && value.getValue() <= 9) {
				return Result.success(null);
			}
			return Result.error("Month must be in warm season (April to September)");
		}));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JANUARY);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartFirstQuarterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> firstQuarter = Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
		Codec<Month> codec = new MonthCodec().apply(config -> config.withIn(firstQuarter));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Month.JUNE);
		assertTrue(result.isError());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), Month.DECEMBER);
		assertTrue(result2.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Month> codec = new MonthCodec().apply(config -> config.withEqualTo(Month.JUNE));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Month> codec = new MonthCodec();
		
		assertEquals("MonthCodec", codec.toString());
	}
}
