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

package net.luis.utils.io.database.dialect.sqlserver;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL Server-specific column.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the column value
 */
public interface SqlServerColumn<T> extends SqlColumn<T> {

	/**
	 * Creates a full-text search condition using {@code FREETEXT}.<br>
	 * Generates SQL: {@code FREETEXT(column, 'searchTerms')}.<br>
	 * <p>
	 *     The {@code FREETEXT} predicate searches for values that match the meaning of the<br>
	 *     search terms, not just the exact wording. It automatically handles word variations<br>
	 *     and inflectional forms.
	 * </p>
	 *
	 * @param searchTerms The text to search for
	 * @return A condition representing the full-text search predicate
	 */
	@NonNull SqlCondition freeText(@NonNull String searchTerms);

	/**
	 * Creates a full-text search condition using {@code CONTAINS}.<br>
	 * Generates SQL: {@code CONTAINS(column, 'searchTerms')}.<br>
	 * <p>
	 *     The {@code CONTAINS} predicate performs precise full-text searches including<br>
	 *     support for prefix terms, proximity terms, weighted terms, and Boolean operators.
	 * </p>
	 *
	 * @param searchTerms The search expression to match against
	 * @return A condition representing the contains search predicate
	 */
	@NonNull SqlCondition containsText(@NonNull String searchTerms);

	/**
	 * Extracts a scalar value from a JSON string using the specified JSON path.<br>
	 * Generates SQL: {@code JSON_VALUE(column, 'path')}.
	 *
	 * @param path The JSON path expression to extract
	 * @return An expression representing the extracted JSON value
	 */
	@NonNull SqlExpression<?> jsonValue(@NonNull String path);

	/**
	 * Creates a condition that tests whether a JSON path exists in this column's value.<br>
	 * Generates SQL: {@code JSON_PATH_EXISTS(column, 'path')}.
	 *
	 * @param path The JSON path to check for existence
	 * @return A condition that evaluates to true if the path exists in the JSON
	 */
	@NonNull SqlCondition isJsonPath(@NonNull String path);

	/**
	 * Returns the column value if it is not null, otherwise returns the specified default.<br>
	 * Generates SQL: {@code ISNULL(column, defaultValue)}.
	 *
	 * @param defaultValue The value to return if the column value is null
	 * @return An expression representing the null-coalesced value
	 */
	@NonNull SqlExpression<?> isNull(@NonNull T defaultValue);

	/**
	 * Attempts to convert this column's value to the specified target type.<br>
	 * Generates SQL: {@code TRY_CONVERT(targetType, column)}.<br>
	 * <p>
	 *     Unlike {@code CONVERT}, this function returns null instead of raising an error<br>
	 *     if the conversion fails.
	 * </p>
	 *
	 * @param targetType The target Java type to convert to
	 * @return An expression representing the attempted conversion
	 */
	@NonNull SqlExpression<?> tryConvert(@NonNull Class<?> targetType);

	/**
	 * Formats a date or time column using the specified format string.<br>
	 * Generates SQL: {@code FORMAT(column, 'format')}.
	 *
	 * @param format The format pattern to apply
	 * @return An expression representing the formatted date value
	 */
	@NonNull SqlExpression<?> formatDate(@NonNull String format);

	/**
	 * Aggregates string values from grouped rows, separated by the specified separator.<br>
	 * Generates SQL: {@code STRING_AGG(column, 'separator')}.<br>
	 * <p>
	 *     This is SQL Server's equivalent of group concatenation. It concatenates the values<br>
	 *     of string expressions and places separator values between them.
	 * </p>
	 *
	 * @param separator The delimiter placed between concatenated values
	 * @return An expression representing the aggregated string
	 */
	@NonNull SqlExpression<?> stringAgg(@NonNull String separator);
}
