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

package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.InvertibleTokenRule;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinition}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		TokenDefinition anyDefinition = word -> word.equals(value);
		return createToken(anyDefinition, value);
	}
	
	private static @NotNull Token createToken(@NotNull TokenDefinition definition, @NotNull String value) {
		return SimpleToken.createUnpositioned(definition, value);
	}
	
	@Test
	void ofCharacterCreatesCharTokenDefinition() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertInstanceOf(CharTokenDefinition.class, definition);
		assertEquals('a', ((CharTokenDefinition) definition).token());
	}
	
	@Test
	void ofCharacterWithSpecialCharacters() {
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\\'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\n'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\t'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of(' '));
	}
	
	@Test
	void ofStringWithNullToken() {
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, false));
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, true));
	}
	
	@Test
	void ofStringWithEmptyToken() {
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", false));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", true));
	}
	
	@Test
	void ofStringCreatesStringTokenDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", false);
		
		assertInstanceOf(StringTokenDefinition.class, definition);
		assertEquals("hello", ((StringTokenDefinition) definition).token());
		assertFalse(((StringTokenDefinition) definition).equalsIgnoreCase());
	}
	
	@Test
	void ofStringWithCaseInsensitive() {
		TokenDefinition definition = TokenDefinition.of("hello", true);
		
		assertInstanceOf(StringTokenDefinition.class, definition);
		assertEquals("hello", ((StringTokenDefinition) definition).token());
		assertTrue(((StringTokenDefinition) definition).equalsIgnoreCase());
	}
	
	@Test
	void ofStringWithSpecialContent() {
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of(" ", false));
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of("\t\n", false));
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of("!@#$", false));
	}
	
	@Test
	void ofEscapedCreatesEscapedTokenDefinition() {
		TokenDefinition definition = TokenDefinition.ofEscaped('a');
		
		assertInstanceOf(EscapedTokenDefinition.class, definition);
		assertEquals('a', ((EscapedTokenDefinition) definition).token());
	}
	
	@Test
	void ofEscapedWithSpecialCharacters() {
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\\'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\n'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\t'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('"'));
	}
	
	@Test
	void combineWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenDefinition.combine((TokenDefinition[]) null));
	}
	
	@Test
	void combineWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.combine(new TokenDefinition[0]));
	}
	
	@Test
	void combineWithSingleDefinition() {
		TokenDefinition charDefinition = TokenDefinition.of('a');
		TokenDefinition result = TokenDefinition.combine(charDefinition);
		
		assertSame(charDefinition, result);
	}
	
	@Test
	void combineCharTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of('a'),
			TokenDefinition.of('b'),
			TokenDefinition.of('c')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("abc"));
		assertEquals("abc", ((StringTokenDefinition) combined).token());
		assertFalse(((StringTokenDefinition) combined).equalsIgnoreCase());
	}
	
	@Test
	void combineStringTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of("world", false)
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("helloworld"));
		assertEquals("helloworld", ((StringTokenDefinition) combined).token());
		assertFalse(((StringTokenDefinition) combined).equalsIgnoreCase());
	}
	
	@Test
	void combineEscapedTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.ofEscaped('\\'),
			TokenDefinition.of('n')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("\\\\n"));
		assertEquals("\\\\n", ((StringTokenDefinition) combined).token());
	}
	
	@Test
	void combineMixedTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of('-'),
			TokenDefinition.ofEscaped('!')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("hello-\\!"));
		assertEquals("hello-\\!", ((StringTokenDefinition) combined).token());
	}
	
	@Test
	void combinePreservesCase() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("HELLO", false),
			TokenDefinition.of("world", false)
		);
		
		assertTrue(combined.matches("HELLOworld"));
		assertFalse(combined.matches("helloworld"));
		assertFalse(combined.matches("HELLOWorld"));
	}
	
	@Test
	void combineWithUnsupportedDefinition() {
		TokenDefinition customDefinition = "custom"::equals;
		
		assertSame(customDefinition, TokenDefinition.combine(customDefinition));
		assertThrows(IllegalArgumentException.class, () ->
			TokenDefinition.combine(TokenDefinition.of('a'), customDefinition));
	}
	
	@Test
	void matchWithNullTokenStream() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertThrows(NullPointerException.class, () -> definition.match((TokenStream) null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertNull(definition.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithNonMatchingToken() {
		TokenDefinition definition = TokenDefinition.of('a');
		List<Token> tokens = List.of(createToken("b"), createToken("c"));
		
		assertNull(definition.match(new TokenStream(tokens)));
		assertNull(definition.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void matchWithMatchingToken() {
		TokenDefinition definition = TokenDefinition.of('a');
		Token matchingToken = createToken(definition, "a");
		List<Token> tokens = List.of(matchingToken, createToken("b"));
		
		TokenRuleMatch match = definition.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
		assertEquals(definition, match.matchingTokenRule());
	}
	
	@Test
	void matchWithMatchingTokenAtDifferentIndex() {
		TokenDefinition definition = TokenDefinition.of('b');
		Token nonMatchingToken = createToken("a");
		Token matchingToken = createToken(definition, "b");
		List<Token> tokens = List.of(nonMatchingToken, matchingToken, createToken("c"));
		
		TokenRuleMatch match = definition.match(new TokenStream(tokens, 1));
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithStringDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", false);
		Token matchingToken = createToken(definition, "hello");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithCaseInsensitiveDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", true);
		Token matchingToken = createToken(definition, "HELLO");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithEscapedDefinition() {
		TokenDefinition definition = TokenDefinition.ofEscaped('n');
		Token matchingToken = createToken(definition, "\\n");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void customDefinitionMatches() {
		TokenDefinition customDefinition = word -> word.startsWith("test");
		
		assertTrue(customDefinition.matches("test"));
		assertTrue(customDefinition.matches("testing"));
		assertFalse(customDefinition.matches("abc"));
		assertFalse(customDefinition.matches(""));
	}
	
	@Test
	void tokenDefinitionImplementsInvertibleTokenRule() {
		TokenDefinition charDef = TokenDefinition.of('a');
		TokenDefinition stringDef = TokenDefinition.of("hello", false);
		TokenDefinition escapedDef = TokenDefinition.ofEscaped('n');
		TokenDefinition customDef = word -> word.startsWith("test");
		
		assertInstanceOf(InvertibleTokenRule.class, charDef);
		assertInstanceOf(InvertibleTokenRule.class, stringDef);
		assertInstanceOf(InvertibleTokenRule.class, escapedDef);
		assertInstanceOf(InvertibleTokenRule.class, customDef);
	}
	
	@Test
	void notReturnsNegatedTokenDefinition() {
		TokenDefinition charDef = TokenDefinition.of('a');
		TokenDefinition stringDef = TokenDefinition.of("hello", false);
		TokenDefinition escapedDef = TokenDefinition.ofEscaped('n');
		TokenDefinition customDef = word -> word.startsWith("test");
		
		TokenRule negatedChar = charDef.not();
		TokenRule negatedString = stringDef.not();
		TokenRule negatedEscaped = escapedDef.not();
		TokenRule negatedCustom = customDef.not();
		
		assertNotNull(negatedChar);
		assertNotNull(negatedString);
		assertNotNull(negatedEscaped);
		assertNotNull(negatedCustom);
		assertNotSame(charDef, negatedChar);
		assertNotSame(stringDef, negatedString);
		assertNotSame(escapedDef, negatedEscaped);
		assertNotSame(customDef, negatedCustom);
	}
	
	@Test
	void doubleNegationReturnsOriginalDefinition() {
		TokenDefinition charDef = TokenDefinition.of('a');
		TokenDefinition stringDef = TokenDefinition.of("hello", false);
		TokenDefinition escapedDef = TokenDefinition.ofEscaped('n');
		TokenDefinition customDef = word -> word.startsWith("test");
		
		TokenRule negatedChar = charDef.not();
		TokenRule doubleNegatedChar = negatedChar.not();
		
		TokenRule negatedString = stringDef.not();
		TokenRule doubleNegatedString = negatedString.not();
		
		TokenRule negatedEscaped = escapedDef.not();
		TokenRule doubleNegatedEscaped = negatedEscaped.not();
		
		TokenRule negatedCustom = customDef.not();
		TokenRule doubleNegatedCustom = negatedCustom.not();
		
		assertSame(charDef, doubleNegatedChar);
		assertSame(stringDef, doubleNegatedString);
		assertSame(escapedDef, doubleNegatedEscaped);
		assertSame(customDef, doubleNegatedCustom);
	}
	
	@Test
	void negatedCharDefinitionMatchesWhenOriginalDoesNot() {
		TokenDefinition charDef = TokenDefinition.of('a');
		TokenRule negatedRule = charDef.not();
		
		Token matchingToken = createToken(charDef, "a");
		Token nonMatchingToken = createToken("b");
		
		List<Token> tokensWithA = List.of(matchingToken, nonMatchingToken);
		TokenStream streamWithA = new TokenStream(tokensWithA, 0);
		
		assertNotNull(charDef.match(streamWithA));
		assertNotNull(negatedRule.match(streamWithA));
		
		List<Token> tokensWithB = List.of(nonMatchingToken, matchingToken);
		TokenStream streamWithB = new TokenStream(tokensWithB, 0);
		
		assertNull(charDef.match(streamWithB));
		TokenRuleMatch negatedMatch = negatedRule.match(streamWithB);
		assertNotNull(negatedMatch);
		assertEquals(0, negatedMatch.startIndex());
		assertEquals(1, negatedMatch.endIndex());
		assertEquals(1, negatedMatch.matchedTokens().size());
	}
	
	@Test
	void negatedStringDefinitionMatchesWhenOriginalDoesNot() {
		TokenDefinition stringDef = TokenDefinition.of("hello", false);
		TokenRule negatedRule = stringDef.not();
		
		Token matchingToken = createToken(stringDef, "hello");
		Token nonMatchingToken = createToken("world");
		
		List<Token> tokensWithHello = List.of(matchingToken);
		TokenStream streamWithHello = new TokenStream(tokensWithHello, 0);
		
		assertNotNull(stringDef.match(streamWithHello));
		assertNull(negatedRule.match(streamWithHello));
		
		List<Token> tokensWithWorld = List.of(nonMatchingToken);
		TokenStream streamWithWorld = new TokenStream(tokensWithWorld, 0);
		
		assertNull(stringDef.match(streamWithWorld));
		assertNotNull(negatedRule.match(streamWithWorld));
	}
	
	@Test
	void negatedCaseInsensitiveDefinition() {
		TokenDefinition caseDef = TokenDefinition.of("hello", true);
		TokenRule negatedRule = caseDef.not();
		
		Token upperToken = createToken(caseDef, "HELLO");
		Token lowerToken = createToken(caseDef, "hello");
		Token otherToken = createToken("world");
		
		List<Token> tokensWithUpper = List.of(upperToken);
		List<Token> tokensWithLower = List.of(lowerToken);
		
		assertNotNull(caseDef.match(new TokenStream(tokensWithUpper, 0)));
		assertNotNull(caseDef.match(new TokenStream(tokensWithLower, 0)));
		
		assertNull(negatedRule.match(new TokenStream(tokensWithUpper, 0)));
		assertNull(negatedRule.match(new TokenStream(tokensWithLower, 0)));
		
		List<Token> tokensWithOther = List.of(otherToken);
		assertNull(caseDef.match(new TokenStream(tokensWithOther, 0)));
		assertNotNull(negatedRule.match(new TokenStream(tokensWithOther, 0)));
	}
	
	@Test
	void negatedEscapedDefinition() {
		TokenDefinition escapedDef = TokenDefinition.ofEscaped('n');
		TokenRule negatedRule = escapedDef.not();
		
		Token matchingToken = createToken(escapedDef, "\\n");
		Token nonMatchingToken = createToken("n");
		
		List<Token> tokensWithEscaped = List.of(matchingToken);
		TokenStream streamWithEscaped = new TokenStream(tokensWithEscaped, 0);
		
		assertNotNull(escapedDef.match(streamWithEscaped));
		assertNull(negatedRule.match(streamWithEscaped));
		
		List<Token> tokensWithPlain = List.of(nonMatchingToken);
		TokenStream streamWithPlain = new TokenStream(tokensWithPlain, 0);
		
		assertNull(escapedDef.match(streamWithPlain));
		assertNotNull(negatedRule.match(streamWithPlain));
	}
	
	@Test
	void negatedCustomDefinition() {
		TokenDefinition customDef = word -> word.startsWith("test");
		TokenRule negatedRule = customDef.not();
		
		Token matchingToken = createToken(customDef, "testing");
		Token nonMatchingToken = createToken("hello");
		
		List<Token> tokensWithTest = List.of(matchingToken);
		TokenStream streamWithTest = new TokenStream(tokensWithTest, 0);
		
		assertNotNull(customDef.match(streamWithTest));
		assertNull(negatedRule.match(streamWithTest));
		
		List<Token> tokensWithHello = List.of(nonMatchingToken);
		TokenStream streamWithHello = new TokenStream(tokensWithHello, 0);
		
		assertNull(customDef.match(streamWithHello));
		assertNotNull(negatedRule.match(streamWithHello));
	}
	
	@Test
	void negatedCombinedDefinition() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of('-'),
			TokenDefinition.of("world", false)
		);
		TokenRule negatedRule = combined.not();
		
		Token matchingToken = createToken(combined, "hello-world");
		Token nonMatchingToken = createToken("goodbye");
		
		List<Token> tokensWithCombined = List.of(matchingToken);
		TokenStream streamWithCombined = new TokenStream(tokensWithCombined, 0);
		
		assertNotNull(combined.match(streamWithCombined));
		assertNull(negatedRule.match(streamWithCombined));
		
		List<Token> tokensWithOther = List.of(nonMatchingToken);
		TokenStream streamWithOther = new TokenStream(tokensWithOther, 0);
		
		assertNull(combined.match(streamWithOther));
		assertNotNull(negatedRule.match(streamWithOther));
	}
	
	@Test
	void negatedRuleConsistentBehavior() {
		TokenDefinition definition = TokenDefinition.of('x');
		TokenRule negatedRule = definition.not();
		
		Token nonMatchingToken = createToken("y");
		List<Token> tokens = List.of(nonMatchingToken);
		TokenStream stream1 = new TokenStream(tokens, 0);
		TokenStream stream2 = new TokenStream(tokens, 0);
		
		TokenRuleMatch match1 = negatedRule.match(stream1);
		TokenRuleMatch match2 = negatedRule.match(stream2);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void negatedRuleWithNullTokenStream() {
		TokenDefinition definition = TokenDefinition.of('a');
		TokenRule negatedRule = definition.not();
		
		assertThrows(NullPointerException.class, () -> negatedRule.match(null));
	}
	
	@Test
	void negatedRuleWithEmptyTokenStream() {
		TokenDefinition definition = TokenDefinition.of('a');
		TokenRule negatedRule = definition.not();
		
		TokenStream emptyStream = new TokenStream(Collections.emptyList(), 0);
		
		assertNull(definition.match(emptyStream));
		
		assertNull(negatedRule.match(emptyStream));
	}
	
	@Test
	void multipleNegationsWork() {
		TokenDefinition definition = TokenDefinition.of("test", false);
		
		TokenRule negated1 = definition.not();
		TokenRule negated2 = negated1.not();
		TokenRule negated3 = negated2.not();
		TokenRule negated4 = negated3.not();
		
		TokenStream streamWithTest = new TokenStream(List.of(createToken(definition, "test")), 0);
		TokenStream streamWithOther = new TokenStream(List.of(createToken("other")), 0);
		
		assertNotNull(definition.match(streamWithTest.copyWithCurrentIndex()));
		assertNotNull(negated2.match(streamWithTest.copyWithCurrentIndex()));
		assertNotNull(negated4.match(streamWithTest.copyWithCurrentIndex()));
		
		assertNull(definition.match(streamWithOther.copyWithCurrentIndex()));
		assertNull(negated2.match(streamWithOther.copyWithCurrentIndex()));
		assertNull(negated4.match(streamWithOther.copyWithCurrentIndex()));
		
		assertNull(negated1.match(streamWithTest.copyWithCurrentIndex()));
		assertNull(negated3.match(streamWithTest.copyWithCurrentIndex()));
		
		assertNotNull(negated1.match(streamWithOther.copyWithCurrentIndex()));
		assertNotNull(negated3.match(streamWithOther.copyWithCurrentIndex()));
	}
	
	@Test
	void negatedDefinitionWithSpecialCharacters() {
		TokenDefinition[] specialDefs = {
			TokenDefinition.of('\n'),
			TokenDefinition.of('\t'),
			TokenDefinition.of(' '),
			TokenDefinition.of('\\'),
			TokenDefinition.ofEscaped('\n'),
			TokenDefinition.ofEscaped('\\')
		};
		
		for (TokenDefinition definition : specialDefs) {
			TokenRule negatedRule = definition.not();
			
			assertDoesNotThrow(negatedRule::not);
			
			TokenRule doubleNegated = negatedRule.not();
			assertSame(definition, doubleNegated);
		}
	}
}
