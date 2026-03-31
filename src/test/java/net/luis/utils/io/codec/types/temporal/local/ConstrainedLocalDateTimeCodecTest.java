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
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalDateTimeCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateTimeCodecTest {
	
	@Test
	void encodeWithValidAfterConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("2023-06-15T12:30"), result);
	}
	
	@Test
	void encodeWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBefore(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBetween(after, before));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime target = LocalDateTime.of(2023, 6, 15, 12, 30, 45);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withEqualTo(target));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime value1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime excluded2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		LocalDateTime value = LocalDateTime.of(2023, 8, 25, 18, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfterOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBeforeOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidDayOfWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 16, 12, 30); // Friday
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withHour(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withMinute(
			NumericConstraintConfig.UNCONSTRAINED.withIn(Set.of(0, 15, 30, 45))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyWithValidConstraint() throws EncoderException {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		String result = codec.encodeKey(value);
		assertEquals("2023-06-15T12:30", result);
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		
		LocalDateTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30"));
		assertEquals(LocalDateTime.of(2023, 6, 15, 12, 30), result);
	}
	
	@Test
	void decodeKeyWithValidConstraint() throws DecoderException {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		
		LocalDateTime result = codec.decodeKey("2023-06-15T12:30");
		assertEquals(LocalDateTime.of(2023, 6, 15, 12, 30), result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0 && value.getSecond() == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("DateTime must be on quarter hour with zero seconds");
		}));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 30, 0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		LocalDateTime valueBefore = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueBefore));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBefore(threshold));
		LocalDateTime valueAfter = LocalDateTime.of(2023, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueAfter));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBetween(after, before));
		LocalDateTime valueTooEarly = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime after = LocalDateTime.of(2020, 1, 1, 0, 0);
		LocalDateTime before = LocalDateTime.of(2025, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withBetween(after, before));
		LocalDateTime valueTooLate = LocalDateTime.of(2026, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooLate));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime target = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withEqualTo(target));
		LocalDateTime differentValue = LocalDateTime.of(2023, 7, 20, 15, 45);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime value1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime value2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		LocalDateTime notInSet = LocalDateTime.of(2023, 8, 25, 18, 0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded = LocalDateTime.of(2023, 6, 15, 12, 30);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime excluded1 = LocalDateTime.of(2023, 6, 15, 12, 30);
		LocalDateTime excluded2 = LocalDateTime.of(2023, 7, 20, 15, 45);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodeDayOfWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withDayOfWeek(
			EnumConstraintConfig.<DayOfWeek>unconstrained().withIn(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 17, 12, 30); // Saturday
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withMonth(
			EnumConstraintConfig.<Month>unconstrained().withIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
		));
		LocalDateTime value = LocalDateTime.of(2023, 1, 15, 12, 30); // January
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withYear(
			NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(2020)
		));
		LocalDateTime value = LocalDateTime.of(2015, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withHour(
			NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17)
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 20, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withMinute(
			NumericConstraintConfig.UNCONSTRAINED.withIn(Set.of(0, 15, 30, 45))
		));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 22);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		LocalDateTime valueBefore = LocalDateTime.of(2019, 6, 15, 12, 30);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(valueBefore));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:30")));
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:30"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0 && value.getSecond() == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("DateTime must be on quarter hour with zero seconds");
		}));
		LocalDateTime value = LocalDateTime.of(2023, 6, 15, 12, 22, 0);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		LocalDateTime threshold = LocalDateTime.of(2020, 1, 1, 0, 0);
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME.apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDateTime> codec = Codecs.LOCAL_DATE_TIME;
		
		assertEquals("LocalDateTimeCodec", codec.toString());
	}
}
