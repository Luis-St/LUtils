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

package net.luis.utils.io.database.dialect.postgres.ops;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface providing PostgreSQL-specific array operations for column conditions.<br>
 * <p>
 *     These operations generate SQL conditions for PostgreSQL array types,
 *     including containment checks, overlap detection, and array length queries.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The element type of the array column
 */
public interface PostgresArrayOps<T> {

	/**
	 * Creates a condition that checks if the array column contains the given element.<br>
	 * Generates SQL: {@code value = ANY(column)}.<br>
	 *
	 * @param element The element to check for
	 * @return The array contains condition
	 */
	@NonNull SqlCondition contains(@NonNull Object element);

	/**
	 * Creates a condition that checks if the array column overlaps with the given list of values.<br>
	 * Generates SQL: {@code column && ARRAY[values]}.<br>
	 *
	 * @param values The values to check for overlap
	 * @return The array overlaps condition
	 */
	@NonNull SqlCondition overlaps(@NonNull List<?> values);

	/**
	 * Creates a condition that checks the length of the array column.<br>
	 * Generates SQL: {@code array_length(column, 1)}.<br>
	 *
	 * @return A condition representing the array length for further comparison
	 */
	@NonNull SqlCondition length();
}
