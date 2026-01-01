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
 * Test class for {@link ByteCodec}.<br>
 *
 * @author Luis-St
 */
class ByteCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		Byte value = 42;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as byte"));
	}
	
	@Test
	void encodeStartWithPositiveValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive((byte) 42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) -42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive((byte) -42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), (byte) 0);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive((byte) 0), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Byte.MAX_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Byte.MAX_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), Byte.MIN_VALUE);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(Byte.MIN_VALUE), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<String> result = codec.encodeKey((byte) 42);
		assertTrue(result.isSuccess());
		assertEquals("42", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<String> result = codec.encodeKey((byte) -42);
		assertTrue(result.isSuccess());
		assertEquals("-42", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive((byte) 42)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as byte"));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42));
		assertTrue(result.isSuccess());
		assertEquals((byte) 42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -42));
		assertTrue(result.isSuccess());
		assertEquals((byte) -42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMaxValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Byte.MAX_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Byte.MAX_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithMinValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Byte.MIN_VALUE));
		assertTrue(result.isSuccess());
		assertEquals(Byte.MIN_VALUE, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeKey("42");
		assertTrue(result.isSuccess());
		assertEquals((byte) 42, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeKey("-42");
		assertTrue(result.isSuccess());
		assertEquals((byte) -42, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as byte"));
	}
	
	@Test
	void decodeKeyWithOutOfRangeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Result<Byte> result = codec.decodeKey("999");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key '999' as byte"));
	}
	
	@Test
	void toStringRepresentation() {
		ByteCodec codec = new ByteCodec();
		assertEquals("ByteCodec", codec.toString());
	}
}
