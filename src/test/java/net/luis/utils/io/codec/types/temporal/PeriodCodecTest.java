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

import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PeriodCodec}.<br>
 *
 * @author Luis-St
 */
class PeriodCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofYears(1);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), period));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as period"));
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ZERO;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), period);
		assertEquals(new JsonPrimitive("0d"), result);
	}
	
	@Test
	void encodeWithYears() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofYears(2);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), period);
		assertEquals(new JsonPrimitive("2y"), result);
	}
	
	@Test
	void encodeWithMonths() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofMonths(6);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), period);
		assertEquals(new JsonPrimitive("6m"), result);
	}
	
	@Test
	void encodeWithDays() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.ofDays(15);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), period);
		assertEquals(new JsonPrimitive("15d"), result);
	}
	
	@Test
	void encodeWithComplexPeriod() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		Period period = Period.of(1, 6, 15);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), period);
		assertEquals(new JsonPrimitive("1y 6m 15d"), result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("1y")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as period"));
	}
	
	@Test
	void decodeWithZero() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Period result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0d"));
		assertEquals(Period.ZERO, result);
	}
	
	@Test
	void decodeWithYears() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Period result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("2y"));
		assertEquals(Period.ofYears(2), result);
	}
	
	@Test
	void decodeWithMonths() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Period result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("6m"));
		assertEquals(Period.ofMonths(6), result);
	}
	
	@Test
	void decodeWithDays() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Period result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("15d"));
		assertEquals(Period.ofDays(15), result);
	}
	
	@Test
	void decodeWithComplexPeriod() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		Period result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1y 6m 15d"));
		assertEquals(Period.of(1, 6, 15), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("invalid-period")));
		assertTrue(exception.getMessage().contains("Unable to decode period"));
		assertTrue(exception.getMessage().contains("Invalid period format"));
	}
	
	@Test
	void decodeWithInvalidUnit() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("5x")));
		assertTrue(exception.getMessage().contains("Unknown time unit"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Period> codec = new PeriodCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void toStringRepresentation() {
		PeriodCodec codec = new PeriodCodec();
		assertEquals("PeriodCodec", codec.toString());
	}
}
