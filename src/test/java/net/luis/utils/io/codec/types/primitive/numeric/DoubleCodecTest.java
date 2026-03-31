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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		Double value = 3.141592653589793;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as double"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 3.141592653589793);
		assertEquals(new JsonPrimitive(3.141592653589793), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), -3.141592653589793);
		assertEquals(new JsonPrimitive(-3.141592653589793), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 0.0);
		assertEquals(new JsonPrimitive(0.0), result);
	}
	
	@Test
	void encodeWithMaxValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Double.MAX_VALUE);
		assertEquals(new JsonPrimitive(Double.MAX_VALUE), result);
	}
	
	@Test
	void encodeWithMinValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Double.MIN_VALUE);
		assertEquals(new JsonPrimitive(Double.MIN_VALUE), result);
	}
	
	@Test
	void encodeWithPositiveInfinity() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertEquals(new JsonPrimitive(Double.POSITIVE_INFINITY), result);
	}
	
	@Test
	void encodeWithNegativeInfinity() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Double.NEGATIVE_INFINITY);
		assertEquals(new JsonPrimitive(Double.NEGATIVE_INFINITY), result);
	}
	
	@Test
	void encodeWithNaN() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Double.NaN);
		assertEquals(new JsonPrimitive(Double.NaN), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		String result = codec.encodeKey(3.141592653589793);
		assertEquals("3.141592653589793", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		String result = codec.encodeKey(-3.141592653589793);
		assertEquals("-3.141592653589793", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(3.141592653589793)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null as double"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.141592653589793));
		assertEquals(3.141592653589793, result, 0.000001);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.141592653589793));
		assertEquals(-3.141592653589793, result, 0.000001);
	}
	
	@Test
	void decodeWithMaxValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.MAX_VALUE));
		assertEquals(Double.MAX_VALUE, result);
	}
	
	@Test
	void decodeWithMinValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.MIN_VALUE));
		assertEquals(Double.MIN_VALUE, result);
	}
	
	@Test
	void decodeWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decodeKey("3.141592653589793");
		assertEquals(3.141592653589793, result, 0.000001);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decodeKey("-3.141592653589793");
		assertEquals(-3.141592653589793, result, 0.000001);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as double"));
	}
	
	@Test
	void decodeKeyWithInfinity() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decodeKey("Infinity");
		assertEquals(Double.POSITIVE_INFINITY, result);
	}
	
	@Test
	void decodeKeyWithNegativeInfinity() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decodeKey("-Infinity");
		assertEquals(Double.NEGATIVE_INFINITY, result);
	}
	
	@Test
	void decodeKeyWithNaN() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Double result = codec.decodeKey("NaN");
		assertTrue(Double.isNaN(result));
	}
	
	@Test
	void toStringRepresentation() {
		DoubleCodec codec = new DoubleCodec();
		assertEquals("DoubleCodec", codec.toString());
	}
}
