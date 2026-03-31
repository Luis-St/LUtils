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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Constraint test class for {@link ZoneOffsetCodec}.<br>
 *
 * @author Luis-St
 */
class ConstrainedZoneOffsetCodecTest {
	
	@Test
	void encodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		ZoneOffset validValue = ZoneOffset.of("+02:00");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), validValue));
	}
	
	@Test
	void encodeKeyWithValidConstrainedValue() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeWithValidConstrainedValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		assertDoesNotThrow(() -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void decodeKeyWithValidConstrainedValue() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("Z"));
	}
	
	@Test
	void toStringWithConstraints() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		assertTrue(codec.toString().contains("Constrained"));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		ZoneOffset differentValue = ZoneOffset.of("+05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void decodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00")));
	}
	
	@Test
	void encodeKeyEqualToConstraintValid() {
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		assertDoesNotThrow(() -> codec.encodeKey(expected));
	}
	
	@Test
	void decodeKeyEqualToConstraintValid() {
		ZoneOffset expected = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.equalTo(expected);
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void decodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void encodeKeyNotEqualToConstraintValid() {
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+05:00")));
	}
	
	@Test
	void decodeKeyNotEqualToConstraintValid() {
		ZoneOffset excluded = ZoneOffset.of("+02:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notEqualTo(excluded);
		
		assertDoesNotThrow(() -> codec.decodeKey("+05:00"));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		ZoneOffset notAllowed = ZoneOffset.of("+05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notAllowed));
	}
	
	@Test
	void decodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00")));
	}
	
	@Test
	void encodeKeyInConstraintValid() {
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyInConstraintValid() {
		Set<ZoneOffset> allowed = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.in(allowed);
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+02:00")));
	}
	
	@Test
	void encodeKeyNotInConstraintValid() {
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+05:00")));
	}
	
	@Test
	void decodeKeyNotInConstraintValid() {
		Set<ZoneOffset> excluded = Set.of(
			ZoneOffset.of("+01:00"),
			ZoneOffset.of("+02:00")
		);
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.notIn(excluded);
		
		assertDoesNotThrow(() -> codec.decodeKey("+05:00"));
	}
	
	@Test
	void encodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		ZoneOffset smallerOffset = ZoneOffset.of("-01:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), smallerOffset));
	}
	
	@Test
	void decodeGreaterThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-01:00")));
	}
	
	@Test
	void encodeKeyGreaterThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+05:00")));
	}
	
	@Test
	void decodeKeyGreaterThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.greaterThan(ZoneOffset.of("+02:00"));
		
		assertDoesNotThrow(() -> codec.decodeKey("+05:00"));
	}
	
	@Test
	void encodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		ZoneOffset largerOffset = ZoneOffset.of("+05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), largerOffset));
	}
	
	@Test
	void decodeLessThanConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00")));
	}
	
	@Test
	void encodeKeyLessThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+01:00")));
	}
	
	@Test
	void decodeKeyLessThanConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.lessThan(ZoneOffset.of("+02:00"));
		
		assertDoesNotThrow(() -> codec.decodeKey("+01:00"));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLow() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		ZoneOffset tooLow = ZoneOffset.of("-08:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooLow));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooHigh() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		ZoneOffset tooHigh = ZoneOffset.of("+08:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), tooHigh));
	}
	
	@Test
	void decodeBetweenConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-08:00")));
	}
	
	@Test
	void encodeKeyBetweenConstraintValid() {
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyBetweenConstraintValid() {
		ZoneOffset min = ZoneOffset.of("-05:00");
		ZoneOffset max = ZoneOffset.of("+05:00");
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.between(min, max);
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		ZoneOffset negativeOffset = ZoneOffset.of("-05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), negativeOffset));
	}
	
	@Test
	void decodePositiveConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-05:00")));
	}
	
	@Test
	void encodeKeyPositiveConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyPositiveConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.positive();
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		ZoneOffset positiveOffset = ZoneOffset.of("+05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), positiveOffset));
	}
	
	@Test
	void decodeNegativeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00")));
	}
	
	@Test
	void encodeKeyNegativeConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("-05:00")));
	}
	
	@Test
	void decodeKeyNegativeConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.negative();
		
		assertDoesNotThrow(() -> codec.decodeKey("-05:00"));
	}
	
	@Test
	void encodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		ZoneOffset nonZeroOffset = ZoneOffset.of("+05:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), nonZeroOffset));
	}
	
	@Test
	void decodeZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:00")));
	}
	
	@Test
	void encodeKeyZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.UTC));
	}
	
	@Test
	void decodeKeyZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.zero();
		
		assertDoesNotThrow(() -> codec.decodeKey("Z"));
	}
	
	@Test
	void encodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		ZoneOffset zeroOffset = ZoneOffset.UTC;
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), zeroOffset));
	}
	
	@Test
	void decodeNonZeroConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("Z")));
	}
	
	@Test
	void encodeKeyNonZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyNonZeroConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.nonZero();
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		ZoneOffset outsideRange = ZoneOffset.of("+08:00");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), outsideRange));
	}
	
	@Test
	void decodeHourConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+08:00")));
	}
	
	@Test
	void encodeKeyHourConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyHourConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.hour(builder -> builder.betweenOrEqual(-5, 5));
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> {
			if (value.getTotalSeconds() % 3600 != 0) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Offset must be on the hour (no minutes)");
		});
		ZoneOffset halfHourOffset = ZoneOffset.of("+05:30");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), halfHourOffset));
	}
	
	@Test
	void decodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> {
			if (value.getTotalSeconds() % 3600 != 0) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Offset must be on the hour (no minutes)");
		});
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("+05:30")));
	}
	
	@Test
	void encodeKeyCustomConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> {
			if (value.getTotalSeconds() % 3600 != 0) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Offset must be on the hour (no minutes)");
		});
		
		assertDoesNotThrow(() -> codec.encodeKey(ZoneOffset.of("+02:00")));
	}
	
	@Test
	void decodeKeyCustomConstraintValid() {
		Codec<ZoneOffset> codec = Codecs.ZONE_OFFSET.custom(value -> {
			if (value.getTotalSeconds() % 3600 != 0) throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Offset must be on the hour (no minutes)");
		});
		
		assertDoesNotThrow(() -> codec.decodeKey("+02:00"));
	}
}
