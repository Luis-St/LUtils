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

import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.condition.conditions.numeric.SqlModEqualsCondition;
import net.luis.utils.io.database.dialect.renderer.SqlDialectRenderer;
import net.luis.utils.io.database.dialect.renderer.SqlRenderingHelper;
import net.luis.utils.io.database.dialect.renderer.expression.condition.SqlNumericConditionRenderer;
import net.luis.utils.io.database.dialect.renderer.expression.function.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectFeatureException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import net.luis.utils.io.database.type.SqlTypeRegistry;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class H2Dialect extends AbstractSqlDialect {
	
	private static final Set<SqlFeature> SUPPORTED_FEATURES = Set.of(
		SqlFeature.CTE,
		SqlFeature.RECURSIVE_CTE,
		SqlFeature.NULLS_ORDERING,
		SqlFeature.SCHEMAS,
		SqlFeature.WINDOW_FUNCTIONS,
		SqlFeature.FOR_UPDATE,
		SqlFeature.IS_DISTINCT_FROM,
		SqlFeature.UPSERT,
		SqlFeature.TRANSACTIONAL_DDL,
		SqlFeature.ROW_LOCKING,
		SqlFeature.RENAME_INDEX,
		SqlFeature.ALTER_COLUMN,
		SqlFeature.ADD_CONSTRAINT,
		SqlFeature.DROP_CONSTRAINT,
		SqlFeature.OFFSET_WITHOUT_LIMIT
	);
	
	private static final Set<SqlIndexMethod> SUPPORTED_INDEX_METHODS = Set.of(
		SqlIndexMethod.BTREE,
		SqlIndexMethod.HASH
	);
	private static final SqlTypeRegistry TYPE_REGISTRY = SqlTypeRegistry.builder()
		.register(SqlTypes.UUID, "UUID")
		.register(SqlTypes.JSON, "JSON")
		.build();
	
	@Override
	public @NonNull String name() {
		return "H2";
	}
	
	@Override
	protected @NonNull SqlTypeRegistry createTypeRegistry() {
		return TYPE_REGISTRY;
	}
	
	@Override
	protected @NonNull SqlDialectRenderer createRenderer() {
		return SqlDialectRenderer.builder(this)
			.temporalFunctionRenderer(new H2TemporalFunctionRenderer(this))
			.numericFunctionRenderer(new H2NumericFunctionRenderer(this))
			.stringFunctionRenderer(new H2StringFunctionRenderer(this))
			.genericFunctionRenderer(new H2GenericFunctionRenderer(this))
			.numericConditionRenderer(new H2NumericConditionRenderer(this))
			.build();
	}
	
	@Override
	protected @NonNull Optional<String> getScalarTypeName(int jdbcType) {
		return switch (jdbcType) {
			case Types.CLOB -> Optional.of("CHARACTER LARGE OBJECT");
			case Types.BLOB, Types.LONGVARBINARY -> Optional.of("BINARY LARGE OBJECT");
			default -> super.getScalarTypeName(jdbcType);
		};
	}
	
	@Override
	protected @NonNull Optional<String> getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		if (parameter instanceof SqlLengthParameter length) {
			return switch (jdbcType) {
				case Types.VARCHAR -> Optional.of("CHARACTER VARYING(" + length.length() + ")");
				case Types.VARBINARY -> Optional.of("BINARY VARYING(" + length.length() + ")");
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
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql Lock mode must not be null");
		
		if (mode == SqlLockMode.FOR_SHARE) {
			throw new SqlDialectFeatureException(SqlFeature.FOR_SHARE, this);
		}
		if (skipLocked) {
			throw new SqlDialectFeatureException(SqlFeature.SKIP_LOCKED, this);
		}
		if (noWait) {
			throw new SqlDialectFeatureException(SqlFeature.NO_WAIT, this);
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.for_().update();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertClause(@NonNull SqlColumn<?, ?> conflictColumn, @NonNull List<SqlColumn<?, ?>> updateColumns) throws SqlException {
		throw new SqlDialectFeatureException(SqlFeature.UPSERT, this);
	}
	
	@Override
	public @NonNull SqlRendered renderUpsertStatement(@NonNull SqlTable<?> table, @NonNull List<SqlColumn<?, ?>> columns, @NonNull SqlColumn<?, ?> conflictColumn, @NonNull SqlRendered valueTuples) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		Objects.requireNonNull(columns, "Sql columns must not be null");
		Objects.requireNonNull(conflictColumn, "Sql conflict column must not be null");
		Objects.requireNonNull(valueTuples, "Sql value tuples must not be null");
		
		List<String> allColumns = new ArrayList<>();
		for (SqlColumn<?, ?> column : columns) {
			allColumns.add(column.name());
		}
		allColumns.addAll(table.auditConfig().map(SqlAuditConfig::columnNames).orElse(List.of()));
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.keyword("MERGE").into().literal(this.quoteIdentifier(table.name())).openingBracket();
		for (int i = 0; i < allColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(allColumns.get(i)));
		}
		
		renderer.closingBracket();
		renderer.keyword("KEY").openingBracket().literal(this.quoteIdentifier(conflictColumn.name())).closingBracket();
		renderer.values().rendered(valueTuples);
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull String getCheckConstraintsQueryString() {
		return "SELECT cc.CONSTRAINT_NAME, cc.CHECK_CLAUSE FROM INFORMATION_SCHEMA.CHECK_CONSTRAINTS cc " +
			"JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc ON tc.CONSTRAINT_SCHEMA = cc.CONSTRAINT_SCHEMA AND tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME " +
			"WHERE tc.TABLE_SCHEMA = ? AND tc.TABLE_NAME = ?";
	}
}

class H2GenericFunctionRenderer extends SqlGenericFunctionRenderer {
	
	H2GenericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderGreatest(@NonNull SqlGreatestFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderTypedVariadic("GREATEST", function.expressions());
	}
	
	@Override
	protected @NonNull SqlRendered renderLeast(@NonNull SqlLeastFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderTypedVariadic("LEAST", function.expressions());
	}
	
	@Override
	protected @NonNull SqlRendered renderCoalesce(@NonNull SqlCoalesceFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderTypedVariadic("COALESCE", function.expressions());
	}
	
	private @NonNull SqlRendered renderTypedVariadic(@NonNull String name, @NonNull List<? extends SqlExpression<?>> expressions) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(name).openingBracket();
		
		for (int i = 0; i < expressions.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			SqlExpression<?> expression = expressions.get(i);
			renderer.rendered(SqlRenderingHelper.renderCast(this.dialect, expression.toSql(this.dialect), expression.type()));
		}
		
		renderer.closingBracket();
		return renderer.toSql();
	}
}

class H2TemporalFunctionRenderer extends SqlTemporalFunctionRenderer {
	
	H2TemporalFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderExtract(@NonNull SqlExtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		String field = switch (function.part()) {
			case DAY_OF_WEEK -> "ISO_DAY_OF_WEEK";
			case DAY_OF_YEAR -> "DAY_OF_YEAR";
			default -> function.part().name();
		};
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("EXTRACT").openingBracket().keyword(field).from().rendered(function.expression().toSql(this.dialect)).closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderFromEpoch(@NonNull SqlFromEpochFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'SECOND'").comma().rendered(function.expression().toSql(this.dialect));
		renderer.comma().literal("TIMESTAMP '1970-01-01 00:00:00'");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalTruncate(@NonNull SqlTemporalTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		if (function.part() != SqlTemporalPart.WEEK) {
			SqlRenderer renderer = SqlRenderer.empty();
			renderer.literal("DATE_TRUNC").openingBracket().literal("'" + function.part().name() + "'").comma();
			renderer.rendered(this.castExpression(function.expression())).closingBracket();
			return renderer.toSql();
		}
		
		SqlRendered inner = this.castExpression(function.expression());
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket().literal("'DAY'").comma();
		renderer.literal("1").literal("-").literal("EXTRACT").openingBracket().keyword("ISO_DAY_OF_WEEK").from().rendered(inner).closingBracket().comma();
		renderer.literal("DATE_TRUNC").openingBracket().literal("'DAY'").comma().rendered(inner).closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalAdd(@NonNull SqlTemporalAddFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'" + function.part().name() + "'").comma();
		renderer.rendered(this.castExpression(function.secondSummand())).comma();
		renderer.rendered(this.castExpression(function.firstSummand()));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderTemporalSubtract(@NonNull SqlTemporalSubtractFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("DATEADD").openingBracket();
		renderer.literal("'" + function.part().name() + "'").comma();
		renderer.literal("-").openingBracket().rendered(this.castExpression(function.subtrahend())).closingBracket().comma();
		renderer.rendered(this.castExpression(function.minuend()));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	private @NonNull SqlRendered castExpression(@NonNull SqlExpression<?> expression) throws SqlException {
		return SqlRenderingHelper.renderCast(this.dialect, expression.toSql(this.dialect), expression.type());
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

class H2NumericFunctionRenderer extends SqlNumericFunctionRenderer {
	
	H2NumericFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderRandom(@NonNull SqlRandomFunction function) throws SqlException {
		return SqlRendered.of("RAND()");
	}
	
	@Override
	protected @NonNull SqlRendered renderCeil(@NonNull SqlCeilFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderCastedCall("CEIL", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderFloor(@NonNull SqlFloorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderCastedCall("FLOOR", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderMod(@NonNull SqlModFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderCastedCall("MOD", function.expression(), function.divisor());
	}
	
	@Override
	protected @NonNull SqlRendered renderRound(@NonNull SqlRoundFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		SqlExpression<? extends Number> precision = function.precision();
		if (precision != null) {
			return this.renderCastedCall("ROUND", function.expression(), precision);
		}
		return this.renderCastedCall("ROUND", function.expression());
	}
	
	@Override
	protected @NonNull SqlRendered renderTruncate(@NonNull SqlNumericTruncateFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("TRUNCATE").openingBracket();
		renderer.rendered(SqlRenderingHelper.renderCast(this.dialect, function.expression().toSql(this.dialect), function.expression().type()));
		renderer.comma().literal("0");
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	private @NonNull SqlRendered renderCastedCall(@NonNull String name, @NonNull SqlExpression<?> @NonNull ... arguments) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(name).openingBracket();
		for (int i = 0; i < arguments.length; i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(SqlRenderingHelper.renderCast(this.dialect, arguments[i].toSql(this.dialect), arguments[i].type()));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderBitwiseAnd(@NonNull SqlBitwiseAndFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderBitwiseFunction("BITAND", function.firstOperand(), function.secondOperand());
	}
	
	@Override
	protected @NonNull SqlRendered renderBitwiseOr(@NonNull SqlBitwiseOrFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderBitwiseFunction("BITOR", function.firstOperand(), function.secondOperand());
	}
	
	@Override
	protected @NonNull SqlRendered renderBitwiseXor(@NonNull SqlBitwiseXorFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderBitwiseFunction("BITXOR", function.firstOperand(), function.secondOperand());
	}
	
	@Override
	protected @NonNull SqlRendered renderBitwiseNot(@NonNull SqlBitwiseNotFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		return this.renderBitwiseFunction("BITNOT", function.expression());
	}
	
	private @NonNull SqlRendered renderBitwiseFunction(@NonNull String name, @NonNull SqlExpression<?> @NonNull ... operands) throws SqlException {
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(name).openingBracket();
		for (int i = 0; i < operands.length; i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(SqlRenderingHelper.renderCast(this.dialect, operands[i].toSql(this.dialect), operands[i].type()));
		}
		renderer.closingBracket();
		return renderer.toSql();
	}
}

class H2StringFunctionRenderer extends SqlStringFunctionRenderer {
	
	H2StringFunctionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderHex(@NonNull SqlHexFunction function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("UPPER").openingBracket();
		renderer.literal("RAWTOHEX").openingBracket().cast().openingBracket().rendered(function.expression().toSql(this.dialect)).as().literal("VARBINARY").closingBracket().closingBracket();
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	protected @NonNull SqlRendered renderUnhex(@NonNull SqlUnhexFunction<?> function) throws SqlException {
		Objects.requireNonNull(function, "Sql function must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.cast().openingBracket();
		renderer.literal("HEXTORAW").openingBracket();
		renderer.literal("REGEXP_REPLACE").openingBracket().rendered(function.expression().toSql(this.dialect)).comma().literal("'(..)'").comma().literal("'00$1'").closingBracket();
		renderer.closingBracket();
		renderer.as().literal("VARBINARY").closingBracket();
		return renderer.toSql();
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
		renderer.literal("GROUP_CONCAT").openingBracket();
		if (distinct) {
			renderer.distinct();
		}
		if (first != null) {
			renderer.rendered(first.toSql(this.dialect));
		}
		if (ordered && first != null) {
			renderer.orderBy().rendered(first.toSql(this.dialect));
		}
		separator.ifPresent(s -> renderer.literal("SEPARATOR").parameter(DEFAULT_STRING_TYPE, s));
		renderer.closingBracket();
		return renderer.toSql();
	}
}

class H2NumericConditionRenderer extends SqlNumericConditionRenderer {
	
	H2NumericConditionRenderer(@NonNull SqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected @NonNull SqlRendered renderModEquals(@NonNull SqlModEqualsCondition condition) throws SqlException {
		Objects.requireNonNull(condition, "Sql condition must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal("MOD").openingBracket();
		renderer.rendered(this.castExpression(condition.value())).comma().rendered(this.castExpression(condition.divisor()));
		renderer.closingBracket();
		renderer.literal("=").rendered(this.castExpression(condition.remainder()));
		return renderer.toSql();
	}
	
	private @NonNull SqlRendered castExpression(@NonNull SqlExpression<?> expression) throws SqlException {
		return SqlRenderingHelper.renderCast(this.dialect, expression.toSql(this.dialect), expression.type());
	}
}
