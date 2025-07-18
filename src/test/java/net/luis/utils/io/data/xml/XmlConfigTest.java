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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlConfig}.<br>
 *
 * @author Luis-St
 */
class XmlConfigTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlConfig(true, true, null, true, true, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new XmlConfig(true, true, "\t", true, true, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_8));
		assertDoesNotThrow(() -> new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_16));
		assertDoesNotThrow(() -> new XmlConfig(true, false, "", true, false, StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void defaultConfig() {
		XmlConfig defaultConfig = XmlConfig.DEFAULT;
		
		assertTrue(defaultConfig.strict());
		assertTrue(defaultConfig.prettyPrint());
		assertEquals("\t", defaultConfig.indent());
		assertTrue(defaultConfig.allowAttributes());
		assertTrue(defaultConfig.simplifyValues());
		assertEquals(StandardCharsets.UTF_8, defaultConfig.charset());
	}
	
	@Test
	void customConfigValues() {
		XmlConfig customConfig = new XmlConfig(false, false, "    ", false, false, StandardCharsets.UTF_16);
		
		assertFalse(customConfig.strict());
		assertFalse(customConfig.prettyPrint());
		assertEquals("    ", customConfig.indent());
		assertFalse(customConfig.allowAttributes());
		assertFalse(customConfig.simplifyValues());
		assertEquals(StandardCharsets.UTF_16, customConfig.charset());
	}
	
	@Test
	void differentIndentations() {
		XmlConfig tabConfig = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_8);
		XmlConfig spaceConfig = new XmlConfig(true, true, "  ", true, true, StandardCharsets.UTF_8);
		XmlConfig emptyConfig = new XmlConfig(true, true, "", true, true, StandardCharsets.UTF_8);
		
		assertEquals("\t", tabConfig.indent());
		assertEquals("  ", spaceConfig.indent());
		assertEquals("", emptyConfig.indent());
	}
	
	@Test
	void differentCharsets() {
		XmlConfig utf8Config = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_8);
		XmlConfig utf16Config = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_16);
		XmlConfig isoConfig = new XmlConfig(true, true, "\t", true, true, StandardCharsets.ISO_8859_1);
		XmlConfig asciiConfig = new XmlConfig(true, true, "\t", true, true, StandardCharsets.US_ASCII);
		
		assertEquals(StandardCharsets.UTF_8, utf8Config.charset());
		assertEquals(StandardCharsets.UTF_16, utf16Config.charset());
		assertEquals(StandardCharsets.ISO_8859_1, isoConfig.charset());
		assertEquals(StandardCharsets.US_ASCII, asciiConfig.charset());
	}
	
	@Test
	void booleanCombinations() {
		XmlConfig allTrue = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_8);
		XmlConfig allFalse = new XmlConfig(false, false, "\t", false, false, StandardCharsets.UTF_8);
		XmlConfig mixed1 = new XmlConfig(true, false, "\t", true, false, StandardCharsets.UTF_8);
		XmlConfig mixed2 = new XmlConfig(false, true, "\t", false, true, StandardCharsets.UTF_8);
		
		assertTrue(allTrue.strict() && allTrue.prettyPrint() && allTrue.allowAttributes() && allTrue.simplifyValues());
		assertFalse(allFalse.strict() || allFalse.prettyPrint() || allFalse.allowAttributes() || allFalse.simplifyValues());
		assertTrue(mixed1.strict() && !mixed1.prettyPrint() && mixed1.allowAttributes() && !mixed1.simplifyValues());
		assertTrue(!mixed2.strict() && mixed2.prettyPrint() && !mixed2.allowAttributes() && mixed2.simplifyValues());
	}
}
