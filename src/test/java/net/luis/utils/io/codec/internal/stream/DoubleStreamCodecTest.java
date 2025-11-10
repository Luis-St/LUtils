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

package net.luis.utils.io.codec.internal.stream;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleStreamCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleStreamCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(1.0, 2.0, 3.0);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, stream));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as double stream"));
	}
	
	@Test
	void encodeStartWithValidStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(1.0, 2.0, 3.0);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1.0), new JsonPrimitive(2.0), new JsonPrimitive(3.0))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.empty();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of()), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(42.5);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.5))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(-1.5, -2.5, -3.5);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1.5), new JsonPrimitive(-2.5), new JsonPrimitive(-3.5))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithLargeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(Double.MAX_VALUE, Double.MIN_VALUE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(Double.MAX_VALUE), new JsonPrimitive(Double.MIN_VALUE))), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray(List.of(new JsonPrimitive(1.0)))));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as double stream"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonArray(List.of(new JsonPrimitive(1.0), new JsonPrimitive(2.0), new JsonPrimitive(3.0))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { 1.0, 2.0, 3.0 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonArray(List.of()));
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] {}, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonArray(List.of(new JsonPrimitive(42.5))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { 42.5 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonArray(List.of(new JsonPrimitive(-1.5), new JsonPrimitive(-2.5), new JsonPrimitive(-3.5))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { -1.5, -2.5, -3.5 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithLargeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonArray(List.of(new JsonPrimitive(Double.MAX_VALUE), new JsonPrimitive(Double.MIN_VALUE))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new double[] { Double.MAX_VALUE, Double.MIN_VALUE }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		Result<DoubleStream> result = codec.decodeStart(typeProvider, new JsonPrimitive(42.0));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		DoubleStreamCodec codec = new DoubleStreamCodec();
		assertEquals("DoubleStreamCodec", codec.toString());
	}
}
