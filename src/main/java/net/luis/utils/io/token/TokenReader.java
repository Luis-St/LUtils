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

import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class TokenReader {
	
	private final Set<TokenDefinition> definitions;
	private final Set<Character> allowedChars;
	private final Set<Character> separators;
	
	public TokenReader(@NotNull Set<TokenDefinition> definitions, @NotNull Set<Character> allowedChars, @NotNull Set<Character> separators) {
		this.definitions = new HashSet<>(definitions);
		this.allowedChars = new HashSet<>(allowedChars);
		this.separators = new HashSet<>(separators);
		this.allowedChars.addAll(separators);
	}
	
	public @NotNull List<Token> readTokens(@NotNull String input) {
		List<Token> tokens = new ArrayList<>();
		int position = 0;
		int line = 0;
		int charInLine = 0;
		
		StringBuilder currentWord = new StringBuilder();
		int wordStartPosition = position;
		int wordStartLine = line;
		int wordStartCharInLine = charInLine;
		
		while (position < input.length()) {
			char current = input.charAt(position);
			
			if (!this.allowedChars.contains(current)) {
				throw new IllegalStateException("Undefined character at line " + line + ", character " + charInLine + ": '" + current + "'");
			}
			
			boolean isSeparator = this.separators.contains(current);
			if (isSeparator) {
				if (!currentWord.isEmpty()) {
					this.addToken(tokens, currentWord.toString(), wordStartPosition, wordStartLine, wordStartCharInLine);
					currentWord = new StringBuilder();
				}
				
				for (TokenDefinition definition : this.definitions) {
					String currentStr = String.valueOf(current);
					if (definition.matches(currentStr)) {
						tokens.add(new Token(definition, currentStr, new TokenPosition(line, position, charInLine)));
						break;
					}
				}
				
				if (current == '\n') {
					line++;
					charInLine = 0;
				} else {					charInLine++;
				}
				position++;
				
				wordStartPosition = position;
				wordStartLine = line;
				wordStartCharInLine = charInLine;
			} else {
				if (currentWord.isEmpty()) {
					wordStartPosition = position;
					wordStartCharInLine = charInLine;
				}
				
				currentWord.append(current);
				charInLine++;
				position++;
			}
		}
		
		if (!currentWord.isEmpty()) {
			this.addToken(tokens, currentWord.toString(), wordStartPosition, wordStartLine, wordStartCharInLine);
		}
		
		return tokens;
	}
	
	private void addToken(@NotNull List<Token> tokens, @NotNull String word, int position, int line, int charInLine) {
		TokenDefinition matchedDef = null;
		
		for (TokenDefinition definition : this.definitions) {
			if (definition.matches(word)) {
				matchedDef = definition;
				break;
			}
		}
		
		if (matchedDef == null) {
			matchedDef = TokenDefinition.WORD;
		}
		
		tokens.add(new Token(matchedDef, word, new TokenPosition(line, position, charInLine)));
	}
}
