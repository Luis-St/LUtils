package net.luis.utils.io.codec.constraint.config.temporal;

import org.jetbrains.annotations.NotNull;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Optional;
import java.util.Set;

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
	@NotNull Optional<Set<Month>> invalidMonths,
	@NotNull Optional<Integer> quarter,
	@NotNull Optional<Boolean> invertQuarter,
	@NotNull Optional<Set<Integer>> validQuarters,
	@NotNull Optional<Set<Integer>> invalidQuarters,
	@NotNull Optional<Boolean> firstHalf,
	@NotNull Optional<Boolean> secondHalf
) {}
