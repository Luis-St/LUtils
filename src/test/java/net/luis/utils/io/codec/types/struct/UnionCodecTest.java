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
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
		assertThrows(NullPointerException.class, () -> new UnionCodec(STRING, null));
		assertThrows(IllegalArgumentException.class, () -> new UnionCodec(STRING, Collections.emptySet()));
		assertThrows(IllegalArgumentException.class, () -> new UnionCodec(STRING, Collections.emptyList()));
		assertDoesNotThrow(() -> new UnionCodec(STRING, Set.of("a", "b", "c")));
		assertDoesNotThrow(() -> new UnionCodec(INTEGER, List.of(1, 2, 3)));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), "pending"));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, "pending"));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as union"));
	}
	
	@Test
	void encodeWithValidValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), "pending");
		assertEquals(new JsonPrimitive("pending"), result);
	}
	
	@Test
	void encodeWithAllValidValues() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertEquals(new JsonPrimitive("pending"), codec.encode(typeProvider, typeProvider.empty(), "pending"));
		assertEquals(new JsonPrimitive("active"), codec.encode(typeProvider, typeProvider.empty(), "active"));
		assertEquals(new JsonPrimitive("completed"), codec.encode(typeProvider, typeProvider.empty(), "completed"));
	}
	
	@Test
	void encodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "invalid"));
		assertTrue(exception.getMessage().contains("value is not in the set of valid values"));
		assertTrue(exception.getMessage().contains("invalid"));
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new UnionCodec(STRING, List.of("apple", "banana", "cherry"));
		assertEquals(new JsonPrimitive("apple"), stringCodec.encode(typeProvider, typeProvider.empty(), "apple"));
		
		Codec<Integer> integerCodec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		assertEquals(new JsonPrimitive(2), integerCodec.encode(typeProvider, typeProvider.empty(), 2));
		
		Codec<Boolean> booleanCodec = new UnionCodec(BOOLEAN, List.of(true));
		assertEquals(new JsonPrimitive(true), booleanCodec.encode(typeProvider, typeProvider.empty(), true));
	}
	
	@Test
	void encodeWithSingleValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("only"));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "only"));
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "other"));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("pending")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as union"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("pending"));
		assertEquals("pending", result);
	}
	
	@Test
	void decodeWithAllValidValues() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertEquals("pending", codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("pending")));
		assertEquals("active", codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("active")));
		assertEquals("completed", codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("completed")));
	}
	
	@Test
	void decodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid")));
		assertTrue(exception.getMessage().contains("value is not in the set of valid values"));
		assertTrue(exception.getMessage().contains("invalid"));
	}
	
	@Test
	void decodeWithInvalidType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("pending", "active", "completed"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(123)));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new UnionCodec(STRING, List.of("apple", "banana", "cherry"));
		assertEquals("banana", stringCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("banana")));
		
		Codec<Integer> integerCodec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		assertEquals(3, integerCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(3)));
		
		Codec<Boolean> booleanCodec = new UnionCodec(BOOLEAN, List.of(true));
		assertTrue(booleanCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true)));
	}
	
	@Test
	void decodeWithSingleValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("only"));
		
		assertEquals("only", codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("only")));
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("other")));
	}
	
	@Test
	void decodeInnerCodecFailure() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnionCodec(INTEGER, List.of(1, 2, 3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void roundTripEncodingDecoding() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new UnionCodec(STRING, List.of("red", "green", "blue"));
		
		JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), "green");
		String decoded = codec.decode(typeProvider, typeProvider.empty(), encoded);
		assertEquals("green", decoded);
	}
	
	@Test
	void roundTripWithMultipleValues() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new UnionCodec(INTEGER, List.of(10, 20, 30, 40, 50));
		
		for (int value : Arrays.asList(10, 20, 30, 40, 50)) {
			JsonElement encoded = codec.encode(typeProvider, typeProvider.empty(), value);
			Integer decoded = codec.decode(typeProvider, typeProvider.empty(), encoded);
			assertEquals(value, decoded);
		}
	}
	
	@Test
	void instanceMethodFromCodec() {
		Codec<String> codec = STRING.union("one", "two", "three");
		assertNotNull(codec);
		assertInstanceOf(UnionCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), "two"));
	}
	
	@Test
	void instanceMethodWithCollection() {
		List<Integer> validValues = List.of(100, 200, 300);
		Codec<Integer> codec = INTEGER.union(validValues);
		assertNotNull(codec);
		assertInstanceOf(UnionCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 200));
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
		
		assertDoesNotThrow(() -> nullableUnion.encode(typeProvider, typeProvider.empty(), null));
		assertDoesNotThrow(() -> nullableUnion.encode(typeProvider, typeProvider.empty(), "a"));
		assertThrows(EncoderException.class, () -> nullableUnion.encode(typeProvider, typeProvider.empty(), "d"));
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("x", "y", "z");
		Codec<Optional<String>> optionalUnion = unionCodec.optional();
		
		assertDoesNotThrow(() -> optionalUnion.encode(typeProvider, typeProvider.empty(), Optional.empty()));
		assertDoesNotThrow(() -> optionalUnion.encode(typeProvider, typeProvider.empty(), Optional.of("x")));
		assertThrows(EncoderException.class, () -> optionalUnion.encode(typeProvider, typeProvider.empty(), Optional.of("invalid")));
	}
	
	@Test
	void integrationWithListCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> unionCodec = STRING.union("red", "green", "blue");
		Codec<List<String>> listUnion = unionCodec.list();
		
		assertDoesNotThrow(() -> listUnion.encode(typeProvider, typeProvider.empty(), List.of("red", "green", "blue")));
		assertThrows(EncoderException.class, () -> listUnion.encode(typeProvider, typeProvider.empty(), List.of("red", "yellow", "blue")));
	}
}
