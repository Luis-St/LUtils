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

package net.luis.utils.io.databasev1.dialect.postgres;

import net.luis.utils.io.databasev1.query.SqlInsertQuery;
import net.luis.utils.io.databasev1.query.SqlUpdateQuery;
import net.luis.utils.io.databasev1.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

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
	 * Adds an {@code ON CONFLICT DO NOTHING} clause without targeting specific columns.<br>
	 * Any conflict causes the insert to be silently skipped.<br>
	 * Generates SQL: {@code INSERT INTO ... ON CONFLICT DO NOTHING}.<br>
	 *
	 * @return This insert query for method chaining
	 */
	@NonNull PostgresInsertQuery<T> onConflictDoNothing();
	
	/**
	 * Adds an {@code ON CONFLICT (columns) DO NOTHING} clause targeting specific columns.<br>
	 * A conflict on the specified columns causes the insert to be silently skipped.<br>
	 * Generates SQL: {@code INSERT INTO ... ON CONFLICT (col1, col2, ...) DO NOTHING}.<br>
	 *
	 * @param columns The conflict target columns
	 * @return This insert query for method chaining
	 */
	@NonNull PostgresInsertQuery<T> onConflictDoNothing(SqlColumn<?> @NonNull ... columns);
	
	/**
	 * Adds an {@code ON CONFLICT (columns) DO UPDATE SET} clause targeting specific columns.<br>
	 * A conflict on the specified columns triggers the update defined by the consumer.<br>
	 * Generates SQL: {@code INSERT INTO ... ON CONFLICT (col1, col2, ...) DO UPDATE SET ...}.<br>
	 *
	 * @param update A consumer that configures the columns to update on conflict
	 * @param columns The conflict target columns
	 * @return This insert query for method chaining
	 */
	@NonNull PostgresInsertQuery<T> onConflictDoUpdate(@NonNull Consumer<SqlUpdateQuery<T>> update, SqlColumn<?> @NonNull ... columns);
}
