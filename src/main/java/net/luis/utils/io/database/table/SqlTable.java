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

import net.luis.utils.io.database.listener.SqlEntityListener;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a SQL table definition.<br>
 * <p>
 *     A table is a pure schema definition. Use {@link net.luis.utils.io.database.SqlDatabase#from(SqlTable)}
 *     or {@link net.luis.utils.io.database.transaction.SqlTransaction#from(SqlTable)} to create queries.<br>
 * </p>
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
	 * Returns the name of this table.<br>
	 * @return The table name
	 */
	@NonNull String getName();

	/**
	 * Returns the entity type of this table.<br>
	 * @return The entity class
	 */
	@NonNull Class<T> getType();

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
	 * Returns a creation audit column reference for the specified column name and type.<br>
	 * Creation columns are automatically filled on INSERT from the audit context.<br>
	 *
	 * @param name The column name
	 * @param type The column value type
	 * @param <V> The column type (e.g., {@link java.time.Instant} or {@link String})
	 * @return A creation column reference
	 */
	<V> @NonNull SqlCreationColumn<T, V> creationColumn(@NonNull String name, @NonNull Class<V> type);

	/**
	 * Returns an update audit column reference for the specified column name and type.<br>
	 * Update columns are automatically filled on INSERT and UPDATE from the audit context.<br>
	 *
	 * @param name The column name
	 * @param type The column value type
	 * @param <V> The column type (e.g., {@link java.time.Instant} or {@link String})
	 * @return An update column reference
	 */
	<V> @NonNull SqlUpdateColumn<T, V> updateColumn(@NonNull String name, @NonNull Class<V> type);

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
