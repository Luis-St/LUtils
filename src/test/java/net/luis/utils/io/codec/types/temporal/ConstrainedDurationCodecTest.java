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
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DurationCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedDurationCodecTest {
	
	@Test
	void encodeStartWithValidGreaterThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		Duration value = Duration.ofHours(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidLessThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.lessThan(threshold);
		Duration value = Duration.ofHours(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofMinutes(30);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofHours(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration target = Duration.ofHours(2).plusMinutes(30);
		Codec<Duration> codec = Codecs.DURATION.equalTo(target);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration value1 = Duration.ofHours(1);
		Duration value2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.in(Set.of(value1, value2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded = Duration.ofHours(1);
		Duration value = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded1 = Duration.ofHours(1);
		Duration excluded2 = Duration.ofHours(2);
		Duration value = Duration.ofHours(3);
		Codec<Duration> codec = Codecs.DURATION.notIn(Set.of(excluded1, excluded2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidPositiveConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.positive();
		Duration value = Duration.ofHours(1);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeStartWithValidNonNegativeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonNegative();
		Duration value = Duration.ZERO;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeStartWithValidNonZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonZero();
		Duration value = Duration.ofSeconds(1);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}

	@Test
	void encodeStartWithValidZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.zero();
		Duration value = Duration.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.hour(builder -> builder.greaterThanOrEqual(1));
		Duration value = Duration.ofHours(2).plusMinutes(30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.minute(builder -> builder.betweenOrEqual(0, 30));
		Duration value = Duration.ofHours(1).plusMinutes(15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidSecondConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.second(builder -> builder.lessThan(30));
		Duration value = Duration.ofMinutes(1).plusSeconds(15);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		
		Result<Duration> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2h"));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.custom(value -> {
			if (value.toMinutes() % 15 == 0) {
				return Result.success(null);
			}
			return Result.error("Duration must be divisible by 15 minutes");
		});
		Duration value = Duration.ofMinutes(45);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		Duration value = Duration.ofHours(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.lessThan(threshold);
		Duration value = Duration.ofHours(2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofHours(1);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofMinutes(30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofHours(1);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofHours(10);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration target = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.equalTo(target);
		Duration differentValue = Duration.ofHours(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration value1 = Duration.ofHours(1);
		Duration value2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.in(Set.of(value1, value2));
		Duration notInSet = Duration.ofHours(3);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notEqualTo(excluded);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded1 = Duration.ofHours(1);
		Duration excluded2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notIn(Set.of(excluded1, excluded2));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartPositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.positive();
		Duration value = Duration.ZERO;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeStartNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonNegative();
		Duration value = Duration.ofSeconds(-1);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeStartNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonZero();
		Duration value = Duration.ZERO;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}

	@Test
	void encodeStartZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.zero();
		Duration value = Duration.ofSeconds(1);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.hour(builder -> builder.greaterThanOrEqual(5));
		Duration value = Duration.ofHours(2).plusMinutes(30);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.minute(builder -> builder.lessThan(15));
		Duration value = Duration.ofHours(1).plusMinutes(45);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(3);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		
		Result<Duration> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2h"));
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.custom(value -> {
			if (value.toMinutes() % 15 == 0) {
				return Result.success(null);
			}
			return Result.error("Duration must be divisible by 15 minutes");
		});
		Duration value = Duration.ofMinutes(47);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
	}
	
	@Test
	void toStringWithConstraints() {
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Duration> codec = Codecs.DURATION;
		
		assertEquals("DurationCodec", codec.toString());
	}
}
