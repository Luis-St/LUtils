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
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A token rule that applies an inner rule to the tokens within a {@link TokenGroup} rather than to the group itself.<br>
 * This rule matches if the token at the start index is a {@link TokenGroup} and the inner rule matches 
 * when applied to the tokens contained within that group.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The inner token rule to apply to the tokens within the group
 */
public record TokenGroupRule(
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	/**
	 * Constructs a new token group rule with the given inner token rule.<br>
	 *
	 * @param tokenRule The inner token rule to apply to the tokens within the group
	 * @throws NullPointerException If the token rule is null
	 */
	public TokenGroupRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		if (!stream.hasMoreTokens()) {
			return null;
		}
		
		int startIndex = stream.getCurrentIndex();
		Token token = stream.getCurrentToken();
		if (!(token instanceof TokenGroup tokenGroup)) {
			return null;
		}
		
		TokenRuleMatch innerMatch = this.tokenRule.match(TokenStream.createMutable(tokenGroup.tokens()), ctx);
		if (innerMatch == null) {
			return null;
		}
		return new TokenRuleMatch(startIndex, stream.advance(), List.of(tokenGroup), this);
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new TokenGroupRule(this.tokenRule.not());
	}
}
