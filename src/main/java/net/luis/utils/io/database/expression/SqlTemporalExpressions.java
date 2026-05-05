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
import net.luis.utils.io.database.condition.conditions.temporal.*;
import net.luis.utils.io.database.function.functions.temporal.*;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.temporal.Temporal;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("unchecked")
public final class SqlTemporalExpressions {
	
	public static @NonNull SqlCondition withinLast(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return new SqlWithinLastCondition(expression, SqlExpression.of(duration));
	}
	
	public static @NonNull SqlCondition withinNext(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return new SqlWithinNextCondition(expression, SqlExpression.of(duration));
	}
	
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) {
		return new SqlBeforeCondition(expression, SqlExpression.of(timestamp));
	}
	
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlBeforeCondition(expression, timestamp);
	}
	
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) {
		return new SqlAfterCondition(expression, SqlExpression.of(timestamp));
	}
	
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return new SqlAfterCondition(expression, timestamp);
	}
	
	public static @NonNull SqlExpression<Instant> now() {
		return new SqlNowFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> now(@NonNull SqlType<T> type) {
		return new SqlNowFunction<>(type);
	}
	
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		return new SqlCurrentDateFunction<>(SqlTypes.LOCAL_DATE);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentDate(@NonNull SqlType<T> type) {
		return new SqlCurrentDateFunction<>(type);
	}
	
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		return new SqlCurrentTimeFunction<>(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTime(@NonNull SqlType<T> type) {
		return new SqlCurrentTimeFunction<>(type);
	}
	
	public static @NonNull SqlExpression<Instant> currentTimestamp() {
		return new SqlCurrentTimestampFunction<>(SqlTypes.INSTANT.configure(SqlParameter.fractional(0)));
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> currentTimestamp(@NonNull SqlType<T> type) {
		return new SqlCurrentTimestampFunction<>(type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> fromEpoch(long epoch, @NonNull SqlType<T> type) {
		return new SqlFromEpochFunction<>(SqlExpression.of(epoch), type);
	}
	
	public static <T extends Number, V extends Temporal> @NonNull SqlExpression<V> fromEpoch(@NonNull SqlExpression<T> expression, @NonNull SqlType<V> type) {
		return new SqlFromEpochFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(int year, int month, int day, @NonNull SqlType<T> type) {
		return new SqlMakeDateFunction<>(SqlExpression.of(year), SqlExpression.of(month), SqlExpression.of(day), type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(@NonNull SqlExpression<Integer> year, @NonNull SqlExpression<Integer> month, @NonNull SqlExpression<Integer> day, @NonNull SqlType<T> type) {
		return new SqlMakeDateFunction<>(year, month, day, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(int hour, int minute, int second, @NonNull SqlType<T> type) {
		return new SqlMakeTimeFunction<>(SqlExpression.of(hour), SqlExpression.of(minute), SqlExpression.of(second), type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(@NonNull SqlExpression<Integer> hour, @NonNull SqlExpression<Integer> minute, @NonNull SqlExpression<Integer> second, @NonNull SqlType<T> type) {
		return new SqlMakeTimeFunction<>(hour, minute, second, type);
	}
	
	public static @NonNull SqlExpression<Integer> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part) {
		return new SqlExtractFunction<>(expression, part, SqlTypes.INTEGER);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlExtractFunction<>(expression, part, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> type) {
		return new SqlTemporalTruncateFunction<>(expression, part, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> type) {
		return new SqlAddFunction<>(expression, SqlExpression.of(amount), type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> type) {
		return add(expression, part, -amount, type);
	}
	
	public static @NonNull SqlExpression<Long> toEpoch(@NonNull SqlExpression<?> expression) {
		return new SqlToEpochFunction<>(expression, SqlTypes.LONG);
	}
	
	public static <T extends Number> @NonNull SqlExpression<T> toEpoch(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToEpochFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toDate(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToDateFunction<>(expression, type);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toTime(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> type) {
		return new SqlToTimeFunction<>(expression, type);
	}
}
