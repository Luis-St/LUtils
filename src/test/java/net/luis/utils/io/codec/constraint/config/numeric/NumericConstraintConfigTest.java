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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NumericConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class NumericConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new NumericConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new NumericConstraintConfig<>(Optional.of(Pair.of(0, true)), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new NumericConstraintConfig<>(Optional.empty(), Optional.of(Pair.of(10, true)), Optional.empty()));
		assertDoesNotThrow(() -> new NumericConstraintConfig<>(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(10, true)), Optional.empty()));
		assertDoesNotThrow(() -> new NumericConstraintConfig<>(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(5, true)), Optional.empty()));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig<>(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig<>(Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig<>(Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void constructorMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig<>(Optional.of(Pair.of(10, true)), Optional.of(Pair.of(5, true)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig<>(Optional.of(Pair.of(100, true)), Optional.of(Pair.of(1, true)), Optional.empty()));
	}
	
	@Test
	void constructorExclusiveBounds() {
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig<>(Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig<>(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(5, false)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig<>(Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, false)), Optional.empty()));
	}
	
	@Test
	void unconstrainedMethod() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}
	
	@Test
	void isUnconstrained() {
		assertTrue(NumericConstraintConfig.unconstrained().isUnconstrained());
		assertFalse(NumericConstraintConfig.<Integer>unconstrained().withMin(5, true).isUnconstrained());
	}
	
	@Test
	void withMinInclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMin(5, true);
		
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isEmpty());
	}
	
	@Test
	void withMinExclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMin(5, false);
		
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withMinPreservesMax() {
		NumericConstraintConfig<Integer> initial = new NumericConstraintConfig<>(Optional.empty(), Optional.of(Pair.of(20, true)), Optional.empty());
		NumericConstraintConfig<Integer> config = initial.withMin(10, true);
		
		assertTrue(config.min().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(20, config.max().get().getFirst());
	}
	
	@Test
	void withMaxInclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMax(10, true);
		
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withMaxExclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMax(10, false);
		
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withMaxPreservesMin() {
		NumericConstraintConfig<Integer> initial = new NumericConstraintConfig<>(Optional.of(Pair.of(5, true)), Optional.empty(), Optional.empty());
		NumericConstraintConfig<Integer> config = initial.withMax(15, true);
		
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(15, config.max().get().getFirst());
	}
	
	@Test
	void withRange() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withRangeExclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, false);
		
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withEquals() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withEquals(7, false);
		
		assertTrue(config.equals().isPresent());
		assertEquals(7, config.equals().get().getFirst());
		assertFalse(config.equals().get().getSecond());
	}
	
	@Test
	void withEqualsNegated() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withEquals(7, true);
		
		assertTrue(config.equals().isPresent());
		assertEquals(7, config.equals().get().getFirst());
		assertTrue(config.equals().get().getSecond());
	}
	
	@Test
	void matchesUnconstrained() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.unconstrained();
		
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(Integer.MAX_VALUE).isSuccess());
		assertTrue(config.matches(Integer.MIN_VALUE).isSuccess());
	}
	
	@Test
	void matchesMinInclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMin(5, true);
		
		Result<Void> resultBelow = config.matches(4);
		assertTrue(resultBelow.isError());
		assertTrue(resultBelow.errorOrThrow().contains("Violated minimum constraint"));
		assertTrue(resultBelow.errorOrThrow().contains("value (4) is less than min (5), but it should be at least min"));
		
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(100).isSuccess());
	}
	
	@Test
	void matchesMinExclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMin(5, false);
		
		Result<Void> resultAtMin = config.matches(5);
		assertTrue(resultAtMin.isError());
		assertTrue(resultAtMin.errorOrThrow().contains("Violated minimum constraint (exclusive)"));
		assertTrue(resultAtMin.errorOrThrow().contains("(value) 5 is less than or equal to min (5), but it should be greater than min"));
		
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(100).isSuccess());
	}
	
	@Test
	void matchesMaxInclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMax(10, true);
		
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		
		Result<Void> resultAbove = config.matches(11);
		assertTrue(resultAbove.isError());
		assertTrue(resultAbove.errorOrThrow().contains("Violated maximum constraint"));
		assertTrue(resultAbove.errorOrThrow().contains("value (11) is greater than max (10), but it should be at most max"));
	}
	
	@Test
	void matchesMaxExclusive() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withMax(10, false);
		
		assertTrue(config.matches(9).isSuccess());
		
		Result<Void> resultAtMax = config.matches(10);
		assertTrue(resultAtMax.isError());
		assertTrue(resultAtMax.errorOrThrow().contains("Violated maximum constraint (exclusive)"));
		assertTrue(resultAtMax.errorOrThrow().contains("value (10) is greater than or equal to max (10), but it should be less than max"));
		
		Result<Void> resultAbove = config.matches(11);
		assertTrue(resultAbove.isError());
	}
	
	@Test
	void matchesRange() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		
		Result<Void> resultBelowMin = config.matches(4);
		assertTrue(resultBelowMin.isError());
		
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(7).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		
		Result<Void> resultAboveMax = config.matches(11);
		assertTrue(resultAboveMax.isError());
	}
	
	@Test
	void matchesEquals() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withEquals(7, false);
		
		Result<Void> resultNotEqual1 = config.matches(6);
		assertTrue(resultNotEqual1.isError());
		assertTrue(resultNotEqual1.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(resultNotEqual1.errorOrThrow().contains("value (6) is not equal to expected (7), but it should be"));
		
		assertTrue(config.matches(7).isSuccess());
		
		Result<Void> resultNotEqual2 = config.matches(8);
		assertTrue(resultNotEqual2.isError());
	}
	
	@Test
	void matchesEqualsNegated() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withEquals(7, true);
		
		assertTrue(config.matches(6).isSuccess());
		
		Result<Void> resultEqual = config.matches(7);
		assertTrue(resultEqual.isError());
		assertTrue(resultEqual.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(resultEqual.errorOrThrow().contains("value (7) is equal to expected (7), but it should not be"));
		
		assertTrue(config.matches(8).isSuccess());
	}
	
	@Test
	void toStringUnconstrained() {
		String str = NumericConstraintConfig.unconstrained().toString();
		assertEquals("NumericConstraintConfig[unconstrained]", str);
	}
	
	@Test
	void toStringWithConstraints() {
		NumericConstraintConfig<Integer> config = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		String str = config.toString();
		assertTrue(str.contains("NumericConstraintConfig["));
		assertTrue(str.contains("min=5 (inclusive)"));
		assertTrue(str.contains("max=10 (inclusive)"));
	}
	
	@Test
	void equality() {
		NumericConstraintConfig<Integer> config1 = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		NumericConstraintConfig<Integer> config2 = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		NumericConstraintConfig<Integer> config3 = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 15, true);
		
		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}
	
	@Test
	void hashCodeConsistency() {
		NumericConstraintConfig<Integer> config1 = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		NumericConstraintConfig<Integer> config2 = NumericConstraintConfig.<Integer>unconstrained().withRange(5, 10, true);
		
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
