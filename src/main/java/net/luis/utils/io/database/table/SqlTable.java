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
import net.luis.utils.io.database.index.SqlIndexDefinition;
import net.luis.utils.io.database.index.SqlIndexInfo;
import net.luis.utils.io.database.listener.SqlEntityListener;
import net.luis.utils.io.database.query.SqlQueryProvider;
import net.luis.utils.io.database.sequence.SqlSequenceDefinition;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL table.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlTable<T> extends SqlQueryProvider<T> {
	
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
	 * Gets the SQL dialect associated with this table.<br>
	 * @return The SQL dialect
	 */
	@NonNull SqlDialect<?, ?> getDialect();
	
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
	 * Returns a version column reference for the specified column name and type.<br>
	 * Version columns are recognized by query builders to apply automatic optimistic locking checks during updates and deletes.
	 *
	 * @param name The column name
	 * @param type The version value type
	 * @param <V> The version type (e.g., {@link Long} or {@link Integer})
	 * @return A version column reference
	 */
	<V> @NonNull SqlVersionColumn<T, V> versionColumn(@NonNull String name, @NonNull Class<V> type);
	
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
	 *
	 * @param name The sequence name
	 * @return The next sequence value
	 */
	long nextSequenceValue(@NonNull String name);
	
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
	
	/**
	 * Adds an entity lifecycle listener to this table.<br>
	 * @param listener The listener to add
	 */
	void addListener(@NonNull SqlEntityListener<T> listener);
	
	/**
	 * Removes an entity lifecycle listener from this table.<br>
	 * @param listener The listener to remove
	 */
	void removeListener(@NonNull SqlEntityListener<T> listener);
}
