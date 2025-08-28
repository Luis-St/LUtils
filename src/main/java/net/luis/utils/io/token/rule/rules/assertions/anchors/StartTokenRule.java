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

package net.luis.utils.io.token.rule.rules.assertions.anchors;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A token rule that matches the start of the token list or start of line.<br>
 * This rule is useful to ensure matching begins at the very start of input or line.<br>
 * <p>
 *     The rule behavior depends on the {@link AnchorType}:
 * </p>
 * <ul>
 *   <li>{@link AnchorType#DOCUMENT} matches only when the start index is 0</li>
 *   <li>{@link AnchorType#LINE} matches when the start index is at the beginning of a line or at the start of the document</li>
 * </ul>
 * <p>
 *     <strong>Note</strong>: {@link AnchorType#LINE} might be inconsistent if the token list has been modified (e.g., tokens added or removed) after the initial parsing,
 *     as it relies on line numbers and newline characters to determine line starts.
 *     Ensure the token list is in a consistent state before applying this rule.
 * </p>
 *
 * @see AnchorType
 *
 * @author Luis-St
 *
 * @param anchorType The type of anchor to match
 */
public record StartTokenRule(
	@NotNull AnchorType anchorType
) implements TokenRule {
	
	/**
	 * Creates a start token rule with the specified anchor type.<br>
	 *
	 * @param anchorType The type of anchor to match
	 * @throws NullPointerException If the anchor type is null
	 */
	public StartTokenRule {
		Objects.requireNonNull(anchorType, "Anchor type must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		return switch (this.anchorType) {
			case DOCUMENT -> this.matchDocumentStart(stream);
			case LINE -> this.matchLineStart(stream);
		};
	}
	
	/**
	 * Matches the start of the entire document (token stream).<br>
	 *
	 * @param stream The token stream
	 * @return A match if at document start, null otherwise
	 * @throws NullPointerException If the token stream is null
	 */
	private @Nullable TokenRuleMatch matchDocumentStart(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		if (stream.getCurrentIndex() == 0) {
			return TokenRuleMatch.empty(0, this);
		}
		return null;
	}
	
	/**
	 * Matches the start of a line or the start of the document.<br>
	 * A line start is detected by comparing the line numbers of the current and previous tokens,
	 * or by checking if the previous token contains a newline character.<br>
	 *
	 * @param stream The token stream
	 * @return A match if at line start, null otherwise
	 * @throws NullPointerException If the token stream is null
	 */
	@SuppressWarnings("DuplicatedCode")
	private @Nullable TokenRuleMatch matchLineStart(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		if (stream.getCurrentIndex() == 0) {
			return TokenRuleMatch.empty(0, this);
		}
		
		Token currentToken = stream.getCurrentToken();
		TokenStream lookbehindStream = stream.lookbehindStream();
		if (!lookbehindStream.hasToken()) {
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
	
	@Override
	public @NotNull TokenRule not() {
		return new TokenRule() {
			private final StartTokenRule self = StartTokenRule.this;
			
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				TokenRuleMatch result = this.self.match(stream);
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
