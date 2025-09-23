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

/**
 * Test class for {@link LookbehindTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookbehindTokenRuleTest {
	
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
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(null, LookMatchMode.POSITIVE));
	}
	
	@Test
	void constructorWithNullMode() {
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(createRule("test"), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenRule tokenRule = createRule("test");
		LookMatchMode mode = LookMatchMode.POSITIVE;
		
		LookbehindTokenRule rule = new LookbehindTokenRule(tokenRule, mode);
		
		assertEquals(tokenRule, rule.tokenRule());
		assertEquals(mode, rule.mode());
	}
	
	@Test
	void matchWithNullStream() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchAtBeginningOfStream() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("current")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchPositiveWithMatchingRuleInLookbehind() {
		TokenRule innerRule = createRule("previous");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("previous"), createToken("current")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchPositiveWithNonMatchingRuleInLookbehind() {
		TokenRule innerRule = createRule("expected");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual"), createToken("current")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchNegativeWithMatchingRuleInLookbehind() {
		TokenRule innerRule = createRule("previous");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("previous"), createToken("current")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchNegativeWithNonMatchingRuleInLookbehind() {
		TokenRule innerRule = createRule("expected");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual"), createToken("current")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		LookbehindTokenRule rule = new LookbehindTokenRule(TokenRules.alwaysMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		LookbehindTokenRule rule = new LookbehindTokenRule(TokenRules.neverMatch(), LookMatchMode.POSITIVE);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		stream.advance();
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void not() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		TokenRule negated = rule.not();
		
		LookbehindTokenRule negatedLookbehind = assertInstanceOf(LookbehindTokenRule.class, negated);
		assertEquals(innerRule, negatedLookbehind.tokenRule());
		assertEquals(LookMatchMode.NEGATIVE, negatedLookbehind.mode());
	}
	
	@Test
	void notWithNegativeMode() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negated = rule.not();
		
		LookbehindTokenRule negatedLookbehind = assertInstanceOf(LookbehindTokenRule.class, negated);
		assertEquals(innerRule, negatedLookbehind.tokenRule());
		assertEquals(LookMatchMode.POSITIVE, negatedLookbehind.mode());
	}
	
	@Test
	void toStringTest() {
		TokenRule tokenRule = createRule("test");
		LookbehindTokenRule rule = new LookbehindTokenRule(tokenRule, LookMatchMode.POSITIVE);
		
		String result = rule.toString();
		
		assertTrue(result.contains("LookbehindTokenRule"));
		assertTrue(result.contains("tokenRule"));
		assertTrue(result.contains("mode"));
	}
}
