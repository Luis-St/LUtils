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

package net.luis.utils.io.codec.types.stream;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntStreamCodec}.<br>
 *
 * @author Luis-St
 */
class IntStreamCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, stream));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as int stream"));
	}
	
	@Test
	void encodeStartWithValidStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(1, 2, 3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.empty();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of()), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(42);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(-1, -2, -3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1), new JsonPrimitive(-2), new JsonPrimitive(-3))), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1)))));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as int stream"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new int[] { 1, 2, 3 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of()));
		assertTrue(result.isSuccess());
		assertArrayEquals(new int[] {}, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(42))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new int[] { 42 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(-1), new JsonPrimitive(-2), new JsonPrimitive(-3))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new int[] { -1, -2, -3 }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		Result<IntStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		IntStreamCodec codec = new IntStreamCodec();
		assertEquals("IntStreamCodec", codec.toString());
	}
}
