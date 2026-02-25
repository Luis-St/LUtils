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

import net.luis.utils.io.database.query.SqlSelectQuery;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific select query.<br>
 * Extends {@link SqlSelectQuery} with PostgreSQL-specific clauses such as {@code DISTINCT ON}.<br>
 *
 * @see SqlSelectQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the result entity
 */
public interface PostgresSelectQuery<T> extends SqlSelectQuery<T> {
	
	/**
	 * Adds a {@code DISTINCT ON} clause to the query.<br>
	 * PostgreSQL-specific extension that selects distinct rows based on the specified columns, keeping only the first row for each distinct combination.<br>
	 * Generates SQL: {@code SELECT DISTINCT ON (col1, col2, ...) ...}.<br>
	 *
	 * @param columns The columns to apply distinct on
	 * @return This query for method chaining
	 */
	@NonNull PostgresSelectQuery<T> distinctOn(SqlColumn<?> @NonNull ... columns);
}
