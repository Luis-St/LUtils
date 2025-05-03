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

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

record StringTokenDefinition(@NotNull String token, boolean equalsIgnoreCase) implements TokenDefinition {
	
	StringTokenDefinition {
		Objects.requireNonNull(token, "Token must not be null");
	}
	
	@Override
	public boolean matches(@NotNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return this.equalsIgnoreCase ? word.equalsIgnoreCase(this.token) : word.equals(this.token);
	}
	
	@Override
	public @NotNull String toString() {
		return "StringTokenDefinition[token=" + this.token.replace("\t", "\\t").replace("\n", "\\n") + ",equalsIgnoreCase=" + this.equalsIgnoreCase + "]";
	}
}
