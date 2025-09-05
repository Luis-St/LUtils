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
 * A token rule that matches the value of a single token against a specific string value.<br>
 * This rule is useful for creating simple matching logic by comparing the token's value to a predefined string.<br>
 *
 * @see NegatableTokenRule
 *
 * @author Luis-St
 *
 * @param value The value to match against
 * @param ignoreCase Whether to ignore case when matching or not
 */
public record ValueTokenRule(
	@NotNull String value,
	boolean ignoreCase
) implements NegatableTokenRule {
	
	/**
	 * Constructs a new value token rule from the given character and case sensitivity option.<br>
	 *
	 * @param value The character to match against
	 * @param ignoreCase Whether to ignore case when matching or not
	 * @throws IllegalArgumentException If the value is empty
	 */
	public ValueTokenRule(char value, boolean ignoreCase) {
		this(String.valueOf(value), ignoreCase);
	}
	
	/**
	 * Constructs a new value token rule from the given string and case sensitivity option.<br>
	 *
	 * @param value The string to match against
	 * @param ignoreCase Whether to ignore case when matching or not
	 * @throws NullPointerException If the value is null
	 * @throws IllegalArgumentException If the value is empty
	 */
	public ValueTokenRule {
		Objects.requireNonNull(value, "Value must not be null");
		if (value.isEmpty()) {
			throw new IllegalArgumentException("Value must not be empty");
		}
	}
	
	@Override
	public boolean match(@NotNull Token token) {
		Objects.requireNonNull(token, "Token must not be null");
		
		return this.ignoreCase ? token.value().equalsIgnoreCase(this.value) : token.value().equals(this.value);
	}
}
