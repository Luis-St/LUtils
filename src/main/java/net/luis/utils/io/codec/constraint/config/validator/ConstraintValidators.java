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

package net.luis.utils.io.codec.constraint.config.validator;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
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
 *     This class contains reusable validation logic that is shared across multiple constraint configuration records.
 *     All methods return {@link Result Result&lt;Void&gt;} indicating success or failure of the validation.
 * </p>
 * <p>
 *     The validation methods use early-exit behavior through the {@link #validateAll(Runnable[])} method,
 *     which stops at the first failed constraint for better performance.
 * </p>
 *
 * @author Luis-St
 */
@SuppressWarnings("OptionalContainsCollection")
public final class ConstraintValidators {
	
	/**
	 * Private constructor to prevent instantiation of this utility class.<br>
	 */
	private ConstraintValidators() {}
	
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
	 *     Evaluates each runnable in order and returns immediately when a failure is encountered.<br>
	 *     This provides better performance than collecting all results when most validations pass.
	 * </p>
	 *
	 * @param checks The constraint check runnable to evaluate
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any constraint is violated
	 */
	public static void validateAll(Runnable @NonNull ... checks) {
		Objects.requireNonNull(checks, "Checks must not be null");
		
		for (Runnable check : checks) {
			check.run();
		}
	}
	
	/**
	 * Validates an equality constraint using {@link Objects#equals(Object, Object)}.<br>
	 *
	 * @param value The value to validate
	 * @param equalTo The equality constraint as a pair of (expected value, negated)
	 * @param <T> The type of the value
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T> void validateEqualTo(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> equalTo) {
		validateEqualTo(value, equalTo, Objects::equals);
	}
	
	/**
	 * Validates an equality constraint using a custom equality predicate.<br>
	 *
	 * @param value The value to validate
	 * @param equalTo The equality constraint as a pair of (expected value, negated)
	 * @param equalityTest The predicate to test equality between two values
	 * @param <T> The type of the value
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T> void validateEqualTo(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> equalTo, @NonNull BiPredicate<T, T> equalityTest) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(equalTo, "Equal to constraint must not be null");
		Objects.requireNonNull(equalityTest, "Equality test must not be null");
		if (equalTo.isEmpty()) {
			return;
		}
		
		T expected = equalTo.get().getFirst();
		boolean negated = equalTo.get().getSecond();
		boolean equals = equalityTest.test(value, expected);
		if (negated) {
			if (equals) {
				throw new ConstraintViolateException("Value '" + value + "' must not be equal to '" + expected + "'");
			}
		} else {
			if (!equals) {
				throw new ConstraintViolateException("Value '" + value + "' must be equal to '" + expected + "'");
			}
		}
	}
	
	/**
	 * Validates a set membership constraint using {@link Set#contains(Object)}.<br>
	 *
	 * @param value The value to validate
	 * @param in The membership constraint as a pair of (set of values, negated)
	 * @param <T> The type of the value
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> void validateIn(@NonNull T value, @NonNull Optional<Pair<Set<T>, Boolean>> in) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(in, "In constraint must not be null");
		if (in.isEmpty()) {
			return;
		}
		
		Set<T> set = in.get().getFirst();
		boolean negated = in.get().getSecond();
		boolean contains = set.contains(value);
		if (negated) {
			if (contains) {
				throw new ConstraintViolateException("Value '" + value + "' must not be in " + formatSet(set));
			}
		} else {
			if (!contains) {
				throw new ConstraintViolateException("Value '" + value + "' must be in " + formatSet(set));
			}
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> void validateIn(@NonNull T value, @NonNull Optional<Pair<Set<T>, Boolean>> in, @NonNull BiPredicate<T, T> containsTest) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(in, "In constraint must not be null");
		Objects.requireNonNull(containsTest, "Contains test must not be null");
		
		if (in.isEmpty()) {
			return;
		}
		
		Set<T> set = in.get().getFirst();
		boolean negated = in.get().getSecond();
		boolean contains = set.stream().anyMatch(element -> containsTest.test(value, element));
		
		if (negated) {
			if (contains) {
				throw new ConstraintViolateException("Value '" + value + "' must not be in " + formatSet(set));
			}
		} else {
			if (!contains) {
				throw new ConstraintViolateException("Value '" + value + "' must be in " + formatSet(set));
			}
		}
	}
	
	/**
	 * Validates a custom constraint.<br>
	 *
	 * @param value The value to validate
	 * @param custom The optional custom constraint
	 * @param <T> The type of the value
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T> void validateCustom(@NonNull T value, @NonNull Optional<Constraint<T>> custom) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(custom, "Custom constraint must not be null");
		
		custom.map(constraint -> constraint.validate(value)).orElseGet(Result::success);
	}
	
	/**
	 * Validates min/max range constraints for comparable types.<br>
	 *
	 * @param value The value to validate
	 * @param min The minimum bound constraint as a pair of (bound value, inclusive)
	 * @param max The maximum bound constraint as a pair of (bound value, inclusive)
	 * @param <T> The type of the value (must be Comparable)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraints are violated
	 */
	public static <T extends Comparable<T>> void validateRange(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> min, @NonNull Optional<Pair<T, Boolean>> max) {
		validateRange(value, min, max, Comparator.naturalOrder());
	}
	
	/**
	 * Validates min/max range constraints using a custom comparator.<br>
	 *
	 * @param value The value to validate
	 * @param min The minimum bound constraint as a pair of (bound value, inclusive)
	 * @param max The maximum bound constraint as a pair of (bound value, inclusive)
	 * @param comparator The comparator to use for comparisons
	 * @param <T> The type of the value
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraints are violated
	 */
	public static <T> void validateRange(@NonNull T value, @NonNull Optional<Pair<T, Boolean>> min, @NonNull Optional<Pair<T, Boolean>> max, @NonNull Comparator<T> comparator) {
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
					throw new ConstraintViolateException("Value '" + value + "' must be greater than or equal to " + minValue);
				}
			} else {
				if (cmp <= 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be greater than " + minValue);
				}
			}
		}
		
		if (max.isPresent()) {
			T maxValue = max.get().getFirst();
			boolean inclusive = max.get().getSecond();
			int cmp = comparator.compare(value, maxValue);
			
			if (inclusive) {
				if (cmp > 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be less than or equal to " + maxValue);
				}
			} else {
				if (cmp >= 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be less than " + maxValue);
				}
			}
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T> void validateFlag(@NonNull T value, @NonNull Optional<Unit> flag, @NonNull Predicate<T> condition, @NonNull String errorMessage) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(flag, "Flag must not be null");
		Objects.requireNonNull(condition, "Condition must not be null");
		Objects.requireNonNull(errorMessage, "Error message must not be null");
		if (flag.isEmpty()) {
			return;
		}
		
		if (!condition.test(value)) {
			throw new ConstraintViolateException(errorMessage);
		}
	}
	
	/**
	 * Validates sign constraints for numeric values.<br>
	 * <p>
	 *     The boolean values in the optional fields have the following meanings:
	 * </p>
	 * <ul>
	 *     <li>positive: false = must be positive, true = must be non-positive</li>
	 *     <li>negative: false = must be negative, true = must be non-negative</li>
	 *     <li>zero: false = must be zero, true = must be non-zero</li>
	 * </ul>
	 *
	 * @param value The numeric value to validate
	 * @param positive The positive/non-positive constraint
	 * @param negative The negative/non-negative constraint
	 * @param zero The zero/non-zero constraint
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any constraint is violated
	 */
	public static void validateSign(@NonNull Number value, @NonNull Optional<Boolean> positive, @NonNull Optional<Boolean> negative, @NonNull Optional<Boolean> zero) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(positive, "Positive constraint must not be null");
		Objects.requireNonNull(negative, "Negative constraint must not be null");
		Objects.requireNonNull(zero, "Zero constraint must not be null");
		
		double doubleValue = value.doubleValue();
		if (positive.isPresent()) {
			boolean nonPositive = positive.get();
			if (nonPositive) {
				if (doubleValue > 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be non-positive (less than or equal to zero)");
				}
			} else {
				if (doubleValue <= 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be positive (greater than zero)");
				}
			}
		}
		
		if (negative.isPresent()) {
			boolean nonNegative = negative.get();
			if (nonNegative) {
				if (doubleValue < 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be non-negative (greater than or equal to zero)");
				}
			} else {
				if (doubleValue >= 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be negative (less than zero)");
				}
			}
		}
		
		if (zero.isPresent()) {
			boolean nonZero = zero.get();
			if (nonZero) {
				if (doubleValue == 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be non-zero");
				}
			} else {
				if (doubleValue != 0) {
					throw new ConstraintViolateException("Value '" + value + "' must be zero");
				}
			}
		}
	}
	
	/**
	 * Validates percentage constraint (value between 0 and 100 inclusive).<br>
	 *
	 * @param value The numeric value to validate
	 * @param percentage The percentage constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validatePercentage(@NonNull Number value, @NonNull Optional<Unit> percentage) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(percentage, "Percentage constraint must not be null");
		if (percentage.isEmpty()) {
			return;
		}
		
		double doubleValue = value.doubleValue();
		if (doubleValue < 0 || doubleValue > 100) {
			throw new ConstraintViolateException("Value '" + value + "' must be a percentage (between 0 and 100 inclusive)");
		}
	}
	
	/**
	 * Validates parity constraints (even/odd).<br>
	 *
	 * @param value The integer value to validate
	 * @param even The even constraint flag
	 * @param odd The odd constraint flag
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any constraint is violated
	 */
	public static void validateParity(long value, @NonNull Optional<Unit> even, @NonNull Optional<Unit> odd) {
		Objects.requireNonNull(even, "Even constraint must not be null");
		Objects.requireNonNull(odd, "Odd constraint must not be null");
		if (even.isPresent() && value % 2 != 0) {
			throw new ConstraintViolateException("Value '" + value + "' must be even");
		}
		
		if (odd.isPresent() && value % 2 == 0) {
			throw new ConstraintViolateException("Value '" + value + "' must be odd");
		}
	}
	
	/**
	 * Validates divisibility constraint.<br>
	 *
	 * @param value The integer value to validate
	 * @param divisibleBy The divisor that the value must be divisible by
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateDivisibleBy(long value, @NonNull Optional<Long> divisibleBy) {
		Objects.requireNonNull(divisibleBy, "DivisibleBy constraint must not be null");
		if (divisibleBy.isEmpty()) {
			return;
		}
		
		long divisor = divisibleBy.get();
		if (value % divisor != 0) {
			throw new ConstraintViolateException("Value '" + value + "' must be divisible by " + divisor);
		}
	}
	
	/**
	 * Validates power-of constraint.<br>
	 *
	 * @param value The integer value to validate
	 * @param powerOf The base that the value must be a power of
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validatePowerOf(long value, @NonNull Optional<Integer> powerOf) {
		Objects.requireNonNull(powerOf, "Power of constraint must not be null");
		if (powerOf.isEmpty()) {
			return;
		}
		
		int base = powerOf.get();
		if (value <= 0) {
			throw new ConstraintViolateException("Value '" + value + "' must be a power of " + base + " (must be positive)");
		}
		
		long temp = value;
		while (temp > 1) {
			if (temp % base != 0) {
				throw new ConstraintViolateException("Value '" + value + "' must be a power of " + base);
			}
			temp /= base;
		}
	}
	
	/**
	 * Validates a prefix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param startsWith The prefix constraint as a pair of (prefix, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateStartsWith(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> startsWith) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(startsWith, "Starts with constraint must not be null");
		if (startsWith.isEmpty()) {
			return;
		}
		
		String prefix = startsWith.get().getFirst();
		boolean negated = startsWith.get().getSecond();
		boolean matches = value.startsWith(prefix);
		if (negated) {
			if (matches) {
				throw new ConstraintViolateException("Value '" + value + "' must not start with '" + prefix + "'");
			}
		} else {
			if (!matches) {
				throw new ConstraintViolateException("Value '" + value + "' must start with '" + prefix + "'");
			}
		}
	}
	
	/**
	 * Validates a multi-prefix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param startsWithAny The prefix set constraint as a pair of (prefixes, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateStartsWithAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> startsWithAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(startsWithAny, "Starts with any constraint must not be null");
		if (startsWithAny.isEmpty()) {
			return;
		}
		
		Set<String> prefixes = startsWithAny.get().getFirst();
		boolean negated = startsWithAny.get().getSecond();
		boolean matchesAny = prefixes.stream().anyMatch(value::startsWith);
		if (negated) {
			if (matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must not start with any of " + formatSet(prefixes));
			}
		} else {
			if (!matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must start with one of " + formatSet(prefixes));
			}
		}
	}
	
	/**
	 * Validates a substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param contains The containment constraint as a pair of (substring, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateContains(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> contains) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(contains, "Contains constraint must not be null");
		if (contains.isEmpty()) {
			return;
		}
		
		String substring = contains.get().getFirst();
		boolean negated = contains.get().getSecond();
		boolean matches = value.contains(substring);
		if (negated) {
			if (matches) {
				throw new ConstraintViolateException("Value '" + value + "' must not contain '" + substring + "'");
			}
		} else {
			if (!matches) {
				throw new ConstraintViolateException("Value '" + value + "' must contain '" + substring + "'");
			}
		}
	}
	
	/**
	 * Validates a multi-substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param containsAny The containment set constraint as a pair of (substrings, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateContainsAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> containsAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsAny, "Contains any constraint must not be null");
		if (containsAny.isEmpty()) {
			return;
		}
		
		Set<String> substrings = containsAny.get().getFirst();
		boolean negated = containsAny.get().getSecond();
		boolean matchesAny = substrings.stream().anyMatch(value::contains);
		if (negated) {
			if (matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must not contain any of " + formatSet(substrings));
			}
		} else {
			if (!matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must contain at least one of " + formatSet(substrings));
			}
		}
	}
	
	/**
	 * Validates an all-substring containment constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param containsAll The set of substrings that must all be contained
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateContainsAll(@NonNull String value, @NonNull Optional<Set<String>> containsAll) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsAll, "Contains all constraint must not be null");
		if (containsAll.isEmpty()) {
			return;
		}
		
		Set<String> substrings = containsAll.get();
		Set<String> missing = substrings.stream().filter(s -> !value.contains(s)).collect(Collectors.toSet());
		if (!missing.isEmpty()) {
			throw new ConstraintViolateException("Value '" + value + "' must contain all of " + formatSet(substrings) + ", missing: " + formatSet(missing));
		}
	}
	
	/**
	 * Validates an only-substring containment constraint.<br>
	 * <p>
	 *     The value must only contain characters that appear in at least one of the allowed substrings.
	 * </p>
	 *
	 * @param value The string value to validate
	 * @param containsOnly The set of allowed substrings/characters
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateContainsOnly(@NonNull String value, @NonNull Optional<Set<String>> containsOnly) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(containsOnly, "Contains only constraint must not be null");
		if (containsOnly.isEmpty()) {
			return;
		}
		
		Set<String> allowed = containsOnly.get();
		String allowedChars = String.join("", allowed);
		for (char c : value.toCharArray()) {
			if (allowedChars.indexOf(c) == -1) {
				throw new ConstraintViolateException("Value '" + value + "' must contain only characters from " + formatSet(allowed));
			}
		}
	}
	
	/**
	 * Validates a suffix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param endsWith The suffix constraint as a pair of (suffix, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateEndsWith(@NonNull String value, @NonNull Optional<Pair<String, Boolean>> endsWith) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(endsWith, "Ends with constraint must not be null");
		if (endsWith.isEmpty()) {
			return;
		}
		
		String suffix = endsWith.get().getFirst();
		boolean negated = endsWith.get().getSecond();
		boolean matches = value.endsWith(suffix);
		if (negated) {
			if (matches) {
				throw new ConstraintViolateException("Value '" + value + "' must not end with '" + suffix + "'");
			}
		} else {
			if (!matches) {
				throw new ConstraintViolateException("Value '" + value + "' must end with '" + suffix + "'");
			}
		}
	}
	
	/**
	 * Validates a multi-suffix constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param endsWithAny The suffix set constraint as a pair of (suffixes, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateEndsWithAny(@NonNull String value, @NonNull Optional<Pair<Set<String>, Boolean>> endsWithAny) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(endsWithAny, "Ends with any constraint must not be null");
		if (endsWithAny.isEmpty()) {
			return;
		}
		
		Set<String> suffixes = endsWithAny.get().getFirst();
		boolean negated = endsWithAny.get().getSecond();
		boolean matchesAny = suffixes.stream().anyMatch(value::endsWith);
		if (negated) {
			if (matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must not end with any of " + formatSet(suffixes));
			}
		} else {
			if (!matchesAny) {
				throw new ConstraintViolateException("Value '" + value + "' must end with one of " + formatSet(suffixes));
			}
		}
	}
	
	/**
	 * Validates a regex pattern constraint.<br>
	 *
	 * @param value The string value to validate
	 * @param matches The pattern constraint as a pair of (pattern, negated)
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validatePattern(@NonNull String value, @NonNull Optional<Pair<Pattern, Boolean>> matches) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(matches, "Matches constraint must not be null");
		if (matches.isEmpty()) {
			return;
		}
		
		Pattern pattern = matches.get().getFirst();
		boolean negated = matches.get().getSecond();
		boolean patternMatches = pattern.matcher(value).matches();
		if (negated) {
			if (patternMatches) {
				throw new ConstraintViolateException("Value '" + value + "' must not match pattern '" + pattern.pattern() + "'");
			}
		} else {
			if (!patternMatches) {
				throw new ConstraintViolateException("Value '" + value + "' must match pattern '" + pattern.pattern() + "'");
			}
		}
	}
	
	/**
	 * Validates a character classification constraint for all characters in a string.<br>
	 *
	 * @param value The string value to validate
	 * @param flag The optional flag (present means the constraint is active)
	 * @param classifier The predicate that each character must satisfy
	 * @param className The name of the character class for error messages
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static void validateCharacterClass(@NonNull String value, @NonNull Optional<Unit> flag, @NonNull Predicate<Character> classifier, @NonNull String className) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(flag, "Flag must not be null");
		Objects.requireNonNull(classifier, "Classifier must not be null");
		Objects.requireNonNull(className, "Class name must not be null");
		if (flag.isEmpty()) {
			return;
		}
		
		for (char c : value.toCharArray()) {
			if (!classifier.test(c)) {
				throw new ConstraintViolateException("Value '" + value + "' must contain only " + className + " characters");
			}
		}
	}
	
	/**
	 * Validates that all required keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param requiredKeys The optional set of required keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any required key is missing
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> void validateRequiredKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> requiredKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(requiredKeys, "Required keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (requiredKeys.isEmpty()) {
			return;
		}
		
		Set<K> required = requiredKeys.get();
		Set<K> missing = new HashSet<>(required);
		missing.removeAll(actualKeys);
		if (!missing.isEmpty()) {
			throw new ConstraintViolateException(typeName + " must contain required keys: " + formatSet(missing));
		}
	}
	
	/**
	 * Validates that no forbidden keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param forbiddenKeys The optional set of forbidden keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any forbidden key is present
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> void validateForbiddenKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> forbiddenKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(forbiddenKeys, "Forbidden keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (forbiddenKeys.isEmpty()) {
			return;
		}
		
		Set<K> forbidden = forbiddenKeys.get();
		Set<K> present = new HashSet<>(actualKeys);
		present.retainAll(forbidden);
		if (!present.isEmpty()) {
			throw new ConstraintViolateException(typeName + " must not contain forbidden keys: " + formatSet(present));
		}
	}
	
	/**
	 * Validates that only allowed keys are present in the actual key set.<br>
	 *
	 * @param actualKeys The actual keys present
	 * @param allowedKeys The optional set of allowed keys
	 * @param typeName The name of the type for error messages (e.g., "Map", "Query")
	 * @param <K> The type of the keys
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If any disallowed key is present
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <K> void validateAllowedKeys(@NonNull Set<K> actualKeys, @NonNull Optional<Set<K>> allowedKeys, @NonNull String typeName) {
		Objects.requireNonNull(actualKeys, "Actual keys must not be null");
		Objects.requireNonNull(allowedKeys, "Allowed keys constraint must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (allowedKeys.isEmpty()) {
			return;
		}
		
		Set<K> allowed = allowedKeys.get();
		Set<K> disallowed = new HashSet<>(actualKeys);
		disallowed.removeAll(allowed);
		if (!disallowed.isEmpty()) {
			throw new ConstraintViolateException(typeName + " contains keys that are not allowed: " + formatSet(disallowed));
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	@SuppressWarnings("DuplicatedCode")
	public static <T> void validateNestedConfig(@NonNull T value, @NonNull Optional<? extends ConstraintConfig<T>> config, @NonNull String fieldName) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(config, "Config constraint must not be null");
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (config.isEmpty()) {
			return;
		}
		
		try {
			config.get().validate(value);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException(fieldName + " constraint failed: " + e.getMessage());
		}
	}
	
	/**
	 * Validates an extracted value against a nested constraint configuration.<br>
	 * <p>
	 *     This method extracts a value from the input using the provided extractor function,
	 *     then delegates validation to the nested {@link ConstraintConfig}.<br>
	 *     Error messages are wrapped with the field name for context.
	 * </p>
	 * <p>
	 *     This is useful when the constraint config operates on a derived value rather than
	 *     the original value, such as validating the length of an array or size of a collection.
	 * </p>
	 *
	 * @param value The value to extract from
	 * @param config The optional nested constraint configuration
	 * @param extractor Function to extract the value to validate from the input
	 * @param fieldName The name of the field for error messages
	 * @param <T> The type of the input value
	 * @param <V> The type of the extracted value to validate
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T, V> void validateExtractedValue(@NonNull T value, @NonNull Optional<? extends ConstraintConfig<V>> config, @NonNull Function<T, V> extractor, @NonNull String fieldName) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(config, "Config constraint must not be null");
		Objects.requireNonNull(extractor, "Extractor must not be null");
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (config.isEmpty()) {
			return;
		}
		
		V extracted = extractor.apply(value);
		try {
			config.get().validate(extracted);
		} catch (ConstraintViolateException e) {
			throw new ConstraintViolateException(fieldName + " constraint failed: " + e.getMessage());
		}
	}
	
	/**
	 * Validates an integer value against a numeric field constraint configuration.<br>
	 * <p>
	 *     This is a convenience method that delegates to {@link #validateNestedConfig(Object, Optional, String)}.
	 * </p>
	 *
	 * @param value The integer value to validate
	 * @param config The optional numeric field constraint configuration
	 * @param fieldName The name of the field for error messages
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	@SuppressWarnings("DuplicatedCode")
	public static void validateNumericField(int value, @NonNull Optional<NumericConstraintConfig> config, @NonNull String fieldName) {
		Objects.requireNonNull(config, "Config constraint must not be null");
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (config.isEmpty()) {
			return;
		}
		
		Result<Void> result = config.get().matches(value);
		if (result.isError()) {
			throw new ConstraintViolateException("Field '" + fieldName + "' constraint failed: " + result.errorOrThrow());
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T extends Temporal & Comparable<? super T>> void validateWithinLast(
		@NonNull T value, @NonNull Optional<Duration> withinLast, @NonNull Supplier<T> nowSupplier, @NonNull BiFunction<T, Duration, T> subtractor, @NonNull String typeName
	) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(withinLast, "Within last constraint must not be null");
		Objects.requireNonNull(nowSupplier, "Now supplier must not be null");
		Objects.requireNonNull(subtractor, "Subtractor must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (withinLast.isEmpty()) {
			return;
		}
		
		Duration duration = withinLast.get();
		T threshold = subtractor.apply(nowSupplier.get(), duration);
		if (value.compareTo(threshold) < 0) {
			throw new ConstraintViolateException(typeName + " '" + value + "' must be within last " + duration);
		}
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
	 * @throws NullPointerException If any parameter is null
	 * @throws ConstraintViolateException If the constraint is violated
	 */
	public static <T extends Temporal & Comparable<? super T>> void validateWithinNext(
		@NonNull T value, @NonNull Optional<Duration> withinNext, @NonNull Supplier<T> nowSupplier, @NonNull BiFunction<T, Duration, T> adder, @NonNull String typeName
	) {
		Objects.requireNonNull(value, "Value must not be null");
		Objects.requireNonNull(withinNext, "Within next constraint must not be null");
		Objects.requireNonNull(nowSupplier, "Now supplier must not be null");
		Objects.requireNonNull(adder, "Adder must not be null");
		Objects.requireNonNull(typeName, "Type name must not be null");
		if (withinNext.isEmpty()) {
			return;
		}
		
		Duration duration = withinNext.get();
		T threshold = adder.apply(nowSupplier.get(), duration);
		if (value.compareTo(threshold) > 0) {
			throw new ConstraintViolateException(typeName + " '" + value + "' must be within next " + duration);
		}
	}
}
