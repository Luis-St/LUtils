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
 * Test class for {@link ShortArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ShortArrayCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 1, 2, 3 };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as short array"));
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 42 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add((short) 42);
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleElements() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 1, 2, 3 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add((short) 1);
		expected.add((short) 2);
		expected.add((short) 3);
		assertEquals(expected, result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as short array"));
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		
		short[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new short[] {}, result);
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		array.add((short) 42);
		
		short[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new short[] { 42 }, result);
	}
	
	@Test
	void decodeWithMultipleElements() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		array.add((short) 1);
		array.add((short) 2);
		array.add((short) 3);
		
		short[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new short[] { 1, 2, 3 }, result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 42)));
	}
	
	@Test
	void toStringRepresentation() {
		ShortArrayCodec codec = new ShortArrayCodec();
		assertEquals("ShortArrayCodec", codec.toString());
	}
}
