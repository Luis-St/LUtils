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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharsetCodec}.<br>
 *
 * @author Luis-St
 */
class CharsetCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_8;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), charset));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, charset));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as charset"));
	}
	
	@Test
	void encodeWithUTF8() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_8;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), charset);
		assertEquals(new JsonPrimitive("UTF-8"), result);
	}
	
	@Test
	void encodeWithISO88591() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.ISO_8859_1;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), charset);
		assertEquals(new JsonPrimitive("ISO-8859-1"), result);
	}
	
	@Test
	void encodeWithUS_ASCII() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.US_ASCII;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), charset);
		assertEquals(new JsonPrimitive("US-ASCII"), result);
	}
	
	@Test
	void encodeWithUTF16() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_16;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), charset);
		assertEquals(new JsonPrimitive("UTF-16"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("UTF-8")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as charset"));
	}
	
	@Test
	void decodeWithUTF8() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Charset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("UTF-8"));
		assertEquals(StandardCharsets.UTF_8, result);
	}
	
	@Test
	void decodeWithISO88591() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Charset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ISO-8859-1"));
		assertEquals(StandardCharsets.ISO_8859_1, result);
	}
	
	@Test
	void decodeWithUS_ASCII() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Charset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("US-ASCII"));
		assertEquals(StandardCharsets.US_ASCII, result);
	}
	
	@Test
	void decodeWithUTF16() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Charset result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("UTF-16"));
		assertEquals(StandardCharsets.UTF_16, result);
	}
	
	@Test
	void decodeWithInvalidCharset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID-CHARSET")));
		assertTrue(exception.getMessage().contains("Unable to decode charset"));
		assertTrue(exception.getMessage().contains("Unable to parse charset"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		CharsetCodec codec = new CharsetCodec();
		assertEquals("CharsetCodec", codec.toString());
	}
}
