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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BoundaryTokenRule}.<br>
 *
 * @author Luis-St
 */
class BoundaryTokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
				Objects.requireNonNull(tokens, "Tokens list must not be null");
				if (startIndex >= tokens.size() || startIndex < 0) {
					return null;
				}
				
				Token token = tokens.get(startIndex);
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, startIndex + 1, List.of(token), this);
				}
				return null;
			}
		};
	}
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullStartRule() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(null, createRule("end")));
	}
	
	@Test
	void constructorWithNullEndRule() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("start"), null));
	}
	
	@Test
	void constructorWithNullBetweenRule() {
		assertThrows(NullPointerException.class, () -> new BoundaryTokenRule(createRule("start"), null, createRule("end")));
	}
	
	@Test
	void constructorWithValidRules() {
		assertDoesNotThrow(() -> new BoundaryTokenRule(createRule("start"), createRule("end")));
		assertDoesNotThrow(() -> new BoundaryTokenRule(createRule("start"), createRule("middle"), createRule("end")));
	}
	
	@Test
	void constructorWithValidNestedRules() {
		BoundaryTokenRule validNested = new BoundaryTokenRule(createRule("inner_start"), createRule("inner_end"));
		RepeatedTokenRule validRepeated = new RepeatedTokenRule(createRule("repeat"), 1, 3);
		SequenceTokenRule validSequence = new SequenceTokenRule(List.of(createRule("seq1"), createRule("seq2")));
		
		assertDoesNotThrow(() -> new BoundaryTokenRule(createRule("start"), validNested, createRule("end")));
		assertDoesNotThrow(() -> new BoundaryTokenRule(createRule("start"), validRepeated, createRule("end")));
		assertDoesNotThrow(() -> new BoundaryTokenRule(createRule("start"), validSequence, createRule("end")));
	}
	
	@Test
	void startTokenRuleReturnsCorrectRule() {
		TokenRule startRule = createRule("start");
		BoundaryTokenRule boundary = new BoundaryTokenRule(startRule, createRule("end"));
		
		assertEquals(startRule, boundary.startTokenRule());
	}
	
	@Test
	void betweenTokenRuleReturnsAlwaysMatchByDefault() {
		BoundaryTokenRule boundary = new BoundaryTokenRule(createRule("start"), createRule("end"));
		
		assertEquals(TokenRules.alwaysMatch(), boundary.betweenTokenRule());
	}
	
	@Test
	void betweenTokenRuleReturnsSpecifiedRule() {
		TokenRule betweenRule = createRule("middle");
		BoundaryTokenRule boundary = new BoundaryTokenRule(createRule("start"), betweenRule, createRule("end"));
		
		assertEquals(betweenRule, boundary.betweenTokenRule());
	}
	
	@Test
	void endTokenRuleReturnsCorrectRule() {
		TokenRule endRule = createRule("end");
		BoundaryTokenRule boundary = new BoundaryTokenRule(createRule("start"), endRule);
		
		assertEquals(endRule, boundary.endTokenRule());
	}
	
	@Test
	void matchWithNullTokenList() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		
		assertNull(rule.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		List<Token> tokens = List.of(createToken("start"));
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
	}
	
	@Test
	void matchWithNoStartMatch() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		List<Token> tokens = List.of(createToken("other"), createToken("end"));
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void matchWithStartButNoEnd() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		List<Token> tokens = List.of(createToken("start"), createToken("other"));
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void matchWithSimpleStartEnd() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		Token startToken = createToken("start");
		Token endToken = createToken("end");
		List<Token> tokens = List.of(startToken, endToken);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
		assertEquals(startToken, match.matchedTokens().get(0));
		assertEquals(endToken, match.matchedTokens().get(1));
	}
	
	@Test
	void matchWithContentBetween() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule("content"), createRule(")"));
		Token start = createToken("(");
		Token content = createToken("content");
		Token end = createToken(")");
		List<Token> tokens = List.of(start, content, end);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals(3, match.matchedTokens().size());
		assertEquals(start, match.matchedTokens().get(0));
		assertEquals(content, match.matchedTokens().get(1));
		assertEquals(end, match.matchedTokens().get(2));
	}
	
	@Test
	void matchWithMultipleContentTokens() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule("content"), createRule(")"));
		Token start = createToken("(");
		Token content1 = createToken("content");
		Token content2 = createToken("content");
		Token content3 = createToken("content");
		Token end = createToken(")");
		List<Token> tokens = List.of(start, content1, content2, content3, end);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(5, match.matchedTokens().size());
	}
	
	@Test
	void matchWithAlwaysMatchBetween() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule(")"));
		Token start = createToken("(");
		Token middle1 = createToken("anything");
		Token middle2 = createToken("goes");
		Token end = createToken(")");
		List<Token> tokens = List.of(start, middle1, middle2, end);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
		assertEquals(start, match.matchedTokens().get(0));
		assertEquals(middle1, match.matchedTokens().get(1));
		assertEquals(middle2, match.matchedTokens().get(2));
		assertEquals(end, match.matchedTokens().get(3));
	}
	
	@Test
	void matchWithOptionalBetween() {
		OptionalTokenRule optional = new OptionalTokenRule(createRule("optional"));
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), optional, createRule("end"));
		
		List<Token> withOptional = List.of(createToken("start"), createToken("optional"), createToken("end"));
		TokenRuleMatch match1 = rule.match(withOptional, 0);
		assertNotNull(match1);
		assertEquals(3, match1.matchedTokens().size());
		
		List<Token> withoutOptional = List.of(createToken("start"), createToken("end"));
		TokenRuleMatch match2 = rule.match(withoutOptional, 0);
		assertNotNull(match2);
		assertEquals(2, match2.matchedTokens().size());
	}
	
	@Test
	void matchWithAnyOfBetween() {
		AnyOfTokenRule anyOf = new AnyOfTokenRule(Set.of(createRule("option1"), createRule("option2")));
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("["), anyOf, createRule("]"));
		
		List<Token> withOption1 = List.of(createToken("["), createToken("option1"), createToken("]"));
		TokenRuleMatch match1 = rule.match(withOption1, 0);
		assertNotNull(match1);
		assertEquals("option1", match1.matchedTokens().get(1).value());
		
		List<Token> withOption2 = List.of(createToken("["), createToken("option2"), createToken("]"));
		TokenRuleMatch match2 = rule.match(withOption2, 0);
		assertNotNull(match2);
		assertEquals("option2", match2.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithRepeatedBetween() {
		RepeatedTokenRule repeated = new RepeatedTokenRule(createRule("x"), 1, 3);
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("<"), repeated, createRule(">"));
		
		List<Token> minRep = List.of(createToken("<"), createToken("x"), createToken(">"));
		TokenRuleMatch match1 = rule.match(minRep, 0);
		assertNotNull(match1);
		assertEquals(3, match1.matchedTokens().size());
		
		List<Token> maxRep = List.of(createToken("<"), createToken("x"), createToken("x"), createToken("x"), createToken(">"));
		TokenRuleMatch match2 = rule.match(maxRep, 0);
		assertNotNull(match2);
		assertEquals(5, match2.matchedTokens().size());
	}
	
	@Test
	void matchWithSequenceBetween() {
		SequenceTokenRule sequence = new SequenceTokenRule(List.of(createRule("a"), createRule("b")));
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("{"), sequence, createRule("}"));
		
		List<Token> tokens = List.of(createToken("{"), createToken("a"), createToken("b"), createToken("}"));
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(4, match.matchedTokens().size());
		assertEquals("a", match.matchedTokens().get(1).value());
		assertEquals("b", match.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithEarliestEndMatch() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("start"), createRule("end"));
		Token start = createToken("start");
		Token end1 = createToken("end");
		Token middle = createToken("middle");
		Token end2 = createToken("end");
		List<Token> tokens = List.of(start, end1, middle, end2);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
		assertEquals(start, match.matchedTokens().get(0));
		assertEquals(end1, match.matchedTokens().get(1));
	}
	
	@Test
	void matchAtDifferentStartIndices() {
		BoundaryTokenRule rule = new BoundaryTokenRule(createRule("("), createRule(")"));
		List<Token> tokens = List.of(
			createToken("prefix"),
			createToken("("),
			createToken("content"),
			createToken(")")
		);
		
		TokenRuleMatch match = rule.match(tokens, 1);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(4, match.endIndex());
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule start = createRule("start");
		TokenRule end = createRule("end");
		BoundaryTokenRule rule1 = new BoundaryTokenRule(start, end);
		BoundaryTokenRule rule2 = new BoundaryTokenRule(start, end);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
}

