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

import static net.luis.utils.io.codec.Codecs.SHORT;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ShortCodec} focusing on integer constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedShortCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	@Test
	void encodeWithPositiveConstraintSuccess() {
		Codec<Short> codec = SHORT.positive();
		short value = 10;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPositiveConstraintFailure() {
		Codec<Short> codec = SHORT.positive();
		short value = -5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNegativeConstraintSuccess() {
		Codec<Short> codec = SHORT.negative();
		short value = -10;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNegativeConstraintFailure() {
		Codec<Short> codec = SHORT.negative();
		short value = 5;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithNonNegativeConstraintSuccess() {
		Codec<Short> codec = SHORT.nonNegative();
		short value = 0;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithNonNegativeConstraintFailure() {
		Codec<Short> codec = SHORT.nonNegative();
		short value = -1;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeWithEvenConstraintSuccess() {
		Codec<Short> codec = SHORT.even();
		short value = 10;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithEvenConstraintFailure() {
		Codec<Short> codec = SHORT.even();
		short value = 7;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void encodeWithOddConstraintSuccess() {
		Codec<Short> codec = SHORT.odd();
		short value = 7;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithOddConstraintFailure() {
		Codec<Short> codec = SHORT.odd();
		short value = 10;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated odd constraint"));
	}

	@Test
	void encodeWithDivisibleByConstraintSuccess() {
		Codec<Short> codec = SHORT.divisibleBy(5);
		short value = 25;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithDivisibleByConstraintFailure() {
		Codec<Short> codec = SHORT.divisibleBy(5);
		short value = 23;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void encodeWithPowerOfTwoConstraintSuccess() {
		Codec<Short> codec = SHORT.powerOfTwo();
		short value = 64;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithPowerOfTwoConstraintFailure() {
		Codec<Short> codec = SHORT.powerOfTwo();
		short value = 63;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated power-of-2 constraint"));
	}

	@Test
	void encodeWithBetweenConstraintSuccess() {
		Codec<Short> codec = SHORT.between((short) 0, (short) 100);
		short value = 50;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenConstraintFailure() {
		Codec<Short> codec = SHORT.between((short) 0, (short) 100);
		short value = 127;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void decodeWithPositiveConstraintSuccess() {
		Codec<Short> codec = SHORT.positive();
		JsonPrimitive json = new JsonPrimitive(10);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals((short) 10, result.resultOrThrow());
	}

	@Test
	void decodeWithPositiveConstraintFailure() {
		Codec<Short> codec = SHORT.positive();
		JsonPrimitive json = new JsonPrimitive(-5);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithEvenConstraintSuccess() {
		Codec<Short> codec = SHORT.even();
		JsonPrimitive json = new JsonPrimitive(10);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals((short) 10, result.resultOrThrow());
	}

	@Test
	void decodeWithEvenConstraintFailure() {
		Codec<Short> codec = SHORT.even();
		JsonPrimitive json = new JsonPrimitive(7);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void decodeWithDivisibleByConstraintSuccess() {
		Codec<Short> codec = SHORT.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive(25);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals((short) 25, result.resultOrThrow());
	}

	@Test
	void decodeWithDivisibleByConstraintFailure() {
		Codec<Short> codec = SHORT.divisibleBy(5);
		JsonPrimitive json = new JsonPrimitive(23);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void encodeWithChainedConstraintsSuccess() {
		Codec<Short> codec = SHORT.positive().even().divisibleBy(10);
		short value = 50;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailure() {
		Codec<Short> codec = SHORT.positive().even().divisibleBy(10);
		short value = 12;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated divisibility constraint"));
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Codec<Short> codec = SHORT.nonNegative().even();
		JsonPrimitive json = new JsonPrimitive(10);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals((short) 10, result.resultOrThrow());
	}

	@Test
	void decodeWithChainedConstraintsFailure() {
		Codec<Short> codec = SHORT.positive().even();
		JsonPrimitive json = new JsonPrimitive(7);

		Result<Short> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated even constraint"));
	}

	@Test
	void toStringWithoutConstraints() {
		Codec<Short> codec = SHORT;
		String str = codec.toString();
		assertEquals("ShortCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<Short> codec = SHORT.positive().even();
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedShortCodec["));
		assertTrue(str.contains("constraints="));
	}
}
