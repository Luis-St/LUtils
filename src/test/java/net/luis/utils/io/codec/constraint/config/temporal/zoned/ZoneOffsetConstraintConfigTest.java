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

package net.luis.utils.io.codec.constraint.config.temporal.zoned;

import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneOffsetConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class ZoneOffsetConstraintConfigTest {
	
	private static final ZoneOffset UTC = ZoneOffset.UTC;
	private static final ZoneOffset PLUS_2 = ZoneOffset.ofHours(2);
	private static final ZoneOffset PLUS_5 = ZoneOffset.ofHours(5);
	private static final ZoneOffset MINUS_5 = ZoneOffset.ofHours(-5);
	private static final ZoneOffset MINUS_8 = ZoneOffset.ofHours(-8);
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullHours() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new ZoneOffsetConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithBothPositiveAndNegative() {
		assertThrows(IllegalArgumentException.class, () -> new ZoneOffsetConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.of(false),
			Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.hour().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(UTC));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(ZoneOffsetConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withPositive();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withEqualTo(PLUS_2);
		assertTrue(config.equalTo().isPresent());
		assertEquals(PLUS_2, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotEqualTo(PLUS_2);
		assertTrue(config.equalTo().isPresent());
		assertEquals(PLUS_2, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withIn(List.of(UTC, PLUS_2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(UTC, PLUS_2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotIn(List.of(UTC, PLUS_2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(UTC, PLUS_2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withGreaterThan() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThan(MINUS_5);
		assertTrue(config.min().isPresent());
		assertEquals(MINUS_5, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThan(null));
	}
	
	@Test
	void withGreaterThanOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(MINUS_5);
		assertTrue(config.min().isPresent());
		assertEquals(MINUS_5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void withGreaterThanOrEqualNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(null));
	}
	
	@Test
	void withLessThan() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThan(PLUS_5);
		assertTrue(config.max().isPresent());
		assertEquals(PLUS_5, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThan(null));
	}
	
	@Test
	void withLessThanOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(PLUS_5);
		assertTrue(config.max().isPresent());
		assertEquals(PLUS_5, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withLessThanOrEqualNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(null));
	}
	
	@Test
	void withBetween() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetween(MINUS_5, PLUS_5);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(MINUS_5, config.min().get().getFirst());
		assertEquals(PLUS_5, config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenNullMin() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetween(null, PLUS_5));
	}
	
	@Test
	void withBetweenNullMax() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetween(MINUS_5, null));
	}
	
	@Test
	void withBetweenOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(MINUS_5, PLUS_5);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(MINUS_5, config.min().get().getFirst());
		assertEquals(PLUS_5, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqualNullMin() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(null, PLUS_5));
	}
	
	@Test
	void withBetweenOrEqualNullMax() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(MINUS_5, null));
	}
	
	@Test
	void withPositive() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}
	
	@Test
	void withNonPositive() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}
	
	@Test
	void withNegative() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}
	
	@Test
	void withNonNegative() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}
	
	@Test
	void withZero() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withUtc() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withUtc();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}
	
	@Test
	void withNonZero() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}
	
	@Test
	void withHours() {
		NumericConstraintConfig hoursConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(-5, 5);
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withHour(hoursConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hoursConfig, config.hour().get());
	}
	
	@Test
	void withHoursNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withHour(null));
	}
	
	@Test
	void withCustom() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withCustom(offset -> {
			if (offset.getTotalSeconds() % 3600 != 0) throw new ConstraintViolateException("Offset must be on the hour");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> ZoneOffsetConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withEqualTo(PLUS_2);
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_5));
	}
	
	@Test
	void validateWithNotEqualTo() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotEqualTo(PLUS_2);
		assertDoesNotThrow(() -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithIn() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withIn(List.of(UTC, PLUS_2));
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_5));
	}
	
	@Test
	void validateWithNotIn() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNotIn(List.of(UTC, PLUS_2));
		assertDoesNotThrow(() -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithGreaterThan() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThan(UTC);
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertDoesNotThrow(() -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithGreaterThanOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(UTC);
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithLessThan() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThan(UTC);
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertDoesNotThrow(() -> config.validate(MINUS_8));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithLessThanOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withLessThanOrEqual(UTC);
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithBetween() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetween(MINUS_5, PLUS_2);
		assertDoesNotThrow(() -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_8));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(MINUS_5, PLUS_2);
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_8));
	}
	
	@Test
	void validateWithPositive() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withPositive();
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertDoesNotThrow(() -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithNonPositive() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonPositive();
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithNegative() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNegative();
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertDoesNotThrow(() -> config.validate(MINUS_8));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
	}
	
	@Test
	void validateWithNonNegative() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonNegative();
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithZero() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		assertDoesNotThrow(() -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithNonZero() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withNonZero();
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
	}
	
	@Test
	void validateWithHoursConstraint() {
		NumericConstraintConfig hoursConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(-2, 2);
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED.withHour(hoursConfig);
		assertDoesNotThrow(() -> config.validate(UTC));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_5));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED
			.withGreaterThanOrEqual(MINUS_5)
			.withLessThanOrEqual(PLUS_2)
			.withNotIn(List.of(UTC));
		
		assertDoesNotThrow(() -> config.validate(MINUS_5));
		assertDoesNotThrow(() -> config.validate(PLUS_2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(PLUS_5));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MINUS_8));
	}
	
	@Test
	void validateWithNullValue() {
		ZoneOffsetConstraintConfig config = ZoneOffsetConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
