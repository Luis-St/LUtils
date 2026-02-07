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
 * Test class for {@link CharacterCodec}.<br>
 *
 * @author Luis-St
 */
class CharacterCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		Character value = 'a';
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as character"));
	}
	
	@Test
	void encodeWithLowercaseLetter() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 'a');
		assertEquals(new JsonPrimitive("a"), result);
	}
	
	@Test
	void encodeWithUppercaseLetter() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 'Z');
		assertEquals(new JsonPrimitive("Z"), result);
	}
	
	@Test
	void encodeWithDigit() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), '5');
		assertEquals(new JsonPrimitive("5"), result);
	}
	
	@Test
	void encodeWithSpecialCharacter() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), '@');
		assertEquals(new JsonPrimitive("@"), result);
	}
	
	@Test
	void encodeWithSpace() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), ' ');
		assertEquals(new JsonPrimitive(" "), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithCharacter() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		String result = codec.encodeKey('x');
		assertEquals("x", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("a")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null as character"));
	}
	
	@Test
	void decodeWithValidCharacter() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Character result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertEquals('a', result);
	}
	
	@Test
	void decodeWithMultiCharacterString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("abc")));
		assertTrue(exception.getMessage().contains("String must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("")));
		assertTrue(exception.getMessage().contains("String must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidCharacter() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Character result = codec.decodeKey("x");
		assertEquals('x', result);
	}
	
	@Test
	void decodeKeyWithMultiCharacterString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("abc"));
		assertTrue(exception.getMessage().contains("Key must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeKeyWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey(""));
		assertTrue(exception.getMessage().contains("Key must have exactly one character to decode as character"));
	}
	
	@Test
	void toStringRepresentation() {
		CharacterCodec codec = new CharacterCodec();
		assertEquals("CharacterCodec", codec.toString());
	}
}
