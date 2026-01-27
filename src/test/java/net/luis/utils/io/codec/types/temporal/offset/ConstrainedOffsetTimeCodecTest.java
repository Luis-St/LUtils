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

package net.luis.utils.io.codec.types.temporal.offset;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link OffsetTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetTimeCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		OffsetTime validValue = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validValue);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetTime> result = codec.decodeKey("12:00:00Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void toStringWithConstraints() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withEqualTo(expected));
		OffsetTime differentValue = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("14:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<OffsetTime> result = codec.decodeKey("14:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<OffsetTime> result = codec.decodeKey("12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withIn(allowed));
		OffsetTime notAllowed = OffsetTime.of(10, 30, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withIn(allowed));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withIn(allowed));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(10, 30, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withIn(allowed));
		
		Result<OffsetTime> result = codec.decodeKey("10:30:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<OffsetTime> result = codec.decodeKey("12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		OffsetTime beforeBoundary = OffsetTime.of(10, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), beforeBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(10, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetTime> result = codec.decodeKey("10:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBefore(boundary));
		OffsetTime afterBoundary = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), afterBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBefore(boundary));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("14:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBefore(boundary));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBefore(boundary));
		
		Result<OffsetTime> result = codec.decodeKey("14:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBetween(start, end));
		OffsetTime tooEarly = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBetween(start, end));
		OffsetTime tooLate = OffsetTime.of(18, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBetween(start, end));
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("08:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBetween(start, end));
		
		Result<String> result = codec.encodeKey(OffsetTime.of(18, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config -> config.withBetween(start, end));
		
		Result<OffsetTime> result = codec.decodeKey("18:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		OffsetTime outsideHours = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideHours);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("08:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<String> result = codec.encodeKey(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<OffsetTime> result = codec.decodeKey("08:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withMinute(NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0))
		);
		OffsetTime nonZeroMinute = OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonZeroMinute);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withMinute(NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0))
		);
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyMinuteConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withMinute(NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0))
		);
		
		Result<String> result = codec.encodeKey(OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyMinuteConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withMinute(NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0))
		);
		
		Result<OffsetTime> result = codec.decodeKey("12:30:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		OffsetTime nonUtcValue = OffsetTime.of(12, 0, 0, 0, ZoneOffset.ofHours(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonUtcValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOffsetConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<String> result = codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.ofHours(2)));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOffsetConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<OffsetTime> result = codec.decodeKey("12:00:00+02:00");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		OffsetTime notOnHour = OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notOnHour);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<String> result = codec.encodeKey(OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<OffsetTime> result = codec.decodeKey("12:30:00Z");
		assertTrue(result.isError());
	}
}
