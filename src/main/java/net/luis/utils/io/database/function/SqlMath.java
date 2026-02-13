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

import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL math functions.<br>
 *
 * @author Luis-St
 */
public class SqlMath {
	
	/**
	 * Returns the absolute value of a numeric expression.<br>
	 * Generates SQL: {@code ABS(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param <T> The numeric type of the expression
	 * @return The absolute value expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> abs(@NonNull SqlExpression<T> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Rounds a numeric expression to the nearest integer.<br>
	 * Generates SQL: {@code ROUND(expression)}.<br>
	 *
	 * @param expr The expression to round
	 * @param <T> The numeric type of the expression
	 * @return The rounded expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the smallest integer greater than or equal to the expression value.<br>
	 * Generates SQL: {@code CEIL(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param <T> The numeric type of the expression
	 * @return The ceiling expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> ceil(@NonNull SqlExpression<T> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the largest integer less than or equal to the expression value.<br>
	 * Generates SQL: {@code FLOOR(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param <T> The numeric type of the expression
	 * @return The floor expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> floor(@NonNull SqlExpression<T> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the remainder of the expression value divided by the given divisor.<br>
	 * Generates SQL: {@code MOD(expression, divisor)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param divisor The divisor
	 * @param <T> The numeric type of the expression
	 * @return The modulo expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> mod(@NonNull SqlExpression<T> expr, @NonNull Number divisor) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Raises the expression value to the given power.<br>
	 * Generates SQL: {@code POWER(expression, exponent)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param exponent The exponent
	 * @param <T> The numeric type of the expression
	 * @return The power expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> power(@NonNull SqlExpression<T> expr, @NonNull Number exponent) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the square root of a numeric expression.<br>
	 * Generates SQL: {@code SQRT(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The square root expression as a double
	 */
	public static @NonNull SqlExpression<Double> sqrt(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a random number.<br>
	 * Generates SQL: {@code RANDOM()} or {@code RAND()} depending on the dialect.<br>
	 *
	 * @return The random expression as a double
	 */
	public static @NonNull SqlExpression<Double> random() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the sign of a numeric expression (-1, 0, or 1).<br>
	 * Generates SQL: {@code SIGN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The sign expression as an integer
	 */
	public static @NonNull SqlExpression<Integer> sign(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Rounds a numeric expression to the specified number of decimal places.<br>
	 * Generates SQL: {@code ROUND(expression, decimals)}.<br>
	 *
	 * @param expr The expression to round
	 * @param decimals The number of decimal places
	 * @param <T> The numeric type of the expression
	 * @return The rounded expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> round(@NonNull SqlExpression<T> expr, int decimals) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Truncates a numeric expression to the specified number of decimal places.<br>
	 * Generates SQL: {@code TRUNCATE(expression, decimals)} or {@code TRUNC(expression, decimals)} depending on the dialect.<br>
	 *
	 * @param expr The expression to truncate
	 * @param decimals The number of decimal places
	 * @param <T> The numeric type of the expression
	 * @return The truncated expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expr, int decimals) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the exponential value of a numeric expression (e^x).<br>
	 * Generates SQL: {@code EXP(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The exponential expression as a double
	 */
	public static @NonNull SqlExpression<Double> exp(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the natural logarithm of a numeric expression.<br>
	 * Generates SQL: {@code LN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The natural logarithm expression as a double
	 */
	public static @NonNull SqlExpression<Double> ln(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the base-10 logarithm of a numeric expression.<br>
	 * Generates SQL: {@code LOG10(expression)} or {@code LOG(10, expression)} depending on the dialect.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The base-10 logarithm expression as a double
	 */
	public static @NonNull SqlExpression<Double> log10(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the base-2 logarithm of a numeric expression.<br>
	 * Generates SQL: {@code LOG2(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The base-2 logarithm expression as a double
	 */
	public static @NonNull SqlExpression<Double> log2(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the sine of a numeric expression (in radians).<br>
	 * Generates SQL: {@code SIN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The sine expression as a double
	 */
	public static @NonNull SqlExpression<Double> sin(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the cosine of a numeric expression (in radians).<br>
	 * Generates SQL: {@code COS(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The cosine expression as a double
	 */
	public static @NonNull SqlExpression<Double> cos(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the tangent of a numeric expression (in radians).<br>
	 * Generates SQL: {@code TAN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The tangent expression as a double
	 */
	public static @NonNull SqlExpression<Double> tan(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the arc sine of a numeric expression.<br>
	 * Generates SQL: {@code ASIN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The arc sine expression as a double
	 */
	public static @NonNull SqlExpression<Double> asin(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the arc cosine of a numeric expression.<br>
	 * Generates SQL: {@code ACOS(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The arc cosine expression as a double
	 */
	public static @NonNull SqlExpression<Double> acos(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the arc tangent of a numeric expression.<br>
	 * Generates SQL: {@code ATAN(expression)}.<br>
	 *
	 * @param expr The expression to evaluate
	 * @return The arc tangent expression as a double
	 */
	public static @NonNull SqlExpression<Double> atan(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the arc tangent of y/x, using the signs to determine the quadrant.<br>
	 * Generates SQL: {@code ATAN2(y, x)}.<br>
	 *
	 * @param y The y-coordinate expression
	 * @param x The x-coordinate
	 * @return The arc tangent expression as a double
	 */
	public static @NonNull SqlExpression<Double> atan2(@NonNull SqlExpression<? extends Number> y, @NonNull Number x) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a numeric expression from degrees to radians.<br>
	 * Generates SQL: {@code RADIANS(expression)}.<br>
	 *
	 * @param expr The expression in degrees
	 * @return The radians expression as a double
	 */
	public static @NonNull SqlExpression<Double> radians(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a numeric expression from radians to degrees.<br>
	 * Generates SQL: {@code DEGREES(expression)}.<br>
	 *
	 * @param expr The expression in radians
	 * @return The degrees expression as a double
	 */
	public static @NonNull SqlExpression<Double> degrees(@NonNull SqlExpression<? extends Number> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value of pi.<br>
	 * Generates SQL: {@code PI()}.<br>
	 *
	 * @return The pi expression as a double
	 */
	public static @NonNull SqlExpression<Double> pi() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Performs a bitwise AND operation between the expression and the given mask.<br>
	 * Generates SQL: {@code expression & mask} or dialect equivalent.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param mask The bitmask
	 * @param <T> The numeric type of the expression
	 * @return The bitwise AND expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitAnd(@NonNull SqlExpression<T> expr, long mask) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Performs a bitwise OR operation between the expression and the given mask.<br>
	 * Generates SQL: {@code expression | mask} or dialect equivalent.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param mask The bitmask
	 * @param <T> The numeric type of the expression
	 * @return The bitwise OR expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitOr(@NonNull SqlExpression<T> expr, long mask) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Performs a bitwise XOR operation between the expression and the given mask.<br>
	 * Generates SQL: {@code expression ^ mask} or dialect equivalent.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param mask The bitmask
	 * @param <T> The numeric type of the expression
	 * @return The bitwise XOR expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitXor(@NonNull SqlExpression<T> expr, long mask) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Performs a bitwise NOT operation on the expression.<br>
	 * Generates SQL: {@code ~expression} or dialect equivalent.<br>
	 *
	 * @param expr The expression to evaluate
	 * @param <T> The numeric type of the expression
	 * @return The bitwise NOT expression of the same type
	 */
	public static <T extends Number> @NonNull SqlExpression<T> bitNot(@NonNull SqlExpression<T> expr) {
		throw new UnsupportedOperationException();
	}
}
