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

package net.luis.utils.io.codec.types.primitiv.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FloatCodec}.<br>
 *
 * @author Luis-St
 */
class FloatCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		Float value = 3.14f;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as float"));
	}
	
	@Test
	void encodeStartWithPositiveValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14f);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3.14f), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14f);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(-3.14f), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0f);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(0.0f), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.MAX_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Float.MAX_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.MIN_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Float.MIN_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithPositiveInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.POSITIVE_INFINITY);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Float.POSITIVE_INFINITY), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.NEGATIVE_INFINITY);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Float.NEGATIVE_INFINITY), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNaN() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Float.NaN);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Float.NaN), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<String> result = codec.encodeKey(3.14f);
		assertTrue(result.isSuccess());
		assertEquals("3.14", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<String> result = codec.encodeKey(-3.14f);
		assertTrue(result.isSuccess());
		assertEquals("-3.14", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive(3.14f)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as float"));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3.14f));
		assertTrue(result.isSuccess());
		assertEquals(3.14f, result.resultOrThrow(), 0.001f);
	}
	
	@Test
	void decodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(-3.14f));
		assertTrue(result.isSuccess());
		assertEquals(-3.14f, result.resultOrThrow(), 0.001f);
	}
	
	@Test
	void decodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MAX_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Float.MAX_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Float.MIN_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Float.MIN_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("3.14");
		assertTrue(result.isSuccess());
		assertEquals(3.14f, result.resultOrThrow(), 0.001f);
	}
	
	@Test
	void decodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("-3.14");
		assertTrue(result.isSuccess());
		assertEquals(-3.14f, result.resultOrThrow(), 0.001f);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as float"));
	}
	
	@Test
	void decodeKeyWithInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("Infinity");
		assertTrue(result.isSuccess());
		assertEquals(Float.POSITIVE_INFINITY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNegativeInfinity() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("-Infinity");
		assertTrue(result.isSuccess());
		assertEquals(Float.NEGATIVE_INFINITY, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNaN() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Float> codec = new FloatCodec();
		
		Result<Float> result = codec.decodeKey("NaN");
		assertTrue(result.isSuccess());
		assertTrue(Float.isNaN(result.resultOrThrow()));
	}
	
	@Test
	void toStringRepresentation() {
		FloatCodec codec = new FloatCodec();
		assertEquals("FloatCodec", codec.toString());
	}
}
