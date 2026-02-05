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
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NumericConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class NumericConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new NumericConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(10, 5));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> NumericConstraintConfig.UNCONSTRAINED.withBetween(5, 5));
	}
	
	@Test
	void unconstrained() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(42).isSuccess());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(NumericConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(10);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualTo() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withNotEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1, 2, 3), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withNotIn(List.of(4, 5));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4, 5), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withGreaterThan() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThan(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withLessThan() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withLessThan(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetween() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetween(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void matchesWithEqualTo() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(42);
		assertTrue(config.matches(42).isSuccess());
		assertTrue(config.matches(43).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withNotEqualTo(42);
		assertTrue(config.matches(41).isSuccess());
		assertTrue(config.matches(42).isError());
	}
	
	@Test
	void matchesWithIn() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withNotIn(List.of(1, 2, 3));
		assertTrue(config.matches(4).isSuccess());
		assertTrue(config.matches(1).isError());
	}
	
	@Test
	void matchesWithGreaterThan() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThan(5);
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(5).isError());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithGreaterThanOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(5);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithLessThan() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withLessThan(10);
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(10).isError());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithLessThanOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(10);
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithBetween() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetween(1, 10);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(10).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 10);
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED
			.withGreaterThanOrEqual(0)
			.withLessThanOrEqual(100)
			.withNotIn(List.of(50));
		
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(49).isSuccess());
		assertTrue(config.matches(50).isError());
		assertTrue(config.matches(-1).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		NumericConstraintConfig config = NumericConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
