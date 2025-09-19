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

package net.luis.utils.io.token.rules.assertions.anchors;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class EndTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NotNull Token createTokenWithPosition(@NotNull String value, int line, int column) {
		return new SimpleToken(value, new TokenPosition(line, column, 0));
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
	void documentMatchWithNullStream() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void documentMatchWithNullContext() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void documentMatchAtEndOfStream() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void documentMatchAfterLastToken() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		stream.advance(); // Move past last token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void documentNoMatchWithTokensAvailable() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void lineMatchWithNullStream() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void lineMatchWithNullContext() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void lineMatchAtEndOfStream() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineMatchAtLastToken() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("text")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result); // Can't determine if it's end of line with only one token
	}
	
	@Test
	void lineMatchWithNewlineInCurrentToken() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("text\n")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineMatchWithPositionedTokensOnDifferentLines() {
		Token currentToken = createTokenWithPosition("current", 1, 5);
		Token nextToken = createTokenWithPosition("next", 2, 1);
		
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(currentToken, nextToken));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineMatchWithPositionedTokensOnSameLine() {
		Token currentToken = createTokenWithPosition("current", 1, 5);
		Token nextToken = createTokenWithPosition("next", 1, 8);
		
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(currentToken, nextToken));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void lineNoMatchWithoutNewlineAndSameLine() {
		EndTokenRule rule = EndTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("text"), createToken("more")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void documentNot() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void documentNotDoubleNegation() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void lineNot() {
		EndTokenRule rule = EndTokenRule.LINE;
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void lineNotDoubleNegation() {
		EndTokenRule rule = EndTokenRule.LINE;
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void negatedRuleMatchWhenOriginalDoesNot() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedRuleNoMatchWhenOriginalMatches() {
		EndTokenRule rule = EndTokenRule.DOCUMENT;
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of());
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void enumValues() {
		EndTokenRule[] values = EndTokenRule.values();
		
		assertEquals(2, values.length);
		assertEquals(EndTokenRule.DOCUMENT, values[0]);
		assertEquals(EndTokenRule.LINE, values[1]);
	}
	
	@Test
	void enumValueOf() {
		assertEquals(EndTokenRule.DOCUMENT, EndTokenRule.valueOf("DOCUMENT"));
		assertEquals(EndTokenRule.LINE, EndTokenRule.valueOf("LINE"));
	}
	
	@Test
	void enumValueOfWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> EndTokenRule.valueOf("INVALID"));
	}
	
	@Test
	void enumValueOfWithNull() {
		assertThrows(NullPointerException.class, () -> EndTokenRule.valueOf(null));
	}
	
	@Test
	void toStringTest() {
		assertEquals("DOCUMENT", EndTokenRule.DOCUMENT.toString());
		assertEquals("LINE", EndTokenRule.LINE.toString());
	}
}
