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

package net.luis.utils.io.codec.constraint_new;

import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for character types that provides character classification validation operations.<br>
 * <p>
 *     This interface extends {@link ComparableConstraint} with methods for constraining characters based on
 *     their Unicode category such as letters, digits, whitespace, and more.<br>
 *     It also provides convenience methods for ASCII and Latin-1 character range validation.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface CharacterConstraint<C> extends ComparableConstraint<Character, C> {
	
	/**
	 * Applies a letter constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are letters (as defined by {@link Character#isLetter(char)}).
	 * </p>
	 *
	 * @return A new type with the applied letter constraint
	 * @see #digit()
	 * @see #alphanumeric()
	 */
	@NonNull C letter();
	
	/**
	 * Applies a digit constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are digits (as defined by {@link Character#isDigit(char)}).
	 * </p>
	 *
	 * @return A new type with the applied digit constraint
	 * @see #letter()
	 * @see #alphanumeric()
	 */
	@NonNull C digit();
	
	/**
	 * Applies an alphanumeric constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are either letters or digits.
	 * </p>
	 *
	 * @return A new type with the applied alphanumeric constraint
	 * @see #letter()
	 * @see #digit()
	 */
	@NonNull C alphanumeric();
	
	/**
	 * Applies a whitespace constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are whitespace (as defined by {@link Character#isWhitespace(char)}).
	 * </p>
	 *
	 * @return A new type with the applied whitespace constraint
	 */
	@NonNull C whitespace();
	
	/**
	 * Applies a punctuation constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are punctuation marks.
	 * </p>
	 *
	 * @return A new type with the applied punctuation constraint
	 * @see #symbol()
	 */
	@NonNull C punctuation();
	
	/**
	 * Applies a symbol constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are symbols (mathematical, currency, etc.).
	 * </p>
	 *
	 * @return A new type with the applied symbol constraint
	 * @see #punctuation()
	 */
	@NonNull C symbol();
	
	/**
	 * Applies a control character constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are control characters (as defined by {@link Character#isISOControl(char)}).
	 * </p>
	 *
	 * @return A new type with the applied control constraint
	 */
	@NonNull C control();
	
	/**
	 * Applies an upper case constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are upper case letters (as defined by {@link Character#isUpperCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied upper case constraint
	 * @see #lowerCase()
	 */
	@NonNull C upperCase();
	
	/**
	 * Applies a lower case constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are lower case letters (as defined by {@link Character#isLowerCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied lower case constraint
	 * @see #upperCase()
	 */
	@NonNull C lowerCase();
	
	/**
	 * Applies an ASCII character constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are within the ASCII range (U+0000 to U+007F).<br>
	 *     This is a convenience method equivalent to {@code betweenOrEqual('\u0000', '\u007F')}.
	 * </p>
	 *
	 * @return A new type with the applied ASCII constraint
	 * @see #latin1()
	 */
	default @NonNull C ascii() {
		return this.betweenOrEqual('\u0000', '\u007F');
	}
	
	/**
	 * Applies a Latin-1 character constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are within the Latin-1 range (U+0000 to U+00FF).<br>
	 *     This is a convenience method equivalent to {@code betweenOrEqual('\u0000', '\u00FF')}.
	 * </p>
	 *
	 * @return A new type with the applied Latin-1 constraint
	 * @see #ascii()
	 */
	default @NonNull C latin1() {
		return this.betweenOrEqual('\u0000', '\u00FF');
	}
}
