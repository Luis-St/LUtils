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

import net.luis.utils.io.data.xml.XmlConfig;
import net.luis.utils.io.data.xml.XmlElement;
import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsDistinctFromCondition;
import net.luis.utils.io.database.condition.conditions.numeric.SqlModEqualsCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.condition.*;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.SqlAtan2Function;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.function.functions.window.SqlValueAtFunction;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.SqlFractionalParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLXML;
import java.sql.Types;
import java.util.*;

/**
 * SQL dialect implementation for Microsoft SQL Server.<br>
 * Provides SQL Server-specific SQL generation by extending {@link AbstractSqlDialect}.<br>
 *
 * @author Luis-St
 */

public class SqlServerDialect extends AbstractSqlDialect {
	
	/**
	 * The set of SQL features supported by this dialect.
	 */
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.NO_WAIT,
		SqlFeature.ROW_LOCKING,
		SqlFeature.UPSERT,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT,
		SqlFeature.OFFSET_WITHOUT_LIMIT
	);
	
	/**
	 * The set of index methods supported by this dialect.
	 */
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH,
		SqlIndexMethod.CLUSTERED,
		SqlIndexMethod.NONCLUSTERED,
		SqlIndexMethod.COLUMNSTORE
	);
	/**
	 * The registry of SQL Server-specific SQL type mappings.
	 */
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.UUID, "UNIQUEIDENTIFIER")
		.register(SqlTypes.XML, "XML", (statement, index, value) -> {
			if (value == null) {
				statement.setNull(index, Types.SQLXML);
				return;
			}
			
			SQLXML xml = statement.getConnection().createSQLXML();
			xml.setString(((XmlElement) value).toString(XmlConfig.DEFAULT));
			statement.setSQLXML(index, xml);
		}, (resultSet, index) -> readXmlElement(resultSet.getSQLXML(index)))
		.build();
	
	@Override
	public @NonNull String name() {
		return "SQL Server";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.stringFunctionRenderer(new SqlServerStringFunctionRenderer(this))
			.numericFunctionRenderer(new SqlServerNumericFunctionRenderer(this))
			.temporalFunctionRenderer(new SqlServerTemporalFunctionRenderer(this))
			.stringConditionRenderer(new SqlServerStringConditionRenderer(this))
			.comparisonConditionRenderer(new SqlServerComparisonConditionRenderer(this))
			.numericConditionRenderer(new SqlServerNumericConditionRenderer(this))
			.windowFunctionRenderer(new SqlServerWindowFunctionRenderer(this, new SqlAggregateFunctionRenderer(this)))
			.tableRenderer(new SqlServerTableRenderer(this))
			.indexRenderer(new SqlServerIndexRenderer(this))
			.columnRenderer(new SqlServerColumnRenderer(this))
			.migrationRenderer(new SqlServerMigrationOperationRenderer(this))
			.schemaRenderer(new SqlServerSchemaRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull Optional<String> getScalarTypeName(int jdbcType) {
		return switch (jdbcType) {
			case Types.BOOLEAN -> Optional.of("BIT");
			case Types.TINYINT -> Optional.of("TINYINT");
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCLOB -> Optional.of("NVARCHAR(MAX)");
			case Types.CLOB -> Optional.of("VARCHAR(MAX)");
			case Types.LONGVARBINARY, Types.BLOB -> Optional.of("VARBINARY(MAX)");
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull Optional<String> getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlFractionalParameter fractional) {
			return switch (jdbcType) {
				case Types.TIMESTAMP -> Optional.of("DATETIME2(" + fractional.digits() + ")");
				case Types.TIMESTAMP_WITH_TIMEZONE -> Optional.of("DATETIMEOFFSET(" + fractional.digits() + ")");
				case Types.TIME_WITH_TIMEZONE -> Optional.of("TIME(" + fractional.digits() + ")");
				default -> super.getParameterizedTypeName(jdbcType, parameter);
			};
		}
		return super.getParameterizedTypeName(jdbcType, parameter);
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return SUPPORTED_FEATURES.contains(feature);
	}
	
	@Override
	public int maxBindParameters() {
		return 2100;
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "[" + identifier.replace("]", "]]") + "]";
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql lock mode must not be null");
		return SqlRendered.of("");
	}
	
	@Override
	public @NonNull SqlRendered renderLockHint(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql lock mode must not be null");
		List<String> hints = new ArrayList<>();
		hints.add(mode == SqlLockMode.FOR_SHARE ? "HOLDLOCK" : "UPDLOCK");
		hints.add("ROWLOCK");
		if (skipLocked) {
			hints.add("READPAST");
		}
		if (noWait) {
			hints.add("NOWAIT");
		}
		return SqlRendered.of("WITH (" + String.join(", ", hints) + ")");
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset, boolean hasOrdering) {
		if (limit < -1) {
			throw new IllegalArgumentException("Limit must be non-negative or -1 for no limit");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (!hasOrdering) {
			renderer.orderBy().openingBracket().select().null_().closingBracket();
		}
		renderer.offset().literal(String.valueOf(offset)).rows();
		if (limit >= 0) {
			renderer.fetch().next().literal(String.valueOf(limit)).rows().only();
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderBooleanLiteral(boolean value) throws SqlException {
		return SqlRendered.of(value ? "1" : "0");
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.UPSERT_SUFFIX, this);
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(conflictColumn, "Sql conflict column must not be null");
		Objects.requireNonNull(valueTuples, "Sql value tuples must not be null");
		
		List<String> auditColumns = table.auditConfig().map(SqlAuditConfig::columnNames).orElse(List.of());
		List<String> allColumns = new ArrayList<>();
		for (SqlColumn<?, ?> column : columns) {
			allColumns.add(column.name());
		}
		allColumns.addAll(auditColumns);
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.keyword("MERGE").into().literal(this.quoteIdentifier(table.name())).as().literal("target");
		
		renderer.using().openingBracket().values().rendered(valueTuples).closingBracket();
		renderer.as().literal("source").openingBracket();
		for (int i = 0; i < allColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(allColumns.get(i)));
		}
		renderer.closingBracket();
		
		String conflictName = this.quoteIdentifier(conflictColumn.name());
		renderer.on().literal("target." + conflictName).literal("=").literal("source." + conflictName);
		
		renderer.when().keyword("MATCHED").then().update().set();
		boolean firstSet = true;
		for (SqlColumn<?, ?> column : columns) {
			if (column.name().equals(conflictColumn.name())) {
				continue;
			}
			
			if (!firstSet) {
				renderer.comma();
			}
			
			firstSet = false;
			String name = this.quoteIdentifier(column.name());
			renderer.literal("target." + name).literal("=").literal("source." + name);
		}
		if (firstSet) {
			renderer.literal("target." + conflictName).literal("=").literal("source." + conflictName);
		}
		
		renderer.when().not().keyword("MATCHED").then().insert().openingBracket();
		for (int i = 0; i < allColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(allColumns.get(i)));
		}
		renderer.closingBracket().values().openingBracket();
		for (int i = 0; i < allColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal("source." + this.quoteIdentifier(allColumns.get(i)));
		}
		renderer.closingBracket().literal(";");
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.INSERT_OR_IGNORE, this);
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "SELECT cc.CONSTRAINT_NAME, cc.CHECK_CLAUSE FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc " +
			"JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc ON tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME AND tc.CONSTRAINT_SCHEMA = cc.CONSTRAINT_SCHEMA " +
			"WHERE tc.TABLE_SCHEMA = ? AND tc.TABLE_NAME = ?";
	}
}

