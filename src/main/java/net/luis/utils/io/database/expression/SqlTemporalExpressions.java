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
import net.luis.utils.io.database.util.SqlTemporalPart;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.temporal.Temporal;

/**
 *
 * @author Luis-St
 *
 */

public final class SqlTemporalExpressions {
	
	public static @NonNull SqlCondition withinLast(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return null;
	}
	
	public static @NonNull SqlCondition withinNext(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Duration duration) {
		return null;
	}
	
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) {
		return null;
	}
	
	public static @NonNull SqlCondition before(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return null;
	}
	
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull Temporal timestamp) {
		return null;
	}
	
	public static @NonNull SqlCondition after(@NonNull SqlExpression<? extends Temporal> expression, @NonNull SqlExpression<? extends Temporal> timestamp) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDateTime> now() {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDate> currentDate() {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalTime> currentTime() {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDateTime> currentTimestamp() {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDateTime> fromEpoch(long epoch) {
		return null;
	}
	
	public static <T extends Number> @NonNull SqlExpression<LocalDateTime> fromEpoch(@NonNull SqlExpression<T> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDate> makeDate(int year, int month, int day) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDate> makeDate(@NonNull SqlExpression<Integer> year, @NonNull SqlExpression<Integer> month, @NonNull SqlExpression<Integer> day) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalTime> makeTime(int hour, int minute, int second) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalTime> makeTime(@NonNull SqlExpression<Integer> hour, @NonNull SqlExpression<Integer> minute, @NonNull SqlExpression<Integer> second) {
		return null;
	}
	
	public static @NonNull SqlExpression<Integer> extract(@NonNull SqlExpression<?> expression, @NonNull SqlTemporalPart part) {
		return null;
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> truncate(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part) {
		return null;
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> add(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount) {
		return null;
	}
	
	public static <T extends Temporal> @NonNull SqlExpression<T> subtract(@NonNull SqlExpression<T> expression, @NonNull SqlTemporalPart part, int amount) {
		return add(expression, part, -amount);
	}
	
	public static @NonNull SqlExpression<Long> toEpoch(@NonNull SqlExpression<?> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalDate> toDate(@NonNull SqlExpression<?> expression) {
		return null;
	}
	
	public static @NonNull SqlExpression<LocalTime> toTime(@NonNull SqlExpression<?> expression) {
		return null;
	}
}
