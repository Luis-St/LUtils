/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A token rule that matches only at the start of the token list.<br>
 * This rule is useful to ensure matching begins at the very start of input.<br>
 * The rule will only match if the start index is 0.<br>
 * This class is implemented as a singleton and can be accessed via {@link TokenRules#start()} or {@link #INSTANCE}.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class StartTokenRule implements TokenRule {
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final StartTokenRule INSTANCE = new StartTokenRule();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private StartTokenRule() {}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex == 0) {
			return TokenRuleMatch.empty(startIndex);
		}
		return null;
	}
}
