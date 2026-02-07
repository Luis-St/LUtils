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
import net.luis.utils.io.codec.provider.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.io.data.xml.XmlElement;
import org.junit.jupiter.api.Test;

import static net.luis.utils.io.codec.Codecs.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NullableCodec}.<br>
 *
 * @author Luis-St
 */
class NullableCodecTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new NullableCodec<>(null));
		assertDoesNotThrow(() -> new NullableCodec<>(INTEGER));
	}
	
	@Test
	void encodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encode(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> codec.encode(typeProvider, null, 1));
	}
	
	@Test
	void encodeWithNullValueJson() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, null);
		assertEquals(JsonNull.INSTANCE, result);
	}
	
	@Test
	void encodeWithNullValueXml() throws EncoderException {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		XmlElement current = typeProvider.empty();
		
		XmlElement result = codec.encode(typeProvider, current, null);
		assertTrue(result.isSelfClosing());
	}
	
	@Test
	void encodeWithNullValueJava() throws EncoderException {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		Object current = typeProvider.empty();
		
		Object result = codec.encode(typeProvider, current, null);
		assertNull(result);
	}
	
	@Test
	void encodeWithNonNullValueJson() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		JsonElement result = codec.encode(typeProvider, current, 42);
		assertEquals(new JsonPrimitive(42), result);
	}
	
	@Test
	void encodeWithNonNullValueXml() throws EncoderException {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		XmlElement current = typeProvider.empty();
		
		XmlElement result = codec.encode(typeProvider, current, "test");
		assertTrue(result.isXmlValue());
	}
	
	@Test
	void encodeWithDifferentTypes() throws EncoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject current = new JsonObject();
		
		Codec<String> stringCodec = new NullableCodec<>(STRING);
		JsonElement stringResult = stringCodec.encode(typeProvider, current, "hello");
		assertEquals(new JsonPrimitive("hello"), stringResult);
		
		Codec<Boolean> boolCodec = new NullableCodec<>(BOOLEAN);
		JsonElement boolResult = boolCodec.encode(typeProvider, current, true);
		assertEquals(new JsonPrimitive(true), boolResult);
		
		JsonElement nullResult = stringCodec.encode(typeProvider, current, null);
		assertEquals(JsonNull.INSTANCE, nullResult);
	}
	
	@Test
	void decodeNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decode(null, typeProvider.empty(), new JsonPrimitive(1)));
	}
	
	@Test
	void decodeWithNullValueJson() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Integer result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertNull(result);
	}
	
	@Test
	void decodeWithNullValueJava() throws DecoderException {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		
		String result = codec.decode(typeProvider, typeProvider.empty(), null);
		assertNull(result);
	}
	
	@Test
	void decodeWithJsonNull() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Integer result = codec.decode(typeProvider, typeProvider.empty(), JsonNull.INSTANCE);
		assertNull(result);
	}
	
	@Test
	void decodeWithXmlNull() throws DecoderException {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		XmlElement xmlNull = typeProvider.createNull();
		
		Integer result = codec.decode(typeProvider, typeProvider.empty(), xmlNull);
		assertNull(result);
	}
	
	@Test
	void decodeWithValidValueJson() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Integer result = codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(42));
		assertNotNull(result);
		assertEquals(42, result);
	}
	
	@Test
	void decodeWithValidValueJava() throws DecoderException {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		
		String result = codec.decode(typeProvider, typeProvider.empty(), "test");
		assertNotNull(result);
		assertEquals("test", result);
	}
	
	@Test
	void decodeWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		assertThrows(DecoderException.class, () -> codec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("not-a-number")));
	}
	
	@Test
	void decodeWithDifferentTypes() throws DecoderException {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new NullableCodec<>(STRING);
		String stringResult = stringCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive("hello"));
		assertNotNull(stringResult);
		assertEquals("hello", stringResult);
		
		Codec<Boolean> boolCodec = new NullableCodec<>(BOOLEAN);
		Boolean boolResult = boolCodec.decode(typeProvider, typeProvider.empty(), new JsonPrimitive(true));
		assertNotNull(boolResult);
		assertTrue(boolResult);
		
		String nullResult = stringCodec.decode(typeProvider, typeProvider.empty(), JsonNull.INSTANCE);
		assertNull(nullResult);
	}
	
	@Test
	void roundTripEncodingAndDecodingJson() throws Exception {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		// Test with null value
		JsonElement encodedNull = codec.encode(typeProvider, current, null);
		Integer decodedNull = codec.decode(typeProvider, typeProvider.empty(), encodedNull);
		assertNull(decodedNull);
		
		// Test with non-null value
		JsonElement encodedValue = codec.encode(typeProvider, current, 123);
		Integer decodedValue = codec.decode(typeProvider, typeProvider.empty(), encodedValue);
		assertEquals(123, decodedValue);
	}
	
	@Test
	void roundTripEncodingAndDecodingXml() throws Exception {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		XmlElement current = typeProvider.empty();
		
		// Test with null value
		XmlElement encodedNull = codec.encode(typeProvider, current, null);
		String decodedNull = codec.decode(typeProvider, typeProvider.empty(), encodedNull);
		assertNull(decodedNull);
		
		// Test with non-null value
		XmlElement encodedValue = codec.encode(typeProvider, current, "test");
		String decodedValue = codec.decode(typeProvider, typeProvider.empty(), encodedValue);
		assertEquals("test", decodedValue);
	}
	
	@Test
	void roundTripEncodingAndDecodingJava() throws Exception {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<Boolean> codec = new NullableCodec<>(BOOLEAN);
		Object current = typeProvider.empty();
		
		// Test with null value
		Object encodedNull = codec.encode(typeProvider, current, null);
		Boolean decodedNull = codec.decode(typeProvider, typeProvider.empty(), encodedNull);
		assertNull(decodedNull);
		
		// Test with non-null value
		Object encodedValue = codec.encode(typeProvider, current, true);
		Boolean decodedValue = codec.decode(typeProvider, typeProvider.empty(), encodedValue);
		assertTrue(decodedValue);
	}
	
	@Test
	void equalsAndHashCode() {
		NullableCodec<Integer> codec1 = new NullableCodec<>(INTEGER);
		NullableCodec<Integer> codec2 = new NullableCodec<>(INTEGER);
		NullableCodec<String> codec3 = new NullableCodec<>(STRING);
		
		assertEquals(codec1, codec2);
		assertEquals(codec1.hashCode(), codec2.hashCode());
		assertNotEquals(codec1, codec3);
	}
	
	@Test
	void toStringRepresentation() {
		NullableCodec<Integer> codec = new NullableCodec<>(INTEGER);
		String result = codec.toString();
		
		assertTrue(result.startsWith("NullableCodec["));
		assertTrue(result.endsWith("]"));
	}
}
