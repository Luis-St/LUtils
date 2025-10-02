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

package net.luis.utils.io.token.type.classifier;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.tokens.EscapedToken;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.type.CustomTokenType;
import net.luis.utils.io.token.type.TokenType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenClassifier}.<br>
 *
 * @author Luis-St
 */
class TokenClassifierTest {
	
	private static final TokenPosition TEST_POSITION = new TokenPosition(0, 0, 0);
	
	private static @NotNull TokenType createTestTokenType(@NotNull String name) {
		return new CustomTokenType(name, null);
	}
	
	@Test
	void classifyTokenReturnsEmptySet() {
		TokenClassifier classifier = token -> Set.of();
		Token token = new SimpleToken("test", TEST_POSITION);
		
		Set<TokenType> types = classifier.classifyToken(token);
		
		assertNotNull(types);
		assertTrue(types.isEmpty());
	}
	
	@Test
	void classifyTokenReturnsSingleType() {
		TokenType testType = createTestTokenType("TEST");
		TokenClassifier classifier = token -> Set.of(testType);
		Token token = new SimpleToken("test", TEST_POSITION);
		
		Set<TokenType> types = classifier.classifyToken(token);
		
		assertNotNull(types);
		assertEquals(1, types.size());
		assertTrue(types.contains(testType));
	}
	
	@Test
	void classifyTokenReturnsMultipleTypes() {
		TokenType type1 = createTestTokenType("TYPE1");
		TokenType type2 = createTestTokenType("TYPE2");
		TokenType type3 = createTestTokenType("TYPE3");
		
		TokenClassifier classifier = token -> Set.of(type1, type2, type3);
		Token token = new SimpleToken("test", TEST_POSITION);
		
		Set<TokenType> types = classifier.classifyToken(token);
		
		assertNotNull(types);
		assertEquals(3, types.size());
		assertTrue(types.contains(type1));
		assertTrue(types.contains(type2));
		assertTrue(types.contains(type3));
	}
	
	@Test
	void classifyTokenBasedOnValue() {
		TokenType numberType = createTestTokenType("NUMBER");
		TokenType wordType = createTestTokenType("WORD");
		
		TokenClassifier classifier = token -> {
			if (token.value().matches("\\d+")) {
				return Set.of(numberType);
			}
			return Set.of(wordType);
		};
		
		Token numberToken = new SimpleToken("123", TEST_POSITION);
		Token wordToken = new SimpleToken("abc", TEST_POSITION);
		
		Set<TokenType> numberTypes = classifier.classifyToken(numberToken);
		Set<TokenType> wordTypes = classifier.classifyToken(wordToken);
		
		assertTrue(numberTypes.contains(numberType));
		assertFalse(numberTypes.contains(wordType));
		assertTrue(wordTypes.contains(wordType));
		assertFalse(wordTypes.contains(numberType));
	}
	
	@Test
	void classifyTokenBasedOnTokenClass() {
		TokenType simpleType = createTestTokenType("SIMPLE");
		TokenType escapedType = createTestTokenType("ESCAPED");
		
		TokenClassifier classifier = token -> {
			if (token instanceof EscapedToken) {
				return Set.of(escapedType);
			} else if (token instanceof SimpleToken) {
				return Set.of(simpleType);
			}
			return Set.of();
		};
		
		Token simpleToken = new SimpleToken("test", TEST_POSITION);
		Token escapedToken = new EscapedToken("\\n", TEST_POSITION);
		
		Set<TokenType> simpleTypes = classifier.classifyToken(simpleToken);
		Set<TokenType> escapedTypes = classifier.classifyToken(escapedToken);
		
		assertTrue(simpleTypes.contains(simpleType));
		assertFalse(simpleTypes.contains(escapedType));
		assertTrue(escapedTypes.contains(escapedType));
		assertFalse(escapedTypes.contains(simpleType));
	}
	
	@Test
	void classifyTokenWithComplexPattern() {
		TokenType operatorType = createTestTokenType("OPERATOR");
		TokenType identifierType = createTestTokenType("IDENTIFIER");
		TokenType numberType = createTestTokenType("NUMBER");
		
		TokenClassifier classifier = token -> {
			String value = token.value();
			if (value.matches("[+\\-*/=]")) {
				return Set.of(operatorType);
			} else if (value.matches("\\d+")) {
				return Set.of(numberType);
			} else if (value.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
				return Set.of(identifierType);
			}
			return Set.of();
		};
		
		assertEquals(Set.of(operatorType), classifier.classifyToken(new SimpleToken("+", TEST_POSITION)));
		assertEquals(Set.of(numberType), classifier.classifyToken(new SimpleToken("42", TEST_POSITION)));
		assertEquals(Set.of(identifierType), classifier.classifyToken(new SimpleToken("variable", TEST_POSITION)));
		assertEquals(Set.of(), classifier.classifyToken(new SimpleToken("@", TEST_POSITION)));
	}
	
