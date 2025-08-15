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

/**
 * Interface for a token.<br>
 * A token is a sequence of characters that matches a token definition.<br>
 * The implementations of this interface serve as a value for a token definition.<br>
 *
 * @author Luis-St
 */
public interface Token {
	
	/**
	 * Returns the token definition that this token matches.<br>
	 * @return The token definition
	 */
	@NotNull TokenDefinition definition();
	
	/**
	 * Returns the value of the token.<br>
	 * @return The value
	 */
	@NotNull String value();
	
	/**
	 * Returns the start position of the token.<br>
	 * The start position is the first character of the token.<br>
	 *
	 * @return The start position
	 */
	@NotNull TokenPosition position();
}
