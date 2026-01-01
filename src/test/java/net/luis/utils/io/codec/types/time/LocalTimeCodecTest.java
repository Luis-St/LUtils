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

package net.luis.utils.io.codec.types.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeCodec}.<br>
 *
 * @author Luis-St
 */
class LocalTimeCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), time));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, time));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as local time"));
	}
	
	@Test
	void encodeStartWithValidTime() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("10:30:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMidnight() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("00:00:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("00:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEndOfDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("23:59:59");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("23:59:59"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("10:30:00.123456789");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30:00.123456789"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("10:30:00")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as local time"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00"));
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.parse("10:30:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMidnight() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("00:00:00"));
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.parse("00:00:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEndOfDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("23:59:59"));
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.parse("23:59:59"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00.123456789"));
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.parse("10:30:00.123456789"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-time"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode local time"));
		assertTrue(result.errorOrThrow().contains("Unable to parse local time"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		LocalTimeCodec codec = new LocalTimeCodec();
		assertEquals("LocalTimeCodec", codec.toString());
	}
}
