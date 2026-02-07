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

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigIntegerCodec}.<br>
 *
 * @author Luis-St
 */
class BigIntegerCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as big integer"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("12345678901234567890"), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("-98765432109876543210");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("-98765432109876543210"), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = BigInteger.ZERO;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("0"), result);
	}
	
	@Test
	void encodeWithVeryLargeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("123456789012345678901234567890123456789012345678901234567890");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("123456789012345678901234567890123456789012345678901234567890"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");
		
		String result = codec.encodeKey(value);
		assertEquals("12345678901234567890", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("-98765432109876543210");
		
		String result = codec.encodeKey(value);
		assertEquals("-98765432109876543210", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("12345678901234567890")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as big integer"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("12345678901234567890"));
		assertEquals(new BigInteger("12345678901234567890"), result);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-98765432109876543210"));
		assertEquals(new BigInteger("-98765432109876543210"), result);
	}
	
	@Test
	void decodeWithZero() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertEquals(BigInteger.ZERO, result);
	}
	
	@Test
	void decodeWithVeryLargeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("123456789012345678901234567890123456789012345678901234567890"));
		assertEquals(new BigInteger("123456789012345678901234567890123456789012345678901234567890"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
		assertTrue(exception.getMessage().contains("Unable to decode big integer"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decodeKey("12345678901234567890");
		assertEquals(new BigInteger("12345678901234567890"), result);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		BigInteger result = codec.decodeKey("-98765432109876543210");
		assertEquals(new BigInteger("-98765432109876543210"), result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as big integer"));
	}
	
	@Test
	void toStringRepresentation() {
		BigIntegerCodec codec = new BigIntegerCodec();
		assertEquals("BigIntegerCodec", codec.toString());
	}
}
