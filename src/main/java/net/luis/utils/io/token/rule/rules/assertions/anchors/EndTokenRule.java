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
 * A token rule that matches the end of the token list or end of line.<br>
 * This rule is useful to ensure that the end of the token list or line is reached.<br>
 * <p>
 *     The rule behavior depends on the {@link AnchorType}:
 * </p>
 * <ul>
 *   <li>{@link AnchorType#DOCUMENT} matches when the start index is larger than or equal to the size of the token list</li>
 *   <li>{@link AnchorType#LINE} matches when the start index is at the end of a line or at the end of the document</li>
 * </ul>
 * <p>
 *     <strong>Note</strong>: {@link AnchorType#LINE} might be inconsistent if the token list has been modified (e.g., tokens added or removed) after the initial parsing,
 *     as it relies on line numbers and newline characters to determine line starts.
 *     Ensure the token list is in a consistent state before applying this rule.
 * </p>
 *
 * @param anchorType The type of anchor to match
 * @author Luis-St
 */
public record EndTokenRule(
	@NotNull AnchorType anchorType
) implements TokenRule {
	
	/**
	 * Creates an end token rule with the specified anchor type.<br>
	 *
	 * @param anchorType The type of anchor to match
	 * @throws NullPointerException If the anchor type is null
	 */
	public EndTokenRule {
		Objects.requireNonNull(anchorType, "Anchor type must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		return switch (this.anchorType) {
			case DOCUMENT -> this.matchDocumentEnd(stream);
			case LINE -> this.matchLineEnd(stream);
		};
	}
	
	/**
	 * Matches the end of the entire document (token list).<br>
	 *
	 * @param stream The token stream
	 * @return A match if at document end, null otherwise
	 * @throws NullPointerException If the token stream is null
	 */
	private @Nullable TokenRuleMatch matchDocumentEnd(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		if (!stream.hasToken()) {
			return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
		}
		return null;
	}
	
	/**
	 * Matches the end of a line or the end of the document.<br>
	 * A line end is detected by comparing the line numbers of the current and next tokens,
	 * or by checking if the current token contains a newline character.<br>
	 *
	 * @param stream The token stream
	 * @return A match if at line end, null otherwise
	 * @throws NullPointerException If the token stream is null
	 */
	@SuppressWarnings("DuplicatedCode")
	private @Nullable TokenRuleMatch matchLineEnd(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		if (!stream.hasToken()) {
			return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
		}
		
		Token currentToken = stream.getCurrentToken();
		TokenStream copyStream = stream.copyWithCurrentIndex();
		copyStream.consumeToken();
		
		if (!copyStream.hasToken()) {
			return null;
		}
		
		Token nextToken = copyStream.readToken();
		
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
	
	@Override
	public @NotNull TokenRule not() {
		return new TokenRule() {
			private final EndTokenRule self = EndTokenRule.this;
			
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
				return this.self; // Inverting the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
}
