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

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.lexer.rules.CharRule;
import net.luis.utils.io.token.tokens.*;
import net.luis.utils.io.token.type.TokenType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single rule within a lexer, combining a character matching pattern with the token kind it produces.<br>
 * <p>
 *     A lexer rule encapsulates the regular {@link CharRule} pattern that recognizes the token's text and the
 *     {@link TokenType} that classifies the produced token.<br>
 *     The classification is therefore folded directly into the rule, replacing a separate classification step.
 * </p>
 * <p>
 *     If the matched text is an escape sequence (a backslash followed by a single character), the produced token is an
 *     {@link EscapedToken}, otherwise a {@link SimpleToken} is produced.<br>
 *     A shadow rule produces a shadow token, which is used for tokens that should be ignored during parsing (e.g., whitespace or comments).
 * </p>
 *
 * @author Luis-St
 *
 * @param name The name of the rule
 * @param pattern The character pattern that recognizes the token text
 * @param type The token type the produced token is classified with
 * @param shadow Whether the produced token is a shadow token
 */
public record LexerRule(
	@NonNull String name,
	@NonNull CharRule pattern,
	@NonNull TokenType type,
	boolean shadow
) {
	
	/**
	 * Constructs a new lexer rule with the given name, pattern, token type and shadow flag.<br>
	 *
	 * @param name The name of the rule
	 * @param pattern The character pattern that recognizes the token text
	 * @param type The token type the produced token is classified with
	 * @param shadow Whether the produced token is a shadow token
	 * @throws NullPointerException If the name, pattern or token type is null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public LexerRule {
		Objects.requireNonNull(name, "Rule name must not be null");
		Objects.requireNonNull(pattern, "Rule pattern must not be null");
		Objects.requireNonNull(type, "Token type must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Rule name must not be empty");
		}
	}
	
	/**
	 * Creates a token from the given matched text and position according to this rule.<br>
	 * The token is classified with this rule's token type and shadowed if this rule is a shadow rule.<br>
	 *
	 * @param matched The matched text of the token
	 * @param position The start position of the token
	 * @return The created token
	 * @throws NullPointerException If the matched text or position is null
	 * @see SimpleToken
	 * @see EscapedToken
	 */
	public @NonNull Token createToken(@NonNull String matched, @NonNull TokenPosition position) {
		Objects.requireNonNull(matched, "Matched text must not be null");
		Objects.requireNonNull(position, "Token position must not be null");
		
		Token token;
		if (matched.length() == 2 && matched.charAt(0) == '\\') {
			token = new EscapedToken(matched, position);
		} else {
			token = new SimpleToken(matched, position);
		}
		
		token.types().add(this.type);
		return this.shadow ? token.shadow() : token;
	}
}
