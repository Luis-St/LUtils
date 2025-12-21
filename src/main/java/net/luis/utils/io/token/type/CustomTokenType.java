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

package net.luis.utils.io.token.type;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Implementation of a custom token type.<br>
 * Allows the creation of user-defined token types with an optional super type.<br>
 *
 * @author Luis-St
 *
 * @param name The name of the token type
 * @param superType The super type of the token type, can be null
 */
public record CustomTokenType(
	@NonNull String name,
	@Nullable TokenType superType
) implements TokenType {
	
	/**
	 * Creates a new custom token type with the given name and optional super type.<br>
	 *
	 * @param name The name of the token type
	 * @param superType The super type of the token type, can be null
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public CustomTokenType {
		Objects.requireNonNull(name, "Token type name must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Token type name must not be empty");
		}
	}
	
	@Override
	public @NonNull String getName() {
		return this.name;
	}
	
	@Override
	public @Nullable TokenType getSuperType() {
		return this.superType;
	}
}
