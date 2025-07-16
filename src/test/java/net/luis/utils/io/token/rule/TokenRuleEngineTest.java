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

package net.luis.utils.io.token.rule;

import net.luis.utils.io.token.definition.StringTokenDefinition;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.actions.GroupingTokenAction;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.rule.actions.TransformTokenAction;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
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
	
	private static @NotNull TokenDefinition createDefinition(@NotNull String value) {
		return new StringTokenDefinition(value, false);
	}
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(createDefinition(value), value);
	}
	
	@Test
	void addRuleWithNullRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null));
	}
	
	@Test
	void addRuleWithValidRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertDoesNotThrow(() -> engine.addRule(TokenRules.alwaysMatch()));
	}
	
	@Test
	void addRuleWithActionAndNullRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(null, TokenAction.identity()));
	}
	
	@Test
	void addRuleWithActionAndNullAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.addRule(TokenRules.alwaysMatch(), null));
	}
	
	@Test
	void addRuleWithValidRuleAndAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertDoesNotThrow(() -> engine.addRule(TokenRules.alwaysMatch(), TokenAction.identity()));
	}
	
	@Test
	void processWithNullTokenList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		
		assertThrows(NullPointerException.class, () -> engine.process(null));
	}
	
	@Test
	void processWithEmptyTokenList() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> emptyList = Collections.emptyList();
		List<Token> result = engine.process(emptyList);
		
		assertTrue(result.isEmpty());
		assertNotSame(emptyList, result);
	}
	
	@Test
	void processWithEmptyTokenListAndRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.alwaysMatch(), TokenAction.identity());
		
		List<Token> emptyList = Collections.emptyList();
		List<Token> result = engine.process(emptyList);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void processWithNoRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(tokens.size(), result.size());
		assertEquals(tokens, result);
		assertNotSame(tokens, result);
	}
	
	@Test
	void processWithIdentityAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.alwaysMatch(), TokenAction.identity());
		
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(tokens.size(), result.size());
		assertEquals(tokens.getFirst().value(), result.getFirst().value());
	}
	
	@Test
	void processWithValidationRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(createDefinition("test"));
		
		List<Token> tokens = List.of(createToken("test"), createToken("other"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(tokens.size(), result.size());
		assertEquals(tokens, result);
	}
	
	@Test
	void processWithTransformAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenAction uppercaseAction = new TransformTokenAction(tokenList ->
			tokenList.stream().map(token -> createToken(token.value().toUpperCase())).toList()
		);
		engine.addRule(createDefinition("test"), uppercaseAction);
		
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(1, result.size());
		assertEquals("TEST", result.getFirst().value());
	}
	
	@Test
	void processWithGroupingAction() {
		TokenRuleEngine engine = new TokenRuleEngine();
		TokenDefinition groupDefinition = "testother"::equals;
		GroupingTokenAction groupingAction = new GroupingTokenAction(groupDefinition);
		engine.addRule(TokenRules.sequence(createDefinition("test"), createDefinition("other")), groupingAction);
		
		List<Token> tokens = List.of(createToken("test"), createToken("other"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(1, result.size());
		assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("testother", result.getFirst().value());
	}
	
	@Test
	void processWithMultipleRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(createDefinition("first"), new TransformTokenAction(tokenList ->
			tokenList.stream().map(token -> createToken("FIRST")).toList()
		));
		engine.addRule(createDefinition("second"), new TransformTokenAction(tokenList ->
			tokenList.stream().map(token -> createToken("SECOND")).toList()
		));
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(3, result.size());
		assertEquals("FIRST", result.getFirst().value());
		assertEquals("SECOND", result.get(1).value());
		assertEquals("third", result.get(2).value());
	}
	
	@Test
	void processWithRuleOrder() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.alwaysMatch(), new TransformTokenAction(tokenList ->
			tokenList.stream().map(token -> createToken("ALWAYS")).toList()
		));
		engine.addRule(createDefinition("test"), new TransformTokenAction(tokenList ->
			tokenList.stream().map(token -> createToken("SPECIFIC")).toList()
		));
		
		List<Token> tokens = List.of(createToken("test"));
		
		List<Token> result = engine.process(tokens);
		
		assertEquals(1, result.size());
		assertEquals("ALWAYS", result.getFirst().value());
	}
	
	@Test
	void processWithNoMatchingRules() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(createDefinition("notmatching"), TokenAction.identity());
		
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(tokens.size(), result.size());
		assertEquals(tokens, result);
	}
	
	@Test
	void processWithSequentialApplication() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.sequence(createDefinition("a"), createDefinition("b")), new TransformTokenAction(tokenList -> List.of(createToken("AB"))));
		engine.addRule(TokenRules.sequence(createDefinition("AB"), createDefinition("c")), new TransformTokenAction(tokenList -> List.of(createToken("ABC"))));
		
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(1, result.size());
		assertEquals("ABC", result.getFirst().value());
	}
	
	@Test
	void processWithOptionalRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.optional(createDefinition("optional")), TokenAction.identity());
		
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertEquals(1, result.size());
		assertEquals("test", result.getFirst().value());
	}
	
	@Test
	void processWithRepeatedRule() {
		TokenRuleEngine engine = new TokenRuleEngine();
		engine.addRule(TokenRules.repeatAtLeast(createDefinition("repeat"), 2), new GroupingTokenAction(word -> word.startsWith("repeat")));
		
		List<Token> tokens = List.of(
			createToken("repeat"),
			createToken("repeat"),
			createToken("repeat"),
			createToken("other")
		);
		
		List<Token> result = engine.process(tokens);
		
		assertEquals(2, result.size());
		assertInstanceOf(TokenGroup.class, result.getFirst());
		assertEquals("repeatrepeatrepeat", result.getFirst().value());
		assertEquals("other", result.get(1).value());
	}
	
	@Test
	void processResultIsUnmodifiable() {
		TokenRuleEngine engine = new TokenRuleEngine();
		List<Token> tokens = List.of(createToken("test"));
		List<Token> result = engine.process(tokens);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
}
