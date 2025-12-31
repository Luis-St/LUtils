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
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyConfig}.<br>
 *
 * @author Luis-St
 */
class PropertyConfigTest {
	
	@Test
	void defaultConfiguration() {
		PropertyConfig config = PropertyConfig.DEFAULT;
		
		assertEquals('=', config.separator());
		assertEquals(1, config.alignment());
		assertEquals(Set.of('#'), config.commentCharacters());
		assertNotNull(config.keyPattern());
		assertNotNull(config.valuePattern());
		assertFalse(config.advancedParsing());
		assertEquals(StandardCharsets.UTF_8, config.charset());
		assertFalse(config.prettyPrint());
		assertEquals("", config.indent());
		assertEquals('[', config.arrayOpenChar());
		assertEquals(']', config.arrayCloseChar());
		assertEquals(',', config.arraySeparator());
		assertTrue(config.allowMultiLineArrays());
		assertFalse(config.parseTypedValues());
		assertEquals(PropertyConfig.NullStyle.EMPTY, config.nullStyle());
		assertFalse(config.enableWriteCompaction());
		assertEquals(2, config.minCompactionGroupSize());
		assertEquals(':', config.variableTypeSeparator());
		assertEquals(":-", config.defaultValueMarker());
		assertNull(config.customVariables());
	}
	
	@Test
	void advancedConfiguration() {
		PropertyConfig config = PropertyConfig.ADVANCED;
		
		assertEquals('=', config.separator());
		assertEquals(1, config.alignment());
		assertTrue(config.advancedParsing());
		assertTrue(config.prettyPrint());
		assertEquals("\t", config.indent());
		assertTrue(config.allowMultiLineArrays());
		assertTrue(config.parseTypedValues());
		assertTrue(config.enableWriteCompaction());
		assertEquals(2, config.minCompactionGroupSize());
	}
	
