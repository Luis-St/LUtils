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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A token rule that matches the value of a single token against a regular expression pattern.<br>
 * This rule is useful for creating complex matching logic by using regular expressions.<br>
 * It will match the token if its value matches the pattern, otherwise it will return null.<br>
 *
 * @author Luis-St
 *
 * @param pattern The pattern to match against
 */
public record PatternTokenRule(
	@NotNull Pattern pattern
) implements TokenRule {
	
	/**
	 * Constructs a new pattern token rule from the given pattern in string format.<br>
	 *
	 * @param regex The regex pattern to match against
	 * @throws NullPointerException If the regex pattern is null
	 */
	public PatternTokenRule(@Language("RegExp") @NotNull String regex) {
		this(Pattern.compile(Objects.requireNonNull(regex, "Regex must not be null")));
	}
	
	/**
	 * Constructs a new pattern token rule from the given pattern.<br>
	 *
	 * @param pattern The pattern to match against
	 * @throws NullPointerException If the pattern is null
	 */
	public PatternTokenRule {
		Objects.requireNonNull(pattern, "Pattern must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		Matcher matcher = this.pattern.matcher(tokens.get(startIndex).value());
		if (matcher.matches()) {
			List<Token> matchedTokens = Collections.singletonList(tokens.get(startIndex));
			return new TokenRuleMatch(startIndex, startIndex + 1, matchedTokens, this);
		}
		return null;
	}
}
