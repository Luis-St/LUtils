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

package net.luis.utils.io.database.dialect.postgres;

import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific table definition.<br>
 * Extends {@link SqlTable} with additional column types that are only available in PostgreSQL.<br>
 *
 * @see SqlTable
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresTable<T> extends SqlTable<T> {

	/**
	 * Creates a new PostgreSQL table reference for the specified table name and entity type.<br>
	 *
	 * @param name The table name
	 * @param type The entity class
	 * @param <T> The entity type
	 * @return A new PostgreSQL table reference
	 */
	static <T> @NonNull PostgresTable<T> of(@NonNull String name, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}

	@Override
	<C> @NonNull PostgresColumn<C> column(@NonNull String name, @NonNull Class<C> type);

	/**
	 * Returns a JSONB column reference for the specified column name.<br>
	 * JSONB is a PostgreSQL-specific binary JSON type that supports indexing and querying.<br>
	 *
	 * @param name The column name
	 * @param <C> The column type
	 * @return A PostgreSQL column reference for the JSONB column
	 */
	<C> @NonNull PostgresColumn<C> jsonbColumn(@NonNull String name);

	/**
	 * Returns an array column reference for the specified column name and element type.<br>
	 * Arrays are a PostgreSQL-specific column type that stores a list of values.<br>
	 *
	 * @param name The column name
	 * @param elementType The element type of the array
	 * @param <C> The element type
	 * @return A PostgreSQL column reference for the array column
	 */
	<C> @NonNull PostgresColumn<C> arrayColumn(@NonNull String name, @NonNull Class<C> elementType);
}
