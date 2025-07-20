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

package net.luis.utils.io.data.xml;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlValue}.<br>
 *
 * @author Luis-St
 */
class XmlValueTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlValue(null, true));
		assertThrows(NullPointerException.class, () -> new XmlValue(null, (Number) null));
		assertThrows(NullPointerException.class, () -> new XmlValue(null, (String) null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, true));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, (Number) null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, (String) null));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlValue("test", true));
		assertDoesNotThrow(() -> new XmlValue("test", false));
		assertDoesNotThrow(() -> new XmlValue("test", 42));
		assertDoesNotThrow(() -> new XmlValue("test", 3.14));
		assertDoesNotThrow(() -> new XmlValue("test", (Number) null));
		assertDoesNotThrow(() -> new XmlValue("test", "value"));
		assertDoesNotThrow(() -> new XmlValue("test", (String) null));
		assertDoesNotThrow(() -> new XmlValue("test", new XmlAttributes(), true));
		assertDoesNotThrow(() -> new XmlValue("test", new XmlAttributes(), "value"));
	}
	
	@Test
	void constructorWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("", true));
		assertThrows(IllegalArgumentException.class, () -> new XmlValue(" ", true));
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("1invalid", true));
	}
	
	@Test
	void getElementType() {
		assertEquals("xml value", new XmlValue("test", true).getElementType());
		assertEquals("xml value", new XmlValue("test", "value").getElementType());
		assertEquals("xml value", new XmlValue("test", 42).getElementType());
	}
	
	@Test
	void isSelfClosing() {
		assertFalse(new XmlValue("test", true).isSelfClosing());
		assertFalse(new XmlValue("test", "value").isSelfClosing());
		assertFalse(new XmlValue("test", 42).isSelfClosing());
	}
	
	@Test
	void getRawValue() {
		assertEquals("true", new XmlValue("test", true).getRawValue());
		assertEquals("false", new XmlValue("test", false).getRawValue());
		assertEquals("1", new XmlValue("test", 1).getRawValue());
		assertEquals("42", new XmlValue("test", 42).getRawValue());
		assertEquals("-17", new XmlValue("test", -17).getRawValue());
		assertEquals("1.0", new XmlValue("test", 1.0).getRawValue());
		assertEquals("3.14", new XmlValue("test", 3.14).getRawValue());
		assertEquals("-2.5", new XmlValue("test", -2.5).getRawValue());
		assertEquals("test", new XmlValue("test", "test").getRawValue());
		assertEquals("value", new XmlValue("test", "value").getRawValue());
		assertEquals("null", new XmlValue("test", (Number) null).getRawValue());
		assertEquals("null", new XmlValue("test", (String) null).getRawValue());
		assertEquals("&lt;test&gt;", new XmlValue("test", "<test>").getRawValue());
		assertEquals("&amp;&quot;&apos;", new XmlValue("test", "&\"'").getRawValue());
		assertEquals("", new XmlValue("test", "").getRawValue());
		assertEquals(" ", new XmlValue("test", " ").getRawValue());
	}
	
	@Test
	void getUnescapedValue() {
		assertEquals("true", new XmlValue("test", true).getUnescapedValue());
		assertEquals("false", new XmlValue("test", false).getUnescapedValue());
		assertEquals("1", new XmlValue("test", 1).getUnescapedValue());
		assertEquals("1.0", new XmlValue("test", 1.0).getUnescapedValue());
		assertEquals("test", new XmlValue("test", "test").getUnescapedValue());
		assertEquals("null", new XmlValue("test", (Number) null).getUnescapedValue());
		assertEquals("null", new XmlValue("test", (String) null).getUnescapedValue());
		assertEquals("<test>", new XmlValue("test", "<test>").getUnescapedValue());
		assertEquals("&\"'", new XmlValue("test", "&\"'").getUnescapedValue());
		assertEquals("", new XmlValue("test", "").getUnescapedValue());
		assertEquals(" ", new XmlValue("test", " ").getUnescapedValue());
	}
	
	@Test
	void getAsString() {
		assertEquals("true", new XmlValue("test", true).getAsString());
		assertEquals("false", new XmlValue("test", false).getAsString());
		assertEquals("1", new XmlValue("test", 1).getAsString());
		assertEquals("1.0", new XmlValue("test", 1.0).getAsString());
		assertEquals("test", new XmlValue("test", "test").getAsString());
		assertEquals("<test>", new XmlValue("test", "<test>").getAsString());
		assertEquals("&\"'", new XmlValue("test", "&\"'").getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new XmlValue("test", true).getAsBoolean());
		assertFalse(new XmlValue("test", false).getAsBoolean());
		assertTrue(new XmlValue("test", "true").getAsBoolean());
		assertFalse(new XmlValue("test", "false").getAsBoolean());
		assertTrue(new XmlValue("test", "TRUE").getAsBoolean());
		assertFalse(new XmlValue("test", "FALSE").getAsBoolean());
		assertTrue(new XmlValue("test", "True").getAsBoolean());
		assertFalse(new XmlValue("test", "False").getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 0).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "yes").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "no").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "0").getAsBoolean());
	}
	
	@Test
	void getAsBooleanWithDefault() {
		assertTrue(new XmlValue("test", true).getAsBoolean(false));
		assertFalse(new XmlValue("test", false).getAsBoolean(true));
		assertTrue(new XmlValue("test", "true").getAsBoolean(false));
		assertFalse(new XmlValue("test", "false").getAsBoolean(true));
		assertTrue(new XmlValue("test", 1).getAsBoolean(true));
		assertFalse(new XmlValue("test", "invalid").getAsBoolean(false));
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals(1L, new XmlValue("test", 1).getAsNumber());
		assertEquals(42L, new XmlValue("test", 42).getAsNumber());
		assertEquals(-17L, new XmlValue("test", -17).getAsNumber());
		assertEquals(1.0, new XmlValue("test", 1.0).getAsNumber());
		assertEquals(3.14, new XmlValue("test", 3.14).getAsNumber());
		assertEquals(-2.5, new XmlValue("test", -2.5).getAsNumber());
		assertEquals(1L, new XmlValue("test", "1").getAsNumber());
		assertEquals(1.0, new XmlValue("test", "1.0").getAsNumber());
		assertEquals(3.14, new XmlValue("test", "3.14").getAsNumber());
		assertEquals(-42L, new XmlValue("test", "-42").getAsNumber());
		assertEquals(0L, new XmlValue("test", "0").getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "text").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", " ").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.2.3").getAsNumber());
	}
	
	@Test
	void getAsNumberWithDefault() {
		assertEquals(1L, new XmlValue("test", 1).getAsNumber(0));
		assertEquals(1.0, new XmlValue("test", 1.0).getAsNumber(0));
		assertEquals(1L, new XmlValue("test", "1").getAsNumber(0));
		assertEquals(1.0, new XmlValue("test", "1.0").getAsNumber(0));
		assertEquals(0, new XmlValue("test", "test").getAsNumber(0));
	}
	
	@Test
	void getAsByteValid() {
		assertEquals((byte) 1, new XmlValue("test", 1).getAsByte());
		assertEquals((byte) 127, new XmlValue("test", 127).getAsByte());
		assertEquals((byte) -128, new XmlValue("test", -128).getAsByte());
		assertEquals((byte) 1, new XmlValue("test", "1").getAsByte());
		assertEquals((byte) 42, new XmlValue("test", "42").getAsByte());
		assertEquals((byte) -17, new XmlValue("test", "-17").getAsByte());
		assertEquals((byte) 0, new XmlValue("test", "0").getAsByte());
	}
	
	@Test
	void getAsByteInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 128).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", -129).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "128").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "-129").getAsByte());
	}
	
	@Test
	void getAsByteWithDefault() {
		assertEquals((byte) 1, new XmlValue("test", 1).getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", 1.0).getAsByte((byte) 0));
		assertEquals((byte) 1, new XmlValue("test", "1").getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", "1.0").getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", "test").getAsByte((byte) 0));
	}
	
	@Test
	void getAsShortValid() {
		assertEquals((short) 1, new XmlValue("test", 1).getAsShort());
		assertEquals((short) 32767, new XmlValue("test", 32767).getAsShort());
		assertEquals((short) -32768, new XmlValue("test", -32768).getAsShort());
		assertEquals((short) 1000, new XmlValue("test", "1000").getAsShort());
		assertEquals((short) -1000, new XmlValue("test", "-1000").getAsShort());
		assertEquals((short) 0, new XmlValue("test", "0").getAsShort());
	}
	
	@Test
	void getAsShortInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 32768).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", -32769).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "32768").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "-32769").getAsShort());
	}
	
	@Test
	void getAsShortWithDefault() {
		assertEquals((short) 1, new XmlValue("test", 1).getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", 1.0).getAsShort((short) 0));
		assertEquals((short) 1, new XmlValue("test", "1").getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", "1.0").getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", "test").getAsShort((short) 0));
	}
	
	@Test
	void getAsIntegerValid() {
		assertEquals(1, new XmlValue("test", 1).getAsInteger());
		assertEquals(2147483647, new XmlValue("test", 2147483647).getAsInteger());
		assertEquals(-2147483648, new XmlValue("test", -2147483648).getAsInteger());
		assertEquals(100000, new XmlValue("test", "100000").getAsInteger());
		assertEquals(-100000, new XmlValue("test", "-100000").getAsInteger());
		assertEquals(0, new XmlValue("test", "0").getAsInteger());
	}
	
	@Test
	void getAsIntegerInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 2147483648L).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", -2147483649L).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "2147483648").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "-2147483649").getAsInteger());
	}
	
	@Test
	void getAsIntegerWithDefault() {
		assertEquals(1, new XmlValue("test", 1).getAsInteger(0));
		assertEquals(0, new XmlValue("test", 1.0).getAsInteger(0));
		assertEquals(1, new XmlValue("test", "1").getAsInteger(0));
		assertEquals(0, new XmlValue("test", "1.0").getAsInteger(0));
		assertEquals(0, new XmlValue("test", "test").getAsInteger(0));
	}
	
	@Test
	void getAsLongValid() {
		assertEquals(1L, new XmlValue("test", 1).getAsLong());
		assertEquals(9223372036854775807L, new XmlValue("test", 9223372036854775807L).getAsLong());
		assertEquals(-9223372036854775808L, new XmlValue("test", -9223372036854775808L).getAsLong());
		assertEquals(1000000000L, new XmlValue("test", "1000000000").getAsLong());
		assertEquals(-1000000000L, new XmlValue("test", "-1000000000").getAsLong());
		assertEquals(0L, new XmlValue("test", "0").getAsLong());
	}
	
	@Test
	void getAsLongInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "invalid").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", Double.MAX_VALUE).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "9223372036854775808").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "-9223372036854775809").getAsLong());
	}
	
	@Test
	void getAsLongWithDefault() {
		assertEquals(1L, new XmlValue("test", 1).getAsLong(0));
		assertEquals(0L, new XmlValue("test", 1.0).getAsLong(0));
		assertEquals(1L, new XmlValue("test", "1").getAsLong(0));
		assertEquals(0L, new XmlValue("test", "1.0").getAsLong(0));
		assertEquals(0L, new XmlValue("test", "test").getAsLong(0));
	}
	
	@Test
	void getAsFloatValid() {
		assertEquals(1.0F, new XmlValue("test", 1).getAsFloat());
		assertEquals(1.0F, new XmlValue("test", 1.0).getAsFloat());
		assertEquals(3.14F, new XmlValue("test", 3.14F).getAsFloat());
		assertEquals(42.0F, new XmlValue("test", "42").getAsFloat());
		assertEquals(3.14F, new XmlValue("test", "3.14").getAsFloat());
		assertEquals(-2.5F, new XmlValue("test", "-2.5").getAsFloat());
		assertEquals(0.0F, new XmlValue("test", "0").getAsFloat());
	}
	
	@Test
	void getAsFloatInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "value").getAsFloat());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "not a number").getAsFloat());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "").getAsFloat());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", " ").getAsFloat());
	}
	
	@Test
	void getAsFloatWithDefault() {
		assertEquals(1.0F, new XmlValue("test", 1).getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", 1.0).getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", "1").getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", "1.0").getAsFloat(0));
		assertEquals(0.0F, new XmlValue("test", "test").getAsFloat(0));
	}
	
	@Test
	void getAsDoubleValid() {
		assertEquals(1.0, new XmlValue("test", 1).getAsDouble());
		assertEquals(1.0, new XmlValue("test", 1.0).getAsDouble());
		assertEquals(3.14, new XmlValue("test", 3.14).getAsDouble());
		assertEquals(42.0, new XmlValue("test", "42").getAsDouble());
		assertEquals(3.14, new XmlValue("test", "3.14").getAsDouble());
		assertEquals(-2.5, new XmlValue("test", "-2.5").getAsDouble());
		assertEquals(0.0, new XmlValue("test", "0").getAsDouble());
	}
	
	@Test
	void getAsDoubleInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "value").getAsDouble());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "not a number").getAsDouble());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "").getAsDouble());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", " ").getAsDouble());
	}
	
	@Test
	void getAsDoubleWithDefault() {
		assertEquals(1.0, new XmlValue("test", 1).getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", 1.0).getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", "1").getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", "1.0").getAsDouble(0));
		assertEquals(0.0, new XmlValue("test", "test").getAsDouble(0));
	}
	
	@Test
	void getAsWithParser() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = string -> new ScopedStringReader(string).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "[true, false]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "true").getAs(parser));
		assertIterableEquals(List.of(), new XmlValue("test", "[]").getAs(parser));
		assertIterableEquals(List.of(true, false), new XmlValue("test", "[true, false]").getAs(parser));
		assertIterableEquals(List.of(false, true, false), new XmlValue("test", "[false, true, false]").getAs(parser));
	}
	
	@Test
	void getAsWithParserAndDefault() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = string -> new ScopedStringReader(string).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "true").getAs(parser, null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "[true, false]").getAs(parser, null));
		assertIterableEquals(List.of(true, false), new XmlValue("test", "[true, false]").getAs(parser, List.of()));
		assertIterableEquals(List.of(), new XmlValue("test", "true").getAs(parser, List.of()));
		assertIterableEquals(List.of(false), new XmlValue("test", "invalid").getAs(parser, List.of(false)));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlValue value1 = new XmlValue("test", "value");
		XmlValue value2 = new XmlValue("test", "value");
		XmlValue value3 = new XmlValue("other", "value");
		XmlValue value4 = new XmlValue("test", "other");
		
		assertEquals(value1, value2);
		assertEquals(value1.hashCode(), value2.hashCode());
		assertNotEquals(value1, value3);
		assertNotEquals(value1, value4);
		
		XmlValue valueWithAttr1 = new XmlValue("test", new XmlAttributes(), "value");
		valueWithAttr1.addAttribute("attr", "test");
		XmlValue valueWithAttr2 = new XmlValue("test", new XmlAttributes(), "value");
		valueWithAttr2.addAttribute("attr", "test");
		
		assertEquals(valueWithAttr1, valueWithAttr2);
		assertEquals(valueWithAttr1.hashCode(), valueWithAttr2.hashCode());
		assertNotEquals(value1, valueWithAttr1);
		
		assertNotEquals(value1, null);
		assertNotEquals(value1, "string");
		assertNotEquals(value1, new XmlElement("test"));
		assertNotEquals(value1, new XmlContainer("test"));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlValue value = new XmlValue("test", true);
		
		assertThrows(NullPointerException.class, () -> value.toString(null));
		assertEquals("<test>true</test>", value.toString());
		assertEquals("<test>true</test>", value.toString(XmlConfig.DEFAULT));
		
		XmlValue valueWithAttr = new XmlValue("test", "value");
		valueWithAttr.addAttribute("attr", "test");
		assertEquals("<test attr=\"test\">value</test>", valueWithAttr.toString(XmlConfig.DEFAULT));
		
		XmlValue emptyValue = new XmlValue("test", "");
		assertEquals("<test></test>", emptyValue.toString(XmlConfig.DEFAULT));
		
		XmlValue escapedValue = new XmlValue("test", "<>&\"'");
		assertEquals("<test>&lt;&gt;&amp;&quot;&apos;</test>", escapedValue.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlValue value = new XmlValue("test", true);
		
		assertEquals("<test>true</test>", value.toString(customConfig));
		
		XmlValue valueWithAttr = new XmlValue("test", "value");
		valueWithAttr.addAttribute("attr", "test");
		assertThrows(IllegalStateException.class, () -> valueWithAttr.toString(customConfig));
		
		XmlValue emptyValue = new XmlValue("test", "");
		assertEquals("<test></test>", emptyValue.toString(customConfig));
	}
	
	@Test
	void toStringWithPrettyPrintAndSimplifyValues() {
		XmlConfig prettyConfig = new XmlConfig(true, true, "\t", true, false, StandardCharsets.UTF_8);
		XmlValue value = new XmlValue("test", "value");
		
		String expected = "<test>" + System.lineSeparator() +
			"\tvalue" + System.lineSeparator() +
			"</test>";
		assertEquals(expected, value.toString(prettyConfig));
		
		XmlValue emptyValue = new XmlValue("test", "");
		expected = "<test>" + System.lineSeparator() +
			"\t" + System.lineSeparator() +
			"</test>";
		assertEquals(expected, emptyValue.toString(prettyConfig));
	}
}
