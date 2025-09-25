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
 * A token rule that matches the end of the token list or end of line.<br>
 * This rule is useful to ensure that the end of the token list or line is reached.<br>
 * <ul>
 *   <li>{@link #DOCUMENT} matches when the start index is larger than or equal to the size of the token list</li>
 *   <li>{@link #LINE} matches when the start index is at the end of a line or at the end of the document</li>
 * </ul>
 * <p>
 *     <strong>Note</strong>: {@link #LINE} might be inconsistent if the token list has been modified (e.g., tokens added or removed) after the initial parsing,
 *     as it relies on line numbers and newline characters to determine line starts.
 *     Ensure the token list is in a consistent state before applying this rule.
 * </p>
 *
 * @author Luis-St
 */
public enum EndTokenRule implements TokenRule {
	
	/**
	 * Match at document end.<br>
	 * This rule matches when the current index is at or beyond the end of the token list.<br>
	 */
	DOCUMENT {
		@Override
		public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
			Objects.requireNonNull(stream, "Token stream must not be null");
			Objects.requireNonNull(ctx, "Token rule context must not be null");
			
			if (!stream.hasMoreTokens()) {
				return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
			}
			return null;
		}
	},
	/**
	 * Match at line end.<br>
	 * This rule matches when the current index is at the end of a line or at the end of the document.<br>
	 */
	LINE {
		@Override
		@SuppressWarnings("DuplicatedCode")
		public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
			Objects.requireNonNull(stream, "Token stream must not be null");
			Objects.requireNonNull(ctx, "Token rule context must not be null");
			if (!stream.hasMoreTokens()) {
				return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
			}
			
			Token currentToken = stream.getCurrentToken();
			TokenStream copyStream = stream.copyWithOffset(0);
			copyStream.advance();
			if (!copyStream.hasMoreTokens()) {
				if (currentToken.value().contains("\n")) {
					return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
				}
				return null;
			}
			
			Token nextToken = copyStream.getCurrentToken();
			if (currentToken.position().isPositioned() && nextToken.position().isPositioned()) {
				if (currentToken.position().line() < nextToken.position().line()) {
					return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
				}
			}
			
			if (currentToken.value().contains("\n")) {
				return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
			}
			return null;
		}
	};
	
	@Override
	public @NotNull TokenRule not() {
		return new TokenRule() {
			private final EndTokenRule self = EndTokenRule.this;
			
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
