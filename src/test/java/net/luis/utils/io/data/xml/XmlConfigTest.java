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
	
	private static final XmlConfig DEFAULT_CONFIG = XmlConfig.DEFAULT;
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_16);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlConfig(true, true, null, true, true, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new XmlConfig(true, true, "\t", true, true, null));
	}
	
	@Test
	void strict() {
		assertTrue(DEFAULT_CONFIG.strict());
		assertFalse(CUSTOM_CONFIG.strict());
	}
	
	@Test
	void prettyPrint() {
		assertTrue(DEFAULT_CONFIG.prettyPrint());
		assertFalse(CUSTOM_CONFIG.prettyPrint());
	}
	
	@Test
	void indent() {
		assertEquals("\t", DEFAULT_CONFIG.indent());
		assertEquals("  ", CUSTOM_CONFIG.indent());
	}
	
	@Test
	void allowAttributes() {
		assertTrue(DEFAULT_CONFIG.allowAttributes());
		assertFalse(CUSTOM_CONFIG.allowAttributes());
	}
	
	@Test
	void simplifyValues() {
		assertTrue(DEFAULT_CONFIG.simplifyValues());
		assertFalse(CUSTOM_CONFIG.simplifyValues());
	}
	
	@Test
	void charset() {
		assertEquals(StandardCharsets.UTF_8, DEFAULT_CONFIG.charset());
		assertEquals(StandardCharsets.UTF_16, CUSTOM_CONFIG.charset());
	}
}
