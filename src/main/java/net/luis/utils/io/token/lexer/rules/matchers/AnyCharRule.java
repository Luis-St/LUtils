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
 * A character rule that matches any single character.<br>
 * The set of characters that are actually permitted is enforced by the lexer's alphabet,<br>
 * so this rule effectively matches any character that is part of the allowed character set.<br>
 *
 * @see NegatableCharRule
 *
 * @author Luis-St
 */
public record AnyCharRule() implements NegatableCharRule {
	
	/**
	 * A reusable instance of the any character rule.<br>
	 */
	public static final AnyCharRule INSTANCE = new AnyCharRule();
	
	@Override
	public boolean match(char c) {
		return true;
	}
}
