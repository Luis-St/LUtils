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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BigDecimalCodec}.<br>
 *
 * @author Luis-St
 */
class BigDecimalCodecTest {
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, value));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null as big decimal"));
	}
	
	@Test
	void encodeWithPositiveValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("123.456"), result);
	}
	
	@Test
	void encodeWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("-987.654");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("-987.654"), result);
	}
	
	@Test
	void encodeWithZero() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = BigDecimal.ZERO;
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("0"), result);
	}
	
	@Test
	void encodeWithVeryLargeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123456789012345678901234567890.123456789012345678901234567890");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("123456789012345678901234567890.123456789012345678901234567890"), result);
	}
	
	@Test
	void encodeWithScientificNotation() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("1.23E+10");
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), value);
		assertEquals(new JsonPrimitive("12300000000"), result);
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		String result = codec.encodeKey(value);
		assertEquals("123.456", result);
	}
	
	@Test
	void encodeKeyWithNegativeValue() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("-987.654");
		
		String result = codec.encodeKey(value);
		assertEquals("-987.654", result);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive("123.456")));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as big decimal"));
	}
	
	@Test
	void decodeWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("123.456"));
		assertEquals(new BigDecimal("123.456"), result);
	}
	
	@Test
	void decodeWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("-987.654"));
		assertEquals(new BigDecimal("-987.654"), result);
	}
	
	@Test
	void decodeWithZero() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertEquals(BigDecimal.ZERO, result);
	}
	
	@Test
	void decodeWithVeryLargeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("123456789012345678901234567890.123456789012345678901234567890"));
		assertEquals(new BigDecimal("123456789012345678901234567890.123456789012345678901234567890"), result);
	}
	
	@Test
	void decodeWithScientificNotation() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("1.23E+10"));
		assertEquals(new BigDecimal("1.23E+10"), result);
	}
	
	@Test
	void decodeWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
		assertTrue(exception.getMessage().contains("Unable to decode big decimal"));
	}
	
	@Test
	void decodeWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decodeKey("123.456");
		assertEquals(new BigDecimal("123.456"), result);
	}
	
	@Test
	void decodeKeyWithNegativeValue() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		BigDecimal result = codec.decodeKey("-987.654");
		assertEquals(new BigDecimal("-987.654"), result);
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decodeKey("invalid"));
		assertTrue(exception.getMessage().contains("Unable to decode key 'invalid' as big decimal"));
	}
	
	@Test
	void toStringRepresentation() {
		BigDecimalCodec codec = new BigDecimalCodec();
		assertEquals("BigDecimalCodec", codec.toString());
	}
}
