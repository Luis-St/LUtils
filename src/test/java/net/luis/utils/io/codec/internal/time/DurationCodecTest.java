/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.internal.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DurationCodec}.<br>
 *
 * @author Luis-St
 */
class DurationCodecTest {

	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofHours(1);

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), duration));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as duration"));
	}

	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ZERO;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), duration);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0s"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithSeconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofSeconds(5);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), duration);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("5s"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithComplexDuration() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofDays(1).plusHours(2).plusMinutes(30);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), duration);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("1d 2h 30m"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithMilliseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofMillis(500);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), duration);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("500ms"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofNanos(123);

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), duration);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("123ns"), result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("5s")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as duration"));
	}

	@Test
	void decodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("0s"));
		assertTrue(result.isSuccess());
		assertEquals(Duration.ZERO, result.resultOrThrow());
	}

	@Test
	void decodeStartWithSeconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("5s"));
		assertTrue(result.isSuccess());
		assertEquals(Duration.ofSeconds(5), result.resultOrThrow());
	}

	@Test
	void decodeStartWithComplexDuration() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("1d 2h 30m"));
		assertTrue(result.isSuccess());
		assertEquals(Duration.ofDays(1).plusHours(2).plusMinutes(30), result.resultOrThrow());
	}

	@Test
	void decodeStartWithMilliseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("500ms"));
		assertTrue(result.isSuccess());
		assertEquals(Duration.ofMillis(500), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("123ns"));
		assertTrue(result.isSuccess());
		assertEquals(Duration.ofNanos(123), result.resultOrThrow());
	}

	@Test
	void decodeStartWithAllUnits() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("1y 2mo 3w 4d 5h 6m 7s 800ms 900ns"));
		assertTrue(result.isSuccess());
		Duration expected = Duration.ofSeconds(365 * 86400L + 2 * 30 * 86400L + 3 * 7 * 86400L + 4 * 86400L + 5 * 3600L + 6 * 60L + 7, 800_000_000L + 900L);
		assertEquals(expected, result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("invalid-duration"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode duration"));
		assertTrue(result.errorOrThrow().contains("Invalid duration format"));
	}

	@Test
	void decodeStartWithInvalidUnit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive("5x"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unknown time unit"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();

		Result<Duration> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void toStringRepresentation() {
		DurationCodec codec = new DurationCodec();
		assertEquals("DurationCodec", codec.toString());
	}
}
