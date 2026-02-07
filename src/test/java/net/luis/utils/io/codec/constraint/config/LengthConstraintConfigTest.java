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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LengthConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class LengthConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeMin() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withMinLength(-1));
	}
	
	@Test
	void constructWithNegativeMax() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withMaxLength(-1));
	}
	
	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withLengthBetween(10, 5));
	}
	
	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> new LengthConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(42));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(LengthConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1);
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withNotEqualTo(10);
		assertTrue(config.equalTo().isPresent());
		assertEquals(10, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1, 2, 3), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withNotIn(List.of(4, 5));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4, 5), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withMinLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5);
		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withMaxLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10);
		assertTrue(config.max().isPresent());
		assertEquals(10, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withExactLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withExactLength(7);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(7, config.min().get().getFirst());
		assertEquals(7, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withLengthBetween() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withLengthBetween(3, 8);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(3, config.min().get().getFirst());
		assertEquals(8, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withCustom() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withCustom(v -> {
			if (v % 2 != 0) throw new ConstraintViolateException("Length must be even");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> LengthConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withEqualTo(42);
		assertDoesNotThrow(() -> config.validate(42));
		assertThrows(ConstraintViolateException.class, () -> config.validate(43));
	}
	
	@Test
	void validateWithNotEqualTo() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withNotEqualTo(42);
		assertDoesNotThrow(() -> config.validate(41));
		assertThrows(ConstraintViolateException.class, () -> config.validate(42));
	}
	
	@Test
	void validateWithIn() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withIn(List.of(1, 2, 3));
		assertDoesNotThrow(() -> config.validate(1));
		assertDoesNotThrow(() -> config.validate(2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(4));
	}
	
	@Test
	void validateWithNotIn() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withNotIn(List.of(1, 2, 3));
		assertDoesNotThrow(() -> config.validate(4));
		assertThrows(ConstraintViolateException.class, () -> config.validate(1));
	}
	
	@Test
	void validateWithMinLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMinLength(5);
		assertDoesNotThrow(() -> config.validate(5));
		assertDoesNotThrow(() -> config.validate(6));
		assertThrows(ConstraintViolateException.class, () -> config.validate(4));
	}
	
	@Test
	void validateWithMaxLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withMaxLength(10);
		assertDoesNotThrow(() -> config.validate(10));
		assertDoesNotThrow(() -> config.validate(9));
		assertThrows(ConstraintViolateException.class, () -> config.validate(11));
	}
	
	@Test
	void validateWithExactLength() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withExactLength(7);
		assertDoesNotThrow(() -> config.validate(7));
		assertThrows(ConstraintViolateException.class, () -> config.validate(6));
		assertThrows(ConstraintViolateException.class, () -> config.validate(8));
	}
	
	@Test
	void validateWithLengthBetween() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED.withLengthBetween(3, 8);
		assertDoesNotThrow(() -> config.validate(3));
		assertDoesNotThrow(() -> config.validate(8));
		assertDoesNotThrow(() -> config.validate(5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(9));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED
			.withMinLength(0)
			.withMaxLength(100)
			.withNotIn(List.of(50));
		
		assertDoesNotThrow(() -> config.validate(0));
		assertDoesNotThrow(() -> config.validate(100));
		assertDoesNotThrow(() -> config.validate(49));
		assertThrows(ConstraintViolateException.class, () -> config.validate(50));
	}
	
	@Test
	void validateWithNullValue() {
		LengthConstraintConfig config = LengthConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
