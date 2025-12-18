package net.luis.utils.io.codec.constraint.config.temporal;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

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
	@NotNull Optional<Set<DayOfWeek>> invalidDays,
	@NotNull Optional<Boolean> weekday,
	@NotNull Optional<Boolean> weekend
) {
	
	public DayConstraintConfig {
		Objects.requireNonNull(equalsDay, "Equals day must not be null");
		Objects.requireNonNull(invertEquals, "Invert equals must not be null");
		Objects.requireNonNull(validDays, "Valid days set must not be null");
		Objects.requireNonNull(invalidDays, "Invalid days set must not be null");
		Objects.requireNonNull(weekday, "Weekday flag must not be null");
		Objects.requireNonNull(weekend, "Weekend flag must not be null");
		
		if (validDays.isPresent() && invalidDays.isPresent()) {
			Set<DayOfWeek> intersection = Sets.intersection(validDays.get(), invalidDays.get());
			
			if (!intersection.isEmpty()) {
				throw new IllegalArgumentException("Valid days and invalid days sets cannot contain the same days: " + intersection);
			}
		}
	}
}
