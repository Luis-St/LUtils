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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link GroupingTokenAction}.<br>
 *
 * @author Luis-St
 */
class GroupingTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value, @NotNull TokenPosition start, @NotNull TokenPosition end) {
		TokenDefinition definition = word -> word.equals(value);
		return new SimpleToken(definition, value, start, end);
	}
	
	@Test
	void constructorWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> new GroupingTokenAction(null));
	}
	
	@Test
	void constructorWithValidDefinition() {
		TokenDefinition definition = word -> true;
		
		assertDoesNotThrow(() -> new GroupingTokenAction(definition));
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition definition = "test"::equals;
		GroupingTokenAction action = new GroupingTokenAction(definition);
		
		assertEquals(definition, action.definition());
	}
	
	@Test
	void applyWithNullMatch() {
		GroupingTokenAction action = new GroupingTokenAction(word -> true);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		GroupingTokenAction action = new GroupingTokenAction(word -> true);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertThrows(IllegalArgumentException.class, () -> action.apply(match));
	}
	
	@Test
	void applyWithMultipleTokens() {
		TokenDefinition groupDefinition = "helloworld"::equals;
		GroupingTokenAction action = new GroupingTokenAction(groupDefinition);
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals(groupDefinition, group.definition());
		assertEquals("helloworld", group.value());
		assertEquals(2, group.tokens().size());
		assertEquals(token1, group.tokens().get(0));
		assertEquals(token2, group.tokens().get(1));
	}
	
	@Test
	void applyWithUnpositionedTokens() {
		TokenDefinition groupDefinition = "test"::equals;
		GroupingTokenAction action = new GroupingTokenAction(groupDefinition);
		Token token0 = createToken("te", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		Token token1 = createToken("st", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(token0, token1), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("test", group.value());
		assertFalse(group.isPositioned());
	}
	
	@Test
	void applyWithMixedPositionedTokens() {
		TokenDefinition groupDefinition = "ab"::equals;
		GroupingTokenAction action = new GroupingTokenAction(groupDefinition);
		Token positionedToken = createToken("a", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token unpositionedToken = createToken("b", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		List<Token> tokens = List.of(positionedToken, unpositionedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("ab", group.value());
		assertFalse(group.isPositioned());
	}
	
	@Test
	void applyWithNumericTokens() {
		TokenDefinition groupDefinition = word -> word.matches("\\d+");
		GroupingTokenAction action = new GroupingTokenAction(groupDefinition);
		Token token1 = createToken("1", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token token2 = createToken("2", new TokenPosition(0, 1, 1), new TokenPosition(0, 1, 1));
		Token token3 = createToken("3", new TokenPosition(0, 2, 2), new TokenPosition(0, 2, 2));
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		TokenGroup group = assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("123", group.value());
		assertEquals(3, group.tokens().size());
	}
	
	@Test
	void applyWithSpecialCharacterTokens() {
		TokenDefinition groupDefinition = "()"::equals;
		GroupingTokenAction action = new GroupingTokenAction(groupDefinition);
		Token token1 = createToken("(", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token token2 = createToken(")", new TokenPosition(0, 1, 1), new TokenPosition(0, 1, 1));
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("()", result.getFirst().value());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		GroupingTokenAction action = new GroupingTokenAction(word -> true);
		Token token = createToken("test", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, List.of(token, token), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(token));
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		TokenDefinition definition = "test"::equals;
		GroupingTokenAction action1 = new GroupingTokenAction(definition);
		GroupingTokenAction action2 = new GroupingTokenAction(definition);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
