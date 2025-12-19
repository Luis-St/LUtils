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

package net.luis.utils.io.codec.types.stream;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LongStreamCodec}.<br>
 *
 * @author Luis-St
 */
class LongStreamCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(1L, 2L, 3L);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, stream));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as long stream"));
	}
	
	@Test
	void encodeStartWithValidStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(1L, 2L, 3L);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1L), new JsonPrimitive(2L), new JsonPrimitive(3L))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyStream() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.empty();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of()), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(42L);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(-1L, -2L, -3L);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1L), new JsonPrimitive(-2L), new JsonPrimitive(-3L))), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithLargeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(Long.MAX_VALUE, Long.MIN_VALUE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), stream);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(List.of(new JsonPrimitive(Long.MAX_VALUE), new JsonPrimitive(Long.MIN_VALUE))), result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1L)))));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as long stream"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1L), new JsonPrimitive(2L), new JsonPrimitive(3L))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { 1L, 2L, 3L }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of()));
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] {}, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(42L))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { 42L }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNegativeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(-1L), new JsonPrimitive(-2L), new JsonPrimitive(-3L))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { -1L, -2L, -3L }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithLargeValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(Long.MAX_VALUE), new JsonPrimitive(Long.MIN_VALUE))));
		assertTrue(result.isSuccess());
		assertArrayEquals(new long[] { Long.MAX_VALUE, Long.MIN_VALUE }, result.resultOrThrow().toArray());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		Result<LongStream> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42L));
		assertTrue(result.isError());
	}
	
	@Test
	void toStringRepresentation() {
		LongStreamCodec codec = new LongStreamCodec();
		assertEquals("LongStreamCodec", codec.toString());
	}
}
