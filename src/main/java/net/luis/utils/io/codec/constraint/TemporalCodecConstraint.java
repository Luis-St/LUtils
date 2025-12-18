/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Objects;
import java.util.Set;

/**
 * A constraint interface for temporal types (dates, times, durations, etc.).<br>
 * Provides methods to validate temporal values against various time-based constraints.<br>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 */
public interface TemporalCodecConstraint<T extends Temporal & Comparable<T>, C extends Codec<T>> extends CodecConstraint<T, C> {
	
	default @NotNull C after(@NotNull T minimum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(minimum) <= 0) {
				return Result.error("Value " + value + " is not after minimum " + minimum);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C afterOrEqual(@NotNull T minimum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(minimum) < 0) {
				return Result.error("Value " + value + " is not after or equal to minimum " + minimum);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C before(@NotNull T maximum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(maximum) >= 0) {
				return Result.error("Value " + value + " is not before maximum " + maximum);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C beforeOrEqual(@NotNull T maximum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(maximum) > 0) {
				return Result.error("Value " + value + " is not before or equal to maximum " + maximum);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C betweenInclusive(@NotNull T minimum, @NotNull T maximum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(minimum) < 0 || value.compareTo(maximum) > 0) {
				return Result.error("Value " + value + " is not between " + minimum + " and " + maximum + " (inclusive)");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C betweenExclusive(@NotNull T minimum, @NotNull T maximum) {
		return this.applyConstraint(value -> {
			if (value.compareTo(minimum) <= 0 || value.compareTo(maximum) >= 0) {
				return Result.error("Value " + value + " is not between " + minimum + " and " + maximum + " (exclusive)");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C equalTo(@NotNull T target) {
		return this.applyConstraint(value -> {
			if (value.compareTo(target) != 0) {
				return Result.error("Value " + value + " is not equal to " + target);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C notEqualTo(@NotNull T target) {
		return this.applyConstraint(value -> {
			if (value.compareTo(target) == 0) {
				return Result.error("Value " + value + " is equal to " + target);
			}
			return Result.success(true);
		});
	}
	
	@NotNull C past();
	
	@NotNull C pastOrPresent();
	
	@NotNull C future();
	
	@NotNull C futureOrPresent();
	
	@NotNull C withinLast(@NotNull Duration duration);
	
	@NotNull C withinNext(@NotNull Duration duration);
	
	@NotNull C componentEquals(@NotNull ChronoField field, int expectedValue);
	
	@NotNull C componentNotEquals(@NotNull ChronoField field, int unexpectedValue);
	
	@NotNull C componentIn(@NotNull ChronoField field, @NotNull Set<Integer> allowedValues);
	
	@NotNull C componentNotIn(@NotNull ChronoField field, @NotNull Set<Integer> disallowedValues);
	
	@NotNull C millisecond(int millisecond);
	
	@NotNull C millisecondIn(@NotNull Set<Integer> milliseconds);
	
	@NotNull C second(int second);
	
	@NotNull C secondIn(@NotNull Set<Integer> seconds);
	
	@NotNull C minute(int minute);
	
	@NotNull C minuteIn(@NotNull Set<Integer> minutes);
	
	@NotNull C hour(int hour);
	
	@NotNull C hourIn(@NotNull Set<Integer> hours);
	
	default @NotNull C day(@NotNull DayOfWeek day) {
		Objects.requireNonNull(day, "Day of week must not be null");
		
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.DAY_OF_WEEK)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support day of week extraction");
			}
			
			DayOfWeek valueDay = DayOfWeek.from(value);
			if (valueDay != day) {
				return Result.error("Day " + valueDay + " of value " + value + " is not equal to required day " + day);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C dayIn(@NotNull Set<DayOfWeek> days) {
		Objects.requireNonNull(days, "Set of days must not be null");
		
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.DAY_OF_WEEK)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support day of week extraction");
			}
			
			DayOfWeek valueDay = DayOfWeek.from(value);
			if (!days.contains(valueDay)) {
				return Result.error("Day " + valueDay + " of value " + value + " is not in allowed days " + days);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C weekday() {
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.DAY_OF_WEEK)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support day of week extraction");
			}
			
			DayOfWeek valueDay = DayOfWeek.from(value);
			if (valueDay == DayOfWeek.SATURDAY || valueDay == DayOfWeek.SUNDAY) {
				return Result.error("Day " + valueDay + " of value " + value + " is not a weekday");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C weekend() {
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.DAY_OF_WEEK)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support day of week extraction");
			}
			
			DayOfWeek valueDay = DayOfWeek.from(value);
			if (valueDay != DayOfWeek.SATURDAY && valueDay != DayOfWeek.SUNDAY) {
				return Result.error("Day " + valueDay + " of value " + value + " is not a weekend day");
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C month(@NotNull Month month) {
		Objects.requireNonNull(month, "Month must not be null");
		
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.MONTH_OF_YEAR)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support month extraction");
			}
			
			Month valueMonth = Month.from(value);
			if (valueMonth != month) {
				return Result.error("Month " + valueMonth + " of value " + value + " is not equal to required month " + month);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C monthIn(@NotNull Set<Month> months) {
		Objects.requireNonNull(months, "Set of months must not be null");
		
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.MONTH_OF_YEAR)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support month extraction");
			}
			
			Month valueMonth = Month.from(value);
			if (!months.contains(valueMonth)) {
				return Result.error("Month " + valueMonth + " of value " + value + " is not in allowed months " + months);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C year(int year) {
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.YEAR)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support year extraction");
			}
			
			int valueYear = value.get(ChronoField.YEAR);
			if (valueYear != year) {
				return Result.error("Year " + valueYear + " of value " + value + " is not equal to required year " + year);
			}
			return Result.success(true);
		});
	}
	
	default @NotNull C yearIn(@NotNull Set<Integer> years) {
		Objects.requireNonNull(years, "Set of years must not be null");
		
		return this.applyConstraint(value -> {
			if (!value.isSupported(ChronoField.YEAR)) {
				return Result.error("Value " + value + "(" + value.getClass().getSimpleName() + ") does not support year extraction");
			}
			
			int valueYear = value.get(ChronoField.YEAR);
			if (!years.contains(valueYear)) {
				return Result.error("Year " + valueYear + " of value " + value + " is not in allowed years " + years);
			}
			return Result.success(true);
		});
	}
}
