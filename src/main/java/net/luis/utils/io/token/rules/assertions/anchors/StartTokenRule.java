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

package net.luis.utils.io.token.rules.assertions.anchors;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A token rule that matches the start of the token list or start of line.<br>
 * This rule is useful to ensure matching begins at the very start of input or line.<br>
 * <ul>
 *   <li>{@link #DOCUMENT} matches only when the start index is 0</li>
 *   <li>{@link #LINE} matches when the start index is at the beginning of a line or at the start of the document</li>
 * </ul>
 * <p>
 *     <strong>Note</strong>: {@link #LINE} might be inconsistent if the token list has been modified (e.g., tokens added or removed) after the initial parsing,
 *     as it relies on line numbers and newline characters to determine line starts.
 *     Ensure the token list is in a consistent state before applying this rule.
 * </p>
 *
 * @author Luis-St
 */
public enum StartTokenRule implements TokenRule {
	
	/**
	 * Matches the start of the document.<br>
	 * This rule matches only when the current index is 0, indicating the very start of the token list.<br>
	 */
	DOCUMENT {
		@Override
		public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
			Objects.requireNonNull(stream, "Token stream must not be null");
			Objects.requireNonNull(ctx, "Token rule context must not be null");
			
			if (stream.getCurrentIndex() == 0) {
				return TokenRuleMatch.empty(0, this);
			}
			return null;
		}
	},
	/**
	 * Matches the start of a line or document.<br>
	 * This rule matches when the current index is at the beginning of a line or at the start of the document.<br>
	 */
	LINE {
		@Override
		@SuppressWarnings("DuplicatedCode")
		public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
			Objects.requireNonNull(stream, "Token stream must not be null");
			Objects.requireNonNull(ctx, "Token rule context must not be null");
			if (stream.getCurrentIndex() == 0) {
				return TokenRuleMatch.empty(0, this);
			}
			if (!stream.hasMoreTokens()) {
				return null;
			}
			
			Token currentToken = stream.getCurrentToken();
			TokenStream lookbehindStream = stream.createLookbehindStream();
			if (!lookbehindStream.hasMoreTokens()) {
				return null;
			}
			
			Token previousToken = lookbehindStream.getCurrentToken();
			if (currentToken.position().isPositioned() && previousToken.position().isPositioned()) {
				if (currentToken.position().line() > previousToken.position().line()) {
					return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
				}
			}
			
			if (previousToken.value().contains("\n")) {
				return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
			}
			return null;
		}
	};
	
	@Override
	public @NotNull TokenRule not() {
		return new TokenRule() {
			private final StartTokenRule self = StartTokenRule.this;
			
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				Objects.requireNonNull(ctx, "Token rule context must not be null");
				
				TokenRuleMatch result = this.self.match(stream, ctx);
				if (result != null) {
					return null;
				}
				return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
			}
			
			@Override
			public @NotNull TokenRule not() {
				return this.self; // Negating the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
}
