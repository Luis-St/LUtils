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

package net.luis.utils.io.codec.types.struct;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.JsonElement;
import net.luis.utils.io.data.json.JsonPrimitive;
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
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), 42));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, 42));
	}
	
	@Test
	void encodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to encode null value as any"));
	}
	
	@Test
	void encodeFirstCodecSucceeds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 42);
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void encodeSecondCodecSucceeds() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		JsonElement result = codec.encode(typeProvider, typeProvider.empty(), 3.14);
		assertEquals(new JsonPrimitive(3.14), result);
	}
	
	@Test
	void encodeAllCodecsFail() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec1 = STRING.union("a", "b");
		Codec<String> codec2 = STRING.union("c", "d");
		Codec<String> codec = new AnyCodec<>(codec1, codec2);
		
		EncoderException exception = assertThrows(EncoderException.class, () -> codec.encode(typeProvider, typeProvider.empty(), "invalid"));
		assertTrue(exception.getMessage().contains("All codecs failed"));
		assertTrue(exception.getMessage().contains("invalid"));
	}
	
	@Test
	void encodeWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> numberCodec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		assertDoesNotThrow(() -> numberCodec.encode(typeProvider, typeProvider.empty(), 42));
		assertDoesNotThrow(() -> numberCodec.encode(typeProvider, typeProvider.empty(), 3.14));
		assertDoesNotThrow(() -> numberCodec.encode(typeProvider, typeProvider.empty(), 1000L));
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(42)));
	}
	
	@Test
	void decodeWithNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), null));
		assertTrue(exception.getMessage().contains("Unable to decode null value as any"));
	}
	
	@Test
	void decodeFirstCodecSucceeds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE, LONG);
		
		Number result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertEquals(42, result);
	}
	
	@Test
	void decodeSecondCodecSucceeds() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<String> codec = new AnyCodec<>(STRING, STRING);
		
		String result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertEquals("hello", result);
	}
	
	@Test
	void decodeAllCodecsFail() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> restrictedCodec1 = INTEGER.union(1, 2, 3);
		Codec<Integer> restrictedCodec2 = INTEGER.union(4, 5, 6);
		Codec<Integer> codec = new AnyCodec<>(restrictedCodec1, restrictedCodec2);
		
		DecoderException exception = assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(10)));
		assertTrue(exception.getMessage().contains("All codecs failed"));
	}
	
	@Test
	void decodeWithInvalidType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> codec = new AnyCodec<>(INTEGER, DOUBLE);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void roundTripEncodingDecoding() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> codec1 = STRING.union("red", "green", "blue");
		Codec<String> codec2 = STRING.union("small", "medium", "large");
		Codec<String> anyCodec = new AnyCodec<>(codec1, codec2);
		
		String color = "red";
		JsonElement encodedColor = anyCodec.encode(typeProvider, typeProvider.empty(), color);
		String decodedColor = anyCodec.decode(typeProvider, typeProvider.empty(), encodedColor);
		assertEquals(color, decodedColor);
		
		String size = "large";
		JsonElement encodedSize = anyCodec.encode(typeProvider, typeProvider.empty(), size);
		String decodedSize = anyCodec.decode(typeProvider, typeProvider.empty(), encodedSize);
		assertEquals(size, decodedSize);
	}
	
	@Test
	void codecOrderMatters() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> codec1 = new AnyCodec<>(INTEGER, DOUBLE);
		
		Number result1 = codec1.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertInstanceOf(Integer.class, result1);
	}
	
	@Test
	void factoryMethodFromCodecInterface() {
		Codec<Number> codec = any(INTEGER, DOUBLE, LONG);
		assertNotNull(codec);
		assertInstanceOf(AnyCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void factoryMethodWithList() {
		List<Codec<? extends Number>> codecs = List.of(INTEGER, DOUBLE, LONG);
		Codec<Number> codec = any(codecs);
		assertNotNull(codec);
		assertInstanceOf(AnyCodec.class, codec);
		
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		assertDoesNotThrow(() -> codec.encode(typeProvider, typeProvider.empty(), 3.14));
	}
	
	@Test
	void polymorphicPaymentMethodExample() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> paymentCodec = new AnyCodec<>(STRING, STRING, STRING);
		
		String cc = paymentCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("cc:1234-5678"));
		assertEquals("cc:1234-5678", cc);
		
		String paypal = paymentCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("paypal:user@example.com"));
		assertEquals("paypal:user@example.com", paypal);
		
		String bank = paymentCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("bank:DE89370400440532013000"));
		assertEquals("bank:DE89370400440532013000", bank);
	}
	
	@Test
	void integrationWithUnionCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> statusCodec1 = STRING.union("pending", "active");
		Codec<String> statusCodec2 = STRING.union("completed", "cancelled");
		
		Codec<String> anyStatusCodec = new AnyCodec<>(statusCodec1, statusCodec2);
		
		assertDoesNotThrow(() -> anyStatusCodec.encode(typeProvider, typeProvider.empty(), "pending"));
		assertDoesNotThrow(() -> anyStatusCodec.encode(typeProvider, typeProvider.empty(), "completed"));
		assertThrows(EncoderException.class, () -> anyStatusCodec.encode(typeProvider, typeProvider.empty(), "invalid"));
	}
	
	@Test
	void integrationWithNullableCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Number> nullableAny = anyCodec.nullable();
		
		assertDoesNotThrow(() -> nullableAny.encode(typeProvider, typeProvider.empty(), null));
		assertDoesNotThrow(() -> nullableAny.encode(typeProvider, typeProvider.empty(), 42));
	}
	
	@Test
	void integrationWithOptionalCodec() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Optional<Number>> optionalAny = anyCodec.optional();
		
		assertDoesNotThrow(() -> optionalAny.encode(typeProvider, typeProvider.empty(), Optional.empty()));
		assertDoesNotThrow(() -> optionalAny.encode(typeProvider, typeProvider.empty(), Optional.of(42)));
	}
	
	@Test
	void integrationWithListCodec() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Number> anyCodec = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<List<Number>> listAny = anyCodec.list();
		
		JsonElement result = listAny.encode(typeProvider, typeProvider.empty(), List.of(42, 3.14, 100));
		List<Number> decoded = listAny.decode(typeProvider, typeProvider.empty(), result);
		assertEquals(3, decoded.size());
	}
	
	@Test
	void nestedAnyCodecs() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<Number> intOrDouble = new AnyCodec<>(INTEGER, DOUBLE);
		Codec<Number> longOrFloat = new AnyCodec<>(LONG, FLOAT);
		Codec<Number> anyNumber = new AnyCodec<>(intOrDouble, longOrFloat);
		
		assertDoesNotThrow(() -> anyNumber.encode(typeProvider, typeProvider.empty(), 42));
		assertDoesNotThrow(() -> anyNumber.encode(typeProvider, typeProvider.empty(), 3.14));
		assertDoesNotThrow(() -> anyNumber.encode(typeProvider, typeProvider.empty(), 1000L));
		assertDoesNotThrow(() -> anyNumber.encode(typeProvider, typeProvider.empty(), 2.5f));
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
	void multipleCodecsWithSameType() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> anyString = new AnyCodec<>(STRING, STRING, STRING);
		
		assertDoesNotThrow(() -> anyString.encode(typeProvider, typeProvider.empty(), "hi"));
		assertDoesNotThrow(() -> anyString.encode(typeProvider, typeProvider.empty(), "medium"));
		assertDoesNotThrow(() -> anyString.encode(typeProvider, typeProvider.empty(), "very long string"));
	}
}
