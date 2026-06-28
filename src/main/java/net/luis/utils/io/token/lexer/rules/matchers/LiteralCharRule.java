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
 * A character rule that matches a single specific character.<br>
 * This rule is useful for matching exact characters such as operators or delimiters.<br>
 *
 * @see NegatableCharRule
 *
 * @author Luis-St
 *
 * @param character The character to match against
 */
public record LiteralCharRule(
	char character
) implements NegatableCharRule {
	
	@Override
	public boolean match(char c) {
		return c == this.character;
	}
}
