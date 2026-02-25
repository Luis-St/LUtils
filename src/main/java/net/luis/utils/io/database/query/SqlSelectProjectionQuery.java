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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.entity.SqlEntityNotFoundException;
import net.luis.utils.io.database.exception.locking.SqlLockNotAvailableException;
import net.luis.utils.io.database.exception.query.SqlQueryException;
import net.luis.utils.io.database.query.async.SqlAsyncSelectProjectionQuery;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Interface representing a SQL select query with column projection.<br>
 * <p>
 *     This interface is used when selecting specific columns rather than full entities,<br>
 *     returning results as {@code Row2}, {@code Row3}, etc. or single column values.
 * </p>
 * <p>
 *     All common query functionality is inherited from {@link SqlSelectQueryBase}.<br>
 *     Unlike {@link SqlSelectQuery}, this interface does not support row-level locking as projection queries are typically read-only operations.
 * </p>
 * <p>
 *     This interface provides additional {@code fetchAs} methods to map projection results to custom data structures (records, DTOs, or interfaces).<br>
 * </p>
 *
 * @see SqlSelectQueryBase
 * @see SqlSelectQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the projection result (e.g., Row2, Row3, or single column type)
 */
public interface SqlSelectProjectionQuery<T> extends SqlSelectQueryBase<T, SqlSelectProjectionQuery<T>> {

	/**
	 * Executes the query and maps all results to the specified type.<br>
	 * <p>
	 *     The target type must have a constructor or factory method that matches the projected columns in order and type.<br>
	 * </p>
	 *
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A list of all matching results mapped to the target type
	 * @throws SqlException If a database access error occurs
	 */
	<R> @NonNull List<R> fetchAs(@NonNull Class<R> type) throws SqlException;

	/**
	 * Executes the query and maps the first result to the specified type.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return An optional containing the first result mapped to the target type, or empty if none found
	 * @throws SqlException If a database access error occurs
	 */
	<R> @NonNull Optional<R> fetchFirstAs(@NonNull Class<R> type) throws SqlException;

	/**
	 * Executes the query and maps exactly one result to the specified type.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return The single matching result mapped to the target type
	 * @throws SqlEntityNotFoundException If no result is found
	 * @throws SqlQueryException If more than one result is found
	 * @throws SqlException If a database access error occurs
	 */
	<R> @NonNull R fetchOneAs(@NonNull Class<R> type) throws SqlException;

	/**
	 * Executes the query and maps one result to the specified type, or returns null.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return The single matching result mapped to the target type, or null if none found
	 * @throws SqlQueryException If more than one result is found
	 * @throws SqlException If a database access error occurs
	 */
	<R> @Nullable R fetchOneOrNullAs(@NonNull Class<R> type) throws SqlException;

	/**
	 * Executes the query and returns results as a stream mapped to the specified type.<br>
	 *
	 * The stream should be closed after use to release database resources.<br>
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A stream of matching results mapped to the target type
	 * @throws SqlException If a database access error occurs
	 */
	<R> @NonNull Stream<R> streamAs(@NonNull Class<R> type) throws SqlException;

	/**
	 * Executes the query with pagination and maps results to the specified type.<br>
	 *
	 * @param page The page number (0-based)
	 * @param pageSize The number of results per page
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A page containing the results mapped to the target type and pagination metadata
	 * @throws SqlException If a database access error occurs
	 */
	<R> @NonNull SqlPage<R> fetchPageAs(int page, int pageSize, @NonNull Class<R> type) throws SqlException;

	/**
	 * Adds {@code FOR UPDATE} clause to lock selected rows.<br>
	 * Prevents other transactions from modifying or locking the rows until this transaction completes.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlSelectProjectionQuery<T> forUpdate();

	/**
	 * Adds {@code SKIP LOCKED} modifier to skip rows that are already locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 * Useful for job queue patterns where multiple workers process available rows.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlSelectProjectionQuery<T> skipLocked();

	/**
	 * Adds NOWAIT modifier to fail immediately if rows are locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 *
	 * @return This query for method chaining
	 * @throws SqlLockNotAvailableException If the rows are locked by another transaction
	 */
	@NonNull SqlSelectProjectionQuery<T> noWait();

	/**
	 * Returns an asynchronous view of this query where all terminal operations return {@link java.util.concurrent.CompletableFuture}.<br>
	 * @return The asynchronous projection query
	 */
	@NonNull SqlAsyncSelectProjectionQuery<T> async();
}
