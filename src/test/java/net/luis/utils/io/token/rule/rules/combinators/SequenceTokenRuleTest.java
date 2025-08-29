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

package net.luis.utils.io.token.rule.rules.combinators;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.rule.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SequenceTokenRule}.<br>
 *
 * @author Luis-St
 */
class SequenceTokenRuleTest {
	
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
	void constructorWithNullTokenRules() {
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyTokenRules() {
		assertThrows(IllegalArgumentException.class, () -> new SequenceTokenRule(Collections.emptyList()));
	}
	
	@Test
	void constructorWithNullElementInTokenRules() {
		List<TokenRule> rulesWithNull = Lists.newArrayList(createRule("test1"), null, createRule("test2"));
		
		assertThrows(NullPointerException.class, () -> new SequenceTokenRule(rulesWithNull));
	}
	
	@Test
	void constructorWithValidTokenRules() {
		List<TokenRule> rules = List.of(createRule("test1"), createRule("test2"));
		
		assertDoesNotThrow(() -> new SequenceTokenRule(rules));
	}
	
	@Test
	void constructorWithSingleTokenRule() {
		List<TokenRule> rules = List.of(createRule("single"));
		
		assertDoesNotThrow(() -> new SequenceTokenRule(rules));
	}
	
	@Test
	void tokenRulesReturnsCorrectList() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		List<TokenRule> originalRules = List.of(rule1, rule2);
		SequenceTokenRule sequence = new SequenceTokenRule(originalRules);
		
		List<TokenRule> returnedRules = sequence.tokenRules();
		
		assertEquals(originalRules, returnedRules);
		assertEquals(2, returnedRules.size());
		assertEquals(rule1, returnedRules.get(0));
		assertEquals(rule2, returnedRules.get(1));
	}
	
	@Test
	void tokenRulesReturnsUnmodifiableList() {
		List<TokenRule> rules = List.of(createRule("test"));
		SequenceTokenRule sequence = new SequenceTokenRule(rules);
		
		List<TokenRule> returnedRules = sequence.tokenRules();
		
		assertThrows(UnsupportedOperationException.class, () -> returnedRules.add(createRule("new")));
		assertThrows(UnsupportedOperationException.class, returnedRules::removeFirst);
		assertThrows(UnsupportedOperationException.class, returnedRules::clear);
	}
	
