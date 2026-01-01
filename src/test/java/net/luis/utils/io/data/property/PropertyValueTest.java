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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.PropertyTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyValue}.<br>
 *
 * @author Luis-St
 */
class PropertyValueTest {
	
	@Test
	void constructorWithBoolean() {
		PropertyValue trueValue = new PropertyValue(true);
		PropertyValue falseValue = new PropertyValue(false);
		
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructorWithNumbers() {
		assertDoesNotThrow(() -> new PropertyValue((byte) 1));
		assertDoesNotThrow(() -> new PropertyValue((short) 2));
		assertDoesNotThrow(() -> new PropertyValue(3));
		assertDoesNotThrow(() -> new PropertyValue(4L));
		assertDoesNotThrow(() -> new PropertyValue(5.5f));
		assertDoesNotThrow(() -> new PropertyValue(6.6));
		assertDoesNotThrow(() -> new PropertyValue(new BigInteger("123456789")));
		assertDoesNotThrow(() -> new PropertyValue(new BigDecimal("123.456")));
		
		assertThrows(NullPointerException.class, () -> new PropertyValue((Number) null));
	}
	
	@Test
	void constructorWithCharacter() {
		PropertyValue charValue = new PropertyValue('a');
		assertEquals("a", charValue.getAsString());
		
		PropertyValue specialChar = new PropertyValue('\n');
		assertEquals("\n", specialChar.getAsString());
	}
	
	@Test
	void constructorWithString() {
		PropertyValue stringValue = new PropertyValue("test string");
		assertEquals("test string", stringValue.getAsString());
		
		PropertyValue emptyString = new PropertyValue("");
		assertEquals("", emptyString.getAsString());
		
		assertThrows(NullPointerException.class, () -> new PropertyValue((String) null));
	}
	
	@Test
	void constructorWithSpecialStringValues() {
		PropertyValue booleanLikeString = new PropertyValue("true");
		assertEquals("true", booleanLikeString.getAsString());
		
		PropertyValue numberLikeString = new PropertyValue("123");
		assertEquals("123", numberLikeString.getAsString());
		
		PropertyValue floatLikeString = new PropertyValue("3.14");
		assertEquals("3.14", floatLikeString.getAsString());
	}
	
	@Test
	void propertyElementTypeChecks() {
		PropertyValue value = new PropertyValue(42);
		
		assertFalse(value.isPropertyNull());
		assertFalse(value.isPropertyObject());
		assertFalse(value.isPropertyArray());
		assertTrue(value.isPropertyValue());
	}
	
	@Test
	void propertyElementConversions() {
		PropertyValue value = new PropertyValue("test");
		
		assertThrows(PropertyTypeException.class, value::getAsPropertyObject);
		assertThrows(PropertyTypeException.class, value::getAsPropertyArray);
		assertSame(value, value.getAsPropertyValue());
	}
	
	@Test
	void getAsStringFromDifferentTypes() {
		assertEquals("true", new PropertyValue(true).getAsString());
		assertEquals("false", new PropertyValue(false).getAsString());
		assertEquals("42", new PropertyValue(42).getAsString());
		assertEquals("3.14", new PropertyValue(3.14).getAsString());
		assertEquals("1.0", new PropertyValue(1.0f).getAsString());
		assertEquals("test", new PropertyValue("test").getAsString());
		assertEquals("123456789", new PropertyValue(new BigInteger("123456789")).getAsString());
		assertEquals("123.456", new PropertyValue(new BigDecimal("123.456")).getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new PropertyValue(true).getAsBoolean());
		assertFalse(new PropertyValue(false).getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(PropertyTypeException.class, () -> new PropertyValue("true").getAsBoolean());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue("false").getAsBoolean());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue("test").getAsBoolean());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue(1).getAsBoolean());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue(0).getAsBoolean());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue(3.14).getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals((byte) 1, new PropertyValue((byte) 1).getAsNumber());
		assertEquals((short) 2, new PropertyValue((short) 2).getAsNumber());
		assertEquals(3, new PropertyValue(3).getAsNumber());
		assertEquals(4L, new PropertyValue(4L).getAsNumber());
		assertEquals(5.5f, new PropertyValue(5.5f).getAsNumber());
		assertEquals(6.6, new PropertyValue(6.6).getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(PropertyTypeException.class, () -> new PropertyValue(false).getAsNumber());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue(true).getAsNumber());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue("test").getAsNumber());
		assertThrows(PropertyTypeException.class, () -> new PropertyValue("123").getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		PropertyValue intValue = new PropertyValue(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertEquals(1, new PropertyValue(1L).getAsInteger());
		assertEquals(1L, new PropertyValue((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		PropertyValue booleanValue = new PropertyValue(true);
		PropertyValue floatValue = new PropertyValue(3.14f);
		PropertyValue doubleValue = new PropertyValue(3.14);
		
		assertThrows(PropertyTypeException.class, booleanValue::getAsByte);
		assertThrows(PropertyTypeException.class, booleanValue::getAsShort);
		assertThrows(PropertyTypeException.class, booleanValue::getAsInteger);
		assertThrows(PropertyTypeException.class, booleanValue::getAsLong);
		
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
		assertEquals(1.0f, new PropertyValue((byte) 1).getAsFloat());
		assertEquals(1.0f, new PropertyValue((short) 1).getAsFloat());
		assertEquals(1.0f, new PropertyValue(1).getAsFloat());
		assertEquals(1.0f, new PropertyValue(1L).getAsFloat());
		
		assertEquals(1.0, new PropertyValue((byte) 1).getAsDouble());
		assertEquals(1.0, new PropertyValue((short) 1).getAsDouble());
		assertEquals(1.0, new PropertyValue(1).getAsDouble());
		assertEquals(1.0, new PropertyValue(1L).getAsDouble());
		
		assertEquals(3.14f, new PropertyValue(3.14f).getAsFloat());
		assertEquals(3.14, new PropertyValue(3.14).getAsDouble());
		assertEquals(3.14f, new PropertyValue(3.14).getAsFloat());
		assertEquals(3.14, new PropertyValue(3.14f).getAsDouble(), 1.0e-6);
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		PropertyValue booleanValue = new PropertyValue(true);
		
		assertThrows(PropertyTypeException.class, booleanValue::getAsFloat);
		assertThrows(PropertyTypeException.class, booleanValue::getAsDouble);
	}
	
	@Test
	void typeCheckMethods() {
		PropertyValue boolValue = new PropertyValue(true);
		assertTrue(boolValue.isBoolean());
		assertFalse(boolValue.isNumber());
		assertFalse(boolValue.isString());
		
		PropertyValue intValue = new PropertyValue(42);
		assertFalse(intValue.isBoolean());
		assertTrue(intValue.isNumber());
		assertTrue(intValue.isInteger());
		assertFalse(intValue.isString());
		
		PropertyValue stringValue = new PropertyValue("test");
		assertFalse(stringValue.isBoolean());
		assertFalse(stringValue.isNumber());
		assertTrue(stringValue.isString());
		
		PropertyValue byteValue = new PropertyValue((byte) 1);
		assertTrue(byteValue.isByte());
		
		PropertyValue shortValue = new PropertyValue((short) 2);
		assertTrue(shortValue.isShort());
		
		PropertyValue longValue = new PropertyValue(3L);
		assertTrue(longValue.isLong());
		
		PropertyValue floatValue = new PropertyValue(4.5f);
		assertTrue(floatValue.isFloat());
		
		PropertyValue doubleValue = new PropertyValue(5.5);
		assertTrue(doubleValue.isDouble());
	}
	
	@Test
	void getAsParsedPropertyValueNoString() {
		PropertyValue booleanValue = new PropertyValue(true);
		PropertyValue numberValue = new PropertyValue(42);
		PropertyValue floatValue = new PropertyValue(3.14);
		
		assertSame(booleanValue, booleanValue.getAsParsedPropertyValue());
		assertSame(numberValue, numberValue.getAsParsedPropertyValue());
		assertSame(floatValue, floatValue.getAsParsedPropertyValue());
	}
	
	@Test
	void getAsParsedPropertyValueFromString() {
		PropertyValue trueString = new PropertyValue("true");
		PropertyValue falseString = new PropertyValue("false");
		PropertyValue intString = new PropertyValue("42");
		PropertyValue floatString = new PropertyValue("3.14");
		PropertyValue normalString = new PropertyValue("test");
		
		PropertyValue parsedTrue = trueString.getAsParsedPropertyValue();
		assertTrue(parsedTrue.getAsBoolean());
		
		PropertyValue parsedFalse = falseString.getAsParsedPropertyValue();
		assertFalse(parsedFalse.getAsBoolean());
		
		PropertyValue parsedInt = intString.getAsParsedPropertyValue();
		assertEquals(42, parsedInt.getAsInteger());
		
		PropertyValue parsedFloat = floatString.getAsParsedPropertyValue();
		assertEquals(3.14, parsedFloat.getAsDouble());
		
		PropertyValue parsedNormal = normalString.getAsParsedPropertyValue();
		assertEquals("test", parsedNormal.getAsString());
	}
	
	@Test
	void getAsParsedPropertyValueCaseInsensitiveBoolean() {
		PropertyValue upperTrue = new PropertyValue("TRUE");
		PropertyValue mixedTrue = new PropertyValue("True");
		PropertyValue upperFalse = new PropertyValue("FALSE");
		PropertyValue mixedFalse = new PropertyValue("False");
		
		assertTrue(upperTrue.getAsParsedPropertyValue().getAsBoolean());
		assertTrue(mixedTrue.getAsParsedPropertyValue().getAsBoolean());
		assertFalse(upperFalse.getAsParsedPropertyValue().getAsBoolean());
		assertFalse(mixedFalse.getAsParsedPropertyValue().getAsBoolean());
	}
	
	@Test
	void equalsAndHashCode() {
		PropertyValue value1 = new PropertyValue("test");
		PropertyValue value2 = new PropertyValue("test");
		PropertyValue value3 = new PropertyValue("different");
		PropertyValue value4 = new PropertyValue(42);
		PropertyValue value5 = new PropertyValue(42);
		
		assertEquals(value1, value2);
		assertEquals(value1.hashCode(), value2.hashCode());
		assertNotEquals(value1, value3);
		
		assertEquals(value4, value5);
		assertEquals(value4.hashCode(), value5.hashCode());
		
		assertNotEquals(value1, value4);
		assertNotEquals(value1, null);
		assertNotEquals(value1, "not a property value");
		
		assertEquals(value1, value1);
		
		PropertyValue boolTrue1 = new PropertyValue(true);
		PropertyValue boolTrue2 = new PropertyValue(true);
		PropertyValue boolFalse = new PropertyValue(false);
		
		assertEquals(boolTrue1, boolTrue2);
		assertEquals(boolTrue1.hashCode(), boolTrue2.hashCode());
		assertNotEquals(boolTrue1, boolFalse);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("false", new PropertyValue(false).toString());
		assertEquals("true", new PropertyValue(true).toString());
		
		assertEquals("42", new PropertyValue(42).toString());
		assertEquals("3.14", new PropertyValue(3.14).toString());
		assertEquals("1", new PropertyValue((byte) 1).toString());
		assertEquals("2", new PropertyValue((short) 2).toString());
		assertEquals("3", new PropertyValue(3L).toString());
		assertEquals("4.5", new PropertyValue(4.5f).toString());
		
		assertEquals("test", new PropertyValue("test").toString());
		assertEquals("", new PropertyValue("").toString());
		assertEquals("true", new PropertyValue("true").toString());
		assertEquals("42", new PropertyValue("42").toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		PropertyValue booleanValue = new PropertyValue(true);
		PropertyValue numberValue = new PropertyValue(42);
		PropertyValue stringValue = new PropertyValue("test");
		
		assertThrows(NullPointerException.class, () -> booleanValue.toString(null));
		assertThrows(NullPointerException.class, () -> numberValue.toString(null));
		assertThrows(NullPointerException.class, () -> stringValue.toString(null));
		
		assertEquals("true", booleanValue.toString(PropertyConfig.DEFAULT));
		assertEquals("42", numberValue.toString(PropertyConfig.DEFAULT));
		assertEquals("test", stringValue.toString(PropertyConfig.DEFAULT));
	}
	
	@Test
	void extremeValues() {
		PropertyValue maxInt = new PropertyValue(Integer.MAX_VALUE);
		PropertyValue minInt = new PropertyValue(Integer.MIN_VALUE);
		PropertyValue maxLong = new PropertyValue(Long.MAX_VALUE);
		PropertyValue minLong = new PropertyValue(Long.MIN_VALUE);
		PropertyValue maxDouble = new PropertyValue(Double.MAX_VALUE);
		PropertyValue minDouble = new PropertyValue(Double.MIN_VALUE);
		
		assertEquals(Integer.MAX_VALUE, maxInt.getAsInteger());
		assertEquals(Integer.MIN_VALUE, minInt.getAsInteger());
		assertEquals(Long.MAX_VALUE, maxLong.getAsLong());
		assertEquals(Long.MIN_VALUE, minLong.getAsLong());
		assertEquals(Double.MAX_VALUE, maxDouble.getAsDouble());
		assertEquals(Double.MIN_VALUE, minDouble.getAsDouble());
		
		String longString = "a".repeat(10000);
		PropertyValue longStringValue = new PropertyValue(longString);
		assertEquals(longString, longStringValue.getAsString());
	}
}
