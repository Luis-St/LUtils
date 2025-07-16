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

import com.google.common.collect.Sets;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.definition.WordTokenDefinition;
import net.luis.utils.io.token.tokens.EscapedToken;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
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
	
	private static @NotNull TokenReader createBasicTokenReader() {
		return createBasicTokenReader(Set.of());
	}
	
	private static @NotNull TokenReader createBasicTokenReader(@NotNull Set<Character> additionalSeparators) {
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
		
		return new TokenReader(definitions, allowedChars, separators);
	}
	
	@Test
	void constructorWithNullDefinitions() {
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of(' ');
		
		assertThrows(NullPointerException.class, () -> new TokenReader(null, allowedChars, separators));
	}
	
	@Test
	void constructorWithNullAllowedChars() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> separators = Set.of(' ');
		
		assertThrows(NullPointerException.class, () -> new TokenReader(definitions, null, separators));
	}
	
	@Test
	void constructorWithNullSeparators() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of('a', 'b');
		
		assertThrows(NullPointerException.class, () -> new TokenReader(definitions, allowedChars, null));
	}
	
	@Test
	void constructorWithWordTokenDefinition() {
		Set<TokenDefinition> definitions = Set.of(WordTokenDefinition.INSTANCE);
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of(' ');
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators));
	}
	
	@Test
	void constructorWithEmptyAllowedChars() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of();
		Set<Character> separators = Set.of(' ');
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators));
	}
	
	@Test
	void constructorWithEmptySeparators() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'));
		Set<Character> allowedChars = Set.of('a', 'b');
		Set<Character> separators = Set.of();
		
		assertThrows(IllegalArgumentException.class, () -> new TokenReader(definitions, allowedChars, separators));
	}
	
	@Test
	void constructorWithValidParameters() {
		Set<TokenDefinition> definitions = Set.of(TokenDefinition.of('a'), TokenDefinition.of('b'));
		Set<Character> allowedChars = Set.of('a', 'b', ' ');
		Set<Character> separators = Set.of(' ');
		
		assertDoesNotThrow(() -> new TokenReader(definitions, allowedChars, separators));
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
		assertEquals(0, firstToken.startPosition().line());
		assertEquals(0, firstToken.startPosition().characterInLine());
		assertEquals(0, firstToken.startPosition().character());
		assertEquals(0, firstToken.endPosition().line());
		assertEquals(1, firstToken.endPosition().characterInLine());
		assertEquals(1, firstToken.endPosition().character());
		
		Token secondToken = tokens.get(1);
		assertEquals(0, secondToken.startPosition().line());
		assertEquals(3, secondToken.startPosition().characterInLine());
		assertEquals(3, secondToken.startPosition().character());
		assertEquals(0, secondToken.endPosition().line());
		assertEquals(3, secondToken.endPosition().characterInLine());
		assertEquals(3, secondToken.endPosition().character());
	}
	
	@Test
	void readTokensWithMultipleLines() {
		TokenReader reader = createBasicTokenReader();
		List<Token> tokens = reader.readTokens("a\nb\nc");
		
		assertEquals(3, tokens.size());
		
		assertEquals(0, tokens.getFirst().startPosition().line());
		assertEquals(1, tokens.get(1).startPosition().line());
		assertEquals(2, tokens.get(2).startPosition().line());
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
}
