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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RecursiveTokenRule}.<br>
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
	void constructorWithNullOpeningRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(null, createRule("content"), createRule(")")));
	}
	
	@Test
	void constructorWithNullContentRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), null, createRule(")")));
	}
	
	@Test
	void constructorWithNullClosingRule() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule("content"), (TokenRule) null));
	}
	
	@Test
	void constructorWithNullContentRuleFactory() {
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) null));
	}
	
	@Test
	void constructorWithContentRuleFactoryReturningNull() {
		Function<TokenRule, TokenRule> nullFactory = recursiveRule -> null;
		assertThrows(NullPointerException.class, () -> new RecursiveTokenRule(createRule("("), createRule(")"), nullFactory));
	}
	
	@Test
	void constructorWithValidRules() {
		assertDoesNotThrow(() -> new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")")));
	}
	
	@Test
	void constructorWithValidContentRuleFactory() {
		Function<TokenRule, TokenRule> factory = recursiveRule -> createRule("content");
		assertDoesNotThrow(() -> new RecursiveTokenRule(createRule("("), createRule(")"), factory));
	}
	
	@Test
	void openingRuleReturnsCorrectRule() {
		TokenRule openingRule = createRule("(");
		RecursiveTokenRule recursiveRule = new RecursiveTokenRule(openingRule, createRule("content"), createRule(")"));
		
		assertEquals(openingRule, recursiveRule.openingRule());
	}
	
	@Test
	void contentRuleReturnsCorrectRule() {
		TokenRule contentRule = createRule("content");
		RecursiveTokenRule recursiveRule = new RecursiveTokenRule(createRule("("), contentRule, createRule(")"));
		
		assertEquals(contentRule, recursiveRule.contentRule());
	}
	
	@Test
	void closingRuleReturnsCorrectRule() {
		TokenRule closingRule = createRule(")");
		RecursiveTokenRule recursiveRule = new RecursiveTokenRule(createRule("("), createRule("content"), closingRule);
		
		assertEquals(closingRule, recursiveRule.closingRule());
	}
	
	@Test
	void matchWithNullTokenStream() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithNoOpeningMatch() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("other"), createToken("content"), createToken(")"));
		
		assertNull(rule.match(new TokenStream(tokens)));
	}
	
	@Test
	void matchWithOpeningButNoContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("other"), createToken(")"));
		
		assertNull(rule.match(new TokenStream(tokens)));
	}
	
	@Test
	void matchWithOpeningAndContentButNoClosing() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("content"), createToken("other"));
		
		assertNull(rule.match(new TokenStream(tokens)));
	}
	
	@Test
	void matchWithSimpleNonRecursiveStructure() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		Token opening = createToken("(");
		Token content = createToken("content");
		Token closing = createToken(")");
		List<Token> tokens = List.of(opening, content, closing);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals(3, match.matchedTokens().size());
		assertEquals(opening, match.matchedTokens().get(0));
		assertEquals(content, match.matchedTokens().get(1));
		assertEquals(closing, match.matchedTokens().get(2));
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithEmptyContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.alwaysMatch().not());
		Token opening = createToken("(");
		Token closing = createToken(")");
		List<Token> tokens = List.of(opening, closing);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(opening, match.matchedTokens().get(0));
		assertEquals(closing, match.matchedTokens().get(1));
	}
	
	@Test
	void matchWithSingleLevelRecursion() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("content"), self));
		
		// Test: (content)
		List<Token> simple = List.of(createToken("("), createToken("content"), createToken(")"));
		TokenRuleMatch simpleMatch = rule.match(new TokenStream(simple));
		assertNotNull(simpleMatch);
		assertEquals(3, simpleMatch.matchedTokens().size());
		
		// Test: (())
		List<Token> nested = List.of(createToken("("), createToken("("), createToken(")"), createToken(")"));
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nested));
		assertNotNull(nestedMatch);
		assertEquals(4, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithMultipleLevelRecursion() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("content"), self));
		
		// Test: ((()))
		List<Token> tripleNested = List.of(
			createToken("("), createToken("("), createToken("("), createToken(")"), createToken(")"), createToken(")")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tripleNested));
		
		assertNotNull(match);
		assertEquals(6, match.matchedTokens().size());
		for (int i = 0; i < 6; i++) {
			assertEquals(tripleNested.get(i), match.matchedTokens().get(i));
		}
	}
	
	@Test
	void matchWithMixedContentAndRecursion() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("content"), self).repeatInfinitely());
		
		// Test: (content(content)content)
		List<Token> mixed = List.of(
			createToken("("), createToken("content"), createToken("("), createToken("content"),
			createToken(")"), createToken("content"), createToken(")")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(mixed));
		
		assertNotNull(match);
		assertEquals(7, match.matchedTokens().size());
	}
	
	@Test
	void matchWithDifferentDelimiters() {
		RecursiveTokenRule bracketRule = new RecursiveTokenRule(createRule("["), createRule("]"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("item"), self));
		
		List<Token> brackets = List.of(createToken("["), createToken("["), createToken("item"), createToken("]"), createToken("]"));
		
		TokenRuleMatch match = bracketRule.match(new TokenStream(brackets));
		
		assertNotNull(match);
		assertEquals(5, match.matchedTokens().size());
	}
	
	@Test
	void matchWithOptionalContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("{"), createRule("}"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("value"), self).optional());
		
		// Test: {}
		List<Token> empty = List.of(createToken("{"), createToken("}"));
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(empty));
		assertNotNull(emptyMatch);
		assertEquals(2, emptyMatch.matchedTokens().size());
		
		// Test: {value}
		List<Token> withValue = List.of(createToken("{"), createToken("value"), createToken("}"));
		TokenRuleMatch valueMatch = rule.match(new TokenStream(withValue));
		assertNotNull(valueMatch);
		assertEquals(3, valueMatch.matchedTokens().size());
		
		// Test: {{}}
		List<Token> nested = List.of(createToken("{"), createToken("{"), createToken("}"), createToken("}"));
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nested));
		assertNotNull(nestedMatch);
		assertEquals(4, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithRepeatedContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("<"), createRule(">"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("x"), self).repeatAtLeast(1));
		
		// Test: <x>
		List<Token> single = List.of(createToken("<"), createToken("x"), createToken(">"));
		TokenRuleMatch singleMatch = rule.match(new TokenStream(single));
		assertNotNull(singleMatch);
		assertEquals(3, singleMatch.matchedTokens().size());
		
		// Test: <xxx>
		List<Token> multiple = List.of(createToken("<"), createToken("x"), createToken("x"), createToken("x"), createToken(">"));
		TokenRuleMatch multipleMatch = rule.match(new TokenStream(multiple));
		assertNotNull(multipleMatch);
		assertEquals(5, multipleMatch.matchedTokens().size());
		
		// Test: <x<x>x>
		List<Token> mixed = List.of(createToken("<"), createToken("x"), createToken("<"), createToken("x"), createToken(">"), createToken("x"), createToken(">"));
		TokenRuleMatch mixedMatch = rule.match(new TokenStream(mixed));
		assertNotNull(mixedMatch);
		assertEquals(7, mixedMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithSequenceContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(
			TokenRules.sequence(createRule("a"), createRule("b")),
			self
		));
		
		// Test: (ab)
		List<Token> sequence = List.of(createToken("("), createToken("a"), createToken("b"), createToken(")"));
		TokenRuleMatch sequenceMatch = rule.match(new TokenStream(sequence));
		assertNotNull(sequenceMatch);
		assertEquals(4, sequenceMatch.matchedTokens().size());
		
		// Test: ((ab))
		List<Token> nested = List.of(createToken("("), createToken("("), createToken("a"), createToken("b"), createToken(")"), createToken(")"));
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nested));
		assertNotNull(nestedMatch);
		assertEquals(6, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void matchAtDifferentStartIndices() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(
			createToken("prefix"), createToken("("), createToken("content"), createToken(")"), createToken("suffix")
		);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(4, match.endIndex());
		assertEquals(3, match.matchedTokens().size());
		
		assertNull(rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void matchWithComplexRecursiveFactory() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("["), createRule("]"), (Function<TokenRule, TokenRule>) self -> {
			TokenRule element = TokenRules.any(createRule("item"), self);
			TokenRule comma = createRule(",");
			return TokenRules.optional(TokenRules.sequence(
				element,
				TokenRules.repeatInfinitely(TokenRules.sequence(comma, element))
			));
		});
		
		// Test: []
		List<Token> empty = List.of(createToken("["), createToken("]"));
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(empty));
		assertNotNull(emptyMatch);
		
		// Test: [item]
		List<Token> single = List.of(createToken("["), createToken("item"), createToken("]"));
		TokenRuleMatch singleMatch = rule.match(new TokenStream(single));
		assertNotNull(singleMatch);
		
		// Test: [item,item]
		List<Token> multiple = List.of(createToken("["), createToken("item"), createToken(","), createToken("item"), createToken("]"));
		TokenRuleMatch multipleMatch = rule.match(new TokenStream(multiple));
		assertNotNull(multipleMatch);
		
		// Test: [item,[item],item]
		List<Token> nested = List.of(
			createToken("["), createToken("item"), createToken(","), createToken("["),
			createToken("item"), createToken("]"), createToken(","), createToken("item"), createToken("]")
		);
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nested));
		assertNotNull(nestedMatch);
		assertEquals(9, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithUnbalancedDelimiters() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		List<Token> missingOpening = List.of(createToken("content"), createToken(")"));
		assertNull(rule.match(new TokenStream(missingOpening)));
		
		List<Token> missingClosing = List.of(createToken("("), createToken("content"));
		assertNull(rule.match(new TokenStream(missingClosing)));
		
		List<Token> extraOpening = List.of(createToken("("), createToken("("), createToken("content"), createToken(")"));
		assertNull(rule.match(new TokenStream(extraOpening)));
		
		List<Token> extraClosing = List.of(createToken("("), createToken("content"), createToken(")"), createToken(")"));
		TokenRuleMatch match = rule.match(new TokenStream(extraClosing));
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void matchWithInsufficientTokens() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		List<Token> oneToken = List.of(createToken("("));
		assertNull(rule.match(new TokenStream(oneToken)));
		
		List<Token> twoTokens = List.of(createToken("("), createToken("content"));
		assertNull(rule.match(new TokenStream(twoTokens)));
	}
	
	@Test
	void matchResultsAreConsistent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("content"), createToken(")"));
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens().size(), match2.matchedTokens().size());
		assertEquals(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void matchWithDeeplyNestedStructure() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("base"), self));
		
		List<Token> tokens = new ArrayList<>();
		int depth = 10;
		for (int i = 0; i < depth; i++) {
			tokens.add(createToken("("));
		}
		tokens.add(createToken("base"));
		for (int i = 0; i < depth; i++) {
			tokens.add(createToken(")"));
		}
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(depth * 2 + 1, match.matchedTokens().size());
	}
	
	@Test
	void matchWithSameOpeningAndClosingDelimiters() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("|"), createRule("|"),
			(Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("content"), self));
		
		// Test: |content|
		List<Token> simple = List.of(createToken("|"), createToken("content"), createToken("|"));
		TokenRuleMatch match = rule.match(new TokenStream(simple));
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		
		// Test nested: ||content||
		List<Token> nested = List.of(createToken("|"), createToken("|"), createToken("content"), createToken("|"), createToken("|"));
		TokenRuleMatch nestedMatch = rule.match(new TokenStream(nested));
		assertNotNull(nestedMatch);
		assertEquals(5, nestedMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithContentRuleThatAlwaysFails() {
		TokenRule alwaysFailRule = new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				return null;
			}
		};
		
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), alwaysFailRule, createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("content"), createToken(")"));
		
		assertNull(rule.match(new TokenStream(tokens)));
	}
	
	@Test
	void matchWithContentRuleThatConsumesNoTokens() {
		TokenRule emptyMatchRule = new TokenRule() {
			@Override
			public @NotNull TokenRuleMatch match(@NotNull TokenStream stream) {
				return new TokenRuleMatch(stream.getCurrentIndex(), stream.getCurrentIndex(), List.of(), this);
			}
		};
		
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), emptyMatchRule, createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken(")"));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void matchWithVeryWideRecursion() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("["), createRule("]"),
			(Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("item"), self).repeatInfinitely());
		
		// Test: [[][][][][][][][][][]
		List<Token> tokens = new ArrayList<>();
		tokens.add(createToken("["));
		for (int i = 0; i < 20; i++) {
			tokens.add(createToken("["));
			tokens.add(createToken("]"));
		}
		tokens.add(createToken("]"));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		assertNotNull(match);
		assertEquals(42, match.matchedTokens().size());
	}
	
	@Test
	void matchAtEndOfStream() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("prefix"), createToken("("), createToken("content"), createToken(")"));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(4, match.endIndex());
		
		TokenStream stream = new TokenStream(tokens, 1);
		rule.match(stream);
		assertFalse(stream.hasToken());
	}
	
	@Test
	void matchWithMultipleCharacterDelimiters() {
		TokenRule beginRule = createRule("BEGIN");
		TokenRule endRule = createRule("END");
		RecursiveTokenRule rule = new RecursiveTokenRule(beginRule, createRule("content"), endRule);
		
		List<Token> tokens = List.of(createToken("BEGIN"), createToken("content"), createToken("END"));
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
		assertEquals("BEGIN", match.matchedTokens().get(0).value());
		assertEquals("END", match.matchedTokens().get(2).value());
	}
	
	@Test
	void matchWithOverlappingContentAndDelimiters() {
		TokenRule anyTokenRule = new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				if (!stream.hasToken()) return null;
				int startIndex = stream.getCurrentIndex();
				Token token = stream.readToken();
				return new TokenRuleMatch(startIndex, stream.getCurrentIndex(), List.of(token), this);
			}
		};
		
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), anyTokenRule, createRule(")"));
		
		List<Token> tokens = List.of(createToken("("), createToken("("), createToken(")"));
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void matchWithStreamPositionConsistency() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("content"), createToken(")"), createToken("after"));
		
		TokenStream stream = new TokenStream(tokens);
		int initialPosition = stream.getCurrentIndex();
		
		TokenRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals(0, initialPosition);
		assertEquals(3, stream.getCurrentIndex());
		assertTrue(stream.hasToken());
		assertEquals("after", stream.getCurrentToken().value());
	}
	
	@Test
	void matchWithFailedMatchPreservesStreamPosition() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> tokens = List.of(createToken("("), createToken("wrong"), createToken(")"));
		
		TokenStream stream = new TokenStream(tokens);
		int initialPosition = stream.getCurrentIndex();
		
		TokenRuleMatch match = rule.match(stream);
		
		assertNull(match);
		assertEquals(initialPosition, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithFactoryThatCreatesComplexRules() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("{"), createRule("}"), (Function<TokenRule, TokenRule>) self -> {
			TokenRule keyValue = TokenRules.sequence(createRule("key"), createRule(":"), createRule("value"));
			TokenRule array = TokenRules.sequence(createRule("["), self, createRule("]"));
			
			return TokenRules.any(keyValue, array, self).optional();
		});
		
		// Test complex nested structure: {key:value}
		List<Token> keyValueTokens = List.of(
			createToken("{"), createToken("key"), createToken(":"), createToken("value"), createToken("}")
		);
		TokenRuleMatch match = rule.match(new TokenStream(keyValueTokens));
		assertNotNull(match);
		assertEquals(5, match.matchedTokens().size());
	}
	
	@Test
	void matchWithExtremeNesting() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(createRule("x"), self));
		
		// Create very deep nesting: ((((((((((x))))))))))
		List<Token> tokens = new ArrayList<>();
		int depth = 50;
		
		for (int i = 0; i < depth; i++) {
			tokens.add(createToken("("));
		}
		tokens.add(createToken("x"));
		for (int i = 0; i < depth; i++) {
			tokens.add(createToken(")"));
		}
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		assertNotNull(match);
		assertEquals(depth * 2 + 1, match.matchedTokens().size());
	}
	
	@Test
	void matchWithInterruptedRecursion() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		List<Token> tokens = List.of(createToken("("), createToken("notcontent"), createToken(")"));
		assertNull(rule.match(new TokenStream(tokens)));
		
		List<Token> tokens2 = List.of(createToken("("), createToken("content"), createToken("notclosing"));
		assertNull(rule.match(new TokenStream(tokens2)));
	}
	
	@Test
	void matchWithAlternatingDelimiters() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("<"), createRule(">"), (Function<TokenRule, TokenRule>) self -> TokenRules.any(
			createRule("item"),
			TokenRules.sequence(createRule("["), self, createRule("]")),
			self
		));
		
		// Test: <[<item>]>
		List<Token> tokens = List.of(
			createToken("<"), createToken("["), createToken("<"), createToken("item"),
			createToken(">"), createToken("]"), createToken(">")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		assertNotNull(match);
		assertEquals(7, match.matchedTokens().size());
	}
	
	@Test
	void matchWithLargeTokenStream() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("x"), createRule(")"));
		
		List<Token> tokens = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			tokens.add(createToken("noise"));
		}
		tokens.add(createToken("("));
		tokens.add(createToken("x"));
		tokens.add(createToken(")"));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1000));
		assertNotNull(match);
		assertEquals(1000, match.startIndex());
		assertEquals(1003, match.endIndex());
	}
	
	@Test
	void matchResultTokensMatchStreamContent() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		List<Token> originalTokens = List.of(createToken("("), createToken("content"), createToken(")"));
		
		TokenRuleMatch match = rule.match(new TokenStream(originalTokens));
		
		assertNotNull(match);
		assertEquals(originalTokens.size(), match.matchedTokens().size());
		
		for (int i = 0; i < originalTokens.size(); i++) {
			assertEquals(originalTokens.get(i).value(), match.matchedTokens().get(i).value());
		}
	}
	
	@Test
	void matchWithFactoryAccessingRecursiveRule() {
		AtomicInteger factoryCallCount = new AtomicInteger(0);
		
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> {
			factoryCallCount.incrementAndGet();
			return TokenRules.any(createRule("base"), self).optional();
		});
		
		List<Token> tokens = List.of(createToken("("), createToken("("), createToken("base"), createToken(")"), createToken(")"));
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		
		assertNotNull(match);
		assertEquals(1, factoryCallCount.get());
		assertEquals(5, match.matchedTokens().size());
	}
	
	@Test
	void matchWithEmptyStreamAtDifferentPositions() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
		
		List<Token> tokens = List.of(createToken("("));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void matchWithRecursiveContentAtEdges() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule(")"), (Function<TokenRule, TokenRule>) self -> TokenRules.sequence(
			self.optional(),
			createRule("mid").optional(),
			self.optional()
		));
		
		// Test: (()(mid)())
		List<Token> tokens = List.of(
			createToken("("), createToken("("), createToken(")"), createToken("mid"),
			createToken("("), createToken(")"), createToken(")")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens));
		assertNotNull(match);
		assertEquals(7, match.matchedTokens().size());
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule opening = createRule("(");
		TokenRule content = createRule("content");
		TokenRule closing = createRule(")");
		RecursiveTokenRule rule1 = new RecursiveTokenRule(opening, content, closing);
		RecursiveTokenRule rule2 = new RecursiveTokenRule(opening, content, closing);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		RecursiveTokenRule rule = new RecursiveTokenRule(createRule("("), createRule("content"), createRule(")"));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("RecursiveTokenRule"));
		assertTrue(ruleString.contains("openingRule"));
		assertTrue(ruleString.contains("contentRule"));
		assertTrue(ruleString.contains("closingRule"));
	}
}
