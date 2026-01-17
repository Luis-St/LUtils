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

import net.luis.utils.io.codec.constraint_new.config.StringConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Constraint interface for string types that provides string-specific validation operations.<br>
 * <p>
 *     This interface extends {@link CharSequenceConstraint} with additional methods for case-insensitive
 *     comparisons, whitespace handling, and character content validation.<br>
 *     It provides methods for validating string content such as numeric, alphabetic, and alphanumeric strings.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface StringConstraint<C> extends ApplicableConstraint<StringConstraintConfig, C>, CharSequenceConstraint<String, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<StringConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull String value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull String value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<String> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<String> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	/**
	 * Applies a case-insensitive equality constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are equal to the specified value, ignoring case.
	 * </p>
	 *
	 * @param value The value to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #notEqualToIgnoreCase(String)
	 * @see #equalTo(String)
	 */
	default @NonNull C equalToIgnoreCase(@NonNull String value) {
		return this.apply(config -> config.withEqualToIgnoreCase(value));
	}
	
	/**
	 * Applies a case-insensitive non-equality constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are not equal to the specified value, ignoring case.
	 * </p>
	 *
	 * @param value The value to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive non-equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #equalToIgnoreCase(String)
	 * @see #notEqualTo(String)
	 */
	default @NonNull C notEqualToIgnoreCase(@NonNull String value) {
		return this.apply(config -> config.withNotEqualToIgnoreCase(value));
	}
	
	/**
	 * Applies a case-insensitive inclusion constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings match any value in the collection, ignoring case.
	 * </p>
	 *
	 * @param values The collection of values to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive inclusion constraint
	 * @throws NullPointerException If the collection is null
	 * @see #notInIgnoreCase(Collection)
	 * @see #in(Collection)
	 */
	default @NonNull C inIgnoreCase(@NonNull Collection<String> values) {
		return this.apply(config -> config.withInIgnoreCase(values));
	}
	
	/**
	 * Applies a case-insensitive exclusion constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings do not match any value in the collection, ignoring case.
	 * </p>
	 *
	 * @param values The collection of values to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive exclusion constraint
	 * @throws NullPointerException If the collection is null
	 * @see #inIgnoreCase(Collection)
	 * @see #notIn(Collection)
	 */
	default @NonNull C notInIgnoreCase(@NonNull Collection<String> values) {
		return this.apply(config -> config.withNotInIgnoreCase(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<String> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C minLength(int minLength) {
		return this.apply(config -> config.withMinLength(minLength));
	}
	
	@Override
	default @NonNull C maxLength(int maxLength) {
		return this.apply(config -> config.withMaxLength(maxLength));
	}
	
	@Override
	default @NonNull C exactLength(int exactLength) {
		return this.apply(config -> config.withExactLength(exactLength));
	}
	
	@Override
	default @NonNull C lengthBetween(int minLength, int maxLength) {
		return this.apply(config -> config.withLengthBetween(minLength, maxLength));
	}
	
	@Override
	default @NonNull C startsWith(@NonNull String prefix) {
		return this.apply(config -> config.withStartsWith(prefix));
	}
	
	@Override
	default @NonNull C notStartsWith(@NonNull String prefix) {
		return this.apply(config -> config.withNotStartsWith(prefix));
	}
	
	@Override
	default @NonNull C startsWithAny(@NonNull Collection<String> prefixes) {
		return this.apply(config -> config.withStartsWithAny(prefixes));
	}
	
	@Override
	default @NonNull C startsWithNone(@NonNull Collection<String> prefixes) {
		return this.apply(config -> config.withStartsWithNone(prefixes));
	}
	
	@Override
	default @NonNull C contains(@NonNull String substring) {
		return this.apply(config -> config.withContains(substring));
	}
	
	@Override
	default @NonNull C notContains(@NonNull String substring) {
		return this.apply(config -> config.withNotContains(substring));
	}
	
	@Override
	default @NonNull C containsAny(@NonNull Collection<String> substrings) {
		return this.apply(config -> config.withContainsAny(substrings));
	}
	
	@Override
	default @NonNull C containsNone(@NonNull Collection<String> substrings) {
		return this.apply(config -> config.withContainsNone(substrings));
	}
	
	@Override
	default @NonNull C containsAll(@NonNull Collection<String> substrings) {
		return this.apply(config -> config.withContainsAll(substrings));
	}
	
	@Override
	default @NonNull C containsOnly(@NonNull Collection<String> substrings) {
		return this.apply(config -> config.withContainsOnly(substrings));
	}
	
	@Override
	default @NonNull C endsWith(@NonNull String suffix) {
		return this.apply(config -> config.withEndsWith(suffix));
	}
	
	@Override
	default @NonNull C notEndsWith(@NonNull String suffix) {
		return this.apply(config -> config.withNotEndsWith(suffix));
	}
	
	@Override
	default @NonNull C endsWithAny(@NonNull Collection<String> suffixes) {
		return this.apply(config -> config.withEndsWithAny(suffixes));
	}
	
	@Override
	default @NonNull C endsWithNone(@NonNull Collection<String> suffixes) {
		return this.apply(config -> config.withEndsWithNone(suffixes));
	}
	
	@Override
	default @NonNull C matches(@NonNull String regex) {
		return this.apply(config -> config.withMatches(regex));
	}
	
	@Override
	default @NonNull C notMatches(@NonNull String regex) {
		return this.apply(config -> config.withNotMatches(regex));
	}
	
	@Override
	default @NonNull C matches(@NonNull Pattern pattern) {
		return this.apply(config -> config.withMatches(pattern));
	}
	
	@Override
	default @NonNull C notMatches(@NonNull Pattern pattern) {
		return this.apply(config -> config.withNotMatches(pattern));
	}
	
	/**
	 * Applies an upper case constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that characters are upper case letters (as defined by {@link Character#isUpperCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied upper case constraint
	 * @see #lowerCase()
	 */
	default @NonNull C upperCase() {
		return this.apply(StringConstraintConfig::withUpperCase);
	}
	
	/**
	 * Applies a lower case constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that characters are lower case letters (as defined by {@link Character#isLowerCase(char)}).
	 * </p>
	 *
	 * @return A new type with the applied lower case constraint
	 * @see #upperCase()
	 */
	default @NonNull C lowerCase() {
		return this.apply(StringConstraintConfig::withLowerCase);
	}
	
	/**
	 * Applies a trimmed constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings have no leading or trailing whitespace.<br>
	 *     A string passes if it equals its trimmed version.
	 * </p>
	 *
	 * @return A new type with the applied trimmed constraint
	 * @see #blank()
	 * @see #notBlank()
	 */
	default @NonNull C trimmed() {
		return this.apply(StringConstraintConfig::withTrimmed);
	}
	
	/**
	 * Applies a blank constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are blank (empty or contain only whitespace).
	 * </p>
	 *
	 * @return A new type with the applied blank constraint
	 * @see #notBlank()
	 * @see #empty()
	 */
	default @NonNull C blank() {
		return this.apply(StringConstraintConfig::withBlank);
	}
	
	/**
	 * Applies a non-blank constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are not blank (contain at least one non-whitespace character).
	 * </p>
	 *
	 * @return A new type with the applied non-blank constraint
	 * @see #blank()
	 * @see #notEmpty()
	 */
	default @NonNull C notBlank() {
		return this.apply(StringConstraintConfig::withNotBlank);
	}
	
	/**
	 * Applies a numeric constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only numeric digits (0-9).
	 * </p>
	 *
	 * @return A new type with the applied numeric constraint
	 * @see #alphabetic()
	 * @see #alphanumeric()
	 */
	default @NonNull C numeric() {
		return this.apply(StringConstraintConfig::withNumeric);
	}
	
	/**
	 * Applies an alphabetic constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only alphabetic letters.
	 * </p>
	 *
	 * @return A new type with the applied alphabetic constraint
	 * @see #numeric()
	 * @see #alphanumeric()
	 */
	default @NonNull C alphabetic() {
		return this.apply(StringConstraintConfig::withAlphabetic);
	}
	
	/**
	 * Applies an alphanumeric constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only letters and digits.
	 * </p>
	 *
	 * @return A new type with the applied alphanumeric constraint
	 * @see #alphabetic()
	 * @see #numeric()
	 */
	default @NonNull C alphanumeric() {
		return this.apply(StringConstraintConfig::withAlphanumeric);
	}
	
	/**
	 * Applies an ASCII constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only ASCII characters (U+0000 to U+007F).
	 * </p>
	 *
	 * @return A new type with the applied ASCII constraint
	 * @see #latin1()
	 */
	default @NonNull C ascii() {
		return this.apply(StringConstraintConfig::withAscii);
	}
	
	/**
	 * Applies a Latin-1 constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only Latin-1 characters (U+0000 to U+00FF).
	 * </p>
	 *
	 * @return A new type with the applied Latin-1 constraint
	 * @see #ascii()
	 */
	default @NonNull C latin1() {
		return this.apply(StringConstraintConfig::withLatin1);
	}
}
