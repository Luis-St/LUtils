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

package net.luis.utils.io.token.rule.rules.quantifiers;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				if (!stream.hasToken()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.consumeToken(), List.of(token), this);
				}
				return null;
			}
		};
	}
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void constructorWithValidTokenRule() {
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> new OptionalTokenRule(rule));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule rule = createRule("test");
		OptionalTokenRule optional = new OptionalTokenRule(rule);
		
		assertEquals(rule, optional.tokenRule());
	}
	
	@Test
	void matchWithNullTokenStream() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithMatchingToken() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("match"));
		Token matchingToken = createToken("match");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithNonMatchingTokenReturnsEmptyMatch() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("expected"));
		Token nonMatchingToken = createToken("different");
		List<Token> tokens = List.of(nonMatchingToken);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchAtDifferentIndices() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("target"));
		Token other = createToken("other");
		Token target = createToken("target");
		Token another = createToken("another");
		List<Token> tokens = List.of(other, target, another);
		
		TokenRuleMatch match0 = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match0);
		assertTrue(match0.matchedTokens().isEmpty());
		assertEquals(0, match0.startIndex());
		assertEquals(0, match0.endIndex());
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 1));
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		assertEquals(target, match1.matchedTokens().getFirst());
		assertEquals(1, match1.startIndex());
		assertEquals(2, match1.endIndex());
		
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 2));
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
		assertEquals(2, match2.startIndex());
		assertEquals(2, match2.endIndex());
	}
	
	@Test
	void matchWithComplexInnerRule() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		OptionalTokenRule optional = new OptionalTokenRule(numberRule);
		Token numberToken = createToken("123");
		Token textToken = createToken("abc");
		
		TokenRuleMatch numberMatch = optional.match(new TokenStream(List.of(numberToken), 0));
		assertNotNull(numberMatch);
		assertEquals(1, numberMatch.matchedTokens().size());
		assertEquals(numberToken, numberMatch.matchedTokens().getFirst());
		
		TokenRuleMatch textMatch = optional.match(new TokenStream(List.of(textToken), 0));
		assertNotNull(textMatch);
		assertTrue(textMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		OptionalTokenRule optional = new OptionalTokenRule(TokenRules.alwaysMatch());
		Token anyToken = createToken("anything");
		List<Token> tokens = List.of(anyToken);
		
		TokenRuleMatch match = optional.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(anyToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithSequenceRule() {
		TokenRule sequence = TokenRules.sequence(createRule("hello"), createRule("world"));
		OptionalTokenRule optional = new OptionalTokenRule(sequence);
		
		List<Token> matchingTokens = List.of(createToken("hello"), createToken("world"));
		TokenRuleMatch match1 = optional.match(new TokenStream(matchingTokens, 0));
		assertNotNull(match1);
		assertEquals(2, match1.matchedTokens().size());
		
		List<Token> nonMatchingTokens = List.of(createToken("hello"), createToken("universe"));
		TokenRuleMatch match2 = optional.match(new TokenStream(nonMatchingTokens, 0));
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithRepeatedRule() {
		TokenRule repeated = TokenRules.repeatExactly(createRule("x"), 2);
		OptionalTokenRule optional = new OptionalTokenRule(repeated);
		
		List<Token> exactTokens = List.of(createToken("x"), createToken("x"));
		TokenRuleMatch exactMatch = optional.match(new TokenStream(exactTokens, 0));
		assertNotNull(exactMatch);
		assertEquals(2, exactMatch.matchedTokens().size());
		
		List<Token> insufficientTokens = List.of(createToken("x"));
		TokenRuleMatch insufficientMatch = optional.match(new TokenStream(insufficientTokens, 0));
		assertNotNull(insufficientMatch);
		assertTrue(insufficientMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAnyOfRule() {
		TokenRule anyOf = TokenRules.any(createRule("option1"), createRule("option2"));
		OptionalTokenRule optional = new OptionalTokenRule(anyOf);
		
		TokenRuleMatch match1 = optional.match(new TokenStream(List.of(createToken("option1")), 0));
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		
		TokenRuleMatch match2 = optional.match(new TokenStream(List.of(createToken("option2")), 0));
		assertNotNull(match2);
		assertEquals(1, match2.matchedTokens().size());
		
		TokenRuleMatch match3 = optional.match(new TokenStream(List.of(createToken("option3")), 0));
		assertNotNull(match3);
		assertTrue(match3.matchedTokens().isEmpty());
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens().size(), match2.matchedTokens().size());
		assertEquals(match1.matchedTokens().getFirst(), match2.matchedTokens().getFirst());
	}
	
	@Test
	void matchEmptyMatchStructure() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("nomatch"));
		Token token = createToken("different");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(emptyMatch);
		assertEquals(0, emptyMatch.startIndex());
		assertEquals(0, emptyMatch.endIndex());
		assertTrue(emptyMatch.matchedTokens().isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> emptyMatch.matchedTokens().add(token));
	}
	
	@Test
	void matchAlwaysReturnsNonNull() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("anything"));
		
		assertNotNull(rule.match(new TokenStream(List.of(createToken("anything")), 0)));
		assertNotNull(rule.match(new TokenStream(List.of(createToken("different")), 0)));
		assertNotNull(rule.match(new TokenStream(List.of(createToken("a"), createToken("b")), 0)));
		assertNotNull(rule.match(new TokenStream(List.of(createToken("a"), createToken("b")), 1)));
	}
	
	@Test
	void nestedOptionalRules() {
		OptionalTokenRule inner = new OptionalTokenRule(createRule("inner"));
		OptionalTokenRule outer = new OptionalTokenRule(inner);
		
		TokenRuleMatch match1 = outer.match(new TokenStream(List.of(createToken("inner")), 0));
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		
		TokenRuleMatch match2 = outer.match(new TokenStream(List.of(createToken("other")), 0));
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
	}
	
	@Test
	void notReturnsValidRule() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.pattern("optional"));
		
		assertDoesNotThrow(rule::not);
		assertNotNull(rule.not());
	}
	
	@Test
	void notBehavesCorrectlyWithOptionalMatch() {
		OptionalTokenRule rule = new OptionalTokenRule(TokenRules.pattern("optional"));
		TokenRule negatedRule = rule.not();
		
		TokenRuleMatch originalMatch = rule.match(new TokenStream(List.of(createToken("optional")), 0));
		TokenRuleMatch negatedMatch = negatedRule.match(new TokenStream(List.of(createToken("optional")), 0));
		
		assertNotNull(originalMatch);
		assertEquals(1, originalMatch.matchedTokens().size());
		
		TokenRuleMatch originalEmpty = rule.match(new TokenStream(List.of(createToken("other")), 0));
		TokenRuleMatch negatedOther = negatedRule.match(new TokenStream(List.of(createToken("other")), 0));
		
		assertNotNull(originalEmpty);
		assertTrue(originalEmpty.matchedTokens().isEmpty());
		assertNotNull(negatedOther);
	}

	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule1 = new OptionalTokenRule(innerRule);
		OptionalTokenRule rule2 = new OptionalTokenRule(innerRule);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("OptionalTokenRule"));
		assertTrue(ruleString.contains("tokenRule="));
	}
}
