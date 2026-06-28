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

package net.luis.utils.io.token.lexer.rules.anchors;

import net.luis.utils.io.token.lexer.CharRuleMatch;
import net.luis.utils.io.token.lexer.rules.CharRule;
import net.luis.utils.io.token.lexer.stream.CharStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * A zero-width character rule that matches the start of the input or the start of a line.<br>
 * This rule does not consume any characters and is useful for line-sensitive tokens.<br>
 *
 * @author Luis-St
 */
public enum StartCharRule implements CharRule {
	
	/**
	 * Matches the start of the entire input.<br>
	 */
	INPUT,
	/**
	 * Matches the start of a line, which is either the start of the input or the position directly after a newline character.<br>
	 */
	LINE;
	
	@Override
	public @Nullable CharRuleMatch match(@NonNull CharStream stream) {
		Objects.requireNonNull(stream, "Character stream must not be null");
		
		int index = stream.getCurrentIndex();
		boolean matches = switch (this) {
			case INPUT -> index == 0;
			case LINE -> index == 0 || stream.getInput().charAt(index - 1) == '\n';
		};
		return matches ? CharRuleMatch.empty(index) : null;
	}
}
