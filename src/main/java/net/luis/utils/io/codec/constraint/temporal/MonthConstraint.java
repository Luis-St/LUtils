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
import net.luis.utils.io.codec.constraint.config.temporal.MonthConstraintConfig;
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Month;
import java.time.temporal.Temporal;
import java.util.*;

@FunctionalInterface
public interface MonthConstraint<T extends Temporal & Comparable<T>, C> extends CodecConstraint<T, C, MonthConstraintConfig> {
	
	Map<Integer, Set<Month>> MONTHS_BY_QUARTER = Utils.make(new HashMap<>(), map -> {
		map.put(1, Set.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH));
		map.put(2, Set.of(Month.APRIL, Month.MAY, Month.JUNE));
		map.put(3, Set.of(Month.JULY, Month.AUGUST, Month.SEPTEMBER));
		map.put(4, Set.of(Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER));
	});
	
	static <T extends Temporal & Comparable<T>, C extends Codec<T>> @NonNull MonthConstraint<T, C> of(@NotNull TemporalConstraint<T, C> temporalConstraint) {
		Objects.requireNonNull(temporalConstraint, "Temporal constraint must not be null");
		
		return new MonthConstraint<>() {
			@Override
			public @NonNull C applyConstraint(@NonNull MonthConstraintConfig config) {
				return null;
			}
		};
	}
	
	@Override
	@NonNull C applyConstraint(@NonNull MonthConstraintConfig config);
	
	default @NotNull C equalTo(@NotNull Month month) {
		return this.applyConstraint(new MonthConstraintConfig(
			Optional.of(month),
			Optional.of(false),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C notEqualTo(@NotNull Month month) {
		return this.applyConstraint(new MonthConstraintConfig(
			Optional.of(month),
			Optional.of(true),
			Optional.empty(),
			Optional.empty()
		));
	}
	
	default @NotNull C in(Month @NotNull ... months) {
		return this.in(Set.of(months));
	}
	
	default @NotNull C in(@NotNull Set<Month> months) {
		return this.applyConstraint(new MonthConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.of(months),
			Optional.empty()
		));
	}
	
	default @NotNull C notIn(Month @NotNull ... months) {
		return this.notIn(Set.of(months));
	}
	
	default @NotNull C notIn(@NotNull Set<Month> months) {
		return this.applyConstraint(new MonthConstraintConfig(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.of(months)
		));
	}
	
	default @NotNull C quarter(int quarter) {
		if (quarter < 1 || quarter > 4) {
			throw new IllegalArgumentException("Quarter must be between 1 and 4");
		}
		return this.in(MONTHS_BY_QUARTER.get(quarter));
	}
	
	default @NotNull C notQuarter(int quarter) {
		if (quarter < 1 || quarter > 4) {
			throw new IllegalArgumentException("Quarter must be between 1 and 4");
		}
		return this.notIn(MONTHS_BY_QUARTER.get(quarter));
	}
	
	default @NotNull C firstHalf() {
		return this.in(Month.JANUARY, Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE);
	}
	
	default @NotNull C secondHalf() {
		return this.in(Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER);
	}
}
