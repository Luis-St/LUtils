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
	void validateElementNameWithNull() {
		assertThrows(NullPointerException.class, () -> XmlHelper.validateElementName(null));
	}
	
	@Test
	void validateElementNameWithInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(""));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(" "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("   "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("\t"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("\n"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("1"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("1invalid"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("9element"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(":"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName(":invalid"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid space"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid\ttab"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid\nnewline"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid@symbol"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid#symbol"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateElementName("invalid$symbol"));
	}
	
	@Test
	void validateElementNameWithValidInputs() {
		assertEquals("a", XmlHelper.validateElementName("a"));
		assertEquals("A", XmlHelper.validateElementName("A"));
		assertEquals("z", XmlHelper.validateElementName("z"));
		assertEquals("Z", XmlHelper.validateElementName("Z"));
		
		assertEquals("_", XmlHelper.validateElementName("_"));
		assertEquals("_element", XmlHelper.validateElementName("_element"));
		assertEquals("_123", XmlHelper.validateElementName("_123"));
		
		assertEquals("-", XmlHelper.validateElementName("-"));
		assertEquals("-element", XmlHelper.validateElementName("-element"));
		assertEquals("-123", XmlHelper.validateElementName("-123"));
		
		assertEquals("element", XmlHelper.validateElementName("element"));
		assertEquals("Element", XmlHelper.validateElementName("Element"));
		assertEquals("ELEMENT", XmlHelper.validateElementName("ELEMENT"));
		
		assertEquals("a1", XmlHelper.validateElementName("a1"));
		assertEquals("element123", XmlHelper.validateElementName("element123"));
		assertEquals("test-element", XmlHelper.validateElementName("test-element"));
		assertEquals("test_element", XmlHelper.validateElementName("test_element"));
		assertEquals("test-element_123", XmlHelper.validateElementName("test-element_123"));
		
		assertEquals("ns:element", XmlHelper.validateElementName("ns:element"));
		assertEquals("namespace:element123", XmlHelper.validateElementName("namespace:element123"));
		assertEquals("a:b", XmlHelper.validateElementName("a:b"));
		assertEquals("_ns:_element", XmlHelper.validateElementName("_ns:_element"));
		assertEquals("ns-1:element-2", XmlHelper.validateElementName("ns-1:element-2"));
	}
	
	@Test
	void validateAttributeKeyWithNull() {
		assertThrows(NullPointerException.class, () -> XmlHelper.validateAttributeKey(null));
	}
	
	@Test
	void validateAttributeKeyWithInvalidInputs() {
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(""));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(" "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("   "));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("\t"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("\n"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(":"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey(":invalid"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid space"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid\ttab"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid\nnewline"));
		
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid@symbol"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid#symbol"));
		assertThrows(IllegalArgumentException.class, () -> XmlHelper.validateAttributeKey("invalid$symbol"));
	}
	
	@Test
	void validateAttributeKeyWithValidInputs() {
		assertEquals("1", XmlHelper.validateAttributeKey("1"));
		assertEquals("123", XmlHelper.validateAttributeKey("123"));
		assertEquals("0", XmlHelper.validateAttributeKey("0"));
		assertEquals("9", XmlHelper.validateAttributeKey("9"));
		
		assertEquals("a", XmlHelper.validateAttributeKey("a"));
		assertEquals("A", XmlHelper.validateAttributeKey("A"));
		assertEquals("z", XmlHelper.validateAttributeKey("z"));
		assertEquals("Z", XmlHelper.validateAttributeKey("Z"));
		
		assertEquals("_", XmlHelper.validateAttributeKey("_"));
		assertEquals("_attribute", XmlHelper.validateAttributeKey("_attribute"));
		assertEquals("_123", XmlHelper.validateAttributeKey("_123"));
		
		assertEquals("-", XmlHelper.validateAttributeKey("-"));
		assertEquals("-attribute", XmlHelper.validateAttributeKey("-attribute"));
		assertEquals("-123", XmlHelper.validateAttributeKey("-123"));
		
		assertEquals("attribute", XmlHelper.validateAttributeKey("attribute"));
		assertEquals("Attribute", XmlHelper.validateAttributeKey("Attribute"));
		assertEquals("ATTRIBUTE", XmlHelper.validateAttributeKey("ATTRIBUTE"));
		
		assertEquals("a1", XmlHelper.validateAttributeKey("a1"));
		assertEquals("attribute123", XmlHelper.validateAttributeKey("attribute123"));
		assertEquals("123abc", XmlHelper.validateAttributeKey("123abc"));
		assertEquals("test-attribute", XmlHelper.validateAttributeKey("test-attribute"));
		assertEquals("test_attribute", XmlHelper.validateAttributeKey("test_attribute"));
		assertEquals("test-attribute_123", XmlHelper.validateAttributeKey("test-attribute_123"));
		assertEquals("123-test_456", XmlHelper.validateAttributeKey("123-test_456"));
		
		assertEquals("ns:attribute", XmlHelper.validateAttributeKey("ns:attribute"));
		assertEquals("namespace:attribute123", XmlHelper.validateAttributeKey("namespace:attribute123"));
		assertEquals("1:2", XmlHelper.validateAttributeKey("1:2"));
		assertEquals("123:abc", XmlHelper.validateAttributeKey("123:abc"));
		assertEquals("ns-1:attribute-2", XmlHelper.validateAttributeKey("ns-1:attribute-2"));
		assertEquals("123:456", XmlHelper.validateAttributeKey("123:456"));
	}
	
	@Test
	void escapeXmlWithNull() {
		assertThrows(NullPointerException.class, () -> XmlHelper.escapeXml(null));
	}
	
	@Test
	void escapeXmlWithRegularText() {
		assertEquals("", XmlHelper.escapeXml(""));
		assertEquals(" ", XmlHelper.escapeXml(" "));
		assertEquals("   ", XmlHelper.escapeXml("   "));
		assertEquals("\t", XmlHelper.escapeXml("\t"));
		assertEquals("\n", XmlHelper.escapeXml("\n"));
		assertEquals("\r", XmlHelper.escapeXml("\r"));
		
		assertEquals("text", XmlHelper.escapeXml("text"));
		assertEquals("Text", XmlHelper.escapeXml("Text"));
		assertEquals("TEXT", XmlHelper.escapeXml("TEXT"));
		assertEquals("123", XmlHelper.escapeXml("123"));
		assertEquals("test123", XmlHelper.escapeXml("test123"));
		assertEquals("hello world", XmlHelper.escapeXml("hello world"));
		assertEquals("line1\nline2", XmlHelper.escapeXml("line1\nline2"));
	}
	
	@Test
	void escapeXmlWithSpecialCharacters() {
		assertEquals("&lt;", XmlHelper.escapeXml("<"));
		assertEquals("&gt;", XmlHelper.escapeXml(">"));
		assertEquals("&amp;", XmlHelper.escapeXml("&"));
		assertEquals("&quot;", XmlHelper.escapeXml("\""));
		assertEquals("&apos;", XmlHelper.escapeXml("'"));
		
		assertEquals("&lt;&gt;", XmlHelper.escapeXml("<>"));
		assertEquals("&lt;&gt;&amp;&quot;&apos;", XmlHelper.escapeXml("<>&\"'"));
		assertEquals("&amp;&amp;", XmlHelper.escapeXml("&&"));
		assertEquals("&quot;&quot;", XmlHelper.escapeXml("\"\""));
		assertEquals("&apos;&apos;", XmlHelper.escapeXml("''"));
		
		assertEquals("&lt;tag&gt;", XmlHelper.escapeXml("<tag>"));
		assertEquals("&lt;tag attr=&quot;value&quot;&gt;", XmlHelper.escapeXml("<tag attr=\"value\">"));
		assertEquals("text &amp; more text", XmlHelper.escapeXml("text & more text"));
		assertEquals("He said: &quot;Hello!&quot;", XmlHelper.escapeXml("He said: \"Hello!\""));
		assertEquals("It&apos;s working", XmlHelper.escapeXml("It's working"));
	}
	
	@Test
	void escapeXmlWithMixedContent() {
		assertEquals("text&lt;tag&gt;more text", XmlHelper.escapeXml("text<tag>more text"));
		assertEquals("&amp;quot;test&amp;quot;", XmlHelper.escapeXml("&quot;test&quot;"));
		assertEquals("&lt;p&gt;Hello &amp; welcome!&lt;/p&gt;", XmlHelper.escapeXml("<p>Hello & welcome!</p>"));
		assertEquals("JavaScript: if (a &lt; b &amp;&amp; c &gt; d) { alert(&quot;test&quot;); }",
			XmlHelper.escapeXml("JavaScript: if (a < b && c > d) { alert(\"test\"); }"));
	}
	
	@Test
	void unescapeXmlWithNull() {
		assertThrows(NullPointerException.class, () -> XmlHelper.unescapeXml(null));
	}
	
	@Test
	void unescapeXmlWithRegularText() {
		assertEquals("", XmlHelper.unescapeXml(""));
		assertEquals(" ", XmlHelper.unescapeXml(" "));
		assertEquals("   ", XmlHelper.unescapeXml("   "));
		assertEquals("\t", XmlHelper.unescapeXml("\t"));
		assertEquals("\n", XmlHelper.unescapeXml("\n"));
		assertEquals("\r", XmlHelper.unescapeXml("\r"));
		
		assertEquals("text", XmlHelper.unescapeXml("text"));
		assertEquals("Text", XmlHelper.unescapeXml("Text"));
		assertEquals("TEXT", XmlHelper.unescapeXml("TEXT"));
		assertEquals("123", XmlHelper.unescapeXml("123"));
		assertEquals("test123", XmlHelper.unescapeXml("test123"));
		assertEquals("hello world", XmlHelper.unescapeXml("hello world"));
		assertEquals("line1\nline2", XmlHelper.unescapeXml("line1\nline2"));
	}
	
	@Test
	void unescapeXmlWithEscapedCharacters() {
		assertEquals("<", XmlHelper.unescapeXml("&lt;"));
		assertEquals(">", XmlHelper.unescapeXml("&gt;"));
		assertEquals("&", XmlHelper.unescapeXml("&amp;"));
		assertEquals("\"", XmlHelper.unescapeXml("&quot;"));
		assertEquals("'", XmlHelper.unescapeXml("&apos;"));
		
		assertEquals("<>", XmlHelper.unescapeXml("&lt;&gt;"));
		assertEquals("<>&\"'", XmlHelper.unescapeXml("&lt;&gt;&amp;&quot;&apos;"));
		assertEquals("&&", XmlHelper.unescapeXml("&amp;&amp;"));
		assertEquals("\"\"", XmlHelper.unescapeXml("&quot;&quot;"));
		assertEquals("''", XmlHelper.unescapeXml("&apos;&apos;"));
		
		assertEquals("<tag>", XmlHelper.unescapeXml("&lt;tag&gt;"));
		assertEquals("<tag attr=\"value\">", XmlHelper.unescapeXml("&lt;tag attr=&quot;value&quot;&gt;"));
		assertEquals("text & more text", XmlHelper.unescapeXml("text &amp; more text"));
		assertEquals("He said: \"Hello!\"", XmlHelper.unescapeXml("He said: &quot;Hello!&quot;"));
		assertEquals("It's working", XmlHelper.unescapeXml("It&apos;s working"));
	}
	
	@Test
	void unescapeXmlWithMixedContent() {
		assertEquals("text<tag>more text", XmlHelper.unescapeXml("text&lt;tag&gt;more text"));
		assertEquals("&quot;test&quot;", XmlHelper.unescapeXml("&amp;quot;test&amp;quot;"));
		assertEquals("<p>Hello & welcome!</p>", XmlHelper.unescapeXml("&lt;p&gt;Hello &amp; welcome!&lt;/p&gt;"));
		assertEquals("JavaScript: if (a < b && c > d) { alert(\"test\"); }", XmlHelper.unescapeXml("JavaScript: if (a &lt; b &amp;&amp; c &gt; d) { alert(&quot;test&quot;); }"));
	}
	
	@Test
	void unescapeXmlWithInvalidEscapeSequences() {
		assertEquals("&invalid;", XmlHelper.unescapeXml("&invalid;"));
		assertEquals("&test", XmlHelper.unescapeXml("&test"));
		assertEquals("&;", XmlHelper.unescapeXml("&;"));
		assertEquals("&", XmlHelper.unescapeXml("&"));
		assertEquals("test&incomplete", XmlHelper.unescapeXml("test&incomplete"));
	}
	
	@Test
	void escapeUnescapeRoundTrip() {
		String[] testStrings = {
			"",
			"simple text",
			"<tag>",
			"<tag attr=\"value\">",
			"text & more text",
			"He said: \"Hello!\"",
			"It's working",
			"<>&\"'",
			"<p>Hello & welcome!</p>",
			"JavaScript: if (a < b && c > d) { alert(\"test\"); }",
			"&amp;quot;nested&amp;quot;",
			"Multiple <tags> & \"quotes\" with 'apostrophes'"
		};
		
		for (String original : testStrings) {
			String escaped = XmlHelper.escapeXml(original);
			String unescaped = XmlHelper.unescapeXml(escaped);
			assertEquals(original, unescaped, "Round trip failed for: " + original);
		}
	}
	
	@Test
	void unescapeEscapeRoundTrip() {
		String[] testStrings = {
			"",
			"simple text",
			"&lt;tag&gt;",
			"&lt;tag attr=&quot;value&quot;&gt;",
			"text &amp; more text",
			"He said: &quot;Hello!&quot;",
			"It&apos;s working",
			"&lt;&gt;&amp;&quot;&apos;",
			"&lt;p&gt;Hello &amp; welcome!&lt;/p&gt;"
		};
		
		for (String escaped : testStrings) {
			String unescaped = XmlHelper.unescapeXml(escaped);
			String reescaped = XmlHelper.escapeXml(unescaped);
			assertEquals(escaped, reescaped, "Round trip failed for: " + escaped);
		}
	}
}
