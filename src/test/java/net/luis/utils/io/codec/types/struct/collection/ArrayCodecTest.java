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

package net.luis.utils.io.codec.types.struct.collection;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ArrayCodec}.<br>
 *
 * @author Luis-St
 */
class ArrayCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new ArrayCodec<>(null, INTEGER));
		assertThrows(NullPointerException.class, () -> new ArrayCodec<>(Integer.class, null));
		assertDoesNotThrow(() -> new ArrayCodec<>(Integer.class, INTEGER));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = { 1, 2, 3 };
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, array));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as array"));
	}
	
	@Test
	void encodeWithValidArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = { 1, 2, 3 };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithEmptyArray() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = {};
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		assertEquals(new JsonArray(), result);
	}
	
	@Test
	void encodeWithSingleElement() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[]> codec = new ArrayCodec<>(String.class, STRING);
		String[] array = { "hello" };
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), array);
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result);
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String[]> stringCodec = new ArrayCodec<>(String.class, STRING);
		assertDoesNotThrow(() -> stringCodec.encode(typeProvider, typeProvider.empty(), new String[] { "a", "b" }));
		
		Codec<Boolean[]> boolCodec = new ArrayCodec<>(Boolean.class, BOOLEAN);
		assertDoesNotThrow(() -> boolCodec.encode(typeProvider, typeProvider.empty(), new Boolean[] { true, false }));
	}
	
	@Test
	void encodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] arrayWithNull = { 1, null, 3 };
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), arrayWithNull));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonArray()));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as array"));
	}
	
	@Test
	void decodeWithValidArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Integer[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, result);
	}
	
	@Test
	void decodeWithEmptyArray() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		Integer[] result = codec.decode(typeProvider, typeProvider.empty(), new JsonArray());
		assertEquals(0, result.length);
	}
	
	@Test
	void decodeWithSingleElement() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[]> codec = new ArrayCodec<>(String.class, STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		String[] result = codec.decode(typeProvider, typeProvider.empty(), array);
		assertArrayEquals(new String[] { "hello" }, result);
	}
	
	@Test
	void decodeWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
		assertTrue(exception.getMessage().contains("Json element '42' is not a json array"));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<String[]> stringCodec = new ArrayCodec<>(String.class, STRING);
		String[] stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), stringArray);
		assertArrayEquals(new String[] { "a", "b" }, stringResult);
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<Boolean[]> boolCodec = new ArrayCodec<>(Boolean.class, BOOLEAN);
		Boolean[] boolResult = boolCodec.decode(typeProvider, typeProvider.empty(), boolArray);
		assertArrayEquals(new Boolean[] { true, false }, boolResult);
	}
	
	@Test
	void decodeWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void decodeWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(100));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), array));
	}
	
	@Test
	void nestedTwoDimensionalArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] originalArray = {
			{ 1, 2, 3 },
			{ 4, 5, 6 },
			{ 7, 8, 9 }
		};
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), originalArray);
		
		JsonArray outerArray = assertInstanceOf(JsonArray.class, encodeResult);
		assertEquals(3, outerArray.size());
		
		JsonArray firstInnerArray = outerArray.get(0).getAsJsonArray();
		assertEquals(3, firstInnerArray.size());
		assertEquals(1, firstInnerArray.get(0).getAsJsonPrimitive().getAsInteger());
		assertEquals(2, firstInnerArray.get(1).getAsJsonPrimitive().getAsInteger());
		assertEquals(3, firstInnerArray.get(2).getAsJsonPrimitive().getAsInteger());
		
		Integer[][] decoded = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, decoded[0]);
		assertArrayEquals(new Integer[] { 4, 5, 6 }, decoded[1]);
		assertArrayEquals(new Integer[] { 7, 8, 9 }, decoded[2]);
	}
	
	@Test
	void nestedThreeDimensionalArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][][]> codec = new ArrayCodec<>(Integer[][].class, new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER)));
		
		Integer[][][] originalArray = {
			{
				{ 1, 2 },
				{ 3, 4 }
			},
			{
				{ 5, 6 },
				{ 7, 8 }
			}
		};
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), originalArray);
		Integer[][][] decoded = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		
		assertEquals(2, decoded.length);
		assertEquals(2, decoded[0].length);
		assertEquals(2, decoded[0][0].length);
		assertEquals(1, decoded[0][0][0]);
		assertEquals(2, decoded[0][0][1]);
		assertEquals(3, decoded[0][1][0]);
		assertEquals(4, decoded[0][1][1]);
		assertEquals(5, decoded[1][0][0]);
		assertEquals(6, decoded[1][0][1]);
		assertEquals(7, decoded[1][1][0]);
		assertEquals(8, decoded[1][1][1]);
	}
	
	@Test
	void jaggedNestedArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] jaggedArray = {
			{ 1, 2, 3, 4, 5 },
			{ 6, 7 },
			{ 8, 9, 10 }
		};
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), jaggedArray);
		Integer[][] decoded = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, decoded[0]);
		assertArrayEquals(new Integer[] { 6, 7 }, decoded[1]);
		assertArrayEquals(new Integer[] { 8, 9, 10 }, decoded[2]);
	}
	
	@Test
	void emptyNestedArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[][]> codec = new ArrayCodec<>(String[].class, new ArrayCodec<>(String.class, STRING));
		
		String[][] emptyNestedArray = {};
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), emptyNestedArray);
		assertEquals(new JsonArray(), encodeResult);
		
		String[][] decoded = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		assertEquals(0, decoded.length);
	}
	
	@Test
	void nestedArraysWithSomeEmptyInnerArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] arrayWithEmptyInner = {
			{ 1, 2, 3 },
			{},
			{ 4, 5 }
		};
		
		JsonElement encodeResult = codec.encode(typeProvider, typeProvider.empty(), arrayWithEmptyInner);
		Integer[][] decoded = codec.decode(typeProvider, typeProvider.empty(), encodeResult);
		
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, decoded[0]);
		assertEquals(0, decoded[1].length);
		assertArrayEquals(new Integer[] { 4, 5 }, decoded[2]);
	}
	
	@Test
	void mixedTypeNestedArrays() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String[][]> stringArrayCodec = new ArrayCodec<>(String[].class, new ArrayCodec<>(String.class, STRING));
		String[][] stringArray = {
			{ "hello", "world" },
			{ "foo", "bar", "baz" }
		};
		
		JsonElement stringEncodeResult = stringArrayCodec.encode(typeProvider, typeProvider.empty(), stringArray);
		String[][] decodedStringArray = stringArrayCodec.decode(typeProvider, typeProvider.empty(), stringEncodeResult);
		assertArrayEquals(new String[] { "hello", "world" }, decodedStringArray[0]);
		assertArrayEquals(new String[] { "foo", "bar", "baz" }, decodedStringArray[1]);
		
		Codec<Boolean[][]> boolArrayCodec = new ArrayCodec<>(Boolean[].class, new ArrayCodec<>(Boolean.class, BOOLEAN));
		Boolean[][] boolArray = {
			{ true, false },
			{ false, true, false }
		};
		
		JsonElement boolEncodeResult = boolArrayCodec.encode(typeProvider, typeProvider.empty(), boolArray);
		Boolean[][] decodedBoolArray = boolArrayCodec.decode(typeProvider, typeProvider.empty(), boolEncodeResult);
		assertArrayEquals(new Boolean[] { true, false }, decodedBoolArray[0]);
		assertArrayEquals(new Boolean[] { false, true, false }, decodedBoolArray[1]);
	}
	
	@Test
	void toStringRepresentation() {
		ArrayCodec<Integer> codec = new ArrayCodec<>(Integer.class, INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ArrayCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("IntegerCodec"));
	}
}
