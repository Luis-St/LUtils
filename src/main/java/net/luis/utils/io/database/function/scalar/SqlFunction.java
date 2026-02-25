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

package net.luis.utils.io.database.function.scalar;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.dialect.SqlColumnType;
import net.luis.utils.io.database.function.SqlExpression;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for general SQL functions.<br>
 *
 * @author Luis-St
 */
public class SqlFunction {
	
	/**
	 * Returns the first non-null value from the given expressions.<br>
	 * Generates SQL: {@code COALESCE(val1, val2, ...)}.<br>
	 *
	 * @param values The expressions to evaluate
	 * @param <T> The type of the expressions
	 * @return The coalesce expression of the same type
	 */
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> coalesce(@NonNull SqlExpression<T> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns {@code null} if the two values are equal, otherwise returns the first value.<br>
	 * Generates SQL: {@code NULLIF(val1, val2)}.<br>
	 *
	 * @param value1 The first value
	 * @param value2 The second value to compare with
	 * @param <T> The type of the expression
	 * @return The nullif expression
	 */
	public static <T> @NonNull SqlExpression<T> nullif(@NonNull SqlExpression<T> value1, @NonNull T value2) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Casts the given expression to the specified type.<br>
	 * Generates SQL: {@code CAST(value AS type)}.<br>
	 *
	 * @param value The expression to cast
	 * @param type The target type
	 * @param <T> The target type
	 * @return The cast expression
	 */
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> value, @NonNull Class<T> type) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Casts the given expression to the specified SQL column type.<br>
	 * The returned expression defers rendering to {@code toSql(SqlRenderer)},
	 * where the renderer's dialect maps the column type to a concrete SQL type name.<br>
	 * Generates SQL: {@code CAST(value AS mapped_type)}.<br>
	 *
	 * @param value The expression to cast
	 * @param columnType The target SQL column type
	 * @param <T> The result type
	 * @return The cast expression
	 */
	@SuppressWarnings("unchecked")
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> value, @NonNull SqlColumnType columnType) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the greatest value from the given expressions.<br>
	 * Generates SQL: {@code GREATEST(val1, val2, ...)}.<br>
	 *
	 * @param values The expressions to evaluate
	 * @param <T> The comparable type of the expressions
	 * @return The greatest expression of the same type
	 */
	@SafeVarargs
	public static <T extends Comparable<T>> @NonNull SqlExpression<T> greatest(@NonNull SqlExpression<T> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the least value from the given expressions.<br>
	 * Generates SQL: {@code LEAST(val1, val2, ...)}.<br>
	 *
	 * @param values The expressions to evaluate
	 * @param <T> The comparable type of the expressions
	 * @return The least expression of the same type
	 */
	@SafeVarargs
	public static <T extends Comparable<T>> @NonNull SqlExpression<T> least(@NonNull SqlExpression<T> @NonNull ... values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a conditional expression.<br>
	 * Generates SQL: {@code CASE WHEN cond THEN val1 ELSE val2 END}.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param thenValue The value to return if the condition is true
	 * @param elseValue The value to return if the condition is false
	 * @param <T> The type of the values
	 * @return The case-when expression of the same type
	 */
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull T thenValue, @NonNull T elseValue) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a custom SQL function expression.<br>
	 * Generates SQL: {@code functionName(arg1, arg2, ...)}.<br>
	 *
	 * @param functionName The name of the SQL function
	 * @param resultType The result type of the function
	 * @param args The function arguments
	 * @param <T> The result type
	 * @return The function expression
	 */
	public static <T> @NonNull SqlExpression<T> of(@NonNull String functionName, @NonNull Class<T> resultType, SqlExpression<?> @NonNull ... args) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates an IF expression that returns one of two values based on a condition.<br>
	 * Generates SQL: {@code IF(condition, ifTrue, ifFalse)} or {@code CASE WHEN condition THEN ifTrue ELSE ifFalse END} depending on the dialect.<br>
	 *
	 * @param condition The condition to evaluate
	 * @param ifTrue The value to return if the condition is true
	 * @param ifFalse The value to return if the condition is false
	 * @param <T> The type of the values
	 * @return The conditional expression of the same type
	 */
	public static <T> @NonNull SqlExpression<T> ifExpression(@NonNull SqlCondition condition, @NonNull T ifTrue, @NonNull T ifFalse) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a condition that checks if the expression is distinct from the given value.<br>
	 * Unlike {@code <>}, this treats {@code NULL} as a comparable value.<br>
	 * Generates SQL: {@code expression IS DISTINCT FROM value} or dialect equivalent.<br>
	 *
	 * @param expr The expression to compare
	 * @param value The value to compare to
	 * @param <T> The type of the expression and value
	 * @return The is-distinct-from condition
	 */
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expr, @NonNull T value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a condition that checks if the expression is not distinct from the given value.<br>
	 * Unlike {@code =}, this treats {@code NULL} as a comparable value.<br>
	 * Generates SQL: {@code expression IS NOT DISTINCT FROM value} or dialect equivalent.<br>
	 *
	 * @param expr The expression to compare
	 * @param value The value to compare to
	 * @param <T> The type of the expression and value
	 * @return The is-not-distinct-from condition
	 */
	public static <T> @NonNull SqlCondition isNotDistinctFrom(@NonNull SqlExpression<T> expr, @NonNull T value) {
		throw new UnsupportedOperationException();
	}
}
