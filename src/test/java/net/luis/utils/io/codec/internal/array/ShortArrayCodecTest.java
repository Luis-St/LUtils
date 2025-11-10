/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.internal.array;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShortArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ShortArrayCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 1, 2, 3 };
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as short array"));
	}
	
	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 42 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add((short) 42);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		short[] array = { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add((short) 1);
		expected.add((short) 2);
		expected.add((short) 3);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		Result<short[]> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as short array"));
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		
		Result<short[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new short[] {}, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		array.add((short) 42);
		
		Result<short[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new short[] { 42 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		JsonArray array = new JsonArray();
		array.add((short) 1);
		array.add((short) 2);
		array.add((short) 3);
		
		Result<short[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new short[] { 1, 2, 3 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<short[]> codec = new ShortArrayCodec();
		
		Result<short[]> result = codec.decodeStart(typeProvider, new JsonPrimitive((short) 42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		ShortArrayCodec codec = new ShortArrayCodec();
		assertEquals("ShortArrayCodec", codec.toString());
	}
}
