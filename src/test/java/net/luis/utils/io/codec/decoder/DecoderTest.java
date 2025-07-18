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
import net.luis.utils.io.codec.ResultMappingFunction;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

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
		Decoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decode(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		Integer result = decoder.decode(typeProvider, new JsonPrimitive(42));
		assertEquals(42, result);
	}
	
	@Test
	void decodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, null));
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> decoder.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		Result<Integer> result = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void decodeStartWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		Result<Integer> result = decoder.decodeStart(typeProvider, null);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER;
		
		Result<Integer> result = decoder.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithBooleanValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Boolean> decoder = Codec.BOOLEAN;
		
		Result<Boolean> trueResult = decoder.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(trueResult.isSuccess());
		assertTrue(trueResult.orThrow());
		
		Result<Boolean> falseResult = decoder.decodeStart(typeProvider, new JsonPrimitive(false));
		assertTrue(falseResult.isSuccess());
		assertFalse(falseResult.orThrow());
	}
	
	@Test
	void decodeStartWithStringValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = Codec.STRING;
		
		Result<String> result = decoder.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.orThrow());
		
		Result<String> emptyResult = decoder.decodeStart(typeProvider, new JsonPrimitive(""));
		assertTrue(emptyResult.isSuccess());
		assertEquals("", emptyResult.orThrow());
	}
	
	@Test
	void mapDecoderNullChecks() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.mapDecoder(null));
	}
	
	@Test
	void mapDecoderWithSuccessfulMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = Codec.INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue));
		
		Result<Double> result = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42.0, result.orThrow());
	}
	
	@Test
	void mapDecoderWithFailingMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = Codec.INTEGER.mapDecoder(ResultMappingFunction.throwable(i -> {
			if (i == 0) {
				throw new IllegalArgumentException("Zero not allowed");
			}
			return i;
		}));
		
		Result<Integer> errorResult = decoder.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(errorResult.isError());
		assertEquals("Zero not allowed", errorResult.errorOrThrow());
		
		Result<Integer> successResult = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(successResult.isSuccess());
		assertEquals(42, successResult.orThrow());
	}
	
	@Test
	void mapDecoderWithDecodingError() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = Codec.INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue));
		
		Result<Double> result = decoder.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void mapDecoderChaining() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = Codec.INTEGER.mapDecoder(ResultMappingFunction.direct(Integer::doubleValue)).mapDecoder(ResultMappingFunction.direct(Object::toString));
		
		Result<String> result = decoder.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals("42.0", result.orThrow());
	}
}
