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

import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MonthCodec}.<br>
 *
 * @author Luis-St
 */
class MonthCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JANUARY;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), month));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, month));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as month"));
	}
	
	@Test
	void encodeWithJanuary() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JANUARY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), month);
		assertEquals(new JsonPrimitive("JANUARY"), result);
	}
	
	@Test
	void encodeWithFebruary() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.FEBRUARY;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), month);
		assertEquals(new JsonPrimitive("FEBRUARY"), result);
	}
	
	@Test
	void encodeWithJune() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.JUNE;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), month);
		assertEquals(new JsonPrimitive("JUNE"), result);
	}
	
	@Test
	void encodeWithDecember() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.DECEMBER;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), month);
		assertEquals(new JsonPrimitive("DECEMBER"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithMonth() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		Month month = Month.MARCH;
		
		String result = codec.encodeKey(month);
		assertEquals("MARCH", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("JANUARY")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as month"));
	}
	
	@Test
	void decodeWithValidJanuary() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("JANUARY"));
		assertEquals(Month.JANUARY, result);
	}
	
	@Test
	void decodeWithValidFebruary() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("FEBRUARY"));
		assertEquals(Month.FEBRUARY, result);
	}
	
	@Test
	void decodeWithValidJune() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("JUNE"));
		assertEquals(Month.JUNE, result);
	}
	
	@Test
	void decodeWithValidDecember() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("DECEMBER"));
		assertEquals(Month.DECEMBER, result);
	}
	
	@Test
	void decodeWithInvalidMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("INVALID_MONTH")));
		assertTrue(exception.getMessage().contains("Unable to decode month"));
	}
	
	@Test
	void decodeWithLowercaseMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("january")));
		assertTrue(exception.getMessage().contains("Unable to decode month"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidMonth() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decodeKey("APRIL");
		assertEquals(Month.APRIL, result);
	}
	
	@Test
	void decodeKeyWithAugust() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		Month result = codec.decodeKey("AUGUST");
		assertEquals(Month.AUGUST, result);
	}
	
	@Test
	void decodeKeyWithInvalidMonth() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Month> codec = new MonthCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("INVALID_MONTH"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'INVALID_MONTH' as month"));
	}
	
	@Test
	void toStringRepresentation() {
		MonthCodec codec = new MonthCodec();
		assertEquals("MonthCodec", codec.toString());
	}
}
