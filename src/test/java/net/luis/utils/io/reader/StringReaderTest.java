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

import net.luis.utils.exception.ConcurrentInteractionException;
import net.luis.utils.exception.InvalidStringException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringReader}.<br>
 *
 * @author Luis-St
 */
class StringReaderTest {
	
	@Test
	void constructWithString() {
		StringReader reader = new StringReader("test");
		assertEquals("test", reader.getString());
		assertEquals(0, reader.getIndex());
		assertEquals(4, reader.getEndIndex());
		assertEquals('\\', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithStringAndEscapeCharacter() {
		StringReader reader = new StringReader("test", '#');
		assertEquals('#', reader.getEscapeCharacter());
		assertTrue(reader.isEscapeCharacter('#'));
	}
	
	@Test
	void constructWithStringAndNullEscapeCharacter() {
		StringReader reader = new StringReader("test", null);
		assertNull(reader.getEscapeCharacter());
		assertFalse(reader.isEscapeCharacter('\\'));
	}
	
	@Test
	void constructWithNullString() {
		assertThrows(NullPointerException.class, () -> new StringReader((String) null));
		assertThrows(NullPointerException.class, () -> new StringReader((String) null, '\\'));
	}
	
	@Test
	void constructWithReader() {
		StringReader reader = new StringReader(new java.io.StringReader("content"));
		assertEquals("content", reader.getString());
		assertEquals('\\', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithReaderAndEscapeCharacter() {
		StringReader reader = new StringReader(new java.io.StringReader("content"), '#');
		assertEquals("content", reader.getString());
		assertEquals('#', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithNullReader() {
		assertThrows(NullPointerException.class, () -> new StringReader((Reader) null));
		assertThrows(NullPointerException.class, () -> new StringReader((Reader) null, '\\'));
	}
	
	@Test
	void constructWithFailingReader() {
		assertThrows(UncheckedIOException.class, () -> new StringReader(new FailingReader()));
		assertThrows(UncheckedIOException.class, () -> new StringReader(new FailingReader(), '\\'));
	}
	
	@Test
	void createSubReaderWithNullCreator() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.createSubReader(null));
		assertThrows(NullPointerException.class, () -> reader.createSubReader(1, null));
	}
	
	@Test
	void createSubReaderWithNegativeLength() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(-1, StringReader::new));
	}
	
	@Test
	void createSubReaderWithLengthAboveRemaining() {
		StringReader reader = new StringReader("abc");
		reader.skip();
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(3, StringReader::new));
		assertDoesNotThrow(() -> reader.createSubReader(2, StringReader::new));
	}
	
	@Test
	void createSubReaderWithCreatorReturningNull() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.createSubReader(string -> null));
	}
	
	@Test
	void createSubReaderWithCreatorReturningSelf() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(string -> reader));
	}
	
	@Test
	void createSubReaderWithDifferentString() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(string -> new StringReader("other")));
	}
	
	@Test
	void createSubReaderWithLinkedReader() {
		StringReader reader = new StringReader("abc");
		
		StringReader subReader = new StringReader("abc").createSubReader(StringReader::new);
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(string -> subReader));
		
		StringReader parentWithChild = new StringReader("abc");
		parentWithChild.createSubReader(StringReader::new);
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(string -> parentWithChild));
		
		StringReader closedSubReader = new StringReader("abc").createSubReader(StringReader::new);
		closedSubReader.close();
		assertThrows(IllegalArgumentException.class, () -> reader.createSubReader(string -> closedSubReader));
	}
	
	@Test
	void interactWithClosedSubReader() {
		StringReader subReader = new StringReader("abc").createSubReader(StringReader::new);
		subReader.close();
		assertThrows(IllegalStateException.class, subReader::read);
		assertThrows(IllegalStateException.class, subReader::peek);
		assertThrows(IllegalStateException.class, () -> subReader.skip());
	}
	
	@Test
	void interactWithParentOfActiveSubReader() {
		StringReader reader = new StringReader("abc");
		reader.createSubReader(StringReader::new);
		assertThrows(ConcurrentInteractionException.class, reader::read);
		assertThrows(ConcurrentInteractionException.class, () -> reader.canRead());
		assertThrows(ConcurrentInteractionException.class, reader::readRemaining);
	}
	
	@Test
	void withLocationWithNullMessage() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.withLocation(null));
	}
	
	@Test
	void setIndexOutsideReadablePart() {
		StringReader reader = new StringReader("abcdefgh");
		reader.skip(2);
		StringReader subReader = reader.createSubReader(3, StringReader::new);
		assertThrows(IndexOutOfBoundsException.class, () -> subReader.setIndex(1));
		assertThrows(IndexOutOfBoundsException.class, () -> subReader.setIndex(6));
	}
	
	@Test
	void readAtEnd() {
		StringReader reader = new StringReader("");
		assertThrows(StringIndexOutOfBoundsException.class, reader::read);
		assertThrows(StringIndexOutOfBoundsException.class, reader::peek);
	}
	
	@Test
	void readAmountWithInvalidAmount() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.read(0));
		assertThrows(IllegalArgumentException.class, () -> reader.read(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> reader.read(5));
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void canReadWithInvalidAmount() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(0));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(-1));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(0, c -> true));
		assertThrows(IllegalArgumentException.class, () -> reader.canRead(-1, c -> true));
	}
	
	@Test
	void canReadWithNullPredicate() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.canRead(1, null));
	}
	
	@Test
	void peekAmountWithInvalidAmount() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.peek(0));
		assertThrows(IllegalArgumentException.class, () -> reader.peek(-1));
	}
	
	@Test
	void skipAmountWithInvalidAmount() {
		StringReader reader = new StringReader("abc");
		assertThrows(IllegalArgumentException.class, () -> reader.skip(0));
		assertThrows(IllegalArgumentException.class, () -> reader.skip(-1));
	}
	
	@Test
	void skipWithNullPredicate() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.skip(null));
	}
	
	@Test
	void readUnquotedStringAtEnd() {
		StringReader reader = new StringReader("");
		StringIndexOutOfBoundsException exception = assertThrows(StringIndexOutOfBoundsException.class, () -> reader.readUnquotedString('x'));
		assertTrue(exception.getMessage().endsWith("at index 0"));
	}
	
	@Test
	void readUntilWithEscapeCharacterTerminator() {
		StringReader reader = new StringReader("a\\b");
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil('\\'));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive('\\'));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilSkip('\\'));
	}
	
	@Test
	void readUntilWithInvalidTerminators() {
		StringReader reader = new StringReader("a,b");
		assertThrows(NullPointerException.class, () -> reader.readUntil((char[]) null));
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((char[]) null));
		assertThrows(NullPointerException.class, () -> reader.readUntilSkip((char[]) null));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil(new char[0]));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive(new char[0]));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilSkip(new char[0]));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil(',', '\\'));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive(',', '\\'));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilSkip(',', '\\'));
	}
	
	@Test
	void readUntilWithNullPredicate() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.readUntil((Predicate<Character>) null));
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive((Predicate<Character>) null));
		assertThrows(NullPointerException.class, () -> reader.readUntilSkip((Predicate<Character>) null));
	}
	
	@Test
	void readUntilWithInvalidTerminatorString() {
		StringReader reader = new StringReader("abc");
		assertThrows(NullPointerException.class, () -> reader.readUntil((String) null, true));
		assertThrows(NullPointerException.class, () -> reader.readUntilInclusive(null, true));
		assertThrows(NullPointerException.class, () -> reader.readUntilSkip(null, true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil("", true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive("", true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilSkip("", true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntil("a\\b", true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilInclusive("a\\b", true));
		assertThrows(IllegalArgumentException.class, () -> reader.readUntilSkip("a\\b", true));
	}
	
	@Test
	void readExpectedWithInvalidArguments() {
		assertThrows(NullPointerException.class, () -> new StringReader("a").readExpected((String) null, true));
		assertThrows(StringIndexOutOfBoundsException.class, () -> new StringReader("").readExpected("a", true));
		assertThrows(IllegalArgumentException.class, () -> new StringReader("a").readExpected("", true));
	}
	
	@Test
	void readExpectedWithNotEnoughCharacters() {
		StringReader reader = new StringReader("ab");
		assertThrows(InvalidStringException.class, () -> reader.readExpected("abc", true));
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void readExpectedListWithInvalidArguments() {
		assertThrows(NullPointerException.class, () -> new StringReader("a").readExpected((List<String>) null, true));
		assertThrows(StringIndexOutOfBoundsException.class, () -> new StringReader("").readExpected(List.of("a"), true));
		assertThrows(IllegalArgumentException.class, () -> new StringReader("a").readExpected(List.of(), true));
	}
	
	@Test
	void readExpectedListWithoutFittingCandidate() {
		StringReader reader = new StringReader("ab");
		assertThrows(InvalidStringException.class, () -> reader.readExpected(List.of("abc", "abcd"), true));
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void closeSubReaderWithActiveChild() {
		StringReader subReader = new StringReader("abc").createSubReader(StringReader::new);
		subReader.createSubReader(StringReader::new);
		assertThrows(ConcurrentInteractionException.class, subReader::close);
	}
	
	@Test
	void createSubReaderContinuesAtParentIndex() {
		StringReader reader = new StringReader("hello world");
		reader.skip(6);
		
		StringReader subReader = reader.createSubReader(5, StringReader::new);
		assertEquals(6, subReader.getIndex());
		assertEquals(11, subReader.getEndIndex());
		assertEquals("world", subReader.readRemaining());
	}
	
	@Test
	void createSubReaderWithoutLength() {
		StringReader reader = new StringReader("abc");
		reader.skip();
		
		StringReader subReader = reader.createSubReader(StringReader::new);
		assertEquals(1, subReader.getIndex());
		assertEquals(reader.getEndIndex(), subReader.getEndIndex());
	}
	
	@Test
	void createSubReaderWithZeroLength() {
		StringReader reader = new StringReader("abc");
		StringReader subReader = reader.createSubReader(0, StringReader::new);
		assertFalse(subReader.canRead());
		assertEquals("", subReader.readRemaining());
	}
	
	@Test
	void createSubReaderOfOtherReaderType() {
		StringReader reader = new StringReader("'a' b");
		QuotedStringReader subReader = reader.createSubReader(string -> new QuotedStringReader(string, null, StringQuoting.NONE));
		assertInstanceOf(QuotedStringReader.class, subReader);
		assertEquals(StringQuoting.NONE, subReader.getQuoting());
		assertNull(subReader.getEscapeCharacter());
	}
	
	@Test
	void isEscapeCharacterBranches() {
		assertFalse(new StringReader("", null).isEscapeCharacter('\\'));
		assertTrue(new StringReader("").isEscapeCharacter('\\'));
		assertFalse(new StringReader("").isEscapeCharacter('a'));
	}
	
	@Test
	void createTrackerIsNeverNested() {
		StringReader.NestingTracker tracker = new StringReader("").createTracker();
		assertFalse(tracker.isNested());
		tracker.accept('"');
		assertFalse(tracker.isNested());
		assertDoesNotThrow(tracker::finish);
	}
	
	@Test
	void describeLocationContainsIndex() {
		StringReader reader = new StringReader("abcdef");
		reader.skip(3);
		assertEquals("index 3", reader.describeLocation());
		assertEquals("message at index 3", reader.withLocation("message"));
	}
	
	@Test
	void setIndexAtBoundaries() {
		StringReader reader = new StringReader("abc");
		reader.setIndex(3);
		assertEquals(3, reader.getIndex());
		reader.setIndex(0);
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void remainingAfterOvershoot() {
		StringReader reader = new StringReader("abc");
		reader.skip(5);
		assertEquals(5, reader.getIndex());
		assertEquals(0, reader.remaining());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readStopsAtSubReaderEnd() {
		StringReader reader = new StringReader("abcd");
		StringReader subReader = reader.createSubReader(2, StringReader::new);
		assertEquals('a', subReader.read());
		assertEquals('b', subReader.read());
		assertThrows(StringIndexOutOfBoundsException.class, subReader::read);
	}
	
	@Test
	void canReadAmountBranches() {
		StringReader reader = new StringReader("abc");
		assertTrue(reader.canRead(3));
		assertFalse(reader.canRead(4));
		assertTrue(reader.canRead());
	}
	
	@Test
	void canReadWithPredicateBranches() {
		StringReader reader = new StringReader("a1");
		assertFalse(reader.canRead(3, Character::isLetter));
		assertFalse(reader.canRead(2, Character::isLetter));
		assertTrue(reader.canRead(1, Character::isLetter));
	}
	
	@Test
	void peekAmountAboveRemaining() {
		StringReader reader = new StringReader("abc");
		assertEquals("abc", reader.peek(10));
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void skipCharacterBranches() {
		StringReader repeated = new StringReader("aab");
		repeated.skip('a');
		assertEquals(2, repeated.getIndex());
		
		StringReader other = new StringReader("b");
		other.skip('a');
		assertEquals(0, other.getIndex());
		
		StringReader onlyMatching = new StringReader("aaa");
		onlyMatching.skip('a');
		assertEquals(3, onlyMatching.getIndex());
	}
	
	@Test
	void skipPredicateBranches() {
		StringReader digits = new StringReader("12ab");
		digits.skip(Character::isDigit);
		assertEquals(2, digits.getIndex());
		
		StringReader letters = new StringReader("ab12");
		letters.skip(Character::isDigit);
		assertEquals(0, letters.getIndex());
	}
	
	@Test
	void resetWithoutMark() {
		StringReader reader = new StringReader("abc");
		reader.skip(2);
		reader.reset();
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void resetWithMark() {
		StringReader reader = new StringReader("abc");
		reader.skip();
		reader.mark();
		reader.skip();
		reader.reset();
		assertEquals(1, reader.getIndex());
		reader.reset();
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void resetSubReaderToStartIndex() {
		StringReader reader = new StringReader("abcdef");
		reader.skip(2);
		StringReader subReader = reader.createSubReader(StringReader::new);
		subReader.skip(2);
		subReader.reset();
		assertEquals(2, subReader.getIndex());
	}
	
	@Test
	void readLineWithoutCharacters() {
		assertEquals("", new StringReader("").readLine(true));
		assertEquals("", new StringReader("").readLine(false));
	}
	
	@Test
	void readLineWithLineFeed() {
		StringReader included = new StringReader("a\nb");
		assertEquals("a\n", included.readLine(true));
		assertEquals("b", included.readRemaining());
		
		StringReader excluded = new StringReader("a\nb");
		assertEquals("a", excluded.readLine(false));
		assertEquals('b', excluded.peek());
	}
	
	@Test
	void readLineWithCarriageReturn() {
		StringReader included = new StringReader("a\rb");
		assertEquals("a\r", included.readLine(true));
		assertEquals("b", included.readRemaining());
		
		StringReader atEnd = new StringReader("a\r");
		assertEquals("a", atEnd.readLine(false));
		assertFalse(atEnd.canRead());
	}
	
	@Test
	void readLineWithCarriageReturnLineFeed() {
		StringReader included = new StringReader("a\r\nb");
		assertEquals("a\r\n", included.readLine(true));
		assertEquals('b', included.peek());
		
		StringReader excluded = new StringReader("a\r\nb");
		assertEquals("a", excluded.readLine(false));
		assertEquals('b', excluded.peek());
	}
	
	@Test
	void readLineWithoutLineBreak() {
		StringReader reader = new StringReader("abc");
		assertEquals("abc", reader.readLine(true));
		assertFalse(reader.canRead());
	}
	
	@Test
	void readRemainingBranches() {
		assertEquals("", new StringReader("").readRemaining());
		
		StringReader reader = new StringReader("abcd");
		reader.skip(2);
		assertEquals("cd", reader.readRemaining());
		assertEquals(4, reader.getIndex());
	}
	
	@Test
	void readRemainingOfSubReader() {
		StringReader reader = new StringReader("abcdef");
		reader.skip();
		StringReader subReader = reader.createSubReader(3, StringReader::new);
		assertEquals("bcd", subReader.readRemaining());
		assertEquals(4, subReader.getIndex());
	}
	
	@Test
	void readUnquotedStringBranches() {
		StringReader reader = new StringReader("ab cd");
		assertEquals("ab", reader.readUnquotedString());
		assertEquals("cd", reader.readUnquotedString());
		
		StringReader custom = new StringReader("abxcd");
		assertEquals("ab", custom.readUnquotedString('x'));
		assertEquals('c', custom.peek());
	}
	
	@Test
	void readUnquotedStringIgnoresEscapes() {
		StringReader reader = new StringReader("a\\ b");
		assertEquals("a\\", reader.readUnquotedString());
		assertEquals("b", reader.readRemaining());
	}
	
	@Test
	void validateTerminatorsRemovesDuplicates() {
		StringReader reader = new StringReader("");
		assertEquals(Set.of('a', 'b'), reader.validateTerminators(new char[] { 'a', 'b', 'a' }));
	}
	
	@Test
	void validateTerminatorWithEscapingDisabled() {
		StringReader reader = new StringReader("ab\\cd", null);
		assertEquals("ab", reader.readUntil('\\'));
		assertDoesNotThrow(() -> reader.validateTerminator("a\\b"));
	}
	
	@Test
	void readUntilCharacterBranches() {
		StringReader atStart = new StringReader(",a");
		assertEquals("", atStart.readUntil(','));
		assertEquals(0, atStart.getIndex());
		
		StringReader inMiddle = new StringReader("ab,c");
		assertEquals("ab", inMiddle.readUntil(','));
		assertEquals(',', inMiddle.peek());
		
		assertEquals("abc", new StringReader("abc").readUntil(','));
	}
	
	@Test
	void readUntilInclusiveCharacter() {
		StringReader reader = new StringReader("ab,c");
		assertEquals("ab,", reader.readUntilInclusive(','));
		assertEquals('c', reader.peek());
	}
	
	@Test
	void readUntilSkipCharacterBranches() {
		StringReader found = new StringReader("ab,c");
		assertEquals("ab", found.readUntilSkip(','));
		assertEquals('c', found.peek());
		
		StringReader missing = new StringReader("abc");
		assertEquals("abc", missing.readUntilSkip(','));
		assertEquals(3, missing.getIndex());
	}
	
	@Test
	void readUntilWithEscapedTerminator() {
		StringReader reader = new StringReader("a\\,b,c");
		assertEquals("a,b", reader.readUntil(','));
		assertEquals(',', reader.peek());
	}
	
	@Test
	void readUntilIgnoresQuotes() {
		StringReader reader = new StringReader("'a,b',c");
		assertEquals("'a", reader.readUntil(','));
	}
	
	@Test
	void readUntilMultipleTerminators() {
		assertEquals("ab", new StringReader("ab;c,d").readUntil(',', ';'));
		assertEquals("ab;", new StringReader("ab;c,d").readUntilInclusive(',', ';'));
		
		StringReader reader = new StringReader("ab;c,d");
		assertEquals("ab", reader.readUntilSkip(',', ';'));
		assertEquals('c', reader.peek());
	}
	
	@Test
	void readUntilPredicateVariants() {
		StringReader reader = new StringReader("ab1c");
		assertEquals("ab", reader.readUntil(Character::isDigit));
		assertEquals('1', reader.peek());
		
		assertEquals("ab1", new StringReader("ab1c").readUntilInclusive(Character::isDigit));
		
		StringReader skipping = new StringReader("ab1c");
		assertEquals("ab", skipping.readUntilSkip(Character::isDigit));
		assertEquals('c', skipping.peek());
	}
	
	@Test
	void readUntilStringCaseSensitive() {
		StringReader matching = new StringReader("a END b");
		assertEquals("a ", matching.readUntil("END", true));
		assertEquals('E', matching.peek());
		
		assertEquals("a end b", new StringReader("a end b").readUntil("END", true));
	}
	
	@Test
	void readUntilStringCaseInsensitive() {
		StringReader reader = new StringReader("a end b");
		assertEquals("a ", reader.readUntil("END", false));
		assertEquals('e', reader.peek());
	}
	
	@Test
	void readUntilStringInclusiveAndSkip() {
		StringReader inclusive = new StringReader("a END b");
		assertEquals("a END", inclusive.readUntilInclusive("END", true));
		assertEquals(' ', inclusive.peek());
		
		StringReader skipping = new StringReader("a END b");
		assertEquals("a ", skipping.readUntilSkip("END", true));
		assertEquals(5, skipping.getIndex());
		
		StringReader missing = new StringReader("abc");
		assertEquals("abc", missing.readUntilSkip("END", true));
		assertEquals(3, missing.getIndex());
	}
	
	@Test
	void readUntilStringWithEscapedTerminator() {
		StringReader reader = new StringReader("a\\abc");
		assertEquals("aabc", reader.readUntil("ab", true));
		assertFalse(reader.canRead());
	}
	
	@Test
	void readExpectedCaseSensitive() {
		StringReader matching = new StringReader("abcd");
		assertEquals("abc", matching.readExpected("abc", true));
		assertEquals('d', matching.peek());
		
		StringReader mismatching = new StringReader("abxd");
		assertThrows(InvalidStringException.class, () -> mismatching.readExpected("abc", true));
		assertEquals(2, mismatching.getIndex());
	}
	
	@Test
	void readExpectedCaseInsensitive() {
		StringReader reader = new StringReader("ABC");
		assertEquals("ABC", reader.readExpected("abc", false));
		assertThrows(InvalidStringException.class, () -> new StringReader("ABC").readExpected("abc", true));
	}
	
	@Test
	void readExpectedListFirstCompleteCandidate() {
		StringReader reader = new StringReader("abcd");
		assertEquals("ab", reader.readExpected(List.of("abc", "ab"), true));
		assertEquals(2, reader.getIndex());
	}
	
	@Test
	void readExpectedListWithoutMatch() {
		StringReader reader = new StringReader("abx");
		InvalidStringException exception = assertThrows(InvalidStringException.class, () -> reader.readExpected(List.of("abc", "abd"), true));
		assertTrue(exception.getMessage().contains("'ab'"));
		assertEquals(2, reader.getIndex());
	}
	
	@Test
	void readExpectedListCaseInsensitive() {
		StringReader reader = new StringReader("TRUE");
		assertEquals("TRUE", reader.readExpected(List.of("true", "false"), false));
		assertFalse(reader.canRead());
	}
	
	@Test
	void closeRootReader() {
		StringReader reader = new StringReader("abc");
		assertDoesNotThrow(reader::close);
		assertEquals('a', reader.read());
	}
	
	@Test
	void closeSubReaderTwice() {
		StringReader reader = new StringReader("abcd");
		StringReader subReader = reader.createSubReader(StringReader::new);
		subReader.skip();
		subReader.close();
		reader.skip();
		subReader.close();
		assertEquals(2, reader.getIndex());
	}
	
	@Test
	void closeTransfersIndexToParent() {
		StringReader reader = new StringReader("abcd");
		StringReader subReader = reader.createSubReader(StringReader::new);
		subReader.skip(3);
		subReader.close();
		assertEquals(3, reader.getIndex());
		assertEquals('d', reader.read());
	}
	
	@Test
	void readAmount() {
		StringReader reader = new StringReader("abc");
		assertEquals("ab", reader.read(2));
		assertEquals(2, reader.getIndex());
	}
	
	@Test
	void peekDoesNotMove() {
		StringReader reader = new StringReader("abc");
		assertEquals('a', reader.peek());
		assertEquals("ab", reader.peek(2));
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void skipWhitespacesSkipsAllKinds() {
		StringReader reader = new StringReader(" \t\n\r x");
		reader.skipWhitespaces();
		assertEquals('x', reader.peek());
	}
	
	@Test
	void equalReaders() {
		StringReader first = new StringReader("abc");
		StringReader second = new StringReader("abc");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void unequalReaders() {
		StringReader reader = new StringReader("abc");
		
		StringReader otherIndex = new StringReader("abc");
		otherIndex.skip();
		StringReader otherMark = new StringReader("abc");
		otherMark.mark();
		StringReader otherEnd = new StringReader("abc").createSubReader(2, StringReader::new);
		
		assertNotEquals(reader, otherIndex);
		assertNotEquals(reader, otherMark);
		assertNotEquals(reader, otherEnd);
		assertNotEquals(reader, new StringReader("abc", null));
		assertNotEquals(reader, new StringReader("abd"));
		assertNotEquals(reader, new PositionedStringReader("abc"));
		assertNotEquals(null, reader);
	}
	
	@Test
	void nestedSubReaders() {
		StringReader reader = new StringReader("abcdef");
		StringReader first = reader.createSubReader(StringReader::new);
		first.skip();
		StringReader second = first.createSubReader(StringReader::new);
		second.skip(2);
		
		second.close();
		assertEquals(3, first.getIndex());
		first.close();
		assertEquals(3, reader.getIndex());
	}
	
	@Test
	void sequentialReading() {
		StringReader reader = new StringReader("key = value\nnext line");
		assertEquals("key", reader.readUntilSkip(' '));
		reader.skipWhitespaces();
		assertEquals("=", reader.readExpected("=", true));
		reader.skipWhitespaces();
		assertEquals("value", reader.readLine(false));
		assertEquals("next line", reader.readLine(false));
	}
	
	@Test
	void readUntilStringOverMultipleMatches() {
		StringReader reader = new StringReader("aaab");
		assertEquals("aa", reader.readUntil("ab", true));
		assertEquals("ab", reader.readRemaining());
	}
	
	private static final class FailingReader extends Reader {
		
		@Override
		public int read(char @NonNull [] buffer, int offset, int length) throws IOException {
			throw new IOException("Failed to read");
		}
		
		@Override
		public void close() {}
	}
}
