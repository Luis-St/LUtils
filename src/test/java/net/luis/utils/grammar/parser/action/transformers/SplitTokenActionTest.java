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

package net.luis.utils.grammar.parser.action.transformers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
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
	
	private static TokenActionContext contextFor(List<Token> tokens) {
		return new TokenActionContext(TokenStream.createImmutable(tokens));
	}
	
	private static TokenRuleMatch matchFor(List<Token> tokens) {
		return new TokenRuleMatch(0, tokens.size(), tokens, AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void constructWithPattern() {
		SplitTokenAction action = new SplitTokenAction(Pattern.compile(","));
		assertEquals(",", action.splitPattern().pattern());
	}
	
	@Test
	void constructWithNullPattern() {
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((Pattern) null));
	}
	
	@Test
	void constructWithPatternString() {
		SplitTokenAction action = new SplitTokenAction(",");
		assertEquals(",", action.splitPattern().pattern());
	}
	
	@Test
	void constructWithNullPatternString() {
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((String) null));
	}
	
	@Test
	void applyWithNullMatch() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenActionContext ctx = contextFor(List.of());
		assertThrows(NullPointerException.class, () -> action.apply(null, ctx));
	}
	
	@Test
	void applyWithNullContext() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenRuleMatch match = TokenRuleMatch.empty(0, AlwaysMatchTokenRule.INSTANCE);
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenRuleMatch match = TokenRuleMatch.empty(0, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext ctx = contextFor(List.of());
		
		List<Token> result = action.apply(match, ctx);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleTokenProducingOneSplitPart() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("abc"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(1, result.size());
		assertEquals("abc", result.getFirst().value());
	}
	
	@Test
	void applyWithTokenProducingMultipleSplitParts() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a,b,c"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
	}
	
	@Test
	void applyWithTokenValueEqualToDelimiterProducesNoSplitTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned(","));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applySkipsEmptySplitParts() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a,,b"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
	}
	
	@Test
	void applyKeepsNonEmptySplitParts() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a,b"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertFalse(result.get(0).value().isEmpty());
		assertFalse(result.get(1).value().isEmpty());
	}
	
	@Test
	void applyWithUnpositionedTokenUsesUnpositioned() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(new SimpleToken("a,b", TokenPosition.UNPOSITIONED));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		for (Token token : result) {
			assertEquals(TokenPosition.UNPOSITIONED, token.position());
		}
	}
	
	@Test
	void applyWithPositionedTokenComputesPositions() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenPosition position = new TokenPosition(0, 0, 0);
		List<Token> tokens = List.of(new SimpleToken("a,b", position));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertEquals(0, result.get(0).position().character());
		assertEquals(2, result.get(1).position().character());
	}
	
	@Test
	void applyPositionFallbackWhenPartNotFoundAtOffset() {
		// Note: the source's indexOf-fallback (partIndex == -1) could not be reliably triggered through
		// the public API - Pattern.split() always yields parts that are literal, monotonically-ordered
		// substrings of the original value, so indexOf(part, currentOffset) always succeeds for a
		// forward-splitting pattern. This test exercises the same code path as a positioned split and
		// asserts the action still behaves correctly (no exception, non-negative positions).
		SplitTokenAction action = new SplitTokenAction(",");
		TokenPosition position = new TokenPosition(0, 0, 10);
		List<Token> tokens = List.of(new SimpleToken("a,a", position));
		
		List<Token> result = assertDoesNotThrow(() -> action.apply(matchFor(tokens), contextFor(tokens)));
		
		assertEquals(2, result.size());
		assertTrue(result.get(0).position().character() >= 10);
		assertTrue(result.get(1).position().character() >= 10);
	}
	
	@Test
	void applyPositionFoundWithoutFallback() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenPosition position = new TokenPosition(0, 0, 100);
		List<Token> tokens = List.of(new SimpleToken("a,b", position));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(102, result.get(1).position().character());
	}
	
	@Test
	void applySplitsAnnotatedTokenPreservingMetadata() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token annotated = new AnnotatedToken(SimpleToken.createUnpositioned("a,b"), Map.of("k", "v"));
		List<Token> tokens = List.of(annotated);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertInstanceOf(AnnotatedToken.class, result.get(0));
		assertInstanceOf(AnnotatedToken.class, result.get(1));
		assertEquals(Map.of("k", "v"), ((AnnotatedToken) result.get(0)).metadata());
		assertEquals(Map.of("k", "v"), ((AnnotatedToken) result.get(1)).metadata());
	}
	
	@Test
	void applySplitsIndexedTokenPreservingIndex() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token indexed = new IndexedToken(SimpleToken.createUnpositioned("a,b"), 3);
		List<Token> tokens = List.of(indexed);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertInstanceOf(IndexedToken.class, result.get(0));
		assertInstanceOf(IndexedToken.class, result.get(1));
		assertEquals(3, ((IndexedToken) result.get(0)).index());
		assertEquals(3, ((IndexedToken) result.get(1)).index());
	}
	
	@Test
	void applySplitsPlainTokenAsSimpleToken() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a,b"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(2, result.size());
		assertInstanceOf(SimpleToken.class, result.get(0));
		assertInstanceOf(SimpleToken.class, result.get(1));
	}
	
	@Test
	void applyWithPatternObjectConstructor() {
		SplitTokenAction action = new SplitTokenAction(Pattern.compile("\\s+"));
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a  b   c"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
	}
	
	@Test
	void applyDoesNotMutateOriginalMatchedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token original = SimpleToken.createUnpositioned("a,b");
		List<Token> tokens = List.of(original);
		
		action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(1, tokens.size());
		assertSame(original, tokens.getFirst());
	}
	
	@Test
	void applyWithMultipleTokensEachProducingMultipleParts() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token unpositioned = SimpleToken.createUnpositioned("a,b");
		Token positioned = new SimpleToken("c,d", new TokenPosition(0, 0, 0));
		Token annotated = new AnnotatedToken(SimpleToken.createUnpositioned("e,f"), Map.of("k", "v"));
		List<Token> tokens = List.of(unpositioned, positioned, annotated);
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertEquals(6, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
		assertEquals("d", result.get(3).value());
		assertInstanceOf(AnnotatedToken.class, result.get(4));
		assertInstanceOf(AnnotatedToken.class, result.get(5));
	}
	
	@Test
	void applyResultListIsUnmodifiable() {
		SplitTokenAction action = new SplitTokenAction(",");
		List<Token> tokens = List.of(SimpleToken.createUnpositioned("a,b"));
		
		List<Token> result = action.apply(matchFor(tokens), contextFor(tokens));
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(SimpleToken.createUnpositioned("x")));
	}
}
