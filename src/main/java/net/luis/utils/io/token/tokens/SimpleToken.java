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

import com.google.common.collect.Sets;
import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.type.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

/**
 * Token implementation for a simple string token.<br>
 *
 * @author Luis-St
 *
 * @param value The string value of the token
 * @param position The position of the token
 * @param types The token types of the token
 */
public record SimpleToken(
	@NotNull String value,
	@NotNull TokenPosition position,
	@NotNull Set<TokenType> types
) implements Token {
	
	/**
	 * Constructs a new simple token for a string value without any types.<br>
	 *
	 * @param value The string value of the token
	 * @param position The start position of the token
	 * @throws NullPointerException If the token value or the position is null
	 */
	public SimpleToken(@NotNull String value, @NotNull TokenPosition position) {
		this(value, position, Set.of());
	}
	
	/**
	 * Constructs a new simple token for a string value.<br>
	 *
	 * @param value The string value of the token
	 * @param position The start position of the token
	 * @param types The token types of the token
	 * @throws NullPointerException If the token value, the position, or the types are null
	 */
	public SimpleToken {
		Objects.requireNonNull(value, "Token value must not be null");
		Objects.requireNonNull(position, "Token position must not be null");
		Objects.requireNonNull(types, "Token types must not be null");
		types = Sets.newHashSet(types);
	}
	
	/**
	 * Creates an unpositioned simple token for the given value.<br>
	 *
	 * @param value The string value of the token
	 * @return The unpositioned simple token
	 * @throws NullPointerException If the token value is null
	 */
	public static @NotNull SimpleToken createUnpositioned(@NotNull String value) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED);
	}
	
	@Override
	public @NotNull String toString() {
		return "SimpleToken[value=" + this.value.replace("\t", "\\\\t").replace("\n", "\\\\n") + ",position=" + this.position + "]";
	}
}
