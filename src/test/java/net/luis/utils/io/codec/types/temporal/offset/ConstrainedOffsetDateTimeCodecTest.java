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

package net.luis.utils.io.codec.types.temporal.offset;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.builder.ZoneOffsetConstraintBuilder;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link OffsetDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetDateTimeCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		OffsetDateTime validValue = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validValue));
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void toStringWithConstraints() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.equalTo(expected);
		OffsetDateTime differentValue = OffsetDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T14:00:00Z")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.equalTo(expected);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		OffsetDateTime expected = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T14:00:00Z"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		OffsetDateTime excluded = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.in(allowed);
		OffsetDateTime notAllowed = OffsetDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-07-20T10:00:00Z")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.in(allowed);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<OffsetDateTime> allowed = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-07-20T10:00:00Z"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<OffsetDateTime> excluded = Set.of(
			OffsetDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		OffsetDateTime beforeBoundary = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), beforeBoundary));
	}
	
	@Test
	void decodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.before(boundary);
		OffsetDateTime afterBoundary = OffsetDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), afterBoundary));
	}
	
	@Test
	void decodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2021-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.before(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		OffsetDateTime boundary = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2021-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.between(start, end);
		OffsetDateTime tooEarly = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.between(start, end);
		OffsetDateTime tooLate = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooLate));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.between(start, end);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		OffsetDateTime start = OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		OffsetDateTime end = OffsetDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		OffsetDateTime outsideHours = OffsetDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideHours));
	}
	
	@Test
	void decodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T08:00:00Z")));
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T08:00:00Z"));
	}
	
	@Test
	void encodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		OffsetDateTime oldYear = OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), oldYear));
	}
	
	@Test
	void decodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyYearConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyYearConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		OffsetDateTime nonUtcValue = OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonUtcValue));
	}
	
	@Test
	void decodeOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00+02:00")));
	}
	
	@Test
	void encodeKeyOffsetConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.ofHours(2))));
	}
	
	@Test
	void decodeKeyOffsetConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00+02:00"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		OffsetDateTime notOnHour = OffsetDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notOnHour));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z")));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<OffsetDateTime> codec = Codecs.OFFSET_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:30:00Z"));
	}
}
