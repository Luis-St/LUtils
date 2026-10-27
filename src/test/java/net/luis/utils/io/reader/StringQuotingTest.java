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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.luis.utils.io.reader.StringQuote.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringQuoting}.<br>
 *
 * @author Luis-St
 */
class StringQuotingTest {
	
	@Test
	void constructWithQuotes() {
		StringQuoting quoting = new StringQuoting(List.of(DOUBLE_QUOTE, SINGLE_QUOTE));
		assertEquals(List.of(DOUBLE_QUOTE, SINGLE_QUOTE), quoting.quotes());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new StringQuoting(null));
	}
	
	@Test
	void constructWithNullQuote() {
		List<StringQuote> quotes = new ArrayList<>();
		quotes.add(null);
		assertThrows(NullPointerException.class, () -> new StringQuoting(quotes));
	}
	
	@Test
	void constructWithDuplicateOpeningCharacter() {
		List<StringQuote> quotes = List.of(new StringQuote('"', '"'), new StringQuote('"', '\''));
		assertThrows(IllegalArgumentException.class, () -> new StringQuoting(quotes));
	}
	
	@Test
	void ofWithNullArray() {
		assertThrows(NullPointerException.class, () -> StringQuoting.of((StringQuote[]) null));
	}
	
	@Test
	void constructCopiesList() {
		List<StringQuote> quotes = new ArrayList<>(List.of(DOUBLE_QUOTE));
		StringQuoting quoting = new StringQuoting(quotes);
		quotes.add(SINGLE_QUOTE);
		assertEquals(1, quoting.quotes().size());
		assertThrows(UnsupportedOperationException.class, () -> quoting.quotes().add(SINGLE_QUOTE));
	}
	
	@Test
	void ofWithoutQuotes() {
		StringQuoting quoting = StringQuoting.of();
		assertTrue(quoting.quotes().isEmpty());
		assertEquals(StringQuoting.NONE, quoting);
	}
	
	@Test
	void getQuoteFound() {
		assertEquals(DOUBLE_QUOTE, StringQuoting.DEFAULT.getQuote('"'));
		assertEquals(SINGLE_QUOTE, StringQuoting.DEFAULT.getQuote('\''));
	}
	
	@Test
	void getQuoteNotFound() {
		assertNull(StringQuoting.DEFAULT.getQuote('a'));
		assertNull(StringQuoting.NONE.getQuote('"'));
	}
	
	@Test
	void getQuoteOfClosingCharacter() {
		StringQuoting quoting = StringQuoting.of(new StringQuote('<', '>'));
		assertNull(quoting.getQuote('>'));
		assertNotNull(quoting.getQuote('<'));
	}
	
	@Test
	void isOpeningBranches() {
		assertTrue(StringQuoting.SINGLE_ONLY.isOpening('\''));
		assertFalse(StringQuoting.SINGLE_ONLY.isOpening('"'));
	}
	
	@Test
	void constantQuotings() {
		assertEquals(List.of(SINGLE_QUOTE, DOUBLE_QUOTE), StringQuoting.DEFAULT.quotes());
		assertEquals(List.of(SINGLE_QUOTE), StringQuoting.SINGLE_ONLY.quotes());
		assertEquals(List.of(DOUBLE_QUOTE), StringQuoting.DOUBLE_ONLY.quotes());
		assertEquals(List.of(), StringQuoting.NONE.quotes());
	}
	
	@Test
	void equalQuotings() {
		assertEquals(StringQuoting.DEFAULT, StringQuoting.of(SINGLE_QUOTE, DOUBLE_QUOTE));
		assertEquals(StringQuoting.DEFAULT.hashCode(), StringQuoting.of(SINGLE_QUOTE, DOUBLE_QUOTE).hashCode());
		assertNotEquals(StringQuoting.DEFAULT, StringQuoting.of(DOUBLE_QUOTE, SINGLE_QUOTE));
	}
}
