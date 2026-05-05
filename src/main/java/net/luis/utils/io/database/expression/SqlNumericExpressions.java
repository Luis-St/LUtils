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
import net.luis.utils.io.database.condition.conditions.numeric.*;
import net.luis.utils.io.database.function.functions.aggregate.SqlAverageFunction;
import net.luis.utils.io.database.function.functions.aggregate.SqlSumFunction;
import net.luis.utils.io.database.function.functions.numeric.*;
import net.luis.utils.io.database.function.functions.numeric.bitwise.*;
import net.luis.utils.io.database.function.functions.numeric.trigonometric.*;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class SqlNumericExpressions {
	
	public static @NonNull SqlCondition isPositive(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsPositiveCondition(expression);
	}
	
	public static @NonNull SqlCondition isNegative(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsNegativeCondition(expression);
	}
	
	public static @NonNull SqlCondition isZero(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlIsZeroCondition(expression);
	}
	
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull Number divisor, @NonNull Number remainder) {
		return new SqlModEqualsCondition(expression, SqlExpression.of(divisor), SqlExpression.of(remainder));
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
	
	public static <T extends Number> @NonNull SqlExpression<T> negate(@NonNull SqlExpression<T> expression) {
		return new SqlNegateFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> sum(@NonNull SqlExpression<T> expression) {
		return new SqlSumFunction(expression);
	}
	
	public static @NonNull SqlExpression<Number> average(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlAverageFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> abs(@NonNull SqlExpression<T> expression) {
		return new SqlAbsFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression) {
		return new SqlRoundFunction(expression, null);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, int precision) {
		return new SqlRoundFunction(expression, SqlExpression.of(precision));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> precision) {
		return new SqlRoundFunction(expression, precision);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> ceil(@NonNull SqlExpression<T> expression) {
		return new SqlCeilFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> floor(@NonNull SqlExpression<T> expression) {
		return new SqlFloorFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression) {
		return new SqlNumericTruncateFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull Number divisor) {
		return new SqlModFunction(expression, SqlExpression.of(divisor));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return new SqlModFunction(expression, divisor);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull Number exponent) {
		return new SqlPowFunction(expression, SqlExpression.of(exponent));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> exponent) {
		return new SqlPowFunction(expression, exponent);
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
		return new SqlLogFunction(expression, SqlExpression.of(2));
	}
	
	public static @NonNull SqlExpression<Double> ln(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, SqlExpression.of(Math.E));
	}
	
	public static @NonNull SqlExpression<Double> log10(@NonNull SqlExpression<? extends Number> expression) {
		return new SqlLogFunction(expression, SqlExpression.of(10));
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
	
	public static @NonNull SqlExpression<Double> atan2(@NonNull SqlExpression<? extends Number> expression, @NonNull Number x) {
		return new SqlAtan2Function(expression, SqlExpression.of(x));
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
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return new SqlBitwiseAndFunction(expression, SqlExpression.of(other));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return new SqlBitwiseAndFunction(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) {
		return new SqlBitwiseAndFunction(expression, SqlExpression.of(other), type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseAndFunction(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return new SqlBitwiseOrFunction(expression, SqlExpression.of(other));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return new SqlBitwiseOrFunction(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) {
		return new SqlBitwiseOrFunction(expression, SqlExpression.of(other), type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseOrFunction(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return new SqlBitwiseXorFunction(expression, SqlExpression.of(other));
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return new SqlBitwiseXorFunction(expression, other);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull Number other, @NonNull SqlType<T> type) {
		return new SqlBitwiseXorFunction(expression, SqlExpression.of(other), type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other, @NonNull SqlType<T> type) {
		return new SqlBitwiseXorFunction(expression, other, type);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression) {
		return new SqlBitwiseNotFunction(expression);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> bitwiseNot(@NonNull SqlExpression<T> expression, @NonNull SqlType<T> type) {
		return new SqlBitwiseNotFunction(expression, type);
	}
}
