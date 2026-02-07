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
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleStreamCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleStreamCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(1.0, 2.0, 3.0);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), stream));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, stream));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as double stream"));
	}
	
	@Test
	void encodeWithValidStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(1.0, 2.0, 3.0);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(1.0), new JsonPrimitive(2.0), new JsonPrimitive(3.0))), result);
	}
	
	@Test
	void encodeWithEmptyStream() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.empty();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of()), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(42.5);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(42.5))), result);
	}
	
	@Test
	void encodeWithNegativeValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(-1.5, -2.5, -3.5);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(-1.5), new JsonPrimitive(-2.5), new JsonPrimitive(-3.5))), result);
	}
	
	@Test
	void encodeWithLargeValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		DoubleStream stream = DoubleStream.of(Double.MAX_VALUE, Double.MIN_VALUE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), stream);
		assertEquals(new JsonArray(List.of(new JsonPrimitive(Double.MAX_VALUE), new JsonPrimitive(Double.MIN_VALUE))), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1.0)))));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as double stream"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DoubleStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(1.0), new JsonPrimitive(2.0), new JsonPrimitive(3.0))));
		assertArrayEquals(new double[] { 1.0, 2.0, 3.0 }, result.toArray());
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DoubleStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of()));
		assertArrayEquals(new double[] {}, result.toArray());
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DoubleStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(42.5))));
		assertArrayEquals(new double[] { 42.5 }, result.toArray());
	}
	
	@Test
	void decodeWithNegativeValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DoubleStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(-1.5), new JsonPrimitive(-2.5), new JsonPrimitive(-3.5))));
		assertArrayEquals(new double[] { -1.5, -2.5, -3.5 }, result.toArray());
	}
	
	@Test
	void decodeWithLargeValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		DoubleStream result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray(List.of(new JsonPrimitive(Double.MAX_VALUE), new JsonPrimitive(Double.MIN_VALUE))));
		assertArrayEquals(new double[] { Double.MAX_VALUE, Double.MIN_VALUE }, result.toArray());
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DoubleStream> codec = new DoubleStreamCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.0)));
	}
	
	@Test
	void toStringRepresentation() {
		DoubleStreamCodec codec = new DoubleStreamCodec();
		assertEquals("DoubleStreamCodec", codec.toString());
	}
}
