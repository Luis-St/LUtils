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

package net.luis.utils.io.database.mapping;

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Interface representing a single row from a SQL result set with type-safe column access.<br>
 * <p>
 *     Unlike raw {@code Object[]} access,<br>
 *     this abstraction provides typed accessto column values using {@link SqlColumn} references,<br>
 *     ensuring compile-time type safety when reading query results.
 * </p>
 *
 * @author Luis-St
 */
public interface SqlResultRow {
	
	/**
	 * Returns the value of the specified column.<br>
	 *
	 * @param column The column to read
	 * @param <T> The type of the column value
	 * @return The column value, or {@code null} if the column is null
	 */
	<T> @Nullable T get(@NonNull SqlColumn<T> column);
	
	/**
	 * Returns the value of the column at the specified index.<br>
	 *
	 * @param index The column index (0-based)
	 * @param type The expected type of the column value
	 * @param <T> The type of the column value
	 * @return The column value, or {@code null} if the column is null
	 */
	<T> @Nullable T get(int index, @NonNull Class<T> type);
	
	/**
	 * Returns the value of the specified column by name.<br>
	 *
	 * @param columnName The column name
	 * @param type The expected type of the column value
	 * @param <T> The type of the column value
	 * @return The column value, or {@code null} if the column is null
	 */
	<T> @Nullable T get(@NonNull String columnName, @NonNull Class<T> type);
	
	/**
	 * Returns the number of columns in this row.<br>
	 * @return The column count
	 */
	int columnCount();
}

