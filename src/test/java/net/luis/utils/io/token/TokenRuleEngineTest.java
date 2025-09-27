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

package net.luis.utils.io.token;

import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.*;
import net.luis.utils.io.token.rules.matchers.ValueTokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleEngine}.<br>
 *
 * @author Luis-St
 */
class TokenRuleEngineTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorDefault() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertNotNull(engine);
	}
	
	@Test
	void constructorWithNullContext() {
		assertThrows(NullPointerException.class, () -> new TokenRuleEngine(null));
	}
	
	@Test
	void constructorWithValidContext() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRuleEngine engine = new TokenRuleEngine(context);
		
		assertNotNull(engine);
	}
	
	@Test
	void addRuleWithNullRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null));
	}
	
	@Test
	void addRuleWithValidRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertDoesNotThrow(() -> engine.addRule(rule));
	}
	
	@Test
	void addRuleWithNullRuleAndAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenAction action = TokenAction.identity();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null, action));
	}
	
	@Test
	void addRuleWithRuleAndNullAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> engine.addRule(rule, null));
	}
	
	@Test
	void addRuleWithValidRuleAndAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenAction action = TokenAction.identity();
		
		assertDoesNotThrow(() -> engine.addRule(rule, action));
	}
	
	@Test
	void processWithNullTokens() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.process(null));
	}
	
	@Test
	void processWithEmptyTokensAndNoRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> emptyTokens = List.of();
		
		List<Token> result = engine.process(emptyTokens);
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void processWithTokensAndNoRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("hello", result.getFirst().value());
		assertEquals("world", result.get(1).value());
	}
	
	@Test
	void processWithTokensAndNeverMatchingRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(NeverMatchTokenRule.INSTANCE);
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("hello", result.getFirst().value());
		assertEquals("world", result.get(1).value());
	}
	
	@Test
	void processWithTokensAndAlwaysMatchingRuleWithIdentityAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(AlwaysMatchTokenRule.INSTANCE, TokenAction.identity());
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("hello", result.getFirst().value());
		assertEquals("world", result.get(1).value());
	}
	
	@Test
	void processWithTokensAndAlwaysMatchingRuleWithRemovalAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenAction removalAction = (match, ctx) -> Collections.emptyList();
		engine.addRule(AlwaysMatchTokenRule.INSTANCE, removalAction);
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void processWithTokensAndAlwaysMatchingRuleWithReplacementAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		Token replacementToken = createToken("REPLACED");
		TokenAction replacementAction = (match, ctx) -> List.of(replacementToken);
		engine.addRule(AlwaysMatchTokenRule.INSTANCE, replacementAction);
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("REPLACED", result.getFirst().value());
		assertEquals("REPLACED", result.get(1).value());
	}
	
	@Test
	void processWithSpecificValueMatchingRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule valueRule = new ValueTokenRule("hello", false);
		Token replacementToken = createToken("HELLO");
		TokenAction replacementAction = (match, ctx) -> List.of(replacementToken);
		engine.addRule(valueRule, replacementAction);
		
		List<Token> tokens = List.of(createToken("hello"), createToken("world"), createToken("hello"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("HELLO", result.getFirst().value());
		assertEquals("world", result.get(1).value());
		assertEquals("HELLO", result.get(2).value());
	}
	
	@Test
	void processWithMultipleRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		TokenRule helloRule = new ValueTokenRule("hello", false);
		TokenRule worldRule = new ValueTokenRule("world", false);
		TokenAction upperCaseAction = (match, ctx) -> List.of(createToken(match.matchedTokens().getFirst().value().toUpperCase()));
		
		engine.addRule(helloRule, upperCaseAction);
		engine.addRule(worldRule, upperCaseAction);
		
		List<Token> tokens = List.of(createToken("hello"), createToken("test"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals("HELLO", result.getFirst().value());
		assertEquals("test", result.get(1).value());
		assertEquals("WORLD", result.get(2).value());
	}
	
	@Test
	void processWithRuleOrderMatters() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		TokenRule firstRule = new ValueTokenRule("test", false);
		TokenRule secondRule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenAction firstAction = (match, ctx) -> List.of(createToken("FIRST"));
		TokenAction secondAction = (match, ctx) -> List.of(createToken("SECOND"));
		
		engine.addRule(firstRule, firstAction);
		engine.addRule(secondRule, secondAction);
		
		List<Token> tokens = List.of(createToken("test"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("SECOND", result.getFirst().value());
	}
	
	@Test
	void processWithActionThatCreatesMultipleTokens() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenRule rule = new ValueTokenRule("split", false);
		TokenAction splitAction = (match, ctx) -> List.of(createToken("part1"), createToken("part2"));
		engine.addRule(rule, splitAction);
		
		List<Token> tokens = List.of(createToken("hello"), createToken("split"), createToken("world"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(4, result.size());
		assertEquals("hello", result.getFirst().value());
		assertEquals("part1", result.get(1).value());
		assertEquals("part2", result.get(2).value());
		assertEquals("world", result.get(3).value());
	}
	
	@Test
	void processWithSingleTokenRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(AlwaysMatchTokenRule.INSTANCE);
		
		List<Token> tokens = List.of(createToken("single"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("single", result.getFirst().value());
	}
	
	@Test
	void processWithCustomContext() {
		TokenRuleContext context = TokenRuleContext.empty();
		TokenRuleEngine engine = new TokenRuleEngine(context);
		
		engine.addRule(AlwaysMatchTokenRule.INSTANCE, TokenAction.identity());
		
		List<Token> tokens = List.of(createToken("test"));
		
		List<Token> result = engine.process(tokens);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("test", result.getFirst().value());
	}
	
	@Test
	void processReturnsUnmodifiableList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> tokens = List.of(createToken("test"));
		
		List<Token> result = engine.process(tokens);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, () -> result.remove(0));
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void processWithEmptyTokensAndRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(AlwaysMatchTokenRule.INSTANCE, TokenAction.identity());
		
		List<Token> emptyTokens = List.of();
		
		List<Token> result = engine.process(emptyTokens);
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
}
