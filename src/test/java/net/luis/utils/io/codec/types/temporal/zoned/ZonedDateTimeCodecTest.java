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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), dateTime));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, dateTime));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as zoned date time"));
	}
	
	@Test
	void encodeWithValidZonedDateTime() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30Z"), result);
	}
	
	@Test
	void encodeWithOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00+01:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30+01:00"), result);
	}
	
	@Test
	void encodeWithNegativeOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00-05:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30-05:00"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		ZonedDateTime dateTime = ZonedDateTime.parse("2025-01-15T10:30:00.123456789Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00.123456789Z"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as zoned date time"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		ZonedDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z"));
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00Z"), result);
	}
	
	@Test
	void decodeWithOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		ZonedDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00+01:00"));
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00+01:00"), result);
	}
	
	@Test
	void decodeWithNegativeOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		ZonedDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00-05:00"));
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00-05:00"), result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		ZonedDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00.123456789Z"));
		assertEquals(ZonedDateTime.parse("2025-01-15T10:30:00.123456789Z"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-datetime")));
		assertTrue(exception.getMessage().contains("Unable to decode zoned date time"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		ZonedDateTimeCodec codec = new ZonedDateTimeCodec();
		assertEquals("ZonedDateTimeCodec", codec.toString());
	}
}
