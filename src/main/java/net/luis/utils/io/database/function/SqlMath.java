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

import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL math functions.<br>
 *
 * @author Luis-St
 */
public class SqlMath {
	
	/**
	 * Returns the absolute value of a numeric column.<br>
	 * Generates SQL: {@code ABS(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @return The absolute value expression
	 */
	public static @NonNull SqlExpression<Number> abs(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Rounds a numeric column to the nearest integer.<br>
	 * Generates SQL: {@code ROUND(column)}.<br>
	 *
	 * @param column The column to round
	 * @return The rounded expression
	 */
	public static @NonNull SqlExpression<Number> round(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the smallest integer greater than or equal to the column value.<br>
	 * Generates SQL: {@code CEIL(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @return The ceiling expression
	 */
	public static @NonNull SqlExpression<Number> ceil(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the largest integer less than or equal to the column value.<br>
	 * Generates SQL: {@code FLOOR(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @return The floor expression
	 */
	public static @NonNull SqlExpression<Number> floor(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the remainder of the column value divided by the given divisor.<br>
	 * Generates SQL: {@code MOD(column, divisor)}.<br>
	 *
	 * @param column The column to evaluate
	 * @param divisor The divisor
	 * @return The modulo expression
	 */
	public static @NonNull SqlExpression<Number> mod(@NonNull SqlColumn<? extends Number> column, @NonNull Number divisor) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Raises the column value to the given power.<br>
	 * Generates SQL: {@code POWER(column, exponent)}.<br>
	 *
	 * @param column The column to evaluate
	 * @param exponent The exponent
	 * @return The power expression
	 */
	public static @NonNull SqlExpression<Number> power(@NonNull SqlColumn<? extends Number> column, @NonNull Number exponent) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the square root of a numeric column.<br>
	 * Generates SQL: {@code SQRT(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @return The square root expression
	 */
	public static @NonNull SqlExpression<Number> sqrt(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns a random number.<br>
	 * Generates SQL: {@code RANDOM()} or {@code RAND()} depending on the dialect.<br>
	 *
	 * @return The random expression
	 */
	public static @NonNull SqlExpression<Number> random() {
		throw new UnsupportedOperationException();
	}
}
