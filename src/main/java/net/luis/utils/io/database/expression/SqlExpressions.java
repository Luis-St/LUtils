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

package net.luis.utils.io.database.expression;

import com.google.common.collect.Lists;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.conditions.comparison.*;
import net.luis.utils.io.database.function.functions.SqlAggregateFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlCountDistinctFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlCountFunction;
import net.luis.utils.io.database.function.functions.generic.*;
import net.luis.utils.io.database.function.functions.window.*;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.util.SqlCaseWhenBranch;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlExpressions {
	
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlEqualToCondition(expression, SqlExpression.of(value));
	}
	
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlEqualToCondition(expression, other);
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, T @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<?>> options = Lists.newArrayList();
		for (T value : values) {
			options.add(SqlExpression.of(value));
		}
		return new SqlInListCondition(expression, options);
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
	
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlIsDistinctFromCondition(expression, SqlExpression.of(value));
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
	
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull T fallback) {
		return new SqlNullIfFunction<>(expression, SqlExpression.of(fallback));
	}
	
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> fallback) {
		return new SqlNullIfFunction<>(expression, fallback);
	}
	
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull T thenValue, @NonNull T elseValue) {
		List<SqlCaseWhenBranch<T>> branches = List.of(new SqlCaseWhenBranch<>(condition, SqlExpression.of(thenValue)));
		return new SqlCaseWhenFunction<>(branches, SqlExpression.of(elseValue));
	}
	
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull SqlExpression<T> thenValue, @NonNull SqlExpression<T> elseValue) {
		List<SqlCaseWhenBranch<T>> branches = List.of(new SqlCaseWhenBranch<>(condition, thenValue));
		return new SqlCaseWhenFunction<>(branches, elseValue);
	}
	
	public static <T> @NonNull SqlExpression<T> ofUnsafe(@NonNull String functionName, @NonNull SqlType<T> resultType, SqlExpression<?> @NonNull ... args) {
		Objects.requireNonNull(args, "Sql arguments must not be null");
		
		return new SqlUnsafeFunction<>(functionName, List.of(args), resultType);
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
		return new SqlTileBucketFunction<>(SqlExpression.of(buckets), over, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> tileBucket(int buckets, @NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlTileBucketFunction<>(SqlExpression.of(buckets), over, type);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, null, null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, SqlExpression.of(offset), null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, SqlExpression.of(offset), SqlExpression.of(defaultValue), over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, null, null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, SqlExpression.of(offset), null, over);
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, SqlExpression.of(offset), SqlExpression.of(defaultValue), over);
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
		return new SqlValueAtFunction<>(expression, SqlExpression.of(position), over);
	}
}
