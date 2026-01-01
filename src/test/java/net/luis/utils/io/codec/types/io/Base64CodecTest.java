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

package net.luis.utils.io.codec.types.io;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Base64Codec}.<br>
 *
 * @author Luis-St
 */
class Base64CodecTest {

	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 1, 2, 3 };

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as base 64"));
	}

	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = {};

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(""), result.resultOrThrow());
	}

	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 42 };

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		String expected = Base64.getEncoder().encodeToString(array);
		assertEquals(new JsonPrimitive(expected), result.resultOrThrow());
	}

	@Test
	void encodeStartWithMultipleElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] array = { 1, 2, 3, 4, 5 };

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		String expected = Base64.getEncoder().encodeToString(array);
		assertEquals(new JsonPrimitive(expected), result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("AQID")));
		assertThrows(NullPointerException.class, () -> codec.decodeStart(typeProvider, null, new JsonPrimitive("AQID")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();

		Result<byte[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as base 64"));
	}

	@Test
	void decodeStartWithEmptyString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonPrimitive value = new JsonPrimitive("");

		Result<byte[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertArrayEquals(new byte[] {}, result.resultOrThrow());
	}

	@Test
	void decodeStartWithValidBase64() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] expected = { 1, 2, 3, 4, 5 };
		String base64 = Base64.getEncoder().encodeToString(expected);
		JsonPrimitive value = new JsonPrimitive(base64);

		Result<byte[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertArrayEquals(expected, result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidBase64() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonPrimitive value = new JsonPrimitive("!!!invalid-base64!!!");

		Result<byte[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode base 64 string"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		JsonArray value = new JsonArray();

		Result<byte[]> result = codec.decodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode base 64 from a non-string value"));
	}

	@Test
	void roundTripEncoding() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<byte[]> codec = new Base64Codec();
		byte[] original = { 72, 101, 108, 108, 111, 32, 87, 111, 114, 108, 100 };

		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), original);
		assertTrue(encodeResult.isSuccess());

		Result<byte[]> decodeResult = codec.decodeStart(typeProvider, typeProvider.empty(), encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		assertArrayEquals(original, decodeResult.resultOrThrow());
	}

	@Test
	void toStringRepresentation() {
		Base64Codec codec = new Base64Codec();
		assertEquals("Base64Codec", codec.toString());
	}
}
