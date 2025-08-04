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

package net.luis.utils.io.token.rule.actions.filters;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExtractTokenAction}.<br>
 *
 * @author Luis-St
 */
class ExtractTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullFilter() {
		Consumer<Token> consumer = token -> {};
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(null, consumer));
	}
	
	@Test
	void constructorWithNullConsumer() {
		Predicate<Token> filter = token -> true;
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(filter, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		Predicate<Token> filter = token -> token.value().length() < 3;
		Consumer<Token> consumer = token -> {};
		
		assertDoesNotThrow(() -> new ExtractTokenAction(filter, consumer));
	}
	
	@Test
	void filterReturnsCorrectPredicate() {
		Predicate<Token> filter = token -> token.value().startsWith("extract");
		Consumer<Token> consumer = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, consumer);
		
		assertEquals(filter, action.filter());
	}
	
	@Test
	void extractorReturnsCorrectConsumer() {
		Predicate<Token> filter = token -> true;
		Consumer<Token> consumer = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, consumer);
		
		assertEquals(consumer, action.extractor());
	}
	
	@Test
	void applyWithNullMatch() {
		ExtractTokenAction action = new ExtractTokenAction(token -> false, token -> {});
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> true, extractedTokens::add);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertTrue(extractedTokens.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithAlwaysTruePredicate() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> true, extractedTokens::add);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertEquals(2, extractedTokens.size());
		assertEquals(token1, extractedTokens.get(0));
		assertEquals(token2, extractedTokens.get(1));
	}
	
	@Test
	void applyWithAlwaysFalsePredicate() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> false, extractedTokens::add);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(token1, result.get(0));
		assertEquals(token2, result.get(1));
		assertTrue(extractedTokens.isEmpty());
	}
	
	@Test
	void applyWithLengthExtractFilter() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> token.value().length() <= 2, extractedTokens::add);
		Token short1 = createToken("hi");
		Token long1 = createToken("hello");
		Token short2 = createToken("no");
		Token long2 = createToken("world");
		List<Token> tokens = List.of(short1, long1, short2, long2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(long1, result.get(0));
		assertEquals(long2, result.get(1));
		
		assertEquals(2, extractedTokens.size());
		assertEquals(short1, extractedTokens.get(0));
		assertEquals(short2, extractedTokens.get(1));
	}
	
	@Test
	void applyWithPrefixExtractFilter() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> token.value().startsWith("extract"), extractedTokens::add);
		Token keep1 = createToken("keep1");
		Token extract1 = createToken("extract1");
		Token keep2 = createToken("keep2");
		Token extract2 = createToken("extractthis");
		List<Token> tokens = List.of(keep1, extract1, keep2, extract2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(keep1, result.get(0));
		assertEquals(keep2, result.get(1));
		
		assertEquals(2, extractedTokens.size());
		assertEquals(extract1, extractedTokens.get(0));
		assertEquals(extract2, extractedTokens.get(1));
	}
	
	@Test
	void applyWithNumericExtractFilter() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> token.value().matches("\\d+"), extractedTokens::add);
		Token text1 = createToken("abc");
		Token number1 = createToken("123");
		Token text2 = createToken("def");
		Token number2 = createToken("456");
		List<Token> tokens = List.of(text1, number1, text2, number2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(text1, result.get(0));
		assertEquals(text2, result.get(1));
		
		assertEquals(2, extractedTokens.size());
		assertEquals(number1, extractedTokens.get(0));
		assertEquals(number2, extractedTokens.get(1));
	}
	
	@Test
	void applyWithSingleTokenToExtract() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> token.value().equals("extract"), extractedTokens::add);
		Token extract = createToken("extract");
		List<Token> tokens = List.of(extract);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertEquals(1, extractedTokens.size());
		assertEquals(extract, extractedTokens.get(0));
	}
	
	@Test
	void applyWithSingleTokenToKeep() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token -> token.value().equals("extract"), extractedTokens::add);
		Token keep = createToken("keep");
		List<Token> tokens = List.of(keep);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(keep, result.getFirst());
		assertTrue(extractedTokens.isEmpty());
	}
	
	@Test
	void applyWithComplexExtractFilter() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(token ->
			token.value().length() < 3 ||
				token.value().contains("bad") ||
				token.value().matches("\\d+"),
			extractedTokens::add
		);
		
		Token keep1 = createToken("good");
		Token extract1 = createToken("no");
		Token keep2 = createToken("excellent");
		Token extract2 = createToken("badword");
		Token extract3 = createToken("123");
		Token keep3 = createToken("perfect");
		
		List<Token> tokens = List.of(keep1, extract1, keep2, extract2, extract3, keep3);
		TokenRuleMatch match = new TokenRuleMatch(0, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(keep1, result.getFirst());
		assertEquals(keep2, result.get(1));
		assertEquals(keep3, result.get(2));
		
		assertEquals(3, extractedTokens.size());
		assertEquals(extract1, extractedTokens.get(0));
		assertEquals(extract2, extractedTokens.get(1));
		assertEquals(extract3, extractedTokens.get(2));
	}
	
	@Test
	void applyWithCountingConsumer() {
		int[] extractedCount = {0};
		ExtractTokenAction action = new ExtractTokenAction(
			token -> token.value().startsWith("count"),
			token -> extractedCount[0]++
		);
		
		Token count1 = createToken("count1");
		Token keep1 = createToken("keep1");
		Token count2 = createToken("count2");
		Token count3 = createToken("count3");
		List<Token> tokens = List.of(count1, keep1, count2, count3);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(keep1, result.getFirst());
		assertEquals(3, extractedCount[0]);
	}
	
	@Test
	void applyWithValueCollectingConsumer() {
		List<String> extractedValues = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(
			token -> token.value().matches("\\d+"),
			token -> extractedValues.add(token.value())
		);
		
		Token text1 = createToken("text");
		Token number1 = createToken("42");
		Token text2 = createToken("more");
		Token number2 = createToken("123");
		List<Token> tokens = List.of(text1, number1, text2, number2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(text1, result.get(0));
		assertEquals(text2, result.get(1));
		
		assertEquals(2, extractedValues.size());
		assertEquals("42", extractedValues.get(0));
		assertEquals("123", extractedValues.get(1));
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		ExtractTokenAction action = new ExtractTokenAction(token -> false, token -> {});
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void applyPreservesTokenOrder() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(
			token -> token.value().length() == 2,
			extractedTokens::add
		);
		Token a = createToken("a");
		Token bb = createToken("bb");
		Token c = createToken("c");
		Token dd = createToken("dd");
		Token e = createToken("e");
		List<Token> tokens = List.of(a, bb, c, dd, e);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(a, result.getFirst());
		assertEquals(c, result.get(1));
		assertEquals(e, result.get(2));
		
		assertEquals(2, extractedTokens.size());
		assertEquals(bb, extractedTokens.get(0));
		assertEquals(dd, extractedTokens.get(1));
	}
	
	@Test
	void applyWithAlternatingExtractKeepPattern() {
		List<Token> extractedTokens = new ArrayList<>();
		ExtractTokenAction action = new ExtractTokenAction(
			token -> token.value().startsWith("e"),
			extractedTokens::add
		);
		Token keep1 = createToken("keep1");
		Token extract1 = createToken("extract1");
		Token keep2 = createToken("keep2");
		Token extract2 = createToken("extract2");
		Token keep3 = createToken("keep3");
		List<Token> tokens = List.of(keep1, extract1, keep2, extract2, keep3);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(keep1, result.getFirst());
		assertEquals(keep2, result.get(1));
		assertEquals(keep3, result.get(2));
		
		assertEquals(2, extractedTokens.size());
		assertEquals(extract1, extractedTokens.get(0));
		assertEquals(extract2, extractedTokens.get(1));
	}
	
	@Test
	void extractActionBehavesLikeSkipActionWithExtraction() {
		List<Token> extractedTokens = new ArrayList<>();
		Predicate<Token> extractShort = token -> token.value().length() <= 3;
		SkipTokenAction skipAction = new SkipTokenAction(extractShort);
		ExtractTokenAction extractAction = new ExtractTokenAction(extractShort, extractedTokens::add);
		
		Token short1 = createToken("hi");
		Token long1 = createToken("hello");
		Token short2 = createToken("bye");
		Token long2 = createToken("world");
		List<Token> tokens = List.of(short1, long1, short2, long2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> skipResult = skipAction.apply(match);
		List<Token> extractResult = extractAction.apply(match);
		
		assertEquals(skipResult.size(), extractResult.size());
		for (int i = 0; i < skipResult.size(); i++) {
			assertEquals(skipResult.get(i), extractResult.get(i));
		}
		
		assertEquals(2, extractedTokens.size());
		assertEquals(short1, extractedTokens.get(0));
		assertEquals(short2, extractedTokens.get(1));
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Predicate<Token> filter = token -> token.value().length() < 5;
		Consumer<Token> consumer = token -> {};
		ExtractTokenAction action1 = new ExtractTokenAction(filter, consumer);
		ExtractTokenAction action2 = new ExtractTokenAction(filter, consumer);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsFilterAndExtractorInfo() {
		ExtractTokenAction action = new ExtractTokenAction(token -> true, token -> {});
		String actionString = action.toString();
		
		assertTrue(actionString.contains("ExtractTokenAction"));
		assertTrue(actionString.contains("filter"));
		assertTrue(actionString.contains("extractor"));
	}
}
