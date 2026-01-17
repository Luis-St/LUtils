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

package net.luis.utils.io.codec.constraint_new.temporal;

import org.jspecify.annotations.NonNull;

import java.time.Duration;

/**
 * Constraint interface for temporal types that provides duration-based span validation operations.<br>
 * <p>
 *     This interface provides methods for constraining temporal values based on their distance
 *     from the current time.<br>
 *     It allows validation that values fall within a specified duration in the past or future.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface TemporalSpanConstraint<T, C> {
	
	/**
	 * Applies a "within last" duration constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are within the last specified duration from the current time.<br>
	 *     For example, {@code withinLast(Duration.ofDays(7))} ensures the value is within the last 7 days,
	 *     or {@code withinLast(Duration.ofHours(2))} ensures the value is within the last 2 hour.
	 * </p>
	 * <p>
	 *     The validation is performed at the time of encoding/decoding, using the current time as reference.
	 * </p>
	 *
	 * @param duration The duration backwards from now
	 * @return A new type with the applied within-last constraint
	 * @throws NullPointerException If the duration is null
	 * @throws IllegalArgumentException If the duration is negative or not supported by the temporal type
	 * @see #withinNext(Duration)
	 */
	@NonNull C withinLast(@NonNull Duration duration);
	
	/**
	 * Applies a "within next" duration constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are within the next specified duration from the current time.<br>
	 *     For example, {@code withinNext(Duration.ofDays(30))} ensures the value is within the next 30 days,
	 *     or {@code withinNext(Duration.ofHours(1))} ensures the value is within the next hour.
	 * </p>
	 * <p>
	 *     The validation is performed at the time of encoding/decoding, using the current time as reference.
	 * </p>
	 *
	 * @param duration The duration forwards from now
	 * @return A new type with the applied within-next constraint
	 * @throws NullPointerException If the duration is null
	 * @throws IllegalArgumentException If the duration is negative or not supported by the temporal type
	 * @see #withinLast(Duration)
	 */
	@NonNull C withinNext(@NonNull Duration duration);
}
