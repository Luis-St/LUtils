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

package net.luis.utils.io.token.rule.rules.reference;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRuleContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Token rule that captures the tokens matched by another token rule and stores them in the context under a specified key.<br>
 * The captured tokens can be retrieved later using the key.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to capture tokens from
 * @param key The key under which to store the captured tokens in the context
 */
public record CaptureTokenRule(
	@NotNull String key,
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	/**
	 * Creates a new capture token rule using the given token rule and key.<br>
	 *
	 * @param tokenRule The token rule to capture tokens from
	 * @param key The key under which to store the captured tokens in the context
	 * @throws NullPointerException If the token rule or key is null
	 * @throws IllegalArgumentException If the key is empty
	 */
	public CaptureTokenRule {
		Objects.requireNonNull(key, "Key must not be null");
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		
		if (key.isEmpty()) {
			throw new IllegalArgumentException("Key must not be empty");
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		TokenRuleMatch match = this.tokenRule.match(stream, ctx);
		if (match != null) {
			ctx.captureTokens(this.key, match.matchedTokens());
			return match;
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new CaptureTokenRule(this.key, this.tokenRule.not());
	}
}
