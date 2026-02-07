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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofHours(1);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), duration));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as duration"));
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ZERO;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), duration);
		assertEquals(new JsonPrimitive("0s"), result);
	}
	
	@Test
	void encodeWithSeconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofSeconds(5);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), duration);
		assertEquals(new JsonPrimitive("5s"), result);
	}
	
	@Test
	void encodeWithComplexDuration() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofDays(1).plusHours(2).plusMinutes(30);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), duration);
		assertEquals(new JsonPrimitive("1d 2h 30m"), result);
	}
	
	@Test
	void encodeWithMilliseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofMillis(500);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), duration);
		assertEquals(new JsonPrimitive("500ms"), result);
	}
	
	@Test
	void encodeWithNanoseconds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		Duration duration = Duration.ofNanos(123);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), duration);
		assertEquals(new JsonPrimitive("123ns"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("5s")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as duration"));
	}
	
	@Test
	void decodeWithZero() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0s"));
		assertEquals(Duration.ZERO, result);
	}
	
	@Test
	void decodeWithSeconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5s"));
		assertEquals(Duration.ofSeconds(5), result);
	}
	
	@Test
	void decodeWithComplexDuration() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1d 2h 30m"));
		assertEquals(Duration.ofDays(1).plusHours(2).plusMinutes(30), result);
	}
	
	@Test
	void decodeWithMilliseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("500ms"));
		assertEquals(Duration.ofMillis(500), result);
	}
	
	@Test
	void decodeWithNanoseconds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("123ns"));
		assertEquals(Duration.ofNanos(123), result);
	}
	
	@Test
	void decodeWithAllUnits() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		Duration result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1y 2mo 3w 4d 5h 6m 7s 800ms 900ns"));
		Duration expected = Duration.ofSeconds(365 * 86400L + 2 * 30 * 86400L + 3 * 7 * 86400L + 4 * 86400L + 5 * 3600L + 6 * 60L + 7, 800_000_000L + 900L);
		assertEquals(expected, result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-duration")));
		assertTrue(exception.getMessage().contains("Unable to decode duration"));
		assertTrue(exception.getMessage().contains("Invalid duration format"));
	}
	
	@Test
	void decodeWithInvalidUnit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5x")));
		assertTrue(exception.getMessage().contains("Unknown time unit"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Duration> codec = new DurationCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		DurationCodec codec = new DurationCodec();
		assertEquals("DurationCodec", codec.toString());
	}
}
