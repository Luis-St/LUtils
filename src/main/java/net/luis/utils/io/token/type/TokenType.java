/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Interface representing a type of token.<br>
 * A token type can be a standard type or a custom type.<br>
 * Standard types are predefined and cannot be modified, while custom types can be created by the user.<br>
 * <p>
 *     A token type can have a super type, which represents a more general category of tokens.<br>
 *     This allows for a hierarchical organization of token types, where specific types can inherit properties from their super types.<br>
 *     For example, a "Keyword" token type can have a super type of "Syntax", indicating that it is a specific kind of syntax token.<br>
 *     The hierarchy can be traversed to determine if a token type is a subtype of another type.
 * </p>
 *
 * @see StandardTokenType
 * @see CustomTokenType
 *
 * @author Luis-St
 *
 */
public sealed interface TokenType permits StandardTokenType, CustomTokenType {
	
	/**
	 * Gets the name of the token type.<br>
	 * @return The name of the type
	 */
	@NonNull String getName();
	
	/**
	 * Gets the super type of the token type.<br>
	 * @return The super type of the type, or null if it has no super type
	 */
	@Nullable TokenType getSuperType();
	
	/**
	 * Gets the base type of the token type.<br>
	 * The base type is the top-most super type in the hierarchy.<br>
	 * If the token type has no super type, it is its own base type.<br>
	 *
	 * @return The base type of the type
	 */
	default @NonNull TokenType getBaseType() {
		TokenType current = this;
		
		while (current.getSuperType() != null) {
			current = current.getSuperType();
		}
		return current;
	}
	
	/**
	 * Checks if the token type is an instance of the given type.<br>
	 * This method traverses the hierarchy of super types to determine if the token type is a subtype of the given type.<br>
	 * <p>
	 *     If the token type is the same as the given type, it is considered an instance of that type.<br>
	 *     If the token type has no super type, it is only an instance of itself.
	 * </p>
	 *
	 * @param type The type to check against
	 * @return True if the token type is an instance of the given type, false otherwise
	 * @throws NullPointerException If the given type is null
	 */
	default boolean isInstanceOf(@NonNull TokenType type) {
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
	
	/**
	 * Gets the hierarchy of the token type as an array.<br>
	 * The hierarchy is ordered from the base type to the token type itself.<br>
	 * <p>
	 *     For example, if the token type is {@link StandardTokenType#KEYWORD} and its super type is {@link StandardTokenType#SYNTAX},, the hierarchy will be ["Syntax", "Keyword"].<br>
	 *     If the token type has no super type, the hierarchy will only contain the token type itself.
	 * </p>
	 *
	 * @return An array containing the hierarchy of the type, from base type to the type itself
	 */
	default TokenType @NonNull [] getHierarchy() {
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
	
	/**
	 * Gets the hierarchy path of the token type as a string.<br>
	 * The hierarchy path is a string representation of the hierarchy, with each type name separated by a slash ("/").<br>
	 * <p>
	 *     For example, if the token type is {@link StandardTokenType#KEYWORD} and its super type is {@link StandardTokenType#SYNTAX}, the hierarchy path will be "Syntax/Keyword".<br>
	 *     If the token type has no super type, the hierarchy path will only contain the token type name.
	 * </p>
	 *
	 * @return A string representing the hierarchy path of the type
	 */
	default @NonNull String getHierarchyPath() {
		StringBuilder builder = new StringBuilder();
		
		for (TokenType type : this.getHierarchy()) {
			if (!builder.isEmpty()) {
				builder.append("/");
			}
			builder.append(type.getName());
		}
		return builder.toString();
	}
	
	/**
	 * Checks if the token type is a syntax token.<br>
	 * A syntax token is any token type that is a subtype of the {@link StandardTokenType#SYNTAX} standard token type.<br>
	 *
	 * @return True if the token type is a syntax token, false otherwise
	 */
	default boolean isSyntaxToken() {
		return this == StandardTokenType.SYNTAX || this.isInstanceOf(StandardTokenType.SYNTAX);
	}
	
	/**
	 * Checks if the token type is an identifier token.<br>
	 * An identifier token is any token type that is a subtype of the "Identifier" standard token type.<br>
	 *
	 * @return True if the token type is an identifier token, false otherwise
	 */
	default boolean isIdentifierToken() {
		return this == StandardTokenType.IDENTIFIER || this.isInstanceOf(StandardTokenType.IDENTIFIER);
	}
	
	/**
	 * Checks if the token type is a literal token.<br>
	 * A literal token is any token type that is a subtype of the {@link StandardTokenType#LITERAL} standard token type.<br>
	 *
	 * @return True if the token type is a literal token, false otherwise
	 */
	default boolean isLiteralToken() {
		return this == StandardTokenType.LITERAL || this.isInstanceOf(StandardTokenType.LITERAL);
	}
	
	/**
	 * Checks if the token type is an operator token.<br>
	 * An operator token is any token type that is a subtype of the {@link StandardTokenType#OPERATOR} standard token type.<br>
	 *
	 * @return True if the token type is an operator token, false otherwise
	 */
	default boolean isOperatorToken() {
		return this == StandardTokenType.OPERATOR || this.isInstanceOf(StandardTokenType.OPERATOR);
	}
	
	/**
	 * Checks if the token type is a delimiter token.<br>
	 * A delimiter token is any token type that is a subtype of the {@link StandardTokenType#DELIMITER} standard token type.<br>
	 *
	 * @return True if the token type is a delimiter token, false otherwise
	 */
	default boolean isDelimiterToken() {
		return this == StandardTokenType.DELIMITER || this.isInstanceOf(StandardTokenType.DELIMITER);
	}
	
	/**
	 * Checks if the token type is a whitespace token.<br>
	 * A whitespace token is any token type that is a subtype of the {@link StandardTokenType#WHITESPACE} standard token type.<br>
	 *
	 * @return True if the token type is a whitespace token, false otherwise
	 */
	default boolean isWhitespaceToken() {
		return this == StandardTokenType.WHITESPACE || this.isInstanceOf(StandardTokenType.WHITESPACE);
	}
	
	/**
	 * Checks if the token type is a comment token.<br>
	 * A comment token is any token type that is a subtype of the {@link StandardTokenType#COMMENT} standard token type.<br>
	 *
	 * @return True if the token type is a comment token, false otherwise
	 */
	default boolean isCommentToken() {
		return this == StandardTokenType.COMMENT || this.isInstanceOf(StandardTokenType.COMMENT);
	}
	
	/**
	 * Checks if the token type is a special token.<br>
	 * A special token is any token type that is a subtype of the {@link StandardTokenType#SPECIAL} standard token type.<br>
	 *
	 * @return True if the token type is a special token, false otherwise
	 */
	default boolean isSpecialToken() {
		return this == StandardTokenType.SPECIAL || this.isInstanceOf(StandardTokenType.SPECIAL);
	}
}
