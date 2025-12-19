package net.luis.utils.io.codec.constraint.config.temporal;

import com.google.common.collect.Sets;
import net.luis.utils.io.codec.constraint.temporal.WeekType;
import net.luis.utils.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record WeekConstraintConfig(
	@NotNull Optional<Pair<WeekType, Integer>> equalsWeek,
	@NotNull Optional<Boolean> invertEquals,
	@NotNull Optional<Pair<WeekType, Set<Integer>>> validWeeks,
	@NotNull Optional<Pair<WeekType, Set<Integer>>> invalidWeeks
) {
	
	public WeekConstraintConfig {
		Objects.requireNonNull(equalsWeek, "Equals week must not be null");
		Objects.requireNonNull(invertEquals, "Invert equals must not be null");
		Objects.requireNonNull(validWeeks, "Valid weeks set must not be null");
		Objects.requireNonNull(invalidWeeks, "Invalid weeks set must not be null");
		
		if (equalsWeek.isPresent()) {
			Pair<WeekType, Integer> pair = equalsWeek.get();
			Objects.requireNonNull(pair, "Equals week must not be null when present");
			Objects.requireNonNull(pair.getFirst(), "Week type of equals week must not be null when present");
			Objects.requireNonNull(pair.getSecond(), "Week of equals week must not be null when present");
			
			if (invertEquals.isEmpty()) {
				throw new IllegalArgumentException("Invert equals must be present when equals week is present");
			}
			Objects.requireNonNull(invertEquals.get(), "Invert equals must not be null when present");
		}
		
		if (validWeeks.isPresent()) {
			Pair<WeekType, Set<Integer>> pair = validWeeks.get();
			Objects.requireNonNull(pair, "Valid weeks set must not be null when present");
			Objects.requireNonNull(pair.getFirst(), "Week type of valid weeks set must not be null when present");
			Objects.requireNonNull(pair.getSecond(), "Weeks set of valid weeks must not be null when present");
			
			for (Integer week : pair.getSecond()) {
				Objects.requireNonNull(week, "Valid weeks set cannot contain null weeks");
			}
		}
		
		if (invalidWeeks.isPresent()) {
			Pair<WeekType, Set<Integer>> pair = invalidWeeks.get();
			Objects.requireNonNull(pair, "Invalid weeks set must not be null when present");
			Objects.requireNonNull(pair.getFirst(), "Week type of invalid weeks set must not be null when present");
			Objects.requireNonNull(pair.getSecond(), "Weeks set of invalid weeks must not be null when present");
			
			for (Integer week : pair.getSecond()) {
				Objects.requireNonNull(week, "Invalid weeks set cannot contain null weeks");
			}
		}
		
		if (validWeeks.isPresent() && invalidWeeks.isPresent()) {
			Pair<WeekType, Set<Integer>> valid = validWeeks.get();
			Pair<WeekType, Set<Integer>> invalid = invalidWeeks.get();
			
			if (valid.getFirst() == invalid.getFirst()) {
				Set<Integer> intersection = Sets.intersection(valid.getSecond(), invalid.getSecond());
				
				if (!intersection.isEmpty()) {
					throw new IllegalArgumentException("Valid weeks and invalid weeks sets cannot contain the same weeks: " + intersection);
				}
			}
		}
	}
}
