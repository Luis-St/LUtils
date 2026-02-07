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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UnitCodec}.<br>
 *
 * @author Luis-St
 */
class UnitCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new UnitCodec<>(null));
		assertDoesNotThrow(() -> new UnitCodec<>(() -> 1));
		assertDoesNotThrow(() -> new UnitCodec<>(() -> null));
	}
	
	@Test
	void encodeNullChecks() {
		Codec<Integer> codec = new UnitCodec<>(() -> 1);
		
		assertThrows(NullPointerException.class, () -> codec.encode(JsonTypeProvider.INSTANCE, null, 1));
	}
	
	@Test
	void encodeIgnoresTypeProvider() throws EncoderException {
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonElement empty = JsonNull.INSTANCE;
		
		JsonElement result = codec.encode(null, empty, 1);
		assertSame(empty, result);
	}
	
	@Test
	void encodeReturnsCurrentValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, 123);
		assertSame(current, result);
	}
	
	@Test
	void encodeWithNullValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonArray current = new JsonArray();
		
		JsonElement result = codec.encode(typeProvider, current, null);
		assertSame(current, result);
	}
	
	@Test
	void encodeWithDifferentCurrentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> "unit");
		
		JsonPrimitive primitive = new JsonPrimitive(42);
		JsonElement primitiveResult = codec.encode(typeProvider, primitive, "ignored");
		assertSame(primitive, primitiveResult);
		
		JsonArray array = new JsonArray();
		JsonElement arrayResult = codec.encode(typeProvider, array, "ignored");
		assertSame(array, arrayResult);
	}
	
	@Test
	void decodeIgnoresTypeProvider() throws DecoderException {
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		
		Integer result = codec.decode(null, null, JsonNull.INSTANCE);
		assertEquals(42, result);
	}
	
	@Test
	void decodeIgnoresValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> "unit-value");
		
		String nullResult = codec.decode(typeProvider, typeProvider.empty(), null);
		assertEquals("unit-value", nullResult);
		
		String primitiveResult = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(123));
		assertEquals("unit-value", primitiveResult);
		
		String objectResult = codec.decode(typeProvider, typeProvider.empty(), new JsonObject());
		assertEquals("unit-value", objectResult);
	}
	
	@Test
	void decodeWithNullSupplier() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> null);
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("anything"));
		assertNull(result);
	}
	
	@Test
	void decodeWithDifferentSupplierTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> intCodec = new UnitCodec<>(() -> 999);
		assertEquals(999, intCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("ignored")));
		
		Codec<Boolean> boolCodec = new UnitCodec<>(() -> true);
		assertTrue(boolCodec.decode(typeProvider, typeProvider.empty(), new JsonArray()));
		
		Codec<Double> doubleCodec = new UnitCodec<>(() -> 3.14);
		assertEquals(3.14, doubleCodec.decode(typeProvider, typeProvider.empty(), null));
	}
	
	@Test
	void decodeWithComplexTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Person defaultPerson = new Person("Default", 0);
		Codec<Person> codec = new UnitCodec<>(() -> defaultPerson);
		
		Person result = codec.decode(typeProvider, typeProvider.empty(), new JsonObject());
		assertSame(defaultPerson, result);
	}
	
	@Test
	void supplierCalledForEachDecode() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		int[] counter = { 0 };
		Codec<Integer> codec = new UnitCodec<>(() -> ++counter[0]);
		
		Integer firstResult = codec.decode(typeProvider, typeProvider.empty(), null);
		assertEquals(1, firstResult);
		
		Integer secondResult = codec.decode(typeProvider, typeProvider.empty(), JsonNull.INSTANCE);
		assertEquals(2, secondResult);
	}
	
	@Test
	void supplierWithException() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> {
			throw new RuntimeException("Supplier failed");
		});
		
		assertThrows(RuntimeException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
	}
	
	@Test
	void toStringRepresentation() {
		UnitCodec<Integer> codec = new UnitCodec<>(() -> 1);
		String result = codec.toString();
		
		assertEquals("unit codec", result);
	}
	
	//region Internal
	private record Person(String name, int age) {}
	//endregion
}
