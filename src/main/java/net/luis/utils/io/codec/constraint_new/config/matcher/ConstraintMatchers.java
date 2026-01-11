/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.codec.constraint_new.config.matcher;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.*;
import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class providing static methods for common constraint validation patterns.<br>
 * <p>
 *     This class contains reusable validation logic that is shared across multiple
 *     constraint configuration records. All methods return {@link Result Result&lt;Void&gt;}
 *     indicating success or failure of the validation.
 * </p>
 * <p>
 *     The validation methods use early-exit behavior through the {@link #allOf(Supplier[])} method,
 *     which stops at the first failed constraint for better performance.
 * </p>
 *
 * @author Luis-St
 */
@SuppressWarnings("OptionalContainsCollection")
public final class ConstraintMatchers {
	
	/**
	 * Private constructor to prevent instantiation of this utility class.<br>
	 */
	private ConstraintMatchers() {}
	
	/**
	 * Formats a set for display in error messages.<br>
	 *
	 * @param set The set to format
	 * @param <T> The type of elements in the set
	 * @return A formatted string representation of the set
	 * @throws NullPointerException If any parameter is null
	 */
	private static <T> @NonNull String formatSet(@NonNull Set<T> set) {
		Objects.requireNonNull(set, "Set must not be null");
		return "[" + set.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}
	
	/**
	 * Combines multiple constraint checks with early-exit behavior.<br>
	 * <p>
	 *     Evaluates each supplier in order and returns immediately when a failure is encountered.
	 *     This provides better performance than collecting all results when most validations pass.
	 * </p>
	 *
	 * @param checks The constraint check suppliers to evaluate
	 * @return A successful result if all checks pass, or the first failed result
	 * @throws NullPointerException If any parameter is null
	 */
	@SafeVarargs
	public static @NonNull Result<Void> allOf(Supplier<Result<Void>> @NonNull ... checks) {
		Objects.requireNonNull(checks, "Checks must not be null");
		
		for (Supplier<Result<Void>> check : checks) {
			Result<Void> result = check.get();
			if (result.isError()) {
				return result;
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates an equality constraint using {@link Objects#equals(Object, Object)}.<br>
	 *
	 * @param value The value to validate
	 * @param equalTo The equality constraint as a pair of (expected value, negated)
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T> @NonNull Result<Void> matchEqualTo(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> equalTo) {
		return matchEqualTo(value, equalTo, Objects::equals);
	}
	
	/**
	 * Validates an equality constraint using a custom equality predicate.<br>
	 *
	 * @param value The value to validate
	 * @param equalTo The equality constraint as a pair of (expected value, negated)
	 * @param equalityTest The predicate to test equality between two values
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T> @NonNull Result<Void> matchEqualTo(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> equalTo, @NonNull BiPredicate<T, T> equalityTest) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(equalTo, "Equal to constraint must not be null");
		Objects.requireNonNull(equalityTest, "Equality test must not be null");
		if (equalTo.isEmpty()) {
			return Result.success();
		}
		
		T expected = equalTo.get().getFirst();
		boolean negated = equalTo.get().getSecond();
		boolean equals = equalityTest.test(value, expected);
		if (negated) {
			if (equals) {
				return Result.error("Value '" + value + "' must not be equal to '" + expected + "'");
			}
		} else {
			if (!equals) {
				return Result.error("Value '" + value + "' must be equal to '" + expected + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a set membership constraint using {@link Set#contains(Object)}.<br>
	 *
	 * @param value The value to validate
	 * @param in The membership constraint as a pair of (set of values, negated)
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> @NonNull Result<Void> matchIn(@NonNull T value, @NonNull Optional<Pair<Set<T>, Boolean>> in) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(in, "In constraint must not be null");
		if (in.isEmpty()) {
			return Result.success();
		}
		
		Set<T> set = in.get().getFirst();
		boolean negated = in.get().getSecond();
		boolean contains = set.contains(value);
		if (negated) {
			if (contains) {
				return Result.error("Value '" + value + "' must not be in " + formatSet(set));
			}
		} else {
			if (!contains) {
				return Result.error("Value '" + value + "' must be in " + formatSet(set));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a set membership constraint using a custom containment predicate.<br>
	 * <p>
	 *     This overload is useful for case-insensitive string comparisons or other
	 *     custom equality semantics.
	 * </p>
	 *
	 * @param value The value to validate
	 * @param in The membership constraint as a pair of (set of values, negated)
	 * @param containsTest The predicate to test if a set element matches the value
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> @NonNull Result<Void> matchIn(@NonNull T value, @NonNull Optional<Pair<Set<T>, Boolean>> in, @NonNull BiPredicate<T, T> containsTest) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(in, "In constraint must not be null");
		Objects.requireNonNull(containsTest, "Contains test must not be null");
		
		if (in.isEmpty()) {
			return Result.success();
		}
		
		Set<T> set = in.get().getFirst();
		boolean negated = in.get().getSecond();
		boolean contains = set.stream().anyMatch(element -> containsTest.test(value, element));
		
		if (negated) {
			if (contains) {
				return Result.error("Value '" + value + "' must not be in " + formatSet(set));
			}
		} else {
			if (!contains) {
				return Result.error("Value '" + value + "' must be in " + formatSet(set));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a custom constraint.<br>
	 *
	 * @param value The value to validate
	 * @param custom The optional custom constraint
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T> @NonNull Result<Void> matchCustom(@NonNull T value, @NonNull Optional<Constraint<T>> custom) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(custom, "Custom constraint must not be null");
		
		return custom.map(constraint -> constraint.validate(value)).orElseGet(Result::success);
	}
	
	/**
	 * Validates min/max range constraints for comparable types.<br>
	 *
	 * @param value The value to validate
	 * @param min The minimum bound constraint as a pair of (bound value, inclusive)
	 * @param max The maximum bound constraint as a pair of (bound value, inclusive)
	 * @param <T> The type of the value (must be Comparable)
	 * @return A successful result if the constraints are satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T extends Comparable<T>> @NonNull Result<Void> matchRange(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> min, @NonNull Optional<Pair<T, Boolean>> max) {
		return matchRange(value, min, max, Comparator.naturalOrder());
	}
	
	/**
	 * Validates min/max range constraints using a custom comparator.<br>
	 *
	 * @param value The value to validate
	 * @param min The minimum bound constraint as a pair of (bound value, inclusive)
	 * @param max The maximum bound constraint as a pair of (bound value, inclusive)
	 * @param comparator The comparator to use for comparisons
	 * @param <T> The type of the value
	 * @return A successful result if the constraints are satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T> @NonNull Result<Void> matchRange(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> min, @NonNull Optional<Pair<T, Boolean>> max, @NonNull Comparator<T> comparator) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		Objects.requireNonNull(comparator, "Comparator must not be null");
		
		if (min.isPresent()) {
			T minValue = min.get().getFirst();
			boolean inclusive = min.get().getSecond();
			int cmp = comparator.compare(value, minValue);
			
			if (inclusive) {
				if (cmp < 0) {
					return Result.error("Value '" + value + "' must be greater than or equal to " + minValue);
				}
			} else {
				if (cmp <= 0) {
					return Result.error("Value '" + value + "' must be greater than " + minValue);
				}
			}
		}
		
		if (max.isPresent()) {
			T maxValue = max.get().getFirst();
			boolean inclusive = max.get().getSecond();
			int cmp = comparator.compare(value, maxValue);
			
			if (inclusive) {
				if (cmp > 0) {
					return Result.error("Value '" + value + "' must be less than or equal to " + maxValue);
				}
			} else {
				if (cmp >= 0) {
					return Result.error("Value '" + value + "' must be less than " + maxValue);
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a flag constraint with a predicate.<br>
	 * <p>
	 *     If the flag is present (has a {@link Unit} value), the predicate is tested.
	 *     If the predicate returns false, an error is returned with the provided message.
	 * </p>
	 *
	 * @param value The value to validate
	 * @param flag The optional flag (present means the constraint is active)
	 * @param condition The predicate that must be true for the constraint to pass
	 * @param errorMessage The error message if the constraint fails
	 * @param <T> The type of the value
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T> @NonNull Result<Void> matchFlag(@NonNull T value, @NonNull Optional<Unit> flag, @NonNull Predicate<T> condition, @NonNull String errorMessage) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(flag, "Flag must not be null");
		Objects.requireNonNull(condition, "Condition must not be null");
		Objects.requireNonNull(errorMessage, "Error message must not be null");
		if (flag.isEmpty()) {
			return Result.success();
		}
		
		if (!condition.test(value)) {
			return Result.error(errorMessage);
		}
		return Result.success();
	}
	
	/**
	 * Validates sign constraints for numeric values.<br>
	 * <p>
	 *     The boolean values in the optional fields have the following meanings:
	 *     <ul>
	 *         <li>positive: false = must be positive, true = must be non-positive</li>
	 *         <li>negative: false = must be negative, true = must be non-negative</li>
	 *         <li>zero: false = must be zero, true = must be non-zero</li>
	 *     </ul>
	 * </p>
	 *
	 * @param value The numeric value to validate
	 * @param positive The positive/non-positive constraint
	 * @param negative The negative/non-negative constraint
	 * @param zero The zero/non-zero constraint
	 * @return A successful result if the constraints are satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchSign(@NonNull Number value, @NonNull Optional<Boolean> positive, @NonNull Optional<Boolean> negative, @NonNull Optional<Boolean> zero) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(positive, "Positive constraint must not be null");
		Objects.requireNonNull(negative, "Negative constraint must not be null");
		Objects.requireNonNull(zero, "Zero constraint must not be null");
		
		double doubleValue = value.doubleValue();
		if (positive.isPresent()) {
			boolean nonPositive = positive.get();
			if (nonPositive) {
				if (doubleValue > 0) {
					return Result.error("Value '" + value + "' must be non-positive (less than or equal to zero)");
				}
			} else {
				if (doubleValue <= 0) {
					return Result.error("Value '" + value + "' must be positive (greater than zero)");
				}
			}
		}
		
		if (negative.isPresent()) {
			boolean nonNegative = negative.get();
			if (nonNegative) {
				if (doubleValue < 0) {
					return Result.error("Value '" + value + "' must be non-negative (greater than or equal to zero)");
				}
			} else {
				if (doubleValue >= 0) {
					return Result.error("Value '" + value + "' must be negative (less than zero)");
				}
			}
		}
		
		if (zero.isPresent()) {
			boolean nonZero = zero.get();
			if (nonZero) {
				if (doubleValue == 0) {
					return Result.error("Value '" + value + "' must be non-zero");
				}
			} else {
				if (doubleValue != 0) {
					return Result.error("Value '" + value + "' must be zero");
				}
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates percentage constraint (value between 0 and 100 inclusive).<br>
	 *
	 * @param value The numeric value to validate
	 * @param percentage The percentage constraint flag
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPercentage(@NonNull Number value, @NonNull Optional<Unit> percentage) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(percentage, "Percentage constraint must not be null");
		if (percentage.isEmpty()) {
			return Result.success();
		}
		
		double doubleValue = value.doubleValue();
		if (doubleValue < 0 || doubleValue > 100) {
			return Result.error("Value '" + value + "' must be a percentage (between 0 and 100 inclusive)");
		}
		return Result.success();
	}
	
	/**
	 * Validates parity constraints (even/odd).<br>
	 *
	 * @param value The integer value to validate
	 * @param even The even constraint flag
	 * @param odd The odd constraint flag
	 * @return A successful result if the constraints are satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchParity(long value, @NonNull Optional<Unit> even, @NonNull Optional<Unit> odd) {
		Objects.requireNonNull(even, "Even constraint must not be null");
		Objects.requireNonNull(odd, "Odd constraint must not be null");
		if (even.isPresent() && value % 2 != 0) {
			return Result.error("Value '" + value + "' must be even");
		}
		
		if (odd.isPresent() && value % 2 == 0) {
			return Result.error("Value '" + value + "' must be odd");
		}
		return Result.success();
	}
	
	/**
	 * Validates divisibility constraint.<br>
	 *
	 * @param value The integer value to validate
	 * @param divisibleBy The divisor that the value must be divisible by
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchDivisibleBy(long value, @NonNull Optional<Long> divisibleBy) {
		Objects.requireNonNull(divisibleBy, "DivisibleBy constraint must not be null");
		if (divisibleBy.isEmpty()) {
			return Result.success();
		}
		
		long divisor = divisibleBy.get();
		if (value % divisor != 0) {
			return Result.error("Value '" + value + "' must be divisible by " + divisor);
		}
		return Result.success();
	}
	
	/**
	 * Validates power-of constraint.<br>
	 *
	 * @param value The integer value to validate
	 * @param powerOf The base that the value must be a power of
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPowerOf(long value, @NonNull Optional<Integer> powerOf) {
		Objects.requireNonNull(powerOf, "Power of constraint must not be null");
		if (powerOf.isEmpty()) {
			return Result.success();
		}
		
		int base = powerOf.get();
		if (value <= 0) {
			return Result.error("Value '" + value + "' must be a power of " + base + " (must be positive)");
		}
		
		long temp = value;
		while (temp > 1) {
			if (temp % base != 0) {
				return Result.error("Value '" + value + "' must be a power of " + base);
			}
			temp /= base;
		}
		return Result.success();
	}
	
	/**
	 * Validates a prefix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param startsWith The prefix constraint as a pair of (prefix, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchStartsWith(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> startsWith) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(startsWith, "Starts with constraint must not be null");
		if (startsWith.isEmpty()) {
			return Result.success();
		}
		
		String prefix = startsWith.get().getFirst();
		boolean negated = startsWith.get().getSecond();
		boolean matches = value.startsWith(prefix);
		if (negated) {
			if (matches) {
				return Result.error("Value '" + value + "' must not start with '" + prefix + "'");
			}
		} else {
			if (!matches) {
				return Result.error("Value '" + value + "' must start with '" + prefix + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a multi-prefix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param startsWithAny The prefix set constraint as a pair of (prefixes, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchStartsWithAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> startsWithAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(startsWithAny, "Starts with any constraint must not be null");
		if (startsWithAny.isEmpty()) {
			return Result.success();
		}
		
		Set<String> prefixes = startsWithAny.get().getFirst();
		boolean negated = startsWithAny.get().getSecond();
		boolean matchesAny = prefixes.stream().anyMatch(value::startsWith);
		if (negated) {
			if (matchesAny) {
				return Result.error("Value '" + value + "' must not start with any of " + formatSet(prefixes));
			}
		} else {
			if (!matchesAny) {
				return Result.error("Value '" + value + "' must start with one of " + formatSet(prefixes));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param contains The containment constraint as a pair of (substring, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchContains(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> contains) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(contains, "Contains constraint must not be null");
		if (contains.isEmpty()) {
			return Result.success();
		}
		
		String substring = contains.get().getFirst();
		boolean negated = contains.get().getSecond();
		boolean matches = value.contains(substring);
		if (negated) {
			if (matches) {
				return Result.error("Value '" + value + "' must not contain '" + substring + "'");
			}
		} else {
			if (!matches) {
				return Result.error("Value '" + value + "' must contain '" + substring + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a multi-substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param containsAny The containment set constraint as a pair of (substrings, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchContainsAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> containsAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsAny, "Contains any constraint must not be null");
		if (containsAny.isEmpty()) {
			return Result.success();
		}
		
		Set<String> substrings = containsAny.get().getFirst();
		boolean negated = containsAny.get().getSecond();
		boolean matchesAny = substrings.stream().anyMatch(value::contains);
		if (negated) {
			if (matchesAny) {
				return Result.error("Value '" + value + "' must not contain any of " + formatSet(substrings));
			}
		} else {
			if (!matchesAny) {
				return Result.error("Value '" + value + "' must contain at least one of " + formatSet(substrings));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates an all-substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param containsAll The set of substrings that must all be contained
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchContainsAll(@NonNull String value, @NonNull Optional<Set<String>> containsAll) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsAll, "Contains all constraint must not be null");
		if (containsAll.isEmpty()) {
			return Result.success();
		}
		
		Set<String> substrings = containsAll.get();
		Set<String> missing = substrings.stream().filter(s -> !value.contains(s)).collect(Collectors.toSet());
		if (!missing.isEmpty()) {
			return Result.error("Value '" + value + "' must contain all of " + formatSet(substrings) + ", missing: " + formatSet(missing));
		}
		return Result.success();
	}
	
	/**
	 * Validates an only-substring containment constraint.<br>
	 * <p>
	 *     The value must only contain characters that appear in at least one of the allowed substrings.
	 * </p>
	 *
	 * @param value The string value to validate
	 * @param containsOnly The set of allowed substrings/characters
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchContainsOnly(@NonNull String value, @NonNull Optional<Set<String>> containsOnly) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsOnly, "Contains only constraint must not be null");
		if (containsOnly.isEmpty()) {
			return Result.success();
		}
		
		Set<String> allowed = containsOnly.get();
		String allowedChars = String.join("", allowed);
		for (char c : value.toCharArray()) {
			if (allowedChars.indexOf(c) == -1) {
				return Result.error("Value '" + value + "' must contain only characters from " + formatSet(allowed));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a suffix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param endsWith The suffix constraint as a pair of (suffix, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchEndsWith(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> endsWith) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(endsWith, "Ends with constraint must not be null");
		if (endsWith.isEmpty()) {
			return Result.success();
		}
		
		String suffix = endsWith.get().getFirst();
		boolean negated = endsWith.get().getSecond();
		boolean matches = value.endsWith(suffix);
		if (negated) {
			if (matches) {
				return Result.error("Value '" + value + "' must not end with '" + suffix + "'");
			}
		} else {
			if (!matches) {
				return Result.error("Value '" + value + "' must end with '" + suffix + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a multi-suffix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param endsWithAny The suffix set constraint as a pair of (suffixes, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchEndsWithAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> endsWithAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(endsWithAny, "Ends with any constraint must not be null");
		if (endsWithAny.isEmpty()) {
			return Result.success();
		}
		
		Set<String> suffixes = endsWithAny.get().getFirst();
		boolean negated = endsWithAny.get().getSecond();
		boolean matchesAny = suffixes.stream().anyMatch(value::endsWith);
		if (negated) {
			if (matchesAny) {
				return Result.error("Value '" + value + "' must not end with any of " + formatSet(suffixes));
			}
		} else {
			if (!matchesAny) {
				return Result.error("Value '" + value + "' must end with one of " + formatSet(suffixes));
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a regex pattern constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param matches The pattern constraint as a pair of (pattern, negated)
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchPattern(@NonNull String value, @NonNull Optional<Pair<Pattern, Boolean>> matches) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(matches, "Matches constraint must not be null");
		if (matches.isEmpty()) {
			return Result.success();
		}
		
		Pattern pattern = matches.get().getFirst();
		boolean negated = matches.get().getSecond();
		boolean patternMatches = pattern.matcher(value).matches();
		if (negated) {
			if (patternMatches) {
				return Result.error("Value '" + value + "' must not match pattern '" + pattern.pattern() + "'");
			}
		} else {
			if (!patternMatches) {
				return Result.error("Value '" + value + "' must match pattern '" + pattern.pattern() + "'");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates a character classification constraint for all characters in a string.<br>
	 *
	 * @param value The string value to validate
	 * @param flag The optional flag (present means the constraint is active)
	 * @param classifier The predicate that each character must satisfy
	 * @param className The name of the character class for error messages
	 * @return A successful result if the constraint is satisfied or not present
	 * @throws NullPointerException If any parameter is null
	 */
	public static @NonNull Result<Void> matchCharacterClass(@NonNull String value, @NonNull Optional<Unit> flag, @NonNull Predicate<Character> classifier, @NonNull String className) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(flag, "Flag must not be null");
		Objects.requireNonNull(classifier, "Classifier must not be null");
		Objects.requireNonNull(className, "Class name must not be null");
		if (flag.isEmpty()) {
			return Result.success();
		}
		
		for (char c : value.toCharArray()) {
			if (!classifier.test(c)) {
				return Result.error("Value '" + value + "' must contain only " + className + " characters");
			}
		}
		return Result.success();
	}
	
	/**
	 * Validates that all required keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param requiredKeys The optional set of required keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @return A successful result if all required keys are present or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> @NonNull Result<Void> matchRequiredKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> requiredKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(requiredKeys, "Required keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (requiredKeys.isEmpty()) {
			return Result.success();
		}
		
		Set<K> required = requiredKeys.get();
		Set<K> missing = new HashSet<>(required);
		missing.removeAll(actualKeys);
		if (!missing.isEmpty()) {
			return Result.error(typeName + " must contain required keys: " + formatSet(missing));
		}
		return Result.success();
	}
	
	/**
	 * Validates that no forbidden keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param forbiddenKeys The optional set of forbidden keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @return A successful result if no forbidden keys are present or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> @NonNull Result<Void> matchForbiddenKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> forbiddenKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(forbiddenKeys, "Forbidden keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (forbiddenKeys.isEmpty()) {
			return Result.success();
		}
		
		Set<K> forbidden = forbiddenKeys.get();
		Set<K> present = new HashSet<>(actualKeys);
		present.retainAll(forbidden);
		if (!present.isEmpty()) {
			return Result.error(typeName + " must not contain forbidden keys: " + formatSet(present));
		}
		return Result.success();
	}
	
	/**
	 * Validates that only allowed keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param allowedKeys The optional set of allowed keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @return A successful result if all keys are in the allowed set or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> @NonNull Result<Void> matchAllowedKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> allowedKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(allowedKeys, "Allowed keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (allowedKeys.isEmpty()) {
			return Result.success();
		}
		
		Set<K> allowed = allowedKeys.get();
		Set<K> disallowed = new HashSet<>(actualKeys);
		disallowed.removeAll(allowed);
		if (!disallowed.isEmpty()) {
			return Result.error(typeName + " contains keys that are not allowed: " + formatSet(disallowed));
		}
		return Result.success();
	}
	
	/**
	 * Validates a value against a nested constraint configuration.<br>
	 * <p>
	 *     This method delegates validation to another {@link ConstraintConfig} and
	 *     wraps any error messages with the field name for context.
	 * </p>
	 *
	 * @param value The value to validate
	 * @param config The optional nested constraint configuration
	 * @param fieldName The name of the field for error messages
	 * @param <T> The type of the value
	 * @return A successful result if validation passes or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> @NonNull Result<Void> matchNestedConfig(@NonNull T value, @NonNull Optional<? extends ConstraintConfig<T>> config, @NonNull String fieldName) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(config, "Config constraint must not be null");
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (config.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = config.get().matches(value);
		if (result.isError()) {
			return Result.error(fieldName + " constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates an enum value against a nested enum constraint configuration.<br>
	 * <p>
	 *     This is a convenience method that delegates to {@link #matchNestedConfig(Object, Optional, String)}.
	 * </p>
	 *
	 * @param value The enum value to validate
	 * @param config The optional enum constraint configuration
	 * @param fieldName The name of the field for error messages
	 * @param <E> The enum type
	 * @return A successful result if validation passes or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	public static <E extends Enum<E>> @NonNull Result<Void> matchEnumField(@NonNull E value, @NonNull Optional<EnumConstraintConfig<E>> config, @NonNull String fieldName) {
		return matchNestedConfig(value, config, fieldName);
	}
	
	/**
	 * Validates an integer value against a numeric field constraint configuration.<br>
	 * <p>
	 *     This is a convenience method that delegates to {@link #matchNestedConfig(Object, Optional, String)}.
	 * </p>
	 *
	 * @param value The integer value to validate
	 * @param config The optional numeric field constraint configuration
	 * @param fieldName The name of the field for error messages
	 * @return A successful result if validation passes or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	@SuppressWarnings("DuplicatedCode")
	public static @NonNull Result<Void> matchNumericField(int value, @NonNull Optional<NumericFieldConstraintConfig> config, @NonNull String fieldName) {
		Objects.requireNonNull(config, "Config constraint must not be null");
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (config.isEmpty()) {
			return Result.success();
		}
		
		Result<Void> result = config.get().matches(value);
		if (result.isError()) {
			return Result.error("Field '" + fieldName + "' constraint failed: " + result.errorOrThrow());
		}
		return Result.success();
	}
	
	/**
	 * Validates that a temporal value is within the last specified duration from now.<br>
	 * <p>
	 *     The threshold is calculated as {@code nowSupplier.get() - withinLast}.<br>
	 *     The value must be after or equal to this threshold.
	 * </p>
	 *
	 * @param value The temporal value to validate
	 * @param withinLast The optional duration constraint
	 * @param nowSupplier Supplier for the current time
	 * @param subtractor Function to subtract duration from a temporal value
	 * @param typeName The name of the type for error messages (e.g., "Instant", "LocalDateTime")
	 * @param <T> The type of the temporal value (must be Temporal and Comparable)
	 * @return A successful result if the value is within the last duration or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T extends Temporal & Comparable<? super T>> @NonNull Result<Void> matchWithinLast(
		@NonNull T value, @NonNull Optional<Duration> withinLast, @NonNull Supplier<T> nowSupplier, @NonNull BiFunction<T, Duration, T> subtractor, @NonNull String typeName
	) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(withinLast, "Within last constraint must not be null");
		Objects.requireNonNull(nowSupplier, "Now supplier must not be null");
		Objects.requireNonNull(subtractor, "Subtractor must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (withinLast.isEmpty()) {
			return Result.success();
		}
		
		Duration duration = withinLast.get();
		T threshold = subtractor.apply(nowSupplier.get(), duration);
		if (value.compareTo(threshold) < 0) {
			return Result.error(typeName + " '" + value + "' must be within last " + duration);
		}
		return Result.success();
	}
	
	/**
	 * Validates that a temporal value is within the next specified duration from now.<br>
	 * <p>
	 *     The threshold is calculated as {@code nowSupplier.get() + withinNext}.<br>
	 *     The value must be before or equal to this threshold.
	 * </p>
	 *
	 * @param value The temporal value to validate
	 * @param withinNext The optional duration constraint
	 * @param nowSupplier Supplier for the current time
	 * @param adder Function to add duration to a temporal value
	 * @param typeName The name of the type for error messages (e.g., "Instant", "LocalDateTime")
	 * @param <T> The type of the temporal value (must be Temporal and Comparable)
	 * @return A successful result if the value is within the next duration or no constraint is set
	 * @throws NullPointerException If any parameter is null
	 */
	public static <T extends Temporal & Comparable<? super T>> @NonNull Result<Void> matchWithinNext(
		@NonNull T value, @NonNull Optional<Duration> withinNext, @NonNull Supplier<T> nowSupplier, @NonNull BiFunction<T, Duration, T> adder, @NonNull String typeName
	) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(withinNext, "Within next constraint must not be null");
		Objects.requireNonNull(nowSupplier, "Now supplier must not be null");
		Objects.requireNonNull(adder, "Adder must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (withinNext.isEmpty()) {
			return Result.success();
		}
		
		Duration duration = withinNext.get();
		T threshold = adder.apply(nowSupplier.get(), duration);
		if (value.compareTo(threshold) > 0) {
			return Result.error(typeName + " '" + value + "' must be within next " + duration);
		}
		return Result.success();
	}
}
