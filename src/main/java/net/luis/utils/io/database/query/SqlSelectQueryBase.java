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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.SqlPage;
import net.luis.utils.io.database.SqlRenderable;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlOrderable;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.entity.SqlEntityNotFoundException;
import net.luis.utils.io.database.exception.query.SqlQueryException;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base interface for SQL select queries providing common functionality
 * for both entity queries and projection queries.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result
 * @param <Q> The self-referencing query type for fluent API support
 */
public interface SqlSelectQueryBase<T, Q extends SqlSelectQueryBase<T, Q>> extends SqlRenderable {

	/**
	 * Adds a Common Table Expression (CTE) to the query.<br>
	 *
	 * @param cte The common table expression to add
	 * @return This query for method chaining
	 */
	@NonNull Q with(@NonNull CommonTableExpression cte);

	/**
	 * Adds a Common Table Expression (CTE) with the specified query to the query.<br>
	 *
	 * @param cte The common table expression to add
	 * @param query The query for the CTE
	 * @return This query for method chaining
	 */
	@NonNull Q with(@NonNull CommonTableExpression cte, @NonNull SqlSelectQuery<?> query);

	/**
	 * Adds a {@code WHERE} condition to the query.<br>
	 * Multiple calls are combined with AND.<br>
	 *
	 * @param condition The condition to add
	 * @return This query for method chaining
	 */
	@NonNull Q where(@NonNull SqlCondition condition);

	/**
	 * Adds a {@code WHERE EXISTS} subquery condition.<br>
	 *
	 * @param subquery The subquery to check for existence
	 * @return This query for method chaining
	 */
	@NonNull Q whereExists(@NonNull SqlSelectQuery<?> subquery);

	/**
	 * Adds a {@code WHERE NOT EXISTS} subquery condition.<br>
	 *
	 * @param subquery The subquery to check for non-existence
	 * @return This query for method chaining
	 */
	@NonNull Q whereNotExists(@NonNull SqlSelectQuery<?> subquery);

	/**
	 * Adds an {@code INNER JOIN} clause to the query.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);

	/**
	 * Adds a {@code LEFT JOIN} clause to the query.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);

	/**
	 * Adds a {@code RIGHT JOIN} clause to the query.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);

	/**
	 * Adds a {@code FULL JOIN} clause to the query.<br>
	 *
	 * @param table The table to join
	 * @param on The join condition
	 * @return This query for method chaining
	 */
	@NonNull Q fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);

	/**
	 * Adds a {@code GROUP BY} clause to the query.<br>
	 *
	 * @param columns The columns to group by
	 * @return This query for method chaining
	 */
	@NonNull Q groupBy(SqlColumn<?> @NonNull ... columns);

	/**
	 * Adds a {@code HAVING} condition to the query.<br>
	 * Requires a preceding GROUP BY clause.<br>
	 *
	 * @param condition The condition to filter groups
	 * @return This query for method chaining
	 */
	@NonNull Q having(@NonNull SqlCondition condition);

	/**
	 * Adds an {@code ORDER BY} clause to the query.<br>
	 *
	 * @param orderables The columns or expressions to order by
	 * @return This query for method chaining
	 */
	@NonNull Q orderBy(SqlOrderable @NonNull ... orderables);

	/**
	 * Limits the number of results returned.<br>
	 *
	 * @param limit The maximum number of results
	 * @return This query for method chaining
	 */
	@NonNull Q limit(int limit);

	/**
	 * Skips the specified number of results.<br>
	 *
	 * @param offset The number of results to skip
	 * @return This query for method chaining
	 */
	@NonNull Q offset(long offset);

	/**
	 * Adds {@code DISTINCT} to the query to eliminate duplicate rows.<br>
	 * @return This query for method chaining
	 */
	@NonNull Q distinct();

	/**
	 * Combines this query with another using {@code UNION} (removes duplicates).<br>
	 *
	 * @param other The other query to combine with
	 * @return This query for method chaining
	 */
	@NonNull Q union(@NonNull SqlSelectQueryBase<T, ?> other);

	/**
	 * Combines this query with another using {@code UNION ALL} (keeps duplicates).<br>
	 *
	 * @param other The other query to combine with
	 * @return This query for method chaining
	 */
	@NonNull Q unionAll(@NonNull SqlSelectQueryBase<T, ?> other);

	/**
	 * Combines this query with another using {@code INTERSECT} (common rows only).<br>
	 *
	 * @param other The other query to combine with
	 * @return This query for method chaining
	 */
	@NonNull Q intersect(@NonNull SqlSelectQueryBase<T, ?> other);

	/**
	 * Combines this query with another using {@code EXCEPT} (rows in this but not other).<br>
	 *
	 * @param other The other query to combine with
	 * @return This query for method chaining
	 */
	@NonNull Q except(@NonNull SqlSelectQueryBase<T, ?> other);

	/**
	 * Executes the query and returns all results as a list.<br>
	 *
	 * @return A list of all matching results, empty list if none found
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> fetch() throws SqlException;

	/**
	 * Executes the query and returns the first result.<br>
	 *
	 * @return An optional containing the first result, or empty if none found
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull Optional<T> fetchFirst() throws SqlException;

	/**
	 * Executes the query and returns exactly one result.<br>
	 *
	 * @return The single matching result
	 * @throws SqlEntityNotFoundException If no result is found
	 * @throws SqlQueryException If more than one result is found
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull T fetchOne() throws SqlException;

	/**
	 * Executes the query and returns one result or null.<br>
	 *
	 * @return The single matching result, or null if none found
	 * @throws SqlQueryException If more than one result is found
	 * @throws SqlException If a database access error occurs
	 */
	@Nullable T fetchOneOrNull() throws SqlException;

	/**
	 * Executes the query and returns the count of matching rows.<br>
	 *
	 * @return The number of matching rows
	 * @throws SqlException If a database access error occurs
	 */
	long count() throws SqlException;

	/**
	 * Checks if any rows match the query conditions.<br>
	 *
	 * @return True if at least one row matches, false otherwise
	 * @throws SqlException If a database access error occurs
	 */
	boolean exists() throws SqlException;

	/**
	 * Executes the query and returns results as a stream.<br>
	 * The stream should be closed after use to release database resources.<br>
	 *
	 * @return A stream of matching results
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull Stream<T> stream() throws SqlException;

	/**
	 * Executes the query with pagination and returns a page of results.<br>
	 *
	 * @param page The page number (0-based)
	 * @param pageSize The number of results per page
	 * @return A page containing the results and pagination metadata
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull SqlPage<T> fetchPage(int page, int pageSize) throws SqlException;

	/**
	 * Returns the parameter values for this query.<br>
	 * @return A list of parameter values in order
	 */
	@NonNull List<Object> getParameters();
}
