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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartWithValidAfterConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2023-06-15T12:30:00Z"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithValidBeforeConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBefore(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBetweenConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant target = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withEqualTo(target));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), target);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant value1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant value2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withIn(Set.of(value1, value2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value1);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotEqualToConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded = Instant.parse("2023-06-15T12:30:00Z");
		Instant value = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidNotInConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant excluded2 = Instant.parse("2023-07-20T15:45:00Z");
		Instant value = Instant.parse("2023-08-25T18:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidAfterOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfterOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartWithValidBeforeOrEqualConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBeforeOrEqual(threshold));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), threshold);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeKeyWithValidConstraint() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant value = Instant.parse("2023-06-15T12:30:00Z");
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("2023-06-15T12:30:00Z", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Result<Instant> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2023-06-15T12:30:00Z"));
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2023-06-15T12:30:00Z"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithValidConstraint() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Result<Instant> result = codec.decodeKey("2023-06-15T12:30:00Z");
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2023-06-15T12:30:00Z"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithCustomConstraint() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withCustom(value -> {
			if (value.getEpochSecond() % 2 == 0) {
				return Result.success(null);
			}
			return Result.error("Instant epoch second must be even");
		}));
		Instant value = Instant.ofEpochSecond(1000);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void encodeStartAfterConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant valueBefore = Instant.parse("2019-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBeforeConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBefore(threshold));
		Instant valueAfter = Instant.parse("2023-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueAfter);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooEarly() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant valueTooEarly = Instant.parse("2019-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooEarly);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartBetweenConstraintViolationTooLate() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant after = Instant.parse("2020-01-01T00:00:00Z");
		Instant before = Instant.parse("2025-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withBetween(after, before));
		Instant valueTooLate = Instant.parse("2026-06-15T12:30:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), valueTooLate);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant target = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withEqualTo(target));
		Instant differentValue = Instant.parse("2023-07-20T15:45:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), differentValue);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant value1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant value2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withIn(Set.of(value1, value2)));
		Instant notInSet = Instant.parse("2023-08-25T18:00:00Z");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), notInSet);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotEqualToConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded = Instant.parse("2023-06-15T12:30:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotEqualTo(excluded));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartNotInConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant excluded1 = Instant.parse("2023-06-15T12:30:00Z");
		Instant excluded2 = Instant.parse("2023-07-20T15:45:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withNotIn(Set.of(excluded1, excluded2)));
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), excluded1);
		assertTrue(result.isError());
	}
	
	@Test
	void encodeKeyConstraintViolation() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		Instant valueBefore = Instant.parse("2019-06-15T12:30:00Z");
		
		Result<String> result = codec.encodeKey(valueBefore);
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Result<Instant> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("2019-06-15T12:30:00Z"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyConstraintViolation() {
		Instant threshold = Instant.parse("2020-01-01T00:00:00Z");
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withAfter(threshold));
		
		Result<Instant> result = codec.decodeKey("2019-06-15T12:30:00Z");
		assertTrue(result.isError());
	}
	
	@Test
	void encodeStartCustomConstraintViolation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = Codecs.INSTANT.apply(config -> config.withCustom(value -> {
			if (value.getEpochSecond() % 2 == 0) {
				return Result.success(null);
			}
			return Result.error("Instant epoch second must be even");
		}));
		Instant value = Instant.ofEpochSecond(1001);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isError());
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
