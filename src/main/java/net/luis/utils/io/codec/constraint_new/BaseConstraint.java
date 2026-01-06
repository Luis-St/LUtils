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
 * Base interface for all constraint types that provides fundamental equality and membership operations.<br>
 * <p>
 *     This interface defines the core constraint methods that are common to all constraint types.<br>
 *     It provides methods to check for equality, non-equality, inclusion in a collection, and exclusion from a collection.<br>
 *     All constraint interfaces in this package extend this interface either directly or indirectly.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface BaseConstraint<T, C> {
	
	/**
	 * Applies an equality constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are equal to the specified value.<br>
	 *     This constraint is useful when only a specific value should be accepted.
	 * </p>
	 *
	 * @param value The exact value that should be matched
	 * @return A new type with the applied equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #notEqualTo(Object)
	 */
	@NonNull C equalTo(@NonNull T value);
	
	/**
	 * Applies a non-equality constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are not equal to the specified value.<br>
	 *     This constraint is useful when a specific value should be excluded.
	 * </p>
	 *
	 * @param value The value that should be excluded
	 * @return A new type with the applied non-equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #equalTo(Object)
	 */
	@NonNull C notEqualTo(@NonNull T value);
	
	/**
	 * Applies an inclusion constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are included in the specified collection.<br>
	 *     This constraint is useful when only a specific set of values should be accepted.
	 * </p>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new type with the applied inclusion constraint
	 * @throws NullPointerException If the collection is null
	 * @see #notIn(Collection)
	 */
	@NonNull C in(@NonNull Collection<T> values);
	
	/**
	 * Applies an exclusion constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are not included in the specified collection.<br>
	 *     This constraint is useful when a specific set of values should be excluded.
	 * </p>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new type with the applied exclusion constraint
	 * @throws NullPointerException If the collection is null
	 * @see #in(Collection)
	 */
	@NonNull C notIn(@NonNull Collection<T> values);
}
