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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringCodec}.<br>
 *
 * @author Luis-St
 */
class StringCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		String value = "test";
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as string"));
	}
	
	@Test
	void encodeWithSimpleString() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "hello");
		assertEquals(new JsonPrimitive("hello"), result);
	}
	
	@Test
	void encodeWithEmptyString() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "");
		assertEquals(new JsonPrimitive(""), result);
	}
	
	@Test
	void encodeWithSpecialCharacters() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "Hello, World! @#$%");
		assertEquals(new JsonPrimitive("Hello, World! @#$%"), result);
	}
	
	@Test
	void encodeWithMultilineString() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "line1\nline2\nline3");
		assertEquals(new JsonPrimitive("line1\nline2\nline3"), result);
	}
	
	@Test
	void encodeWithUnicodeCharacters() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "Hello \u4E16\u754C");
		assertEquals(new JsonPrimitive("Hello \u4E16\u754C"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithString() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.encodeKey("myKey");
		assertEquals("myKey", result);
	}
	
	@Test
	void encodeKeyWithEmptyString() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.encodeKey("");
		assertEquals("", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("test")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null as string"));
	}
	
	@Test
	void decodeWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertEquals("hello", result);
	}
	
	@Test
	void decodeWithEmptyString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertEquals("", result);
	}
	
	@Test
	void decodeWithSpecialCharacters() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("test@123"));
		assertEquals("test@123", result);
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.decodeKey("myKey");
		assertEquals("myKey", result);
	}
	
	@Test
	void decodeKeyWithEmptyString() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new StringCodec();
		
		String result = codec.decodeKey("");
		assertEquals("", result);
	}
	
	@Test
	void toStringRepresentation() {
		StringCodec codec = new StringCodec();
		assertEquals("StringCodec", codec.toString());
	}
}
