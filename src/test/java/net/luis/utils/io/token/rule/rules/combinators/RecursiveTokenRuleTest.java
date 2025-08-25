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
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the enhanced {@link RecursiveTokenRule}.<br>
 *
 * @author Luis-St
 */
class RecursiveTokenRuleTest {
	
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
	void constructorWithNullRuleFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null));
	}
	
	@Test
	void constructorWithRuleFactoryReturningNull() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(self -> null));
	}
	
	@Test
	void constructorWithValidRuleFactory() {
		assertDoesNotThrow(() -> new RecursiveTokenRule(self -> createRule("test")));
	}
	
	@Test
	void constructorWithNullOpeningRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule("content"), createRule("end")));
	}
	
	@Test
	void constructorWithNullContentRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("start"), null, createRule("end")));
	}
	
	@Test
	void constructorWithNullClosingRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("start"), createRule("content"), (TokenRule) null));
	}
	
	@Test
	void constructorWithValidRules() {
		assertDoesNotThrow(() -> new RecursiveTokenRule(createRule("start"), createRule("content"), createRule("end")));
	}
	
	@Test
	void factoryConstructorWithNullContentRuleFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("start"), createRule("end"), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void getTokenRuleReturnsCorrectRule() {
		TokenRule expectedRule = createRule("test");
		RecursiveTokenRule recursiveRule = new RecursiveTokenRule(self -> expectedRule);
		
		assertEquals(expectedRule, recursiveRule.getTokenRule());
	}
	
	@Test
	void matchWithNullTokenStream() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> createRule("test"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void simpleNonRecursiveMatch() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> createRule("hello"));
		TokenRuleMatch match = rule.match(new TokenStream(List.of(createToken("hello"))));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals("hello", match.matchedTokens().getFirst().value());
	}
	
	@Test
	void simpleRecursiveParenthesesMatch() {
		// Grammar: Expr ::= "value" | "(" Expr ")"
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				createRule("value"),
				TokenRules.sequence(
					createRule("("),
					self,
					createRule(")")
				)
			)
		);
		
		// Test simple value
		List<Token> simpleTokens = List.of(createToken("value"));
		TokenRuleMatch simpleMatch = rule.match(new TokenStream(simpleTokens));
		assertNotNull(simpleMatch);
		assertEquals(1, simpleMatch.matchedTokens().size());
		
		// Test nested parentheses: (value)
		List<Token> nestedTokens = List.of(
			createToken("("), createToken("value"), createToken(")")
		);
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nestedTokens));
		assertNotNull(nestedMatch);
		assertEquals(3, nestedMatch.matchedTokens().size());
		
		// Test double nested: ((value))
		List<Token> doubleNestedTokens = List.of(
			createToken("("), createToken("("), createToken("value"), createToken(")"), createToken(")")
		);
		TokenRuleMatch doubleNestedMatch = rule.match(new TokenStream(doubleNestedTokens));
		assertNotNull(doubleNestedMatch);
		assertEquals(5, doubleNestedMatch.matchedTokens().size());
	}
	
	@Test
	void annotationValueGrammarMatch() {
		// Grammar: AnnotationValue ::= "primitive" | "identifier" | "{" AnnotationValue ("," AnnotationValue)* "}"
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				createRule("primitive"),
				createRule("identifier"),
				TokenRules.sequence(
					createRule("{"),
					TokenRules.separatedList(self, createRule(",")),
					createRule("}")
				)
			)
		);
		
		// Test primitive
		List<Token> primitiveTokens = List.of(createToken("primitive"));
		TokenRuleMatch primitiveMatch = rule.match(new TokenStream(primitiveTokens));
		assertNotNull(primitiveMatch);
		
		// Test simple array: {primitive}
		List<Token> simpleArrayTokens = List.of(
			createToken("{"), createToken("primitive"), createToken("}")
		);
		TokenRuleMatch simpleArrayMatch = rule.match(new TokenStream(simpleArrayTokens));
		assertNotNull(simpleArrayMatch);
		assertEquals(3, simpleArrayMatch.matchedTokens().size());
		
		// Test multiple elements: {primitive, identifier}
		List<Token> multipleTokens = List.of(
			createToken("{"), createToken("primitive"), createToken(","),
			createToken("identifier"), createToken("}")
		);
		TokenRuleMatch multipleMatch = rule.match(new TokenStream(multipleTokens));
		assertNotNull(multipleMatch);
		assertEquals(5, multipleMatch.matchedTokens().size());
		
		// Test nested: {primitive, {identifier}}
		List<Token> nestedTokens = List.of(
			createToken("{"), createToken("primitive"), createToken(","),
			createToken("{"), createToken("identifier"), createToken("}"),
			createToken("}")
		);
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nestedTokens));
		assertNotNull(nestedMatch);
		assertEquals(7, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void jsonValueGrammarMatch() {
		// Simplified JSON: JsonValue ::= "string" | "number" | "[" (JsonValue ("," JsonValue)*)? "]"
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				createRule("string"),
				createRule("number"),
				TokenRules.sequence(
					createRule("["),
					TokenRules.separatedList(self, createRule(",")).optional(),
					createRule("]")
				)
			)
		);
		
		// Test empty array: []
		List<Token> emptyArrayTokens = List.of(createToken("["), createToken("]"));
		TokenRuleMatch emptyArrayMatch = rule.match(new TokenStream(emptyArrayTokens));
		assertNotNull(emptyArrayMatch);
		assertEquals(2, emptyArrayMatch.matchedTokens().size());
		
		// Test single element array: ["string"]
		List<Token> singleElementTokens = List.of(
			createToken("["), createToken("string"), createToken("]")
		);
		TokenRuleMatch singleElementMatch = rule.match(new TokenStream(singleElementTokens));
		assertNotNull(singleElementMatch);
		assertEquals(3, singleElementMatch.matchedTokens().size());
		
		// Test nested array: [["string"], "number"]
		List<Token> nestedArrayTokens = List.of(
			createToken("["),
			createToken("["), createToken("string"), createToken("]"),
			createToken(","),
			createToken("number"),
			createToken("]")
		);
		TokenRuleMatch nestedArrayMatch = rule.match(new TokenStream(nestedArrayTokens));
		assertNotNull(nestedArrayMatch);
		assertEquals(7, nestedArrayMatch.matchedTokens().size());
	}
	
	@Test
	void scopeBehavior() {
		RecursiveTokenRule oldStyleRule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		TokenRuleMatch match = oldStyleRule.match(new TokenStream(List.of(
			createToken("("), createToken("content"), createToken(")")
		)));
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals("(", match.matchedTokens().get(0).value());
		assertEquals("content", match.matchedTokens().get(1).value());
		assertEquals(")", match.matchedTokens().get(2).value());
	}
	
	@Test
	void factoryConstructorBehavior() {
		// Test the factory constructor with recursive content
		RecursiveTokenRule factoryRule = new RecursiveTokenRule(
			createRule("("),
			createRule(")"),
			(Function<TokenRule, TokenRule>) self -> TokenRules.any(
				createRule("value"),
				self
			)
		);
		
		// Test nested: (value)
		List<Token> nestedTokens = List.of(
			createToken("("), createToken("value"), createToken(")")
		);
		TokenRuleMatch nestedMatch = factoryRule.match(new TokenStream(nestedTokens));
		assertNotNull(nestedMatch);
		assertEquals(3, nestedMatch.matchedTokens().size());
		
		// Test double nested: ((value))
		List<Token> doubleNestedTokens = List.of(
			createToken("("), createToken("("), createToken("value"),
			createToken(")"), createToken(")")
		);
		TokenRuleMatch doubleNestedMatch = factoryRule.match(new TokenStream(doubleNestedTokens));
		assertNotNull(doubleNestedMatch);
		assertEquals(5, doubleNestedMatch.matchedTokens().size());
	}
	
	@Test
	void noMatchScenarios() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				createRule("valid"),
				TokenRules.sequence(
					createRule("("), self, createRule(")")
				)
			)
		);
		
		List<Token> invalidTokens = List.of(createToken("invalid"));
		TokenRuleMatch invalidMatch = rule.match(new TokenStream(invalidTokens));
		assertNull(invalidMatch);
		
		List<Token> incompleteTokens = List.of(createToken("("), createToken("valid"));
		TokenRuleMatch incompleteMatch = rule.match(new TokenStream(incompleteTokens));
		assertNull(incompleteMatch);
		
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(Collections.emptyList()));
		assertNull(emptyMatch);
	}
	
	@Test
	void streamPositionHandling() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> createRule("test"));
		TokenStream stream = new TokenStream(List.of(
			createToken("prefix"), createToken("test"), createToken("suffix")
		));
		
		TokenRuleMatch noMatch = rule.match(stream);
		assertNull(noMatch);
		assertEquals(0, stream.getCurrentIndex());
		
		stream.consumeToken();
		TokenRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void complexNestedRecursion() {
		// Grammar: List ::= Element | "[" (List ("," List)*)? "]"
		// Element ::= "item"
		RecursiveTokenRule rule = new RecursiveTokenRule(self ->
			TokenRules.any(
				createRule("item"),
				TokenRules.sequence(
					createRule("["),
					TokenRules.separatedList(self, createRule(",")).optional(),
					createRule("]")
				)
			)
		);
		
		// Test deeply nested: [item, [item, [item]]]
		List<Token> deeplyNestedTokens = List.of(
			createToken("["),
			createToken("item"),
			createToken(","),
			createToken("["),
			createToken("item"),
			createToken(","),
			createToken("["),
			createToken("item"),
			createToken("]"),
			createToken("]"),
			createToken("]")
		);
		
		TokenRuleMatch deepMatch = rule.match(new TokenStream(deeplyNestedTokens));
		assertNotNull(deepMatch);
		assertEquals(11, deepMatch.matchedTokens().size());
	}
	
	@Test
	void equalityAndHashCode() {
		TokenRule simpleRule = createRule("test");
		
		RecursiveTokenRule rule1 = new RecursiveTokenRule(self -> simpleRule);
		RecursiveTokenRule rule2 = new RecursiveTokenRule(self -> simpleRule);
		
		assertEquals(rule1, rule2);
		
		assertEquals(rule1, rule1);
		assertEquals(rule1.hashCode(), rule1.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		RecursiveTokenRule rule = new RecursiveTokenRule(self -> createRule("test"));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("RecursiveTokenRule"));
		assertTrue(ruleString.contains("tokenRule="));
	}
}
