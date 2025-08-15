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

package net.luis.utils.io.token.rule.actions.transformers;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.actions.core.TokenDefinitionProvider;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SplitTokenAction}.<br>
 *
 * @author Luis-St
 */
class SplitTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull Token createPositionedToken() {
		return new SimpleToken("a,b,c"::equals, "a,b,c", new TokenPosition(0, 0, 0));
	}
	
	@Test
	void constructorWithNullPattern() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((Pattern) null, provider));
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((String) null, provider));
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((Pattern) null));
		assertThrows(NullPointerException.class, () -> new SplitTokenAction((String) null));
	}
	
	@Test
	void constructorWithNullDefinitionProvider() {
		Pattern pattern = Pattern.compile("\\s+");
		assertThrows(NullPointerException.class, () -> new SplitTokenAction(pattern, null));
		assertThrows(NullPointerException.class, () -> new SplitTokenAction("\\s+", null));
	}
	
	@Test
	void constructorWithValidPattern() {
		Pattern pattern = Pattern.compile("\\s+");
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		
		assertDoesNotThrow(() -> new SplitTokenAction(pattern, provider));
		assertDoesNotThrow(() -> new SplitTokenAction(pattern));
	}
	
	@Test
	void constructorWithValidString() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		
		assertDoesNotThrow(() -> new SplitTokenAction("\\s+", provider));
		assertDoesNotThrow(() -> new SplitTokenAction("\\s+"));
	}
	
	@Test
	void splitPatternReturnsCorrectPattern() {
		Pattern pattern = Pattern.compile(",");
		SplitTokenAction action = new SplitTokenAction(pattern);
		
		assertEquals(pattern, action.splitPattern());
	}
	
	@Test
	void definitionProviderReturnsCorrectProvider() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		SplitTokenAction action = new SplitTokenAction(",", provider);
		
		assertEquals(provider, action.definitionProvider());
	}
	
	@Test
	void applyWithNullMatch() {
		SplitTokenAction action = new SplitTokenAction(",");
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		SplitTokenAction action = new SplitTokenAction(",");
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithCommaSplit() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("a,b,c");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
		
		for (Token resultToken : result) {
			assertTrue(resultToken.definition().matches(resultToken.value()));
		}
	}
	
	@Test
	void applyWithCustomDefinitionProvider() {
		TokenDefinitionProvider customProvider = value -> {
			if (value.matches("\\d+")) {
				return word -> word.matches("\\d+");
			} else {
				return word -> word.matches("[a-zA-Z]+");
			}
		};
		
		SplitTokenAction action = new SplitTokenAction(",", customProvider);
		Token token = createToken("abc,123,def");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("abc", result.get(0).value());
		assertEquals("123", result.get(1).value());
		assertEquals("def", result.get(2).value());
		
		assertTrue(result.get(0).definition().matches("abc"));
		assertFalse(result.get(0).definition().matches("123"));
		assertTrue(result.get(1).definition().matches("123"));
		assertFalse(result.get(1).definition().matches("abc"));
		assertTrue(result.get(2).definition().matches("def"));
	}
	
	@Test
	void applyWithConstantDefinitionProvider() {
		TokenDefinition constantDefinition = word -> !word.isEmpty();
		TokenDefinitionProvider constantProvider = TokenDefinitionProvider.constant(constantDefinition);
		SplitTokenAction action = new SplitTokenAction(",", constantProvider);
		Token token = createToken("a,bb,ccc");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		for (Token resultToken : result) {
			assertSame(constantDefinition, resultToken.definition());
		}
	}
	
	@Test
	void applyWithPatternBasedProvider() {
		SplitTokenAction action = new SplitTokenAction(",", TokenDefinitionProvider.patternBased());
		Token token = createToken("word,123,symbol!");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("word", result.get(0).value());
		assertEquals("123", result.get(1).value());
		assertEquals("symbol!", result.get(2).value());
		
		assertTrue(result.get(0).definition().matches("word"));
		assertFalse(result.get(0).definition().matches("123"));
		assertTrue(result.get(1).definition().matches("123"));
		assertFalse(result.get(1).definition().matches("word"));
		assertTrue(result.get(2).definition().matches("symbol!"));
	}
	
	@Test
	void applyWithSpaceSplit() {
		SplitTokenAction action = new SplitTokenAction("\\s+");
		Token token = createToken("hello world test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("hello", result.get(0).value());
		assertEquals("world", result.get(1).value());
		assertEquals("test", result.get(2).value());
	}
	
	@Test
	void applyWithNoSplitMatch() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("nosplithere");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("nosplithere", result.getFirst().value());
	}
	
	@Test
	void applyWithEmptyParts() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("a,,b");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
	}
	
	@Test
	void applyWithLeadingAndTrailingSeparators() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken(",a,b,");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token1 = createToken("a,b");
		Token token2 = createToken("c,d,e");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(5, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
		assertEquals("d", result.get(3).value());
		assertEquals("e", result.get(4).value());
	}
	
	@Test
	void applyWithPositionedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createPositionedToken();
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		
		Token firstPart = result.getFirst();
		assertEquals("a", firstPart.value());
		assertEquals(0, firstPart.position().character());
		
		Token secondPart = result.get(1);
		assertEquals("b", secondPart.value());
		assertEquals(2, secondPart.position().character());
		
		Token thirdPart = result.get(2);
		assertEquals("c", thirdPart.value());
		assertEquals(4, thirdPart.position().character());
	}
	
	@Test
	void applyWithAnnotatedToken() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token baseToken = createToken("x,y");
		AnnotatedToken annotated = AnnotatedToken.of(baseToken, "type", "test");
		List<Token> tokens = List.of(annotated);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("x", result.get(0).value());
		assertEquals("y", result.get(1).value());
		
		for (Token resultToken : result) {
			assertInstanceOf(AnnotatedToken.class, resultToken);
			AnnotatedToken annotatedResult = (AnnotatedToken) resultToken;
			assertEquals("test", annotatedResult.getMetadata("type"));
		}
	}
	
	@Test
	void applyPreservesDefinitionProviderBehavior() {
		TokenDefinition strictDefinition = "a,b,c"::equals;
		Token strictToken = SimpleToken.createUnpositioned(strictDefinition, "a,b,c");
		
		SplitTokenAction flexibleAction = new SplitTokenAction(",", TokenDefinitionProvider.acceptAll());
		List<Token> flexibleResult = flexibleAction.apply(
			new TokenRuleMatch(0, 1, List.of(strictToken), TokenRules.alwaysMatch())
		);
		
		assertEquals(3, flexibleResult.size());
		for (Token resultToken : flexibleResult) {
			assertTrue(resultToken.definition().matches(resultToken.value()));
		}
		
		SplitTokenAction patternAction = new SplitTokenAction(",", TokenDefinitionProvider.patternBased());
		List<Token> patternResult = patternAction.apply(
			new TokenRuleMatch(0, 1, List.of(strictToken), TokenRules.alwaysMatch())
		);
		
		assertEquals(3, patternResult.size());
		for (Token resultToken : patternResult) {
			assertTrue(resultToken.definition().matches(resultToken.value()));
		}
	}
	
	@Test
	void applyWithIndexedToken() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token baseToken = createToken("p,q");
		IndexedToken indexed = new IndexedToken(baseToken, 5);
		List<Token> tokens = List.of(indexed);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("p", result.get(0).value());
		assertEquals("q", result.get(1).value());
		
		for (Token resultToken : result) {
			assertInstanceOf(IndexedToken.class, resultToken);
			IndexedToken indexedResult = (IndexedToken) resultToken;
			assertEquals(5, indexedResult.index());
		}
	}
	
	@Test
	void applyWithComplexPattern() {
		SplitTokenAction action = new SplitTokenAction("[,;:]");
		Token token = createToken("a,b;c:d");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(4, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
		assertEquals("d", result.get(3).value());
	}
	
	@Test
	void applyWithDigitPattern() {
		SplitTokenAction action = new SplitTokenAction("\\d+");
		Token token = createToken("abc123def456ghi");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("abc", result.get(0).value());
		assertEquals("def", result.get(1).value());
		assertEquals("ghi", result.get(2).value());
	}
	
	@Test
	void applyDoesNotPreserveTokenDefinition() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("a,b");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		for (Token resultToken : result) {
			assertNotEquals(token.definition(), resultToken.definition());
		}
	}
	
	@Test
	void applyWithUnpositionedTokens() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("x,y,z");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		for (Token resultToken : result) {
			assertEquals(TokenPosition.UNPOSITIONED, resultToken.position());
		}
	}
	
	@Test
	void applyWithSingleCharacterSplit() {
		SplitTokenAction action = new SplitTokenAction("a");
		Token token = createToken("banana");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("b", result.get(0).value());
		assertEquals("n", result.get(1).value());
		assertEquals("n", result.get(2).value());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		SplitTokenAction action = new SplitTokenAction(",");
		Token token = createToken("a,b");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void applyWithSpecialCharacters() {
		SplitTokenAction action = new SplitTokenAction("\\|");
		Token token = createToken("a|b|c");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("a", result.get(0).value());
		assertEquals("b", result.get(1).value());
		assertEquals("c", result.get(2).value());
	}
	
	@Test
	void definitionProviderWithStrictPatternMatching() {
		TokenDefinitionProvider strictProvider = value -> {
			if (value.matches("\\d+")) {
				return word -> word.matches("\\d+");
			} else if (value.matches("[a-zA-Z]+")) {
				return word -> word.matches("[a-zA-Z]+");
			} else {
				throw new IllegalArgumentException("Unsupported value pattern: " + value);
			}
		};
		
		SplitTokenAction action = new SplitTokenAction(",", strictProvider);
		
		Token validToken = createToken("abc,123,def");
		List<Token> validResult = action.apply(
			new TokenRuleMatch(0, 1, List.of(validToken), TokenRules.alwaysMatch())
		);
		
		assertEquals(3, validResult.size());
		assertEquals("abc", validResult.get(0).value());
		assertEquals("123", validResult.get(1).value());
		assertEquals("def", validResult.get(2).value());
		
		assertTrue(validResult.get(0).definition().matches("abc"));
		assertTrue(validResult.get(1).definition().matches("123"));
		assertTrue(validResult.get(2).definition().matches("def"));
		
		assertFalse(validResult.get(0).definition().matches("123"));
		assertFalse(validResult.get(1).definition().matches("abc"));
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Pattern pattern = Pattern.compile(",");
		SplitTokenAction action1 = new SplitTokenAction(pattern);
		SplitTokenAction action2 = new SplitTokenAction(pattern);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsPatternInfo() {
		SplitTokenAction action = new SplitTokenAction(",");
		String actionString = action.toString();
		
		assertTrue(actionString.contains("SplitTokenAction"));
		assertTrue(actionString.contains("splitPattern"));
	}
}
