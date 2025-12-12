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

package net.luis.utils.io.codec.internal.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyCodec}.<br>
 *
 * @author Luis-St
 */
class AnyCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new AnyCodec<>((List<Codec<? extends Number>>) null));
		assertThrows(IllegalArgumentException.class, () -> new AnyCodec<>(Collections.emptyList()));
		assertThrows(IllegalArgumentException.class, () -> new AnyCodec<>(List.of(INTEGER)));
		assertThrows(NullPointerException.class, () -> new AnyCodec<>(List.of(INTEGER, null, DOUBLE)));
		assertDoesNotThrow(() -> new AnyCodec<>(List.of(INTEGER, DOUBLE)));
	}
	
	@Test
	void constructorVarargs() {
		assertThrows(NullPointerException.class, () -> new AnyCodec<>((Codec<? extends Number>[]) null));
		assertThrows(IllegalArgumentException.class, () -> new AnyCodec<>(new Codec[0]));
		assertThrows(IllegalArgumentException.class, () -> new AnyCodec<>(INTEGER));
		assertThrows(NullPointerException.class, () -> new AnyCodec<>(INTEGER, null));
		assertDoesNotThrow(() -> new AnyCodec<>(INTEGER, DOUBLE));
	}
	
	@Test
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 42));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 42));
	}
	
	@Test
	void encodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to encode null value as any"));
	}
	
	@Test
	void encodeStartFirstCodecSucceeds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartSecondCodecSucceeds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<? extends Number> restrictedIntCodec = INTEGER.validate(i -> {
			if (i > 100) {
				return Result.success(i);
			}
			return Result.error("Value too small");
		});
		Codec<Number> codec = new AnyCodec<>(restrictedIntCodec, DOUBLE);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(3.14), result.resultOrThrow());
	}
	
	@Test
	void encodeStartAllCodecsFail() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec1 = STRING.union("a", "b");
		Codec<String> codec2 = STRING.union("c", "d");
		Codec<String> codec = new AnyCodec<>(codec1, codec2);
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), "invalid");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("All codecs failed"));
		assertTrue(result.errorOrThrow().contains("invalid"));
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> numberCodec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		Result<JsonElement> intResult = numberCodec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(intResult.isSuccess());
		
		Result<JsonElement> doubleResult = numberCodec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(doubleResult.isSuccess());
		
		Result<JsonElement> longResult = numberCodec.encodeStart(typeProvider, typeProvider.empty(), 1000L);
		assertTrue(longResult.isSuccess());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(42)));
	}
	
	@Test
	void decodeStartWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		Result<Number> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Unable to decode null value as any"));
	}
	
	@Test
	void decodeStartFirstCodecSucceeds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		Result<Number> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartSecondCodecSucceeds() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec1 = STRING.validate(s -> {
			if (s.startsWith("a")) {
				return Result.success(s);
			}
			return Result.error("Must start with 'a'");
		});
		Codec<String> codec = new AnyCodec<>(codec1, STRING);
		
		Result<String> result = codec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(result.isSuccess());
		assertEquals("hello", result.resultOrThrow());
	}
	
	@Test
	void decodeStartAllCodecsFail() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> restrictedCodec1 = INTEGER.union(1, 2, 3);
		Codec<Integer> restrictedCodec2 = INTEGER.union(4, 5, 6);
		Codec<Integer> codec = new AnyCodec<>(restrictedCodec1, restrictedCodec2);
		
		Result<Integer> result = codec.decodeStart(typeProvider, new JsonPrimitive(10));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("All codecs failed"));
	}
	
	@Test
	void decodeStartWithInvalidType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		Result<Number> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> shortCodec = STRING.validate(s -> {
			if (s.length() <= 3) {
				return Result.success(s);
			}
			return Result.error("Too long");
		});
		
		Codec<String> longCodec = STRING.validate(s -> {
			if (s.length() > 3) {
				return Result.success(s);
			}
			return Result.error("Too short");
		});
		
		Codec<String> anyCodec = new AnyCodec<>(shortCodec, longCodec);
		
		Result<String> shortResult = anyCodec.decodeStart(typeProvider, new JsonPrimitive("abc"));
		assertTrue(shortResult.isSuccess());
		assertEquals("abc", shortResult.resultOrThrow());
		
		Result<String> longResult = anyCodec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(longResult.isSuccess());
		assertEquals("hello", longResult.resultOrThrow());
	}
	
	@Test
	void roundTripEncodingDecoding() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> codec1 = STRING.union("red", "green", "blue");
		Codec<String> codec2 = STRING.union("small", "medium", "large");
		Codec<String> anyCodec = new AnyCodec<>(codec1, codec2);
		
		String color = "red";
		Result<JsonElement> encodedColor = anyCodec.encodeStart(typeProvider, typeProvider.empty(), color);
		assertTrue(encodedColor.isSuccess());
		Result<String> decodedColor = anyCodec.decodeStart(typeProvider, encodedColor.resultOrThrow());
		assertTrue(decodedColor.isSuccess());
		assertEquals(color, decodedColor.resultOrThrow());
		
		String size = "large";
		Result<JsonElement> encodedSize = anyCodec.encodeStart(typeProvider, typeProvider.empty(), size);
		assertTrue(encodedSize.isSuccess());
		Result<String> decodedSize = anyCodec.decodeStart(typeProvider, encodedSize.resultOrThrow());
		assertTrue(decodedSize.isSuccess());
		assertEquals(size, decodedSize.resultOrThrow());
	}
	
	@Test
	void codecOrderMatters() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> codec1 = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Number> codec2 = new AnyCodec<>(DOUBLE, INTEGER);
		
		Result<Number> result1 = codec1.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result1.isSuccess());
		assertInstanceOf(Integer.class, result1.resultOrThrow());
		
		Result<Number> result2 = codec2.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result2.isSuccess());
	}
	
	@Test
	void factoryMethodFromCodecInterface() {
		Codec<Number> codec = Codec.any(INTEGER, DOUBLE, LONG);
		assertNotNull(codec);
		assertInstanceOf(AnyCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void factoryMethodWithList() {
		List<Codec<? extends Number>> codecs = List.of(INTEGER, DOUBLE, LONG);
		Codec<Number> codec = Codec.any(codecs);
		assertNotNull(codec);
		assertInstanceOf(AnyCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Result<JsonElement> result = codec.encodeStart(typeProvider, typeProvider.empty(), 3.14);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void polymorphicPaymentMethodExample() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> creditCardCodec = STRING.validate(s -> {
			if (s.startsWith("cc:")) {
				return Result.success(s);
			}
			return Result.error("Not a credit card");
		});
		
		Codec<String> paypalCodec = STRING.validate(s -> {
			if (s.startsWith("paypal:")) {
				return Result.success(s);
			}
			return Result.error("Not a PayPal payment");
		});
		
		Codec<String> bankTransferCodec = STRING.validate(s -> {
			if (s.startsWith("bank:")) {
				return Result.success(s);
			}
			return Result.error("Not a bank transfer");
		});
		
		Codec<String> paymentCodec = new AnyCodec<>(creditCardCodec, paypalCodec, bankTransferCodec);
		
		Result<String> cc = paymentCodec.decodeStart(typeProvider, new JsonPrimitive("cc:1234-5678"));
		assertTrue(cc.isSuccess());
		assertEquals("cc:1234-5678", cc.resultOrThrow());
		
		Result<String> paypal = paymentCodec.decodeStart(typeProvider, new JsonPrimitive("paypal:user@example.com"));
		assertTrue(paypal.isSuccess());
		assertEquals("paypal:user@example.com", paypal.resultOrThrow());
		
		Result<String> bank = paymentCodec.decodeStart(typeProvider, new JsonPrimitive("bank:DE89370400440532013000"));
		assertTrue(bank.isSuccess());
		assertEquals("bank:DE89370400440532013000", bank.resultOrThrow());
		
		Result<String> invalid = paymentCodec.decodeStart(typeProvider, new JsonPrimitive("invalid:method"));
		assertTrue(invalid.isError());
	}
	
	@Test
	void integrationWithUnionCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> statusCodec1 = STRING.union("pending", "active");
		Codec<String> statusCodec2 = STRING.union("completed", "cancelled");
		
		Codec<String> anyStatusCodec = new AnyCodec<>(statusCodec1, statusCodec2);
		
		assertTrue(anyStatusCodec.encodeStart(typeProvider, typeProvider.empty(), "pending").isSuccess());
		assertTrue(anyStatusCodec.encodeStart(typeProvider, typeProvider.empty(), "completed").isSuccess());
		assertTrue(anyStatusCodec.encodeStart(typeProvider, typeProvider.empty(), "invalid").isError());
	}
	
	@Test
	void integrationWithNullableCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Number> nullableAny = anyCodec.nullable();
		
		Result<JsonElement> nullResult = nullableAny.encodeStart(typeProvider, typeProvider.empty(), null);
		assertTrue(nullResult.isSuccess());
		
		Result<JsonElement> validResult = nullableAny.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(validResult.isSuccess());
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Optional<Number>> optionalAny = anyCodec.optional();
		
		Result<JsonElement> emptyResult = optionalAny.encodeStart(typeProvider, typeProvider.empty(), Optional.empty());
		assertTrue(emptyResult.isSuccess());
		
		Result<JsonElement> validResult = optionalAny.encodeStart(typeProvider, typeProvider.empty(), Optional.of(42));
		assertTrue(validResult.isSuccess());
	}
	
	@Test
	void integrationWithListCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<List<Number>> listAny = anyCodec.list();
		
		Result<JsonElement> result = listAny.encodeStart(typeProvider, typeProvider.empty(), List.of(42, 3.14, 100));
		assertTrue(result.isSuccess());
		
		Result<List<Number>> decoded = listAny.decodeStart(typeProvider, result.resultOrThrow());
		assertTrue(decoded.isSuccess());
		assertEquals(3, decoded.resultOrThrow().size());
	}
	
	@Test
	void nestedAnyCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> intOrDouble = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Number> longOrFloat = new AnyCodec<>(LONG, FLOAT);
		Codec<Number> anyNumber = new AnyCodec<>(intOrDouble, longOrFloat);
		
		assertTrue(anyNumber.encodeStart(typeProvider, typeProvider.empty(), 42).isSuccess());
		assertTrue(anyNumber.encodeStart(typeProvider, typeProvider.empty(), 3.14).isSuccess());
		assertTrue(anyNumber.encodeStart(typeProvider, typeProvider.empty(), 1000L).isSuccess());
		assertTrue(anyNumber.encodeStart(typeProvider, typeProvider.empty(), 2.5f).isSuccess());
	}
	
	@Test
	void equalsAndHashCode() {
		AnyCodec<Number> codec1 = new AnyCodec<>(INTEGER, DOUBLE);
		AnyCodec<Number> codec2 = new AnyCodec<>(INTEGER, DOUBLE);
		AnyCodec<Number> codec3 = new AnyCodec<>(DOUBLE, INTEGER);
		AnyCodec<Number> codec4 = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		assertEquals(codec1, codec2);
		assertEquals(codec1.hashCode(), codec2.hashCode());
		assertNotEquals(codec1, codec3);
		assertNotEquals(codec1, codec4);
	}
	
	@Test
	void toStringRepresentation() {
		AnyCodec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		String result = codec.toString();
		
		assertTrue(result.startsWith("AnyCodec["));
		assertTrue(result.endsWith("]"));
		assertTrue(result.contains("IntegerCodec"));
		assertTrue(result.contains("DoubleCodec"));
		assertTrue(result.contains("LongCodec"));
	}
	
	@Test
	void errorMessagesContainAllFailures() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		// Use map() to apply errors to both encoding and decoding
		Codec<Integer> codec1 = INTEGER.map(
			i -> Result.error("Error from codec 1"),
			result -> Result.error("Error from codec 1")
		);
		Codec<Integer> codec2 = INTEGER.map(
			i -> Result.error("Error from codec 2"),
			result -> Result.error("Error from codec 2")
		);
		Codec<Integer> codec3 = INTEGER.map(
			i -> Result.error("Error from codec 3"),
			result -> Result.error("Error from codec 3")
		);
		
		Codec<Integer> anyCodec = new AnyCodec<>(codec1, codec2, codec3);
		
		Result<JsonElement> encodeResult = anyCodec.encodeStart(typeProvider, typeProvider.empty(), 42);
		assertTrue(encodeResult.isError());
		assertTrue(encodeResult.errorOrThrow().contains("Error from codec 1"));
		assertTrue(encodeResult.errorOrThrow().contains("Error from codec 2"));
		assertTrue(encodeResult.errorOrThrow().contains("Error from codec 3"));
		
		Result<Integer> decodeResult = anyCodec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(decodeResult.isError());
		assertTrue(decodeResult.errorOrThrow().contains("Error from codec 1"));
		assertTrue(decodeResult.errorOrThrow().contains("Error from codec 2"));
		assertTrue(decodeResult.errorOrThrow().contains("Error from codec 3"));
	}
	
	@Test
	void multipleCodecsWithSameType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> shortStrings = STRING.validate(s -> {
			if (s.length() <= 5) {
				return Result.success(s);
			}
			return Result.error("String too long");
		});
		
		Codec<String> mediumStrings = STRING.validate(s -> {
			if (s.length() > 5 && s.length() <= 10) {
				return Result.success(s);
			}
			return Result.error("String not medium length");
		});
		
		Codec<String> longStrings = STRING.validate(s -> {
			if (s.length() > 10) {
				return Result.success(s);
			}
			return Result.error("String not long enough");
		});
		
		Codec<String> anyString = new AnyCodec<>(shortStrings, mediumStrings, longStrings);
		
		assertTrue(anyString.encodeStart(typeProvider, typeProvider.empty(), "hi").isSuccess());
		assertTrue(anyString.encodeStart(typeProvider, typeProvider.empty(), "medium").isSuccess());
		assertTrue(anyString.encodeStart(typeProvider, typeProvider.empty(), "very long string").isSuccess());
	}
}
