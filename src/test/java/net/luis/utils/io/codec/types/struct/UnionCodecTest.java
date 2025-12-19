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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UnionCodec}.<br>
 *
 * @author Luis-St
 */
class UnionCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new UnionCodec(null, Set.of("a", "b")));
		assertThrows(NullPointerException.class, () -> new UnionCodec(STRING, (Collection<String>) null));
		assertThrows(IllegalArgumentException.class, () -> new UnionCodec(STRING, Collections.emptySet()));
		assertThrows(IllegalArgumentException.class, () -> new UnionCodec(STRING, Collections.emptyList()));
		assertDoesNotThrow(() -> new UnionCodec(STRING, Set.of("a", "b", "c")));
		assertDoesNotThrow(() -> new UnionCodec(INTEGER, List.of(1, 2, 3)));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), "pending"));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, "pending"));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as union"));
	}
	
	@Test
	void encodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "pending");
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("pending"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithAllValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<JsonElement> result1 = codec.encodeStart(typeProvider, typeProvider.empty(), "pending");
		assertTrue(result1.isSuccess());
		assertEquals(new JsonPrimitive("pending"), result1.resultOrThrow());
		
		Result<JsonElement> result2 = codec.encodeStart(typeProvider, typeProvider.empty(), "active");
		assertTrue(result2.isSuccess());
		assertEquals(new JsonPrimitive("active"), result2.resultOrThrow());
		
		Result<JsonElement> result3 = codec.encodeStart(typeProvider, typeProvider.empty(), "completed");
		assertTrue(result3.isSuccess());
		assertEquals(new JsonPrimitive("completed"), result3.resultOrThrow());
	}
	
	@Test
	void encodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("value is not in the set of valid values"));
		assertTrue(result.errorOrThrow().contains("invalid"));
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new UnionCodec(STRING, List.of("apple", "banana", "cherry"));
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), "apple");
		assertTrue(stringResult.isSuccess());
		assertEquals(new JsonPrimitive("apple"), stringResult.resultOrThrow());
		
		Codec<Integer> integerCodec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		Result<JsonElement> intResult = integerCodec.encodeStart(typeProvider, typeProvider.empty(), 2);
		assertTrue(intResult.isSuccess());
		assertEquals(new JsonPrimitive(2), intResult.resultOrThrow());
		
		Codec<Boolean> booleanCodec = new UnionCodec(BOOLEAN, List.of(true));
		Result<JsonElement> boolResult = booleanCodec.encodeStart(typeProvider, typeProvider.empty(), true);
		assertTrue(boolResult.isSuccess());
		assertEquals(new JsonPrimitive(true), boolResult.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("only"));
		
		Result<JsonElement> validResult = codec.encodeStart(typeProvider, typeProvider.empty(), "only");
		assertTrue(validResult.isSuccess());
		
		Result<JsonElement> invalidResult = codec.encodeStart(typeProvider, typeProvider.empty(), "other");
		assertTrue(invalidResult.isError());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("pending")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as union"));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("pending"));
		assertTrue(result.isSuccess());
		assertEquals("pending", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithAllValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<String> result1 = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("pending"));
		assertTrue(result1.isSuccess());
		assertEquals("pending", result1.resultOrThrow());
		
		Result<String> result2 = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("active"));
		assertTrue(result2.isSuccess());
		assertEquals("active", result2.resultOrThrow());
		
		Result<String> result3 = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("completed"));
		assertTrue(result3.isSuccess());
		assertEquals("completed", result3.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("value is not in the set of valid values"));
		assertTrue(result.errorOrThrow().contains("invalid"));
	}
	
	@Test
	void decodeStartWithInvalidType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		Result<String> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(123));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new UnionCodec(STRING, List.of("apple", "banana", "cherry"));
		Result<String> stringResult = stringCodec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("banana"));
		assertTrue(stringResult.isSuccess());
		assertEquals("banana", stringResult.resultOrThrow());
		
		Codec<Integer> integerCodec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		Result<Integer> intResult = integerCodec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(3));
		assertTrue(intResult.isSuccess());
		assertEquals(3, intResult.resultOrThrow());
		
		Codec<Boolean> booleanCodec = new UnionCodec(BOOLEAN, List.of(true));
		Result<Boolean> boolResult = booleanCodec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertTrue(boolResult.isSuccess());
		assertTrue(boolResult.resultOrThrow());
	}
	
	@Test
	void decodeStartWithSingleValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("only"));
		
		Result<String> validResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("only"));
		assertTrue(validResult.isSuccess());
		assertEquals("only", validResult.resultOrThrow());
		
		Result<String> invalidResult = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("other"));
		assertTrue(invalidResult.isError());
	}
	
	@Test
	void decodeStartInnerCodecFailure() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		
		Result<Integer> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void roundTripEncodingDecoding() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("red", "green", "blue"));
		
		Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), "green");
		assertTrue(encoded.isSuccess());
		
		Result<String> decoded = codec.decodeStart(typeProvider, typeProvider.empty(), encoded.resultOrThrow());
		assertTrue(decoded.isSuccess());
		assertEquals("green", decoded.resultOrThrow());
	}
	
	@Test
	void roundTripWithMultipleValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnionCodec(INTEGER, List.of(10, 20, 30, 40, 50));
		
		for (int value : Arrays.asList(10, 20, 30, 40, 50)) {
			Result<JsonElement> encoded = codec.encodeStart(typeProvider, typeProvider.empty(), value);
			assertTrue(encoded.isSuccess());
			
			Result<Integer> decoded = codec.decodeStart(typeProvider, typeProvider.empty(), encoded.resultOrThrow());
			assertTrue(decoded.isSuccess());
			assertEquals(value, decoded.resultOrThrow());
		}
	}
	
	@Test
	void instanceMethodFromCodec() {
		Codec<String> codec = STRING.union("one", "two", "three");
		assertNotNull(codec);
		assertInstanceOf(UnionCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "two");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void instanceMethodWithCollection() {
		List<Integer> validValues = List.of(100, 200, 300);
		Codec<Integer> codec = INTEGER.union(validValues);
		assertNotNull(codec);
		assertInstanceOf(UnionCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 200);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void equalsAndHashCode() {
		UnionCodec<String> codec1 = new UnionCodec(STRING, List.of("a", "b", "c"));
		UnionCodec<String> codec2 = new UnionCodec(STRING, List.of("a", "b", "c"));
		UnionCodec<String> codec3 = new UnionCodec(STRING, List.of("a", "b", "c", "d"));
		
		assertEquals(codec1, codec2);
		assertEquals(codec1.hashCode(), codec2.hashCode());
		assertNotEquals(codec1, codec3);
	}
	
	@Test
	void equalityWithDifferentCollectionTypes() {
		UnionCodec<String> codecFromSet = new UnionCodec(STRING, Set.of("a", "b", "c"));
		UnionCodec<String> codecFromList = new UnionCodec(STRING, List.of("a", "b", "c"));
		UnionCodec<String> codecFromVarargs = new UnionCodec(STRING, List.of("a", "b", "c"));
		
		assertEquals(codecFromSet, codecFromList);
		assertEquals(codecFromSet, codecFromVarargs);
		assertEquals(codecFromSet.hashCode(), codecFromList.hashCode());
		assertEquals(codecFromSet.hashCode(), codecFromVarargs.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		UnionCodec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		String result = codec.toString();
		
		assertTrue(result.startsWith("UnionCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("pending") || result.contains("["));
		assertTrue(result.contains("active") || result.contains("]"));
		assertTrue(result.contains("completed") || result.contains(","));
	}
	
	@Test
	void integrationWithNullableCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("a", "b", "c");
		Codec<String> nullableUnion = unionCodec.nullable();
		
		Result<JsonElement> nullResult = nullableUnion.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(nullResult.isSuccess());
		
		Result<JsonElement> validResult = nullableUnion.encodeStart(typeProvider, typeProvider.empty(), "a");
		assertTrue(validResult.isSuccess());
		
		Result<JsonElement> invalidResult = nullableUnion.encodeStart(typeProvider, typeProvider.empty(), "d");
		assertTrue(invalidResult.isError());
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("x", "y", "z");
		Codec<Optional<String>> optionalUnion = unionCodec.optional();
		
		Result<JsonElement> emptyResult = optionalUnion.encodeStart(typeProvider, typeProvider.empty(), Optional.empty());
		assertTrue(emptyResult.isSuccess());
		
		Result<JsonElement> validResult = optionalUnion.encodeStart(typeProvider, typeProvider.empty(), Optional.of("x"));
		assertTrue(validResult.isSuccess());
		
		Result<JsonElement> invalidResult = optionalUnion.encodeStart(typeProvider, typeProvider.empty(), Optional.of("invalid"));
		assertTrue(invalidResult.isError());
	}
	
	@Test
	void integrationWithListCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("red", "green", "blue");
		Codec<List<String>> listUnion = unionCodec.list();
		
		Result<JsonElement> validResult = listUnion.encodeStart(typeProvider, typeProvider.empty(), List.of("red", "green", "blue"));
		assertTrue(validResult.isSuccess());
		
		Result<JsonElement> invalidResult = listUnion.encodeStart(typeProvider, typeProvider.empty(), List.of("red", "yellow", "blue"));
		assertTrue(invalidResult.isPartial());
	}
}
