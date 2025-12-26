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

package net.luis.utils.io.codec.types.time.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZonedDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ZonedDateTimeCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), dateTime));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, dateTime));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as zoned date time"));
	}
	
	@Test
	void encodeStartWithValidZonedDateTime() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), dateTime);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30Z"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00+01:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), dateTime);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30+01:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00-05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), dateTime);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30-05:00"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00.123456789Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), dateTime);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00.123456789Z"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as zoned date time"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z"));
		assertTrue(result.isSuccess());
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00Z"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00+01:00"));
		assertTrue(result.isSuccess());
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00+01:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00-05:00"));
		assertTrue(result.isSuccess());
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00-05:00"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00.123456789Z"));
		assertTrue(result.isSuccess());
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00.123456789Z"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-datetime"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode zoned date time"));
		assertTrue(result.errorOrThrow().contains("Unable to parse zoned date time"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		ZonedDateTimeCodec codec = new ZonedDateTimeCodec();
		assertEquals("ZonedDateTimeCodec", codec.toString());
	}
}
