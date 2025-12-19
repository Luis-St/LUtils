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

package net.luis.utils.io.codec.types.time;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OffsetTimeCodec}.<br>
 *
 * @author Luis-St
 */
class OffsetTimeCodecTest {

	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.now();

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), time));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, time));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as offset time"));
	}

	@Test
	void encodeStartWithValidTime() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00+01:00");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30+01:00"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithUTCOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00Z");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30Z"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00-05:00");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30-05:00"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00.123456789+02:00");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), time);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("10:30:00.123456789+02:00"), result.resultOrThrow());
	}

	@Test
	void encodeKeyNullChecks() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}

	@Test
	void encodeKeyWithValidTime() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();
		OffsetTime time = OffsetTime.parse("10:30:00+01:00");

		Result<String> result = codec.encodeKey(time);
		assertTrue(result.isSuccess());
		assertEquals("10:30+01:00", result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("10:30:00+01:00")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as offset time"));
	}

	@Test
	void decodeStartWithValidString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00+01:00"));
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.parse("10:30:00+01:00"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithUTCOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00Z"));
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.parse("10:30:00Z"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNegativeOffset() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00-05:00"));
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.parse("10:30:00-05:00"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNanoseconds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("10:30:00.123456789+02:00"));
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.parse("10:30:00.123456789+02:00"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-time"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode offset time"));
		assertTrue(result.errorOrThrow().contains("Unable to offset local time"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeKeyNullChecks() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}

	@Test
	void decodeKeyWithValidString() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeKey("10:30:00+01:00");
		assertTrue(result.isSuccess());
		assertEquals(OffsetTime.parse("10:30:00+01:00"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithInvalidString() {
		Codec<OffsetTime> codec = new OffsetTimeCodec();

		Result<OffsetTime> result = codec.decodeKey("invalid-time");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid-time' as offset time"));
	}

	@Test
	void toStringRepresentation() {
		OffsetTimeCodec codec = new OffsetTimeCodec();
		assertEquals("OffsetTimeCodec", codec.toString());
	}
}
