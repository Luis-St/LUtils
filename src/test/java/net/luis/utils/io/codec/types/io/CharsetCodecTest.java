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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_8;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), charset));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, charset));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as charset"));
	}
	
	@Test
	void encodeStartWithUTF8() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_8;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), charset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("UTF-8"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithISO88591() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.ISO_8859_1;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), charset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("ISO-8859-1"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUS_ASCII() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.US_ASCII;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), charset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("US-ASCII"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUTF16() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		Charset charset = StandardCharsets.UTF_16;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), charset);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("UTF-16"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("UTF-8")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as charset"));
	}
	
	@Test
	void decodeStartWithUTF8() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("UTF-8"));
		assertTrue(result.isSuccess());
		assertEquals(StandardCharsets.UTF_8, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithISO88591() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("ISO-8859-1"));
		assertTrue(result.isSuccess());
		assertEquals(StandardCharsets.ISO_8859_1, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithUS_ASCII() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("US-ASCII"));
		assertTrue(result.isSuccess());
		assertEquals(StandardCharsets.US_ASCII, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithUTF16() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("UTF-16"));
		assertTrue(result.isSuccess());
		assertEquals(StandardCharsets.UTF_16, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidCharset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID-CHARSET"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode charset"));
		assertTrue(result.errorOrThrow().contains("Unable to parse charset"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Charset> codec = new CharsetCodec();
		
		Result<Charset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		CharsetCodec codec = new CharsetCodec();
		assertEquals("CharsetCodec", codec.toString());
	}
}
