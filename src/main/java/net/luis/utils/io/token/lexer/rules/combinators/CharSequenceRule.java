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

package net.luis.utils.io.token.lexer.rules.combinators;

import net.luis.utils.io.token.lexer.CharRuleMatch;
import net.luis.utils.io.token.lexer.rules.CharRule;
import net.luis.utils.io.token.lexer.stream.CharStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A character rule that matches a sequence (concatenation) of character rules.<br>
 * This rule is useful for creating complex matching logic by combining multiple rules.<br>
 * It will match the character rules in the order they are provided, otherwise it will return null.<br>
 *
 * @author Luis-St
 *
 * @param charRules The list of character rules to match in sequence
 */
public record CharSequenceRule(
	@NonNull List<CharRule> charRules
) implements CharRule {
	
	/**
	 * Constructs a new character sequence rule with the given character rules.<br>
	 *
	 * @param charRules The list of character rules to match in sequence
	 * @throws NullPointerException If the character rule list or any of its elements are null
	 * @throws IllegalArgumentException If the character rule list is empty or contains less than two rules
	 */
	public CharSequenceRule {
		Objects.requireNonNull(charRules, "Character rule list must not be null");
		if (charRules.isEmpty()) {
			throw new IllegalArgumentException("Character rule list must not be empty");
		}
		if (charRules.size() == 1) {
			throw new IllegalArgumentException("At least two character rules are required");
		}
		
		for (CharRule charRule : charRules) {
			Objects.requireNonNull(charRule, "Character rule list must not contain a null element");
		}
		charRules = List.copyOf(charRules);
	}
	
	@Override
	public @Nullable CharRuleMatch match(@NonNull CharStream stream) {
		Objects.requireNonNull(stream, "Character stream must not be null");
		
		int startIndex = stream.getCurrentIndex();
		CharStream workingStream = stream.copyWithOffset(0);
		StringBuilder matched = new StringBuilder();
		for (CharRule charRule : this.charRules) {
			CharRuleMatch match = charRule.match(workingStream);
			if (match == null) {
				return null;
			}
			matched.append(match.matched());
		}
		
		stream.advanceTo(workingStream.getCurrentIndex());
		return new CharRuleMatch(startIndex, stream.getCurrentIndex(), matched.toString());
	}
	
	@Override
	public @NonNull CharRule not() {
		return new CharAnyOfRule(this.charRules.stream().map(CharRule::not).toList()); // Negation using De Morgan's laws
	}
}
