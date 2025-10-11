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

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InstantCodec}.<br>
 *
 * @author Luis-St
 */
class InstantCodecTest {

	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.now();

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), instant));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, instant));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as instant"));
	}

	@Test
	void encodeStartWithValidInstant() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.parse("2025-01-15T10:30:00Z");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), instant);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00Z"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithEpoch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.EPOCH;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), instant);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("1970-01-01T00:00:00Z"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();
		Instant instant = Instant.parse("2025-01-15T10:30:00.123456789Z");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), instant);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("2025-01-15T10:30:00.123456789Z"), result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive("2025-01-15T10:30:00Z")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as instant"));
	}

	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, new JsonPrimitive("2025-01-15T10:30:00Z"));
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2025-01-15T10:30:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithEpoch() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, new JsonPrimitive("1970-01-01T00:00:00Z"));
		assertTrue(result.isSuccess());
		assertEquals(Instant.EPOCH, result.resultOrThrow());
	}

	@Test
	void decodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, new JsonPrimitive("2025-01-15T10:30:00.123456789Z"));
		assertTrue(result.isSuccess());
		assertEquals(Instant.parse("2025-01-15T10:30:00.123456789Z"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, new JsonPrimitive("invalid-instant"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode instant"));
		assertTrue(result.errorOrThrow().contains("Unable to parse instant"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Instant> codec = new InstantCodec();

		Result<Instant> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void toStringRepresentation() {
		InstantCodec codec = new InstantCodec();
		assertEquals("InstantCodec", codec.toString());
	}
}
