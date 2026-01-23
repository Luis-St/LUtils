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

package net.luis.utils.io.codec.constraint.merged.temporal.local;

import net.luis.utils.io.codec.constraint.core.temporal.*;
import net.luis.utils.io.codec.constraint_new.ApplicableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.temporal.local.LocalDateConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link LocalDate} that provides temporal validation operations.<br>
 * <p>
 *     This interface extends {@link TemporalComparableConstraint}, {@link TemporalSpanConstraint},
 *     and {@link DateFieldConstraint} to provide comprehensive local date validation capabilities.<br>
 *     It inherits temporal comparison constraints (after, before, between), span constraints
 *     (withinLast, withinNext), and date field constraints (dayOfWeek, dayOfMonth, dayOfYear,
 *     weekOfMonth, weekOfYear, month, year) for validating local date values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface LocalDateConstraint<C> extends ApplicableConstraint<LocalDateConstraintConfig, C>, TemporalComparableConstraint<LocalDate, C>, TemporalSpanConstraint<LocalDate, C>, DateFieldConstraint<LocalDate, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<LocalDateConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull LocalDate value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull LocalDate value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<LocalDate> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<LocalDate> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<LocalDate> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C after(@NonNull LocalDate value) {
		return this.apply(config -> config.withAfter(value));
	}
	
	@Override
	default @NonNull C afterOrEqual(@NonNull LocalDate value) {
		return this.apply(config -> config.withAfterOrEqual(value));
	}
	
	@Override
	default @NonNull C before(@NonNull LocalDate value) {
		return this.apply(config -> config.withBefore(value));
	}
	
	@Override
	default @NonNull C beforeOrEqual(@NonNull LocalDate value) {
		return this.apply(config -> config.withBeforeOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull LocalDate min, @NonNull LocalDate max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull LocalDate min, @NonNull LocalDate max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
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
	default @NonNull C dayOfWeek(@NonNull UnaryOperator<EnumConstraintBuilder<DayOfWeek>> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		EnumConstraintBuilder<DayOfWeek> enumBuilder = new EnumConstraintBuilder<>();
		builder.apply(enumBuilder);
		return this.apply(config -> config.withDayOfWeek(enumBuilder.build()));
	}
	
	@Override
	default @NonNull C dayOfMonth(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withDayOfMonth(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C dayOfYear(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withDayOfYear(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C weekOfMonth(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withWeekOfMonth(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C weekOfYear(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withWeekOfYear(numericBuilder.build()));
	}
	
	@Override
	default @NonNull C month(@NonNull UnaryOperator<EnumConstraintBuilder<Month>> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		EnumConstraintBuilder<Month> enumBuilder = new EnumConstraintBuilder<>();
		builder.apply(enumBuilder);
		return this.apply(config -> config.withMonth(enumBuilder.build()));
	}
	
	@Override
	default @NonNull C year(@NonNull UnaryOperator<NumericConstraintBuilder> builder) {
		Objects.requireNonNull(builder, "Builder must not be null");
		
		NumericConstraintBuilder numericBuilder = new NumericConstraintBuilder();
		builder.apply(numericBuilder);
		return this.apply(config -> config.withYear(numericBuilder.build()));
	}
}
