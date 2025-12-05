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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

/**
 * A constraint interface for temporal types (dates, times, durations, etc.).<br>
 * Provides methods to validate temporal values against various time-based constraints.<br>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained (must extend {@link Temporal} and {@link Comparable})
 */
public interface TemporalConstraint<T extends Temporal & Comparable<? super T>> {
	
	/**
	 * Checks if the temporal value is after the specified minimum.<br>
	 *
	 * @param value The value to check
	 * @param minimum The minimum allowed temporal value (exclusive)
	 * @return {@code true} if the value is after the minimum, {@code false} otherwise
	 */
	default boolean isAfter(@Nullable T value, @NotNull T minimum) {
		if (value == null) return false;
		return value.compareTo(minimum) > 0;
	}
	
	/**
	 * Checks if the temporal value is after or equal to the specified minimum.<br>
	 *
	 * @param value The value to check
	 * @param minimum The minimum allowed temporal value (inclusive)
	 * @return {@code true} if the value is after or equal to the minimum, {@code false} otherwise
	 */
	default boolean isAfterOrEqual(@Nullable T value, @NotNull T minimum) {
		if (value == null) return false;
		return value.compareTo(minimum) >= 0;
	}
	
	/**
	 * Checks if the temporal value is before the specified maximum.<br>
	 *
	 * @param value The value to check
	 * @param maximum The maximum allowed temporal value (exclusive)
	 * @return {@code true} if the value is before the maximum, {@code false} otherwise
	 */
	default boolean isBefore(@Nullable T value, @NotNull T maximum) {
		if (value == null) return false;
		return value.compareTo(maximum) < 0;
	}
	
	/**
	 * Checks if the temporal value is before or equal to the specified maximum.<br>
	 *
	 * @param value The value to check
	 * @param maximum The maximum allowed temporal value (inclusive)
	 * @return {@code true} if the value is before or equal to the maximum, {@code false} otherwise
	 */
	default boolean isBeforeOrEqual(@Nullable T value, @NotNull T maximum) {
		if (value == null) return false;
		return value.compareTo(maximum) <= 0;
	}
	
	/**
	 * Checks if the temporal value is within the specified range (inclusive).<br>
	 *
	 * @param value The value to check
	 * @param minimum The minimum allowed temporal value (inclusive)
	 * @param maximum The maximum allowed temporal value (inclusive)
	 * @return {@code true} if the value is within the range, {@code false} otherwise
	 */
	default boolean isInRange(@Nullable T value, @NotNull T minimum, @NotNull T maximum) {
		if (value == null) return false;
		return value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
	}
	
	/**
	 * Checks if the temporal value is within the specified range (exclusive).<br>
	 *
	 * @param value The value to check
	 * @param minimum The minimum allowed temporal value (exclusive)
	 * @param maximum The maximum allowed temporal value (exclusive)
	 * @return {@code true} if the value is within the range, {@code false} otherwise
	 */
	default boolean isInRangeExclusive(@Nullable T value, @NotNull T minimum, @NotNull T maximum) {
		if (value == null) return false;
		return value.compareTo(minimum) > 0 && value.compareTo(maximum) < 0;
	}
	
	/**
	 * Checks if the temporal value is in the past (before the current time/date).<br>
	 * Implementation depends on the specific temporal type.<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is in the past, {@code false} otherwise
	 */
	boolean isPast(@Nullable T value);
	
	/**
	 * Checks if the temporal value is in the future (after the current time/date).<br>
	 * Implementation depends on the specific temporal type.<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value is in the future, {@code false} otherwise
	 */
	boolean isFuture(@Nullable T value);
	
	/**
	 * Checks if the temporal value represents the present (current time/date).<br>
	 * Implementation depends on the specific temporal type and acceptable tolerance.<br>
	 *
	 * @param value The value to check
	 * @return {@code true} if the value represents the present, {@code false} otherwise
	 */
	boolean isPresent(@Nullable T value);
	
	/**
	 * Default implementation for {@link LocalDate}.<br>
	 */
	interface ForLocalDate extends TemporalConstraint<LocalDate> {
		
