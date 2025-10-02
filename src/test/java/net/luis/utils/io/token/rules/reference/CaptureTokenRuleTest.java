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

package net.luis.utils.io.token.rules.reference;

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
 * Test class for {@link CaptureTokenRule}.<br>
 *
 * @author Luis-St
 */
class CaptureTokenRuleTest {
	
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
	void constructorWithNullKey() {
		assertThrows(NullPointerException.class, () -> new CaptureTokenRule(null, createRule("test")));
	}
	
	@Test
	void constructorWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> new CaptureTokenRule("", createRule("test")));
	}
	
	@Test
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new CaptureTokenRule("key", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		String key = "testKey";
		TokenRule tokenRule = createRule("test");
		
		CaptureTokenRule rule = new CaptureTokenRule(key, tokenRule);
		
		assertEquals(key, rule.key());
		assertEquals(tokenRule, rule.tokenRule());
	}
	
	@Test
	void matchWithNullStream() {
		CaptureTokenRule rule = new CaptureTokenRule("key", createRule("test"));
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		CaptureTokenRule rule = new CaptureTokenRule("key", createRule("test"));
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void matchWithEmptyStream() {
		CaptureTokenRule rule = new CaptureTokenRule("key", createRule("test"));
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		assertNull(result);
		assertNull(context.getCapturedTokens("key"));
	}
	
	@Test
	void matchWithSuccessfulInnerMatch() {
		String key = "captured";
		TokenRule innerRule = createRule("test");
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals("test", result.matchedTokens().getFirst().value());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertEquals(1, capturedTokens.size());
		assertEquals("test", capturedTokens.getFirst().value());
	}
	
	@Test
	void matchWithFailedInnerMatch() {
		String key = "captured";
		TokenRule innerRule = createRule("expected");
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNull(capturedTokens);
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		String key = "always";
		CaptureTokenRule rule = new CaptureTokenRule(key, TokenRules.alwaysMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("any", result.matchedTokens().getFirst().value());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertEquals("any", capturedTokens.getFirst().value());
	}
	
	@Test
	void matchWithNeverMatchRule() {
		String key = "never";
		CaptureTokenRule rule = new CaptureTokenRule(key, TokenRules.neverMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("any")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertNull(context.getCapturedTokens(key));
	}
	
	@Test
	void matchWithMultipleTokens() {
		String key = "multiCapture";
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("second"));
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertEquals(2, capturedTokens.size());
		assertEquals("first", capturedTokens.get(0).value());
		assertEquals("second", capturedTokens.get(1).value());
	}
	
	@Test
	void matchWithPartialTokenSequence() {
		String key = "partial";
		TokenRule innerRule = TokenRules.sequence(createRule("first"), createRule("second"));
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("wrong")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertNull(context.getCapturedTokens(key));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithOptionalRule() {
		String key = "optional";
		TokenRule innerRule = TokenRules.optional(createRule("test"));
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("different")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertTrue(result.matchedTokens().isEmpty());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertTrue(capturedTokens.isEmpty());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithStreamAdvancement() {
		String key = "advance";
		TokenRule innerRule = createRule("match");
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("match"), createToken("remaining")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, stream.getCurrentIndex());
		assertTrue(stream.hasMoreTokens());
		assertEquals("remaining", stream.getCurrentToken().value());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertEquals(1, capturedTokens.size());
		assertEquals("match", capturedTokens.getFirst().value());
	}
	
	@Test
	void matchOverwritesPreviousCapture() {
		String key = "overwrite";
		TokenRule innerRule1 = createRule("first");
		TokenRule innerRule2 = createRule("second");
		CaptureTokenRule rule1 = new CaptureTokenRule(key, innerRule1);
		CaptureTokenRule rule2 = new CaptureTokenRule(key, innerRule2);
		
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenStream stream1 = TokenStream.createMutable(List.of(createToken("first")));
		rule1.match(stream1, context);
		
		TokenStream stream2 = TokenStream.createMutable(List.of(createToken("second")));
		rule2.match(stream2, context);
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertEquals(1, capturedTokens.size());
		assertEquals("second", capturedTokens.getFirst().value());
	}
	
	@Test
	void matchWithEmptyTokenList() {
		String key = "empty";
		TokenRule innerRule = TokenRules.endDocument();
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertTrue(result.matchedTokens().isEmpty());
		
		List<Token> capturedTokens = context.getCapturedTokens(key);
		assertNotNull(capturedTokens);
		assertTrue(capturedTokens.isEmpty());
	}
	
	@Test
	void not() {
		String key = "test";
		TokenRule innerRule = TokenRules.pattern("test");
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(CaptureTokenRule.class, negated);
		CaptureTokenRule negatedCapture = (CaptureTokenRule) negated;
		assertEquals(key, negatedCapture.key());
		assertNotEquals(innerRule, negatedCapture.tokenRule());
	}
	
	@Test
	void notWithAlwaysMatchRule() {
		String key = "test";
		CaptureTokenRule rule = new CaptureTokenRule(key, TokenRules.alwaysMatch());
		
		TokenRule negated = rule.not();
		
		assertInstanceOf(CaptureTokenRule.class, negated);
		CaptureTokenRule negatedCapture = (CaptureTokenRule) negated;
		assertEquals(TokenRules.neverMatch().getClass(), negatedCapture.tokenRule().getClass());
	}
	
	@Test
	void toStringTest() {
		String key = "testKey";
		TokenRule innerRule = createRule("test");
		CaptureTokenRule rule = new CaptureTokenRule(key, innerRule);
		
		String result = rule.toString();
		
		assertTrue(result.contains("CaptureTokenRule"));
		assertTrue(result.contains("key"));
		assertTrue(result.contains("tokenRule"));
	}
}
