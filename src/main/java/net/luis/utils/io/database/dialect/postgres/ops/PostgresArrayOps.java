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
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface providing PostgreSQL-specific array operations for column conditions.<br>
 * <p>
 *     These operations generate SQL conditions and expressions for PostgreSQL array types,<br>
 *     including containment checks, overlap detection, array length queries,<br>
 *     element manipulation, and array transformation.
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
	
	/**
	 * Appends an element to the end of the array column.<br>
	 * Generates SQL: {@code array_append(column, element)}.<br>
	 *
	 * @param element The element to append
	 * @return The modified array expression
	 */
	@NonNull SqlExpression<List<T>> append(@NonNull T element);
	
	/**
	 * Prepends an element to the beginning of the array column.<br>
	 * Generates SQL: {@code array_prepend(element, column)}.<br>
	 *
	 * @param element The element to prepend
	 * @return The modified array expression
	 */
	@NonNull SqlExpression<List<T>> prepend(@NonNull T element);
	
	/**
	 * Removes all occurrences of an element from the array column.<br>
	 * Generates SQL: {@code array_remove(column, element)}.<br>
	 *
	 * @param element The element to remove
	 * @return The modified array expression
	 */
	@NonNull SqlExpression<List<T>> remove(@NonNull T element);
	
	/**
	 * Replaces all occurrences of an element with a new element in the array column.<br>
	 * Generates SQL: {@code array_replace(column, oldElement, newElement)}.<br>
	 *
	 * @param oldElement The element to replace
	 * @param newElement The replacement element
	 * @return The modified array expression
	 */
	@NonNull SqlExpression<List<T>> replace(@NonNull T oldElement, @NonNull T newElement);
	
	/**
	 * Concatenates the array column with another array expression.<br>
	 * Generates SQL: {@code array_cat(column, other)}.<br>
	 *
	 * @param other The other array expression to concatenate
	 * @return The concatenated array expression
	 */
	@NonNull SqlExpression<List<T>> cat(@NonNull SqlExpression<List<T>> other);
	
	/**
	 * Returns the position of an element in the array column.<br>
	 * Generates SQL: {@code array_position(column, element)}.<br>
	 *
	 * @param element The element to find
	 * @return The position expression (1-based, or null if not found)
	 */
	@NonNull SqlExpression<Integer> position(@NonNull T element);
	
	/**
	 * Creates a condition that checks if the array column contains all of the given elements.<br>
	 * Generates SQL: {@code column @> ARRAY[elements]}.<br>
	 *
	 * @param elements The elements to check for
	 * @return The contains-all condition
	 */
	@NonNull SqlCondition containsAll(@NonNull List<?> elements);
	
	/**
	 * Creates a condition that checks if the array column is a subset of the given elements.<br>
	 * Generates SQL: {@code column <@ ARRAY[elements]}.<br>
	 *
	 * @param elements The elements to check against
	 * @return The is-contained-by condition
	 */
	@NonNull SqlCondition isContainedBy(@NonNull List<?> elements);
	
	/**
	 * Converts the array column to a string with the given delimiter.<br>
	 * Generates SQL: {@code array_to_string(column, delimiter)}.<br>
	 *
	 * @param delimiter The delimiter between elements
	 * @return The string expression
	 */
	@NonNull SqlExpression<String> toStringExpr(@NonNull String delimiter);
}
