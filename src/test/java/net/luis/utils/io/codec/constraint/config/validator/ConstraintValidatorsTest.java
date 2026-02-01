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

import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.config.SizeConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConstraintValidators}.<br>
 *
 * @author Luis-St
 */
class ConstraintValidatorsTest {
	
	@Test
	void allOfWithAllPass() {
		Result<Void> result = ConstraintValidators.validateAll(
			Result::success,
			Result::success,
			Result::success
		);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateAllWithFirstFails() {
		Result<Void> result = ConstraintValidators.validateAll(
			() -> Result.error("first error"),
			Result::success,
			Result::success
		);
		assertTrue(result.isError());
		assertEquals("first error", result.errorOrThrow());
	}
	
	@Test
	void validateAllWithEarlyExit() {
		int[] counter = { 0 };
		Result<Void> result = ConstraintValidators.validateAll(
			() -> {
				counter[0]++;
				return Result.success();
			},
			() -> {
				counter[0]++;
				return Result.error("second error");
			},
			() -> {
				counter[0]++;
				return Result.success();
			}
		);
		assertTrue(result.isError());
		assertEquals(2, counter[0]);
	}
	
	@Test
	void validateAllWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.allOf((Supplier<Result<Void>>[]) null));
	}
	
	@Test
	void validateEqualToWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateEqualTo("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEqualToWithValidate() {
		Result<Void> result = ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("test", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEqualToWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("other", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be equal to"));
	}
	
	@Test
	void validateEqualToWithNegated() {
		Result<Void> result = ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("other", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("test", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be equal to"));
	}
	
	@Test
	void validateEqualToWithCustomPredicate() {
		Result<Void> result = ConstraintValidators.validateEqualTo("TEST", Optional.of(Pair.of("test", false)), String::equalsIgnoreCase);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateEqualToWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo("test", Optional.empty(), null));
	}
	
	@Test
	void validateInWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateIn("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInWithInSet() {
		Result<Void> result = ConstraintValidators.validateIn("test", Optional.of(Pair.of(Set.of("test", "other"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInWithNotInSet() {
		Result<Void> result = ConstraintValidators.validateIn("missing", Optional.of(Pair.of(Set.of("test", "other"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be in"));
	}
	
	@Test
	void validateInWithNegated() {
		Result<Void> result = ConstraintValidators.validateIn("missing", Optional.of(Pair.of(Set.of("test", "other"), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validateIn("test", Optional.of(Pair.of(Set.of("test", "other"), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be in"));
	}
	
	@Test
	void validateInWithCustomPredicate() {
		Result<Void> result = ConstraintValidators.validateIn("TEST", Optional.of(Pair.of(Set.of("test", "other"), false)), String::equalsIgnoreCase);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateInWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn("test", Optional.empty(), null));
	}
	
	@Test
	void validateCustomWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateCustom("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateCustomWithConstraintPasses() {
		Constraint<String> constraint = value -> Result.success();
		Result<Void> result = ConstraintValidators.validateCustom("test", Optional.of(constraint));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateCustomWithConstraintFails() {
		Constraint<String> constraint = value -> Result.error("custom error");
		Result<Void> result = ConstraintValidators.validateCustom("test", Optional.of(constraint));
		assertTrue(result.isError());
		assertEquals("custom error", result.errorOrThrow());
	}
	
	@Test
	void validateCustomWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCustom(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCustom("test", null));
	}
	
	@Test
	void validateRangeWithMinOnly() {
		Result<Void> result = ConstraintValidators.validateRange(10, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateRange(3, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be greater than or equal to"));
	}
	
	@Test
	void validateRangeWithMaxOnly() {
		Result<Void> result = ConstraintValidators.validateRange(5, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateRange(15, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be less than or equal to"));
	}
	
	@Test
	void validateRangeWithBoth() {
		Result<Void> result = ConstraintValidators.validateRange(7, Optional.of(Pair.of(5, true)), Optional.of(Pair.of(10, true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateRangeWithInclusiveBounds() {
		Result<Void> minResult = ConstraintValidators.validateRange(5, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = ConstraintValidators.validateRange(10, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void validateRangeWithExclusiveBounds() {
		Result<Void> minResult = ConstraintValidators.validateRange(5, Optional.of(Pair.of(5, false)), Optional.empty());
		assertTrue(minResult.isError());
		assertTrue(minResult.errorOrThrow().contains("must be greater than"));
		Result<Void> maxResult = ConstraintValidators.validateRange(10, Optional.empty(), Optional.of(Pair.of(10, false)));
		assertTrue(maxResult.isError());
		assertTrue(maxResult.errorOrThrow().contains("must be less than"));
	}
	
	@Test
	void validateRangeWithCustomComparator() {
		Result<Void> result = ConstraintValidators.validateRange("b", Optional.of(Pair.of("a", true)), Optional.of(Pair.of("c", true)), String::compareTo);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(5, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(5, Optional.empty(), null));
	}
	
	@Test
	void validateFlagWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateFlag("test", Optional.empty(), s -> true, "error");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateFlagWithConditionTrue() {
		Result<Void> result = ConstraintValidators.validateFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 4, "error");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateFlagWithConditionFalse() {
		Result<Void> result = ConstraintValidators.validateFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 5, "Length must be 5");
		assertTrue(result.isError());
		assertEquals("Length must be 5", result.errorOrThrow());
	}
	
	@Test
	void validateFlagWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateFlag(null, Optional.empty(), s -> true, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateFlag("test", null, s -> true, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateFlag("test", Optional.empty(), null, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateFlag("test", Optional.empty(), s -> true, null));
	}
	
	@Test
	void validateSignWithPositive() {
		Result<Void> result = ConstraintValidators.validateSign(5, Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(-5, Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void validateSignWithNegative() {
		Result<Void> result = ConstraintValidators.validateSign(-5, Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(5, Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void validateSignWithZero() {
		Result<Void> result = ConstraintValidators.validateSign(0, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(5, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void validateSignWithNonPositive() {
		Result<Void> result = ConstraintValidators.validateSign(-5, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = ConstraintValidators.validateSign(0, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(5, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void validateSignWithNonNegative() {
		Result<Void> result = ConstraintValidators.validateSign(5, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = ConstraintValidators.validateSign(0, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(-5, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void validateSignWithNonZero() {
		Result<Void> result = ConstraintValidators.validateSign(5, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateSign(0, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void validateSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateSign(5, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateSign(5, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateSign(5, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void validatePercentageWithValidPercentage() {
		Result<Void> result = ConstraintValidators.validatePercentage(50, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
		Result<Void> minResult = ConstraintValidators.validatePercentage(0, Optional.of(Unit.INSTANCE));
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = ConstraintValidators.validatePercentage(100, Optional.of(Unit.INSTANCE));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void validatePercentageWithBelowZero() {
		Result<Void> result = ConstraintValidators.validatePercentage(-1, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a percentage"));
	}
	
	@Test
	void validatePercentageWithAbove100() {
		Result<Void> result = ConstraintValidators.validatePercentage(101, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a percentage"));
	}
	
	@Test
	void validatePercentageWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePercentage(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePercentage(50, null));
	}
	
	@Test
	void validateParityWithEven() {
		Result<Void> result = ConstraintValidators.validateParity(4, Optional.of(Unit.INSTANCE), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateParity(5, Optional.of(Unit.INSTANCE), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be even"));
	}
	
	@Test
	void validateParityWithOdd() {
		Result<Void> result = ConstraintValidators.validateParity(5, Optional.empty(), Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintValidators.validateParity(4, Optional.empty(), Optional.of(Unit.INSTANCE));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be odd"));
	}
	
	@Test
	void validateParityWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateParity(4, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateParity(4, Optional.empty(), null));
	}
	
	@Test
	void validateDivisibleByWithDivisible() {
		Result<Void> result = ConstraintValidators.validateDivisibleBy(12, Optional.of(3L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateDivisibleByWithNotDivisible() {
		Result<Void> result = ConstraintValidators.validateDivisibleBy(13, Optional.of(3L));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be divisible by"));
	}
	
	@Test
	void validateDivisibleByWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateDivisibleBy(12, null));
	}
	
	@Test
	void validatePowerOfWithIsPower() {
		Result<Void> result = ConstraintValidators.validatePowerOf(8, Optional.of(2));
		assertTrue(result.isSuccess());
		Result<Void> result27 = ConstraintValidators.validatePowerOf(27, Optional.of(3));
		assertTrue(result27.isSuccess());
	}
	
	@Test
	void validatePowerOfWithNotPower() {
		Result<Void> result = ConstraintValidators.validatePowerOf(10, Optional.of(2));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a power of"));
	}
	
	@Test
	void validatePowerOfWithNegativeValues() {
		Result<Void> result = ConstraintValidators.validatePowerOf(-8, Optional.of(2));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void validatePowerOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePowerOf(8, null));
	}
	
	@Test
	void matchStartsWithWithValidate() {
		Result<Void> result = ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("hello", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchStartsWithWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("world", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must start with"));
	}
	
	@Test
	void validateStartsWithWithNegated() {
		Result<Void> result = ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("hello", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not start with"));
	}
	
	@Test
	void validateStartsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWith("test", null));
	}
	
	@Test
	void validateStartsWithAnyWithValidate() {
		Result<Void> result = ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hello"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateStartsWithAnyWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hey"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must start with one of"));
	}
	
	@Test
	void validateStartsWithAnyWithNegated() {
		Result<Void> result = ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "hi"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not start with any of"));
	}
	
	@Test
	void validateStartsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWithAny("test", null));
	}
	
	@Test
	void matchContainsWithValidate() {
		Result<Void> result = ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("lo wo", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchContainsWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("xyz", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain"));
	}
	
	@Test
	void validateContainsWithNegated() {
		Result<Void> result = ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("xyz", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not contain"));
	}
	
	@Test
	void validateContainsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContains(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContains("test", null));
	}
	
	@Test
	void validateContainsAnyWithValidate() {
		Result<Void> result = ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "world"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateContainsAnyWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "abc"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain at least one of"));
	}
	
	@Test
	void validateContainsAnyWithNegated() {
		Result<Void> result = ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("world", "hello"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not contain any of"));
	}
	
	@Test
	void validateContainsAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAny("test", null));
	}
	
	@Test
	void validateContainsAllWithAllPresent() {
		Result<Void> result = ConstraintValidators.validateContainsAll("hello world", Optional.of(Set.of("hello", "world")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateContainsAllWithMissing() {
		Result<Void> result = ConstraintValidators.validateContainsAll("hello world", Optional.of(Set.of("hello", "xyz")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain all of"));
	}
	
	@Test
	void validateContainsAllWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAll(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAll("test", null));
	}
	
	@Test
	void validateContainsOnlyWithValid() {
		Result<Void> result = ConstraintValidators.validateContainsOnly("abc", Optional.of(Set.of("a", "b", "c")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateContainsOnlyWithInvalid() {
		Result<Void> result = ConstraintValidators.validateContainsOnly("abcd", Optional.of(Set.of("a", "b", "c")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain only characters from"));
	}
	
	@Test
	void validateContainsOnlyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsOnly(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsOnly("test", null));
	}
	
	@Test
	void matchEndsWithWithValidate() {
		Result<Void> result = ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("world", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEndsWithWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("hello", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must end with"));
	}
	
	@Test
	void validateEndsWithWithNegated() {
		Result<Void> result = ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("hello", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not end with"));
	}
	
	@Test
	void validateEndsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWith("test", null));
	}
	
	@Test
	void validateEndsWithAnyWithValidate() {
		Result<Void> result = ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateEndsWithAnyWithNoValidate() {
		Result<Void> result = ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "test"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must end with one of"));
	}
	
	@Test
	void validateEndsWithAnyWithNegated() {
		Result<Void> result = ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not end with any of"));
	}
	
	@Test
	void validateEndsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWithAny("test", null));
	}
	
	@Test
	void matchPatternWithValidate() {
		Result<Void> result = ConstraintValidators.validatePattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPatternWithNoValidate() {
		Result<Void> result = ConstraintValidators.validatePattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must match pattern"));
	}
	
	@Test
	void validatePatternWithNegated() {
		Result<Void> result = ConstraintValidators.validatePattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintValidators.validatePattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not match pattern"));
	}
	
	@Test
	void validatePatternWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePattern(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePattern("test", null));
	}
	
	@Test
	void matchCharacterClassWithAllValidate() {
		Result<Void> result = ConstraintValidators.validateCharacterClass("ABC", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateCharacterClassWithSomeFail() {
		Result<Void> result = ConstraintValidators.validateCharacterClass("ABc", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain only uppercase characters"));
	}
	
	@Test
	void validateCharacterClassWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCharacterClass(null, Optional.empty(), Character::isUpperCase, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCharacterClass("test", null, Character::isUpperCase, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCharacterClass("test", Optional.empty(), null, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCharacterClass("test", Optional.empty(), Character::isUpperCase, null));
	}
	
	@Test
	void validateRequiredKeysWithAllPresent() {
		Result<Void> result = ConstraintValidators.validateRequiredKeys(Set.of("a", "b", "c"), Optional.of(Set.of("a", "b")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateRequiredKeysWithMissing() {
		Result<Void> result = ConstraintValidators.validateRequiredKeys(Set.of("a", "c"), Optional.of(Set.of("a", "b")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain required keys"));
	}
	
	@Test
	void validateRequiredKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateForbiddenKeysWithNonePresent() {
		Result<Void> result = ConstraintValidators.validateForbiddenKeys(Set.of("a", "b"), Optional.of(Set.of("c", "d")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateForbiddenKeysWithSomePresent() {
		Result<Void> result = ConstraintValidators.validateForbiddenKeys(Set.of("a", "b", "c"), Optional.of(Set.of("c", "d")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not contain forbidden keys"));
	}
	
	@Test
	void validateForbiddenKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateAllowedKeysWithAllAllowed() {
		Result<Void> result = ConstraintValidators.validateAllowedKeys(Set.of("a", "b"), Optional.of(Set.of("a", "b", "c")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateAllowedKeysWithDisallowed() {
		Result<Void> result = ConstraintValidators.validateAllowedKeys(Set.of("a", "b", "d"), Optional.of(Set.of("a", "b", "c")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("contains keys that are not allowed"));
	}
	
	@Test
	void validateAllowedKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateNestedConfigWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateNestedConfig("test", Optional.empty(), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateNestedConfigWithPass() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		Result<Void> result = ConstraintValidators.validateNestedConfig(5, Optional.of(config), "value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateNestedConfigWithFail() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(10);
		Result<Void> result = ConstraintValidators.validateNestedConfig(5, Optional.of(config), "value");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("constraint failed"));
	}
	
	@Test
	void validateNestedConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNestedConfig(null, Optional.empty(), "field"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNestedConfig("test", null, "field"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNestedConfig("test", Optional.empty(), null));
	}
	
	@Test
	void validateExtractedValueWithEmptyConfig() {
		List<String> list = List.of("a", "b", "c");
		Optional<SizeConstraintConfig> config = Optional.empty();
		assertTrue(ConstraintValidators.validateExtractedValue(list, config, List::size, "size").isSuccess());
	}
	
	@Test
	void validateExtractedValueWithMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1).withMaxSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		assertTrue(ConstraintValidators.validateExtractedValue(list, config, List::size, "size").isSuccess());
	}
	
	@Test
	void validateExtractedValueWithNonMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		Result<Void> result = ConstraintValidators.validateExtractedValue(list, config, List::size, "size");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("size constraint failed"));
	}
	
	@Test
	void validateExtractedValueWithArrayLength() {
		String[] array = new String[] { "a", "b" };
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(3);
		Optional<LengthConstraintConfig> config = Optional.of(lengthConfig);
		assertTrue(ConstraintValidators.validateExtractedValue(array, config, arr -> arr.length, "length").isSuccess());
	}
	
	@Test
	void validateExtractedValueWithNullValue() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateExtractedValue((List<String>) null, Optional.empty(), list -> list.size(), "size"));
	}
	
	@Test
	void validateExtractedValueWithNullConfig() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateExtractedValue(List.of(), null, List::size, "size"));
	}
	
	@Test
	void validateExtractedValueWithNullExtractor() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateExtractedValue(List.of(), Optional.empty(), null, "size"));
	}
	
	@Test
	void validateExtractedValueWithNullFieldName() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateExtractedValue(List.of(), Optional.empty(), List::size, null));
	}
	
	@Test
	void validateNumericFieldWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateNumericField(5, Optional.empty(), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateNumericFieldWithPass() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 10);
		Result<Void> result = ConstraintValidators.validateNumericField(5, Optional.of(config), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateNumericFieldWithFail() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(10, 20);
		Result<Void> result = ConstraintValidators.validateNumericField(5, Optional.of(config), "field");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("constraint failed"));
	}
	
	@Test
	void validateNumericFieldWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNumericField(5, null, "field"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNumericField(5, Optional.empty(), null));
	}
	
	@Test
	void validateWithinLastWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateWithinLast(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateWithinLastWithPass() {
		Instant now = Instant.now();
		Instant recent = now.minusSeconds(30);
		Result<Void> result = ConstraintValidators.validateWithinLast(recent, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateWithinLastWithFail() {
		Instant now = Instant.now();
		Instant old = now.minusSeconds(120);
		Result<Void> result = ConstraintValidators.validateWithinLast(old, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within last"));
	}
	
	@Test
	void validateWithinLastWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinLast(null, Optional.empty(), Instant::now, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinLast(Instant.now(), null, Instant::now, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinLast(Instant.now(), Optional.empty(), null, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinLast(Instant.now(), Optional.empty(), Instant::now, null, "Instant"));
	}
	
	@Test
	void validateWithinNextWithEmptyOptional() {
		Result<Void> result = ConstraintValidators.validateWithinNext(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateWithinNextWithPass() {
		Instant now = Instant.now();
		Instant future = now.plusSeconds(30);
		Result<Void> result = ConstraintValidators.validateWithinNext(future, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void validateWithinNextWithFail() {
		Instant now = Instant.now();
		Instant farFuture = now.plusSeconds(120);
		Result<Void> result = ConstraintValidators.validateWithinNext(farFuture, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within next"));
	}
	
	@Test
	void validateWithinNextWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinNext(null, Optional.empty(), Instant::now, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinNext(Instant.now(), null, Instant::now, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinNext(Instant.now(), Optional.empty(), null, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateWithinNext(Instant.now(), Optional.empty(), Instant::now, null, "Instant"));
	}
	
	private enum TestEnum {
		VALUE1, VALUE2
	}
}
