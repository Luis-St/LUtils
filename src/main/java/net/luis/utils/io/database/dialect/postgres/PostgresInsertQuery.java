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

import net.luis.utils.io.database.query.SqlInsertQuery;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific insert query.<br>
 * Extends {@link SqlInsertQuery} with PostgreSQL-specific conflict handling clauses.<br>
 *
 * @see SqlInsertQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresInsertQuery<T> extends SqlInsertQuery<T> {

	/**
	 * Adds an {@code ON CONFLICT DO NOTHING} clause to the insert query.<br>
	 * Rows that would cause a conflict are silently skipped.<br>
	 * Generates SQL: {@code INSERT INTO ... ON CONFLICT DO NOTHING}.<br>
	 *
	 * @return This insert query for method chaining
	 */
	@NonNull PostgresInsertQuery<T> onConflictDoNothing();

	/**
	 * Adds an {@code ON CONFLICT} clause targeting the specified columns.<br>
	 * Used together with an update action to implement upsert behavior.<br>
	 * Generates SQL: {@code INSERT INTO ... ON CONFLICT (col1, col2, ...) ...}.<br>
	 *
	 * @param columns The conflict target columns
	 * @return This insert query for method chaining
	 */
	@NonNull PostgresInsertQuery<T> onConflict(SqlColumn<?> @NonNull ... columns);
}
