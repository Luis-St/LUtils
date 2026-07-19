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
import net.luis.utils.grammar.parser.rule.TokenRules;
import net.luis.utils.grammar.parser.rule.core.ReferenceType;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ReferenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class ReferenceTokenRuleTest {
	
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
	void constructWithKeyAndType() {
		ReferenceTokenRule rule = new ReferenceTokenRule("key", ReferenceType.RULE);
		
		assertEquals("key", rule.key());
		assertEquals(ReferenceType.RULE, rule.type());
	}
	
	@Test
	void constructWithNullKeyThrows() {
		assertThrows(NullPointerException.class, () -> new ReferenceTokenRule(null, ReferenceType.RULE));
	}
	
	@Test
	void constructWithNullTypeThrows() {
		assertThrows(NullPointerException.class, () -> new ReferenceTokenRule("key", null));
	}
	
	@Test
	void constructWithEmptyKeyThrows() {
		assertThrows(IllegalArgumentException.class, () -> new ReferenceTokenRule("", ReferenceType.RULE));
	}
	
	@Test
	void matchWithNullStreamThrows() {
		ReferenceTokenRule rule = new ReferenceTokenRule("key", ReferenceType.RULE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, TokenRuleContext.empty()));
	}
	
	@Test
	void matchWithNullContextThrows() {
		ReferenceTokenRule rule = new ReferenceTokenRule("key", ReferenceType.RULE);
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void constructWithNonEmptyKeySucceeds() {
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.TOKENS);
		
		assertNotNull(rule);
	}
	
	@Test
	void matchRuleTypeWithDefinedRuleDelegatesAndReturnsMatch() {
		TokenStream stream = streamOf("x");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("r", TokenRules.value("x", false));
		ReferenceTokenRule rule = new ReferenceTokenRule("r", ReferenceType.RULE);
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchRuleTypeWithUndefinedRuleReturnsNull() {
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("missing", ReferenceType.RULE);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchTokensTypeOnEmptyStreamReturnsNull() {
		TokenStream stream = TokenStream.createMutable(List.of());
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchTokensTypeWithNoCapturedTokensReturnsNull() {
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchTokensTypeWithEmptyCapturedListReturnsNull() {
		TokenStream stream = streamOf("x");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("cap", List.of());
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		assertNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchTokensTypeWithSingleCapturedTokenMatchesByValue() {
		TokenStream stream = streamOf("x");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("cap", List.of(token("x")));
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
	}
	
	@Test
	void matchTokensTypeWithMultipleCapturedTokensMatchesBySequence() {
		TokenStream stream = streamOf("a", "b");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("cap", List.of(token("a"), token("b")));
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void matchDynamicTypeWithBothRuleAndTokensDefinedReturnsNull() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("k", TokenRules.value("x", false));
		ctx.captureTokens("k", List.of(token("x")));
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.DYNAMIC);
		
		assertNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchDynamicTypeWithOnlyRuleDefinedDelegatesToRuleMatching() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("k", TokenRules.value("x", false));
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.DYNAMIC);
		
		assertNotNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchDynamicTypeWithOnlyTokensCapturedDelegatesToTokensMatching() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("k", List.of(token("x")));
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.DYNAMIC);
		
		assertNotNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchDynamicTypeWithNeitherRuleNorTokensReturnsNull() {
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.DYNAMIC);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
	}
	
	@Test
	void matchRuleTypeAdvancesWorkingStreamAndOriginalStream() {
		TokenStream stream = streamOf("x", "y");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("r", TokenRules.value("x", false));
		ReferenceTokenRule rule = new ReferenceTokenRule("r", ReferenceType.RULE);
		
		TokenRuleMatch match = rule.match(stream, ctx);
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchTokensTypeWithNonMatchingCapturedTokenReturnsNull() {
		TokenStream stream = streamOf("z");
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.captureTokens("cap", List.of(token("x")));
		ReferenceTokenRule rule = new ReferenceTokenRule("cap", ReferenceType.TOKENS);
		
		assertNull(rule.match(stream, ctx));
	}
	
	@Test
	void matchDoesNotMutateOriginalStreamOnFailedMatch() {
		TokenStream stream = streamOf("x");
		ReferenceTokenRule rule = new ReferenceTokenRule("missing", ReferenceType.RULE);
		
		assertNull(rule.match(stream, TokenRuleContext.empty()));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void dynamicTypeBehavesLikeRuleTypeWhenOnlyRuleIsRegisteredAcrossMultipleMatches() {
		TokenRuleContext ctx = TokenRuleContext.empty();
		ctx.defineRule("k", TokenRules.value("x", false));
		ReferenceTokenRule rule = new ReferenceTokenRule("k", ReferenceType.DYNAMIC);
		
		TokenRuleMatch matching = rule.match(streamOf("x"), ctx);
		TokenRuleMatch nonMatching = rule.match(streamOf("y"), ctx);
		
		assertNotNull(matching);
		assertNull(nonMatching);
	}
}
