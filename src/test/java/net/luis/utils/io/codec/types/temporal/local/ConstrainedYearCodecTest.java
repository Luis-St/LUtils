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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearCodec} focusing on year constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedYearCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - After Constraint

	@Test
	void encodeWithAfterConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		Year value = Year.of(2024);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		Year value = Year.of(2019);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAfterConstraintBoundary() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		Year value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - AfterOrEqual Constraint

	@Test
	void encodeWithAfterOrEqualConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.afterOrEqual(reference);
		Year value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterOrEqualConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.afterOrEqual(reference);
		Year value = Year.of(2019);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Before Constraint

	@Test
	void encodeWithBeforeConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.before(reference);
		Year value = Year.of(2019);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.before(reference);
		Year value = Year.of(2024);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBeforeConstraintBoundary() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.before(reference);
		Year value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - BeforeOrEqual Constraint

	@Test
	void encodeWithBeforeOrEqualConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.beforeOrEqual(reference);
		Year value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeOrEqualConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.beforeOrEqual(reference);
		Year value = Year.of(2024);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Between Constraint

	@Test
	void encodeWithBetweenConstraintSuccess() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.between(min, max);
		Year value = Year.of(2020);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenConstraintFailure() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.between(min, max);
		Year value = Year.of(2035);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBetweenConstraintMinBoundary() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.between(min, max);
		Year value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBetweenConstraintMaxBoundary() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.between(min, max);
		Year value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - BetweenOrEqual Constraint

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		Year value = Year.of(2020);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMinBoundary() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		Year value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMaxBoundary() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		Year value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		Year value = Year.of(1999);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - After Constraint

	@Test
	void decodeWithAfterConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		JsonPrimitive json = new JsonPrimitive(2024);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2024), result.resultOrThrow());
	}

	@Test
	void decodeWithAfterConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		JsonPrimitive json = new JsonPrimitive(2019);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - AfterOrEqual Constraint

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive(2020);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive(2019);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Before Constraint

	@Test
	void decodeWithBeforeConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.before(reference);
		JsonPrimitive json = new JsonPrimitive(2019);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2019), result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.before(reference);
		JsonPrimitive json = new JsonPrimitive(2024);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - BeforeOrEqual Constraint

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive(2020);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive(2024);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - BetweenOrEqual Constraint

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive(2020);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2020), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		Year min = Year.of(2000);
		Year max = Year.of(2030);
		Codec<Year> codec = YEAR.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive(2035);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests

	@Test
	void encodeWithChainedConstraintsSuccess() {
		Year min = Year.of(2000);
		Year max = Year.of(2050);
		Codec<Year> codec = YEAR.afterOrEqual(min).beforeOrEqual(max);
		Year value = Year.of(2024);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailure() {
		Year min = Year.of(2000);
		Year max = Year.of(2050);
		Codec<Year> codec = YEAR.afterOrEqual(min).beforeOrEqual(max);
		Year value = Year.of(2055);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Year min = Year.of(2000);
		Year max = Year.of(2050);
		Codec<Year> codec = YEAR.afterOrEqual(min).beforeOrEqual(max);
		JsonPrimitive json = new JsonPrimitive(2024);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Year.of(2024), result.resultOrThrow());
	}

	@Test
	void decodeWithChainedConstraintsFailure() {
		Year min = Year.of(2000);
		Year max = Year.of(2050);
		Codec<Year> codec = YEAR.afterOrEqual(min).beforeOrEqual(max);
		JsonPrimitive json = new JsonPrimitive(1995);

		Result<Year> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<Year> codec = YEAR;
		String str = codec.toString();
		assertEquals("YearCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Year reference = Year.of(2020);
		Codec<Year> codec = YEAR.after(reference);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedYearCodec["));
		assertTrue(str.contains("constraints="));
	}
}
