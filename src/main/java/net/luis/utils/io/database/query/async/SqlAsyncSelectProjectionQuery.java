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

package net.luis.utils.io.database.query.async;

import net.luis.utils.io.database.SqlPage;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * Interface representing an asynchronous SQL select query with column projection.<br>
 * <p>
 *     This interface is used when selecting specific columns rather than full entities,<br>
 *     returning results as {@code SqlRow2}, {@code SqlRow3}, etc. or single column values.<br>
 *     All terminal operations return {@link CompletableFuture} for non-blocking execution.
 * </p>
 * <p>
 *     All common query functionality is inherited from {@link SqlAsyncSelectQueryBase}.<br>
 *     Unlike {@link SqlAsyncSelectQuery}, this interface does not support row-level locking as projection queries are typically read-only operations.<br>
 * </p>
 * <p>
 *     This interface provides additional {@code fetchAs} methods to map projection results to custom data structures (records, DTOs, or interfaces).<br>
 * </p>
 *
 * @see SqlAsyncSelectQueryBase
 * @see SqlAsyncSelectQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the projection result (e.g., SqlRow2, SqlRow3, or single column type)
 */
public interface SqlAsyncSelectProjectionQuery<T> extends SqlAsyncSelectQueryBase<T, SqlAsyncSelectProjectionQuery<T>> {
	
	/**
	 * Asynchronously executes the query and maps all results to the specified type.<br>
	 * The target type must have a constructor or factory method that matches the projected columns in order and type.<br>
	 *
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A future that completes with a list of all matching results mapped to the target type
	 */
	<R> @NonNull CompletableFuture<List<R>> fetchAs(@NonNull Class<R> type);
	
	/**
	 * Asynchronously executes the query and maps the first result to the specified type.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return A future that completes with an optional containing the first result mapped to the target type, or empty if none found
	 */
	<R> @NonNull CompletableFuture<Optional<R>> fetchFirstAs(@NonNull Class<R> type);
	
	/**
	 * Asynchronously executes the query and maps exactly one result to the specified type.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return A future that completes with the single matching result mapped to the target type
	 */
	<R> @NonNull CompletableFuture<R> fetchOneAs(@NonNull Class<R> type);
	
	/**
	 * Asynchronously executes the query and maps one result to the specified type, or returns null.<br>
	 *
	 * @param type The class to map the result to
	 * @param <R> The target type
	 * @return A future that completes with the single result mapped to the target type or null
	 */
	<R> @NonNull CompletableFuture<@Nullable R> fetchOneOrNullAs(@NonNull Class<R> type);
	
	/**
	 * Asynchronously executes the query and returns results as a stream mapped to the specified type.<br>
	 * The stream should be closed after use to release database resources.<br>
	 *
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A future that completes with a stream of matching results mapped to the target type
	 */
	<R> @NonNull CompletableFuture<Stream<R>> streamAs(@NonNull Class<R> type);
	
	/**
	 * Asynchronously executes the query with pagination and maps results to the specified type.<br>
	 *
	 * @param page The page number (0-based)
	 * @param pageSize The number of results per page
	 * @param type The class to map results to
	 * @param <R> The target type
	 * @return A future that completes with a page of results mapped to the target type
	 */
	<R> @NonNull CompletableFuture<SqlPage<R>> fetchPageAs(int page, int pageSize, @NonNull Class<R> type);
}
