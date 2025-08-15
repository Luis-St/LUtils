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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.rule.rules.assertions.anchors.AnchorType;
import net.luis.utils.io.token.rule.rules.assertions.anchors.StartTokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StartTokenRule}.<br>
 *
 * @author Luis-St
 */
class StartTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithValidAnchorType() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		assertEquals(AnchorType.DOCUMENT, documentRule.anchorType());
		
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		assertEquals(AnchorType.LINE, lineRule.anchorType());
	}
	
	@Test
	void constructorWithNullAnchorType() {
		assertThrows(NullPointerException.class, () -> new StartTokenRule(null));
	}
	
	@Test
	void documentMatchWithNullTokenList() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void documentMatchWithEmptyTokenListAtStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchAtStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token = createToken("first");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentNoMatchAtNonStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void documentMatchWithMultipleTokensOnlyMatchesAtStart() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token1 = createToken("token1");
		Token token2 = createToken("token2");
		Token token3 = createToken("token3");
		List<Token> tokens = List.of(token1, token2, token3);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 2));
	}
	
	@Test
	void documentMatchWithNegativeIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(tokens, -1));
		assertNull(rule.match(tokens, -5));
	}
	
	@Test
	void documentMatchWithIndexOutOfBounds() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, 100));
	}
	
	@Test
	void documentMatchDoesNotConsumeTokens() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchWithLargeTokenList() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		List<Token> largeList = IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
		
		TokenRuleMatch match = rule.match(largeList, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		for (int i = 1; i < largeList.size(); i++) {
			assertNull(rule.match(largeList, i));
		}
	}
	
	@Test
	void lineMatchWithNullTokenList() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void lineMatchWithEmptyTokenListAtStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchAtDocumentStart() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		Token token = createToken("first");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchAfterNewlineToken() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\n"),
			createToken("line2")
		);
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineMatchAfterCarriageReturnToken() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\r"),
			createToken("line2")
		);
		
		assertNull(rule.match(tokens, 2));
	}
	
	@Test
	void lineMatchAfterCarriageReturnNewlineToken() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\r\n"),
			createToken("line2")
		);
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lineNoMatchInMiddleOfLine() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("word1"),
			createToken("word2"),
			createToken("word3")
		);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		assertNotNull(match);
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 2));
	}
	
	@Test
	void lineMatchWithMultipleNewlines() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\n"),
			createToken("\n"),
			createToken("line3")
		);
		
		TokenRuleMatch match0 = rule.match(tokens, 0);
		assertNotNull(match0);
		
		TokenRuleMatch match2 = rule.match(tokens, 2);
		assertNotNull(match2);
		
		TokenRuleMatch match3 = rule.match(tokens, 3);
		assertNotNull(match3);
		
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void lineNoMatchAtTokenContainingNewline() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("text\nmore"),
			createToken("line2")
		);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		assertNotNull(match);
		
		assertNull(rule.match(tokens, 1));
		
		TokenRuleMatch match2 = rule.match(tokens, 2);
		assertNotNull(match2);
	}
	
	@Test
	void lineMatchWithNegativeIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(tokens, -1));
		assertNull(rule.match(tokens, -5));
	}
	
	@Test
	void equalityAndHashCode() {
		StartTokenRule rule1 = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule rule2 = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule rule3 = new StartTokenRule(AnchorType.LINE);
		
		assertEquals(rule1, rule2);
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1, rule3);
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
	}
	
	@Test
	void matchResultsAreConsistent() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch docMatch1 = documentRule.match(tokens, 0);
		TokenRuleMatch docMatch2 = documentRule.match(tokens, 0);
		
		assertNotNull(docMatch1);
		assertNotNull(docMatch2);
		assertEquals(docMatch1.startIndex(), docMatch2.startIndex());
		assertEquals(docMatch1.endIndex(), docMatch2.endIndex());
		assertEquals(docMatch1.matchedTokens(), docMatch2.matchedTokens());
		
		TokenRuleMatch lineMatch1 = lineRule.match(tokens, 0);
		TokenRuleMatch lineMatch2 = lineRule.match(tokens, 0);
		
		assertNotNull(lineMatch1);
		assertNotNull(lineMatch2);
		assertEquals(lineMatch1.startIndex(), lineMatch2.startIndex());
		assertEquals(lineMatch1.endIndex(), lineMatch2.endIndex());
		assertEquals(lineMatch1.matchedTokens(), lineMatch2.matchedTokens());
	}
	
	@Test
	void matchOnlyAtExactPosition() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = IntStream.range(0, 10).mapToObj(i -> createToken("token" + i)).toList();
		
		for (int i = 0; i < tokens.size(); i++) {
			if (i == 0) {
				TokenRuleMatch match = documentRule.match(tokens, i);
				assertNotNull(match);
				assertTrue(match.matchedTokens().isEmpty());
			} else {
				assertNull(documentRule.match(tokens, i));
			}
		}
	}
	
	@Test
	void recordPropertiesAccess() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		assertEquals(AnchorType.DOCUMENT, documentRule.anchorType());
		assertEquals(AnchorType.LINE, lineRule.anchorType());
	}
	
	@Test
	void factoryMethodReturnsCorrectInstance() {
		StartTokenRule startRule = (StartTokenRule) TokenRules.startDocument();
		assertEquals(AnchorType.DOCUMENT, startRule.anchorType());
		
		StartTokenRule startDocumentRule = (StartTokenRule) TokenRules.startDocument();
		assertEquals(AnchorType.DOCUMENT, startDocumentRule.anchorType());
		
		StartTokenRule startLineRule = (StartTokenRule) TokenRules.startLine();
		assertEquals(AnchorType.LINE, startLineRule.anchorType());
	}
	
	@Test
	void toStringContainsAnchorType() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		String docString = documentRule.toString();
		String lineString = lineRule.toString();
		
		assertTrue(docString.contains("DOCUMENT"));
		assertTrue(lineString.contains("LINE"));
	}
	
	@Test
	void matchBehaviorIndependentOfTokenContent() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		Token unicode = createToken("🚀");
		Token longText = createToken("verylongtokenvalue");
		Token number = createToken("42");
		
		List<Token> unicodeTokens = List.of(unicode);
		TokenRuleMatch unicodeDocMatch = documentRule.match(unicodeTokens, 0);
		TokenRuleMatch unicodeLineMatch = lineRule.match(unicodeTokens, 0);
		assertNotNull(unicodeDocMatch);
		assertNotNull(unicodeLineMatch);
		assertTrue(unicodeDocMatch.matchedTokens().isEmpty());
		assertTrue(unicodeLineMatch.matchedTokens().isEmpty());
		
		List<Token> longTokens = List.of(longText);
		TokenRuleMatch longDocMatch = documentRule.match(longTokens, 0);
		TokenRuleMatch longLineMatch = lineRule.match(longTokens, 0);
		assertNotNull(longDocMatch);
		assertNotNull(longLineMatch);
		assertTrue(longDocMatch.matchedTokens().isEmpty());
		assertTrue(longLineMatch.matchedTokens().isEmpty());
		
		List<Token> numberTokens = List.of(number);
		TokenRuleMatch numberDocMatch = documentRule.match(numberTokens, 0);
		TokenRuleMatch numberLineMatch = lineRule.match(numberTokens, 0);
		assertNotNull(numberDocMatch);
		assertNotNull(numberLineMatch);
		assertTrue(numberDocMatch.matchedTokens().isEmpty());
		assertTrue(numberLineMatch.matchedTokens().isEmpty());
	}
}
