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

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearCodec}.<br>
 *
 * @author Luis-St
 */
class YearCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), year));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, year));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as year"));
	}
	
	@Test
	void encodeWithCurrentYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), year);
		assertEquals(new JsonPrimitive(2025), result);
	}
	
	@Test
	void encodeWithPastYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(1990);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), year);
		assertEquals(new JsonPrimitive(1990), result);
	}
	
	@Test
	void encodeWithFutureYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(3000);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), year);
		assertEquals(new JsonPrimitive(3000), result);
	}
	
	@Test
	void encodeWithMinYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(Year.MIN_VALUE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), year);
		assertEquals(new JsonPrimitive(Year.MIN_VALUE), result);
	}
	
	@Test
	void encodeWithMaxYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(Year.MAX_VALUE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), year);
		assertEquals(new JsonPrimitive(Year.MAX_VALUE), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithYear() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		Year year = Year.of(2025);
		
		String result = codec.encodeKey(year);
		assertEquals("2025", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(2025)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as year"));
	}
	
	@Test
	void decodeWithValidYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(2025));
		assertEquals(Year.of(2025), result);
	}
	
	@Test
	void decodeWithPastYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(1990));
		assertEquals(Year.of(1990), result);
	}
	
	@Test
	void decodeWithFutureYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3000));
		assertEquals(Year.of(3000), result);
	}
	
	@Test
	void decodeWithMinYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Year.MIN_VALUE));
		assertEquals(Year.of(Year.MIN_VALUE), result);
	}
	
	@Test
	void decodeWithMaxYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Year.MAX_VALUE));
		assertEquals(Year.of(Year.MAX_VALUE), result);
	}
	
	@Test
	void decodeWithNonInteger() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-year")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decodeKey("2025");
		assertEquals(Year.of(2025), result);
	}
	
	@Test
	void decodeKeyWithPastYear() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		Year result = codec.decodeKey("1990");
		assertEquals(Year.of(1990), result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = new YearCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as year"));
	}
	
	@Test
	void toStringRepresentation() {
		YearCodec codec = new YearCodec();
		assertEquals("YearCodec", codec.toString());
	}
}
