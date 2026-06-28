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

package net.luis.utils.io.token.lexer;

import net.luis.utils.io.token.lexer.rules.CharRule;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * A class that represents a match of a {@link CharRule} against a character stream.<br>
 * It contains the start and end index of the match and the matched character sequence.<br>
 * It is the lexer-side analogue of the parser-side token rule match.<br>
 *
 * @author Luis-St
 *
 * @param startIndex The start index of the match
 * @param endIndex The end index of the match (exclusive)
 * @param matched The matched character sequence
 */
public record CharRuleMatch(
	int startIndex,
	int endIndex,
	@NonNull String matched
) {
	
	/**
	 * Constructs a new character rule match with the given start and end index and matched character sequence.<br>
	 *
	 * @param startIndex The start index of the match
	 * @param endIndex The end index of the match (exclusive)
	 * @param matched The matched character sequence
	 * @throws NullPointerException If the matched character sequence is null
	 */
	public CharRuleMatch {
		Objects.requireNonNull(matched, "Matched character sequence must not be null");
	}
	
	/**
	 * Creates an empty character rule match with the given index.<br>
	 * An empty character rule match has a start and end index of the given index and an empty matched character sequence.<br>
	 * This is useful for zero-width matches such as anchors that do not consume any characters.<br>
	 *
	 * @param index The index of the empty character rule match
	 * @return An empty character rule match
	 */
	public static @NonNull CharRuleMatch empty(int index) {
		return new CharRuleMatch(index, index, "");
	}
}
