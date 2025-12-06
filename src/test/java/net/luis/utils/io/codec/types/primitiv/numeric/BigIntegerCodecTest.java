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

package net.luis.utils.io.codec.types.primitiv.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");

		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), value));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, value));
	}

	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null as big integer"));
	}

	@Test
	void encodeStartWithPositiveValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("12345678901234567890"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("-98765432109876543210");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("-98765432109876543210"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = BigInteger.ZERO;

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("0"), result.resultOrThrow());
	}

	@Test
	void encodeStartWithVeryLargeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("123456789012345678901234567890123456789012345678901234567890");

		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), value);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive("123456789012345678901234567890123456789012345678901234567890"), result.resultOrThrow());
	}

	@Test
	void encodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		assertThrows(NullPointerException.class, () -> codec.encodeKey(null));
	}

	@Test
	void encodeKeyWithValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("12345678901234567890");

		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("12345678901234567890", result.resultOrThrow());
	}

	@Test
	void encodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();
		BigInteger value = new BigInteger("-98765432109876543210");

		Result<String> result = codec.encodeKey(value);
		assertTrue(result.isSuccess());
		assertEquals("-98765432109876543210", result.resultOrThrow());
	}

	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, typeProvider.empty(), new JsonPrimitive("12345678901234567890")));
	}

	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as big integer"));
	}

	@Test
	void decodeStartWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("12345678901234567890"));
		assertTrue(result.isSuccess());
		assertEquals(new BigInteger("12345678901234567890"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("-98765432109876543210"));
		assertTrue(result.isSuccess());
		assertEquals(new BigInteger("-98765432109876543210"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithZero() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("0"));
		assertTrue(result.isSuccess());
		assertEquals(BigInteger.ZERO, result.resultOrThrow());
	}

	@Test
	void decodeStartWithVeryLargeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("123456789012345678901234567890123456789012345678901234567890"));
		assertTrue(result.isSuccess());
		assertEquals(new BigInteger("123456789012345678901234567890123456789012345678901234567890"), result.resultOrThrow());
	}

	@Test
	void decodeStartWithInvalidFormat() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode big integer"));
	}

	@Test
	void decodeStartWithNonString() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeStart(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertTrue(result.isError());
	}

	@Test
	void decodeKeyNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		assertThrows(NullPointerException.class, () -> codec.decodeKey(null));
	}

	@Test
	void decodeKeyWithValidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeKey("12345678901234567890");
		assertTrue(result.isSuccess());
		assertEquals(new BigInteger("12345678901234567890"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithNegativeValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeKey("-98765432109876543210");
		assertTrue(result.isSuccess());
		assertEquals(new BigInteger("-98765432109876543210"), result.resultOrThrow());
	}

	@Test
	void decodeKeyWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<BigInteger> codec = new BigIntegerCodec();

		Result<BigInteger> result = codec.decodeKey("invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode key 'invalid' as big integer"));
	}

	@Test
	void toStringRepresentation() {
		BigIntegerCodec codec = new BigIntegerCodec();
		assertEquals("BigIntegerCodec", codec.toString());
	}
}
