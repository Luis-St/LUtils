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

package net.luis.utils.io.codec.constraint_new.config.matcher;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility class providing static methods for Duration and Period-specific constraint validation patterns.<br>
 * <p>
 *     This class contains domain-specific matchers that don't fit in the generic {@link ConstraintMatchers}
 *     class due to their specialized semantics for Duration and Period types.
 * </p>
 *
 * @author Luis-St
 */
public final class TemporalMatchers {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private TemporalMatchers() {}
	
	/**
	 * Validates sign constraints (positive/negative/zero) for a Duration value.<br>
	 * <p>
	 *     The boolean flags use inverted semantics:<br>
	 *     - positive: false=must be positive, true=must be non-positive<br>
	 *     - negative: false=must be negative, true=must be non-negative<br>
	 *     - zero: false=must be zero, true=must be non-zero
	 * </p>
	 *
	 * @param value The Duration value to validate
	 * @param positive The positive constraint flag
	 * @param negative The negative constraint flag
	 * @param zero The zero constraint flag
	 * @return A successful result if all present constraints are satisfied
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchDurationSign(
		@NonNull Duration value, @NonNull Optional<Boolean> positive, @NonNull Optional<Boolean> negative, @NonNull Optional<Boolean> zero
	) {
		Objects.requireNonNull(value, "Duration must not be null");
		Objects.requireNonNull(positive, "Positive constraint must not be null");
		Objects.requireNonNull(negative, "Negative constraint must not be null");
		Objects.requireNonNull(zero, "Zero constraint must not be null");
		
		if (positive.isPresent()) {
			boolean nonPositive = positive.get();
			if (nonPositive) {
				if (!value.isNegative() && !value.isZero()) {
					return Result.error("Duration '" + value + "' must be non-positive");
				}
			} else {
				if (value.isNegative() || value.isZero()) {
					return Result.error("Duration '" + value + "' must be positive");
				}
			}
		}
		
		if (negative.isPresent()) {
			boolean nonNegative = negative.get();
			if (nonNegative) {
				if (value.isNegative()) {
					return Result.error("Duration '" + value + "' must be non-negative");
				}
			} else {
				if (!value.isNegative()) {
					return Result.error("Duration '" + value + "' must be negative");
				}
			}
		}
		
		if (zero.isPresent()) {
			boolean nonZero = zero.get();
			if (nonZero) {
				if (value.isZero()) {
					return Result.error("Duration '" + value + "' must be non-zero");
				}
			} else {
				if (!value.isZero()) {
					return Result.error("Duration '" + value + "' must be zero");
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that a Duration value is within the last specified duration.<br>
	 * <p>
	 *     Unlike temporal types that compare against "now", Duration compares directly
	 *     against the negated threshold. The value must be greater than or equal to
	 *     the negated withinLast duration.
	 * </p>
	 *
	 * @param value The Duration value to validate
	 * @param withinLast The optional duration threshold
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchDurationWithinLast(@NonNull Duration value, @NonNull Optional<Duration> withinLast) {
		Objects.requireNonNull(value, "Duration must not be null");
		Objects.requireNonNull(withinLast, "Within last constraint must not be null");
		if (withinLast.isEmpty()) {
			return Result.success();
		}
		
		Duration threshold = withinLast.get().negated();
		if (value.compareTo(threshold) < 0) {
			return Result.error("Duration '" + value + "' must be within last " + withinLast.get());
		}
		return Result.success();
	}
	
	/**
	 * Validates that a Duration value is within the next specified duration.<br>
	 * <p>
	 *     Unlike temporal types that compare against "now", Duration compares directly
	 *     against the threshold. The value must be less than or equal to the withinNext duration.
	 * </p>
	 *
	 * @param value The Duration value to validate
	 * @param withinNext The optional duration threshold
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchDurationWithinNext(@NonNull Duration value, @NonNull Optional<Duration> withinNext) {
		Objects.requireNonNull(value, "Duration must not be null");
		Objects.requireNonNull(withinNext, "Within next constraint must not be null");
		if (withinNext.isEmpty()) {
			return Result.success();
		}
		
		if (value.compareTo(withinNext.get()) > 0) {
			return Result.error("Duration '" + value + "' must be within next " + withinNext.get());
		}
		return Result.success();
	}
	
	/**
	 * Validates sign constraints (positive/negative/zero) for a Period value.<br>
	 * <p>
	 *     The boolean flags use inverted semantics:<br>
	 *     - positive: false=must be positive, true=must be non-positive<br>
	 *     - negative: false=must be negative, true=must be non-negative<br>
	 *     - zero: false=must be zero, true=must be non-zero
	 * </p>
	 *
	 * @param value The Period value to validate
	 * @param positive The positive constraint flag
	 * @param negative The negative constraint flag
	 * @param zero The zero constraint flag
	 * @return A successful result if all present constraints are satisfied
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPeriodSign(
		@NonNull Period value, @NonNull Optional<Boolean> positive, @NonNull Optional<Boolean> negative, @NonNull Optional<Boolean> zero
	) {
		Objects.requireNonNull(value, "Period must not be null");
		Objects.requireNonNull(positive, "Positive constraint must not be null");
		Objects.requireNonNull(negative, "Negative constraint must not be null");
		Objects.requireNonNull(zero, "Zero constraint must not be null");
		
		if (positive.isPresent()) {
			boolean nonPositive = positive.get();
			boolean isPositive = !value.isNegative() && !value.isZero();
			if (nonPositive) {
				if (isPositive) {
					return Result.error("Period '" + value + "' must be non-positive");
				}
			} else {
				if (!isPositive) {
					return Result.error("Period '" + value + "' must be positive");
				}
			}
		}
		
		if (negative.isPresent()) {
			boolean nonNegative = negative.get();
			boolean isNegative = value.isNegative();
			if (nonNegative) {
				if (isNegative) {
					return Result.error("Period '" + value + "' must be non-negative");
				}
			} else {
				if (!isNegative) {
					return Result.error("Period '" + value + "' must be negative");
				}
			}
		}
		
		if (zero.isPresent()) {
			boolean nonZero = zero.get();
			boolean isZero = value.isZero();
			if (nonZero) {
				if (isZero) {
					return Result.error("Period '" + value + "' must be non-zero");
				}
			} else {
				if (!isZero) {
					return Result.error("Period '" + value + "' must be zero");
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that a Period value is within the specified range.<br>
	 * <p>
	 *     Period comparison uses total months first, then days as a tiebreaker.
	 *     This provides a consistent ordering for Period values.
	 * </p>
	 *
	 * @param value The Period value to validate
	 * @param min The optional minimum bound as a pair of (value, inclusive)
	 * @param max The optional maximum bound as a pair of (value, inclusive)
	 * @return A successful result if the value is within the specified range
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull Result<Void> matchPeriodRange(
		@NonNull Period value, @NonNull Optional<Pair<Period, Boolean>> min, @NonNull Optional<Pair<Period, Boolean>> max
	) {
		Objects.requireNonNull(value, "Period must not be null");
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		
		long totalMonths = value.toTotalMonths();
		long totalDays = value.getDays();
		
		if (min.isPresent()) {
			Period minPeriod = min.get().getFirst();
			boolean inclusive = min.get().getSecond();
			long minTotalMonths = minPeriod.toTotalMonths();
			long minTotalDays = minPeriod.getDays();
			
			int cmp = Long.compare(totalMonths, minTotalMonths);
			if (cmp == 0) {
				cmp = Long.compare(totalDays, minTotalDays);
			}
			
			if (inclusive) {
				if (cmp < 0) {
					return Result.error("Period '" + value + "' must be greater than or equal to " + minPeriod);
				}
			} else {
				if (cmp <= 0) {
					return Result.error("Period '" + value + "' must be greater than " + minPeriod);
				}
			}
		}
		
		if (max.isPresent()) {
			Period maxPeriod = max.get().getFirst();
			boolean inclusive = max.get().getSecond();
			long maxTotalMonths = maxPeriod.toTotalMonths();
			long maxTotalDays = maxPeriod.getDays();
			
			int cmp = Long.compare(totalMonths, maxTotalMonths);
			if (cmp == 0) {
				cmp = Long.compare(totalDays, maxTotalDays);
			}
			
			if (inclusive) {
				if (cmp > 0) {
					return Result.error("Period '" + value + "' must be less than or equal to " + maxPeriod);
				}
			} else {
				if (cmp >= 0) {
					return Result.error("Period '" + value + "' must be less than " + maxPeriod);
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates sign constraints (positive/negative/zero) for a ZoneOffset value.<br>
	 * <p>
	 *     The boolean flags use inverted semantics:<br>
	 *     - positive: false=must be positive, true=must be non-positive<br>
	 *     - negative: false=must be negative, true=must be non-negative<br>
	 *     - zero: false=must be zero (UTC), true=must be non-zero
	 * </p>
	 *
	 * @param value The ZoneOffset value to validate
	 * @param positive The positive constraint flag
	 * @param negative The negative constraint flag
	 * @param zero The zero constraint flag
	 * @return A successful result if all present constraints are satisfied
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchZoneOffsetSign(
		@NonNull ZoneOffset value, @NonNull Optional<Boolean> positive, @NonNull Optional<Boolean> negative, @NonNull Optional<Boolean> zero
	) {
		Objects.requireNonNull(value, "Zone offset must not be null");
		Objects.requireNonNull(positive, "Positive constraint must not be null");
		Objects.requireNonNull(negative, "Negative constraint must not be null");
		Objects.requireNonNull(zero, "Zero constraint must not be null");
		
		int totalSeconds = value.getTotalSeconds();
		
		if (positive.isPresent()) {
			boolean nonPositive = positive.get();
			if (nonPositive) {
				if (totalSeconds > 0) {
					return Result.error("Zone offset '" + value + "' must be non-positive");
				}
			} else {
				if (totalSeconds <= 0) {
					return Result.error("Zone offset '" + value + "' must be positive");
				}
			}
		}
		
		if (negative.isPresent()) {
			boolean nonNegative = negative.get();
			if (nonNegative) {
				if (totalSeconds < 0) {
					return Result.error("Zone offset '" + value + "' must be non-negative");
				}
			} else {
				if (totalSeconds >= 0) {
					return Result.error("Zone offset '" + value + "' must be negative");
				}
			}
		}
		
		if (zero.isPresent()) {
			boolean nonZero = zero.get();
			if (nonZero) {
				if (totalSeconds == 0) {
					return Result.error("Zone offset '" + value + "' must be non-zero");
				}
			} else {
				if (totalSeconds != 0) {
					return Result.error("Zone offset '" + value + "' must be zero (UTC)");
				}
			}
		}
		return Result.success();
	}
}
