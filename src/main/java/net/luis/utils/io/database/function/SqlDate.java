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

import java.time.*;

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
	 * Extracts the year from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(YEAR FROM expression)}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The year expression
	 */
	public static @NonNull SqlExpression<Integer> year(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Extracts the month from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(MONTH FROM expression)}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The month expression
	 */
	public static @NonNull SqlExpression<Integer> month(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Extracts the day from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(DAY FROM expression)}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The day expression
	 */
	public static @NonNull SqlExpression<Integer> day(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Truncates a timestamp expression to the specified unit.<br>
	 * Generates SQL: {@code DATE_TRUNC('unit', expression)}.<br>
	 *
	 * @param expr The timestamp expression to truncate
	 * @param unit The unit to truncate to
	 * @return The truncated timestamp expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> dateTrunc(@NonNull SqlExpression<?> expr, @NonNull DatePart unit) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adds the specified number of days to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n DAYS'}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param days The number of days to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addDays(@NonNull SqlExpression<?> expr, int days) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Calculates the difference between two date or timestamp expressions in the specified unit.<br>
	 * Generates SQL: {@code DATEDIFF(part, expr1, expr2)}.<br>
	 *
	 * @param part The date part to calculate the difference in
	 * @param expr1 The first date or timestamp expression
	 * @param expr2 The second date or timestamp expression
	 * @return The date difference expression
	 */
	public static @NonNull SqlExpression<Long> dateDiff(@NonNull DatePart part, @NonNull SqlExpression<?> expr1, @NonNull SqlExpression<?> expr2) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a date or timestamp expression to a formatted string.<br>
	 * Generates SQL: {@code TO_CHAR(expression, 'format')}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param format The format pattern
	 * @return The formatted string expression
	 */
	public static @NonNull SqlExpression<String> toChar(@NonNull SqlExpression<?> expr, @NonNull String format) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the current time.<br>
	 * Generates SQL: {@code CURRENT_TIME}.<br>
	 *
	 * @return The current time expression
	 */
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the current timestamp.<br>
	 * Generates SQL: {@code CURRENT_TIMESTAMP}.<br>
	 *
	 * @return The current timestamp expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> currentTimestamp() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the hour from a time or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(HOUR FROM expression)}.<br>
	 *
	 * @param expr The time or timestamp expression
	 * @return The hour expression
	 */
	public static @NonNull SqlExpression<Integer> hour(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the minute from a time or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(MINUTE FROM expression)}.<br>
	 *
	 * @param expr The time or timestamp expression
	 * @return The minute expression
	 */
	public static @NonNull SqlExpression<Integer> minute(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the second from a time or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(SECOND FROM expression)}.<br>
	 *
	 * @param expr The time or timestamp expression
	 * @return The second expression
	 */
	public static @NonNull SqlExpression<Integer> second(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the day of the week from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(DOW FROM expression)} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The day of week expression
	 */
	public static @NonNull SqlExpression<Integer> dayOfWeek(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the day of the year from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(DOY FROM expression)} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The day of year expression
	 */
	public static @NonNull SqlExpression<Integer> dayOfYear(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the ISO week number from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(WEEK FROM expression)} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The week expression
	 */
	public static @NonNull SqlExpression<Integer> week(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the quarter from a date or timestamp expression.<br>
	 * Generates SQL: {@code EXTRACT(QUARTER FROM expression)}.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The quarter expression
	 */
	public static @NonNull SqlExpression<Integer> quarter(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of years to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n YEARS'} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param years The number of years to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addYears(@NonNull SqlExpression<?> expr, int years) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of months to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n MONTHS'} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param months The number of months to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addMonths(@NonNull SqlExpression<?> expr, int months) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of hours to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n HOURS'} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param hours The number of hours to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addHours(@NonNull SqlExpression<?> expr, int hours) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of minutes to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n MINUTES'} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param minutes The number of minutes to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addMinutes(@NonNull SqlExpression<?> expr, int minutes) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the specified number of seconds to a date or timestamp expression.<br>
	 * Generates SQL: {@code expression + INTERVAL 'n SECONDS'} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param seconds The number of seconds to add
	 * @return The adjusted date expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> addSeconds(@NonNull SqlExpression<?> expr, int seconds) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Formats a date or timestamp expression using the given pattern.<br>
	 * Generates SQL: {@code TO_CHAR(expression, 'pattern')} or {@code DATE_FORMAT(expression, 'pattern')} depending on the dialect.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @param pattern The format pattern
	 * @return The formatted string expression
	 */
	public static @NonNull SqlExpression<String> formatDate(@NonNull SqlExpression<?> expr, @NonNull String pattern) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a date or timestamp expression to a Unix epoch value in seconds.<br>
	 * Generates SQL: {@code EXTRACT(EPOCH FROM expression)} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The epoch value expression
	 */
	public static @NonNull SqlExpression<Long> toEpoch(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Converts a Unix epoch value in seconds to a timestamp.<br>
	 * Generates SQL: {@code TO_TIMESTAMP(value)} or dialect equivalent.<br>
	 *
	 * @param value The epoch value expression
	 * @return The timestamp expression
	 */
	public static @NonNull SqlExpression<LocalDateTime> fromEpoch(@NonNull SqlExpression<? extends Number> value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs a date from the given year, month, and day components.<br>
	 * Generates SQL: {@code MAKE_DATE(year, month, day)} or dialect equivalent.<br>
	 *
	 * @param year The year
	 * @param month The month
	 * @param day The day
	 * @return The constructed date expression
	 */
	public static @NonNull SqlExpression<LocalDate> makeDate(int year, int month, int day) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Constructs a time from the given hour, minute, and second components.<br>
	 * Generates SQL: {@code MAKE_TIME(hour, minute, second)} or dialect equivalent.<br>
	 *
	 * @param hour The hour
	 * @param minute The minute
	 * @param second The second
	 * @return The constructed time expression
	 */
	public static @NonNull SqlExpression<LocalTime> makeTime(int hour, int minute, int second) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the date portion from a timestamp expression, discarding the time component.<br>
	 * Generates SQL: {@code CAST(expression AS DATE)} or {@code DATE(expression)} depending on the dialect.<br>
	 *
	 * @param expr The timestamp expression
	 * @return The date expression
	 */
	public static @NonNull SqlExpression<LocalDate> toDate(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Extracts the time portion from a timestamp expression, discarding the date component.<br>
	 * Generates SQL: {@code CAST(expression AS TIME)} or {@code TIME(expression)} depending on the dialect.<br>
	 *
	 * @param expr The timestamp expression
	 * @return The time expression
	 */
	public static @NonNull SqlExpression<LocalTime> toTime(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Returns the last day of the month for the given date or timestamp expression.<br>
	 * Generates SQL: {@code LAST_DAY(expression)} or dialect equivalent.<br>
	 *
	 * @param expr The date or timestamp expression
	 * @return The last day expression
	 */
	public static @NonNull SqlExpression<LocalDate> lastDay(@NonNull SqlExpression<?> expr) {
		throw new UnsupportedOperationException();
	}
}
