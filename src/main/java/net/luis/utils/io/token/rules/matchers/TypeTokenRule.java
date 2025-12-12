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

package net.luis.utils.io.token.rules.matchers;

import net.luis.utils.io.token.rules.NegatableTokenRule;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.type.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * A token rule that matches tokens based on their token types.<br>
 * This rule checks whether a token contains all the specified token types, making it useful for filtering tokens by their type classifications.<br>
 * <p>
 *     A token matches this rule if and only if its type set contains all the types specified in this rule.<br>
 *     Additional types on the token beyond those specified do not prevent a match.
 * </p>
 *
 * @see NegatableTokenRule
 *
 * @author Luis-St
 *
 * @param tokenTypes The set of token types that must all be present on a matching token
 */
public record TypeTokenRule(
	@NotNull Set<TokenType> tokenTypes
) implements NegatableTokenRule {
	
	/**
	 * Constructs a new type token rule with the given set of token types.<br>
	 *
	 * @param tokenTypes The set of token types that must all be present on a matching token
	 * @throws NullPointerException If the token types set is null
	 * @throws IllegalArgumentException If the token types set is empty
	 */
	public TypeTokenRule {
		Objects.requireNonNull(tokenTypes, "Token types must not be null");
		
		if (tokenTypes.isEmpty()) {
			throw new IllegalArgumentException("Token types must not be empty");
		}
	}
	
	@Override
	public boolean match(@NotNull Token token) {
		Objects.requireNonNull(token, "Token must not be null");
		return token.types().containsAll(this.tokenTypes);
	}
}
