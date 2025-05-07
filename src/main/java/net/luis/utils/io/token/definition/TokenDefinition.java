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

package net.luis.utils.io.token.definition;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenDefinition extends TokenRule {
	
	TokenDefinition WORD = WordTokenDefinition.INSTANCE;
	
	static @NotNull TokenDefinition of(char token) {
		return new CharTokenDefinition(token);
	}
	
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase) {
		Objects.requireNonNull(token, "Token must not be null");
		return new StringTokenDefinition(token, equalsIgnoreCase);
	}
	
	static @NotNull TokenDefinition ofEscaped(char token) {
		return new EscapedTokenDefinition(token);
	}
	
	boolean matches(@NotNull String word);
	
	@Override
	default @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		if (this.equals(tokens.get(startIndex).definition())) {
			List<Token> matchedTokens = Collections.singletonList(tokens.get(startIndex));
			return new TokenRuleMatch(startIndex, startIndex + 1, matchedTokens, this);
		}
		return null;
	}
}
