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

class StartTokenRuleTest {
	
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
	void documentMatchAtStartOfStream() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void documentMatchAtIndexZeroEmptyStream() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
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
	void documentNoMatchAtNonZeroIndex() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		stream.advance(); // Move to index 1
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void documentMatchWithNullStream() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void documentMatchWithNullContext() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void lineMatchAtStartOfStream() {
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(0, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineMatchWithNewlineInPreviousToken() {
		Token previousToken = createToken("text\n");
		Token currentToken = createToken("current");
		
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(previousToken, currentToken));
		stream.advance(); // Move to current token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineMatchWithPositionedTokensOnDifferentLines() {
		Token previousToken = createTokenWithPosition("previous", 1, 5);
		Token currentToken = createTokenWithPosition("current", 2, 1);
		
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(previousToken, currentToken));
		stream.advance(); // Move to current token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
		assertEquals(rule, result.matchingTokenRule());
	}
	
	@Test
	void lineNoMatchWithPositionedTokensOnSameLine() {
		Token previousToken = createTokenWithPosition("previous", 1, 5);
		Token currentToken = createTokenWithPosition("current", 1, 8);
		
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(previousToken, currentToken));
		stream.advance(); // Move to current token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void lineNoMatchWithoutNewlineAndSameLine() {
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of(createToken("first"), createToken("second")));
		stream.advance(); // Move to second token
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = rule.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void lineMatchWithEmptyStream() {
		StartTokenRule rule = StartTokenRule.LINE;
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
	void lineMatchWithNullStream() {
		StartTokenRule rule = StartTokenRule.LINE;
		TokenRuleContext context = TokenRuleContext.empty();
		
		assertThrows(NullPointerException.class, () -> rule.match(null, context));
	}
	
	@Test
	void lineMatchWithNullContext() {
		StartTokenRule rule = StartTokenRule.LINE;
		TokenStream stream = TokenStream.createMutable(List.of());
		
		assertThrows(NullPointerException.class, () -> rule.match(stream, null));
	}
	
	@Test
	void documentNot() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void documentNotDoubleNegation() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void lineNot() {
		StartTokenRule rule = StartTokenRule.LINE;
		
		TokenRule negated = rule.not();
		
		assertNotNull(negated);
		assertNotEquals(rule, negated);
	}
	
	@Test
	void lineNotDoubleNegation() {
		StartTokenRule rule = StartTokenRule.LINE;
		
		TokenRule doubleNegated = rule.not().not();
		
		assertEquals(rule, doubleNegated);
	}
	
	@Test
	void negatedRuleMatchWhenOriginalDoesNot() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		stream.advance(); // Move to non-zero index
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(1, result.endIndex());
		assertTrue(result.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedRuleNoMatchWhenOriginalMatches() {
		StartTokenRule rule = StartTokenRule.DOCUMENT;
		TokenRule negated = rule.not();
		
		TokenStream stream = TokenStream.createMutable(List.of(createToken("test")));
		TokenRuleContext context = TokenRuleContext.empty();
		
		TokenRuleMatch result = negated.match(stream, context);
		
		assertNull(result);
	}
	
	@Test
	void enumValues() {
		StartTokenRule[] values = StartTokenRule.values();
		
		assertEquals(2, values.length);
		assertEquals(StartTokenRule.DOCUMENT, values[0]);
		assertEquals(StartTokenRule.LINE, values[1]);
	}
	
	@Test
	void enumValueOf() {
		assertEquals(StartTokenRule.DOCUMENT, StartTokenRule.valueOf("DOCUMENT"));
		assertEquals(StartTokenRule.LINE, StartTokenRule.valueOf("LINE"));
	}
	
	@Test
	void enumValueOfWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> StartTokenRule.valueOf("INVALID"));
	}
	
	@Test
	void enumValueOfWithNull() {
		assertThrows(NullPointerException.class, () -> StartTokenRule.valueOf(null));
	}
	
	@Test
	void toStringTest() {
		assertEquals("DOCUMENT", StartTokenRule.DOCUMENT.toString());
		assertEquals("LINE", StartTokenRule.LINE.toString());
	}
}
