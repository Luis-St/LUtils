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
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Base64Codec}.<br>
 *
 * @author Luis-St
 */
class Base64CodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 1, 2, 3 };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as base 64"));
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonPrimitive(""), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 42 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		String expected = Base64.getEncoder().encodeToString(array);
		assertEquals(new JsonPrimitive(expected), result);
	}
	
	@Test
	void encodeWithMultipleElements() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 1, 2, 3, 4, 5 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		String expected = Base64.getEncoder().encodeToString(array);
		assertEquals(new JsonPrimitive(expected), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("AQID")));
		assertThrows(NullPointerException.class, () -> codec.decode(typeProvider, null, new JsonPrimitive("AQID")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as base 64"));
	}
	
	@Test
	void decodeWithEmptyString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonPrimitive value = new JsonPrimitive("");
		
		byte[] result = codec.decode(typeProvider, typeProvider.empty(), value);
		assertArrayEquals(new byte[] {}, result);
	}
	
	@Test
	void decodeWithValidBase64() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] expected = { 1, 2, 3, 4, 5 };
		String base64 = Base64.getEncoder().encodeToString(expected);
		JsonPrimitive value = new JsonPrimitive(base64);
		
		byte[] result = codec.decode(typeProvider, typeProvider.empty(), value);
		assertArrayEquals(expected, result);
	}
	
	@Test
	void decodeWithInvalidBase64() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonPrimitive value = new JsonPrimitive("!!!invalid-base64!!!");
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), value));
		assertTrue(exception.getMessage().contains("Unable to decode base 64 string"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonArray value = new JsonArray();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), value));
		assertTrue(exception.getMessage().contains("Json element '[]' is not a json primitive"));
	}
	
	@Test
	void roundTripEncoding() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] original = { 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100 };
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), original);
		byte[] decodeResult = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		assertArrayEquals(original, decodeResult);
	}
	
	@Test
	void toStringRepresentation() {
		Base64Codec codec = new Base64Codec();
		assertEquals("Base64Codec", codec.toString());
	}
}
