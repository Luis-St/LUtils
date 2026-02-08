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

package net.luis.utils.io.codec.types.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InstantCodec} with constraints.<br>
 *
 * @author Luis-St
 */
class ConstrainedInstantCodecTest {
	
	@Test
	void encodeWithValidAfterConstraint() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("2023-06-15T12:30:00Z"), result);
	}
	
	@Test
	void encodeWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBefore(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant target = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withEqualTo(target));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), target));
	}
	
	@Test
	void encodeWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant value1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant value2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withIn(Set.of(value1, value2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value1));
	}
	
	@Test
	void encodeWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded = Instant.parse("2023-06-15T12:30:00Z");
		Instant value = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotEqualTo(excluded));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant excluded2 = Instant.parse("2023-07-20T15:45:00Z");
		Instant value = Instant.parse("2023-08-25T18:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfterOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBeforeOrEqual(threshold));
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), threshold));
	}
	
	@Test
	void encodeKeyWithValidConstraint() throws EncoderException {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		String result = codec.encodeKey(value);
		assertEquals("2023-06-15T12:30:00Z", result);
	}
	
	@Test
	void decodeWithValidConstraint() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Instant result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z"));
		assertEquals(Instant.parse("2023-06-15T12:30:00Z"), result);
	}
	
	@Test
	void decodeKeyWithValidConstraint() throws DecoderException {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Instant result = codec.decodeKey("2023-06-15T12:30:00Z");
		assertEquals(Instant.parse("2023-06-15T12:30:00Z"), result);
	}
	
	@Test
	void encodeWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withCustom(value -> {
			if (value.getEpochSecond() % 2 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Instant epoch second must be even");
		}));
		Instant value = Instant.ofEpochSecond(1000);
		
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void encodeAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant valueBefore = Instant.parse("2019-06-15T12:30:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueBefore));
	}
	
	@Test
	void encodeBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBefore(threshold));
		Instant valueAfter = Instant.parse("2023-06-15T12:30:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueAfter));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant valueTooEarly = Instant.parse("2019-06-15T12:30:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooEarly));
	}
	
	@Test
	void encodeBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant valueTooLate = Instant.parse("2026-06-15T12:30:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), valueTooLate));
	}
	
	@Test
	void encodeEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant target = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withEqualTo(target));
		Instant differentValue = Instant.parse("2023-07-20T15:45:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), differentValue));
	}
	
	@Test
	void encodeInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant value1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant value2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withIn(Set.of(value1, value2)));
		Instant notInSet = Instant.parse("2023-08-25T18:00:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), notInSet));
	}
	
	@Test
	void encodeNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotEqualTo(excluded));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded));
	}
	
	@Test
	void encodeNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant excluded2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), excluded1));
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant valueBefore = Instant.parse("2019-06-15T12:30:00Z");
		
		assertThrows(EncoderException.class, () -> codec.encodeKey(valueBefore));
	}
	
	@Test
	void decodeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:30:00Z")));
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		assertThrows(DecoderException.class, () -> codec.decodeKey("2019-06-15T12:30:00Z"));
	}
	
	@Test
	void encodeCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withCustom(value -> {
			if (value.getEpochSecond() % 2 == 0) {
				return;
			}
			throw new net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException("Instant epoch second must be even");
		}));
		Instant value = Instant.ofEpochSecond(1001);
		
		assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), value));
	}
	
	@Test
	void toStringWithConstraints() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		String toString = codec.toString();
		assertTrue(toString.contains("Constrained"));
	}
	
	@Test
	void toStringWithoutConstraints() {
		Codec<Instant> codec = Codecs.INSTANT;
		
		assertEquals("InstantCodec", codec.toString());
	}
}