/**
 * SQL Server-specific renderer for table-related SQL statements.<br>
 * Extends {@link SqlTableRenderer} to render {@code IF OBJECT_ID}-based existence checks for conditional table creation
 * and {@code IDENTITY(1, 1)} auto-increment columns.<br>
 *
 * @author Luis-St
 */
class SqlServerTableRenderer extends SqlTableRenderer {
	
	/**
	 * Constructs a new SQL Server table renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerTableRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		if (!ifNotExists) {
			return super.renderCreateTable(table, false);
		}
		
		String existenceCheck = "IF OBJECT_ID(N'" + this.dialect.quoteIdentifier(table.name()).replace("'", "''") + "', N'U') IS NULL";
		return SqlRenderer.empty().literal(existenceCheck).rendered(super.renderCreateTable(table, false)).toSql();
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("IDENTITY").openingBracket().literal("1").comma().literal("1").closingBracket();
	}
}

/**
 * SQL Server-specific renderer for index-related SQL statements.<br>
 * Extends {@link SqlIndexRenderer} to render {@code CLUSTERED} and {@code NONCLUSTERED} index methods and standard
 * drop-index statements, while rejecting index renaming as an unsupported feature.<br>
 *
 * @author Luis-St
 */
class SqlServerIndexRenderer extends SqlIndexRenderer {
	
