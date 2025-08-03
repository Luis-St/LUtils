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

package net.luis.utils.io.codec.mapping;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecArrayHelper}.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class CodecArrayHelperTest {
	
	@Test
	void nullParametersThrowExceptions() {
		Function<Class<?>, Codec<Object>> validGetter = clazz -> (Codec) STRING;
		
		assertThrows(NullPointerException.class, () -> CodecArrayHelper.getOrCreateArrayCodec(null, validGetter));
		assertThrows(NullPointerException.class, () -> CodecArrayHelper.getOrCreateArrayCodec(String[].class, null));
	}
	
	@Test
	void nonArrayClassThrowsException() {
		Function<Class<?>, Codec<Object>> validGetter = clazz -> (Codec) STRING;
		
		assertThrows(IllegalArgumentException.class, () -> CodecArrayHelper.getOrCreateArrayCodec(String.class, validGetter));
		assertThrows(IllegalArgumentException.class, () -> CodecArrayHelper.getOrCreateArrayCodec(int.class, validGetter));
		assertThrows(IllegalArgumentException.class, () -> CodecArrayHelper.getOrCreateArrayCodec(Object.class, validGetter));
	}
	
	@Test
	void singleDimensionalPrimitiveArraysWork() {
		Function<Class<?>, Codec<Object>> getter = clazz -> (Codec) STRING;
		
		Codec<boolean[]> booleanCodec = CodecArrayHelper.getOrCreateArrayCodec(boolean[].class, getter);
		assertNotNull(booleanCodec);
		
		boolean[] booleanArray = { true, false, true };
		JsonElement encodedBoolean = booleanCodec.encode(JsonTypeProvider.INSTANCE, booleanArray);
		boolean[] decodedBoolean = booleanCodec.decode(JsonTypeProvider.INSTANCE, encodedBoolean);
		assertArrayEquals(booleanArray, decodedBoolean);
		
		Codec<int[]> intCodec = CodecArrayHelper.getOrCreateArrayCodec(int[].class, getter);
		assertNotNull(intCodec);
		
		int[] intArray = { 1, 2, 3, -1, Integer.MAX_VALUE };
		JsonElement encodedInt = intCodec.encode(JsonTypeProvider.INSTANCE, intArray);
		int[] decodedInt = intCodec.decode(JsonTypeProvider.INSTANCE, encodedInt);
		assertArrayEquals(intArray, decodedInt);
		
		Codec<double[]> doubleCodec = CodecArrayHelper.getOrCreateArrayCodec(double[].class, getter);
		assertNotNull(doubleCodec);
		
		double[] doubleArray = { 1.5, -2.7, 0.0, Double.MAX_VALUE };
		JsonElement encodedDouble = doubleCodec.encode(JsonTypeProvider.INSTANCE, doubleArray);
		double[] decodedDouble = doubleCodec.decode(JsonTypeProvider.INSTANCE, encodedDouble);
		assertArrayEquals(doubleArray, decodedDouble);
	}
	
	@Test
	void singleDimensionalObjectArraysWork() {
		JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
		Function<Class<?>, Codec<Object>> getter = clazz -> {
			if (clazz == String.class) {
				return (Codec) STRING;
			} else if (clazz == LocalDate.class) {
				return (Codec) LOCAL_DATE;
			}
			throw new IllegalArgumentException("Unsupported type: " + clazz);
		};
		
		Codec<String[]> stringCodec = CodecArrayHelper.getOrCreateArrayCodec(String[].class, getter);
		assertNotNull(stringCodec);
		
		String[] stringArray = { "hello", "world", "", "test" };
		JsonElement encodedString = stringCodec.encode(provider, stringArray);
		String[] decodedString = stringCodec.decode(provider, encodedString);
		assertArrayEquals(stringArray, decodedString);
		
		Codec<LocalDate[]> dateCodec = CodecArrayHelper.getOrCreateArrayCodec(LocalDate[].class, getter);
		assertNotNull(dateCodec);
		
		LocalDate[] dateArray = {
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		};
		JsonElement encodedDate = dateCodec.encode(provider, dateArray);
		LocalDate[] decodedDate = dateCodec.decode(provider, encodedDate);
		assertArrayEquals(dateArray, decodedDate);
	}
	
	@Test
	void multiDimensionalArraysWork() {
		Function<Class<?>, Codec<Object>> getter = clazz -> {
			if (clazz == String.class) {
				return (Codec) STRING;
			}
			throw new IllegalArgumentException("Unsupported type: " + clazz);
		};
		
		Codec<String[][]> string2DCodec = CodecArrayHelper.getOrCreateArrayCodec(String[][].class, getter);
		assertNotNull(string2DCodec);
		
		String[][] string2DArray = {
			{ "a", "b", "c" },
			{ "d", "e", "f" }
		};
		JsonElement encoded2D = string2DCodec.encode(JsonTypeProvider.INSTANCE, string2DArray);
		String[][] decoded2D = string2DCodec.decode(JsonTypeProvider.INSTANCE, encoded2D);
		
		assertEquals(string2DArray.length, decoded2D.length);
		for (int i = 0; i < string2DArray.length; i++) {
			assertArrayEquals(string2DArray[i], decoded2D[i]);
		}
		
		Codec<int[][][]> int3DCodec = CodecArrayHelper.getOrCreateArrayCodec(int[][][].class, getter);
		assertNotNull(int3DCodec);
		
		int[][][] int3DArray = {
			{
				{ 1, 2 },
				{ 3, 4 }
			},
			{
				{ 5, 6 },
				{ 7, 8 }
			}
		};
		JsonElement encoded3D = int3DCodec.encode(JsonTypeProvider.INSTANCE, int3DArray);
		int[][][] decoded3D = int3DCodec.decode(JsonTypeProvider.INSTANCE, encoded3D);
		
		assertEquals(int3DArray.length, decoded3D.length);
		for (int i = 0; i < int3DArray.length; i++) {
			assertEquals(int3DArray[i].length, decoded3D[i].length);
			for (int j = 0; j < int3DArray[i].length; j++) {
				assertArrayEquals(int3DArray[i][j], decoded3D[i][j]);
			}
		}
	}
	
	@Test
	void emptyArraysAreSupported() {
		Function<Class<?>, Codec<Object>> getter = clazz -> (Codec) STRING;
		
		Codec<int[]> intCodec = CodecArrayHelper.getOrCreateArrayCodec(int[].class, getter);
		int[] emptyIntArray = new int[0];
		JsonElement encodedEmpty = intCodec.encode(JsonTypeProvider.INSTANCE, emptyIntArray);
		int[] decodedEmpty = intCodec.decode(JsonTypeProvider.INSTANCE, encodedEmpty);
		assertEquals(0, decodedEmpty.length);
		
		Codec<String[]> stringCodec = CodecArrayHelper.getOrCreateArrayCodec(String[].class, getter);
		String[] emptyStringArray = new String[0];
		JsonElement encodedEmptyString = stringCodec.encode(JsonTypeProvider.INSTANCE, emptyStringArray);
		String[] decodedEmptyString = stringCodec.decode(JsonTypeProvider.INSTANCE, encodedEmptyString);
		assertEquals(0, decodedEmptyString.length);
	}
	
	@Test
	void listToTypedArrayNullParametersThrowExceptions() {
		assertThrows(NullPointerException.class, () -> CodecArrayHelper.listToTypedArray(null, String.class, 1));
		assertThrows(NullPointerException.class, () -> CodecArrayHelper.listToTypedArray(List.of(), null, 1));
	}
	
	@Test
	void emptyListCreatesEmptyArrays() {
		Object result1D = CodecArrayHelper.listToTypedArray(List.of(), String.class, 1);
		assertInstanceOf(String[].class, result1D);
		String[] stringArray = (String[]) result1D;
		assertEquals(0, stringArray.length);
		
		Object result2D = CodecArrayHelper.listToTypedArray(List.of(), int.class, 2);
		assertInstanceOf(int[][].class, result2D);
		int[][] intArray2D = (int[][]) result2D;
		assertEquals(0, intArray2D.length);
		
		Object result3D = CodecArrayHelper.listToTypedArray(List.of(), boolean.class, 3);
		assertInstanceOf(boolean[][][].class, result3D);
		boolean[][][] boolArray3D = (boolean[][][]) result3D;
		assertEquals(0, boolArray3D.length);
	}
	
	@Test
	void singleDimensionalArrayConversionWorks() {
		List<String> stringList = List.of("hello", "world", "test");
		Object stringResult = CodecArrayHelper.listToTypedArray(stringList, String.class, 1);
		assertInstanceOf(String[].class, stringResult);
		String[] stringArray = (String[]) stringResult;
		assertArrayEquals(new String[] { "hello", "world", "test" }, stringArray);
		
		List<Integer> intList = List.of(1, 2, 3, 4, 5);
		Object intResult = CodecArrayHelper.listToTypedArray(intList, Integer.class, 1);
		assertInstanceOf(Integer[].class, intResult);
		Integer[] intArray = (Integer[]) intResult;
		assertArrayEquals(new Integer[] { 1, 2, 3, 4, 5 }, intArray);
		
		Object primitiveIntResult = CodecArrayHelper.listToTypedArray(intList, int.class, 1);
		assertInstanceOf(int[].class, primitiveIntResult);
		int[] primitiveIntArray = (int[]) primitiveIntResult;
		assertArrayEquals(new int[] { 1, 2, 3, 4, 5 }, primitiveIntArray);
	}
	
	@Test
	void twoDimensionalArrayConversionWorks() {
		List<List<String>> string2DList = List.of(
			List.of("a", "b", "c"),
			List.of("d", "e", "f"),
			List.of("g", "h", "i")
		);
		
		Object string2DResult = CodecArrayHelper.listToTypedArray(string2DList, String.class, 2);
		assertInstanceOf(String[][].class, string2DResult);
		String[][] string2DArray = (String[][]) string2DResult;
		
		assertEquals(3, string2DArray.length);
		assertArrayEquals(new String[] { "a", "b", "c" }, string2DArray[0]);
		assertArrayEquals(new String[] { "d", "e", "f" }, string2DArray[1]);
		assertArrayEquals(new String[] { "g", "h", "i" }, string2DArray[2]);
		
		List<List<Integer>> int2DList = List.of(
			List.of(1, 2, 3),
			List.of(4, 5, 6)
		);
		
		Object int2DResult = CodecArrayHelper.listToTypedArray(int2DList, int.class, 2);
		assertInstanceOf(int[][].class, int2DResult);
		int[][] int2DArray = (int[][]) int2DResult;
		
		assertEquals(2, int2DArray.length);
		assertArrayEquals(new int[] { 1, 2, 3 }, int2DArray[0]);
		assertArrayEquals(new int[] { 4, 5, 6 }, int2DArray[1]);
	}
	
	@Test
	void threeDimensionalArrayConversionWorks() {
		List<List<List<Integer>>> int3DList = List.of(
			List.of(
				List.of(1, 2),
				List.of(3, 4)
			),
			List.of(
				List.of(5, 6),
				List.of(7, 8)
			)
		);
		
		Object int3DResult = CodecArrayHelper.listToTypedArray(int3DList, int.class, 3);
		assertInstanceOf(int[][][].class, int3DResult);
		int[][][] int3DArray = (int[][][]) int3DResult;
		
		assertEquals(2, int3DArray.length);
		assertEquals(2, int3DArray[0].length);
		assertEquals(2, int3DArray[0][0].length);
		assertEquals(1, int3DArray[0][0][0]);
		assertEquals(2, int3DArray[0][0][1]);
		assertEquals(3, int3DArray[0][1][0]);
		assertEquals(4, int3DArray[0][1][1]);
		assertEquals(5, int3DArray[1][0][0]);
		assertEquals(6, int3DArray[1][0][1]);
		assertEquals(7, int3DArray[1][1][0]);
		assertEquals(8, int3DArray[1][1][1]);
	}
	
	@Test
	void jaggedArrayConversionWorks() {
		List<List<String>> jaggedList = List.of(
			List.of("a"),
			List.of("b", "c", "d"),
			List.of("e", "f"),
			List.of("g", "h", "i", "j", "k")
		);
		
		Object jaggedResult = CodecArrayHelper.listToTypedArray(jaggedList, String.class, 2);
		String[][] jaggedArray = assertInstanceOf(String[][].class, jaggedResult);
		
		assertEquals(4, jaggedArray.length);
		assertArrayEquals(new String[] { "a" }, jaggedArray[0]);
		assertArrayEquals(new String[] { "b", "c", "d" }, jaggedArray[1]);
		assertArrayEquals(new String[] { "e", "f" }, jaggedArray[2]);
		assertArrayEquals(new String[] { "g", "h", "i", "j", "k" }, jaggedArray[3]);
	}
	
	@Test
	void differentComponentTypesWork() {
		List<LocalDate> dateList = List.of(
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		);
		
		Object dateResult = CodecArrayHelper.listToTypedArray(dateList, LocalDate.class, 1);
		LocalDate[] dateArray = assertInstanceOf(LocalDate[].class, dateResult);
		assertArrayEquals(new LocalDate[] {
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		}, dateArray);
		
		List<Boolean> booleanList = List.of(true, false, true, false);
		Object booleanResult = CodecArrayHelper.listToTypedArray(booleanList, Boolean.class, 1);
		Boolean[] booleanArray = assertInstanceOf(Boolean[].class, booleanResult);
		assertArrayEquals(new Boolean[] { true, false, true, false }, booleanArray);
		
		Object primitiveBooleanResult = CodecArrayHelper.listToTypedArray(booleanList, boolean.class, 1);
		assertInstanceOf(boolean[].class, primitiveBooleanResult);
		boolean[] primitiveBooleanArray = (boolean[]) primitiveBooleanResult;
		assertArrayEquals(new boolean[] { true, false, true, false }, primitiveBooleanArray);
	}
	
	@Test
	void emptySubListsInJaggedArraysWork() {
		List<List<Integer>> jaggedWithEmptyList = List.of(
			List.of(1, 2, 3),
			List.of(),
			List.of(4, 5)
		);
		
		Object jaggedResult = CodecArrayHelper.listToTypedArray(jaggedWithEmptyList, int.class, 2);
		int[][] jaggedArray = assertInstanceOf(int[][].class, jaggedResult);
		
		assertEquals(3, jaggedArray.length);
		assertArrayEquals(new int[] { 1, 2, 3 }, jaggedArray[0]);
		assertArrayEquals(new int[] {}, jaggedArray[1]);
		assertArrayEquals(new int[] { 4, 5 }, jaggedArray[2]);
	}
	
	@Test
	void arrayToTypedListNullParameterThrowsException() {
		assertThrows(NullPointerException.class, () -> CodecArrayHelper.arrayToTypedList(null));
	}
	
	@Test
	void singleDimensionalArraysConvertToListsCorrectly() {
		int[] intArray = { 1, 2, 3, 4, 5 };
		List<Integer> intList = CodecArrayHelper.arrayToTypedList(intArray);
		assertEquals(List.of(1, 2, 3, 4, 5), intList);
		
		boolean[] boolArray = { true, false, true };
		List<Boolean> boolList = CodecArrayHelper.arrayToTypedList(boolArray);
		assertEquals(List.of(true, false, true), boolList);
		
		double[] doubleArray = { 1.5, 2.7, -3.14 };
		List<Double> doubleList = CodecArrayHelper.arrayToTypedList(doubleArray);
		assertEquals(List.of(1.5, 2.7, -3.14), doubleList);
		
		String[] stringArray = { "hello", "world", "test" };
		List<String> stringList = CodecArrayHelper.arrayToTypedList(stringArray);
		assertEquals(List.of("hello", "world", "test"), stringList);
		
		LocalDate[] dateArray = {
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		};
		List<LocalDate> dateList = CodecArrayHelper.arrayToTypedList(dateArray);
		assertEquals(List.of(
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 12, 31)
		), dateList);
	}
	
	@Test
	void emptyArraysConvertToEmptyLists() {
		int[] emptyIntArray = new int[0];
		List<Integer> emptyIntList = CodecArrayHelper.arrayToTypedList(emptyIntArray);
		assertTrue(emptyIntList.isEmpty());
		
		String[] emptyStringArray = new String[0];
		List<String> emptyStringList = CodecArrayHelper.arrayToTypedList(emptyStringArray);
		assertTrue(emptyStringList.isEmpty());
	}
	
	@Test
	void twoDimensionalArraysConvertToNestedLists() {
		int[][] int2DArray = {
			{ 1, 2, 3 },
			{ 4, 5, 6 },
			{ 7, 8, 9 }
		};
		List<List<Integer>> int2DList = CodecArrayHelper.arrayToTypedList(int2DArray);
		
		assertEquals(3, int2DList.size());
		assertEquals(List.of(1, 2, 3), int2DList.getFirst());
		assertEquals(List.of(4, 5, 6), int2DList.get(1));
		assertEquals(List.of(7, 8, 9), int2DList.get(2));
		
		String[][] string2DArray = {
			{ "a", "b" },
			{ "c", "d" },
			{ "e", "f" }
		};
		List<List<String>> string2DList = CodecArrayHelper.arrayToTypedList(string2DArray);
		
		assertEquals(3, string2DList.size());
		assertEquals(List.of("a", "b"), string2DList.getFirst());
		assertEquals(List.of("c", "d"), string2DList.get(1));
		assertEquals(List.of("e", "f"), string2DList.get(2));
	}
	
	@Test
	void threeDimensionalArraysConvertToTriplyNestedLists() {
		int[][][] int3DArray = {
			{
				{ 1, 2 },
				{ 3, 4 }
			},
			{
				{ 5, 6 },
				{ 7, 8 }
			}
		};
		List<List<List<Integer>>> int3DList = CodecArrayHelper.arrayToTypedList(int3DArray);
		
		assertEquals(2, int3DList.size());
		assertEquals(2, int3DList.getFirst().size());
		assertEquals(2, int3DList.getFirst().getFirst().size());
		assertEquals(List.of(1, 2), int3DList.getFirst().getFirst());
		assertEquals(List.of(3, 4), int3DList.getFirst().get(1));
		assertEquals(List.of(5, 6), int3DList.get(1).getFirst());
		assertEquals(List.of(7, 8), int3DList.get(1).get(1));
	}
	
	@Test
	void jaggedArraysConvertCorrectly() {
		String[][] jaggedArray = {
			{ "a" },
			{ "b", "c", "d" },
			{ "e", "f" },
			{ "g", "h", "i", "j", "k" }
		};
		List<List<String>> jaggedList = CodecArrayHelper.arrayToTypedList(jaggedArray);
		
		assertEquals(4, jaggedList.size());
		assertEquals(List.of("a"), jaggedList.getFirst());
		assertEquals(List.of("b", "c", "d"), jaggedList.get(1));
		assertEquals(List.of("e", "f"), jaggedList.get(2));
		assertEquals(List.of("g", "h", "i", "j", "k"), jaggedList.get(3));
		
		int[][] jaggedIntArray = {
			{ 1, 2, 3, 4, 5 },
			{ 6, 7 },
			{ 8, 9, 10 }
		};
		List<List<Integer>> jaggedIntList = CodecArrayHelper.arrayToTypedList(jaggedIntArray);
		
		assertEquals(3, jaggedIntList.size());
		assertEquals(List.of(1, 2, 3, 4, 5), jaggedIntList.getFirst());
		assertEquals(List.of(6, 7), jaggedIntList.get(1));
		assertEquals(List.of(8, 9, 10), jaggedIntList.get(2));
	}
	
	@Test
	void emptySubArraysInJaggedArraysConvertCorrectly() {
		int[][] jaggedWithEmpty = {
			{ 1, 2, 3 },
			{},
			{ 4, 5 },
			{}
		};
		List<List<Integer>> jaggedList = CodecArrayHelper.arrayToTypedList(jaggedWithEmpty);
		
		assertEquals(4, jaggedList.size());
		assertEquals(List.of(1, 2, 3), jaggedList.getFirst());
		assertTrue(jaggedList.get(1).isEmpty());
		assertEquals(List.of(4, 5), jaggedList.get(2));
		assertTrue(jaggedList.get(3).isEmpty());
	}
	
	@Test
	void complexObjectArraysConvertCorrectly() {
		LocalDate[][] dateMatrix = {
			{
				LocalDate.of(2025, 1, 1),
				LocalDate.of(2025, 1, 2)
			},
			{
				LocalDate.of(2025, 6, 15),
				LocalDate.of(2025, 6, 16)
			}
		};
		List<List<LocalDate>> dateList = CodecArrayHelper.arrayToTypedList(dateMatrix);
		
		assertEquals(2, dateList.size());
		assertEquals(List.of(
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 1, 2)
		), dateList.getFirst());
		assertEquals(List.of(
			LocalDate.of(2025, 6, 15),
			LocalDate.of(2025, 6, 16)
		), dateList.get(1));
	}
	
	@Test
	void roundTripConversionMaintainsDataIntegrity() {
		int[][] originalArray = {
			{ 1, 2, 3 },
			{ 4, 5 },
			{ 6, 7, 8, 9 }
		};
		
		List<List<Integer>> convertedList = CodecArrayHelper.arrayToTypedList(originalArray);
		
		Object reconvertedArray = CodecArrayHelper.listToTypedArray(convertedList, int.class, 2);
		int[][] finalArray = assertInstanceOf(int[][].class, reconvertedArray);
		
		assertEquals(originalArray.length, finalArray.length);
		for (int i = 0; i < originalArray.length; i++) {
			assertArrayEquals(originalArray[i], finalArray[i]);
		}
		
		String[] originalStringArray = { "hello", "world", "test", "" };
		List<String> stringList = CodecArrayHelper.arrayToTypedList(originalStringArray);
		Object reconvertedStringArray = CodecArrayHelper.listToTypedArray(stringList, String.class, 1);
		String[] finalStringArray = assertInstanceOf(String[].class, reconvertedStringArray);
		assertArrayEquals(originalStringArray, finalStringArray);
	}
}
