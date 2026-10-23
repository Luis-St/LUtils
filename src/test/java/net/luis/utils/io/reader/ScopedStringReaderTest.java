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

import static net.luis.utils.io.reader.StringScope.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ScopedStringReader}.<br>
 *
 * @author Luis-St
 */
class ScopedStringReaderTest {
	
	private static final Map<Character, StringScope> TRACKED_SCOPES = Map.of('(', PARENTHESES, '[', SQUARE_BRACKETS);
	
	@Test
	void constructWithString() {
		ScopedStringReader reader = new ScopedStringReader("test");
		assertEquals(ScopedStringReader.DEFAULT_SCOPES, reader.getScopes());
		assertEquals(StringQuoting.DEFAULT, reader.getQuoting());
		assertEquals('\\', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithQuoting() {
		ScopedStringReader reader = new ScopedStringReader("test", null, StringQuoting.NONE);
		assertNull(reader.getEscapeCharacter());
		assertEquals(StringQuoting.NONE, reader.getQuoting());
		assertEquals(ScopedStringReader.DEFAULT_SCOPES, reader.getScopes());
	}
	
	@Test
	void constructWithScopes() {
		ScopedStringReader reader = new ScopedStringReader("test", '\\', StringQuoting.DEFAULT, Set.of(SQUARE_BRACKETS));
		assertEquals(Set.of(SQUARE_BRACKETS), reader.getScopes());
	}
	
	@Test
	void constructWithReader() {
		assertEquals("content", new ScopedStringReader(new java.io.StringReader("content")).getString());
		
		ScopedStringReader quoted = new ScopedStringReader(new java.io.StringReader("content"), null, StringQuoting.NONE);
		assertEquals(StringQuoting.NONE, quoted.getQuoting());
		
		ScopedStringReader scoped = new ScopedStringReader(new java.io.StringReader("content"), '\\', StringQuoting.DEFAULT, Set.of(PARENTHESES));
		assertEquals(Set.of(PARENTHESES), scoped.getScopes());
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((String) null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader("test", '\\', null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader("test", '\\', StringQuoting.DEFAULT, null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader((Reader) null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader(new java.io.StringReader("test"), '\\', null));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader(new java.io.StringReader("test"), '\\', StringQuoting.DEFAULT, null));
	}
	
	@Test
	void constructWithNullScope() {
		Set<StringScope> scopes = new HashSet<>();
		scopes.add(null);
		assertThrows(NullPointerException.class, () -> new ScopedStringReader("test", '\\', StringQuoting.DEFAULT, scopes));
	}
	
	@Test
	void constructWithDuplicateOpeningCharacter() {
		Set<StringScope> scopes = Set.of(new StringScope('(', ')'), new StringScope('(', ']'));
		assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader("test", '\\', StringQuoting.DEFAULT, scopes));
	}
	
	@Test
	void readScopeWithNullScope() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader("(a)").readScope(null));
	}
	
	@Test
	void readScopeWithWrongFirstCharacter() {
		assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader("a(b)").readScope(PARENTHESES));
	}
	
	@Test
	void readScopeNotClosed() {
		InvalidStringException single = assertThrows(InvalidStringException.class, () -> new ScopedStringReader("(a").readScope(PARENTHESES));
		assertTrue(single.getMessage().contains("scope is not closed"));
		
		InvalidStringException multiple = assertThrows(InvalidStringException.class, () -> new ScopedStringReader("((a").readScope(PARENTHESES));
		assertTrue(multiple.getMessage().contains("scopes are not closed"));
	}
	
