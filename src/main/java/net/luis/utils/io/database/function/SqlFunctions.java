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

package net.luis.utils.io.database.function;

import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.condition.SqlExpression;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.query.crud.SqlSelectQuery;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlFunctions {
	
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition equalTo(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, T @NonNull ... values) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, SqlExpression<T> @NonNull ... otherExpressions) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition in(@NonNull SqlExpression<T> expression, @NonNull SqlSelectQuery<T> subquery) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull T value) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition isDistinctFrom(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> other) {
		return null;
	}
	
	public static <T> @NonNull SqlCondition isNull(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<Long> count(@NonNull SqlExpression<T> expression, boolean distinct) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> cast(@NonNull SqlExpression<?> expression, @NonNull Class<T> targetType) {
		return null;
	}
	
	@SafeVarargs
	public static <T> @NonNull SqlExpression<T> coalesce(@NonNull SqlExpression<T> @NonNull ... values) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull T fallback) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> nullIf(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<T> fallback) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull T thenValue, @NonNull T elseValue) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> caseWhen(@NonNull SqlCondition condition, @NonNull SqlExpression<T> thenValue, @NonNull SqlExpression<T> elseValue) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> ofUnsafe(@NonNull String functionName, @NonNull Class<T> resultType, SqlExpression<?> @NonNull ... args) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> over() {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> over(@NonNull SqlExpression<T> expression, @NonNull SqlWindowClause clause) {
		return null;
	}
	
	public static @NonNull SqlExpression<Long> rowNumber() {
		return null;
	}
	
	public static @NonNull SqlExpression<Long> rank() {
		return null;
	}
	
	public static @NonNull SqlExpression<Long> denseRank() {
		return null;
	}
	
	public static @NonNull SqlExpression<Long> tileBucket(int buckets) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column, int offset) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lag(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column, int offset) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lead(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		return null;
	}
	
	public static @NonNull SqlExpression<Double> percentRank() {
		return null;
	}
	
	public static @NonNull SqlExpression<Double> cumulativeDistribution() {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> firstValue(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> lastValue(@NonNull SqlColumn<T> column) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> valueAt(@NonNull SqlColumn<T> column, int position) {
		return null;
	}
}
