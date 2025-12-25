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

package net.luis.utils.io.codec.constraint.core;

import org.jspecify.annotations.NonNull;

/**
 * Provides numeric constant values for a specific number type.<br>
 * <p>
 *     This interface defines methods to obtain common numeric constants (zero, one, hundred)
 *     for a given numeric type. It is used by numeric constraint interfaces to provide
 *     default implementations without requiring knowledge of the specific numeric type.
 * </p>
 *
 * @see net.luis.utils.io.codec.constraint.numeric.NumericConstraint
 *
 * @author Luis-St
 *
 * @param <T> The numeric type for which constants are provided
 */
public interface NumberProvider<T extends Number & Comparable<T>> {

	/**
	 * Returns the zero value for the numeric type.<br>
	 *
	 * @return The zero constant
	 */
	@NonNull T zero();

	/**
	 * Returns the one value for the numeric type.<br>
	 *
	 * @return The one constant
	 */
	@NonNull T one();

	/**
	 * Returns the hundred value for the numeric type.<br>
	 *
	 * @return The hundred constant
	 */
	@NonNull T hundred();
}
