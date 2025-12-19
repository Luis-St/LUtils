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

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearCodec}.<br>
 *
 * @author Luis-St
 */
class YearCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), year));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, year));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as year"));
	}
	
	@Test
	void encodeStartWithCurrentYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), year);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(2025), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithPastYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(1990);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), year);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(1990), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFutureYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(3000);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), year);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3000), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMinYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(Year.MIN_VALUE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), year);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Year.MIN_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMaxYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(Year.MAX_VALUE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), year);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Year.MAX_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		Result<String> result = codec.encodeKey(year);
		assertTrue(result.isSuccess());
		assertEquals("2025", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive(2025)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as year"));
	}
	
	@Test
	void decodeStartWithValidYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(2025));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2025), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithPastYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(1990));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(1990), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithFutureYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3000));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(3000), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMinYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Year.MIN_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(Year.MIN_VALUE), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMaxYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Year.MAX_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Year.of(Year.MAX_VALUE), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonInteger() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-year"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeKey("2025");
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2025), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithPastYear() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeKey("1990");
		assertTrue(result.isSuccess());
		assertEquals(Year.of(1990), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Result<Year> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as year"));
	}
	
	@Test
	void toStringRepresentation() {
		YearCodec codec = new YearCodec();
		assertEquals("YearCodec", codec.toString());
	}
}
