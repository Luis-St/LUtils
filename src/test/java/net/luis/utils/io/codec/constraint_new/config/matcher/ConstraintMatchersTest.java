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
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConstraintMatchers}.<br>
 *
 * @author Luis-St
 */
class ConstraintMatchersTest {
	
	@Test
	void allOfWithAllPass() {
		Result<Void> result = ConstraintMatchers.allOf(
			Result::success,
			Result::success,
			Result::success
		);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void allOfWithFirstFails() {
		Result<Void> result = ConstraintMatchers.allOf(
			() -> Result.error("first error"),
			Result::success,
			Result::success
		);
		assertTrue(result.isError());
		assertEquals("first error", result.errorOrThrow());
	}
	
	@Test
	void allOfWithEarlyExit() {
		int[] counter = { 0 };
		Result<Void> result = ConstraintMatchers.allOf(
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
	void allOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.allOf(null));
	}
	
	@Test
	void matchEqualToWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchEqualTo("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEqualToWithMatch() {
		Result<Void> result = ConstraintMatchers.matchEqualTo("test", Optional.of(Pair.of("test", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEqualToWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchEqualTo("test", Optional.of(Pair.of("other", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be equal to"));
	}
	
	@Test
	void matchEqualToWithNegated() {
		Result<Void> result = ConstraintMatchers.matchEqualTo("test", Optional.of(Pair.of("other", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchEqualTo("test", Optional.of(Pair.of("test", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be equal to"));
	}
	
	@Test
	void matchEqualToWithCustomPredicate() {
		Result<Void> result = ConstraintMatchers.matchEqualTo("TEST", Optional.of(Pair.of("test", false)), String::equalsIgnoreCase);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEqualToWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEqualTo(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEqualTo("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEqualTo("test", Optional.empty(), null));
	}
	
	@Test
	void matchInWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchIn("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInWithInSet() {
		Result<Void> result = ConstraintMatchers.matchIn("test", Optional.of(Pair.of(Set.of("test", "other"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInWithNotInSet() {
		Result<Void> result = ConstraintMatchers.matchIn("missing", Optional.of(Pair.of(Set.of("test", "other"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be in"));
	}
	
	@Test
	void matchInWithNegated() {
		Result<Void> result = ConstraintMatchers.matchIn("missing", Optional.of(Pair.of(Set.of("test", "other"), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchIn("test", Optional.of(Pair.of(Set.of("test", "other"), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not be in"));
	}
	
	@Test
	void matchInWithCustomPredicate() {
		Result<Void> result = ConstraintMatchers.matchIn("TEST", Optional.of(Pair.of(Set.of("test", "other"), false)), String::equalsIgnoreCase);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchInWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchIn(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchIn("test", null));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchIn("test", Optional.empty(), null));
	}
	
	@Test
	void matchCustomWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchCustom("test", Optional.empty());
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchCustomWithConstraintPasses() {
		Constraint<String> constraint = value -> Result.success();
		Result<Void> result = ConstraintMatchers.matchCustom("test", Optional.of(constraint));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchCustomWithConstraintFails() {
		Constraint<String> constraint = value -> Result.error("custom error");
		Result<Void> result = ConstraintMatchers.matchCustom("test", Optional.of(constraint));
		assertTrue(result.isError());
		assertEquals("custom error", result.errorOrThrow());
	}
	
	@Test
	void matchCustomWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCustom(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCustom("test", null));
	}
	
	@Test
	void matchRangeWithMinOnly() {
		Result<Void> result = ConstraintMatchers.matchRange(10, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchRange(3, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be greater than or equal to"));
	}
	
	@Test
	void matchRangeWithMaxOnly() {
		Result<Void> result = ConstraintMatchers.matchRange(5, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchRange(15, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be less than or equal to"));
	}
	
	@Test
	void matchRangeWithBoth() {
		Result<Void> result = ConstraintMatchers.matchRange(7, Optional.of(Pair.of(5, true)), Optional.of(Pair.of(10, true)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRangeWithInclusiveBounds() {
		Result<Void> minResult = ConstraintMatchers.matchRange(5, Optional.of(Pair.of(5, true)), Optional.empty());
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = ConstraintMatchers.matchRange(10, Optional.empty(), Optional.of(Pair.of(10, true)));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void matchRangeWithExclusiveBounds() {
		Result<Void> minResult = ConstraintMatchers.matchRange(5, Optional.of(Pair.of(5, false)), Optional.empty());
		assertTrue(minResult.isError());
		assertTrue(minResult.errorOrThrow().contains("must be greater than"));
		Result<Void> maxResult = ConstraintMatchers.matchRange(10, Optional.empty(), Optional.of(Pair.of(10, false)));
		assertTrue(maxResult.isError());
		assertTrue(maxResult.errorOrThrow().contains("must be less than"));
	}
	
	@Test
	void matchRangeWithCustomComparator() {
		Result<Void> result = ConstraintMatchers.matchRange("b", Optional.of(Pair.of("a", true)), Optional.of(Pair.of("c", true)), String::compareTo);
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRangeWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRange(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRange(5, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRange(5, Optional.empty(), null));
	}
	
	@Test
	void matchFlagWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchFlag("test", Optional.empty(), s -> true, "error");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchFlagWithConditionTrue() {
		Result<Void> result = ConstraintMatchers.matchFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 4, "error");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchFlagWithConditionFalse() {
		Result<Void> result = ConstraintMatchers.matchFlag("test", Optional.of(Unit.INSTANCE), s -> s.length() == 5, "Length must be 5");
		assertTrue(result.isError());
		assertEquals("Length must be 5", result.errorOrThrow());
	}
	
	@Test
	void matchFlagWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchFlag(null, Optional.empty(), s -> true, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchFlag("test", null, s -> true, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchFlag("test", Optional.empty(), null, "error"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchFlag("test", Optional.empty(), s -> true, null));
	}
	
	@Test
	void matchSignWithPositive() {
		Result<Void> result = ConstraintMatchers.matchSign(5, Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(-5, Optional.of(false), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void matchSignWithNegative() {
		Result<Void> result = ConstraintMatchers.matchSign(-5, Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(5, Optional.empty(), Optional.of(false), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be negative"));
	}
	
	@Test
	void matchSignWithZero() {
		Result<Void> result = ConstraintMatchers.matchSign(0, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(5, Optional.empty(), Optional.empty(), Optional.of(false));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be zero"));
	}
	
	@Test
	void matchSignWithNonPositive() {
		Result<Void> result = ConstraintMatchers.matchSign(-5, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = ConstraintMatchers.matchSign(0, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(5, Optional.of(true), Optional.empty(), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-positive"));
	}
	
	@Test
	void matchSignWithNonNegative() {
		Result<Void> result = ConstraintMatchers.matchSign(5, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> zeroResult = ConstraintMatchers.matchSign(0, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(zeroResult.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(-5, Optional.empty(), Optional.of(true), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-negative"));
	}
	
	@Test
	void matchSignWithNonZero() {
		Result<Void> result = ConstraintMatchers.matchSign(5, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchSign(0, Optional.empty(), Optional.empty(), Optional.of(true));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be non-zero"));
	}
	
	@Test
	void matchSignWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchSign(null, Optional.empty(), Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchSign(5, null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchSign(5, Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchSign(5, Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void matchPercentageWithValidPercentage() {
		Result<Void> result = ConstraintMatchers.matchPercentage(50, Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
		Result<Void> minResult = ConstraintMatchers.matchPercentage(0, Optional.of(Unit.INSTANCE));
		assertTrue(minResult.isSuccess());
		Result<Void> maxResult = ConstraintMatchers.matchPercentage(100, Optional.of(Unit.INSTANCE));
		assertTrue(maxResult.isSuccess());
	}
	
	@Test
	void matchPercentageWithBelowZero() {
		Result<Void> result = ConstraintMatchers.matchPercentage(-1, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a percentage"));
	}
	
	@Test
	void matchPercentageWithAbove100() {
		Result<Void> result = ConstraintMatchers.matchPercentage(101, Optional.of(Unit.INSTANCE));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a percentage"));
	}
	
	@Test
	void matchPercentageWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchPercentage(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchPercentage(50, null));
	}
	
	@Test
	void matchParityWithEven() {
		Result<Void> result = ConstraintMatchers.matchParity(4, Optional.of(Unit.INSTANCE), Optional.empty());
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchParity(5, Optional.of(Unit.INSTANCE), Optional.empty());
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be even"));
	}
	
	@Test
	void matchParityWithOdd() {
		Result<Void> result = ConstraintMatchers.matchParity(5, Optional.empty(), Optional.of(Unit.INSTANCE));
		assertTrue(result.isSuccess());
		Result<Void> failResult = ConstraintMatchers.matchParity(4, Optional.empty(), Optional.of(Unit.INSTANCE));
		assertTrue(failResult.isError());
		assertTrue(failResult.errorOrThrow().contains("must be odd"));
	}
	
	@Test
	void matchParityWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchParity(4, null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchParity(4, Optional.empty(), null));
	}
	
	@Test
	void matchDivisibleByWithDivisible() {
		Result<Void> result = ConstraintMatchers.matchDivisibleBy(12, Optional.of(3L));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchDivisibleByWithNotDivisible() {
		Result<Void> result = ConstraintMatchers.matchDivisibleBy(13, Optional.of(3L));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be divisible by"));
	}
	
	@Test
	void matchDivisibleByWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchDivisibleBy(12, null));
	}
	
	@Test
	void matchPowerOfWithIsPower() {
		Result<Void> result = ConstraintMatchers.matchPowerOf(8, Optional.of(2));
		assertTrue(result.isSuccess());
		Result<Void> result27 = ConstraintMatchers.matchPowerOf(27, Optional.of(3));
		assertTrue(result27.isSuccess());
	}
	
	@Test
	void matchPowerOfWithNotPower() {
		Result<Void> result = ConstraintMatchers.matchPowerOf(10, Optional.of(2));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be a power of"));
	}
	
	@Test
	void matchPowerOfWithNegativeValues() {
		Result<Void> result = ConstraintMatchers.matchPowerOf(-8, Optional.of(2));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be positive"));
	}
	
	@Test
	void matchPowerOfWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchPowerOf(8, null));
	}
	
	@Test
	void matchStartsWithWithMatch() {
		Result<Void> result = ConstraintMatchers.matchStartsWith("hello world", Optional.of(Pair.of("hello", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchStartsWithWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchStartsWith("hello world", Optional.of(Pair.of("world", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must start with"));
	}
	
	@Test
	void matchStartsWithWithNegated() {
		Result<Void> result = ConstraintMatchers.matchStartsWith("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchStartsWith("hello world", Optional.of(Pair.of("hello", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not start with"));
	}
	
	@Test
	void matchStartsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchStartsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchStartsWith("test", null));
	}
	
	@Test
	void matchStartsWithAnyWithMatch() {
		Result<Void> result = ConstraintMatchers.matchStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hello"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchStartsWithAnyWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hi", "hey"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must start with one of"));
	}
	
	@Test
	void matchStartsWithAnyWithNegated() {
		Result<Void> result = ConstraintMatchers.matchStartsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "hi"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not start with any of"));
	}
	
	@Test
	void matchStartsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchStartsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchStartsWithAny("test", null));
	}
	
	@Test
	void matchContainsWithMatch() {
		Result<Void> result = ConstraintMatchers.matchContains("hello world", Optional.of(Pair.of("lo wo", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchContainsWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchContains("hello world", Optional.of(Pair.of("xyz", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain"));
	}
	
	@Test
	void matchContainsWithNegated() {
		Result<Void> result = ConstraintMatchers.matchContains("hello world", Optional.of(Pair.of("xyz", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchContains("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not contain"));
	}
	
	@Test
	void matchContainsWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContains(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContains("test", null));
	}
	
	@Test
	void matchContainsAnyWithMatch() {
		Result<Void> result = ConstraintMatchers.matchContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "world"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchContainsAnyWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchContainsAny("hello world", Optional.of(Pair.of(Set.of("xyz", "abc"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain at least one of"));
	}
	
	@Test
	void matchContainsAnyWithNegated() {
		Result<Void> result = ConstraintMatchers.matchContainsAny("hello world", Optional.of(Pair.of(Set.of("world", "hello"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not contain any of"));
	}
	
	@Test
	void matchContainsAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsAny("test", null));
	}
	
	@Test
	void matchContainsAllWithAllPresent() {
		Result<Void> result = ConstraintMatchers.matchContainsAll("hello world", Optional.of(Set.of("hello", "world")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchContainsAllWithMissing() {
		Result<Void> result = ConstraintMatchers.matchContainsAll("hello world", Optional.of(Set.of("hello", "xyz")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain all of"));
	}
	
	@Test
	void matchContainsAllWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsAll(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsAll("test", null));
	}
	
	@Test
	void matchContainsOnlyWithValid() {
		Result<Void> result = ConstraintMatchers.matchContainsOnly("abc", Optional.of(Set.of("a", "b", "c")));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchContainsOnlyWithInvalid() {
		Result<Void> result = ConstraintMatchers.matchContainsOnly("abcd", Optional.of(Set.of("a", "b", "c")));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain only characters from"));
	}
	
	@Test
	void matchContainsOnlyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsOnly(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchContainsOnly("test", null));
	}
	
	@Test
	void matchEndsWithWithMatch() {
		Result<Void> result = ConstraintMatchers.matchEndsWith("hello world", Optional.of(Pair.of("world", false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEndsWithWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchEndsWith("hello world", Optional.of(Pair.of("hello", false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must end with"));
	}
	
	@Test
	void matchEndsWithWithNegated() {
		Result<Void> result = ConstraintMatchers.matchEndsWith("hello world", Optional.of(Pair.of("hello", true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchEndsWith("hello world", Optional.of(Pair.of("world", true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not end with"));
	}
	
	@Test
	void matchEndsWithWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEndsWith(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEndsWith("test", null));
	}
	
	@Test
	void matchEndsWithAnyWithMatch() {
		Result<Void> result = ConstraintMatchers.matchEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEndsWithAnyWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchEndsWithAny("hello world", Optional.of(Pair.of(Set.of("hello", "test"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must end with one of"));
	}
	
	@Test
	void matchEndsWithAnyWithNegated() {
		Result<Void> result = ConstraintMatchers.matchEndsWithAny("hello world", Optional.of(Pair.of(Set.of("world", "test"), true)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not end with any of"));
	}
	
	@Test
	void matchEndsWithAnyWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEndsWithAny(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchEndsWithAny("test", null));
	}
	
	@Test
	void matchPatternWithMatch() {
		Result<Void> result = ConstraintMatchers.matchPattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false)));
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchPatternWithNoMatch() {
		Result<Void> result = ConstraintMatchers.matchPattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), false)));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must match pattern"));
	}
	
	@Test
	void matchPatternWithNegated() {
		Result<Void> result = ConstraintMatchers.matchPattern("hello", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true)));
		assertTrue(result.isSuccess());
		Result<Void> negatedResult = ConstraintMatchers.matchPattern("hello123", Optional.of(Pair.of(Pattern.compile("[a-z]+\\d+"), true)));
		assertTrue(negatedResult.isError());
		assertTrue(negatedResult.errorOrThrow().contains("must not match pattern"));
	}
	
	@Test
	void matchPatternWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchPattern(null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchPattern("test", null));
	}
	
	@Test
	void matchCharacterClassWithAllMatch() {
		Result<Void> result = ConstraintMatchers.matchCharacterClass("ABC", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchCharacterClassWithSomeFail() {
		Result<Void> result = ConstraintMatchers.matchCharacterClass("ABc", Optional.of(Unit.INSTANCE), Character::isUpperCase, "uppercase");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain only uppercase characters"));
	}
	
	@Test
	void matchCharacterClassWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCharacterClass(null, Optional.empty(), Character::isUpperCase, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCharacterClass("test", null, Character::isUpperCase, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCharacterClass("test", Optional.empty(), null, "uppercase"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchCharacterClass("test", Optional.empty(), Character::isUpperCase, null));
	}
	
	@Test
	void matchRequiredKeysWithAllPresent() {
		Result<Void> result = ConstraintMatchers.matchRequiredKeys(Set.of("a", "b", "c"), Optional.of(Set.of("a", "b")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchRequiredKeysWithMissing() {
		Result<Void> result = ConstraintMatchers.matchRequiredKeys(Set.of("a", "c"), Optional.of(Set.of("a", "b")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must contain required keys"));
	}
	
	@Test
	void matchRequiredKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRequiredKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRequiredKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchRequiredKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void matchForbiddenKeysWithNonePresent() {
		Result<Void> result = ConstraintMatchers.matchForbiddenKeys(Set.of("a", "b"), Optional.of(Set.of("c", "d")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchForbiddenKeysWithSomePresent() {
		Result<Void> result = ConstraintMatchers.matchForbiddenKeys(Set.of("a", "b", "c"), Optional.of(Set.of("c", "d")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must not contain forbidden keys"));
	}
	
	@Test
	void matchForbiddenKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchForbiddenKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchForbiddenKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchForbiddenKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void matchAllowedKeysWithAllAllowed() {
		Result<Void> result = ConstraintMatchers.matchAllowedKeys(Set.of("a", "b"), Optional.of(Set.of("a", "b", "c")), "Map");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchAllowedKeysWithDisallowed() {
		Result<Void> result = ConstraintMatchers.matchAllowedKeys(Set.of("a", "b", "d"), Optional.of(Set.of("a", "b", "c")), "Map");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("contains keys that are not allowed"));
	}
	
	@Test
	void matchAllowedKeysWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchAllowedKeys(null, Optional.empty(), "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchAllowedKeys(Set.of("a"), null, "Map"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchAllowedKeys(Set.of("a"), Optional.empty(), null));
	}
	
	@Test
	void matchNestedConfigWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchNestedConfig("test", Optional.empty(), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchNestedConfigWithPass() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		Result<Void> result = ConstraintMatchers.matchNestedConfig(5, Optional.of(config), "value");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchNestedConfigWithFail() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(10);
		Result<Void> result = ConstraintMatchers.matchNestedConfig(5, Optional.of(config), "value");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("constraint failed"));
	}
	
	@Test
	void matchNestedConfigWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchNestedConfig(null, Optional.empty(), "field"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchNestedConfig("test", null, "field"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchNestedConfig("test", Optional.empty(), null));
	}

	@Test
	void matchExtractedValueWithEmptyConfig() {
		List<String> list = List.of("a", "b", "c");
		Optional<SizeConstraintConfig> config = Optional.empty();
		assertTrue(ConstraintMatchers.matchExtractedValue(list, config, List::size, "size").isSuccess());
	}

	@Test
	void matchExtractedValueWithMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1).withMaxSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		assertTrue(ConstraintMatchers.matchExtractedValue(list, config, List::size, "size").isSuccess());
	}

	@Test
	void matchExtractedValueWithNonMatchingValue() {
		List<String> list = List.of("a", "b", "c");
		SizeConstraintConfig sizeConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(5);
		Optional<SizeConstraintConfig> config = Optional.of(sizeConfig);
		Result<Void> result = ConstraintMatchers.matchExtractedValue(list, config, List::size, "size");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("size constraint failed"));
	}

	@Test
	void matchExtractedValueWithArrayLength() {
		String[] array = new String[] { "a", "b" };
		LengthConstraintConfig lengthConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1).withMaxLength(3);
		Optional<LengthConstraintConfig> config = Optional.of(lengthConfig);
		assertTrue(ConstraintMatchers.matchExtractedValue(array, config, arr -> arr.length, "length").isSuccess());
	}

	@Test
	void matchExtractedValueWithNullValue() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchExtractedValue((List<String>) null, Optional.empty(), list -> list.size(), "size"));
	}

	@Test
	void matchExtractedValueWithNullConfig() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchExtractedValue(List.of(), null, List::size, "size"));
	}

	@Test
	void matchExtractedValueWithNullExtractor() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchExtractedValue(List.of(), Optional.empty(), null, "size"));
	}

	@Test
	void matchExtractedValueWithNullFieldName() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchExtractedValue(List.of(), Optional.empty(), List::size, null));
	}

	@Test
	void matchEnumFieldWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchEnumField(TestEnum.VALUE1, Optional.empty(), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEnumFieldWithPass() {
		EnumConstraintConfig<TestEnum> config = EnumConstraintConfig.<TestEnum>unconstrained().withEqualTo(TestEnum.VALUE1);
		Result<Void> result = ConstraintMatchers.matchEnumField(TestEnum.VALUE1, Optional.of(config), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchEnumFieldWithFail() {
		EnumConstraintConfig<TestEnum> config = EnumConstraintConfig.<TestEnum>unconstrained().withEqualTo(TestEnum.VALUE2);
		Result<Void> result = ConstraintMatchers.matchEnumField(TestEnum.VALUE1, Optional.of(config), "field");
		assertTrue(result.isError());
	}
	
	@Test
	void matchNumericFieldWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchNumericField(5, Optional.empty(), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchNumericFieldWithPass() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 10);
		Result<Void> result = ConstraintMatchers.matchNumericField(5, Optional.of(config), "field");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchNumericFieldWithFail() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(10, 20);
		Result<Void> result = ConstraintMatchers.matchNumericField(5, Optional.of(config), "field");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("constraint failed"));
	}
	
	@Test
	void matchNumericFieldWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchNumericField(5, null, "field"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchNumericField(5, Optional.empty(), null));
	}
	
	@Test
	void matchWithinLastWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchWithinLast(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchWithinLastWithPass() {
		Instant now = Instant.now();
		Instant recent = now.minusSeconds(30);
		Result<Void> result = ConstraintMatchers.matchWithinLast(recent, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchWithinLastWithFail() {
		Instant now = Instant.now();
		Instant old = now.minusSeconds(120);
		Result<Void> result = ConstraintMatchers.matchWithinLast(old, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.minus(d), "Instant");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within last"));
	}
	
	@Test
	void matchWithinLastWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinLast(null, Optional.empty(), Instant::now, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinLast(Instant.now(), null, Instant::now, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinLast(Instant.now(), Optional.empty(), null, (t, d) -> t.minus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinLast(Instant.now(), Optional.empty(), Instant::now, null, "Instant"));
	}
	
	@Test
	void matchWithinNextWithEmptyOptional() {
		Result<Void> result = ConstraintMatchers.matchWithinNext(Instant.now(), Optional.empty(), Instant::now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchWithinNextWithPass() {
		Instant now = Instant.now();
		Instant future = now.plusSeconds(30);
		Result<Void> result = ConstraintMatchers.matchWithinNext(future, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isSuccess());
	}
	
	@Test
	void matchWithinNextWithFail() {
		Instant now = Instant.now();
		Instant farFuture = now.plusSeconds(120);
		Result<Void> result = ConstraintMatchers.matchWithinNext(farFuture, Optional.of(Duration.ofMinutes(1)), () -> now, (t, d) -> t.plus(d), "Instant");
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("must be within next"));
	}
	
	@Test
	void matchWithinNextWithNullChecks() {
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinNext(null, Optional.empty(), Instant::now, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinNext(Instant.now(), null, Instant::now, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinNext(Instant.now(), Optional.empty(), null, (t, d) -> t.plus(d), "Instant"));
		assertThrows(NullPointerException.class, () -> ConstraintMatchers.matchWithinNext(Instant.now(), Optional.empty(), Instant::now, null, "Instant"));
	}
	
	private enum TestEnum {
		VALUE1, VALUE2
	}
}
