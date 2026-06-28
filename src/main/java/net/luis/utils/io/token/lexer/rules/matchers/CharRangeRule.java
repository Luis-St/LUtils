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

package net.luis.utils.io.token.lexer.rules.matchers;

import net.luis.utils.io.token.lexer.rules.NegatableCharRule;

/**
 * A character rule that matches a single character that lies within an inclusive range.<br>
 * This rule is useful for matching character classes such as {@code 'a'} to {@code 'z'} or {@code '0'} to {@code '9'}.<br>
 *
 * @see NegatableCharRule
 *
 * @author Luis-St
 *
 * @param start The start of the range (inclusive)
 * @param end The end of the range (inclusive)
 */
public record CharRangeRule(
	char start,
	char end
) implements NegatableCharRule {
	
	/**
	 * Constructs a new character range rule with the given inclusive range.<br>
	 *
	 * @param start The start of the range (inclusive)
	 * @param end The end of the range (inclusive)
	 * @throws IllegalArgumentException If the end of the range is less than the start of the range
	 */
	public CharRangeRule {
		if (end < start) {
			throw new IllegalArgumentException("End of range must not be less than start of range");
		}
	}
	
	@Override
	public boolean match(char c) {
		return this.start <= c && c <= this.end;
	}
}
