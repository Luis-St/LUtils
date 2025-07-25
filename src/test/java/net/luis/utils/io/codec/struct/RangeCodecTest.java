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

package net.luis.utils.io.codec.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RangeCodec}.<br>
 *
 * @author Luis-St
 */
class RangeCodecTest {
	
	@Test
	void encodeIntegerStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1));
	}
	
	@Test
	void encodeIntegerStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as number"));
	}
	
	@Test
	void encodeIntegerStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<JsonElement> negativeResult = codec.encodeStart(typeProvider, typeProvider.empty(), -42);
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-42), negativeResult.orThrow());
		
		Result<JsonElement> zeroResult = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0), zeroResult.orThrow());
		
		Result<JsonElement> positiveResult = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(42), positiveResult.orThrow());
		
		Result<JsonElement> maxResult = codec.encodeStart(typeProvider, typeProvider.empty(), Integer.MAX_VALUE);
		assertTrue(maxResult.isSuccess());
		assertEquals(new JsonPrimitive(Integer.MAX_VALUE), maxResult.orThrow());
		
		Result<JsonElement> minResult = codec.encodeStart(typeProvider, typeProvider.empty(), Integer.MIN_VALUE);
		assertTrue(minResult.isSuccess());
		assertEquals(new JsonPrimitive(Integer.MIN_VALUE), minResult.orThrow());
	}
	
	@Test
	void encodeFloatingPointStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<JsonElement> negativeResult = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-3.14), negativeResult.orThrow());
		
		Result<JsonElement> zeroResult = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0);
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0.0), zeroResult.orThrow());
		
		Result<JsonElement> positiveResult = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(3.14), positiveResult.orThrow());
		
		Result<JsonElement> infinityResult = codec.encodeStart(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isSuccess());
		
		Result<JsonElement> nanResult = codec.encodeStart(typeProvider, typeProvider.empty(), Double.NaN);
		assertTrue(nanResult.isSuccess());
	}
	
	@Test
	void encodeIntegerKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, 1));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
	}
	
	@Test
	void encodeIntegerKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<String> negativeResult = codec.encodeKey(typeProvider, -42);
		assertTrue(negativeResult.isSuccess());
		assertEquals("-42", negativeResult.orThrow());
		
		Result<String> zeroResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroResult.isSuccess());
		assertEquals("0", zeroResult.orThrow());
		
		Result<String> positiveResult = codec.encodeKey(typeProvider, 42);
		assertTrue(positiveResult.isSuccess());
		assertEquals("42", positiveResult.orThrow());
		
		Result<String> maxResult = codec.encodeKey(typeProvider, Integer.MAX_VALUE);
		assertTrue(maxResult.isSuccess());
		assertEquals(String.valueOf(Integer.MAX_VALUE), maxResult.orThrow());
		
		Result<String> minResult = codec.encodeKey(typeProvider, Integer.MIN_VALUE);
		assertTrue(minResult.isSuccess());
		assertEquals(String.valueOf(Integer.MIN_VALUE), minResult.orThrow());
	}
	
	@Test
	void encodeFloatingPointKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<String> negativeResult = codec.encodeKey(typeProvider, -3.14);
		assertTrue(negativeResult.isSuccess());
		assertEquals("-3.14", negativeResult.orThrow());
		
		Result<String> zeroResult = codec.encodeKey(typeProvider, 0.0);
		assertTrue(zeroResult.isSuccess());
		assertEquals("0.0", zeroResult.orThrow());
		
		Result<String> positiveResult = codec.encodeKey(typeProvider, 3.14);
		assertTrue(positiveResult.isSuccess());
		assertEquals("3.14", positiveResult.orThrow());
		
		Result<String> scientificResult = codec.encodeKey(typeProvider, 1.23e-4);
		assertTrue(scientificResult.isSuccess());
		assertEquals("1.23E-4", scientificResult.orThrow());
		
		Result<String> infinityResult = codec.encodeKey(typeProvider, Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isSuccess());
		assertEquals("Infinity", infinityResult.orThrow());
		
		Result<String> nanResult = codec.encodeKey(typeProvider, Double.NaN);
		assertTrue(nanResult.isSuccess());
		assertEquals("NaN", nanResult.orThrow());
	}
	
	@Test
	void decodeIntegerStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeIntegerStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<Integer> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as number"));
	}
	
	@Test
	void decodeIntegerStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-42));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.orThrow());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(positiveResult.isSuccess());
		assertEquals(42, positiveResult.orThrow());
		
		Result<Integer> maxResult = codec.decodeStart(typeProvider, new JsonPrimitive(Integer.MAX_VALUE));
		assertTrue(maxResult.isSuccess());
		assertEquals(Integer.MAX_VALUE, maxResult.orThrow());
		
		Result<Integer> minResult = codec.decodeStart(typeProvider, new JsonPrimitive(Integer.MIN_VALUE));
		assertTrue(minResult.isSuccess());
		assertEquals(Integer.MIN_VALUE, minResult.orThrow());
	}
	
	@Test
	void decodeIntegerStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<Integer> stringResult = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(stringResult.isError());
		
		Result<Integer> boolResult = codec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isError());
		
		Result<Integer> doubleResult = codec.decodeStart(typeProvider, new JsonPrimitive(3.14));
		assertTrue(doubleResult.isError());
	}
	
	@Test
	void decodeFloatingPointStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<Double> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-3.14));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-3.14, negativeResult.orThrow());
		
		Result<Double> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0.0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(3.14));
		assertTrue(positiveResult.isSuccess());
		assertEquals(3.14, positiveResult.orThrow());
		
		Result<Double> integerResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(integerResult.isSuccess());
		assertEquals(42.0, integerResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<Double> stringResult = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(stringResult.isError());
		
		Result<Double> boolResult = codec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isError());
	}
	
	@Test
	void decodeIntegerKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
	}
	
	@Test
	void decodeIntegerKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<Integer> negativeResult = codec.decodeKey(typeProvider, "-42");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.orThrow());
		
		Result<Integer> zeroResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = codec.decodeKey(typeProvider, "42");
		assertTrue(positiveResult.isSuccess());
		assertEquals(42, positiveResult.orThrow());
		
		Result<Integer> maxResult = codec.decodeKey(typeProvider, String.valueOf(Integer.MAX_VALUE));
		assertTrue(maxResult.isSuccess());
		assertEquals(Integer.MAX_VALUE, maxResult.orThrow());
		
		Result<Integer> minResult = codec.decodeKey(typeProvider, String.valueOf(Integer.MIN_VALUE));
		assertTrue(minResult.isSuccess());
		assertEquals(Integer.MIN_VALUE, minResult.orThrow());
	}
	
	@Test
	void decodeIntegerKeyWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		Result<Integer> invalidResult = codec.decodeKey(typeProvider, "not-a-number");
		assertTrue(invalidResult.isError());
		
		Result<Integer> emptyResult = codec.decodeKey(typeProvider, "");
		assertTrue(emptyResult.isError());
		
		Result<Integer> spaceResult = codec.decodeKey(typeProvider, " ");
		assertTrue(spaceResult.isError());
		
		Result<Integer> doubleResult = codec.decodeKey(typeProvider, "3.14");
		assertTrue(doubleResult.isError());
		
		Result<Integer> overflowResult = codec.decodeKey(typeProvider, "99999999999999999999");
		assertTrue(overflowResult.isError());
	}
	
	@Test
	void decodeFloatingPointKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<Double> negativeResult = codec.decodeKey(typeProvider, "-3.14");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-3.14, negativeResult.orThrow());
		
		Result<Double> zeroResult = codec.decodeKey(typeProvider, "0.0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> positiveResult = codec.decodeKey(typeProvider, "3.14");
		assertTrue(positiveResult.isSuccess());
		assertEquals(3.14, positiveResult.orThrow());
		
		Result<Double> scientificResult = codec.decodeKey(typeProvider, "1.23e-4");
		assertTrue(scientificResult.isSuccess());
		assertEquals(1.23e-4, scientificResult.orThrow());
		
		Result<Double> integerResult = codec.decodeKey(typeProvider, "42");
		assertTrue(integerResult.isSuccess());
		assertEquals(42.0, integerResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointKeyWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		Result<Double> invalidResult = codec.decodeKey(typeProvider, "not-a-number");
		assertTrue(invalidResult.isError());
		
		Result<Double> emptyResult = codec.decodeKey(typeProvider, "");
		assertTrue(emptyResult.isError());
		
		Result<Double> spaceResult = codec.decodeKey(typeProvider, " ");
		assertTrue(spaceResult.isError());
	}
	
	@Test
	void positiveRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.positive();
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isError());
		assertTrue(zeroResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isError());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
	}
	
	@Test
	void positiveOrZeroRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.positiveOrZero();
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
	}
	
	@Test
	void negativeRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.negative();
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isError());
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
	}
	
	@Test
	void negativeOrZeroRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.negativeOrZero();
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
	}
	
	@Test
	void atLeastRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.atLeast(5);
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(4));
		assertTrue(belowResult.isError());
		
		Result<Integer> exactResult = codec.decodeStart(typeProvider, new JsonPrimitive(5));
		assertTrue(exactResult.isSuccess());
		assertEquals(5, exactResult.orThrow());
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(10));
		assertTrue(aboveResult.isSuccess());
		assertEquals(10, aboveResult.orThrow());
	}
	
	@Test
	void atMostRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.atMost(5);
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(6));
		assertTrue(aboveResult.isError());
		
		Result<Integer> exactResult = codec.decodeStart(typeProvider, new JsonPrimitive(5));
		assertTrue(exactResult.isSuccess());
		assertEquals(5, exactResult.orThrow());
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(belowResult.isSuccess());
		assertEquals(0, belowResult.orThrow());
	}
	
	@Test
	void customRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.range(10, 20);
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(9));
		assertTrue(belowResult.isError());
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(21));
		assertTrue(aboveResult.isError());
		
		Result<Integer> lowerBoundResult = codec.decodeStart(typeProvider, new JsonPrimitive(10));
		assertTrue(lowerBoundResult.isSuccess());
		assertEquals(10, lowerBoundResult.orThrow());
		
		Result<Integer> upperBoundResult = codec.decodeStart(typeProvider, new JsonPrimitive(20));
		assertTrue(upperBoundResult.isSuccess());
		assertEquals(20, upperBoundResult.orThrow());
		
		Result<Integer> middleResult = codec.decodeStart(typeProvider, new JsonPrimitive(15));
		assertTrue(middleResult.isSuccess());
		assertEquals(15, middleResult.orThrow());
	}
	
	@Test
	void doubleRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.range(1.0, 10.0);
		
		Result<Double> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(0.9));
		assertTrue(belowResult.isError());
		
		Result<Double> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(10.1));
		assertTrue(aboveResult.isError());
		
		Result<Double> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(5.5));
		assertTrue(validResult.isSuccess());
		assertEquals(5.5, validResult.orThrow());
	}
	
	@Test
	void rangeMethodNullChecks() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.atLeast(null));
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.atMost(null));
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.range(null, 10));
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.range(0, null));
		
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.atLeast(null));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.atMost(null));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.range(null, 10.0));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.range(0.0, null));
	}
	
	@Test
	void rangeMethodsReturnNewInstances() {
		RangeCodec<Integer> original = Codec.INTEGER;
		
		assertNotSame(original, original.positive());
		assertNotSame(original, original.positiveOrZero());
		assertNotSame(original, original.negative());
		assertNotSame(original, original.negativeOrZero());
		assertNotSame(original, original.atLeast(0));
		assertNotSame(original, original.atMost(100));
		assertNotSame(original, original.range(0, 100));
	}
	
	@Test
	void equalsAndHashCode() {
		RangeCodec<Integer> codec1 = Codec.INTEGER.range(0, 100);
		RangeCodec<Integer> codec2 = Codec.INTEGER.range(0, 100);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		RangeCodec<Integer> codec = Codec.INTEGER.range(0, 100);
		String result = codec.toString();
		
		assertTrue(result.contains("Range"));
		assertTrue(result.contains("Codec"));
		assertTrue(result.contains("0..100"));
	}
}
