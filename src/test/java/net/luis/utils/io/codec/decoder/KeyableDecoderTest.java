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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyableDecoder}.<br>
 *
 * @author Luis-St
 */
class KeyableDecoderTest {
	
	@Test
	void ofNullChecks() {
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(null, Integer::valueOf));
		assertThrows(NullPointerException.class, () -> KeyableDecoder.of(Codec.INTEGER, null));
	}
	
	@Test
	void ofCreatesValidDecoder() {
		KeyableDecoder<Integer> decoder = KeyableDecoder.of(Codec.INTEGER, Integer::valueOf);
		assertNotNull(decoder);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<Integer> result = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(typeProvider, null));
	}
	
	@Test
	void decodeKeyWithValidKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = Codec.INTEGER;
		
		Result<Integer> result = decoder.decodeKey(typeProvider, "42");
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
		
		Result<Integer> negativeResult = decoder.decodeKey(typeProvider, "-42");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.orThrow());
		
		Result<Integer> zeroResult = decoder.decodeKey(typeProvider, "0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
	}
	
	@Test
	void decodeKeyWithInvalidKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = Codec.INTEGER;
		
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
		KeyableDecoder<Double> decoder = Codec.DOUBLE;
		
		Result<Double> result = decoder.decodeKey(typeProvider, "42.5");
		assertTrue(result.isSuccess());
		assertEquals(42.5, result.orThrow());
		
		Result<Double> scientificResult = decoder.decodeKey(typeProvider, "1.23e-4");
		assertTrue(scientificResult.isSuccess());
		assertEquals(1.23e-4, scientificResult.orThrow());
	}
	
	@Test
	void decodeKeyWithStringValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<String> decoder = Codec.STRING;
		
		Result<String> result = decoder.decodeKey(typeProvider, "hello");
		assertTrue(result.isSuccess());
		assertEquals("hello", result.orThrow());
		
		Result<String> emptyResult = decoder.decodeKey(typeProvider, "");
		assertTrue(emptyResult.isSuccess());
		assertEquals("", emptyResult.orThrow());
	}
	
	@Test
	void decodeKeyWithCustomKeyDecoder() {
		KeyableDecoder<Integer> decoder = KeyableDecoder.of(Codec.INTEGER, key -> {
			if ("special".equals(key)) {
				return 999;
			}
			try {
				return Integer.parseInt(key);
			} catch (NumberFormatException e) {
				return null;
			}
		});
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> specialResult = decoder.decodeKey(typeProvider, "special");
		assertTrue(specialResult.isSuccess());
		assertEquals(999, specialResult.orThrow());
		
		Result<Integer> numberResult = decoder.decodeKey(typeProvider, "42");
		assertTrue(numberResult.isSuccess());
		assertEquals(42, numberResult.orThrow());
		
		Result<Integer> invalidResult = decoder.decodeKey(typeProvider, "invalid");
		assertTrue(invalidResult.isError());
		assertTrue(invalidResult.errorOrThrow().contains("could not be converted back to a value"));
	}
	
	@Test
	void decodeKeyWithNullReturningKeyDecoder() {
		KeyableDecoder<String> decoder = KeyableDecoder.of(Codec.STRING, key -> null);
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> result = decoder.decodeKey(typeProvider, "anything");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("could not be converted back to a value"));
	}
	
	@Test
	void inheritsDecoderBehavior() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableDecoder<Integer> decoder = Codec.INTEGER;
		
		Result<Integer> normalDecodeResult = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(normalDecodeResult.isSuccess());
		assertEquals(42, normalDecodeResult.orThrow());
		
		Result<Integer> normalDecodeErrorResult = decoder.decodeStart(typeProvider, new JsonPrimitive("invalid"));
		assertTrue(normalDecodeErrorResult.isError());
	}
}