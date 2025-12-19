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

package net.luis.utils.io.codec.types.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DayOfWeekCodec}.<br>
 *
 * @author Luis-St
 */
class DayOfWeekCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.MONDAY;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), day));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, day));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as day of week"));
	}
	
	@Test
	void encodeStartWithMonday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.MONDAY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), day);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("MONDAY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithTuesday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.TUESDAY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), day);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("TUESDAY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithWednesday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.WEDNESDAY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), day);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("WEDNESDAY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSunday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.SUNDAY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), day);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("SUNDAY"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.FRIDAY;
		
		Result<String> result = codec.encodeKey(day);
		assertTrue(result.isSuccess());
		assertEquals("FRIDAY", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("MONDAY")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as day of week"));
	}
	
	@Test
	void decodeStartWithValidMonday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("MONDAY"));
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.MONDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidTuesday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("TUESDAY"));
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.TUESDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidWednesday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("WEDNESDAY"));
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.WEDNESDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidSunday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("SUNDAY"));
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.SUNDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID_DAY"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode day of week"));
	}
	
	@Test
	void decodeStartWithLowercaseDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("monday"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode day of week"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeKey("FRIDAY");
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.FRIDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithSaturday() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeKey("SATURDAY");
		assertTrue(result.isSuccess());
		assertEquals(DayOfWeek.SATURDAY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		Result<DayOfWeek> result = codec.decodeKey("INVALID_DAY");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'INVALID_DAY' as day of week"));
	}
	
	@Test
	void toStringRepresentation() {
		DayOfWeekCodec codec = new DayOfWeekCodec();
		assertEquals("DayOfWeekCodec", codec.toString());
	}
}
