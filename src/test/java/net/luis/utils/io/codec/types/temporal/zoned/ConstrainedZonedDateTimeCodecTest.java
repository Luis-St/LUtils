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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link ZonedDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedZonedDateTimeCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		ZonedDateTime validValue = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validValue);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void toStringWithConstraints() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withEqualTo(expected));
		ZonedDateTime differentValue = ZonedDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withEqualTo(expected));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T14:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withEqualTo(expected));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withEqualTo(expected));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T14:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<String> result = codec.encodeKey(excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotEqualTo(excluded));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withIn(allowed));
		ZonedDateTime notAllowed = ZonedDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withIn(allowed));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-07-20T10:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withIn(allowed));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withIn(allowed));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-07-20T10:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotIn(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotIn(excluded));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotIn(excluded));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withNotIn(excluded));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		ZonedDateTime beforeBoundary = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), beforeBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withAfter(boundary));
		
		Result<ZonedDateTime> result = codec.decodeKey("2019-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBefore(boundary));
		ZonedDateTime afterBoundary = ZonedDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), afterBoundary);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBefore(boundary));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2021-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBefore(boundary));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBefore(boundary));
		
		Result<ZonedDateTime> result = codec.decodeKey("2021-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBetween(start, end));
		ZonedDateTime tooEarly = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBetween(start, end));
		ZonedDateTime tooLate = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBetween(start, end));
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBetween(start, end));
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config -> config.withBetween(start, end));
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		ZonedDateTime outsideHours = ZonedDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideHours);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T08:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withHour(NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17))
		);
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T08:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		ZonedDateTime oldYear = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), oldYear);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyYearConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyYearConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withYear(NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020))
		);
		
		Result<ZonedDateTime> result = codec.decodeKey("2019-06-15T12:00:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartZoneConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withZone(ZoneIdConstraintConfig.UNCONSTRAINED.withUtc())
		);
		ZonedDateTime nonUtcValue = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Berlin"));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonUtcValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZoneConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withZone(ZoneIdConstraintConfig.UNCONSTRAINED.withUtc())
		);
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00+02:00[Europe/Berlin]"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZoneConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withZone(ZoneIdConstraintConfig.UNCONSTRAINED.withUtc())
		);
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Berlin")));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyZoneConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withZone(ZoneIdConstraintConfig.UNCONSTRAINED.withUtc())
		);
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:00:00+02:00[Europe/Berlin]");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		ZonedDateTime notOnHour = ZonedDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notOnHour);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<ZonedDateTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<String> result = codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<ZonedDateTime> codec = new ZonedDateTimeCodec().apply(config ->
			config.withCustom(value -> value.getMinute() == 0 && value.getSecond() == 0 ? Result.success(null) : Result.error("Value must be on the hour"))
		);
		
		Result<ZonedDateTime> result = codec.decodeKey("2023-06-15T12:30:00Z");
		assertTrue(result.isError());
	}
}
