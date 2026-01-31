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

package net.luis.utils.io.codec.constraint.core;

import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for numeric types that provides numeric-specific validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} with additional methods specific to numeric values.<br>
 *     It provides convenience methods for common numeric constraints such as percentage validation.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface NumericConstraint<T, C> extends SignedConstraint<T, C> {
	
	/**
	 * Applies a percentage value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are between zero and one hundred (inclusive).<br>
	 *     This is a convenience method equivalent to {@code between(zero, hundred)}.
	 * </p>
	 *
	 * @return A new type with the applied percentage constraint
	 * @see #betweenOrEqual(Object, Object)
	 */
	@NonNull C percentage();
}
