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

package net.luis.utils.io.codec.constraint.merged.temporal.offset;

import net.luis.utils.io.codec.constraint.core.temporal.*;
import net.luis.utils.io.codec.constraint.core.ApplicableConstraint;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint_new.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import net.luis.utils.io.codec.constraint.config.temporal.offset.OffsetDateTimeConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.Collection;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link OffsetDateTime} that provides temporal validation operations.<br>
 * <p>
 *     This interface extends {@link TemporalComparableConstraint}, {@link TemporalSpanConstraint},
 *     {@link DateFieldConstraint}, and {@link TimeFieldConstraint} to provide comprehensive
 *     offset date time validation capabilities.<br>
 *     It inherits temporal comparison constraints (after, before, between), span constraints
 *     (withinLast, withinNext), date field constraints (dayOfWeek, dayOfMonth, dayOfYear,
 *     weekOfMonth, weekOfYear, month, year), and time field constraints (hour, minute, second,
 *     millisecond, nanosecond) for validating offset date time values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface OffsetDateTimeConstraint<C> extends ApplicableConstraint<OffsetDateTimeConstraintConfig, C>, TemporalComparableConstraint<OffsetDateTime, C>, TemporalSpanConstraint<OffsetDateTime, C>, DateFieldConstraint<OffsetDateTime, C>, TimeFieldConstraint<OffsetDateTime, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<OffsetDateTimeConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<OffsetDateTime> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<OffsetDateTime> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<OffsetDateTime> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C after(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withAfter(value));
	}
	
	@Override
	default @NonNull C afterOrEqual(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withAfterOrEqual(value));
	}
	
	@Override
	default @NonNull C before(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withBefore(value));
	}
	
	@Override
	default @NonNull C beforeOrEqual(@NonNull OffsetDateTime value) {
		return this.apply(config -> config.withBeforeOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull OffsetDateTime min, @NonNull OffsetDateTime max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull OffsetDateTime min, @NonNull OffsetDateTime max) {
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
