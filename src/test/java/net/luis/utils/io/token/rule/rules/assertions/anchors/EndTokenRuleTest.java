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

package net.luis.utils.io.token.rule.rules.assertions.anchors;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.assertions.anchors.AnchorType;
import net.luis.utils.io.token.rule.rules.assertions.anchors.EndTokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndTokenRule}.<br>
 *
 * @author Luis-St
 */
class EndTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull Token createPositionedToken(@NotNull String value, int line, int character) {
		TokenPosition startPos = new TokenPosition(line, 0, character);
		TokenPosition endPos = new TokenPosition(line, character + value.length() - 1, character + value.length() - 1);
		return new SimpleToken(word -> word.equals(value), value, startPos, endPos);
	}
	
	private static @NotNull @Unmodifiable List<Token> createTokenList() {
		return java.util.stream.IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
	}
	
	@Test
	void constructorWithNullAnchorType() {
		assertThrows(NullPointerException.class, () -> new EndTokenRule(null));
	}
	
	@Test
	void constructorWithValidAnchorTypes() {
		EndTokenRule documentRule = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule = new EndTokenRule(AnchorType.LINE);
		
		assertEquals(AnchorType.DOCUMENT, documentRule.anchorType());
		assertEquals(AnchorType.LINE, lineRule.anchorType());
	}
	
	@Test
	void documentMatchWithNullTokenList() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void documentMatchWithEmptyTokenListAtIndexZero() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchWithEmptyTokenListAtHigherIndex() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 5);
		
		assertNotNull(match);
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchWithTokensAtValidEndIndex() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchWithTokensAtIndexBeyondEnd() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch match = rule.match(tokens, 5);
		
		assertNotNull(match);
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentNoMatchWithTokensAtValidTokenIndex() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		assertNull(rule.match(tokens, 0));
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void documentNoMatchWithSingleTokenAtIndexZero() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = List.of(createToken("single"));
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void documentMatchWithLargeTokenListAtEnd() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> largeList = createTokenList();
		
		TokenRuleMatch match = rule.match(largeList, 100);
		
		assertNotNull(match);
		assertEquals(100, match.startIndex());
		assertEquals(100, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentNoMatchWithLargeTokenListInMiddle() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> largeList = createTokenList();
		
		assertNull(rule.match(largeList, 50));
		assertNull(rule.match(largeList, 99));
	}
	
	@Test
	void documentMatchAtNegativeIndexBeyondSize() {
		EndTokenRule rule = new EndTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = List.of(createToken("test"));
		
		assertNull(rule.match(tokens, -5));
	}
	
	@Test
	void lineMatchWithNullTokenList() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void lineMatchWithEmptyTokenListAtIndexZero() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchAtDocumentEnd() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(createPositionedToken("first", 1, 1), createPositionedToken("second", 1, 7));
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchWhenNextTokenOnDifferentLine() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createPositionedToken("first", 1, 1),
			createPositionedToken("second", 2, 1)
		);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchWhenCurrentTokenContainsNewline() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("text\n"),
			createToken("next")
		);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineNoMatchWhenTokensOnSameLine() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createPositionedToken("first", 1, 1),
			createPositionedToken("second", 1, 7)
		);
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void lineNoMatchWithUnpositionedTokens() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("first"),
			createToken("second")
		);
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void lineMatchWithMixedPositionedAndUnpositionedTokens() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createPositionedToken("first", 1, 1),
			createToken("unpositioned")
		);
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void lineMatchWithNewlineInMiddleOfToken() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("before\nafter"),
			createToken("next")
		);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineNoMatchAtLastTokenWithoutNewline() {
		EndTokenRule rule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createPositionedToken("first", 1, 1),
			createPositionedToken("last", 1, 7)
		);
		
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		EndTokenRule documentRule = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch docMatch1 = documentRule.match(tokens, 1);
		TokenRuleMatch docMatch2 = documentRule.match(tokens, 1);
		
		assertNotNull(docMatch1);
		assertNotNull(docMatch2);
		assertEquals(docMatch1.startIndex(), docMatch2.startIndex());
		assertEquals(docMatch1.endIndex(), docMatch2.endIndex());
		assertEquals(docMatch1.matchedTokens(), docMatch2.matchedTokens());
		
		TokenRuleMatch lineMatch1 = lineRule.match(tokens, 1);
		TokenRuleMatch lineMatch2 = lineRule.match(tokens, 1);
		
		assertNotNull(lineMatch1);
		assertNotNull(lineMatch2);
		assertEquals(lineMatch1.startIndex(), lineMatch2.startIndex());
		assertEquals(lineMatch1.endIndex(), lineMatch2.endIndex());
		assertEquals(lineMatch1.matchedTokens(), lineMatch2.matchedTokens());
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		EndTokenRule documentRule = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule = new EndTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("text"),
			createToken("123"),
			createToken("!@#")
		);
		
		TokenRuleMatch docMatch = documentRule.match(tokens, 3);
		assertNotNull(docMatch);
		assertEquals(3, docMatch.startIndex());
		assertEquals(3, docMatch.endIndex());
		assertTrue(docMatch.matchedTokens().isEmpty());
		
		TokenRuleMatch lineMatch = lineRule.match(tokens, 3);
		assertNotNull(lineMatch);
		assertEquals(3, lineMatch.startIndex());
		assertEquals(3, lineMatch.endIndex());
		assertTrue(lineMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void equalInstancesHaveSameHashCode() {
		EndTokenRule rule1 = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule rule2 = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule rule3 = new EndTokenRule(AnchorType.LINE);
		EndTokenRule rule4 = new EndTokenRule(AnchorType.LINE);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertEquals(rule3.hashCode(), rule4.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
	}
	
	@Test
	void equalityBetweenInstances() {
		EndTokenRule docRule1 = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule docRule2 = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule1 = new EndTokenRule(AnchorType.LINE);
		EndTokenRule lineRule2 = new EndTokenRule(AnchorType.LINE);
		
		assertEquals(docRule1, docRule2);
		assertEquals(lineRule1, lineRule2);
		assertNotEquals(docRule1, lineRule1);
		assertNotEquals(docRule2, lineRule2);
	}
	
	@Test
	void recordAccessors() {
		EndTokenRule documentRule = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule = new EndTokenRule(AnchorType.LINE);
		
		assertEquals(AnchorType.DOCUMENT, documentRule.anchorType());
		assertEquals(AnchorType.LINE, lineRule.anchorType());
	}
	
	@Test
	void toStringContainsAnchorType() {
		EndTokenRule documentRule = new EndTokenRule(AnchorType.DOCUMENT);
		EndTokenRule lineRule = new EndTokenRule(AnchorType.LINE);
		
		String docString = documentRule.toString();
		String lineString = lineRule.toString();
		
		assertTrue(docString.contains("DOCUMENT"));
		assertTrue(lineString.contains("LINE"));
	}
}
