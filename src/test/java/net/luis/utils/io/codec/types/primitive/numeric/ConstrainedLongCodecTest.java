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

import static net.luis.utils.io.codec.Codecs.LONG;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LongCodec} focusing on integer constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedLongCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<Long> codec = LONG.positive();
		long value = 10L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<Long> codec = LONG.positive();
		long value = -5L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<Long> codec = LONG.negative();
		long value = -10L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<Long> codec = LONG.negative();
		long value = 5L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<Long> codec = LONG.nonNegative();
		long value = 0L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<Long> codec = LONG.nonNegative();
		long value = -1L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeWithEvenConstraintSuccess() {
		Codec<Long> codec = LONG.even();
		long value = 10L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEvenConstraintFailure() {
		Codec<Long> codec = LONG.even();
		long value = 7L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void encodeWithOddConstraintSuccess() {
		Codec<Long> codec = LONG.odd();
		long value = 7L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOddConstraintFailure() {
		Codec<Long> codec = LONG.odd();
		long value = 10L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated odd constraint"));
	}

	@Test
	void encodeWithDivisibleByConstraintSuccess() {
		Codec<Long> codec = LONG.divisibleBy(5L);
		long value = 25L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDivisibleByConstraintFailure() {
		Codec<Long> codec = LONG.divisibleBy(5L);
		long value = 23L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void encodeWithPowerOfTwoConstraintSuccess() {
		Codec<Long> codec = LONG.powerOfTwo();
		long value = 64L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPowerOfTwoConstraintFailure() {
		Codec<Long> codec = LONG.powerOfTwo();
		long value = 63L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated power-of-2 constraint"));
	}

	@Test
	void encodeWithBetweenConstraintSuccess() {
		Codec<Long> codec = LONG.between(0L, 100L);
		long value = 50L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenConstraintFailure() {
		Codec<Long> codec = LONG.between(0L, 100L);
		long value = 127L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<Long> codec = LONG.positive();
		JsonPrimitive json = new JsonPrimitive(10L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10L, result.resultOrThrow());
	}

	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<Long> codec = LONG.positive();
		JsonPrimitive json = new JsonPrimitive(-5L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithEvenConstraintSuccess() {
		Codec<Long> codec = LONG.even();
		JsonPrimitive json = new JsonPrimitive(10L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10L, result.resultOrThrow());
	}

	@Test
	void decodeWithEvenConstraintFailure() {
		Codec<Long> codec = LONG.even();
		JsonPrimitive json = new JsonPrimitive(7L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void decodeWithDivisibleByConstraintSuccess() {
		Codec<Long> codec = LONG.divisibleBy(5L);
		JsonPrimitive json = new JsonPrimitive(25L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(25L, result.resultOrThrow());
	}

	@Test
	void decodeWithDivisibleByConstraintFailure() {
		Codec<Long> codec = LONG.divisibleBy(5L);
		JsonPrimitive json = new JsonPrimitive(23L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Long> codec = LONG.positive().even().divisibleBy(10L);
		long value = 50L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<Long> codec = LONG.positive().even().divisibleBy(10L);
		long value = 12L;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Long> codec = LONG.nonNegative().even();
		JsonPrimitive json = new JsonPrimitive(10L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(10L, result.resultOrThrow());
	}

	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<Long> codec = LONG.positive().even();
		JsonPrimitive json = new JsonPrimitive(7L);

		Result<Long> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Long> codec = LONG;
		String str = codec.toString();
		assertEquals("LongCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<Long> codec = LONG.positive().even();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedLongCodec["));
		assertTrue(str.contains("constraints="));
	}
}
