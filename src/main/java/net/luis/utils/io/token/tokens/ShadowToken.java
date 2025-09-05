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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Token implementation that wraps another token to indicate it is a shadow token.<br>
 * A shadow token is a token that does not affect the parsing process.<br>
 * It is used to mark tokens that should be ignored during parsing but still need to be retained for other purposes (e.g., comments, whitespace).<br>
 * Shadow tokens are typically skipped by parsers and do not contribute to the syntactic structure of the input.<br>
 *
 * @author Luis-St
 *
 * @param token The wrapped token
 */
public record ShadowToken(
	@NotNull Token token
) implements Token {
	
	/**
	 * Constructs a new shadow token with the given token.<br>
	 *
	 * @param token The wrapped token
	 * @throws NullPointerException If the token is null
	 */
	public ShadowToken {
		Objects.requireNonNull(token, "Token must not be null");
	}
	
	@Override
	public @NotNull String value() {
		return this.token.value();
	}
	
	@Override
	public @NotNull TokenPosition position() {
		return this.token.position();
	}
	
	@Override
	public @NotNull Token shadow() {
		return this; // Already a shadow token
	}
	
	@Override
	public @NotNull Token unshadow() {
		return this.token;
	}
}
