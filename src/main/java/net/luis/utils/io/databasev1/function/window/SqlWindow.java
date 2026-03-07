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

package net.luis.utils.io.databasev1.function.window;

import net.luis.utils.io.databasev1.function.SqlWindowExpression;
import net.luis.utils.io.databasev1.table.SqlColumn;
import org.jspecify.annotations.NonNull;

/**
 * Static utility class for SQL window functions.<br>
 *
 * @author Luis-St
 */
public class SqlWindow {
	
	/**
	 * Returns the row number of each row within a result set partition.<br>
	 * Generates SQL: {@code ROW_NUMBER()}.<br>
	 *
	 * @return The row number expression
	 */
	public static @NonNull SqlWindowExpression<Long> rowNumber() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the rank of each row within a result set partition with gaps.<br>
	 * Generates SQL: {@code RANK()}.<br>
	 *
	 * @return The rank expression
	 */
	public static @NonNull SqlWindowExpression<Long> rank() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the rank of each row within a result set partition without gaps.<br>
	 * Generates SQL: {@code DENSE_RANK()}.<br>
	 *
	 * @return The dense rank expression
	 */
	public static @NonNull SqlWindowExpression<Long> denseRank() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Distributes rows into the specified number of groups.<br>
	 * Generates SQL: {@code NTILE(buckets)}.<br>
	 *
	 * @param buckets The number of groups
	 * @return The ntile expression
	 */
	public static @NonNull SqlWindowExpression<Long> ntile(int buckets) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the previous row in the result set.<br>
	 * Generates SQL: {@code LAG(column)}.<br>
	 *
	 * @param column The column to access
	 * @param <T> The type of the column
	 * @return The lag expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lag(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the next row in the result set.<br>
	 * Generates SQL: {@code LEAD(column)}.<br>
	 *
	 * @param column The column to access
	 * @param <T> The type of the column
	 * @return The lead expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lead(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the relative rank of each row within a result set partition as a value between 0 and 1.<br>
	 * Generates SQL: {@code PERCENT_RANK()}.<br>
	 *
	 * @return The percent rank expression
	 */
	public static @NonNull SqlWindowExpression<Double> percentRank() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the cumulative distribution of a value within a result set partition.<br>
	 * Generates SQL: {@code CUME_DIST()}.<br>
	 *
	 * @return The cumulative distribution expression
	 */
	public static @NonNull SqlWindowExpression<Double> cumeDist() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the row at the specified offset before the current row.<br>
	 * Generates SQL: {@code LAG(column, offset)}.<br>
	 *
	 * @param column The column to access
	 * @param offset The number of rows before the current row
	 * @param <T> The type of the column
	 * @return The lag expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lag(@NonNull SqlColumn<T> column, int offset) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the row at the specified offset before the current row, with a default value for out-of-range rows.<br>
	 * Generates SQL: {@code LAG(column, offset, defaultValue)}.<br>
	 *
	 * @param column The column to access
	 * @param offset The number of rows before the current row
	 * @param defaultValue The default value if the offset goes out of range
	 * @param <T> The type of the column
	 * @return The lag expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lag(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the row at the specified offset after the current row.<br>
	 * Generates SQL: {@code LEAD(column, offset)}.<br>
	 *
	 * @param column The column to access
	 * @param offset The number of rows after the current row
	 * @param <T> The type of the column
	 * @return The lead expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lead(@NonNull SqlColumn<T> column, int offset) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the value from the row at the specified offset after the current row, with a default value for out-of-range rows.<br>
	 * Generates SQL: {@code LEAD(column, offset, defaultValue)}.<br>
	 *
	 * @param column The column to access
	 * @param offset The number of rows after the current row
	 * @param defaultValue The default value if the offset goes out of range
	 * @param <T> The type of the column
	 * @return The lead expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lead(@NonNull SqlColumn<T> column, int offset, @NonNull T defaultValue) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the first value in an ordered partition.<br>
	 * Generates SQL: {@code FIRST_VALUE(column)}.<br>
	 *
	 * @param column The column to access
	 * @param <T> The type of the column
	 * @return The first value expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> firstValue(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the last value in an ordered partition.<br>
	 * Generates SQL: {@code LAST_VALUE(column)}.<br>
	 *
	 * @param column The column to access
	 * @param <T> The type of the column
	 * @return The last value expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> lastValue(@NonNull SqlColumn<T> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the nth value in an ordered partition.<br>
	 * Generates SQL: {@code NTH_VALUE(column, n)}.<br>
	 *
	 * @param column The column to access
	 * @param n The position of the value to return (1-based)
	 * @param <T> The type of the column
	 * @return The nth value expression
	 */
	public static <T> @NonNull SqlWindowExpression<T> nthValue(@NonNull SqlColumn<T> column, int n) {
		throw new UnsupportedOperationException();
	}
}
