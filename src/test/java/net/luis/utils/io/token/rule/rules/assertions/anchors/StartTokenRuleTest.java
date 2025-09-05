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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
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
	void documentMatchWithNullTokenStream() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void documentMatchWithEmptyTokenStreamAtStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		
		TokenRuleMatch match = rule.match(new TokenStream(Collections.emptyList(), 0));
		
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
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
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
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void documentMatchWithMultipleTokensOnlyMatchesAtStart() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token1 = createToken("token1");
		Token token2 = createToken("token2");
		Token token3 = createToken("token3");
		List<Token> tokens = List.of(token1, token2, token3);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
		assertNull(rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void documentMatchWithNegativeIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -5)));
	}
	
	@Test
	void documentMatchDoesNotConsumeTokens() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void documentMatchWithLargeTokenStream() {
		StartTokenRule rule = new StartTokenRule(AnchorType.DOCUMENT);
		List<Token> largeList = IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
		
		TokenRuleMatch match = rule.match(new TokenStream(largeList, 0));
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		for (int i = 1; i < largeList.size(); i++) {
			assertNull(rule.match(new TokenStream(largeList, i)));
		}
	}
	
	@Test
	void lineMatchWithNullTokenStream() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void lineMatchWithEmptyTokenStreamAtStartIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		
		TokenRuleMatch match = rule.match(new TokenStream(Collections.emptyList(), 0));
		
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
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
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
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 2));
		
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
		
		assertNull(rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void lineMatchAfterCarriageReturnNewlineToken() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\r\n"),
			createToken("line2")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 2));
		
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
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match);
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
		assertNull(rule.match(new TokenStream(tokens, 2)));
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
		
		TokenRuleMatch match0 = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match0);
		
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 2));
		assertNotNull(match2);
		
		TokenRuleMatch match3 = rule.match(new TokenStream(tokens, 3));
		assertNotNull(match3);
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void lineNoMatchAtTokenContainingNewline() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("text\nmore"),
			createToken("line2")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match);
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
		
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 2));
		assertNotNull(match2);
	}
	
	@Test
	void lineMatchWithNegativeIndex() {
		StartTokenRule rule = new StartTokenRule(AnchorType.LINE);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -5)));
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
		
		TokenRuleMatch docMatch1 = documentRule.match(new TokenStream(tokens, 0));
		TokenRuleMatch docMatch2 = documentRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(docMatch1);
		assertNotNull(docMatch2);
		assertEquals(docMatch1.startIndex(), docMatch2.startIndex());
		assertEquals(docMatch1.endIndex(), docMatch2.endIndex());
		assertEquals(docMatch1.matchedTokens(), docMatch2.matchedTokens());
		
		TokenRuleMatch lineMatch1 = lineRule.match(new TokenStream(tokens, 0));
		TokenRuleMatch lineMatch2 = lineRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(lineMatch1);
		assertNotNull(lineMatch2);
		assertEquals(lineMatch1.startIndex(), lineMatch2.startIndex());
		assertEquals(lineMatch1.endIndex(), lineMatch2.endIndex());
		assertEquals(lineMatch1.matchedTokens(), lineMatch2.matchedTokens());
	}
	
	@Test
	void matchOnlyAtExactStartPosition() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		List<Token> tokens = IntStream.range(0, 10).mapToObj(i -> createToken("token" + i)).toList();
		
		for (int i = 0; i < tokens.size(); i++) {
			if (i == 0) {
				TokenRuleMatch match = documentRule.match(new TokenStream(tokens, i));
				assertNotNull(match);
				assertTrue(match.matchedTokens().isEmpty());
			} else {
				assertNull(documentRule.match(new TokenStream(tokens, i)));
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
	void matchBehaviorIndependentOfTokenContent() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		Token unicode = createToken("🚀");
		Token longText = createToken("verylongtokenvalue");
		Token number = createToken("42");
		
		List<Token> unicodeTokens = List.of(unicode);
		TokenRuleMatch unicodeDocMatch = documentRule.match(new TokenStream(unicodeTokens, 0));
		TokenRuleMatch unicodeLineMatch = lineRule.match(new TokenStream(unicodeTokens, 0));
		assertNotNull(unicodeDocMatch);
		assertNotNull(unicodeLineMatch);
		assertTrue(unicodeDocMatch.matchedTokens().isEmpty());
		assertTrue(unicodeLineMatch.matchedTokens().isEmpty());
		
		List<Token> longTokens = List.of(longText);
		TokenRuleMatch longDocMatch = documentRule.match(new TokenStream(longTokens, 0));
		TokenRuleMatch longLineMatch = lineRule.match(new TokenStream(longTokens, 0));
		assertNotNull(longDocMatch);
		assertNotNull(longLineMatch);
		assertTrue(longDocMatch.matchedTokens().isEmpty());
		assertTrue(longLineMatch.matchedTokens().isEmpty());
		
		List<Token> numberTokens = List.of(number);
		TokenRuleMatch numberDocMatch = documentRule.match(new TokenStream(numberTokens, 0));
		TokenRuleMatch numberLineMatch = lineRule.match(new TokenStream(numberTokens, 0));
		assertNotNull(numberDocMatch);
		assertNotNull(numberLineMatch);
		assertTrue(numberDocMatch.matchedTokens().isEmpty());
		assertTrue(numberLineMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void notReturnsNegatedRule() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		TokenRule negatedDocumentRule = documentRule.not();
		TokenRule negatedLineRule = lineRule.not();
		
		assertNotNull(negatedDocumentRule);
		assertNotNull(negatedLineRule);
		assertNotSame(documentRule, negatedDocumentRule);
		assertNotSame(lineRule, negatedLineRule);
	}
	
	@Test
	void doubleNegationReturnsOriginalRule() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		
		TokenRule negatedDocumentRule = documentRule.not();
		TokenRule doubleNegatedDocumentRule = negatedDocumentRule.not();
		
		TokenRule negatedLineRule = lineRule.not();
		TokenRule doubleNegatedLineRule = negatedLineRule.not();
		
		assertSame(documentRule, doubleNegatedDocumentRule);
		assertSame(lineRule, doubleNegatedLineRule);
	}
	
	@Test
	void negatedDocumentRuleMatchesWhenOriginalDoesNot() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		TokenRule negatedRule = documentRule.not();
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		assertNull(documentRule.match(stream));
		
		TokenRuleMatch negatedMatch = negatedRule.match(stream);
		assertNotNull(negatedMatch);
		assertEquals(1, negatedMatch.startIndex());
		assertEquals(1, negatedMatch.endIndex());
		assertTrue(negatedMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedDocumentRuleDoesNotMatchWhenOriginalMatches() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		TokenRule negatedRule = documentRule.not();
		
		List<Token> tokens = List.of(createToken("first"));
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch originalMatch = documentRule.match(stream);
		assertNotNull(originalMatch);
		
		assertNull(negatedRule.match(stream));
	}
	
	@Test
	void negatedLineRuleMatchesWhenOriginalDoesNot() {
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		TokenRule negatedRule = lineRule.not();
		
		List<Token> tokens = List.of(
			createToken("word1"),
			createToken("word2"),
			createToken("word3")
		);
		TokenStream stream = new TokenStream(tokens, 1);
		
		assertNull(lineRule.match(stream));
		
		TokenRuleMatch negatedMatch = negatedRule.match(stream);
		assertNotNull(negatedMatch);
		assertEquals(1, negatedMatch.startIndex());
		assertEquals(1, negatedMatch.endIndex());
		assertTrue(negatedMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedLineRuleDoesNotMatchWhenOriginalMatches() {
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		TokenRule negatedRule = lineRule.not();
		
		List<Token> tokens = List.of(
			createToken("line1"),
			createToken("\n"),
			createToken("line2")
		);
		TokenStream stream = new TokenStream(tokens, 2);
		
		TokenRuleMatch originalMatch = lineRule.match(stream);
		assertNotNull(originalMatch);
		
		assertNull(negatedRule.match(stream));
	}
	
	@Test
	void negatedRuleConsistentBehavior() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		TokenRule negatedRule = documentRule.not();
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream1 = new TokenStream(tokens, 1);
		TokenStream stream2 = new TokenStream(tokens, 1);
		
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
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		TokenRule negatedRule = documentRule.not();
		
		assertThrows(NullPointerException.class, () -> negatedRule.match(null));
	}
	
	@Test
	void negatedRuleDoesNotConsumeTokens() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		TokenRule negatedRule = documentRule.not();
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		TokenStream stream = new TokenStream(tokens, 1);
		
		TokenRuleMatch match = negatedRule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedRuleComplexScenarios() {
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		TokenRule negatedRule = lineRule.not();
		
		List<Token> tokens = List.of(createToken("first"));
		TokenStream startStream = new TokenStream(tokens, 0);
		
		assertNotNull(lineRule.match(startStream));
		assertNull(negatedRule.match(startStream));
		
		List<Token> tokensWithNewline = List.of(
			createToken("line1"),
			createToken("\n"),
			createToken("line2")
		);
		TokenStream afterNewlineStream = new TokenStream(tokensWithNewline, 2);
		
		assertNotNull(lineRule.match(afterNewlineStream));
		assertNull(negatedRule.match(afterNewlineStream));
		
		TokenStream middleStream = new TokenStream(tokensWithNewline, 1);
		
		assertNull(lineRule.match(middleStream));
		assertNotNull(negatedRule.match(middleStream));
	}
	
	@Test
	void negatedRuleWithEmptyTokenStream() {
		StartTokenRule documentRule = new StartTokenRule(AnchorType.DOCUMENT);
		StartTokenRule lineRule = new StartTokenRule(AnchorType.LINE);
		TokenRule negatedDocumentRule = documentRule.not();
		TokenRule negatedLineRule = lineRule.not();
		
		TokenStream emptyStream = new TokenStream(Collections.emptyList(), 0);
		
		assertNotNull(documentRule.match(emptyStream));
		assertNotNull(lineRule.match(emptyStream));
		
		assertNull(negatedDocumentRule.match(emptyStream));
		assertNull(negatedLineRule.match(emptyStream));
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
}
