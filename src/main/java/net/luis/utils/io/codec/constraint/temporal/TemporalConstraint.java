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
import net.luis.utils.io.codec.constraint.CodecConstraint;
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import org.jspecify.annotations.NonNull;

import java.time.temporal.Temporal;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for temporal value validation.<br>
 * <p>
 *     This interface provides a set of default methods to apply common temporal constraints to codecs.<br>
 *     The constraints are applied to temporal values during encoding and decoding operations, ensuring that
 *     only values meeting the temporal requirements are successfully processed.
 * </p>
 * <p>
 *     All temporal types (LocalDate, LocalTime, LocalDateTime, Instant, ZonedDateTime, OffsetDateTime, OffsetTime, Year)
 *     can use the basic equality constraints provided by this interface.
 * </p>
 *
 * @see TemporalComparisonConstraint
 * @see TemporalSpanConstraint
 * @see TimeFieldConstraint
 * @see DateFieldConstraint
 *
 * @author Luis-St
 *
 * @param <T> The temporal type being constrained
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
@FunctionalInterface
public interface TemporalConstraint<T extends Temporal, C extends Codec<T>, V extends TemporalConstraintConfigProvider<T, V>> extends CodecConstraint<C, V> {
	
	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<V> configModifier);
	
	/**
	 * Applies an equality constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are equal to the specified temporal value.<br>
	 *     This constraint is useful when only a specific temporal value should be accepted.
	 * </p>
	 *
	 * @param value The exact temporal value that should be matched
	 * @return A new codec with the applied equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #notEqualTo(Temporal)
	 */
	default @NonNull C equalTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withEquals(value, false));
	}
	
	/**
	 * Applies a non-equality constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are not equal to the specified temporal value.<br>
	 *     This constraint is useful when a specific temporal value should be excluded.
	 * </p>
	 *
	 * @param value The temporal value that should be excluded
	 * @return A new codec with the applied non-equality constraint
	 * @throws NullPointerException If the value is null
	 * @see #equalTo(Temporal)
	 */
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withEquals(value, true));
	}
}
