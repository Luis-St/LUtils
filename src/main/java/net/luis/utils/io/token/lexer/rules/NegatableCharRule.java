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

package net.luis.utils.io.token.lexer.rules;

import net.luis.utils.io.token.lexer.CharRuleMatch;
import net.luis.utils.io.token.lexer.stream.CharStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A functional interface representing a character rule that can be inverted.<br>
 * It extends the {@link CharRule} interface and provides a method to match a single character against the rule.<br>
 * The rule can be negated using the {@link #not()} method, which returns a new {@link CharRule} that matches characters not matching the original rule.<br>
 * <p>
 *     The rule only handles a single character at a time, and the matching logic is defined in the {@link #match(char)} method.<br>
 *     The {@link #match(CharStream)} method is overridden to provide a default implementation that checks the current character in the stream against the rule.<br>
 *     If the character matches, it consumes the character and returns a {@link CharRuleMatch} containing the matched character; otherwise, it returns null.
 * </p>
 * <p>
 *     The {@link #not()} method creates a new rule that negates the original rule's logic.<br>
 *     If the inverted rule is inverted again, it returns the original rule, eliminating double negation.
 * </p>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface NegatableCharRule extends CharRule {
	
	/**
	 * Matches the given character against the logic of the rule.<br>
	 * This method should be implemented to define the specific matching logic of the rule.<br>
	 *
	 * @param c The character to match against the rule
	 * @return True if the character matches the rule, false otherwise
	 */
	boolean match(char c);
	
	@Override
	default @Nullable CharRuleMatch match(@NonNull CharStream stream) {
		Objects.requireNonNull(stream, "Character stream must not be null");
		if (!stream.hasMore()) {
			return null;
		}
		
		int startIndex = stream.getCurrentIndex();
		char c = stream.getCurrentChar();
		if (this.match(c)) {
			stream.advanceTo(startIndex + 1);
			return new CharRuleMatch(startIndex, stream.getCurrentIndex(), String.valueOf(c));
		}
		return null;
	}
	
	@Override
	default @NonNull CharRule not() {
		return new CharRule() {
			@Override
			public @Nullable CharRuleMatch match(@NonNull CharStream stream) {
				Objects.requireNonNull(stream, "Character stream must not be null");
				if (!stream.hasMore()) {
					return null;
				}
				
				int startIndex = stream.getCurrentIndex();
				char c = stream.getCurrentChar();
				if (!NegatableCharRule.this.match(c)) {
					stream.advanceTo(startIndex + 1);
					return new CharRuleMatch(startIndex, stream.getCurrentIndex(), String.valueOf(c));
				}
				return null;
			}
			
			@Override
			public @NonNull CharRule not() {
				return NegatableCharRule.this; // Negating the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
}
