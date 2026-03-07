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

package net.luis.utils.io.databasev1.function.scalar;

import net.luis.utils.io.databasev1.function.SqlWindowExpression;
import net.luis.utils.io.databasev1.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL aggregate functions.<br>
 *
 * @author Luis-St
 */
public class SqlAgg {
	
	/**
	 * Counts the number of rows.<br>
	 * Generates SQL: {@code COUNT(*)}.<br>
	 *
	 * @return The count expression
	 */
	public static @NonNull SqlWindowExpression<Long> count() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Calculates the sum of a numeric column.<br>
	 * Generates SQL: {@code SUM(column)}.<br>
	 *
	 * @param column The column to sum
	 * @param <T> The numeric type of the column
	 * @return The sum expression of the same type
	 */
	public static <T extends Number> @NonNull SqlWindowExpression<T> sum(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Calculates the average of a numeric column.<br>
	 * Generates SQL: {@code AVG(column)}.<br>
	 *
	 * @param column The column to average
	 * @return The average expression as a double
	 */
	public static @NonNull SqlWindowExpression<Double> avg(@NonNull SqlColumn<? extends Number> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the minimum value of a column.<br>
	 * Generates SQL: {@code MIN(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @param <T> The type of the column
	 * @return The min expression
	 */
	public static <T extends Comparable<T>> @NonNull SqlWindowExpression<T> min(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the maximum value of a column.<br>
	 * Generates SQL: {@code MAX(column)}.<br>
	 *
	 * @param column The column to evaluate
	 * @param <T> The type of the column
	 * @return The max expression
	 */
	public static <T extends Comparable<T>> @NonNull SqlWindowExpression<T> max(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Counts the number of distinct values in a column.<br>
	 * Generates SQL: {@code COUNT(DISTINCT column)}.<br>
	 *
	 * @param column The column to count distinct values of
	 * @return The count-distinct expression
	 */
	public static @NonNull SqlWindowExpression<Long> countDistinct(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}
}
