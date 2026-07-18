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

package net.luis.utils.grammar.parser.action.enhancers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IndexTokenAction}.<br>
 *
 * @author Luis-St
 */
class IndexTokenActionTest {
	
	private static TokenRuleMatch matchOf(List<Token> tokens) {
		return new TokenRuleMatch(0, tokens.size(), tokens, AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void constructWithNoArguments() {
		IndexTokenAction action = new IndexTokenAction();
		assertEquals(0, action.startIndex());
	}
	
	@Test
	void constructWithValidStartIndex() {
		IndexTokenAction action = new IndexTokenAction(5);
		assertEquals(5, action.startIndex());
	}
	
	@Test
	void constructWithNegativeStartIndexThrows() {
		assertThrows(IllegalArgumentException.class, () -> new IndexTokenAction(-1));
	}
	
	@Test
	void applyWithNullMatchThrows() {
		IndexTokenAction action = new IndexTokenAction();
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		assertThrows(NullPointerException.class, () -> action.apply(null, ctx));
	}
	
	@Test
	void applyWithNullContextThrows() {
		IndexTokenAction action = new IndexTokenAction();
		TokenRuleMatch match = matchOf(List.of());
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyList() {
		IndexTokenAction action = new IndexTokenAction();
		TokenRuleMatch match = matchOf(List.of());
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithPlainTokenWrapsInIndexedToken() {
		IndexTokenAction action = new IndexTokenAction(0);
		Token plain = SimpleToken.createUnpositioned("a");
		TokenRuleMatch match = matchOf(List.of(plain));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertInstanceOf(IndexedToken.class, result.get(0));
		IndexedToken indexed = (IndexedToken) result.get(0);
		assertEquals(0, indexed.index());
		assertEquals(plain, indexed.token());
	}
	
	@Test
	void applyWithAlreadyIndexedTokenKeepsItUnchanged() {
		IndexTokenAction action = new IndexTokenAction(0);
		IndexedToken existing = new IndexedToken(SimpleToken.createUnpositioned("a"), 7);
		TokenRuleMatch match = matchOf(List.of(existing));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertSame(existing, result.get(0));
		assertEquals(7, ((IndexedToken) result.get(0)).index());
	}
	
	@Test
	void applyWithMultiplePlainTokensAssignsSequentialIndices() {
		IndexTokenAction action = new IndexTokenAction();
		Token first = SimpleToken.createUnpositioned("a");
		Token second = SimpleToken.createUnpositioned("b");
		Token third = SimpleToken.createUnpositioned("c");
		TokenRuleMatch match = matchOf(List.of(first, second, third));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertEquals(0, ((IndexedToken) result.get(0)).index());
		assertEquals(1, ((IndexedToken) result.get(1)).index());
		assertEquals(2, ((IndexedToken) result.get(2)).index());
	}
	
	@Test
	void applyWithCustomStartIndexOffsetsSequentialIndices() {
		IndexTokenAction action = new IndexTokenAction(10);
		Token first = SimpleToken.createUnpositioned("a");
		Token second = SimpleToken.createUnpositioned("b");
		TokenRuleMatch match = matchOf(List.of(first, second));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertEquals(10, ((IndexedToken) result.get(0)).index());
		assertEquals(11, ((IndexedToken) result.get(1)).index());
	}
	
	@Test
	void applyReturnsUnmodifiableList() {
		IndexTokenAction action = new IndexTokenAction();
		TokenRuleMatch match = matchOf(List.of(SimpleToken.createUnpositioned("a")));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("b")));
	}
	
	@Test
	void applyWithMixOfPlainAndIndexedTokensPreservesOrderAndSkipsReindexing() {
		IndexTokenAction action = new IndexTokenAction(5);
		Token firstPlain = SimpleToken.createUnpositioned("a");
		IndexedToken alreadyIndexed = new IndexedToken(SimpleToken.createUnpositioned("b"), 99);
		Token secondPlain = SimpleToken.createUnpositioned("c");
		TokenRuleMatch match = matchOf(List.of(firstPlain, alreadyIndexed, secondPlain));
		TokenActionContext ctx = new TokenActionContext(TokenStream.EMPTY);
		
		List<Token> result = action.apply(match, ctx);
		assertEquals(5, ((IndexedToken) result.get(0)).index());
		assertSame(alreadyIndexed, result.get(1));
		assertEquals(99, ((IndexedToken) result.get(1)).index());
		assertEquals(7, ((IndexedToken) result.get(2)).index());
	}
}