		@Override
		default boolean isPast(@Nullable LocalDate value) {
			return value != null && value.isBefore(LocalDate.now());
		}
		
		@Override
		default boolean isFuture(@Nullable LocalDate value) {
			return value != null && value.isAfter(LocalDate.now());
		}
		
		@Override
		default boolean isPresent(@Nullable LocalDate value) {
			return value != null && value.isEqual(LocalDate.now());
		}
	}
	
	/**
	 * Default implementation for {@link LocalDateTime}.<br>
	 */
	interface ForLocalDateTime extends TemporalConstraint<LocalDateTime> {
		
		@Override
		default boolean isPast(@Nullable LocalDateTime value) {
			return value != null && value.isBefore(LocalDateTime.now());
		}
		
		@Override
		default boolean isFuture(@Nullable LocalDateTime value) {
			return value != null && value.isAfter(LocalDateTime.now());
		}
		
		@Override
		default boolean isPresent(@Nullable LocalDateTime value) {
			if (value == null) return false;
			LocalDateTime now = LocalDateTime.now();
			return Math.abs(ChronoUnit.SECONDS.between(value, now)) <= 1;
		}
	}
	
	/**
	 * Default implementation for {@link LocalTime}.<br>
	 */
	interface ForLocalTime extends TemporalConstraint<LocalTime> {
		
		@Override
		default boolean isPast(@Nullable LocalTime value) {
			return value != null && value.isBefore(LocalTime.now());
		}
		
		@Override
		default boolean isFuture(@Nullable LocalTime value) {
			return value != null && value.isAfter(LocalTime.now());
		}
		
		@Override
		default boolean isPresent(@Nullable LocalTime value) {
			if (value == null) return false;
			LocalTime now = LocalTime.now();
			return Math.abs(ChronoUnit.SECONDS.between(value, now)) <= 1;
		}
	}
	
	/**
	 * Default implementation for {@link Instant}.<br>
	 */
	interface ForInstant extends TemporalConstraint<Instant> {
		
		@Override
		default boolean isPast(@Nullable Instant value) {
			return value != null && value.isBefore(Instant.now());
		}
		
		@Override
		default boolean isFuture(@Nullable Instant value) {
			return value != null && value.isAfter(Instant.now());
		}
		
		@Override
		default boolean isPresent(@Nullable Instant value) {
			if (value == null) return false;
			Instant now = Instant.now();
			return Math.abs(ChronoUnit.SECONDS.between(value, now)) <= 1;
		}
	}
	
	/**
	 * Default implementation for {@link ZonedDateTime}.<br>
	 */
	interface ForZonedDateTime extends TemporalConstraint<ZonedDateTime> {
		
		@Override
		default boolean isPast(@Nullable ZonedDateTime value) {
			return value != null && value.isBefore(ZonedDateTime.now());
		}
		
		@Override
		default boolean isFuture(@Nullable ZonedDateTime value) {
			return value != null && value.isAfter(ZonedDateTime.now());
		}
		
		@Override
		default boolean isPresent(@Nullable ZonedDateTime value) {
			if (value == null) return false;
			ZonedDateTime now = ZonedDateTime.now();
			return Math.abs(ChronoUnit.SECONDS.between(value, now)) <= 1;
		}
	}
	
	/**
	 * Default implementation for {@link OffsetDateTime}.<br>
	 */
	interface ForOffsetDateTime extends TemporalConstraint<OffsetDateTime> {
		
		@Override
		default boolean isPast(@Nullable OffsetDateTime value) {
			return value != null && value.isBefore(OffsetDateTime.now());
		}
		
		@Override
		default boolean isFuture(@Nullable OffsetDateTime value) {
			return value != null && value.isAfter(OffsetDateTime.now());
		}
		
		@Override
		default boolean isPresent(@Nullable OffsetDateTime value) {
			if (value == null) return false;
			OffsetDateTime now = OffsetDateTime.now();
			return Math.abs(ChronoUnit.SECONDS.between(value, now)) <= 1;
		}
	}
	
