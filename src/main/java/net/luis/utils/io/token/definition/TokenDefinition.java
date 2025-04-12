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

package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.TokenCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 *
 * @author Luis-St
 *
 */

public interface TokenDefinition {
	
	TokenDefinition WORD = new WordTokenDefinition();
	
	static @NotNull TokenDefinition of(char token) {
		return new CharTokenDefinition(token);
	}
	
	static @NotNull TokenDefinition of(char token, @NotNull TokenCategory category) {
		return new CharTokenDefinition(token, Optional.of(category));
	}
	
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase) {
		return new StringTokenDefinition(token, equalsIgnoreCase);
	}
	
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase, @NotNull TokenCategory category) {
		return new StringTokenDefinition(token, equalsIgnoreCase, Optional.of(category));
	}
	
	@NotNull Optional<TokenCategory> category();
	
	default boolean isCategory(@NotNull TokenCategory category) {
		return this.category().isPresent() && this.category().get() == category;
	}
	
	boolean matches(@NotNull String word);
}
