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
import net.luis.utils.grammar.token.ShadowToken;
import net.luis.utils.grammar.token.Token;
import net.luis.utils.grammar.token.type.StandardTokenType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Lexer}.<br>
 *
 * @author Luis-St
 */
class LexerTest {
	
	@Test
	void builderWithValidConfiguration() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertNotNull(lexer);
		assertEquals(Set.of('a'), lexer.getAllowedChars());
		assertEquals(1, lexer.getRules().size());
	}
	
	@Test
	void builderWithNullBuilderFunctionThrows() {
		assertThrows(NullPointerException.class, () -> Lexer.builder(null));
	}
	
	@Test
	void constructWithNullAllowedCharsThrows() {
		List<LexerRule> rules = List.of(new LexerRule("A", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false));
		assertThrows(NullPointerException.class, () -> new Lexer(null, rules));
	}
	
	@Test
	void constructWithNullRulesThrows() {
		assertThrows(NullPointerException.class, () -> new Lexer(Set.of('a'), null));
	}
	
	@Test
	void constructWithEmptyAllowedCharsThrows() {
		List<LexerRule> rules = List.of(new LexerRule("A", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false));
		assertThrows(IllegalStateException.class, () -> new Lexer(Set.of(), rules));
	}
	
	@Test
	void constructWithEmptyRulesThrows() {
		assertThrows(IllegalStateException.class, () -> new Lexer(Set.of('a'), List.of()));
	}
	
	@Test
	void constructMakesDefensiveCopiesOfAllowedCharsAndRules() {
		Set<Character> mutableChars = new HashSet<>(Set.of('a'));
		List<LexerRule> mutableRules = new ArrayList<>(List.of(new LexerRule("A", CharRules.literal('a'), StandardTokenType.IDENTIFIER, false)));
		
		Lexer lexer = new Lexer(mutableChars, mutableRules);
		mutableChars.add('b');
		mutableRules.add(new LexerRule("B", CharRules.literal('b'), StandardTokenType.IDENTIFIER, false));
		
		assertEquals(Set.of('a'), lexer.getAllowedChars());
		assertEquals(1, lexer.getRules().size());
	}
	
	@Test
	void tokenizeWithNullInputThrows() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertThrows(NullPointerException.class, () -> lexer.tokenize(null));
	}
	
	@Test
	void tokenizeWithUndefinedCharacterAtStartThrows() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> lexer.tokenize("b"));
		assertTrue(exception.getMessage().contains("'b'"));
	}
	
	@Test
	void tokenizeWithNoMatchingRuleThrows() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		assertThrows(IllegalStateException.class, () -> lexer.tokenize("b"));
	}
	
	@Test
	void tokenizeWithMatchedTextContainingUndefinedCharacterThrows() {
		CharRule pattern = CharRules.sequence(CharRules.literal('a'), CharRules.literal('b'));
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("AB", StandardTokenType.IDENTIFIER, pattern);
		});
		assertThrows(IllegalStateException.class, () -> lexer.tokenize("ab"));
	}
	
	@Test
	void tokenizeWithEmptyInputProducesNoTokens() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		List<Token> tokens = lexer.tokenize("");
		assertNotNull(tokens);
		assertTrue(tokens.isEmpty());
	}
	
	@Test
	void tokenizeWithSingleTokenInputProducesOneToken() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		List<Token> tokens = lexer.tokenize("a");
		assertEquals(1, tokens.size());
		assertEquals("a", tokens.getFirst().value());
	}
	
	@Test
	void tokenizeSkipsRulesWithNullOrZeroWidthMatch() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("ANCHOR", StandardTokenType.UNKNOWN, CharRules.startOfInput());
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		List<Token> tokens = lexer.tokenize("a");
		assertEquals(1, tokens.size());
		assertEquals("a", tokens.getFirst().value());
	}
	
	@Test
	void tokenizeMaximalMunchPrefersLongerMatchOverEarlierDeclaredShorterRule() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("SHORT", StandardTokenType.UNKNOWN, CharRules.literal('a'));
			b.rule("LONG", StandardTokenType.STRING, CharRules.sequence(CharRules.literal('a'), CharRules.literal('a')));
		});
		List<Token> tokens = lexer.tokenize("aa");
		assertEquals(1, tokens.size());
		assertEquals("aa", tokens.getFirst().value());
		assertTrue(tokens.getFirst().types().contains(StandardTokenType.STRING));
	}
	
	@Test
	void tokenizeMaximalMunchBreaksTiesByDeclarationOrder() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("FIRST", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
			b.rule("SECOND", StandardTokenType.KEYWORD, CharRules.literal('a'));
		});
		List<Token> tokens = lexer.tokenize("a");
		assertEquals(1, tokens.size());
		assertTrue(tokens.getFirst().types().contains(StandardTokenType.IDENTIFIER));
		assertFalse(tokens.getFirst().types().contains(StandardTokenType.KEYWORD));
	}
	
	@Test
	void tokenizeCreatesShadowTokenForShadowRule() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow(' ');
			b.shadowRule("WS", StandardTokenType.SPACE, CharRules.whitespace());
		});
		List<Token> tokens = lexer.tokenize(" ");
		assertEquals(1, tokens.size());
		assertInstanceOf(ShadowToken.class, tokens.getFirst());
	}
	
	@Test
	void tokenizeAdvancesPastMatchedTextForSubsequentTokens() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
			b.rule("B", StandardTokenType.IDENTIFIER, CharRules.literal('b'));
		});
		List<Token> tokens = lexer.tokenize("ab");
		assertEquals(2, tokens.size());
		assertEquals("a", tokens.get(0).value());
		assertEquals("b", tokens.get(1).value());
	}
	
	@Test
	void getAllowedCharsReturnsConfiguredAlphabet() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('x', 'y');
			b.rule("XY", StandardTokenType.IDENTIFIER, CharRules.anyOf('x', 'y'));
		});
		assertEquals(Set.of('x', 'y'), lexer.getAllowedChars());
	}
	
	@Test
	void getRulesReturnsConfiguredRulesInOrder() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a', 'b');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
			b.rule("B", StandardTokenType.IDENTIFIER, CharRules.literal('b'));
		});
		List<LexerRule> rules = lexer.getRules();
		assertEquals(2, rules.size());
		assertEquals("A", rules.get(0).name());
		assertEquals("B", rules.get(1).name());
	}
	
	@Test
	void tokenizeMultiCharacterWordInput() {
		Lexer lexer = Lexer.builder(b -> {
			b.allowRange('a', 'z');
			b.rule("WORD", StandardTokenType.IDENTIFIER, CharRules.oneOrMore(CharRules.letter()));
		});
		List<Token> tokens = lexer.tokenize("hello");
		assertEquals(1, tokens.size());
		assertEquals("hello", tokens.getFirst().value());
	}
	
	@Test
	void getAllowedCharsAndGetRulesReturnUnmodifiableViews() {
		Lexer lexer = Lexer.builder(b -> {
			b.allow('a');
			b.rule("A", StandardTokenType.IDENTIFIER, CharRules.literal('a'));
		});
		LexerRule extraRule = new LexerRule("B", CharRules.literal('b'), StandardTokenType.IDENTIFIER, false);
		assertThrows(UnsupportedOperationException.class, () -> lexer.getAllowedChars().add('z'));
		assertThrows(UnsupportedOperationException.class, () -> lexer.getRules().add(extraRule));
	}
	
	@Test
	void tokenizeMixedShadowAndNonShadowRulesProducesFullTokenStreamIncludingShadows() {
		Lexer lexer = Lexer.builder(b -> {
			b.allowRange('a', 'z');
			b.allow(' ', ';');
			b.shadowRule("WS", StandardTokenType.SPACE, CharRules.oneOrMore(CharRules.whitespace()));
			b.rule("IDENT", StandardTokenType.IDENTIFIER, CharRules.oneOrMore(CharRules.letter()));
			b.rule("SEMI", StandardTokenType.SEPARATOR, CharRules.literal(';'));
		});
		List<Token> tokens = lexer.tokenize("foo bar;");
		assertEquals(4, tokens.size());
		assertEquals("foo", tokens.get(0).value());
		assertFalse(tokens.get(0) instanceof ShadowToken);
		assertEquals(" ", tokens.get(1).value());
		assertInstanceOf(ShadowToken.class, tokens.get(1));
		assertEquals("bar", tokens.get(2).value());
		assertFalse(tokens.get(2) instanceof ShadowToken);
		assertEquals(";", tokens.get(3).value());
		assertFalse(tokens.get(3) instanceof ShadowToken);
	}
	
	@Test
	void tokenizeMultiLineInputTracksLineAndCharacterPositionInThrownException() {
		Lexer lexer = Lexer.builder(b -> {
			b.allowRange('a', 'z');
			b.allow('\n');
			b.rule("LETTER", StandardTokenType.IDENTIFIER, CharRules.letter());
			b.rule("NEWLINE", StandardTokenType.NEWLINE, CharRules.literal('\n'));
		});
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> lexer.tokenize("a\n#"));
		assertTrue(exception.getMessage().contains("line 1"));
		assertTrue(exception.getMessage().contains("'#'"));
	}
}
