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
import net.luis.utils.io.codec.internal.struct.ArrayCodec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
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
		assertThrows(NullPointerException.class, () -> new ArrayCodec<>(null, INTEGER, 0, Integer.MAX_VALUE));
		assertThrows(NullPointerException.class, () -> new ArrayCodec<>(Integer.class, null, 0, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ArrayCodec<>(Integer.class, INTEGER, -1, Integer.MAX_VALUE));
		assertThrows(IllegalArgumentException.class, () -> new ArrayCodec<>(Integer.class, INTEGER, 1, 0));
		assertDoesNotThrow(() -> new ArrayCodec<>(Integer.class, INTEGER));
		assertDoesNotThrow(() -> new ArrayCodec<>(Integer.class, INTEGER, 0, 10));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = new Integer[] { 1, 2, 3 };
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), array));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, array));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as array"));
	}
	
	@Test
	void encodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = new Integer[] { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive(1));
		expected.add(new JsonPrimitive(2));
		expected.add(new JsonPrimitive(3));
		
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] array = new Integer[] {};
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		assertEquals(new JsonArray(), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[]> codec = new ArrayCodec<>(String.class, STRING);
		String[] array = new String[] { "hello" };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
		
		JsonArray expected = new JsonArray();
		expected.add(new JsonPrimitive("hello"));
		assertEquals(expected, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String[]> stringCodec = new ArrayCodec<>(String.class, STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, typeProvider.empty(), new String[] { "a", "b" });
		assertTrue(stringResult.isSuccess());
		
		Codec<Boolean[]> boolCodec = new ArrayCodec<>(Boolean.class, BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, typeProvider.empty(), new Boolean[] { true, false });
		assertTrue(boolResult.isSuccess());
	}
	
	@Test
	void encodeStartSizedExactMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 3, 3);
		Integer[] array = new Integer[] { 1, 2, 3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartSizedTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 3, 5);
		Integer[] array = new Integer[] { 1, 2 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Array length '2' is out of range: 3..5"));
	}
	
	@Test
	void encodeStartSizedTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 1, 3);
		Integer[] array = new Integer[] { 1, 2, 3, 4 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Array length '4' is out of range: 1..3"));
	}
	
	@Test
	void encodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		Integer[] arrayWithNull = new Integer[] { 1, null, 3 };
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), arrayWithNull);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode some elements"));
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonArray()));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as array"));
	}
	
	@Test
	void decodeStartWithValidArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new Integer[] { 1, 2, 3 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithEmptyArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, new JsonArray());
		assertTrue(result.isSuccess());
		assertEquals(0, result.resultOrThrow().length);
	}
	
	@Test
	void decodeStartWithSingleElement() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[]> codec = new ArrayCodec<>(String.class, STRING);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive("hello"));
		
		Result<String[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new String[] { "hello" }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNonArray() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode array"));
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		JsonArray stringArray = new JsonArray();
		stringArray.add(new JsonPrimitive("a"));
		stringArray.add(new JsonPrimitive("b"));
		
		Codec<String[]> stringCodec = new ArrayCodec<>(String.class, STRING);
		Result<String[]> stringResult = stringCodec.decodeStart(typeProvider, stringArray);
		assertTrue(stringResult.isSuccess());
		assertArrayEquals(new String[] { "a", "b" }, stringResult.resultOrThrow());
		
		JsonArray boolArray = new JsonArray();
		boolArray.add(new JsonPrimitive(true));
		boolArray.add(new JsonPrimitive(false));
		
		Codec<Boolean[]> boolCodec = new ArrayCodec<>(Boolean.class, BOOLEAN);
		Result<Boolean[]> boolResult = boolCodec.decodeStart(typeProvider, boolArray);
		assertTrue(boolResult.isSuccess());
		assertArrayEquals(new Boolean[] { true, false }, boolResult.resultOrThrow());
	}
	
	@Test
	void decodeStartSizedExactMatch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 2, 2);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isSuccess());
		assertArrayEquals(new Integer[] { 1, 2 }, result.resultOrThrow());
	}
	
	@Test
	void decodeStartSizedTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 3, 5);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("length '2' is out of range: 3..5"));
	}
	
	@Test
	void decodeStartSizedTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER, 1, 2);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("length '3' is out of range: 1..2"));
	}
	
	@Test
	void decodeStartWithInvalidElements() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive("not-a-number"));
		array.add(new JsonPrimitive(3));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode some elements"));
	}
	
	@Test
	void decodeStartWithMixedValidInvalid() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[]> codec = new ArrayCodec<>(Integer.class, INTEGER);
		
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(42));
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(100));
		
		Result<Integer[]> result = codec.decodeStart(typeProvider, array);
		assertTrue(result.isError());
	}
	
	@Test
	void nestedTwoDimensionalArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] originalArray = new Integer[][] {
			{ 1, 2, 3 },
			{ 4, 5, 6 },
			{ 7, 8, 9 }
		};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), originalArray);
		assertTrue(encodeResult.isSuccess());
		
		JsonArray outerArray = assertInstanceOf(JsonArray.class, encodeResult.resultOrThrow());
		assertEquals(3, outerArray.size());
		
		JsonArray firstInnerArray = outerArray.get(0).getAsJsonArray();
		assertEquals(3, firstInnerArray.size());
		assertEquals(1, firstInnerArray.get(0).getAsJsonPrimitive().getAsInteger());
		assertEquals(2, firstInnerArray.get(1).getAsJsonPrimitive().getAsInteger());
		assertEquals(3, firstInnerArray.get(2).getAsJsonPrimitive().getAsInteger());
		
		Result<Integer[][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		Integer[][] decoded = decodeResult.resultOrThrow();
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, decoded[0]);
		assertArrayEquals(new Integer[] { 4, 5, 6 }, decoded[1]);
		assertArrayEquals(new Integer[] { 7, 8, 9 }, decoded[2]);
	}
	
	@Test
	void nestedThreeDimensionalArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][][]> codec = new ArrayCodec<>(Integer[][].class, new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER)));
		
		Integer[][][] originalArray = new Integer[][][] {
			{
				{ 1, 2 },
				{ 3, 4 }
			},
			{
				{ 5, 6 },
				{ 7, 8 }
			}
		};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), originalArray);
		assertTrue(encodeResult.isSuccess());
		
		Result<Integer[][][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		Integer[][][] decoded = decodeResult.resultOrThrow();
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
	void jaggedNestedArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] jaggedArray = new Integer[][] {
			{ 1, 2, 3, 4, 5 },
			{ 6, 7 },
			{ 8, 9, 10 }
		};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), jaggedArray);
		assertTrue(encodeResult.isSuccess());
		
		Result<Integer[][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		Integer[][] decoded = decodeResult.resultOrThrow();
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, decoded[0]);
		assertArrayEquals(new Integer[] { 6, 7 }, decoded[1]);
		assertArrayEquals(new Integer[] { 8, 9, 10 }, decoded[2]);
	}
	
	@Test
	void emptyNestedArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String[][]> codec = new ArrayCodec<>(String[].class, new ArrayCodec<>(String.class, STRING));
		
		String[][] emptyNestedArray = new String[][] {};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), emptyNestedArray);
		assertTrue(encodeResult.isSuccess());
		assertEquals(new JsonArray(), encodeResult.resultOrThrow());
		
		Result<String[][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		assertEquals(0, decodeResult.resultOrThrow().length);
	}
	
	@Test
	void nestedArraysWithSomeEmptyInnerArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class, new ArrayCodec<>(Integer.class, INTEGER));
		
		Integer[][] arrayWithEmptyInner = new Integer[][] {
			{ 1, 2, 3 },
			{},
			{ 4, 5 }
		};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), arrayWithEmptyInner);
		assertTrue(encodeResult.isSuccess());
		
		Result<Integer[][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		Integer[][] decoded = decodeResult.resultOrThrow();
		assertEquals(3, decoded.length);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, decoded[0]);
		assertEquals(0, decoded[1].length);
		assertArrayEquals(new Integer[] { 4, 5 }, decoded[2]);
	}
	
	@Test
	void nestedArraysWithConstraints() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer[][]> codec = new ArrayCodec<>(Integer[].class,
			new ArrayCodec<>(Integer.class, INTEGER, 2, 4), 2, 3);
		
		Integer[][] validArray = new Integer[][] {
			{ 1, 2, 3 },
			{ 4, 5, 6, 7 }
		};
		
		Result<JsonElement> encodeResult = codec.encodeStart(typeProvider, typeProvider.empty(), validArray);
		assertTrue(encodeResult.isSuccess());
		
		Result<Integer[][]> decodeResult = codec.decodeStart(typeProvider, encodeResult.resultOrThrow());
		assertTrue(decodeResult.isSuccess());
		
		Integer[][] invalidInnerArray = new Integer[][] {
			{ 1 },
			{ 2, 3 }
		};
		
		Result<JsonElement> invalidResult = codec.encodeStart(typeProvider, typeProvider.empty(), invalidInnerArray);
		assertTrue(invalidResult.isError());
		
		Integer[][] tooManyOuter = new Integer[][] {
			{ 1, 2 },
			{ 3, 4 },
			{ 5, 6 },
			{ 7, 8 }
		};
		
		Result<JsonElement> tooManyResult = codec.encodeStart(typeProvider, typeProvider.empty(), tooManyOuter);
		assertTrue(tooManyResult.isError());
	}
	
	@Test
	void mixedTypeNestedArrays() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String[][]> stringArrayCodec = new ArrayCodec<>(String[].class, new ArrayCodec<>(String.class, STRING));
		String[][] stringArray = new String[][] {
			{ "hello", "world" },
			{ "foo", "bar", "baz" }
		};
		
		Result<JsonElement> stringEncodeResult = stringArrayCodec.encodeStart(typeProvider, typeProvider.empty(), stringArray);
		assertTrue(stringEncodeResult.isSuccess());
		
		Result<String[][]> stringDecodeResult = stringArrayCodec.decodeStart(typeProvider, stringEncodeResult.resultOrThrow());
		assertTrue(stringDecodeResult.isSuccess());
		String[][] decodedStringArray = stringDecodeResult.resultOrThrow();
		assertArrayEquals(new String[] { "hello", "world" }, decodedStringArray[0]);
		assertArrayEquals(new String[] { "foo", "bar", "baz" }, decodedStringArray[1]);
		
		Codec<Boolean[][]> boolArrayCodec = new ArrayCodec<>(Boolean[].class, new ArrayCodec<>(Boolean.class, BOOLEAN));
		Boolean[][] boolArray = new Boolean[][] {
			{ true, false },
			{ false, true, false }
		};
		
		Result<JsonElement> boolEncodeResult = boolArrayCodec.encodeStart(typeProvider, typeProvider.empty(), boolArray);
		assertTrue(boolEncodeResult.isSuccess());
		
		Result<Boolean[][]> boolDecodeResult = boolArrayCodec.decodeStart(typeProvider, boolEncodeResult.resultOrThrow());
		assertTrue(boolDecodeResult.isSuccess());
		Boolean[][] decodedBoolArray = boolDecodeResult.resultOrThrow();
		assertArrayEquals(new Boolean[] { true, false }, decodedBoolArray[0]);
		assertArrayEquals(new Boolean[] { false, true, false }, decodedBoolArray[1]);
	}
	
	@Test
	void equalsAndHashCode() {
		ArrayCodec<Integer> codec1 = new ArrayCodec<>(Integer.class, INTEGER, 0, 10);
		ArrayCodec<Integer> codec2 = new ArrayCodec<>(Integer.class, INTEGER, 0, 10);
		ArrayCodec<Integer> codec3 = new ArrayCodec<>(Integer.class, INTEGER, 0, 5);
		
		assertEquals(codec1, codec2);
		assertEquals(codec1.hashCode(), codec2.hashCode());
		assertNotEquals(codec1, codec3);
		assertNotEquals(codec1.hashCode(), codec3.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		ArrayCodec<Integer> codec = new ArrayCodec<>(Integer.class, INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("ArrayCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("IntegerCodec"));
		
		ArrayCodec<Integer> sizedCodec = new ArrayCodec<>(Integer.class, INTEGER, 1, 5);
		String sizedResult = sizedCodec.toString();
		assertTrue(sizedResult.contains("1..5"));
	}
}
