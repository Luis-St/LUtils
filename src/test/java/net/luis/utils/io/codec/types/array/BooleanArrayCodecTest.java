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
 * Test class for {@link BooleanArrayCodec}.<br>
 *
 * @author Luis-St
 */
class BooleanArrayCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true, false, true };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as boolean array"));
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add(true);
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleElements() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true, false, true };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add(true);
		expected.add(false);
		expected.add(true);
		assertEquals(expected, result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as boolean array"));
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		
		boolean[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new boolean[] {}, result);
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		array.add(true);
		
		boolean[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new boolean[] { true }, result);
	}
	
	@Test
	void decodeWithMultipleElements() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		array.add(true);
		array.add(false);
		array.add(true);
		
		boolean[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new boolean[] { true, false, true }, result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true)));
	}
	
	@Test
	void toStringRepresentation() {
		BooleanArrayCodec codec = new BooleanArrayCodec();
		assertEquals("BooleanArrayCodec", codec.toString());
	}
}
