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
