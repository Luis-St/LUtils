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

package net.luis.utils.io.codec.types.temporal.zoned;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.constraint.builder.ZoneIdConstraintBuilder;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link ZonedDateTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedZonedDateTimeCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		ZonedDateTime validValue = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validValue));
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void toStringWithConstraints() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.equalTo(expected);
		ZonedDateTime differentValue = ZonedDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T14:00:00Z")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.equalTo(expected);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 14, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		ZonedDateTime expected = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T14:00:00Z"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		ZonedDateTime excluded = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.in(allowed);
		ZonedDateTime notAllowed = ZonedDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-07-20T10:00:00Z")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.in(allowed);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 7, 20, 10, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<ZonedDateTime> allowed = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-07-20T10:00:00Z"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<ZonedDateTime> excluded = Set.of(
			ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
			ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		ZonedDateTime beforeBoundary = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), beforeBoundary));
	}
	
	@Test
	void decodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.before(boundary);
		ZonedDateTime afterBoundary = ZonedDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), afterBoundary));
	}
	
	@Test
	void decodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2021-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.before(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2021, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		ZonedDateTime boundary = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2021-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.between(start, end);
		ZonedDateTime tooEarly = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.between(start, end);
		ZonedDateTime tooLate = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooLate));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.between(start, end);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		ZonedDateTime start = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		ZonedDateTime end = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		ZonedDateTime outsideHours = ZonedDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideHours));
	}
	
	@Test
	void decodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T08:00:00Z")));
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 8, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T08:00:00Z"));
	}
	
	@Test
	void encodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		ZonedDateTime oldYear = ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), oldYear));
	}
	
	@Test
	void decodeYearConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:00:00Z")));
	}
	
	@Test
	void encodeKeyYearConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2019, 6, 15, 12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyYearConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.year(builder -> builder.greaterThanOrEqual(2020));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:00:00Z"));
	}
	
	@Test
	void encodeZoneConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.zone(ZoneIdConstraintBuilder::utc);
		ZonedDateTime nonUtcValue = ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Berlin"));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonUtcValue));
	}
	
	@Test
	void decodeZoneConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.zone(ZoneIdConstraintBuilder::utc);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:00:00+02:00[Europe/Berlin]")));
	}
	
	@Test
	void encodeKeyZoneConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.zone(ZoneIdConstraintBuilder::utc);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 0, 0, 0, ZoneId.of("Europe/Berlin"))));
	}
	
	@Test
	void decodeKeyZoneConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.zone(ZoneIdConstraintBuilder::utc);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:00:00+02:00[Europe/Berlin]"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		ZonedDateTime notOnHour = ZonedDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notOnHour));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z")));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(ZonedDateTime.of(2023, 6, 15, 12, 30, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<ZonedDateTime> codec = Codecs.ZONED_DATE_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2023-06-15T12:30:00Z"));
	}
}
