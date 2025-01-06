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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlHelper}.<br>
 *
 * @author Luis-St
 */
class XmlHelperTest {
	
	@Test
	void validateElementName() {
		assertThrows(NullPointerException.class, () -> XmlHelper.validateElementName(null));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(""));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(" "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("1"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(":"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("a"));
		assertDoesNotThrow(() -> XmlHelper.validateElementName("a1"));
		assertDoesNotThrow(() -> XmlHelper.validateElementName("_1"));
		assertDoesNotThrow(() -> XmlHelper.validateElementName("-1"));
		assertDoesNotThrow(() -> XmlHelper.validateElementName("a-1"));
		assertDoesNotThrow(() -> XmlHelper.validateElementName("a1:test"));
	}
	
	@Test
	void validateAttributeKey() {
		assertThrows(NullPointerException.class, () -> XmlHelper.validateAttributeKey(null));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(""));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(" "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(":"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("1"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("a"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("a"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("1"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("-_"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("a-1"));
		assertDoesNotThrow(() -> XmlHelper.validateAttributeKey("a1:test"));
	}
	
	@Test
	void escapeXml() {
		assertThrows(NullPointerException.class, () -> XmlHelper.escapeXml(null));
		assertEquals("", XmlHelper.escapeXml(""));
		assertEquals(" ", XmlHelper.escapeXml(" "));
		assertEquals("1", XmlHelper.escapeXml("1"));
		assertEquals("a", XmlHelper.escapeXml("a"));
		
		assertEquals("&lt;", XmlHelper.escapeXml("<"));
		assertEquals("&gt;", XmlHelper.escapeXml(">"));
		assertEquals("&amp;", XmlHelper.escapeXml("&"));
		assertEquals("&quot;", XmlHelper.escapeXml("\""));
		assertEquals("&apos;", XmlHelper.escapeXml("'"));
		assertEquals("&lt;&gt;&amp;&quot;&apos;", XmlHelper.escapeXml("<>&\"'"));
	}
	
	@Test
	void unescapeXml() {
		assertThrows(NullPointerException.class, () -> XmlHelper.unescapeXml(null));
		assertEquals("", XmlHelper.unescapeXml(""));
		assertEquals(" ", XmlHelper.unescapeXml(" "));
		assertEquals("1", XmlHelper.unescapeXml("1"));
		assertEquals("a", XmlHelper.unescapeXml("a"));
		
		assertEquals("<", XmlHelper.unescapeXml("&lt;"));
		assertEquals(">", XmlHelper.unescapeXml("&gt;"));
		assertEquals("&", XmlHelper.unescapeXml("&amp;"));
		assertEquals("\"", XmlHelper.unescapeXml("&quot;"));
		assertEquals("'", XmlHelper.unescapeXml("&apos;"));
		assertEquals("<>&\"'", XmlHelper.unescapeXml("&lt;&gt;&amp;&quot;&apos;"));
	}
}
