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

import net.luis.utils.io.codec.constraint.config.temporal.FieldConstraintConfig;
import net.luis.utils.io.codec.constraint.core.ComparableConstraintBuilder;
import net.luis.utils.io.codec.constraint.core.provider.TimeFieldConstraintConfigProvider;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for time field validation.<br>
 * <p>
 *     This interface provides methods to apply constraints to individual time components
 *     such as hour, minute, second, and millisecond of temporal types.<br>
 *     Constraints are defined using the {@link ComparableConstraintBuilder} which allows
 *     chaining of multiple constraint operations.
 * </p>
 * <p>
 *     Applies to: LocalDateTime, LocalTime, ZonedDateTime, OffsetDateTime, OffsetTime.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Business hours: 9 AM to 5 PM, excluding noon
 * codec.hour(c -> c.between(9, 17).notEqualTo(12))
 *
 * // Only times on the half-hour
 * codec.minute(c -> c.equalTo(30))
 *
 * // Microsecond precision
 * codec.millisecond(c -> c.equalTo(0))
 * }</pre>
 *
 * @see ComparableConstraintBuilder
 * @see DateFieldConstraint
 *
 * @author Luis-St
 *
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
@FunctionalInterface
public interface TimeFieldConstraint<C, V extends TimeFieldConstraintConfigProvider<V>> {
	
	/**
	 * Applies a time field constraint to the codec.<br>
	 * <p>
	 *     This method must be implemented by codecs to handle the application of time field constraints.
	 * </p>
	 *
	 * @param configModifier A function that modifies the constraint configuration
	 * @return A new codec with the applied constraint
	 * @throws NullPointerException If the constraint config modifier is null
	 */
	@NonNull C applyTimeFieldConstraint(@NonNull UnaryOperator<V> configModifier);
	
	/**
	 * Applies a constraint to the hour field of the temporal value.<br>
	 * <p>
	 *     The hour field represents the hour of day in the range 0-23.<br>
	 *     The constraint is defined using a builder function that configures the allowed hour values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the hour constraint using a builder
	 * @return A new codec with the applied hour constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #minute(Function)
	 */
	default @NonNull C hour(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyTimeFieldConstraint(config -> config.withHour(fieldConfig));
	}

	/**
	 * Applies a constraint to the minute field of the temporal value.<br>
	 * <p>
	 *     The minute field represents the minute of hour in the range 0-59.<br>
	 *     The constraint is defined using a builder function that configures the allowed minute values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the minute constraint using a builder
	 * @return A new codec with the applied minute constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #hour(Function)
	 * @see #second(Function)
	 */
	default @NonNull C minute(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyTimeFieldConstraint(config -> config.withMinute(fieldConfig));
	}

	/**
	 * Applies a constraint to the second field of the temporal value.<br>
	 * <p>
	 *     The second field represents the second of minute in the range 0-59.<br>
	 *     The constraint is defined using a builder function that configures the allowed second values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the second constraint using a builder
	 * @return A new codec with the applied second constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #minute(Function)
	 * @see #millisecond(Function)
	 */
	default @NonNull C second(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyTimeFieldConstraint(config -> config.withSecond(fieldConfig));
	}

	/**
	 * Applies a constraint to the millisecond field of the temporal value.<br>
	 * <p>
	 *     The millisecond field represents the millisecond of second in the range 0-999.<br>
	 *     The constraint is defined using a builder function that configures the allowed millisecond values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the millisecond constraint using a builder
	 * @return A new codec with the applied millisecond constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #second(Function)
	 */
	default @NonNull C millisecond(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyTimeFieldConstraint(config -> config.withMillisecond(fieldConfig));
	}
}
