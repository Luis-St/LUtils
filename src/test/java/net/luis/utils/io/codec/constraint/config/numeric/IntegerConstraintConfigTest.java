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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IntegerConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class IntegerConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPercentage() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullEven() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullOdd() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullDivisibleBy() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullPowerOf() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withBetweenOrEqual(10, 5));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withBetween(5, 5));
	}
	
	@Test
	void constructWithEvenAndOdd() {
		assertThrows(IllegalArgumentException.class, () -> new IntegerConstraintConfig<Integer>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNonPositiveDivisibleBy() {
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withDivisibleBy(0));
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withDivisibleBy(-1));
	}
	
	@Test
	void constructWithInvalidPowerOf() {
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withPowerOf(0));
		assertThrows(IllegalArgumentException.class, () -> IntegerConstraintConfig.<Integer>unconstrained().withPowerOf(1));
	}
	
	@Test
	void unconstrained() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.percentage().isEmpty());
		assertTrue(config.even().isEmpty());
		assertTrue(config.odd().isEmpty());
		assertTrue(config.divisibleBy().isEmpty());
		assertTrue(config.powerOf().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(42).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNotEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withIn(List.of(1, 2, 3));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1, 2, 3), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNotIn(List.of(4, 5));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4, 5), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withGreaterThan() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withGreaterThan(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withGreaterThanOrEqual(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withLessThan(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withLessThanOrEqual(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withBetween(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withBetweenOrEqual(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withPositive() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}
	
	@Test
	void withNonPositive() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}
	
	@Test
	void withNegative() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}
	
	@Test
	void withNonNegative() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}
	
	@Test
	void withZero() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withNonZero() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}
	
	@Test
	void withPercentage() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPercentage();
		assertTrue(config.percentage().isPresent());
		assertEquals(Unit.INSTANCE, config.percentage().get());
	}
	
	@Test
	void withEven() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEven();
		assertTrue(config.even().isPresent());
		assertEquals(Unit.INSTANCE, config.even().get());
	}
	
	@Test
	void withOdd() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withOdd();
		assertTrue(config.odd().isPresent());
		assertEquals(Unit.INSTANCE, config.odd().get());
	}
	
	@Test
	void withDivisibleBy() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withDivisibleBy(5);
		assertTrue(config.divisibleBy().isPresent());
		assertEquals(5L, config.divisibleBy().get());
	}
	
	@Test
	void withPowerOfTwo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerOfTwo();
		assertTrue(config.powerOf().isPresent());
		assertEquals(2, config.powerOf().get());
	}
	
	@Test
	void withPowerOf() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerOf(3);
		assertTrue(config.powerOf().isPresent());
		assertEquals(3, config.powerOf().get());
	}
	
	@Test
	void matchesWithEqualTo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEqualTo(42);
		assertTrue(config.matches(42).isSuccess());
		assertTrue(config.matches(43).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNotEqualTo(42);
		assertTrue(config.matches(41).isSuccess());
		assertTrue(config.matches(42).isError());
	}
	
	@Test
	void matchesWithIn() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withIn(List.of(1, 2, 3));
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNotIn(List.of(1, 2, 3));
		assertTrue(config.matches(4).isSuccess());
		assertTrue(config.matches(1).isError());
	}
	
	@Test
	void matchesWithGreaterThan() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withGreaterThan(5);
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(5).isError());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithGreaterThanOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withGreaterThanOrEqual(5);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithLessThan() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withLessThan(10);
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(10).isError());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithLessThanOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withLessThanOrEqual(10);
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithBetween() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withBetween(1, 10);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(10).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withBetweenOrEqual(1, 10);
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithPositive() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPositive();
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(-1).isError());
	}
	
	@Test
	void matchesWithNonPositive() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonPositive();
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(-1).isSuccess());
		assertTrue(config.matches(1).isError());
	}
	
	@Test
	void matchesWithNegative() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNegative();
		assertTrue(config.matches(-1).isSuccess());
		assertTrue(config.matches(-100).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(1).isError());
	}
	
	@Test
	void matchesWithNonNegative() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonNegative();
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(-1).isError());
	}
	
	@Test
	void matchesWithZero() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withZero();
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(-1).isError());
	}
	
	@Test
	void matchesWithNonZero() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withNonZero();
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(-1).isSuccess());
		assertTrue(config.matches(0).isError());
	}
	
	@Test
	void matchesWithPercentage() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPercentage();
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(50).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(-1).isError());
		assertTrue(config.matches(101).isError());
	}
	
	@Test
	void matchesWithEven() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withEven();
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(-4).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(-3).isError());
	}
	
	@Test
	void matchesWithOdd() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withOdd();
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(-3).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(2).isError());
	}
	
	@Test
	void matchesWithDivisibleBy() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withDivisibleBy(5);
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(-15).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(7).isError());
	}
	
	@Test
	void matchesWithPowerOfTwo() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerOfTwo();
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(4).isSuccess());
		assertTrue(config.matches(8).isSuccess());
		assertTrue(config.matches(16).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(3).isError());
		assertTrue(config.matches(5).isError());
	}
	
	@Test
	void matchesWithPowerOf() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained().withPowerOf(3);
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(3).isSuccess());
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(27).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(2).isError());
		assertTrue(config.matches(6).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.<Integer>unconstrained()
			.withPositive()
			.withEven()
			.withLessThanOrEqual(100);
		
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(50).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(102).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		IntegerConstraintConfig<Integer> config = IntegerConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
