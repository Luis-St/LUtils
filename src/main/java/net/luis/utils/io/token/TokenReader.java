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

import com.google.common.collect.Lists;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.definition.WordTokenDefinition;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * A class that reads tokens from a string input based on defined token definitions.<br>
 * It processes the input string, identifies tokens, and returns a list of tokens.<br>
 * It handles escape sequences and allows for custom token definitions and separators.<br>
 *
 * @author Luis-St
 */
public class TokenReader {
	
	/**
	 * The set of token definitions used to identify tokens in the input string.<br>
	 */
	private final Set<TokenDefinition> definitions;
	/**
	 * The set of allowed characters that can be part of tokens.<br>
	 */
	private final Set<Character> allowedChars;
	/**
	 * The set of characters that are considered as separators in the input string.<br>
	 */
	private final Set<Character> separators;
	
	/**
	 * Constructs a new token reader with the specified token definitions, allowed characters, and separators.<br>
	 * The characters '\\' and '\n' are added to the set of separators.<br>
	 * The list of separators is added to the list of allowed characters to ensure they are valid.<br>
	 *
	 * @param definitions The set of token definitions
	 * @param allowedChars The set of allowed characters
	 * @param separators The set of separators
	 * @throws NullPointerException If any of the parameters are null
	 * @throws IllegalArgumentException If the definitions contain the word token definition, or if the allowed characters or separators are empty
	 */
	public TokenReader(@NotNull Set<TokenDefinition> definitions, @NotNull Set<Character> allowedChars, @NotNull Set<Character> separators) {
		this.definitions = new HashSet<>(Objects.requireNonNull(definitions, "Token definitions must not be null"));
		this.allowedChars = new HashSet<>(Objects.requireNonNull(allowedChars, "Allowed characters must not be null"));
		this.separators = new HashSet<>(Objects.requireNonNull(separators, "Separators must not be null"));
		
		if (this.definitions.contains(WordTokenDefinition.INSTANCE)) {
			throw new IllegalArgumentException("Word token definition must not be part of the definitions");
		}
		if (this.allowedChars.isEmpty()) {
			throw new IllegalArgumentException("Allowed characters must not be empty");
		}
		if (this.separators.isEmpty()) {
			throw new IllegalArgumentException("Separators must not be empty");
		}
		
		this.separators.addAll(Arrays.asList('\\', '\n'));
		this.allowedChars.addAll(this.separators);
	}
	
	/**
	 * Reads tokens from the input string and returns a list of tokens.<br>
	 * It processes the input string, identifies tokens based on the defined token definitions, and handles escape sequences.<br>
	 *
	 * @param input The input string to read tokens from
	 * @return An unmodifiable list of tokens read from the input string
	 * @throws NullPointerException If the input string is null
	 * @throws IllegalStateException If an undefined character is encountered in the input string
	 * @see SimpleToken
	 * @see EscapedToken
	 */
	public @NotNull @Unmodifiable List<Token> readTokens(@NotNull String input) {
		Objects.requireNonNull(input, "Input string must not be null");
		List<Token> tokens = Lists.newArrayList();
		
		boolean escape = false;
		StringBuilder currentWord = new StringBuilder();
		PositionTracker current = new PositionTracker();
		PositionTracker wordStart = new PositionTracker();
		while (current.position < input.length()) {
			char c = input.charAt(current.position);
			
			if (!this.allowedChars.contains(c)) {
				throw new IllegalStateException("Undefined character at line " + current.line + ", character " + current.charInLine + ": '" + c + "'");
			}
			
			if (escape) {
				tokens.add(new EscapedToken(TokenDefinition.ofEscaped(c), "\\" + c, wordStart.toTokenPosition(), current.toTokenPosition()));
				escape = false;
				current.increment();
				wordStart.copyFrom(current);
			} else if (this.separators.contains(c)) {
				if (!currentWord.isEmpty()) {
					this.addToken(tokens, currentWord.toString(), wordStart.toTokenPosition(), new TokenPosition(current.line, current.charInLine - 1, current.position - 1));
					currentWord = new StringBuilder();
				}
				
				if (c == '\\' && current.position + 1 < input.length()) {
					escape = true;
					wordStart.copyFrom(current);
					current.increment();
					continue;
				}
				
				for (TokenDefinition definition : this.definitions) {
					String str = String.valueOf(c);
					if (definition.matches(str)) {
						TokenPosition position = current.toTokenPosition();
						tokens.add(new SimpleToken(definition, str, position, position));
						break;
					}
				}
				
				if (c == '\n') {
					current.newLine();
				} else {
					current.increment();
				}
				
				wordStart.copyFrom(current);
			} else {
				if (currentWord.isEmpty()) {
					wordStart.copyFrom(current);
				}
				
				currentWord.append(c);
				current.increment();
			}
		}
		
		if (!currentWord.isEmpty()) {
			TokenPosition endPosition = new TokenPosition(current.line, current.charInLine - 1, current.position - 1);
			this.addToken(tokens, currentWord.toString(), wordStart.toTokenPosition(), endPosition);
		}
		return List.copyOf(tokens);
	}
	
