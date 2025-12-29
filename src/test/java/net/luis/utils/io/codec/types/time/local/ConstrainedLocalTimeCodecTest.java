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

package net.luis.utils.io.codec.types.time.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeCodec} focusing on local time constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalTimeCodecTest {

	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - Temporal Comparison Constraints (After)

	@Test
	void encodeWithAfterConstraintSuccess() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.after(reference);
		LocalTime value = LocalTime.of(13, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.after(reference);
		LocalTime value = LocalTime.of(11, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAfterConstraintBoundary() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.after(reference);
		LocalTime value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Before)

	@Test
	void encodeWithBeforeConstraintSuccess() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.before(reference);
		LocalTime value = LocalTime.of(11, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.before(reference);
		LocalTime value = LocalTime.of(13, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Between)

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		LocalTime value = LocalTime.of(12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMinBoundary() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		LocalTime value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMaxBoundary() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		LocalTime value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		LocalTime value = LocalTime.of(18, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Temporal Comparison Constraints

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("11:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		LocalTime reference = LocalTime.of(12, 0);
		Codec<LocalTime> codec = LOCAL_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("13:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		Codec<LocalTime> codec = LOCAL_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("18:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Hour)

	@Test
	void encodeWithHourEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.equalTo(12));
		LocalTime value = LocalTime.of(12, 30, 45);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.equalTo(12));
		LocalTime value = LocalTime.of(13, 30, 45);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithHourBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(18, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithHourGreaterThanOrEqualConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.greaterThanOrEqual(9));
		LocalTime value = LocalTime.of(10, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourGreaterThanOrEqualConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.greaterThanOrEqual(9));
		LocalTime value = LocalTime.of(8, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Hour)

	@Test
	void decodeWithHourEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.equalTo(12));
		JsonPrimitive json = new JsonPrimitive("12:30:45");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30, 45), result.resultOrThrow());
	}

	@Test
	void decodeWithHourEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.equalTo(12));
		JsonPrimitive json = new JsonPrimitive("13:30:45");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithHourBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithHourBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("18:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Minute)

	@Test
	void encodeWithMinuteEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.equalTo(30));
		LocalTime value = LocalTime.of(12, 30, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinuteEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.equalTo(30));
		LocalTime value = LocalTime.of(12, 45, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMinuteBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.betweenOrEqual(0, 30));
		LocalTime value = LocalTime.of(12, 15, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinuteBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.betweenOrEqual(0, 30));
		LocalTime value = LocalTime.of(12, 45, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Minute)

	@Test
	void decodeWithMinuteEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.equalTo(30));
		JsonPrimitive json = new JsonPrimitive("12:30:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMinuteEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.equalTo(30));
		JsonPrimitive json = new JsonPrimitive("12:45:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMinuteBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:15:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 15, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMinuteBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.minute(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:45:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Second)

	@Test
	void encodeWithSecondEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSecondEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 30);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithSecondBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.betweenOrEqual(0, 30));
		LocalTime value = LocalTime.of(12, 30, 15);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSecondBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.betweenOrEqual(0, 30));
		LocalTime value = LocalTime.of(12, 30, 45);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Second)

	@Test
	void decodeWithSecondEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithSecondEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:30");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithSecondBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:30:15");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30, 15), result.resultOrThrow());
	}

	@Test
	void decodeWithSecondBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.second(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:30:45");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Millisecond)

	@Test
	void encodeWithMillisecondEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 45, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMillisecondEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 45, 500_000_000);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMillisecondBetweenConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.betweenOrEqual(0, 500));
		LocalTime value = LocalTime.of(12, 30, 45, 250_000_000);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMillisecondBetweenConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.betweenOrEqual(0, 500));
		LocalTime value = LocalTime.of(12, 30, 45, 750_000_000);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Millisecond)

	@Test
	void decodeWithMillisecondEqualToConstraintSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:45");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30, 45, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMillisecondEqualToConstraintFailure() {
		Codec<LocalTime> codec = LOCAL_TIME.millisecond(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:45.500");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME
			.afterOrEqual(LocalTime.of(9, 0))
			.beforeOrEqual(LocalTime.of(17, 0))
			.minute(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<LocalTime> codec = LOCAL_TIME
			.afterOrEqual(LocalTime.of(9, 0))
			.beforeOrEqual(LocalTime.of(17, 0))
			.minute(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 30);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME
			.afterOrEqual(LocalTime.of(9, 0))
			.beforeOrEqual(LocalTime.of(17, 0))
			.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<LocalTime> codec = LOCAL_TIME
			.afterOrEqual(LocalTime.of(9, 0))
			.beforeOrEqual(LocalTime.of(17, 0))
			.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("18:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMultipleFieldConstraintsSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 0, 0);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMultipleFieldConstraintsFailure() {
		Codec<LocalTime> codec = LOCAL_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		LocalTime value = LocalTime.of(12, 0, 30);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMultipleFieldConstraintsSuccess() {
		Codec<LocalTime> codec = LOCAL_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:00");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 0, 0), result.resultOrThrow());
	}

	@Test
	void decodeWithMultipleFieldConstraintsFailure() {
		Codec<LocalTime> codec = LOCAL_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:30");

		Result<LocalTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<LocalTime> codec = LOCAL_TIME;
		String str = codec.toString();
		assertEquals("LocalTimeCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<LocalTime> codec = LOCAL_TIME.hour(c -> c.betweenOrEqual(9, 17));
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedLocalTimeCodec["));
		assertTrue(str.contains("constraints="));
	}
}
