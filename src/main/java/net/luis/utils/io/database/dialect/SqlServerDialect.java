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
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.conditions.comparison.SqlIsDistinctFromCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlComparisonConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlStringConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import net.luis.utils.io.database.function.functions.string.SqlLengthFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.SqlFractionalParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.SQLXML;
import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class SqlServerDialect extends AbstractSqlDialect {
	
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
		SqlFeature.DROP_CONSTRAINT
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH,
		SqlIndexMethod.CLUSTERED,
		SqlIndexMethod.NONCLUSTERED,
		SqlIndexMethod.COLUMNSTORE
	);
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
			.tableRenderer(new SqlServerTableRenderer(this))
			.indexRenderer(new SqlServerIndexRenderer(this))
			.columnRenderer(new SqlServerColumnRenderer(this))
			.migrationRenderer(new SqlServerMigrationOperationRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedRenderingException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BIT";
			case Types.TINYINT -> "TINYINT";
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCLOB -> "NVARCHAR(MAX)";
			case Types.CLOB -> "VARCHAR(MAX)";
			case Types.LONGVARBINARY, Types.BLOB -> "VARBINARY(MAX)";
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedRenderingException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlFractionalParameter fractional) {
			return switch (jdbcType) {
				case Types.TIMESTAMP -> "DATETIME2(" + fractional.digits() + ")";
				case Types.TIMESTAMP_WITH_TIMEZONE -> "DATETIMEOFFSET(" + fractional.digits() + ")";
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

class SqlServerTableRenderer extends SqlTableRenderer {
	
	SqlServerTableRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("IDENTITY").openingBracket().literal("1").comma().literal("1").closingBracket();
	}
}

class SqlServerIndexRenderer extends SqlIndexRenderer {
	
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

class SqlServerColumnRenderer extends SqlColumnRenderer {
	
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

class SqlServerMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
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

class SqlServerNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	SqlServerNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		return SqlRendered.of("RAND()");
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

class SqlServerComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
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

class SqlServerStringConditionRenderer extends SqlStringConditionRenderer {
	
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

class SqlServerStringFunctionRenderer extends SqlStringFunctionRenderer {
	
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
}

class SqlServerTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	SqlServerTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("SECOND").comma().rendered(function.expression().toSql(this.dialect));
		renderer.comma().literal("'1970-01-01'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEDIFF").openingBracket();
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
		renderer.literal(function.part().name()).comma();
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
		renderer.literal(function.part().name()).comma();
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
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATETRUNC").openingBracket();
		renderer.literal(function.part().name()).comma();
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

