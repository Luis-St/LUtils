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

class ReferenceTokenRuleTest {
	
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
	void constructorWithValidParameters() {
		String key = "testKey";
		ReferenceType type = ReferenceType.RULE;
		
		ReferenceTokenRule rule = new ReferenceTokenRule(key, type);
		
		assertEquals(key, rule.key());
		assertEquals(type, rule.type());
	}
	
	@Test
	void constructorWithNullKey() {
		assertThrows(NullPointerException.class, () -> new ReferenceTokenRule(null, ReferenceType.RULE));
	}
	
	@Test
	void constructorWithEmptyKey() {
		assertThrows(IllegalArgumentException.class, () -> new ReferenceTokenRule("", ReferenceType.RULE));
	}
	
	@Test
	void constructorWithNullType() {
		assertThrows(NullPointerException.class, () -> new ReferenceTokenRule("key", null));
	}
	
	@Test
	void matchWithRuleReferenceFound() {
		String key = "testRule";
		TokenRule referencedRule = createRule("test");
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.RULE);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.defineRule(key, referencedRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().get(0).value());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithRuleReferenceNotFound() {
		String key = "nonExistentRule";
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.RULE);
		
		TokenRuleContext context = TokenRuleContext.empty();
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithTokensReferenceFound() {
		String key = "testTokens";
		List<Token> referencedTokens = List.of(createToken("first"), createToken("second"));
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.TOKENS);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.captureTokens(key, referencedTokens);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(2, result.matchedTokens().size());
		assertEquals("first", result.matchedTokens().get(0).value());
		assertEquals("second", result.matchedTokens().get(1).value());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithTokensReferenceNotFound() {
		String key = "nonExistentTokens";
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.TOKENS);
		
		TokenRuleContext context = TokenRuleContext.empty();
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithTokensReferenceMismatch() {
		String key = "testTokens";
		List<Token> referencedTokens = List.of(createToken("expected"));
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.TOKENS);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.captureTokens(key, referencedTokens);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("actual")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithDynamicReferenceRuleOnly() {
		String key = "dynamicKey";
		TokenRule referencedRule = createRule("test");
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.DYNAMIC);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.defineRule(key, referencedRule);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithDynamicReferenceTokensOnly() {
		String key = "dynamicKey";
		List<Token> referencedTokens = List.of(createToken("test"));
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.DYNAMIC);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.captureTokens(key, referencedTokens);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals("test", result.matchedTokens().get(0).value());
	}
	
	@Test
	void matchWithDynamicReferenceBothPresent() {
		String key = "dynamicKey";
		TokenRule referencedRule = createRule("rule");
		List<Token> referencedTokens = List.of(createToken("tokens"));
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.DYNAMIC);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.defineRule(key, referencedRule);
		context.captureTokens(key, referencedTokens);
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result); // Should not match when both are present
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithDynamicReferenceNeitherPresent() {
		String key = "dynamicKey";
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.DYNAMIC);
		
		TokenRuleContext context = TokenRuleContext.empty();
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithEmptyStream() {
		String key = "testKey";
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.TOKENS);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.captureTokens(key, List.of(createToken("test")));
		
		TokenStream stream = TokenStream.createMutable(List.of());
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void matchWithRuleReferenceReturningNull() {
		String key = "neverMatchRule";
		ReferenceTokenRule rule = new ReferenceTokenRule(key, ReferenceType.RULE);
		
		TokenRuleContext context = TokenRuleContext.empty();
		context.defineRule(key, TokenRules.neverMatch());
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
		assertEquals(0, stream.getCurrentIndex()); // Stream not advanced
	}
	
	@Test
	void matchWithNullStream() {
		ReferenceTokenRule rule = new ReferenceTokenRule("key", ReferenceType.RULE);
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void matchWithNullContext() {
		ReferenceTokenRule rule = new ReferenceTokenRule("key", ReferenceType.RULE);
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void equalsAndHashCode() {
		String key1 = "key1";
		String key2 = "key2";
		ReferenceType type1 = ReferenceType.RULE;
		ReferenceType type2 = ReferenceType.TOKENS;
		
		ReferenceTokenRule rule1 = new ReferenceTokenRule(key1, type1);
		ReferenceTokenRule rule2 = new ReferenceTokenRule(key1, type1);
		ReferenceTokenRule rule3 = new ReferenceTokenRule(key2, type1);
		ReferenceTokenRule rule4 = new ReferenceTokenRule(key1, type2);
		
		assertEquals(rule1, rule2);
		assertNotEquals(rule1, rule3); // Different key
		assertNotEquals(rule1, rule4); // Different type
		assertNotEquals(rule1, null);
		assertNotEquals(rule1, "string");
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringTest() {
		String key = "testKey";
		ReferenceType type = ReferenceType.RULE;
		ReferenceTokenRule rule = new ReferenceTokenRule(key, type);
		
		String result = rule.toString();
		
		assertTrue(result.contains("ReferenceTokenRule"));
		assertTrue(result.contains("key"));
		assertTrue(result.contains("type"));
	}
}