	/**
	 * Adds a token to the list of tokens.<br>
	 * It uses the token definitions to determine the type of token and creates a new token object.<br>
	 * If no matching definition is found, a word token is created.<br>
	 *
	 * @param tokens The list of tokens to add the new token to
	 * @param word The word to be added as a token
	 * @param startPosition The start position of the token in the input string
	 * @param endPosition The end position of the token in the input string
	 * @throws NullPointerException If any of the parameters are null
	 */
	private void addToken(@NotNull List<Token> tokens, @NotNull String word, @NotNull TokenPosition startPosition, @NotNull TokenPosition endPosition) {
		Objects.requireNonNull(tokens, "Token list must not be null");
		Objects.requireNonNull(word, "Word must not be null");
		
		TokenDefinition matchedDefinition = this.definitions.stream().filter(definition -> definition.matches(word)).findFirst().orElse(WordTokenDefinition.INSTANCE);
		tokens.add(new SimpleToken(matchedDefinition, word, startPosition, endPosition));
	}
	
	//region Internal
	
	/**
	 * A class that tracks the position of the current character in the input string.<br>
	 * It keeps track of the line number, character position, and character index in the line.<br>
	 */
	private static final class PositionTracker {
		
		/**
		 * The line number of the current character in the input string.<br>
		 */
		private int line;
		/**
		 * The index of the current character in the input string.<br>
		 */
		private int position;
		/**
		 * The index of the current character in the line.<br>
		 */
		private int charInLine;
		
		/**
		 * Counts a new character in the input string.<br>
		 * It increments the position and character index.<br>
		 */
		private void increment() {
			this.position++;
			this.charInLine++;
		}
		
		/**
		 * Counts a new line in the input string.<br>
		 * It increments the position and character index, and resets the character in line index to 0.<br>
		 */
		private void newLine() {
			this.increment();
			this.line++;
			this.charInLine = 0;
		}
		
		/**
		 * Copies the position from another position tracker.<br>
		 * It sets the line number, character position, and character index to the values of the other position tracker.<br>
		 *
		 * @param other The other position tracker to copy from
		 * @throws NullPointerException If the other position tracker is null
		 */
		private void copyFrom(@NotNull PositionTracker other) {
			Objects.requireNonNull(other, "Position tracker must not be null");
			this.line = other.line;
			this.position = other.position;
			this.charInLine = other.charInLine;
		}
		
		/**
		 * Creates a new token position based on the current position tracker.<br>
		 *
		 * @return A new token position
		 */
		private @NotNull TokenPosition toTokenPosition() {
			return new TokenPosition(this.line, this.charInLine, this.position);
		}
	}
	//endregion
}
