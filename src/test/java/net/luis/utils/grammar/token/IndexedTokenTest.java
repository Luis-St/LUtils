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

package net.luis.utils.grammar.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IndexedToken}.<br>
 *
 * @author Luis-St
 */
class IndexedTokenTest {
	
	@Test
	void constructWithTokenAndIndex() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = new IndexedToken(base, 5);
		assertEquals(base, token.token());
		assertEquals(5, token.index());
	}
	
	@Test
	void constructWithNullTokenThrows() {
		assertThrows(NullPointerException.class, () -> new IndexedToken(null, 0));
	}
	
	@Test
	void constructWithNegativeIndexThrows() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		assertThrows(IllegalArgumentException.class, () -> new IndexedToken(base, -1));
	}
	
	@Test
	void indexMethodWithNegativeIndexThrows() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = new IndexedToken(base, 2);
		assertThrows(IllegalArgumentException.class, () -> token.index(-1));
	}
	
	@Test
	void constructWithZeroIndexSucceeds() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = new IndexedToken(base, 0);
		assertEquals(0, token.index());
	}
	
	@Test
	void isFirstReturnsTrueForIndexZero() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		assertTrue(new IndexedToken(base, 0).isFirst());
	}
	
	@Test
	void isFirstReturnsFalseForNonZeroIndex() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		assertFalse(new IndexedToken(base, 1).isFirst());
	}
	
	@Test
	void hasIndexReturnsTrueForMatchingIndex() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		assertTrue(new IndexedToken(base, 3).hasIndex(3));
	}
	
	@Test
	void hasIndexReturnsFalseForNonMatchingIndex() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		assertFalse(new IndexedToken(base, 3).hasIndex(4));
	}
	
	@Test
	void indexMethodWithSameIndexReturnsSelf() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken original = new IndexedToken(base, 2);
		assertSame(original, original.index(2));
	}
	
	@Test
	void indexMethodWithDifferentIndexCreatesNewInstance() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken original = new IndexedToken(base, 2);
		Token result = original.index(5);
		assertNotSame(original, result);
		assertInstanceOf(IndexedToken.class, result);
		assertEquals(5, ((IndexedToken) result).index());
		assertEquals(base, ((IndexedToken) result).token());
	}
	
	@Test
	void firstCreatesTokenWithIndexZero() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = IndexedToken.first(base);
		assertEquals(0, token.index());
		assertTrue(token.isFirst());
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		IndexedToken token = new IndexedToken(SimpleToken.createUnpositioned("hello"), 0);
		assertEquals("hello", token.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = new IndexedToken(base, 0);
		assertEquals(base.position(), token.position());
	}
	
	@Test
	void typesDelegatesToWrappedToken() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken token = new IndexedToken(base, 0);
		assertEquals(base.types(), token.types());
	}
	
	@Test
	void chainedIndexCallsProduceIndependentInstances() {
		SimpleToken base = SimpleToken.createUnpositioned("v");
		IndexedToken t0 = IndexedToken.first(base);
		Token t1 = t0.index(1);
		Token t2 = t1.index(2);
		
		assertEquals(0, t0.index());
		assertEquals(1, ((IndexedToken) t1).index());
		assertEquals(2, ((IndexedToken) t2).index());
		assertEquals(base, t0.token());
		assertEquals(base, ((IndexedToken) t1).token());
		assertEquals(base, ((IndexedToken) t2).token());
	}
}
