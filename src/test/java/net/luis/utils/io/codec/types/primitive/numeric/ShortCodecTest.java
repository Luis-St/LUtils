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
 * Test class for {@link ShortCodec}.<br>
 *
 * @author Luis-St
 */
class ShortCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		Short value = 1000;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as short"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (short) 1000);
		assertEquals(new JsonPrimitive((short) 1000), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (short) -1000);
		assertEquals(new JsonPrimitive((short) -1000), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (short) 0);
		assertEquals(new JsonPrimitive((short) 0), result);
	}
	
	@Test
	void encodeWithMaxValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Short.MAX_VALUE);
		assertEquals(new JsonPrimitive(Short.MAX_VALUE), result);
	}
	
	@Test
	void encodeWithMinValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Short.MIN_VALUE);
		assertEquals(new JsonPrimitive(Short.MIN_VALUE), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		String result = codec.encodeKey((short) 1000);
		assertEquals("1000", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		String result = codec.encodeKey((short) -1000);
		assertEquals("-1000", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive((short) 1000)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as short"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) 1000));
		assertEquals((short) 1000, result);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((short) -1000));
		assertEquals((short) -1000, result);
	}
	
	@Test
	void decodeWithMaxValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Short.MAX_VALUE));
		assertEquals(Short.MAX_VALUE, result);
	}
	
	@Test
	void decodeWithMinValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Short.MIN_VALUE));
		assertEquals(Short.MIN_VALUE, result);
	}
	
	@Test
	void decodeWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decodeKey("1000");
		assertEquals((short) 1000, result);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		Short result = codec.decodeKey("-1000");
		assertEquals((short) -1000, result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as short"));
	}
	
	@Test
	void decodeKeyWithOutOfRangeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Short> codec = new ShortCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("99999"));
		assertTrue(exception.getMessage().contains("Unable to decode key '99999' as short"));
	}
	
	@Test
	void toStringRepresentation() {
		ShortCodec codec = new ShortCodec();
		assertEquals("ShortCodec", codec.toString());
	}
}
