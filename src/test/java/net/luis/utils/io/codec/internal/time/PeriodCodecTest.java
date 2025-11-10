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

import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PeriodCodec}.<br>
 *
 * @author Luis-St
 */
class PeriodCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofYears(1);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), period));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as period"));
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), period);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0d"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithYears() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofYears(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), period);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2y"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMonths() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofMonths(6);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), period);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("6m"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofDays(15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), period);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("15d"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithComplexPeriod() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.of(1, 6, 15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), period);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("1y 6m 15d"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("1y")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as period"));
	}
	
	@Test
	void decodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("0d"));
		assertTrue(result.isSuccess());
		assertEquals(Period.ZERO, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithYears() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("2y"));
		assertTrue(result.isSuccess());
		assertEquals(Period.ofYears(2), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMonths() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("6m"));
		assertTrue(result.isSuccess());
		assertEquals(Period.ofMonths(6), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithDays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("15d"));
		assertTrue(result.isSuccess());
		assertEquals(Period.ofDays(15), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithComplexPeriod() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("1y 6m 15d"));
		assertTrue(result.isSuccess());
		assertEquals(Period.of(1, 6, 15), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("invalid-period"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode period"));
		assertTrue(result.errorOrThrow().contains("Invalid period format"));
	}
	
	@Test
	void decodeStartWithInvalidUnit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive("5x"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unknown time unit"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Result<Period> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		PeriodCodec codec = new PeriodCodec();
		assertEquals("PeriodCodec", codec.toString());
	}
}
