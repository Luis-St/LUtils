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
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for temporal span-based operations.<br>
 * <p>
 *     This interface extends {@link TemporalComparisonConstraint} with duration-based constraints
 *     that allow validation of temporal values relative to the current time.<br>
 *     It provides methods to constrain values to be within the last or next duration from now.
 * </p>
 * <p>
 *     Applies to: LocalDate, LocalDateTime, LocalTime, ZonedDateTime, OffsetDateTime, OffsetTime.
 * </p>
 * <p>
 *     Note: LocalDate, LocalTime, and OffsetTime do not support Duration for fields that are
 *     not supported by the type itself (e.g., days for LocalDate, hours/minutes/seconds for LocalTime).
 * </p>
 *
 * @see TemporalComparisonConstraint
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
@FunctionalInterface
public interface TemporalSpanConstraint<T extends Temporal & Comparable<? super T>, C extends Codec<T>, V extends TemporalConstraintConfigProvider<T, V>> extends TemporalComparisonConstraint<T, C, V> {
	
	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<V> configModifier);
	
	/**
	 * Applies a "within last" duration constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are within the last specified duration from the current time.<br>
	 *     For example, {@code withinLast(Duration.ofDays(7))} ensures the value is within the last 7 days,
	 *     or {@code withinLast(Duration.ofHours(2))} ensures the value is within the last 2 hours.
	 * </p>
	 * <p>
	 *     The validation is performed at the time of encoding/decoding, using the current time as reference.
	 * </p>
	 *
	 * @param duration The duration backwards from now
	 * @return A new codec with the applied within-last constraint
	 * @throws NullPointerException If the duration is null
	 * @throws IllegalArgumentException If the duration is negative or not supported by the temporal type
	 * @see #withinNext(Duration)
	 */
	default @NonNull C withinLast(@NonNull Duration duration) {
		return this.applyConstraint(config -> config.withWithinLast(duration));
	}

	/**
	 * Applies a "within next" duration constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are within the next specified duration from the current time.<br>
	 *     For example, {@code withinNext(Duration.ofDays(30))} ensures the value is within the next 30 days,
	 *     or {@code withinNext(Duration.ofHours(1))} ensures the value is within the next hour.
	 * </p>
	 * <p>
	 *     The validation is performed at the time of encoding/decoding, using the current time as reference.
	 * </p>
	 *
	 * @param duration The duration forwards from now
	 * @return A new codec with the applied within-next constraint
	 * @throws NullPointerException If the duration is null
	 * @throws IllegalArgumentException If the duration is negative or not supported by the temporal type
	 * @see #withinLast(Duration)
	 */
	default @NonNull C withinNext(@NonNull Duration duration) {
		return this.applyConstraint(config -> config.withWithinNext(duration));
	}
}
