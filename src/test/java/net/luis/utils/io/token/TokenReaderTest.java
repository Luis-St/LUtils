/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

import com.google.common.collect.Sets;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.definition.WordTokenDefinition;
import net.luis.utils.io.token.tokens.*;
import net.luis.utils.io.token.type.StandardTokenType;
import net.luis.utils.io.token.type.TokenType;
import net.luis.utils.io.token.type.classifier.TokenClassifier;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenReader}.<br>
 *
 * @author Luis-St
 */
class TokenReaderTest {
	
	private static @NonNull TokenReader createBasicTokenReader() {
		return createBasicTokenReader(Set.of());
	}
	
	private static @NonNull TokenReader createBasicTokenReader(@NonNull Set<Character> additionalSeparators) {
		return createBasicTokenReader(additionalSeparators, token -> Set.of());
	}
	
	private static @NonNull TokenReader createBasicTokenReader(@NonNull Set<Character> additionalSeparators, @NonNull TokenClassifier classifier) {
		Set<TokenDefinition> definitions = Set.of(
			TokenDefinition.of('('), TokenDefinition.of(')'),
			TokenDefinition.of('0'), TokenDefinition.of('1'), TokenDefinition.of('2'),
			TokenDefinition.of('3'), TokenDefinition.of('4'), TokenDefinition.of('5'),
			TokenDefinition.of('6'), TokenDefinition.of('7'), TokenDefinition.of('8'),
			TokenDefinition.of('9')
		);
		
		Set<Character> allowedChars = Set.of(
			'(', ')', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z'
		);
		
		Set<Character> separators = Sets.union(Set.of(' '), additionalSeparators);
		
		return new TokenReader(definitions, allowedChars, separators, classifier);
	}
	
	@Test
	void constructorWithNullDefinitions() {
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of(' ');
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(NullPointerException.class, () -> new TokenReader(null, allowedChars, separators, classifier));
	}
	
