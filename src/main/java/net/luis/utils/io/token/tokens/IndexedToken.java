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
import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Token implementation that wraps another token with index information.<br>
 * This token preserves all properties of the wrapped token while adding index support.<br>
 * The index typically represents the position of the token in a sequence or list.<br>
 *
 * @author Luis-St
 *
 * @param token The wrapped token
 * @param index The index associated with the token
 */
public record IndexedToken(
	@NotNull Token token,
	int index
) implements Token {
	
	/**
	 * Constructs a new indexed token with the given token and index.<br>
	 *
	 * @param token The wrapped token
	 * @param index The index associated with the token
	 * @throws NullPointerException If the token is null
	 * @throws IllegalArgumentException If the index is negative
	 */
	public IndexedToken {
		Objects.requireNonNull(token, "Token must not be null");
		if (index < 0) {
			throw new IllegalArgumentException("Index must not be negative");
		}
	}
	
	/**
	 * Creates an indexed token with index 0.<br>
	 *
	 * @param token The wrapped token
	 * @return A new indexed token with index 0
	 * @throws NullPointerException If the token is null
	 */
	public static @NotNull IndexedToken first(@NotNull Token token) {
		return new IndexedToken(token, 0);
	}
	
	@Override
	public @NotNull TokenDefinition definition() {
		return this.token.definition();
	}
	
	@Override
	public @NotNull String value() {
		return this.token.value();
	}
	
	@Override
	public @NotNull TokenPosition position() {
		return this.token.position();
	}
	
	/**
	 * Checks if this is the first token (index 0).<br>
	 *
	 * @return True if the index is 0, false otherwise
	 */
	public boolean isFirst() {
		return this.index == 0;
	}
	
	/**
	 * Checks if this token has the given index.<br>
	 *
	 * @param expectedIndex The expected index
	 * @return True if the index matches, false otherwise
	 */
	public boolean hasIndex(int expectedIndex) {
		return this.index == expectedIndex;
	}
}
