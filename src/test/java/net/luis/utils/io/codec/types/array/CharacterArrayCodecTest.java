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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharacterArrayCodec}.<br>
 *
 * @author Luis-St
 */
class CharacterArrayCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		char[] array = { 'a', 'b', 'c' };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as char array"));
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		char[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		char[] array = { 'x' };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add("x");
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleElements() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		char[] array = { 'a', 'b', 'c' };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add("a");
		expected.add("b");
		expected.add("c");
		assertEquals(expected, result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as char array"));
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		JsonArray array = new JsonArray();
		
		char[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new char[] {}, result);
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		JsonArray array = new JsonArray();
		array.add("x");
		
		char[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new char[] { 'x' }, result);
	}
	
	@Test
	void decodeWithMultipleElements() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		JsonArray array = new JsonArray();
		array.add("a");
		array.add("b");
		array.add("c");
		
		char[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new char[] { 'a', 'b', 'c' }, result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<char[]> codec = new CharacterArrayCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("x")));
	}
	
	@Test
	void toStringRepresentation() {
		CharacterArrayCodec codec = new CharacterArrayCodec();
		assertEquals("CharacterArrayCodec", codec.toString());
	}
}
