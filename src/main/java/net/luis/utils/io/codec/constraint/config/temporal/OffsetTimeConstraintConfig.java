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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import net.luis.utils.io.codec.constraint.core.provider.TimeFieldConstraintConfigProvider;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for OffsetTime constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link OffsetTime} values.
 * </p>
 *
 * @author Luis-St
 */
public record OffsetTimeConstraintConfig(
	@NonNull Optional<Pair<OffsetTime, Boolean>> min,
	@NonNull Optional<Pair<OffsetTime, Boolean>> max,
	@NonNull Optional<Pair<OffsetTime, Boolean>> equals,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<FieldConstraintConfig> hour,
	@NonNull Optional<FieldConstraintConfig> minute,
	@NonNull Optional<FieldConstraintConfig> second,
	@NonNull Optional<FieldConstraintConfig> millisecond
) implements TemporalConstraintConfigProvider<OffsetTime, OffsetTimeConstraintConfig>, TimeFieldConstraintConfigProvider<OffsetTimeConstraintConfig> {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link OffsetTime} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final OffsetTimeConstraintConfig UNCONSTRAINED = new OffsetTimeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Constructs a new OffsetTime constraint configuration with the specified constraints.<br>
	 *
	 * @param min The minimum value constraint (empty if unconstrained)
	 * @param max The maximum value constraint (empty if unconstrained)
	 * @param equals The exact value constraint (empty if unconstrained)
	 * @param withinLast The "within last" duration constraint (empty if unconstrained)
	 * @param withinNext The "within next" duration constraint (empty if unconstrained)
	 * @param hour The hour constraint (empty if unconstrained)
	 * @param minute The minute constraint (empty if unconstrained)
	 * @param second The second constraint (empty if unconstrained)
	 * @param millisecond The millisecond constraint (empty if unconstrained)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If constraints are invalid
	 */
	public OffsetTimeConstraintConfig {
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		Objects.requireNonNull(equals, "Equals constraint must not be null");
		Objects.requireNonNull(withinLast, "Within last constraint must not be null");
		Objects.requireNonNull(withinNext, "Within next constraint must not be null");
		Objects.requireNonNull(hour, "Hour constraint must not be null");
		Objects.requireNonNull(minute, "Minute constraint must not be null");
		Objects.requireNonNull(second, "Second constraint must not be null");
		Objects.requireNonNull(millisecond, "Millisecond constraint must not be null");

		if (min.isPresent() && max.isPresent()) {
			Pair<OffsetTime, Boolean> minPair = min.get();
			Pair<OffsetTime, Boolean> maxPair = max.get();
			int comparison = minPair.getFirst().compareTo(maxPair.getFirst());
			if (comparison > 0 || (comparison == 0 && (!minPair.getSecond() || !maxPair.getSecond()))) {
				throw new IllegalArgumentException("Minimum value must not be greater than maximum value: min=" + minPair + ", max=" + maxPair);
			}
		}
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.min.isEmpty() && this.max.isEmpty() && this.equals.isEmpty() && this.withinLast.isEmpty() && this.withinNext.isEmpty() && (this.hour.isEmpty() || this.hour.get().isUnconstrained()) && (this.minute.isEmpty() || this.minute.get().isUnconstrained()) && (this.second.isEmpty() || this.second.get().isUnconstrained()) && (this.millisecond.isEmpty() || this.millisecond.get().isUnconstrained()));
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withEquals(@NonNull OffsetTime value, boolean negated) {
		return new OffsetTimeConstraintConfig(this.min, this.max, Optional.of(Pair.of(value, negated)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withMin(@NonNull OffsetTime min, boolean inclusive) {
		return new OffsetTimeConstraintConfig(Optional.of(Pair.of(min, inclusive)), this.max, this.equals, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withMax(@NonNull OffsetTime max, boolean inclusive) {
		return new OffsetTimeConstraintConfig(this.min, Optional.of(Pair.of(max, inclusive)), this.equals, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withRange(@NonNull OffsetTime min, @NonNull OffsetTime max, boolean inclusive) {
		return new OffsetTimeConstraintConfig(Optional.of(Pair.of(min, inclusive)), Optional.of(Pair.of(max, inclusive)), this.equals, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, Optional.of(duration), this.withinNext, this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, this.withinLast, Optional.of(duration), this.hour, this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withHour(@NonNull FieldConstraintConfig hourConfig) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, this.withinLast, this.withinNext, Optional.of(hourConfig), this.minute, this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withMinute(@NonNull FieldConstraintConfig minuteConfig) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, this.withinLast, this.withinNext, this.hour, Optional.of(minuteConfig), this.second, this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withSecond(@NonNull FieldConstraintConfig secondConfig) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, this.withinLast, this.withinNext, this.hour, this.minute, Optional.of(secondConfig), this.millisecond);
	}

	@Override
	public @NonNull OffsetTimeConstraintConfig withMillisecond(@NonNull FieldConstraintConfig millisecondConfig) {
		return new OffsetTimeConstraintConfig(this.min, this.max, this.equals, this.withinLast, this.withinNext, this.hour, this.minute, this.second, Optional.of(millisecondConfig));
	}

	public @NonNull Result<Void> matches(@NonNull OffsetTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.equals.isPresent()) {
			Pair<OffsetTime, Boolean> pair = this.equals.get();
			if (pair.getSecond()) {
				if (!value.equals(pair.getFirst())) {
					return Result.success();
				}
				return Result.error("Violated equals constraint: value (" + value + ") is equal to expected (" + pair.getFirst() + "), but it should not be");
			} else {
				if (value.equals(pair.getFirst())) {
					return Result.success();
				}
				return Result.error("Violated equals constraint: value (" + value + ") is not equal to expected (" + pair.getFirst() + "), but it should be");
			}
		}

		if (this.withinLast.isPresent()) {
			OffsetTime threshold = OffsetTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			OffsetTime threshold = OffsetTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		if (this.min.isPresent()) {
			Pair<OffsetTime, Boolean> pair = this.min.get();
			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison < 0) {
					return Result.error("Violated minimum constraint: value (" + value + ") is before min (" + pair.getFirst() + "), but it should be at least min");
				}
			} else {
				if (comparison <= 0) {
					return Result.error("Violated minimum constraint (exclusive): value (" + value + ") is before or equal to min (" + pair.getFirst() + "), but it should be after min");
				}
			}
		}

		if (this.max.isPresent()) {
			Pair<OffsetTime, Boolean> pair = this.max.get();
			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison > 0) {
					return Result.error("Violated maximum constraint: value (" + value + ") is after max (" + pair.getFirst() + "), but it should be at most max");
				}
			} else {
				if (comparison >= 0) {
					return Result.error("Violated maximum constraint (exclusive): value (" + value + ") is after or equal to max (" + pair.getFirst() + "), but it should be before max");
				}
			}
		}

		if (this.hour.isPresent()) {
			Result<Void> hourResult = this.hour.get().matches("hour", value.getHour());
			if (hourResult.isError()) {
				return hourResult;
			}
		}

		if (this.minute.isPresent()) {
			Result<Void> minuteResult = this.minute.get().matches("minute", value.getMinute());
			if (minuteResult.isError()) {
				return minuteResult;
			}
		}

		if (this.second.isPresent()) {
			Result<Void> secondResult = this.second.get().matches("second", value.getSecond());
			if (secondResult.isError()) {
				return secondResult;
			}
		}

		if (this.millisecond.isPresent()) {
			Result<Void> millisecondResult = this.millisecond.get().matches("millisecond", value.getNano() / 1_000_000);
			if (millisecondResult.isError()) {
				return millisecondResult;
			}
		}

		return Result.success();
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "OffsetTimeConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.min.ifPresent(pair -> constraints.add("min=" + pair.getFirst()));
		this.max.ifPresent(pair -> constraints.add("max=" + pair.getFirst()));
		this.equals.ifPresent(pair -> constraints.add("equals=" + pair.getFirst()));
		this.withinLast.ifPresent(d -> constraints.add("withinLast=" + d));
		this.withinNext.ifPresent(d -> constraints.add("withinNext=" + d));
		this.hour.ifPresent(h -> h.appendConstraints("hour", constraints));
		this.minute.ifPresent(m -> m.appendConstraints("minute", constraints));
		this.second.ifPresent(s -> s.appendConstraints("second", constraints));
		this.millisecond.ifPresent(ms -> ms.appendConstraints("millisecond", constraints));
		return "OffsetTimeConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
