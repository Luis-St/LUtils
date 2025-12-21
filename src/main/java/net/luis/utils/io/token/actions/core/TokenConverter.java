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

package net.luis.utils.io.token.actions.core;

import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;

/**
 * Token converter that transforms a token into another token.<br>
 * The converter can modify the token properties, including value, definition, and positions.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenConverter {
	
	/**
	 * Converts the given token into another token.<br>
	 * The converter can modify any property of the token.<br>
	 *
	 * @param token The token to convert
	 * @return The converted token
	 * @throws NullPointerException If the token is null
	 * @apiNote This method should return a non-null token
	 */
	@NonNull Token convert(@NonNull Token token);
}
