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

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeCodec}.<br>
 *
 * @author Luis-St
 */
class LocalTimeCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), time));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, time));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as local time"));
	}
	
	@Test
	void encodeWithValidTime() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("10:30:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30"), result);
	}
	
	@Test
	void encodeWithMidnight() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("00:00:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("00:00"), result);
	}
	
	@Test
	void encodeWithEndOfDay() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("23:59:59");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("23:59:59"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		LocalTime time = LocalTime.parse("10:30:00.123456789");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30:00.123456789"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("10:30:00")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as local time"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		LocalTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00"));
		assertEquals(LocalTime.parse("10:30:00"), result);
	}
	
	@Test
	void decodeWithMidnight() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		LocalTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("00:00:00"));
		assertEquals(LocalTime.parse("00:00:00"), result);
	}
	
	@Test
	void decodeWithEndOfDay() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		LocalTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("23:59:59"));
		assertEquals(LocalTime.parse("23:59:59"), result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		LocalTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00.123456789"));
		assertEquals(LocalTime.parse("10:30:00.123456789"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-time")));
		assertTrue(exception.getMessage().contains("Unable to decode local time"));
		assertTrue(exception.getMessage().contains("Unable to parse local time"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = new LocalTimeCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		LocalTimeCodec codec = new LocalTimeCodec();
		assertEquals("LocalTimeCodec", codec.toString());
	}
}
