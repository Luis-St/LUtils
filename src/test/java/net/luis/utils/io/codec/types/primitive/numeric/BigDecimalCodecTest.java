/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as big decimal"));
	}
	
	@Test
	void encodeStartWithPositiveValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("123.456"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("-987.654");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("-987.654"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = BigDecimal.ZERO;
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithVeryLargeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123456789012345678901234567890.123456789012345678901234567890");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("123456789012345678901234567890.123456789012345678901234567890"), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithScientificNotation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("1.23E+10");
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("12300000000"), result.resultOrThrow());
	}
	
	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}
	
	@Test
	void encodeKeyWithValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("123.456");
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("123.456", result.resultOrThrow());
	}
	
	@Test
	void encodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		BigDecimal value = new BigDecimal("-987.654");
		
		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("-987.654", result.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("123.456")));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as big decimal"));
	}
	
	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("123.456"));
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("123.456"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-987.654"));
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("-987.654"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
		assertEquals(BigDecimal.ZERO, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithVeryLargeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("123456789012345678901234567890.123456789012345678901234567890"));
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("123456789012345678901234567890.123456789012345678901234567890"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithScientificNotation() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("1.23E+10"));
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("1.23E+10"), result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode big decimal"));
	}
	
	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}
	
	@Test
	void decodeKeyWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeKey("123.456");
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("123.456"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeKey("-987.654");
		assertTrue(result.isSuccess());
		assertEquals(new BigDecimal("-987.654"), result.resultOrThrow());
	}
	
	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigDecimal> codec = new BigDecimalCodec();
		
		Result<BigDecimal> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as big decimal"));
	}
	
	@Test
	void toStringRepresentation() {
		BigDecimalCodec codec = new BigDecimalCodec();
		assertEquals("BigDecimalCodec", codec.toString());
	}
}
