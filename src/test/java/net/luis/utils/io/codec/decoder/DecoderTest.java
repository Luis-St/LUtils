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

package net.luis.utils.io.codec.decoder;

import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import net.luis.utils.util.result.ResultMappingFunction;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Decoder}.<br>
 *
 * @author Luis-St
 */
class DecoderTest {
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> Codecs.INTEGER.decode(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Integer result = Codecs.INTEGER.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42, result);
	}
	
	@Test
	void decodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = INTEGER;
		
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), null));
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonPrimitive testValue = new JsonPrimitive(1);
		assertThrows(NullPointerException.class, () -> Codecs.INTEGER.decodeStart(null, testValue, testValue));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> result = Codecs.INTEGER.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> result = Codecs.INTEGER.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Result<Integer> result = Codecs.INTEGER.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithBooleanValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Boolean> decoder = BOOLEAN;
		
		Result<Boolean> trueResult = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(trueResult.isSuccess());
		assertTrue(trueResult.resultOrThrow());
		
		Result<Boolean> falseResult = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(false));
		assertTrue(falseResult.isSuccess());
		assertFalse(falseResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithStringValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = STRING;
		
		Result<String> result = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
		
		Result<String> emptyResult = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertTrue(emptyResult.isSuccess());
		assertEquals("", emptyResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSpecialNumbers() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> encoder = DOUBLE;
		
		Result<Double> infinityResult = encoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.POSITIVE_INFINITY));
		assertTrue(infinityResult.isSuccess());
		assertEquals(Double.POSITIVE_INFINITY, infinityResult.resultOrThrow());
		
		Result<Double> negInfinityResult = encoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NEGATIVE_INFINITY));
		assertTrue(negInfinityResult.isSuccess());
		assertEquals(Double.NEGATIVE_INFINITY, negInfinityResult.resultOrThrow());
		
		Result<Double> nanResult = encoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NaN));
		assertTrue(nanResult.isSuccess());
		assertEquals(Double.NaN, nanResult.resultOrThrow());
	}
	
	@Test
	void mapDecoderNullChecks() {
		assertThrows(NullPointerException.class, () -> INTEGER.mapDecoder(null));
	}
	
	@Test
	void mapDecoderWithSuccessfulMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue));
		
		Result<Double> result = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42.0, result.resultOrThrow());
	}
	
	@Test
	void mapDecoderWithFailingMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = INTEGER.mapDecoder(ResultMappingFunction.throwable(i -> {
			if (i == 0) {
				throw new IllegalArgumentException("Zero not allowed");
			}
			return i;
		}));
		
		Result<Integer> errorResult = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(0));
		assertTrue(errorResult.isError());
		assertEquals("Zero not allowed", errorResult.errorOrThrow());
		
		Result<Integer> successResult = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(successResult.isSuccess());
		assertEquals(42, successResult.resultOrThrow());
	}
	
	@Test
	void mapDecoderWithDecodingError() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue));
		
		Result<Double> result = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void mapDecoderChaining() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue)).mapDecoder(ResultMappingFunction.direct(Object::toString));
		
		Result<String> result = decoder.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals("42.0", result.resultOrThrow());
	}
}
