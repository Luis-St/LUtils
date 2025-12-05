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

package net.luis.utils.io.codec.constraint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.regex.Pattern;

/**
 * A constraint interface for length-based validation of strings, arrays, and other sequential types.<br>
 * Provides methods to validate the length of strings and arrays.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 */
public interface LengthConstraint<T> {
	
	/**
	 * Gets the length of the given value.<br>
	 * Implementations should handle various types appropriately.<br>
	 *
	 * @param value The value to get the length of
	 * @return The length of the value, or -1 if the value is null or unsupported
	 */
	int getLength(@Nullable T value);
	
	/**
	 * Checks if the value has at least the specified minimum length.<br>
	 *
	 * @param value The value to check
	 * @param minLength The minimum allowed length (inclusive)
	 * @return {@code true} if the value has at least the minimum length, {@code false} otherwise
	 */
	default boolean hasMinLength(@Nullable T value, int minLength) {
		int length = getLength(value);
		return length >= 0 && length >= minLength;
	}
	
	/**
	 * Checks if the value has at most the specified maximum length.<br>
	 *
	 * @param value The value to check
	 * @param maxLength The maximum allowed length (inclusive)
	 * @return {@code true} if the value has at most the maximum length, {@code false} otherwise
	 */
	default boolean hasMaxLength(@Nullable T value, int maxLength) {
		int length = getLength(value);
		return length >= 0 && length <= maxLength;
	}
	
	/**
	 * Checks if the value has exactly the specified length.<br>
	 *
	 * @param value The value to check
	 * @param exactLength The exact required length
	 * @return {@code true} if the value has exactly the specified length, {@code false} otherwise
	 */
	default boolean hasExactLength(@Nullable T value, int exactLength) {
		int length = getLength(value);
		return length == exactLength;
	}
	
	/**
	 * Checks if the value's length is within the specified range (inclusive).<br>
	 *
	 * @param value The value to check
	 * @param minLength The minimum allowed length (inclusive)
	 * @param maxLength The maximum allowed length (inclusive)
	 * @return {@code true} if the value's length is within the range, {@code false} otherwise
	 */
	default boolean hasLengthInRange(@Nullable T value, int minLength, int maxLength) {
		int length = getLength(value);
		return length >= 0 && length >= minLength && length <= maxLength;
	}
	
	/**
	 * Checks if the value is empty (has zero length).<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is empty, {@code false} otherwise
	 */
	default boolean isEmpty(@Nullable T value) {
		return getLength(value) == 0;
	}
	
	/**
	 * Checks if the value is not empty (has at least one character/element).<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is not empty, {@code false} otherwise
	 */
	default boolean isNotEmpty(@Nullable T value) {
		int length = getLength(value);
		return length > 0;
	}
	
	/**
	 * Default implementation for strings.<br>
	 */
	interface ForString extends LengthConstraint<String> {
		
		@Override
		default int getLength(@Nullable String value) {
			return value == null ? -1 : value.length();
		}
		
		/**
		 * Checks if the string is blank (empty or contains only whitespace).<br>
		 *
		 * @param value The string to check
		 * @return {@code true} if the string is blank, {@code false} otherwise
		 */
		default boolean isBlank(@Nullable String value) {
			return value == null || value.isBlank();
		}
		
		/**
		 * Checks if the string is not blank (contains at least one non-whitespace character).<br>
		 *
		 * @param value The string to check
		 * @return {@code true} if the string is not blank, {@code false} otherwise
		 */
		default boolean isNotBlank(@Nullable String value) {
			return value != null && !value.isBlank();
		}
		
		/**
		 * Checks if the string matches the specified regular expression pattern.<br>
		 *
		 * @param value The string to check
		 * @param pattern The regular expression pattern to match against
		 * @return {@code true} if the string matches the pattern, {@code false} otherwise
		 */
		default boolean matches(@Nullable String value, @NotNull String pattern) {
			return value != null && value.matches(pattern);
		}
		
		/**
		 * Checks if the string matches the specified compiled pattern.<br>
		 *
		 * @param value The string to check
		 * @param pattern The compiled pattern to match against
		 * @return {@code true} if the string matches the pattern, {@code false} otherwise
		 */
		default boolean matches(@Nullable String value, @NotNull Pattern pattern) {
			return value != null && pattern.matcher(value).matches();
		}
		
		/**
		 * Checks if the string starts with the specified prefix.<br>
		 *
		 * @param value The string to check
		 * @param prefix The prefix to check for
		 * @return {@code true} if the string starts with the prefix, {@code false} otherwise
		 */
		default boolean startsWith(@Nullable String value, @NotNull String prefix) {
			return value != null && value.startsWith(prefix);
		}
		
		/**
		 * Checks if the string ends with the specified suffix.<br>
		 *
		 * @param value The string to check
		 * @param suffix The suffix to check for
		 * @return {@code true} if the string ends with the suffix, {@code false} otherwise
		 */
		default boolean endsWith(@Nullable String value, @NotNull String suffix) {
			return value != null && value.endsWith(suffix);
		}
		
		/**
		 * Checks if the string contains the specified substring.<br>
		 *
		 * @param value The string to check
		 * @param substring The substring to check for
		 * @return {@code true} if the string contains the substring, {@code false} otherwise
		 */
		default boolean contains(@Nullable String value, @NotNull String substring) {
			return value != null && value.contains(substring);
		}
	}
	
	/**
	 * Default implementation for arrays.<br>
	 *
	 * @param <E> The element type of the array
	 */
	interface ForArray<E> extends LengthConstraint<E[]> {
		
		@Override
		default int getLength(@Nullable E[] value) {
			return value == null ? -1 : value.length;
		}
	}
	
	/**
	 * Default implementation for primitive arrays.<br>
	 */
	interface ForPrimitiveArray extends LengthConstraint<Object> {
		
		@Override
		default int getLength(@Nullable Object value) {
			if (value == null || !value.getClass().isArray()) {
				return -1;
			}
			return Array.getLength(value);
		}
	}
	
}
