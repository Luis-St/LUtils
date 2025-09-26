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

package net.luis.utils.io.token.rules.quantifiers;

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

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
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
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void constructorWithValidRule() {
		TokenRule innerRule = createRule("test");
		
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		assertEquals(innerRule, rule.tokenRule());
	}
	
	@Test
	void matchWithNullStream() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithInnerRuleMatching() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals("test", result.matchedTokens().getFirst().value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithInnerRuleNotMatching() {
		TokenRule innerRule = createRule("expected");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
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
	void matchWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
		assertEquals("any", result.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.neverMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchAtStreamEnd() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNegativeIndex() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		assertNotNull(result);
	}
	
	
	
	@Test
	void not() {
		TokenRule innerRule = TokenRules.pattern("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		TokenRule negated = rule.not();
		
		OptionalTokenRule negatedOptional = assertInstanceOf(OptionalTokenRule.class, negated);
		assertNotEquals(innerRule, negatedOptional.tokenRule());
	}
	
	@Test
	void notBehavior() {
		TokenRule innerRule = TokenRules.pattern("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		TokenRule negated = rule.not();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("test")));
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("other")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(rule.match(stream1, context));
		assertNotNull(negated.match(stream2, context));
	}
	
	@Test
	void notWithAlwaysMatchRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.alwaysMatch());
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(OptionalTokenRule.class, negated);
		OptionalTokenRule negatedOptional = (OptionalTokenRule) negated;
		assertEquals(TokenRules.neverMatch().getClass(), negatedOptional.tokenRule().getClass());
	}
	
	@Test
	void toStringTest() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule = new OptionalTokenRule(innerRule);
		
		String result = rule.toString();
		
		assertTrue(result.contains("OptionalTokenRule"));
		assertTrue(result.contains("tokenRule"));
	}
}
