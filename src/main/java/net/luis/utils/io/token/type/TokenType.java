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

package net.luis.utils.io.token.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */
public sealed interface TokenType permits StandardTokenType, CustomTokenType {
	
	@NotNull String getName();
	
	@Nullable TokenType getSuperType();
	
	default @NotNull TokenType getBaseType() {
		TokenType current = this;
		
		while (current.getSuperType() != null) {
			current = current.getSuperType();
		}
		return current;
	}
	
	default boolean isInstanceOf(@NotNull TokenType type) {
		Objects.requireNonNull(type, "Type must not be null");
		
		TokenType current = this;
		while (current != null) {
			if (current.equals(type)) {
				return true;
			}
			
			current = current.getSuperType();
		}
		return false;
	}
	
	default TokenType @NotNull [] getHierarchy() {
		int depth = 1;
		TokenType current = this;
		while (current.getSuperType() != null) {
			depth++;
			current = current.getSuperType();
		}
		
		TokenType[] hierarchy = new TokenType[depth];
		current = this;
		for (int i = depth - 1; i >= 0; i--) {
			hierarchy[i] = current;
			current = Objects.requireNonNull(current).getSuperType();
		}
		return hierarchy;
	}
	
	default @NotNull String getHierarchyPath() {
		StringBuilder builder = new StringBuilder();
		
		for (TokenType type : this.getHierarchy()) {
			if (!builder.isEmpty()) {
				builder.append("/");
			}
			builder.append(type.getName());
		}
		return builder.toString();
	}
	
	default boolean isSyntaxToken() {
		return this == StandardTokenType.SYNTAX || this.isInstanceOf(StandardTokenType.SYNTAX);
	}
	
	default boolean isIdentifierToken() {
		return this == StandardTokenType.IDENTIFIER || this.isInstanceOf(StandardTokenType.IDENTIFIER);
	}
	
	default boolean isLiteralToken() {
		return this == StandardTokenType.LITERAL || this.isInstanceOf(StandardTokenType.LITERAL);
	}
	
	default boolean isOperatorToken() {
		return this == StandardTokenType.OPERATOR || this.isInstanceOf(StandardTokenType.OPERATOR);
	}
	
	default boolean isDelimiterToken() {
		return this == StandardTokenType.DELIMITER || this.isInstanceOf(StandardTokenType.DELIMITER);
	}
	
	default boolean isWhitespaceToken() {
		return this == StandardTokenType.WHITESPACE || this.isInstanceOf(StandardTokenType.WHITESPACE);
	}
	
	default boolean isCommentToken() {
		return this == StandardTokenType.COMMENT || this.isInstanceOf(StandardTokenType.COMMENT);
	}
	
	default boolean isSpecialToken() {
		return this == StandardTokenType.SPECIAL || this.isInstanceOf(StandardTokenType.SPECIAL);
	}
}
