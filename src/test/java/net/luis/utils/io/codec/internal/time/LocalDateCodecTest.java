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

package net.luis.utils.io.codec.internal.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateCodec}.<br>
 *
 * @author Luis-St
 */
class LocalDateCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.now();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), date));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, date));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as local date"));
	}
	
	@Test
	void encodeStartWithValidDate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("2025-01-15");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), date);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEpoch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("1970-01-01");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), date);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("1970-01-01"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFutureDate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("2030-12-31");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), date);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2030-12-31"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("2025-01-15")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as local date"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, new JsonPrimitive("2025-01-15"));
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.parse("2025-01-15"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEpoch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, new JsonPrimitive("1970-01-01"));
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.parse("1970-01-01"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithFutureDate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, new JsonPrimitive("2030-12-31"));
		assertTrue(result.isSuccess());
		assertEquals(LocalDate.parse("2030-12-31"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, new JsonPrimitive("invalid-date"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode local date"));
		assertTrue(result.errorOrThrow().contains("Unable to parse local date"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		Result<LocalDate> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		LocalDateCodec codec = new LocalDateCodec();
		assertEquals("LocalDateCodec", codec.toString());
	}
}
