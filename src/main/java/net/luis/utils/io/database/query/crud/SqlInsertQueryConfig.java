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
import net.luis.utils.io.database.audit.SqlAuditUserProvider;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Holds the immutable state of a {@link SqlInsertQuery}.<br>
 * Supports plain inserts as well as upsert, insert-or-ignore and insert-from-select variants.<br>
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities to insert
 * @param table The table to insert rows into
 * @param dialect The sql dialect used to render the query
 * @param connectionSource The connection source used to execute the query
 * @param queryTimeout The timeout applied to the query execution
 * @param rowMapper The row mapper used to map result rows to entities
 * @param entities The entities to insert
 * @param fromSelect The select query providing the rows for an insert-from-select query, or {@code null}
 * @param conflictColumn The conflict column used for upsert queries, or {@code null}
 * @param conflictColumns The conflict columns used for insert-or-ignore queries, or {@code null}
 * @param isUpsert Whether this is an upsert query
 * @param isInsertOrIgnore Whether this is an insert-or-ignore query
 * @param isInsertFromSelect Whether this is an insert-from-select query
 * @param auditUserProvider The provider supplying the current user for audit columns, or {@code null}
 */
record SqlInsertQueryConfig<E>(
	@NonNull SqlTable<E> table,
	@NonNull SqlDialect dialect,
	@NonNull SqlConnectionSource connectionSource,
	@NonNull Duration queryTimeout,
	@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
	@NonNull List<E> entities,
	@Nullable SqlSelectQuery<?> fromSelect,
	@Nullable SqlColumn<E, ?> conflictColumn,
	@Nullable List<SqlColumn<E, ?>> conflictColumns,
	boolean isUpsert,
	boolean isInsertOrIgnore,
	boolean isInsertFromSelect,
	@Nullable SqlAuditUserProvider auditUserProvider
) {
	
	/**
	 * Constructs a new insert query configuration validating the required components.<br>
	 *
	 * @throws NullPointerException If the table, dialect, connection source, query timeout, row mapper or entities are null
	 * @throws IllegalArgumentException If the entities list is empty and this is not an insert-from-select query
	 */
	SqlInsertQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(entities, "Entities must not be null");
		
		if (entities.isEmpty() && !isInsertFromSelect) {
			throw new IllegalArgumentException("Entities list must not be empty for insert queries");
		}
	}
	
	/**
	 * Creates a new insert query configuration validating the consistency of the insert variant flags.<br>
	 *
	 * @param table The table to insert rows into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the query execution
	 * @param rowMapper The row mapper used to map result rows to entities
	 * @param entities The entities to insert
	 * @param fromSelect The select query providing the rows for an insert-from-select query, or {@code null}
	 * @param conflictColumn The conflict column used for upsert queries, or {@code null}
	 * @param conflictColumns The conflict columns used for insert-or-ignore queries, or {@code null}
	 * @param isUpsert Whether this is an upsert query
	 * @param isInsertOrIgnore Whether this is an insert-or-ignore query
	 * @param isInsertFromSelect Whether this is an insert-from-select query
	 * @param auditUserProvider The provider supplying the current user for audit columns, or {@code null}
	 * @return The created insert query configuration
	 * @param <E> The type of the entities to insert
	 * @throws SqlStatementBuilderException If upsert and insert-or-ignore are combined, an upsert is missing its conflict column,
	 * an insert-or-ignore is missing its conflict columns, or an insert-from-select is missing its source query
	 */
	static <E> @NonNull SqlInsertQueryConfig<E> create(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@Nullable SqlSelectQuery<?> fromSelect,
		@Nullable SqlColumn<E, ?> conflictColumn,
		@Nullable List<SqlColumn<E, ?>> conflictColumns,
		boolean isUpsert,
		boolean isInsertOrIgnore,
		boolean isInsertFromSelect,
		@Nullable SqlAuditUserProvider auditUserProvider
	) throws SqlStatementBuilderException {
		if (isUpsert && isInsertOrIgnore) {
			throw new SqlStatementBuilderException("Upsert and insert or ignore are mutually exclusive");
		}
		
		if (isUpsert && conflictColumn == null) {
			throw new SqlStatementBuilderException("Conflict column must be specified for upsert queries");
		}
		
		if (isInsertOrIgnore) {
			if (conflictColumns == null || conflictColumns.isEmpty()) {
				throw new SqlStatementBuilderException("Conflict columns must be specified for insert or ignore queries");
			}
		}
		
		if (isInsertFromSelect && fromSelect == null) {
			throw new SqlStatementBuilderException("From select query must be specified for insert from select queries");
		}
		
		return new SqlInsertQueryConfig<>(table, dialect, connectionSource, queryTimeout, rowMapper, entities, fromSelect, conflictColumn, conflictColumns, isUpsert, isInsertOrIgnore, isInsertFromSelect, auditUserProvider);
	}
}
