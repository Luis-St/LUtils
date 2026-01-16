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

package net.luis.utils.io.codec.constraint_new.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.luis.utils.util.Pair;

import java.util.*;

/**
 * Test class for {@link NumericFieldConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class NumericFieldConstraintConfigTest {

	@Test
	void unconstrained() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(42).isSuccess());
	}

	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new NumericFieldConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new NumericFieldConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new NumericFieldConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new NumericFieldConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}

	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new NumericFieldConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}

	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new NumericFieldConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(10, 5));
	}

	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> NumericFieldConstraintConfig.UNCONSTRAINED.withBetween(5, 5));
	}

	@Test
	void withEqualTo() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualTo() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withNotEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withIn() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1, 2, 3), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withNotIn() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withNotIn(List.of(4, 5));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4, 5), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withGreaterThan() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThan(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}

	@Test
	void withGreaterThanOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}

	@Test
	void withLessThan() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withLessThan(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}

	@Test
	void withLessThanOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}

	@Test
	void withBetween() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetween(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}

	@Test
	void withBetweenOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 10);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1, config.min().get().getFirst());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}

	@Test
	void matchesWithEqualTo() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(42);
		assertTrue(config.matches(42).isSuccess());
		assertTrue(config.matches(43).isError());
	}

	@Test
	void matchesWithNotEqualTo() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withNotEqualTo(42);
		assertTrue(config.matches(41).isSuccess());
		assertTrue(config.matches(42).isError());
	}

	@Test
	void matchesWithIn() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(4).isError());
	}

	@Test
	void matchesWithNotIn() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withNotIn(List.of(1, 2, 3));
		assertTrue(config.matches(4).isSuccess());
		assertTrue(config.matches(1).isError());
	}

	@Test
	void matchesWithGreaterThan() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThan(5);
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(5).isError());
		assertTrue(config.matches(4).isError());
	}

	@Test
	void matchesWithGreaterThanOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(5);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(4).isError());
	}

	@Test
	void matchesWithLessThan() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withLessThan(10);
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(10).isError());
		assertTrue(config.matches(11).isError());
	}

	@Test
	void matchesWithLessThanOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(10);
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(11).isError());
	}

	@Test
	void matchesWithBetween() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetween(1, 10);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(1).isError());
		assertTrue(config.matches(10).isError());
	}

	@Test
	void matchesWithBetweenOrEqual() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(1, 10);
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(0).isError());
		assertTrue(config.matches(11).isError());
	}

	@Test
	void matchesWithMultipleConstraints() {
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED
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
		NumericFieldConstraintConfig config = NumericFieldConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
