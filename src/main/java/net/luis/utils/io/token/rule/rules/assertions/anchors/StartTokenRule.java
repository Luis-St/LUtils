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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
public record StartTokenRule(@NotNull AnchorType anchorType) implements TokenRule {
	
	/**
	 * Creates a start token rule with the specified anchor type.<br>
	 *
	 * @param anchorType The type of anchor to match
	 * @throws NullPointerException If the anchor type is null
	 */
	public StartTokenRule(@NotNull AnchorType anchorType) {
		this.anchorType = Objects.requireNonNull(anchorType, "Anchor type must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		return switch (this.anchorType) {
			case DOCUMENT -> this.matchDocumentStart(tokens, startIndex);
			case LINE -> this.matchLineStart(tokens, startIndex);
		};
	}
	
	/**
	 * Matches the start of the entire document (token list).<br>
	 *
	 * @param tokens The list of tokens
	 * @param startIndex The starting index
	 * @return A match if at document start, null otherwise
	 */
	private @Nullable TokenRuleMatch matchDocumentStart(@NotNull List<Token> tokens, int startIndex) {
		if (startIndex == 0) {
			return TokenRuleMatch.empty(startIndex);
		}
		return null;
	}
	
	/**
	 * Matches the start of a line or the start of the document.<br>
	 * A line start is detected by comparing the line numbers of the current and previous tokens,
	 * or by checking if the previous token contains a newline character.<br>
	 *
	 * @param tokens The list of tokens
	 * @param startIndex The starting index
	 * @return A match if at line start, null otherwise
	 */
	private @Nullable TokenRuleMatch matchLineStart(@NotNull List<Token> tokens, int startIndex) {
		if (startIndex == 0) {
			return TokenRuleMatch.empty(startIndex);
		}
		
		if (startIndex > 0 && startIndex <= tokens.size()) {
			Token currentToken = tokens.get(startIndex);
			Token previousToken = tokens.get(startIndex - 1);
			
			if (currentToken.endPosition().isPositioned() && previousToken.startPosition().isPositioned()) {
				if (currentToken.endPosition().line() < previousToken.startPosition().line()) {
					return TokenRuleMatch.empty(startIndex);
				}
			}
			
			if (previousToken.value().contains("\n")) {
				return TokenRuleMatch.empty(startIndex);
			}
		}
		
		return null;
	}
}
