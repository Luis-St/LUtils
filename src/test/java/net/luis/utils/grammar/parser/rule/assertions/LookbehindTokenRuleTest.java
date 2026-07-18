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

package net.luis.utils.grammar.parser.rule.assertions;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.*;
import net.luis.utils.grammar.parser.rule.core.LookMatchMode;
import net.luis.utils.grammar.parser.rule.matchers.ValueTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LookbehindTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookbehindTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		return TokenStream.createMutable(List.of(java.util.Arrays.stream(values).map(LookbehindTokenRuleTest::token).toArray(Token[]::new)));
	}
	
	@Test
	void constructWithValidTokenRuleAndPositiveMode() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertSame(AlwaysMatchTokenRule.INSTANCE, rule.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, rule.mode());
	}
	
	@Test
	void constructWithValidTokenRuleAndNegativeMode() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		assertEquals(LookMatchMode.NEGATIVE, rule.mode());
	}
	
	@Test
	void constructWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(null, LookMatchMode.POSITIVE));
	}
	
	@Test
	void constructWithNullMode() {
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, null));
	}
	
	@Test
	void matchWithNullStreamThrowsException() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContextThrowsException() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = streamOf("a");
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithNoMoreTokensReturnsNull() {
		LookbehindTokenRule rule = new LookbehindTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(), 0);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithMoreTokensAndPositiveModeInnerMatchesReturnsMatch() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithMoreTokensAndPositiveModeInnerDoesNotMatchReturnsNull() {
		LookbehindTokenRule rule = new LookbehindTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNegativeModeInnerMatchesReturnsNull() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNegativeModeInnerDoesNotMatchReturnsMatch() {
		LookbehindTokenRule rule = new LookbehindTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void notFromPositiveModeReturnsNegativeMode() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(LookbehindTokenRule.class, notRule);
		LookbehindTokenRule casted = (LookbehindTokenRule) notRule;
		assertEquals(LookMatchMode.NEGATIVE, casted.mode());
		assertSame(AlwaysMatchTokenRule.INSTANCE, casted.tokenRule());
	}
	
	@Test
	void notFromNegativeModeReturnsPositiveMode() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		TokenRule notRule = rule.not();
		
		assertInstanceOf(LookbehindTokenRule.class, notRule);
		LookbehindTokenRule casted = (LookbehindTokenRule) notRule;
		assertEquals(LookMatchMode.POSITIVE, casted.mode());
		assertSame(AlwaysMatchTokenRule.INSTANCE, casted.tokenRule());
	}
	
	@Test
	void matchAtNonZeroStreamOffsetReturnsCorrectIndex() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b"), token("c")), 2);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchDoesNotConsumeTokensOnSuccess() {
		LookbehindTokenRule rule = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		rule.match(stream, TokenRuleContext.empty());
		
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchUsesLookbehindStreamNotOriginalStream() {
		LookbehindTokenRule rule = new LookbehindTokenRule(new ValueTokenRule("A", false), LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("A"), token("B")), 1);
		
		assertNotNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchAtStreamStartUsesEmptyLookbehindStream() {
		TokenStream stream = TokenStream.createMutable(List.of(token("A"), token("B")), 0);
		LookbehindTokenRule positive = new LookbehindTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		LookbehindTokenRule negative = new LookbehindTokenRule(NeverMatchTokenRule.INSTANCE, LookMatchMode.NEGATIVE);
		
		assertNull(positive.match(stream, TokenRuleContext.empty()));
		TokenRuleMatch negativeMatch = negative.match(TokenStream.createMutable(List.of(token("A"), token("B")), 0), TokenRuleContext.empty());
		assertNotNull(negativeMatch);
		assertEquals(0, negativeMatch.startIndex());
	}
	
	@Test
	void notThenMatchBehavesAsInvertedRule() {
		LookbehindTokenRule original = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		TokenRule negated = original.not();
		
		TokenRuleMatch originalMatch = original.match(TokenStream.createMutable(List.of(token("a"), token("b")), 1), TokenRuleContext.empty());
		TokenRuleMatch negatedMatch = negated.match(TokenStream.createMutable(List.of(token("a"), token("b")), 1), TokenRuleContext.empty());
		
		assertNotNull(originalMatch);
		assertNull(negatedMatch);
	}
	
	// Plan-vs-source discrepancy: the plan expects outer.match(...) to be non-null, but tracing the source shows
	// createLookbehindStream() always returns a stream positioned at index 0, so a second-level lookbehind
	// (the inner rule's own createLookbehindStream() call) is always empty regardless of how many tokens
	// precede the outer stream's current index. AlwaysMatchTokenRule therefore never matches inside the inner
	// rule, so inner.match(...) is always null and outer (POSITIVE) also always returns null. The source-correct
	// expectation (null) is asserted here instead of the plan's stated non-null outcome.
	@Test
	void matchWithNestedLookbehindTokenRuleAsInnerRule() {
		LookbehindTokenRule inner = new LookbehindTokenRule(AlwaysMatchTokenRule.INSTANCE, LookMatchMode.POSITIVE);
		LookbehindTokenRule outer = new LookbehindTokenRule(inner, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(token("a"), token("b")), 1);
		
		TokenRuleMatch match = outer.match(stream, TokenRuleContext.empty());
		
		assertNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
}
