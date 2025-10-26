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
 * Test class for {@link BooleanArrayCodec}.<br>
 *
 * @author Luis-St
 */
class BooleanArrayCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true, false, true };
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as boolean array"));
	}
	
	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(true);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		boolean[] array = { true, false, true };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(true);
		expected.add(false);
		expected.add(true);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		Result<boolean[]> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as boolean array"));
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		
		Result<boolean[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new boolean[] {}, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		array.add(true);
		
		Result<boolean[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new boolean[] { true }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		JsonArray array = new JsonArray();
		array.add(true);
		array.add(false);
		array.add(true);
		
		Result<boolean[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new boolean[] { true, false, true }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<boolean[]> codec = new BooleanArrayCodec();
		
		Result<boolean[]> result = codec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		BooleanArrayCodec codec = new BooleanArrayCodec();
		assertEquals("BooleanArrayCodec", codec.toString());
	}
}
