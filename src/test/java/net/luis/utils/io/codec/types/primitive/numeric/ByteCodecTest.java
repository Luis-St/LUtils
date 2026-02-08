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
 * Test class for {@link ByteCodec}.<br>
 *
 * @author Luis-St
 */
class ByteCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		Byte value = 42;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as byte"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (byte) 42);
		assertEquals(new JsonPrimitive((byte) 42), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (byte) -42);
		assertEquals(new JsonPrimitive((byte) -42), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), (byte) 0);
		assertEquals(new JsonPrimitive((byte) 0), result);
	}
	
	@Test
	void encodeWithMaxValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Byte.MAX_VALUE);
		assertEquals(new JsonPrimitive(Byte.MAX_VALUE), result);
	}
	
	@Test
	void encodeWithMinValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), Byte.MIN_VALUE);
		assertEquals(new JsonPrimitive(Byte.MIN_VALUE), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		String result = codec.encodeKey((byte) 42);
		assertEquals("42", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		String result = codec.encodeKey((byte) -42);
		assertEquals("-42", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive((byte) 42)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as byte"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) 42));
		assertEquals((byte) 42, result);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive((byte) -42));
		assertEquals((byte) -42, result);
	}
	
	@Test
	void decodeWithMaxValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Byte.MAX_VALUE));
		assertEquals(Byte.MAX_VALUE, result);
	}
	
	@Test
	void decodeWithMinValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(Byte.MIN_VALUE));
		assertEquals(Byte.MIN_VALUE, result);
	}
	
	@Test
	void decodeWithNonNumber() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not a number")));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decodeKey("42");
		assertEquals((byte) 42, result);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		Byte result = codec.decodeKey("-42");
		assertEquals((byte) -42, result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as byte"));
	}
	
	@Test
	void decodeKeyWithOutOfRangeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Byte> codec = new ByteCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("999"));
		assertTrue(exception.getMessage().contains("Unable to decode key '999' as byte"));
	}
	
	@Test
	void toStringRepresentation() {
		ByteCodec codec = new ByteCodec();
		assertEquals("ByteCodec", codec.toString());
	}
}
