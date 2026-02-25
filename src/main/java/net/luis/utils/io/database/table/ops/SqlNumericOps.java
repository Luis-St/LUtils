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

package net.luis.utils.io.database.table.ops;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 * Interface providing numeric-specific SQL operations for column conditions.<br>
 * <p>
 *     These operations generate SQL conditions specific to numeric columns
 *     such as sign checks and modulo comparisons.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The numeric type of the column value
 */
public interface SqlNumericOps<T> {

	/**
	 * Creates a condition that checks if the column value is positive (greater than zero).<br>
	 * Generates SQL: {@code column > 0}.<br>
	 *
	 * @return The is-positive condition
	 */
	@NonNull SqlCondition isPositive();

	/**
	 * Creates a condition that checks if the column value is negative (less than zero).<br>
	 * Generates SQL: {@code column < 0}.<br>
	 *
	 * @return The is-negative condition
	 */
	@NonNull SqlCondition isNegative();

	/**
	 * Creates a condition that checks if the column value is zero.<br>
	 * Generates SQL: {@code column = 0}.<br>
	 *
	 * @return The is-zero condition
	 */
	@NonNull SqlCondition isZero();

	/**
	 * Creates a condition that checks if the column value modulo the given divisor equals the given remainder.<br>
	 * Generates SQL: {@code MOD(column, divisor) = remainder}.<br>
	 *
	 * @param divisor The divisor for the modulo operation
	 * @param remainder The expected remainder
	 * @return The modulo-equals condition
	 */
	@NonNull SqlCondition moduloEquals(@NonNull Number divisor, @NonNull Number remainder);
}
