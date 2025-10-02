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

package net.luis.utils.io.token.rules.assertions;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.rules.TokenRules;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Test class for {@link LookaheadTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookaheadTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				if (!stream.hasMoreTokens()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.advance(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	@Test
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(null, LookMatchMode.POSITIVE));
	}
	
	@Test
	void constructorWithNullMode() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(createRule("test"), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenRule tokenRule = createRule("test");
		LookMatchMode mode = LookMatchMode.POSITIVE;
		
		LookaheadTokenRule rule = new LookaheadTokenRule(tokenRule, mode);
		
		assertEquals(tokenRule, rule.tokenRule());
		assertEquals(mode, rule.mode());
	}
	
	@Test
	void matchWithNullStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEmptyStreamNegative() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
	}
	
	@Test
	void matchPositiveWithMatchingRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPositiveWithMatchingRuleAtLaterPosition() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("target")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPositiveWithNonMatchingRule() {
		TokenRule innerRule = createRule("expected");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPositiveWithPartialSequenceMatch() {
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("missing"));
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchNegativeWithMatchingRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchNegativeWithNonMatchingRule() {
		TokenRule innerRule = createRule("expected");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchNegativeWithPartialSequenceMatch() {
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("missing"));
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(TokenRules.alwaysMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(TokenRules.neverMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPreservesStreamPositionAfterAdvancingInnerRule() {
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("second"));
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second"), createToken("third")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNestedLookaheadRules() {
		TokenRule innerInnerRule = createRule("target");
		TokenRule innerRule = new LookaheadTokenRule(innerInnerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("target")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithComplexInnerRule() {
		LookaheadTokenRule rule = new LookaheadTokenRule(
			TokenRules.sequence(
				TokenRules.value("if", false),
				TokenRules.value("(", false),
				TokenRules.pattern("\\w+"),
				TokenRules.value(")", false)
			),
			LookMatchMode.POSITIVE
		);
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("if"),
			SimpleToken.createUnpositioned("("),
			SimpleToken.createUnpositioned("condition"),
			SimpleToken.createUnpositioned(")"),
			SimpleToken.createUnpositioned("statement")
		);
		
		TokenStream stream = TokenStream.createMutable(tokens);
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithComplexInnerRuleAtMiddleOfStream() {
		LookaheadTokenRule rule = new LookaheadTokenRule(
			TokenRules.sequence(
				TokenRules.value("if", false),
				TokenRules.value("(", false),
				TokenRules.pattern("\\w+"),
				TokenRules.value(")", false)
			),
			LookMatchMode.POSITIVE
		);
		List<Token> tokens = List.of(
			SimpleToken.createUnpositioned("prefix"),
			SimpleToken.createUnpositioned("if"),
			SimpleToken.createUnpositioned("("),
			SimpleToken.createUnpositioned("condition"),
			SimpleToken.createUnpositioned(")"),
			SimpleToken.createUnpositioned("statement")
		);
		
		TokenStream stream = TokenStream.createMutable(tokens);
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenRule negated = rule.not();
		
		LookaheadTokenRule negatedLookahead = assertInstanceOf(LookaheadTokenRule.class, negated);
		assertEquals(innerRule, negatedLookahead.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negatedLookahead.mode());
	}
	
	@Test
	void notWithNegativeMode() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negated = rule.not();
		
		LookaheadTokenRule negatedLookahead = assertInstanceOf(LookaheadTokenRule.class, negated);
		assertEquals(innerRule, negatedLookahead.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, negatedLookahead.mode());
	}
	
	@Test
	void toStringTest() {
		TokenRule tokenRule = createRule("test");
		LookaheadTokenRule rule = new LookaheadTokenRule(tokenRule, LookMatchMode.POSITIVE);
		
		String result = rule.toString();
		
		assertTrue(result.contains("LookaheadTokenRule"));
		assertTrue(result.contains("tokenRule"));
		assertTrue(result.contains("mode"));
	}
}
