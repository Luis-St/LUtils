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

package net.luis.utils.io.codec.types.time.offset;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OffsetTimeCodec} focusing on offset time constraint functionality.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetTimeCodecTest {

	private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);
	private final JsonTypeProvider provider = JsonTypeProvider.INSTANCE;

	// Encode Tests - Temporal Comparison Constraints (After)

	@Test
	void encodeWithAfterConstraintSuccess() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.after(reference);
		OffsetTime value = OffsetTime.of(13, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithAfterConstraintFailure() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.after(reference);
		OffsetTime value = OffsetTime.of(11, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithAfterConstraintBoundary() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.after(reference);
		OffsetTime value = reference;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Before)

	@Test
	void encodeWithBeforeConstraintSuccess() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.before(reference);
		OffsetTime value = OffsetTime.of(11, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBeforeConstraintFailure() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.before(reference);
		OffsetTime value = OffsetTime.of(13, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Temporal Comparison Constraints (Between)

	@Test
	void encodeWithBetweenOrEqualConstraintSuccess() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		OffsetTime value = OffsetTime.of(12, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMinBoundary() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		OffsetTime value = min;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintMaxBoundary() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		OffsetTime value = max;

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithBetweenOrEqualConstraintFailure() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		OffsetTime value = OffsetTime.of(18, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Temporal Comparison Constraints

	@Test
	void decodeWithAfterOrEqualConstraintSuccess() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithAfterOrEqualConstraintFailure() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.afterOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("11:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBeforeOrEqualConstraintSuccess() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(reference, result.resultOrThrow());
	}

	@Test
	void decodeWithBeforeOrEqualConstraintFailure() {
		OffsetTime reference = OffsetTime.of(12, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.beforeOrEqual(reference);
		JsonPrimitive json = new JsonPrimitive("13:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithBetweenOrEqualConstraintSuccess() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 0, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithBetweenOrEqualConstraintFailure() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		Codec<OffsetTime> codec = OFFSET_TIME.betweenOrEqual(min, max);
		JsonPrimitive json = new JsonPrimitive("18:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Hour)

	@Test
	void encodeWithHourEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.equalTo(12));
		OffsetTime value = OffsetTime.of(12, 30, 45, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.equalTo(12));
		OffsetTime value = OffsetTime.of(13, 30, 45, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithHourBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.betweenOrEqual(9, 17));
		OffsetTime value = OffsetTime.of(12, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.betweenOrEqual(9, 17));
		OffsetTime value = OffsetTime.of(18, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithHourGreaterThanOrEqualConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.greaterThanOrEqual(9));
		OffsetTime value = OffsetTime.of(10, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithHourGreaterThanOrEqualConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.greaterThanOrEqual(9));
		OffsetTime value = OffsetTime.of(8, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Hour)

	@Test
	void decodeWithHourEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.equalTo(12));
		JsonPrimitive json = new JsonPrimitive("12:30:45+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 30, 45, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithHourEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.equalTo(12));
		JsonPrimitive json = new JsonPrimitive("13:30:45+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithHourBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 0, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithHourBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.betweenOrEqual(9, 17));
		JsonPrimitive json = new JsonPrimitive("18:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Minute)

	@Test
	void encodeWithMinuteEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.equalTo(30));
		OffsetTime value = OffsetTime.of(12, 30, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinuteEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.equalTo(30));
		OffsetTime value = OffsetTime.of(12, 45, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMinuteBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.betweenOrEqual(0, 30));
		OffsetTime value = OffsetTime.of(12, 15, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMinuteBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.betweenOrEqual(0, 30));
		OffsetTime value = OffsetTime.of(12, 45, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Minute)

	@Test
	void decodeWithMinuteEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.equalTo(30));
		JsonPrimitive json = new JsonPrimitive("12:30:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 30, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithMinuteEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.equalTo(30));
		JsonPrimitive json = new JsonPrimitive("12:45:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMinuteBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:15:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 15, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithMinuteBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.minute(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:45:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Second)

	@Test
	void encodeWithSecondEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 30, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSecondEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 30, 30, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithSecondBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.betweenOrEqual(0, 30));
		OffsetTime value = OffsetTime.of(12, 30, 15, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithSecondBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.betweenOrEqual(0, 30));
		OffsetTime value = OffsetTime.of(12, 30, 45, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Second)

	@Test
	void decodeWithSecondEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 30, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithSecondEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:30+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithSecondBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:30:15+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 30, 15, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithSecondBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.second(c -> c.betweenOrEqual(0, 30));
		JsonPrimitive json = new JsonPrimitive("12:30:45+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Encode Tests - Time Field Constraints (Millisecond)

	@Test
	void encodeWithMillisecondEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 30, 45, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMillisecondEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 30, 45, 500_000_000, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMillisecondBetweenConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.betweenOrEqual(0, 500));
		OffsetTime value = OffsetTime.of(12, 30, 45, 250_000_000, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMillisecondBetweenConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.betweenOrEqual(0, 500));
		OffsetTime value = OffsetTime.of(12, 30, 45, 750_000_000, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Decode Tests - Time Field Constraints (Millisecond)

	@Test
	void decodeWithMillisecondEqualToConstraintSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:45+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 30, 45, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithMillisecondEqualToConstraintFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME.millisecond(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:30:45.500+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// Chained Constraints Tests

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.afterOrEqual(OffsetTime.of(9, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetTime.of(17, 0, 0, 0, OFFSET))
			.minute(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.afterOrEqual(OffsetTime.of(9, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetTime.of(17, 0, 0, 0, OFFSET))
			.minute(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 30, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.afterOrEqual(OffsetTime.of(9, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetTime.of(17, 0, 0, 0, OFFSET))
			.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 0, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithChainedTemporalAndFieldConstraintsFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.afterOrEqual(OffsetTime.of(9, 0, 0, 0, OFFSET))
			.beforeOrEqual(OffsetTime.of(17, 0, 0, 0, OFFSET))
			.minute(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("18:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void encodeWithMultipleFieldConstraintsSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 0, 0, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeWithMultipleFieldConstraintsFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		OffsetTime value = OffsetTime.of(12, 0, 30, 0, OFFSET);

		Result<JsonElement> result = codec.encodeStart(this.provider, this.provider.empty(), value);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	@Test
	void decodeWithMultipleFieldConstraintsSuccess() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:00+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.of(12, 0, 0, 0, OFFSET), result.resultOrThrow());
	}

	@Test
	void decodeWithMultipleFieldConstraintsFailure() {
		Codec<OffsetTime> codec = OFFSET_TIME
			.hour(c -> c.betweenOrEqual(9, 17))
			.minute(c -> c.equalTo(0))
			.second(c -> c.equalTo(0));
		JsonPrimitive json = new JsonPrimitive("12:00:30+02:00");

		Result<OffsetTime> result = codec.decodeStart(this.provider, this.provider.empty(), json);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("does not meet constraints"));
	}

	// toString Tests

	@Test
	void toStringWithoutConstraints() {
		Codec<OffsetTime> codec = OFFSET_TIME;
		String str = codec.toString();
		assertEquals("OffsetTimeCodec", str);
	}

	@Test
	void toStringWithConstraints() {
		Codec<OffsetTime> codec = OFFSET_TIME.hour(c -> c.betweenOrEqual(9, 17));
		String str = codec.toString();
		assertTrue(str.startsWith("ConstrainedOffsetTimeCodec["));
		assertTrue(str.contains("constraints="));
	}
}
