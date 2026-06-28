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
 * A character rule that matches a character rule a number of times.<br>
 * This rule is useful for creating complex matching logic by repeating a rule.<br>
 * It will match the character rule a number of times between the min and max occurrences.<br>
 * It covers the regular quantifiers {@code *}, {@code +}, {@code ?} and {@code {n,m}}.<br>
 *
 * @author Luis-St
 *
 * @param charRule The character rule to match
 * @param minOccurrences The minimum number of occurrences
 * @param maxOccurrences The maximum number of occurrences
 */
public record CharRepeatedRule(
	@NonNull CharRule charRule,
	int minOccurrences,
	int maxOccurrences
) implements CharRule {
	
	/**
	 * Constructs a new repeated character rule with the given character rule and exact number of occurrences.<br>
	 *
	 * @param charRule The character rule to match
	 * @param occurrences The exact number of occurrences
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 */
	public CharRepeatedRule(@NonNull CharRule charRule, int occurrences) {
		this(charRule, occurrences, occurrences);
	}
	
	/**
	 * Constructs a new repeated character rule with the given character rule and min and max number of occurrences.<br>
	 *
	 * @param charRule The character rule to match
	 * @param minOccurrences The minimum number of occurrences
	 * @param maxOccurrences The maximum number of occurrences
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the min or max occurrences are lower than 0, or if the max occurrences are lower than the min occurrences, or if both are 0
	 */
	public CharRepeatedRule {
		Objects.requireNonNull(charRule, "Character rule must not be null");
		
		if (minOccurrences < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (maxOccurrences < minOccurrences) {
			throw new IllegalArgumentException("Max occurrences must not be less than min occurrences");
		}
		if (maxOccurrences == 0) {
			throw new IllegalArgumentException("Min and max occurrences must not be 0, this rule will never match");
		}
	}
	
	@Override
	public @Nullable CharRuleMatch match(@NonNull CharStream stream) {
		Objects.requireNonNull(stream, "Character stream must not be null");
		
		int startIndex = stream.getCurrentIndex();
		CharStream workingStream = stream.copyWithOffset(0);
		StringBuilder matched = new StringBuilder();
		
		int occurrences = 0;
		while (workingStream.hasMore() && occurrences < this.maxOccurrences) {
			int previousIndex = workingStream.getCurrentIndex();
			CharRuleMatch match = this.charRule.match(workingStream);
			if (match == null) {
				break;
			}
			if (workingStream.getCurrentIndex() == previousIndex) {
				break;
			}
			
			matched.append(match.matched());
			occurrences++;
		}
		
		if (this.minOccurrences <= occurrences && occurrences <= this.maxOccurrences) {
			stream.advanceTo(workingStream.getCurrentIndex());
			return new CharRuleMatch(startIndex, stream.getCurrentIndex(), matched.toString());
		}
		return null;
	}
}
