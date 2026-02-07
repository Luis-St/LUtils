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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class LocalDateTimeCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		LocalDateTime dateTime = LocalDateTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), dateTime));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, dateTime));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as local date time"));
	}
	
	@Test
	void encodeWithValidDateTime() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		LocalDateTime dateTime = LocalDateTime.parse("2025-01-15T10:30:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30"), result);
	}
	
	@Test
	void encodeWithEpoch() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		LocalDateTime dateTime = LocalDateTime.parse("1970-01-01T00:00:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("1970-01-01T00:00"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		LocalDateTime dateTime = LocalDateTime.parse("2025-01-15T10:30:00.123456789");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), dateTime);
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00.123456789"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as local date time"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		LocalDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00"));
		assertEquals(LocalDateTime.parse("2025-01-15T10:30:00"), result);
	}
	
	@Test
	void decodeWithEpoch() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		LocalDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1970-01-01T00:00:00"));
		assertEquals(LocalDateTime.parse("1970-01-01T00:00:00"), result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		LocalDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00.123456789"));
		assertEquals(LocalDateTime.parse("2025-01-15T10:30:00.123456789"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-datetime")));
		assertTrue(exception.getMessage().contains("Unable to decode local date time"));
		assertTrue(exception.getMessage().contains("Unable to parse local date time"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = new LocalDateTimeCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		LocalDateTimeCodec codec = new LocalDateTimeCodec();
		assertEquals("LocalDateTimeCodec", codec.toString());
	}
}
