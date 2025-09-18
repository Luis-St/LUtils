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

package net.luis.utils.io.token.context;

import net.luis.utils.io.token.stream.MutableTokenStream;
import net.luis.utils.io.token.stream.TokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Objects;

/**
 * Context for a token action, containing the token stream.<br>
 *
 * @author Luis-St
 *
 * @param stream The token stream immutable view after the associated token rule was matched
 */
public record TokenActionContext(
	@NotNull @Unmodifiable TokenStream stream
) {
	
	/**
	 * Constructs a new token action context with the given token stream.<br>
	 *
	 * @param stream The token stream immutable view after the associated token rule was matched
	 * @throws NullPointerException If the token stream is null
	 * @throws IllegalArgumentException If the token stream is mutable
	 */
	public TokenActionContext {
		Objects.requireNonNull(stream, "The token stream must not be null");
		if (stream instanceof MutableTokenStream) {
			throw new IllegalArgumentException("The token stream must be immutable");
		}
	}
}
