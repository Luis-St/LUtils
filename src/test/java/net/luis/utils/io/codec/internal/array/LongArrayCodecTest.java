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
 * Test class for {@link LongArrayCodec}.<br>
 *
 * @author Luis-St
 */
class LongArrayCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		long[] array = { 1L, 2L, 3L };
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as long array"));
	}
	
	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		long[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		long[] array = { 42L };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(42L);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		long[] array = { 1L, 2L, 3L };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(1L);
		expected.add(2L);
		expected.add(3L);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		
		Result<long[]> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as long array"));
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		JsonArray array = new JsonArray();
		
		Result<long[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] {}, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		JsonArray array = new JsonArray();
		array.add(42L);
		
		Result<long[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { 42L }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		JsonArray array = new JsonArray();
		array.add(1L);
		array.add(2L);
		array.add(3L);
		
		Result<long[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { 1L, 2L, 3L }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<long[]> codec = new LongArrayCodec();
		
		Result<long[]> result = codec.decodeStart(typeProvider, new JsonPrimitive(42L));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		LongArrayCodec codec = new LongArrayCodec();
		assertEquals("LongArrayCodec", codec.toString());
	}
}
