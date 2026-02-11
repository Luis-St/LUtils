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

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Interface representing a SQL insert query.<br>
 *
 * @param <T> The type of the entity
 * @author Luis-St
 */
public interface SqlInsertQuery<T> {
	
	/**
	 * Adds an entity to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull T entity);
	
	/**
	 * Adds multiple entities to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return This insert query
	 */
	@SuppressWarnings("unchecked")
	@NonNull SqlInsertQuery<T> insert(T @NonNull ... entities);
	
	/**
	 * Adds an entity to be upserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...) ON CONFLICT (column) DO UPDATE SET ...}.<br>
	 *
	 * @param entity The entity to upsert
	 * @param conflictColumn The column that may cause a conflict
	 * @param onConflict The function to apply on conflict
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);
	
	/**
	 * Adds an entity to be inserted, ignoring conflicts.<br>
	 * Generates SQL: {@code INSERT OR IGNORE INTO table (...) VALUES (...)}.<br>
	 *
	 * @param entity The entity to insert
	 * @param conflictColumns The columns that may cause a conflict
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);
	
	/**
	 * Inserts data from a select query.<br>
	 * Generates SQL: {@code INSERT INTO table (...) SELECT ...}.<br>
	 *
	 * @param query The select query to insert from
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> fromSelect(@NonNull SqlSelectQuery<?> query);
	
	/**
	 * Executes the insert query.<br>
	 * @return The number of rows inserted
	 */
	int execute();
	
	/**
	 * Executes the insert query and returns the inserted entities.<br>
	 * Generates SQL: {@code INSERT INTO ... RETURNING *}.<br>
	 *
	 * @return The list of inserted entities
	 */
	@NonNull List<T> returning();
	
	/**
	 * Executes the insert query and fetches the inserted entities.<br>
	 * @return The list of inserted entities
	 */
	@NonNull List<T> fetchInserted();
	
	/**
	 * Asynchronously executes the insert query.<br>
	 * @return A future containing the number of rows inserted
	 */
	@NonNull CompletableFuture<Integer> executeAsync();
	
	/**
	 * Asynchronously executes the insert query and returns the inserted entities.<br>
	 * Generates SQL: {@code INSERT INTO ... RETURNING *}.<br>
	 *
	 * @return A future containing the list of inserted entities
	 */
	@NonNull CompletableFuture<List<T>> returningAsync();
	
	/**
	 * Asynchronously executes the insert query and fetches the inserted entities.<br>
	 * @return A future containing the list of inserted entities
	 */
	@NonNull CompletableFuture<List<T>> fetchInsertedAsync();
}
