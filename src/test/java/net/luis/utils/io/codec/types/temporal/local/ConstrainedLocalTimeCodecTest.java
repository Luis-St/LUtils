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

package net.luis.utils.io.codec.types.temporal.local;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalTimeCodecTest {
	
	@Test
	void encodeStartWithValidAfterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("12:30"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(18, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBefore(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(8, 0);
		LocalTime before = LocalTime.of(18, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime value = LocalTime.of(12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime target = LocalTime.of(12, 30, 45);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime value1 = LocalTime.of(9, 0);
		LocalTime value2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded = LocalTime.of(12, 0);
		LocalTime value = LocalTime.of(14, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded1 = LocalTime.of(9, 0);
		LocalTime excluded2 = LocalTime.of(12, 0);
		LocalTime value = LocalTime.of(15, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfterOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBeforeOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(12, 30);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.minute(builder -> builder.in(Set.of(0, 15, 30, 45)));
		LocalTime value = LocalTime.of(12, 30);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidSecondConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.second(builder -> builder.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 0);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstraint() {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("12:30", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30"));
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstraint() {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		Result<LocalTime> result = codec.decodeKey("12:30");
		assertTrue(result.isSuccess());
		assertEquals(LocalTime.of(12, 30), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0) {
				return Result.success(null);
			}
			return Result.error("Minutes must be divisible by 15");
		}));
		LocalTime value = LocalTime.of(12, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime valueBefore = LocalTime.of(10, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBefore(threshold));
		LocalTime valueAfter = LocalTime.of(14, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueAfter);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(9, 0);
		LocalTime before = LocalTime.of(17, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime valueTooEarly = LocalTime.of(7, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(9, 0);
		LocalTime before = LocalTime.of(17, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime valueTooLate = LocalTime.of(20, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime target = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withEqualTo(target));
		LocalTime differentValue = LocalTime.of(14, 45);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime value1 = LocalTime.of(9, 0);
		LocalTime value2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		LocalTime notInSet = LocalTime.of(15, 30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded1 = LocalTime.of(9, 0);
		LocalTime excluded2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(20, 30);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.minute(builder -> builder.in(Set.of(0, 15, 30, 45)));
		LocalTime value = LocalTime.of(12, 25);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartSecondConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.second(builder -> builder.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 45);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime valueBefore = LocalTime.of(10, 30);
		
		Result<String> result = codec.encodeKey(valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		Result<LocalTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		Result<LocalTime> result = codec.decodeKey("10:30");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0) {
				return Result.success(null);
			}
			return Result.error("Minutes must be divisible by 15");
		}));
		LocalTime value = LocalTime.of(12, 22);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalTime> codec = Codecs.LOCAL_TIME;
		
		assertEquals("LocalTimeCodec", codec.toString());
	}
}
