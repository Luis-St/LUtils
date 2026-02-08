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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntStreamCodec}.<br>
 *
 * @author Luis-St
 */
class IntStreamCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(1, 2, 3);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, stream));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as int stream"));
	}
	
	@Test
	void encodeWithValidStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(1, 2, 3);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3))), result);
	}
	
	@Test
	void encodeWithEmptyStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.empty();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of()), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(42);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42))), result);
	}
	
	@Test
	void encodeWithNegativeValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		IntStream stream = IntStream.of(-1, -2, -3);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1), new JsonPrimitive(-2), new JsonPrimitive(-3))), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1)))));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as int stream"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		IntStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1), new JsonPrimitive(2), new JsonPrimitive(3))));
		assertArrayEquals(new int[] { 1, 2, 3 }, result.toArray());
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		IntStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of()));
		assertArrayEquals(new int[] {}, result.toArray());
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		IntStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(42))));
		assertArrayEquals(new int[] { 42 }, result.toArray());
	}
	
	@Test
	void decodeWithNegativeValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		IntStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(-1), new JsonPrimitive(-2), new JsonPrimitive(-3))));
		assertArrayEquals(new int[] { -1, -2, -3 }, result.toArray());
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<IntStream> codec = new IntStreamCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		IntStreamCodec codec = new IntStreamCodec();
		assertEquals("IntStreamCodec", codec.toString());
	}
}
