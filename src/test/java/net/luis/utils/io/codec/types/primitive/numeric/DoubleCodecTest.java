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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DoubleCodec}.<br>
 *
 * @author Luis-St
 */
class DoubleCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		Double value = 3.141592653589793;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as double"));
	}
	
	@Test
	void encodeStartWithPositiveValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.141592653589793);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3.141592653589793), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.141592653589793);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(-3.141592653589793), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(0.0), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.MAX_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Double.MAX_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.MIN_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Double.MIN_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithPositiveInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Double.POSITIVE_INFINITY), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.NEGATIVE_INFINITY);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Double.NEGATIVE_INFINITY), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNaN() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Double.NaN);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Double.NaN), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<String> result = codec.encodeKey(3.141592653589793);
		assertTrue(result.isSuccess());
		assertEquals("3.141592653589793", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<String> result = codec.encodeKey(-3.141592653589793);
		assertTrue(result.isSuccess());
		assertEquals("-3.141592653589793", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive(3.141592653589793)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as double"));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.141592653589793));
		assertTrue(result.isSuccess());
		assertEquals(3.141592653589793, result.resultOrThrow(), 0.000001);
	}
	
	@Test
	void decodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.141592653589793));
		assertTrue(result.isSuccess());
		assertEquals(-3.141592653589793, result.resultOrThrow(), 0.000001);
	}
	
	@Test
	void decodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.MAX_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Double.MAX_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.MIN_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Double.MIN_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("3.141592653589793");
		assertTrue(result.isSuccess());
		assertEquals(3.141592653589793, result.resultOrThrow(), 0.000001);
	}
	
	@Test
	void decodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("-3.141592653589793");
		assertTrue(result.isSuccess());
		assertEquals(-3.141592653589793, result.resultOrThrow(), 0.000001);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as double"));
	}
	
	@Test
	void decodeKeyWithInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("Infinity");
		assertTrue(result.isSuccess());
		assertEquals(Double.POSITIVE_INFINITY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNegativeInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("-Infinity");
		assertTrue(result.isSuccess());
		assertEquals(Double.NEGATIVE_INFINITY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNaN() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Double> codec = new DoubleCodec();
		
		Result<Double> result = codec.decodeKey("NaN");
		assertTrue(result.isSuccess());
		assertTrue(Double.isNaN(result.resultOrThrow()));
	}
	
	@Test
	void toStringRepresentation() {
		DoubleCodec codec = new DoubleCodec();
		assertEquals("DoubleCodec", codec.toString());
	}
}
