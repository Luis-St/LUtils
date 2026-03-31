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

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedLocalTimeCodecTest {
	
	@Test
	void encodeWithValidAfterConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("12:30"), result);
	}
	
	@Test
	void encodeWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(18, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBefore(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(8, 0);
		LocalTime before = LocalTime.of(18, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime value = LocalTime.of(12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime target = LocalTime.of(12, 30, 45);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withEqualTo(target));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime value1 = LocalTime.of(9, 0);
		LocalTime value2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded = LocalTime.of(12, 0);
		LocalTime value = LocalTime.of(14, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded1 = LocalTime.of(9, 0);
		LocalTime excluded2 = LocalTime.of(12, 0);
		LocalTime value = LocalTime.of(15, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfterOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBeforeOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidHourConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidMinuteConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.minute(builder -> builder.in(Set.of(0, 15, 30, 45)));
		LocalTime value = LocalTime.of(12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidSecondConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.second(builder -> builder.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 0);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyWithValidConstraint() throws EncoderException {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime value = LocalTime.of(12, 30);
		
		String result = codec.encodeKey(value);
		assertEquals("12:30", result);
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		LocalTime result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12:30"));
		assertEquals(LocalTime.of(12, 30), result);
	}
	
	@Test
	void decodeKeyWithValidConstraint() throws DecoderException {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		LocalTime result = codec.decodeKey("12:30");
		assertEquals(LocalTime.of(12, 30), result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Minutes must be divisible by 15");
		}));
		LocalTime value = LocalTime.of(12, 30);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime valueBefore = LocalTime.of(10, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueBefore));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBefore(threshold));
		LocalTime valueAfter = LocalTime.of(14, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueAfter));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(9, 0);
		LocalTime before = LocalTime.of(17, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime valueTooEarly = LocalTime.of(7, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime after = LocalTime.of(9, 0);
		LocalTime before = LocalTime.of(17, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withBetween(after, before));
		LocalTime valueTooLate = LocalTime.of(20, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooLate));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime target = LocalTime.of(12, 30);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withEqualTo(target));
		LocalTime differentValue = LocalTime.of(14, 45);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime value1 = LocalTime.of(9, 0);
		LocalTime value2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withIn(Set.of(value1, value2)));
		LocalTime notInSet = LocalTime.of(15, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotEqualTo(excluded));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime excluded1 = LocalTime.of(9, 0);
		LocalTime excluded2 = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.hour(builder -> builder.betweenOrEqual(9, 17));
		LocalTime value = LocalTime.of(20, 30);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeMinuteConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.minute(builder -> builder.in(Set.of(0, 15, 30, 45)));
		LocalTime value = LocalTime.of(12, 25);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeSecondConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.second(builder -> builder.equalTo(0));
		LocalTime value = LocalTime.of(12, 30, 45);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		LocalTime valueBefore = LocalTime.of(10, 30);
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(valueBefore));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30")));
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		LocalTime threshold = LocalTime.of(12, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("10:30"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withCustom(value -> {
			if (value.getMinute() % 15 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Minutes must be divisible by 15");
		}));
		LocalTime value = LocalTime.of(12, 22);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		LocalTime threshold = LocalTime.of(8, 0);
		Codec<LocalTime> codec = Codecs.LOCAL_TIME.apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<LocalTime> codec = Codecs.LOCAL_TIME;
		
		assertEquals("LocalTimeCodec", codec.toString());
	}
}
