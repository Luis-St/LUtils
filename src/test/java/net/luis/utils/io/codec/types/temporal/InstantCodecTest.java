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

package net.luis.utils.io.codec.types.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InstantCodec}.<br>
 *
 * @author Luis-St
 */
class InstantCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), instant));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, instant));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as instant"));
	}
	
	@Test
	void encodeWithValidInstant() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.parse("2025-01-15T10:30:00Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), instant);
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00Z"), result);
	}
	
	@Test
	void encodeWithEpoch() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.EPOCH;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), instant);
		assertEquals(new JsonPrimitive("1970-01-01T00:00:00Z"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.parse("2025-01-15T10:30:00.123456789Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), instant);
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00.123456789Z"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as instant"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		Instant result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00Z"));
		assertEquals(Instant.parse("2025-01-15T10:30:00Z"), result);
	}
	
	@Test
	void decodeWithEpoch() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		Instant result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1970-01-01T00:00:00Z"));
		assertEquals(Instant.EPOCH, result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		Instant result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15T10:30:00.123456789Z"));
		assertEquals(Instant.parse("2025-01-15T10:30:00.123456789Z"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-instant")));
		assertTrue(exception.getMessage().contains("Unable to decode instant"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		InstantCodec codec = new InstantCodec();
		assertEquals("InstantCodec", codec.toString());
	}
}
