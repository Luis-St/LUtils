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
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DayOfWeekCodec}.<br>
 *
 * @author Luis-St
 */
class DayOfWeekCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.MONDAY;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), day));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, day));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as day of week"));
	}
	
	@Test
	void encodeWithMonday() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.MONDAY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), day);
		assertEquals(new JsonPrimitive("MONDAY"), result);
	}
	
	@Test
	void encodeWithTuesday() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.TUESDAY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), day);
		assertEquals(new JsonPrimitive("TUESDAY"), result);
	}
	
	@Test
	void encodeWithWednesday() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.WEDNESDAY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), day);
		assertEquals(new JsonPrimitive("WEDNESDAY"), result);
	}
	
	@Test
	void encodeWithSunday() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.SUNDAY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), day);
		assertEquals(new JsonPrimitive("SUNDAY"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithDay() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		DayOfWeek day = DayOfWeek.FRIDAY;
		
		String result = codec.encodeKey(day);
		assertEquals("FRIDAY", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("MONDAY")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as day of week"));
	}
	
	@Test
	void decodeWithValidMonday() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("MONDAY"));
		assertEquals(DayOfWeek.MONDAY, result);
	}
	
	@Test
	void decodeWithValidTuesday() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("TUESDAY"));
		assertEquals(DayOfWeek.TUESDAY, result);
	}
	
	@Test
	void decodeWithValidWednesday() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("WEDNESDAY"));
		assertEquals(DayOfWeek.WEDNESDAY, result);
	}
	
	@Test
	void decodeWithValidSunday() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("SUNDAY"));
		assertEquals(DayOfWeek.SUNDAY, result);
	}
	
	@Test
	void decodeWithInvalidDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID_DAY")));
		assertTrue(exception.getMessage().contains("Unable to decode day of week"));
	}
	
	@Test
	void decodeWithLowercaseDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("monday")));
		assertTrue(exception.getMessage().contains("Unable to decode day of week"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidDay() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decodeKey("FRIDAY");
		assertEquals(DayOfWeek.FRIDAY, result);
	}
	
	@Test
	void decodeKeyWithSaturday() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DayOfWeek result = codec.decodeKey("SATURDAY");
		assertEquals(DayOfWeek.SATURDAY, result);
	}
	
	@Test
	void decodeKeyWithInvalidDay() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<DayOfWeek> codec = new DayOfWeekCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("INVALID_DAY"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'INVALID_DAY' as day of week"));
	}
	
	@Test
	void toStringRepresentation() {
		DayOfWeekCodec codec = new DayOfWeekCodec();
		assertEquals("DayOfWeekCodec", codec.toString());
	}
}
