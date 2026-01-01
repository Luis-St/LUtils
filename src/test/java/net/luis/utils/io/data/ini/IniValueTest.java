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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniValue}.<br>
 *
 * @author Luis-St
 */
class IniValueTest {
	
	private static final IniConfig CUSTOM_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithBoolean() {
		IniValue trueValue = new IniValue(true);
		IniValue falseValue = new IniValue(false);
		
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructorWithNumbers() {
		assertDoesNotThrow(() -> new IniValue((byte) 1));
		assertDoesNotThrow(() -> new IniValue((short) 2));
		assertDoesNotThrow(() -> new IniValue(3));
		assertDoesNotThrow(() -> new IniValue(4L));
		assertDoesNotThrow(() -> new IniValue(5.5f));
		assertDoesNotThrow(() -> new IniValue(6.6));
		assertDoesNotThrow(() -> new IniValue(new BigInteger("123456789")));
		assertDoesNotThrow(() -> new IniValue(new BigDecimal("123.456")));
		
		assertThrows(NullPointerException.class, () -> new IniValue((Number) null));
	}
	
	@Test
	void constructorWithCharacter() {
		IniValue charValue = new IniValue('a');
		assertEquals("a", charValue.getAsString());
		
		IniValue digitChar = new IniValue('5');
		assertEquals("5", digitChar.getAsString());
	}
	
	@Test
	void constructorWithString() {
		IniValue stringValue = new IniValue("test string");
		assertEquals("test string", stringValue.getAsString());
		
		IniValue emptyString = new IniValue("");
		assertEquals("", emptyString.getAsString());
		
		assertThrows(NullPointerException.class, () -> new IniValue((String) null));
	}
	
	@Test
	void constructorWithSpecialStringValues() {
		IniValue booleanLikeString = new IniValue("true");
		assertEquals("true", booleanLikeString.getAsString());
		
		IniValue numberLikeString = new IniValue("123");
		assertEquals("123", numberLikeString.getAsString());
		
		IniValue floatLikeString = new IniValue("3.14");
		assertEquals("3.14", floatLikeString.getAsString());
	}
	
	@Test
	void iniElementTypeChecks() {
		IniValue value = new IniValue(42);
		
		assertFalse(value.isIniNull());
		assertTrue(value.isIniValue());
		assertFalse(value.isIniSection());
		assertFalse(value.isIniDocument());
	}
	
	@Test
	void iniElementConversions() {
		IniValue value = new IniValue("test");
		
		assertThrows(IniTypeException.class, value::getAsIniSection);
		assertThrows(IniTypeException.class, value::getAsIniDocument);
		assertSame(value, value.getAsIniValue());
	}
	
	@Test
	void getAsStringFromDifferentTypes() {
		assertEquals("true", new IniValue(true).getAsString());
		assertEquals("false", new IniValue(false).getAsString());
		assertEquals("42", new IniValue(42).getAsString());
		assertEquals("3.14", new IniValue(3.14).getAsString());
		assertEquals("1.0", new IniValue(1.0f).getAsString());
		assertEquals("test", new IniValue("test").getAsString());
		assertEquals("123456789", new IniValue(new BigInteger("123456789")).getAsString());
		assertEquals("123.456", new IniValue(new BigDecimal("123.456")).getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new IniValue(true).getAsBoolean());
		assertFalse(new IniValue(false).getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(IniTypeException.class, () -> new IniValue("true").getAsBoolean());
		assertThrows(IniTypeException.class, () -> new IniValue("false").getAsBoolean());
		assertThrows(IniTypeException.class, () -> new IniValue("test").getAsBoolean());
		assertThrows(IniTypeException.class, () -> new IniValue(1).getAsBoolean());
		assertThrows(IniTypeException.class, () -> new IniValue(0).getAsBoolean());
		assertThrows(IniTypeException.class, () -> new IniValue(3.14).getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals((byte) 1, new IniValue((byte) 1).getAsNumber());
		assertEquals((short) 2, new IniValue((short) 2).getAsNumber());
		assertEquals(3, new IniValue(3).getAsNumber());
		assertEquals(4L, new IniValue(4L).getAsNumber());
		assertEquals(5.5f, new IniValue(5.5f).getAsNumber());
		assertEquals(6.6, new IniValue(6.6).getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(IniTypeException.class, () -> new IniValue(false).getAsNumber());
		assertThrows(IniTypeException.class, () -> new IniValue(true).getAsNumber());
		assertThrows(IniTypeException.class, () -> new IniValue("test").getAsNumber());
		assertThrows(IniTypeException.class, () -> new IniValue("123").getAsNumber());
		assertThrows(IniTypeException.class, () -> new IniValue("").getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		IniValue intValue = new IniValue(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertEquals(1, new IniValue(1L).getAsInteger());
		assertEquals(1L, new IniValue((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		IniValue booleanValue = new IniValue(true);
		IniValue stringValue = new IniValue("test");
		
		assertThrows(IniTypeException.class, booleanValue::getAsByte);
		assertThrows(IniTypeException.class, booleanValue::getAsShort);
		assertThrows(IniTypeException.class, booleanValue::getAsInteger);
		assertThrows(IniTypeException.class, booleanValue::getAsLong);
		
		assertThrows(IniTypeException.class, stringValue::getAsByte);
		assertThrows(IniTypeException.class, stringValue::getAsShort);
		assertThrows(IniTypeException.class, stringValue::getAsInteger);
		assertThrows(IniTypeException.class, stringValue::getAsLong);
	}
	
	@Test
	void getAsIntegralTypesFromFloatingPoint() {
		IniValue floatValue = new IniValue(3.14f);
		IniValue doubleValue = new IniValue(3.14);
		
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
		assertEquals(1.0f, new IniValue((byte) 1).getAsFloat());
		assertEquals(1.0f, new IniValue((short) 1).getAsFloat());
		assertEquals(1.0f, new IniValue(1).getAsFloat());
		assertEquals(1.0f, new IniValue(1L).getAsFloat());
		
		assertEquals(1.0, new IniValue((byte) 1).getAsDouble());
		assertEquals(1.0, new IniValue((short) 1).getAsDouble());
		assertEquals(1.0, new IniValue(1).getAsDouble());
		assertEquals(1.0, new IniValue(1L).getAsDouble());
		
		assertEquals(3.14f, new IniValue(3.14f).getAsFloat());
		assertEquals(3.14, new IniValue(3.14).getAsDouble());
		assertEquals(3.14f, new IniValue(3.14).getAsFloat());
		assertEquals(3.14, new IniValue(3.14f).getAsDouble(), 1.0e-6);
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		IniValue booleanValue = new IniValue(true);
		IniValue stringValue = new IniValue("test");
		
		assertThrows(IniTypeException.class, booleanValue::getAsFloat);
		assertThrows(IniTypeException.class, booleanValue::getAsDouble);
		
		assertThrows(IniTypeException.class, stringValue::getAsFloat);
		assertThrows(IniTypeException.class, stringValue::getAsDouble);
	}
	
	@Test
	void getAsParsedIniValueNoString() {
		IniValue booleanValue = new IniValue(true);
		IniValue numberValue = new IniValue(42);
		IniValue floatValue = new IniValue(3.14);
		
		assertSame(booleanValue, booleanValue.getAsParsedIniValue());
		assertSame(numberValue, numberValue.getAsParsedIniValue());
		assertSame(floatValue, floatValue.getAsParsedIniValue());
	}
	
	@Test
	void getAsParsedIniValueFromString() {
		IniValue trueString = new IniValue("true");
		IniValue falseString = new IniValue("false");
		IniValue intString = new IniValue("42");
		IniValue floatString = new IniValue("3.14");
		IniValue normalString = new IniValue("test");
		
		IniValue parsedTrue = trueString.getAsParsedIniValue();
		assertTrue(parsedTrue.getAsBoolean());
		
		IniValue parsedFalse = falseString.getAsParsedIniValue();
		assertFalse(parsedFalse.getAsBoolean());
		
		IniValue parsedInt = intString.getAsParsedIniValue();
		assertEquals(42, parsedInt.getAsInteger());
		
		IniValue parsedFloat = floatString.getAsParsedIniValue();
		assertEquals(3.14, parsedFloat.getAsDouble());
		
		IniValue parsedNormal = normalString.getAsParsedIniValue();
		assertEquals("test", parsedNormal.getAsString());
	}
	
	@Test
	void getAsParsedIniValueCaseInsensitiveBoolean() {
		assertTrue(new IniValue("TRUE").getAsParsedIniValue().getAsBoolean());
		assertTrue(new IniValue("True").getAsParsedIniValue().getAsBoolean());
		assertFalse(new IniValue("FALSE").getAsParsedIniValue().getAsBoolean());
		assertFalse(new IniValue("False").getAsParsedIniValue().getAsBoolean());
	}
	
	@Test
	void equalsAndHashCode() {
		IniValue value1 = new IniValue("test");
		IniValue value2 = new IniValue("test");
		IniValue value3 = new IniValue("different");
		IniValue value4 = new IniValue(42);
		IniValue value5 = new IniValue(42);
		
		assertEquals(value1, value2);
		assertEquals(value1.hashCode(), value2.hashCode());
		assertNotEquals(value1, value3);
		
		assertEquals(value4, value5);
		assertEquals(value4.hashCode(), value5.hashCode());
		
		assertNotEquals(value1, value4);
		assertNotEquals(value1, null);
		assertNotEquals(value1, "not a value");
		
		assertEquals(value1, value1);
		
		IniValue boolTrue1 = new IniValue(true);
		IniValue boolTrue2 = new IniValue(true);
		IniValue boolFalse = new IniValue(false);
		
		assertEquals(boolTrue1, boolTrue2);
		assertEquals(boolTrue1.hashCode(), boolTrue2.hashCode());
		assertNotEquals(boolTrue1, boolFalse);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("false", new IniValue(false).toString());
		assertEquals("true", new IniValue(true).toString());
		
		assertEquals("42", new IniValue(42).toString());
		assertEquals("3.14", new IniValue(3.14).toString());
		assertEquals("1", new IniValue((byte) 1).toString());
		assertEquals("2", new IniValue((short) 2).toString());
		assertEquals("3", new IniValue(3L).toString());
		assertEquals("4.5", new IniValue(4.5f).toString());
		
		assertEquals("test", new IniValue("test").toString());
		assertEquals("", new IniValue("").toString());
		assertEquals("true", new IniValue("true").toString());
		assertEquals("42", new IniValue("42").toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		IniValue booleanValue = new IniValue(true);
		IniValue numberValue = new IniValue(42);
		IniValue stringValue = new IniValue("test");
		
		assertThrows(NullPointerException.class, () -> booleanValue.toString(null));
		assertThrows(NullPointerException.class, () -> numberValue.toString(null));
		assertThrows(NullPointerException.class, () -> stringValue.toString(null));
		
		assertEquals("true", booleanValue.toString(CUSTOM_CONFIG));
		assertEquals("42", numberValue.toString(CUSTOM_CONFIG));
		assertEquals("test", stringValue.toString(CUSTOM_CONFIG));
	}
	
	@Test
	void extremeValues() {
		IniValue maxInt = new IniValue(Integer.MAX_VALUE);
		IniValue minInt = new IniValue(Integer.MIN_VALUE);
		IniValue maxLong = new IniValue(Long.MAX_VALUE);
		IniValue minLong = new IniValue(Long.MIN_VALUE);
		IniValue maxDouble = new IniValue(Double.MAX_VALUE);
		IniValue minDouble = new IniValue(Double.MIN_VALUE);
		
		assertEquals(Integer.MAX_VALUE, maxInt.getAsInteger());
		assertEquals(Integer.MIN_VALUE, minInt.getAsInteger());
		assertEquals(Long.MAX_VALUE, maxLong.getAsLong());
		assertEquals(Long.MIN_VALUE, minLong.getAsLong());
		assertEquals(Double.MAX_VALUE, maxDouble.getAsDouble());
		assertEquals(Double.MIN_VALUE, minDouble.getAsDouble());
		
		String longString = "a".repeat(10000);
		IniValue longStringValue = new IniValue(longString);
		assertEquals(longString, longStringValue.getAsString());
	}
	
	@Test
	void primitiveTypeChecks() {
		IniValue boolValue = new IniValue(true);
		IniValue byteValue = new IniValue((byte) 1);
		IniValue shortValue = new IniValue((short) 2);
		IniValue intValue = new IniValue(3);
		IniValue longValue = new IniValue(4L);
		IniValue floatValue = new IniValue(5.5f);
		IniValue doubleValue = new IniValue(6.6);
		IniValue stringValue = new IniValue("test");
		
		assertTrue(boolValue.isBoolean());
		assertFalse(boolValue.isNumber());
		assertFalse(boolValue.isString());
		
		assertFalse(byteValue.isBoolean());
		assertTrue(byteValue.isNumber());
		assertTrue(byteValue.isByte());
		assertFalse(byteValue.isString());
		
		assertTrue(shortValue.isShort());
		assertTrue(intValue.isInteger());
		assertTrue(longValue.isLong());
		assertTrue(floatValue.isFloat());
		assertTrue(doubleValue.isDouble());
		
		assertFalse(stringValue.isBoolean());
		assertFalse(stringValue.isNumber());
		assertTrue(stringValue.isString());
	}
}
