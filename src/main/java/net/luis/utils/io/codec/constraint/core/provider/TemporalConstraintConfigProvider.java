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

package net.luis.utils.io.codec.constraint.core.provider;

import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.Temporal;

/**
 * A helper interface for temporal constraint configuration manipulation.<br>
 * <p>
 *     This interface provides methods to build and modify temporal constraint configurations
 *     in a type-safe manner. It serves as a bridge between the constraint interfaces and
 *     the underlying configuration records.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 * @param <V> The constraint configuration type
 */
public interface TemporalConstraintConfigProvider<T extends Temporal, V> {
	
	/**
	 * Creates a new configuration with an equality constraint.<br>
	 *
	 * @param value The temporal value to match or exclude
	 * @param negated True to exclude the value (not equal), false to require it (equal)
	 * @return A new configuration with the equality constraint applied
	 * @throws NullPointerException If the value is null
	 */
	@NonNull V withEquals(@NonNull T value, boolean negated);
	
	/**
	 * Creates a new configuration with a minimum boundary constraint.<br>
	 *
	 * @param min The minimum temporal value
	 * @param inclusive True for inclusive (>=), false for exclusive (>)
	 * @return A new configuration with the minimum constraint applied
	 * @throws NullPointerException If the min value is null
	 */
	@NonNull V withMin(@NonNull T min, boolean inclusive);
	
	/**
	 * Creates a new configuration with a maximum boundary constraint.<br>
	 *
	 * @param max The maximum temporal value
	 * @param inclusive True for inclusive (<=), false for exclusive (<)
	 * @return A new configuration with the maximum constraint applied
	 * @throws NullPointerException If the max value is null
	 */
	@NonNull V withMax(@NonNull T max, boolean inclusive);
	
	/**
	 * Creates a new configuration with a range constraint.<br>
	 *
	 * @param min The minimum temporal value
	 * @param max The maximum temporal value
	 * @param inclusive True for inclusive bounds, false for exclusive
	 * @return A new configuration with the range constraint applied
	 * @throws NullPointerException If the min or max value is null
	 */
	@NonNull V withRange(@NonNull T min, @NonNull T max, boolean inclusive);
	
	/**
	 * Creates a new configuration with a "within last" duration constraint.<br>
	 *
	 * @param duration The duration from now backwards
	 * @return A new configuration with the within-last constraint applied
	 * @throws NullPointerException If the duration is null
	 */
	@NonNull V withWithinLast(@NonNull Duration duration);
	
	/**
	 * Creates a new configuration with a "within next" duration constraint.<br>
	 *
	 * @param duration The duration from now forwards
	 * @return A new configuration with the within-next constraint applied
	 * @throws NullPointerException If the duration is null
	 */
	@NonNull V withWithinNext(@NonNull Duration duration);
}
