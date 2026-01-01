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

package net.luis.utils.io.token.actions.transformers;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SplitTokenAction}.<br>
 *
 * @author Luis-St
 */
class SplitTokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullPattern() {
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((Pattern) null));
	}
	
	@Test
	void constructorWithNullStringPattern() {
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((String) null));
	}
	
	@Test
	void constructorWithValidPattern() {
		Pattern pattern = Pattern.compile(",");
		
		SplitTokenAction action = new SplitTokenAction(pattern);
		
		assertEquals(pattern, action.splitPattern());
	}
	
	@Test
	void constructorWithValidStringPattern() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		assertEquals(",", action.splitPattern().pattern());
	}
	
	@Test
	void applyWithNullMatch() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleTokenSimpleSplit() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of(createToken("a,b,c"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		SplitTokenAction action = new SplitTokenAction(" ");
		
		List<Token> tokens = List.of(createToken("hello world"), createToken("foo bar"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(4, result.size());
		assertEquals("hello", result.get(0).value());
		assertEquals("world", result.get(1).value());
		assertEquals("foo", result.get(2).value());
		assertEquals("bar", result.get(3).value());
	}
	
	@Test
	void applyWithNoSplitNeeded() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of(createToken("nosplit"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(1, result.size());
		assertEquals("nosplit", result.getFirst().value());
	}
	
	@Test
	void applyIgnoresEmptySplits() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of(createToken("a,,b,"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
	}
	
	@Test
	void applyWithComplexRegexPattern() {
		SplitTokenAction action = new SplitTokenAction("\\s+");
		
		List<Token> tokens = List.of(createToken("word1  word2\tword3\n\nword4"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(4, result.size());
		assertEquals("word1", result.get(0).value());
		assertEquals("word2", result.get(1).value());
		assertEquals("word3", result.get(2).value());
		assertEquals("word4", result.get(3).value());
	}
	
	@Test
	void applyWithPositionedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token positionedToken = new SimpleToken("a,b,c", new TokenPosition(1, 5, 10));
		
		List<Token> tokens = List.of(positionedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
		
		assertEquals(new TokenPosition(1, 5, 10), result.get(0).position());
		assertEquals(new TokenPosition(1, 7, 12), result.get(1).position());
		assertEquals(new TokenPosition(1, 9, 14), result.get(2).position());
	}
	
	@Test
	void applyWithAnnotatedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Map<String, Object> metadata = Map.of("type", "test");
		AnnotatedToken annotated = new AnnotatedToken(createToken("x,y"), metadata);
		
		List<Token> tokens = List.of(annotated);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(2, result.size());
		
		AnnotatedToken split1 = assertInstanceOf(AnnotatedToken.class, result.get(0));
		AnnotatedToken split2 = assertInstanceOf(AnnotatedToken.class, result.get(1));
		
		assertEquals("x", split1.value());
		assertEquals("y", split2.value());
		assertEquals(metadata, split1.metadata());
		assertEquals(metadata, split2.metadata());
	}
	
	@Test
	void applyWithIndexedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		IndexedToken indexed = new IndexedToken(createToken("p,q"), 5);
		
		List<Token> tokens = List.of(indexed);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(2, result.size());
		
		IndexedToken split1 = assertInstanceOf(IndexedToken.class, result.get(0));
		IndexedToken split2 = assertInstanceOf(IndexedToken.class, result.get(1));
		assertEquals("p", split1.value());
		assertEquals("q", split2.value());
		assertEquals(5, split1.index());
		assertEquals(5, split2.index());
	}
	
	@Test
	void applyReturnsImmutableList() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		List<Token> tokens = List.of(createToken("a,b"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void toStringTest() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("SplitTokenAction"));
	}
}
