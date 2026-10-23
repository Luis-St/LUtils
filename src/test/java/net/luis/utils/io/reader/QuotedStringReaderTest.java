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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link QuotedStringReader}.<br>
 *
 * @author Luis-St
 */
class QuotedStringReaderTest {
	
	@Test
	void constructWithString() {
		QuotedStringReader reader = new QuotedStringReader("test");
		assertEquals(StringQuoting.DEFAULT, reader.getQuoting());
		assertEquals('\\', reader.getEscapeCharacter());
	}
	
	@Test
	void constructWithConfiguration() {
		QuotedStringReader reader = new QuotedStringReader("test", null, StringQuoting.DOUBLE_ONLY);
		assertNull(reader.getEscapeCharacter());
		assertEquals(StringQuoting.DOUBLE_ONLY, reader.getQuoting());
	}
	
	@Test
	void constructWithReader() {
		QuotedStringReader reader = new QuotedStringReader(new java.io.StringReader("content"));
		assertEquals("content", reader.getString());
		assertEquals(StringQuoting.DEFAULT, reader.getQuoting());
		
		QuotedStringReader configured = new QuotedStringReader(new java.io.StringReader("content"), '#', StringQuoting.NONE);
		assertEquals('#', configured.getEscapeCharacter());
		assertEquals(StringQuoting.NONE, configured.getQuoting());
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new QuotedStringReader((String) null));
		assertThrows(NullPointerException.class, () -> new QuotedStringReader((String) null, '\\', StringQuoting.DEFAULT));
		assertThrows(NullPointerException.class, () -> new QuotedStringReader("test", '\\', null));
		assertThrows(NullPointerException.class, () -> new QuotedStringReader((Reader) null));
		assertThrows(NullPointerException.class, () -> new QuotedStringReader((Reader) null, '\\', StringQuoting.DEFAULT));
		assertThrows(NullPointerException.class, () -> new QuotedStringReader(new java.io.StringReader("test"), '\\', null));
	}
	
	@Test
	void readQuotedStringAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new QuotedStringReader("").readQuotedString());
	}
	
	@Test
	void readQuotedStringWithoutQuote() {
		QuotedStringReader reader = new QuotedStringReader("abc");
		assertThrows(InvalidStringException.class, reader::readQuotedString);
		assertEquals(0, reader.getIndex());
	}
	
	@Test
	void readQuotedStringWithUnhandledQuote() {
		QuotedStringReader reader = new QuotedStringReader("'a'", '\\', StringQuoting.DOUBLE_ONLY);
		assertThrows(InvalidStringException.class, reader::readQuotedString);
	}
	
	@Test
	void readQuotedStringNotClosed() {
		QuotedStringReader reader = new QuotedStringReader("\"abc");
		InvalidStringException exception = assertThrows(InvalidStringException.class, reader::readQuotedString);
		assertTrue(exception.getMessage().contains("'\"abc'"));
		assertTrue(exception.getMessage().endsWith("at line 1, column 5"));
	}
	
	@Test
	void readStringAtEnd() {
		assertThrows(StringIndexOutOfBoundsException.class, () -> new QuotedStringReader("").readString());
	}
	
	@Test
	void constructQuoteTrackerWithNullQuoting() {
		assertThrows(NullPointerException.class, () -> new QuotedStringReader.QuoteTracker(null));
	}
	
	@Test
	void isQuoteBranches() {
		QuotedStringReader reader = new QuotedStringReader("");
		assertTrue(reader.isQuote('"'));
		assertTrue(reader.isQuote('\''));
		assertFalse(new QuotedStringReader("", '\\', StringQuoting.DOUBLE_ONLY).isQuote('\''));
		assertFalse(reader.isQuote('a'));
	}
	
	@Test
	void createTrackerTracksQuotes() {
		StringReader.NestingTracker tracker = new QuotedStringReader("").createTracker();
		assertInstanceOf(QuotedStringReader.QuoteTracker.class, tracker);
		tracker.accept('"');
		assertTrue(tracker.isNested());
	}
	
	@Test
	void readQuotedStringWithDoubleQuotes() {
		QuotedStringReader reader = new QuotedStringReader("\"abc\" rest");
		assertEquals("abc", reader.readQuotedString());
		assertEquals(5, reader.getIndex());
	}
	
	@Test
	void readQuotedStringWithEscapedQuote() {
		QuotedStringReader reader = new QuotedStringReader("\"a\\\"b\"");
		assertEquals("a\\\"b", reader.readQuotedString());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readQuotedStringWithEscapingDisabled() {
		QuotedStringReader reader = new QuotedStringReader("\"a\\\"b\"", null, StringQuoting.DEFAULT);
		assertEquals("a\\", reader.readQuotedString());
		assertEquals('b', reader.peek());
	}
	
	@Test
	void readQuotedStringWithOtherQuoteInside() {
		assertEquals("it's", new QuotedStringReader("\"it's\"").readQuotedString());
		assertEquals("say \"hi\"", new QuotedStringReader("'say \"hi\"'").readQuotedString());
	}
	
	@Test
	void readQuotedStringEmpty() {
		QuotedStringReader reader = new QuotedStringReader("\"\"");
		assertEquals("", reader.readQuotedString());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readQuotedStringWithAsymmetricQuote() {
		QuotedStringReader reader = new QuotedStringReader("<a<b>", '\\', StringQuoting.of(new StringQuote('<', '>')));
		assertEquals("a<b", reader.readQuotedString());
		assertFalse(reader.canRead());
	}
	
	@Test
	void readStringQuoted() {
		QuotedStringReader reader = new QuotedStringReader("'is a' test");
		assertEquals("is a", reader.readString());
		assertEquals(' ', reader.peek());
	}
	
	@Test
	void readStringUnquoted() {
		QuotedStringReader reader = new QuotedStringReader("is a");
		assertEquals("is", reader.readString());
		assertEquals('a', reader.peek());
	}
	
	@Test
	void readStringWithUnhandledQuote() {
		QuotedStringReader reader = new QuotedStringReader("'is a'", '\\', StringQuoting.DOUBLE_ONLY);
		assertEquals("'is", reader.readString());
		assertEquals("a'", reader.readString());
	}
	
	@Test
	void readUntilIgnoresTerminatorInQuotes() {
		QuotedStringReader reader = new QuotedStringReader("'a,b',c");
		assertEquals("'a,b'", reader.readUntil(','));
		assertEquals(',', reader.peek());
	}
	
	@Test
	void readUntilWithUnclosedQuote() {
		QuotedStringReader reader = new QuotedStringReader("'a,b");
		assertEquals("'a,b", assertDoesNotThrow(() -> reader.readUntil(',')));
	}
	
	@Test
	void readUntilWithoutQuoting() {
		QuotedStringReader reader = new QuotedStringReader("'a,b',c", '\\', StringQuoting.NONE);
		assertEquals("'a", reader.readUntil(','));
	}
	
	@Test
	void readUntilStringIgnoresTerminatorInQuotes() {
		QuotedStringReader reader = new QuotedStringReader("\"a end\" b end");
		assertEquals("\"a end\" b ", reader.readUntil("end", true));
		assertEquals("end", reader.readRemaining());
	}
	
	@Test
	void quoteTrackerOpensAndCloses() {
		QuotedStringReader.QuoteTracker tracker = new QuotedStringReader.QuoteTracker(StringQuoting.DEFAULT);
		assertFalse(tracker.isNested());
		tracker.accept('"');
		assertTrue(tracker.isInQuote());
		assertTrue(tracker.isNested());
		tracker.accept('"');
		assertFalse(tracker.isInQuote());
		assertFalse(tracker.isNested());
	}
	
	@Test
	void quoteTrackerIgnoresOtherQuoteInside() {
		QuotedStringReader.QuoteTracker tracker = new QuotedStringReader.QuoteTracker(StringQuoting.DEFAULT);
		tracker.accept('"');
		tracker.accept('\'');
		assertTrue(tracker.isNested());
		tracker.accept('"');
		assertFalse(tracker.isNested());
	}
	
	@Test
	void quoteTrackerIgnoresOrdinaryCharacter() {
		QuotedStringReader.QuoteTracker tracker = new QuotedStringReader.QuoteTracker(StringQuoting.DEFAULT);
		tracker.accept('a');
		assertFalse(tracker.isNested());
	}
	
	@Test
	void quoteTrackerOpensQuoteBranches() {
		QuotedStringReader.QuoteTracker tracker = new QuotedStringReader.QuoteTracker(StringQuoting.DEFAULT);
		assertTrue(tracker.opensQuote('"'));
		assertFalse(tracker.opensQuote('a'));
		tracker.accept('"');
		assertFalse(tracker.opensQuote('"'));
	}
	
	@Test
	void equalReadersWithSameQuoting() {
		QuotedStringReader first = new QuotedStringReader("abc");
		QuotedStringReader second = new QuotedStringReader("abc");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void unequalReadersWithOtherQuoting() {
		QuotedStringReader reader = new QuotedStringReader("abc", '\\', StringQuoting.DEFAULT);
		assertNotEquals(reader, new QuotedStringReader("abc", '\\', StringQuoting.NONE));
	}
	
	@Test
	void readQuotedStringWithSingleQuotes() {
		assertEquals("string", new QuotedStringReader("'string'").readQuotedString());
	}
	
	@Test
	void readStringSequence() {
		QuotedStringReader reader = new QuotedStringReader("this \"is a simple\" 'test string'");
		assertEquals("this", reader.readString());
		assertEquals("is a simple", reader.readString());
		reader.skip();
		assertEquals("test string", reader.readString());
	}
	
	@Test
	void readUntilWithNestedQuotes() {
		QuotedStringReader reader = new QuotedStringReader("\"it's, ok\", next");
		assertEquals("\"it's, ok\"", reader.readUntil(','));
	}
	
	@Test
	void subReaderWithoutQuoting() {
		QuotedStringReader reader = new QuotedStringReader("'a ${b}' rest");
		try (StringReader subReader = reader.createSubReader(8, StringReader::new)) {
			assertEquals("'a ", subReader.readUntil('$'));
		}
		assertEquals(3, reader.getIndex());
		assertEquals("${b}' rest", reader.readRemaining());
	}
}
