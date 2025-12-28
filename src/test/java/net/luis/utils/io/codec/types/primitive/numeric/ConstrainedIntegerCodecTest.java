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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerCodec} focusing on integer constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedIntegerCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<Integer> codec = INTEGER.positive();
		int value = 10;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<Integer> codec = INTEGER.positive();
		int value = -5;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<Integer> codec = INTEGER.negative();
		int value = -10;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<Integer> codec = INTEGER.negative();
		int value = 5;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<Integer> codec = INTEGER.nonNegative();
		int value = 0;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<Integer> codec = INTEGER.nonNegative();
		int value = -1;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithEvenConstraintSuccess() {
		Codec<Integer> codec = INTEGER.even();
		int value = 10;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEvenConstraintFailure() {
		Codec<Integer> codec = INTEGER.even();
		int value = 7;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void encodeWithOddConstraintSuccess() {
		Codec<Integer> codec = INTEGER.odd();
		int value = 7;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOddConstraintFailure() {
		Codec<Integer> codec = INTEGER.odd();
		int value = 10;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated odd constraint"));
	}
	
	@Test
	void encodeWithDivisibleByConstraintSuccess() {
		Codec<Integer> codec = INTEGER.divisibleBy(5);
		int value = 25;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithDivisibleByConstraintFailure() {
		Codec<Integer> codec = INTEGER.divisibleBy(5);
		int value = 23;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void encodeWithPowerOfTwoConstraintSuccess() {
		Codec<Integer> codec = INTEGER.powerOfTwo();
		int value = 64;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPowerOfTwoConstraintFailure() {
		Codec<Integer> codec = INTEGER.powerOfTwo();
		int value = 63;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated power-of-2 constraint"));
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Codec<Integer> codec = INTEGER.betweenOrEqual(0, 100);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), 50);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Codec<Integer> codec = INTEGER.betweenOrEqual(0, 100);
		int value = 127;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<Integer> codec = INTEGER.positive();
		JsonPrimitive json = new JsonPrimitive(10);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10, result.resultOrThrow());
	}
	
	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<Integer> codec = INTEGER.positive();
		JsonPrimitive json = new JsonPrimitive(-5);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void decodeWithEvenConstraintSuccess() {
		Codec<Integer> codec = INTEGER.even();
		JsonPrimitive json = new JsonPrimitive(10);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10, result.resultOrThrow());
	}
	
	@Test
	void decodeWithEvenConstraintFailure() {
		Codec<Integer> codec = INTEGER.even();
		JsonPrimitive json = new JsonPrimitive(7);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void decodeWithDivisibleByConstraintSuccess() {
		Codec<Integer> codec = INTEGER.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive(25);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(25, result.resultOrThrow());
	}
	
	@Test
	void decodeWithDivisibleByConstraintFailure() {
		Codec<Integer> codec = INTEGER.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive(23);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Integer> codec = INTEGER.positive().even().divisibleBy(10);
		int value = 50;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<Integer> codec = INTEGER.positive().even().divisibleBy(10);
		int value = 12;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Integer> codec = INTEGER.nonNegative().even();
		JsonPrimitive json = new JsonPrimitive(10);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10, result.resultOrThrow());
	}
	
	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<Integer> codec = INTEGER.positive().even();
		JsonPrimitive json = new JsonPrimitive(7);
		
		Result<Integer> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Integer> codec = INTEGER;
		String str = codec.toString();
		assertEquals("IntegerCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Integer> codec = INTEGER.positive().even();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedIntegerCodec["));
		assertTrue(str.contains("constraints="));
	}
}
