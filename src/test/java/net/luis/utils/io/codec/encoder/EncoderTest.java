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

package net.luis.utils.io.codec.encoder;

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static net.luis.utils.io.codec.Codecs.*;
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
		Encoder<Integer> encoder = INTEGER;
		
		assertThrows(NullPointerException.class, () -> encoder.encode(null, 1));
		assertThrows(NullPointerException.class, () -> encoder.encode(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> encoder.encode(typeProvider, null, 1));
	}
	
	@Test
	void encodeWithValidValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = INTEGER;
		
		JsonElement result = encoder.encode(typeProvider, 42);
		assertEquals(new JsonPrimitive(42), result);
		
		JsonElement resultWithCurrent = encoder.encode(typeProvider, typeProvider.empty(), 42);
		assertEquals(new JsonPrimitive(42), resultWithCurrent);
	}
	
	@Test
	void encodeWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = INTEGER;
		
		assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, null));
		assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, typeProvider.empty(), null));
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonElement stringResult = STRING.encode(typeProvider, "hello");
		assertEquals(new JsonPrimitive("hello"), stringResult);
		
		JsonElement boolResult = BOOLEAN.encode(typeProvider, true);
		assertEquals(new JsonPrimitive(true), boolResult);
		
		JsonElement doubleResult = DOUBLE.encode(typeProvider, 3.14);
		assertEquals(new JsonPrimitive(3.14), doubleResult);
	}
	
	@Test
	void encodeWithSpecialNumbers() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Double> encoder = DOUBLE;
		
		JsonElement infinityResult = encoder.encode(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertEquals(new JsonPrimitive(Double.POSITIVE_INFINITY), infinityResult);
		
		JsonElement negInfinityResult = encoder.encode(typeProvider, typeProvider.empty(), Double.NEGATIVE_INFINITY);
		assertEquals(new JsonPrimitive(Double.NEGATIVE_INFINITY), negInfinityResult);
		
		JsonElement nanResult = encoder.encode(typeProvider, typeProvider.empty(), Double.NaN);
		assertEquals(new JsonPrimitive(Double.NaN), nanResult);
	}
	
	@Test
	void encodeKeyNullChecks() {
		Encoder<Integer> encoder = INTEGER;
		assertThrows(NullPointerException.class, () -> encoder.encodeKey(null));
	}
	
	@Test
	void encodeKeyNotSupported() {
		Encoder<Duration> encoder = DURATION;
		assertThrows(EncoderException.class, () -> encoder.encodeKey(Duration.ofMillis(42)));
	}
	
	@Test
	void mapEncoderNullChecks() {
		assertThrows(NullPointerException.class, () -> INTEGER.mapEncoder(null));
	}
	
	@Test
	void mapEncoderWithSuccessfulMapping() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Double> encoder = INTEGER.mapEncoder(Double::intValue);
		
		JsonElement result = encoder.encode(typeProvider, typeProvider.empty(), 42.7);
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void mapEncoderWithFailingMapping() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Integer> encoder = INTEGER.mapEncoder(i -> {
			if (i == 0) {
				throw new EncoderException("Zero not allowed");
			}
			return i;
		});
		
		EncoderException exception = assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, typeProvider.empty(), 0));
		assertTrue(exception.getMessage().contains("Zero not allowed"));
		
		JsonElement successResult = encoder.encode(typeProvider, typeProvider.empty(), 42);
		assertEquals(new JsonPrimitive(42), successResult);
	}
	
	@Test
	void mapEncoderWithNullMapping() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = INTEGER.mapEncoder(s -> null);
		
		assertThrows(EncoderException.class, () -> encoder.encode(typeProvider, typeProvider.empty(), "anything"));
	}
	
	@Test
	void mapEncoderChaining() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<String> encoder = INTEGER.mapEncoder(Double::intValue).mapEncoder(Double::parseDouble);
		
		JsonElement result = encoder.encode(typeProvider, typeProvider.empty(), "42");
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void mapEncoderWithComplexMapping() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Encoder<Person> encoder = STRING.mapEncoder(Person::name);
		
		JsonElement result = encoder.encode(typeProvider, typeProvider.empty(), new Person("John", 25));
		assertEquals(new JsonPrimitive("John"), result);
	}
	
	@Test
	void mapEncoderEncodeKey() throws EncoderException {
		Encoder<Double> encoder = STRING.mapEncoder(d -> String.valueOf(d.intValue()));
		
		String key = encoder.encodeKey(42.7);
		assertEquals("42", key);
	}
	
	//region Internal
	private record Person(String name, int age) {}
	//endregion
}
