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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigIntegerCodec} focusing on integer constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedBigIntegerCodecTest {
	
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;
	
	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.positive();
		BigInteger value = BigInteger.valueOf(10);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.positive();
		BigInteger value = BigInteger.valueOf(-5);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.negative();
		BigInteger value = BigInteger.valueOf(-10);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.negative();
		BigInteger value = BigInteger.valueOf(5);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.nonNegative();
		BigInteger value = BigInteger.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.nonNegative();
		BigInteger value = BigInteger.valueOf(-1);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeWithEvenConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.even();
		BigInteger value = BigInteger.valueOf(10);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithEvenConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.even();
		BigInteger value = BigInteger.valueOf(7);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void encodeWithOddConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.odd();
		BigInteger value = BigInteger.valueOf(7);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithOddConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.odd();
		BigInteger value = BigInteger.valueOf(10);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated odd constraint"));
	}
	
	@Test
	void encodeWithDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.divisibleBy(5);
		BigInteger value = BigInteger.valueOf(25);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithDivisibleByConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.divisibleBy(5);
		BigInteger value = BigInteger.valueOf(23);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void encodeWithPowerOfTwoConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.powerOfTwo();
		BigInteger value = BigInteger.valueOf(64);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithPowerOfTwoConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.powerOfTwo();
		BigInteger value = BigInteger.valueOf(63);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated power-of-2 constraint"));
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.betweenOrEqual(BigInteger.ZERO, BigInteger.valueOf(100));
		BigInteger value = BigInteger.valueOf(50);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.betweenOrEqual(BigInteger.ZERO, BigInteger.valueOf(100));
		BigInteger value = BigInteger.valueOf(127);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.positive();
		JsonPrimitive json = new JsonPrimitive("10");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(10), result.resultOrThrow());
	}
	
	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.positive();
		JsonPrimitive json = new JsonPrimitive("-5");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}
	
	@Test
	void decodeWithEvenConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.even();
		JsonPrimitive json = new JsonPrimitive("10");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(10), result.resultOrThrow());
	}
	
	@Test
	void decodeWithEvenConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.even();
		JsonPrimitive json = new JsonPrimitive("7");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void decodeWithDivisibleByConstraintSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive("25");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(25), result.resultOrThrow());
	}
	
	@Test
	void decodeWithDivisibleByConstraintFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive("23");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.positive().even().divisibleBy(10);
		BigInteger value = BigInteger.valueOf(50);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.positive().even().divisibleBy(10);
		BigInteger value = BigInteger.valueOf(12);
		
		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}
	
	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<BigInteger> codec = BIG_INTEGER.nonNegative().even();
		JsonPrimitive json = new JsonPrimitive("10");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.valueOf(10), result.resultOrThrow());
	}
	
	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<BigInteger> codec = BIG_INTEGER.positive().even();
		JsonPrimitive json = new JsonPrimitive("7");
		
		Result<BigInteger> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<BigInteger> codec = BIG_INTEGER;
		String str = codec.toString();
		assertEquals("BigIntegerCodec", str);
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<BigInteger> codec = BIG_INTEGER.positive().even();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedBigIntegerCodec["));
		assertTrue(str.contains("constraints="));
	}
}
