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
import net.luis.utils.io.database.audit.*;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.dialect.SqlFeature;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.SqlStatementBuilderException;
import net.luis.utils.io.database.function.functions.temporal.SqlCurrentTimestampFunction;
import net.luis.utils.io.database.query.SqlQuery;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A builder for sql {@code INSERT} statements that can be executed against a database.<br>
 * Supports plain inserts, insert-or-ignore, upsert and insert-from-select via dedicated static factory methods.<br>
 * Large batches are automatically split into multiple statements based on the bind parameter limit of the dialect.<br>
 * If the target table is audited, the configured audit columns are populated automatically on insert.<br>
 *
 * @see SqlQuery
 * @see SqlInsertQueryConfig
 *
 * @author Luis-St
 *
 * @param <E> The type of the entities to insert
 */
public class SqlInsertQuery<E> implements SqlQuery<E> {
	
	/**
	 * The immutable configuration that holds the entities and settings of this insert query.
	 */
	private final SqlInsertQueryConfig<E> config;
	
	/**
	 * Constructs a new insert query for the given entities without an audit user provider.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 */
	public SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities
	) throws SqlStatementBuilderException {
		this(table, dialect, connectionSource, queryTimeout, rowMapper, entities, null);
	}
	
	/**
	 * Constructs a new insert query for the given entities using the given audit user provider.<br>
	 * The audit user provider supplies the user recorded in the audit columns of an audited table.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @param auditUserProvider The provider that resolves the audit user, or {@code null} for no audit user
	 * @throws NullPointerException If any argument except the audit user provider is null
	 * @throws IllegalArgumentException If the entities list is empty
	 */
	public SqlInsertQuery(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@Nullable SqlAuditUserProvider auditUserProvider
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "Entities must not be null");
		this(SqlInsertQueryConfig.create(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, null, null, false, false, false, auditUserProvider));
	}
	
	/**
	 * Constructs a new insert query that wraps the given configuration.<br>
	 *
	 * @param config The configuration that holds the entities and settings of the query
	 * @throws NullPointerException If the configuration is null
	 */
	SqlInsertQuery(@NonNull SqlInsertQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql insert query config must not be null");
	}
	
	/**
	 * Creates a new insert query that ignores rows conflicting on the given columns instead of failing.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert
	 * @param conflictColumns The columns that define a conflict to be ignored
	 * @param <E> The type of the entities to insert
	 * @return The newly created insert-or-ignore query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 * @throws SqlStatementBuilderException If the conflict columns are empty
	 */
	public static <E> @NonNull SqlInsertQuery<E> insertOrIgnore(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull List<SqlColumn<E, ?>> conflictColumns
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		Objects.requireNonNull(conflictColumns, "Sql conflict columns must not be null");
		
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, null, List.copyOf(conflictColumns), false, true, false, null));
	}
	
	/**
	 * Creates a new upsert query that updates the existing row instead of failing on a conflict on the given column.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param entities The entities to insert or update
	 * @param conflictColumn The column that defines a conflict triggering the update
	 * @param <E> The type of the entities to insert
	 * @return The newly created upsert query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws IllegalArgumentException If the entities list is empty
	 */
	public static <E> @NonNull SqlInsertQuery<E> upsert(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull List<E> entities,
		@NonNull SqlColumn<E, ?> conflictColumn
	) throws SqlStatementBuilderException {
		Objects.requireNonNull(entities, "List of entities must not be null");
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(table, dialect, connectionSource, queryTimeout, rowMapper, List.copyOf(entities), null, conflictColumn, null, true, false, false, null));
	}
	
	/**
	 * Creates a new insert query that inserts the rows produced by the given select query.<br>
	 * No entities are required since the inserted rows are derived from the result of the select query.<br>
	 *
	 * @param table The table to insert into
	 * @param dialect The sql dialect used to render the query
	 * @param connectionSource The connection source used to execute the query
	 * @param queryTimeout The timeout applied to the executed query
	 * @param rowMapper The mapper that converts a result set row into an entity
	 * @param fromSelect The select query that produces the rows to insert
	 * @param <E> The type of the entities to insert
	 * @return The newly created insert-from-select query
	 * @throws NullPointerException If any of the arguments is null
	 * @throws SqlStatementBuilderException If the select query is null
	 */
	public static <E> @NonNull SqlInsertQuery<E> insertFromSelect(
		@NonNull SqlTable<E> table,
		@NonNull SqlDialect dialect,
		@NonNull SqlConnectionSource connectionSource,
		@NonNull Duration queryTimeout,
		@NonNull ThrowableFunction<ResultSet, E, SqlException> rowMapper,
		@NonNull SqlSelectQuery<?> fromSelect
	) throws SqlStatementBuilderException {
		return new SqlInsertQuery<>(SqlInsertQueryConfig.create(table, dialect, connectionSource, queryTimeout, rowMapper, List.of(), fromSelect, null, null, false, false, true, null));
	}
	
	/**
	 * Adds the given value as a typed bind parameter to the given renderer.<br>
	 * The value is cast to the type expected by the given sql type.<br>
	 *
	 * @param renderer The renderer to add the parameter to
	 * @param type The sql type of the parameter
	 * @param value The value of the parameter, may be {@code null}
	 * @param <T> The java type associated with the sql type
	 * @throws NullPointerException If the renderer is null
	 */
	@SuppressWarnings("unchecked")
	private static <T> void addParameter(@NonNull SqlRenderer renderer, @NonNull SqlType<T> type, @Nullable Object value) {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		
		renderer.parameter(type, (T) value);
	}
	
	/**
	 * Executes this insert query and returns the number of affected rows.<br>
	 * If the entities exceed the bind parameter limit of the dialect, the insert is executed as a batched update.<br>
	 *
	 * @return The number of affected rows
	 * @throws SqlException If an error occurs while executing the query
	 */
	public int execute() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeUpdate(this.config.dialect(), this.config.connectionSource(), chunks.getFirst(), this.config.queryTimeout());
		}
		return SqlQueryExecutor.executeBatchedUpdate(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
	/**
	 * Executes this insert query and returns the generated keys of the inserted rows.<br>
	 *
	 * @return The list of generated keys
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<Long> executeReturningKeys() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		return SqlQueryExecutor.executeUpdateReturningKeys(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
	/**
	 * Executes this insert query and returns the inserted rows mapped back into entities.<br>
	 * All columns of the target table are returned and mapped using the configured row mapper.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If an error occurs while executing the query
	 */
	public @NonNull List<E> returning() throws SqlException {
		SqlDialect dialect = this.config.dialect();
		List<SqlRendered> chunks = this.renderChunks(dialect);
		SqlRendered returning = dialect.renderReturning(List.copyOf(this.config.table().columns()));
		
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeReturningQuery(dialect, this.config.connectionSource(), chunks.getFirst(), returning, this.config.queryTimeout(), this.config.rowMapper());
		}
		return SqlQueryExecutor.executeBatchedReturningQuery(dialect, this.config.connectionSource(), chunks, returning, this.config.queryTimeout(), this.config.rowMapper());
	}
	
	@Override
	public @NonNull SqlRendered toSql(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		return this.renderInsert(dialect, this.config.entities());
	}
	
	/**
	 * Renders this insert query into one or more sql statements.<br>
	 * The entities are split into multiple chunks if their combined bind parameters exceed the limit of the dialect.<br>
	 *
	 * @param dialect The sql dialect used to render the query
	 * @return The list of rendered statements
	 * @throws NullPointerException If the dialect is null
	 * @throws SqlException If an error occurs while rendering the query
	 */
	private @NonNull List<SqlRendered> renderChunks(@NonNull SqlDialect dialect) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		if (this.config.isInsertFromSelect()) {
			return List.of(this.renderInsert(dialect, this.config.entities()));
		}
		
		int paramsPerRow = this.config.table().columns().size();
		SqlAuditConfig auditConfig = this.config.table().auditConfig().orElse(null);
		if (auditConfig != null) {
			paramsPerRow += auditConfig.auditColumns().size();
		}
		
		int maxRows = Math.max(1, dialect.maxBindParameters() / Math.max(1, paramsPerRow));
		if (this.config.entities().size() <= maxRows) {
			return List.of(this.renderInsert(dialect, this.config.entities()));
		}
		
		List<SqlRendered> chunks = new ArrayList<>();
		for (int start = 0; start < this.config.entities().size(); start += maxRows) {
			int end = Math.min(start + maxRows, this.config.entities().size());
			chunks.add(this.renderInsert(dialect, this.config.entities().subList(start, end)));
		}
		return chunks;
	}
	
	/**
	 * Renders a single {@code INSERT} statement for the given rows.<br>
	 * Handles the column list, the value tuples or the embedded select query as well as the upsert and insert-or-ignore clauses depending on the configuration.<br>
	 *
	 * @param dialect The sql dialect used to render the query
	 * @param rows The rows to render the value tuples for
	 * @return The rendered insert statement
	 * @throws NullPointerException If the dialect or rows is null
	 * @throws SqlException If an error occurs while rendering the query
	 */
	@SuppressWarnings("unchecked")
	private @NonNull SqlRendered renderInsert(@NonNull SqlDialect dialect, @NonNull List<E> rows) throws SqlException {
		Objects.requireNonNull(dialect, "Sql dialect must not be null");
		Objects.requireNonNull(rows, "Sql rows must not be null");
		
		SqlTable<E> table = this.config.table();
		if (this.config.isUpsert() && !dialect.isFeatureSupported(SqlFeature.UPSERT_SUFFIX)) {
			SqlColumn<E, ?> conflictColumn = Objects.requireNonNull(this.config.conflictColumn(), "Conflict column must not be null");
			
			SqlRenderer valueTuples = SqlRenderer.empty();
			for (int e = 0; e < rows.size(); e++) {
				if (e > 0) {
					valueTuples.comma();
				}
				this.renderValueTuple(valueTuples, rows.get(e), table.columns(), table.auditConfig().orElse(null), dialect);
			}
			return dialect.renderUpsertStatement(table, (List<SqlColumn<?, ?>>) (List<?>) table.columns(), conflictColumn, valueTuples.toSql());
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		
		List<SqlColumn<E, ?>> columns = table.columns();
		renderer.insert();
		
		if (this.config.isInsertOrIgnore() && dialect.usesInsertOrIgnoreModifier()) {
			SqlRendered modifier = dialect.renderInsertOrIgnoreModifier();
			if (!modifier.sql().isEmpty()) {
				renderer.rendered(modifier);
			}
		}
		
		renderer.into().literal(dialect.quoteIdentifier(table.name()));
		
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			renderer.literal(dialect.quoteIdentifier(columns.get(i).name()));
		}
		
		SqlAuditConfig auditConfig = table.auditConfig().orElse(null);
		if (auditConfig != null && !this.config.isInsertFromSelect()) {
			for (SqlAuditColumn column : auditConfig.auditColumns()) {
				renderer.comma().literal(dialect.quoteIdentifier(column.name()));
			}
		}
		renderer.closingBracket();
		
		if (this.config.isInsertFromSelect()) {
			renderer.rendered(Objects.requireNonNull(this.config.fromSelect(), "From select query must not be null").toSql(dialect));
		} else {
			renderer.values();
			for (int e = 0; e < rows.size(); e++) {
				if (e > 0) {
					renderer.comma();
				}
				this.renderValueTuple(renderer, rows.get(e), columns, auditConfig, dialect);
			}
		}
		
		if (this.config.isUpsert()) {
			renderer.rendered(dialect.renderUpsertClause(Objects.requireNonNull(this.config.conflictColumn(), "Conflict column must not be null"), (List<SqlColumn<?, ?>>) (List<?>) columns));
		}
		
		if (this.config.isInsertOrIgnore() && !dialect.usesInsertOrIgnoreModifier()) {
			List<SqlColumn<E, ?>> conflictColumns = Objects.requireNonNull(this.config.conflictColumns(), "Conflict columns must not be null");
			SqlRendered suffix = dialect.renderInsertOrIgnoreSuffix((List<SqlColumn<?, ?>>) (List<?>) conflictColumns);
			
			if (!suffix.sql().isEmpty()) {
				renderer.rendered(suffix);
			}
		}
		return renderer.toSql();
	}
	
	/**
	 * Renders the value tuple of a single entity into the given renderer.<br>
	 * Each column value is added as a bind parameter, followed by the audit column values if the table is audited.<br>
	 * The audit values for the version, created-at and created-by columns are populated based on the audit configuration, while the updated-at and updated-by columns are set to {@code null}.<br>
	 *
	 * @param renderer The renderer to add the value tuple to
	 * @param entity The entity whose values are rendered
	 * @param columns The columns of the table in render order
	 * @param auditConfig The audit configuration of the table, or {@code null} if the table is not audited
	 * @param dialect The sql dialect used to render the query
	 * @throws SqlException If an error occurs while rendering the value tuple
	 */
	private void renderValueTuple(@NonNull SqlRenderer renderer, @NonNull E entity, @NonNull List<SqlColumn<E, ?>> columns, @Nullable SqlAuditConfig auditConfig, @NonNull SqlDialect dialect) throws SqlException {
		renderer.openingBracket();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			SqlColumn<E, ?> column = columns.get(i);
			Object value = column.getter().apply(entity);
			addParameter(renderer, column.type(), value);
		}
		
		if (auditConfig != null) {
			String auditUser = SqlQueryExecutor.resolveUser(this.config.auditUserProvider());
			boolean databaseSource = auditConfig.valueSource() == SqlAuditValueSource.DATABASE;
			LocalDateTime now = LocalDateTime.now(auditConfig.clock());
			
			for (SqlAuditColumn column : auditConfig.auditColumns()) {
				renderer.comma();
				switch (column.role()) {
					case VERSION -> {
						if (databaseSource) {
							renderer.literal("1");
						} else {
							renderer.parameter(auditConfig.versionType(), 1L);
						}
					}
					case CREATED_AT -> {
						if (databaseSource) {
							renderer.rendered(dialect.renderFunction(new SqlCurrentTimestampFunction<>(auditConfig.timestampType())));
						} else {
							renderer.parameter(auditConfig.timestampType(), now);
						}
					}
					case CREATED_BY -> {
						if (auditUser == null) {
							renderer.null_();
						} else {
							renderer.parameter(auditConfig.userType(), auditUser);
						}
					}
					case UPDATED_AT, UPDATED_BY -> renderer.null_();
				}
			}
		}
		
		renderer.closingBracket();
	}
}