	@Test
	void readObjectAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new ScopedStringReader("").readObject());
	}
	
	@Test
	void readCollectionWithNullArguments() {
		ScopedStringReader reader = new ScopedStringReader("[1]");
		assertThrows(NullPointerException.class, () -> reader.readList(null));
		assertThrows(NullPointerException.class, () -> reader.readSet(null));
		assertThrows(NullPointerException.class, () -> reader.readList(null, StringReader::readRemaining));
		assertThrows(NullPointerException.class, () -> reader.readList(StringReader::new, null));
		assertThrows(NullPointerException.class, () -> reader.readSet(null, StringReader::readRemaining));
		assertThrows(NullPointerException.class, () -> reader.readSet(StringReader::new, null));
	}
	
	@Test
	void readCollectionAtEnd() {
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("").readList(ScopedStringReader::readObject));
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("").readSet(ScopedStringReader::readObject));
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("").readMap(ScopedStringReader::readObject, ScopedStringReader::readObject));
	}
	
	@Test
	void readCollectionWithWrongOpener() {
		assertThrows(IllegalArgumentException.class, () -> new ScopedStringReader("(1)").readList(ScopedStringReader::readObject));
	}
	
	@Test
	void readCollectionWithUnclosedScope() {
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("[1, 2").readList(ScopedStringReader::readObject));
	}
	
	@Test
	void readCollectionWithUnclosedInnerScope() {
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("[(1, 2]").readList(StringReader::readRemaining));
	}
	
	@Test
	void readCollectionWithInvalidFactory() {
		ScopedStringReader reader = new ScopedStringReader("[1]");
		assertThrows(IllegalArgumentException.class, () -> reader.readList(string -> new StringReader("other"), StringReader::readRemaining));
	}
	
	@Test
	void readMapWithNullArguments() {
		ScopedStringReader reader = new ScopedStringReader("{a=1}");
		assertThrows(NullPointerException.class, () -> reader.readMap(null, StringReader::readRemaining));
		assertThrows(NullPointerException.class, () -> reader.readMap(StringReader::readRemaining, null));
		assertThrows(NullPointerException.class, () -> reader.readMap(null, StringReader::readRemaining, StringReader::readRemaining));
		assertThrows(NullPointerException.class, () -> reader.readMap(StringReader::new, null, StringReader::readRemaining));
		assertThrows(NullPointerException.class, () -> reader.readMap(StringReader::new, StringReader::readRemaining, null));
	}
	
	@Test
	void readMapEntryWithoutSeparator() {
		ScopedStringReader reader = new ScopedStringReader("{\n  a=1,\n  b\n}");
		InvalidStringException exception = assertThrows(InvalidStringException.class, () -> reader.readMap(StringReader::readRemaining, StringReader::readRemaining));
		assertTrue(exception.getMessage().contains("'b'"));
		assertTrue(exception.getMessage().endsWith("at line 3, column 3"));
	}
	
	@Test
	void constructScopeTrackerWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new ScopedStringReader.ScopeTracker(null, TRACKED_SCOPES));
		assertThrows(NullPointerException.class, () -> new ScopedStringReader.ScopeTracker(StringQuoting.DEFAULT, null));
	}
	
	@Test
	void createTrackerTracksScopes() {
		StringReader.NestingTracker tracker = new ScopedStringReader("").createTracker();
		assertInstanceOf(ScopedStringReader.ScopeTracker.class, tracker);
		tracker.accept('[');
		assertTrue(tracker.isNested());
	}
	
	@Test
	void createElementReaderUsesSameConfiguration() {
		ScopedStringReader reader = new ScopedStringReader("x", null, StringQuoting.SINGLE_ONLY, Set.of(PARENTHESES));
		ScopedStringReader element = reader.createElementReader("y");
		assertEquals("y", element.getString());
		assertNull(element.getEscapeCharacter());
		assertEquals(StringQuoting.SINGLE_ONLY, element.getQuoting());
		assertEquals(Set.of(PARENTHESES), element.getScopes());
	}
	
	@Test
	void readScopeAtEnd() {
		assertEquals("", new ScopedStringReader("").readScope(PARENTHESES));
	}
	
	@Test
	void readScopeSimple() {
		ScopedStringReader reader = new ScopedStringReader("(a) b");
		assertEquals("(a)", reader.readScope(PARENTHESES));
		assertEquals(3, reader.getIndex());
	}
	
	@Test
	void readScopeNested() {
		ScopedStringReader reader = new ScopedStringReader("(a(b)c) d");
		assertEquals("(a(b)c)", reader.readScope(PARENTHESES));
	}
	
	@Test
	void readScopeWithQuotedScopeCharacters() {
		ScopedStringReader reader = new ScopedStringReader("(a')'b) c");
		assertEquals("(a')'b)", reader.readScope(PARENTHESES));
	}
	
	@Test
	void readScopeWithEscapedScopeCharacters() {
		ScopedStringReader reader = new ScopedStringReader("(a\\)b) c");
		assertEquals("(a\\)b)", reader.readScope(PARENTHESES));
	}
	
	@Test
	void readScopeOfUnhandledScope() {
		ScopedStringReader reader = new ScopedStringReader("aBz", '\\', StringQuoting.DEFAULT, Set.of());
		assertEquals("aBz", reader.readScope(new StringScope('a', 'z')));
	}
	
	@Test
	void readObjectList() {
		assertEquals(List.of(1L, 2L), new ScopedStringReader("[1, 2]").readObject());
	}
	
	@Test
	void readObjectSet() {
		assertEquals(Set.of(1L, 2L), new ScopedStringReader("(1, 2)").readObject());
	}
	
	@Test
	void readObjectMap() {
		assertEquals(Map.of("a", 1L), new ScopedStringReader("{a=1}").readObject());
	}
	
	@Test
	void readObjectScalar() {
		assertEquals(true, new ScopedStringReader("true").readObject());
	}
	
	@Test
	void readListEmpty() {
		assertEquals(List.of(), new ScopedStringReader("[]").readList(ScopedStringReader::readObject));
		assertEquals(List.of(), new ScopedStringReader("[ ]").readList(ScopedStringReader::readObject));
	}
	
	@Test
	void readListSingleElement() {
		assertEquals(List.of(1), new ScopedStringReader("[1]").readList(ValueStringReader::readInt));
	}
	
	@Test
	void readListMultipleElements() {
		assertEquals(List.of(1, 2, 3), new ScopedStringReader("[1 ,2,  3 ]").readList(ValueStringReader::readInt));
	}
	
	@Test
	void readListSkipsEmptyElements() {
		assertEquals(List.of(1, 2), new ScopedStringReader("[1,,2,]").readList(ValueStringReader::readInt));
	}
	
	@Test
	void readListWithSeparatorInQuotes() {
		assertEquals(List.of("a,b", "c"), new ScopedStringReader("['a,b', c]").readList(QuotedStringReader::readString));
	}
	
	@Test
	void readListWithSeparatorInScope() {
		List<String> list = new ScopedStringReader("[(1, 2), 3]").readList(StringReader::readRemaining);
		assertEquals(2, list.size());
		assertEquals(List.of("(1, 2)", "3"), list);
	}
	
	@Test
	void readListWithEscapedSeparator() {
		assertEquals(List.of("a\\,b"), new ScopedStringReader("[a\\,b]").readList(StringReader::readRemaining));
	}
	
	@Test
	void readListElementReaderIsLimited() {
		List<Integer> lengths = new ScopedStringReader("[ab, cde]").readList(reader -> reader.readRemaining().length());
		assertEquals(List.of(2, 3), lengths);
	}
	
	@Test
	void readListWithFactory() {
		ScopedStringReader reader = new ScopedStringReader("['a ${b}', \"c\"]");
		assertEquals(List.of("'a ${b}'", "\"c\""), reader.readList(StringReader::new, StringReader::readRemaining));
	}
	
	@Test
	void readListPositionAfterList() {
		ScopedStringReader reader = new ScopedStringReader("[ab, cd] rest");
		assertEquals(List.of('a', 'c'), reader.readList(StringReader::read));
		assertEquals(8, reader.getIndex());
		assertEquals(' ', reader.peek());
	}
	
	@Test
	void readListParserException() {
		ScopedStringReader reader = new ScopedStringReader("[1, x] rest");
		assertThrows(IllegalStateException.class, () -> reader.readList(element -> {
			throw new IllegalStateException("Failed to parse element");
		}));
		assertDoesNotThrow(() -> reader.canRead());
	}
	
	@Test
	void readSetRemovesDuplicates() {
		Set<Integer> set = new ScopedStringReader("(1, 2, 1)").readSet(ValueStringReader::readInt);
		assertEquals(List.of(1, 2), new ArrayList<>(set));
	}
	
	@Test
	void readSetWithFactory() {
		ScopedStringReader reader = new ScopedStringReader("(a, b)");
		assertEquals(Set.of("a", "b"), reader.readSet(StringReader::new, StringReader::readRemaining));
	}
	
	@Test
	void readMapEmpty() {
		assertEquals(Map.of(), new ScopedStringReader("{}").readMap(StringReader::readRemaining, StringReader::readRemaining));
	}
	
	@Test
	void readMapSplitsAtFirstSeparator() {
		ScopedStringReader reader = new ScopedStringReader("{a=b=c}");
		assertEquals(Map.of("a", "b=c"), reader.readMap(StringReader::readRemaining, StringReader::readRemaining));
	}
	
	@Test
	void readMapWithSeparatorInNestedParts() {
		ScopedStringReader quoted = new ScopedStringReader("{'a=b'=1}");
		assertEquals(Map.of("'a=b'", "1"), quoted.readMap(StringReader::readRemaining, StringReader::readRemaining));
		
		ScopedStringReader scoped = new ScopedStringReader("{k={x=1}}");
		assertEquals(Map.of("k", "{x=1}"), scoped.readMap(StringReader::readRemaining, StringReader::readRemaining));
	}
	
	@Test
	void readMapDuplicateKeys() {
		ScopedStringReader reader = new ScopedStringReader("{a=1, a=2}");
		assertEquals(Map.of("a", 2L), reader.readMap(StringReader::readRemaining, ValueStringReader::readNumber));
	}
	
	@Test
	void readMapWithFactory() {
		ScopedStringReader reader = new ScopedStringReader("{a=1}");
		assertEquals(Map.of("a", "1"), reader.readMap(StringReader::new, StringReader::readRemaining, StringReader::readRemaining));
	}
	
	@Test
	void readUntilIgnoresTerminatorInScope() {
		ScopedStringReader reader = new ScopedStringReader("(a,b),c");
		assertEquals("(a,b)", reader.readUntil(','));
		assertEquals(',', reader.peek());
	}
	
	@Test
	void readUntilWithUnclosedScope() {
		assertThrows(InvalidStringException.class, () -> new ScopedStringReader("(a,b").readUntil(','));
	}
	
	@Test
	void readUntilIgnoresScopeInQuotes() {
		assertEquals("'('", new ScopedStringReader("'(',b").readUntil(','));
	}
	
	@Test
	void scopeTrackerBranches() {
		ScopedStringReader.ScopeTracker tracker = new ScopedStringReader.ScopeTracker(StringQuoting.DEFAULT, TRACKED_SCOPES);
		tracker.accept('(');
		assertTrue(tracker.isNested());
		tracker.accept(']');
		assertTrue(tracker.isNested());
		tracker.accept(')');
		assertFalse(tracker.isNested());
		tracker.accept('a');
		assertFalse(tracker.isNested());
		
		tracker.accept('"');
		tracker.accept('(');
		tracker.accept('"');
		assertFalse(tracker.isNested());
	}
	
	@Test
	void scopeTrackerFinishMessage() {
		ScopedStringReader.ScopeTracker single = new ScopedStringReader.ScopeTracker(StringQuoting.DEFAULT, TRACKED_SCOPES);
		single.accept('(');
		InvalidStringException singleException = assertThrows(InvalidStringException.class, single::finish);
		assertTrue(singleException.getMessage().contains("1 scope is not closed"));
		assertTrue(singleException.getMessage().contains("')'"));
		
		ScopedStringReader.ScopeTracker multiple = new ScopedStringReader.ScopeTracker(StringQuoting.DEFAULT, TRACKED_SCOPES);
		multiple.accept('(');
		multiple.accept('[');
		InvalidStringException multipleException = assertThrows(InvalidStringException.class, multiple::finish);
		assertTrue(multipleException.getMessage().contains("2 scopes are not closed"));
		assertTrue(multipleException.getMessage().contains("']', ')'"));
	}
	
	@Test
	void scopeTrackerFinishWithoutOpenScope() {
		ScopedStringReader.ScopeTracker tracker = new ScopedStringReader.ScopeTracker(StringQuoting.DEFAULT, TRACKED_SCOPES);
		assertDoesNotThrow(tracker::finish);
	}
	
	@Test
	void equalReadersWithSameScopes() {
		ScopedStringReader first = new ScopedStringReader("abc");
		ScopedStringReader second = new ScopedStringReader("abc");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void unequalReadersWithOtherScopes() {
		ScopedStringReader reader = new ScopedStringReader("abc");
		assertNotEquals(reader, new ScopedStringReader("abc", '\\', StringQuoting.DEFAULT, Set.of(PARENTHESES)));
	}
	
	@Test
	void readListOfStrings() {
		ScopedStringReader reader = new ScopedStringReader("[a, 'b c', \"d\"]");
		assertEquals(List.of("a", "b c", "d"), reader.readList(QuotedStringReader::readString));
	}
	
	@Test
	void readMapOfStrings() {
		ScopedStringReader reader = new ScopedStringReader("{ a = 1 , b=2 }");
		assertEquals(Map.of("a", "1", "b", "2"), reader.readMap(StringReader::readRemaining, StringReader::readRemaining));
	}
	
	@Test
	void readObjectDeeplyNested() {
		ScopedStringReader reader = new ScopedStringReader("[1, \"a,b\", [2, 3], {k=v}, (true), 0x1F]");
		List<Object> expected = List.of(1L, "a,b", List.of(2L, 3L), Map.of("k", "v"), Set.of(true), 31L);
		assertEquals(expected, reader.readObject());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readListErrorPositionInOriginalString() {
		ScopedStringReader reader = new ScopedStringReader("[1,\n 2,\n x3y]");
		InvalidStringException exception = assertThrows(InvalidStringException.class, () -> reader.readList(ValueStringReader::readInt));
		assertTrue(exception.getMessage().endsWith("at line 3, column 2"));
	}
	
	@Test
	void readMapWithoutQuoting() {
		ScopedStringReader reader = new ScopedStringReader("{a=1, b='x'}", null, StringQuoting.NONE);
		assertEquals(Map.of("a", "1", "b", "'x'"), reader.readMap(StringReader::readRemaining, StringReader::readRemaining));
	}
}
