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

package net.luis.utils.grammar.parser.rule.reference;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.rule.TokenRules;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CaptureTokenRule}.<br>
 *
 * @author Luis-St
 */
class CaptureTokenRuleTest {
	
	private static Token token(String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static TokenStream streamOf(String... values) {
		Token[] tokens = new Token[values.length];
		for (int i = 0; i < values.length; i++) {
			tokens[i] = token(values[i]);
		}
		return TokenStream.createMutable(List.of(tokens));
	}
	
	@Test
	void constructWithKeyAndTokenRule() {
		TokenRule inner = TokenRules.alwaysMatch();
		CaptureTokenRule rule = new CaptureTokenRule("key", inner);
		
		assertEquals("key", rule.key());
		assertEquals(inner, rule.tokenRule());
	}
	
	@Test
	void constructWithNullKeyThrows() {
		assertThrows(NullPointerException.class, () -> new CaptureTokenRule(null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructWithNullTokenRuleThrows() {
		assertThrows(NullPointerException.class, () -> new CaptureTokenRule("key", null));
	}
	
	@Test
	void constructWithEmptyKeyThrows() {
		assertThrows(IllegalArgumentException.class, () -> new CaptureTokenRule("", TokenRules.alwaysMatch()));
	}
	
	@Test
	void matchWithNullStreamThrows() {
		CaptureTokenRule rule = new CaptureTokenRule("key", TokenRules.alwaysMatch());
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContextThrows() {
		CaptureTokenRule rule = new CaptureTokenRule("key", TokenRules.alwaysMatch());
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void constructWithNonEmptyKeySucceeds() {
		CaptureTokenRule rule = new CaptureTokenRule("k", TokenRules.alwaysMatch());
		
		assertEquals("k", rule.key());
	}
	
	@Test
	void matchWithMatchingRuleCapturesTokensAndReturnsMatch() {
		TokenStream stream = streamOf("x");
		CaptureTokenRule rule = new CaptureTokenRule("cap", TokenRules.value("x", false));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(match.matchedTokens(), ctx.getCapturedTokens("cap"));
	}
	
	@Test
	void matchWithNonMatchingRuleReturnsNullAndDoesNotCapture() {
		TokenStream stream = streamOf("x");
		CaptureTokenRule rule = new CaptureTokenRule("cap", TokenRules.value("y", false));
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		assertNull(rule.match(stream, ctx));
		assertNull(ctx.getCapturedTokens("cap"));
	}
	
	@Test
	void matchOnEmptyStreamWithNonMatchingRuleReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of());
		CaptureTokenRule rule = new CaptureTokenRule("cap", TokenRules.value("x", false));
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void notReturnsNewCaptureTokenRuleWithNegatedInnerRule() {
		CaptureTokenRule original = new CaptureTokenRule("cap", TokenRules.value("x", false));
		
		TokenRule negated = original.not();
		
		assertInstanceOf(CaptureTokenRule.class, negated);
		CaptureTokenRule castedNegated = (CaptureTokenRule) negated;
		assertEquals("cap", castedNegated.key());
		assertNotNull(castedNegated.tokenRule().match(streamOf("y"), TokenRuleContext.empty()));
		assertNull(castedNegated.tokenRule().match(streamOf("x"), TokenRuleContext.empty()));
	}
	
	@Test
	void capturedTokensAreOverwrittenOnRepeatedMatchesWithSameKey() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		CaptureTokenRule ruleA = new CaptureTokenRule("cap", TokenRules.value("a", false));
		CaptureTokenRule ruleB = new CaptureTokenRule("cap", TokenRules.value("b", false));
		
		TokenRuleMatch matchA = ruleA.match(streamOf("a"), ctx);
		TokenRuleMatch matchB = ruleB.match(streamOf("b"), ctx);
		
		assertNotNull(matchA);
		assertNotNull(matchB);
		assertEquals(matchB.matchedTokens(), ctx.getCapturedTokens("cap"));
	}
	
	@Test
	void notThenMatchCapturesUnderSameKeyForNegatedRule() {
		TokenStream stream = streamOf("y");
		CaptureTokenRule negated = (CaptureTokenRule) new CaptureTokenRule("cap", TokenRules.value("x", false)).not();
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenRuleMatch match = negated.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(match.matchedTokens(), ctx.getCapturedTokens("cap"));
	}
}
