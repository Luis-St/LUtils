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

import net.luis.utils.io.database.condition.SqlCondition;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlOrderableExpressions {
	
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition greaterThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition greaterThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition lessThan(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition lessThanOrEqualTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull T start, @NonNull T end) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition between(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> start, @NonNull SqlExpression<T> end) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> min(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> max(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> greatest(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		return null;
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> least(@NonNull SqlExpression<T> first, @NonNull SqlExpression<T> second, @NonNull SqlExpression<T> @NonNull ... others) {
		return null;
	}
}
