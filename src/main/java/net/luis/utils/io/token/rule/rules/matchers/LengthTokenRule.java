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

package net.luis.utils.io.token.rule.rules.matchers;

import net.luis.utils.io.token.rule.rules.NegatableTokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A token rule that matches tokens based on their value length.<br>
 * This rule is useful for filtering tokens by length constraints.<br>
 *
 * @see NegatableTokenRule
 *
 * @author Luis-St
 *
 * @param minLength The minimum length of the token value (inclusive)
 * @param maxLength The maximum length of the token value (inclusive)
 */
public record LengthTokenRule(
	int minLength,
	int maxLength
) implements NegatableTokenRule {
	
	/**
	 * Constructs a new length token rule with the given minimum and maximum length.<br>
	 *
	 * @param minLength The minimum length of the token value (inclusive)
	 * @param maxLength The maximum length of the token value (inclusive)
	 * @throws IllegalArgumentException If minLength is negative, maxLength is negative, or maxLength is less than minLength
	 */
	public LengthTokenRule {
		if (minLength < 0) {
			throw new IllegalArgumentException("Minimum length must not be negative");
		}
		if (maxLength < 0) {
			throw new IllegalArgumentException("Maximum length must not be negative");
		}
		if (maxLength < minLength) {
			throw new IllegalArgumentException("Maximum length must not be less than minimum length");
		}
	}
	
	@Override
	public boolean match(@NotNull Token token) {
		Objects.requireNonNull(token, "Token must not be null");
		
		int length = token.value().length();
		return length >= this.minLength && length <= this.maxLength;
	}
}
