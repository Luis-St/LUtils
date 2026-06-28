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
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

/**
 * A character rule that matches a single character that is a member of a set of characters.<br>
 * This rule is useful for matching one of several allowed characters such as a set of operator symbols.<br>
 *
 * @see NegatableCharRule
 *
 * @author Luis-St
 *
 * @param characters The set of characters to match against
 */
public record CharSetRule(
	@NonNull Set<Character> characters
) implements NegatableCharRule {
	
	/**
	 * Constructs a new character set rule with the given set of characters.<br>
	 *
	 * @param characters The set of characters to match against
	 * @throws NullPointerException If the set of characters is null
	 * @throws IllegalArgumentException If the set of characters is empty
	 */
	public CharSetRule {
		Objects.requireNonNull(characters, "Character set must not be null");
		if (characters.isEmpty()) {
			throw new IllegalArgumentException("Character set must not be empty");
		}
		characters = Set.copyOf(characters);
	}
	
	@Override
	public boolean match(char c) {
		return this.characters.contains(c);
	}
}
