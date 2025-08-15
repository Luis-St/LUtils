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

package net.luis.utils.io.token.rule.actions.enhancers;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IndexTokenAction}.<br>
 *
 * @author Luis-St
 */
class IndexTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorDoesNotThrow() {
		assertDoesNotThrow(IndexTokenAction::new);
	}
	
	@Test
	void applyWithNullMatch() {
		IndexTokenAction action = new IndexTokenAction();
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		IndexTokenAction action = new IndexTokenAction();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithSingleToken() {
		IndexTokenAction action = new IndexTokenAction();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(token, indexed.token());
		assertEquals(0, indexed.index());
		assertEquals("test", indexed.value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		IndexTokenAction action = new IndexTokenAction();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		
		IndexedToken indexed0 = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(token1, indexed0.token());
		assertEquals(0, indexed0.index());
		
		IndexedToken indexed1 = assertInstanceOf(IndexedToken.class, result.get(1));
		assertEquals(token2, indexed1.token());
		assertEquals(1, indexed1.index());
		
		IndexedToken indexed2 = assertInstanceOf(IndexedToken.class, result.get(2));
		assertEquals(token3, indexed2.token());
		assertEquals(2, indexed2.index());
	}
	
	@Test
	void applyWithAlreadyIndexedToken() {
		IndexTokenAction action = new IndexTokenAction();
		Token baseToken = createToken("test");
		IndexedToken existingIndexed = new IndexedToken(baseToken, 5);
		List<Token> tokens = List.of(existingIndexed);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertSame(existingIndexed, result.getFirst());
		
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(5, indexed.index());
	}
	
	@Test
	void applyWithMixedTokenTypes() {
		IndexTokenAction action = new IndexTokenAction();
		Token simpleToken = createToken("simple");
		IndexedToken existingIndexed = new IndexedToken(createToken("existing"), 10);
		Token anotherSimple = createToken("another");
		
		List<Token> tokens = List.of(simpleToken, existingIndexed, anotherSimple);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		
		IndexedToken result0 = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(simpleToken, result0.token());
		assertEquals(0, result0.index());
		
		assertSame(existingIndexed, result.get(1));
		
		IndexedToken result2 = assertInstanceOf(IndexedToken.class, result.get(2));
		assertEquals(anotherSimple, result2.token());
		assertEquals(2, result2.index());
	}
	
	@Test
	void applyPreservesOriginalTokenProperties() {
		IndexTokenAction action = new IndexTokenAction();
		Token originalToken = createToken("original");
		List<Token> tokens = List.of(originalToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(originalToken.definition(), indexed.definition());
		assertEquals(originalToken.value(), indexed.value());
		assertEquals(originalToken.position(), indexed.position());
	}
	
	@Test
	void applyWithLargeTokenList() {
		IndexTokenAction action = new IndexTokenAction();
		List<Token> tokens = List.of(
			createToken("token0"), createToken("token1"), createToken("token2"),
			createToken("token3"), createToken("token4"), createToken("token5"),
			createToken("token6"), createToken("token7"), createToken("token8"),
			createToken("token9")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 10, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(10, result.size());
		for (int i = 0; i < 10; i++) {
			IndexedToken indexed = assertInstanceOf(IndexedToken.class, result.get(i));
			assertEquals(tokens.get(i), indexed.token());
			assertEquals(i, indexed.index());
			assertEquals("token" + i, indexed.value());
		}
	}
	
	@Test
	void applyWithDifferentMatchIndices() {
		IndexTokenAction action = new IndexTokenAction();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(5, 7, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		
		IndexedToken indexed0 = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(0, indexed0.index());
		
		IndexedToken indexed1 = assertInstanceOf(IndexedToken.class, result.get(1));
		assertEquals(1, indexed1.index());
	}
	
	@Test
	void applyIndexStartsFromZero() {
		IndexTokenAction action = new IndexTokenAction();
		Token token1 = createToken("a");
		Token token2 = createToken("b");
		Token token3 = createToken("c");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		IndexedToken first = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertTrue(first.isFirst());
		assertEquals(0, first.index());
	}
	
	@Test
	void applyWithSpecialCharacterTokens() {
		IndexTokenAction action = new IndexTokenAction();
		Token space = createToken(" ");
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		List<Token> tokens = List.of(space, tab, newline);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		for (int i = 0; i < 3; i++) {
			IndexedToken indexed = (IndexedToken) result.get(i);
			assertEquals(tokens.get(i), indexed.token());
			assertEquals(i, indexed.index());
		}
	}
	
	@Test
	void applyWithEmptyValueTokens() {
		IndexTokenAction action = new IndexTokenAction();
		Token empty1 = createToken("");
		Token empty2 = createToken("");
		List<Token> tokens = List.of(empty1, empty2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		
		IndexedToken indexed0 = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals(0, indexed0.index());
		assertEquals("", indexed0.value());
		
		IndexedToken indexed1 = assertInstanceOf(IndexedToken.class, result.get(1));
		assertEquals(1, indexed1.index());
		assertEquals("", indexed1.value());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		IndexTokenAction action = new IndexTokenAction();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void applyPreservesListSize() {
		IndexTokenAction action = new IndexTokenAction();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(tokens.size(), result.size());
	}
	
	@Test
	void multipleApplicationsProduceSameResult() {
		IndexTokenAction action = new IndexTokenAction();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result1 = action.apply(match);
		List<Token> result2 = action.apply(match);
		
		assertEquals(result1.size(), result2.size());
		for (int i = 0; i < result1.size(); i++) {
			IndexedToken indexed1 = (IndexedToken) result1.get(i);
			IndexedToken indexed2 = (IndexedToken) result2.get(i);
			
			assertEquals(indexed1.token(), indexed2.token());
			assertEquals(indexed1.index(), indexed2.index());
		}
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		IndexTokenAction action1 = new IndexTokenAction();
		IndexTokenAction action2 = new IndexTokenAction();
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsClassName() {
		IndexTokenAction action = new IndexTokenAction();
		String actionString = action.toString();
		
		assertTrue(actionString.contains("IndexTokenAction"));
	}
}
