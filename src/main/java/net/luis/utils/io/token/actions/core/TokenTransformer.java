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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Token transformer that transforms a list of tokens into another list of tokens.<br>
 * The transformer can modify the tokens, remove them, or add new tokens.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenTransformer {
	
	/**
	 * Transforms the given list of tokens into another list of tokens.<br>
	 * The transformer can modify the tokens, remove them, or add new tokens.<br>
	 *
	 * @param tokens The list of tokens to transform
	 * @return The transformed list of tokens
	 * @apiNote This method should return an unmodifiable list of tokens
	 */
	@NotNull
	@Unmodifiable
	List<Token> transform(@NotNull List<Token> tokens);
}
