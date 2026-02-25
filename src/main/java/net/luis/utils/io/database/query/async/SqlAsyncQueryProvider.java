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

import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Interface providing methods to create asynchronous SQL queries for a specific entity type.<br>
 * <p>
 *     This interface mirrors {@link net.luis.utils.io.database.query.SqlQueryProvider} but only
 *     provides query operations (no DDL), with all terminal operations returning
 *     {@link java.util.concurrent.CompletableFuture} for non-blocking execution.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlAsyncQueryProvider<T> {
	
	/**
	 * Creates an asynchronous select query for all columns of this table.<br>
	 * @return An async select query returning full entities
	 */
	@NonNull SqlAsyncSelectQuery<T> select();
	
	/**
	 * Creates an asynchronous select query for the specified expressions (columns, aggregates, functions).<br>
	 *
	 * @param expressions The expressions to select
	 * @return An async projection query returning the selected values
	 */
	@NonNull SqlAsyncSelectProjectionQuery<?> select(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Adds an entity to be inserted asynchronously.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @return An async insert query
	 */
	@NonNull SqlAsyncInsertQuery<T> insert(@NonNull T entity);
	
	/**
	 * Adds multiple entities to be inserted asynchronously.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return An async insert query
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlAsyncInsertQuery<T> insert(T @NonNull ... entities);
	
	/**
	 * Adds multiple entities to be inserted asynchronously.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return An async insert query
	 */
	@NonNull SqlAsyncInsertQuery<T> insert(@NonNull Collection<T> entities);
	
	/**
	 * Adds multiple entities to be inserted asynchronously in batches.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)} for each batch of entities.<br>
	 *
	 * @param entities The entities to insert
	 * @param batchSize The size of each batch
	 * @return An async insert query
	 */
	@NonNull SqlAsyncInsertQuery<T> insert(@NonNull Collection<T> entities, int batchSize);
	
	/**
	 * Adds an entity to be upserted asynchronously.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...) ON CONFLICT (column) DO UPDATE SET ...}.<br>
	 *
	 * @param entity The entity to upsert
	 * @param conflictColumn The column that may cause a conflict
	 * @param onConflict The function to apply on conflict
	 * @return An async insert query
	 */
	@NonNull SqlAsyncInsertQuery<T> upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);
	
	/**
	 * Adds an entity to be inserted asynchronously, ignoring conflicts.<br>
	 * Generates SQL: {@code INSERT OR IGNORE INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @param conflictColumns The columns that may cause a conflict
	 * @return An async insert query
	 */
	@NonNull SqlAsyncInsertQuery<T> insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);
	
	/**
	 * Creates an asynchronous update query builder for this table.<br>
	 * @return An async update query builder
	 */
	@NonNull SqlAsyncUpdateQuery<T> update();
	
	/**
	 * Creates an asynchronous delete query builder for this table.<br>
	 * @return An async delete query builder
	 */
	@NonNull SqlAsyncDeleteQuery<T> delete();
}
