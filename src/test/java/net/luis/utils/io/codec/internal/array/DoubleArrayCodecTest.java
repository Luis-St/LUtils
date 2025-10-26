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
 * Test class for {@link DoubleArrayCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleArrayCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 1.0, 2.0, 3.0 };
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as double array"));
	}
	
	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = {};
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 42.5 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(42.5);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 1.1, 2.2, 3.3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		JsonArray expected = new JsonArray();
		expected.add(1.1);
		expected.add(2.2);
		expected.add(3.3);
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		Result<double[]> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as double array"));
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		
		Result<double[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] {}, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		array.add(42.5);
		
		Result<double[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { 42.5 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		array.add(1.1);
		array.add(2.2);
		array.add(3.3);
		
		Result<double[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { 1.1, 2.2, 3.3 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		Result<double[]> result = codec.decodeStart(typeProvider, new JsonPrimitive(42.5));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		DoubleArrayCodec codec = new DoubleArrayCodec();
		assertEquals("DoubleArrayCodec", codec.toString());
	}
}