	/**
	 * Constructs a new SQL Server index renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerIndexRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		if (index.method() == SqlIndexMethod.CLUSTERED) {
			renderer.keyword("CLUSTERED");
		} else if (index.method() == SqlIndexMethod.NONCLUSTERED) {
			renderer.keyword("NONCLUSTERED");
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
	public @NonNull SqlRendered renderDropIndex(@Nullable SqlTable<?> owningTable, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(owningTable, "Sql index owning table must not be null");
		return this.renderStandardDropIndexOnTable(owningTable, indexName);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.RENAME_INDEX, this.dialect);
	}
}

/**
 * SQL Server-specific renderer for column-related SQL statements.<br>
 * Extends {@link SqlColumnRenderer} to render {@code ALTER TABLE ... ALTER COLUMN} statements for changing a column's
 * type and nullability.<br>
 *
 * @author Luis-St
 */
class SqlServerColumnRenderer extends SqlColumnRenderer {
	
	/**
	 * Constructs a new SQL Server column renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerColumnRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(newType, "New sql type must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).literal(this.dialect.getTypeName(newType)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).alter().column().literal(this.dialect.quoteIdentifier(columnName)).literal(this.dialect.getTypeName(column.type()));
		
		if (nullable) {
			return renderer.null_().toSql();
		} else {
			return renderer.not().null_().toSql();
		}
	}
}

/**
 * SQL Server-specific renderer for schema migration operations.<br>
 * Extends {@link SqlMigrationOperationRenderer} to render table and column renaming through the {@code sp_rename}
 * stored procedure.<br>
 *
 * @author Luis-St
 */
class SqlServerMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	/**
	 * Constructs a new SQL Server migration operation renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameTable(@NonNull SqlTable<?> fromTable, @NonNull SqlTable<?> toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Sql source table must not be null");
		Objects.requireNonNull(toTable, "Sql target table must not be null");
		
		return SqlRenderer.empty().literal("EXEC").literal("sp_rename").literal("'" + fromTable.name() + "'").comma().literal("'" + toTable.name() + "'").toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderRenameColumn(@NonNull SqlTable<?> table, @NonNull SqlColumn<?, ?> fromColumn, @NonNull SqlColumn<?, ?> toColumn) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(fromColumn, "Sql source column name must not be null");
		Objects.requireNonNull(toColumn, "Sql target column name must not be null");
		
		return SqlRenderer.empty().literal("EXEC").literal("sp_rename").literal("'" + table.name() + "." + fromColumn.name() + "'").comma().literal("'" + toColumn.name() + "'").comma().literal("'COLUMN'").toSql();
	}
}

/**
 * SQL Server-specific renderer for numeric SQL functions.<br>
 * Extends {@link SqlNumericFunctionRenderer} to render SQL Server function names and idioms such as {@code RAND},
 * {@code ATN2}, {@code CEILING}, the {@code %} modulo operator and {@code ROUND}-based rounding, logarithm,
 * radians and truncation functions.<br>
 *
 * @author Luis-St
 */
class SqlServerNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	/**
	 * Constructs a new SQL Server numeric function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		return SqlRendered.of("RAND()");
	}
	
	@Override
	protected @NonNull SqlRendered renderAtan2(@NonNull SqlAtan2Function function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ATN2", function.y(), function.x());
	}
	
	@Override
	protected @NonNull SqlRendered renderCeil(@NonNull SqlCeilFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "CEILING", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderRound(@NonNull SqlRoundFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> precision = function.precision();
		if (precision != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression(), precision);
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "ROUND", function.expression(), Sql.of(0, SqlTypes.INTEGER));
	}
	
	@Override
	protected @NonNull SqlRendered renderLog(@NonNull SqlLogFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlExpression<? extends Number> base = function.base();
		if (base != null) {
			return SqlRenderingHelper.renderFunctionCall(this.dialect, "LOG", function.expression(), base);
		}
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LOG", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderMod(@NonNull SqlModFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderInfix(this.dialect, function.expression(), "%", function.divisor());
	}
	
	@Override
	protected @NonNull SqlRendered renderRadians(@NonNull SqlRadiansFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("RADIANS").openingBracket();
		renderer.cast().openingBracket().rendered(function.expression().toSql(this.dialect)).as().literal("FLOAT").closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("ROUND").openingBracket();
		renderer.rendered(function.expression().toSql(this.dialect)).comma();
		renderer.rendered(new SqlValueExpression<>(0, SqlTypes.INTEGER).toSql(this.dialect)).comma();
		renderer.rendered(new SqlValueExpression<>(1, SqlTypes.INTEGER).toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
}

/**
 * SQL Server-specific renderer for window SQL functions.<br>
 * Extends {@link SqlWindowFunctionRenderer} to emulate value-at functions, supporting only position 1 via
 * {@code FIRST_VALUE} since SQL Server does not provide {@code NTH_VALUE}.<br>
 *
 * @author Luis-St
 */
class SqlServerWindowFunctionRenderer extends SqlWindowFunctionRenderer {
	
