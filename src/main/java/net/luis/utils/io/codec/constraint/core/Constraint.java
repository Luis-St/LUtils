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

import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * Constraint interface that defines a validation method for a given value of type {@code T}.<br>
 * This interface can be implemented to create various constraints that can be applied to values during encoding or decoding processes.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value to be validated
 */
@FunctionalInterface
public interface Constraint<T> {
	
	/**
	 * Validates the given value against the constraint.<br>
	 * <p>
	 *     If the value satisfies the constraint, a successful Result is returned.<br>
	 *     If the value does not satisfy the constraint, a failed Result with an appropriate error message is returned.
	 * </p>
	 *
	 * @param value The value to be validated
	 * @return A result indicating success or failure of the validation
	 * @throws NullPointerException If the value is null
	 */
	@NotNull Result<Void> validate(@NonNull T value);
}
