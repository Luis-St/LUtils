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

package net.luis.utils.io.codec.internal.primitiv;

import net.luis.utils.io.codec.KeyableCodec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringCodec}.<br>
 *
 * @author Luis-St
 */
class StringCodecTest {
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		String value = "test";
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as string"));
	}
	
	@Test
	void encodeStartWithSimpleString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "hello");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("hello"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(""), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSpecialCharacters() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello, World! @#$%");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("Hello, World! @#$%"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithMultilineString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "line1\nline2\nline3");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("line1\nline2\nline3"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithUnicodeCharacters() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "Hello \u4E16\u754C");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("Hello \u4E16\u754C"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, "test"));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
	}
	
	@Test
	void encodeKeyWithString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.encodeKey(typeProvider, "myKey");
		assertTrue(result.isSuccess());
		assertEquals("myKey", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.encodeKey(typeProvider, "");
		assertTrue(result.isSuccess());
		assertEquals("", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("test")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as string"));
	}
	
	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeStart(typeProvider, new JsonPrimitive(""));
		assertTrue(result.isSuccess());
		assertEquals("", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSpecialCharacters() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeStart(typeProvider, new JsonPrimitive("test@123"));
		assertTrue(result.isSuccess());
		assertEquals("test@123", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "test"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
	}
	
	@Test
	void decodeKeyWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeKey(typeProvider, "myKey");
		assertTrue(result.isSuccess());
		assertEquals("myKey", result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableCodec<String> codec = new StringCodec();
		
		Result<String> result = codec.decodeKey(typeProvider, "");
		assertTrue(result.isSuccess());
		assertEquals("", result.resultOrThrow());
	}
	
	@Test
	void toStringRepresentation() {
		StringCodec codec = new StringCodec();
		assertEquals("StringCodec", codec.toString());
	}
}
