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
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlValue(null, true));
		assertThrows(NullPointerException.class, () -> new XmlValue(null, (Number) null));
		assertThrows(NullPointerException.class, () -> new XmlValue(null, (String) null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, true));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, (Number) null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", null, (String) null));
	}
	
	@Test
	void getElementType() {
		assertEquals("xml value", new XmlValue("test", true).getElementType());
	}
	
	@Test
	void isSelfClosing() {
		assertFalse(new XmlValue("test", true).isSelfClosing());
	}
	
	@Test
	void getRawValue() {
		assertEquals("true", new XmlValue("test", true).getRawValue());
		assertEquals("1", new XmlValue("test", 1).getRawValue());
		assertEquals("1.0", new XmlValue("test", 1.0).getRawValue());
		assertEquals("test", new XmlValue("test", "test").getRawValue());
		assertEquals("&lt;test&gt;", new XmlValue("test", "<test>").getRawValue());
	}
	
	@Test
	void getUnescapedValue() {
		assertEquals("true", new XmlValue("test", true).getUnescapedValue());
		assertEquals("1", new XmlValue("test", 1).getUnescapedValue());
		assertEquals("1.0", new XmlValue("test", 1.0).getUnescapedValue());
		assertEquals("test", new XmlValue("test", "test").getUnescapedValue());
		assertEquals("<test>", new XmlValue("test", "<test>").getUnescapedValue());
	}
	
	@Test
	void getAsString() {
		assertEquals("true", new XmlValue("test", true).getAsString());
		assertEquals("1", new XmlValue("test", 1).getAsString());
		assertEquals("1.0", new XmlValue("test", 1.0).getAsString());
		assertEquals("test", new XmlValue("test", "test").getAsString());
		assertEquals("<test>", new XmlValue("test", "<test>").getAsString());
	}
	
	@Test
	void getAsBoolean() {
		assertTrue(new XmlValue("test", true).getAsBoolean());
		assertFalse(new XmlValue("test", false).getAsBoolean());
		assertTrue(new XmlValue("test", "true").getAsBoolean());
		assertFalse(new XmlValue("test", "false").getAsBoolean());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1).getAsBoolean());
		
		assertTrue(new XmlValue("test", true).getAsBoolean(false));
		assertFalse(new XmlValue("test", false).getAsBoolean(true));
		assertTrue(new XmlValue("test", "true").getAsBoolean(false));
		assertFalse(new XmlValue("test", "false").getAsBoolean(true));
		assertTrue(new XmlValue("test", 1).getAsBoolean(true));
	}
	
	@Test
	void getAsNumber() {
		assertEquals(1L, new XmlValue("test", 1).getAsNumber());
		assertEquals(1.0, new XmlValue("test", 1.0).getAsNumber());
		assertEquals(1L, new XmlValue("test", "1").getAsNumber());
		assertEquals(1.0, new XmlValue("test", "1.0").getAsNumber());
		
		assertEquals(1L, new XmlValue("test", 1).getAsNumber(0));
		assertEquals(1.0, new XmlValue("test", 1.0).getAsNumber(0));
		assertEquals(1L, new XmlValue("test", "1").getAsNumber(0));
		assertEquals(1.0, new XmlValue("test", "1.0").getAsNumber(0));
		assertEquals(0, new XmlValue("test", "test").getAsNumber(0));
	}
	
	@Test
	void getAsByte() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsByte());
		assertEquals((byte) 1, new XmlValue("test", 1).getAsByte());
		assertEquals((byte) 1, new XmlValue("test", "1").getAsByte());
		
		assertEquals((byte) 1, new XmlValue("test", 1).getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", 1.0).getAsByte((byte) 0));
		assertEquals((byte) 1, new XmlValue("test", "1").getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", "1.0").getAsByte((byte) 0));
		assertEquals((byte) 0, new XmlValue("test", "test").getAsByte((byte) 0));
	}
	
	@Test
	void getAsShort() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsShort());
		assertEquals((short) 1, new XmlValue("test", 1).getAsShort());
		assertEquals((short) 1, new XmlValue("test", "1").getAsShort());
		
		assertEquals((short) 1, new XmlValue("test", 1).getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", 1.0).getAsShort((short) 0));
		assertEquals((short) 1, new XmlValue("test", "1").getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", "1.0").getAsShort((short) 0));
		assertEquals((short) 0, new XmlValue("test", "test").getAsShort((short) 0));
	}
	
	@Test
	void getAsInteger() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsInteger());
		assertEquals(1, new XmlValue("test", 1).getAsInteger());
		assertEquals(1, new XmlValue("test", "1").getAsInteger());
		
		assertEquals(1, new XmlValue("test", 1).getAsInteger(0));
		assertEquals(0, new XmlValue("test", 1.0).getAsInteger(0));
		assertEquals(1, new XmlValue("test", "1").getAsInteger(0));
		assertEquals(0, new XmlValue("test", "1.0").getAsInteger(0));
		assertEquals(0, new XmlValue("test", "test").getAsInteger(0));
	}
	
	@Test
	void getAsLong() {
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", 1.0).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "1.0").getAsLong());
		assertEquals(1L, new XmlValue("test", 1).getAsLong());
		assertEquals(1L, new XmlValue("test", "1").getAsLong());
		
		assertEquals(1L, new XmlValue("test", 1).getAsLong(0));
		assertEquals(0L, new XmlValue("test", 1.0).getAsLong(0));
		assertEquals(1L, new XmlValue("test", "1").getAsLong(0));
		assertEquals(0L, new XmlValue("test", "1.0").getAsLong(0));
		assertEquals(0L, new XmlValue("test", "test").getAsLong(0));
	}
	
	@Test
	void getAsFloat() {
		assertEquals(1.0F, new XmlValue("test", 1).getAsFloat());
		assertEquals(1.0F, new XmlValue("test", 1.0).getAsFloat());
		assertEquals(1.0F, new XmlValue("test", "1").getAsFloat());
		assertEquals(1.0F, new XmlValue("test", "1.0").getAsFloat());
		
		assertEquals(1.0F, new XmlValue("test", 1).getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", 1.0).getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", "1").getAsFloat(0));
		assertEquals(1.0F, new XmlValue("test", "1.0").getAsFloat(0));
		assertEquals(0.0F, new XmlValue("test", "test").getAsFloat(0));
	}
	
	@Test
	void getAsDouble() {
		assertEquals(1.0, new XmlValue("test", 1).getAsDouble());
		assertEquals(1.0, new XmlValue("test", 1.0).getAsDouble());
		assertEquals(1.0, new XmlValue("test", "1").getAsDouble());
		assertEquals(1.0, new XmlValue("test", "1.0").getAsDouble());
		
		assertEquals(1.0, new XmlValue("test", 1).getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", 1.0).getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", "1").getAsDouble(0));
		assertEquals(1.0, new XmlValue("test", "1.0").getAsDouble(0));
		assertEquals(0.0, new XmlValue("test", "test").getAsDouble(0));
	}
	
	@Test
	void getAs() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = string -> new ScopedStringReader(string).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "[true, false]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> new XmlValue("test", "true").getAs(parser));
		assertIterableEquals(List.of(true, false), new XmlValue("test", "[true, false]").getAs(parser));
		
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "true").getAs(parser, null));
		assertThrows(NullPointerException.class, () -> new XmlValue("test", "[true, false]").getAs(parser, null));
		assertIterableEquals(List.of(true, false), new XmlValue("test", "[true, false]").getAs(parser, List.of()));
		assertIterableEquals(List.of(), new XmlValue("test", "true").getAs(parser, List.of()));
		
	}
	
	@Test
	void toStringDefaultConfig() {
		XmlValue value = new XmlValue("test", true);
		assertThrows(NullPointerException.class, () -> value.toString(null));
		assertEquals("<test>true</test>", value.toString(XmlConfig.DEFAULT));
		value.addAttribute("test", "test");
		assertEquals("<test test=\"test\">true</test>", value.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		XmlValue value = new XmlValue("test", true);
		assertThrows(NullPointerException.class, () -> value.toString(null));
		assertEquals("<test>true</test>", value.toString(CUSTOM_CONFIG));
		value.addAttribute("test", "test");
		assertThrows(IllegalStateException.class, () -> value.toString(CUSTOM_CONFIG));
	}
}
