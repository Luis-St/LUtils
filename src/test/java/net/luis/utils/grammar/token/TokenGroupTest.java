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

import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroup}.<br>
 *
 * @author Luis-St
 */
class TokenGroupTest {
	
	@Test
	void constructWithTokensOnly() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"));
		TokenGroup group = new TokenGroup(tokens);
		assertEquals("", group.label());
		assertEquals(tokens, group.tokens());
	}
	
	@Test
	void constructWithNullTokensOneArgThrows() {
		assertThrows(NullPointerException.class, () -> new TokenGroup(null));
	}
	
	@Test
	void constructWithLabelAndTokens() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"));
		TokenGroup group = new TokenGroup("expr", tokens);
		assertEquals("expr", group.label());
		assertEquals(tokens, group.tokens());
	}
	
	@Test
	void constructWithNullLabelThrows() {
		assertThrows(NullPointerException.class, () -> new TokenGroup(null, List.of(SimpleToken.createUnpositioned("a"))));
	}
	
	@Test
	void constructWithNullTokensThrows() {
		assertThrows(NullPointerException.class, () -> new TokenGroup("label", null));
	}
	
	@Test
	void constructWithEmptyTokensThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup("label", List.of()));
	}
	
	@Test
	void constructWithListContainingNullElementThrows() {
		List<Token> tokens = Arrays.asList(SimpleToken.createUnpositioned("a"), null);
		assertThrows(NullPointerException.class, () -> new TokenGroup("label", tokens));
	}
	
	@Test
	void constructWithSingleNonNullTokenSucceeds() {
		TokenGroup group = new TokenGroup("label", List.of(SimpleToken.createUnpositioned("a")));
		assertEquals(1, group.tokens().size());
	}
	
	@Test
	void isLeafReturnsTrueWhenNoChildIsTokenGroup() {
		TokenGroup group = new TokenGroup("label", List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b")));
		assertTrue(group.isLeaf());
	}
	
	@Test
	void isLeafReturnsFalseWhenAChildIsTokenGroup() {
		TokenGroup nested = new TokenGroup(List.of(SimpleToken.createUnpositioned("a")));
		TokenGroup outer = new TokenGroup("label", List.of(nested, SimpleToken.createUnpositioned("b")));
		assertFalse(outer.isLeaf());
	}
	
	@Test
	void valueConcatenatesSingleTokenValue() {
		TokenGroup group = new TokenGroup(List.of(SimpleToken.createUnpositioned("abc")));
		assertEquals("abc", group.value());
	}
	
	@Test
	void valueConcatenatesMultipleTokenValuesInOrder() {
		TokenGroup group = new TokenGroup(List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b"), SimpleToken.createUnpositioned("c")));
		assertEquals("abc", group.value());
	}
	
	@Test
	void positionReturnsFirstTokenPosition() {
		Token first = new SimpleToken("a", new TokenPosition(0, 0, 0), Set.of());
		Token second = new SimpleToken("b", new TokenPosition(0, 1, 1), Set.of());
		TokenGroup group = new TokenGroup(List.of(first, second));
		assertEquals(first.position(), group.position());
	}
	
	@Test
	void typesReturnsEmptySetWhenNoChildHasTypes() {
		TokenGroup group = new TokenGroup(List.of(SimpleToken.createUnpositioned("a"), SimpleToken.createUnpositioned("b")));
		assertTrue(group.types().isEmpty());
	}
	
	@Test
	void typesUnionsAllChildTypes() {
		Token first = new SimpleToken("a", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		Token second = new SimpleToken("b", TokenPosition.UNPOSITIONED, Set.of());
		TokenGroup group = new TokenGroup(List.of(first, second));
		assertTrue(group.types().contains(StandardTokenType.UNKNOWN));
		assertEquals(1, group.types().size());
	}
	
	@Test
	void childrenReturnsSameTokensAsTokensAccessor() {
		TokenGroup group = new TokenGroup(List.of(SimpleToken.createUnpositioned("a")));
		assertEquals(group.tokens(), group.children());
	}
	
	@Test
	void tokensAreDefensivelyCopiedFromInputList() {
		List<Token> mutable = new ArrayList<>(List.of(SimpleToken.createUnpositioned("a")));
		TokenGroup group = new TokenGroup("label", mutable);
		mutable.clear();
		assertEquals(1, group.tokens().size());
	}
	
	@Test
	void tokensListIsImmutable() {
		TokenGroup group = new TokenGroup("label", List.of(SimpleToken.createUnpositioned("a")));
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().add(SimpleToken.createUnpositioned("b")));
	}
	
	@Test
	void typesDeduplicatesSharedTypesAcrossChildren() {
		Token first = new SimpleToken("a", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		Token second = new SimpleToken("b", TokenPosition.UNPOSITIONED, Set.of(StandardTokenType.UNKNOWN));
		TokenGroup group = new TokenGroup(List.of(first, second));
		assertEquals(1, group.types().size());
		assertTrue(group.types().contains(StandardTokenType.UNKNOWN));
	}
	
	@Test
	void nestedTokenGroupsComputeValuePositionAndLeafStatusRecursively() {
		Token leaf1 = new SimpleToken("a", new TokenPosition(0, 0, 0), Set.of());
		Token leaf2 = new SimpleToken("b", new TokenPosition(0, 1, 1), Set.of());
		TokenGroup inner = new TokenGroup("inner", List.of(leaf1, leaf2));
		Token leaf3 = new SimpleToken("c", new TokenPosition(0, 2, 2), Set.of());
		TokenGroup outer = new TokenGroup("outer", List.of(inner, leaf3));
		
		assertEquals("abc", outer.value());
		assertEquals(leaf1.position(), outer.position());
		assertFalse(outer.isLeaf());
		assertTrue(inner.isLeaf());
	}
	
	@Test
	void labelDistinguishesGroupsWithIdenticalTokensButDifferentSemantics() {
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a"));
		TokenGroup unlabeled = new TokenGroup(tokens);
		TokenGroup labeled = new TokenGroup("expr", tokens);
		
		assertNotEquals(unlabeled, labeled);
		assertEquals(unlabeled.value(), labeled.value());
		assertEquals("", unlabeled.label());
		assertEquals("expr", labeled.label());
	}
}
