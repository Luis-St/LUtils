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

import net.luis.utils.io.codec.constraint.core.*;
import net.luis.utils.io.codec.constraint.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.temporal.PeriodConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.Period;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link Period} that provides date-based period validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} with methods for constraining periods
 *     based on their day, month, and year components.<br>
 *     It allows fine-grained validation of date-based period values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface PeriodConstraint<C> extends ApplicableConstraint<PeriodConstraintConfig, C>, SignedConstraint<Period, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<PeriodConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Period value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Period value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Period> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Period> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Period> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C greaterThan(@NonNull Period value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull Period value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull Period value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull Period value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull Period min, @NonNull Period max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull Period min, @NonNull Period max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	@Override
	default @NonNull C positive() {
		return this.apply(PeriodConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(PeriodConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(PeriodConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(PeriodConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(PeriodConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(PeriodConstraintConfig::withNonZero);
	}
	
	/**
	 * Applies a day constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the day component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the day constraint using a numeric constraint builder
	 * @return A new type with the applied day constraint
	 * @throws NullPointerException If the builder is null
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	default @NonNull C day(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withDay(numericBuilder.build()));
	}
	
	/**
	 * Applies a month constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the month component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the month constraint using a numeric constraint builder
	 * @return A new type with the applied month constraint
	 * @throws NullPointerException If the builder is null
	 * @see #day(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	default @NonNull C month(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withMonth(numericBuilder.build()));
	}
	
	/**
	 * Applies a year constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the year component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the year constraint using a numeric constraint builder
	 * @return A new type with the applied year constraint
	 * @throws NullPointerException If the builder is null
	 * @see #day(UnaryOperator)
	 * @see #month(UnaryOperator)
	 */
	default @NonNull C year(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withYear(numericBuilder.build()));
	}
}
