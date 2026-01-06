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

import java.util.Collection;

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
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface StringConstraint<T, C> extends CharSequenceConstraint<T, C> {

	/**
	 * Applies a case-insensitive equality constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are equal to the specified value, ignoring case.
	 * </p>
	 *
	 * @param value The value to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #notEqualToIgnoreCase(Object)
	 * @see #equalTo(Object)
	 */
	@NonNull C equalToIgnoreCase(@NonNull T value);

	/**
	 * Applies a case-insensitive non-equality constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings are not equal to the specified value, ignoring case.
	 * </p>
	 *
	 * @param value The value to compare against (case-insensitive)
	 * @return A new type with the applied case-insensitive non-equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #equalToIgnoreCase(Object)
	 * @see #notEqualTo(Object)
	 */
	@NonNull C notEqualToIgnoreCase(@NonNull T value);

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
	@NonNull C inIgnoreCase(@NonNull Collection<T> values);

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
	@NonNull C notInIgnoreCase(@NonNull Collection<T> values);

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
	@NonNull C trimmed();

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
	@NonNull C blank();

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
	@NonNull C notBlank();

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
	@NonNull C numeric();

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
	@NonNull C alphabetic();

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
	@NonNull C alphanumeric();

	/**
	 * Applies an ASCII constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only ASCII characters (U+0000 to U+007F).
	 * </p>
	 *
	 * @return A new type with the applied ASCII constraint
	 * @see #latin1()
	 */
	@NonNull C ascii();

	/**
	 * Applies a Latin-1 constraint to the string.<br>
	 * <p>
	 *     The returned type will validate that strings contain only Latin-1 characters (U+0000 to U+00FF).
	 * </p>
	 *
	 * @return A new type with the applied Latin-1 constraint
	 * @see #ascii()
	 */
	@NonNull C latin1();
}
