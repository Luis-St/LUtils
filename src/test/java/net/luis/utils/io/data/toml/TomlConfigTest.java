/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.data.toml;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlConfig}.<br>
 *
 * @author Luis-St
 */
class TomlConfigTest {
	
	@Test
	void constructorValidatesParameters() {
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new TomlConfig(
			true, true, null,
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, 80, false,
			null, false, StandardCharsets.UTF_8
		));
		
		assertThrows(NullPointerException.class, () -> new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, null
		));
	}
	
	@Test
	void constructorValidatesNegativeValues() {
		assertThrows(IllegalArgumentException.class, () -> new TomlConfig(
			true, true, "\t",
			false, -1, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new TomlConfig(
			true, true, "\t",
			false, 3, false, -1, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertThrows(IllegalArgumentException.class, () -> new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, -1, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, 0, false, 0, false, 0, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void defaultConfigurationValues() {
		TomlConfig config = TomlConfig.DEFAULT;
		
		assertTrue(config.strict());
		assertTrue(config.prettyPrint());
		assertEquals("  ", config.indent());
		assertFalse(config.useInlineTables());
		assertEquals(3, config.maxInlineTableSize());
		assertTrue(config.useInlineArrays());
		assertEquals(10, config.maxInlineArraySize());
		assertFalse(config.useMultiLineStrings());
		assertEquals(80, config.multiLineStringThreshold());
		assertTrue(config.useArrayOfTablesNotation());
		assertEquals(TomlConfig.DateTimeStyle.RFC_3339, config.dateTimeStyle());
		assertFalse(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void compactConfigurationValues() {
		TomlConfig config = TomlConfig.COMPACT;
		
		assertTrue(config.strict());
		assertFalse(config.prettyPrint());
		assertEquals("", config.indent());
		assertTrue(config.useInlineTables());
		assertEquals(5, config.maxInlineTableSize());
		assertTrue(config.useInlineArrays());
		assertEquals(20, config.maxInlineArraySize());
		assertFalse(config.useMultiLineStrings());
		assertEquals(120, config.multiLineStringThreshold());
		assertFalse(config.useArrayOfTablesNotation());
		assertEquals(TomlConfig.DateTimeStyle.RFC_3339, config.dateTimeStyle());
		assertFalse(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_8, config.charset());
	}
	
	@Test
	void customConfigurationValues() {
		TomlConfig config = new TomlConfig(
			false, false, "    ",
			true, 5, false, 20, true, 100, false,
			TomlConfig.DateTimeStyle.ISO_8601, true, StandardCharsets.UTF_16
		);
		
		assertFalse(config.strict());
		assertFalse(config.prettyPrint());
		assertEquals("    ", config.indent());
		assertTrue(config.useInlineTables());
		assertEquals(5, config.maxInlineTableSize());
		assertFalse(config.useInlineArrays());
		assertEquals(20, config.maxInlineArraySize());
		assertTrue(config.useMultiLineStrings());
		assertEquals(100, config.multiLineStringThreshold());
		assertFalse(config.useArrayOfTablesNotation());
		assertEquals(TomlConfig.DateTimeStyle.ISO_8601, config.dateTimeStyle());
		assertTrue(config.allowDuplicateKeys());
		assertEquals(StandardCharsets.UTF_16, config.charset());
	}
	
	@Test
	void allParameterCombinations() {
		for (boolean strict : new boolean[] { true, false }) {
			for (boolean prettyPrint : new boolean[] { true, false }) {
				for (boolean useInlineTables : new boolean[] { true, false }) {
					for (boolean useInlineArrays : new boolean[] { true, false }) {
						assertDoesNotThrow(() -> new TomlConfig(
							strict, prettyPrint, "  ",
							useInlineTables, 5, useInlineArrays, 10, false, 80, false,
							TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
						));
					}
				}
			}
		}
	}
	
	@Test
	void differentIndentStrings() {
		String[] indents = { "\t", "  ", "    ", "        ", "", "xyz" };
		
		for (String indent : indents) {
			TomlConfig config = new TomlConfig(
				true, true, indent,
				false, 3, false, 10, false, 80, false,
				TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
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
			TomlConfig config = new TomlConfig(
				true, true, "\t",
				false, 3, false, 10, false, 80, false,
				TomlConfig.DateTimeStyle.RFC_3339, false, charset
			);
			assertEquals(charset, config.charset());
		}
	}
	
	@Test
	void boundarySizeValues() {
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, 0, false, 0, false, 0, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, 1, false, 1, false, 1, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, 1000, false, 1000, false, 1000, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
		
		assertDoesNotThrow(() -> new TomlConfig(
			true, true, "\t",
			false, Integer.MAX_VALUE, false, Integer.MAX_VALUE, false, Integer.MAX_VALUE, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void recordEquality() {
		TomlConfig config1 = new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);
		
		TomlConfig config2 = new TomlConfig(
			true, true, "\t",
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);
		
		TomlConfig config3 = new TomlConfig(
			false, true, "\t",
			false, 3, false, 10, false, 80, false,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		);
		
		assertEquals(config1, config2);
		assertEquals(config1.hashCode(), config2.hashCode());
		assertNotEquals(config1, config3);
		
		assertEquals(TomlConfig.DEFAULT, new TomlConfig(
			true, true, "  ",
			false, 3, true, 10, false, 80, true,
			TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
		));
	}
	
	@Test
	void recordToString() {
		TomlConfig config = new TomlConfig(
			true, false, "  ",
			true, 5, false, 20, true, 100, false,
			TomlConfig.DateTimeStyle.ISO_8601, true, StandardCharsets.UTF_16
		);
		
		String toString = config.toString();
		assertTrue(toString.contains("strict=true"));
		assertTrue(toString.contains("prettyPrint=false"));
		assertTrue(toString.contains("useInlineTables=true"));
		assertTrue(toString.contains("maxInlineTableSize=5"));
		assertTrue(toString.contains("useInlineArrays=false"));
		assertTrue(toString.contains("maxInlineArraySize=20"));
		assertTrue(toString.contains("useMultiLineStrings=true"));
		assertTrue(toString.contains("multiLineStringThreshold=100"));
		assertTrue(toString.contains("useArrayOfTablesNotation=false"));
		assertTrue(toString.contains("ISO_8601"));
		assertTrue(toString.contains("allowDuplicateKeys=true"));
		assertTrue(toString.contains("UTF-16"));
	}
	
	@Test
	void dateTimeStyleValues() {
		assertEquals(3, TomlConfig.DateTimeStyle.values().length);
		
		assertNotNull(TomlConfig.DateTimeStyle.RFC_3339);
		assertNotNull(TomlConfig.DateTimeStyle.ISO_8601);
		assertNotNull(TomlConfig.DateTimeStyle.SPACE_SEPARATED);
		
		assertEquals(TomlConfig.DateTimeStyle.RFC_3339, TomlConfig.DateTimeStyle.valueOf("RFC_3339"));
		assertEquals(TomlConfig.DateTimeStyle.ISO_8601, TomlConfig.DateTimeStyle.valueOf("ISO_8601"));
		assertEquals(TomlConfig.DateTimeStyle.SPACE_SEPARATED, TomlConfig.DateTimeStyle.valueOf("SPACE_SEPARATED"));
	}
	
	@Test
	void dateTimeStyleInConfigs() {
		for (TomlConfig.DateTimeStyle style : TomlConfig.DateTimeStyle.values()) {
			TomlConfig config = new TomlConfig(
				true, true, "\t",
				false, 3, false, 10, false, 80, false,
				style, false, StandardCharsets.UTF_8
			);
			assertEquals(style, config.dateTimeStyle());
		}
	}
}
