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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeWithValidGreaterThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		Duration value = Duration.ofHours(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidLessThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.lessThan(threshold);
		Duration value = Duration.ofHours(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofMinutes(30);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofHours(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration target = Duration.ofHours(2).plusMinutes(30);
		Codec<Duration> codec = Codecs.DURATION.equalTo(target);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration value1 = Duration.ofHours(1);
		Duration value2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.in(Set.of(value1, value2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded = Duration.ofHours(1);
		Duration value = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded1 = Duration.ofHours(1);
		Duration excluded2 = Duration.ofHours(2);
		Duration value = Duration.ofHours(3);
		Codec<Duration> codec = Codecs.DURATION.notIn(Set.of(excluded1, excluded2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidPositiveConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.positive();
		Duration value = Duration.ofHours(1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNonNegativeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonNegative();
		Duration value = Duration.ZERO;
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNonZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonZero();
		Duration value = Duration.ofSeconds(1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.zero();
		Duration value = Duration.ZERO;
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.hour(builder -> builder.greaterThanOrEqual(1));
		Duration value = Duration.ofHours(2).plusMinutes(30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.minute(builder -> builder.betweenOrEqual(0, 30));
		Duration value = Duration.ofHours(1).plusMinutes(15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidSecondConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.second(builder -> builder.lessThan(30));
		Duration value = Duration.ofMinutes(1).plusSeconds(15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void decodeWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2h")));
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.custom(value -> {
			if (value.toMinutes() % 15 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Duration must be divisible by 15 minutes");
		});
		Duration value = Duration.ofMinutes(45);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		Duration value = Duration.ofHours(1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(1);
		Codec<Duration> codec = Codecs.DURATION.lessThan(threshold);
		Duration value = Duration.ofHours(2);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofHours(1);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofMinutes(30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration min = Duration.ofHours(1);
		Duration max = Duration.ofHours(5);
		Codec<Duration> codec = Codecs.DURATION.between(min, max);
		Duration value = Duration.ofHours(10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration target = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.equalTo(target);
		Duration differentValue = Duration.ofHours(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration value1 = Duration.ofHours(1);
		Duration value2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.in(Set.of(value1, value2));
		Duration notInSet = Duration.ofHours(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration excluded1 = Duration.ofHours(1);
		Duration excluded2 = Duration.ofHours(2);
		Codec<Duration> codec = Codecs.DURATION.notIn(Set.of(excluded1, excluded2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.positive();
		Duration value = Duration.ZERO;
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonNegative();
		Duration value = Duration.ofSeconds(-1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.nonZero();
		Duration value = Duration.ZERO;
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.zero();
		Duration value = Duration.ofSeconds(1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.hour(builder -> builder.greaterThanOrEqual(5));
		Duration value = Duration.ofHours(2).plusMinutes(30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.minute(builder -> builder.lessThan(15));
		Duration value = Duration.ofHours(1).plusMinutes(45);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Duration threshold = Duration.ofHours(3);
		Codec<Duration> codec = Codecs.DURATION.greaterThan(threshold);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2h")));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = Codecs.DURATION.custom(value -> {
			if (value.toMinutes() % 15 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Duration must be divisible by 15 minutes");
		});
		Duration value = Duration.ofMinutes(47);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
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
