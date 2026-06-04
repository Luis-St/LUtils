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

import net.luis.utils.io.database.condition.conditions.comparison.SqlIsDistinctFromCondition;
import net.luis.utils.io.database.dialect.renderer.*;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlComparisonConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlStringConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.numeric.SqlRandomFunction;
import net.luis.utils.io.database.function.functions.string.SqlConcatFunction;
import net.luis.utils.io.database.function.functions.temporal.*;
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

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class MySqlDialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.FOR_SHARE,
		SqlFeature.SKIP_LOCKED,
		SqlFeature.NO_WAIT,
		SqlFeature.UPSERT_SUFFIX,
		SqlFeature.ROW_LOCKING,
		SqlFeature.INSERT_OR_IGNORE,
		SqlFeature.RENAME_INDEX,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT,
		SqlFeature.JOINED_DML
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH
	);
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.JSON, "JSON")
		.build();
	
	@Override
	public @NonNull String name() {
		return "MySQL";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.stringFunctionRenderer(new MySqlStringFunctionRenderer(this))
			.numericFunctionRenderer(new MySqlNumericFunctionRenderer(this))
			.temporalFunctionRenderer(new MySqlTemporalFunctionRenderer(this))
			.stringConditionRenderer(new MySqlStringConditionRenderer(this))
			.comparisonConditionRenderer(new MySqlComparisonConditionRenderer(this))
			.tableRenderer(new MySqlTableRenderer(this))
			.indexRenderer(new MySqlIndexRenderer(this))
			.columnRenderer(new MySqlColumnRenderer(this))
			.migrationRenderer(new MySqlMigrationOperationRenderer(this))
			.schemaRenderer(new MySqlSchemaRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull Optional<String> getScalarTypeName(int jdbcType) {
		return switch (jdbcType) {
			case Types.BOOLEAN -> Optional.of("TINYINT(1)");
			case Types.TINYINT -> Optional.of("TINYINT");
			case Types.LONGVARCHAR, Types.LONGNVARCHAR, Types.NCLOB, Types.CLOB -> Optional.of("LONGTEXT");
			case Types.LONGVARBINARY, Types.BLOB -> Optional.of("LONGBLOB");
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull Optional<String> getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlFractionalParameter fractional) {
			return switch (jdbcType) {
				case Types.TIMESTAMP_WITH_TIMEZONE -> Optional.of("DATETIME(" + fractional.digits() + ")");
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
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return SUPPORTED_INDEX_METHODS.contains(method);
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "`" + identifier.replace("`", "``") + "`";
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		Objects.requireNonNull(conflictColumn, "Sql conflict column must not be null");
		Objects.requireNonNull(updateColumns, "Sql update columns must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.on().literal("DUPLICATE").key().update();
		
		for (int i = 0; i < updateColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			String quotedName = this.quoteIdentifier(updateColumns.get(i).name());
			renderer.literal(quotedName).literal("=").literal("VALUES(" + quotedName + ")");
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreModifier() throws SqlException {
		return SqlRendered.of("IGNORE");
	}
	
	@Override
	public @NonNull SqlRendered renderInsertOrIgnoreSuffix(@NonNull List<SqlColumn<?, ?>> conflictColumns) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.INSERT_OR_IGNORE, this);
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "SELECT CONSTRAINT_NAME, CHECK_CLAUSE FROM information_schema.CHECK_CONSTRAINTS WHERE CONSTRAINT_SCHEMA = ? AND TABLE_NAME = ?";
	}
}

class MySqlTableRenderer extends SqlTableRenderer {
	
	MySqlTableRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.rendered(this.renderAutoIncrementKeyword());
	}
	
	@Override
	public @NonNull SqlRendered renderAutoIncrementKeyword() throws SqlException {
		return SqlRenderer.empty().literal("AUTO_INCREMENT").toSql();
	}
}

class MySqlIndexRenderer extends SqlIndexRenderer {
	
	MySqlIndexRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@Nullable SqlTable<?> owningTable, @NonNull String indexName) throws SqlException {
		Objects.requireNonNull(owningTable, "Sql index owning table must not be null");
		return this.renderStandardDropIndexOnTable(owningTable, indexName);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameIndex(@Nullable SqlTable<?> table, @NonNull String from, @NonNull String to) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(from, "Sql source index name must not be null");
		Objects.requireNonNull(to, "Sql target index name must not be null");
		
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(table.name())).literal("RENAME").index().literal(this.dialect.quoteIdentifier(from)).to().literal(this.dialect.quoteIdentifier(to)).toSql();
	}
}

class MySqlColumnRenderer extends SqlColumnRenderer {
	
	MySqlColumnRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnType(@NonNull SqlColumn<?, ?> column, @NonNull SqlType<?> newType) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(newType, "New sql type must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		return SqlRenderer.empty().alter().table().literal(this.dialect.quoteIdentifier(tableName)).modify().column().literal(this.dialect.quoteIdentifier(columnName)).literal(this.dialect.getTypeName(newType)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderAlterColumnNullability(@NonNull SqlColumn<?, ?> column, boolean nullable) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		String tableName = column.owningTable().name();
		String columnName = column.name();
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.alter().table().literal(this.dialect.quoteIdentifier(tableName)).modify().column().literal(this.dialect.quoteIdentifier(columnName)).literal(this.dialect.getTypeName(column.type()));
		
		if (nullable) {
			return renderer.null_().toSql();
		} else {
			return renderer.not().null_().toSql();
		}
	}
}

class MySqlSchemaRenderer extends SqlSchemaRenderer {
	
	MySqlSchemaRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateSchema(@NonNull String name, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().database();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		return renderer.literal(this.dialect.quoteIdentifier(name)).toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropSchema(@NonNull String name, boolean ifExists, boolean cascade) throws SqlException {
		Objects.requireNonNull(name, "Sql schema name must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().database();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		return renderer.literal(this.dialect.quoteIdentifier(name)).toSql();
	}
}

class MySqlMigrationOperationRenderer extends SqlMigrationOperationRenderer {
	
	MySqlMigrationOperationRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	public @NonNull SqlRendered renderRenameTable(@NonNull SqlTable<?> fromTable, @NonNull SqlTable<?> toTable) throws SqlException {
		Objects.requireNonNull(fromTable, "Sql source table must not be null");
		Objects.requireNonNull(toTable, "Sql target table must not be null");
		
		return SqlRenderer.empty().rename().table().literal(this.dialect.quoteIdentifier(fromTable.name())).to().literal(this.dialect.quoteIdentifier(toTable.name())).toSql();
	}
}

class MySqlNumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	MySqlNumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		return SqlRendered.of("RAND()");
	}
}

class MySqlStringConditionRenderer extends SqlStringConditionRenderer {
	
	MySqlStringConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderConcatExpression(@NonNull SqlRenderer renderer, @NonNull SqlRendered left, @NonNull SqlRendered right) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(left, "Left sql rendered must not be null");
		Objects.requireNonNull(right, "Right sql rendered must not be null");
		
		return renderer.literal("CONCAT").openingBracket().rendered(left).comma().rendered(right).closingBracket().toSql();
	}
}

