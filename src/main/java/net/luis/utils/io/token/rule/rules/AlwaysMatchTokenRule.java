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

import java.util.*;

/**
 * A token rule that always matches a single token if the start index is valid.<br>
 * This rule is useful for testing or as a default case in a chain of token rules.<br>
 * This class is implemented as a singleton and can be accessed via {@link TokenRules#alwaysMatch()} or {@link #INSTANCE}.<br>
 *
 * @author Luis-St
 */
@Singleton
public final class AlwaysMatchTokenRule implements TokenRule {
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final AlwaysMatchTokenRule INSTANCE = new AlwaysMatchTokenRule();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private AlwaysMatchTokenRule() {}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size() || startIndex < 0) {
			return null;
		}
		
		List<Token> matchedTokens = Collections.singletonList(tokens.get(startIndex));
		return new TokenRuleMatch(startIndex, startIndex + 1, matchedTokens, this);
	}
}
