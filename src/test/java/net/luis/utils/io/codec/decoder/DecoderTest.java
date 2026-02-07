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
		assertThrows(NullPointerException.class, () -> Codecs.INTEGER.decode(null, new JsonPrimitive(1)));
		assertThrows(NullPointerException.class, () -> Codecs.INTEGER.decode(null, new JsonPrimitive(1), new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Integer result = Codecs.INTEGER.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42, result);
		
		Integer resultDirect = Codecs.INTEGER.decode(typeProvider, new JsonPrimitive(42));
		assertEquals(42, resultDirect);
	}
	
	@Test
	void decodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = INTEGER;
		
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), null));
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void decodeWithBooleanValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Boolean> decoder = BOOLEAN;
		
		Boolean trueResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(trueResult);
		
		Boolean falseResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(false));
		assertFalse(falseResult);
	}
	
	@Test
	void decodeWithStringValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = STRING;
		
		String result = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertEquals("hello", result);
		
		String emptyResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(""));
		assertEquals("", emptyResult);
	}
	
	@Test
	void decodeWithSpecialNumbers() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = DOUBLE;
		
		Double infinityResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.POSITIVE_INFINITY));
		assertEquals(Double.POSITIVE_INFINITY, infinityResult);
		
		Double negInfinityResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NEGATIVE_INFINITY));
		assertEquals(Double.NEGATIVE_INFINITY, negInfinityResult);
		
		Double nanResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Double.NaN));
		assertEquals(Double.NaN, nanResult);
	}
	
	@Test
	void decodeKeyNullChecks() {
		Decoder<Integer> decoder = INTEGER;
		assertThrows(NullPointerException.class, () -> decoder.decodeKey(null));
	}
	
	@Test
	void decodeKeyNotSupported() {
		Decoder<Integer> decoder = INTEGER;
		assertThrows(DecoderException.class, () -> decoder.decodeKey("42"));
	}
	
	@Test
	void mapDecoderNullChecks() {
		assertThrows(NullPointerException.class, () -> INTEGER.mapDecoder(null));
	}
	
	@Test
	void mapDecoderWithSuccessfulMapping() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = INTEGER.mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			return either.leftOrThrow().doubleValue();
		});
		
		Double result = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42.0, result);
	}
	
	@Test
	void mapDecoderWithFailingMapping() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Integer> decoder = INTEGER.mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			Integer v = either.leftOrThrow();
			if (v == 0) {
				throw new DecoderException("Zero not allowed");
			}
			return v;
		});
		
		DecoderException exception = assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(0)));
		assertEquals("Zero not allowed", exception.getMessage());
		
		Integer successResult = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42, successResult);
	}
	
	@Test
	void mapDecoderWithDecodingError() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<Double> decoder = INTEGER.mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			return either.leftOrThrow().doubleValue();
		});
		
		assertThrows(DecoderException.class, () -> decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void mapDecoderChaining() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Decoder<String> decoder = INTEGER.mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			return either.leftOrThrow().doubleValue();
		}).mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			return either.leftOrThrow().toString();
		});
		
		String result = decoder.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals("42.0", result);
	}
	
	@Test
	void mapDecoderDecodeKey() throws DecoderException {
		Decoder<Double> decoder = STRING.mapDecoder(either -> {
			if (either.isRight()) throw either.rightOrThrow();
			return Double.parseDouble(either.leftOrThrow());
		});
		
		Double key = decoder.decodeKey("3.14");
		assertEquals(3.14, key);
	}
}
