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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlHelper}.<br>
 *
 * @author Luis-St
 */
class TomlHelperTest {
	
	@Test
	void formatKeyWithNull() {
		assertThrows(NullPointerException.class, () -> TomlHelper.formatKey(null));
	}
	
	@Test
	void formatKeyWithBareKeys() {
		assertEquals("key", TomlHelper.formatKey("key"));
		assertEquals("Key", TomlHelper.formatKey("Key"));
		assertEquals("KEY", TomlHelper.formatKey("KEY"));
		
		assertEquals("key123", TomlHelper.formatKey("key123"));
		assertEquals("123key", TomlHelper.formatKey("123key"));
		assertEquals("123", TomlHelper.formatKey("123"));
		
		assertEquals("key_name", TomlHelper.formatKey("key_name"));
		assertEquals("_key", TomlHelper.formatKey("_key"));
		assertEquals("key_", TomlHelper.formatKey("key_"));
		assertEquals("_", TomlHelper.formatKey("_"));
		
		assertEquals("key-name", TomlHelper.formatKey("key-name"));
		assertEquals("-key", TomlHelper.formatKey("-key"));
		assertEquals("key-", TomlHelper.formatKey("key-"));
		assertEquals("-", TomlHelper.formatKey("-"));
		
		assertEquals("key_name-123", TomlHelper.formatKey("key_name-123"));
		assertEquals("A1_b2-C3", TomlHelper.formatKey("A1_b2-C3"));
	}
	
	@Test
	void formatKeyWithQuotedKeys() {
		assertEquals("\"key.name\"", TomlHelper.formatKey("key.name"));
		assertEquals("\"key:name\"", TomlHelper.formatKey("key:name"));
		assertEquals("\"key name\"", TomlHelper.formatKey("key name"));
		assertEquals("\"key\\tname\"", TomlHelper.formatKey("key\tname"));
		assertEquals("\"key\\nname\"", TomlHelper.formatKey("key\nname"));
		
		assertEquals("\"key@name\"", TomlHelper.formatKey("key@name"));
		assertEquals("\"key#name\"", TomlHelper.formatKey("key#name"));
		assertEquals("\"key$name\"", TomlHelper.formatKey("key$name"));
		assertEquals("\"key%name\"", TomlHelper.formatKey("key%name"));
		
		assertEquals("\"key[name\"", TomlHelper.formatKey("key[name"));
		assertEquals("\"key]name\"", TomlHelper.formatKey("key]name"));
		assertEquals("\"key{name\"", TomlHelper.formatKey("key{name"));
		assertEquals("\"key}name\"", TomlHelper.formatKey("key}name"));
	}
	
	@Test
	void formatKeyWithEmptyKey() {
		assertEquals("\"\"", TomlHelper.formatKey(""));
	}
	
	@Test
	void formatKeyWithSpecialCharacters() {
		assertEquals("\"key\\\"name\"", TomlHelper.formatKey("key\"name"));
		assertEquals("\"key\\\\name\"", TomlHelper.formatKey("key\\name"));
		assertEquals("\"key\\\"\\\\name\"", TomlHelper.formatKey("key\"\\name"));
		
		assertEquals("\" \"", TomlHelper.formatKey(" "));
		assertEquals("\"  \"", TomlHelper.formatKey("  "));
		assertEquals("\" key\"", TomlHelper.formatKey(" key"));
		assertEquals("\"key \"", TomlHelper.formatKey("key "));
	}
	
	@Test
	void escapeStringWithNull() {
		assertThrows(NullPointerException.class, () -> TomlHelper.escapeString(null));
	}
	
	@Test
	void escapeStringWithRegularText() {
		assertEquals("", TomlHelper.escapeString(""));
		assertEquals(" ", TomlHelper.escapeString(" "));
		assertEquals("   ", TomlHelper.escapeString("   "));
		
		assertEquals("text", TomlHelper.escapeString("text"));
		assertEquals("Text", TomlHelper.escapeString("Text"));
		assertEquals("TEXT", TomlHelper.escapeString("TEXT"));
		assertEquals("123", TomlHelper.escapeString("123"));
		assertEquals("test123", TomlHelper.escapeString("test123"));
		assertEquals("hello world", TomlHelper.escapeString("hello world"));
		
		assertEquals("special chars: # @ $ % ^ & * ( ) [ ] { } | ; : , . < > / ?",
			TomlHelper.escapeString("special chars: # @ $ % ^ & * ( ) [ ] { } | ; : , . < > / ?"));
	}
	
	@Test
	void escapeStringWithSpecialCharacters() {
		assertEquals("\\\"", TomlHelper.escapeString("\""));
		assertEquals("\\\\", TomlHelper.escapeString("\\"));
		assertEquals("\\b", TomlHelper.escapeString("\b"));
		assertEquals("\\f", TomlHelper.escapeString("\f"));
		assertEquals("\\n", TomlHelper.escapeString("\n"));
		assertEquals("\\r", TomlHelper.escapeString("\r"));
		assertEquals("\\t", TomlHelper.escapeString("\t"));
		
		assertEquals("\\\"\\\"", TomlHelper.escapeString("\"\""));
		assertEquals("\\\\\\\\", TomlHelper.escapeString("\\\\"));
		assertEquals("\\n\\n", TomlHelper.escapeString("\n\n"));
		assertEquals("\\r\\r", TomlHelper.escapeString("\r\r"));
		assertEquals("\\t\\t", TomlHelper.escapeString("\t\t"));
		
		assertEquals("\\\"\\\\\\b\\f\\n\\r\\t", TomlHelper.escapeString("\"\\\b\f\n\r\t"));
	}
	
	@Test
	void escapeStringWithControlCharacters() {
		assertEquals("\\u0000", TomlHelper.escapeString("\u0000"));
		assertEquals("\\u0001", TomlHelper.escapeString("\u0001"));
		assertEquals("\\u0002", TomlHelper.escapeString("\u0002"));
		assertEquals("\\u001F", TomlHelper.escapeString("\u001F"));
		
		assertEquals("a\\u0000b", TomlHelper.escapeString("a\u0000b"));
		assertEquals("\\u0001\\u0002\\u0003", TomlHelper.escapeString("\u0001\u0002\u0003"));
		
		assertEquals(" ", TomlHelper.escapeString("\u0020"));
		assertEquals("!", TomlHelper.escapeString("\u0021"));
	}
	
	@Test
	void escapeStringWithMixedContent() {
		assertEquals("line1\\nline2", TomlHelper.escapeString("line1\nline2"));
		assertEquals("tab\\there", TomlHelper.escapeString("tab\there"));
		assertEquals("quote: \\\"value\\\"", TomlHelper.escapeString("quote: \"value\""));
		assertEquals("path: C:\\\\Users\\\\test", TomlHelper.escapeString("path: C:\\Users\\test"));
		assertEquals("mixed\\n\\ttext\\\"here\\\\end", TomlHelper.escapeString("mixed\n\ttext\"here\\end"));
		assertEquals("backspace\\band formfeed\\f", TomlHelper.escapeString("backspace\band formfeed\f"));
		assertEquals("carriage\\rreturn", TomlHelper.escapeString("carriage\rreturn"));
		assertEquals("all: \\\"\\\\\\b\\f\\n\\r\\t", TomlHelper.escapeString("all: \"\\\b\f\n\r\t"));
		assertEquals("control\\u0001char", TomlHelper.escapeString("control\u0001char"));
	}
}
