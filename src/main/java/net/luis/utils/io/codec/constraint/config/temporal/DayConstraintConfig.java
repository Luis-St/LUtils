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

package net.luis.utils.io.codec.constraint.config.temporal;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("OptionalContainsCollection")
public record DayConstraintConfig(
	@NotNull Optional<DayOfWeek> equalsDay,
	@NotNull Optional<Boolean> invertEquals,
	@NotNull Optional<Set<DayOfWeek>> validDays,
	@NotNull Optional<Set<DayOfWeek>> invalidDays
) {
	
	public DayConstraintConfig {
		Objects.requireNonNull(equalsDay, "Equals day must not be null");
		Objects.requireNonNull(invertEquals, "Invert equals must not be null");
		Objects.requireNonNull(validDays, "Valid days set must not be null");
		Objects.requireNonNull(invalidDays, "Invalid days set must not be null");
		
		if (equalsDay.isPresent()) {
			Objects.requireNonNull(equalsDay.get(), "Equals day must not be null when present");
			
			if (invertEquals.isEmpty()) {
				throw new IllegalArgumentException("Invert equals must be present when equals day is present");
			}
			Objects.requireNonNull(invertEquals.get(), "Invert equals must not be null when present");
		}
		
		if (validDays.isPresent()) {
			Objects.requireNonNull(validDays.get(), "Valid days set must not be null when present");
			
			for (DayOfWeek day : validDays.get()) {
				Objects.requireNonNull(day, "Valid days set cannot contain null days");
			}
		}
		
		if (invalidDays.isPresent()) {
			Objects.requireNonNull(invalidDays.get(), "Invalid days set must not be null when present");
			
			for (DayOfWeek day : invalidDays.get()) {
				Objects.requireNonNull(day, "Invalid days set cannot contain null days");
			}
		}
		
		if (validDays.isPresent() && invalidDays.isPresent()) {
			Set<DayOfWeek> intersection = Sets.intersection(validDays.get(), invalidDays.get());
			
			if (!intersection.isEmpty()) {
				throw new IllegalArgumentException("Valid days and invalid days sets cannot contain the same days: " + intersection);
			}
		}
	}
}