	/**
	 * Constraint methods for {@link Duration}.<br>
	 */
	interface ForDuration {
		
		/**
		 * Checks if the duration is shorter than the specified maximum.<br>
		 *
		 * @param value The duration to check
		 * @param maximum The maximum allowed duration (exclusive)
		 * @return {@code true} if the duration is shorter than the maximum, {@code false} otherwise
		 */
		default boolean isShorterThan(@Nullable Duration value, @NotNull Duration maximum) {
			return value != null && value.compareTo(maximum) < 0;
		}
		
		/**
		 * Checks if the duration is longer than the specified minimum.<br>
		 *
		 * @param value The duration to check
		 * @param minimum The minimum allowed duration (exclusive)
		 * @return {@code true} if the duration is longer than the minimum, {@code false} otherwise
		 */
		default boolean isLongerThan(@Nullable Duration value, @NotNull Duration minimum) {
			return value != null && value.compareTo(minimum) > 0;
		}
		
		/**
		 * Checks if the duration is within the specified range (inclusive).<br>
		 *
		 * @param value The duration to check
		 * @param minimum The minimum allowed duration (inclusive)
		 * @param maximum The maximum allowed duration (inclusive)
		 * @return {@code true} if the duration is within the range, {@code false} otherwise
		 */
		default boolean isInRange(@Nullable Duration value, @NotNull Duration minimum, @NotNull Duration maximum) {
			return value != null && value.compareTo(minimum) >= 0 && value.compareTo(maximum) <= 0;
		}
		
		/**
		 * Checks if the duration is positive (greater than zero).<br>
		 *
		 * @param value The duration to check
		 * @return {@code true} if the duration is positive, {@code false} otherwise
		 */
		default boolean isPositive(@Nullable Duration value) {
			return value != null && !value.isNegative() && !value.isZero();
		}
		
		/**
		 * Checks if the duration is negative (less than zero).<br>
		 *
		 * @param value The duration to check
		 * @return {@code true} if the duration is negative, {@code false} otherwise
		 */
		default boolean isNegative(@Nullable Duration value) {
			return value != null && value.isNegative();
		}
		
		/**
		 * Checks if the duration is zero.<br>
		 *
		 * @param value The duration to check
		 * @return {@code true} if the duration is zero, {@code false} otherwise
		 */
		default boolean isZero(@Nullable Duration value) {
			return value != null && value.isZero();
		}
	}
	
	/**
	 * Constraint methods for {@link Period}.<br>
	 */
	interface ForPeriod {
		
		/**
		 * Checks if the period is positive (has at least one positive unit).<br>
		 *
		 * @param value The period to check
		 * @return {@code true} if the period is positive, {@code false} otherwise
		 */
		default boolean isPositive(@Nullable Period value) {
			return value != null && !value.isNegative() && !value.isZero();
		}
		
		/**
		 * Checks if the period is negative (all units are non-positive with at least one negative).<br>
		 *
		 * @param value The period to check
		 * @return {@code true} if the period is negative, {@code false} otherwise
		 */
		default boolean isNegative(@Nullable Period value) {
			return value != null && value.isNegative();
		}
		
		/**
		 * Checks if the period is zero.<br>
		 *
		 * @param value The period to check
		 * @return {@code true} if the period is zero, {@code false} otherwise
		 */
		default boolean isZero(@Nullable Period value) {
			return value != null && value.isZero();
		}
		
		/**
		 * Checks if the period's total months is within the specified range.<br>
		 *
		 * @param value The period to check
		 * @param minMonths The minimum number of months (inclusive)
		 * @param maxMonths The maximum number of months (inclusive)
		 * @return {@code true} if the period's total months is within the range, {@code false} otherwise
		 */
		default boolean hasTotalMonthsInRange(@Nullable Period value, int minMonths, int maxMonths) {
			if (value == null) return false;
			long totalMonths = value.toTotalMonths();
			return totalMonths >= minMonths && totalMonths <= maxMonths;
		}
	}
	
}
