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

package net.luis.utils.io.reader;

import net.luis.utils.exception.InvalidStringException;
import org.junit.jupiter.api.Test;

import java.io.Reader;
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
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((String) null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((Reader) null));
	}
	
	@Test
	void readScope() {
		ScopedStringReader reader = new ScopedStringReader(" ");
		reader.read();
		assertThrows(NullPointerException.class, () -> reader.readScope(null));
		reader.reset();
		assertThrows(IllegalArgumentException.class, () -> reader.readScope(StringScope.PARENTHESES));
		reader.read();
		assertEquals("", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParentheses() {
		ScopedStringReader success = new ScopedStringReader("(Hello) (World it is (nice here))");
		assertEquals("(Hello)", success.readScope(StringScope.PARENTHESES));
		assertEquals(' ', success.read());
		assertEquals("(World it is (nice here))", success.readScope(StringScope.PARENTHESES));
		
		ScopedStringReader failMissing = new ScopedStringReader("(Hello) (World it is (nice here)");
		assertEquals("(Hello)", failMissing.readScope(StringScope.PARENTHESES));
		assertEquals(' ', failMissing.read());
		assertThrows(InvalidStringException.class, () -> failMissing.readScope(StringScope.PARENTHESES));
		
		ScopedStringReader failExtra = new ScopedStringReader("(Hello) (World (it is (nice here))");
		assertEquals("(Hello)", failExtra.readScope(StringScope.PARENTHESES));
		assertEquals(' ', failExtra.read());
		assertThrows(InvalidStringException.class, () -> failExtra.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeCurlyBrackets() {
		ScopedStringReader success = new ScopedStringReader("{Hello} {World it is {nice here}}");
		assertEquals("{Hello}", success.readScope(StringScope.CURLY_BRACKETS));
		assertEquals(' ', success.read());
		assertEquals("{World it is {nice here}}", success.readScope(StringScope.CURLY_BRACKETS));
		
		ScopedStringReader failMissing = new ScopedStringReader("{Hello} {World it is {nice here}");
		assertEquals("{Hello}", failMissing.readScope(StringScope.CURLY_BRACKETS));
		assertEquals(' ', failMissing.read());
		assertThrows(InvalidStringException.class, () -> failMissing.readScope(StringScope.CURLY_BRACKETS));
		
		ScopedStringReader failExtra = new ScopedStringReader("{Hello} {World {it is {nice here}}");
		assertEquals("{Hello}", failExtra.readScope(StringScope.CURLY_BRACKETS));
		assertEquals(' ', failExtra.read());
		assertThrows(InvalidStringException.class, () -> failExtra.readScope(StringScope.CURLY_BRACKETS));
	}
	
	@Test
	void readScopeSquareBrackets() {
		ScopedStringReader success = new ScopedStringReader("[Hello] [World it is [nice here]]");
		assertEquals("[Hello]", success.readScope(StringScope.SQUARE_BRACKETS));
		assertEquals(' ', success.read());
		assertEquals("[World it is [nice here]]", success.readScope(StringScope.SQUARE_BRACKETS));
		
		ScopedStringReader failMissing = new ScopedStringReader("[Hello] [World it is [nice here]");
		assertEquals("[Hello]", failMissing.readScope(StringScope.SQUARE_BRACKETS));
		assertEquals(' ', failMissing.read());
		assertThrows(InvalidStringException.class, () -> failMissing.readScope(StringScope.SQUARE_BRACKETS));
		
		ScopedStringReader failExtra = new ScopedStringReader("[Hello] [World [it is [nice here]]");
		assertEquals("[Hello]", failExtra.readScope(StringScope.SQUARE_BRACKETS));
		assertEquals(' ', failExtra.read());
		assertThrows(InvalidStringException.class, () -> failExtra.readScope(StringScope.SQUARE_BRACKETS));
	}
	
	@Test
	void readScopeAngleBrackets() {
		ScopedStringReader success = new ScopedStringReader("<Hello> <World it is <nice here>>");
		assertEquals("<Hello>", success.readScope(StringScope.ANGLE_BRACKETS));
		assertEquals(' ', success.read());
		assertEquals("<World it is <nice here>>", success.readScope(StringScope.ANGLE_BRACKETS));
		
		ScopedStringReader failMissing = new ScopedStringReader("<Hello> <World it is <nice here>");
		assertEquals("<Hello>", failMissing.readScope(StringScope.ANGLE_BRACKETS));
		assertEquals(' ', failMissing.read());
		assertThrows(InvalidStringException.class, () -> failMissing.readScope(StringScope.ANGLE_BRACKETS));
		
		ScopedStringReader failExtra = new ScopedStringReader("<Hello> <World <it is <nice here>>");
		assertEquals("<Hello>", failExtra.readScope(StringScope.ANGLE_BRACKETS));
		assertEquals(' ', failExtra.read());
		assertThrows(InvalidStringException.class, () -> failExtra.readScope(StringScope.ANGLE_BRACKETS));
	}
	
	@Test
	void readUntil() {
		ScopedStringReader reader = new ScopedStringReader("this is a sImple \\test 'string for the' [string] {string = 10} \\reade\\r");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertEquals("", reader.readUntil('t'));
		assertEquals("his is a sImple tes", reader.readUntil('t'));
		reader.skip();
		assertEquals("'string for the'", reader.readUntil(' '));
		assertEquals("[string]", reader.readUntil(' '));
		assertEquals("{string = 10}", reader.readUntil(' '));
		assertEquals("reader", reader.readUntil('r'));
		assertEquals("", reader.readUntil(' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntil((char[]) null));
		assertThrows(IllegalArgumentException.class, reader::readUntil);
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertEquals("", reader.readUntil('t', 's'));
		assertEquals("hi", reader.readUntil('t', 's'));
		reader.skip();
		assertEquals("is a sImpl", reader.readUntil('e', 't'));
		reader.skip();
		assertEquals("te", reader.readUntil('s', ' '));
		reader.skip(2);
		assertEquals("'string for the'", reader.readUntil('s', ' '));
		assertEquals("[string]", reader.readUntil(' '));
		assertEquals("{string = 10}", reader.readUntil('=', ' '));
		assertEquals("reader", reader.readUntil('r'));
		assertEquals("", reader.readUntil(' ', '\0'));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntil((String) null, false));
		assertThrows(NullPointerException.class, () -> reader.readUntil((String) null, true));
		assertEquals("", reader.readUntil("t", false));
		assertEquals('h', reader.peek());
		reader.skip(4);
		assertEquals("is ", reader.readUntil("a", false));
		reader.skip();
		assertEquals("", reader.readUntil("sImple", true));
		reader.skip(7);
		assertEquals("'string for the'", reader.readUntil(" ", true));
		assertEquals("[string]", reader.readUntil(" ", false));
		assertEquals("{string = 10}", reader.readUntil(" ", false));
		assertEquals("reader", reader.readUntil("r", true));
	}
	
	@Test
	void readUntilInclusive() {
		ScopedStringReader reader = new ScopedStringReader("this is a sImple \\test 'string for the' [string] {string = 10} \\reade\\r");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive('\\'));
		assertEquals("t", reader.readUntilInclusive('t'));
		assertEquals("his is a sImple test", reader.readUntilInclusive('t'));
		reader.skip();
		assertEquals("'string for the' ", reader.readUntilInclusive(' '));
		assertEquals("[string] ", reader.readUntilInclusive(' '));
		assertEquals("{string = 10} ", reader.readUntilInclusive(' '));
		assertEquals("reader", reader.readUntilInclusive('r'));
		assertEquals("", reader.readUntilInclusive(' '));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((char[]) null));
		assertThrows(IllegalArgumentException.class, reader::readUntilInclusive);
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive('\\'));
		assertEquals("t", reader.readUntilInclusive('t', 's'));
		assertEquals("his", reader.readUntilInclusive('t', 's'));
		reader.skip();
		assertEquals("is a sImple", reader.readUntilInclusive('e', 't'));
		reader.skip();
		assertEquals("tes", reader.readUntilInclusive('s', ' '));
		reader.skip(2);
		assertEquals("'string for the' ", reader.readUntilInclusive('s', ' '));
		assertEquals("[string] ", reader.readUntilInclusive(' '));
		assertEquals("{string = 10} ", reader.readUntilInclusive('=', ' '));
		assertEquals("reader", reader.readUntilInclusive('r'));
		assertEquals("", reader.readUntilInclusive(' ', '\0'));
		
		reader.reset();
		
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((String) null, false));
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((String) null, true));
		assertEquals("t", reader.readUntilInclusive("t", false));
		assertEquals('h', reader.peek());
		reader.skip(4);
		assertEquals("is a", reader.readUntilInclusive("a", false));
		reader.skip();
		assertEquals("sImple", reader.readUntilInclusive("sImple", true));
		reader.skip(7);
		assertEquals("'string for the' ", reader.readUntilInclusive(" ", true));
		assertEquals("[string] ", reader.readUntilInclusive(" ", false));
		assertEquals("{string = 10} ", reader.readUntilInclusive(" ", false));
		assertEquals("reader", reader.readUntilInclusive("r", true));
	}
	
	@Test
	void readList() {
		ScopedStringReader reader = new ScopedStringReader("[] [ true , false ] [  10.0  ,  15.5  ,  20.6  ,  25.89  ] [10,15,20,25] [\"Hello\", \"World\"] [[\"str1\"], [\"str1\", \"str2\"]]");
		assertIterableEquals(List.of(), reader.readList(StringReader::readBoolean));
		assertEquals(' ', reader.read());
		assertIterableEquals(List.of(true, false), reader.readList(StringReader::readBoolean));
		assertEquals(' ', reader.read());
		assertIterableEquals(List.of(10.0, 15.5, 20.6, 25.89), reader.readList(StringReader::readDouble));
		assertEquals(' ', reader.read());
		assertIterableEquals(List.of(10, 15, 20, 25), reader.readList(StringReader::readInt));
		assertEquals(' ', reader.read());
		assertIterableEquals(List.of("Hello", "World"), reader.readList(StringReader::readString));
		assertEquals(' ', reader.read());
		assertIterableEquals(List.of(List.of("str1"), List.of("str1", "str2")), reader.readList(r -> r.readList(StringReader::readString)));
	}
	
	@Test
	void readSet() {
		ScopedStringReader reader = new ScopedStringReader("() ( true , false ) (  10.0  ,  15.5  ,  20.6  ,  25.89  ) (10,15,20,25) (\"Hello\", \"World\") ([\"str1\"], [\"str1\", \"str2\"])");
		assertEquals(Set.of(), reader.readSet(StringReader::readBoolean));
		assertEquals(' ', reader.read());
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
		ScopedStringReader reader = new ScopedStringReader("{} {key1= value1, key2= value2} {key1=10, key2=20} {0=\"Hello\", 1=\"World\"} { 0.0 = [ \"str1\" ], 1.0 = [ \"str1\" , \"str2\" ] }");
		assertEquals(Map.of(), reader.readMap(StringReader::readString, StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Map.of("key1", "value1", "key2", "value2"), reader.readMap(StringReader::readString, StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Map.of("key1", 10, "key2", 20), reader.readMap(StringReader::readString, StringReader::readInt));
		assertEquals(' ', reader.read());
		assertEquals(Map.of(0, "Hello", 1, "World"), reader.readMap(StringReader::readInt, StringReader::readString));
		assertEquals(' ', reader.read());
		assertEquals(Map.of(0.0, List.of("str1"), 1.0, List.of("str1", "str2")), reader.readMap(StringReader::readDouble, r -> r.readList(StringReader::readString)));
	}
}