	/**
	 * Constructs a new SQL Server window function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @param aggregateRenderer The aggregate function renderer used to render aggregate window functions
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerWindowFunctionRenderer(@NonNull SqlDialect dialect, @NonNull SqlAggregateFunctionRenderer aggregateRenderer) {
		super(dialect, aggregateRenderer);
	}
	
	/**
	 * Checks whether the given position expression is a constant numeric value equal to the expected position.<br>
	 *
	 * @param position The position expression to check
	 * @param expected The expected constant position value
	 * @return True if the position is a constant value equal to the expected position, otherwise false
	 */
	private static boolean isConstantPosition(@NonNull SqlExpression<? extends Number> position, int expected) {
		if (position instanceof SqlValueExpression<?> value && value.value() instanceof Number number) {
			return number.intValue() == expected;
		}
		return false;
	}
	
	@Override
	protected @NonNull SqlRendered renderValueAt(@NonNull SqlValueAtFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (isConstantPosition(function.position(), 1)) {
			return this.renderWindowCall("FIRST_VALUE", function.over(), function.column());
		}
		throw new SqlDialectUnsupportedRenderingException("SQL Server does not support NTH_VALUE; only position 1 (FIRST_VALUE) can be emulated");
	}
}

/**
 * SQL Server-specific renderer for numeric SQL conditions.<br>
 * Extends {@link SqlNumericConditionRenderer} to render modulo-equality conditions using the {@code %} operator.<br>
 *
 * @author Luis-St
 */
class SqlServerNumericConditionRenderer extends SqlNumericConditionRenderer {
	
