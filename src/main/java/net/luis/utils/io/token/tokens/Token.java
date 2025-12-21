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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.type.TokenType;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * Interface for a token, a token is a sequence of characters.<br>
 *
 * @author Luis-St
 */
public interface Token {
	
	/**
	 * Returns the value of the token.<br>
	 * @return The value
	 */
	@NonNull String value();
	
	/**
	 * Returns the start position of the token.<br>
	 * The start position is the first character of the token.<br>
	 *
	 * @return The start position
	 */
	@NonNull TokenPosition position();
	
	/**
	 * Returns the types of the token.<br>
	 * <p>
	 *     This set is by default empty and mutable.<br>
	 *     Types can be added directly to the token by getting the set and adding the type to it:
	 * </p>
	 * <pre>{@code
	 *     token.types().add(myType);
	 *}</pre>
	 *
	 * @return The set of token types
	 */
	@NonNull Set<TokenType> types();
	
	/**
	 * Creates an indexed token that wraps this token with the given index.<br>
	 *
	 * @param index The index to associate with the token
	 * @return The indexed token
	 * @throws IllegalArgumentException If the index is negative
	 * @apiNote Calling this method is equivalent to {@code new IndexedToken(this, index)} but is more readable.<br>
	 * If you call this method on an already indexed token, it will create a new indexed token only if the index is different.
	 * @see IndexedToken#IndexedToken(Token, int)
	 */
	default @NonNull Token index(int index) {
		return new IndexedToken(this, index);
	}
	
	/**
	 * Creates an annotated token that wraps this token with the given annotations.<br>
	 * The annotations can be used to store additional information about the token during processing.<br>
	 *
	 * @param annotations The annotations to associate with the token
	 * @return The annotated token
	 * @throws NullPointerException If the annotations map is null
	 * @apiNote Calling this method is equivalent to {@code new AnnotatedToken(this, annotations)} but is more readable.<br>
	 * If you call this method on an already annotated token, it will return itself.
	 * @see AnnotatedToken#AnnotatedToken(Token, Map)
	 */
	default @NonNull Token annotate(@NonNull Map<String, Object> annotations) {
		return new AnnotatedToken(this, annotations);
	}
	
	/**
	 * Creates a shadow token that wraps this token.<br>
	 * A shadow token is a token that does not affect the parsing process.<br>
	 * It is used to mark tokens that should be ignored during parsing but still need to be retained for other purposes (e.g., comments, whitespace).<br>
	 * Shadow tokens are typically skipped by parsers and do not contribute to the syntactic structure of the input.<br>
	 *
	 * @return The shadow token
	 * @apiNote Calling this method is equivalent to {@code new ShadowToken(this)} but is more readable.<br>
	 * If you call this method on an already shadow token, it will return itself.
	 * @see ShadowToken#ShadowToken(Token)
	 */
	default @NonNull Token shadow() {
		return new ShadowToken(this);
	}
	
	/**
	 * If this token is a shadow token, it returns the underlying token that it wraps.<br>
	 * If this token is not a shadow token, it returns itself.<br>
	 * This method is useful for retrieving the original token when dealing with shadow tokens.<br>
	 *
	 * @return The unshadowed token or itself if not a shadow token
	 * @apiNote Calling this method on a non-shadow token will return itself.<br>
	 * If you call this method on an already unshadowed token, it will return itself.
	 * @see ShadowToken#token()
	 */
	default @NonNull Token unshadow() {
		return this;
	}
}
