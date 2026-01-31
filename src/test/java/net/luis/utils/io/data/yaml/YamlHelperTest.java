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

package net.luis.utils.io.data.yaml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlHelper}.<br>
 *
 * @author Luis-St
 */
class YamlHelperTest {
	
	@Test
	void isValidAnchorNameWithNull() {
		assertThrows(NullPointerException.class, () -> YamlHelper.isValidAnchorName(null));
	}
	
	@Test
	void isValidAnchorNameWithValidNames() {
		assertTrue(YamlHelper.isValidAnchorName("anchor"));
		assertTrue(YamlHelper.isValidAnchorName("Anchor"));
		assertTrue(YamlHelper.isValidAnchorName("ANCHOR"));
		assertTrue(YamlHelper.isValidAnchorName("anchor123"));
		assertTrue(YamlHelper.isValidAnchorName("123anchor"));
		assertTrue(YamlHelper.isValidAnchorName("123"));
		
		assertTrue(YamlHelper.isValidAnchorName("anchor_name"));
		assertTrue(YamlHelper.isValidAnchorName("_anchor"));
		assertTrue(YamlHelper.isValidAnchorName("anchor_"));
		assertTrue(YamlHelper.isValidAnchorName("_"));
		assertTrue(YamlHelper.isValidAnchorName("__"));
		
		assertTrue(YamlHelper.isValidAnchorName("anchor-name"));
		assertTrue(YamlHelper.isValidAnchorName("-anchor"));
		assertTrue(YamlHelper.isValidAnchorName("anchor-"));
		assertTrue(YamlHelper.isValidAnchorName("-"));
		assertTrue(YamlHelper.isValidAnchorName("--"));
		
		assertTrue(YamlHelper.isValidAnchorName("anchor_name-123"));
		assertTrue(YamlHelper.isValidAnchorName("_-_-_"));
		assertTrue(YamlHelper.isValidAnchorName("a1_b2-c3"));
	}
	
	@Test
	void isValidAnchorNameWithInvalidNames() {
		assertFalse(YamlHelper.isValidAnchorName("anchor name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor\tname"));
		assertFalse(YamlHelper.isValidAnchorName("anchor\nname"));
		assertFalse(YamlHelper.isValidAnchorName(" anchor"));
		assertFalse(YamlHelper.isValidAnchorName("anchor "));
		
		assertFalse(YamlHelper.isValidAnchorName("anchor.name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor:name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor,name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor;name"));
		
		assertFalse(YamlHelper.isValidAnchorName("anchor@name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor#name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor$name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor%name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor&name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor*name"));
		
		assertFalse(YamlHelper.isValidAnchorName("anchor[name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor]name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor{name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor}name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor(name"));
		assertFalse(YamlHelper.isValidAnchorName("anchor)name"));
	}
	
	@Test
	void isValidAnchorNameWithEmptyString() {
		assertTrue(YamlHelper.isValidAnchorName(""));
	}
	
	@Test
	void isYamlSpecialCharacterWithSpecialChars() {
		assertTrue(YamlHelper.isYamlSpecialCharacter('#'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('&'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('*'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('!'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('|'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('>'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('\''));
		assertTrue(YamlHelper.isYamlSpecialCharacter('"'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('%'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('@'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('`'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('{'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('['));
		assertTrue(YamlHelper.isYamlSpecialCharacter('-'));
		assertTrue(YamlHelper.isYamlSpecialCharacter('?'));
	}
	
	@Test
	void isYamlSpecialCharacterWithRegularChars() {
		assertFalse(YamlHelper.isYamlSpecialCharacter('a'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('A'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('z'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('Z'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('m'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('M'));
		
		assertFalse(YamlHelper.isYamlSpecialCharacter('0'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('1'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('5'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('9'));
		
		assertFalse(YamlHelper.isYamlSpecialCharacter(' '));
		assertFalse(YamlHelper.isYamlSpecialCharacter('\t'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('\n'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('\r'));
		
		assertFalse(YamlHelper.isYamlSpecialCharacter('_'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('.'));
		assertFalse(YamlHelper.isYamlSpecialCharacter(','));
		assertFalse(YamlHelper.isYamlSpecialCharacter(';'));
		assertFalse(YamlHelper.isYamlSpecialCharacter(':'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('/'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('\\'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('('));
		assertFalse(YamlHelper.isYamlSpecialCharacter(')'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('<'));
		assertFalse(YamlHelper.isYamlSpecialCharacter(']'));
		assertFalse(YamlHelper.isYamlSpecialCharacter('}'));
	}
	
	@Test
	void escapeStringWithNull() {
		assertThrows(NullPointerException.class, () -> YamlHelper.escapeString(null));
	}
	
	@Test
	void escapeStringWithRegularText() {
		assertEquals("", YamlHelper.escapeString(""));
		assertEquals(" ", YamlHelper.escapeString(" "));
		assertEquals("   ", YamlHelper.escapeString("   "));
		
		assertEquals("text", YamlHelper.escapeString("text"));
		assertEquals("Text", YamlHelper.escapeString("Text"));
		assertEquals("TEXT", YamlHelper.escapeString("TEXT"));
		assertEquals("123", YamlHelper.escapeString("123"));
		assertEquals("test123", YamlHelper.escapeString("test123"));
		assertEquals("hello world", YamlHelper.escapeString("hello world"));
		
		assertEquals("special chars: # & * ! | > ' % @ ` { [ - ?",
			YamlHelper.escapeString("special chars: # & * ! | > ' % @ ` { [ - ?"));
	}
	
	@Test
	void escapeStringWithSpecialCharacters() {
		assertEquals("\\\"", YamlHelper.escapeString("\""));
		assertEquals("\\\\", YamlHelper.escapeString("\\"));
		assertEquals("\\n", YamlHelper.escapeString("\n"));
		assertEquals("\\r", YamlHelper.escapeString("\r"));
		assertEquals("\\t", YamlHelper.escapeString("\t"));
		
		assertEquals("\\\"\\\"", YamlHelper.escapeString("\"\""));
		assertEquals("\\\\\\\\", YamlHelper.escapeString("\\\\"));
		assertEquals("\\n\\n", YamlHelper.escapeString("\n\n"));
		assertEquals("\\r\\r", YamlHelper.escapeString("\r\r"));
		assertEquals("\\t\\t", YamlHelper.escapeString("\t\t"));
		
		assertEquals("\\\"\\\\\\n\\r\\t", YamlHelper.escapeString("\"\\\n\r\t"));
	}
	
	@Test
	void escapeStringWithMixedContent() {
		assertEquals("line1\\nline2", YamlHelper.escapeString("line1\nline2"));
		assertEquals("tab\\there", YamlHelper.escapeString("tab\there"));
		assertEquals("quote: \\\"value\\\"", YamlHelper.escapeString("quote: \"value\""));
		assertEquals("path: C:\\\\Users\\\\test", YamlHelper.escapeString("path: C:\\Users\\test"));
		assertEquals("mixed\\n\\ttext\\\"here\\\\end", YamlHelper.escapeString("mixed\n\ttext\"here\\end"));
		assertEquals("carriage\\rreturn", YamlHelper.escapeString("carriage\rreturn"));
		assertEquals("all: \\\"\\\\\\n\\r\\t", YamlHelper.escapeString("all: \"\\\n\r\t"));
	}
}
