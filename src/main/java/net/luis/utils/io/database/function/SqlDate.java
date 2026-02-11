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

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Static utility class for SQL date functions.<br>
 *
 * @author Luis-St
 */
public class SqlDate {
	
	/**
	 * Returns the current timestamp.<br>
	 * Generates SQL: {@code NOW()} or {@code CURRENT_TIMESTAMP} depending on the dialect.<br>
	 *
	 * @return The current timestamp expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> now() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the current date.<br>
	 * Generates SQL: {@code CURRENT_DATE}.<br>
	 *
	 * @return The current date expression
	 */
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the year from a date or timestamp column.<br>
	 * Generates SQL: {@code EXTRACT(YEAR FROM column)}.<br>
	 *
	 * @param column The date or timestamp column
	 * @return The year expression
	 */
	public static @NonNull SqlExpression<Integer> year(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the month from a date or timestamp column.<br>
	 * Generates SQL: {@code EXTRACT(MONTH FROM column)}.<br>
	 *
	 * @param column The date or timestamp column
	 * @return The month expression
	 */
	public static @NonNull SqlExpression<Integer> month(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the day from a date or timestamp column.<br>
	 * Generates SQL: {@code EXTRACT(DAY FROM column)}.<br>
	 *
	 * @param column The date or timestamp column
	 * @return The day expression
	 */
	public static @NonNull SqlExpression<Integer> day(@NonNull SqlColumn<?> column) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Truncates a timestamp column to the specified unit.<br>
	 * Generates SQL: {@code DATE_TRUNC('unit', column)}.<br>
	 *
	 * @param column The timestamp column to truncate
	 * @param unit The unit to truncate to
	 * @return The truncated timestamp expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> dateTrunc(@NonNull SqlColumn<?> column, @NonNull DatePart unit) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of days to a date or timestamp column.<br>
	 * Generates SQL: {@code column + INTERVAL 'n DAYS'}.<br>
	 *
	 * @param column The date or timestamp column
	 * @param days The number of days to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addDays(@NonNull SqlColumn<?> column, int days) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Calculates the difference between two date or timestamp columns in the specified unit.<br>
	 * Generates SQL: {@code DATEDIFF(part, col1, col2)}.<br>
	 *
	 * @param part The date part to calculate the difference in
	 * @param column1 The first date or timestamp column
	 * @param column2 The second date or timestamp column
	 * @return The date difference expression
	 */
	public static @NonNull SqlExpression<Long> dateDiff(@NonNull DatePart part, @NonNull SqlColumn<?> column1, @NonNull SqlColumn<?> column2) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a date or timestamp column to a formatted string.<br>
	 * Generates SQL: {@code TO_CHAR(column, 'format')}.<br>
	 *
	 * @param column The date or timestamp column
	 * @param format The format pattern
	 * @return The formatted string expression
	 */
	public static @NonNull SqlExpression<String> toChar(@NonNull SqlColumn<?> column, @NonNull String format) {
		throw new UnsupportedOperationException();
	}
}
