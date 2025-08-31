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

package net.luis.utils.io.token.rule.rules.combinators;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A token rule that matches a sequence of tokens with a start, between, and end token rule.<br>
 * This rule is useful for matching patterns that have a specific structure, such as a start and end delimiter with optional content in between.<br>
 * The start and end token rules are required, while the between token rule can match zero or infinite tokens.<br>
 *
 * @author Luis-St
 *
 * @param startTokenRule The token rule that marks the start of the sequence
 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
 * @param endTokenRule The token rule that marks the end of the sequence
 */
public record BoundaryTokenRule(
	@NotNull TokenRule startTokenRule,
	@NotNull TokenRule betweenTokenRule,
	@NotNull TokenRule endTokenRule
) implements TokenRule {
	
	/**
	 * Constructs a new boundary token rule with the given start, and end token rule.<br>
	 * The between token rule is set to {@link TokenRules#alwaysMatch()} by default.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @throws NullPointerException If the start or end token rule is null
	 */
	public BoundaryTokenRule(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		this(Objects.requireNonNull(startTokenRule, "Start rule must not be null"), TokenRules.alwaysMatch(), Objects.requireNonNull(endTokenRule, "End rule must not be null"));
	}
	
	/**
	 * Constructs a new boundary token rule with the given start, between, and end token rule.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @throws NullPointerException If the start, between, or end token rule is null
	 */
	public BoundaryTokenRule {
		Objects.requireNonNull(startTokenRule, "Start token rule must not be null");
		Objects.requireNonNull(betweenTokenRule, "Between token rule must not be null");
		Objects.requireNonNull(endTokenRule, "End token rule must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		if (!stream.hasToken()) {
			return null;
		}
		
		int startIndex = stream.getCurrentIndex();
		TokenStream globalWorkingStream = stream.copyWithCurrentIndex();
		TokenRuleMatch startMatch = this.startTokenRule.match(globalWorkingStream);
		if (startMatch == null) {
			return null;
		}
		
		List<Token> matchedTokens = Lists.newArrayList(startMatch.matchedTokens());
		while (globalWorkingStream.hasToken()) {
			TokenStream localWorkingStream = globalWorkingStream.copyWithCurrentIndex();
			TokenRuleMatch endMatch = this.endTokenRule.match(localWorkingStream);
			
			if (endMatch != null) {
				stream.advanceTo(localWorkingStream);
				matchedTokens.addAll(endMatch.matchedTokens());
				return new TokenRuleMatch(startIndex, endMatch.endIndex(), matchedTokens, this);
			}
			
			TokenRuleMatch betweenMatch = this.betweenTokenRule.match(localWorkingStream);
			if (betweenMatch == null) {
				return null;
			}
			
			globalWorkingStream.advanceTo(localWorkingStream);
			if (globalWorkingStream.hasToken()) {
				matchedTokens.addAll(betweenMatch.matchedTokens());
			}
		}
		
		TokenRuleMatch endMatch = this.endTokenRule.match(globalWorkingStream); // Checking the end rule again to include the case where the end rule does not consume any tokens
		if (endMatch != null) {
			stream.advanceTo(globalWorkingStream);
			matchedTokens.addAll(endMatch.matchedTokens());
			return new TokenRuleMatch(startIndex, endMatch.endIndex(), matchedTokens, this);
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new BoundaryTokenRule(this.startTokenRule.not(), this.betweenTokenRule.not(), this.endTokenRule.not());
	}
}
