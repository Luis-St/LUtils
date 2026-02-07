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

package net.luis.utils.io.codec.types.primitive;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BooleanCodec}.<br>
 *
 * @author Luis-St
 */
class BooleanCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		Boolean value = true;
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as boolean"));
	}
	
	@Test
	void encodeWithTrue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), true);
		assertEquals(new JsonPrimitive(true), result);
	}
	
	@Test
	void encodeWithFalse() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), false);
		assertEquals(new JsonPrimitive(false), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithTrue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		String result = codec.encodeKey(true);
		assertEquals("true", result);
	}
	
	@Test
	void encodeKeyWithFalse() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		String result = codec.encodeKey(false);
		assertEquals("false", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(true)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null as boolean"));
	}
	
	@Test
	void decodeWithTrue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Boolean result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertEquals(true, result);
	}
	
	@Test
	void decodeWithFalse() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Boolean result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(false));
		assertEquals(false, result);
	}
	
	@Test
	void decodeWithNonBoolean() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithTrue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Boolean result = codec.decodeKey("true");
		assertEquals(true, result);
	}
	
	@Test
	void decodeKeyWithFalse() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		Boolean result = codec.decodeKey("false");
		assertEquals(false, result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Boolean> codec = new BooleanCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as boolean"));
	}
	
	@Test
	void toStringRepresentation() {
		BooleanCodec codec = new BooleanCodec();
		assertEquals("BooleanCodec", codec.toString());
	}
}
