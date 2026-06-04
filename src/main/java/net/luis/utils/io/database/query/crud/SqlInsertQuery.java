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
 *
 * @author Luis-St
 *
 */

public class SqlInsertQuery<E> implements SqlQuery<E> {
	
	private final SqlInsertQueryConfig<E> config;
	
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
	
	SqlInsertQuery(@NonNull SqlInsertQueryConfig<E> config) {
		this.config = Objects.requireNonNull(config, "Sql insert query config must not be null");
	}
	
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
	
	@SuppressWarnings("unchecked")
	private static <T> void addParameter(@NonNull SqlRenderer renderer, @NonNull SqlType<T> type, @Nullable Object value) {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		
		renderer.parameter(type, (T) value);
	}
	
	public int execute() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		if (chunks.size() == 1) {
			return SqlQueryExecutor.executeUpdate(this.config.dialect(), this.config.connectionSource(), chunks.getFirst(), this.config.queryTimeout());
		}
		return SqlQueryExecutor.executeBatchedUpdate(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
	public @NonNull List<Long> executeReturningKeys() throws SqlException {
		List<SqlRendered> chunks = this.renderChunks(this.config.dialect());
		return SqlQueryExecutor.executeUpdateReturningKeys(this.config.dialect(), this.config.connectionSource(), chunks, this.config.queryTimeout());
	}
	
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
		
		if (this.config.isInsertOrIgnore()) {
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
		
		if (this.config.isInsertOrIgnore()) {
			List<SqlColumn<E, ?>> conflictColumns = Objects.requireNonNull(this.config.conflictColumns(), "Conflict columns must not be null");
			SqlRendered suffix = dialect.renderInsertOrIgnoreSuffix((List<SqlColumn<?, ?>>) (List<?>) conflictColumns);
			
			if (!suffix.sql().isEmpty()) {
				renderer.rendered(suffix);
			}
		}
		return renderer.toSql();
	}
	
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
