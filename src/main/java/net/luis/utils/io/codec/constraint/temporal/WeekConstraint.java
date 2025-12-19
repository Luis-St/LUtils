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
import net.luis.utils.io.codec.constraint.config.temporal.WeekConstraintConfig;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FunctionalInterface
public interface WeekConstraint<T extends Temporal & Comparable<T>, C> extends CodecConstraint<T, C, WeekConstraintConfig> {
	
	static <T extends Temporal & Comparable<T>, C extends Codec<T>> @NonNull WeekConstraint<T, C> of(@NotNull TemporalConstraint<T, C> temporalConstraint) {
		Objects.requireNonNull(temporalConstraint, "Temporal constraint must not be null");
		
		return new WeekConstraint<>() {
			@Override
			public @NonNull C applyConstraint(@NonNull WeekConstraintConfig config) {
				return null;
			}
		};
	}
	
	@Override
	@NonNull C applyConstraint(@NonNull WeekConstraintConfig config);
	
	default @NotNull C equalTo(@NotNull WeekType type, int week) {
		return this.applyConstraint(new WeekConstraintConfig(
			Optional.of(Pair.of(type, week)),
			Optional.of(false),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C notEqualTo(@NotNull WeekType type, int week) {
		return this.applyConstraint(new WeekConstraintConfig(
			Optional.of(Pair.of(type, week)),
			Optional.of(true),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C in(@NotNull WeekType type, int @NotNull ... weeks) {
		return this.in(type, IntStream.of(weeks).boxed().collect(Collectors.toSet()));
	}
	
	default @NotNull C in(@NotNull WeekType type, @NotNull Set<Integer> weeks) {
		return this.applyConstraint(new WeekConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.of(Pair.of(type, weeks)),
			Optional.empty()
		));
	}
	
	default @NotNull C notIn(@NotNull WeekType type, int @NotNull ... weeks) {
		return this.notIn(type, IntStream.of(weeks).boxed().collect(Collectors.toSet()));
	}
	
	default @NotNull C notIn(@NotNull WeekType type, @NotNull Set<Integer> weeks) {
		return this.applyConstraint(new WeekConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(Pair.of(type, weeks))
		));
	}
	
	default @NotNull C first(@NotNull WeekType type) {
		return this.equalTo(type, 1);
	}
	
	default @NotNull C last(@NotNull WeekType type) {
		return this.equalTo(type, type.getLast());
	}
}
