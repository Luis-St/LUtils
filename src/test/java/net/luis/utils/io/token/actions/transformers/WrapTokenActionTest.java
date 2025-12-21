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

package net.luis.utils.io.token.actions.transformers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WrapTokenAction}.<br>
 *
 * @author Luis-St
 */
class WrapTokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullPrefixToken() {
		Token suffix = createToken(")");
		
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(null, suffix));
	}
	
	@Test
	void constructorWithNullSuffixToken() {
		Token prefix = createToken("(");
		
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(prefix, null));
	}
	
	@Test
	void constructorWithValidTokens() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		assertEquals(prefix, action.prefixToken());
		assertEquals(suffix, action.suffixToken());
	}
	
	@Test
	void applyWithNullMatch() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithSingleToken() {
		Token prefix = createToken("[");
		Token suffix = createToken("]");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("content"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("[", result.get(0).value());
		assertEquals("content", result.get(1).value());
		assertEquals("]", result.get(2).value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		Token prefix = createToken("{");
		Token suffix = createToken("}");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(5, result.size());
		assertEquals("{", result.get(0).value());
		assertEquals("first", result.get(1).value());
		assertEquals("second", result.get(2).value());
		assertEquals("third", result.get(3).value());
		assertEquals("}", result.get(4).value());
	}
	
	@Test
	void applyWithEmptyTokens() {
		Token prefix = createToken("<<");
		Token suffix = createToken(">>");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyMaintainsTokenOrder() {
		Token prefix = createToken("start");
		Token suffix = createToken("end");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("alpha"), createToken("beta"), createToken("gamma"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(5, result.size());
		assertEquals("start", result.get(0).value());
		assertEquals("alpha", result.get(1).value());
		assertEquals("beta", result.get(2).value());
		assertEquals("gamma", result.get(3).value());
		assertEquals("end", result.get(4).value());
	}
	
	@Test
	void applyWithSamePrefixAndSuffix() {
		Token quote = createToken("\"");
		WrapTokenAction action = new WrapTokenAction(quote, quote);
		
		List<Token> tokens = List.of(createToken("quoted text"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("\"", result.get(0).value());
		assertEquals("quoted text", result.get(1).value());
		assertEquals("\"", result.get(2).value());
		assertSame(result.get(0), result.get(2));
	}
	
	@Test
	void applyWithComplexTokens() {
		Token openTag = createToken("<tag>");
		Token closeTag = createToken("</tag>");
		WrapTokenAction action = new WrapTokenAction(openTag, closeTag);
		
		List<Token> tokens = List.of(createToken("element"), createToken("content"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(4, result.size());
		assertEquals("<tag>", result.get(0).value());
		assertEquals("element", result.get(1).value());
		assertEquals("content", result.get(2).value());
		assertEquals("</tag>", result.get(3).value());
	}
	
	@Test
	void applyPreservesOriginalTokens() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		Token originalToken = createToken("preserve");
		List<Token> tokens = List.of(originalToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertSame(originalToken, result.get(1));
		assertEquals("(", result.get(0).value());
		assertEquals("preserve", result.get(1).value());
		assertEquals(")", result.get(2).value());
	}
	
	@Test
	void applyReturnsImmutableList() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyWithEmptyValueTokens() {
		Token prefix = createToken("");
		Token suffix = createToken("");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("content"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("", result.get(0).value());
		assertEquals("content", result.get(1).value());
		assertEquals("", result.get(2).value());
	}
	
	@Test
	void applyWithWhitespaceTokens() {
		Token prefix = createToken(" ");
		Token suffix = createToken(" ");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		List<Token> tokens = List.of(createToken("word"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals(" ", result.get(0).value());
		assertEquals("word", result.get(1).value());
		assertEquals(" ", result.get(2).value());
	}
	
	@Test
	void toStringTest() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("WrapTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		Token prefix1 = createToken("(");
		Token suffix1 = createToken(")");
		Token prefix2 = createToken("(");
		Token suffix2 = createToken(")");
		Token prefix3 = createToken("[");
		Token suffix3 = createToken("]");
		
		WrapTokenAction action1 = new WrapTokenAction(prefix1, suffix1);
		WrapTokenAction action2 = new WrapTokenAction(prefix2, suffix2);
		WrapTokenAction action3 = new WrapTokenAction(prefix3, suffix3);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
