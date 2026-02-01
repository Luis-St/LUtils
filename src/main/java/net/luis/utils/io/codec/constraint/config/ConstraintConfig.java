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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.core.Constraint;
import org.jspecify.annotations.NonNull;

/**
 * Configuration interface that defines a validation method for a given value of type {@code T}.<br>
 * <p>
 *     This interface is implemented by constraint configuration records to provide validation of values against the configured constraints.<br>
 *     It mirrors the {@link Constraint} functional interface but is specifically designed for configuration records.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the value to be validated
 */
@FunctionalInterface
public interface ConstraintConfig<T> {
	
	/**
	 * Validates the given value against all configured constraints.<br>
	 * <p>
	 *     If the value satisfies all constraints, a successful Result is returned.<br>
	 *     If the value does not satisfy any constraint, a failed Result with an appropriate error message is returned.<br>
	 *     The validation uses early-exit behavior, stopping at the first failed constraint.
	 * </p>
	 *
	 * @param value The value to be validated
	 * @throws NullPointerException If the value is null
	 * @throws ConstraintViolateException If the value does not satisfy the configured constraints
	 */
	void validate(@NonNull T value);
}
