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

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link OffsetTimeCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedOffsetTimeCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		OffsetTime validValue = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validValue));
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z")));
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertDoesNotThrow(() -> codec.decodeKey("12:00:00Z"));
	}
	
	@Test
	void toStringWithConstraints() {
		OffsetTime boundary = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.equalTo(expected);
		OffsetTime differentValue = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("14:00:00Z")));
	}
	
	@Test
	void encodeKeyEqualToConstraintViolation() {
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.equalTo(expected);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyEqualToConstraintViolation() {
		OffsetTime expected = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("14:00:00Z"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintViolation() {
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(excluded));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintViolation() {
		OffsetTime excluded = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12:00:00Z"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.in(allowed);
		OffsetTime notAllowed = OffsetTime.of(10, 30, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00Z")));
	}
	
	@Test
	void encodeKeyInConstraintViolation() {
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.in(allowed);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(10, 30, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyInConstraintViolation() {
		Set<OffsetTime> allowed = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10:30:00Z"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00Z")));
	}
	
	@Test
	void encodeKeyNotInConstraintViolation() {
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyNotInConstraintViolation() {
		Set<OffsetTime> excluded = Set.of(
			OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC),
			OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC)
		);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12:00:00Z"));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		OffsetTime beforeBoundary = OffsetTime.of(10, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), beforeBoundary));
	}
	
	@Test
	void decodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:00:00Z")));
	}
	
	@Test
	void encodeKeyAfterConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(10, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyAfterConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.after(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10:00:00Z"));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.before(boundary);
		OffsetTime afterBoundary = OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), afterBoundary));
	}
	
	@Test
	void decodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("14:00:00Z")));
	}
	
	@Test
	void encodeKeyBeforeConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.before(boundary);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(14, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBeforeConstraintViolation() {
		OffsetTime boundary = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.before(boundary);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("14:00:00Z"));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.between(start, end);
		OffsetTime tooEarly = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.between(start, end);
		OffsetTime tooLate = OffsetTime.of(18, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooLate));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("08:00:00Z")));
	}
	
	@Test
	void encodeKeyBetweenConstraintViolation() {
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.between(start, end);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(18, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyBetweenConstraintViolation() {
		OffsetTime start = OffsetTime.of(9, 0, 0, 0, ZoneOffset.UTC);
		OffsetTime end = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC);
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.between(start, end);
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("18:00:00Z"));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		OffsetTime outsideHours = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideHours));
	}
	
	@Test
	void decodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("08:00:00Z")));
	}
	
	@Test
	void encodeKeyHourConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyHourConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("08:00:00Z"));
	}
	
	@Test
	void encodeMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.minute(builder -> builder.equalTo(0));
		OffsetTime nonZeroMinute = OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonZeroMinute));
	}
	
	@Test
	void decodeMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.minute(builder -> builder.equalTo(0));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30:00Z")));
	}
	
	@Test
	void encodeKeyMinuteConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.minute(builder -> builder.equalTo(0));
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyMinuteConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.minute(builder -> builder.equalTo(0));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12:30:00Z"));
	}
	
	@Test
	void encodeOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		OffsetTime nonUtcValue = OffsetTime.of(12, 0, 0, 0, ZoneOffset.ofHours(2));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonUtcValue));
	}
	
	@Test
	void decodeOffsetConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:00:00+02:00")));
	}
	
	@Test
	void encodeKeyOffsetConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.offset(ZoneOffsetConstraintBuilder::zero);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(12, 0, 0, 0, ZoneOffset.ofHours(2))));
	}
	
	@Test
	void decodeKeyOffsetConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.offset(builder -> builder.zero());
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12:00:00+02:00"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		OffsetTime notOnHour = OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notOnHour));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30:00Z")));
	}
	
	@Test
	void encodeKeyCustomConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(OffsetTime.of(12, 30, 0, 0, ZoneOffset.UTC)));
	}
	
	@Test
	void decodeKeyCustomConstraintViolation() {
		Codec<OffsetTime> codec = Codecs.OFFSET_TIME.custom(value -> {
			if (!(value.getMinute() == 0 && value.getSecond() == 0)) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Value must be on the hour");
		});
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("12:30:00Z"));
	}
}
