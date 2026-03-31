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
 * Test class for {@link LocalDateCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalDateCodecTest {
	
	@Test
	void encodeWithValidAfterConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("2023-06-15"), result);
	}
	
	@Test
	void encodeWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.before(threshold);
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.between(after, before);
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate target = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.equalTo(target);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate value1 = LocalDate.of(2023, 6, 15);
		LocalDate value2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.in(Set.of(value1, value2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded = LocalDate.of(2023, 6, 15);
		LocalDate value = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded1 = LocalDate.of(2023, 6, 15);
		LocalDate excluded2 = LocalDate.of(2023, 7, 20);
		LocalDate value = LocalDate.of(2023, 8, 25);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.notIn(Set.of(excluded1, excluded2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.afterOrEqual(threshold);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.beforeOrEqual(threshold);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidDayOfWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.dayOfWeek(builder -> builder.in(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)));
		LocalDate value = LocalDate.of(2023, 6, 16); // Friday
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidDayOfMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.dayOfMonth(builder -> builder.betweenOrEqual(1, 15));
		LocalDate value = LocalDate.of(2023, 6, 10);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMonthConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.month(builder -> builder.in(Set.of(Month.JUNE, Month.JULY, Month.AUGUST)));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidYearConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.year(builder -> builder.greaterThanOrEqual(2020));
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyWithValidConstraint() throws EncoderException {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		String result = codec.encodeKey(value);
		assertEquals("2023-06-15", result);
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		
		LocalDate result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15"));
		assertEquals(LocalDate.of(2023, 6, 15), result);
	}
	
	@Test
	void decodeKeyWithValidConstraint() throws DecoderException {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		
		LocalDate result = codec.decodeKey("2023-06-15");
		assertEquals(LocalDate.of(2023, 6, 15), result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.custom(value -> {
			if (value.getDayOfMonth() % 5 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Day of month must be divisible by 5");
		});
		LocalDate value = LocalDate.of(2023, 6, 15);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		LocalDate valueBefore = LocalDate.of(2019, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueBefore));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.before(threshold);
		LocalDate valueAfter = LocalDate.of(2023, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueAfter));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.between(after, before);
		LocalDate valueTooEarly = LocalDate.of(2019, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate after = LocalDate.of(2020, 1, 1);
		LocalDate before = LocalDate.of(2025, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.between(after, before);
		LocalDate valueTooLate = LocalDate.of(2026, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooLate));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate target = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.equalTo(target);
		LocalDate differentValue = LocalDate.of(2023, 7, 20);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate value1 = LocalDate.of(2023, 6, 15);
		LocalDate value2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.in(Set.of(value1, value2));
		LocalDate notInSet = LocalDate.of(2023, 8, 25);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded = LocalDate.of(2023, 6, 15);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate excluded1 = LocalDate.of(2023, 6, 15);
		LocalDate excluded2 = LocalDate.of(2023, 7, 20);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.notIn(Set.of(excluded1, excluded2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodeDayOfWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.dayOfWeek(builder -> builder.in(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)));
		LocalDate value = LocalDate.of(2023, 6, 17); // Saturday
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeDayOfMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.dayOfMonth(builder -> builder.betweenOrEqual(1, 15));
		LocalDate value = LocalDate.of(2023, 6, 20);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMonthConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.month(builder -> builder.in(Set.of(Month.JUNE, Month.JULY, Month.AUGUST)));
		LocalDate value = LocalDate.of(2023, 1, 15); // January
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.year(builder -> builder.greaterThanOrEqual(2020));
		LocalDate value = LocalDate.of(2015, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		LocalDate valueBefore = LocalDate.of(2019, 6, 15);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(valueBefore));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15")));
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.custom(value -> {
			if (value.getDayOfMonth() % 5 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Day of month must be divisible by 5");
		});
		LocalDate value = LocalDate.of(2023, 6, 17);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		LocalDate threshold = LocalDate.of(2020, 1, 1);
		Codec<LocalDate> codec = Codecs.LOCAL_DATE.after(threshold);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalDate> codec = Codecs.LOCAL_DATE;
		
		assertEquals("LocalDateCodec", codec.toString());
	}
}
