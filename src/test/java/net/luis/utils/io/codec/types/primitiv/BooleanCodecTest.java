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

package net.luis.utils.io.codec.types.primitiv;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BooleanCodec}.<br>
 *
 * @author Luis-St
 */
class BooleanCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		Boolean value = true;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as boolean"));
	}
	
	@Test
	void encodeStartWithTrue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), true);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(true), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithFalse() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), false);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(false), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithTrue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<String> result = codec.encodeKey(true);
		assertTrue(result.isSuccess());
		assertEquals("true", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithFalse() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<String> result = codec.encodeKey(false);
		assertTrue(result.isSuccess());
		assertEquals("false", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive(true)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as boolean"));
	}
	
	@Test
	void decodeStartWithTrue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(result.isSuccess());
		assertEquals(true, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithFalse() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(false));
		assertTrue(result.isSuccess());
		assertEquals(false, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonBoolean() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithTrue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeKey("true");
		assertTrue(result.isSuccess());
		assertEquals(true, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithFalse() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeKey("false");
		assertTrue(result.isSuccess());
		assertEquals(false, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Result<Boolean> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as boolean"));
	}
	
	@Test
	void toStringRepresentation() {
		BooleanCodec codec = new BooleanCodec();
		assertEquals("BooleanCodec", codec.toString());
	}
}
