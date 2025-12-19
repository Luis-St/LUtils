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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.CodecConstraint;
import net.luis.utils.io.codec.constraint.config.temporal.DayConstraintConfig;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.temporal.Temporal;
import java.util.*;

@FunctionalInterface
public interface DayConstraint<T extends Temporal & Comparable<T>, C> extends CodecConstraint<T, C, DayConstraintConfig> {
	
	static <T extends Temporal & Comparable<T>, C extends Codec<T>> @NonNull DayConstraint<T, C> of(@NotNull TemporalConstraint<T, C> temporalConstraint) {
		Objects.requireNonNull(temporalConstraint, "Temporal constraint must not be null");
		
		return new DayConstraint<>() {
			@Override
			public @NonNull C applyConstraint(@NonNull DayConstraintConfig config) {
				return null;
			}
		};
	}
	
	@Override
	@NonNull C applyConstraint(@NonNull DayConstraintConfig config);
	
	default @NotNull C equalTo(@NotNull DayOfWeek day) {
		return this.applyConstraint(new DayConstraintConfig(
			Optional.of(day),
			Optional.of(false),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C notEqualTo(@NotNull DayOfWeek day) {
		return this.applyConstraint(new DayConstraintConfig(
			Optional.of(day),
			Optional.of(true),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C in(DayOfWeek @NotNull ... days) {
		return this.in(Set.of(days));
	}
	
	default @NotNull C in(@NotNull Set<DayOfWeek> days) {
		return this.applyConstraint(new DayConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.of(days),
			Optional.empty()
		));
	}
	
	default @NotNull C notIn(DayOfWeek @NotNull ... days) {
		return this.notIn(Set.of(days));
	}
	
	default @NotNull C notIn(@NotNull Set<DayOfWeek> days) {
		return this.applyConstraint(new DayConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(days)
		));
	}
	
	default @NotNull C weekday() {
		return this.in(Set.of(
			DayOfWeek.MONDAY,
			DayOfWeek.TUESDAY,
			DayOfWeek.WEDNESDAY,
			DayOfWeek.THURSDAY,
			DayOfWeek.FRIDAY
		));
	}
	
	default @NotNull C weekend() {
		return this.in(Set.of(
			DayOfWeek.SATURDAY,
			DayOfWeek.SUNDAY
		));
	}
}
