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

package net.luis.utils.io.codec.types.primitiv;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharacterCodec}.<br>
 *
 * @author Luis-St
 */
class CharacterCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		Character value = 'a';
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as character"));
	}
	
	@Test
	void encodeStartWithLowercaseLetter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'a');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("a"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUppercaseLetter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 'Z');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("Z"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDigit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '5');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("5"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSpecialCharacter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), '@');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("@"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSpace() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ' ');
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(" "), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithCharacter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<String> result = codec.encodeKey('x');
		assertTrue(result.isSuccess());
		assertEquals("x", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("a")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as character"));
	}
	
	@Test
	void decodeStartWithValidCharacter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("a"));
		assertTrue(result.isSuccess());
		assertEquals('a', result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultiCharacterString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("abc"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("String must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("String must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidCharacter() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeKey("x");
		assertTrue(result.isSuccess());
		assertEquals('x', result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithMultiCharacterString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeKey("abc");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Key must have exactly one character to decode as character"));
	}
	
	@Test
	void decodeKeyWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Character> codec = new CharacterCodec();
		
		Result<Character> result = codec.decodeKey("");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Key must have exactly one character to decode as character"));
	}
	
	@Test
	void toStringRepresentation() {
		CharacterCodec codec = new CharacterCodec();
		assertEquals("CharacterCodec", codec.toString());
	}
}
