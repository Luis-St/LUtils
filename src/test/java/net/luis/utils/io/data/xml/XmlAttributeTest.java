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
 * Test class for {@link XmlAttribute}.<br>
 *
 * @author Luis-St
 */
class XmlAttributeTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, true));
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, 0));
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, "value"));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlAttribute("name", true));
		assertDoesNotThrow(() -> new XmlAttribute("name", (Number) null));
		assertDoesNotThrow(() -> new XmlAttribute("name", (String) null));
		assertDoesNotThrow(() -> new XmlAttribute("name", 42));
		assertDoesNotThrow(() -> new XmlAttribute("name", 3.14));
		assertDoesNotThrow(() -> new XmlAttribute("name", "test value"));
	}
	
	@Test
	void constructorWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("", true));
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute(" ", true));
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute(":invalid", true));
	}
	
	@Test
	void getName() {
		assertEquals("name", new XmlAttribute("name", true).getName());
		assertEquals("test-attr", new XmlAttribute("test-attr", "value").getName());
		assertEquals("_underscore", new XmlAttribute("_underscore", 0).getName());
	}
	
	@Test
	void getRawValue() {
		assertEquals("true", new XmlAttribute("name", true).getRawValue());
		assertEquals("false", new XmlAttribute("name", false).getRawValue());
		assertEquals("0", new XmlAttribute("name", 0).getRawValue());
		assertEquals("42", new XmlAttribute("name", 42).getRawValue());
		assertEquals("0.0", new XmlAttribute("name", 0.0).getRawValue());
		assertEquals("3.14", new XmlAttribute("name", 3.14).getRawValue());
		assertEquals("value", new XmlAttribute("name", "value").getRawValue());
		assertEquals("null", new XmlAttribute("name", (Number) null).getRawValue());
		assertEquals("null", new XmlAttribute("name", (String) null).getRawValue());
		assertEquals("&lt;value&gt;", new XmlAttribute("name", "<value>").getRawValue());
		assertEquals("&amp;&quot;&apos;", new XmlAttribute("name", "&\"'").getRawValue());
	}
	
	@Test
	void getUnescapedValue() {
		assertEquals("true", new XmlAttribute("name", true).getUnescapedValue());
		assertEquals("false", new XmlAttribute("name", false).getUnescapedValue());
		assertEquals("0", new XmlAttribute("name", 0).getUnescapedValue());
		assertEquals("0.0", new XmlAttribute("name", 0.0).getUnescapedValue());
		assertEquals("value", new XmlAttribute("name", "value").getUnescapedValue());
		assertEquals("null", new XmlAttribute("name", (Number) null).getUnescapedValue());
		assertEquals("null", new XmlAttribute("name", (String) null).getUnescapedValue());
		assertEquals("<value>", new XmlAttribute("name", "<value>").getUnescapedValue());
		assertEquals("&\"'", new XmlAttribute("name", "&\"'").getUnescapedValue());
	}
	
	@Test
	void getAsString() {
		assertEquals("true", new XmlAttribute("name", true).getAsString());
		assertEquals("0", new XmlAttribute("name", 0).getAsString());
		assertEquals("0.0", new XmlAttribute("name", 0.0).getAsString());
		assertEquals("value", new XmlAttribute("name", "value").getAsString());
		assertEquals("null", new XmlAttribute("name", (Number) null).getAsString());
		assertEquals("null", new XmlAttribute("name", (String) null).getAsString());
		assertEquals("<value>", new XmlAttribute("name", "<value>").getAsString());
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new XmlAttribute("name", true).getAsBoolean());
		assertFalse(new XmlAttribute("name", false).getAsBoolean());
		assertTrue(new XmlAttribute("name", "true").getAsBoolean());
		assertFalse(new XmlAttribute("name", "false").getAsBoolean());
		assertTrue(new XmlAttribute("name", "TRUE").getAsBoolean());
		assertFalse(new XmlAttribute("name", "FALSE").getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0).getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "invalid").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "yes").getAsBoolean());
	}
	
	@Test
	void getAsBooleanWithDefault() {
		assertTrue(new XmlAttribute("name", true).getAsBoolean(false));
		assertFalse(new XmlAttribute("name", false).getAsBoolean(true));
		assertTrue(new XmlAttribute("name", 0).getAsBoolean(true));
		assertFalse(new XmlAttribute("name", "invalid").getAsBoolean(false));
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals(0L, new XmlAttribute("name", 0).getAsNumber());
		assertEquals(42L, new XmlAttribute("name", 42).getAsNumber());
		assertEquals(-17L, new XmlAttribute("name", -17).getAsNumber());
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsNumber());
		assertEquals(3.14, new XmlAttribute("name", 3.14).getAsNumber());
		assertEquals(42L, new XmlAttribute("name", "42").getAsNumber());
		assertEquals(3.14, new XmlAttribute("name", "3.14").getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "not a number").getAsNumber());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "").getAsNumber());
	}
	
	@Test
	void getAsNumberWithDefault() {
		assertEquals(0L, new XmlAttribute("name", 0).getAsNumber(1L));
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsNumber(1.0));
		assertEquals(1L, new XmlAttribute("name", "value").getAsNumber(1L));
		assertEquals(2.0, new XmlAttribute("name", "invalid").getAsNumber(2.0));
	}
	
	@Test
	void getAsByteValid() {
		assertEquals((byte) 0, new XmlAttribute("name", 0).getAsByte());
		assertEquals((byte) 127, new XmlAttribute("name", 127).getAsByte());
		assertEquals((byte) -128, new XmlAttribute("name", -128).getAsByte());
		assertEquals((byte) 42, new XmlAttribute("name", "42").getAsByte());
	}
	
	@Test
	void getAsByteInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 128).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", -129).getAsByte());
	}
	
	@Test
	void getAsByteWithDefault() {
		assertEquals((byte) 0, new XmlAttribute("name", 0).getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", 0.0).getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", "value").getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", 128).getAsByte((byte) 1));
	}
	
	@Test
	void getAsShortValid() {
		assertEquals((short) 0, new XmlAttribute("name", 0).getAsShort());
		assertEquals((short) 32767, new XmlAttribute("name", 32767).getAsShort());
		assertEquals((short) -32768, new XmlAttribute("name", -32768).getAsShort());
		assertEquals((short) 1000, new XmlAttribute("name", "1000").getAsShort());
	}
	
	@Test
	void getAsShortInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 32768).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", -32769).getAsShort());
	}
	
	@Test
	void getAsShortWithDefault() {
		assertEquals((short) 0, new XmlAttribute("name", 0).getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", 0.0).getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", "value").getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", 32768).getAsShort((short) 1));
	}
	
	@Test
	void getAsIntegerValid() {
		assertEquals(0, new XmlAttribute("name", 0).getAsInteger());
		assertEquals(2147483647, new XmlAttribute("name", 2147483647).getAsInteger());
		assertEquals(-2147483648, new XmlAttribute("name", -2147483648).getAsInteger());
		assertEquals(100000, new XmlAttribute("name", "100000").getAsInteger());
	}
	
	@Test
	void getAsIntegerInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 2147483648L).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", -2147483649L).getAsInteger());
	}
	
	@Test
	void getAsIntegerWithDefault() {
		assertEquals(0, new XmlAttribute("name", 0).getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", 0.0).getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", "value").getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", 2147483648L).getAsInteger(1));
	}
	
	@Test
	void getAsLongValid() {
		assertEquals(0L, new XmlAttribute("name", 0).getAsLong());
		assertEquals(9223372036854775807L, new XmlAttribute("name", 9223372036854775807L).getAsLong());
		assertEquals(-9223372036854775808L, new XmlAttribute("name", -9223372036854775808L).getAsLong());
		assertEquals(1000000000L, new XmlAttribute("name", "1000000000").getAsLong());
	}
	
	@Test
	void getAsLongInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", Double.MAX_VALUE).getAsLong());
	}
	
	@Test
	void getAsLongWithDefault() {
		assertEquals(0L, new XmlAttribute("name", 0).getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", 0.0).getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", "value").getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", Double.MAX_VALUE).getAsLong(1L));
	}
	
	@Test
	void getAsFloatValid() {
		assertEquals(0.0F, new XmlAttribute("name", 0).getAsFloat());
		assertEquals(0.0F, new XmlAttribute("name", 0.0).getAsFloat());
		assertEquals(3.14F, new XmlAttribute("name", 3.14F).getAsFloat());
		assertEquals(42.0F, new XmlAttribute("name", "42").getAsFloat());
		assertEquals(3.14F, new XmlAttribute("name", "3.14").getAsFloat());
	}
	
	@Test
	void getAsFloatInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsFloat());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "not a number").getAsFloat());
	}
	
	@Test
	void getAsFloatWithDefault() {
		assertEquals(0.0F, new XmlAttribute("name", 0).getAsFloat(1.0F));
		assertEquals(0.0F, new XmlAttribute("name", 0.0).getAsFloat(1.0F));
		assertEquals(1.0F, new XmlAttribute("name", "value").getAsFloat(1.0F));
	}
	
	@Test
	void getAsDoubleValid() {
		assertEquals(0.0, new XmlAttribute("name", 0).getAsDouble());
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsDouble());
		assertEquals(3.14, new XmlAttribute("name", 3.14).getAsDouble());
		assertEquals(42.0, new XmlAttribute("name", "42").getAsDouble());
		assertEquals(3.14, new XmlAttribute("name", "3.14").getAsDouble());
	}
	
	@Test
	void getAsDoubleInvalid() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsDouble());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "not a number").getAsDouble());
	}
	
	@Test
	void getAsDoubleWithDefault() {
		assertEquals(0.0, new XmlAttribute("name", 0).getAsDouble(1.0));
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsDouble(1.0));
		assertEquals(1.0, new XmlAttribute("name", "value").getAsDouble(1.0));
	}
	
	@Test
	void getAsWithParser() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new XmlAttribute("name", "[]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "()").getAs(parser));
		assertIterableEquals(List.of(), new XmlAttribute("name", "[]").getAs(parser));
		assertIterableEquals(List.of(true, false), new XmlAttribute("name", "[true, false]").getAs(parser));
		assertIterableEquals(List.of(false, true, false), new XmlAttribute("name", "[false, true, false]").getAs(parser));
	}
	
	@Test
	void getAsWithParserAndDefault() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		assertIterableEquals(List.of(false, true), new XmlAttribute("name", "(true, false)").getAs(parser, List.of(false, true)));
		assertIterableEquals(List.of(true, false), new XmlAttribute("name", "[true, false]").getAs(parser, List.of()));
		assertIterableEquals(List.of(), new XmlAttribute("name", "invalid").getAs(parser, List.of()));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlAttribute attr1 = new XmlAttribute("name", "value");
		XmlAttribute attr2 = new XmlAttribute("name", "value");
		XmlAttribute attr3 = new XmlAttribute("other", "value");
		XmlAttribute attr4 = new XmlAttribute("name", "other");
		
		assertEquals(attr1, attr2);
		assertEquals(attr1.hashCode(), attr2.hashCode());
		assertNotEquals(attr1, attr3);
		assertNotEquals(attr1, attr4);
		assertNotEquals(attr1, null);
		assertNotEquals(attr1, "string");
	}
	
	@Test
	void toStringWithDefaultConfig() {
		assertEquals("name=\"true\"", new XmlAttribute("name", true).toString());
		assertEquals("name=\"0\"", new XmlAttribute("name", 0).toString());
		assertEquals("name=\"0.0\"", new XmlAttribute("name", 0.0).toString());
		assertEquals("name=\"value\"", new XmlAttribute("name", "value").toString());
		assertEquals("name=\"&lt;test&gt;\"", new XmlAttribute("name", "<test>").toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		
		assertEquals("name=\"true\"", new XmlAttribute("name", true).toString(customConfig));
		assertEquals("name=\"0\"", new XmlAttribute("name", 0).toString(customConfig));
		assertEquals("name=\"0.0\"", new XmlAttribute("name", 0.0).toString(customConfig));
		assertEquals("name=\"value\"", new XmlAttribute("name", "value").toString(customConfig));
		assertEquals("name=\"&lt;test&gt;\"", new XmlAttribute("name", "<test>").toString(customConfig));
	}
	
	@Test
	void toStringWithNullConfig() {
		assertDoesNotThrow(() -> new XmlAttribute("name", true).toString(null));
	}
}
