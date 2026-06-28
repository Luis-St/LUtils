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

package net.luis.utils.io.token.lexer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.lexer.rules.CharRule;
import net.luis.utils.io.token.type.TokenType;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Builder class for constructing {@link Lexer} instances.<br>
 * <p>
 *     This builder provides methods to define the lexer's alphabet (the set of allowed characters) and the ordered list of lexer rules.<br>
 *     Rules are processed in the order they are defined, with ties in maximal munch broken by declaration order.
 * </p>
 *
 * @author Luis-St
 */
public class LexerBuilder {
	
	/**
	 * The set of characters that are allowed in the input.
	 */
	private final Set<Character> allowedChars = Sets.newHashSet();
	/**
	 * The ordered list of lexer rules.
	 */
	private final List<LexerRule> rules = Lists.newArrayList();
	
	/**
	 * Package-private constructor for creating a new lexer builder instance.<br>
	 * Use {@link Lexer#builder(java.util.function.Consumer)} to create a lexer.<br>
	 */
	LexerBuilder() {}
	
	/**
	 * Adds the given characters to the lexer's alphabet of allowed characters.<br>
	 *
	 * @param characters The characters to allow
	 * @throws NullPointerException If the character array is null
	 */
	public void allow(char @NonNull ... characters) {
		Objects.requireNonNull(characters, "Character array must not be null");
		
		for (char character : characters) {
			this.allowedChars.add(character);
		}
	}
	
	/**
	 * Adds all characters in the given inclusive range to the lexer's alphabet of allowed characters.<br>
	 *
	 * @param start The start of the range (inclusive)
	 * @param end The end of the range (inclusive)
	 * @throws IllegalArgumentException If the end of the range is less than the start of the range
	 */
	public void allowRange(char start, char end) {
		if (end < start) {
			throw new IllegalArgumentException("End of range must not be less than start of range");
		}
		
		for (char c = start; c <= end; c++) {
			this.allowedChars.add(c);
		}
	}
	
	/**
	 * Adds a new lexer rule that produces a regular (non-shadow) token of the given kind.<br>
	 *
	 * @param name The name of the rule
	 * @param kind The token type the produced token is classified with
	 * @param pattern The character pattern that recognizes the token text
	 * @throws NullPointerException If the name, kind or pattern is null
	 * @throws IllegalArgumentException If the name is empty
	 * @see LexerRule
	 */
	public void rule(@NonNull String name, @NonNull TokenType kind, @NonNull CharRule pattern) {
		this.rules.add(new LexerRule(name, pattern, kind, false));
	}
	
	/**
	 * Adds a new lexer rule that produces a shadow token of the given kind.<br>
	 * Shadow tokens are used for tokens that should be ignored during parsing (e.g., whitespace or comments).<br>
	 *
	 * @param name The name of the rule
	 * @param kind The token type the produced token is classified with
	 * @param pattern The character pattern that recognizes the token text
	 * @throws NullPointerException If the name, kind or pattern is null
	 * @throws IllegalArgumentException If the name is empty
	 * @see LexerRule
	 */
	public void shadowRule(@NonNull String name, @NonNull TokenType kind, @NonNull CharRule pattern) {
		this.rules.add(new LexerRule(name, pattern, kind, true));
	}
	
	/**
	 * Builds and returns a new {@link Lexer} instance with the defined alphabet and rules.<br>
	 *
	 * @return A new lexer containing the defined alphabet and rules
	 * @throws IllegalStateException If no allowed characters or no rules have been defined
	 */
	public @NonNull Lexer build() {
		return new Lexer(this.allowedChars, this.rules);
	}
}
