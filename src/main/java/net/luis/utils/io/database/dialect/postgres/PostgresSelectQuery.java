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
 * Interface representing a PostgreSQL-specific select query with DISTINCT ON support.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the result entity
 */
public interface PostgresSelectQuery<T> extends SqlSelectQuery<T> {
	
	/**
	 * Applies {@code DISTINCT ON} to the select query using the specified columns.<br>
	 * Generates SQL: {@code SELECT DISTINCT ON (column1, column2, ...) ...}.<br>
	 * <p>
	 *     Unlike standard {@code DISTINCT}, PostgreSQL's {@code DISTINCT ON} keeps only the first row<br>
	 *     for each unique combination of the specified columns. The result depends on the {@code ORDER BY} clause.<br>
	 * </p>
	 *
	 * @param columns The columns to apply DISTINCT ON to
	 * @return This query for method chaining
	 */
	@NonNull PostgresSelectQuery<T> distinctOn(SqlColumn<?> @NonNull ... columns);
}
