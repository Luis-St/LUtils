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
	
	public static @NonNull SqlExpression<LocalDateTime> now() {
		return (SqlExpression<LocalDateTime>) (SqlExpression<?>) new SqlNowFunction();
	}
	
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		return new SqlCurrentDateFunction();
	}
	
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		return new SqlCurrentTimeFunction();
	}
	
	public static @NonNull SqlExpression<LocalDateTime> currentTimestamp() {
		return (SqlExpression<LocalDateTime>) (SqlExpression<?>) new SqlCurrentTimestampFunction();
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> fromEpoch(long epoch, @NonNull SqlType<T> temporalType) {
		return new SqlFromEpochFunction<>(SqlExpression.of(epoch), temporalType);
	}
	
	public static <T extends Number, V extends Temporal> @NonNull SqlExpression<V> fromEpoch(@NonNull SqlExpression<T> expression, @NonNull SqlType<V> temporalType) {
		return new SqlFromEpochFunction<>(expression, temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(int year, int month, int day, @NonNull SqlType<T> temporalType) {
		return new SqlMakeDateFunction<>(SqlExpression.of(year), SqlExpression.of(month), SqlExpression.of(day), temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeDate(@NonNull SqlExpression<Integer> year, @NonNull SqlExpression<Integer> month, @NonNull SqlExpression<Integer> day, @NonNull SqlType<T> temporalType) {
		return new SqlMakeDateFunction<>(year, month, day, temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(int hour, int minute, int second, @NonNull SqlType<T> temporalType) {
		return new SqlMakeTimeFunction<>(SqlExpression.of(hour), SqlExpression.of(minute), SqlExpression.of(second), temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> makeTime(@NonNull SqlExpression<Integer> hour, @NonNull SqlExpression<Integer> minute, @NonNull SqlExpression<Integer> second, @NonNull SqlType<T> temporalType) {
		return new SqlMakeTimeFunction<>(hour, minute, second, temporalType);
	}
	
	public static @NonNull SqlExpression<Integer> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part) {
		return new SqlExtractFunction(expression, part);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, @NonNull SqlType<T> temporalType) {
		return new SqlTemporalTruncateFunction<>(expression, part, temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> temporalType) {
		return new SqlAddFunction<>(expression, SqlExpression.of(amount), temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount, @NonNull SqlType<T> temporalType) {
		return add(expression, part, -amount, temporalType);
	}
	
	public static @NonNull SqlExpression<Long> toEpoch(@NonNull SqlExpression<?> expression) {
		return new SqlToEpochFunction(expression);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toDate(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> temporalType) {
		return new SqlToDateFunction<>(expression, temporalType);
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> toTime(@NonNull SqlExpression<?> expression, @NonNull SqlType<T> temporalType) {
		return new SqlToTimeFunction<>(expression, temporalType);
	}
}
