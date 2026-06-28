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
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.generic.SqlGreatestFunction;
import net.luis.utils.io.database.function.functions.generic.SqlLeastFunction;
import net.luis.utils.io.database.function.functions.numeric.SqlNumericTruncateFunction;
import net.luis.utils.io.database.function.functions.numeric.bitwise.SqlBitwiseXorFunction;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.*;

/**
 * Implementation of the {@link SqlDialect} for SQLite databases.<br>
 * Extends {@link AbstractSqlDialect} and provides SQLite-specific SQL generation, type mapping and feature support.<br>
 *
 * @author Luis-St
 */

public class SqliteDialect extends AbstractSqlDialect {
	
	/**
	 * The set of SQL features supported by this dialect.
	 */
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.RETURNING,
		SqlFeature.UPDATE_RETURNING,
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
			.stringFunctionRenderer(new SqliteStringFunctionRenderer(this))
			.comparisonConditionRenderer(new SqliteComparisonConditionRenderer(this))
			.genericFunctionRenderer(new SqliteGenericFunctionRenderer(this))
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

/**
 * SQLite-specific renderer for generic SQL functions.<br>
 * Specializes the rendering of the greatest and least functions, which are emulated using the {@code MAX} and {@code MIN} functions since SQLite has no dedicated {@code GREATEST} and {@code LEAST} functions.<br>
 *
 * @author Luis-St
 */
class SqliteGenericFunctionRenderer extends SqlGenericFunctionRenderer {
	
	/**
	 * Constructs a new SQLite generic function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqliteGenericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderGreatest(@NonNull SqlGreatestFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "MAX", function.expressions());
	}
	
	@Override
	protected @NonNull SqlRendered renderLeast(@NonNull SqlLeastFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCallWithList(this.dialect, "MIN", function.expressions());
	}
}

/**
 * SQLite-specific renderer for numeric SQL functions.<br>
 * Specializes the rendering of the truncate function using the {@code TRUNC} function and emulates the bitwise xor function using a combination of bitwise or and and operators since SQLite has no dedicated bitwise xor operator.<br>
 *
 * @author Luis-St
 */
class SqliteNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	/**
	 * Constructs a new SQLite numeric function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqliteNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRUNC", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderBitwiseXor(@NonNull SqlBitwiseXorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRendered first = function.firstOperand().toSql(this.dialect);
		SqlRendered second = function.secondOperand().toSql(this.dialect);
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.openingBracket().rendered(first).literal("|").rendered(second).closingBracket();
		renderer.literal("-");
		renderer.openingBracket().rendered(first).literal("&").rendered(second).closingBracket();
		return renderer.toSql();
	}
}

/**
 * SQLite-specific renderer for comparison conditions.<br>
 * Specializes the rendering of the is-distinct-from condition using the {@code IS NOT} operator since SQLite has no dedicated {@code IS DISTINCT FROM} operator.<br>
 *
 * @author Luis-St
 */
class SqliteComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
	/**
	 * Constructs a new SQLite comparison condition renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
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

/**
 * SQLite-specific renderer for table-level SQL statements.<br>
 * Specializes auto-increment rendering using the {@code AUTOINCREMENT} keyword, the rendering of single-column primary keys, truncation which is emulated using a {@code DELETE FROM} statement and the table rebuild which recreates the table via a temporary copy.<br>
 *
 * @author Luis-St
 */
class SqliteTableRenderer extends SqlTableRenderer {
	
	/**
	 * Constructs a new SQLite table renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
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

/**
 * SQLite-specific renderer for index-level SQL statements.<br>
 * Specializes the rendering of create-index statements and rejects index renaming since SQLite does not support renaming indexes.<br>
 *
 * @author Luis-St
 */
class SqliteIndexRenderer extends SqlIndexRenderer {
	
	/**
	 * Constructs a new SQLite index renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqliteIndexRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		renderer.index().literal(this.dialect.quoteIdentifier(index.name()));
		renderer.on().literal(this.dialect.quoteIdentifier(index.columns().getFirst().owningTable().name()));
		
		renderer.openingBracket();
		SqlRenderingHelper.renderList(renderer, index.columns(), (r, column) -> r.literal(this.dialect.quoteIdentifier(column.name())));
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this.dialect));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.RENAME_INDEX, this.dialect);
	}
}

/**
 * SQLite-specific renderer for column-level SQL statements.<br>
 * Rejects all alter-column operations as single statements since SQLite emulates them via a table rebuild through the migration API.<br>
 *
 * @author Luis-St
 */
class SqliteColumnRenderer extends SqlColumnRenderer {
	
