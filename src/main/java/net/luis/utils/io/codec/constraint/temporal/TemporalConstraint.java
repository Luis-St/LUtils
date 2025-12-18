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

package net.luis.utils.io.codec.constraint.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.CodecConstraint;
import net.luis.utils.io.codec.constraint.config.temporal.TemporalConstraintConfig;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;

/**
 * A constraint interface for temporal types (dates, times, durations, etc.).<br>
 * Provides methods to validate temporal values against various time-based constraints.<br>
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 */
public interface TemporalConstraint<T extends Temporal & Comparable<T>, C extends Codec<T>> extends CodecConstraint<T, C, TemporalConstraintConfig> {
	
	@Override
	@NonNull C applyConstraint(@NonNull TemporalConstraintConfig config);
	
	@NotNull C after(@NotNull T minimum);
	
	@NotNull C afterOrEqual(@NotNull T minimum);
	
	@NotNull C before(@NotNull T maximum);
	
	@NotNull C beforeOrEqual(@NotNull T maximum);
	
	@NotNull C betweenInclusive(@NotNull T minimum, @NotNull T maximum);
	
	@NotNull C betweenExclusive(@NotNull T minimum, @NotNull T maximum);
	
	@NotNull C equalTo(@NotNull T target);
	
	@NotNull C notEqualTo(@NotNull T target);
	
	@NotNull C past();
	
	@NotNull C pastOrPresent();
	
	@NotNull C future();
	
	@NotNull C futureOrPresent();
	
	@NotNull C withinLast(@NotNull Duration duration);
	
	@NotNull C withinNext(@NotNull Duration duration);
	
	default @NotNull TemporalFieldConstraint<T, C> millisecond() {
		return TemporalFieldConstraint.of(this, ChronoField.MILLI_OF_SECOND);
	}
	
	default @NotNull TemporalFieldConstraint<T, C> second() {
		return TemporalFieldConstraint.of(this, ChronoField.SECOND_OF_MINUTE);
	}
	
	default @NotNull TemporalFieldConstraint<T, C> minute() {
		return TemporalFieldConstraint.of(this, ChronoField.MINUTE_OF_HOUR);
	}
	
	default @NotNull TemporalFieldConstraint<T, C> hour() {
		return TemporalFieldConstraint.of(this, ChronoField.HOUR_OF_DAY);
	}
	
	default @NotNull DayConstraint<T, C> day() {
		return DayConstraint.of(this);
	}
	
	default @NotNull WeekConstraint<T, C> week() {
		return WeekConstraint.of(this);
	}
	
	default @NotNull MonthConstraint<T, C> month() {
		return MonthConstraint.of(this);
	}
	
	default @NotNull TemporalFieldConstraint<T, C> year() {
		return TemporalFieldConstraint.of(this, ChronoField.YEAR);
	}
}
