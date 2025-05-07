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
import org.jetbrains.annotations.*;

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
		this.definitions = new HashSet<>(Objects.requireNonNull(definitions, "Token definitions must not be null"));
		this.allowedChars = new HashSet<>(Objects.requireNonNull(allowedChars, "Allowed characters must not be null"));
		this.separators = new HashSet<>(Objects.requireNonNull(separators, "Separators must not be null"));
		
		this.separators.addAll(Arrays.asList('\\', '\n'));
		this.allowedChars.addAll(this.separators);
	}
	
	public @NotNull @Unmodifiable List<Token> readTokens(@NotNull String input) {
		Objects.requireNonNull(input, "Input string must not be null");
		List<Token> tokens = Lists.newArrayList();
		
		boolean escape = false;
		StringBuilder currentWord = new StringBuilder();
		PositionTracker current = new PositionTracker(0, 0, 0);
		PositionTracker wordStart = new PositionTracker(0, 0, 0);
		while (current.position < input.length()) {
			char c = input.charAt(current.position);
			
			if (!this.allowedChars.contains(c)) {
				throw new IllegalStateException("Undefined character at line " + current.line + ", character " + current.charInLine + ": '" + c + "'");
			}
			
			if (escape)  {
				tokens.add(new EscapedToken(TokenDefinition.ofEscaped(c), "\\" + c, wordStart.toTokenPosition(), current.toTokenPosition()));
				escape = false;
				current.increment();
				wordStart.copyFrom(current);
			} else if (this.separators.contains(c)) {
				if (!currentWord.isEmpty()) {
					this.addToken(tokens, currentWord.toString(), wordStart.toTokenPosition(), new TokenPosition(current.line, current.position - 1, current.charInLine - 1));
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
			this.addToken(tokens, currentWord.toString(), wordStart.toTokenPosition(), current.toTokenPosition());
		}
		return List.copyOf(tokens);
	}
	
	private void addToken(@NotNull List<Token> tokens, @NotNull String word, @NotNull TokenPosition startPosition, @NotNull TokenPosition endPosition) {
		Objects.requireNonNull(tokens, "Token list must not be null");
		Objects.requireNonNull(word, "Word must not be null");
		
		TokenDefinition matchedDefinition = this.definitions.stream().filter(definition -> definition.matches(word)).findFirst().orElse(WordTokenDefinition.INSTANCE);
		tokens.add(new SimpleToken(matchedDefinition, word, startPosition, endPosition));
	}
	
	//region Internal
	private static final class PositionTracker {
		
		private int line;
		private int position;
		private int charInLine;
		
		private PositionTracker(int line, int position, int charInLine) {
			this.line = line;
			this.position = position;
			this.charInLine = charInLine;
		}
		
		private void increment() {
			this.position++;
			this.charInLine++;
		}
		
		private void newLine() {
			this.increment();
			this.charInLine = 0;
		}
		
		private void copyFrom(@NotNull PositionTracker other) {
			this.line = other.line;
			this.position = other.position;
			this.charInLine = other.charInLine;
		}
		
		private @NotNull TokenPosition toTokenPosition() {
			return new TokenPosition(this.line, this.position, this.charInLine);
		}
	}
	//endregion
}
