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

import java.time.Period;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PeriodCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedPeriodCodecTest {
	
	@Test
	void encodeWithValidGreaterThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(6);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withGreaterThan(threshold));
		Period value = Period.ofYears(1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidLessThanConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(2);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withLessThan(threshold));
		Period value = Period.ofMonths(6);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(1);
		Period max = Period.ofYears(2);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withBetween(min, max));
		Period value = Period.ofMonths(6);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period target = Period.of(1, 2, 15);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withEqualTo(target));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period value1 = Period.ofMonths(1);
		Period value2 = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withIn(Set.of(value1, value2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded = Period.ofMonths(1);
		Period value = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withNotEqualTo(excluded));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded1 = Period.ofMonths(1);
		Period excluded2 = Period.ofMonths(3);
		Period value = Period.ofMonths(6);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidPositiveConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.positive();
		Period value = Period.ofMonths(1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNonNegativeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.nonNegative();
		Period value = Period.ZERO;
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNonZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.nonZero();
		Period value = Period.ofDays(1);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidZeroConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.zero();
		Period value = Period.ZERO;
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidDayConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.day(builder -> builder.greaterThanOrEqual(10));
		Period value = Period.ofDays(15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.month(builder -> builder.betweenOrEqual(1, 6));
		Period value = Period.ofMonths(3);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.year(builder -> builder.lessThanOrEqual(5));
		Period value = Period.ofYears(2);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void decodeWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(1);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withGreaterThan(threshold));
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1y")));
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withCustom(value -> {
			if (value.getDays() % 7 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Period days must be divisible by 7");
		}));
		Period value = Period.ofDays(14);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(1);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withGreaterThan(threshold));
		Period value = Period.ofMonths(6);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofMonths(6);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withLessThan(threshold));
		Period value = Period.ofYears(1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooSmall() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(6);
		Period max = Period.ofYears(2);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withBetween(min, max));
		Period value = Period.ofMonths(1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLarge() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period min = Period.ofMonths(1);
		Period max = Period.ofYears(1);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withBetween(min, max));
		Period value = Period.ofYears(3);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period target = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withEqualTo(target));
		Period differentValue = Period.ofMonths(6);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period value1 = Period.ofMonths(1);
		Period value2 = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withIn(Set.of(value1, value2)));
		Period notInSet = Period.ofMonths(6);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withNotEqualTo(excluded));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period excluded1 = Period.ofMonths(1);
		Period excluded2 = Period.ofMonths(3);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.positive();
		Period value = Period.ZERO;
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeNonNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.nonNegative();
		Period value = Period.ofDays(-1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.nonZero();
		Period value = Period.ZERO;
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.zero();
		Period value = Period.ofDays(1);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeDayConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.day(builder -> builder.greaterThanOrEqual(20));
		Period value = Period.ofDays(10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.month(builder -> builder.lessThan(3));
		Period value = Period.ofMonths(6);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.year(builder -> builder.lessThanOrEqual(2));
		Period value = Period.ofYears(5);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Period threshold = Period.ofYears(2);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withGreaterThan(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("6m")));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withCustom(value -> {
			if (value.getDays() % 7 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Period days must be divisible by 7");
		}));
		Period value = Period.ofDays(10);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		Period threshold = Period.ofMonths(1);
		Codec<Period> codec = Codecs.PERIOD.apply(config -> config.withGreaterThan(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Period> codec = Codecs.PERIOD;
		
		assertEquals("PeriodCodec", codec.toString());
	}
}