	/**
	 * Constructs a new SQLite column renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
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

/**
 * SQLite-specific renderer for migration operations.<br>
 * Rejects adding unique, foreign key, check and composite primary key constraints as single statements since SQLite emulates them via a table rebuild through the migration API and rejects dropping constraints as an unsupported feature.<br>
 *
 * @author Luis-St
 */
class SqliteMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	/**
	 * Constructs a new SQLite migration operation renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
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

/**
 * SQLite-specific renderer for temporal SQL functions.<br>
 * Specializes the rendering of temporal extraction, truncation, epoch conversion, current date and time access, temporal addition and subtraction, date and time conversion and interval rendering using SQLite's {@code strftime} and {@code datetime} functions.<br>
 *
 * @author Luis-St
 */
class SqliteTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	/**
	 * Constructs a new SQLite temporal function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqliteTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	/**
	 * Converts the given temporal part name to the corresponding SQLite datetime modifier unit.<br>
	 *
	 * @param part The temporal part name to convert
	 * @return The SQLite datetime modifier unit for the given part
	 * @throws NullPointerException If the temporal part is null
	 */
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
	protected @NonNull SqlRendered renderExtract(@NonNull SqlExtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRendered inner = function.expression().toSql(this.dialect);
		SqlRenderer renderer = SqlRenderer.empty();
		if (function.part() == SqlTemporalPart.DAY_OF_WEEK) {
			renderer.openingBracket().openingBracket().openingBracket();
			renderer.cast().openingBracket().literal("strftime").openingBracket().literal("'%w'").comma().rendered(inner).closingBracket().as().literal("INTEGER").closingBracket();
			renderer.literal("+").literal("6").closingBracket().literal("%").literal("7").closingBracket().literal("+").literal("1").closingBracket();
			return renderer.toSql();
		}
		if (function.part() == SqlTemporalPart.QUARTER) {
			renderer.openingBracket().openingBracket();
			renderer.cast().openingBracket().literal("strftime").openingBracket().literal("'%m'").comma().rendered(inner).closingBracket().as().literal("INTEGER").closingBracket();
			renderer.literal("+").literal("2").closingBracket().literal("/").literal("3").closingBracket();
			return renderer.toSql();
		}
		
		String format = switch (function.part()) {
			case YEAR -> "%Y";
			case MONTH -> "%m";
			case DAY -> "%d";
			case HOUR -> "%H";
			case MINUTE -> "%M";
			case SECOND -> "%S";
			case WEEK -> "%W";
			case DAY_OF_WEEK -> "%w";
			case DAY_OF_YEAR -> "%j";
			default -> throw new SqlDialectUnsupportedRenderingException("EXTRACT part " + function.part() + " is not supported by dialect " + this.dialect.name());
		};
		renderer.cast().openingBracket().literal("strftime").openingBracket().literal("'" + format + "'").comma().rendered(inner).closingBracket().as().literal("INTEGER").closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRendered inner = function.expression().toSql(this.dialect);
		SqlRenderer renderer = SqlRenderer.empty();
		switch (function.part()) {
			case WEEK -> {
				renderer.literal("strftime").openingBracket().literal("'%Y-%m-%d 00:00:00'").comma().rendered(inner).comma();
				renderer.literal("'-' ||").openingBracket().openingBracket().literal("strftime").openingBracket().literal("'%w'").comma().rendered(inner).closingBracket().literal("+").literal("6").closingBracket().literal("%").literal("7").closingBracket().literal("|| ' days'");
				renderer.closingBracket();
			}
			case QUARTER -> {
				renderer.literal("printf").openingBracket().literal("'%s-%02d-01 00:00:00'").comma();
				renderer.literal("strftime").openingBracket().literal("'%Y'").comma().rendered(inner).closingBracket().comma();
				renderer.openingBracket().openingBracket().openingBracket();
				renderer.cast().openingBracket().literal("strftime").openingBracket().literal("'%m'").comma().rendered(inner).closingBracket().as().literal("INTEGER").closingBracket();
				renderer.literal("-").literal("1").closingBracket().literal("/").literal("3").closingBracket().literal("*").literal("3").literal("+").literal("1").closingBracket();
				renderer.closingBracket();
			}
			default -> {
				String format = switch (function.part()) {
					case YEAR -> "%Y-01-01 00:00:00";
					case MONTH -> "%Y-%m-01 00:00:00";
					case DAY, DAY_OF_WEEK, DAY_OF_YEAR -> "%Y-%m-%d 00:00:00";
					case HOUR -> "%Y-%m-%d %H:00:00";
					case MINUTE -> "%Y-%m-%d %H:%M:00";
					case SECOND -> "%Y-%m-%d %H:%M:%S";
					default -> throw new SqlDialectUnsupportedRenderingException("DATE_TRUNC part " + function.part() + " is not supported by dialect " + this.dialect.name());
				};
				renderer.literal("strftime").openingBracket().literal("'" + format + "'").comma().rendered(inner).closingBracket();
			}
		}
		return renderer.toSql();
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

/**
 * SQLite-specific renderer for string SQL functions.<br>
 * Specializes the rendering of distinct or ordered concatenation using {@code group_concat}, the position function using {@code INSTR}, the substring, trim, left and right functions and emulates left and right padding using SQLite primitives.<br>
 *
 * @author Luis-St
 */
class SqliteStringFunctionRenderer extends SqlStringFunctionRenderer {
	
	/**
	 * Constructs a new SQLite string function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqliteStringFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderConcat(@NonNull SqlConcatFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		boolean distinct = function.distinct();
		boolean ordered = function.ordered();
		if (!distinct && !ordered) {
			return super.renderConcat(function);
		}
		
		List<? extends SqlExpression<? extends CharSequence>> values = function.expressions();
		Optional<String> separator = function.separator();
		SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("group_concat").openingBracket();
		if (distinct) {
			renderer.distinct();
		}
		if (first != null) {
			renderer.rendered(first.toSql(this.dialect));
		}
		if (!distinct) {
			renderer.comma().parameter(DEFAULT_STRING_TYPE, separator.orElse(","));
		}
		if (ordered && first != null) {
			renderer.orderBy().rendered(first.toSql(this.dialect));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderPosition(@NonNull SqlPositionFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "INSTR", function.expression(), function.substring());
	}
	
	@Override
	protected @NonNull SqlRendered renderSubstring(@NonNull SqlSubstringFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("substr").openingBracket().rendered(function.expression().toSql(this.dialect)).comma().rendered(function.start().toSql(this.dialect));
		SqlExpression<? extends Number> length = function.length();
		if (length != null) {
			renderer.comma().rendered(length.toSql(this.dialect));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTrimChars(@NonNull SqlTrimCharsFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "TRIM", function.expression(), function.characters());
	}
	
	@Override
	protected @NonNull SqlRendered renderLeft(@NonNull SqlLeftFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("substr").openingBracket().rendered(function.expression().toSql(this.dialect)).comma().literal("1").comma().rendered(function.length().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderRight(@NonNull SqlRightFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("substr").openingBracket().rendered(function.expression().toSql(this.dialect)).comma().literal("-").openingBracket().rendered(function.length().toSql(this.dialect)).closingBracket().closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderLeftPad(@NonNull SqlLeftPadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderPad(function.expression(), function.length(), function.fill(), true);
	}
	
	@Override
	protected @NonNull SqlRendered renderRightPad(@NonNull SqlRightPadFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderPad(function.expression(), function.length(), function.fill(), false);
	}
	
	/**
	 * Renders a left or right padding expression emulated using SQLite string primitives.<br>
	 *
	 * @param expression The expression to pad
	 * @param length The expression giving the target length
	 * @param fill The expression giving the fill character
	 * @param left Whether to pad on the left ({@code true}) or on the right ({@code false})
	 * @return The rendered padding expression
	 * @throws SqlException If an error occurs while rendering the expressions
	 */
	private @NonNull SqlRendered renderPad(@NonNull SqlExpression<?> expression, @NonNull SqlExpression<?> length, @NonNull SqlExpression<?> fill, boolean left) throws SqlException {
		SqlRendered value = expression.toSql(this.dialect);
		SqlRendered count = length.toSql(this.dialect);
		
		SqlRenderer pad = SqlRenderer.empty();
		pad.literal("substr").openingBracket();
		pad.literal("replace").openingBracket().literal("hex").openingBracket().literal("zeroblob").openingBracket().rendered(count).closingBracket().closingBracket().comma().literal("'00'").comma().rendered(fill.toSql(this.dialect)).closingBracket();
		pad.comma().literal("1").comma().rendered(count).literal("-").literal("length").openingBracket().rendered(value).closingBracket();
		pad.closingBracket();
		SqlRendered fillRun = pad.toSql();
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.case_().when().literal("length").openingBracket().rendered(value).closingBracket().literal(">=").rendered(count).then();
		renderer.literal("substr").openingBracket().rendered(value).comma().literal("1").comma().rendered(count).closingBracket();
		renderer.else_();
		if (left) {
			renderer.rendered(fillRun).literal("||").rendered(value);
		} else {
			renderer.rendered(value).literal("||").rendered(fillRun);
		}
		renderer.end();
		return renderer.toSql();
	}
}