	@Test
	void matchWithNullTokenStream() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test1"), createRule("test2")));
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test")));
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithSingleRuleSequence() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("single")));
		Token matchingToken = createToken("single");
		Token nonMatchingToken = createToken("other");
		
		TokenRuleMatch match1 = rule.match(new TokenStream(List.of(matchingToken), 0));
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(1, match1.endIndex());
		assertEquals(1, match1.matchedTokens().size());
		assertEquals(matchingToken, match1.matchedTokens().getFirst());
		
		assertNull(rule.match(new TokenStream(List.of(nonMatchingToken), 0)));
	}
	
	@Test
	void matchWithTwoRuleSequence() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("first"), createRule("second")));
		Token firstToken = createToken("first");
		Token secondToken = createToken("second");
		Token wrongToken = createToken("wrong");
		
		List<Token> perfectMatch = List.of(firstToken, secondToken);
		TokenRuleMatch match1 = rule.match(new TokenStream(perfectMatch, 0));
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(2, match1.endIndex());
		assertEquals(2, match1.matchedTokens().size());
		assertEquals(firstToken, match1.matchedTokens().get(0));
		assertEquals(secondToken, match1.matchedTokens().get(1));
		
		List<Token> wrongFirst = List.of(wrongToken, secondToken);
		assertNull(rule.match(new TokenStream(wrongFirst, 0)));
		
		List<Token> wrongSecond = List.of(firstToken, wrongToken);
		assertNull(rule.match(new TokenStream(wrongSecond, 0)));
		
		List<Token> insufficient = List.of(firstToken);
		assertNull(rule.match(new TokenStream(insufficient, 0)));
	}
	
	@Test
	void matchWithLongSequence() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(
			createRule("a"), createRule("b"), createRule("c"), createRule("d"), createRule("e")
		));
		List<Token> tokens = List.of(
			createToken("a"), createToken("b"), createToken("c"), createToken("d"), createToken("e")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(5, match.matchedTokens().size());
		for (int i = 0; i < 5; i++) {
			assertEquals(tokens.get(i), match.matchedTokens().get(i));
		}
	}
	
	@Test
	void matchWithPartialSequenceFailure() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(
			createRule("good"), createRule("good"), createRule("bad"), createRule("good")
		));
		List<Token> tokens = List.of(
			createToken("good"), createToken("good"), createToken("wrong"), createToken("good")
		);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchAtDifferentIndices() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("x"), createRule("y")));
		List<Token> tokens = List.of(
			createToken("a"), createToken("x"), createToken("y"), createToken("z")
		);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
		assertEquals("x", match.matchedTokens().get(0).value());
		assertEquals("y", match.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithComplexInnerRules() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		TokenRule letterRule = TokenRules.pattern("[a-z]+");
		SequenceTokenRule rule = new SequenceTokenRule(List.of(numberRule, letterRule, numberRule));
		
		List<Token> matchingTokens = List.of(createToken("123"), createToken("abc"), createToken("456"));
		List<Token> nonMatchingTokens = List.of(createToken("123"), createToken("ABC"), createToken("456"));
		
		TokenRuleMatch match1 = rule.match(new TokenStream(matchingTokens, 0));
		assertNotNull(match1);
		assertEquals(3, match1.matchedTokens().size());
		
		assertNull(rule.match(new TokenStream(nonMatchingTokens, 0)));
	}
	
	@Test
	void matchWithOptionalInnerRules() {
		OptionalTokenRule optional = new OptionalTokenRule(createRule("maybe"));
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("start"), optional, createRule("end")));
		
		List<Token> withOptional = List.of(createToken("start"), createToken("maybe"), createToken("end"));
		TokenRuleMatch match1 = rule.match(new TokenStream(withOptional, 0));
		assertNotNull(match1);
		assertEquals(3, match1.matchedTokens().size());
		
		List<Token> withoutOptional = List.of(createToken("start"), createToken("end"));
		TokenRuleMatch match2 = rule.match(new TokenStream(withoutOptional, 0));
		assertNotNull(match2);
		assertEquals(2, match2.matchedTokens().size());
	}
	
	@Test
	void matchWithRepeatedInnerRules() {
		RepeatedTokenRule repeated = new RepeatedTokenRule(createRule("rep"), 2, 3);
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("begin"), repeated, createRule("finish")));
		
		List<Token> tokens = List.of(
			createToken("begin"),
			createToken("rep"), createToken("rep"),
			createToken("finish")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
	}
	
	@Test
	void matchWithNestedSequences() {
		SequenceTokenRule inner = new SequenceTokenRule(List.of(createRule("inner1"), createRule("inner2")));
		SequenceTokenRule outer = new SequenceTokenRule(List.of(createRule("outer"), inner));
		
		List<Token> tokens = List.of(
			createToken("outer"), createToken("inner1"), createToken("inner2")
		);
		
		TokenRuleMatch match = outer.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(
			createRule("specific"), TokenRules.alwaysMatch(), createRule("another")
		));
		
		List<Token> tokens = List.of(
			createToken("specific"), createToken("anything"), createToken("another")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals("anything", match.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithInsufficientTokensForSequence() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(
			createRule("need"), createRule("more"), createRule("tokens")
		));
		
		List<Token> twoTokens = List.of(createToken("need"), createToken("more"));
		
		assertNull(rule.match(new TokenStream(twoTokens, 0)));
	}
	
	@Test
	void matchWithExactTokenCount() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("exact"), createRule("match")));
		List<Token> exactTokens = List.of(createToken("exact"), createToken("match"));
		
		TokenRuleMatch match = rule.match(new TokenStream(exactTokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void matchWithExtraTokensAfterSequence() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("first"), createRule("second")));
		List<Token> tokensWithExtra = List.of(
			createToken("first"), createToken("second"), createToken("extra"), createToken("more")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokensWithExtra, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test1"), createRule("test2")));
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens().size(), match2.matchedTokens().size());
	}
	
	@Test
	void notThrowsUnsupportedOperationException() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test1"), createRule("test2")));
		
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		List<TokenRule> rules = List.of(createRule("test1"), createRule("test2"));
		SequenceTokenRule rule1 = new SequenceTokenRule(rules);
		SequenceTokenRule rule2 = new SequenceTokenRule(rules);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		SequenceTokenRule rule = new SequenceTokenRule(List.of(createRule("test1"), createRule("test2")));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("SequenceTokenRule"));
		assertTrue(ruleString.contains("tokenRules="));
	}
}
