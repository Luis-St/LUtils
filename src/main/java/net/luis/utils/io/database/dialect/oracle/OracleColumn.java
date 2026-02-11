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

package net.luis.utils.io.database.dialect.oracle;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing an Oracle-specific column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface OracleColumn<T> extends SqlColumn<T> {
	
	/**
	 * Returns the column value if it is not null, otherwise returns the specified default.<br>
	 * Generates SQL: {@code NVL(column, defaultValue)}.
	 *
	 * @param defaultValue The value to return if the column value is null
	 * @return An expression representing the null-coalesced value
	 */
	@NonNull SqlExpression<?> nvl(@NonNull T defaultValue);
	
	/**
	 * Returns one value if the column is not null, and another if it is null.<br>
	 * Generates SQL: {@code NVL2(column, ifNotNull, ifNull)}.<br>
	 * <p>
	 *     Unlike {@code NVL}, this function allows specifying different return values for<br>
	 *     both the null and non-null cases.
	 * </p>
	 *
	 * @param ifNotNull The value to return if the column is not null
	 * @param ifNull The value to return if the column is null
	 * @return An expression representing the conditional null check
	 */
	@NonNull SqlExpression<?> nvl2(@NonNull T ifNotNull, @NonNull T ifNull);
	
	/**
	 * Evaluates the column against a series of search-result pairs and returns the matching result.<br>
	 * Generates SQL: {@code DECODE(column, search1, result1, search2, result2, ..., default)}.<br>
	 * <p>
	 *     The {@code DECODE} function compares the column value to each search value in order<br>
	 *     and returns the corresponding result. An optional default value can be provided as the last argument.
	 * </p>
	 *
	 * @param searchResults Alternating search values and result values, optionally followed by a default
	 * @return An expression representing the decoded value
	 */
	@NonNull SqlExpression<?> decode(Object @NonNull ... searchResults);
	
	/**
	 * Converts this column's value to a character string using the specified format.<br>
	 * Generates SQL: {@code TO_CHAR(column, 'format')}.
	 *
	 * @param format The format model to apply
	 * @return An expression representing the formatted string value
	 */
	@NonNull SqlExpression<?> toChar(@NonNull String format);
	
	/**
	 * Converts this column's string value to a date using the specified format.<br>
	 * Generates SQL: {@code TO_DATE(column, 'format')}.
	 *
	 * @param format The date format model to parse against
	 * @return An expression representing the parsed date value
	 */
	@NonNull SqlExpression<?> toDate(@NonNull String format);
	
	/**
	 * Creates a condition that matches the column value against a regular expression pattern.<br>
	 * Generates SQL: {@code REGEXP_LIKE(column, 'pattern')}.
	 *
	 * @param pattern The regular expression pattern to match against
	 * @return A condition representing the regular expression match
	 */
	@NonNull SqlCondition regexpLike(@NonNull String pattern);
	
	/**
	 * Creates a hierarchical query expression using this column as the prior reference.<br>
	 * Generates SQL: {@code CONNECT BY PRIOR column}.<br>
	 * <p>
	 *     This is used in Oracle hierarchical queries to define parent-child relationships between rows.<br>
	 *     The prior column value in the parent row is matched against values in child rows to build the hierarchy.
	 * </p>
	 *
	 * @return An expression representing the hierarchical prior reference
	 */
	@NonNull SqlExpression<?> connectByPrior();
	
	/**
	 * Returns the path from the root to the current node in a hierarchical query.<br>
	 * Generates SQL: {@code SYS_CONNECT_BY_PATH(column, 'separator')}.<br>
	 * <p>
	 *     This function concatenates the column values along the path from root to the current row,<br>
	 *     separated by the specified delimiter.<br>
	 *     It can only be used in hierarchical queries with {@code CONNECT BY}.
	 * </p>
	 *
	 * @param separator The delimiter to place between path elements
	 * @return An expression representing the hierarchical path
	 */
	@NonNull SqlExpression<?> sysConnectByPath(@NonNull String separator);
	
	/**
	 * Aggregates string values from grouped rows into a single string, separated by the specified separator.<br>
	 * Generates SQL: {@code LISTAGG(column, 'separator') WITHIN GROUP (ORDER BY column)}.<br>
	 * <p>
	 *     The {@code LISTAGG} function is Oracle's standard aggregate for concatenating<br>
	 *     values from multiple rows into a single string result.
	 * </p>
	 *
	 * @param separator The delimiter placed between concatenated values
	 * @return An expression representing the aggregated string
	 */
	@NonNull SqlExpression<?> listagg(@NonNull String separator);
	
	/**
	 * Extracts a scalar value from a JSON document using the specified JSON path.<br>
	 * Generates SQL: {@code JSON_VALUE(column, 'path')}.
	 *
	 * @param path The JSON path expression to extract
	 * @return An expression representing the extracted JSON value
	 */
	@NonNull SqlExpression<?> jsonValue(@NonNull String path);
}
