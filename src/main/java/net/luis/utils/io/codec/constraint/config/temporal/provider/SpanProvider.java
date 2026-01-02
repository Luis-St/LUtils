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

package net.luis.utils.io.codec.constraint.config.temporal.provider;

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.function.Supplier;

/**
 * Functional interface that provides temporal arithmetic operations for span constraints.<br>
 * <p>
 *     This interface enables uniform handling of temporal types for duration-based constraints
 *     by providing access to current time, arithmetic operations, and comparisons.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type
 */
@FunctionalInterface
public interface SpanProvider<T extends Temporal & Comparable<? super T>> {
	
	/**
	 * Gets the current time for this temporal type.<br>
	 *
	 * @return A Supplier that provides the current temporal value
	 */
	@NonNull Supplier<T> now();
	
	/**
	 * Subtracts a duration from a temporal value.<br>
	 *
	 * @param value The temporal value
	 * @param duration The duration to subtract
	 * @return The result of the subtraction
	 */
	@SuppressWarnings("unchecked")
	default @NonNull T minus(@NonNull T value, @NonNull Duration duration) {
		return (T) value.minus(duration);
	}
	
	/**
	 * Adds a duration to a temporal value.<br>
	 *
	 * @param value The temporal value
	 * @param duration The duration to add
	 * @return The result of the addition
	 */
	@SuppressWarnings("unchecked")
	default @NonNull T plus(@NonNull T value, @NonNull Duration duration) {
		return (T) value.plus(duration);
	}
	
	/**
	 * Checks if a temporal value is before another.<br>
	 *
	 * @param value The value to check
	 * @param other The value to compare against
	 * @return True if value is before other, false otherwise
	 */
	default boolean isBefore(@NonNull T value, @NonNull T other) {
		return value.compareTo(other) < 0;
	}
	
	/**
	 * Checks if a temporal value is after another.<br>
	 *
	 * @param value The value to check
	 * @param other The value to compare against
	 * @return True if value is after other, false otherwise
	 */
	default boolean isAfter(@NonNull T value, @NonNull T other) {
		return value.compareTo(other) > 0;
	}
}
