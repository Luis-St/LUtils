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

package net.luis.utils.io.codec.constraint.merged.temporal;

import net.luis.utils.io.codec.constraint_new.*;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.temporal.DurationConstraintConfig;
import net.luis.utils.io.codec.constraint.core.temporal.TemporalSpanConstraint;
import net.luis.utils.io.codec.constraint.core.temporal.TimeFieldConstraint;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link Duration} that provides time-based duration validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} and {@link TimeFieldConstraint} to provide
 *     comprehensive duration validation capabilities.<br>
 *     It inherits sign-based constraints (positive, negative, zero) and time field constraints
 *     (hour, minute, second, millisecond, nanosecond) for validating duration values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface DurationConstraint<C> extends ApplicableConstraint<DurationConstraintConfig, C>, SignedConstraint<Duration, C>, TimeFieldConstraint<Duration, C>, TemporalSpanConstraint<Duration, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<DurationConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Duration value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Duration value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Duration> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Duration> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Duration> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C greaterThan(@NonNull Duration value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull Duration value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull Duration value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull Duration value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull Duration min, @NonNull Duration max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull Duration min, @NonNull Duration max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	@Override
	default @NonNull C positive() {
		return this.apply(DurationConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(DurationConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(DurationConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(DurationConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(DurationConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(DurationConstraintConfig::withNonZero);
	}
	
	@Override
	default @NonNull C withinLast(@NonNull Duration duration) {
		return this.apply(config -> config.withWithinLast(duration));
	}
	
	@Override
	default @NonNull C withinNext(@NonNull Duration duration) {
		return this.apply(config -> config.withWithinNext(duration));
	}
	
	@Override
	default @NonNull C hour(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withHour(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C minute(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withMinute(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C second(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withSecond(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C millisecond(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withMillisecond(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C nanosecond(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withNanosecond(numericBuilder.build()));
	}
}
