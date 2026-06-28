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
import net.luis.utils.io.database.exception.client.SqlTypeNotFoundException;
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
 * Central static factory and entry-point for building the various sql building blocks.<br>
 * Provides a fluent dsl of static helper methods that construct {@link SqlExpression} and {@link SqlCondition} instances such as value expressions, comparison conditions, numeric, string, temporal and window functions.<br>
 * The created expressions and conditions hide their concrete implementation types and are dialect-agnostic; they are rendered into actual sql later through a {@link net.luis.utils.io.database.dialect.SqlDialect}.<br>
 *
 * @see SqlExpression
 * @see SqlCondition
 *
 * @author Luis-St
 */
@SuppressWarnings("DuplicatedCode")
public class Sql {
	
	/**
	 * Creates a value expression wrapping the given value, inferring its sql type from the runtime type of the value.<br>
	 *
	 * @param value The constant value to wrap
	 * @return A value expression representing the given value
	 * @throws NullPointerException If the value is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the value
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> of(@NonNull T value) throws SqlTypeNotFoundException {
		return new SqlValueExpression<>(value);
	}
	
	/**
	 * Creates a value expression wrapping the given value with the explicitly provided sql type.<br>
	 *
	 * @param value The constant value to wrap
	 * @param type The sql type of the value
	 * @return A value expression representing the given value
	 * @throws NullPointerException If the value or type is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> of(@NonNull T value, @NonNull SqlType<T> type) {
		return new SqlValueExpression<>(value, type);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is equal to the other expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if both expressions are equal
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlEqualToCondition(expression, other);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is equal to the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression equals the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return equalTo(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates an in-list condition that checks whether the given expression matches any of the given expressions.<br>
	 *
	 * @param expression The expression to check
	 * @param otherExpressions The expressions forming the in-list
	 * @return A condition that is true if the expression equals any of the given expressions
	 * @throws NullPointerException If the expression, the array or any of the expressions is null
	 * @param <T> The type of the compared values
	 */
	@SafeVarargs
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, SqlExpression<T> @NonNull ... otherExpressions) {
		Objects.requireNonNull(otherExpressions, "Other sql expressions must not be null");
		
		List<SqlExpression<?>> options = Lists.newArrayList();
		for (SqlExpression<T> other : otherExpressions) {
			options.add(Objects.requireNonNull(other, "Other sql expression must not be null"));
		}
		return new SqlInListCondition(expression, options);
	}
	
	/**
	 * Creates an in-list condition that checks whether the given expression matches any of the given values.<br>
	 * The values are wrapped in value expressions using the type of the given expression.<br>
	 *
	 * @param expression The expression to check
	 * @param values The values forming the in-list
	 * @return A condition that is true if the expression equals any of the given values
	 * @throws NullPointerException If the expression, the array or any of the values is null
	 * @param <T> The type of the compared values
	 */
	@SafeVarargs
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, @NonNull T @NonNull ... values) {
		Objects.requireNonNull(values, "Values must not be null");
		
		SqlType<T> type = expression.type();
		List<SqlExpression<?>> options = Lists.newArrayList();
		for (T value : values) {
			options.add(of(Objects.requireNonNull(value, "Value must not be null"), type));
		}
		return new SqlInListCondition(expression, options);
	}
	
	/**
	 * Creates an in-query condition that checks whether the given expression matches any row produced by the given subquery.<br>
	 *
	 * @param expression The expression to check
	 * @param subquery The subquery producing the candidate values
	 * @return A condition that is true if the expression equals any value returned by the subquery
	 * @throws NullPointerException If the expression or subquery is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, @NonNull SqlSelectQuery<T> subquery) {
		return new SqlInQueryCondition(expression, subquery);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is distinct from the other expression.<br>
	 * Unlike a plain equality check, this treats two null values as equal and a null value as distinct from a non-null value.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if both expressions are distinct
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlIsDistinctFromCondition(expression, other);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is distinct from the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression is distinct from the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return isDistinctFrom(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given expression evaluates to {@code null}.<br>
	 *
	 * @param expression The expression to check
	 * @return A condition that is true if the expression is null
	 * @throws NullPointerException If the expression is null
	 * @param <T> The type of the checked value
	 */
	public static <T> @NonNull SqlCondition isNull(@NonNull SqlExpression<T> expression) {
		return new SqlIsNullCondition(expression);
	}
	
	/**
	 * Creates a count aggregate over the given expression.<br>
	 * If {@code distinct} is {@code true}, only distinct non-null values are counted.<br>
	 *
	 * @param expression The expression to count
	 * @param distinct Whether to count only distinct values
	 * @return An expression evaluating to the number of counted rows
	 * @throws NullPointerException If the expression is null
	 * @param <T> The type of the counted value
	 */
	public static <T> @NonNull SqlExpression<Long> count(@NonNull SqlExpression<T> expression, boolean distinct) {
		return distinct ? new SqlCountDistinctFunction(expression) : new SqlCountFunction(expression);
	}
	
	/**
	 * Creates a cast expression that converts the given expression to the target sql type.<br>
	 *
	 * @param expression The expression to cast
	 * @param targetType The sql type to cast the expression to
	 * @return An expression evaluating to the casted value
	 * @throws NullPointerException If the expression or target type is null
	 * @param <T> The target type of the cast
	 */
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> targetType) {
		Objects.requireNonNull(expression, "Sql Expression must not be null");
		Objects.requireNonNull(targetType, "Sql target type must not be null");
		
		return new SqlCastFunction<>(expression, targetType);
	}
	
	/**
	 * Creates a coalesce expression that evaluates to the first non-null value of the given expressions.<br>
	 *
	 * @param values The expressions to evaluate in order
	 * @return An expression evaluating to the first non-null value
	 * @throws NullPointerException If the array or any of the expressions is null
	 * @param <T> The type of the values
	 */
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> coalesce(@NonNull SqlExpression<T> @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<T>> list = Lists.newArrayList();
		for (SqlExpression<T> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlCoalesceFunction<>(list);
	}
	
	/**
	 * Creates a null-if expression that evaluates to {@code null} if the expression equals the compare value, otherwise to the expression.<br>
	 *
	 * @param expression The expression to evaluate
	 * @param compareValue The expression to compare against
	 * @return An expression that is null when both expressions are equal
	 * @throws NullPointerException If the expression or compare value is null
	 * @param <T> The type of the values
	 */
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> compareValue) {
		return new SqlNullIfFunction<>(expression, compareValue);
	}
	
	/**
	 * Creates a null-if expression that evaluates to {@code null} if the expression equals the compare value, otherwise to the expression.<br>
	 * The compare value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to evaluate
	 * @param compareValue The value to compare against
	 * @return An expression that is null when the expression equals the value
	 * @throws NullPointerException If the expression or compare value is null
	 * @param <T> The type of the values
	 */
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull T compareValue) {
		return nullIf(expression, of(compareValue, expression.type()));
	}
	
	/**
	 * Creates a single-branch case expression that evaluates to the then value if the condition is true, otherwise to the else value.<br>
	 *
	 * @param condition The condition deciding which value is returned
	 * @param thenValue The value returned when the condition is true
	 * @param elseValue The value returned when the condition is false
	 * @return An expression evaluating to either the then or else value
	 * @throws NullPointerException If the condition, then value or else value is null
	 * @param <T> The type of the result values
	 */
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull SqlExpression<T> thenValue, @NonNull SqlExpression<T> elseValue) {
		List<SqlCaseWhenBranch<T>> branches = List.of(new SqlCaseWhenBranch<>(condition, thenValue));
		return new SqlCaseWhenFunction<>(branches, elseValue);
	}
	
	/**
	 * Creates a single-branch case expression that evaluates to the then value if the condition is true, otherwise to the else value.<br>
	 * Both values are wrapped in value expressions with their sql types inferred from the runtime values.<br>
	 *
	 * @param condition The condition deciding which value is returned
	 * @param thenValue The value returned when the condition is true
	 * @param elseValue The value returned when the condition is false
	 * @return An expression evaluating to either the then or else value
	 * @throws NullPointerException If the condition, then value or else value is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for one of the values
	 * @param <T> The type of the result values
	 */
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull T thenValue, @NonNull T elseValue) throws SqlTypeNotFoundException {
		return caseWhen(condition, of(thenValue), of(elseValue));
	}
	
	/**
	 * Creates an expression invoking an arbitrary sql function by name with the given arguments and result type.<br>
	 * This bypasses the type-safety guarantees of the dedicated factory methods and should be used with caution.<br>
	 *
	 * @param functionName The name of the sql function to invoke
	 * @param resultType The sql type of the function result
	 * @param args The arguments passed to the function
	 * @return An expression evaluating to the result of the function call
	 * @throws NullPointerException If the function name, result type or argument array is null
	 * @param <T> The result type of the function
	 * @deprecated Bypasses type validation; prefer the dedicated factory methods where available
	 */
	@Deprecated
	public static <T> @NonNull SqlExpression<T> ofUnsafe(@NonNull String functionName, @NonNull SqlType<T> resultType, SqlExpression<?> @NonNull ... args) {
		Objects.requireNonNull(args, "Sql arguments must not be null");
		
		return new SqlUnsafeFunction<>(functionName, List.of(args), resultType);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is greater than the other expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if the expression is greater than the other expression
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, false);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is greater than the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression is greater than the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return greaterThan(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given expression is greater than or equal to the other expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if the expression is greater than or equal to the other expression
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, true);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is greater than or equal to the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression is greater than or equal to the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return greaterThanOrEqualTo(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given expression is less than the other expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if the expression is less than the other expression
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, false);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is less than the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression is less than the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return lessThan(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given expression is less than or equal to the other expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param other The expression to compare against
	 * @return A condition that is true if the expression is less than or equal to the other expression
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, true);
	}
	
	/**
	 * Creates a condition that checks whether the given expression is less than or equal to the given value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression is less than or equal to the value
	 * @throws NullPointerException If the expression or value is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return lessThanOrEqualTo(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given expression lies within the inclusive range defined by the start and end expressions.<br>
	 *
	 * @param expression The expression to check
	 * @param start The expression defining the lower bound
	 * @param end The expression defining the upper bound
	 * @return A condition that is true if the expression lies between the bounds
	 * @throws NullPointerException If the expression, start or end is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> start, @NonNull SqlExpression<T> end) {
		return new SqlBetweenCondition(expression, start, end);
	}
	
	/**
	 * Creates a condition that checks whether the given expression lies within the inclusive range defined by the start and end values.<br>
	 * Both bounds are wrapped in value expressions using the type of the given expression.<br>
	 *
	 * @param expression The expression to check
	 * @param start The value defining the lower bound
	 * @param end The value defining the upper bound
	 * @return A condition that is true if the expression lies between the bounds
	 * @throws NullPointerException If the expression, start or end is null
	 * @param <T> The type of the compared values
	 */
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull T start, @NonNull T end) {
		SqlType<T> type = expression.type();
		return between(expression, of(start, type), of(end, type));
	}
	
	/**
	 * Creates a min aggregate that evaluates to the smallest value of the given expression.<br>
	 *
	 * @param expression The expression to aggregate
	 * @return An expression evaluating to the minimum value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The type of the aggregated value
	 */
	public static <T> @NonNull SqlExpression<T> min(@NonNull SqlExpression<T> expression) {
		return new SqlMinFunction<>(expression);
	}
	
	/**
	 * Creates a max aggregate that evaluates to the largest value of the given expression.<br>
	 *
	 * @param expression The expression to aggregate
	 * @return An expression evaluating to the maximum value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The type of the aggregated value
	 */
	public static <T> @NonNull SqlExpression<T> max(@NonNull SqlExpression<T> expression) {
		return new SqlMaxFunction<>(expression);
	}
	
	/**
	 * Creates a greatest expression that evaluates to the largest value among the given expressions.<br>
	 *
	 * @param first The first expression
	 * @param second The second expression
	 * @param others The additional expressions
	 * @return An expression evaluating to the greatest of the given values
	 * @throws NullPointerException If the first, second, the array or any of the other expressions is null
	 * @param <T> The type of the compared values
	 */
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
	
	/**
	 * Creates a least expression that evaluates to the smallest value among the given expressions.<br>
	 *
	 * @param first The first expression
	 * @param second The second expression
	 * @param others The additional expressions
	 * @return An expression evaluating to the least of the given values
	 * @throws NullPointerException If the first, second, the array or any of the other expressions is null
	 * @param <T> The type of the compared values
	 */
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
	
	/**
	 * Creates a condition that checks whether the given numeric expression is positive.<br>
	 *
	 * @param expression The numeric expression to check
	 * @return A condition that is true if the expression is greater than zero
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlCondition isPositive(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsPositiveCondition(expression);
	}
	
	/**
	 * Creates a condition that checks whether the given numeric expression is negative.<br>
	 *
	 * @param expression The numeric expression to check
	 * @return A condition that is true if the expression is less than zero
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlCondition isNegative(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsNegativeCondition(expression);
	}
	
	/**
	 * Creates a condition that checks whether the given numeric expression is zero.<br>
	 *
	 * @param expression The numeric expression to check
	 * @return A condition that is true if the expression equals zero
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlCondition isZero(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsZeroCondition(expression);
	}
	
	/**
	 * Creates a condition that checks whether the given expression modulo the divisor equals the remainder.<br>
	 *
	 * @param expression The numeric expression to check
	 * @param divisor The divisor of the modulo operation
	 * @param remainder The expected remainder
	 * @return A condition that is true if the expression modulo the divisor equals the remainder
	 * @throws NullPointerException If the expression, divisor or remainder is null
	 */
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull SqlExpression<? extends Number> divisor, @NonNull SqlExpression<? extends Number> remainder) {
		return new SqlModEqualsCondition(expression, divisor, remainder);
	}
	
	/**
	 * Creates a condition that checks whether the given expression modulo the divisor equals the remainder.<br>
	 * The divisor and remainder are wrapped in value expressions with their sql types inferred from the runtime values.<br>
	 *
	 * @param expression The numeric expression to check
	 * @param divisor The divisor of the modulo operation
	 * @param remainder The expected remainder
	 * @return A condition that is true if the expression modulo the divisor equals the remainder
	 * @throws NullPointerException If the expression, divisor or remainder is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the divisor or remainder
	 */
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull Number divisor, @NonNull Number remainder) throws SqlTypeNotFoundException {
		return modEquals(expression, of(divisor), of(remainder));
	}
	
	/**
	 * Creates an expression that evaluates to a random value in the range from zero to one.<br>
	 * @return An expression evaluating to a random value
	 */
	public static @NonNull SqlExpression<Double> random() {
		return new SqlRandomFunction();
	}
	
	/**
	 * Creates an expression that evaluates to the mathematical constant pi.<br>
	 * @return An expression evaluating to pi
	 */
	public static @NonNull SqlExpression<Double> pi() {
		return new SqlPiFunction();
	}
	
	/**
	 * Creates an expression that adds the addend to the given expression.<br>
	 *
	 * @param expression The expression to add to
	 * @param addend The value to add
	 * @return An expression evaluating to the sum of both operands
	 * @throws NullPointerException If the expression or addend is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> addend) {
		return new SqlNumericAddFunction<>(expression, addend);
	}
	
	/**
	 * Creates an expression that adds the addend to the given expression.<br>
	 * The addend is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to add to
	 * @param addend The value to add
	 * @return An expression evaluating to the sum of both operands
	 * @throws NullPointerException If the expression or addend is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the addend
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull Number addend) throws SqlTypeNotFoundException {
		return add(expression, of(addend));
	}
	
	/**
	 * Creates an expression that subtracts the subtrahend from the given expression.<br>
	 *
	 * @param expression The expression to subtract from
	 * @param subtrahend The value to subtract
	 * @return An expression evaluating to the difference of both operands
	 * @throws NullPointerException If the expression or subtrahend is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> subtrahend) {
		return new SqlNumericSubtractFunction<>(expression, subtrahend);
	}
	
	/**
	 * Creates an expression that subtracts the subtrahend from the given expression.<br>
	 * The subtrahend is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to subtract from
	 * @param subtrahend The value to subtract
	 * @return An expression evaluating to the difference of both operands
	 * @throws NullPointerException If the expression or subtrahend is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the subtrahend
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull Number subtrahend) throws SqlTypeNotFoundException {
		return subtract(expression, of(subtrahend));
	}
	
	/**
	 * Creates an expression that multiplies the given expression by the multiplier.<br>
	 *
	 * @param expression The expression to multiply
	 * @param multiplier The value to multiply by
	 * @return An expression evaluating to the product of both operands
	 * @throws NullPointerException If the expression or multiplier is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> multiply(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> multiplier) {
		return new SqlNumericMultiplyFunction<>(expression, multiplier);
	}
	
	/**
	 * Creates an expression that multiplies the given expression by the multiplier.<br>
	 * The multiplier is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to multiply
	 * @param multiplier The value to multiply by
	 * @return An expression evaluating to the product of both operands
	 * @throws NullPointerException If the expression or multiplier is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the multiplier
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> multiply(@NonNull SqlExpression<T> expression, @NonNull Number multiplier) throws SqlTypeNotFoundException {
		return multiply(expression, of(multiplier));
	}
	
	/**
	 * Creates an expression that divides the given expression by the divisor.<br>
	 *
	 * @param expression The expression to divide
	 * @param divisor The value to divide by
	 * @return An expression evaluating to the quotient of both operands
	 * @throws NullPointerException If the expression or divisor is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> divide(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return new SqlNumericDivideFunction<>(expression, divisor);
	}
	
	/**
	 * Creates an expression that divides the given expression by the divisor.<br>
	 * The divisor is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to divide
	 * @param divisor The value to divide by
	 * @return An expression evaluating to the quotient of both operands
	 * @throws NullPointerException If the expression or divisor is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the divisor
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> divide(@NonNull SqlExpression<T> expression, @NonNull Number divisor) throws SqlTypeNotFoundException {
		return divide(expression, of(divisor));
	}
	
	/**
	 * Creates an expression that negates the given numeric expression.<br>
	 *
	 * @param expression The expression to negate
	 * @return An expression evaluating to the arithmetic negation of the value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> negate(@NonNull SqlExpression<T> expression) {
		return new SqlNegateFunction<>(expression);
	}
	
	/**
	 * Creates a sum aggregate that evaluates to the total of the given expression over all rows.<br>
	 *
	 * @param expression The expression to aggregate
	 * @return An expression evaluating to the sum of the values
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> sum(@NonNull SqlExpression<T> expression) {
		return new SqlSumFunction<>(expression);
	}
	
	/**
	 * Creates an average aggregate that evaluates to the mean of the given expression over all rows.<br>
	 *
	 * @param expression The expression to aggregate
	 * @return An expression evaluating to the average of the values
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> average(@NonNull SqlExpression<T> expression) {
		return new SqlAverageFunction<>(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the absolute value of the given expression.<br>
	 *
	 * @param expression The expression to take the absolute value of
	 * @return An expression evaluating to the absolute value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> abs(@NonNull SqlExpression<T> expression) {
		return new SqlAbsFunction<>(expression);
	}
	
	/**
	 * Creates an expression that rounds the given expression to the nearest integer.<br>
	 *
	 * @param expression The expression to round
	 * @return An expression evaluating to the rounded value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression) {
		return new SqlRoundFunction<>(expression, null);
	}
	
	/**
	 * Creates an expression that rounds the given expression to the given number of decimal places.<br>
	 *
	 * @param expression The expression to round
	 * @param precision The number of decimal places to round to
	 * @return An expression evaluating to the rounded value
	 * @throws NullPointerException If the expression or precision is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> precision) {
		return new SqlRoundFunction<>(expression, precision);
	}
	
	/**
	 * Creates an expression that rounds the given expression to the given number of decimal places.<br>
	 * The precision is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to round
	 * @param precision The number of decimal places to round to
	 * @return An expression evaluating to the rounded value
	 * @throws NullPointerException If the expression or precision is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the precision
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, @NonNull Number precision) throws SqlTypeNotFoundException {
		return round(expression, of(precision));
	}
	
	/**
	 * Creates an expression that rounds the given expression up to the nearest integer.<br>
	 *
	 * @param expression The expression to round up
	 * @return An expression evaluating to the ceiling of the value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> ceil(@NonNull SqlExpression<T> expression) {
		return new SqlCeilFunction<>(expression);
	}
	
	/**
	 * Creates an expression that rounds the given expression down to the nearest integer.<br>
	 *
	 * @param expression The expression to round down
	 * @return An expression evaluating to the floor of the value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> floor(@NonNull SqlExpression<T> expression) {
		return new SqlFloorFunction<>(expression);
	}
	
	/**
	 * Creates an expression that truncates the given expression towards zero, discarding any fractional part.<br>
	 *
	 * @param expression The expression to truncate
	 * @return An expression evaluating to the truncated value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression) {
		return new SqlNumericTruncateFunction<>(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the remainder of dividing the given expression by the divisor.<br>
	 *
	 * @param expression The expression to take the remainder of
	 * @param divisor The value to divide by
	 * @return An expression evaluating to the remainder of the division
	 * @throws NullPointerException If the expression or divisor is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return new SqlModFunction<>(expression, divisor);
	}
	
	/**
	 * Creates an expression that evaluates to the remainder of dividing the given expression by the divisor.<br>
	 * The divisor is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The expression to take the remainder of
	 * @param divisor The value to divide by
	 * @return An expression evaluating to the remainder of the division
	 * @throws NullPointerException If the expression or divisor is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the divisor
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull Number divisor) throws SqlTypeNotFoundException {
		return mod(expression, of(divisor));
	}
	
	/**
	 * Creates an expression that raises the given expression to the power of the exponent.<br>
	 *
	 * @param expression The base expression
	 * @param exponent The exponent to raise the base to
	 * @return An expression evaluating to the base raised to the exponent
	 * @throws NullPointerException If the expression or exponent is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> exponent) {
		return new SqlPowFunction<>(expression, exponent);
	}
	
	/**
	 * Creates an expression that raises the given expression to the power of the exponent.<br>
	 * The exponent is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The base expression
	 * @param exponent The exponent to raise the base to
	 * @return An expression evaluating to the base raised to the exponent
	 * @throws NullPointerException If the expression or exponent is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the exponent
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull Number exponent) throws SqlTypeNotFoundException {
		return pow(expression, of(exponent));
	}
	
	/**
	 * Creates an expression that evaluates to the square root of the given expression.<br>
	 *
	 * @param expression The expression to take the square root of
	 * @return An expression evaluating to the square root of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> sqrt(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSqrtFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the sign of the given expression.<br>
	 * The result is negative one, zero or positive one depending on the sign of the value.<br>
	 *
	 * @param expression The expression to take the sign of
	 * @return An expression evaluating to the sign of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Integer> sign(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSignFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the exponential function of the given expression.<br>
	 *
	 * @param expression The exponent expression
	 * @return An expression evaluating to e raised to the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> exp(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlExpFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the base-2 logarithm of the given expression.<br>
	 *
	 * @param expression The expression to take the logarithm of
	 * @return An expression evaluating to the base-2 logarithm of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> log2(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(2, SqlTypes.INTEGER));
	}
	
	/**
	 * Creates an expression that evaluates to the natural logarithm of the given expression.<br>
	 *
	 * @param expression The expression to take the logarithm of
	 * @return An expression evaluating to the natural logarithm of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> ln(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(Math.E, SqlTypes.DOUBLE));
	}
	
	/**
	 * Creates an expression that evaluates to the base-10 logarithm of the given expression.<br>
	 *
	 * @param expression The expression to take the logarithm of
	 * @return An expression evaluating to the base-10 logarithm of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> log10(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, of(10, SqlTypes.INTEGER));
	}
	
	/**
	 * Creates an expression that evaluates to the sine of the given expression in radians.<br>
	 *
	 * @param expression The angle expression in radians
	 * @return An expression evaluating to the sine of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> sin(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlSinFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the cosine of the given expression in radians.<br>
	 *
	 * @param expression The angle expression in radians
	 * @return An expression evaluating to the cosine of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> cos(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlCosFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the tangent of the given expression in radians.<br>
	 *
	 * @param expression The angle expression in radians
	 * @return An expression evaluating to the tangent of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> tan(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlTanFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the arc sine of the given expression in radians.<br>
	 *
	 * @param expression The expression to take the arc sine of
	 * @return An expression evaluating to the arc sine of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> asin(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAsinFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the arc cosine of the given expression in radians.<br>
	 *
	 * @param expression The expression to take the arc cosine of
	 * @return An expression evaluating to the arc cosine of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> acos(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAcosFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the arc tangent of the given expression in radians.<br>
	 *
	 * @param expression The expression to take the arc tangent of
	 * @return An expression evaluating to the arc tangent of the value
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> atan(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAtanFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the arc tangent of the two given expressions, using their signs to determine the correct quadrant.<br>
	 *
	 * @param expression The expression representing the y coordinate
	 * @param x The expression representing the x coordinate
	 * @return An expression evaluating to the angle of the point in radians
	 * @throws NullPointerException If the expression or x is null
	 */
	public static @NonNull SqlExpression<Double> atan2(@NonNull SqlExpression<? extends Number> expression, @NonNull SqlExpression<? extends Number> x) {
		return new SqlAtan2Function(expression, x);
	}
	
	/**
	 * Creates an expression that converts the given expression from degrees to radians.<br>
	 *
	 * @param expression The angle expression in degrees
	 * @return An expression evaluating to the angle in radians
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> radians(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlRadiansFunction(expression);
	}
	
	/**
	 * Creates an expression that converts the given expression from radians to degrees.<br>
	 *
	 * @param expression The angle expression in radians
	 * @return An expression evaluating to the angle in degrees
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Double> degrees(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlDegreesFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise and of the given expression and the other expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise and of both operands
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseAndFunction<>(expression, other);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise and of the given expression and the other value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise and of both operands
	 * @throws NullPointerException If the expression or other value is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull T other) {
		return bitwiseAnd(expression, of(other, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise and of the given expression and the other expression, using the given result type.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise and of both operands
	 * @throws NullPointerException If the expression, other expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseAndFunction<>(expression, other, type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise and of the given expression and the other value, using the given result type.<br>
	 * The value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise and of both operands
	 * @throws NullPointerException If the expression, other value or type is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the other value
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) throws SqlTypeNotFoundException {
		return bitwiseAnd(expression, of(other), type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise or of the given expression and the other expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise or of both operands
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseOrFunction<>(expression, other);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise or of the given expression and the other value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise or of both operands
	 * @throws NullPointerException If the expression or other value is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull T other) {
		return bitwiseOr(expression, of(other, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise or of the given expression and the other expression, using the given result type.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise or of both operands
	 * @throws NullPointerException If the expression, other expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseOrFunction<>(expression, other, type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise or of the given expression and the other value, using the given result type.<br>
	 * The value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise or of both operands
	 * @throws NullPointerException If the expression, other value or type is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the other value
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) throws SqlTypeNotFoundException {
		return bitwiseOr(expression, of(other), type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise exclusive or of the given expression and the other expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise exclusive or of both operands
	 * @throws NullPointerException If the expression or other expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlBitwiseXorFunction<>(expression, other);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise exclusive or of the given expression and the other value.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @return An expression evaluating to the bitwise exclusive or of both operands
	 * @throws NullPointerException If the expression or other value is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull T other) {
		return bitwiseXor(expression, of(other, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise exclusive or of the given expression and the other expression, using the given result type.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise exclusive or of both operands
	 * @throws NullPointerException If the expression, other expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseXorFunction<>(expression, other, type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise exclusive or of the given expression and the other value, using the given result type.<br>
	 * The value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The first operand
	 * @param other The second operand
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise exclusive or of both operands
	 * @throws NullPointerException If the expression, other value or type is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the other value
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) throws SqlTypeNotFoundException {
		return bitwiseXor(expression, of(other), type);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise complement of the given expression.<br>
	 *
	 * @param expression The operand to complement
	 * @return An expression evaluating to the bitwise complement of the value
	 * @throws NullPointerException If the expression is null
	 * @param <T> The numeric type of the expression
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression) {
		return new SqlBitwiseNotFunction<>(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the bitwise complement of the given expression, using the given result type.<br>
	 *
	 * @param expression The operand to complement
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bitwise complement of the value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression, @NonNull SqlType<T> type) {
		return new SqlBitwiseNotFunction<>(expression, type);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression starts with the prefix expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param prefix The prefix expression
	 * @return A condition that is true if the expression starts with the prefix
	 * @throws NullPointerException If the expression or prefix is null
	 */
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> prefix) {
		return new SqlStartsWithCondition(expression, prefix);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression starts with the given prefix.<br>
	 * The prefix is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param prefix The prefix string
	 * @return A condition that is true if the expression starts with the prefix
	 * @throws NullPointerException If the expression or prefix is null
	 */
	public static @NonNull SqlCondition startsWith(@NonNull SqlExpression<String> expression, @NonNull String prefix) {
		return startsWith(expression, of(prefix, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given string expression contains the substring expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param substring The substring expression
	 * @return A condition that is true if the expression contains the substring
	 * @throws NullPointerException If the expression or substring is null
	 */
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring) {
		return new SqlContainsCondition(expression, substring);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression contains the given substring.<br>
	 * The substring is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param substring The substring
	 * @return A condition that is true if the expression contains the substring
	 * @throws NullPointerException If the expression or substring is null
	 */
	public static @NonNull SqlCondition contains(@NonNull SqlExpression<String> expression, @NonNull String substring) {
		return contains(expression, of(substring, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given string expression ends with the suffix expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param suffix The suffix expression
	 * @return A condition that is true if the expression ends with the suffix
	 * @throws NullPointerException If the expression or suffix is null
	 */
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> suffix) {
		return new SqlEndsWithCondition(expression, suffix);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression ends with the given suffix.<br>
	 * The suffix is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param suffix The suffix string
	 * @return A condition that is true if the expression ends with the suffix
	 * @throws NullPointerException If the expression or suffix is null
	 */
	public static @NonNull SqlCondition endsWith(@NonNull SqlExpression<String> expression, @NonNull String suffix) {
		return endsWith(expression, of(suffix, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given string expression matches the like pattern expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param pattern The like pattern expression
	 * @return A condition that is true if the expression matches the pattern
	 * @throws NullPointerException If the expression or pattern is null
	 */
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> pattern) {
		return new SqlLikeCondition(expression, pattern);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression matches the given like pattern.<br>
	 * The pattern is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to check
	 * @param pattern The like pattern
	 * @return A condition that is true if the expression matches the pattern
	 * @throws NullPointerException If the expression or pattern is null
	 */
	public static @NonNull SqlCondition like(@NonNull SqlExpression<String> expression, @NonNull String pattern) {
		return like(expression, of(pattern, expression.type()));
	}
	
	/**
	 * Creates a condition that checks whether the given string expression equals the value expression, ignoring case.<br>
	 *
	 * @param expression The string expression to compare
	 * @param value The value expression to compare against
	 * @return A condition that is true if both expressions are equal ignoring case
	 * @throws NullPointerException If the expression or value is null
	 */
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> value) {
		return new SqlEqualsIgnoreCaseCondition(expression, value);
	}
	
	/**
	 * Creates a condition that checks whether the given string expression equals the given value, ignoring case.<br>
	 * The value is wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to compare
	 * @param value The value to compare against
	 * @return A condition that is true if the expression equals the value ignoring case
	 * @throws NullPointerException If the expression or value is null
	 */
	public static @NonNull SqlCondition equalsIgnoreCase(@NonNull SqlExpression<String> expression, @NonNull String value) {
		return equalsIgnoreCase(expression, of(value, expression.type()));
	}
	
	/**
	 * Creates an expression that converts the given string expression to lower case.<br>
	 *
	 * @param expression The string expression to convert
	 * @return An expression evaluating to the lower case string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> lower(@NonNull SqlExpression<String> expression) {
		return new SqlLowerFunction<>(expression);
	}
	
	/**
	 * Creates an expression that converts the given string expression to upper case.<br>
	 *
	 * @param expression The string expression to convert
	 * @return An expression evaluating to the upper case string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> upper(@NonNull SqlExpression<String> expression) {
		return new SqlUpperFunction<>(expression);
	}
	
	/**
	 * Creates an expression that removes leading and trailing whitespace from the given string expression.<br>
	 *
	 * @param expression The string expression to trim
	 * @return An expression evaluating to the trimmed string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> trim(@NonNull SqlExpression<String> expression) {
		return new SqlTrimFunction<>(expression);
	}
	
	/**
	 * Creates an expression that removes leading whitespace from the given string expression.<br>
	 *
	 * @param expression The string expression to trim
	 * @return An expression evaluating to the left-trimmed string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> leftTrim(@NonNull SqlExpression<String> expression) {
		return new SqlLeftTrimFunction<>(expression);
	}
	
	/**
	 * Creates an expression that removes trailing whitespace from the given string expression.<br>
	 *
	 * @param expression The string expression to trim
	 * @return An expression evaluating to the right-trimmed string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> rightTrim(@NonNull SqlExpression<String> expression) {
		return new SqlRightTrimFunction<>(expression);
	}
	
	/**
	 * Creates an expression that removes the given characters from both ends of the given string expression.<br>
	 *
	 * @param expression The string expression to trim
	 * @param characters The characters to remove
	 * @return An expression evaluating to the trimmed string
	 * @throws NullPointerException If the expression or characters is null
	 */
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> characters) {
		return new SqlTrimCharsFunction<>(expression, characters);
	}
	
	/**
	 * Creates an expression that removes the given characters from both ends of the given string expression.<br>
	 * The characters are wrapped in a value expression using the type of the given expression.<br>
	 *
	 * @param expression The string expression to trim
	 * @param characters The characters to remove
	 * @return An expression evaluating to the trimmed string
	 * @throws NullPointerException If the expression or characters is null
	 */
	public static @NonNull SqlExpression<String> trimChars(@NonNull SqlExpression<String> expression, @NonNull String characters) {
		return trimChars(expression, of(characters, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the length of the given string expression as an integer.<br>
	 *
	 * @param expression The string expression to measure
	 * @return An expression evaluating to the length of the string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<Integer> length(@NonNull SqlExpression<String> expression) {
		return new SqlLengthFunction<>(expression, SqlTypes.INTEGER);
	}
	
	/**
	 * Creates an expression that evaluates to the length of the given string expression, using the given numeric result type.<br>
	 *
	 * @param expression The string expression to measure
	 * @param type The sql type of the result
	 * @return An expression evaluating to the length of the string
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> length(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlLengthFunction<>(expression, type);
	}
	
	/**
	 * Creates an expression that evaluates to a substring of the given string expression.<br>
	 *
	 * @param expression The string expression to take the substring of
	 * @param start The expression defining the start position
	 * @param length The expression defining the length of the substring
	 * @return An expression evaluating to the extracted substring
	 * @throws NullPointerException If the expression, start or length is null
	 */
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> start, @NonNull SqlExpression<Integer> length) {
		return new SqlSubstringFunction<>(expression, start, length);
	}
	
	/**
	 * Creates an expression that evaluates to a substring of the given string expression.<br>
	 * The start position and length are wrapped in integer value expressions.<br>
	 *
	 * @param expression The string expression to take the substring of
	 * @param start The start position
	 * @param length The length of the substring
	 * @return An expression evaluating to the extracted substring
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> substring(@NonNull SqlExpression<String> expression, int start, int length) {
		return substring(expression, of(start, SqlTypes.INTEGER), of(length, SqlTypes.INTEGER));
	}
	
	/**
	 * Creates an expression that concatenates the given string expressions in order.<br>
	 *
	 * @param values The string expressions to concatenate
	 * @return An expression evaluating to the concatenated string
	 * @throws NullPointerException If the array or any of the expressions is null
	 */
	@SafeVarargs
	public static @NonNull SqlExpression<String> concat(SqlExpression<String> @NonNull ... values) {
		Objects.requireNonNull(values, "Sql values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (SqlExpression<String> value : values) {
			list.add(Objects.requireNonNull(value, "Sql value expression must not be null"));
		}
		return new SqlConcatFunction<>(list, Optional.empty(), false, false);
	}
	
	/**
	 * Creates an expression that concatenates the given strings in order.<br>
	 * Each value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param values The strings to concatenate
	 * @return An expression evaluating to the concatenated string
	 * @throws NullPointerException If the array or any of the values is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for one of the values
	 */
	public static @NonNull SqlExpression<String> concat(@NonNull String @NonNull ... values) throws SqlTypeNotFoundException {
		Objects.requireNonNull(values, "Values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (String value : values) {
			list.add(of(Objects.requireNonNull(value, "Value must not be null")));
		}
		return new SqlConcatFunction<>(list, Optional.empty(), false, false);
	}
	
	/**
	 * Creates an expression that concatenates the given string expressions, joining them with the given separator.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The string expressions to concatenate
	 * @return An expression evaluating to the separated concatenation
	 * @throws NullPointerException If the separator, the array or any of the expressions is null
	 */
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
	
	/**
	 * Creates an expression that concatenates the given strings, joining them with the given separator.<br>
	 * Each value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The strings to concatenate
	 * @return An expression evaluating to the separated concatenation
	 * @throws NullPointerException If the separator, the array or any of the values is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for one of the values
	 */
	public static @NonNull SqlExpression<String> concatWithSeparator(@NonNull String separator, @NonNull String @NonNull ... values) throws SqlTypeNotFoundException {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (String value : values) {
			list.add(of(Objects.requireNonNull(value, "Value must not be null")));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, false);
	}
	
	/**
	 * Creates an expression that concatenates the distinct values of the given string expressions, joining them with the given separator.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The string expressions to concatenate
	 * @return An expression evaluating to the separated concatenation of the distinct values
	 * @throws NullPointerException If the separator, the array or any of the expressions is null
	 */
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
	
	/**
	 * Creates an expression that concatenates the distinct values of the given strings, joining them with the given separator.<br>
	 * Each value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The strings to concatenate
	 * @return An expression evaluating to the separated concatenation of the distinct values
	 * @throws NullPointerException If the separator, the array or any of the values is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for one of the values
	 */
	public static @NonNull SqlExpression<String> concatDistinctWithSeparator(@NonNull String separator, @NonNull String @NonNull ... values) throws SqlTypeNotFoundException {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (String value : values) {
			list.add(of(Objects.requireNonNull(value, "Value must not be null")));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), true, false);
	}
	
	/**
	 * Creates an expression that concatenates the ordered values of the given string expressions, joining them with the given separator.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The string expressions to concatenate
	 * @return An expression evaluating to the separated concatenation of the ordered values
	 * @throws NullPointerException If the separator, the array or any of the expressions is null
	 */
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
	
	/**
	 * Creates an expression that concatenates the ordered values of the given strings, joining them with the given separator.<br>
	 * Each value is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param separator The separator placed between the values
	 * @param values The strings to concatenate
	 * @return An expression evaluating to the separated concatenation of the ordered values
	 * @throws NullPointerException If the separator, the array or any of the values is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for one of the values
	 */
	public static @NonNull SqlExpression<String> concatOrderedWithSeparator(@NonNull String separator, @NonNull String @NonNull ... values) throws SqlTypeNotFoundException {
		Objects.requireNonNull(separator, "Separator must not be null");
		Objects.requireNonNull(values, "Values must not be null");
		
		List<SqlExpression<String>> list = Lists.newArrayList();
		for (String value : values) {
			list.add(of(Objects.requireNonNull(value, "Value must not be null")));
		}
		return new SqlConcatFunction<>(list, Optional.of(separator), false, true);
	}
	
	/**
	 * Creates an expression that replaces all occurrences of the search expression with the replacement expression in the given string expression.<br>
	 *
	 * @param expression The string expression to search in
	 * @param search The expression to search for
	 * @param replacement The expression to replace matches with
	 * @return An expression evaluating to the string with all replacements applied
	 * @throws NullPointerException If the expression, search or replacement is null
	 */
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> search, @NonNull SqlExpression<String> replacement) {
		return new SqlReplaceFunction<>(expression, search, replacement);
	}
	
	/**
	 * Creates an expression that replaces all occurrences of the search string with the replacement string in the given string expression.<br>
	 * Both strings are wrapped in value expressions using the type of the given expression.<br>
	 *
	 * @param expression The string expression to search in
	 * @param search The string to search for
	 * @param replacement The string to replace matches with
	 * @return An expression evaluating to the string with all replacements applied
	 * @throws NullPointerException If the expression, search or replacement is null
	 */
	public static @NonNull SqlExpression<String> replace(@NonNull SqlExpression<String> expression, @NonNull String search, @NonNull String replacement) {
		return replace(expression, of(search, expression.type()), of(replacement, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the position of the substring within the given string expression.<br>
	 *
	 * @param expression The string expression to search in
	 * @param substring The substring to search for
	 * @param type The sql type of the result
	 * @return An expression evaluating to the position of the substring
	 * @throws NullPointerException If the expression, substring or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> position(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<String> substring, @NonNull SqlType<T> type) {
		return new SqlPositionFunction<>(substring, expression, type);
	}
	
	/**
	 * Creates an expression that evaluates to the leftmost characters of the given string expression.<br>
	 *
	 * @param expression The string expression to take from
	 * @param n The expression defining the number of characters
	 * @return An expression evaluating to the leftmost characters
	 * @throws NullPointerException If the expression or n is null
	 */
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> n) {
		return new SqlLeftFunction<>(expression, n);
	}
	
	/**
	 * Creates an expression that evaluates to the leftmost characters of the given string expression.<br>
	 * The number of characters is wrapped in an integer value expression.<br>
	 *
	 * @param expression The string expression to take from
	 * @param n The number of characters
	 * @return An expression evaluating to the leftmost characters
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> left(@NonNull SqlExpression<String> expression, int n) {
		return left(expression, of(n, SqlTypes.INTEGER));
	}
	
	/**
	 * Creates an expression that evaluates to the rightmost characters of the given string expression.<br>
	 *
	 * @param expression The string expression to take from
	 * @param n The expression defining the number of characters
	 * @return An expression evaluating to the rightmost characters
	 * @throws NullPointerException If the expression or n is null
	 */
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> n) {
		return new SqlRightFunction<>(expression, n);
	}
	
	/**
	 * Creates an expression that evaluates to the rightmost characters of the given string expression.<br>
	 * The number of characters is wrapped in an integer value expression.<br>
	 *
	 * @param expression The string expression to take from
	 * @param n The number of characters
	 * @return An expression evaluating to the rightmost characters
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> right(@NonNull SqlExpression<String> expression, int n) {
		return right(expression, of(n, SqlTypes.INTEGER));
	}
	
	/**
	 * Creates an expression that pads the given string expression on the left with the fill string until it reaches the given length.<br>
	 *
	 * @param expression The string expression to pad
	 * @param length The expression defining the target length
	 * @param fill The expression defining the fill string
	 * @return An expression evaluating to the left-padded string
	 * @throws NullPointerException If the expression, length or fill is null
	 */
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> length, @NonNull SqlExpression<String> fill) {
		return new SqlLeftPadFunction<>(expression, length, fill);
	}
	
	/**
	 * Creates an expression that pads the given string expression on the left with the fill string until it reaches the given length.<br>
	 * The length is wrapped in an integer value expression and the fill string using the type of the given expression.<br>
	 *
	 * @param expression The string expression to pad
	 * @param length The target length
	 * @param fill The fill string
	 * @return An expression evaluating to the left-padded string
	 * @throws NullPointerException If the expression or fill is null
	 */
	public static @NonNull SqlExpression<String> leftPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return leftPad(expression, of(length, SqlTypes.INTEGER), of(fill, expression.type()));
	}
	
	/**
	 * Creates an expression that pads the given string expression on the right with the fill string until it reaches the given length.<br>
	 *
	 * @param expression The string expression to pad
	 * @param length The expression defining the target length
	 * @param fill The expression defining the fill string
	 * @return An expression evaluating to the right-padded string
	 * @throws NullPointerException If the expression, length or fill is null
	 */
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expression, @NonNull SqlExpression<Integer> length, @NonNull SqlExpression<String> fill) {
		return new SqlRightPadFunction<>(expression, length, fill);
	}
	
	/**
	 * Creates an expression that pads the given string expression on the right with the fill string until it reaches the given length.<br>
	 * The length is wrapped in an integer value expression and the fill string using the type of the given expression.<br>
	 *
	 * @param expression The string expression to pad
	 * @param length The target length
	 * @param fill The fill string
	 * @return An expression evaluating to the right-padded string
	 * @throws NullPointerException If the expression or fill is null
	 */
	public static @NonNull SqlExpression<String> rightPad(@NonNull SqlExpression<String> expression, int length, @NonNull String fill) {
		return rightPad(expression, of(length, SqlTypes.INTEGER), of(fill, expression.type()));
	}
	
	/**
	 * Creates an expression that evaluates to the hexadecimal representation of the given character sequence expression.<br>
	 *
	 * @param expression The character sequence expression to encode
	 * @return An expression evaluating to the hexadecimal string
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<? extends CharSequence> expression) {
		return new SqlHexFunction(expression);
	}
	
	/**
	 * Creates an expression that evaluates to the hexadecimal representation of the given character sequence expression, using the given result type.<br>
	 *
	 * @param expression The character sequence expression to encode
	 * @param type The sql type of the result
	 * @return An expression evaluating to the hexadecimal string
	 * @throws NullPointerException If the expression or type is null
	 */
	public static @NonNull SqlExpression<String> hex(@NonNull SqlExpression<? extends CharSequence> expression, @NonNull SqlType<String> type) {
		return new SqlHexFunction(expression, type);
	}
	
	/**
	 * Creates an expression that decodes the given hexadecimal string expression back into its raw bytes.<br>
	 *
	 * @param expression The hexadecimal string expression to decode
	 * @return An expression evaluating to the decoded bytes
	 * @throws NullPointerException If the expression is null
	 */
	public static @NonNull SqlExpression<byte[]> unhex(@NonNull SqlExpression<String> expression) {
		return new SqlUnhexFunction<>(expression, SqlTypes.LARGE_BYTES);
	}
	
	/**
	 * Creates an expression that decodes the given hexadecimal string expression back into a value of the given result type.<br>
	 *
	 * @param expression The hexadecimal string expression to decode
	 * @param type The sql type of the result
	 * @return An expression evaluating to the decoded value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The type of the result
	 */
	public static <T> @NonNull SqlExpression<T> unhex(@NonNull SqlExpression<String> expression, @NonNull SqlType<T> type) {
		return new SqlUnhexFunction<>(expression, type);
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression lies within the last duration relative to the current time.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param duration The expression defining the duration window
	 * @return A condition that is true if the expression lies within the last duration
	 * @throws NullPointerException If the expression or duration is null
	 */
	public static @NonNull SqlCondition withinLast(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<Duration> duration) {
		return new SqlWithinLastCondition(expression, duration);
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression lies within the last duration relative to the current time.<br>
	 * The duration is wrapped in a value expression of the duration type.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param duration The duration window
	 * @return A condition that is true if the expression lies within the last duration
	 * @throws NullPointerException If the expression or duration is null
	 */
	public static @NonNull SqlCondition withinLast(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return withinLast(expression, of(duration, SqlTypes.DURATION));
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression lies within the next duration relative to the current time.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param duration The expression defining the duration window
	 * @return A condition that is true if the expression lies within the next duration
	 * @throws NullPointerException If the expression or duration is null
	 */
	public static @NonNull SqlCondition withinNext(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<Duration> duration) {
		return new SqlWithinNextCondition(expression, duration);
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression lies within the next duration relative to the current time.<br>
	 * The duration is wrapped in a value expression of the duration type.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param duration The duration window
	 * @return A condition that is true if the expression lies within the next duration
	 * @throws NullPointerException If the expression or duration is null
	 */
	public static @NonNull SqlCondition withinNext(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return withinNext(expression, of(duration, SqlTypes.DURATION));
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression is before the timestamp expression.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param timestamp The timestamp expression to compare against
	 * @return A condition that is true if the expression is before the timestamp
	 * @throws NullPointerException If the expression or timestamp is null
	 */
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlBeforeCondition(expression, timestamp);
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression is before the given timestamp.<br>
	 * The timestamp is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param timestamp The timestamp to compare against
	 * @return A condition that is true if the expression is before the timestamp
	 * @throws NullPointerException If the expression or timestamp is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the timestamp
	 */
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) throws SqlTypeNotFoundException {
		return before(expression, of(timestamp));
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression is after the timestamp expression.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param timestamp The timestamp expression to compare against
	 * @return A condition that is true if the expression is after the timestamp
	 * @throws NullPointerException If the expression or timestamp is null
	 */
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlAfterCondition(expression, timestamp);
	}
	
	/**
	 * Creates a condition that checks whether the given temporal expression is after the given timestamp.<br>
	 * The timestamp is wrapped in a value expression with its sql type inferred from the runtime value.<br>
	 *
	 * @param expression The temporal expression to check
	 * @param timestamp The timestamp to compare against
	 * @return A condition that is true if the expression is after the timestamp
	 * @throws NullPointerException If the expression or timestamp is null
	 * @throws SqlTypeNotFoundException If no sql type can be inferred for the timestamp
	 */
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) throws SqlTypeNotFoundException {
		return after(expression, of(timestamp));
	}
	
	/**
	 * Creates an expression that evaluates to the current instant with second precision.<br>
	 * @return An expression evaluating to the current instant
	 */
	public static @NonNull SqlExpression<Instant> now() {
		return new SqlNowFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	/**
	 * Creates an expression that evaluates to the current instant using the given temporal result type.<br>
	 *
	 * @param type The sql type of the result
	 * @return An expression evaluating to the current instant
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> now(@NonNull SqlType<T> type) {
		return new SqlNowFunction<>(type);
	}
	
	/**
	 * Creates an expression that evaluates to the current date.<br>
	 * @return An expression evaluating to the current date
	 */
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		return new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE);
	}
	
	/**
	 * Creates an expression that evaluates to the current date using the given temporal result type.<br>
	 *
	 * @param type The sql type of the result
	 * @return An expression evaluating to the current date
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> currentDate(@NonNull SqlType<T> type) {
		return new SqlCurrentDateFunction<>(type);
	}
	
	/**
	 * Creates an expression that evaluates to the current time with second precision.<br>
	 * @return An expression evaluating to the current time
	 */
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		return new SqlCurrentTimeFunction<>(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0)));
	}
	
	/**
	 * Creates an expression that evaluates to the current time using the given temporal result type.<br>
	 *
	 * @param type The sql type of the result
	 * @return An expression evaluating to the current time
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTime(@NonNull SqlType<T> type) {
		return new SqlCurrentTimeFunction<>(type);
	}
	
	/**
	 * Creates an expression that evaluates to the current timestamp with second precision.<br>
	 * @return An expression evaluating to the current timestamp
	 */
	public static @NonNull SqlExpression<Instant> currentTimestamp() {
		return new SqlCurrentTimestampFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	/**
	 * Creates an expression that evaluates to the current timestamp using the given temporal result type.<br>
	 *
	 * @param type The sql type of the result
	 * @return An expression evaluating to the current timestamp
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTimestamp(@NonNull SqlType<T> type) {
		return new SqlCurrentTimestampFunction<>(type);
	}
	
	/**
	 * Creates an expression that converts the given epoch expression into a temporal value of the given type.<br>
	 *
	 * @param expression The numeric epoch expression to convert
	 * @param type The sql type of the result
	 * @return An expression evaluating to the temporal value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The numeric type of the epoch expression
	 * @param <V> The temporal type of the result
	 */
	public static <T extends Number, V extends Temporal> @NonNull SqlExpression<V> fromEpoch(@NonNull SqlExpression<T> expression, @NonNull SqlType<V> type) {
		return new SqlFromEpochFunction<>(expression, type);
	}
	
	/**
	 * Creates an expression that constructs a temporal value from the given year, month and day expressions.<br>
	 *
	 * @param year The expression defining the year
	 * @param month The expression defining the month
	 * @param day The expression defining the day
	 * @param type The sql type of the result
	 * @return An expression evaluating to the constructed date
	 * @throws NullPointerException If the year, month, day or type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(@NonNull SqlExpression<Integer> year, @NonNull SqlExpression<Integer> month, @NonNull SqlExpression<Integer> day, @NonNull SqlType<T> type) {
		return new SqlMakeDateFunction<>(year, month, day, type);
	}
	
	/**
	 * Creates an expression that constructs a temporal value from the given year, month and day values.<br>
	 * The components are wrapped in integer value expressions.<br>
	 *
	 * @param year The year
	 * @param month The month
	 * @param day The day
	 * @param type The sql type of the result
	 * @return An expression evaluating to the constructed date
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(int year, int month, int day, @NonNull SqlType<T> type) {
		return makeDate(of(year, SqlTypes.INTEGER), of(month, SqlTypes.INTEGER), of(day, SqlTypes.INTEGER), type);
	}
	
	/**
	 * Creates an expression that constructs a temporal value from the given hour, minute and second expressions.<br>
	 *
	 * @param hour The expression defining the hour
	 * @param minute The expression defining the minute
	 * @param second The expression defining the second
	 * @param type The sql type of the result
	 * @return An expression evaluating to the constructed time
	 * @throws NullPointerException If the hour, minute, second or type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(@NonNull SqlExpression<Integer> hour, @NonNull SqlExpression<Integer> minute, @NonNull SqlExpression<Integer> second, @NonNull SqlType<T> type) {
		return new SqlMakeTimeFunction<>(hour, minute, second, type);
	}
	
	/**
	 * Creates an expression that constructs a temporal value from the given hour, minute and second values.<br>
	 * The components are wrapped in integer value expressions.<br>
	 *
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @param type The sql type of the result
	 * @return An expression evaluating to the constructed time
	 * @throws NullPointerException If the type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(int hour, int minute, int second, @NonNull SqlType<T> type) {
		return makeTime(of(hour, SqlTypes.INTEGER), of(minute, SqlTypes.INTEGER), of(second, SqlTypes.INTEGER), type);
	}
	
	/**
	 * Creates an expression that extracts the given temporal part from the given expression as an integer.<br>
	 *
	 * @param expression The expression to extract from
	 * @param part The temporal part to extract
	 * @return An expression evaluating to the extracted part
	 * @throws NullPointerException If the expression or part is null
	 */
	public static @NonNull SqlExpression<Integer> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part) {
		return new SqlExtractFunction<>(expression, part, SqlTypes.INTEGER);
	}
	
	/**
	 * Creates an expression that extracts the given temporal part from the given expression, using the given numeric result type.<br>
	 *
	 * @param expression The expression to extract from
	 * @param part The temporal part to extract
	 * @param type The sql type of the result
	 * @return An expression evaluating to the extracted part
	 * @throws NullPointerException If the expression, part or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlExtractFunction<>(expression, part, type);
	}
	
	/**
	 * Creates an expression that truncates the given temporal expression to the given temporal part.<br>
	 *
	 * @param expression The temporal expression to truncate
	 * @param part The temporal part to truncate to
	 * @param type The sql type of the result
	 * @return An expression evaluating to the truncated temporal value
	 * @throws NullPointerException If the expression, part or type is null
	 * @param <T> The temporal type of the expression
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlTemporalTruncateFunction<>(expression, part, type);
	}
	
	/**
	 * Creates an expression that adds the given amount of the given temporal part to the given temporal expression.<br>
	 *
	 * @param expression The temporal expression to add to
	 * @param part The temporal part of the amount
	 * @param amount The expression defining the amount to add
	 * @param type The sql type of the result
	 * @return An expression evaluating to the shifted temporal value
	 * @throws NullPointerException If the expression, part, amount or type is null
	 * @param <T> The temporal type of the expression
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlExpression<Integer> amount, @NonNull SqlType<T> type) {
		return new SqlTemporalAddFunction<>(expression, part, amount, type);
	}
	
	/**
	 * Creates an expression that adds the given amount of the given temporal part to the given temporal expression.<br>
	 * The amount is wrapped in an integer value expression.<br>
	 *
	 * @param expression The temporal expression to add to
	 * @param part The temporal part of the amount
	 * @param amount The amount to add
	 * @param type The sql type of the result
	 * @return An expression evaluating to the shifted temporal value
	 * @throws NullPointerException If the expression, part or type is null
	 * @param <T> The temporal type of the expression
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> type) {
		return add(expression, part, of(amount, SqlTypes.INTEGER), type);
	}
	
	/**
	 * Creates an expression that subtracts the given amount of the given temporal part from the given temporal expression.<br>
	 *
	 * @param expression The temporal expression to subtract from
	 * @param part The temporal part of the amount
	 * @param amount The expression defining the amount to subtract
	 * @param type The sql type of the result
	 * @return An expression evaluating to the shifted temporal value
	 * @throws NullPointerException If the expression, part, amount or type is null
	 * @param <T> The temporal type of the expression
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlExpression<?> amount, @NonNull SqlType<T> type) {
		return new SqlTemporalSubtractFunction<>(expression, part, amount, type);
	}
	
	/**
	 * Creates an expression that subtracts the given amount of the given temporal part from the given temporal expression.<br>
	 * The amount is wrapped in an integer value expression.<br>
	 *
	 * @param expression The temporal expression to subtract from
	 * @param part The temporal part of the amount
	 * @param amount The amount to subtract
	 * @param type The sql type of the result
	 * @return An expression evaluating to the shifted temporal value
	 * @throws NullPointerException If the expression, part or type is null
	 * @param <T> The temporal type of the expression
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> type) {
		return subtract(expression, part, of(amount, SqlTypes.INTEGER), type);
	}
	
	/**
	 * Creates an expression that converts the given temporal expression into an epoch value of the given numeric type.<br>
	 *
	 * @param expression The temporal expression to convert
	 * @param type The sql type of the result
	 * @return An expression evaluating to the epoch value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> toEpoch(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToEpochFunction<>(expression, type);
	}
	
	/**
	 * Creates an expression that converts the given expression into a date value of the given temporal type.<br>
	 *
	 * @param expression The expression to convert
	 * @param type The sql type of the result
	 * @return An expression evaluating to the date value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> toDate(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToDateFunction<>(expression, type);
	}
	
	/**
	 * Creates an expression that converts the given expression into a time value of the given temporal type.<br>
	 *
	 * @param expression The expression to convert
	 * @param type The sql type of the result
	 * @return An expression evaluating to the time value
	 * @throws NullPointerException If the expression or type is null
	 * @param <T> The temporal type of the result
	 */
	public static <T extends Temporal> @NonNull SqlExpression<T> toTime(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToTimeFunction<>(expression, type);
	}
	
	/**
	 * Creates a windowed expression that evaluates the given aggregate over the given window clause.<br>
	 *
	 * @param aggregate The aggregate function to apply
	 * @param clause The window clause defining partitioning, ordering and framing
	 * @return An expression evaluating to the windowed aggregate
	 * @throws NullPointerException If the aggregate or clause is null
	 * @param <T> The type of the aggregated value
	 */
	public static <T> @NonNull SqlExpression<T> over(@NonNull SqlAggregateFunction<T> aggregate, @NonNull SqlWindowClause clause) {
		return new SqlWindowedAggregate<>(aggregate, clause);
	}
	
	/**
	 * Creates a window expression that evaluates to the sequential number of the current row within its window.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the row number
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Long> rowNumber(@NonNull SqlWindowClause over) {
		return new SqlRowNumberFunction<>(over, SqlTypes.LONG);
	}
	
	/**
	 * Creates a window expression that evaluates to the sequential number of the current row within its window, using the given numeric result type.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the row number
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> rowNumber(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlRowNumberFunction<>(over, type);
	}
	
	/**
	 * Creates a window expression that evaluates to the rank of the current row within its window, leaving gaps after ties.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the rank
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Long> rank(@NonNull SqlWindowClause over) {
		return new SqlRankFunction<>(over, SqlTypes.LONG);
	}
	
	/**
	 * Creates a window expression that evaluates to the rank of the current row within its window, leaving gaps after ties and using the given numeric result type.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the rank
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> rank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlRankFunction<>(over, type);
	}
	
	/**
	 * Creates a window expression that evaluates to the rank of the current row within its window without leaving gaps after ties.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the dense rank
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Long> denseRank(@NonNull SqlWindowClause over) {
		return new SqlDenseRankFunction<>(over, SqlTypes.LONG);
	}
	
	/**
	 * Creates a window expression that evaluates to the rank of the current row within its window without leaving gaps after ties, using the given numeric result type.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the dense rank
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> denseRank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlDenseRankFunction<>(over, type);
	}
	
	/**
	 * Creates a window expression that distributes the rows of its window into the given number of buckets and evaluates to the bucket of the current row.<br>
	 *
	 * @param buckets The number of buckets to distribute the rows into
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the bucket of the current row
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Long> tileBucket(int buckets, @NonNull SqlWindowClause over) {
		return new SqlTileBucketFunction<>(of(buckets, SqlTypes.INTEGER), over, SqlTypes.LONG);
	}
	
	/**
	 * Creates a window expression that distributes the rows of its window into the given number of buckets and evaluates to the bucket of the current row, using the given numeric result type.<br>
	 *
	 * @param buckets The number of buckets to distribute the rows into
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the bucket of the current row
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> tileBucket(int buckets, @NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlTileBucketFunction<>(of(buckets, SqlTypes.INTEGER), over, type);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression in the preceding row of the window.<br>
	 *
	 * @param expression The expression to evaluate in the preceding row
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the preceding row
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, null, null, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression a given number of rows before the current row of the window.<br>
	 * The offset is wrapped in an integer value expression.<br>
	 *
	 * @param expression The expression to evaluate in the preceding row
	 * @param offset The number of rows to look back
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the offset row
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, of(offset, SqlTypes.INTEGER), null, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression a given number of rows before the current row of the window, falling back to the default value when no such row exists.<br>
	 * The offset is wrapped in an integer value expression and the default value using the given type.<br>
	 *
	 * @param expression The expression to evaluate in the preceding row
	 * @param offset The number of rows to look back
	 * @param defaultValue The value used when no preceding row exists
	 * @param type The sql type of the default value
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the offset row or the default value
	 * @throws NullPointerException If the expression, default value, type or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlType<T> type, @NonNull SqlWindowClause over) {
		return new SqlLagFunction<>(expression, of(offset, SqlTypes.INTEGER), of(defaultValue, type), over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression in the following row of the window.<br>
	 *
	 * @param expression The expression to evaluate in the following row
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the following row
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, null, null, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression a given number of rows after the current row of the window.<br>
	 * The offset is wrapped in an integer value expression.<br>
	 *
	 * @param expression The expression to evaluate in the following row
	 * @param offset The number of rows to look ahead
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the offset row
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, of(offset, SqlTypes.INTEGER), null, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression a given number of rows after the current row of the window, falling back to the default value when no such row exists.<br>
	 * The offset is wrapped in an integer value expression and the default value using the given type.<br>
	 *
	 * @param expression The expression to evaluate in the following row
	 * @param offset The number of rows to look ahead
	 * @param defaultValue The value used when no following row exists
	 * @param type The sql type of the default value
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the value of the offset row or the default value
	 * @throws NullPointerException If the expression, default value, type or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlExpression<T> expression, int offset, @NonNull T defaultValue, @NonNull SqlType<T> type, @NonNull SqlWindowClause over) {
		return new SqlLeadFunction<>(expression, of(offset, SqlTypes.INTEGER), of(defaultValue, type), over);
	}
	
	/**
	 * Creates a window expression that evaluates to the relative rank of the current row within its window as a value between zero and one.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the percent rank
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Double> percentRank(@NonNull SqlWindowClause over) {
		return new SqlPercentRankFunction<>(over, SqlTypes.DOUBLE);
	}
	
	/**
	 * Creates a window expression that evaluates to the relative rank of the current row within its window as a value between zero and one, using the given numeric result type.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the percent rank
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> percentRank(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlPercentRankFunction<>(over, type);
	}
	
	/**
	 * Creates a window expression that evaluates to the cumulative distribution of the current row within its window.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @return An expression evaluating to the cumulative distribution
	 * @throws NullPointerException If the window clause is null
	 */
	public static @NonNull SqlExpression<Double> cumulativeDistribution(@NonNull SqlWindowClause over) {
		return new SqlCumulativeDistributionFunction<>(over, SqlTypes.DOUBLE);
	}
	
	/**
	 * Creates a window expression that evaluates to the cumulative distribution of the current row within its window, using the given numeric result type.<br>
	 *
	 * @param over The window clause defining partitioning and ordering
	 * @param type The sql type of the result
	 * @return An expression evaluating to the cumulative distribution
	 * @throws NullPointerException If the window clause or type is null
	 * @param <T> The numeric type of the result
	 */
	public static <T extends Number> @NonNull SqlExpression<T> cumulativeDistribution(@NonNull SqlWindowClause over, @NonNull SqlType<T> type) {
		return new SqlCumulativeDistributionFunction<>(over, type);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression in the first row of the window frame.<br>
	 *
	 * @param expression The expression to evaluate in the first row
	 * @param over The window clause defining partitioning, ordering and framing
	 * @return An expression evaluating to the first value of the frame
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> firstValue(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlFirstValueFunction<>(expression, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression in the last row of the window frame.<br>
	 *
	 * @param expression The expression to evaluate in the last row
	 * @param over The window clause defining partitioning, ordering and framing
	 * @return An expression evaluating to the last value of the frame
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> lastValue(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause over) {
		return new SqlLastValueFunction<>(expression, over);
	}
	
	/**
	 * Creates a window expression that evaluates to the value of the given expression at the given position within the window frame.<br>
	 * The position is wrapped in an integer value expression.<br>
	 *
	 * @param expression The expression to evaluate at the given position
	 * @param position The one-based position within the frame
	 * @param over The window clause defining partitioning, ordering and framing
	 * @return An expression evaluating to the value at the given position
	 * @throws NullPointerException If the expression or window clause is null
	 * @param <T> The type of the value
	 */
	public static <T> @NonNull SqlExpression<T> valueAt(@NonNull SqlExpression<T> expression, int position, @NonNull SqlWindowClause over) {
		return new SqlValueAtFunction<>(expression, of(position, SqlTypes.INTEGER), over);
	}
}
