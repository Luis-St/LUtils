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

package net.luis.utils.io.codec.types.primitive.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LongCodec}.<br>
 *
 * @author Luis-St
 */
class LongCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		Long value = 9999999999L;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as long"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 9999999999L);
		assertEquals(new JsonPrimitive(9999999999L), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), -9999999999L);
		assertEquals(new JsonPrimitive(-9999999999L), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 0L);
		assertEquals(new JsonPrimitive(0L), result);
	}
	
	@Test
	void encodeWithMaxValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Long.MAX_VALUE);
		assertEquals(new JsonPrimitive(Long.MAX_VALUE), result);
	}
	
	@Test
	void encodeWithMinValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Long.MIN_VALUE);
		assertEquals(new JsonPrimitive(Long.MIN_VALUE), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		String result = codec.encodeKey(9999999999L);
		assertEquals("9999999999", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		String result = codec.encodeKey(-9999999999L);
		assertEquals("-9999999999", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(9999999999L)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as long"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(9999999999L));
		assertEquals(9999999999L, result);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(-9999999999L));
		assertEquals(-9999999999L, result);
	}
	
	@Test
	void decodeWithMaxValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Long.MAX_VALUE));
		assertEquals(Long.MAX_VALUE, result);
	}
	
	@Test
	void decodeWithMinValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Long.MIN_VALUE));
		assertEquals(Long.MIN_VALUE, result);
	}
	
	@Test
	void decodeWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decodeKey("9999999999");
		assertEquals(9999999999L, result);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		Long result = codec.decodeKey("-9999999999");
		assertEquals(-9999999999L, result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as long"));
	}
	
	@Test
	void decodeKeyWithOutOfRangeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Long> codec = new LongCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("99999999999999999999999999999"));
		assertTrue(exception.getMessage().contains("Unable to decode key '99999999999999999999999999999' as long"));
	}
	
	@Test
	void toStringRepresentation() {
		LongCodec codec = new LongCodec();
		assertEquals("LongCodec", codec.toString());
	}
}
