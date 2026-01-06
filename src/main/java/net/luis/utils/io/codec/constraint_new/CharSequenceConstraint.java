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
import java.util.regex.Pattern;

/**
 * Constraint interface for character sequence types that provides string pattern matching operations.<br>
 * <p>
 *     This interface extends {@link LengthConstraint} with methods for constraining character sequences
 *     based on prefix, suffix, substring containment, and regular expression matching.<br>
 *     It is suitable for string-like types that support character sequence operations.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface CharSequenceConstraint<T, C> extends LengthConstraint<T, C> {
	
	/**
	 * Applies a prefix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values start with the specified prefix.
	 * </p>
	 *
	 * @param prefix The prefix that values must start with
	 * @return A new type with the applied prefix constraint
	 * @throws NullPointerException If the prefix is null
	 * @see #notStartsWith(Object)
	 * @see #startsWithAny(Collection)
	 */
	@NonNull C startsWith(@NonNull T prefix);
	
	/**
	 * Applies a negative prefix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not start with the specified prefix.
	 * </p>
	 *
	 * @param prefix The prefix that values must not start with
	 * @return A new type with the applied negative prefix constraint
	 * @throws NullPointerException If the prefix is null
	 * @see #startsWith(Object)
	 * @see #startsWithNone(Collection)
	 */
	@NonNull C notStartsWith(@NonNull T prefix);
	
	/**
	 * Applies a multi-prefix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values start with any of the specified prefixes.
	 * </p>
	 *
	 * @param prefixes The collection of prefixes, one of which values must start with
	 * @return A new type with the applied multi-prefix constraint
	 * @throws NullPointerException If the collection is null
	 * @see #startsWith(Object)
	 * @see #startsWithNone(Collection)
	 */
	@NonNull C startsWithAny(@NonNull Collection<T> prefixes);
	
	/**
	 * Applies a negative multi-prefix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not start with any of the specified prefixes.
	 * </p>
	 *
	 * @param prefixes The collection of prefixes that values must not start with
	 * @return A new type with the applied negative multi-prefix constraint
	 * @throws NullPointerException If the collection is null
	 * @see #notStartsWith(Object)
	 * @see #startsWithAny(Collection)
	 */
	@NonNull C startsWithNone(@NonNull Collection<T> prefixes);
	
	/**
	 * Applies a substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain the specified substring.
	 * </p>
	 *
	 * @param substring The substring that values must contain
	 * @return A new type with the applied containment constraint
	 * @throws NullPointerException If the substring is null
	 * @see #notContains(Object)
	 * @see #containsAny(Collection)
	 * @see #containsNone(Collection)
	 * @see #containsAll(Collection)
	 * @see #containsOnly(Collection)
	 */
	@NonNull C contains(@NonNull T substring);
	
	/**
	 * Applies a negative substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not contain the specified substring.
	 * </p>
	 *
	 * @param substring The substring that values must not contain
	 * @return A new type with the applied negative containment constraint
	 * @throws NullPointerException If the substring is null
	 * @see #contains(Object)
	 * @see #containsNone(Collection)
	 * @see #containsAny(Collection)
	 * @see #containsAll(Collection)
	 * @see #containsOnly(Collection)
	 */
	@NonNull C notContains(@NonNull T substring);
	
	/**
	 * Applies a multi-substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain any of the specified substrings.
	 * </p>
	 *
	 * @param substrings The collection of substrings, one of which values must contain
	 * @return A new type with the applied multi-containment constraint
	 * @throws NullPointerException If the collection is null
	 * @see #contains(Object)
	 * @see #notContains(Object)
	 * @see #containsNone(Collection)
	 * @see #containsAll(Collection)
	 * @see #containsOnly(Collection)
	 */
	@NonNull C containsAny(@NonNull Collection<T> substrings);
	
	/**
	 * Applies a negative multi-substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not contain any of the specified substrings.
	 * </p>
	 *
	 * @param substrings The collection of substrings that values must not contain
	 * @return A new type with the applied negative multi-containment constraint
	 * @throws NullPointerException If the collection is null
	 * @see #contains(Object)
	 * @see #notContains(Object)
	 * @see #containsAny(Collection)
	 * @see #containsAll(Collection)
	 * @see #containsOnly(Collection)
	 */
	@NonNull C containsNone(@NonNull Collection<T> substrings);
	
	/**
	 * Applies an all-substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain all of the specified substrings.
	 * </p>
	 *
	 * @param substrings The collection of substrings that values must contain
	 * @return A new type with the applied all-containment constraint
	 * @throws NullPointerException If the collection is null
	 * @see #contains(Object)
	 * @see #notContains(Object)
	 * @see #containsAny(Collection)
	 * @see #containsNone(Collection)
	 * @see #containsOnly(Collection)
	 */
	@NonNull C containsAll(@NonNull Collection<T> substrings);
	
	/**
	 * Applies an only-substring containment constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain only the specified substrings.
	 * </p>
	 *
	 * @param substrings The collection of substrings that values must contain exclusively
	 * @return A new type with the applied only-containment constraint
	 * @throws NullPointerException If the collection is null
	 * @see #contains(Object)
	 * @see #notContains(Object)
	 * @see #containsAny(Collection)
	 * @see #containsNone(Collection)
	 * @see #containsAll(Collection)
	 */
	@NonNull C containsOnly(@NonNull Collection<T> substrings);
	
	/**
	 * Applies a suffix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values end with the specified suffix.
	 * </p>
	 *
	 * @param suffix The suffix that values must end with
	 * @return A new type with the applied suffix constraint
	 * @throws NullPointerException If the suffix is null
	 * @see #notEndsWith(Object)
	 * @see #endsWithAny(Collection)
	 */
	@NonNull C endsWith(@NonNull T suffix);
	
	/**
	 * Applies a negative suffix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not end with the specified suffix.
	 * </p>
	 *
	 * @param suffix The suffix that values must not end with
	 * @return A new type with the applied negative suffix constraint
	 * @throws NullPointerException If the suffix is null
	 * @see #endsWith(Object)
	 * @see #endsWithNone(Collection)
	 */
	@NonNull C notEndsWith(@NonNull T suffix);
	
	/**
	 * Applies a multi-suffix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values end with any of the specified suffixes.
	 * </p>
	 *
	 * @param suffixes The collection of suffixes, one of which values must end with
	 * @return A new type with the applied multi-suffix constraint
	 * @throws NullPointerException If the collection is null
	 * @see #endsWith(Object)
	 * @see #endsWithNone(Collection)
	 */
	@NonNull C endsWithAny(@NonNull Collection<T> suffixes);
	
	/**
	 * Applies a negative multi-suffix constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not end with any of the specified suffixes.
	 * </p>
	 *
	 * @param suffixes The collection of suffixes that values must not end with
	 * @return A new type with the applied negative multi-suffix constraint
	 * @throws NullPointerException If the collection is null
	 * @see #notEndsWith(Object)
	 * @see #endsWithAny(Collection)
	 */
	@NonNull C endsWithNone(@NonNull Collection<T> suffixes);
	
	/**
	 * Applies a regular expression constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values match the specified regular expression.
	 * </p>
	 *
	 * @param regex The regular expression that values must match
	 * @return A new type with the applied regex constraint
	 * @throws NullPointerException If the regex is null
	 * @see #notMatches(String)
	 * @see #matches(Pattern)
	 */
	@NonNull C matches(@NonNull String regex);
	
	/**
	 * Applies a negative regular expression constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not match the specified regular expression.
	 * </p>
	 *
	 * @param regex The regular expression that values must not match
	 * @return A new type with the applied negative regex constraint
	 * @throws NullPointerException If the regex is null
	 * @see #matches(String)
	 * @see #notMatches(Pattern)
	 */
	@NonNull C notMatches(@NonNull String regex);
	
	/**
	 * Applies a compiled pattern constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values match the specified compiled pattern.<br>
	 *     This method is more efficient than {@link #matches(String)} when the same pattern is used multiple times.
	 * </p>
	 *
	 * @param pattern The compiled pattern that values must match
	 * @return A new type with the applied pattern constraint
	 * @throws NullPointerException If the pattern is null
	 * @see #notMatches(Pattern)
	 * @see #matches(String)
	 */
	@NonNull C matches(@NonNull Pattern pattern);
	
	/**
	 * Applies a negative compiled pattern constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values do not match the specified compiled pattern.<br>
	 *     This method is more efficient than {@link #notMatches(String)} when the same pattern is used multiple times.
	 * </p>
	 *
	 * @param pattern The compiled pattern that values must not match
	 * @return A new type with the applied negative pattern constraint
	 * @throws NullPointerException If the pattern is null
	 * @see #matches(Pattern)
	 * @see #notMatches(String)
	 */
	@NonNull C notMatches(@NonNull Pattern pattern);
}
