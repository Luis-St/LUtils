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

import java.time.DayOfWeek;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DayOfWeekCodec} with constraints.<br>
 * Note: DayOfWeekCodec uses EnumConstraintConfig which does not support key encoding/decoding.<br>
 *
 * @author Luis-St
 */
class ConstrainedDayOfWeekCodecTest {
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		JsonElement result = null;
		try {
			result = codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY);
		} catch (EncoderException e) {
			throw new RuntimeException(e);
		}
		assertEquals(new JsonPrimitive("MONDAY"), result);
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notEqualTo(DayOfWeek.SUNDAY);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notIn(weekend);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void encodeWithWeekendConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekend);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY));
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		DayOfWeek result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("MONDAY"));
		assertEquals(DayOfWeek.MONDAY, result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.custom(value -> {
			if (value.getValue() <= 5) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Day must be a weekday");
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void encodeWithAllDaysInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> allDays = Set.of(DayOfWeek.values());
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(allDays);
		
		for (DayOfWeek day : DayOfWeek.values()) {
			assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), day));
		}
	}
	
	@Test
	void encodeWithMidWeekConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> midWeek = Set.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(midWeek);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.WEDNESDAY));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notEqualTo(DayOfWeek.SUNDAY);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.notIn(weekend);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SUNDAY));
	}
	
	@Test
	void encodeWeekendConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekend = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekend);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> weekdays = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(weekdays);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("SATURDAY")));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.custom(value -> {
			if (value.getValue() <= 5) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Day must be a weekday");
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.SATURDAY));
	}
	
	@Test
	void encodeMidWeekConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<DayOfWeek> midWeek = Set.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.in(midWeek);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.MONDAY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), DayOfWeek.FRIDAY));
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK.equalTo(DayOfWeek.MONDAY);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<DayOfWeek> codec = Codecs.DAY_OF_WEEK;
		
		assertEquals("DayOfWeekCodec", codec.toString());
	}
}
