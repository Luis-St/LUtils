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

package net.luis.utils.io.codec.constraint.temporal;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import org.jspecify.annotations.NonNull;

import java.time.temporal.Temporal;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for temporal comparison operations.<br>
 * <p>
 *     This interface extends {@link TemporalConstraint} with comparison-based constraints
 *     that allow validation of temporal values relative to other temporal values.<br>
 *     It provides methods to constrain values to be before, after, or within a range of times.
 * </p>
 * <p>
 *     Applies to: LocalDate, LocalDateTime, LocalTime, Instant, ZonedDateTime, OffsetDateTime, OffsetTime, Year.
 * </p>
 *
 * @see TemporalConstraint
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
@FunctionalInterface
public interface TemporalComparisonConstraint<T extends Temporal & Comparable<? super T>, C extends Codec<T>, V extends TemporalConstraintConfigProvider<T, V>> extends TemporalConstraint<T, C, V> {
	
	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<V> configModifier);
	
	/**
	 * Applies an "after" constraint to the codec (exclusive).<br>
	 * <p>
	 *     The returned codec will validate that values are strictly after the specified time.<br>
	 *     For example, if the constraint is {@code after(10:00)}, then {@code 10:00:01} passes but {@code 10:00:00} fails.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be after
	 * @return A new codec with the applied after constraint
	 * @throws NullPointerException If the value is null
	 * @see #afterOrEqual(Temporal)
	 * @see #before(Temporal)
	 */
	default @NonNull C after(@NonNull T value) {
		return this.applyConstraint(config -> config.withMin(value, false));
	}
	
	/**
	 * Applies an "after or equal" constraint to the codec (inclusive).<br>
	 * <p>
	 *     The returned codec will validate that values are after or equal to the specified time.<br>
	 *     For example, if the constraint is {@code afterOrEqual(10:00)}, then both {@code 10:00:00} and {@code 10:00:01} pass.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be after or equal to
	 * @return A new codec with the applied after-or-equal constraint
	 * @throws NullPointerException If the value is null
	 * @see #after(Temporal)
	 * @see #beforeOrEqual(Temporal)
	 */
	default @NonNull C afterOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withMin(value, true));
	}
	
	/**
	 * Applies a "before" constraint to the codec (exclusive).<br>
	 * <p>
	 *     The returned codec will validate that values are strictly before the specified time.<br>
	 *     For example, if the constraint is {@code before(10:00)}, then {@code 09:59:59} passes but {@code 10:00:00} fails.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be before
	 * @return A new codec with the applied before constraint
	 * @throws NullPointerException If the value is null
	 * @see #beforeOrEqual(Temporal)
	 * @see #after(Temporal)
	 */
	default @NonNull C before(@NonNull T value) {
		return this.applyConstraint(config -> config.withMax(value, false));
	}
	
	/**
	 * Applies a "before or equal" constraint to the codec (inclusive).<br>
	 * <p>
	 *     The returned codec will validate that values are before or equal to the specified time.<br>
	 *     For example, if the constraint is {@code beforeOrEqual(10:00)}, then both {@code 09:59:59} and {@code 10:00:00} pass.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be before or equal to
	 * @return A new codec with the applied before-or-equal constraint
	 * @throws NullPointerException If the value is null
	 * @see #before(Temporal)
	 * @see #afterOrEqual(Temporal)
	 */
	default @NonNull C beforeOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withMax(value, true));
	}
	
	/**
	 * Applies a temporal range constraint to the codec (exclusive bounds).<br>
	 * <p>
	 *     The returned codec will validate that values are strictly within the specified time range.<br>
	 *     For example, if the constraint is {@code between(09:00, 17:00)}, then {@code 10:00} passes but {@code 09:00} and {@code 17:00} fail.
	 * </p>
	 *
	 * @param min The minimum temporal value (exclusive)
	 * @param max The maximum temporal value (exclusive)
	 * @return A new codec with the applied range constraint
	 * @throws NullPointerException If min or max is null
	 * @throws IllegalArgumentException If min is after or equal to max
	 * @see #betweenOrEqual(Temporal, Temporal)
	 */
	default @NonNull C between(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withRange(min, max, false));
	}
	
	/**
	 * Applies a temporal range constraint to the codec (inclusive bounds).<br>
	 * <p>
	 *     The returned codec will validate that values are within the specified time range, including boundaries.<br>
	 *     For example, if the constraint is {@code betweenOrEqual(09:00, 17:00)}, then {@code 09:00}, {@code 10:00}, and {@code 17:00} all pass.
	 * </p>
	 *
	 * @param min The minimum temporal value (inclusive)
	 * @param max The maximum temporal value (inclusive)
	 * @return A new codec with the applied range constraint
	 * @throws NullPointerException If min or max is null
	 * @throws IllegalArgumentException If min is after max
	 * @see #between(Temporal, Temporal)
	 */
	default @NonNull C betweenOrEqual(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withRange(min, max, true));
	}
}
