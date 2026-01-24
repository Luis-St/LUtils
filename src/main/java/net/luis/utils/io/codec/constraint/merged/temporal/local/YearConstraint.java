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

import net.luis.utils.io.codec.constraint.core.ApplicableConstraint;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.temporal.local.YearConstraintConfig;
import net.luis.utils.io.codec.constraint.core.temporal.TemporalComparableConstraint;
import org.jspecify.annotations.NonNull;

import java.time.Year;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link Year} that provides temporal validation operations.<br>
 * <p>
 *     This interface extends {@link TemporalComparableConstraint} to provide year validation capabilities.<br>
 *     It inherits temporal comparison constraints (after, before, between) for validating year values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface YearConstraint<C> extends ApplicableConstraint<YearConstraintConfig, C>, TemporalComparableConstraint<Year, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<YearConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Year value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Year value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Year> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Year> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Year> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C after(@NonNull Year value) {
		return this.apply(config -> config.withAfter(value));
	}
	
	@Override
	default @NonNull C afterOrEqual(@NonNull Year value) {
		return this.apply(config -> config.withAfterOrEqual(value));
	}
	
	@Override
	default @NonNull C before(@NonNull Year value) {
		return this.apply(config -> config.withBefore(value));
	}
	
	@Override
	default @NonNull C beforeOrEqual(@NonNull Year value) {
		return this.apply(config -> config.withBeforeOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull Year min, @NonNull Year max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull Year min, @NonNull Year max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
}