	@Test
	void constructorWithNullAllowedChars() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> separators = Set.of(' ');
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(NullPointerException.class, () -> new TokenReader(definitions, null, separators, classifier));
	}
	
	@Test
	void constructorWithNullSeparators() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of('a', 'b');
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(NullPointerException.class, () -> new TokenReader(definitions, allowedChars, null, classifier));
	}
	
	@Test
	void constructorWithNullClassifier() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of(' ');
		
		assertThrows(NullPointerException.class, () -> new TokenReader(definitions, allowedChars, separators, null));
	}
	
	@Test
	void constructorWithWordTokenDefinition() {
		Set<TokenDefinition> definitions = Set.of(WordTokenDefinition.INSTANCE);
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of(' ');
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators, classifier));
	}
	
	@Test
	void constructorWithEmptyAllowedChars() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of();
		Set<Character> separators = Set.of(' ');
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators, classifier));
	}
	
	@Test
	void constructorWithEmptySeparators() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of();
		TokenClassifier classifier = token -> Set.of();
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators, classifier));
	}
	
	@Test
	void constructorWithValidParameters() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'), TokenDefinition.of('b'));
		Set<Character> allowedChars = Set.of('a', 'b', ' ');
		Set<Character> separators = Set.of(' ');
		TokenClassifier classifier = token -> Set.of();
		
		assertDoesNotThrow(() -> new TokenReader(definitions, allowedChars, separators, classifier));
	}
	
	@Test
	void readTokensWithNullInput() {
		TokenReader reader = createBasicTokenReader();
		
		assertThrows(NullPointerException.class, () -> reader.readTokens(null));
	}
	
	@Test
	void readTokensWithUndefinedCharacter() {
		TokenReader reader = createBasicTokenReader();
		
		assertThrows(IllegalStateException.class, () -> reader.readTokens("_"));
		assertThrows(IllegalStateException.class, () -> reader.readTokens("abc_"));
		assertThrows(IllegalStateException.class, () -> reader.readTokens("_abc"));
	}
	
	@Test
	void readTokensWithEmptyString() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("");
		
		assertTrue(tokens.isEmpty());
	}
	
	@Test
	void readTokensWithSingleCharacters() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("()");
		
		assertEquals(1, tokens.size());
		assertEquals("()", tokens.getFirst().value());
	}
	
	@Test
	void readTokensWithParentheses() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("( () ( ) )");
		
		assertEquals(5, tokens.size());
		assertEquals("(", tokens.getFirst().value());
		assertEquals("()", tokens.get(1).value());
		assertEquals("(", tokens.get(2).value());
		assertEquals(")", tokens.get(3).value());
		assertEquals(")", tokens.get(4).value());
	}
	
	@Test
	void readTokensWithNumbers() {
		TokenReader reader = createBasicTokenReader(Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
		List<Token> tokens = reader.readTokens("0123456789");
		
		assertEquals(10, tokens.size());
		for (int i = 0; i < 10; i++) {
			assertEquals(String.valueOf(i), tokens.get(i).value());
		}
	}
	
	@Test
	void readTokensWithWords() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("abc def");
		
		assertEquals(2, tokens.size());
		assertEquals("abc", tokens.getFirst().value());
		assertEquals("def", tokens.get(1).value());
		assertInstanceOf(SimpleToken.class, tokens.getFirst());
		assertInstanceOf(SimpleToken.class, tokens.get(1));
	}
	
	@Test
	void readTokensWithMixedContent() {
		TokenReader reader = createBasicTokenReader(Set.of('(', ')'));
		List<Token> tokens = reader.readTokens("(123)");
		
		assertEquals(3, tokens.size());
		assertEquals("(", tokens.getFirst().value());
		assertEquals("123", tokens.get(1).value());
		assertEquals(")", tokens.get(2).value());
	}
	
	@Test
	void readTokensWithEscapeSequences() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("\\n\\t\\\\");
		
		assertEquals(3, tokens.size());
		assertEquals("\\n", tokens.getFirst().value());
		assertEquals("\\t", tokens.get(1).value());
		assertEquals("\\\\", tokens.get(2).value());
		assertInstanceOf(EscapedToken.class, tokens.getFirst());
		assertInstanceOf(EscapedToken.class, tokens.get(1));
		assertInstanceOf(EscapedToken.class, tokens.get(2));
	}
	
	@Test
	void readTokensWithNewlines() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("a\nb");
		
		assertEquals(2, tokens.size());
		assertEquals("a", tokens.getFirst().value());
		assertEquals("b", tokens.get(1).value());
	}
	
	@Test
	void readTokensWithSpaces() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("a b c");
		
		assertEquals(3, tokens.size());
		assertEquals("a", tokens.getFirst().value());
		assertEquals("b", tokens.get(1).value());
		assertEquals("c", tokens.get(2).value());
	}
	
	@Test
	void readTokensVerifiesPositions() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("ab c");
		
		assertEquals(2, tokens.size());
		
		Token firstToken = tokens.getFirst();
		assertEquals(0, firstToken.position().line());
		assertEquals(0, firstToken.position().characterInLine());
		assertEquals(0, firstToken.position().character());
		
		Token secondToken = tokens.get(1);
		assertEquals(0, secondToken.position().line());
		assertEquals(3, secondToken.position().characterInLine());
		assertEquals(3, secondToken.position().character());
	}
	
	@Test
	void readTokensWithMultipleLines() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("a\nb\nc");
		
		assertEquals(3, tokens.size());
		
		assertEquals(0, tokens.getFirst().position().line());
		assertEquals(1, tokens.get(1).position().line());
		assertEquals(2, tokens.get(2).position().line());
	}
	
	@Test
	void readTokensWithComplexInput() {
		TokenReader reader = createBasicTokenReader(Set.of('(', ')', '\n'));
		List<Token> tokens = reader.readTokens("hello(123)\\nworld");
		
		assertEquals(6, tokens.size());
		assertEquals("hello", tokens.getFirst().value());
		assertEquals("(", tokens.get(1).value());
		assertEquals("123", tokens.get(2).value());
		assertEquals(")", tokens.get(3).value());
		assertEquals("\\n", tokens.get(4).value());
		assertEquals("world", tokens.get(5).value());
	}
	
	@Test
	void readTokensWithClassifierInvocation() {
		TokenType customType = StandardTokenType.EOF;
		
		TokenClassifier classifier = token -> Set.of(customType);
		TokenReader reader = createBasicTokenReader(Set.of(), classifier);
		
		List<Token> tokens = reader.readTokens("abc");
		
		assertEquals(1, tokens.size());
		Token token = tokens.getFirst();
		assertEquals("abc", token.value());
		assertTrue(token.types().contains(customType));
	}
	
	@Test
	void readTokensWithClassifierForMultipleTokens() {
		TokenType wordType = StandardTokenType.IDENTIFIER;
		TokenType numberType = StandardTokenType.NUMBER;
		
		TokenClassifier classifier = token -> {
			if (token.value().matches("\\d+")) {
				return Set.of(numberType);
			}
			return Set.of(wordType);
		};
		
		TokenReader reader = createBasicTokenReader(Set.of(), classifier);
		List<Token> tokens = reader.readTokens("hello 123");
		
		assertEquals(2, tokens.size());
		assertTrue(tokens.get(0).types().contains(wordType));
		assertTrue(tokens.get(1).types().contains(numberType));
	}
	
	@Test
	void readTokensWithClassifierReturningEmptySet() {
		TokenClassifier classifier = token -> Set.of();
		TokenReader reader = createBasicTokenReader(Set.of(), classifier);
		
		List<Token> tokens = reader.readTokens("abc");
		
		assertEquals(1, tokens.size());
		assertTrue(tokens.getFirst().types().isEmpty());
	}
	
	@Test
	void readTokensWithClassifierForEscapedTokens() {
		TokenType escapedType = StandardTokenType.STRING;
		
		TokenClassifier classifier = token -> {
			if (token instanceof EscapedToken) {
				return Set.of(escapedType);
			}
			return Set.of();
		};
		
		TokenReader reader = createBasicTokenReader(Set.of(), classifier);
		List<Token> tokens = reader.readTokens("\\n");
		
		assertEquals(1, tokens.size());
		assertInstanceOf(EscapedToken.class, tokens.getFirst());
		assertTrue(tokens.getFirst().types().contains(escapedType));
	}
}
