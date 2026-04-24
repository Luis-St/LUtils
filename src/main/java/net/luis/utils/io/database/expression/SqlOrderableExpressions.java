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
import net.luis.utils.io.database.function.functions.aggregate.SqlMaxFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlMinFunction;
import net.luis.utils.io.database.function.functions.generic.SqlGreatestFunction;
import net.luis.utils.io.database.function.functions.generic.SqlLeastFunction;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlOrderableExpressions {
	
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlGreaterThanCondition(expression, SqlExpression.of(value), false);
	}
	
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlGreaterThanCondition(expression, SqlExpression.of(value), true);
	}
	
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, false);
	}
	
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlGreaterThanCondition(expression, other, true);
	}
	
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlLessThanCondition(expression, SqlExpression.of(value), false);
	}
	
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return new SqlLessThanCondition(expression, SqlExpression.of(value), true);
	}
	
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, false);
	}
	
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return new SqlLessThanCondition(expression, other, true);
	}
	
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull T start, @NonNull T end) {
		return new SqlBetweenCondition(expression, SqlExpression.of(start), SqlExpression.of(end));
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
	@SuppressWarnings("DuplicatedCode")
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
	@SuppressWarnings("DuplicatedCode")
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
}
