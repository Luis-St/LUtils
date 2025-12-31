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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.yaml.exception.YamlTypeException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlScalar}.<br>
 *
 * @author Luis-St
 */
class YamlScalarTest {
	
	private static final YamlConfig CUSTOM_CONFIG = new YamlConfig(true, true, "  ", true, false, YamlConfig.NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	
	@Test
	void constructorWithBoolean() {
		YamlScalar trueValue = new YamlScalar(true);
		YamlScalar falseValue = new YamlScalar(false);
		
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructorWithNumbers() {
		assertDoesNotThrow(() -> new YamlScalar((byte) 1));
		assertDoesNotThrow(() -> new YamlScalar((short) 2));
		assertDoesNotThrow(() -> new YamlScalar(3));
		assertDoesNotThrow(() -> new YamlScalar(4L));
		assertDoesNotThrow(() -> new YamlScalar(5.5f));
		assertDoesNotThrow(() -> new YamlScalar(6.6));
		assertDoesNotThrow(() -> new YamlScalar(new BigInteger("123456789")));
		assertDoesNotThrow(() -> new YamlScalar(new BigDecimal("123.456")));
		
		assertThrows(NullPointerException.class, () -> new YamlScalar((Number) null));
	}
	
	@Test
	void constructorWithString() {
		YamlScalar stringValue = new YamlScalar("test string");
		assertEquals("test string", stringValue.getAsString());
		
		YamlScalar emptyString = new YamlScalar("");
		assertEquals("", emptyString.getAsString());
		
		assertThrows(NullPointerException.class, () -> new YamlScalar((String) null));
	}
	
	@Test
	void constructorWithChar() {
		YamlScalar charValue = new YamlScalar('a');
		assertEquals("a", charValue.getAsString());
		
		YamlScalar spaceChar = new YamlScalar(' ');
		assertEquals(" ", spaceChar.getAsString());
	}
	
	@Test
	void constructorWithSpecialStringValues() {
		YamlScalar booleanLikeString = new YamlScalar("true");
		assertEquals("true", booleanLikeString.getAsString());
		
		YamlScalar numberLikeString = new YamlScalar("123");
		assertEquals("123", numberLikeString.getAsString());
		
		YamlScalar floatLikeString = new YamlScalar("3.14");
		assertEquals("3.14", floatLikeString.getAsString());
	}
	
	@Test
	void yamlElementTypeChecks() {
		YamlScalar scalar = new YamlScalar(42);
		
		assertFalse(scalar.isYamlNull());
		assertFalse(scalar.isYamlMapping());
		assertFalse(scalar.isYamlSequence());
		assertTrue(scalar.isYamlScalar());
		assertFalse(scalar.isYamlAnchor());
		assertFalse(scalar.isYamlAlias());
	}
	
	@Test
	void yamlElementConversions() {
		YamlScalar scalar = new YamlScalar("test");
		
		assertThrows(YamlTypeException.class, scalar::getAsYamlMapping);
		assertThrows(YamlTypeException.class, scalar::getAsYamlSequence);
		assertSame(scalar, scalar.getAsYamlScalar());
		assertThrows(YamlTypeException.class, scalar::getAsYamlAnchor);
		assertThrows(YamlTypeException.class, scalar::getAsYamlAlias);
	}
	
	@Test
	void unwrap() {
		YamlScalar scalar = new YamlScalar("test");
		assertSame(scalar, scalar.unwrap());
	}
	
	@Test
	void scalarTypeChecks() {
		YamlScalar booleanScalar = new YamlScalar(true);
		YamlScalar byteScalar = new YamlScalar((byte) 1);
		YamlScalar shortScalar = new YamlScalar((short) 2);
		YamlScalar intScalar = new YamlScalar(3);
		YamlScalar longScalar = new YamlScalar(4L);
		YamlScalar floatScalar = new YamlScalar(5.5f);
		YamlScalar doubleScalar = new YamlScalar(6.6);
		YamlScalar stringScalar = new YamlScalar("test");
		
		assertTrue(booleanScalar.isBoolean());
		assertFalse(booleanScalar.isNumber());
		assertFalse(booleanScalar.isString());
		
		assertTrue(byteScalar.isNumber());
		assertTrue(byteScalar.isByte());
		assertFalse(byteScalar.isBoolean());
		
		assertTrue(shortScalar.isNumber());
		assertTrue(shortScalar.isShort());
		
		assertTrue(intScalar.isNumber());
		assertTrue(intScalar.isInteger());
		
		assertTrue(longScalar.isNumber());
		assertTrue(longScalar.isLong());
		
		assertTrue(floatScalar.isNumber());
		assertTrue(floatScalar.isFloat());
		
		assertTrue(doubleScalar.isNumber());
		assertTrue(doubleScalar.isDouble());
		
		assertTrue(stringScalar.isString());
		assertFalse(stringScalar.isNumber());
		assertFalse(stringScalar.isBoolean());
	}
	
	@Test
	void getAsStringFromDifferentTypes() {
		assertEquals("true", new YamlScalar(true).getAsString());
		assertEquals("false", new YamlScalar(false).getAsString());
		assertEquals("42", new YamlScalar(42).getAsString());
		assertEquals("3.14", new YamlScalar(3.14).getAsString());
		assertEquals("1.0", new YamlScalar(1.0f).getAsString());
		assertEquals("test", new YamlScalar("test").getAsString());
		assertEquals("123456789", new YamlScalar(new BigInteger("123456789")).getAsString());
		assertEquals("123.456", new YamlScalar(new BigDecimal("123.456")).getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new YamlScalar(true).getAsBoolean());
		assertFalse(new YamlScalar(false).getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(YamlTypeException.class, () -> new YamlScalar("true").getAsBoolean());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("false").getAsBoolean());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsBoolean());
		assertThrows(YamlTypeException.class, () -> new YamlScalar(1).getAsBoolean());
		assertThrows(YamlTypeException.class, () -> new YamlScalar(0).getAsBoolean());
		assertThrows(YamlTypeException.class, () -> new YamlScalar(3.14).getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals((byte) 1, new YamlScalar((byte) 1).getAsNumber());
		assertEquals((short) 2, new YamlScalar((short) 2).getAsNumber());
		assertEquals(3, new YamlScalar(3).getAsNumber());
		assertEquals(4L, new YamlScalar(4L).getAsNumber());
		assertEquals(5.5f, new YamlScalar(5.5f).getAsNumber());
		assertEquals(6.6, new YamlScalar(6.6).getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(YamlTypeException.class, () -> new YamlScalar(false).getAsNumber());
		assertThrows(YamlTypeException.class, () -> new YamlScalar(true).getAsNumber());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("test").getAsNumber());
		assertThrows(YamlTypeException.class, () -> new YamlScalar("123").getAsNumber());
	}
	
	@Test
	void getAsIntegralTypesValid() {
		YamlScalar intValue = new YamlScalar(42);
		
		assertEquals((byte) 42, intValue.getAsByte());
		assertEquals((short) 42, intValue.getAsShort());
		assertEquals(42, intValue.getAsInteger());
		assertEquals(42L, intValue.getAsLong());
		
		assertEquals(1, new YamlScalar(1L).getAsInteger());
		assertEquals(1L, new YamlScalar((short) 1).getAsLong());
	}
	
	@Test
	void getAsIntegralTypesInvalid() {
		YamlScalar booleanValue = new YamlScalar(true);
		YamlScalar stringValue = new YamlScalar("42");
		
		assertThrows(YamlTypeException.class, booleanValue::getAsByte);
		assertThrows(YamlTypeException.class, booleanValue::getAsShort);
		assertThrows(YamlTypeException.class, booleanValue::getAsInteger);
		assertThrows(YamlTypeException.class, booleanValue::getAsLong);
		
		assertThrows(YamlTypeException.class, stringValue::getAsByte);
		assertThrows(YamlTypeException.class, stringValue::getAsShort);
		assertThrows(YamlTypeException.class, stringValue::getAsInteger);
		assertThrows(YamlTypeException.class, stringValue::getAsLong);
	}
	
	@Test
	void getAsFloatingPointTypesValid() {
		assertEquals(1.0f, new YamlScalar((byte) 1).getAsFloat());
		assertEquals(1.0f, new YamlScalar((short) 1).getAsFloat());
		assertEquals(1.0f, new YamlScalar(1).getAsFloat());
		assertEquals(1.0f, new YamlScalar(1L).getAsFloat());
		
		assertEquals(1.0, new YamlScalar((byte) 1).getAsDouble());
		assertEquals(1.0, new YamlScalar((short) 1).getAsDouble());
		assertEquals(1.0, new YamlScalar(1).getAsDouble());
		assertEquals(1.0, new YamlScalar(1L).getAsDouble());
		
		assertEquals(3.14f, new YamlScalar(3.14f).getAsFloat());
		assertEquals(3.14, new YamlScalar(3.14).getAsDouble());
		assertEquals(3.14f, new YamlScalar(3.14).getAsFloat());
		assertEquals(3.14, new YamlScalar(3.14f).getAsDouble(), 1.0e-6);
	}
	
	@Test
	void getAsFloatingPointTypesInvalid() {
		YamlScalar booleanValue = new YamlScalar(true);
		YamlScalar stringValue = new YamlScalar("3.14");
		
		assertThrows(YamlTypeException.class, booleanValue::getAsFloat);
		assertThrows(YamlTypeException.class, booleanValue::getAsDouble);
		assertThrows(YamlTypeException.class, stringValue::getAsFloat);
		assertThrows(YamlTypeException.class, stringValue::getAsDouble);
	}
	
	@Test
	void getAsParsedYamlScalarNoString() {
		YamlScalar booleanScalar = new YamlScalar(true);
		YamlScalar numberScalar = new YamlScalar(42);
		YamlScalar floatScalar = new YamlScalar(3.14);
		
		assertSame(booleanScalar, booleanScalar.getAsParsedYamlScalar());
		assertSame(numberScalar, numberScalar.getAsParsedYamlScalar());
		assertSame(floatScalar, floatScalar.getAsParsedYamlScalar());
	}
	
	@Test
	void getAsParsedYamlScalarFromString() {
		YamlScalar trueString = new YamlScalar("true");
		YamlScalar falseString = new YamlScalar("false");
		YamlScalar intString = new YamlScalar("42");
		YamlScalar floatString = new YamlScalar("3.14");
		YamlScalar normalString = new YamlScalar("test");
		
		YamlScalar parsedTrue = trueString.getAsParsedYamlScalar();
		assertTrue(parsedTrue.getAsBoolean());
		
		YamlScalar parsedFalse = falseString.getAsParsedYamlScalar();
		assertFalse(parsedFalse.getAsBoolean());
		
		YamlScalar parsedInt = intString.getAsParsedYamlScalar();
		assertEquals(42L, parsedInt.getAsLong());
		
		YamlScalar parsedFloat = floatString.getAsParsedYamlScalar();
		assertEquals(3.14, parsedFloat.getAsDouble());
		
		YamlScalar parsedNormal = normalString.getAsParsedYamlScalar();
		assertEquals("test", parsedNormal.getAsString());
	}
	
	@Test
	void getAsParsedYamlScalarSpecialValues() {
		YamlScalar infString = new YamlScalar(".inf");
		YamlScalar negInfString = new YamlScalar("-.inf");
		YamlScalar nanString = new YamlScalar(".nan");
		
		YamlScalar parsedInf = infString.getAsParsedYamlScalar();
		assertEquals(Double.POSITIVE_INFINITY, parsedInf.getAsDouble());
		
		YamlScalar parsedNegInf = negInfString.getAsParsedYamlScalar();
		assertEquals(Double.NEGATIVE_INFINITY, parsedNegInf.getAsDouble());
		
		YamlScalar parsedNan = nanString.getAsParsedYamlScalar();
		assertTrue(Double.isNaN(parsedNan.getAsDouble()));
	}
	
	@Test
	void equalsAndHashCode() {
		YamlScalar scalar1 = new YamlScalar("test");
		YamlScalar scalar2 = new YamlScalar("test");
		YamlScalar scalar3 = new YamlScalar("different");
		YamlScalar scalar4 = new YamlScalar(42);
		YamlScalar scalar5 = new YamlScalar(42);
		
		assertEquals(scalar1, scalar2);
		assertEquals(scalar1.hashCode(), scalar2.hashCode());
		assertNotEquals(scalar1, scalar3);
		
		assertEquals(scalar4, scalar5);
		assertEquals(scalar4.hashCode(), scalar5.hashCode());
		
		assertNotEquals(scalar1, scalar4);
		assertNotEquals(scalar1, null);
		assertNotEquals(scalar1, "not a scalar");
		
		assertEquals(scalar1, scalar1);
		
		YamlScalar boolTrue1 = new YamlScalar(true);
		YamlScalar boolTrue2 = new YamlScalar(true);
		YamlScalar boolFalse = new YamlScalar(false);
		
		assertEquals(boolTrue1, boolTrue2);
		assertEquals(boolTrue1.hashCode(), boolTrue2.hashCode());
		assertNotEquals(boolTrue1, boolFalse);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("false", new YamlScalar(false).toString());
		assertEquals("true", new YamlScalar(true).toString());
		
		assertEquals("42", new YamlScalar(42).toString());
		assertEquals("3.14", new YamlScalar(3.14).toString());
		assertEquals("1", new YamlScalar((byte) 1).toString());
		assertEquals("2", new YamlScalar((short) 2).toString());
		assertEquals("3", new YamlScalar(3L).toString());
		assertEquals("4.5", new YamlScalar(4.5f).toString());
		
		assertEquals("test", new YamlScalar("test").toString());
		assertEquals("\"\"", new YamlScalar("").toString());
	}
	
	@Test
	void toStringWithQuoting() {
		assertEquals("\"#comment\"", new YamlScalar("#comment").toString());
		assertEquals("\"&anchor\"", new YamlScalar("&anchor").toString());
		assertEquals("\"*alias\"", new YamlScalar("*alias").toString());
		assertEquals("\"key: value\"", new YamlScalar("key: value").toString());
		assertEquals("\"line1\\nline2\"", new YamlScalar("line1\nline2").toString());
		assertEquals("\"{flow}\"", new YamlScalar("{flow}").toString());
		assertEquals("\"[array]\"", new YamlScalar("[array]").toString());
		assertEquals("\"-dash\"", new YamlScalar("-dash").toString());
		assertEquals("\"?question\"", new YamlScalar("?question").toString());
	}
	
	@Test
	void toStringWithoutQuoting() {
		assertEquals("simple", new YamlScalar("simple").toString());
		assertEquals("hello_world", new YamlScalar("hello_world").toString());
		assertEquals("CamelCase", new YamlScalar("CamelCase").toString());
	}
	
	@Test
	void toStringSpecialFloatValues() {
		assertEquals(".inf", new YamlScalar(Double.POSITIVE_INFINITY).toString());
		assertEquals("-.inf", new YamlScalar(Double.NEGATIVE_INFINITY).toString());
		assertEquals(".nan", new YamlScalar(Double.NaN).toString());
		
		assertEquals(".inf", new YamlScalar(Float.POSITIVE_INFINITY).toString());
		assertEquals("-.inf", new YamlScalar(Float.NEGATIVE_INFINITY).toString());
		assertEquals(".nan", new YamlScalar(Float.NaN).toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		YamlScalar booleanValue = new YamlScalar(true);
		YamlScalar numberValue = new YamlScalar(42);
		YamlScalar stringValue = new YamlScalar("test");
		
		assertThrows(NullPointerException.class, () -> booleanValue.toString(null));
		assertThrows(NullPointerException.class, () -> numberValue.toString(null));
		assertThrows(NullPointerException.class, () -> stringValue.toString(null));
		
		assertEquals("true", booleanValue.toString(CUSTOM_CONFIG));
		assertEquals("42", numberValue.toString(CUSTOM_CONFIG));
		assertEquals("test", stringValue.toString(CUSTOM_CONFIG));
	}
	
	@Test
	void extremeValues() {
		YamlScalar maxInt = new YamlScalar(Integer.MAX_VALUE);
		YamlScalar minInt = new YamlScalar(Integer.MIN_VALUE);
		YamlScalar maxLong = new YamlScalar(Long.MAX_VALUE);
		YamlScalar minLong = new YamlScalar(Long.MIN_VALUE);
		YamlScalar maxDouble = new YamlScalar(Double.MAX_VALUE);
		YamlScalar minDouble = new YamlScalar(Double.MIN_VALUE);
		
		assertEquals(Integer.MAX_VALUE, maxInt.getAsInteger());
		assertEquals(Integer.MIN_VALUE, minInt.getAsInteger());
		assertEquals(Long.MAX_VALUE, maxLong.getAsLong());
		assertEquals(Long.MIN_VALUE, minLong.getAsLong());
		assertEquals(Double.MAX_VALUE, maxDouble.getAsDouble());
		assertEquals(Double.MIN_VALUE, minDouble.getAsDouble());
		
		String longString = "a".repeat(10000);
		YamlScalar longStringScalar = new YamlScalar(longString);
		assertEquals(longString, longStringScalar.getAsString());
	}
	
	@Test
	void numericConversions() {
		YamlScalar floatValue = new YamlScalar(3.14f);
		YamlScalar doubleValue = new YamlScalar(3.14);
		
		assertEquals((byte) 3, floatValue.getAsByte());
		assertEquals((short) 3, floatValue.getAsShort());
		assertEquals(3, floatValue.getAsInteger());
		assertEquals(3L, floatValue.getAsLong());
		
		assertEquals((byte) 3, doubleValue.getAsByte());
		assertEquals((short) 3, doubleValue.getAsShort());
		assertEquals(3, doubleValue.getAsInteger());
		assertEquals(3L, doubleValue.getAsLong());
	}
}
