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
	void constructorWithNullString() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((String) null));
	}
	
	@Test
	void constructorWithNullReader() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((Reader) null));
	}
	
	@Test
	void constructorWithValidInputs() {
		assertDoesNotThrow(() -> new ScopedStringReader("test"));
		assertDoesNotThrow(() -> new ScopedStringReader(new java.io.StringReader("test")));
	}
	
	@Test
	void readScopeWithNullScope() {
		ScopedStringReader reader = new ScopedStringReader("(test)");
		
		assertThrows(NullPointerException.class, () -> reader.readScope(null));
	}
	
	@Test
	void readScopeWithWrongOpeningCharacter() {
		ScopedStringReader reader = new ScopedStringReader("test");
		
		assertThrows(IllegalArgumentException.class, () -> reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeWithEmptyString() {
		ScopedStringReader reader = new ScopedStringReader("");
		
		assertEquals("", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeAfterReadRemaining() {
		ScopedStringReader reader = new ScopedStringReader(" (test)");
		reader.readRemaining();
		
		assertEquals("", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParenthesesSimple() {
		ScopedStringReader reader = new ScopedStringReader("(Hello)");
		
		assertEquals("(Hello)", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParenthesesNested() {
		ScopedStringReader reader = new ScopedStringReader("(World it is (nice here))");
		
		assertEquals("(World it is (nice here))", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParenthesesMultiple() {
		ScopedStringReader reader = new ScopedStringReader("(Hello) (World)");
		
		assertEquals("(Hello)", reader.readScope(StringScope.PARENTHESES));
		assertEquals(' ', reader.read());
		assertEquals("(World)", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParenthesesMissingClosing() {
		ScopedStringReader reader = new ScopedStringReader("(Hello (World)");
		
		assertThrows(InvalidStringException.class, () -> reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeParenthesesExtraClosing() {
		ScopedStringReader reader = new ScopedStringReader("(Hello) World)");
		
		assertEquals("(Hello)", reader.readScope(StringScope.PARENTHESES));
		assertEquals(' ', reader.read());
		assertThrows(IllegalArgumentException.class, () -> reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeCurlyBracketsSimple() {
		ScopedStringReader reader = new ScopedStringReader("{Hello}");
		
		assertEquals("{Hello}", reader.readScope(StringScope.CURLY_BRACKETS));
	}
	
	@Test
	void readScopeCurlyBracketsNested() {
		ScopedStringReader reader = new ScopedStringReader("{World it is {nice here}}");
		
		assertEquals("{World it is {nice here}}", reader.readScope(StringScope.CURLY_BRACKETS));
	}
	
	@Test
	void readScopeCurlyBracketsMissingClosing() {
		ScopedStringReader reader = new ScopedStringReader("{Hello {World}");
		
		assertThrows(InvalidStringException.class, () -> reader.readScope(StringScope.CURLY_BRACKETS));
	}
	
	@Test
	void readScopeSquareBracketsSimple() {
		ScopedStringReader reader = new ScopedStringReader("[Hello]");
		
		assertEquals("[Hello]", reader.readScope(StringScope.SQUARE_BRACKETS));
	}
	
	@Test
	void readScopeSquareBracketsNested() {
		ScopedStringReader reader = new ScopedStringReader("[World it is [nice here]]");
		
		assertEquals("[World it is [nice here]]", reader.readScope(StringScope.SQUARE_BRACKETS));
	}
	
	@Test
	void readScopeAngleBracketsSimple() {
		ScopedStringReader reader = new ScopedStringReader("<Hello>");
		
		assertEquals("<Hello>", reader.readScope(StringScope.ANGLE_BRACKETS));
	}
	
	@Test
	void readScopeAngleBracketsNested() {
		ScopedStringReader reader = new ScopedStringReader("<World it is <nice here>>");
		
		assertEquals("<World it is <nice here>>", reader.readScope(StringScope.ANGLE_BRACKETS));
	}
	
	@Test
	void readScopeWithQuotedContent() {
		ScopedStringReader reader = new ScopedStringReader("('string with ) inside')");
		
		assertEquals("('string with ) inside')", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readScopeWithEscapedCharacters() {
		ScopedStringReader reader = new ScopedStringReader("(escaped \\) character)");
		
		assertEquals("(escaped \\) character)", reader.readScope(StringScope.PARENTHESES));
	}
	
	@Test
	void readUntilInheritedMethods() {
		ScopedStringReader reader = new ScopedStringReader("this is a 'test string' (with scope) {and more}");
		
		assertEquals("this is a ", reader.readUntil('\''));
		assertEquals("test string", reader.readUntil('\''));
		reader.skip();
		assertEquals("(with scope)", reader.readUntil(' '));
		assertEquals("{and more}", reader.readUntil(' '));
	}
	
	@Test
	void readUntilWithScopeAwareness() {
		ScopedStringReader reader = new ScopedStringReader("before (scope with , comma) after");
		
		assertEquals("before (scope with , comma) after", reader.readUntil(','));
	}
	
	@Test
	void readUntilInclusiveWithScopeAwareness() {
		ScopedStringReader reader = new ScopedStringReader("before (scope) , after");
		
		assertEquals("before (scope) ,", reader.readUntilInclusive(','));
		assertEquals(" after", reader.readRemaining());
	}
	
	@Test
	void readUntilStringWithScopeAwareness() {
		ScopedStringReader reader = new ScopedStringReader("text (with test inside) test here");
		
		assertEquals("text (with test inside) ", reader.readUntil("test", true));
		assertEquals(" here", reader.readRemaining());
	}
	
	@Test
	void readListEmpty() {
		ScopedStringReader reader = new ScopedStringReader("[]");
		
		List<Boolean> result = reader.readList(StringReader::readBoolean);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void readListWithBooleans() {
		ScopedStringReader reader = new ScopedStringReader("[ true , false ]");
		
		List<Boolean> result = reader.readList(StringReader::readBoolean);
		assertEquals(List.of(true, false), result);
	}
	
	@Test
	void readListWithDoubles() {
		ScopedStringReader reader = new ScopedStringReader("[  10.0  ,  15.5  ,  20.6  ]");
		
		List<Double> result = reader.readList(StringReader::readDouble);
		assertEquals(List.of(10.0, 15.5, 20.6), result);
	}
	
	@Test
	void readListWithIntegers() {
		ScopedStringReader reader = new ScopedStringReader("[10,15,20,25]");
		
		List<Integer> result = reader.readList(StringReader::readInt);
		assertEquals(List.of(10, 15, 20, 25), result);
	}
	
	@Test
	void readListWithStrings() {
		ScopedStringReader reader = new ScopedStringReader("[\"Hello\", \"World\"]");
		
		List<String> result = reader.readList(StringReader::readString);
		assertEquals(List.of("Hello", "World"), result);
	}
	
	@Test
	void readListNested() {
		ScopedStringReader reader = new ScopedStringReader("[[\"str1\"], [\"str1\", \"str2\"]]");
		
		List<List<String>> result = reader.readList(r -> r.readList(StringReader::readString));
		assertEquals(List.of(List.of("str1"), List.of("str1", "str2")), result);
	}
	
	@Test
	void readListWithEmptyOnNoContent() {
		ScopedStringReader reader = new ScopedStringReader("");
		
		List<String> result = reader.readList(StringReader::readString);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void readListWithNullParser() {
		ScopedStringReader reader = new ScopedStringReader("[1,2,3]");
		
		assertThrows(NullPointerException.class, () -> reader.readList(null));
	}
	
	@Test
	void readSetEmpty() {
		ScopedStringReader reader = new ScopedStringReader("()");
		
		Set<Boolean> result = reader.readSet(StringReader::readBoolean);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void readSetWithBooleans() {
		ScopedStringReader reader = new ScopedStringReader("( true , false )");
		
		Set<Boolean> result = reader.readSet(StringReader::readBoolean);
		assertEquals(Set.of(true, false), result);
	}
	
	@Test
	void readSetWithDuplicates() {
		ScopedStringReader reader = new ScopedStringReader("(10, 20, 10, 30)");
		
		Set<Integer> result = reader.readSet(StringReader::readInt);
		assertEquals(Set.of(10, 20, 30), result);
	}
	
	@Test
	void readSetWithNullParser() {
		ScopedStringReader reader = new ScopedStringReader("(1,2,3)");
		
		assertThrows(NullPointerException.class, () -> reader.readSet(null));
	}
	
	@Test
	void readMapEmpty() {
		ScopedStringReader reader = new ScopedStringReader("{}");
		
		Map<String, String> result = reader.readMap(StringReader::readString, StringReader::readString);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void readMapWithStringValues() {
		ScopedStringReader reader = new ScopedStringReader("{key1= value1, key2= value2}");
		
		Map<String, String> result = reader.readMap(StringReader::readString, StringReader::readString);
		assertEquals(Map.of("key1", "value1", "key2", "value2"), result);
	}
	
	@Test
	void readMapWithIntegerValues() {
		ScopedStringReader reader = new ScopedStringReader("{key1=10, key2=20}");
		
		Map<String, Integer> result = reader.readMap(StringReader::readString, StringReader::readInt);
		assertEquals(Map.of("key1", 10, "key2", 20), result);
	}
	
	@Test
	void readMapWithIntegerKeys() {
		ScopedStringReader reader = new ScopedStringReader("{0=\"Hello\", 1=\"World\"}");
		
		Map<Integer, String> result = reader.readMap(StringReader::readInt, StringReader::readString);
		assertEquals(Map.of(0, "Hello", 1, "World"), result);
	}
	
	@Test
	void readMapWithComplexValues() {
		ScopedStringReader reader = new ScopedStringReader("{ 0.0 = [ \"str1\" ], 1.0 = [ \"str1\" , \"str2\" ] }");
		
		Map<Double, List<String>> result = reader.readMap(StringReader::readDouble, r -> r.readList(StringReader::readString));
		assertEquals(Map.of(0.0, List.of("str1"), 1.0, List.of("str1", "str2")), result);
	}
	
	@Test
	void readMapWithDuplicateKeys() {
		ScopedStringReader reader = new ScopedStringReader("{key=value1, key=value2}");
		
		Map<String, String> result = reader.readMap(StringReader::readString, StringReader::readString);
		assertEquals(Map.of("key", "value2"), result);
	}
	
	@Test
	void readMapWithInvalidEntry() {
		ScopedStringReader reader = new ScopedStringReader("{key_without_value, key2=value2}");
		
		assertThrows(InvalidStringException.class, () -> reader.readMap(StringReader::readString, StringReader::readString));
	}
	
	@Test
	void readMapWithNullParsers() {
		ScopedStringReader reader = new ScopedStringReader("{key=value}");
		
		assertThrows(NullPointerException.class, () -> reader.readMap(null, StringReader::readString));
		assertThrows(NullPointerException.class, () -> reader.readMap(StringReader::readString, null));
	}
	
	@Test
	void readMapWithEmptyOnNoContent() {
		ScopedStringReader reader = new ScopedStringReader("");
		
		Map<String, String> result = reader.readMap(StringReader::readString, StringReader::readString);
		assertTrue(result.isEmpty());
	}
}
