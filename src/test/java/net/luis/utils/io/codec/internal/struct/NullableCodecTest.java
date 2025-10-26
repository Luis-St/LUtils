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
import net.luis.utils.io.codec.provider.*;
import net.luis.utils.io.data.json.*;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.util.result.Result;
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
	void encodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.encodeStart(null, typeProvider.empty(), 1));
		assertThrows(NullPointerException.class, () -> codec.encodeStart(typeProvider, null, 1));
	}
	
	@Test
	void encodeStartWithNullValueJson() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertEquals(JsonNull.INSTANCE, result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNullValueXml() {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		XmlElement current = typeProvider.empty();
		
		Result<XmlElement> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isSelfClosing());
	}
	
	@Test
	void encodeStartWithNullValueJava() {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		Object current = typeProvider.empty();
		
		Result<Object> result = codec.encodeStart(typeProvider, current, null);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNonNullValueJson() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		Result<JsonElement> result = codec.encodeStart(typeProvider, current, 42);
		assertTrue(result.isSuccess());
		assertEquals(new JsonPrimitive(42), result.resultOrThrow());
	}
	
	@Test
	void encodeStartWithNonNullValueXml() {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		XmlElement current = typeProvider.empty();
		
		Result<XmlElement> result = codec.encodeStart(typeProvider, current, "test");
		assertTrue(result.isSuccess());
		assertTrue(result.resultOrThrow().isXmlValue());
	}
	
	@Test
	void encodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		JsonObject current = new JsonObject();
		
		Codec<String> stringCodec = new NullableCodec<>(STRING);
		Result<JsonElement> stringResult = stringCodec.encodeStart(typeProvider, current, "hello");
		assertTrue(stringResult.isSuccess());
		assertEquals(new JsonPrimitive("hello"), stringResult.resultOrThrow());
		
		Codec<Boolean> boolCodec = new NullableCodec<>(BOOLEAN);
		Result<JsonElement> boolResult = boolCodec.encodeStart(typeProvider, current, true);
		assertTrue(boolResult.isSuccess());
		assertEquals(new JsonPrimitive(true), boolResult.resultOrThrow());
		
		Result<JsonElement> nullResult = stringCodec.encodeStart(typeProvider, current, null);
		assertTrue(nullResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, nullResult.resultOrThrow());
	}
	
	@Test
	void decodeStartNullChecks() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		assertThrows(NullPointerException.class, () -> codec.decodeStart(null, new JsonPrimitive(1)));
	}
	
	@Test
	void decodeStartWithNullValueJson() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Result<Integer> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithNullValueJava() {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		
		Result<String> result = codec.decodeStart(typeProvider, null);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithJsonNull() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Result<Integer> result = codec.decodeStart(typeProvider, JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithXmlNull() {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		XmlElement xmlNull = typeProvider.createNull().resultOrThrow();
		
		Result<Integer> result = codec.decodeStart(typeProvider, xmlNull);
		assertTrue(result.isSuccess());
		assertNull(result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidValueJson() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Result<Integer> result = codec.decodeStart(typeProvider, new JsonPrimitive(42));
		assertTrue(result.isSuccess());
		assertNotNull(result.resultOrThrow());
		assertEquals(42, result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithValidValueJava() {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		
		Result<String> result = codec.decodeStart(typeProvider, "test");
		assertTrue(result.isSuccess());
		assertNotNull(result.resultOrThrow());
		assertEquals("test", result.resultOrThrow());
	}
	
	@Test
	void decodeStartWithInvalidValue() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		
		Result<Integer> result = codec.decodeStart(typeProvider, new JsonPrimitive("not-a-number"));
		assertTrue(result.isError());
	}
	
	@Test
	void decodeStartWithDifferentTypes() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		
		Codec<String> stringCodec = new NullableCodec<>(STRING);
		Result<String> stringResult = stringCodec.decodeStart(typeProvider, new JsonPrimitive("hello"));
		assertTrue(stringResult.isSuccess());
		assertNotNull(stringResult.resultOrThrow());
		assertEquals("hello", stringResult.resultOrThrow());
		
		Codec<Boolean> boolCodec = new NullableCodec<>(BOOLEAN);
		Result<Boolean> boolResult = boolCodec.decodeStart(typeProvider, new JsonPrimitive(true));
		assertTrue(boolResult.isSuccess());
		assertNotNull(boolResult.resultOrThrow());
		assertTrue(boolResult.resultOrThrow());
		
		Result<String> nullResult = stringCodec.decodeStart(typeProvider, JsonNull.INSTANCE);
		assertTrue(nullResult.isSuccess());
		assertNull(nullResult.resultOrThrow());
	}
	
	@Test
	void roundTripEncodingAndDecodingJson() {
		JsonTypeProvider typeProvider = JsonTypeProvider.INSTANCE;
		Codec<Integer> codec = new NullableCodec<>(INTEGER);
		JsonObject current = new JsonObject();
		
		// Test with null value
		Result<JsonElement> encodedNull = codec.encodeStart(typeProvider, current, null);
		assertTrue(encodedNull.isSuccess());
		Result<Integer> decodedNull = codec.decodeStart(typeProvider, encodedNull.resultOrThrow());
		assertTrue(decodedNull.isSuccess());
		assertNull(decodedNull.resultOrThrow());
		
		// Test with non-null value
		Result<JsonElement> encodedValue = codec.encodeStart(typeProvider, current, 123);
		assertTrue(encodedValue.isSuccess());
		Result<Integer> decodedValue = codec.decodeStart(typeProvider, encodedValue.resultOrThrow());
		assertTrue(decodedValue.isSuccess());
		assertEquals(123, decodedValue.resultOrThrow());
	}
	
	@Test
	void roundTripEncodingAndDecodingXml() {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE;
		Codec<String> codec = new NullableCodec<>(STRING);
		XmlElement current = typeProvider.empty();
		
		// Test with null value
		Result<XmlElement> encodedNull = codec.encodeStart(typeProvider, current, null);
		assertTrue(encodedNull.isSuccess());
		Result<String> decodedNull = codec.decodeStart(typeProvider, encodedNull.resultOrThrow());
		assertTrue(decodedNull.isSuccess());
		assertNull(decodedNull.resultOrThrow());
		
		// Test with non-null value
		Result<XmlElement> encodedValue = codec.encodeStart(typeProvider, current, "test");
		assertTrue(encodedValue.isSuccess());
		Result<String> decodedValue = codec.decodeStart(typeProvider, encodedValue.resultOrThrow());
		assertTrue(decodedValue.isSuccess());
		assertEquals("test", decodedValue.resultOrThrow());
	}
	
	@Test
	void roundTripEncodingAndDecodingJava() {
		JavaTypeProvider typeProvider = JavaTypeProvider.INSTANCE;
		Codec<Boolean> codec = new NullableCodec<>(BOOLEAN);
		Object current = typeProvider.empty();
		
		// Test with null value
		Result<Object> encodedNull = codec.encodeStart(typeProvider, current, null);
		assertTrue(encodedNull.isSuccess());
		Result<Boolean> decodedNull = codec.decodeStart(typeProvider, encodedNull.resultOrThrow());
		assertTrue(decodedNull.isSuccess());
		assertNull(decodedNull.resultOrThrow());
		
		// Test with non-null value
		Result<Object> encodedValue = codec.encodeStart(typeProvider, current, true);
		assertTrue(encodedValue.isSuccess());
		Result<Boolean> decodedValue = codec.decodeStart(typeProvider, encodedValue.resultOrThrow());
		assertTrue(decodedValue.isSuccess());
		assertTrue(decodedValue.resultOrThrow());
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
