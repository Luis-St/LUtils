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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.ResultingFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyableEncoder}.<br>
 *
 * @author Luis-St
 */
class KeyableEncoderTest {
	
	@Test
	void ofNullChecks() {
		assertThrows(NullPointerException.class, () -> KeyableEncoder.of(null, ResultingFunction.direct(String::valueOf)));
		assertThrows(NullPointerException.class, () -> KeyableEncoder.of(Codec.INTEGER, null));
	}
	
	@Test
	void ofCreatesValidEncoder() {
		KeyableEncoder<Integer> encoder = KeyableEncoder.of(Codec.INTEGER, ResultingFunction.direct(String::valueOf));
		assertNotNull(encoder);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Integer> encoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> encoder.encodeKey(null, 1));
		assertThrows(NullPointerException.class, () -> encoder.encodeKey(typeProvider, null));
	}
	
	@Test
	void encodeKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Integer> encoder = Codec.INTEGER;
		
		Result<String> result = encoder.encodeKey(typeProvider, 42);
		assertTrue(result.isSuccess());
		assertEquals("42", result.orThrow());
		
		Result<String> negativeResult = encoder.encodeKey(typeProvider, -42);
		assertTrue(negativeResult.isSuccess());
		assertEquals("-42", negativeResult.orThrow());
		
		Result<String> zeroResult = encoder.encodeKey(typeProvider, 0);
		assertTrue(zeroResult.isSuccess());
		assertEquals("0", zeroResult.orThrow());
	}
	
	@Test
	void encodeKeyWithDoubleValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Double> encoder = Codec.DOUBLE;
		
		Result<String> result = encoder.encodeKey(typeProvider, 42.5);
		assertTrue(result.isSuccess());
		assertEquals("42.5", result.orThrow());
		
		Result<String> scientificResult = encoder.encodeKey(typeProvider, 1.23e-4);
		assertTrue(scientificResult.isSuccess());
		assertEquals("1.23E-4", scientificResult.orThrow());
	}
	
	@Test
	void encodeKeyWithStringValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<String> encoder = Codec.STRING;
		
		Result<String> result = encoder.encodeKey(typeProvider, "hello");
		assertTrue(result.isSuccess());
		assertEquals("hello", result.orThrow());
		
		Result<String> emptyResult = encoder.encodeKey(typeProvider, "");
		assertTrue(emptyResult.isSuccess());
		assertEquals("", emptyResult.orThrow());
		
		Result<String> spaceResult = encoder.encodeKey(typeProvider, " ");
		assertTrue(spaceResult.isSuccess());
		assertEquals(" ", spaceResult.orThrow());
	}
	
	@Test
	void encodeKeyWithSpecialNumbers() { // Currently not supported by the JsonTypeProvider
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Double> encoder = Codec.DOUBLE;
		
		Result<String> infinityResult = encoder.encodeKey(typeProvider, Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isError());
		
		Result<String> negInfinityResult = encoder.encodeKey(typeProvider, Double.NEGATIVE_INFINITY);
		assertTrue(negInfinityResult.isError());
		
		Result<String> nanResult = encoder.encodeKey(typeProvider, Double.NaN);
		assertTrue(nanResult.isError());
	}
	
	@Test
	void encodeKeyWithCustomKeyEncoder() {
		KeyableEncoder<Integer> encoder = KeyableEncoder.of(Codec.INTEGER, ResultingFunction.direct(value -> {
			if (value == 999) {
				return "special";
			}
			return String.valueOf(value);
		}));
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> specialResult = encoder.encodeKey(typeProvider, 999);
		assertTrue(specialResult.isSuccess());
		assertEquals("special", specialResult.orThrow());
		
		Result<String> numberResult = encoder.encodeKey(typeProvider, 42);
		assertTrue(numberResult.isSuccess());
		assertEquals("42", numberResult.orThrow());
	}
	
	@Test
	void encodeKeyWithResultingKeyEncoder() {
		KeyableEncoder<Integer> encoder = KeyableEncoder.of(Codec.INTEGER, value -> {
			if (value == 999) {
				return Result.success("special");
			}
			if (value < 0) {
				return Result.error("Negative numbers not allowed");
			}
			return Result.success(String.valueOf(value));
		});
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> specialResult = encoder.encodeKey(typeProvider, 999);
		assertTrue(specialResult.isSuccess());
		assertEquals("special", specialResult.orThrow());
		
		Result<String> numberResult = encoder.encodeKey(typeProvider, 42);
		assertTrue(numberResult.isSuccess());
		assertEquals("42", numberResult.orThrow());
		
		Result<String> errorResult = encoder.encodeKey(typeProvider, -5);
		assertTrue(errorResult.isError());
		assertTrue(errorResult.errorOrThrow().contains("Negative numbers not allowed"));
	}
	
	@Test
	void encodeKeyWithNullReturningKeyEncoder() {
		KeyableEncoder<String> encoder = KeyableEncoder.of(Codec.STRING, ResultingFunction.direct(value -> null));
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> result = encoder.encodeKey(typeProvider, "anything");
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
	}
	
	@Test
	void encodeKeyWithExceptionThrowingKeyEncoder() {
		KeyableEncoder<String> encoder = KeyableEncoder.of(Codec.STRING, value -> {
			if ("bad".equals(value)) {
				return Result.error("Bad value");
			}
			return Result.success(value);
		});
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<String> errorResult = encoder.encodeKey(typeProvider, "bad");
		assertTrue(errorResult.isError());
		assertTrue(errorResult.errorOrThrow().contains("Bad value"));
		
		Result<String> successResult = encoder.encodeKey(typeProvider, "good");
		assertTrue(successResult.isSuccess());
		assertEquals("good", successResult.orThrow());
	}
	
	@Test
	void inheritsEncoderBehavior() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		KeyableEncoder<Integer> encoder = Codec.INTEGER;
		
		Result<JsonElement> normalEncodeResult = encoder.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(normalEncodeResult.isSuccess());
		assertEquals(new JsonPrimitive(42), normalEncodeResult.orThrow());
		
		Result<JsonElement> normalEncodeErrorResult = encoder.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(normalEncodeErrorResult.isError());
	}
}
