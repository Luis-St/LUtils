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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link OffsetDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetDateTimeCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		OffsetDateTime validValue = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validValue);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void toStringWithConstraints() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withEqualTo(expected));
		OffsetDateTime differentValue = OffsetDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T14:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withEqualTo(expected));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T14:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withIn(allowed));
		OffsetDateTime notAllowed = OffsetDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withIn(allowed));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-07-20T10:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withIn(allowed));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withIn(allowed));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-07-20T10:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withNotIn(excluded));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		OffsetDateTime beforeBoundary = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), beforeBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withAfter(boundary));
		
		Result<OffsetDateTime> result = codec.decodeKey("2019-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBefore(boundary));
		OffsetDateTime afterBoundary = OffsetDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), afterBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBefore(boundary));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2021-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBefore(boundary));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBefore(boundary));
		
		Result<OffsetDateTime> result = codec.decodeKey("2021-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBetween(start, end));
		OffsetDateTime tooEarly = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBetween(start, end));
		OffsetDateTime tooLate = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBetween(start, end));
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBetween(start, end));
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config -> config.withBetween(start, end));
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		OffsetDateTime outsideHours = OffsetDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideHours);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T08:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T08:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		OffsetDateTime oldYear = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), oldYear);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyYearConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyYearConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<OffsetDateTime> result = codec.decodeKey("2019-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		OffsetDateTime nonUtcValue = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonUtcValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyOffsetConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2)));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyOffsetConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withOffset(ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero())
		);
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:00:00+02:00");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		OffsetDateTime notOnHour = OffsetDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notOnHour);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<OffsetDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<String> result = codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<OffsetDateTime> result = codec.decodeKey("2023-06-15T12:30:00Z");
		assertTrue(result.isError());
	}
}
