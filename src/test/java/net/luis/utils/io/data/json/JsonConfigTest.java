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

package net.luis.utils.io.data.json;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonConfig}.<br>
 *
 * @author Luis-St
 */
class JsonConfigTest {
	
	private static final JsonConfig DEFAULT_CONFIG = JsonConfig.DEFAULT;
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(false, false, "  ", false, 1, false, 10, StandardCharsets.UTF_16);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new JsonConfig(true, true, "\t", true, 10, true, 1, null));
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(true, true, "\t", true, 0, true, 1, StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new JsonConfig(true, true, "\t", true, 10, true, 0, StandardCharsets.UTF_8));
	}
	
	@Test
	void strict() {
		assertTrue(DEFAULT_CONFIG.strict());
		assertFalse(CUSTOM_CONFIG.strict());
	}
	
	@Test
	void indent() {
		assertEquals("\t", DEFAULT_CONFIG.indent());
		assertEquals("  ", CUSTOM_CONFIG.indent());
	}
	
	@Test
	void prettyPrint() {
		assertTrue(DEFAULT_CONFIG.prettyPrint());
		assertFalse(CUSTOM_CONFIG.prettyPrint());
	}
	
	@Test
	void simplifyArrays() {
		assertTrue(DEFAULT_CONFIG.simplifyArrays());
		assertFalse(CUSTOM_CONFIG.simplifyArrays());
	}
	
	@Test
	void maxArraySimplificationSize() {
		assertEquals(10, DEFAULT_CONFIG.maxArraySimplificationSize());
		assertEquals(1, CUSTOM_CONFIG.maxArraySimplificationSize());
	}
	
	@Test
	void simplifyObjects() {
		assertTrue(DEFAULT_CONFIG.simplifyObjects());
		assertFalse(CUSTOM_CONFIG.simplifyObjects());
	}
	
	@Test
	void maxObjectSimplificationSize() {
		assertEquals(1, DEFAULT_CONFIG.maxObjectSimplificationSize());
		assertEquals(10, CUSTOM_CONFIG.maxObjectSimplificationSize());
	}
	
	@Test
	void charset() {
		assertEquals(StandardCharsets.UTF_8, DEFAULT_CONFIG.charset());
		assertEquals(StandardCharsets.UTF_16, CUSTOM_CONFIG.charset());
	}
}
