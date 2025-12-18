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
import net.luis.utils.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Month;
import java.time.temporal.Temporal;
import java.util.*;

@FunctionalInterface
public interface MonthConstraint<T extends Temporal & Comparable<T>, C extends Codec<T>> {
	
	Map<Integer, List<Month>> MONTHS_BY_QUARTER = Utils.make(new HashMap<>(), map -> {
		map.put(1, List.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH));
		map.put(2, List.of(Month.APRIL, Month.MAY, Month.JUNE));
		map.put(3, List.of(Month.JULY, Month.AUGUST, Month.SEPTEMBER));
		map.put(4, List.of(Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER));
	});
	
	static <T extends Temporal & Comparable<T>, C extends Codec<T>> @NonNull MonthConstraint<T, C> of(@NotNull TemporalConstraint<T, C> parent) {
		return new MonthConstraint<>() {
			@Override
			public @NotNull TemporalConstraint<T, C> parent() {
				return parent;
			}
		};
	}
	
	@NotNull TemporalConstraint<T, C> parent();
	
	default @NotNull C equalTo(@NotNull Month month) {
		return null;
	}
	
	default @NotNull C notEqualTo(@NotNull Month month) {
		return null;
	}
	
	default @NotNull C in(Month @NotNull ... months) {
		return this.in(Set.of(months));
	}
	
	default @NotNull C in(@NotNull Set<Month> months) {
		return null;
	}
	
	default @NotNull C notIn(Month @NotNull ... months) {
		return this.notIn(Set.of(months));
	}
	
	default @NotNull C notIn(@NotNull Set<Month> months) {
		return null;
	}
	
	default @NotNull C quarter(int quarter) {
		return null;
	}
	
	default @NotNull C notQuarter(int quarter) {
		return null;
	}
	
	default @NotNull C quarterIn(int @NotNull ... quarters) {
		return this.quarterIn(new HashSet<>(Arrays.stream(quarters).boxed().toList()));
	}
	
	default @NotNull C quarterIn(@NotNull Set<Integer> quarters) {
		if (quarters.isEmpty()) {
			throw new IllegalArgumentException("Quarters set must not be empty");
		}
		
		Set<Month> months = EnumSet.noneOf(Month.class);
		for (int quarter : quarters) {
			List<Month> quarterMonths = MONTHS_BY_QUARTER.get(quarter);
			if (quarterMonths == null) {
				throw new IllegalArgumentException("Invalid quarter: " + quarter);
			}
			months.addAll(quarterMonths);
		}
		return this.in(months);
	}
	
	default @NotNull C firstHalf() {
		return this.quarterIn(1, 2);
	}
	
	default @NotNull C secondHalf() {
		return this.quarterIn(3, 4);
	}
}
