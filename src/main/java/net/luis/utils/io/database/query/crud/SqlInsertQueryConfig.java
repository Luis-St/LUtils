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
import net.luis.utils.io.database.query.util.SqlColumnValue;
import net.luis.utils.io.database.query.util.SqlSetClause;
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
 * @param conflictColumns The conflict columns used for upsert or insert-or-ignore queries, or {@code null}
 * @param upsertUpdateClauses The custom set clauses applied on conflict for upsert queries, or {@code null} to update every
 * non-conflict column with its proposed value
 * @param omittedColumns The columns to omit from the rendered value tuple in addition to auto-increment columns, which are
 * always omitted
 * @param overrides The columns whose value is computed from a custom expression instead of the entity getter
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
	@Nullable List<SqlColumn<E, ?>> conflictColumns,
	@Nullable List<SqlSetClause<E, ?>> upsertUpdateClauses,
	@NonNull List<SqlColumn<E, ?>> omittedColumns,
	@NonNull List<SqlColumnValue<E, ?>> overrides,
	boolean isUpsert,
	boolean isInsertOrIgnore,
	boolean isInsertFromSelect,
	@Nullable SqlAuditUserProvider auditUserProvider
) {
	
	/**
	 * Constructs a new insert query configuration validating the required components.<br>
	 *
	 * @throws NullPointerException If the table, dialect, connection source, query timeout, row mapper, entities, omitted
	 * columns or overrides are null
	 * @throws IllegalArgumentException If the entities list is empty and this is not an insert-from-select query
	 */
	SqlInsertQueryConfig {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(connectionSource, "Sql connection source must not be null");
		Objects.requireNonNull(queryTimeout, "Query timeout must not be null");
		Objects.requireNonNull(rowMapper, "Row mapper must not be null");
		Objects.requireNonNull(entities, "Entities must not be null");
		Objects.requireNonNull(omittedColumns, "Sql omitted columns must not be null");
		Objects.requireNonNull(overrides, "Sql overrides must not be null");
		
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
	 * @param conflictColumns The conflict columns used for upsert or insert-or-ignore queries, or {@code null}
	 * @param upsertUpdateClauses The custom set clauses applied on conflict for upsert queries, or {@code null} to update every
	 * non-conflict column with its proposed value
	 * @param isUpsert Whether this is an upsert query
	 * @param isInsertOrIgnore Whether this is an insert-or-ignore query
	 * @param isInsertFromSelect Whether this is an insert-from-select query
	 * @param auditUserProvider The provider supplying the current user for audit columns, or {@code null}
	 * @return The created insert query configuration
	 * @param <E> The type of the entities to insert
	 * @throws SqlStatementBuilderException If upsert and insert-or-ignore are combined, an upsert or insert-or-ignore is missing
	 * its conflict columns, or an insert-from-select is missing its source query
	 */
	static <E> @NonNull SqlInsertQueryConfig<E> create(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@Nullable SqlSelectQuery<?> fromSelect,
		@Nullable List<SqlColumn<E, ?>> conflictColumns,
		@Nullable List<SqlSetClause<E, ?>> upsertUpdateClauses,
		boolean isUpsert,
		boolean isInsertOrIgnore,
		boolean isInsertFromSelect,
		@Nullable SqlAuditUserProvider auditUserProvider
	) throws SqlStatementBuilderException {
		if (isUpsert && isInsertOrIgnore) {
			throw new SqlStatementBuilderException("Upsert and insert or ignore are mutually exclusive");
		}
		
		if (isUpsert || isInsertOrIgnore) {
			if (conflictColumns == null || conflictColumns.isEmpty()) {
				throw new SqlStatementBuilderException("Conflict columns must be specified for upsert and insert or ignore queries");
			}
		}
		
		if (isInsertFromSelect && fromSelect == null) {
			throw new SqlStatementBuilderException("From select query must be specified for insert from select queries");
		}
		
		return new SqlInsertQueryConfig<>(
			table, dialect, connectionSource, queryTimeout, rowMapper, entities, fromSelect, conflictColumns, upsertUpdateClauses,
			List.of(), List.of(), isUpsert, isInsertOrIgnore, isInsertFromSelect, auditUserProvider
		);
	}
	
	/**
	 * Creates a copy of this configuration with the given omitted columns.<br>
	 *
	 * @param omittedColumns The columns to omit from the rendered value tuple
	 * @return The copied configuration
	 * @throws NullPointerException If the omitted columns is null
	 */
	@NonNull SqlInsertQueryConfig<E> withOmittedColumns(@NonNull List<SqlColumn<E, ?>> omittedColumns) {
		Objects.requireNonNull(omittedColumns, "Sql omitted columns must not be null");
		return new SqlInsertQueryConfig<>(
			this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.entities, this.fromSelect, this.conflictColumns, this.upsertUpdateClauses,
			omittedColumns, this.overrides, this.isUpsert, this.isInsertOrIgnore, this.isInsertFromSelect, this.auditUserProvider
		);
	}
	
	/**
	 * Creates a copy of this configuration with the given column value overrides.<br>
	 *
	 * @param overrides The columns whose value is computed from a custom expression instead of the entity getter
	 * @return The copied configuration
	 * @throws NullPointerException If the overrides is null
	 */
	@NonNull SqlInsertQueryConfig<E> withOverrides(@NonNull List<SqlColumnValue<E, ?>> overrides) {
		Objects.requireNonNull(overrides, "Sql overrides must not be null");
		return new SqlInsertQueryConfig<>(
			this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.entities, this.fromSelect, this.conflictColumns, this.upsertUpdateClauses,
			this.omittedColumns, overrides, this.isUpsert, this.isInsertOrIgnore, this.isInsertFromSelect, this.auditUserProvider
		);
	}
	
	/**
	 * Creates a copy of this configuration with the given audit user provider.<br>
	 *
	 * @param auditUserProvider The provider supplying the current user for audit columns, or {@code null} for no audit user
	 * @return The copied configuration
	 */
	@NonNull SqlInsertQueryConfig<E> withAuditUserProvider(@Nullable SqlAuditUserProvider auditUserProvider) {
		return new SqlInsertQueryConfig<>(
			this.table, this.dialect, this.connectionSource, this.queryTimeout, this.rowMapper, this.entities, this.fromSelect, this.conflictColumns, this.upsertUpdateClauses,
			this.omittedColumns, this.overrides, this.isUpsert, this.isInsertOrIgnore, this.isInsertFromSelect, auditUserProvider
		);
	}
}
