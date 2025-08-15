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

package net.luis.utils.io.token.tokens;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IndexedToken}.<br>
 *
 * @author Luis-St
 */
class IndexedTokenTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullToken() {
		assertThrows(NullPointerException.class, () -> new IndexedToken(null, 0));
	}
	
	@Test
	void constructorWithNegativeIndex() {
		Token token = createToken("test");
		
		assertThrows(IllegalArgumentException.class, () -> new IndexedToken(token, -1));
		assertThrows(IllegalArgumentException.class, () -> new IndexedToken(token, -5));
	}
	
	@Test
	void constructorWithValidParameters() {
		Token token = createToken("test");
		
		assertDoesNotThrow(() -> new IndexedToken(token, 0));
		assertDoesNotThrow(() -> new IndexedToken(token, 5));
		assertDoesNotThrow(() -> new IndexedToken(token, 100));
	}
	
	@Test
	void constructorWithZeroIndex() {
		Token token = createToken("test");
		
		assertDoesNotThrow(() -> new IndexedToken(token, 0));
	}
	
	@Test
	void firstWithNullToken() {
		assertThrows(NullPointerException.class, () -> IndexedToken.first(null));
	}
	
	@Test
	void firstWithValidToken() {
		Token token = createToken("test");
		
		IndexedToken indexed = IndexedToken.first(token);
		
		assertEquals(token, indexed.token());
		assertEquals(0, indexed.index());
		assertTrue(indexed.isFirst());
	}
	
	@Test
	void tokenReturnsWrappedToken() {
		Token originalToken = createToken("original");
		IndexedToken indexed = new IndexedToken(originalToken, 5);
		
		assertSame(originalToken, indexed.token());
	}
	
	@Test
	void indexReturnsCorrectValue() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 42);
		
		assertEquals(42, indexed.index());
	}
	
	@Test
	void definitionDelegatesToWrappedToken() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 1);
		
		assertEquals(token.definition(), indexed.definition());
	}
	
	@Test
	void valueDelegatesToWrappedToken() {
		Token token = createToken("testValue");
		IndexedToken indexed = new IndexedToken(token, 1);
		
		assertEquals("testValue", indexed.value());
	}
	
	@Test
	void positionDelegatesToWrappedToken() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 1);
		
		assertEquals(token.position(), indexed.position());
	}
	
	@Test
	void isFirstWithIndexZero() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 0);
		
		assertTrue(indexed.isFirst());
	}
	
	@Test
	void isFirstWithNonZeroIndex() {
		Token token = createToken("test");
		IndexedToken indexed1 = new IndexedToken(token, 1);
		IndexedToken indexed5 = new IndexedToken(token, 5);
		IndexedToken indexed100 = new IndexedToken(token, 100);
		
		assertFalse(indexed1.isFirst());
		assertFalse(indexed5.isFirst());
		assertFalse(indexed100.isFirst());
	}
	
	@Test
	void hasIndexWithMatchingIndex() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 7);
		
		assertTrue(indexed.hasIndex(7));
	}
	
	@Test
	void hasIndexWithNonMatchingIndex() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 7);
		
		assertFalse(indexed.hasIndex(0));
		assertFalse(indexed.hasIndex(6));
		assertFalse(indexed.hasIndex(8));
		assertFalse(indexed.hasIndex(100));
	}
	
	@Test
	void hasIndexWithNegativeInput() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 5);
		
		assertFalse(indexed.hasIndex(-1));
		assertFalse(indexed.hasIndex(-5));
	}
	
	@Test
	void indexedTokenImplementsTokenInterface() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 1);
		
		assertInstanceOf(Token.class, indexed);
	}
	
	@Test
	void wrappingIndexedToken() {
		Token baseToken = createToken("base");
		IndexedToken firstLevel = new IndexedToken(baseToken, 1);
		IndexedToken secondLevel = new IndexedToken(firstLevel, 2);
		
		assertEquals(firstLevel, secondLevel.token());
		assertEquals(2, secondLevel.index());
		assertEquals(baseToken, firstLevel.token());
		assertEquals(1, firstLevel.index());
	}
	
	@Test
	void indexedTokenWithDifferentTokenTypes() {
		Token simpleToken = createToken("simple");
		AnnotatedToken annotatedToken = AnnotatedToken.of(createToken("annotated"), "type", "test");
		
		IndexedToken indexedSimple = new IndexedToken(simpleToken, 1);
		IndexedToken indexedAnnotated = new IndexedToken(annotatedToken, 2);
		
		assertEquals(simpleToken, indexedSimple.token());
		assertEquals(1, indexedSimple.index());
		
		assertEquals(annotatedToken, indexedAnnotated.token());
		assertEquals(2, indexedAnnotated.index());
	}
	
	@Test
	void indexedTokenWithLargeIndex() {
		Token token = createToken("test");
		int largeIndex = Integer.MAX_VALUE;
		IndexedToken indexed = new IndexedToken(token, largeIndex);
		
		assertEquals(largeIndex, indexed.index());
		assertFalse(indexed.isFirst());
		assertTrue(indexed.hasIndex(largeIndex));
	}
	
	@Test
	void indexedTokenPreservesTokenProperties() {
		Token originalToken = createToken("original");
		IndexedToken indexed = new IndexedToken(originalToken, 5);
		
		assertEquals(originalToken.definition(), indexed.definition());
		assertEquals(originalToken.value(), indexed.value());
		assertEquals(originalToken.position(), indexed.position());
	}
	
	@Test
	void firstFactoryMethodCreatesCorrectToken() {
		Token token = createToken("test");
		IndexedToken first = IndexedToken.first(token);
		IndexedToken manual = new IndexedToken(token, 0);
		
		assertEquals(manual.token(), first.token());
		assertEquals(manual.index(), first.index());
		assertEquals(manual.isFirst(), first.isFirst());
	}
	
	@Test
	void multipleIndexedTokensWithSameBase() {
		Token baseToken = createToken("base");
		IndexedToken indexed0 = new IndexedToken(baseToken, 0);
		IndexedToken indexed1 = new IndexedToken(baseToken, 1);
		IndexedToken indexed2 = new IndexedToken(baseToken, 2);
		
		assertTrue(indexed0.isFirst());
		assertFalse(indexed1.isFirst());
		assertFalse(indexed2.isFirst());
		
		assertTrue(indexed0.hasIndex(0));
		assertTrue(indexed1.hasIndex(1));
		assertTrue(indexed2.hasIndex(2));
		
		assertFalse(indexed0.hasIndex(1));
		assertFalse(indexed1.hasIndex(0));
		assertFalse(indexed2.hasIndex(1));
	}
	
	@Test
	void indexedTokenWithSpecialCharacters() {
		Token spaceToken = createToken(" ");
		Token tabToken = createToken("\t");
		Token newlineToken = createToken("\n");
		
		IndexedToken indexedSpace = new IndexedToken(spaceToken, 0);
		IndexedToken indexedTab = new IndexedToken(tabToken, 1);
		IndexedToken indexedNewline = new IndexedToken(newlineToken, 2);
		
		assertEquals(" ", indexedSpace.value());
		assertEquals("\t", indexedTab.value());
		assertEquals("\n", indexedNewline.value());
		
		assertTrue(indexedSpace.isFirst());
		assertFalse(indexedTab.isFirst());
		assertFalse(indexedNewline.isFirst());
	}
	
	@Test
	void indexedTokenWithEmptyValue() {
		Token emptyToken = createToken("");
		IndexedToken indexed = new IndexedToken(emptyToken, 0);
		
		assertEquals("", indexed.value());
		assertTrue(indexed.isFirst());
	}
	
	@Test
	void toStringContainsTokenInfo() {
		Token token = createToken("test");
		IndexedToken indexed = new IndexedToken(token, 5);
		String tokenString = indexed.toString();
		
		assertTrue(tokenString.contains("IndexedToken"));
		assertTrue(tokenString.contains("token"));
		assertTrue(tokenString.contains("index"));
		assertTrue(tokenString.contains("5"));
	}
	
	@Test
	void equalIndexedTokensHaveSameHashCode() {
		Token token = createToken("test");
		IndexedToken indexed1 = new IndexedToken(token, 3);
		IndexedToken indexed2 = new IndexedToken(token, 3);
		
		assertEquals(indexed1.hashCode(), indexed2.hashCode());
	}
	
	@Test
	void indexBoundaryValues() {
		Token token = createToken("test");
		
		IndexedToken minIndex = new IndexedToken(token, 0);
		assertEquals(0, minIndex.index());
		assertTrue(minIndex.isFirst());
		
		IndexedToken maxIndex = new IndexedToken(token, Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, maxIndex.index());
		assertFalse(maxIndex.isFirst());
		assertTrue(maxIndex.hasIndex(Integer.MAX_VALUE));
	}
}
