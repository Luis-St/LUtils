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

package net.luis.utils.io.codec.types.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InstantCodec} focusing on instant constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedInstantCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - After Constraint

	@Test
	void encodeWithAfterConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		Instant value = Instant.parse("2024-06-15T13:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		Instant value = Instant.parse("2024-06-15T11:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAfterConstraintBoundary() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		Instant value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - AfterOrEqual Constraint

	@Test
	void encodeWithAfterOrEqualConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(reference);
		Instant value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterOrEqualConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(reference);
		Instant value = Instant.parse("2024-06-15T11:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Before Constraint

	@Test
	void encodeWithBeforeConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.before(reference);
		Instant value = Instant.parse("2024-06-15T11:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.before(reference);
		Instant value = Instant.parse("2024-06-15T13:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBeforeConstraintBoundary() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.before(reference);
		Instant value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - BeforeOrEqual Constraint

	@Test
	void encodeWithBeforeOrEqualConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.beforeOrEqual(reference);
		Instant value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeOrEqualConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.beforeOrEqual(reference);
		Instant value = Instant.parse("2024-06-15T13:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Between Constraint

	@Test
	void encodeWithBetweenConstraintSuccess() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.between(min, max);
		Instant value = Instant.parse("2024-06-15T12:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenConstraintFailure() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.between(min, max);
		Instant value = Instant.parse("2024-06-15T15:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBetweenConstraintMinBoundary() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.between(min, max);
		Instant value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithBetweenConstraintMaxBoundary() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.between(min, max);
		Instant value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - BetweenOrEqual Constraint

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		Instant value = Instant.parse("2024-06-15T12:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMinBoundary() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		Instant value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMaxBoundary() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		Instant value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		Instant value = Instant.parse("2024-06-15T09:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - After Constraint

	@Test
	void decodeWithAfterConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T13:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2024-06-15T13:00:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeWithAfterConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T11:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - AfterOrEqual Constraint

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T11:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Before Constraint

	@Test
	void decodeWithBeforeConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.before(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T11:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2024-06-15T11:00:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.before(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T13:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - BeforeOrEqual Constraint

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T13:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - BetweenOrEqual Constraint

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T12:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2024-06-15T12:00:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T14:00:00Z");
		Codec<Instant> codec = INSTANT.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T15:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests

	@Test
	void encodeWithChainedConstraintsSuccess() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T20:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(min).beforeOrEqual(max);
		Instant value = Instant.parse("2024-06-15T15:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedConstraintsFailure() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T20:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(min).beforeOrEqual(max);
		Instant value = Instant.parse("2024-06-15T21:00:00Z");

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithChainedConstraintsSuccess() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T20:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(min).beforeOrEqual(max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T15:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2024-06-15T15:00:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeWithChainedConstraintsFailure() {
		Instant min = Instant.parse("2024-06-15T10:00:00Z");
		Instant max = Instant.parse("2024-06-15T20:00:00Z");
		Codec<Instant> codec = INSTANT.afterOrEqual(min).beforeOrEqual(max);
		JsonPrimitive json = new JsonPrimitive("2024-06-15T09:00:00Z");

		Result<Instant> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<Instant> codec = INSTANT;
		String str = codec.toString();
		assertEquals("InstantCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Instant reference = Instant.parse("2024-06-15T12:00:00Z");
		Codec<Instant> codec = INSTANT.after(reference);
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedInstantCodec["));
		assertTrue(str.contains("constraints="));
	}
}