	/**
	 * Constructs a new SQL Server numeric condition renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerNumericConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderModEquals(@NonNull SqlModEqualsCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(condition.value().toSql(this.dialect)).literal("%").rendered(condition.divisor().toSql(this.dialect));
		renderer.literal("=").rendered(condition.remainder().toSql(this.dialect));
		return renderer.toSql();
	}
}

/**
 * SQL Server-specific renderer for comparison SQL conditions.<br>
 * Extends {@link SqlComparisonConditionRenderer} to emulate the {@code IS DISTINCT FROM} condition using a
 * {@code CASE} expression since SQL Server lacks native support for it.<br>
 *
 * @author Luis-St
 */
class SqlServerComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
	/**
	 * Constructs a new SQL Server comparison condition renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderIsDistinctFrom(@NonNull SqlIsDistinctFromCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.openingBracket().case_().when();
		renderer.openingBracket().rendered(condition.first().toSql(this.dialect)).literal("=").rendered(condition.second().toSql(this.dialect)).closingBracket();
		renderer.or();
		renderer.openingBracket().rendered(condition.first().toSql(this.dialect)).is().null_().and().rendered(condition.second().toSql(this.dialect)).is().null_().closingBracket();
		renderer.then().literal("0").else_().literal("1").end().literal("=").literal("1").closingBracket();
		return renderer.toSql();
	}
}

/**
 * SQL Server-specific renderer for string SQL conditions.<br>
 * Extends {@link SqlStringConditionRenderer} to render string concatenation expressions using the {@code +}
 * operator.<br>
 *
 * @author Luis-St
 */
class SqlServerStringConditionRenderer extends SqlStringConditionRenderer {
	
	/**
	 * Constructs a new SQL Server string condition renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerStringConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderConcatExpression(@NonNull SqlRenderer renderer, @NonNull SqlRendered left, @NonNull SqlRendered right) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(left, "Left sql rendered must not be null");
		Objects.requireNonNull(right, "Right sql rendered must not be null");
		
		return renderer.rendered(left).literal("+").rendered(right).toSql();
	}
}

/**
 * SQL Server-specific renderer for string SQL functions.<br>
 * Extends {@link SqlStringFunctionRenderer} to render SQL Server string idioms such as {@code +}- and
 * {@code STRING_AGG}-based concatenation, {@code LEN}, {@code CHARINDEX}, {@code SUBSTRING}, {@code REPLICATE}-based
 * padding and {@code CONVERT}-based hex and unhex functions.<br>
 *
 * @author Luis-St
 */
class SqlServerStringFunctionRenderer extends SqlStringFunctionRenderer {
	
