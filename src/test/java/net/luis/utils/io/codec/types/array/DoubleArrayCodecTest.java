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

package net.luis.utils.io.codec.types.array;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleArrayCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleArrayCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 1.0, 2.0, 3.0 };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as double array"));
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 42.5 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add(42.5);
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithMultipleElements() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		double[] array = { 1.1, 2.2, 3.3 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		JsonArray expected = new JsonArray();
		expected.add(1.1);
		expected.add(2.2);
		expected.add(3.3);
		assertEquals(expected, result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as double array"));
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		
		double[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new double[] {}, result);
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		array.add(42.5);
		
		double[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new double[] { 42.5 }, result);
	}
	
	@Test
	void decodeWithMultipleElements() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		JsonArray array = new JsonArray();
		array.add(1.1);
		array.add(2.2);
		array.add(3.3);
		
		double[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new double[] { 1.1, 2.2, 3.3 }, result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<double[]> codec = new DoubleArrayCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42.5)));
	}
	
	@Test
	void toStringRepresentation() {
		DoubleArrayCodec codec = new DoubleArrayCodec();
		assertEquals("DoubleArrayCodec", codec.toString());
	}
}
