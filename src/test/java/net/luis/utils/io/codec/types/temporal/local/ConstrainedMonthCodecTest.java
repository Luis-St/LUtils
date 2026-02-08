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

import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MonthCodec} with constraints.<br>
 * Note: MonthCodec uses EnumConstraintConfig which does not support key encoding/decoding.<br>
 *
 * @author Luis-St
 */
class ConstrainedMonthCodecTest {
	
	@Test
	void encodeWithValidEqualToConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.equalTo(Month.JUNE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Month.JUNE);
		assertEquals(new JsonPrimitive("JUNE"), result);
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = Codecs.MONTH.in(summerMonths);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JUNE));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JULY));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.AUGUST));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.notEqualTo(Month.JANUARY);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JUNE));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> winterMonths = Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY);
		Codec<Month> codec = Codecs.MONTH.notIn(winterMonths);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JUNE));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JULY));
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = Codecs.MONTH.in(summerMonths);
		
		Month result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("JUNE"));
		assertEquals(Month.JUNE, result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.custom(value -> {
			if (value.getValue() >= 4 && value.getValue() <= 9) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Month must be in warm season (April to September)");
		});
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JUNE));
	}
	
	@Test
	void encodeWithAllMonthsInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> allMonths = Set.of(Month.values());
		Codec<Month> codec = Codecs.MONTH.in(allMonths);
		
		for (Month month : Month.values()) {
			assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), month));
		}
	}
	
	@Test
	void encodeWithFirstQuarterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> firstQuarter = Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
		Codec<Month> codec = Codecs.MONTH.in(firstQuarter);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), Month.MARCH));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.equalTo(Month.JUNE);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = Codecs.MONTH.in(summerMonths);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.DECEMBER));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.notEqualTo(Month.JANUARY);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> winterMonths = Set.of(Month.DECEMBER, Month.JANUARY, Month.FEBRUARY);
		Codec<Month> codec = Codecs.MONTH.notIn(winterMonths);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.DECEMBER));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> summerMonths = Set.of(Month.JUNE, Month.JULY, Month.AUGUST);
		Codec<Month> codec = Codecs.MONTH.in(summerMonths);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("JANUARY")));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = Codecs.MONTH.custom(value -> {
			if (value.getValue() >= 4 && value.getValue() <= 9) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Month must be in warm season (April to September)");
		});
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JANUARY));
	}
	
	@Test
	void encodeFirstQuarterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<Month> firstQuarter = Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH);
		Codec<Month> codec = Codecs.MONTH.in(firstQuarter);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.JUNE));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), Month.DECEMBER));
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<Month> codec = Codecs.MONTH.equalTo(Month.JUNE);
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Month> codec = Codecs.MONTH;
		
		assertEquals("MonthCodec", codec.toString());
	}
}
