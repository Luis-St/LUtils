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

import net.luis.utils.io.database.SqlDatabase;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.function.scalar.SqlAgg;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.transaction.SqlTransaction;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.Function;

/**
 * Interface providing methods to create SQL queries for a specific entity type.<br>
 * Implemented by {@link SqlTable} to allow query creation directly from the table reference.<br>
 *
 * @author Luis-St
 */
public interface SqlQueryProvider<T> {
	
	/**
	 * Returns a view of this query provider bound to the specified database.<br>
	 *
	 * @param database The database to use
	 * @return A query provider reference bound to the database
	 */
	@NonNull SqlQueryProvider<T> from(@NonNull SqlDatabase database);
	
	/**
	 * Returns a view of this query provider bound to the specified transaction.<br>
	 *
	 * @param transaction The transaction to use
	 * @return A query provider reference bound to the transaction
	 */
	@NonNull SqlQueryProvider<T> withIn(@NonNull SqlTransaction transaction);
	
	/**
	 * Returns a scoped view of this table where automatic version checks are suppressed.<br>
	 * <p>
	 *     Entity-level update and delete on the returned view will not add automatic {@code WHERE version = ?} and {@code SET version = version + 1} clauses.<br>
	 *     Composable with {@link #from(SqlDatabase)} and {@link #withIn(SqlTransaction)}.
	 * </p>
	 *
	 * @return A table view with version checking disabled
	 */
	@NonNull SqlQueryProvider<T> skipVersionCheck();
	
	/**
	 * Returns a dialect-specific view of this table.<br>
	 *
	 * @param dialect The SQL dialect to use
	 * @param <TT> The dialect table type
	 * @param <CC> The dialect column type
	 * @return The dialect-specific table
	 */
	<TT, CC> @NonNull TT dialect(@NonNull SqlDialect<TT, CC> dialect);
	
	/**
	 * Creates a select query for all columns of this table.<br>
	 * @return A select query returning full entities
	 */
	@NonNull SqlSelectQuery<T> select();
	
	/**
	 * Creates a select query for the specified expressions (columns, aggregates, functions).<br>
	 * Supports columns, aliased columns via {@link SqlColumn#as(String)}, and SQL expressions like aggregates ({@link SqlAgg}).<br>
	 *
	 * @param expressions The expressions to select
	 * @return A projection query returning the selected values
	 */
	@NonNull SqlSelectProjectionQuery<?> select(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Creates a subquery selecting the specified expressions.<br>
	 * Subqueries can be used in {@code IN} conditions or {@code EXISTS} checks.<br>
	 *
	 * @param expressions The expressions to select
	 * @return A select query for use as a subquery
	 */
	@NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions);
	
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
	 * Adds multiple entities to be inserted.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)}.<br>
	 *
	 * @param entities The entities to insert
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities);
	
	/**
	 * Adds multiple entities to be inserted in batches.<br>
	 * Generates SQL: {@code INSERT INTO table (...) VALUES (...), (...)} for each batch of entities.<br>
	 *
	 * @param entities The entities to insert
	 * @param batchSize The size of each batch
	 * @return This insert query
	 */
	@NonNull SqlInsertQuery<T> insert(@NonNull Collection<T> entities, int batchSize);
	
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
	@NonNull SqlInsertQuery<T> insertFromSelect(@NonNull SqlSelectQuery<?> query);
	
	/**
	 * Creates an update query builder for this table.<br>
	 * @return An update query builder
	 */
	@NonNull SqlUpdateQuery<T> update();
	
	/**
	 * Creates a delete query builder for this table.<br>
	 * @return A delete query builder
	 */
	@NonNull SqlDeleteQuery<T> delete();
}
