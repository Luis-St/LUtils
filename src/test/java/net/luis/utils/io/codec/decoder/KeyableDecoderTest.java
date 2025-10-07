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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.ResultingFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyableDecoder}.<br>
 *
 * @author Luis-St
 */
class KeyableDecoderTest {
	
	@Test
	void ofNullChecks() {
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(null, ResultingFunction.direct(Integer::valueOf)));
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(INTEGER, null));
	}
	
	@Test
	void ofCreatesValidDecoder() {
		KeyableDecoder<Integer> decoder = KeyableDecoder.of(INTEGER, ResultingFunction.direct(Integer::valueOf));
		assertNotNull(decoder);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> result = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(typeProvider, null));
	}
	
	@Test
	void decodeKeyWithValidKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = INTEGER;
		
		Result<Integer> result = decoder.decodeKey(typeProvider, "42");
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
		
		Result<Integer> negativeResult = decoder.decodeKey(typeProvider, "-42");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.resultOrThrow());
		
		Result<Integer> zeroResult = decoder.decodeKey(typeProvider, "0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = INTEGER;
		
		Result<Integer> result = decoder.decodeKey(typeProvider, "not-a-number");
		assertTrue(result.isError());
		
		Result<Integer> emptyResult = decoder.decodeKey(typeProvider, "");
		assertTrue(emptyResult.isError());
		
		Result<Integer> spaceResult = decoder.decodeKey(typeProvider, " ");
		assertTrue(spaceResult.isError());
	}
	
	@Test
	void decodeKeyWithDoubleValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Double> decoder = DOUBLE;
		
		Result<Double> result = decoder.decodeKey(typeProvider, "42.5");
		assertTrue(result.isSuccess());
		assertEquals(42.5, result.resultOrThrow());
		
		Result<Double> scientificResult = decoder.decodeKey(typeProvider, "1.23e-4");
		assertTrue(scientificResult.isSuccess());
		assertEquals(1.23e-4, scientificResult.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithStringValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<String> decoder = STRING;
		
		Result<String> result = decoder.decodeKey(typeProvider, "hello");
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
		
		Result<String> emptyResult = decoder.decodeKey(typeProvider, "");
		assertTrue(emptyResult.isSuccess());
		assertEquals("", emptyResult.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithCustomKeyDecoder() {
		KeyableDecoder<Integer> decoder = KeyableDecoder.of(INTEGER, ResultingFunction.direct(key -> {
			if ("special".equals(key)) {
				return 999;
			}
			try {
				return Integer.parseInt(key);
			} catch (NumberFormatException e) {
				return null;
			}
		}));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> specialResult = decoder.decodeKey(typeProvider, "special");
		assertTrue(specialResult.isSuccess());
		assertEquals(999, specialResult.resultOrThrow());
		
		Result<Integer> numberResult = decoder.decodeKey(typeProvider, "42");
		assertTrue(numberResult.isSuccess());
		assertEquals(42, numberResult.resultOrThrow());
		
		Result<Integer> invalidResult = decoder.decodeKey(typeProvider, "invalid");
		assertTrue(invalidResult.isSuccess());
		assertNull(invalidResult.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithResultingKeyDecoder() {
		KeyableDecoder<Integer> decoder = KeyableDecoder.of(INTEGER, key -> {
			if ("special".equals(key)) {
				return Result.success(999);
			}
			try {
				return Result.success(Integer.parseInt(key));
			} catch (NumberFormatException e) {
				return Result.error("Invalid number: " + key);
			}
		});
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> specialResult = decoder.decodeKey(typeProvider, "special");
		assertTrue(specialResult.isSuccess());
		assertEquals(999, specialResult.resultOrThrow());
		
		Result<Integer> numberResult = decoder.decodeKey(typeProvider, "42");
		assertTrue(numberResult.isSuccess());
		assertEquals(42, numberResult.resultOrThrow());
		
		Result<Integer> invalidResult = decoder.decodeKey(typeProvider, "invalid");
		assertTrue(invalidResult.isError());
		assertTrue(invalidResult.errorOrThrow().contains("Invalid number"));
	}
	
	@Test
	void decodeKeyWithNullReturningKeyDecoder() {
		KeyableDecoder<String> decoder = KeyableDecoder.of(STRING, ResultingFunction.direct(key -> null));
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> result = decoder.decodeKey(typeProvider, "anything");
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void inheritsDecoderBehavior() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = INTEGER;
		
		Result<Integer> normalDecodeResult = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(normalDecodeResult.isSuccess());
		assertEquals(42, normalDecodeResult.resultOrThrow());
		
		Result<Integer> normalDecodeErrorResult = decoder.decodeStart(typeProvider, new JsonPrimitive("invalid"));
		assertTrue(normalDecodeErrorResult.isError());
	}
}
