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
import net.luis.utils.io.token.tokens.*;
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
	void matchWithComplexInnerRule() {
		TokenRule innerRule = TokenRules.sequence(
			createRule("first"),
			createRule("second"),
			createRule("third")
		);
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("first"),
			createToken("second"),
			createToken("third")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group, result.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithOptionalInnerRule() {
		TokenRule innerRule = TokenRules.optional(createRule("optional"));
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("different"), createToken("different")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group, result.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithStreamAdvancement() {
		TokenRule innerRule = createRule("match");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("match"), createToken("match")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group, createToken("remaining")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group, result.matchedTokens().getFirst());
		assertEquals(1, stream.getCurrentIndex());
		assertTrue(stream.hasMoreTokens());
		assertEquals("remaining", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithStreamNoAdvancementOnFailure() {
		TokenRule innerRule = createRule("expected");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("actual"), createToken("actual")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group, createToken("remaining")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex());
		assertEquals(group, stream.getCurrentToken());
	}
	
	@Test
	void matchWithAlwaysMatchInnerRule() {
		TokenGroupRule rule = new TokenGroupRule(TokenRules.alwaysMatch());
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("any"), createToken("any")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group, result.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithNeverMatchInnerRule() {
		TokenGroupRule rule = new TokenGroupRule(TokenRules.neverMatch());
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("any"), createToken("any")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNestedGroups() {
		TokenGroup innerGroup = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("inner1"),
			SimpleToken.createUnpositioned("inner2")
		));
		TokenGroup outerGroup = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("outer"),
			innerGroup
		));
		TokenGroupRule rule = new TokenGroupRule(
			TokenRules.sequence(
				TokenRules.value("outer", false),
				TokenRules.value("inner1inner2", false)
			)
		);
		
		TokenStream stream = TokenStream.createMutable(List.of(outerGroup));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
	}
	
	@Test
	void matchWithShadowTokensInGroup() {
		TokenGroup group = new TokenGroup(List.of(
			SimpleToken.createUnpositioned("visible"),
			new ShadowToken(SimpleToken.createUnpositioned("hidden")),
			SimpleToken.createUnpositioned("also_visible")
		));
		TokenGroupRule rule = new TokenGroupRule(
			TokenRules.sequence(
				TokenRules.value("visible", false),
				TokenRules.value("also_visible", false)
			)
		);
		
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.matchedTokens().size());
	}
	
	@Test
	void matchWithMultipleGroupsInStream() {
		TokenRule innerRule = createRule("first");
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group1 = new TokenGroup(List.of(
			createToken("first"), createToken("first")
		));
		TokenGroup group2 = new TokenGroup(List.of(
			createToken("second"), createToken("second")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group1, group2));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group1, result.matchedTokens().getFirst());
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(group2, stream.getCurrentToken());
	}
	
	@Test
	void matchWithRepeatedInnerRule() {
		TokenRule innerRule = TokenRules.atLeast(createRule("repeat"), 2);
		TokenGroupRule rule = new TokenGroupRule(innerRule);
		
		TokenGroup group = new TokenGroup(List.of(
			createToken("repeat"),
			createToken("repeat"),
			createToken("repeat")
		));
		TokenStream stream = TokenStream.createMutable(List.of(group));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(group, result.matchedTokens().getFirst());
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
