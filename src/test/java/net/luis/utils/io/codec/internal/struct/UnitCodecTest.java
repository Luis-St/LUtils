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

package net.luis.utils.io.codec.internal.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		Codec<Integer> codec = new UnitCodec<>(() -> 1);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(JsonTypeProvider.INSTANCE, null, 1));
	}
	
	@Test
	void encodeStartIgnoresTypeProvider() {
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonElement empty = JsonNull.INSTANCE;
		
		Result<JsonElement> result = codec.encodeStart(null, empty, 1);
		assertTrue(result.isSuccess());
		assertSame(empty, result.resultOrThrow());
	}
	
	@Test
	void encodeStartReturnsCurrentValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, 123);
		assertTrue(result.isSuccess());
		assertSame(current, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNullValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		JsonArray current = new JsonArray();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertSame(current, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentCurrentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> "unit");
		
		JsonPrimitive primitive = new JsonPrimitive(42);
		Result<JsonElement> primitiveResult = codec.encodeStart(typeProvider, primitive, "ignored");
		assertTrue(primitiveResult.isSuccess());
		assertSame(primitive, primitiveResult.resultOrThrow());
		
		JsonArray array = new JsonArray();
		Result<JsonElement> arrayResult = codec.encodeStart(typeProvider, array, "ignored");
		assertTrue(arrayResult.isSuccess());
		assertSame(array, arrayResult.resultOrThrow());
	}
	
	@Test
	void decodeStartIgnoresTypeProvider() {
		Codec<Integer> codec = new UnitCodec<>(() -> 42);
		
		Result<Integer> result = codec.decodeStart(null, JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartIgnoresValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> "unit-value");
		
		Result<String> nullResult = codec.decodeStart(typeProvider, null);
		assertTrue(nullResult.isSuccess());
		assertEquals("unit-value", nullResult.resultOrThrow());
		
		Result<String> primitiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(123));
		assertTrue(primitiveResult.isSuccess());
		assertEquals("unit-value", primitiveResult.resultOrThrow());
		
		Result<String> objectResult = codec.decodeStart(typeProvider, new JsonObject());
		assertTrue(objectResult.isSuccess());
		assertEquals("unit-value", objectResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNullSupplier() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> null);
		
		Result<String> result = codec.decodeStart(typeProvider, new JsonPrimitive("anything"));
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithDifferentSupplierTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Integer> intCodec = new UnitCodec<>(() -> 999);
		Result<Integer> intResult = intCodec.decodeStart(typeProvider, new JsonPrimitive("ignored"));
		assertTrue(intResult.isSuccess());
		assertEquals(999, intResult.resultOrThrow());
		
		Codec<Boolean> boolCodec = new UnitCodec<>(() -> true);
		Result<Boolean> boolResult = boolCodec.decodeStart(typeProvider, new JsonArray());
		assertTrue(boolResult.isSuccess());
		assertTrue(boolResult.resultOrThrow());
		
		Codec<Double> doubleCodec = new UnitCodec<>(() -> 3.14);
		Result<Double> doubleResult = doubleCodec.decodeStart(typeProvider, null);
		assertTrue(doubleResult.isSuccess());
		assertEquals(3.14, doubleResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithComplexTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Person defaultPerson = new Person("Default", 0);
		Codec<Person> codec = new UnitCodec<>(() -> defaultPerson);
		
		Result<Person> result = codec.decodeStart(typeProvider, new JsonObject());
		assertTrue(result.isSuccess());
		assertSame(defaultPerson, result.resultOrThrow());
	}
	
	@Test
	void supplierCalledForEachDecode() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		int[] counter = { 0 };
		Codec<Integer> codec = new UnitCodec<>(() -> ++counter[0]);
		
		Result<Integer> firstResult = codec.decodeStart(typeProvider, null);
		assertTrue(firstResult.isSuccess());
		assertEquals(1, firstResult.resultOrThrow());
		
		Result<Integer> secondResult = codec.decodeStart(typeProvider, JsonNull.INSTANCE);
		assertTrue(secondResult.isSuccess());
		assertEquals(2, secondResult.resultOrThrow());
	}
	
	@Test
	void supplierWithException() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnitCodec<>(() -> {
			throw new RuntimeException("Supplier failed");
		});
		
		assertThrows(RuntimeException.class, () -> codec.decodeStart(typeProvider, null));
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
