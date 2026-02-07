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
 * Test class for {@link FloatCodec}.<br>
 *
 * @author Luis-St
 */
class FloatCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		Float value = 3.14f;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as float"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 3.14f);
		assertEquals(new JsonPrimitive(3.14f), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), -3.14f);
		assertEquals(new JsonPrimitive(-3.14f), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 0.0f);
		assertEquals(new JsonPrimitive(0.0f), result);
	}
	
	@Test
	void encodeWithMaxValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Float.MAX_VALUE);
		assertEquals(new JsonPrimitive(Float.MAX_VALUE), result);
	}
	
	@Test
	void encodeWithMinValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Float.MIN_VALUE);
		assertEquals(new JsonPrimitive(Float.MIN_VALUE), result);
	}
	
	@Test
	void encodeWithPositiveInfinity() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Float.POSITIVE_INFINITY);
		assertEquals(new JsonPrimitive(Float.POSITIVE_INFINITY), result);
	}
	
	@Test
	void encodeWithNegativeInfinity() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Float.NEGATIVE_INFINITY);
		assertEquals(new JsonPrimitive(Float.NEGATIVE_INFINITY), result);
	}
	
	@Test
	void encodeWithNaN() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Float.NaN);
		assertEquals(new JsonPrimitive(Float.NaN), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		String result = codec.encodeKey(3.14f);
		assertEquals("3.14", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		String result = codec.encodeKey(-3.14f);
		assertEquals("-3.14", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as float"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertEquals(3.14f, result, 0.001f);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f));
		assertEquals(-3.14f, result, 0.001f);
	}
	
	@Test
	void decodeWithMaxValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MAX_VALUE));
		assertEquals(Float.MAX_VALUE, result);
	}
	
	@Test
	void decodeWithMinValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MIN_VALUE));
		assertEquals(Float.MIN_VALUE, result);
	}
	
	@Test
	void decodeWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decodeKey("3.14");
		assertEquals(3.14f, result, 0.001f);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decodeKey("-3.14");
		assertEquals(-3.14f, result, 0.001f);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as float"));
	}
	
	@Test
	void decodeKeyWithInfinity() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decodeKey("Infinity");
		assertEquals(Float.POSITIVE_INFINITY, result);
	}
	
	@Test
	void decodeKeyWithNegativeInfinity() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decodeKey("-Infinity");
		assertEquals(Float.NEGATIVE_INFINITY, result);
	}
	
	@Test
	void decodeKeyWithNaN() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Float result = codec.decodeKey("NaN");
		assertTrue(Float.isNaN(result));
	}
	
	@Test
	void toStringRepresentation() {
		FloatCodec codec = new FloatCodec();
		assertEquals("FloatCodec", codec.toString());
	}
}
