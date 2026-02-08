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

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YearCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedYearCodecTest {
	
	@Test
	void encodeWithValidAfterConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		Year value = Year.of(2023);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive(2023), result);
	}
	
	@Test
	void encodeWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2025);
		Codec<Year> codec = Codecs.YEAR.before(threshold);
		Year value = Year.of(2023);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = Codecs.YEAR.between(after, before);
		Year value = Year.of(2023);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year target = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.equalTo(target);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year value1 = Year.of(2023);
		Year value2 = Year.of(2024);
		Codec<Year> codec = Codecs.YEAR.in(Set.of(value1, value2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded = Year.of(2023);
		Year value = Year.of(2024);
		Codec<Year> codec = Codecs.YEAR.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded1 = Year.of(2023);
		Year excluded2 = Year.of(2024);
		Year value = Year.of(2025);
		Codec<Year> codec = Codecs.YEAR.notIn(Set.of(excluded1, excluded2));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.afterOrEqual(threshold);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.beforeOrEqual(threshold);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBetweenOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year min = Year.of(2020);
		Year max = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.betweenOrEqual(min, max);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), min));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), max));
	}
	
	@Test
	void encodeKeyWithValidConstraint() throws EncoderException {
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		Year value = Year.of(2023);
		
		String result = codec.encodeKey(value);
		assertEquals("2023", result);
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		
		Year result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(2023));
		assertEquals(Year.of(2023), result);
	}
	
	@Test
	void decodeKeyWithValidConstraint() throws DecoderException {
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		
		Year result = codec.decodeKey("2023");
		assertEquals(Year.of(2023), result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = Codecs.YEAR.custom(value -> {
			if (value.getValue() % 4 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Year must be divisible by 4 (leap year candidates)");
		});
		Year value = Year.of(2024);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		Year valueBefore = Year.of(2019);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueBefore));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.before(threshold);
		Year valueAfter = Year.of(2023);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueAfter));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = Codecs.YEAR.between(after, before);
		Year valueTooEarly = Year.of(2019);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year after = Year.of(2020);
		Year before = Year.of(2025);
		Codec<Year> codec = Codecs.YEAR.between(after, before);
		Year valueTooLate = Year.of(2026);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooLate));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year target = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.equalTo(target);
		Year differentValue = Year.of(2024);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year value1 = Year.of(2023);
		Year value2 = Year.of(2024);
		Codec<Year> codec = Codecs.YEAR.in(Set.of(value1, value2));
		Year notInSet = Year.of(2025);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded = Year.of(2023);
		Codec<Year> codec = Codecs.YEAR.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year excluded1 = Year.of(2023);
		Year excluded2 = Year.of(2024);
		Codec<Year> codec = Codecs.YEAR.notIn(Set.of(excluded1, excluded2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		Year valueBefore = Year.of(2019);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(valueBefore));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(2019)));
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Year> codec = Codecs.YEAR.custom(value -> {
			if (value.getValue() % 4 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Year must be divisible by 4 (leap year candidates)");
		});
		Year value = Year.of(2023);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		Year threshold = Year.of(2020);
		Codec<Year> codec = Codecs.YEAR.after(threshold);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Year> codec = Codecs.YEAR;
		
		assertEquals("YearCodec", codec.toString());
	}
}
