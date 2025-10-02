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

package net.luis.utils.io.token.type.classifier;

import net.luis.utils.io.token.TokenReader;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.type.TokenType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * A functional interface for classifying tokens into their respective types.<br>
 * It defines a single method that takes a token and returns a set of token types.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenClassifier {
	
	/**
	 * Classifies the given token and returns a set of token types.<br>
	 * The returned set can be empty or contain multiple types if the token matches multiple classifications.<br>
	 *
	 * @param token The token to classify
	 * @return A set of token types that the token belongs to
	 * @throws NullPointerException If the token is null
	 * @see TokenReader
	 */
	@NotNull Set<TokenType> classifyToken(@NotNull Token token);
}