	@Test
	void classifyTokenWithMultipleCriteria() {
		TokenType numericType = createTestTokenType("NUMERIC");
		TokenType shortType = createTestTokenType("SHORT");
		TokenType longType = createTestTokenType("LONG");
		
		TokenClassifier classifier = token -> {
			Set<TokenType> types = new java.util.HashSet<>();
			String value = token.value();
			
			if (value.matches("\\d+")) {
				types.add(numericType);
			}
			if (value.length() <= 3) {
				types.add(shortType);
			}
			if (value.length() > 5) {
				types.add(longType);
			}
			
			return types;
		};
		
		Token shortNumber = new SimpleToken("12", TEST_POSITION);
		Token longNumber = new SimpleToken("123456", TEST_POSITION);
		Token shortWord = new SimpleToken("abc", TEST_POSITION);
		
		Set<TokenType> shortNumberTypes = classifier.classifyToken(shortNumber);
		assertTrue(shortNumberTypes.contains(numericType));
		assertTrue(shortNumberTypes.contains(shortType));
		assertFalse(shortNumberTypes.contains(longType));
		
		Set<TokenType> longNumberTypes = classifier.classifyToken(longNumber);
		assertTrue(longNumberTypes.contains(numericType));
		assertFalse(longNumberTypes.contains(shortType));
		assertTrue(longNumberTypes.contains(longType));
		
		Set<TokenType> shortWordTypes = classifier.classifyToken(shortWord);
		assertFalse(shortWordTypes.contains(numericType));
		assertTrue(shortWordTypes.contains(shortType));
		assertFalse(shortWordTypes.contains(longType));
	}
	
	@Test
	void classifyTokenWithChainedClassifiers() {
		TokenType type1 = createTestTokenType("TYPE1");
		TokenType type2 = createTestTokenType("TYPE2");
		
		TokenClassifier classifier1 = token -> Set.of(type1);
		TokenClassifier classifier2 = token -> Set.of(type2);
		
		// Combine classifiers
		TokenClassifier combinedClassifier = token -> {
			Set<TokenType> types = new java.util.HashSet<>();
			types.addAll(classifier1.classifyToken(token));
			types.addAll(classifier2.classifyToken(token));
			return types;
		};
		
		Token token = new SimpleToken("test", TEST_POSITION);
		Set<TokenType> types = combinedClassifier.classifyToken(token);
		
		assertEquals(2, types.size());
		assertTrue(types.contains(type1));
		assertTrue(types.contains(type2));
	}
	
	@Test
	void classifyTokenWithConditionalChaining() {
		TokenType baseType = createTestTokenType("BASE");
		TokenType enhancedType = createTestTokenType("ENHANCED");
		
		TokenClassifier baseClassifier = token -> Set.of(baseType);
		TokenClassifier enhancedClassifier = token -> {
			Set<TokenType> types = new java.util.HashSet<>(baseClassifier.classifyToken(token));
			if (token.value().length() > 3) {
				types.add(enhancedType);
			}
			return types;
		};
		
		Token shortToken = new SimpleToken("ab", TEST_POSITION);
		Token longToken = new SimpleToken("abcd", TEST_POSITION);
		
		Set<TokenType> shortTypes = enhancedClassifier.classifyToken(shortToken);
		assertEquals(1, shortTypes.size());
		assertTrue(shortTypes.contains(baseType));
		
		Set<TokenType> longTypes = enhancedClassifier.classifyToken(longToken);
		assertEquals(2, longTypes.size());
		assertTrue(longTypes.contains(baseType));
		assertTrue(longTypes.contains(enhancedType));
	}
	
	@Test
	void classifyTokenWithCaseInsensitiveMatching() {
		TokenType keywordType = createTestTokenType("KEYWORD");
		
		Set<String> keywords = Set.of("if", "else", "while", "for", "return");
		TokenClassifier classifier = token -> {
			if (keywords.contains(token.value().toLowerCase())) {
				return Set.of(keywordType);
			}
			return Set.of();
		};
		
		assertTrue(classifier.classifyToken(new SimpleToken("if", TEST_POSITION)).contains(keywordType));
		assertTrue(classifier.classifyToken(new SimpleToken("IF", TEST_POSITION)).contains(keywordType));
		assertTrue(classifier.classifyToken(new SimpleToken("If", TEST_POSITION)).contains(keywordType));
		assertFalse(classifier.classifyToken(new SimpleToken("variable", TEST_POSITION)).contains(keywordType));
	}
}
