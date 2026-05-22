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

package net.luis.utils.io.database.dialect;

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqliteDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.RETURNING,
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING
	);
	
	@Override
	public @NonNull String name() {
		return "SQLite";
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.temporalFunctionRenderer(new SqliteTemporalFunctionRenderer(this))
			.tableRenderer(new SqliteTableRenderer(this))
			.indexRenderer(new SqliteIndexRenderer(this))
			.columnRenderer(new SqliteColumnRenderer(this))
			.migrationRenderer(new SqliteMigrationOperationRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.BOOLEAN, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT -> "INTEGER";
			case Types.REAL, Types.FLOAT, Types.DOUBLE -> "REAL";
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.CLOB, Types.NCLOB, Types.DATE -> "TEXT";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported scalar JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR, Types.TIME, Types.TIMESTAMP, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP_WITH_TIMEZONE -> "TEXT";
			case Types.BINARY, Types.VARBINARY -> "BLOB";
			case Types.NUMERIC, Types.DECIMAL -> "NUMERIC";
			default -> throw new SqlDialectUnsupportedRenderingException("Unsupported parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		Objects.requireNonNull(columns, "Columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.returning();
		if (columns.isEmpty()) {
			renderer.literal("*");
		} else {
			SqlRenderingHelper.renderList(renderer, columns, (r, column) -> r.literal(this.quoteIdentifier(column.name())));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("Row-level locking is not supported by dialect " + this.name());
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "";
	}
}

class SqliteTableRenderer extends SqlTableRenderer {
	
	SqliteTableRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("AUTOINCREMENT");
	}
	
	@Override
	protected <E, C> @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<E, C> column, boolean skipPrimaryKey) throws SqlException {
		if (column.primaryKey() && column.autoIncrement() && column.type().jdbcType() == Types.INTEGER) {
			
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.literal(this.dialect.quoteIdentifier(column.name()));
			renderer.literal("INTEGER").primary().key().keyword("AUTOINCREMENT");
			if (!column.nullable()) {
				renderer.not().null_();
			}
			return renderer.toSql();
		}
		return super.renderColumnForTable(column, skipPrimaryKey);
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.delete().from().literal(this.dialect.quoteIdentifier(table.name()));
		return renderer.toSql();
	}
}

class SqliteIndexRenderer extends SqlIndexRenderer {
	
	SqliteIndexRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("RENAME INDEX is not supported by dialect SQLite");
	}
}

class SqliteColumnRenderer extends SqlColumnRenderer {
	
	SqliteColumnRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnSetDefault(@NonNull SqlColumn<?, ?> column, @NonNull String renderedDefault) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnDropDefault(@NonNull SqlColumn<?, ?> column) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN is not supported by dialect SQLite, table recreation is required");
	}
}

class SqliteMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	SqliteMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddForeignKey(
		@NonNull SqlTable<?> table,
		@NonNull String constraintName,
		@NonNull List<SqlColumn<?, ?>> columns,
		@NonNull SqlTable<?> referencedTable,
		@NonNull List<SqlColumn<?, ?>> referencedColumns,
		@NonNull SqlReferentialAction onDelete,
		@NonNull SqlReferentialAction onUpdate
	) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
	
	@Override
	public @NonNull SqlRendered renderDropConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("DROP CONSTRAINT is not supported by dialect SQLite, table recreation is required");
	}
}

class SqliteTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	SqliteTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	private static @NonNull String toSqliteModifier(@NonNull String part) {
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		return switch (part.toUpperCase()) {
			case "YEAR" -> "years";
			case "MONTH" -> "months";
			case "DAY" -> "days";
			case "HOUR" -> "hours";
			case "MINUTE" -> "minutes";
			case "SECOND" -> "seconds";
			default -> part.toLowerCase() + "s";
		};
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.expression().toSql(this.dialect)).comma().literal("'unixepoch'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("strftime").openingBracket();
		renderer.literal("'%s'").comma().rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderNow() throws SqlException {
		return SqlRendered.of("datetime('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentDate() throws SqlException {
		return SqlRendered.of("date('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTime() throws SqlException {
		return SqlRendered.of("time('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTimestamp() throws SqlException {
		return SqlRendered.of("datetime('now')");
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.firstSummand().toSql(this.dialect)).comma();
		renderer.literal("'+' ||").rendered(function.secondSummand().toSql(this.dialect)).literal("|| ' " + toSqliteModifier(function.part().name()) + "'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("datetime").openingBracket();
		renderer.rendered(function.minuend().toSql(this.dialect)).comma();
		renderer.literal("'-' ||").rendered(function.subtrahend().toSql(this.dialect)).literal("|| ' " + toSqliteModifier(function.part().name()) + "'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToDate(@NonNull SqlToDateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("date").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToTime(@NonNull SqlToTimeFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("time").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect)).literal("|| ' " + toSqliteModifier(part) + "'");
		return renderer.toSql();
	}
}
