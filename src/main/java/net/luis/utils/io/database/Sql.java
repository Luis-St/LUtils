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

package net.luis.utils.io.database;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.condition.conditions.string.*;
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.exception.type.SqlTypeNotFoundException;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlValueExpression;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.*;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.function.functions.string.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("DuplicatedCode")
public class Sql {
	
	public static <T> @NonNull SqlExpression<T> of(@NonNull T value) throws SqlTypeNotFoundException {
		return new SqlValueExpression<>(value);
	}
	
	public static <T> @NonNull SqlExpression<T> of(@NonNull T value, @NonNull SqlType<T> type) {
		return new SqlValueExpression<>(value, type);
	}
	
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlEqualToCondition(expression, other);
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, SqlExpression<T> @NonNull ... otherExpressions) {
		Objects.requireNonNull(otherExpressions, "Other sql expressions must not be null");
		
		List<SqlExpression<?>> options = Lists.newArrayList();
		for (SqlExpression<T> other : otherExpressions) {
			options.add(Objects.requireNonNull(other, "Other sql expression must not be null"));
		}
		return new SqlInListCondition(expression, options);
	}
	
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, @NonNull SqlSelectQuery<T> subquery) {
		return new SqlInQueryCondition(expression, subquery);
	}
	
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlIsDistinctFromCondition(expression, other);
	}
	
	public static <T> @NonNull SqlCondition isNull(@NonNull SqlExpression<T> expression) {
		return new SqlIsNullCondition(expression);
	}
	
	public static <T> @NonNull SqlExpression<Long> count(@NonNull SqlExpression<T> expression, boolean distinct) {
		return distinct ? new SqlCountDistinctFunction(expression) : new SqlCountFunction(expression);
	}
	
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> targetType) {
		Objects.requireNonNull(expression, "Sql Expression must not be null");
		Objects.requireNonNull(targetType, "Sql target type must not be null");
		
		return new SqlCastFunction<>(expression, targetType);
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> coalesce(@NonNull SqlExpression<T> @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<T>> list = Lists.newArrayList();
		for (SqlExpression<T> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlCoalesceFunction<>(list);
	}
	
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> compareValue) {
		return new SqlNullIfFunction<>(expression, compareValue);
	}
	
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull SqlExpression<T> thenValue, @NonNull SqlExpression<T> elseValue) {
		List<SqlCaseWhenBranch<T>> branches = List.of(new SqlCaseWhenBranch<>(condition, thenValue));
		return new SqlCaseWhenFunction<>(branches, elseValue);
	}
	
	@Deprecated
	public static <T> @NonNull SqlExpression<T> ofUnsafe(@NonNull String functionName, @NonNull SqlType<T> resultType, SqlExpression<?> @NonNull ... args) {
		Objects.requireNonNull(args, "Sql arguments must not be null");
		
		return new SqlUnsafeFunction<>(functionName, List.of(args), resultType);
	}
	
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, false);
	}
	
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, true);
	}
	
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, false);
	}
	
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, true);
	}
	
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> start, @NonNull SqlExpression<T> end) {
		return new SqlBetweenCondition(expression, start, end);
	}
	
	public static <T> @NonNull SqlExpression<T> min(@NonNull SqlExpression<T> expression) {
		return new SqlMinFunction<>(expression);
	}
	
	public static <T> @NonNull SqlExpression<T> max(@NonNull SqlExpression<T> expression) {
		return new SqlMaxFunction<>(expression);
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> greatest(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		Objects.requireNonNull(first, "Sql first expression must not be null");
		Objects.requireNonNull(second, "Sql second expression must not be null");
		Objects.requireNonNull(others, "Other sql expressions must not be null");
		
		List<SqlExpression<T>> values = Lists.newArrayList(first, second);
		for (SqlExpression<T> other : others) {
			values.add(Objects.requireNonNull(other, "Other sql expression must not be null"));
		}
		return new SqlGreatestFunction<>(values);
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> least(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		Objects.requireNonNull(first, "Sql first expression must not be null");
		Objects.requireNonNull(second, "Sql second expression must not be null");
		Objects.requireNonNull(others, "Other sql expressions must not be null");
		
		List<SqlExpression<T>> values = Lists.newArrayList(first, second);
		for (SqlExpression<T> other : others) {
			values.add(Objects.requireNonNull(other, "Other sql expression must not be null"));
		}
		return new SqlLeastFunction<>(values);
	}
	
	public static @NonNull SqlCondition isPositive(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsPositiveCondition(expression);
	}
	
	public static @NonNull SqlCondition isNegative(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsNegativeCondition(expression);
	}
	
	public static @NonNull SqlCondition isZero(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsZeroCondition(expression);
	}
	
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull SqlExpression<? extends Number> divisor, @NonNull SqlExpression<? extends Number> remainder) {
		return new SqlModEqualsCondition(expression, divisor, remainder);
	}
	
	public static @NonNull SqlExpression<Double> random() {
		return new SqlRandomFunction();
	}
	
	public static @NonNull SqlExpression<Double> pi() {
		return new SqlPiFunction();
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> addend) {
		return new SqlNumericAddFunction<>(expression, addend);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> subtrahend) {
		return new SqlNumericSubtractFunction<>(expression, subtrahend);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> multiply(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> multiplier) {
		return new SqlNumericMultiplyFunction<>(expression, multiplier);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> divide(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return new SqlNumericDivideFunction<>(expression, divisor);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> negate(@NonNull SqlExpression<T> expression) {
		return new SqlNegateFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> sum(@NonNull SqlExpression<T> expression) {
		return new SqlSumFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> average(@NonNull SqlExpression<T> expression) {
		return new SqlAverageFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> abs(@NonNull SqlExpression<T> expression) {
		return new SqlAbsFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression) {
		return new SqlRoundFunction<>(expression, null);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> precision) {
		return new SqlRoundFunction<>(expression, precision);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> ceil(@NonNull SqlExpression<T> expression) {
		return new SqlCeilFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> floor(@NonNull SqlExpression<T> expression) {
		return new SqlFloorFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression) {
		return new SqlNumericTruncateFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return new SqlModFunction<>(expression, divisor);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> exponent) {
		return new SqlPowFunction<>(expression, exponent);
	}
	
	public static @NonNull SqlExpression<Double> sqrt(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSqrtFunction(expression);
	}
	
	public static @NonNull SqlExpression<Integer> sign(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSignFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> exp(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlExpFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> log2(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(2, SqlTypes.INTEGER));
	}
	
	public static @NonNull SqlExpression<Double> ln(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(Math.E, SqlTypes.DOUBLE));
	}
	
	public static @NonNull SqlExpression<Double> log10(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(10, SqlTypes.INTEGER));
	}
	
	public static @NonNull SqlExpression<Double> sin(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSinFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> cos(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlCosFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> tan(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlTanFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> asin(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAsinFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> acos(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAcosFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> atan(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAtanFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> atan2(@NonNull SqlExpression<? extends Number> expression, @NonNull SqlExpression<? extends Number> x) {
		return new SqlAtan2Function(expression, x);
	}
	
	public static @NonNull SqlExpression<Double> radians(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlRadiansFunction(expression);
	}
	
	public static @NonNull SqlExpression<Double> degrees(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlDegreesFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseAndFunction<>(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseAndFunction<>(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseOrFunction<>(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseOrFunction<>(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseXorFunction<>(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseXorFunction<>(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression) {
		return new SqlBitwiseNotFunction<>(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression, @NonNull SqlType<T> type) {
		return new SqlBitwiseNotFunction<>(expression, type);
	}
	
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> prefix) {
		return new SqlStartsWithCondition(expression, prefix);
	}
	
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring) {
		return new SqlContainsCondition(expression, substring);
	}
	
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> suffix) {
		return new SqlEndsWithCondition(expression, suffix);
	}
	
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> pattern) {
		return new SqlLikeCondition(expression, pattern);
	}
	
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> value) {
		return new SqlEqualsIgnoreCaseCondition(expression, value);
	}
	
	public static @NonNull SqlExpression<String> lower(@NonNull SqlExpression<String> expression) {
		return new SqlLowerFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> upper(@NonNull SqlExpression<String> expression) {
		return new SqlUpperFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> trim(@NonNull SqlExpression<String> expression) {
		return new SqlTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> leftTrim(@NonNull SqlExpression<String> expression) {
		return new SqlLeftTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> rightTrim(@NonNull SqlExpression<String> expression) {
		return new SqlRightTrimFunction<>(expression);
	}
	
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> characters) {
		return new SqlTrimCharsFunction<>(expression, characters);
	}
	
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<String> expression) {
		return new SqlLengthFunction<>(expression, SqlTypes.INTEGER);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> length(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlLengthFunction<>(expression, type);
	}
	
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> start, @NonNull SqlExpression<Integer> length) {
		return new SqlSubstringFunction<>(expression, start, length);
	}
	
	@SafeVarargs
	public static @NonNull SqlExpression<String> concat(SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.empty(), false, false);
	}
	
	@SafeVarargs
	public static @NonNull SqlExpression<String> concatWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, false);
	}
	
	@SafeVarargs
	public static @NonNull SqlExpression<String> concatDistinctWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), true, false);
	}
	
	@SafeVarargs
	public static @NonNull SqlExpression<String> concatOrderedWithSeparator(@NonNull String separator, SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, true);
	}
	
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> search, @NonNull SqlExpression<String> replacement) {
		return new SqlReplaceFunction<>(expression, search, replacement);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> position(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring, @NonNull SqlType<T> type) {
		return new SqlPositionFunction<>(substring, expression, type);
	}
	
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> n) {
		return new SqlLeftFunction<>(expression, n);
	}
	
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> n) {
		return new SqlRightFunction<>(expression, n);
	}
	
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> length, @NonNull SqlExpression<String> fill) {
		return new SqlLeftPadFunction<>(expression, length, fill);
	}
	
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> length, @NonNull SqlExpression<String> fill) {
		return new SqlRightPadFunction<>(expression, length, fill);
	}
	
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<? extends CharSequence> expression) {
		return new SqlHexFunction(expression);
	}
	
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<? extends CharSequence> expression, @NonNull SqlType<String> type) {
		return new SqlHexFunction(expression, type);
	}
	
	public static @NonNull SqlExpression<byte[]> unhex(@NonNull SqlExpression<String> expression) {
		return new SqlUnhexFunction<>(expression, SqlTypes.LARGE_BYTES);
	}
	
	public static <T> @NonNull SqlExpression<T> unhex(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlUnhexFunction<>(expression, type);
	}
	
	public static @NonNull SqlCondition withinLast(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<Duration> duration) {
		return new SqlWithinLastCondition(expression, duration);
	}
	
	public static @NonNull SqlCondition withinNext(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<Duration> duration) {
		return new SqlWithinNextCondition(expression, duration);
	}
	
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlBeforeCondition(expression, timestamp);
	}
	
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlAfterCondition(expression, timestamp);
	}
	
	public static @NonNull SqlExpression<Instant> now() {
		return new SqlNowFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> now(@NonNull SqlType<T> type) {
		return new SqlNowFunction<>(type);
	}
	
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		return new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentDate(@NonNull SqlType<T> type) {
		return new SqlCurrentDateFunction<>(type);
	}
	
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		return new SqlCurrentTimeFunction<>(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTime(@NonNull SqlType<T> type) {
		return new SqlCurrentTimeFunction<>(type);
	}
	
	public static @NonNull SqlExpression<Instant> currentTimestamp() {
		return new SqlCurrentTimestampFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTimestamp(@NonNull SqlType<T> type) {
		return new SqlCurrentTimestampFunction<>(type);
	}
	
	public static <T extends Number, V extends Temporal> @NonNull SqlExpression<V> fromEpoch(@NonNull SqlExpression<T> expression, @NonNull SqlType<V> type) {
		return new SqlFromEpochFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(@NonNull SqlExpression<Integer> year, @NonNull SqlExpression<Integer> month, @NonNull SqlExpression<Integer> day, @NonNull SqlType<T> type) {
		return new SqlMakeDateFunction<>(year, month, day, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(@NonNull SqlExpression<Integer> hour, @NonNull SqlExpression<Integer> minute, @NonNull SqlExpression<Integer> second, @NonNull SqlType<T> type) {
		return new SqlMakeTimeFunction<>(hour, minute, second, type);
	}
	
	public static @NonNull SqlExpression<Integer> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part) {
		return new SqlExtractFunction<>(expression, part, SqlTypes.INTEGER);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlExtractFunction<>(expression, part, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlTemporalTruncateFunction<>(expression, part, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlExpression<Integer> amount, @NonNull SqlType<T> type) {
		return new SqlTemporalAddFunction<>(expression, part, amount, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlExpression<?> amount, @NonNull SqlType<T> type) {
		return new SqlTemporalSubtractFunction<>(expression, part, amount, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> toEpoch(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToEpochFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toDate(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToDateFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toTime(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToTimeFunction<>(expression, type);
	}
	
	public static <T> @NonNull SqlExpression<T> over(@NonNull SqlAggregateFunction<T> aggregate, @NonNull SqlWindowClause clause) {
		return new SqlWindowedAggregate<>(aggregate, clause);
	}
	
	public static @NonNull SqlExpression<Long> rowNumber(@NonNull SqlWindowClause over) {
		return new SqlRowNumberFunction<>(over, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> rowNumber(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlRowNumberFunction<>(over, type);
	}
	
	public static @NonNull SqlExpression<Long> rank(@NonNull SqlWindowClause over) {
		return new SqlRankFunction<>(over, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> rank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlRankFunction<>(over, type);
	}
	
	public static @NonNull SqlExpression<Long> denseRank(@NonNull SqlWindowClause over) {
		return new SqlDenseRankFunction<>(over, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> denseRank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlDenseRankFunction<>(over, type);
	}
	
	public static @NonNull SqlExpression<Long> tileBucket(int buckets, @NonNull SqlWindowClause over) {
		return new SqlTileBucketFunction<>(of(buckets, SqlTypes.INTEGER), over, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> tileBucket(int buckets, @NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlTileBucketFunction<>(of(buckets, SqlTypes.INTEGER), over, type);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, null, null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, of(offset, SqlTypes.INTEGER), null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlType<T> type, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, of(offset, SqlTypes.INTEGER), of(defaultValue, type), over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, null, null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, of(offset, SqlTypes.INTEGER), null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlType<T> type, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, of(offset, SqlTypes.INTEGER), of(defaultValue, type), over);
	}
	
	public static @NonNull SqlExpression<Double> percentRank(@NonNull SqlWindowClause over) {
		return new SqlPercentRankFunction<>(over, SqlTypes.DOUBLE);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> percentRank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlPercentRankFunction<>(over, type);
	}
	
	public static @NonNull SqlExpression<Double> cumulativeDistribution(@NonNull SqlWindowClause over) {
		return new SqlCumulativeDistributionFunction<>(over, SqlTypes.DOUBLE);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> cumulativeDistribution(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlCumulativeDistributionFunction<>(over, type);
	}
	
	public static <T> @NonNull SqlExpression<T> firstValue(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlFirstValueFunction<>(expression, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lastValue(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLastValueFunction<>(expression, over);
	}
	
	public static <T> @NonNull SqlExpression<T> valueAt(@NonNull SqlExpression<T> expression, int position, @NonNull SqlWindowClause over) {
		return new SqlValueAtFunction<>(expression, of(position, SqlTypes.INTEGER), over);
	}
}
