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

package net.luis.utils.io.codec.constraint_new.temporal;

import net.luis.utils.io.codec.constraint_new.*;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.config.temporal.ZoneOffsetConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link ZoneOffset} that provides offset validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} with methods for constraining zone offsets
 *     based on their hour component and special values like UTC.<br>
 *     It allows validation of time zone offsets which can be positive (east of UTC) or negative (west of UTC).
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface ZoneOffsetConstraint<C> extends ApplicableConstraint<ZoneOffsetConstraintConfig, C>, SignedConstraint<ZoneOffset, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<ZoneOffsetConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<ZoneOffset> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<ZoneOffset> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<ZoneOffset> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C greaterThan(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull ZoneOffset value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull ZoneOffset min, @NonNull ZoneOffset max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	@Override
	default @NonNull C positive() {
		return this.apply(ZoneOffsetConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(ZoneOffsetConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(ZoneOffsetConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(ZoneOffsetConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(ZoneOffsetConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(ZoneOffsetConstraintConfig::withNonZero);
	}
	
	/**
	 * Applies a UTC offset constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone offsets are UTC (zero offset).<br>
	 *     This is a convenience method equivalent to {@code zero()}.
	 * </p>
	 *
	 * @return A new type with the applied UTC constraint
	 * @see #zero()
	 */
	default @NonNull C utc() {
		return this.zero();
	}
	
	/**
	 * Applies an hour constraint to the zone offset using a builder.<br>
	 * <p>
	 *     The returned type will validate that the hour component of zone offsets matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the hour constraint using a numeric constraint builder
	 * @return A new type with the applied hour constraint
	 * @throws NullPointerException If the builder is null
	 */
	default @NonNull C hour(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withHour(numericBuilder.build()));
	}
}
