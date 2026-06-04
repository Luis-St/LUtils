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
import net.luis.utils.io.database.audit.SqlAuditColumn;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsDistinctFromCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlComparisonConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlNumericFunctionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.SqlTemporalFunctionRenderer;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
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
		SqlFeature.NULLS_ORDERING,
		SqlFeature.UPSERT_SUFFIX,
		SqlFeature.INSERT_OR_IGNORE,
		SqlFeature.TRANSACTIONAL_DDL,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.TABLE_REBUILD
	);
	
	@Override
	public @NonNull String name() {
		return "SQLite";
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.temporalFunctionRenderer(new SqliteTemporalFunctionRenderer(this))
			.numericFunctionRenderer(new SqliteNumericFunctionRenderer(this))
			.comparisonConditionRenderer(new SqliteComparisonConditionRenderer(this))
			.tableRenderer(new SqliteTableRenderer(this))
			.indexRenderer(new SqliteIndexRenderer(this))
			.columnRenderer(new SqliteColumnRenderer(this))
			.migrationRenderer(new SqliteMigrationOperationRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull Optional<String> getScalarTypeName(int jdbcType) {
		return Optional.ofNullable(switch (jdbcType) {
			case Types.BOOLEAN, Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT -> "INTEGER";
			case Types.REAL, Types.FLOAT, Types.DOUBLE -> "REAL";
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.CLOB, Types.NCLOB, Types.DATE -> "TEXT";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			default -> null;
		});
	}
	
	@Override
	protected @NonNull Optional<String> getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) {
		return Optional.ofNullable(switch (jdbcType) {
			case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR, Types.TIME, Types.TIMESTAMP, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP_WITH_TIMEZONE -> "TEXT";
			case Types.BINARY, Types.VARBINARY -> "BLOB";
			case Types.NUMERIC, Types.DECIMAL -> "NUMERIC";
			default -> null;
		});
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public int maxBindParameters() {
		return 999;
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		return this.renderStandardReturning(columns);
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.ROW_LOCKING, this);
	}
	
	@Override
	protected @Nullable String getCheckConstraintsQueryString() {
		return null;
	}
}

class SqliteNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	SqliteNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRUNC", function.expression());
	}
}

class SqliteComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
	SqliteComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderIsDistinctFrom(@NonNull SqlIsDistinctFromCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.first().toSql(this.dialect)).is().not().rendered(condition.second().toSql(this.dialect));
		return renderer.toSql();
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
	
	@Override
	public @NonNull List<SqlRendered> renderTableRebuild(@NonNull SqlTable<?> table, @NonNull List<? extends SqlColumn<?, ?>> newColumns, @NonNull List<SqlRendered> extraInlineConstraints) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(newColumns, "Sql columns must not be null");
		Objects.requireNonNull(extraInlineConstraints, "Extra inline constraints must not be null");
		
		String original = this.dialect.quoteIdentifier(table.name());
		String temp = this.dialect.quoteIdentifier("_" + table.name() + "_rebuild");
		boolean hasCompositeKey = table.compositePrimaryKey().isPresent();
		
		List<String> columnNames = new ArrayList<>();
		SqlRenderer create = SqlRenderer.empty();
		create.create().table().literal(temp).openingBracket();
		for (int i = 0; i < newColumns.size(); i++) {
			if (i > 0) {
				create.comma();
			}
			SqlColumn<?, ?> column = newColumns.get(i);
			create.rendered(this.renderColumnForTable(column, hasCompositeKey));
			columnNames.add(this.dialect.quoteIdentifier(column.name()));
		}
		if (table.auditConfig().isPresent()) {
			for (SqlAuditColumn auditColumn : table.auditConfig().get().auditColumns()) {
				create.comma().rendered(this.renderAuditColumnForTable(auditColumn));
				columnNames.add(this.dialect.quoteIdentifier(auditColumn.name()));
			}
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			create.comma().rendered(tableConstraints);
		}
		for (SqlRendered extra : extraInlineConstraints) {
			if (!extra.sql().isEmpty()) {
				create.comma().rendered(extra);
			}
		}
		create.closingBracket();
		
		String columns = String.join(", ", columnNames);
		SqlRenderer copy = SqlRenderer.empty();
		copy.insert().into().literal(temp).openingBracket().literal(columns).closingBracket();
		copy.select().literal(columns).from().literal(original);
		
		List<SqlRendered> statements = new ArrayList<>();
		// PRAGMA foreign_keys is a silent no-op inside a transaction, and migrations always run in one, so it cannot be used here.
		// legacy_alter_table keeps the RENAME from rewriting references in other schema objects; defer_foreign_keys postpones FK
		// enforcement to commit time (honored within a transaction, auto-reset on commit/rollback) instead of disabling it outright.
		statements.add(SqlRendered.of("PRAGMA legacy_alter_table=ON"));
		statements.add(SqlRendered.of("PRAGMA defer_foreign_keys=ON"));
		statements.add(create.toSql());
		statements.add(copy.toSql());
		statements.add(SqlRenderer.empty().drop().table().literal(original).toSql());
		statements.add(SqlRenderer.empty().alter().table().literal(temp).rename().to().literal(original).toSql());
		statements.add(SqlRendered.of("PRAGMA legacy_alter_table=OFF"));
		return List.copyOf(statements);
	}
}

class SqliteIndexRenderer extends SqlIndexRenderer {
	
	SqliteIndexRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.RENAME_INDEX, this.dialect);
	}
}

class SqliteColumnRenderer extends SqlColumnRenderer {
	
	SqliteColumnRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnSetDefault(@NonNull SqlColumn<?, ?> column, @NonNull String renderedDefault) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnDropDefault(@NonNull SqlColumn<?, ?> column) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ALTER COLUMN cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
}

class SqliteMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	SqliteMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAddUniqueConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
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
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCheckConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull SqlCondition condition) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderAddCompositePrimaryKey(@NonNull SqlTable<?> table, @NonNull String constraintName, @NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedRenderingException("ADD CONSTRAINT cannot be rendered as a single statement on SQLite, it is emulated via a table rebuild through the migration API");
	}
	
	@Override
	public @NonNull SqlRendered renderDropConstraint(@NonNull SqlTable<?> table, @NonNull String constraintName) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.DROP_CONSTRAINT, this.dialect);
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
