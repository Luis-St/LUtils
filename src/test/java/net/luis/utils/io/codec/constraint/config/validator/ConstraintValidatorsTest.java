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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
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
		assertDoesNotThrow(() -> ConstraintValidators.validateAll(
			() -> {},
			() -> {},
			() -> {}
		));
	}
	
	@Test
	void validateAllWithFirstFails() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateAll(
			() -> {
				throw new ConstraintViolateException("first error");
			},
			() -> {},
			() -> {}
		));
		assertEquals("first error", exception.getMessage());
	}
	
	@Test
	void validateAllWithEarlyExit() {
		int[] counter = { 0 };
		assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateAll(
			() -> counter[0]++,
			() -> {
				counter[0]++;
				throw new ConstraintViolateException("second error");
			},
			() -> counter[0]++
		));
		assertEquals(2, counter[0]);
	}
	
	@Test
	void validateAllWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAll((Runnable[]) null));
	}
	
	@Test
	void validateEqualToWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEqualTo("test", Optional.empty()));
	}
	
	@Test
	void matchEqualToWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("test", false))));
	}
	
	@Test
	void matchEqualToWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("other", false))));
		assertTrue(exception.getMessage().contains("must be equal to"));
	}
	
	@Test
	void validateEqualToWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("other", true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEqualTo("test", Optional.of(Pair.of("test", true))));
		assertTrue(exception.getMessage().contains("must not be equal to"));
	}
	
	@Test
	void validateEqualToWithCustomPredicate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEqualTo("TEST", Optional.of(Pair.of("test", false)), String::equalsIgnoreCase));
	}
	
	@Test
	void validateEqualToWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEqualTo("test", Optional.empty(), null));
	}
	
	@Test
	void validateInWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateIn("test", Optional.empty()));
	}
	
	@Test
	void validateInWithInSet() {
		assertDoesNotThrow(() -> ConstraintValidators.validateIn("test", Optional.of(Pair.of(Set.of("test", "other"), false))));
	}
	
	@Test
	void validateInWithNotInSet() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateIn("missing", Optional.of(Pair.of(Set.of("test", "other"), false))));
		assertTrue(exception.getMessage().contains("must be in"));
	}
	
	@Test
	void validateInWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validateIn("missing", Optional.of(Pair.of(Set.of("test", "other"), true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateIn("test", Optional.of(Pair.of(Set.of("test", "other"), true))));
		assertTrue(exception.getMessage().contains("must not be in"));
	}
	
	@Test
	void validateInWithCustomPredicate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateIn("TEST", Optional.of(Pair.of(Set.of("test", "other"), false)), String::equalsIgnoreCase));
	}
	
	@Test
	void validateInWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateIn("test", Optional.empty(), null));
	}
	
	@Test
	void validateCustomWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateCustom("test", Optional.empty()));
	}
	
	@Test
	void validateCustomWithConstraintPasses() {
		Constraint<String> constraint = value -> {};
		assertDoesNotThrow(() -> ConstraintValidators.validateCustom("test", Optional.of(constraint)));
	}
	
	@Test
	void validateCustomWithConstraintFails() {
		Constraint<String> constraint = value -> {
			throw new ConstraintViolateException("custom error");
		};
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateCustom("test", Optional.of(constraint)));
		assertEquals("custom error", exception.getMessage());
	}
	
	@Test
	void validateCustomWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCustom(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateCustom("test", null));
	}
	
	@Test
	void validateRangeWithMinOnly() {
		assertDoesNotThrow(() -> ConstraintValidators.validateRange(10, Optional.of(Pair.of(5, true)), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateRange(3, Optional.of(Pair.of(5, true)), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be greater than or equal to"));
	}
	
	@Test
	void validateRangeWithMaxOnly() {
		assertDoesNotThrow(() -> ConstraintValidators.validateRange(5, Optional.empty(), Optional.of(Pair.of(10, true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateRange(15, Optional.empty(), Optional.of(Pair.of(10, true))));
		assertTrue(exception.getMessage().contains("must be less than or equal to"));
	}
	
	@Test
	void validateRangeWithBoth() {
		assertDoesNotThrow(() -> ConstraintValidators.validateRange(7, Optional.of(Pair.of(5, true)), Optional.of(Pair.of(10, true))));
	}
	
	@Test
	void validateRangeWithInclusiveBounds() {
		assertDoesNotThrow(() -> ConstraintValidators.validateRange(5, Optional.of(Pair.of(5, true)), Optional.empty()));
		assertDoesNotThrow(() -> ConstraintValidators.validateRange(10, Optional.empty(), Optional.of(Pair.of(10, true))));
	}
	
	@Test
	void validateRangeWithExclusiveBounds() {
		ConstraintViolateException minException = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateRange(5, Optional.of(Pair.of(5, false)), Optional.empty()));
		assertTrue(minException.getMessage().contains("must be greater than"));
		ConstraintViolateException maxException = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateRange(10, Optional.empty(), Optional.of(Pair.of(10, false))));
		assertTrue(maxException.getMessage().contains("must be less than"));
	}
	
	@Test
	void validateRangeWithCustomComparator() {
		assertDoesNotThrow(() -> ConstraintValidators.validateRange("b", Optional.of(Pair.of("a", true)), Optional.of(Pair.of("c", true)), String::compareTo));
	}
	
	@Test
	void validateRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(5, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRange(5, Optional.empty(), null));
	}
	
	@Test
	void validateFlagWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateFlag("test", Optional.empty(), s -> true, "error"));
	}
	
	@Test
	void validateFlagWithConditionTrue() {
		assertDoesNotThrow(() -> ConstraintValidators.validateFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 4, "error"));
	}
	
	@Test
	void validateFlagWithConditionFalse() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 5, "Length must be 5"));
		assertEquals("Length must be 5", exception.getMessage());
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
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(5, Optional.of(false), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(-5, Optional.of(false), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be positive"));
	}
	
	@Test
	void validateSignWithNegative() {
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(-5, Optional.empty(), Optional.of(false), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(5, Optional.empty(), Optional.of(false), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be negative"));
	}
	
	@Test
	void validateSignWithZero() {
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(0, Optional.empty(), Optional.empty(), Optional.of(false)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(5, Optional.empty(), Optional.empty(), Optional.of(false)));
		assertTrue(exception.getMessage().contains("must be zero"));
	}
	
	@Test
	void validateSignWithNonPositive() {
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(-5, Optional.of(true), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(0, Optional.of(true), Optional.empty(), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(5, Optional.of(true), Optional.empty(), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-positive"));
	}
	
	@Test
	void validateSignWithNonNegative() {
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(5, Optional.empty(), Optional.of(true), Optional.empty()));
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(0, Optional.empty(), Optional.of(true), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(-5, Optional.empty(), Optional.of(true), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be non-negative"));
	}
	
	@Test
	void validateSignWithNonZero() {
		assertDoesNotThrow(() -> ConstraintValidators.validateSign(5, Optional.empty(), Optional.empty(), Optional.of(true)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateSign(0, Optional.empty(), Optional.empty(), Optional.of(true)));
		assertTrue(exception.getMessage().contains("must be non-zero"));
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
		assertDoesNotThrow(() -> ConstraintValidators.validatePercentage(50, Optional.of(Unit.INSTANCE)));
		assertDoesNotThrow(() -> ConstraintValidators.validatePercentage(0, Optional.of(Unit.INSTANCE)));
		assertDoesNotThrow(() -> ConstraintValidators.validatePercentage(100, Optional.of(Unit.INSTANCE)));
	}
	
	@Test
	void validatePercentageWithBelowZero() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePercentage(-1, Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be a percentage"));
	}
	
	@Test
	void validatePercentageWithAbove100() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePercentage(101, Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be a percentage"));
	}
	
	@Test
	void validatePercentageWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePercentage(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePercentage(50, null));
	}
	
	@Test
	void validateParityWithEven() {
		assertDoesNotThrow(() -> ConstraintValidators.validateParity(4, Optional.of(Unit.INSTANCE), Optional.empty()));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateParity(5, Optional.of(Unit.INSTANCE), Optional.empty()));
		assertTrue(exception.getMessage().contains("must be even"));
	}
	
	@Test
	void validateParityWithOdd() {
		assertDoesNotThrow(() -> ConstraintValidators.validateParity(5, Optional.empty(), Optional.of(Unit.INSTANCE)));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateParity(4, Optional.empty(), Optional.of(Unit.INSTANCE)));
		assertTrue(exception.getMessage().contains("must be odd"));
	}
	
	@Test
	void validateParityWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateParity(4, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateParity(4, Optional.empty(), null));
	}
	
	@Test
	void validateDivisibleByWithDivisible() {
		assertDoesNotThrow(() -> ConstraintValidators.validateDivisibleBy(12, Optional.of(3L)));
	}
	
	@Test
	void validateDivisibleByWithNotDivisible() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateDivisibleBy(13, Optional.of(3L)));
		assertTrue(exception.getMessage().contains("must be divisible by"));
	}
	
	@Test
	void validateDivisibleByWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateDivisibleBy(12, null));
	}
	
	@Test
	void validatePowerOfWithIsPower() {
		assertDoesNotThrow(() -> ConstraintValidators.validatePowerOf(8, Optional.of(2)));
		assertDoesNotThrow(() -> ConstraintValidators.validatePowerOf(27, Optional.of(3)));
	}
	
	@Test
	void validatePowerOfWithNotPower() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePowerOf(10, Optional.of(2)));
		assertTrue(exception.getMessage().contains("must be a power of"));
	}
	
	@Test
	void validatePowerOfWithNegativeValues() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePowerOf(-8, Optional.of(2)));
		assertTrue(exception.getMessage().contains("must be positive") || exception.getMessage().contains("must be a power of"));
	}
	
	@Test
	void validatePowerOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePowerOf(8, null));
	}
	
	@Test
	void matchStartsWithWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("hello", false))));
	}
	
	@Test
	void matchStartsWithWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("world", false))));
		assertTrue(exception.getMessage().contains("must start with"));
	}
	
	@Test
	void validateStartsWithWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("world", true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateStartsWith("hello world", Optional.of(Pair.of("hello", true))));
		assertTrue(exception.getMessage().contains("must not start with"));
	}
	
	@Test
	void validateStartsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWith("test", null));
	}
	
	@Test
	void validateStartsWithAnyWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hello"), false))));
	}
	
	@Test
	void validateStartsWithAnyWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hey"), false))));
		assertTrue(exception.getMessage().contains("must start with one of"));
	}
	
	@Test
	void validateStartsWithAnyWithNegated() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "hi"), true))));
		assertTrue(exception.getMessage().contains("must not start with any of"));
	}
	
	@Test
	void validateStartsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateStartsWithAny("test", null));
	}
	
	@Test
	void matchContainsWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("lo wo", false))));
	}
	
	@Test
	void matchContainsWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("xyz", false))));
		assertTrue(exception.getMessage().contains("must contain"));
	}
	
	@Test
	void validateContainsWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("xyz", true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContains("hello world", Optional.of(Pair.of("world", true))));
		assertTrue(exception.getMessage().contains("must not contain"));
	}
	
	@Test
	void validateContainsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContains(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContains("test", null));
	}
	
	@Test
	void validateContainsAnyWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "world"), false))));
	}
	
	@Test
	void validateContainsAnyWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "abc"), false))));
		assertTrue(exception.getMessage().contains("must contain at least one of"));
	}
	
	@Test
	void validateContainsAnyWithNegated() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContainsAny("hello world", Optional.of(Pair.of(Set.of("world", "hello"), true))));
		assertTrue(exception.getMessage().contains("must not contain any of"));
	}
	
	@Test
	void validateContainsAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAny("test", null));
	}
	
	@Test
	void validateContainsAllWithAllPresent() {
		assertDoesNotThrow(() -> ConstraintValidators.validateContainsAll("hello world", Optional.of(Set.of("hello", "world"))));
	}
	
	@Test
	void validateContainsAllWithMissing() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContainsAll("hello world", Optional.of(Set.of("hello", "xyz"))));
		assertTrue(exception.getMessage().contains("must contain all of"));
	}
	
	@Test
	void validateContainsAllWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAll(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsAll("test", null));
	}
	
	@Test
	void validateContainsOnlyWithValid() {
		assertDoesNotThrow(() -> ConstraintValidators.validateContainsOnly("abc", Optional.of(Set.of("a", "b", "c"))));
	}
	
	@Test
	void validateContainsOnlyWithInvalid() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateContainsOnly("abcd", Optional.of(Set.of("a", "b", "c"))));
		assertTrue(exception.getMessage().contains("must contain only characters from"));
	}
	
	@Test
	void validateContainsOnlyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsOnly(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateContainsOnly("test", null));
	}
	
	@Test
	void matchEndsWithWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("world", false))));
	}
	
	@Test
	void matchEndsWithWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("hello", false))));
		assertTrue(exception.getMessage().contains("must end with"));
	}
	
	@Test
	void validateEndsWithWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("hello", true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEndsWith("hello world", Optional.of(Pair.of("world", true))));
		assertTrue(exception.getMessage().contains("must not end with"));
	}
	
	@Test
	void validateEndsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWith("test", null));
	}
	
	@Test
	void validateEndsWithAnyWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), false))));
	}
	
	@Test
	void validateEndsWithAnyWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "test"), false))));
		assertTrue(exception.getMessage().contains("must end with one of"));
	}
	
	@Test
	void validateEndsWithAnyWithNegated() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), true))));
		assertTrue(exception.getMessage().contains("must not end with any of"));
	}
	
	@Test
	void validateEndsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateEndsWithAny("test", null));
	}
	
	@Test
	void matchPatternWithValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validatePattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false))));
	}
	
	@Test
	void matchPatternWithNoValidate() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false))));
		assertTrue(exception.getMessage().contains("must match pattern"));
	}
	
	@Test
	void validatePatternWithNegated() {
		assertDoesNotThrow(() -> ConstraintValidators.validatePattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true))));
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validatePattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true))));
		assertTrue(exception.getMessage().contains("must not match pattern"));
	}
	
	@Test
	void validatePatternWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePattern(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validatePattern("test", null));
	}
	
	@Test
	void matchCharacterClassWithAllValidate() {
		assertDoesNotThrow(() -> ConstraintValidators.validateCharacterClass("ABC", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase"));
	}
	
	@Test
	void validateCharacterClassWithSomeFail() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateCharacterClass("ABc", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase"));
		assertTrue(exception.getMessage().contains("must contain only uppercase characters"));
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
		assertDoesNotThrow(() -> ConstraintValidators.validateRequiredKeys(Set.of("a", "b", "c"), Optional.of(Set.of("a", "b")), "Map"));
	}
	
	@Test
	void validateRequiredKeysWithMissing() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateRequiredKeys(Set.of("a", "c"), Optional.of(Set.of("a", "b")), "Map"));
		assertTrue(exception.getMessage().contains("must contain required keys"));
	}
	
	@Test
	void validateRequiredKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateRequiredKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateForbiddenKeysWithNonePresent() {
		assertDoesNotThrow(() -> ConstraintValidators.validateForbiddenKeys(Set.of("a", "b"), Optional.of(Set.of("c", "d")), "Map"));
	}
	
	@Test
	void validateForbiddenKeysWithSomePresent() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateForbiddenKeys(Set.of("a", "b", "c"), Optional.of(Set.of("c", "d")), "Map"));
		assertTrue(exception.getMessage().contains("must not contain forbidden keys"));
	}
	
	@Test
	void validateForbiddenKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateForbiddenKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateAllowedKeysWithAllAllowed() {
		assertDoesNotThrow(() -> ConstraintValidators.validateAllowedKeys(Set.of("a", "b"), Optional.of(Set.of("a", "b", "c")), "Map"));
	}
	
	@Test
	void validateAllowedKeysWithDisallowed() {
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateAllowedKeys(Set.of("a", "b", "d"), Optional.of(Set.of("a", "b", "c")), "Map"));
		assertTrue(exception.getMessage().contains("contains keys that are not allowed"));
	}
	
	@Test
	void validateAllowedKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateAllowedKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void validateNestedConfigWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateNestedConfig("test", Optional.empty(), "field"));
	}
	
	@Test
	void validateNestedConfigWithPass() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		assertDoesNotThrow(() -> ConstraintValidators.validateNestedConfig(5, Optional.of(config), "value"));
	}
	
	@Test
	void validateNestedConfigWithFail() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(10);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateNestedConfig(5, Optional.of(config), "value"));
		assertTrue(exception.getMessage().contains("constraint failed"));
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
		assertDoesNotThrow(() -> ConstraintValidators.validateExtractedValue(list, config, List::size, "size"));
	}
	
	@Test
	void validateExtractedValueWithMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1).withMaxSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		assertDoesNotThrow(() -> ConstraintValidators.validateExtractedValue(list, config, List::size, "size"));
	}
	
	@Test
	void validateExtractedValueWithNonMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateExtractedValue(list, config, List::size, "size"));
		assertTrue(exception.getMessage().contains("size constraint failed"));
	}
	
	@Test
	void validateExtractedValueWithArrayLength() {
		String[] array = { "a", "b" };
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(3);
		Optional<LengthConstraintConfig> config = Optional.of(lengthConfig);
		assertDoesNotThrow(() -> ConstraintValidators.validateExtractedValue(array, config, arr -> arr.length, "length"));
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
		assertDoesNotThrow(() -> ConstraintValidators.validateNumericField(5, Optional.empty(), "field"));
	}
	
	@Test
	void validateNumericFieldWithPass() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 10);
		assertDoesNotThrow(() -> ConstraintValidators.validateNumericField(5, Optional.of(config), "field"));
	}
	
	@Test
	void validateNumericFieldWithFail() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(10, 20);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateNumericField(5, Optional.of(config), "field"));
		assertTrue(exception.getMessage().contains("constraint failed"));
	}
	
	@Test
	void validateNumericFieldWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNumericField(5, null, "field"));
		assertThrows(NullPointerException.class, () -> ConstraintValidators.validateNumericField(5, Optional.empty(), null));
	}
	
	@Test
	void validateWithinLastWithEmptyOptional() {
		assertDoesNotThrow(() -> ConstraintValidators.validateWithinLast(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.minus(d), "Instant"));
	}
	
	@Test
	void validateWithinLastWithPass() {
		Instant now = Instant.now();
		Instant recent = now.minusSeconds(30);
		assertDoesNotThrow(() -> ConstraintValidators.validateWithinLast(recent, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant"));
	}
	
	@Test
	void validateWithinLastWithFail() {
		Instant now = Instant.now();
		Instant old = now.minusSeconds(120);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateWithinLast(old, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant"));
		assertTrue(exception.getMessage().contains("must be within last"));
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
		assertDoesNotThrow(() -> ConstraintValidators.validateWithinNext(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.plus(d), "Instant"));
	}
	
	@Test
	void validateWithinNextWithPass() {
		Instant now = Instant.now();
		Instant future = now.plusSeconds(30);
		assertDoesNotThrow(() -> ConstraintValidators.validateWithinNext(future, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant"));
	}
	
	@Test
	void validateWithinNextWithFail() {
		Instant now = Instant.now();
		Instant farFuture = now.plusSeconds(120);
		ConstraintViolateException exception = assertThrows(ConstraintViolateException.class, () -> ConstraintValidators.validateWithinNext(farFuture, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant"));
		assertTrue(exception.getMessage().contains("must be within next"));
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
