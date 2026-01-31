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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link ZoneOffsetCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedZoneOffsetCodecTest {
	
	@Test
	void encodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		ZoneOffset validValue = ZoneOffset.of("+02:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), validValue);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		Result<ZoneOffset> result = codec.decodeKey("Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		ZoneOffset differentValue = ZoneOffset.of("+05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyEqualToConstraintValid() {
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		Result<String> result = codec.encodeKey(expected);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyEqualToConstraintValid() {
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotEqualToConstraintValid() {
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+05:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotEqualToConstraintValid() {
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		Result<ZoneOffset> result = codec.decodeKey("+05:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		ZoneOffset notAllowed = ZoneOffset.of("+05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notAllowed);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyInConstraintValid() {
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyInConstraintValid() {
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), ZoneOffset.of("+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNotInConstraintValid() {
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+05:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNotInConstraintValid() {
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		Result<ZoneOffset> result = codec.decodeKey("+05:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		ZoneOffset smallerOffset = ZoneOffset.of("-01:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), smallerOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-01:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyGreaterThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+05:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyGreaterThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		Result<ZoneOffset> result = codec.decodeKey("+05:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		ZoneOffset largerOffset = ZoneOffset.of("+05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), largerOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyLessThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+01:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyLessThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		Result<ZoneOffset> result = codec.decodeKey("+01:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLow() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		ZoneOffset tooLow = ZoneOffset.of("-08:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooLow);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooHigh() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		ZoneOffset tooHigh = ZoneOffset.of("+08:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), tooHigh);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-08:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyBetweenConstraintValid() {
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyBetweenConstraintValid() {
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		ZoneOffset negativeOffset = ZoneOffset.of("-05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), negativeOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyPositiveConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyPositiveConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		ZoneOffset positiveOffset = ZoneOffset.of("+05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), positiveOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNegativeConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("-05:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNegativeConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		Result<ZoneOffset> result = codec.decodeKey("-05:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		ZoneOffset nonZeroOffset = ZoneOffset.of("+05:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), nonZeroOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		Result<String> result = codec.encodeKey(ZoneOffset.UTC);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		Result<ZoneOffset> result = codec.decodeKey("Z");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		ZoneOffset zeroOffset = ZoneOffset.UTC;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), zeroOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyNonZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyNonZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		ZoneOffset outsideRange = ZoneOffset.of("+08:00");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), outsideRange);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+08:00"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyHourConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyHourConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> value.getTotalSeconds() % 3600 == 0 ? Result.success(null) : Result.error("Offset must be on the hour (no minutes)"));
		ZoneOffset halfHourOffset = ZoneOffset.of("+05:30");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), halfHourOffset);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> value.getTotalSeconds() % 3600 == 0 ? Result.success(null) : Result.error("Offset must be on the hour (no minutes)"));
		
		Result<ZoneOffset> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:30"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyCustomConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> value.getTotalSeconds() % 3600 == 0 ? Result.success(null) : Result.error("Offset must be on the hour (no minutes)"));
		
		Result<String> result = codec.encodeKey(ZoneOffset.of("+02:00"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeKeyCustomConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> value.getTotalSeconds() % 3600 == 0 ? Result.success(null) : Result.error("Offset must be on the hour (no minutes)"));
		
		Result<ZoneOffset> result = codec.decodeKey("+02:00");
		assertTrue(result.isSuccess());
	}
}
