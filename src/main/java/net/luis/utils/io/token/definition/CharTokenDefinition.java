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

package net.luis.utils.io.token.definition;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Token definition for a single character.<br>
 * This token definition matches a string that is equal to the token character.<br>
 *
 * @author Luis-St
 *
 * @param token The token character
 */
public record CharTokenDefinition(
	char token
) implements TokenDefinition {
	
	@Override
	public boolean matches(@NonNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return word.length() == 1 && word.charAt(0) == this.token;
	}
	
	@Override
	public @NonNull String toString() {
		return "CharTokenDefinition[token=" + ("" + this.token).replace("\t", "\\t").replace("\n", "\\n") + "]";
	}
}
