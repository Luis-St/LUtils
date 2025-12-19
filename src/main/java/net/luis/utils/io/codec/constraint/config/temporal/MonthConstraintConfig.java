package net.luis.utils.io.codec.constraint.config.temporal;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.time.Month;
import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

@SuppressWarnings("OptionalContainsCollection")
public record MonthConstraintConfig(
	@NotNull Optional<Month> equalsMonth,
	@NotNull Optional<Boolean> invertEquals,
	@NotNull Optional<Set<Month>> validMonths,
	@NotNull Optional<Set<Month>> invalidMonths
) {
	
	public MonthConstraintConfig {
		Objects.requireNonNull(equalsMonth, "Equals month must not be null");
		Objects.requireNonNull(invertEquals, "Invert equals must not be null");
		Objects.requireNonNull(validMonths, "Valid months set must not be null");
		Objects.requireNonNull(invalidMonths, "Invalid months set must not be null");
		
		if (equalsMonth.isPresent()) {
			Objects.requireNonNull(equalsMonth.get(), "Equals month must not be null when present");
			
			if (invertEquals.isEmpty()) {
				throw new IllegalArgumentException("Invert equals must be present when equals month is present");
			}
			Objects.requireNonNull(invertEquals.get(), "Invert equals must not be null when present");
		}
		
		if (validMonths.isPresent()) {
			Objects.requireNonNull(validMonths.get(), "Valid months set must not be null when present");
			
			for (Month month : validMonths.get()) {
				Objects.requireNonNull(month, "Valid months set cannot contain null months");
			}
		}
		
		if (invalidMonths.isPresent()) {
			Objects.requireNonNull(invalidMonths.get(), "Invalid months set must not be null when present");
			
			for (Month month : invalidMonths.get()) {
				Objects.requireNonNull(month, "Invalid months set cannot contain null months");
			}
		}
		
		if (validMonths.isPresent() && invalidMonths.isPresent()) {
			Set<Month> intersection = Sets.intersection(validMonths.get(), invalidMonths.get());
			
			if (!intersection.isEmpty()) {
				throw new IllegalArgumentException("Valid months and invalid months sets cannot contain the same months: " + intersection);
			}
		}
	}
}
