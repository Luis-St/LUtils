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
	
	//region Encode
	@Test
	void encodeIntegerStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		
		Result<JsonElement> negativeResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), -1));
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-1), assertInstanceOf(JsonPrimitive.class, negativeResult.orThrow()));
		
		Result<JsonElement> zeroResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), 0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0), assertInstanceOf(JsonPrimitive.class, zeroResult.orThrow()));
		
		Result<JsonElement> positiveResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), 1));
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(1), assertInstanceOf(JsonPrimitive.class, positiveResult.orThrow()));
	}
	
	@Test
	void encodeFloatingPointStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 1.0));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1.0));
		
		assertTrue(assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), null)).isError());
		
		Result<JsonElement> negativeResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), -1.0));
		assertTrue(negativeResult.isSuccess());
		assertEquals(new JsonPrimitive(-1.0), assertInstanceOf(JsonPrimitive.class, negativeResult.orThrow()));
		
		Result<JsonElement> zeroResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), 0.0));
		assertTrue(zeroResult.isSuccess());
		assertEquals(new JsonPrimitive(0.0), assertInstanceOf(JsonPrimitive.class, zeroResult.orThrow()));
		
		Result<JsonElement> positiveResult = assertDoesNotThrow(() -> codec.encodeStart(typeProvider, typeProvider.empty(), 1.0));
		assertTrue(positiveResult.isSuccess());
		assertEquals(new JsonPrimitive(1.0), assertInstanceOf(JsonPrimitive.class, positiveResult.orThrow()));
	}
	//endregion
	
	//region Encode key
	@Test
	void encodeIntegerKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, 1));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
		
		Result<String> negativeResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, -1));
		assertTrue(negativeResult.isSuccess());
		assertEquals("-1", negativeResult.orThrow());
		
		Result<String> zeroResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, 0));
		assertTrue(zeroResult.isSuccess());
		assertEquals("0", zeroResult.orThrow());
		
		Result<String> positiveResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, 1));
		assertTrue(positiveResult.isSuccess());
		assertEquals("1", positiveResult.orThrow());
	}
	
	@Test
	void encodeFloatingPointKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null, 1.0));
		assertThrows(NullPointerException.class, () -> codec.encodeKey(typeProvider, null));
		
		Result<String> negativeResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, -1.0));
		assertTrue(negativeResult.isSuccess());
		assertEquals("-1.0", negativeResult.orThrow());
		
		Result<String> zeroResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, 0.0));
		assertTrue(zeroResult.isSuccess());
		assertEquals("0.0", zeroResult.orThrow());
		
		Result<String> positiveResult = assertDoesNotThrow(() -> codec.encodeKey(typeProvider, 1.0));
		assertTrue(positiveResult.isSuccess());
		assertEquals("1.0", positiveResult.orThrow());
	}
	//endregion
	
	//region Decode
	@Test
	void decodeIntegerStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
		
		Result<Integer> nullResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null));
		assertTrue(nullResult.isError());
		
		Result<Integer> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive("test"))).isError());
	}
	
	@Test
	void decodeFloatingPointStart() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
		
		Result<Double> nullResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, null));
		assertTrue(nullResult.isError());
		
		Result<Double> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1.0)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1.0, negativeResult.orThrow());
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0.0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1.0)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1.0, positiveResult.orThrow());
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive("test"))).isError());
	}
	//endregion
	
	//region Decode key
	@Test
	void decodeIntegerKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER;
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "1"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
		
		Result<Integer> negativeResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "-1"));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "0"));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "1"));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "test")).isError());
	}
	@Test
	void decodeFloatingPointKey() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE;
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null, "1.0"));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(typeProvider, null));
		
		Result<Double> negativeResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "-1.0"));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1.0, negativeResult.orThrow());
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "0.0"));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> positiveResult = assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "1.0"));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1.0, positiveResult.orThrow());
		
		assertTrue(assertDoesNotThrow(() -> codec.decodeKey(typeProvider, "test")).isError());
	}
	//endregion
	
	//region Decode positive
	@Test
	void decodeIntegerPositive() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.positive();
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0)));
		assertTrue(zeroResult.isError());
		
		Result<Integer> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointPositive() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.positive();
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0.0)));
		assertTrue(zeroResult.isError());
		
		Result<Double> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1.0)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1.0, positiveResult.orThrow());
	}
	//endregion
	
	//region Decode positive or zero
	@Test
	void decodeIntegerPositiveOrZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.positiveOrZero();
		
		Result<Integer> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1)));
		assertTrue(negativeResult.isError());
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1, positiveResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointPositiveOrZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.positiveOrZero();
		
		Result<Double> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1.0)));
		assertTrue(negativeResult.isError());
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0.0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1.0)));
		assertTrue(positiveResult.isSuccess());
		assertEquals(1.0, positiveResult.orThrow());
	}
	//endregion
	
	//region Decode negative
	@Test
	void decodeIntegerNegative() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.negative();
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0)));
		assertTrue(zeroResult.isError());
		
		Result<Integer> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointNegative() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.negative();
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0.0)));
		assertTrue(zeroResult.isError());
		
		Result<Double> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1.0)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1.0, negativeResult.orThrow());
	}
	//endregion
	
	//region Decode negative or zero
	@Test
	void decodeIntegerNegativeOrZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.negativeOrZero();
		
		Result<Integer> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1)));
		assertTrue(positiveResult.isError());
		
		Result<Integer> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0, zeroResult.orThrow());
		
		Result<Integer> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1, negativeResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointNegativeOrZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.negativeOrZero();
		
		Result<Double> positiveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(1.0)));
		assertTrue(positiveResult.isError());
		
		Result<Double> zeroResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(0.0)));
		assertTrue(zeroResult.isSuccess());
		assertEquals(0.0, zeroResult.orThrow());
		
		Result<Double> negativeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(-1.0)));
		assertTrue(negativeResult.isSuccess());
		assertEquals(-1.0, negativeResult.orThrow());
	}
	//endregion
	
	//region Decode at least
	@Test
	void decodeIntegerAtLeast() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.atLeast(5);
		
		Result<Integer> outOfRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(4)));
		assertTrue(outOfRangeResult.isError());
		
		Result<Integer> inRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(5)));
		assertTrue(inRangeResult.isSuccess());
		assertEquals(5, inRangeResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointAtLeast() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.atLeast(5.0);
		
		Result<Double> outOfRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(4.0)));
		assertTrue(outOfRangeResult.isError());
		
		Result<Double> inRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(5.0)));
		assertTrue(inRangeResult.isSuccess());
		assertEquals(5.0, inRangeResult.orThrow());
	}
	//endregion
	
	//region Decode at most
	@Test
	void decodeIntegerAtMost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.atMost(5);
		
		Result<Integer> outOfRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(6)));
		assertTrue(outOfRangeResult.isError());
		
		Result<Integer> inRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(5)));
		assertTrue(inRangeResult.isSuccess());
		assertEquals(5, inRangeResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointAtMost() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.atMost(5.0);
		
		Result<Double> outOfRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(6.0)));
		assertTrue(outOfRangeResult.isError());
		
		Result<Double> inRangeResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(5.0)));
		assertTrue(inRangeResult.isSuccess());
		assertEquals(5.0, inRangeResult.orThrow());
	}
	//endregion
	
	//region Decode range
	@Test
	void decodeIntegerRange() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Integer> codec = Codec.INTEGER.range(10, 20);
		
		Result<Integer> outOfRangeBelowResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(9)));
		assertTrue(outOfRangeBelowResult.isError());
		
		Result<Integer> outOfRangeAboveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(21)));
		assertTrue(outOfRangeAboveResult.isError());
		
		Result<Integer> inRangeLowerResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(10)));
		assertTrue(inRangeLowerResult.isSuccess());
		assertEquals(10, inRangeLowerResult.orThrow());
		
		Result<Integer> inRangeUpperResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(20)));
		assertTrue(inRangeUpperResult.isSuccess());
		assertEquals(20, inRangeUpperResult.orThrow());
	}
	
	@Test
	void decodeFloatingPointRange() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		RangeCodec<Double> codec = Codec.DOUBLE.range(10.0, 20.0);
		
		Result<Double> outOfRangeBelowResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(9.0)));
		assertTrue(outOfRangeBelowResult.isError());
		
		Result<Double> outOfRangeAboveResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(21.0)));
		assertTrue(outOfRangeAboveResult.isError());
		
		Result<Double> inRangeLowerResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(10.0)));
		assertTrue(inRangeLowerResult.isSuccess());
		assertEquals(10.0, inRangeLowerResult.orThrow());
		
		Result<Double> inRangeUpperResult = assertDoesNotThrow(() -> codec.decodeStart(typeProvider, new JsonPrimitive(20.0)));
		assertTrue(inRangeUpperResult.isSuccess());
		assertEquals(20.0, inRangeUpperResult.orThrow());
	}
	//endregion
	
	@Test
	void positive() {
		assertNotNull(Codec.INTEGER.positive());
		assertNotNull(Codec.DOUBLE.positive());
	}
	
	@Test
	void positiveOrZero() {
		assertNotNull(Codec.INTEGER.positiveOrZero());
		assertNotNull(Codec.DOUBLE.positiveOrZero());
	}
	
	@Test
	void negative() {
		assertNotNull(Codec.INTEGER.negative());
		assertNotNull(Codec.DOUBLE.negative());
	}
	
	@Test
	void negativeOrZero() {
		assertNotNull(Codec.INTEGER.negativeOrZero());
		assertNotNull(Codec.DOUBLE.negativeOrZero());
	}
	
	@Test
	void atLeast() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.atLeast(null));
		assertNotNull(Codec.INTEGER.atLeast(0));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.atLeast(null));
		assertNotNull(Codec.DOUBLE.atLeast(0.0));
	}
	
	@Test
	void atMost() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.atMost(null));
		assertNotNull(Codec.INTEGER.atMost(0));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.atMost(null));
		assertNotNull(Codec.DOUBLE.atMost(0.0));
	}
	
	@Test
	void range() {
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.range(null, 10));
		assertThrows(NullPointerException.class, () -> Codec.INTEGER.range(0, null));
		assertNotNull(Codec.INTEGER.range(0, 10));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.range(null, 10.0));
		assertThrows(NullPointerException.class, () -> Codec.DOUBLE.range(0.0, null));
		assertNotNull(Codec.DOUBLE.range(0.0, 10.0));
	}
}
