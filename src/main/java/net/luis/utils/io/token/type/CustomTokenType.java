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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 * @param name
 */
public record CustomTokenType(
	@NotNull String name,
	@Nullable TokenType superType
) implements TokenType {
	
	public CustomTokenType {
		Objects.requireNonNull(name, "Token type name must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Token type name must not be empty");
		}
	}
	
	@Override
	public @NotNull String getName() {
		return this.name;
	}
	
	@Override
	public @Nullable TokenType getSuperType() {
		return this.superType;
	}
}
