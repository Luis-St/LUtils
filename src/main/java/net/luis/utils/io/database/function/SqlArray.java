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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Static utility class for SQL array functions.<br>
 *
 * @author Luis-St
 */
public class SqlArray {

	/**
	 * Appends an element to the end of an array expression.<br>
	 * Generates SQL: {@code ARRAY_APPEND(expression, element)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param element The element to append
	 * @param <T> The type of the element
	 * @return The modified array expression
	 */
	public static <T> @NonNull SqlExpression<Object> append(@NonNull SqlExpression<?> expr, @NonNull T element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Prepends an element to the beginning of an array expression.<br>
	 * Generates SQL: {@code ARRAY_PREPEND(element, expression)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param element The element to prepend
	 * @param <T> The type of the element
	 * @return The modified array expression
	 */
	public static <T> @NonNull SqlExpression<Object> prepend(@NonNull SqlExpression<?> expr, @NonNull T element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes all occurrences of an element from an array expression.<br>
	 * Generates SQL: {@code ARRAY_REMOVE(expression, element)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param element The element to remove
	 * @param <T> The type of the element
	 * @return The modified array expression
	 */
	public static <T> @NonNull SqlExpression<Object> remove(@NonNull SqlExpression<?> expr, @NonNull T element) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Replaces all occurrences of an element with a new element in an array expression.<br>
	 * Generates SQL: {@code ARRAY_REPLACE(expression, oldElement, newElement)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param oldElement The element to replace
	 * @param newElement The replacement element
	 * @param <T> The type of the elements
	 * @return The modified array expression
	 */
	public static <T> @NonNull SqlExpression<Object> replace(@NonNull SqlExpression<?> expr, @NonNull T oldElement, @NonNull T newElement) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Concatenates two array expressions.<br>
	 * Generates SQL: {@code ARRAY_CAT(expression, other)} or {@code expression || other} depending on the dialect.<br>
	 *
	 * @param expr The first array expression
	 * @param other The second array expression
	 * @return The concatenated array expression
	 */
	public static @NonNull SqlExpression<Object> cat(@NonNull SqlExpression<?> expr, @NonNull SqlExpression<?> other) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the length of an array expression.<br>
	 * Generates SQL: {@code ARRAY_LENGTH(expression, 1)} or {@code CARDINALITY(expression)} depending on the dialect.<br>
	 *
	 * @param expr The array expression
	 * @return The array length expression
	 */
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the position of an element in an array expression.<br>
	 * Generates SQL: {@code ARRAY_POSITION(expression, element)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param element The element to find
	 * @param <T> The type of the element
	 * @return The position expression (1-based, or null if not found)
	 */
	public static <T> @NonNull SqlExpression<Integer> position(@NonNull SqlExpression<?> expr, @NonNull T element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a condition that checks if an array expression contains the given element.<br>
	 * Generates SQL: {@code element = ANY(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param element The element to check for
	 * @param <T> The type of the element
	 * @return The contains condition
	 */
	public static <T> @NonNull SqlCondition contains(@NonNull SqlExpression<?> expr, @NonNull T element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a condition that checks if an array expression contains all of the given elements.<br>
	 * Generates SQL: {@code expression @> ARRAY[elements]} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param elements The elements to check for
	 * @param <T> The type of the elements
	 * @return The contains-all condition
	 */
	public static <T> @NonNull SqlCondition containsAll(@NonNull SqlExpression<?> expr, @NonNull List<T> elements) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a condition that checks if an array expression overlaps with the given elements.<br>
	 * Generates SQL: {@code expression && ARRAY[elements]} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param elements The elements to check for overlap
	 * @param <T> The type of the elements
	 * @return The overlaps condition
	 */
	public static <T> @NonNull SqlCondition overlaps(@NonNull SqlExpression<?> expr, @NonNull List<T> elements) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a condition that checks if the array is a subset of the given elements.<br>
	 * Generates SQL: {@code column <@ ARRAY[elements]} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param elements The elements to check against
	 * @param <T> The type of the elements
	 * @return The is-contained-by condition
	 */
	public static <T> @NonNull SqlCondition isContainedBy(@NonNull SqlExpression<?> expr, @NonNull List<T> elements) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns an array expression with duplicate elements removed.<br>
	 * Generates SQL: dialect-specific array distinct operation.<br>
	 *
	 * @param expr The array expression
	 * @return The distinct array expression
	 */
	public static @NonNull SqlExpression<Object> distinct(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns an array expression with elements sorted in ascending order.<br>
	 * Generates SQL: dialect-specific array sort operation.<br>
	 *
	 * @param expr The array expression
	 * @return The sorted array expression
	 */
	public static @NonNull SqlExpression<Object> sort(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Converts an array expression to a string with the given delimiter.<br>
	 * Generates SQL: {@code ARRAY_TO_STRING(expression, delimiter)} or dialect equivalent.<br>
	 *
	 * @param expr The array expression
	 * @param delimiter The delimiter between elements
	 * @return The string expression
	 */
	public static @NonNull SqlExpression<String> toStringExpr(@NonNull SqlExpression<?> expr, @NonNull String delimiter) {
		throw new UnsupportedOperationException();
	}
}
