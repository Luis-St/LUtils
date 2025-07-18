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
 * Test class for {@link Encoder}.<br>
 *
 * @author Luis-St
 */
class EncoderTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> encoder.encode(null, 1));
		assertThrows(NullPointerException.class, () -> encoder.encode(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> encoder.encode(typeProvider, null, 1));
	}
	
	@Test
	void encodeWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		JsonElement result = encoder.encode(typeProvider, 42);
		assertEquals(new JsonPrimitive(42), result);
		
		JsonElement resultWithCurrent = encoder.encode(typeProvider, typeProvider.empty(), 42);
		assertEquals(new JsonPrimitive(42), resultWithCurrent);
	}
	
	@Test
	void encodeWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, null));
		assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, typeProvider.empty(), null));
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonElement stringResult = Codec.STRING.encode(typeProvider, "hello");
		assertEquals(new JsonPrimitive("hello"), stringResult);
		
		JsonElement boolResult = Codec.BOOLEAN.encode(typeProvider, true);
		assertEquals(new JsonPrimitive(true), boolResult);
		
		JsonElement doubleResult = Codec.DOUBLE.encode(typeProvider, 3.14);
		assertEquals(new JsonPrimitive(3.14), doubleResult);
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> encoder.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> encoder.encodeStart(typeProvider, null, 1));
	}
	
	@Test
	void encodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void encodeStartWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER;
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartWithSpecialNumbers() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Double> encoder = Codec.DOUBLE;
		
		Result<JsonElement> infinityResult = encoder.encodeStart(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isSuccess());
		
		Result<JsonElement> nanResult = encoder.encodeStart(typeProvider, typeProvider.empty(), Double.NaN);
		assertTrue(nanResult.isSuccess());
		
		Result<JsonElement> negInfinityResult = encoder.encodeStart(typeProvider, typeProvider.empty(), Double.NEGATIVE_INFINITY);
		assertTrue(negInfinityResult.isSuccess());
	}
	
	@Test
	void mapEncoderNullChecks() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.mapEncoder(null));
	}
	
	@Test
	void mapEncoderWithSuccessfulMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Double> encoder = Codec.INTEGER.mapEncoder(ResultingFunction.direct(Double::intValue));
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), 42.7);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void mapEncoderWithFailingMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = Codec.INTEGER.mapEncoder(ResultingFunction.throwable(i -> {
			if (i == 0) {
				throw new IllegalArgumentException("Zero not allowed");
			}
			return i;
		}));
		
		Result<JsonElement> errorResult = encoder.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(errorResult.isError());
		assertTrue(errorResult.errorOrThrow().contains("Failed to map value to encode"));
		assertTrue(errorResult.errorOrThrow().contains("Zero not allowed"));
		
		Result<JsonElement> successResult = encoder.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(successResult.isSuccess());
		assertEquals(new JsonPrimitive(42), successResult.orThrow());
	}
	
	@Test
	void mapEncoderWithNullMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = Codec.INTEGER.mapEncoder(ResultingFunction.direct(s -> null));
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), "anything");
		assertTrue(result.isError());
	}
	
	@Test
	void mapEncoderChaining() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = Codec.INTEGER.mapEncoder(ResultingFunction.direct(Double::intValue)).mapEncoder(ResultingFunction.direct(Double::parseDouble));
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), "42");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.orThrow());
	}
	
	@Test
	void mapEncoderWithComplexMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Person> encoder = Codec.STRING.mapEncoder(ResultingFunction.direct(Person::name));
		
		Result<JsonElement> result = encoder.encodeStart(typeProvider, typeProvider.empty(), new Person("John", 25));
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("John"), result.orThrow());
	}
	
	//region Internal
	private record Person(String name, int age) {}
	//endregion
}
