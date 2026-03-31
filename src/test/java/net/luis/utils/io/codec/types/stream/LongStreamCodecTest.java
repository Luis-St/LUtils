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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(1L, 2L, 3L);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, stream));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as long stream"));
	}
	
	@Test
	void encodeWithValidStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(1L, 2L, 3L);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1L), new JsonPrimitive(2L), new JsonPrimitive(3L))), result);
	}
	
	@Test
	void encodeWithEmptyStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.empty();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of()), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(42L);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42L))), result);
	}
	
	@Test
	void encodeWithNegativeValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(-1L, -2L, -3L);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1L), new JsonPrimitive(-2L), new JsonPrimitive(-3L))), result);
	}
	
	@Test
	void encodeWithLargeValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		LongStream stream = LongStream.of(Long.MAX_VALUE, Long.MIN_VALUE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(Long.MAX_VALUE), new JsonPrimitive(Long.MIN_VALUE))), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1L)))));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as long stream"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		LongStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1L), new JsonPrimitive(2L), new JsonPrimitive(3L))));
		assertArrayEquals(new long[] { 1L, 2L, 3L }, result.toArray());
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		LongStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of()));
		assertArrayEquals(new long[] {}, result.toArray());
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		LongStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(42L))));
		assertArrayEquals(new long[] { 42L }, result.toArray());
	}
	
	@Test
	void decodeWithNegativeValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		LongStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(-1L), new JsonPrimitive(-2L), new JsonPrimitive(-3L))));
		assertArrayEquals(new long[] { -1L, -2L, -3L }, result.toArray());
	}
	
	@Test
	void decodeWithLargeValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		LongStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(Long.MAX_VALUE), new JsonPrimitive(Long.MIN_VALUE))));
		assertArrayEquals(new long[] { Long.MAX_VALUE, Long.MIN_VALUE }, result.toArray());
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LongStream> codec = new LongStreamCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42L)));
	}
	
	@Test
	void toStringRepresentation() {
		LongStreamCodec codec = new LongStreamCodec();
		assertEquals("LongStreamCodec", codec.toString());
	}
}
