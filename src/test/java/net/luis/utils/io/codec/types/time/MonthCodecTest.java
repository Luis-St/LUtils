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

import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MonthCodec}.<br>
 *
 * @author Luis-St
 */
class MonthCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JANUARY;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), month));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, month));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as month"));
	}
	
	@Test
	void encodeStartWithJanuary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JANUARY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), month);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("JANUARY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFebruary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.FEBRUARY;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), month);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("FEBRUARY"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithJune() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JUNE;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), month);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("JUNE"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDecember() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.DECEMBER;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), month);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("DECEMBER"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.MARCH;
		
		Result<String> result = codec.encodeKey(month);
		assertTrue(result.isSuccess());
		assertEquals("MARCH", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("JANUARY")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as month"));
	}
	
	@Test
	void decodeStartWithValidJanuary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("JANUARY"));
		assertTrue(result.isSuccess());
		assertEquals(Month.JANUARY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidFebruary() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("FEBRUARY"));
		assertTrue(result.isSuccess());
		assertEquals(Month.FEBRUARY, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidJune() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("JUNE"));
		assertTrue(result.isSuccess());
		assertEquals(Month.JUNE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidDecember() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("DECEMBER"));
		assertTrue(result.isSuccess());
		assertEquals(Month.DECEMBER, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID_MONTH"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode month"));
	}
	
	@Test
	void decodeStartWithLowercaseMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("january"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode month"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeKey("APRIL");
		assertTrue(result.isSuccess());
		assertEquals(Month.APRIL, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithAugust() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeKey("AUGUST");
		assertTrue(result.isSuccess());
		assertEquals(Month.AUGUST, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Result<Month> result = codec.decodeKey("INVALID_MONTH");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'INVALID_MONTH' as month"));
	}
	
	@Test
	void toStringRepresentation() {
		MonthCodec codec = new MonthCodec();
		assertEquals("MonthCodec", codec.toString());
	}
}
