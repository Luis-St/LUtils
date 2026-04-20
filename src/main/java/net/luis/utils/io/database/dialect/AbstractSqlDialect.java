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

import com.google.common.collect.Lists;
import net.luis.utils.function.throwable.ThrowableBiConsumer;
import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.condition.NegatedSqlCondition;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.*;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.dialect.*;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.orderable.*;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.function.functions.*;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.query.SqlLockMode;
import net.luis.utils.io.database.query.SqlSetOperation;
import net.luis.utils.io.database.rendering.SqlRendered;
import net.luis.utils.io.database.rendering.SqlRenderer;
import net.luis.utils.io.database.table.*;
import net.luis.utils.io.database.type.*;
import net.luis.utils.io.database.type.parameter.*;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.sql.Types;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public abstract class AbstractSqlDialect implements SqlDialect {
	
	@Override
	public boolean isTypeSupported(@NonNull SqlType<?> type) {
		Objects.requireNonNull(type, "Sql type must not be null");
		return !(type.getBaseType() instanceof SqlArrayType<?>);
	}
	
	@Override
	public @NonNull String getTypeName(@NonNull SqlType<?> type) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(type, "Sql type must not be null");
		if (!this.isTypeSupported(type)) {
			throw new SqlDialectUnsupportedTypeException("Sql type " + type + " is not supported by dialect " + this.name());
		}
		
		return switch (type.getBaseType()) {
			case SqlScalarType<?> scalar -> this.getScalarTypeName(scalar.jdbcType());
			case ParameterizedSqlType<?, ?> parameterized -> this.getParameterizedTypeName(parameterized.jdbcType(), parameterized.parameter());
			default -> throw new SqlDialectUnsupportedTypeException("Unknown sql type structure: " + type);
		};
	}
	
	protected @NonNull String getScalarTypeName(int jdbcType) throws SqlDialectUnsupportedTypeException {
		return switch (jdbcType) {
			case Types.BOOLEAN -> "BOOLEAN";
			case Types.TINYINT -> "TINYINT";
			case Types.SMALLINT -> "SMALLINT";
			case Types.INTEGER -> "INTEGER";
			case Types.BIGINT -> "BIGINT";
			case Types.REAL -> "REAL";
			case Types.FLOAT -> "FLOAT";
			case Types.DOUBLE -> "DOUBLE PRECISION";
			case Types.LONGVARCHAR -> "TEXT";
			case Types.LONGNVARCHAR, Types.NCLOB -> "NCLOB";
			case Types.CLOB -> "CLOB";
			case Types.LONGVARBINARY, Types.BLOB -> "BLOB";
			case Types.DATE -> "DATE";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported JDBC scalar type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getParameterizedTypeName(int jdbcType, @NonNull SqlParameter parameter) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(parameter, "Sql parameter must not be null");
		
		return switch (parameter) {
			case SqlLengthParameter length -> this.getLengthParameterizedTypeName(jdbcType, length);
			case SqlPrecisionParameter precision -> this.getPrecisionParameterizedTypeName(jdbcType, precision);
			case SqlFractionalParameter fractional -> this.getFractionalParameterizedTypeName(jdbcType, fractional);
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported sql parameter type: " + parameter.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getLengthParameterizedTypeName(int jdbcType, @NonNull SqlLengthParameter length) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(length, "Length parameter must not be null");
		
		return switch (jdbcType) {
			case Types.CHAR -> "CHAR(" + length.length() + ")";
			case Types.VARCHAR -> "VARCHAR(" + length.length() + ")";
			case Types.NCHAR -> "NCHAR(" + length.length() + ")";
			case Types.NVARCHAR -> "NVARCHAR(" + length.length() + ")";
			case Types.BINARY -> "BINARY(" + length.length() + ")";
			case Types.VARBINARY -> "VARBINARY(" + length.length() + ")";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported length-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getPrecisionParameterizedTypeName(int jdbcType, @NonNull SqlPrecisionParameter precision) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(precision, "Precision parameter must not be null");
		
		return switch (jdbcType) {
			case Types.NUMERIC -> "NUMERIC(" + precision.precision() + ", " + precision.scale() + ")";
			case Types.DECIMAL -> "DECIMAL(" + precision.precision() + ", " + precision.scale() + ")";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported precision-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	protected @NonNull String getFractionalParameterizedTypeName(int jdbcType, @NonNull SqlFractionalParameter fractional) throws SqlDialectUnsupportedTypeException {
		Objects.requireNonNull(fractional, "Fractional parameter must not be null");
		
		return switch (jdbcType) {
			case Types.TIME -> "TIME(" + fractional.digits() + ")";
			case Types.TIMESTAMP -> "TIMESTAMP(" + fractional.digits() + ")";
			case Types.TIME_WITH_TIMEZONE -> "TIME(" + fractional.digits() + ") WITH TIME ZONE";
			case Types.TIMESTAMP_WITH_TIMEZONE -> "TIMESTAMP(" + fractional.digits() + ") WITH TIME ZONE";
			default -> throw new SqlDialectUnsupportedTypeException("Unsupported fractional-parameterized JDBC type: " + jdbcType + " in dialect " + this.name());
		};
	}
	
	@Override
	public @NonNull SqlRendered renderExpression(@NonNull SqlExpression<?> expression) throws SqlException {
		return switch (expression) {
			case OrderedSqlExpression<?> expr -> this.renderExpression(expr.expression());
			case SqlFunction<?> func -> this.renderFunction(func);
			
			case null -> throw new NullPointerException("Sql expression must not be null");
			default -> throw new SqlDialectUnsupportedExpressionException("Unknown sql expression type: " + expression.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected <T> void renderList(@NonNull SqlRenderer renderer, @NonNull List<T> values, @NonNull ThrowableBiConsumer<SqlRenderer, T, SqlException> itemRenderer) throws SqlException {
		Objects.requireNonNull(values, "Values list must not be null");
		Objects.requireNonNull(itemRenderer, "Item renderer function must not be null");
		
		for (int i = 0; i < values.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			
			itemRenderer.accept(renderer, values.get(i));
		}
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderInfix(@NonNull SqlExpression<?> first, @NonNull String operator, @NonNull SqlExpression<?> second) {
		Objects.requireNonNull(first, "First operand must not be null");
		Objects.requireNonNull(operator, "Operator must not be null");
		Objects.requireNonNull(second, "Second operand must not be null");
		
		return renderer -> renderer.rendered(first.toSql(this)).literal(operator).rendered(second.toSql(this)).toSql();
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderFunction(@NonNull String functionName, SqlExpression<?> @NonNull ... arguments) {
		Objects.requireNonNull(functionName, "Function name must not be null");
		Objects.requireNonNull(arguments, "Arguments must not be null");
		
		return renderer -> {
			renderer.literal(functionName).openingBracket();
			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(arguments[i].toSql(this));
			}
			renderer.closingBracket();
			return renderer.toSql();
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderLiteral(@NonNull String literal) {
		Objects.requireNonNull(literal, "Literal must not be null");
		return renderer -> renderer.literal(literal).toSql();
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderCast(@NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> inner, @NonNull SqlType<?> type) {
		Objects.requireNonNull(inner, "Inner renderer must not be null");
		Objects.requireNonNull(type, "Target type must not be null");
		return renderer -> renderer.cast().openingBracket().rendered(inner.apply(SqlRenderer.empty())).as().literal(this.getTypeName(type)).closingBracket().toSql();
	}
	
	protected @NonNull SqlRendered renderFunctionWithList(@NonNull SqlRenderer renderer, @NonNull List<? extends SqlExpression<?>> values) throws SqlException {
		Objects.requireNonNull(renderer, "Renderer must not be null");
		Objects.requireNonNull(values, "Values list must not be null");
		
		renderer.openingBracket();
		this.renderList(renderer, values, (r, item) -> r.rendered(item.toSql(this)));
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderWindowCall(@NonNull String functionName, @NonNull SqlWindowClause over, SqlExpression<?> @NonNull ... arguments) {
		Objects.requireNonNull(functionName, "Function name must not be null");
		Objects.requireNonNull(over, "Window clause must not be null");
		Objects.requireNonNull(arguments, "Arguments must not be null");
		
		return renderer -> {
			renderer.literal(functionName).openingBracket();
			for (int i = 0; i < arguments.length; i++) {
				if (i > 0) {
					renderer.comma();
				}
				renderer.rendered(arguments[i].toSql(this));
			}
			renderer.closingBracket().over().openingBracket().rendered(over.toSql(this)).closingBracket();
			return renderer.toSql();
		};
	}
	
	protected SqlExpression<?> @NonNull [] collectOptionalArgs(@Nullable SqlExpression<?> @NotNull ... arguments) {
		Objects.requireNonNull(arguments, "Arguments array must not be null");
		return Lists.newArrayList(arguments).stream().filter(Objects::nonNull).toArray(SqlExpression[]::new);
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderCountDistinctFunction(@NotNull SqlCountDistinctFunction function) {
		Objects.requireNonNull(function, "Count distinct function must not be null");
		
		return renderer -> {
			renderer.literal("COUNT").openingBracket().distinct();
			
			SqlExpression<?> value = function.value();
			if (value != null) {
				renderer.rendered(value.toSql(this));
			} else {
				renderer.literal("*");
			}
			
			renderer.closingBracket();
			return renderer.toSql();
		};
	}
	
	@SuppressWarnings("DuplicatedCode")
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderConcatFunction(@NonNull SqlConcatFunction<?> function) {
		Objects.requireNonNull(function, "Concat function must not be null");
		
		return renderer -> {
			List<? extends SqlExpression<? extends CharSequence>> values = function.values();
			Optional<String> separator = function.separator();
			boolean distinct = function.distinct();
			boolean ordered = function.ordered();
			
			if (distinct || ordered) {
				renderer.literal("STRING_AGG").openingBracket();
				
				if (distinct) {
					renderer.distinct();
				}
				
				SqlExpression<? extends CharSequence> first = values.isEmpty() ? null : values.getFirst();
				if (first != null) {
					renderer.rendered(first.toSql(this));
				}
				
				renderer.comma().parameter(separator.orElse(""));
				
				if (ordered && first != null) {
					renderer.orderBy().rendered(first.toSql(this));
				}
				renderer.closingBracket();
			} else {
				for (int i = 0; i < values.size(); i++) {
					if (i > 0) {
						renderer.literal("||");
						separator.ifPresent(s -> renderer.parameter(s).literal("||"));
					}
					
					renderer.rendered(values.get(i).toSql(this));
				}
			}
			
			return renderer.toSql();
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderSubstringFunction(@NonNull SqlSubstringFunction<?> function) {
		Objects.requireNonNull(function, "Substring function must not be null");
		
		return renderer -> {
			renderer.literal("SUBSTRING").openingBracket().rendered(function.value().toSql(this)).from().rendered(function.start().toSql(this));
			
			SqlExpression<? extends Number> length = function.length();
			if (length != null) {
				renderer.for_().rendered(length.toSql(this));
			}
			
			renderer.closingBracket();
			return renderer.toSql();
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderCaseWhenFunction(@NonNull SqlCaseWhenFunction<?> function) {
		Objects.requireNonNull(function, "Case when function must not be null");
		
		return renderer -> {
			renderer.case_();
			
			for (SqlCaseWhenBranch<?> branch : function.branches()) {
				renderer.when().rendered(branch.condition().toSql(this));
				renderer.then().rendered(branch.value().toSql(this));
			}
			
			SqlExpression<?> elseValue = function.elseValue();
			if (elseValue != null) {
				renderer.else_().rendered(elseValue.toSql(this));
			}
			
			renderer.end();
			return renderer.toSql();
		};
	}
	
	@Override
	public @NonNull SqlRendered renderFunction(@NonNull SqlFunction<?> function) throws SqlException {
		return (switch (function) {
			case SqlAggregateFunction<?> func -> this.renderAggregateFunction(func);
			case SqlNumericFunction<?> func -> this.renderNumericFunction(func);
			case SqlStringFunction<?> func -> this.renderStringFunction(func);
			case SqlTemporalFunction<?> func -> this.renderTemporalFunction(func);
			case SqlWindowFunction<?> func -> this.renderWindowFunction(func);
			
			case null -> throw new NullPointerException("Sql function must not be null");
			default -> this.renderGenericFunction(function);
		}).apply(SqlRenderer.empty());
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderAggregateFunction(@NonNull SqlAggregateFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlAverageFunction func -> this.renderFunction("AVG", func.value());
			case SqlCountFunction(@Nullable SqlExpression<?> value) -> value != null ? this.renderFunction("COUNT", value) : this.renderLiteral("COUNT(*)");
			case SqlCountDistinctFunction func -> this.renderCountDistinctFunction(func);
			case SqlMaxFunction<?> func -> this.renderFunction("MAX", func.value());
			case SqlMinFunction<?> func -> this.renderFunction("MIN", func.value());
			case SqlSumFunction<?> func -> this.renderFunction("SUM", func.value());
			
			case null -> throw new NullPointerException("Sql aggregate function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql aggregate function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderNumericFunction(@NonNull SqlNumericFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlBitwiseAndFunction<?> func -> this.renderInfix(func.firstOperand(), "&", func.secondOperand());
			case SqlBitwiseNotFunction<?> func -> renderer -> renderer.literal("~").openingBracket().rendered(func.value().toSql(this)).closingBracket().toSql();
			case SqlBitwiseOrFunction<?> func -> this.renderInfix(func.firstOperand(), "|", func.secondOperand());
			case SqlBitwiseXorFunction<?> func -> this.renderInfix(func.firstOperand(), "^", func.secondOperand());
			
			case SqlAcosFunction func -> this.renderFunction("ACOS", func.value());
			case SqlAsinFunction func -> this.renderFunction("ASIN", func.value());
			case SqlAtan2Function(var y, var x) -> this.renderFunction("ATAN2", y, x);
			case SqlAtanFunction func -> this.renderFunction("ATAN", func.value());
			case SqlCosFunction func -> this.renderFunction("COS", func.value());
			case SqlSinFunction func -> this.renderFunction("SIN", func.value());
			case SqlTanFunction func -> this.renderFunction("TAN", func.value());
			
			case SqlAbsFunction<?> func -> this.renderFunction("ABS", func.value());
			case SqlCeilFunction<?> func -> this.renderFunction("CEIL", func.value());
			case SqlDegreesFunction func -> this.renderFunction("DEGREES", func.value());
			case SqlExpFunction func -> this.renderFunction("EXP", func.value());
			case SqlFloorFunction<?> func -> this.renderFunction("FLOOR", func.value());
			case SqlLogFunction(var value, @Nullable SqlExpression<? extends Number> base) -> base != null ? this.renderFunction("LOG", base, value) : this.renderFunction("LN", value);
			case SqlModFunction(var dividend, var divisor) -> this.renderFunction("MOD", dividend, divisor);
			case SqlNegateFunction<?> func -> renderer -> renderer.literal("-").openingBracket().rendered(func.value().toSql(this)).closingBracket().toSql();
			case SqlPiFunction _ -> this.renderLiteral("PI()");
			case SqlPowFunction(var value, var exponent) -> this.renderFunction("POWER", value, exponent);
			case SqlRadiansFunction func -> this.renderFunction("RADIANS", func.value());
			case SqlRandomFunction _ -> this.renderLiteral("RANDOM()");
			case SqlRoundFunction(var value, @Nullable SqlExpression<? extends Number> precision) -> precision != null ? this.renderFunction("ROUND", value, precision) : this.renderFunction("ROUND", value);
			case SqlSignFunction func -> this.renderFunction("SIGN", func.value());
			case SqlSqrtFunction func -> this.renderFunction("SQRT", func.value());
			case SqlNumericTruncateFunction<?> func -> this.renderFunction("TRUNCATE", func.value());
			
			case null -> throw new NullPointerException("Sql numeric function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql numeric function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderStringFunction(@NonNull SqlStringFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlConcatFunction<?> func -> this.renderConcatFunction(func);
			case SqlHexFunction func -> this.renderFunction("HEX", func.value());
			case SqlLeftFunction(var value, var length) -> this.renderFunction("LEFT", value, length);
			case SqlLeftPadFunction(var value, var length, var fill) -> this.renderFunction("LPAD", value, length, fill);
			case SqlLeftTrimFunction<?> func -> this.renderFunction("LTRIM", func.value());
			case SqlLengthFunction func -> this.renderFunction("LENGTH", func.value());
			case SqlLowerFunction<?> func -> this.renderFunction("LOWER", func.value());
			case SqlPositionFunction(var substring, var string) -> renderer -> renderer.literal("POSITION").openingBracket().rendered(substring.toSql(this)).in().rendered(string.toSql(this)).closingBracket().toSql();
			case SqlReplaceFunction(var string, var target, var replacement) -> this.renderFunction("REPLACE", string, target, replacement);
			case SqlRightFunction(var value, var length) -> this.renderFunction("RIGHT", value, length);
			case SqlRightPadFunction(var value, var length, var fill) -> this.renderFunction("RPAD", value, length, fill);
			case SqlRightTrimFunction<?> func -> this.renderFunction("RTRIM", func.value());
			case SqlSubstringFunction<?> func -> this.renderSubstringFunction(func);
			case SqlTrimCharsFunction(var value, var chars) -> renderer -> renderer.literal("TRIM").openingBracket().rendered(chars.toSql(this)).from().rendered(value.toSql(this)).closingBracket().toSql();
			case SqlTrimFunction<?> func -> this.renderFunction("TRIM", func.value());
			case SqlUnhexFunction func -> this.renderFunction("UNHEX", func.value());
			case SqlUpperFunction<?> func -> this.renderFunction("UPPER", func.value());
			
			case null -> throw new NullPointerException("Sql string function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql string function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderTemporalFunction(@NonNull SqlTemporalFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlAddFunction(var firstSummand, var secondSummand, var type) -> this.renderCast(this.renderInfix(firstSummand, "+", secondSummand), type);
			case SqlCurrentDateFunction() -> this.renderLiteral("CURRENT_DATE");
			case SqlCurrentTimeFunction() -> this.renderLiteral("CURRENT_TIME");
			case SqlCurrentTimestampFunction() -> this.renderLiteral("CURRENT_TIMESTAMP");
			case SqlExtractFunction(var value, var part) -> renderer -> renderer.literal("EXTRACT").openingBracket().keyword(part.name()).from().rendered(value.toSql(this)).closingBracket().toSql();
			case SqlFromEpochFunction(var epoch, var type) -> this.renderCast(this.renderFunction("FROM_UNIXTIME", epoch), type);
			case SqlMakeDateFunction(var year, var month, var day, var type) -> this.renderCast(this.renderFunction("MAKE_DATE", year, month, day), type);
			case SqlMakeTimeFunction(var hour, var minute, var second, var type) -> this.renderCast(this.renderFunction("MAKE_TIME", hour, minute, second), type);
			case SqlNowFunction() -> this.renderLiteral("CURRENT_TIMESTAMP");
			case SqlSubtractFunction(var minuend, var subtrahend, var type) -> this.renderCast(this.renderInfix(minuend, "-", subtrahend), type);
			case SqlToDateFunction(var value, var type) -> renderer -> renderer.cast().openingBracket().rendered(value.toSql(this)).as().literal(this.getTypeName(type)).closingBracket().toSql();
			case SqlToTimeFunction(var value, var type) -> renderer -> renderer.cast().openingBracket().rendered(value.toSql(this)).as().literal(this.getTypeName(type)).closingBracket().toSql();
			case SqlToEpochFunction func -> renderer -> renderer.literal("EXTRACT").openingBracket().keyword("EPOCH").from().rendered(func.value().toSql(this)).closingBracket().toSql();
			case SqlTemporalTruncateFunction(var value, var part, var type) ->
				this.renderCast(renderer -> renderer.literal("DATE_TRUNC").openingBracket().keyword(part.name()).comma().rendered(value.toSql(this)).closingBracket().toSql(), type);
			
			case null -> throw new NullPointerException("Sql temporal function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql temporal function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderWindowFunction(@NonNull SqlWindowFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlCumulativeDistributionFunction func -> this.renderWindowCall("CUME_DIST", func.over());
			case SqlDenseRankFunction func -> this.renderWindowCall("DENSE_RANK", func.over());
			case SqlFirstValueFunction(var column, var over) -> this.renderWindowCall("FIRST_VALUE", over, column);
			case SqlLagFunction(var column, var nullableOffset, var nullableDefaultValue, var over) -> this.renderWindowCall("LAG", over, this.collectOptionalArgs(column, nullableOffset, nullableDefaultValue));
			case SqlLastValueFunction(SqlExpression<?> column, SqlWindowClause over) -> this.renderWindowCall("LAST_VALUE", over, column);
			case SqlLeadFunction(var column, var nullableOffset, var nullableDefaultValue, var over) -> this.renderWindowCall("LEAD", over, this.collectOptionalArgs(column, nullableOffset, nullableDefaultValue));
			case SqlPercentRankFunction func -> this.renderWindowCall("PERCENT_RANK", func.over());
			case SqlRankFunction func -> this.renderWindowCall("RANK", func.over());
			case SqlRowNumberFunction func -> this.renderWindowCall("ROW_NUMBER", func.over());
			case SqlTileBucketFunction(var buckets, var over) -> this.renderWindowCall("NTILE", over, buckets);
			case SqlValueAtFunction(var column, var position, var over) -> this.renderWindowCall("NTH_VALUE", over, column, position);
			case SqlWindowedAggregate(var aggregate, var over) -> renderer -> renderer.rendered(this.renderFunction(aggregate)).over().openingBracket().rendered(over.toSql(this)).closingBracket().toSql();
			
			case null -> throw new NullPointerException("Sql window function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql window function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderGenericFunction(@NonNull SqlFunction<?> function) throws SqlDialectUnsupportedFunctionException {
		return switch (function) {
			case SqlCaseWhenFunction<?> func -> this.renderCaseWhenFunction(func);
			case SqlCastFunction(var value, var targetType) -> renderer -> renderer.cast().openingBracket().rendered(value.toSql(this)).as().literal(this.getTypeName(targetType)).closingBracket().toSql();
			case SqlCoalesceFunction<?> func -> renderer -> this.renderFunctionWithList(renderer.literal("COALESCE"), func.values());
			case SqlGreatestFunction<?> func -> renderer -> this.renderFunctionWithList(renderer.literal("GREATEST"), func.values());
			case SqlLeastFunction<?> func -> renderer -> this.renderFunctionWithList(renderer.literal("LEAST"), func.values());
			case SqlNullIfFunction(var expression, var fallback) -> this.renderFunction("NULLIF", expression, fallback);
			case SqlUnsafeFunction(var expression, var type) -> this.renderCast(renderer -> renderer.literal(expression).toSql(), type);
			
			case null -> throw new NullPointerException("Sql function must not be null");
			default -> throw new SqlDialectUnsupportedFunctionException("Unknown sql function type: " + function.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	@Override
	public @NonNull SqlRendered renderCondition(@NonNull SqlCondition condition) throws SqlException {
		return (switch (condition) {
			case NegatedSqlCondition cond -> (ThrowableFunction<SqlRenderer, SqlRendered, SqlException>) renderer -> renderer.not().openingBracket().rendered(this.renderCondition(cond.condition())).closingBracket().toSql();
			case SqlComparisonCondition cond -> this.renderComparisonCondition(cond);
			case SqlNumericCondition cond -> this.renderNumericCondition(cond);
			case SqlStringCondition cond -> this.renderStringCondition(cond);
			case SqlTemporalCondition cond -> this.renderTemporalCondition(cond);
			
			case null -> throw new NullPointerException("Sql condition must not be null");
			default -> throw new SqlDialectUnsupportedConditionException("Unknown sql condition type: " + condition.getClass().getName() + " in dialect " + this.name());
		}).apply(SqlRenderer.empty());
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderComparisonCondition(@NonNull SqlComparisonCondition condition) throws SqlDialectUnsupportedConditionException {
		return switch (condition) {
			case SqlBetweenCondition(var value, var lower, var upper) -> renderer -> renderer.rendered(value.toSql(this)).between().rendered(lower.toSql(this)).and().rendered(upper.toSql(this)).toSql();
			case SqlEqualToCondition(var first, var second) -> this.renderInfix(first, "=", second);
			case SqlGreaterThanCondition(var value, var threshold, var equalTo) -> this.renderInfix(value, equalTo ? ">=" : ">", threshold);
			case SqlInListCondition(var value, var options) -> renderer -> this.renderFunctionWithList(renderer.rendered(value.toSql(this)).in(), options);
			case SqlInQueryCondition(var value, var query) -> renderer -> renderer.rendered(value.toSql(this)).in().openingBracket().rendered(query.toSql(this)).closingBracket().toSql();
			case SqlIsDistinctFromCondition(var first, var second) -> renderer -> renderer.rendered(first.toSql(this)).is().keyword("DISTINCT").from().rendered(second.toSql(this)).toSql();
			case SqlIsNullCondition func -> renderer -> renderer.rendered(func.value().toSql(this)).is().null_().toSql();
			case SqlLessThanCondition(var value, var threshold, var equalTo) -> this.renderInfix(value, equalTo ? "<=" : "<", threshold);
			
			case null -> throw new NullPointerException("Sql comparison condition must not be null");
			default -> throw new SqlDialectUnsupportedConditionException("Unknown sql comparison condition type: " + condition.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderNumericCondition(@NonNull SqlNumericCondition condition) throws SqlDialectUnsupportedConditionException {
		return switch (condition) {
			case SqlIsNegativeCondition func -> renderer -> renderer.rendered(func.value().toSql(this)).literal("<").literal("0").toSql();
			case SqlIsPositiveCondition func -> renderer -> renderer.rendered(func.value().toSql(this)).literal(">").literal("0").toSql();
			case SqlIsZeroCondition func -> renderer -> renderer.rendered(func.value().toSql(this)).literal("=").literal("0").toSql();
			case SqlModEqualsCondition(var value, var divisor, var remainder) ->
				renderer -> renderer.literal("MOD").openingBracket().rendered(value.toSql(this)).comma().rendered(divisor.toSql(this)).closingBracket().literal("=").rendered(remainder.toSql(this)).toSql();
			
			case null -> throw new NullPointerException("Sql numeric condition must not be null");
			default -> throw new SqlDialectUnsupportedConditionException("Unknown sql numeric condition type: " + condition.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderStringCondition(@NonNull SqlStringCondition condition) throws SqlDialectUnsupportedConditionException {
		return switch (condition) {
			case SqlContainsCondition(var value, var substring) -> renderer -> renderer.rendered(value.toSql(this)).like().literal("'%'").literal("||").rendered(substring.toSql(this)).literal("||").literal("'%'").toSql();
			case SqlEndsWithCondition(var value, var suffix) -> renderer -> renderer.rendered(value.toSql(this)).like().literal("'%'").literal("||").rendered(suffix.toSql(this)).toSql();
			case SqlEqualsIgnoreCaseCondition(var first, var second) ->
				renderer -> renderer.literal("UPPER").openingBracket().rendered(first.toSql(this)).closingBracket().literal("=").literal("UPPER").openingBracket().rendered(second.toSql(this)).closingBracket().toSql();
			case SqlLikeCondition(var value, var pattern) -> renderer -> renderer.rendered(value.toSql(this)).like().rendered(pattern.toSql(this)).toSql();
			case SqlStartsWithCondition(var value, var prefix) -> renderer -> renderer.rendered(value.toSql(this)).like().rendered(prefix.toSql(this)).literal("||").literal("'%'").toSql();
			
			case null -> throw new NullPointerException("Sql string condition must not be null");
			default -> throw new SqlDialectUnsupportedConditionException("Unknown sql string condition type: " + condition.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	protected @NonNull ThrowableFunction<SqlRenderer, SqlRendered, SqlException> renderTemporalCondition(@NonNull SqlTemporalCondition condition) throws SqlDialectUnsupportedConditionException {
		return switch (condition) {
			case SqlAfterCondition(var value, var earlierBound) -> this.renderInfix(value, ">", earlierBound);
			case SqlBeforeCondition(var value, var laterBound) -> this.renderInfix(value, "<", laterBound);
			case SqlWithinNextCondition(var value, var duration) -> renderer -> renderer.rendered(value.toSql(this)).literal("<=").keyword("CURRENT_TIMESTAMP").literal("+").rendered(duration.toSql(this)).toSql();
			case SqlWithinLastCondition(var value, var duration) -> renderer -> renderer.rendered(value.toSql(this)).literal(">=").keyword("CURRENT_TIMESTAMP").literal("-").rendered(duration.toSql(this)).toSql();
			
			case null -> throw new NullPointerException("Sql temporal condition must not be null");
			default -> throw new SqlDialectUnsupportedConditionException("Unknown sql temporal condition type: " + condition.getClass().getName() + " in dialect " + this.name());
		};
	}
	
	@Override
	public boolean isFeatureSupported(@NonNull SqlFeature feature) {
		Objects.requireNonNull(feature, "Sql feature must not be null");
		return false;
	}
	
	@Override
	public boolean isIndexMethodSupported(@NonNull SqlIndexMethod method) {
		Objects.requireNonNull(method, "Sql index method must not be null");
		return method == SqlIndexMethod.BTREE;
	}
	
	@Override
	public @NonNull String getIndexMethodName(@NonNull SqlIndexMethod method) throws SqlDialectUnsupportedIndexMethodException {
		Objects.requireNonNull(method, "Sql index method must not be null");
		
		if (!this.isIndexMethodSupported(method)) {
			throw new SqlDialectUnsupportedIndexMethodException("Sql index method " + method + " is not supported by dialect " + this.name());
		}
		return method.name();
	}
	
	@Override
	public @NonNull String quoteIdentifier(@NonNull String identifier) {
		Objects.requireNonNull(identifier, "Identifier must not be null");
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}
	
	@Override
	public @NonNull String renderQualifiedName(@NonNull String schema, @NonNull String name) throws SqlException {
		Objects.requireNonNull(schema, "Schema must not be null");
		Objects.requireNonNull(name, "Name must not be null");
		
		return this.quoteIdentifier(schema) + "." + this.quoteIdentifier(name);
	}
	
	@Override
	public @NonNull SqlRendered renderCreateTable(@NonNull SqlTable<?> table, boolean ifNotExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create().table();
		if (ifNotExists) {
			renderer.if_().not().exists();
		}
		
		renderer.literal(this.quoteIdentifier(table.getName()));
		renderer.openingBracket();
		
		boolean hasCompositeKey = table.getCompositePrimaryKey().isPresent();
		List<? extends SqlColumn<?, ?>> columns = table.getColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.rendered(this.renderColumnForTable(columns.get(i), hasCompositeKey));
		}
		
		SqlRendered tableConstraints = this.renderTableConstraints(table);
		if (!tableConstraints.sql().isEmpty()) {
			renderer.comma().rendered(tableConstraints);
		}
		
		renderer.closingBracket();
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropTable(@NonNull SqlTable<?> table, boolean ifExists) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().table();
		
		if (ifExists) {
			renderer.if_().exists();
		}
		
		renderer.literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderTruncateTable(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.truncate().table().literal(this.quoteIdentifier(table.getName()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderColumnDefinition(@NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		return this.renderColumnForTable(column, false);
	}
	
	protected void renderForeignKey(@NonNull SqlRenderer renderer, @NonNull SqlForeignKey<?, ?> fk) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(fk, "Sql foreign key must not be null");
		
		renderer.literal(this.quoteIdentifier(fk.getReferencedTable().getName()));
		renderer.openingBracket();
		
		List<? extends SqlColumn<?, ?>> referencedColumns = fk.getReferencedColumns();
		for (int i = 0; i < referencedColumns.size(); i++) {
			if (i > 0) {
				renderer.comma();
			}
			renderer.literal(this.quoteIdentifier(referencedColumns.get(i).getName()));
		}
		renderer.closingBracket();
		
		if (fk.getOnUpdate() != SqlReferentialAction.NO_ACTION) {
			renderer.on().update();
			this.renderReferentialAction(renderer, fk.getOnUpdate());
		}
		
		if (fk.getOnDelete() != SqlReferentialAction.NO_ACTION) {
			renderer.on().delete();
			this.renderReferentialAction(renderer, fk.getOnDelete());
		}
	}
	
	protected @NonNull SqlRendered renderColumnForTable(@NonNull SqlColumn<?, ?> column, boolean skipPrimaryKey) throws SqlException {
		Objects.requireNonNull(column, "Sql column must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.literal(this.quoteIdentifier(column.getName()));
		
		try {
			renderer.literal(this.getTypeName(column.getType()));
		} catch (SqlDialectUnsupportedTypeException e) {
			throw new IllegalArgumentException("Column type is not supported by dialect " + this.name(), e);
		}
		
		if (!column.isNullable()) {
			renderer.not().null_();
		}
		if (column.getDefaultValue().isPresent()) {
			renderer.default_().parameter(column.getDefaultValue().get());
		}
		if (column.isAutoIncrement()) {
			this.renderAutoIncrement(renderer, column);
		}
		if (column.isPrimaryKey() && !skipPrimaryKey) {
			renderer.primary().key();
		}
		if (column.isUnique()) {
			renderer.unique();
		}
		
		if (column.getForeignKey().isPresent()) {
			renderer.references();
			this.renderForeignKey(renderer, column.getForeignKey().get());
		}
		
		for (SqlCondition check : column.getChecks()) {
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
		}
		return renderer.toSql();
	}
	
	protected void renderAutoIncrement(@NonNull SqlRenderer renderer, @NonNull SqlColumn<?, ?> column) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(column, "Sql column must not be null");
		
		renderer.keyword("GENERATED").keyword("ALWAYS").as().keyword("IDENTITY");
	}
	
	protected void renderReferentialAction(@NonNull SqlRenderer renderer, @NonNull SqlReferentialAction action) throws SqlException {
		Objects.requireNonNull(renderer, "Sql renderer must not be null");
		Objects.requireNonNull(action, "Sql referential action must not be null");
		
		switch (action) {
			case NO_ACTION -> renderer.noAction();
			case RESTRICT -> renderer.restrict();
			case CASCADE -> renderer.cascade();
			case SET_NULL -> renderer.setNull();
			case SET_DEFAULT -> renderer.setDefault();
		}
	}
	
	protected @NonNull SqlRendered renderTableConstraints(@NonNull SqlTable<?> table) throws SqlException {
		Objects.requireNonNull(table, "Sql table must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		boolean first = true;
		
		if (table.getCompositePrimaryKey().isPresent()) {
			SqlCompositePrimaryKey<?> pk = table.getCompositePrimaryKey().get();
			
			renderer.primary().key().openingBracket();
			this.renderList(renderer, pk.columns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlForeignKey<?, ?> fk : table.getForeignKeys()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.foreign().key().openingBracket();
			this.renderList(renderer, fk.getReferencingColumns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket().references();
			this.renderForeignKey(renderer, fk);
			
			first = false;
		}
		
		for (List<? extends SqlColumn<?, ?>> uniqueColumns : table.getUniqueConstraints()) {
			if (!first) {
				renderer.comma();
			}
			
			renderer.unique().openingBracket();
			this.renderList(renderer, uniqueColumns, (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
			renderer.closingBracket();
			
			first = false;
		}
		
		for (SqlCondition check : table.getCheckConstraints()) {
			if (!first) {
				renderer.comma();
			}
			renderer.check().openingBracket().rendered(check.toSql(this)).closingBracket();
			first = false;
		}
		
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderCreateIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.create();
		if (index.unique()) {
			renderer.unique();
		}
		
		renderer.index().literal(this.quoteIdentifier(index.name()));
		renderer.on().literal(this.quoteIdentifier(index.columns().getFirst().getOwningTable().getName()));
		
		try {
			renderer.using().literal(this.getIndexMethodName(index.method()));
		} catch (SqlDialectUnsupportedIndexMethodException e) {
			throw new IllegalArgumentException("Index method is not supported by dialect " + this.name(), e);
		}
		
		renderer.openingBracket();
		this.renderList(renderer, index.columns(), (r, column) -> r.literal(this.quoteIdentifier(column.getName())));
		renderer.closingBracket();
		
		if (index.whereCondition() != null) {
			renderer.where().rendered(index.whereCondition().toSql(this));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderDropIndex(@NonNull SqlIndex index) throws SqlException {
		Objects.requireNonNull(index, "Sql index must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		renderer.drop().index().literal(this.quoteIdentifier(index.name()));
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderLimitOffset(long limit, long offset) throws SqlException {
		if (limit < 0) {
			throw new IllegalArgumentException("Limit must be non-negative");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be non-negative");
		}
		
		SqlRenderer renderer = SqlRenderer.empty();
		if (limit > 0) {
			renderer.limit().literal(String.valueOf(limit));
		}
		if (offset > 0) {
			renderer.offset().literal(String.valueOf(offset));
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull SqlRendered renderReturning(@NonNull List<SqlColumn<?, ?>> columns) throws SqlException {
		throw new SqlDialectUnsupportedFeatureException("RETURNING clause is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull SqlRendered renderLockClause(@NonNull SqlLockMode mode, boolean skipLocked, boolean noWait) throws SqlException {
		Objects.requireNonNull(mode, "Sql lock mode must not be null");
		
		SqlRenderer renderer = SqlRenderer.empty();
		switch (mode) {
			case FOR_UPDATE -> renderer.for_().update();
			case FOR_SHARE -> renderer.for_().share();
		}
		
		if (skipLocked) {
			renderer.skip().locked();
		}
		
		if (noWait) {
			renderer.nowait();
		}
		return renderer.toSql();
	}
	
	@Override
	public @NonNull String renderSetOperation(@NonNull SqlSetOperation operation) throws SqlException {
		Objects.requireNonNull(operation, "Sql set operation must not be null");
		
		return switch (operation) {
			case UNION -> "UNION";
			case UNION_ALL -> "UNION ALL";
			case INTERSECT -> "INTERSECT";
			case EXCEPT -> "EXCEPT";
		};
	}
	
	@Override
	public @NonNull String renderLateralJoin() throws SqlException {
		throw new SqlDialectUnsupportedFeatureException("LATERAL join is not supported by dialect " + this.name());
	}
	
	@Override
	public @NonNull String renderBooleanLiteral(boolean value) throws SqlException {
		return value ? "TRUE" : "FALSE";
	}
}
