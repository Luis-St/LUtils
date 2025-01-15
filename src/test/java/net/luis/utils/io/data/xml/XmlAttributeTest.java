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
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, true));
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, 0));
		assertThrows(NullPointerException.class, () -> new XmlAttribute(null, "value"));
		assertDoesNotThrow(() -> new XmlAttribute("name", true));
		assertDoesNotThrow(() -> new XmlAttribute("name", (Number) null));
		assertDoesNotThrow(() -> new XmlAttribute("name", (String) null));
	}
	
	@Test
	void getName() {
		assertEquals("name", new XmlAttribute("name", true).getName());
	}
	
	@Test
	void getRawValue() {
		assertEquals("true", new XmlAttribute("name", true).getRawValue());
		assertEquals("0", new XmlAttribute("name", 0).getRawValue());
		assertEquals("0.0", new XmlAttribute("name", 0.0).getRawValue());
		assertEquals("value", new XmlAttribute("name", "value").getRawValue());
		assertEquals("null", new XmlAttribute("name", (Number) null).getRawValue());
		assertEquals("null", new XmlAttribute("name", (String) null).getRawValue());
		assertEquals("&lt;value&gt;", new XmlAttribute("name", "<value>").getRawValue());
	}
	
	@Test
	void getUnescapedValue() {
		assertEquals("true", new XmlAttribute("name", true).getUnescapedValue());
		assertEquals("0", new XmlAttribute("name", 0).getUnescapedValue());
		assertEquals("0.0", new XmlAttribute("name", 0.0).getUnescapedValue());
		assertEquals("value", new XmlAttribute("name", "value").getUnescapedValue());
		assertEquals("null", new XmlAttribute("name", (Number) null).getUnescapedValue());
		assertEquals("null", new XmlAttribute("name", (String) null).getUnescapedValue());
		assertEquals("<value>", new XmlAttribute("name", "<value>").getUnescapedValue());
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
	void getAsBoolean() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0).getAsBoolean());
		assertTrue(new XmlAttribute("name", true).getAsBoolean());
		assertFalse(new XmlAttribute("name", false).getAsBoolean());
		
		assertTrue(new XmlAttribute("name", true).getAsBoolean(false));
		assertFalse(new XmlAttribute("name", false).getAsBoolean(true));
		assertTrue(new XmlAttribute("name", 0).getAsBoolean(true));
	}
	
	@Test
	void getAsNumber() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsNumber());
		assertEquals(0L, new XmlAttribute("name", 0).getAsNumber());
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsNumber());
		
		assertEquals(0L, new XmlAttribute("name", 0).getAsNumber(1L));
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsNumber(1.0));
		assertEquals(1L, new XmlAttribute("name", "value").getAsNumber(1L));
	}
	
	@Test
	void getAsByte() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", Byte.MAX_VALUE + 1).getAsByte());
		assertEquals((byte) 0, new XmlAttribute("name", 0).getAsByte());
		
		assertEquals((byte) 0, new XmlAttribute("name", 0).getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", 0.0).getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", "value").getAsByte((byte) 1));
		assertEquals((byte) 1, new XmlAttribute("name", Byte.MAX_VALUE + 1).getAsByte((byte) 1));
	}
	
	@Test
	void getAsShort() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", Short.MAX_VALUE + 1).getAsShort());
		assertEquals((short) 0, new XmlAttribute("name", 0).getAsShort());
		
		assertEquals((short) 0, new XmlAttribute("name", 0).getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", 0.0).getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", "value").getAsShort((short) 1));
		assertEquals((short) 1, new XmlAttribute("name", Short.MAX_VALUE + 1).getAsShort((short) 1));
	}
	
	@Test
	void getAsInteger() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", Integer.MAX_VALUE + 1L).getAsInteger());
		assertEquals(0, new XmlAttribute("name", 0).getAsInteger());
		
		assertEquals(0, new XmlAttribute("name", 0).getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", 0.0).getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", "value").getAsInteger(1));
		assertEquals(1, new XmlAttribute("name", Integer.MAX_VALUE + 1L).getAsInteger(1));
	}
	
	@Test
	void getAsLong() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", 0.0).getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", Long.MAX_VALUE + 1.0).getAsLong());
		assertEquals(0L, new XmlAttribute("name", 0).getAsLong());
		
		assertEquals(0L, new XmlAttribute("name", 0).getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", 0.0).getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", "value").getAsLong(1L));
		assertEquals(1L, new XmlAttribute("name", Long.MAX_VALUE + 1.0).getAsLong(1L));
	}
	
	@Test
	void getAsFloat() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsFloat());
		assertEquals(0.0F, new XmlAttribute("name", 0).getAsFloat());
		assertEquals(0.0F, new XmlAttribute("name", 0.0).getAsFloat());
		
		assertEquals(0.0F, new XmlAttribute("name", 0).getAsFloat(1.0F));
		assertEquals(0.0F, new XmlAttribute("name", 0.0).getAsFloat(1.0F));
		assertEquals(1.0F, new XmlAttribute("name", "value").getAsFloat(1.0F));
	}
	
	@Test
	void getAsDouble() {
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "value").getAsDouble());
		assertEquals(0.0, new XmlAttribute("name", 0).getAsDouble());
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsDouble());
		
		assertEquals(0.0, new XmlAttribute("name", 0).getAsDouble(1.0));
		assertEquals(0.0, new XmlAttribute("name", 0.0).getAsDouble(1.0));
		assertEquals(1.0, new XmlAttribute("name", "value").getAsDouble(1.0));
	}
	
	@Test
	void getAs() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> new XmlAttribute("name", "[]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> new XmlAttribute("name", "()").getAs(parser));
		assertIterableEquals(List.of(true, false), new XmlAttribute("name", "[true, false]").getAs(parser));
		
		assertIterableEquals(List.of(false, true), new XmlAttribute("name", "(true, false)").getAs(parser, List.of(false, true)));
		assertIterableEquals(List.of(true, false), new XmlAttribute("name", "[true, false]").getAs(parser, List.of()));
	}
	
	@Test
	void toStringDefaultConfig() {
		assertDoesNotThrow(() -> new XmlAttribute("name", true).toString(null));
		assertEquals("name=\"true\"", new XmlAttribute("name", true).toString(XmlConfig.DEFAULT));
		assertEquals("name=\"0\"", new XmlAttribute("name", 0).toString(XmlConfig.DEFAULT));
		assertEquals("name=\"0.0\"", new XmlAttribute("name", 0.0).toString(XmlConfig.DEFAULT));
		assertEquals("name=\"value\"", new XmlAttribute("name", "value").toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		assertDoesNotThrow(() -> new XmlAttribute("name", true).toString(null));
		assertEquals("name=\"true\"", new XmlAttribute("name", true).toString(CUSTOM_CONFIG));
		assertEquals("name=\"0\"", new XmlAttribute("name", 0).toString(CUSTOM_CONFIG));
		assertEquals("name=\"0.0\"", new XmlAttribute("name", 0.0).toString(CUSTOM_CONFIG));
		assertEquals("name=\"value\"", new XmlAttribute("name", "value").toString(CUSTOM_CONFIG));
	}
}
