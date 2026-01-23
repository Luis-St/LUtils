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

package net.luis.utils.io.codec.constraint.merged;

import net.luis.utils.io.codec.constraint_new.*;
import net.luis.utils.io.codec.constraint.config.CharacterConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

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
@FunctionalInterface
public interface CharacterConstraint<C> extends ApplicableConstraint<CharacterConstraintConfig, C>, ComparableConstraint<Character, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<CharacterConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Character value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Character value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Character> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Character> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Character> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C greaterThan(@NonNull Character value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull Character value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull Character value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull Character value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull Character min, @NonNull Character max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull Character min, @NonNull Character max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
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
	default @NonNull C letter() {
		return this.apply(CharacterConstraintConfig::withLetter);
	}
	
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
	default @NonNull C digit() {
		return this.apply(CharacterConstraintConfig::withDigit);
	}
	
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
	default @NonNull C alphanumeric() {
		return this.apply(CharacterConstraintConfig::withAlphanumeric);
	}
	
	/**
	 * Applies a whitespace constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are whitespace (as defined by {@link Character#isWhitespace(char)}).
	 * </p>
	 *
	 * @return A new type with the applied whitespace constraint
	 */
	default @NonNull C whitespace() {
		return this.apply(CharacterConstraintConfig::withWhitespace);
	}
	
	/**
	 * Applies a punctuation constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are punctuation marks.
	 * </p>
	 *
	 * @return A new type with the applied punctuation constraint
	 * @see #symbol()
	 */
	default @NonNull C punctuation() {
		return this.apply(CharacterConstraintConfig::withPunctuation);
	}
	
	/**
	 * Applies a symbol constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are symbols (mathematical, currency, etc.).
	 * </p>
	 *
	 * @return A new type with the applied symbol constraint
	 * @see #punctuation()
	 */
	default @NonNull C symbol() {
		return this.apply(CharacterConstraintConfig::withSymbol);
	}
	
	/**
	 * Applies a control character constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are control characters (as defined by {@link Character#isISOControl(char)}).
	 * </p>
	 *
	 * @return A new type with the applied control constraint
	 */
	default @NonNull C control() {
		return this.apply(CharacterConstraintConfig::withControl);
	}
	
	/**
	 * Applies an upper case constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are upper case letters (as defined by {@link Character#isUpperCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied upper case constraint
	 * @see #lowerCase()
	 */
	default @NonNull C upperCase() {
		return this.apply(CharacterConstraintConfig::withUpperCase);
	}
	
	/**
	 * Applies a lower case constraint to the character.<br>
	 * <p>
	 *     The returned type will validate that characters are lower case letters (as defined by {@link Character#isLowerCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied lower case constraint
	 * @see #upperCase()
	 */
	default @NonNull C lowerCase() {
		return this.apply(CharacterConstraintConfig::withLowerCase);
	}
	
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