	@Test
	void constructorWithNullValues() {
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, null,
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			null, Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), null,
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, null,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, null,
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			null,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(NullPointerException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', null,
			null
		));
	}
	
	@Test
	void constructorWithInvalidSeparator() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'\0', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			' ', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'\t', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'\n', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void constructorWithSeparatorAsCommentCharacter() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'#', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void constructorWithNegativeAlignment() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', -1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void constructorWithInvalidCompactionGroupSize() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 1,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 0,
			':', ":-",
			null
		));
	}
	
	@Test
	void constructorWithInvalidArrayCharacters() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', '[', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', '[',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ']',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void constructorWithEmptyDefaultValueMarker() {
		assertThrows(IllegalArgumentException.class, () -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', "",
			null
		));
	}
	
	@Test
	void ensureKeyMatches() {
		PropertyConfig config = PropertyConfig.DEFAULT;
		
		assertThrows(NullPointerException.class, () -> config.ensureKeyMatches(null));
		assertThrows(PropertySyntaxException.class, () -> config.ensureKeyMatches(""));
		assertThrows(PropertySyntaxException.class, () -> config.ensureKeyMatches("   "));
		
		assertDoesNotThrow(() -> config.ensureKeyMatches("validKey"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("key.with.dots"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("key_with_underscores"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("key-with-hyphens"));
		assertDoesNotThrow(() -> config.ensureKeyMatches("key123"));
	}
	
	@Test
	void ensureKeyMatchesWithCustomPattern() {
		PropertyConfig config = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile("^[a-z]+$"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		assertDoesNotThrow(() -> config.ensureKeyMatches("lowercase"));
		assertThrows(PropertySyntaxException.class, () -> config.ensureKeyMatches("UPPERCASE"));
		assertThrows(PropertySyntaxException.class, () -> config.ensureKeyMatches("mixed123"));
		assertThrows(PropertySyntaxException.class, () -> config.ensureKeyMatches("with.dot"));
	}
	
	@Test
	void ensureValueMatches() {
		PropertyConfig config = PropertyConfig.DEFAULT;
		
		assertThrows(NullPointerException.class, () -> config.ensureValueMatches(null));
		
		assertDoesNotThrow(() -> config.ensureValueMatches("any value"));
		assertDoesNotThrow(() -> config.ensureValueMatches(""));
		assertDoesNotThrow(() -> config.ensureValueMatches("   "));
		assertDoesNotThrow(() -> config.ensureValueMatches("123"));
		assertDoesNotThrow(() -> config.ensureValueMatches("special!@#$%"));
	}
	
	@Test
	void ensureValueMatchesWithCustomPattern() {
		PropertyConfig config = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile("^[0-9]+$"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		assertDoesNotThrow(() -> config.ensureValueMatches("123"));
		assertDoesNotThrow(() -> config.ensureValueMatches("0"));
		assertThrows(PropertySyntaxException.class, () -> config.ensureValueMatches("abc"));
		assertThrows(PropertySyntaxException.class, () -> config.ensureValueMatches("12.34"));
	}
	
	@Test
	void getCustomVariables() {
		PropertyConfig configWithNull = PropertyConfig.DEFAULT;
		assertEquals(Map.of(), configWithNull.getCustomVariables());
		
		Map<String, String> customVars = Map.of("key1", "value1", "key2", "value2");
		PropertyConfig configWithVars = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			customVars
		);
		
		Map<String, String> retrieved = configWithVars.getCustomVariables();
		assertEquals(customVars, retrieved);
		assertThrows(UnsupportedOperationException.class, () -> retrieved.put("new", "value"));
	}
	
	@Test
	void nullStyleEnumValues() {
		assertEquals(3, PropertyConfig.NullStyle.values().length);
		assertEquals(PropertyConfig.NullStyle.EMPTY, PropertyConfig.NullStyle.valueOf("EMPTY"));
		assertEquals(PropertyConfig.NullStyle.NULL_STRING, PropertyConfig.NullStyle.valueOf("NULL_STRING"));
		assertEquals(PropertyConfig.NullStyle.TILDE, PropertyConfig.NullStyle.valueOf("TILDE"));
	}
	
	@Test
	void recordEquality() {
		PropertyConfig config1 = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyConfig config2 = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		PropertyConfig config3 = new PropertyConfig(
			':', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		assertNotEquals(config1, config3);
	}
	
	@Test
	void customConfiguration() {
		PropertyConfig config = new PropertyConfig(
			':',
			2,
			Set.of('#', ';', '!'),
			Pattern.compile("^\\w+$"),
			Pattern.compile("^[^=]+$"),
			true,
			StandardCharsets.ISO_8859_1,
			true,
			"    ",
			'(',
			')',
			';',
			false,
			true,
			PropertyConfig.NullStyle.TILDE,
			true,
			3,
			'|',
			"||",
			Map.of("ENV", "production")
		);
		
		assertEquals(':', config.separator());
		assertEquals(2, config.alignment());
		assertEquals(Set.of('#', ';', '!'), config.commentCharacters());
		assertTrue(config.advancedParsing());
		assertEquals(StandardCharsets.ISO_8859_1, config.charset());
		assertTrue(config.prettyPrint());
		assertEquals("    ", config.indent());
		assertEquals('(', config.arrayOpenChar());
		assertEquals(')', config.arrayCloseChar());
		assertEquals(';', config.arraySeparator());
		assertFalse(config.allowMultiLineArrays());
		assertTrue(config.parseTypedValues());
		assertEquals(PropertyConfig.NullStyle.TILDE, config.nullStyle());
		assertTrue(config.enableWriteCompaction());
		assertEquals(3, config.minCompactionGroupSize());
		assertEquals('|', config.variableTypeSeparator());
		assertEquals("||", config.defaultValueMarker());
		assertEquals(Map.of("ENV", "production"), config.getCustomVariables());
	}
	
	@Test
	void validSeparators() {
		assertDoesNotThrow(() -> new PropertyConfig(
			':', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertDoesNotThrow(() -> new PropertyConfig(
			'|', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertDoesNotThrow(() -> new PropertyConfig(
			'-', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void zeroAlignment() {
		assertDoesNotThrow(() -> new PropertyConfig(
			'=', 0, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
	}
	
	@Test
	void edgeCaseCompactionGroupSize() {
		assertDoesNotThrow(() -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		));
		
		assertDoesNotThrow(() -> new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 100,
			':', ":-",
			null
		));
	}
}
