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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.ini.exception.IniSyntaxException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniConfig}.<br>
 *
 * @author Luis-St
 */
class IniConfigTest {
	
	private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
	private static final Pattern SECTION_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+$");
	
	@Test
	void constructorValidatesParameters() {
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, null, Set.of(';', '#'), '=', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, "\t", null, '=', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			null, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			KEY_PATTERN, null,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			null, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, null
		));
	}
	
	@Test
	void constructorValidatesSeparator() {
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '\0', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), ' ', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '\t', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), ';', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '#', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), ':', 1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void constructorValidatesAlignment() {
		assertThrows(IllegalArgumentException.class, () -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', -1,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 0,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void defaultConfigurationValues() {
		IniConfig config = IniConfig.DEFAULT;
		
		assertTrue(config.strict());
		assertTrue(config.prettyPrint());
		assertEquals("", config.indent());
		assertEquals(Set.of(';', '#'), config.commentCharacters());
		assertEquals('=', config.separator());
		assertEquals(1, config.alignment());
		assertFalse(config.allowDuplicateKeys());
		assertFalse(config.allowDuplicateSections());
		assertFalse(config.parseTypedValues());
		assertEquals("^[a-zA-Z0-9._-]+$", config.keyPattern().pattern());
		assertEquals("^[a-zA-Z0-9._-]+$", config.sectionPattern().pattern());
		assertEquals(IniConfig.NullStyle.EMPTY, config.nullStyle());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void customConfigurationValues() {
		IniConfig config = new IniConfig(
			false, false, "    ", Set.of('#'), ':', 2,
			true, true, true,
			Pattern.compile("^[a-z]+$"), Pattern.compile("^[A-Z]+$"),
			IniConfig.NullStyle.NULL_STRING, StandardCharsets.UTF_16
		);
		
		assertFalse(config.strict());
		assertFalse(config.prettyPrint());
		assertEquals("    ", config.indent());
		assertEquals(Set.of('#'), config.commentCharacters());
		assertEquals(':', config.separator());
		assertEquals(2, config.alignment());
		assertTrue(config.allowDuplicateKeys());
		assertTrue(config.allowDuplicateSections());
		assertTrue(config.parseTypedValues());
		assertEquals("^[a-z]+$", config.keyPattern().pattern());
		assertEquals("^[A-Z]+$", config.sectionPattern().pattern());
		assertEquals(IniConfig.NullStyle.NULL_STRING, config.nullStyle());
		assertEquals(StandardCharsets.UTF_16, config.charset());
	}
	
	@Test
	void allParameterCombinations() {
		for (boolean strict : new boolean[] { true, false }) {
			for (boolean prettyPrint : new boolean[] { true, false }) {
				for (boolean allowDuplicateKeys : new boolean[] { true, false }) {
					for (boolean allowDuplicateSections : new boolean[] { true, false }) {
						for (boolean parseTypedValues : new boolean[] { true, false }) {
							assertDoesNotThrow(() -> new IniConfig(
								strict, prettyPrint, "  ", Set.of(';'), '=', 1,
								allowDuplicateKeys, allowDuplicateSections, parseTypedValues,
								KEY_PATTERN, SECTION_PATTERN,
								IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
							));
						}
					}
				}
			}
		}
	}
	
	@Test
	void differentIndentStrings() {
		String[] indents = { "\t", "  ", "    ", "        ", "", "xyz" };
		
		for (String indent : indents) {
			IniConfig config = new IniConfig(
				true, true, indent, Set.of(';', '#'), '=', 1,
				false, false, false,
				KEY_PATTERN, SECTION_PATTERN,
				IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
			);
			assertEquals(indent, config.indent());
		}
	}
	
	@Test
	void differentCharsets() {
		var charsets = new java.nio.charset.Charset[] {
			StandardCharsets.UTF_8,
			StandardCharsets.UTF_16,
			StandardCharsets.UTF_16BE,
			StandardCharsets.UTF_16LE,
			StandardCharsets.ISO_8859_1,
			StandardCharsets.US_ASCII
		};
		
		for (var charset : charsets) {
			IniConfig config = new IniConfig(
				true, true, "\t", Set.of(';', '#'), '=', 1,
				false, false, false,
				KEY_PATTERN, SECTION_PATTERN,
				IniConfig.NullStyle.EMPTY, charset
			);
			assertEquals(charset, config.charset());
		}
	}
	
	@Test
	void boundaryAlignmentValues() {
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 0,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 100,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', Integer.MAX_VALUE,
			false, false, false,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void recordEquality() {
		IniConfig config1 = new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniConfig config2 = new IniConfig(
			true, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		IniConfig config3 = new IniConfig(
			false, true, "\t", Set.of(';', '#'), '=', 1,
			false, false, false,
			Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		assertNotEquals(config1, config3);
	}
	
	@Test
	void recordToString() {
		IniConfig config = new IniConfig(
			true, false, "  ", Set.of(';'), ':', 2,
			true, false, true,
			KEY_PATTERN, SECTION_PATTERN,
			IniConfig.NullStyle.NULL_STRING, StandardCharsets.UTF_16
		);
		
		String toString = config.toString();
		assertTrue(toString.contains("strict=true"));
		assertTrue(toString.contains("prettyPrint=false"));
		assertTrue(toString.contains("allowDuplicateKeys=true"));
		assertTrue(toString.contains("allowDuplicateSections=false"));
		assertTrue(toString.contains("parseTypedValues=true"));
		assertTrue(toString.contains("NULL_STRING"));
		assertTrue(toString.contains("UTF-16"));
	}
	
	@Test
	void nullStyleValues() {
		assertEquals(3, IniConfig.NullStyle.values().length);
		
		assertNotNull(IniConfig.NullStyle.EMPTY);
		assertNotNull(IniConfig.NullStyle.NULL_STRING);
		assertNotNull(IniConfig.NullStyle.SKIP);
		
		assertEquals(IniConfig.NullStyle.EMPTY, IniConfig.NullStyle.valueOf("EMPTY"));
		assertEquals(IniConfig.NullStyle.NULL_STRING, IniConfig.NullStyle.valueOf("NULL_STRING"));
		assertEquals(IniConfig.NullStyle.SKIP, IniConfig.NullStyle.valueOf("SKIP"));
	}
	
	@Test
	void ensureKeyMatchesValid() {
		IniConfig config = IniConfig.DEFAULT;
		
		assertDoesNotThrow(() -> config.ensureKeyMatches("key"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("my_key"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("my-key"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("my.key"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("key123"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("KEY"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("a"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("1"));
	}
	
	@Test
	void ensureKeyMatchesInvalid() {
		IniConfig config = IniConfig.DEFAULT;
		
		assertThrows(NullPointerException.class, () -> config.ensureKeyMatches(null));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches(""));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("   "));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("key with space"));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("key=value"));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("key[0]"));
	}
	
	@Test
	void ensureSectionMatchesValid() {
		IniConfig config = IniConfig.DEFAULT;
		
		assertDoesNotThrow(() -> config.ensureSectionMatches("section"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("my_section"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("my-section"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("my.section"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("section123"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("SECTION"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("a"));
		assertDoesNotThrow(() -> config.ensureSectionMatches("1"));
	}
	
	@Test
	void ensureSectionMatchesInvalid() {
		IniConfig config = IniConfig.DEFAULT;
		
		assertThrows(NullPointerException.class, () -> config.ensureSectionMatches(null));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches(""));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("   "));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("section with space"));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("[section]"));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("section=value"));
	}
	
	@Test
	void customPatternMatching() {
		IniConfig config = new IniConfig(
			true, true, "", Set.of(';', '#'), '=', 1,
			false, false, false,
			Pattern.compile("^[a-z]+$"), Pattern.compile("^[A-Z]+$"),
			IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
		);
		
		assertDoesNotThrow(() -> config.ensureKeyMatches("lowercase"));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("UPPERCASE"));
		assertThrows(IniSyntaxException.class, () -> config.ensureKeyMatches("MixedCase"));
		
		assertDoesNotThrow(() -> config.ensureSectionMatches("UPPERCASE"));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("lowercase"));
		assertThrows(IniSyntaxException.class, () -> config.ensureSectionMatches("MixedCase"));
	}
}
