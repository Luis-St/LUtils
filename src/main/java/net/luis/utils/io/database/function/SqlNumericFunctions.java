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
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Luis-St
 *
 */

public abstract class SqlNumericFunctions {
	
	public static @NonNull SqlCondition isPositive(@NonNull SqlExpression<? extends Number> expression) {
		return null;
	}
	
	public static @NonNull SqlCondition isNegative(@NonNull SqlExpression<? extends Number> expression) {
		return null;
	}
	
	public static @NonNull SqlCondition isZero(@NonNull SqlExpression<? extends Number> expression) {
		return null;
	}
	
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull Number divisor, @NonNull Number remainder) {
		return null;
	}
	
	public static @NonNull SqlCondition modEquals(@NonNull SqlExpression<? extends Number> expression, @NonNull SqlExpression<? extends Number> divisor, @NonNull SqlExpression<? extends Number> remainder) {
		return null;
	}
	
	public static @NonNull SqlExpression<Double> random() {
		return null;
	}
	
	public static @NonNull SqlExpression<Double> pi() {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> negate(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> sum(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> average(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> abs(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, int precision) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> precision) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> ceil(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> floor(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull Number divisor) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> divisor) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull Number exponent) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> pow(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> exponent) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> sqrt(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> sign(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> exp(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> log2(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> ln(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> log10(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> sin(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> cos(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> tan(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> asin(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> acos(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> atan(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> atan2(@NonNull SqlExpression<T> expression, @NonNull Number x) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> atan2(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> x) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> radians(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> degrees(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseAnd(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseOr(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull Number other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseXor(@NonNull SqlExpression<T> expression, @NonNull SqlExpression<? extends Number> other) {
		return null;
	}
	
	public static <T> @NonNull SqlExpression<T> bitwiseNot() {
		return null;
	}
}
