/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.reader;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ScopedStringReader}.<br>
 *
 * @author Luis-St
 */
class ScopedStringReaderTest {
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader(null));
	}
	
	@Test
	void readScope() {
		ScopedStringReader reader = new ScopedStringReader(" ");
		reader.read();
		assertThrows(NullPointerException.class, () -> reader.readScope(null));
		reader.reset();
		assertThrows(IllegalArgumentException.class, () -> reader.readScope(ScopedStringReader.PARENTHESES));
		reader.read();
		assertEquals("", reader.readScope(ScopedStringReader.PARENTHESES));
	}
	
	@Test
	void readScopeParentheses() {
		ScopedStringReader success = new ScopedStringReader("(Hello) (World it is (nice here))");
		assertEquals("(Hello)", success.readScope(ScopedStringReader.PARENTHESES));
		assertEquals(' ', success.read());
		assertEquals("(World it is (nice here))", success.readScope(ScopedStringReader.PARENTHESES));
		
		ScopedStringReader failMissing = new ScopedStringReader("(Hello) (World it is (nice here)");
		assertEquals("(Hello)", failMissing.readScope(ScopedStringReader.PARENTHESES));
		assertEquals(' ', failMissing.read());
		assertThrows(IllegalStateException.class, () -> failMissing.readScope(ScopedStringReader.PARENTHESES));
		
		ScopedStringReader failExtra = new ScopedStringReader("(Hello) (World (it is (nice here))");
		assertEquals("(Hello)", failExtra.readScope(ScopedStringReader.PARENTHESES));
		assertEquals(' ', failExtra.read());
		assertThrows(IllegalStateException.class, () -> failExtra.readScope(ScopedStringReader.PARENTHESES));
	}
	
	@Test
	void readScopeCurlyBrackets() {
		ScopedStringReader success = new ScopedStringReader("{Hello} {World it is {nice here}}");
		assertEquals("{Hello}", success.readScope(ScopedStringReader.CURLY_BRACKETS));
		assertEquals(' ', success.read());
		assertEquals("{World it is {nice here}}", success.readScope(ScopedStringReader.CURLY_BRACKETS));
		
		ScopedStringReader failMissing = new ScopedStringReader("{Hello} {World it is {nice here}");
		assertEquals("{Hello}", failMissing.readScope(ScopedStringReader.CURLY_BRACKETS));
		assertEquals(' ', failMissing.read());
		assertThrows(IllegalStateException.class, () -> failMissing.readScope(ScopedStringReader.CURLY_BRACKETS));
		
		ScopedStringReader failExtra = new ScopedStringReader("{Hello} {World {it is {nice here}}");
		assertEquals("{Hello}", failExtra.readScope(ScopedStringReader.CURLY_BRACKETS));
		assertEquals(' ', failExtra.read());
		assertThrows(IllegalStateException.class, () -> failExtra.readScope(ScopedStringReader.CURLY_BRACKETS));
	}
	
	@Test
	void readScopeSquareBrackets() {
		ScopedStringReader success = new ScopedStringReader("[Hello] [World it is [nice here]]");
		assertEquals("[Hello]", success.readScope(ScopedStringReader.SQUARE_BRACKETS));
		assertEquals(' ', success.read());
		assertEquals("[World it is [nice here]]", success.readScope(ScopedStringReader.SQUARE_BRACKETS));
		
		ScopedStringReader failMissing = new ScopedStringReader("[Hello] [World it is [nice here]");
		assertEquals("[Hello]", failMissing.readScope(ScopedStringReader.SQUARE_BRACKETS));
		assertEquals(' ', failMissing.read());
		assertThrows(IllegalStateException.class, () -> failMissing.readScope(ScopedStringReader.SQUARE_BRACKETS));
		
		ScopedStringReader failExtra = new ScopedStringReader("[Hello] [World [it is [nice here]]");
		assertEquals("[Hello]", failExtra.readScope(ScopedStringReader.SQUARE_BRACKETS));
		assertEquals(' ', failExtra.read());
		assertThrows(IllegalStateException.class, () -> failExtra.readScope(ScopedStringReader.SQUARE_BRACKETS));
	}
	
	@Test
	void readList() {
		ScopedStringReader reader = new ScopedStringReader("[ true , false ] [  10.0  ,  15.5  ,  20.6  ,  25.89  ] [10,15,20,25] [\"Hello\", \"World\"] [[\"str1\"], [\"str1\", \"str2\"]]");
		assertEquals(List.of(true, false), reader.readList(StringReader::readBoolean));
		assertEquals(' ', reader.read());
		assertEquals(List.of(10.0, 15.5, 20.6, 25.89), reader.readList(StringReader::readDouble));
		assertEquals(' ', reader.read());
		assertEquals(List.of(10, 15, 20, 25), reader.readList(StringReader::readInt));
		assertEquals(' ', reader.read());
		assertEquals(List.of("Hello", "World"), reader.readList(StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(List.of(List.of("str1"), List.of("str1", "str2")), reader.readList(r -> r.readList(StringReader::readString)));
	}
	
	@Test
	void readSet() {
		ScopedStringReader reader = new ScopedStringReader("( true , false ) (  10.0  ,  15.5  ,  20.6  ,  25.89  ) (10,15,20,25) (\"Hello\", \"World\") ([\"str1\"], [\"str1\", \"str2\"])");
		assertEquals(Set.of(true, false), reader.readSet(StringReader::readBoolean));
		assertEquals(' ', reader.read());
		assertEquals(Set.of(10.0, 15.5, 20.6, 25.89), reader.readSet(StringReader::readDouble));
		assertEquals(' ', reader.read());
		assertEquals(Set.of(10, 15, 20, 25), reader.readSet(StringReader::readInt));
		assertEquals(' ', reader.read());
		assertEquals(Set.of("Hello", "World"), reader.readSet(StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Set.of(List.of("str1"), List.of("str1", "str2")), reader.readSet(r -> r.readList(StringReader::readString)));
	}
	
	@Test
	void readMap() {
		ScopedStringReader reader = new ScopedStringReader("{key1= value1, key2= value2} {key1=10, key2=20} {0=\"Hello\", 1=\"World\"} { 0.0 = [ \"str1\" ], 1.0 = [ \"str1\" , \"str2\" ] }");
		assertEquals(Map.of("key1", "value1", "key2", "value2"), reader.readMap(StringReader::readString, StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Map.of("key1", 10, "key2", 20), reader.readMap(StringReader::readString, StringReader::readInt));
		assertEquals(' ', reader.read());
		assertEquals(Map.of(0, "Hello", 1, "World"), reader.readMap(StringReader::readInt, StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Map.of(0.0, List.of("str1"), 1.0, List.of("str1", "str2")), reader.readMap(StringReader::readDouble, r -> r.readList(StringReader::readString)));
	}
	
	@Nested
	class StringScopeTest {
		
		@Test
		void constructor() {
			assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader.StringScope('\0', '\''));
			assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader.StringScope('\'', '\0'));
			assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader.StringScope('"', '"'));
			assertDoesNotThrow(() -> new ScopedStringReader.StringScope('(', ')'));
		}
	}
}