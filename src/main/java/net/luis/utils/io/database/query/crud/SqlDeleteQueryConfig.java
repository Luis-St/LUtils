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

package net.luis.utils.io.database.query.crud;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.SqlConnectionSource;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.util.SqlJoinClause;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Holds the immutable state of a {@link SqlDeleteQuery}.<br>
 * Each builder operation on the query produces a new configuration via the {@code with...} methods.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities mapped from the affected rows
 * @param table The table to delete rows from
 * @param dialect The sql dialect used to render the query
 * @param connectionSource The connection source used to execute the query
 * @param queryTimeout The timeout applied to the query execution
 * @param rowMapper The row mapper used to map result rows to entities
 * @param joins The join clauses applied to the delete query
 * @param whereCondition The condition restricting which rows are deleted, or {@code null} for no condition
 * @param allowAll Whether deleting all rows without a where condition is explicitly allowed
 */
record SqlDeleteQueryConfig<E>(
	@NonNull SqlTable<E> table,
	@NonNull SqlDialect dialect,
	@NonNull SqlConnectionSource connectionSource,
	@NonNull Duration queryTimeout,
	@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
	@NonNull List<SqlJoinClause> joins,
	@Nullable SqlCondition whereCondition,
	boolean allowAll
) {
	
	/**
	 * Constructs a new delete query configuration validating the required components.<br>
	 * @throws NullPointerException If the table, dialect, connection source, query timeout, row mapper or joins are null
	 */
	SqlDeleteQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(joins, "Sql join clauses must not be null");
	}
	
	/**
	 * Creates a copy of this configuration with the given join clauses.<br>
	 * @param joins The join clauses to apply
	 * @return A new configuration with the updated joins
	 */
	@NonNull SqlDeleteQueryConfig<E> withJoins(@NonNull List<SqlJoinClause> joins) {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, joins, this.whereCondition, this.allowAll);
	}
	
	/**
	 * Creates a copy of this configuration with the given where condition.<br>
	 * @param whereCondition The condition restricting which rows are deleted, or {@code null} for no condition
	 * @return A new configuration with the updated where condition
	 */
	@NonNull SqlDeleteQueryConfig<E> withWhereCondition(@Nullable SqlCondition whereCondition) {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.joins, whereCondition, this.allowAll);
	}
	
	/**
	 * Creates a copy of this configuration that allows deleting all rows without a where condition.<br>
	 * @return A new configuration with the allow-all flag enabled
	 */
	@NonNull SqlDeleteQueryConfig<E> withAllowAll() {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.joins, this.whereCondition, true);
	}
}
