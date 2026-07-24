/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.grammar.lexer;

import net.luis.utils.grammar.lexer.rule.CharRule;
import net.luis.utils.grammar.lexer.rule.CharRules;
import net.luis.utils.grammar.token.*;
import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LexerRule}.<br>
 *
 * @author Luis-St
 */
class LexerRuleTest {
	
	private static final TokenPosition POSITION = new TokenPosition(0, 0, 0);
	
	@Test
	void constructWithValidValues() {
		CharRule pattern = CharRules.literal('a');
		LexerRule rule = new LexerRule("NAME", pattern, StandardTokenType.IDENTIFIER, false);
		assertEquals("NAME", rule.name());
		assertSame(pattern, rule.pattern());
		assertEquals(StandardTokenType.IDENTIFIER, rule.type());
		assertFalse(rule.shadow());
	}
	
	@Test
	void constructWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> new LexerRule(null, CharRules.literal('a'), StandardTokenType.IDENTIFIER, false));
	}
	
	@Test
	void constructWithNullPatternThrows() {
		assertThrows(NullPointerException.class, () -> new LexerRule("NAME", null, StandardTokenType.IDENTIFIER, false));
	}
	
	@Test
	void constructWithNullTokenTypeThrows() {
		assertThrows(NullPointerException.class, () -> new LexerRule("NAME", CharRules.literal('a'), null, false));
	}
	
	@Test
	void constructWithEmptyNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new LexerRule("", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false));
	}
	
	@Test
	void createTokenWithNullMatchedThrows() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		assertThrows(NullPointerException.class, () -> rule.createToken(null, POSITION));
	}
	
	@Test
	void createTokenWithNullPositionThrows() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		assertThrows(NullPointerException.class, () -> rule.createToken("a", null));
	}
	
	@Test
	void constructWithNonEmptyNameSucceeds() {
		LexerRule rule = new LexerRule("OTHER_NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		assertEquals("OTHER_NAME", rule.name());
	}
	
	@Test
	void createTokenWithEscapeSequenceCreatesEscapedToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("\\n", POSITION);
		assertInstanceOf(EscapedToken.class, token);
	}
	
	@Test
	void createTokenWithNonEscapeTextCreatesSimpleToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("abc", POSITION);
		assertInstanceOf(SimpleToken.class, token);
	}
	
	@Test
	void createTokenWithTwoCharTextNotStartingWithBackslashCreatesSimpleToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("ab", POSITION);
		assertInstanceOf(SimpleToken.class, token);
	}
	
	@Test
	void createTokenForNonShadowRuleReturnsUnshadowedToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("a", POSITION);
		assertFalse(token instanceof ShadowToken);
	}
	
	@Test
	void createTokenForShadowRuleReturnsShadowedToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, true);
		Token token = rule.createToken("a", POSITION);
		assertInstanceOf(ShadowToken.class, token);
	}
	
	@Test
	void createTokenAppliesConfiguredTokenType() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.KEYWORD, false);
		Token token = rule.createToken("a", POSITION);
		assertTrue(token.types().contains(StandardTokenType.KEYWORD));
	}
	
	@Test
	void createTokenWithSingleCharacterText() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("a", POSITION);
		assertInstanceOf(SimpleToken.class, token);
		assertEquals("a", token.value());
	}
	
	@Test
	void createTokenWithEmptyMatchedTextCreatesSimpleToken() {
		LexerRule rule = new LexerRule("NAME", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false);
		Token token = rule.createToken("", POSITION);
		assertInstanceOf(SimpleToken.class, token);
		assertEquals("", token.value());
	}
	
	@Test
	void createTokenEscapeSequenceCarriesCorrectTypeAndShadowFlag() {
		LexerRule rule = new LexerRule("NAME", CharRules.escaped(), StandardTokenType.STRING, true);
		Token token = rule.createToken("\\t", POSITION);
		assertInstanceOf(ShadowToken.class, token);
		assertInstanceOf(EscapedToken.class, ((ShadowToken) token).token());
		assertTrue(token.types().contains(StandardTokenType.STRING));
	}
}
