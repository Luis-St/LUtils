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
 *
 * @author Luis-St
 *
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
	
	SqlDeleteQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(joins, "Sql join clauses must not be null");
	}
	
	@NonNull SqlDeleteQueryConfig<E> withJoins(@NonNull List<SqlJoinClause> joins) {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, joins, this.whereCondition, this.allowAll);
	}
	
	@NonNull SqlDeleteQueryConfig<E> withWhereCondition(@Nullable SqlCondition whereCondition) {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.joins, whereCondition, this.allowAll);
	}
	
	@NonNull SqlDeleteQueryConfig<E> withAllowAll() {
		return new SqlDeleteQueryConfig<>(this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.joins, this.whereCondition, true);
	}
}
