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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroupRule}.<br>
 *
 * @author Luis-St
 */
class TokenGroupRuleTest {
	
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
		assertThrows(NullPointerException.class, () -> new TokenGroupRule(null));
	}
	
	@Test
	void constructorWithValidRule() {
		TokenRule innerRule = createRule("test");
		
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		assertEquals(innerRule, rule.tokenRule());
	}
	
	@Test
	void matchWithNullStream() {
		TokenRule innerRule = createRule("test");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		TokenRule innerRule = createRule("test");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		TokenRule innerRule = createRule("test");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNonTokenGroup() {
		TokenRule innerRule = createRule("test");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithTokenGroup() {
		TokenRule innerRule = createRule("inner");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(createToken("inner"), createToken("other")));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(group, result.matchedTokens().getFirst());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithTokenGroupNoInnerMatch() {
		TokenRule innerRule = createRule("notfound");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(createToken("inner"), createToken("other")));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void not() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertInstanceOf(TokenGroupRule.class, negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void notWithAlwaysMatchRule() {
		TokenGroupRule rule = new TokenGroupRule(AlwaysMatchTokenRule.INSTANCE);
		
		TokenRule negated = rule.not();
		
		TokenGroupRule negatedGroup = assertInstanceOf(TokenGroupRule.class, negated);
		assertEquals(NeverMatchTokenRule.INSTANCE, negatedGroup.tokenRule());
	}
	
	@Test
	void toStringTest() {
		TokenRule innerRule = createRule("test");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		String result = rule.toString();
		
		assertTrue(result.contains("TokenGroupRule"));
		assertTrue(result.contains("tokenRule"));
	}
}
