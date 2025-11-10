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

import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

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
		
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
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
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("true").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("false").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("TRUE").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("FALSE").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("True").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("False").getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("test").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("0").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(1).getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(0).getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(3.14).getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("yes").getAsBoolean());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("no").getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals((byte) 1, new JsonPrimitive((byte) 1).getAsNumber());
		assertEquals((short) 2, new JsonPrimitive((short) 2).getAsNumber());
		assertEquals(3, new JsonPrimitive(3).getAsNumber());
		assertEquals(4L, new JsonPrimitive(4L).getAsNumber());
		assertEquals(5.5f, new JsonPrimitive(5.5f).getAsNumber());
		assertEquals(6.6, new JsonPrimitive(6.6).getAsNumber());
		
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1s").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1.0f").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1l").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1.0d").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1i").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1b").getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(false).getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive(true).getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("test").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("not a number").getAsNumber());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("").getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		JsonPrimitive intValue = new JsonPrimitive(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1b").getAsByte());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1s").getAsShort());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1i").getAsInteger());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1l").getAsLong());
		
		assertEquals(1, new JsonPrimitive(1L).getAsInteger());
		assertEquals(1L, new JsonPrimitive((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		JsonPrimitive floatValue = new JsonPrimitive(3.14f);
		JsonPrimitive doubleValue = new JsonPrimitive(3.14);
		
		assertThrows(JsonTypeException.class, booleanValue::getAsByte);
		assertThrows(JsonTypeException.class, booleanValue::getAsShort);
		assertThrows(JsonTypeException.class, booleanValue::getAsInteger);
		assertThrows(JsonTypeException.class, booleanValue::getAsLong);
		
		assertEquals((byte) 3, floatValue.getAsByte());
		assertEquals((short) 3, floatValue.getAsShort());
		assertEquals(3, floatValue.getAsInteger());
		assertEquals(3L, floatValue.getAsLong());
		
		assertEquals((byte) 3, doubleValue.getAsByte());
		assertEquals((short) 3, doubleValue.getAsShort());
		assertEquals(3, doubleValue.getAsInteger());
		assertEquals(3L, doubleValue.getAsLong());
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
		assertEquals(3.14, new JsonPrimitive(3.14f).getAsDouble(), 1.0e-6);
		
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1f").getAsFloat());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1.5f").getAsFloat());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1d").getAsDouble());
		assertThrows(JsonTypeException.class, () -> new JsonPrimitive("1.5d").getAsDouble());
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		
		assertThrows(JsonTypeException.class, booleanValue::getAsFloat);
		assertThrows(JsonTypeException.class, booleanValue::getAsDouble);
	}
	
	@Test
	void getAsParsedJsonPrimitiveNoString() {
		JsonPrimitive booleanPrimitive = new JsonPrimitive(true);
		JsonPrimitive numberPrimitive = new JsonPrimitive(42);
		JsonPrimitive floatPrimitive = new JsonPrimitive(3.14);
		
		assertSame(booleanPrimitive, booleanPrimitive.getAsParsedJsonPrimitive());
		assertSame(numberPrimitive, numberPrimitive.getAsParsedJsonPrimitive());
		assertSame(floatPrimitive, floatPrimitive.getAsParsedJsonPrimitive());
	}
	
	@Test
	void getAsParsedJsonPrimitiveFromString() {
		JsonPrimitive trueString = new JsonPrimitive("true");
		JsonPrimitive falseString = new JsonPrimitive("false");
		JsonPrimitive intString = new JsonPrimitive("42");
		JsonPrimitive floatString = new JsonPrimitive("3.14");
		JsonPrimitive normalString = new JsonPrimitive("test");
		
		JsonPrimitive parsedTrue = trueString.getAsParsedJsonPrimitive();
		assertTrue(parsedTrue.getAsBoolean());
		
		JsonPrimitive parsedFalse = falseString.getAsParsedJsonPrimitive();
		assertFalse(parsedFalse.getAsBoolean());
		
		JsonPrimitive parsedInt = intString.getAsParsedJsonPrimitive();
		assertEquals(42, parsedInt.getAsInteger());
		
		JsonPrimitive parsedFloat = floatString.getAsParsedJsonPrimitive();
		assertEquals(3.14, parsedFloat.getAsDouble());
		
		JsonPrimitive parsedNormal = normalString.getAsParsedJsonPrimitive();
		assertEquals("test", parsedNormal.getAsString());
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
		assertEquals("\"true\"", new JsonPrimitive("true").toString());
		assertEquals("\"42\"", new JsonPrimitive("42").toString());
		
		assertEquals("\"hello world\"", new JsonPrimitive("hello world").toString());
		assertEquals("\"line1\nline2\"", new JsonPrimitive("line1\nline2").toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		JsonPrimitive booleanValue = new JsonPrimitive(true);
		JsonPrimitive numberValue = new JsonPrimitive(42);
		JsonPrimitive stringValue = new JsonPrimitive("test");
		
		assertThrows(NullPointerException.class, () -> booleanValue.toString(null));
		assertThrows(NullPointerException.class, () -> numberValue.toString(null));
		assertThrows(NullPointerException.class, () -> stringValue.toString(null));
		
		assertEquals("true", booleanValue.toString(CUSTOM_CONFIG));
		assertEquals("42", numberValue.toString(CUSTOM_CONFIG));
		assertEquals("\"test\"", stringValue.toString(CUSTOM_CONFIG));
	}
	
	@Test
	void toStringTypeParsing() {
		assertEquals("\"true\"", new JsonPrimitive("true").toString());
		assertEquals("\"false\"", new JsonPrimitive("false").toString());
		
		assertEquals("\"123\"", new JsonPrimitive("123").toString());
		assertEquals("\"3.14\"", new JsonPrimitive("3.14").toString());
		assertEquals("\"-42\"", new JsonPrimitive("-42").toString());
		
		assertEquals("\"not_a_boolean\"", new JsonPrimitive("not_a_boolean").toString());
		assertEquals("\"123abc\"", new JsonPrimitive("123abc").toString());
		assertEquals("\"\"", new JsonPrimitive("").toString());
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
