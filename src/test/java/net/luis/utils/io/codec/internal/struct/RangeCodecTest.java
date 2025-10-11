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

import net.luis.utils.io.codec.internal.struct.RangeCodec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
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
		RangeCodec<Integer> codec = INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1));
	}
	
	@Test
	void encodeIntegerStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as number"));
	}
	
	@Test
	void encodeIntegerStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<JsonElement> negativeResult = codec.encodeStart(typeProvider, typeProvider.empty(), -42);
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-42), negativeResult.resultOrThrow());
		
		Result<JsonElement> zeroResult = codec.encodeStart(typeProvider, typeProvider.empty(), 0);
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0), zeroResult.resultOrThrow());
		
		Result<JsonElement> positiveResult = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(42), positiveResult.resultOrThrow());
		
		Result<JsonElement> maxResult = codec.encodeStart(typeProvider, typeProvider.empty(), Integer.MAX_VALUE);
		assertTrue(maxResult.isSuccess());
		assertEquals(new JsonPrimitive(Integer.MAX_VALUE), maxResult.resultOrThrow());
		
		Result<JsonElement> minResult = codec.encodeStart(typeProvider, typeProvider.empty(), Integer.MIN_VALUE);
		assertTrue(minResult.isSuccess());
		assertEquals(new JsonPrimitive(Integer.MIN_VALUE), minResult.resultOrThrow());
	}
	
	@Test
	void encodeFloatingPointStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE;
		
		Result<JsonElement> negativeResult = codec.encodeStart(typeProvider, typeProvider.empty(), -3.14);
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-3.14), negativeResult.resultOrThrow());
		
		Result<JsonElement> zeroResult = codec.encodeStart(typeProvider, typeProvider.empty(), 0.0);
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0.0), zeroResult.resultOrThrow());
		
		Result<JsonElement> positiveResult = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(3.14), positiveResult.resultOrThrow());
		
		// Currently not supported by the JsonTypeProvider
		Result<JsonElement> infinityResult = codec.encodeStart(typeProvider, typeProvider.empty(), Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isError());
		
		Result<JsonElement> nanResult = codec.encodeStart(typeProvider, typeProvider.empty(), Double.NaN);
		assertTrue(nanResult.isError());
	}
	
	@Test
	void encodeIntegerKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, 1));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
	}
	
	@Test
	void encodeIntegerKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<String> negativeResult = codec.encodeKey(typeProvider, -42);
		assertTrue(negativeResult.isSuccess());
		assertEquals("-42", negativeResult.resultOrThrow());
		
		Result<String> zeroResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroResult.isSuccess());
		assertEquals("0", zeroResult.resultOrThrow());
		
		Result<String> positiveResult = codec.encodeKey(typeProvider, 42);
		assertTrue(positiveResult.isSuccess());
		assertEquals("42", positiveResult.resultOrThrow());
		
		Result<String> maxResult = codec.encodeKey(typeProvider, Integer.MAX_VALUE);
		assertTrue(maxResult.isSuccess());
		assertEquals(String.valueOf(Integer.MAX_VALUE), maxResult.resultOrThrow());
		
		Result<String> minResult = codec.encodeKey(typeProvider, Integer.MIN_VALUE);
		assertTrue(minResult.isSuccess());
		assertEquals(String.valueOf(Integer.MIN_VALUE), minResult.resultOrThrow());
	}
	
	@Test
	void encodeFloatingPointKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE;
		
		Result<String> negativeResult = codec.encodeKey(typeProvider, -3.14);
		assertTrue(negativeResult.isSuccess());
		assertEquals("-3.14", negativeResult.resultOrThrow());
		
		Result<String> zeroResult = codec.encodeKey(typeProvider, 0.0);
		assertTrue(zeroResult.isSuccess());
		assertEquals("0.0", zeroResult.resultOrThrow());
		
		Result<String> positiveResult = codec.encodeKey(typeProvider, 3.14);
		assertTrue(positiveResult.isSuccess());
		assertEquals("3.14", positiveResult.resultOrThrow());
		
		Result<String> scientificResult = codec.encodeKey(typeProvider, 1.23e-4);
		assertTrue(scientificResult.isSuccess());
		assertEquals("1.23E-4", scientificResult.resultOrThrow());
		
		// Currently not supported by the JsonTypeProvider
		Result<String> infinityResult = codec.encodeKey(typeProvider, Double.POSITIVE_INFINITY);
		assertTrue(infinityResult.isError());
		
		Result<String> nanResult = codec.encodeKey(typeProvider, Double.NaN);
		assertTrue(nanResult.isError());
	}
	
	@Test
	void decodeIntegerStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeIntegerStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<Integer> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as number"));
	}
	
	@Test
	void decodeIntegerStartWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-42));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.resultOrThrow());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.resultOrThrow());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(positiveResult.isSuccess());
		assertEquals(42, positiveResult.resultOrThrow());
		
		Result<Integer> maxResult = codec.decodeStart(typeProvider, new JsonPrimitive(Integer.MAX_VALUE));
		assertTrue(maxResult.isSuccess());
		assertEquals(Integer.MAX_VALUE, maxResult.resultOrThrow());
		
		Result<Integer> minResult = codec.decodeStart(typeProvider, new JsonPrimitive(Integer.MIN_VALUE));
		assertTrue(minResult.isSuccess());
		assertEquals(Integer.MIN_VALUE, minResult.resultOrThrow());
	}
	
	@Test
	void decodeIntegerStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
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
		RangeCodec<Double> codec = DOUBLE;
		
		Result<Double> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-3.14));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-3.14, negativeResult.resultOrThrow());
		
		Result<Double> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0.0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.resultOrThrow());
		
		Result<Double> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(3.14));
		assertTrue(positiveResult.isSuccess());
		assertEquals(3.14, positiveResult.resultOrThrow());
		
		Result<Double> integerResult = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(integerResult.isSuccess());
		assertEquals(42.0, integerResult.resultOrThrow());
	}
	
	@Test
	void decodeFloatingPointStartWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE;
		
		Result<Double> stringResult = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(stringResult.isError());
		
		Result<Double> boolResult = codec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isError());
	}
	
	@Test
	void decodeIntegerKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
	}
	
	@Test
	void decodeIntegerKeyWithValidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
		Result<Integer> negativeResult = codec.decodeKey(typeProvider, "-42");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-42, negativeResult.resultOrThrow());
		
		Result<Integer> zeroResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.resultOrThrow());
		
		Result<Integer> positiveResult = codec.decodeKey(typeProvider, "42");
		assertTrue(positiveResult.isSuccess());
		assertEquals(42, positiveResult.resultOrThrow());
		
		Result<Integer> maxResult = codec.decodeKey(typeProvider, String.valueOf(Integer.MAX_VALUE));
		assertTrue(maxResult.isSuccess());
		assertEquals(Integer.MAX_VALUE, maxResult.resultOrThrow());
		
		Result<Integer> minResult = codec.decodeKey(typeProvider, String.valueOf(Integer.MIN_VALUE));
		assertTrue(minResult.isSuccess());
		assertEquals(Integer.MIN_VALUE, minResult.resultOrThrow());
	}
	
	@Test
	void decodeIntegerKeyWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER;
		
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
		RangeCodec<Double> codec = DOUBLE;
		
		Result<Double> negativeResult = codec.decodeKey(typeProvider, "-3.14");
		assertTrue(negativeResult.isSuccess());
		assertEquals(-3.14, negativeResult.resultOrThrow());
		
		Result<Double> zeroResult = codec.decodeKey(typeProvider, "0.0");
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.resultOrThrow());
		
		Result<Double> positiveResult = codec.decodeKey(typeProvider, "3.14");
		assertTrue(positiveResult.isSuccess());
		assertEquals(3.14, positiveResult.resultOrThrow());
		
		Result<Double> scientificResult = codec.decodeKey(typeProvider, "1.23e-4");
		assertTrue(scientificResult.isSuccess());
		assertEquals(1.23e-4, scientificResult.resultOrThrow());
		
		Result<Double> integerResult = codec.decodeKey(typeProvider, "42");
		assertTrue(integerResult.isSuccess());
		assertEquals(42.0, integerResult.resultOrThrow());
	}
	
	@Test
	void decodeFloatingPointKeyWithInvalidValues() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE;
		
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
		RangeCodec<Integer> codec = INTEGER.positive();
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isError());
		assertTrue(zeroResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isError());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.resultOrThrow());
	}
	
	@Test
	void positiveRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.positive();
		
		Result<String> zeroEncodeResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroEncodeResult.isError());
		assertTrue(zeroEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> negativeEncodeResult = codec.encodeKey(typeProvider, -1);
		assertTrue(negativeEncodeResult.isError());
		assertTrue(negativeEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> positiveEncodeResult = codec.encodeKey(typeProvider, 1);
		assertTrue(positiveEncodeResult.isSuccess());
		assertEquals("1", positiveEncodeResult.resultOrThrow());
		
		Result<Integer> zeroDecodeResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroDecodeResult.isError());
		assertTrue(zeroDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> negativeDecodeResult = codec.decodeKey(typeProvider, "-1");
		assertTrue(negativeDecodeResult.isError());
		assertTrue(negativeDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> positiveDecodeResult = codec.decodeKey(typeProvider, "1");
		assertTrue(positiveDecodeResult.isSuccess());
		assertEquals(1, positiveDecodeResult.resultOrThrow());
	}
	
	@Test
	void positiveOrZeroRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.positiveOrZero();
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.resultOrThrow());
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.resultOrThrow());
	}
	
	@Test
	void positiveOrZeroRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.positiveOrZero();
		
		Result<String> negativeEncodeResult = codec.encodeKey(typeProvider, -1);
		assertTrue(negativeEncodeResult.isError());
		
		Result<String> zeroEncodeResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroEncodeResult.isSuccess());
		assertEquals("0", zeroEncodeResult.resultOrThrow());
		
		Result<String> positiveEncodeResult = codec.encodeKey(typeProvider, 1);
		assertTrue(positiveEncodeResult.isSuccess());
		assertEquals("1", positiveEncodeResult.resultOrThrow());
		
		Result<Integer> negativeDecodeResult = codec.decodeKey(typeProvider, "-1");
		assertTrue(negativeDecodeResult.isError());
		
		Result<Integer> zeroDecodeResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroDecodeResult.isSuccess());
		assertEquals(0, zeroDecodeResult.resultOrThrow());
		
		Result<Integer> positiveDecodeResult = codec.decodeKey(typeProvider, "1");
		assertTrue(positiveDecodeResult.isSuccess());
		assertEquals(1, positiveDecodeResult.resultOrThrow());
	}
	
	@Test
	void negativeRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.negative();
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isError());
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.resultOrThrow());
	}
	
	@Test
	void negativeRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.negative();
		
		Result<String> positiveEncodeResult = codec.encodeKey(typeProvider, 1);
		assertTrue(positiveEncodeResult.isError());
		
		Result<String> zeroEncodeResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroEncodeResult.isError());
		
		Result<String> negativeEncodeResult = codec.encodeKey(typeProvider, -1);
		assertTrue(negativeEncodeResult.isSuccess());
		assertEquals("-1", negativeEncodeResult.resultOrThrow());
		
		Result<Integer> positiveDecodeResult = codec.decodeKey(typeProvider, "1");
		assertTrue(positiveDecodeResult.isError());
		
		Result<Integer> zeroDecodeResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroDecodeResult.isError());
		
		Result<Integer> negativeDecodeResult = codec.decodeKey(typeProvider, "-1");
		assertTrue(negativeDecodeResult.isSuccess());
		assertEquals(-1, negativeDecodeResult.resultOrThrow());
	}
	
	@Test
	void negativeOrZeroRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.negativeOrZero();
		
		Result<Integer> positiveResult = codec.decodeStart(typeProvider, new JsonPrimitive(1));
		assertTrue(positiveResult.isError());
		
		Result<Integer> zeroResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.resultOrThrow());
		
		Result<Integer> negativeResult = codec.decodeStart(typeProvider, new JsonPrimitive(-1));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.resultOrThrow());
	}
	
	@Test
	void negativeOrZeroRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.negativeOrZero();
		
		Result<String> positiveEncodeResult = codec.encodeKey(typeProvider, 1);
		assertTrue(positiveEncodeResult.isError());
		
		Result<String> zeroEncodeResult = codec.encodeKey(typeProvider, 0);
		assertTrue(zeroEncodeResult.isSuccess());
		assertEquals("0", zeroEncodeResult.resultOrThrow());
		
		Result<String> negativeEncodeResult = codec.encodeKey(typeProvider, -1);
		assertTrue(negativeEncodeResult.isSuccess());
		assertEquals("-1", negativeEncodeResult.resultOrThrow());
		
		Result<Integer> positiveDecodeResult = codec.decodeKey(typeProvider, "1");
		assertTrue(positiveDecodeResult.isError());
		
		Result<Integer> zeroDecodeResult = codec.decodeKey(typeProvider, "0");
		assertTrue(zeroDecodeResult.isSuccess());
		assertEquals(0, zeroDecodeResult.resultOrThrow());
		
		Result<Integer> negativeDecodeResult = codec.decodeKey(typeProvider, "-1");
		assertTrue(negativeDecodeResult.isSuccess());
		assertEquals(-1, negativeDecodeResult.resultOrThrow());
	}
	
	@Test
	void atLeastRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.atLeast(5);
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(4));
		assertTrue(belowResult.isError());
		
		Result<Integer> exactResult = codec.decodeStart(typeProvider, new JsonPrimitive(5));
		assertTrue(exactResult.isSuccess());
		assertEquals(5, exactResult.resultOrThrow());
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(10));
		assertTrue(aboveResult.isSuccess());
		assertEquals(10, aboveResult.resultOrThrow());
	}
	
	@Test
	void atLeastRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.atLeast(5);
		
		Result<String> belowEncodeResult = codec.encodeKey(typeProvider, 4);
		assertTrue(belowEncodeResult.isError());
		assertTrue(belowEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> exactEncodeResult = codec.encodeKey(typeProvider, 5);
		assertTrue(exactEncodeResult.isSuccess());
		assertEquals("5", exactEncodeResult.resultOrThrow());
		
		Result<String> aboveEncodeResult = codec.encodeKey(typeProvider, 10);
		assertTrue(aboveEncodeResult.isSuccess());
		assertEquals("10", aboveEncodeResult.resultOrThrow());
		
		Result<Integer> belowDecodeResult = codec.decodeKey(typeProvider, "4");
		assertTrue(belowDecodeResult.isError());
		assertTrue(belowDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> exactDecodeResult = codec.decodeKey(typeProvider, "5");
		assertTrue(exactDecodeResult.isSuccess());
		assertEquals(5, exactDecodeResult.resultOrThrow());
		
		Result<Integer> aboveDecodeResult = codec.decodeKey(typeProvider, "10");
		assertTrue(aboveDecodeResult.isSuccess());
		assertEquals(10, aboveDecodeResult.resultOrThrow());
	}
	
	@Test
	void atMostRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.atMost(5);
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(6));
		assertTrue(aboveResult.isError());
		
		Result<Integer> exactResult = codec.decodeStart(typeProvider, new JsonPrimitive(5));
		assertTrue(exactResult.isSuccess());
		assertEquals(5, exactResult.resultOrThrow());
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(0));
		assertTrue(belowResult.isSuccess());
		assertEquals(0, belowResult.resultOrThrow());
	}
	
	@Test
	void atMostRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.atMost(5);
		
		Result<String> aboveEncodeResult = codec.encodeKey(typeProvider, 6);
		assertTrue(aboveEncodeResult.isError());
		assertTrue(aboveEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> exactEncodeResult = codec.encodeKey(typeProvider, 5);
		assertTrue(exactEncodeResult.isSuccess());
		assertEquals("5", exactEncodeResult.resultOrThrow());
		
		Result<String> belowEncodeResult = codec.encodeKey(typeProvider, 0);
		assertTrue(belowEncodeResult.isSuccess());
		assertEquals("0", belowEncodeResult.resultOrThrow());
		
		Result<Integer> aboveDecodeResult = codec.decodeKey(typeProvider, "6");
		assertTrue(aboveDecodeResult.isError());
		assertTrue(aboveDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> exactDecodeResult = codec.decodeKey(typeProvider, "5");
		assertTrue(exactDecodeResult.isSuccess());
		assertEquals(5, exactDecodeResult.resultOrThrow());
		
		Result<Integer> belowDecodeResult = codec.decodeKey(typeProvider, "0");
		assertTrue(belowDecodeResult.isSuccess());
		assertEquals(0, belowDecodeResult.resultOrThrow());
	}
	
	@Test
	void customRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.range(10, 20);
		
		Result<Integer> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(9));
		assertTrue(belowResult.isError());
		
		Result<Integer> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(21));
		assertTrue(aboveResult.isError());
		
		Result<Integer> lowerBoundResult = codec.decodeStart(typeProvider, new JsonPrimitive(10));
		assertTrue(lowerBoundResult.isSuccess());
		assertEquals(10, lowerBoundResult.resultOrThrow());
		
		Result<Integer> upperBoundResult = codec.decodeStart(typeProvider, new JsonPrimitive(20));
		assertTrue(upperBoundResult.isSuccess());
		assertEquals(20, upperBoundResult.resultOrThrow());
		
		Result<Integer> middleResult = codec.decodeStart(typeProvider, new JsonPrimitive(15));
		assertTrue(middleResult.isSuccess());
		assertEquals(15, middleResult.resultOrThrow());
	}
	
	@Test
	void customRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = INTEGER.range(10, 20);
		
		Result<String> belowEncodeResult = codec.encodeKey(typeProvider, 9);
		assertTrue(belowEncodeResult.isError());
		assertTrue(belowEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> aboveEncodeResult = codec.encodeKey(typeProvider, 21);
		assertTrue(aboveEncodeResult.isError());
		assertTrue(aboveEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> lowerBoundEncodeResult = codec.encodeKey(typeProvider, 10);
		assertTrue(lowerBoundEncodeResult.isSuccess());
		assertEquals("10", lowerBoundEncodeResult.resultOrThrow());
		
		Result<String> upperBoundEncodeResult = codec.encodeKey(typeProvider, 20);
		assertTrue(upperBoundEncodeResult.isSuccess());
		assertEquals("20", upperBoundEncodeResult.resultOrThrow());
		
		Result<String> middleEncodeResult = codec.encodeKey(typeProvider, 15);
		assertTrue(middleEncodeResult.isSuccess());
		assertEquals("15", middleEncodeResult.resultOrThrow());
		
		Result<Integer> belowDecodeResult = codec.decodeKey(typeProvider, "9");
		assertTrue(belowDecodeResult.isError());
		assertTrue(belowDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> aboveDecodeResult = codec.decodeKey(typeProvider, "21");
		assertTrue(aboveDecodeResult.isError());
		assertTrue(aboveDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Integer> lowerBoundDecodeResult = codec.decodeKey(typeProvider, "10");
		assertTrue(lowerBoundDecodeResult.isSuccess());
		assertEquals(10, lowerBoundDecodeResult.resultOrThrow());
		
		Result<Integer> upperBoundDecodeResult = codec.decodeKey(typeProvider, "20");
		assertTrue(upperBoundDecodeResult.isSuccess());
		assertEquals(20, upperBoundDecodeResult.resultOrThrow());
		
		Result<Integer> middleDecodeResult = codec.decodeKey(typeProvider, "15");
		assertTrue(middleDecodeResult.isSuccess());
		assertEquals(15, middleDecodeResult.resultOrThrow());
	}
	
	@Test
	void doubleRangeValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE.range(1.0, 10.0);
		
		Result<Double> belowResult = codec.decodeStart(typeProvider, new JsonPrimitive(0.9));
		assertTrue(belowResult.isError());
		
		Result<Double> aboveResult = codec.decodeStart(typeProvider, new JsonPrimitive(10.1));
		assertTrue(aboveResult.isError());
		
		Result<Double> validResult = codec.decodeStart(typeProvider, new JsonPrimitive(5.5));
		assertTrue(validResult.isSuccess());
		assertEquals(5.5, validResult.resultOrThrow());
	}
	
	@Test
	void doubleRangeKeyValidation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = DOUBLE.range(1.0, 10.0);
		
		Result<String> belowEncodeResult = codec.encodeKey(typeProvider, 0.9);
		assertTrue(belowEncodeResult.isError());
		assertTrue(belowEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> aboveEncodeResult = codec.encodeKey(typeProvider, 10.1);
		assertTrue(aboveEncodeResult.isError());
		assertTrue(aboveEncodeResult.errorOrThrow().contains("out of range"));
		
		Result<String> validEncodeResult = codec.encodeKey(typeProvider, 5.5);
		assertTrue(validEncodeResult.isSuccess());
		assertEquals("5.5", validEncodeResult.resultOrThrow());
		
		Result<Double> belowDecodeResult = codec.decodeKey(typeProvider, "0.9");
		assertTrue(belowDecodeResult.isError());
		assertTrue(belowDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Double> aboveDecodeResult = codec.decodeKey(typeProvider, "10.1");
		assertTrue(aboveDecodeResult.isError());
		assertTrue(aboveDecodeResult.errorOrThrow().contains("out of range"));
		
		Result<Double> validDecodeResult = codec.decodeKey(typeProvider, "5.5");
		assertTrue(validDecodeResult.isSuccess());
		assertEquals(5.5, validDecodeResult.resultOrThrow());
	}
	
	@Test
	void rangeMethodNullChecks() {
		assertThrows(NullPointerException.class, () -> INTEGER.atLeast(null));
		assertThrows(NullPointerException.class, () -> INTEGER.atMost(null));
		assertThrows(NullPointerException.class, () -> INTEGER.range(null, 10));
		assertThrows(NullPointerException.class, () -> INTEGER.range(0, null));
		
		assertThrows(NullPointerException.class, () -> DOUBLE.atLeast(null));
		assertThrows(NullPointerException.class, () -> DOUBLE.atMost(null));
		assertThrows(NullPointerException.class, () -> DOUBLE.range(null, 10.0));
		assertThrows(NullPointerException.class, () -> DOUBLE.range(0.0, null));
	}
	
	@Test
	void rangeMethodsReturnNewInstances() {
		RangeCodec<Integer> original = INTEGER;
		
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
		RangeCodec<Integer> codec1 = INTEGER.range(0, 100);
		RangeCodec<Integer> codec2 = INTEGER.range(0, 100);
		
		assertEquals(codec1.hashCode(), codec2.hashCode());
	}
	
	@Test
	void toStringRepresentation() {
		RangeCodec<Integer> codec = INTEGER.range(0, 100);
		String result = codec.toString();
		
		assertTrue(result.contains("Range"));
		assertTrue(result.contains("Codec"));
		assertTrue(result.contains("0..100"));
	}
}
