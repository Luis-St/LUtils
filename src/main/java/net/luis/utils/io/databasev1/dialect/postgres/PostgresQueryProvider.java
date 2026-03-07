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

package net.luis.utils.io.databasev1.dialect.postgres;

import net.luis.utils.io.databasev1.SqlDatabase;
import net.luis.utils.io.databasev1.exception.SqlException;
import net.luis.utils.io.databasev1.query.SqlQueryProvider;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/**
 * Interface providing PostgreSQL-specific query operations for a specific entity type.<br>
 * Extends {@link SqlQueryProvider} with narrowed return types and additional operations
 * such as {@link #truncateCascade()}.<br>
 *
 * @see SqlQueryProvider
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresQueryProvider<T> extends SqlQueryProvider<T> {
	
	/**
	 * Creates a new PostgreSQL query provider for the given database and table.<br>
	 *
	 * @param db The database connection
	 * @param table The PostgreSQL table
	 * @param <T> The entity type
	 * @return A new PostgreSQL query provider
	 */
	static <T> @NonNull PostgresQueryProvider<T> from(@NonNull SqlDatabase db, @NonNull PostgresTable<T> table) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	@NonNull PostgresQueryProvider<T> skipVersionCheck();
	
	@Override
	@NonNull PostgresSelectQuery<T> select();
	
	@Override
	@NonNull PostgresInsertQuery<T> insert(@NonNull T entity);
	
	@SuppressWarnings("unchecked")
	@Override
	@NonNull PostgresInsertQuery<T> insert(T @NonNull ... entities);
	
	@Override
	@NonNull PostgresInsertQuery<T> insert(@NonNull Collection<T> entities);
	
	@Override
	@NonNull PostgresInsertQuery<T> insert(@NonNull Collection<T> entities, int batchSize);
	
	@Override
	@NonNull PostgresUpdateQuery<T> update();
	
	@Override
	@NonNull PostgresDeleteQuery<T> delete();
	
	/**
	 * Truncates (removes all rows from) this table with cascade.<br>
	 * Also truncates all tables that have foreign key references to this table.<br>
	 * Generates SQL: {@code TRUNCATE TABLE table CASCADE}.<br>
	 *
	 * @throws SqlException If a database access error occurs
	 */
	void truncateCascade() throws SqlException;
}
