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

package net.luis.utils.io.token.context;

import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.rules.NeverMatchTokenRule;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleContext}.<br>
 *
 * @author Luis-St
 */
class TokenRuleContextTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void empty() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNotNull(context);
		assertNull(context.getRuleReference("nonexistent"));
		assertNull(context.getCapturedTokens("nonexistent"));
	}
	
	@Test
	void defineRuleWithNullKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> context.defineRule(null, rule));
	}
	
	@Test
	void defineRuleWithNullRule() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> context.defineRule("key", null));
	}
	
	@Test
	void defineRuleWithValidParameters() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertDoesNotThrow(() -> context.defineRule("testRule", rule));
		assertEquals(rule, context.getRuleReference("testRule"));
	}
	
	@Test
	void defineRuleOverwriteExisting() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRule rule1 = AlwaysMatchTokenRule.INSTANCE;
		TokenRule rule2 = NeverMatchTokenRule.INSTANCE;
		
		context.defineRule("testRule", rule1);
		context.defineRule("testRule", rule2);
		
		assertEquals(rule2, context.getRuleReference("testRule"));
	}
	
	@Test
	void defineRuleWithEmptyKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertDoesNotThrow(() -> context.defineRule("", rule));
		assertEquals(rule, context.getRuleReference(""));
	}
	
	@Test
	void getRuleReferenceWithNullKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> context.getRuleReference(null));
	}
	
	@Test
	void getRuleReferenceWithNonExistentKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNull(context.getRuleReference("nonexistent"));
	}
	
	@Test
	void getRuleReferenceWithExistingKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		context.defineRule("testRule", rule);
		
		assertEquals(rule, context.getRuleReference("testRule"));
	}
	
	@Test
	void getRuleReferenceWithEmptyKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNull(context.getRuleReference(""));
	}
	
	@Test
	void captureTokensWithNullKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(NullPointerException.class, () -> context.captureTokens(null, tokens));
	}
	
	@Test
	void captureTokensWithNullTokens() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> context.captureTokens("key", null));
	}
	
	@Test
	void captureTokensWithValidParameters() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		assertDoesNotThrow(() -> context.captureTokens("testTokens", tokens));
		assertEquals(tokens, context.getCapturedTokens("testTokens"));
	}
	
	@Test
	void captureTokensOverwriteExisting() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> tokens1 = List.of(createToken("first"));
		List<Token> tokens2 = List.of(createToken("second"));
		
		context.captureTokens("testTokens", tokens1);
		context.captureTokens("testTokens", tokens2);
		
		assertEquals(tokens2, context.getCapturedTokens("testTokens"));
	}
	
	@Test
	void captureTokensWithEmptyList() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> emptyTokens = List.of();
		
		assertDoesNotThrow(() -> context.captureTokens("emptyTokens", emptyTokens));
		assertEquals(emptyTokens, context.getCapturedTokens("emptyTokens"));
	}
	
	@Test
	void captureTokensWithMutableList() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> mutableTokens = new ArrayList<>();
		mutableTokens.add(createToken("test"));
		
		assertDoesNotThrow(() -> context.captureTokens("mutableTokens", mutableTokens));
		
		List<Token> stored = context.getCapturedTokens("mutableTokens");
		assertEquals(mutableTokens, stored);
		
		mutableTokens.add(createToken("modified"));
		assertNotNull(stored);
		assertEquals(1, stored.size());
	}
	
	@Test
	void captureTokensWithEmptyKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> tokens = List.of(createToken("test"));
		
		assertDoesNotThrow(() -> context.captureTokens("", tokens));
		assertEquals(tokens, context.getCapturedTokens(""));
	}
	
	@Test
	void getCapturedTokensWithNullKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> context.getCapturedTokens(null));
	}
	
	@Test
	void getCapturedTokensWithNonExistentKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNull(context.getCapturedTokens("nonexistent"));
	}
	
	@Test
	void getCapturedTokensWithExistingKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		context.captureTokens("testTokens", tokens);
		
		assertEquals(tokens, context.getCapturedTokens("testTokens"));
	}
	
	@Test
	void getCapturedTokensWithEmptyKey() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertNull(context.getCapturedTokens(""));
	}
	
	@Test
	void multipleRulesAndTokens() {
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRule rule1 = AlwaysMatchTokenRule.INSTANCE;
		TokenRule rule2 = NeverMatchTokenRule.INSTANCE;
		List<Token> tokens1 = List.of(createToken("first"));
		List<Token> tokens2 = List.of(createToken("second"), createToken("third"));
		
		context.defineRule("rule1", rule1);
		context.defineRule("rule2", rule2);
		context.captureTokens("tokens1", tokens1);
		context.captureTokens("tokens2", tokens2);
		
		assertEquals(rule1, context.getRuleReference("rule1"));
		assertEquals(rule2, context.getRuleReference("rule2"));
		assertEquals(tokens1, context.getCapturedTokens("tokens1"));
		assertEquals(tokens2, context.getCapturedTokens("tokens2"));
		
		assertNull(context.getRuleReference("nonexistent"));
		assertNull(context.getCapturedTokens("nonexistent"));
	}
}