	/**
	 * Constructs a new SQL Server string function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerStringFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull SqlRendered renderConcat(@NonNull SqlConcatFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		List<? extends SqlExpression<? extends CharSequence>> values = function.expressions();
		Optional<String> separator = function.separator();
		boolean distinct = function.distinct();
		boolean ordered = function.ordered();
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (distinct || ordered) {
			renderer.literal("STRING_AGG").openingBracket();
			SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
			if (first != null) {
				renderer.rendered(first.toSql(this.dialect));
			}
			renderer.comma().parameter(DEFAULT_STRING_TYPE, separator.orElse(""));
			renderer.closingBracket();
			if (ordered && first != null) {
				renderer.literal("WITHIN GROUP").openingBracket().orderBy().rendered(first.toSql(this.dialect)).closingBracket();
			}
		} else {
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.literal("+");
					separator.ifPresent(s -> renderer.parameter(DEFAULT_STRING_TYPE, s).literal("+"));
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
		}
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderLength(@NonNull SqlLengthFunction<?> function) throws SqlException {
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "LEN", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderPosition(@NonNull SqlPositionFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "CHARINDEX", function.substring(), function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderSubstring(@NonNull SqlSubstringFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("SUBSTRING").openingBracket().rendered(function.expression().toSql(this.dialect)).comma().rendered(function.start().toSql(this.dialect)).comma();
		SqlExpression<? extends Number> length = function.length();
		if (length != null) {
			renderer.rendered(length.toSql(this.dialect));
		} else {
			renderer.literal("LEN").openingBracket().rendered(function.expression().toSql(this.dialect)).closingBracket();
		}
		renderer.closingBracket();
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
	 * Renders a left or right padding expression for the given value, target length and fill string.<br>
	 * If the value is already at least as long as the target length it is truncated with {@code LEFT}, otherwise it is padded with a {@code REPLICATE}-based fill run on the left or right side depending on the given flag.<br>
	 *
	 * @param expression The expression to pad
	 * @param length The target length expression
	 * @param fill The fill expression used for padding
	 * @param left Whether to pad on the left side, otherwise on the right side
	 * @return The rendered padding expression
	 * @throws SqlException If rendering fails
	 */
	private @NonNull SqlRendered renderPad(@NonNull SqlExpression<?> expression, @NonNull SqlExpression<?> length, @NonNull SqlExpression<?> fill, boolean left) throws SqlException {
		SqlRendered value = expression.toSql(this.dialect);
		SqlRendered count = length.toSql(this.dialect);
		
		SqlRenderer filler = SqlRenderer.empty();
		filler.literal("LEFT").openingBracket();
		filler.literal("REPLICATE").openingBracket().rendered(fill.toSql(this.dialect)).comma().rendered(count).closingBracket();
		filler.comma().rendered(count).literal("-").literal("LEN").openingBracket().rendered(value).closingBracket();
		filler.closingBracket();
		SqlRendered fillRun = filler.toSql();
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.case_().when().literal("LEN").openingBracket().rendered(value).closingBracket().literal(">=").rendered(count).then();
		renderer.literal("LEFT").openingBracket().rendered(value).comma().rendered(count).closingBracket();
		renderer.else_();
		if (left) {
			renderer.rendered(fillRun).literal("+").rendered(value);
		} else {
			renderer.rendered(value).literal("+").rendered(fillRun);
		}
		renderer.end();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderHex(@NonNull SqlHexFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("CONVERT").openingBracket().literal("VARCHAR(MAX)").comma();
		renderer.literal("CONVERT").openingBracket().literal("VARBINARY(MAX)").comma();
		renderer.cast().openingBracket().rendered(function.expression().toSql(this.dialect)).as().literal("VARCHAR(MAX)").closingBracket().closingBracket();
		renderer.comma().literal("2").closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderUnhex(@NonNull SqlUnhexFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("CONVERT").openingBracket().literal("VARBINARY(MAX)").comma().rendered(function.expression().toSql(this.dialect)).comma().literal("2").closingBracket();
		return renderer.toSql();
	}
}

/**
 * SQL Server-specific renderer for temporal SQL functions.<br>
 * Extends {@link SqlTemporalFunctionRenderer} to render SQL Server temporal idioms such as {@code DATEPART}-based
 * extraction, {@code GETDATE}-based current date, time and timestamp, {@code DATEADD} and {@code DATEDIFF}-based epoch
 * conversions and temporal arithmetic, and {@code DATETRUNC}-based truncation.<br>
 *
 * @author Luis-St
 */
class SqlServerTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	/**
	 * Constructs a new SQL Server temporal function renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	/**
	 * Maps the given temporal part to its SQL Server {@code DATEPART} unit name.<br>
	 *
	 * @param part The temporal part to map
	 * @return The SQL Server date part unit name
	 * @throws NullPointerException If the temporal part is null
	 */
	private static @NonNull String toDatePart(@NonNull SqlTemporalPart part) {
		Objects.requireNonNull(part, "Temporal part must not be null");
		return switch (part) {
			case YEAR -> "YEAR";
			case MONTH -> "MONTH";
			case DAY -> "DAY";
			case HOUR -> "HOUR";
			case MINUTE -> "MINUTE";
			case SECOND -> "SECOND";
			case QUARTER -> "QUARTER";
			case WEEK -> "WEEK";
			case DAY_OF_WEEK -> "WEEKDAY";
			case DAY_OF_YEAR -> "DAYOFYEAR";
		};
	}
	
	@Override
	protected @NonNull SqlRendered renderExtract(@NonNull SqlExtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (function.part() == SqlTemporalPart.DAY_OF_WEEK) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.openingBracket().openingBracket();
			renderer.literal("DATEDIFF").openingBracket().literal("DAY").comma().literal("'1900-01-01'").comma().rendered(function.expression().toSql(this.dialect)).closingBracket();
			renderer.literal("%").literal("7").closingBracket().literal("+").literal("1").closingBracket();
			return renderer.toSql();
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEPART").openingBracket();
		renderer.literal(function.part() == SqlTemporalPart.WEEK ? "ISO_WEEK" : toDatePart(function.part())).comma().rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentDate() throws SqlException {
		return SqlRenderer.empty().cast().openingBracket().literal("GETDATE()").as().literal("DATE").closingBracket().toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTime() throws SqlException {
		return SqlRenderer.empty().cast().openingBracket().literal("GETDATE()").as().literal("TIME").closingBracket().toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("SECOND").comma().rendered(function.expression().toSql(this.dialect)).literal("%").literal("86400").comma();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("DAY").comma().rendered(function.expression().toSql(this.dialect)).literal("/").literal("86400").comma().literal("'1970-01-01'");
		renderer.closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEDIFF_BIG").openingBracket();
		renderer.literal("SECOND").comma().literal("'1970-01-01'").comma();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal(toDatePart(function.part())).comma();
		renderer.rendered(function.secondSummand().toSql(this.dialect)).comma();
		renderer.rendered(function.firstSummand().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal(toDatePart(function.part())).comma();
		renderer.literal("-").openingBracket().rendered(function.subtrahend().toSql(this.dialect)).closingBracket().comma();
		renderer.rendered(function.minuend().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderNow() throws SqlException {
		return SqlRendered.of("GETDATE()");
	}
	
	@Override
	protected @NonNull SqlRendered renderCurrentTimestamp() throws SqlException {
		return SqlRendered.of("GETDATE()");
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		if (function.part() == SqlTemporalPart.WEEK) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.literal("DATEADD").openingBracket().literal("DAY").comma();
			renderer.literal("-").openingBracket();
			renderer.literal("DATEDIFF").openingBracket().literal("DAY").comma().literal("'1900-01-01'").comma().rendered(function.expression().toSql(this.dialect)).closingBracket();
			renderer.literal("%").literal("7").closingBracket().comma();
			renderer.cast().openingBracket().rendered(function.expression().toSql(this.dialect)).as().literal("DATE").closingBracket();
			renderer.closingBracket();
			return renderer.toSql();
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATETRUNC").openingBracket();
		renderer.literal(toDatePart(function.part())).comma();
		renderer.rendered(function.expression().toSql(this.dialect));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.rendered(duration.toSql(this.dialect));
		return renderer.toSql();
	}
}

/**
 * SQL Server-specific renderer for schema-related SQL statements.<br>
 * Extends {@link SqlSchemaRenderer} to render schema creation with conditional existence checks via
 * {@code sys.schemas} and {@code EXEC}, and schema dropping with optional {@code IF EXISTS}.<br>
 *
 * @author Luis-St
 */
class SqlServerSchemaRenderer extends SqlSchemaRenderer {
	
	/**
	 * Constructs a new SQL Server schema renderer for the given dialect.<br>
	 *
	 * @param dialect The dialect this renderer belongs to
	 * @throws NullPointerException If the dialect is null
	 */
	SqlServerSchemaRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		String create = "CREATE SCHEMA " + this.dialect.quoteIdentifier(name);
		if (!ifNotExists) {
			return SqlRendered.of(create);
		}
		return SqlRendered.of("IF NOT EXISTS (SELECT 1 FROM sys.schemas WHERE name = N'" + name.replace("'", "''") + "') EXEC(N'" + create.replace("'", "''") + "')");
	}
	
	@Override
	public @NonNull SqlRendered renderDropSchema(@NonNull String name, boolean ifExists, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().schema();
		if (ifExists) {
			renderer.if_().exists();
		}
		renderer.literal(this.dialect.quoteIdentifier(name));
		return renderer.toSql();
	}
}
