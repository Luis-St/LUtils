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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.operation.SqlArrayOps;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a PostgreSQL-specific column.<br>
 *
 * @param <T> The type of the column value
 * @author Luis-St
 */
public interface PostgresColumn<T> extends SqlColumn<T> {
	
	/**
	 * Creates a condition using the SQL {@code SIMILAR TO} operator.<br>
	 * Generates SQL: {@code column SIMILAR TO pattern}.<br>
	 * <p>
	 *     The {@code SIMILAR TO} operator combines SQL {@code LIKE} syntax with POSIX regular expression syntax.<br>
	 *     It supports patterns like {@code %}, {@code _}, {@code |}, {@code *}, and {@code +}.<br>
	 * </p>
	 *
	 * @param pattern The SIMILAR TO pattern to match against
	 * @return The SIMILAR TO condition
	 */
	@NonNull SqlCondition similarTo(@NonNull String pattern);
	
	/**
	 * Creates a condition using a POSIX regular expression match.<br>
	 * Generates SQL: {@code column ~ pattern}.<br>
	 * <p>
	 *     POSIX regular expressions provide more powerful pattern matching than {@code LIKE} or {@code SIMILAR TO}.<br>
	 *     The {@code ~} operator performs a case-sensitive match against the given regex pattern.<br>
	 * </p>
	 *
	 * @param pattern The POSIX regular expression pattern
	 * @return The POSIX regex condition
	 */
	@NonNull SqlCondition posixRegex(@NonNull String pattern);
	
	/**
	 * Returns array-specific operations for this column.<br>
	 * @return Array operations accessor
	 */
	@NonNull SqlArrayOps<T> array();
	
	/**
	 * Returns PostgreSQL-specific JSON operations for this column.<br>
	 * @return JSON operations accessor
	 */
	@NonNull PostgresJsonOps json();
	
	/**
	 * Creates a case-insensitive pattern matching condition.<br>
	 * Generates SQL: {@code column ILIKE pattern}.<br>
	 *
	 * @param pattern The pattern to match against
	 * @return The case-insensitive like condition
	 */
	@NonNull SqlCondition ilike(@NonNull String pattern);
	
	/**
	 * Creates a negated case-insensitive pattern matching condition.<br>
	 * Generates SQL: {@code column NOT ILIKE pattern}.<br>
	 *
	 * @param pattern The pattern to match against
	 * @return The negated case-insensitive like condition
	 */
	@NonNull SqlCondition notIlike(@NonNull String pattern);
	
	/**
	 * Creates a condition that checks if the array column contains the given element.<br>
	 * Generates SQL: {@code element = ANY(column)}.<br>
	 *
	 * @param element The element to check for
	 * @return The array contains condition
	 */
	@NonNull SqlCondition arrayContains(@NonNull T element);
	
	/**
	 * Creates a condition that checks if the array column overlaps with the given elements.<br>
	 * Generates SQL: {@code column && ARRAY[...]}.<br>
	 *
	 * @param elements The elements to check overlap with
	 * @return The array overlaps condition
	 */
	@NonNull SqlCondition arrayOverlaps(@NonNull List<T> elements);
	
	/**
	 * Creates an aggregate expression that collects all values into an array.<br>
	 * Generates SQL: {@code ARRAY_AGG(column)}.<br>
	 *
	 * @return The array aggregate expression
	 */
	@NonNull SqlExpression<?> arrayAgg();
	
	/**
	 * Extracts a JSON value at the specified path and casts it to the given type.<br>
	 * Generates SQL: {@code column #>> '{path}'}.<br>
	 *
	 * @param path The JSON path to extract
	 * @param type The target type to cast the extracted value to
	 * @param <R> The result type
	 * @return The extracted JSON value expression
	 */
	<R> @NonNull SqlExpression<R> jsonExtract(@NonNull String path, @NonNull Class<R> type);
	
	/**
	 * Creates a condition that checks if a JSON path exists in the column.<br>
	 * Generates SQL: {@code jsonb_path_exists(column, 'path')}.<br>
	 *
	 * @param path The JSON path to check
	 * @return The JSON path exists condition
	 */
	@NonNull SqlCondition jsonExists(@NonNull String path);
	
	/**
	 * Creates a full-text search condition on this column.<br>
	 * Generates SQL: {@code to_tsvector(column) @@ to_tsquery('query')}.<br>
	 *
	 * @param query The full-text search query
	 * @return The full-text search condition
	 */
	@NonNull SqlCondition fullTextSearch(@NonNull String query);
}
