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

package net.luis.utils.io.database.table;

import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlDatabaseException;
import net.luis.utils.io.database.function.SqlAgg;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.index.SqlIndexDefinition;
import net.luis.utils.io.database.index.SqlIndexInfo;
import net.luis.utils.io.database.query.*;
import net.luis.utils.io.database.sequence.SqlSequenceDefinition;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Function;

/**
 * Interface representing a SQL table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlTable<T> {
	
	/**
	 * Creates a new table reference for the specified table name and entity type.<br>
	 *
	 * @param name The table name
	 * @param type The entity class
	 * @param <T> The entity type
	 * @return A new table reference
	 */
	static <T> @NonNull SqlTable<T> of(@NonNull String name, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
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
	 * Returns a column reference for the specified column name and type.<br>
	 *
	 * @param name The column name
	 * @param type The column value type
	 * @param <C> The column type
	 * @return A column reference
	 */
	<C> @NonNull SqlColumn<C> column(@NonNull String name, @NonNull Class<C> type);
	
	/**
	 * Returns a foreign column reference for the specified column name and referenced table.<br>
	 *
	 * @param name The column name
	 * @param type The column value type
	 * @param referencedTable The referenced table
	 * @param <C> The column type
	 * @param <R> The referenced entity type
	 * @return A foreign column reference
	 */
	<C, R> @NonNull SqlForeignColumn<C, R> foreignColumn(@NonNull String name, @NonNull Class<C> type, @NonNull SqlTable<R> referencedTable);
	
	/**
	 * Creates a select query for all columns of this table.<br>
	 * @return A select query returning full entities
	 */
	@NonNull SqlSelectQuery<T> select();
	
	/**
	 * Creates a select query for the specified expressions (columns, aggregates, functions).<br>
	 * <p>
	 *     Supports columns, aliased columns via {@link SqlColumn#as(String)}, and SQL expressions like aggregates ({@link SqlAgg}).<br>
	 * </p>
	 *
	 * @param expressions The expressions to select
	 * @return A projection query returning the selected values
	 */
	@NonNull SqlSelectProjectionQuery<?> select(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Creates a subquery selecting the specified expressions.<br>
	 * <p>
	 *     Subqueries can be used in IN conditions or EXISTS checks.<br>
	 * </p>
	 * @param expressions The expressions to select
	 * @return A select query for use as a subquery
	 */
	@NonNull SqlSelectQuery<?> subquery(SqlExpression<?> @NonNull ... expressions);
	
	/**
	 * Creates an insert query builder for this table.<br>
	 * @return An insert query builder
	 */
	@NonNull SqlInsertQuery<T> insert();
	
	/**
	 * Inserts an entity into this table.<br>
	 *
	 * @param entity The entity to insert
	 * @return The inserted entity (with generated values populated)
	 */
	@NonNull T insert(@NonNull T entity);
	
	/**
	 * Inserts multiple entities into this table.<br>
	 *
	 * @param entities The entities to insert
	 * @return The inserted entities (with generated values populated)
	 */
	@SuppressWarnings("unchecked")
	@NonNull List<T> insert(T @NonNull ... entities);
	
	/**
	 * Inserts or updates an entity based on conflict detection.<br>
	 *
	 * @param entity The entity to insert or update
	 * @param conflictColumn The column to detect conflicts on
	 * @param onConflict Function to apply on conflict, receives existing entity
	 * @return The resulting entity
	 */
	@NonNull T upsert(@NonNull T entity, @NonNull SqlColumn<?> conflictColumn, @NonNull Function<T, T> onConflict);
	
	/**
	 * Inserts an entity, ignoring if a conflict occurs.<br>
	 *
	 * @param entity The entity to insert
	 * @param conflictColumns The columns to detect conflicts on
	 */
	void insertOrIgnore(@NonNull T entity, SqlColumn<?> @NonNull ... conflictColumns);
	
	/**
	 * Creates an update query builder for this table.<br>
	 * @return An update query builder
	 */
	@NonNull SqlUpdateQuery<T> update();
	
	/**
	 * Updates an entity in this table.<br>
	 * @param entity The entity to update
	 */
	void update(@NonNull T entity);
	
	/**
	 * Creates a delete query builder for this table.<br>
	 * @return A delete query builder
	 */
	@NonNull SqlDeleteQuery<T> delete();
	
	/**
	 * Deletes an entity from this table.<br>
	 * @param entity The entity to delete
	 */
	void delete(@NonNull T entity);
	
	/**
	 * Creates an index on the specified columns.<br>
	 *
	 * @param name The index name
	 * @param columns The columns to index
	 */
	void createIndex(@NonNull String name, SqlColumn<?> @NonNull ... columns);
	
	/**
	 * Creates an index using the specified definition.<br>
	 * @param definition The index definition
	 */
	void createIndex(@NonNull SqlIndexDefinition definition);
	
	/**
	 * Drops an index by name.<br>
	 * @param name The index name to drop
	 */
	void dropIndex(@NonNull String name);
	
	/**
	 * Lists all indexes on this table.<br>
	 * @return A list of index information
	 */
	@NonNull List<SqlIndexInfo> listIndexes();
	
	/**
	 * Creates a sequence using the specified definition.<br>
	 * @param definition The sequence definition
	 */
	void createSequence(@NonNull SqlSequenceDefinition definition);
	
	/**
	 * Returns the next value from the specified sequence.<br>
	 * @param name The sequence name
	 * @return The next sequence value
	 */
	long nextSequenceValue(@NonNull String name);
	
	/**
	 * Generates the CREATE TABLE SQL for this table.<br>
	 * @return The CREATE TABLE SQL string
	 */
	@NonNull String generateCreateSql();
	
	/**
	 * Generates the DROP TABLE SQL for this table.<br>
	 * @return The DROP TABLE SQL string
	 */
	@NonNull String generateDropSql();
	
	/**
	 * Creates this table in the database.<br>
	 * @throws SqlDatabaseException If the table already exists
	 */
	void create();
	
	/**
	 * Creates this table in the database if it does not exist.<br>
	 */
	void createIfNotExists();
	
	/**
	 * Drops this table from the database.<br>
	 * @throws SqlDatabaseException If the table does not exist
	 */
	void drop();
	
	/**
	 * Drops this table from the database if it exists.<br>
	 */
	void dropIfExists();
	
	/**
	 * Truncates (removes all rows from) this table.<br>
	 */
	void truncate();
	
	/**
	 * Checks if this table exists in the database.<br>
	 * @return True if the table exists, false otherwise
	 */
	boolean exists();
}
