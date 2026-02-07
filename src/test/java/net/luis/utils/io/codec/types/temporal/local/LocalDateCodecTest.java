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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateCodec}.<br>
 *
 * @author Luis-St
 */
class LocalDateCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.now();
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), date));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, date));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as local date"));
	}
	
	@Test
	void encodeWithValidDate() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("2025-01-15");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), date);
		assertEquals(new JsonPrimitive("2025-01-15"), result);
	}
	
	@Test
	void encodeWithEpoch() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("1970-01-01");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), date);
		assertEquals(new JsonPrimitive("1970-01-01"), result);
	}
	
	@Test
	void encodeWithFutureDate() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		LocalDate date = LocalDate.parse("2030-12-31");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), date);
		assertEquals(new JsonPrimitive("2030-12-31"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("2025-01-15")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as local date"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		LocalDate result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2025-01-15"));
		assertEquals(LocalDate.parse("2025-01-15"), result);
	}
	
	@Test
	void decodeWithEpoch() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		LocalDate result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1970-01-01"));
		assertEquals(LocalDate.parse("1970-01-01"), result);
	}
	
	@Test
	void decodeWithFutureDate() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		LocalDate result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2030-12-31"));
		assertEquals(LocalDate.parse("2030-12-31"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-date")));
		assertTrue(exception.getMessage().contains("Unable to decode local date"));
		assertTrue(exception.getMessage().contains("Unable to parse local date"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = new LocalDateCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		LocalDateCodec codec = new LocalDateCodec();
		assertEquals("LocalDateCodec", codec.toString());
	}
}
