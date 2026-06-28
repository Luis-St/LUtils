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

package net.luis.utils.io.token.lexer.rules.quantifiers;

import net.luis.utils.io.token.lexer.CharRuleMatch;
import net.luis.utils.io.token.lexer.rules.CharRule;
import net.luis.utils.io.token.lexer.stream.CharStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A character rule that matches a single character rule optionally.<br>
 * This rule is useful for creating optional matching logic.<br>
 * It will match the character rule if it is present, otherwise it will return an empty (zero-width) match.<br>
 *
 * @author Luis-St
 *
 * @param charRule The character rule to match optionally
 */
public record CharOptionalRule(
	@NonNull CharRule charRule
) implements CharRule {
	
	/**
	 * Constructs a new optional character rule with the given character rule.<br>
	 *
	 * @param charRule The character rule to match optionally
	 * @throws NullPointerException If the character rule is null
	 */
	public CharOptionalRule {
		Objects.requireNonNull(charRule, "Character rule must not be null");
	}
	
	@Override
	public @Nullable CharRuleMatch match(@NonNull CharStream stream) {
		Objects.requireNonNull(stream, "Character stream must not be null");
		
		int startIndex = stream.getCurrentIndex();
		CharRuleMatch match = this.charRule.match(stream);
		if (match != null) {
			return match;
		}
		return CharRuleMatch.empty(startIndex);
	}
	
	@Override
	public @NonNull CharRule not() {
		return new CharOptionalRule(this.charRule.not());
	}
}
