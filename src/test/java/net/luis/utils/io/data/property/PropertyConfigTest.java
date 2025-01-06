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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.property.exception.PropertySyntaxException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyConfig}.<br>
 *
 * @author Luis-St
 */
class PropertyConfigTest {
	
	private static final PropertyConfig DEFAULT_CONFIG = PropertyConfig.DEFAULT;
	private static final PropertyConfig CUSTOM_CONFIG = new PropertyConfig(':', 0, Set.of(';'), Pattern.compile("^[a-z._]+$"), Pattern.compile("^[ a-zA-Z0-9._-]*$"), true, StandardCharsets.UTF_16);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new PropertyConfig('=', 1, null, Pattern.compile(""), Pattern.compile(""), false, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new PropertyConfig('=', 1, Set.of('#'), null, Pattern.compile(""), false, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new PropertyConfig('=', 1, Set.of('#'), Pattern.compile(""), null, false, StandardCharsets.UTF_8));
		assertThrows(NullPointerException.class, () -> new PropertyConfig('=', 1, Set.of('#'), Pattern.compile(""), Pattern.compile(""), false, null));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig('\0', 1, Set.of('#'), Pattern.compile(""), Pattern.compile(""), false, StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig('\t', 1, Set.of('#'), Pattern.compile(""), Pattern.compile(""), false, StandardCharsets.UTF_8));
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig('#', 1, Set.of('#'), Pattern.compile(""), Pattern.compile(""), false, StandardCharsets.UTF_8));
	}
	
	@Test
	void ensureKeyMatches() {
		assertThrows(NullPointerException.class, () -> DEFAULT_CONFIG.ensureKeyMatches(null));
		assertThrows(PropertySyntaxException.class, () -> DEFAULT_CONFIG.ensureKeyMatches(""));
		assertThrows(PropertySyntaxException.class, () -> DEFAULT_CONFIG.ensureKeyMatches(" "));
		assertDoesNotThrow(() -> DEFAULT_CONFIG.ensureKeyMatches("this.is-a_key.123_example"));
		assertDoesNotThrow(() -> DEFAULT_CONFIG.ensureKeyMatches("THIS.IS-A_KEY.123_EXAMPLE"));
		
		assertThrows(NullPointerException.class, () -> CUSTOM_CONFIG.ensureKeyMatches(null));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureKeyMatches(""));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureKeyMatches(" "));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureKeyMatches("this.is-a.key"));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureKeyMatches("this.123.key"));
		assertDoesNotThrow(() -> CUSTOM_CONFIG.ensureKeyMatches("this.is.a_key.example"));
	}
	
	@Test
	void ensureValueMatches() {
		assertThrows(NullPointerException.class, () -> DEFAULT_CONFIG.ensureValueMatches(null));
		assertDoesNotThrow(() -> DEFAULT_CONFIG.ensureValueMatches(""));
		assertDoesNotThrow(() -> DEFAULT_CONFIG.ensureValueMatches("abc"));
		assertDoesNotThrow(() -> DEFAULT_CONFIG.ensureValueMatches("[10, 20, 30]"));
		
		assertThrows(NullPointerException.class, () -> CUSTOM_CONFIG.ensureValueMatches(null));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureValueMatches("$abc"));
		assertThrows(PropertySyntaxException.class, () -> CUSTOM_CONFIG.ensureValueMatches("[10, 20, 30]"));
		assertDoesNotThrow(() -> CUSTOM_CONFIG.ensureValueMatches(""));
		assertDoesNotThrow(() -> CUSTOM_CONFIG.ensureValueMatches("abc"));
	}
	
	@Test
	void separator() {
		assertEquals('=', DEFAULT_CONFIG.separator());
		assertEquals(':', CUSTOM_CONFIG.separator());
	}
	
	@Test
	void alignment() {
		assertEquals(1, DEFAULT_CONFIG.alignment());
		assertEquals(0, CUSTOM_CONFIG.alignment());
	}
	
	@Test
	void commentCharacters() {
		assertNotNull(DEFAULT_CONFIG.commentCharacters());
		assertEquals(Set.of('#'), DEFAULT_CONFIG.commentCharacters());
		assertNotNull(CUSTOM_CONFIG.commentCharacters());
		assertEquals(Set.of(';'), CUSTOM_CONFIG.commentCharacters());
	}
	
	@Test
	void keyPattern() {
		assertNotNull(DEFAULT_CONFIG.keyPattern());
		assertEquals("^[a-zA-Z0-9._-]+$", DEFAULT_CONFIG.keyPattern().pattern());
		assertNotNull(CUSTOM_CONFIG.keyPattern());
		assertEquals("^[a-z._]+$", CUSTOM_CONFIG.keyPattern().pattern());
	}
	
	@Test
	void valuePattern() {
		assertNotNull(DEFAULT_CONFIG.valuePattern());
		assertEquals(".*", DEFAULT_CONFIG.valuePattern().pattern());
		assertNotNull(CUSTOM_CONFIG.valuePattern());
		assertEquals("^[ a-zA-Z0-9._-]*$", CUSTOM_CONFIG.valuePattern().pattern());
	}
	
	@Test
	void advancedParsing() {
		assertFalse(DEFAULT_CONFIG.advancedParsing());
		assertTrue(CUSTOM_CONFIG.advancedParsing());
	}
	
	@Test
	void charset() {
		assertNotNull(DEFAULT_CONFIG.charset());
		assertEquals(StandardCharsets.UTF_8, DEFAULT_CONFIG.charset());
		assertNotNull(CUSTOM_CONFIG.charset());
		assertEquals(StandardCharsets.UTF_16, CUSTOM_CONFIG.charset());
	}
}
