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

import net.luis.utils.io.codec.constraint_new.ApplicableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint.config.temporal.InstantConstraintConfig;
import net.luis.utils.io.codec.constraint.core.temporal.TemporalComparableConstraint;
import net.luis.utils.io.codec.constraint.core.temporal.TemporalSpanConstraint;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for {@link Instant} that provides temporal validation operations.<br>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface InstantConstraint<C> extends ApplicableConstraint<InstantConstraintConfig, C>, TemporalComparableConstraint<Instant, C>, TemporalSpanConstraint<Instant, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<InstantConstraintConfig> configModifier);
	
	@Override
	default @NonNull C equalTo(@NonNull Instant value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull Instant value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<Instant> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<Instant> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<Instant> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	@Override
	default @NonNull C after(@NonNull Instant value) {
		return this.apply(config -> config.withAfter(value));
	}
	
	@Override
	default @NonNull C afterOrEqual(@NonNull Instant value) {
		return this.apply(config -> config.withAfterOrEqual(value));
	}
	
	@Override
	default @NonNull C before(@NonNull Instant value) {
		return this.apply(config -> config.withBefore(value));
	}
	
	@Override
	default @NonNull C beforeOrEqual(@NonNull Instant value) {
		return this.apply(config -> config.withBeforeOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull Instant min, @NonNull Instant max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull Instant min, @NonNull Instant max) {
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
}
