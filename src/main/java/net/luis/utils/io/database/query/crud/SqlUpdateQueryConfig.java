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
import net.luis.utils.io.database.query.util.SqlSetClause;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Holds the immutable state of a {@link SqlUpdateQuery}.<br>
 * Each builder operation on the query produces a new configuration via the {@code with...} methods.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities mapped from the affected rows
 * @param table The table to update rows in
 * @param dialect The sql dialect used to render the query
 * @param connectionSource The connection source used to execute the query
 * @param queryTimeout The timeout applied to the query execution
 * @param rowMapper The row mapper used to map result rows to entities
 * @param setClauses The set clauses describing the column assignments
 * @param joins The join clauses applied to the update query
 * @param whereCondition The condition restricting which rows are updated, or {@code null} for no condition
 * @param allowAll Whether updating all rows without a where condition is explicitly allowed
 */
record SqlUpdateQueryConfig<E>(
	@NonNull SqlTable<E> table,
	@NonNull SqlDialect dialect,
	@NonNull SqlConnectionSource connectionSource,
	@NonNull Duration queryTimeout,
	@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
	@NonNull List<SqlSetClause<E, ?>> setClauses,
	@NonNull List<SqlJoinClause> joins,
	@Nullable SqlCondition whereCondition,
	boolean allowAll
) {
	
	/**
	 * Constructs a new update query configuration with the given core components and default empty clauses.<br>
	 * The set clauses and joins are empty, the where condition is {@code null} and the allow-all flag is disabled.<br>
	 *
	 * @param table The table to update rows in
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the query execution
	 * @param rowMapper The row mapper used to map result rows to entities
	 */
	SqlUpdateQueryConfig(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper
	) {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), List.of(), null, false);
	}
	
	/**
	 * Constructs a new update query configuration validating the required components.<br>
	 * @throws NullPointerException If the table, dialect, connection source, query timeout, row mapper, set clauses or joins are null
	 */
	SqlUpdateQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(setClauses, "Sql set clauses must not be null");
		Objects.requireNonNull(joins, "Sql join clauses must not be null");
	}
	
	/**
	 * Creates a copy of this configuration with the given set clauses.<br>
	 *
	 * @param setClauses The set clauses describing the column assignments
	 * @return A new configuration with the updated set clauses
	 */
	@NonNull SqlUpdateQueryConfig<E> withSetClauses(@NonNull List<SqlSetClause<E, ?>> setClauses) {
		return new SqlUpdateQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, setClauses, this.joins, this.whereCondition, this.allowAll);
	}
	
	/**
	 * Creates a copy of this configuration with the given join clauses.<br>
	 *
	 * @param joins The join clauses to apply
	 * @return A new configuration with the updated joins
	 */
	@NonNull SqlUpdateQueryConfig<E> withJoins(@NonNull List<SqlJoinClause> joins) {
		return new SqlUpdateQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.setClauses, joins, this.whereCondition, this.allowAll);
	}
	
	/**
	 * Creates a copy of this configuration with the given where condition.<br>
	 *
	 * @param whereCondition The condition restricting which rows are updated, or {@code null} for no condition
	 * @return A new configuration with the updated where condition
	 */
	@NonNull SqlUpdateQueryConfig<E> withWhereCondition(@Nullable SqlCondition whereCondition) {
		return new SqlUpdateQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.setClauses, this.joins, whereCondition, this.allowAll);
	}
	
	/**
	 * Creates a copy of this configuration that allows updating all rows without a where condition.<br>
	 * @return A new configuration with the allow-all flag enabled
	 */
	@NonNull SqlUpdateQueryConfig<E> withAllowAll() {
		return new SqlUpdateQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.setClauses, this.joins, this.whereCondition, true);
	}
}
