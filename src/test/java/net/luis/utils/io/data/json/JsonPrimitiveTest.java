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

package net.luis.utils.io.data.json;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonPrimitive}.<br>
 *
 * @author Luis-St
 */
class JsonPrimitiveTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, true, "  ", true, 10, true, 2, StandardCharsets.UTF_8);
	
	@Test
	void constructorWithBoolean() {
		JsonPrimitive trueValue = new JsonPrimitive(true);
		JsonPrimitive falseValue = new JsonPrimitive(false);
		
		assertEquals("true", trueValue.getAsString());
		assertEquals("false", falseValue.getAsString());
	}
	
	@Test
	void constructorWithNumbers() {
		assertDoesNotThrow(() -> new JsonPrimitive((byte) 1));
		assertDoesNotThrow(() -> new JsonPrimitive((short) 2));
		assertDoesNotThrow(() -> new JsonPrimitive(3));
		assertDoesNotThrow(() -> new JsonPrimitive(4L));
		assertDoesNotThrow(() -> new JsonPrimitive(5.5f));
		assertDoesNotThrow(() -> new JsonPrimitive(6.6));
		assertDoesNotThrow(() -> new JsonPrimitive(new BigInteger("123456789")));
		assertDoesNotThrow(() -> new JsonPrimitive(new BigDecimal("123.456")));
		
		assertThrows(NullPointerException.class, () -> new JsonPrimitive((Number) null));
	}
	
	@Test
	void constructorWithString() {
		JsonPrimitive stringValue = new JsonPrimitive("test string");
		assertEquals("test string", stringValue.getAsString());
		
		JsonPrimitive emptyString = new JsonPrimitive("");
		assertEquals("", emptyString.getAsString());
		
		assertThrows(NullPointerException.class, () -> new JsonPrimitive((String) null));
	}
	
	@Test
	void constructorWithSpecialStringValues() {
		JsonPrimitive booleanLikeString = new JsonPrimitive("true");
		assertEquals("true", booleanLikeString.getAsString());
		
		JsonPrimitive numberLikeString = new JsonPrimitive("123");
		assertEquals("123", numberLikeString.getAsString());
		
		JsonPrimitive floatLikeString = new JsonPrimitive("3.14");
		assertEquals("3.14", floatLikeString.getAsString());
	}
	
	@Test
	void jsonElementTypeChecks() {
		JsonPrimitive primitive = new JsonPrimitive(42);
		
		assertFalse(primitive.isJsonNull());
		assertFalse(primitive.isJsonObject());
		assertFalse(primitive.isJsonArray());
		assertTrue(primitive.isJsonPrimitive());
	}
	
	@Test
	void jsonElementConversions() {
		JsonPrimitive primitive = new JsonPrimitive("test");
		
		assertThrows(JsonTypeException.class, primitive::getAsJsonObject);
		assertThrows(JsonTypeException.class, primitive::getAsJsonArray);
		assertSame(primitive, primitive.getAsJsonPrimitive());
	}
	
	@Test
	void getAsStringFromDifferentTypes() {
		assertEquals("true", new JsonPrimitive(true).getAsString());
		assertEquals("false", new JsonPrimitive(false).getAsString());
		assertEquals("42", new JsonPrimitive(42).getAsString());
		assertEquals("3.14", new JsonPrimitive(3.14).getAsString());
		assertEquals("1.0", new JsonPrimitive(1.0f).getAsString());
		assertEquals("test", new JsonPrimitive("test").getAsString());
		assertEquals("123456789", new JsonPrimitive(new BigInteger("123456789")).getAsString());
		assertEquals("123.456", new JsonPrimitive(new BigDecimal("123.456")).getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new JsonPrimitive(true).getAsBoolean());
		assertFalse(new JsonPrimitive(false).getAsBoolean());
		assertTrue(new JsonPrimitive("true").getAsBoolean());
		assertFalse(new JsonPrimitive("false").getAsBoolean());
		assertTrue(new JsonPrimitive("TRUE").getAsBoolean());
		assertFalse(new JsonPrimitive("FALSE").getAsBoolean());
		assertTrue(new JsonPrimitive("True").getAsBoolean());
		assertFalse(new JsonPrimitive("False").getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("test").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("1").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("0").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(1).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(0).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(3.14).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("yes").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("no").getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals(1L, new JsonPrimitive((byte) 1).getAsNumber());
		assertEquals(2L, new JsonPrimitive((short) 2).getAsNumber());
		assertEquals(3L, new JsonPrimitive(3).getAsNumber());
		assertEquals(4L, new JsonPrimitive(4L).getAsNumber());
		assertEquals(5.5, new JsonPrimitive(5.5f).getAsNumber());
		assertEquals(6.6, new JsonPrimitive(6.6).getAsNumber());
		
		assertEquals((short) 1, new JsonPrimitive("1s").getAsNumber());
		assertEquals(1.0F, new JsonPrimitive("1.0f").getAsNumber());
		assertEquals(1L, new JsonPrimitive("1l").getAsNumber());
		assertEquals(1.0, new JsonPrimitive("1.0d").getAsNumber());
		assertEquals(1, new JsonPrimitive("1i").getAsNumber());
		assertEquals((byte) 1, new JsonPrimitive("1b").getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(false).getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive(true).getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("test").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("not a number").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("").getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		JsonPrimitive intValue = new JsonPrimitive(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertEquals((byte) 1, new JsonPrimitive("1b").getAsByte());
		assertEquals((short) 1, new JsonPrimitive("1s").getAsShort());
		assertEquals(1, new JsonPrimitive("1i").getAsInteger());
		assertEquals(1L, new JsonPrimitive("1l").getAsLong());
		
		assertEquals(1, new JsonPrimitive(1L).getAsInteger());
		assertEquals(1L, new JsonPrimitive((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		JsonPrimitive floatValue = new JsonPrimitive(3.14f);
		JsonPrimitive doubleValue = new JsonPrimitive(3.14);
		
		assertThrows(IllegalArgumentException.class, booleanValue::getAsByte);
		assertThrows(IllegalArgumentException.class, booleanValue::getAsShort);
		assertThrows(IllegalArgumentException.class, booleanValue::getAsInteger);
		assertThrows(IllegalArgumentException.class, booleanValue::getAsLong);
		
		assertThrows(IllegalArgumentException.class, floatValue::getAsByte);
		assertThrows(IllegalArgumentException.class, floatValue::getAsShort);
		assertThrows(IllegalArgumentException.class, floatValue::getAsInteger);
		assertThrows(IllegalArgumentException.class, floatValue::getAsLong);
		
		assertThrows(IllegalArgumentException.class, doubleValue::getAsByte);
		assertThrows(IllegalArgumentException.class, doubleValue::getAsShort);
		assertThrows(IllegalArgumentException.class, doubleValue::getAsInteger);
		assertThrows(IllegalArgumentException.class, doubleValue::getAsLong);
	}
	
	@Test
	void getAsFloatingPointTypesValid() {
		assertEquals(1.0f, new JsonPrimitive((byte) 1).getAsFloat());
		assertEquals(1.0f, new JsonPrimitive((short) 1).getAsFloat());
		assertEquals(1.0f, new JsonPrimitive(1).getAsFloat());
		assertEquals(1.0f, new JsonPrimitive(1L).getAsFloat());
		
		assertEquals(1.0, new JsonPrimitive((byte) 1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive((short) 1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1).getAsDouble());
		assertEquals(1.0, new JsonPrimitive(1L).getAsDouble());
		
		assertEquals(3.14f, new JsonPrimitive(3.14f).getAsFloat());
		assertEquals(3.14, new JsonPrimitive(3.14).getAsDouble());
		assertEquals(3.14f, new JsonPrimitive(3.14).getAsFloat());
		assertEquals(3.14, new JsonPrimitive(3.14f).getAsDouble());
		
		assertEquals(1.0f, new JsonPrimitive("1f").getAsFloat());
		assertEquals(1.5f, new JsonPrimitive("1.5f").getAsFloat());
		assertEquals(1.0, new JsonPrimitive("1d").getAsDouble());
		assertEquals(1.5, new JsonPrimitive("1.5d").getAsDouble());
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		
		assertThrows(IllegalArgumentException.class, booleanValue::getAsFloat);
		assertThrows(IllegalArgumentException.class, booleanValue::getAsDouble);
	}
	
	@Test
	void getAsWithCustomParser() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new JsonPrimitive("[]").getAs(null));
		
		List<Boolean> result = new JsonPrimitive("[true, false, true]").getAs(parser);
		assertEquals(List.of(true, false, true), result);
		
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("invalid format").getAs(parser));
		assertThrows(IllegalArgumentException.class, () -> new JsonPrimitive("()").getAs(parser));
	}
	
	@Test
	void equalsAndHashCode() {
		JsonPrimitive primitive1 = new JsonPrimitive("test");
		JsonPrimitive primitive2 = new JsonPrimitive("test");
		JsonPrimitive primitive3 = new JsonPrimitive("different");
		JsonPrimitive primitive4 = new JsonPrimitive(42);
		JsonPrimitive primitive5 = new JsonPrimitive(42);
		
		assertEquals(primitive1, primitive2);
		assertEquals(primitive1.hashCode(), primitive2.hashCode());
		assertNotEquals(primitive1, primitive3);
		
		// Number equality
		assertEquals(primitive4, primitive5);
		assertEquals(primitive4.hashCode(), primitive5.hashCode());
		
		assertNotEquals(primitive1, primitive4);
		assertNotEquals(primitive1, null);
		assertNotEquals(primitive1, "not a primitive");
		
		assertEquals(primitive1, primitive1);
		
		JsonPrimitive boolTrue1 = new JsonPrimitive(true);
		JsonPrimitive boolTrue2 = new JsonPrimitive(true);
		JsonPrimitive boolFalse = new JsonPrimitive(false);
		
		assertEquals(boolTrue1, boolTrue2);
		assertEquals(boolTrue1.hashCode(), boolTrue2.hashCode());
		assertNotEquals(boolTrue1, boolFalse);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("false", new JsonPrimitive(false).toString());
		assertEquals("true", new JsonPrimitive(true).toString());
		
		assertEquals("42", new JsonPrimitive(42).toString());
		assertEquals("3.14", new JsonPrimitive(3.14).toString());
		assertEquals("1", new JsonPrimitive((byte) 1).toString());
		assertEquals("2", new JsonPrimitive((short) 2).toString());
		assertEquals("3", new JsonPrimitive(3L).toString());
		assertEquals("4.5", new JsonPrimitive(4.5f).toString());
		
		assertEquals("\"test\"", new JsonPrimitive("test").toString());
		assertEquals("\"\"", new JsonPrimitive("").toString());
		assertEquals("true", new JsonPrimitive("true").toString());
		assertEquals("42", new JsonPrimitive("42").toString());
		
		assertEquals("\"hello world\"", new JsonPrimitive("hello world").toString());
		assertEquals("\"line1\nline2\"", new JsonPrimitive("line1\nline2").toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		JsonPrimitive numberValue = new JsonPrimitive(42);
		JsonPrimitive stringValue = new JsonPrimitive("test");
		
		assertEquals("true", booleanValue.toString(CUSTOM_CONFIG));
		assertEquals("42", numberValue.toString(CUSTOM_CONFIG));
		assertEquals("\"test\"", stringValue.toString(CUSTOM_CONFIG));
		
		assertEquals("true", booleanValue.toString(null));
		assertEquals("42", numberValue.toString(null));
		assertEquals("\"test\"", stringValue.toString(null));
	}
	
	@Test
	void toStringTypeParsing() {
		assertEquals("true", new JsonPrimitive("true").toString());
		assertEquals("false", new JsonPrimitive("false").toString());
		
		assertEquals("123", new JsonPrimitive("123").toString());
		assertEquals("3.14", new JsonPrimitive("3.14").toString());
		assertEquals("-42", new JsonPrimitive("-42").toString());
		
		assertEquals("\"not_a_boolean\"", new JsonPrimitive("not_a_boolean").toString());
		assertEquals("\"123abc\"", new JsonPrimitive("123abc").toString());
		assertEquals("\"\"", new JsonPrimitive("").toString());
	}
	
	@Test
	void valueTypePersistence() {
		JsonPrimitive intValue = new JsonPrimitive(42);
		JsonPrimitive stringValue = new JsonPrimitive("42");
		
		assertEquals(intValue, stringValue);
		assertEquals("42", intValue.toString());
		assertEquals("42", stringValue.toString());
	}
	
	@Test
	void extremeValues() {
		JsonPrimitive maxInt = new JsonPrimitive(Integer.MAX_VALUE);
		JsonPrimitive minInt = new JsonPrimitive(Integer.MIN_VALUE);
		JsonPrimitive maxLong = new JsonPrimitive(Long.MAX_VALUE);
		JsonPrimitive minLong = new JsonPrimitive(Long.MIN_VALUE);
		JsonPrimitive maxDouble = new JsonPrimitive(Double.MAX_VALUE);
		JsonPrimitive minDouble = new JsonPrimitive(Double.MIN_VALUE);
		
		assertEquals(Integer.MAX_VALUE, maxInt.getAsInteger());
		assertEquals(Integer.MIN_VALUE, minInt.getAsInteger());
		assertEquals(Long.MAX_VALUE, maxLong.getAsLong());
		assertEquals(Long.MIN_VALUE, minLong.getAsLong());
		assertEquals(Double.MAX_VALUE, maxDouble.getAsDouble());
		assertEquals(Double.MIN_VALUE, minDouble.getAsDouble());
		
		String longString = "a".repeat(10000);
		JsonPrimitive longStringPrimitive = new JsonPrimitive(longString);
		assertEquals(longString, longStringPrimitive.getAsString());
	}
}
