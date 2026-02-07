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

package net.luis.utils.io.codec.types.temporal.offset;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OffsetTimeCodec}.<br>
 *
 * @author Luis-St
 */
class OffsetTimeCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), time));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, time));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as offset time"));
	}
	
	@Test
	void encodeWithValidTime() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00+01:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30+01:00"), result);
	}
	
	@Test
	void encodeWithUTCOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30Z"), result);
	}
	
	@Test
	void encodeWithNegativeOffset() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00-05:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30-05:00"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00.123456789+02:00");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), time);
		assertEquals(new JsonPrimitive("10:30:00.123456789+02:00"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValidTime() throws EncoderException {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00+01:00");
		
		String result = codec.encodeKey(time);
		assertEquals("10:30+01:00", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("10:30:00+01:00")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as offset time"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		OffsetTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00+01:00"));
		assertEquals(OffsetTime.parse("10:30:00+01:00"), result);
	}
	
	@Test
	void decodeWithUTCOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		OffsetTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00Z"));
		assertEquals(OffsetTime.parse("10:30:00Z"), result);
	}
	
	@Test
	void decodeWithNegativeOffset() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		OffsetTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00-05:00"));
		assertEquals(OffsetTime.parse("10:30:00-05:00"), result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		OffsetTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00.123456789+02:00"));
		assertEquals(OffsetTime.parse("10:30:00.123456789+02:00"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-time")));
		assertTrue(exception.getMessage().contains("Unable to decode offset time"));
		assertTrue(exception.getMessage().contains("Unable to parse offset time"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidString() throws DecoderException {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		OffsetTime result = codec.decodeKey("10:30:00+01:00");
		assertEquals(OffsetTime.parse("10:30:00+01:00"), result);
	}
	
	@Test
	void decodeKeyWithInvalidString() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid-time"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid-time' as offset time"));
	}
	
	@Test
	void toStringRepresentation() {
		OffsetTimeCodec codec = new OffsetTimeCodec();
		assertEquals("OffsetTimeCodec", codec.toString());
	}
}
