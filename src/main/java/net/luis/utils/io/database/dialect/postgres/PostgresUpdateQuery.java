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

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.query.SqlUpdateQuery;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a PostgreSQL-specific update query.<br>
 * Extends {@link SqlUpdateQuery} with PostgreSQL-specific clauses such as {@code FROM}.<br>
 *
 * @see SqlUpdateQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface PostgresUpdateQuery<T> extends SqlUpdateQuery<T> {
	
	/**
	 * Adds a {@code FROM} clause to the update query.<br>
	 * Allows the update to reference columns from another table, which is a PostgreSQL-specific extension.<br>
	 * Generates SQL: {@code UPDATE ... SET ... FROM other_table WHERE ...}.<br>
	 *
	 * @param table The table to reference in the update
	 * @return This update query for method chaining
	 */
	@NonNull PostgresUpdateQuery<T> from(@NonNull SqlTable<?> table);
	
	@Override
	@NonNull PostgresUpdateQuery<T> innerJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@Override
	@NonNull PostgresUpdateQuery<T> leftJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@Override
	@NonNull PostgresUpdateQuery<T> rightJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@Override
	@NonNull PostgresUpdateQuery<T> fullJoin(@NonNull SqlTable<?> table, @NonNull SqlCondition on);
	
	@Override
	@NonNull PostgresUpdateQuery<T> batchSize(int batchSize);
}