class MySqlComparisonConditionRenderer extends SqlComparisonConditionRenderer {
	
	MySqlComparisonConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderIsDistinctFrom(@NonNull SqlIsDistinctFromCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.not().openingBracket().rendered(condition.first().toSql(this.dialect)).literal("<=>").rendered(condition.second().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
}

class MySqlStringFunctionRenderer extends SqlStringFunctionRenderer {
	
	MySqlStringFunctionRenderer(@NonNull SqlDialect dialect) {
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
			renderer.literal("GROUP_CONCAT").openingBracket();
			if (distinct) {
				renderer.distinct();
			}
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
			if (ordered && !values.isEmpty()) {
				renderer.orderBy().rendered(values.getFirst().toSql(this.dialect));
			}
			separator.ifPresent(s -> renderer.literal("SEPARATOR").parameter(DEFAULT_STRING_TYPE, s));
			renderer.closingBracket();
		} else if (separator.isPresent()) {
			renderer.literal("CONCAT_WS").openingBracket();
			renderer.parameter(DEFAULT_STRING_TYPE, separator.get());
			for (SqlExpression<? extends CharSequence> value : values) {
				renderer.comma().rendered(value.toSql(this.dialect));
			}
			renderer.closingBracket();
		} else {
			renderer.literal("CONCAT").openingBracket();
			for (int i = 0; i < values.size(); i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(values.get(i).toSql(this.dialect));
			}
			renderer.closingBracket();
		}
		return renderer.toSql();
	}
}

class MySqlTemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	MySqlTemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "FROM_UNIXTIME", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeDate(@NonNull SqlMakeDateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("STR_TO_DATE").openingBracket();
		renderer.literal("CONCAT").openingBracket();
		renderer.rendered(function.year().toSql(this.dialect)).comma().literal("'-'").comma();
		renderer.rendered(function.month().toSql(this.dialect)).comma().literal("'-'").comma();
		renderer.rendered(function.day().toSql(this.dialect));
		renderer.closingBracket().comma().literal("'%Y-%m-%d'").closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderMakeTime(@NonNull SqlMakeTimeFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "MAKETIME", function.hour(), function.minute(), function.second());
	}
	
	@Override
	protected @NonNull SqlRendered renderToEpoch(@NonNull SqlToEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		return SqlRenderingHelper.renderFunctionCall(this.dialect, "UNIX_TIMESTAMP", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_ADD").openingBracket();
		renderer.rendered(function.firstSummand().toSql(this.dialect)).comma();
		renderer.literal("INTERVAL").rendered(function.secondSummand().toSql(this.dialect)).literal(function.part().name());
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATE_SUB").openingBracket();
		renderer.rendered(function.minuend().toSql(this.dialect)).comma();
		renderer.literal("INTERVAL").rendered(function.subtrahend().toSql(this.dialect)).literal(function.part().name());
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderInterval(@NonNull SqlExpression<?> duration, @NonNull String part) throws SqlException {
		Objects.requireNonNull(duration, "Sql duration expression must not be null");
		Objects.requireNonNull(part, "Temporal part must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("INTERVAL").rendered(duration.toSql(this.dialect)).literal(part);
		return renderer.toSql();
	}
}
