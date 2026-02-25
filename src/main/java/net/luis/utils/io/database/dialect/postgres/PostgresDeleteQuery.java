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

import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.SqlDeleteQuery;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a PostgreSQL-specific delete query.<br>
 * Extends {@link SqlDeleteQuery} with PostgreSQL-specific clauses such as {@code USING}.<br>
 *
 * @see SqlDeleteQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresDeleteQuery<T> extends SqlDeleteQuery<T> {
	
	/**
	 * Adds a {@code USING} clause to the delete query.<br>
	 * Allows the delete to reference columns from another table, which is a PostgreSQL-specific extension.<br>
	 * Generates SQL: {@code DELETE FROM ... USING other_table WHERE ...}.<br>
	 *
	 * @param table The table to reference in the delete
	 * @return This delete query for method chaining
	 */
	@NonNull PostgresDeleteQuery<T> using(@NonNull SqlTable<?> table);
	
	/**
	 * Executes the delete query and returns the deleted entities.<br>
	 * Generates SQL: {@code DELETE FROM table WHERE ... RETURNING *}.<br>
	 *
	 * @return The list of deleted entities
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> returning() throws SqlException;
}
