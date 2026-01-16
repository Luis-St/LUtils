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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DepthConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class DepthConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new DepthConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withMinDepth(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withDepthBetween(10, 5));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new DepthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(42).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withNotEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1, 2, 3), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withNotIn(List.of(4, 5));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4, 5), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withMinDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withMaxDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withExactDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withExactDepth(7);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(7, config.min().get().getFirst());
		assertEquals(7, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withDepthBetween() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withDepthBetween(3, 8);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(3, config.min().get().getFirst());
		assertEquals(8, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withCustom() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withCustom(v -> v % 2 != 0 ? Result.error("Depth must be even") : Result.success());
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> DepthConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withEqualTo(42);
		assertTrue(config.matches(42).isSuccess());
		assertTrue(config.matches(43).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withNotEqualTo(42);
		assertTrue(config.matches(41).isSuccess());
		assertTrue(config.matches(42).isError());
	}
	
	@Test
	void matchesWithIn() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.matches(1).isSuccess());
		assertTrue(config.matches(2).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withNotIn(List.of(1, 2, 3));
		assertTrue(config.matches(4).isSuccess());
		assertTrue(config.matches(1).isError());
	}
	
	@Test
	void matchesWithMinDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(5);
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(6).isSuccess());
		assertTrue(config.matches(4).isError());
	}
	
	@Test
	void matchesWithMaxDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withMaxDepth(10);
		assertTrue(config.matches(10).isSuccess());
		assertTrue(config.matches(9).isSuccess());
		assertTrue(config.matches(11).isError());
	}
	
	@Test
	void matchesWithExactDepth() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withExactDepth(7);
		assertTrue(config.matches(7).isSuccess());
		assertTrue(config.matches(6).isError());
		assertTrue(config.matches(8).isError());
	}
	
	@Test
	void matchesWithDepthBetween() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED.withDepthBetween(3, 8);
		assertTrue(config.matches(3).isSuccess());
		assertTrue(config.matches(8).isSuccess());
		assertTrue(config.matches(5).isSuccess());
		assertTrue(config.matches(2).isError());
		assertTrue(config.matches(9).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED
			.withMinDepth(0)
			.withMaxDepth(100)
			.withNotIn(List.of(50));
		
		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(100).isSuccess());
		assertTrue(config.matches(49).isSuccess());
		assertTrue(config.matches(50).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		DepthConstraintConfig config = DepthConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
