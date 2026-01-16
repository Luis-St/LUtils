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

package net.luis.utils.io.codec.constraint_new.config.numeric;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.luis.utils.io.codec.constraint_new.core.Unit;
import net.luis.utils.util.Pair;

import java.util.*;

/**
 * Test class for {@link DecimalConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class DecimalConstraintConfigTest {

	@Test
	void unconstrained() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.positive().isEmpty());
		assertTrue(config.negative().isEmpty());
		assertTrue(config.zero().isEmpty());
		assertTrue(config.percentage().isEmpty());
		assertTrue(config.finite().isEmpty());
		assertTrue(config.notNaN().isEmpty());
		assertTrue(config.integral().isEmpty());
		assertTrue(config.normalized().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(3.14).isSuccess());
	}

	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMin() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullPositive() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNegative() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullZero() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullPercentage() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullFinite() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNotNaN() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIntegral() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNormalized() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}

	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}

	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new DecimalConstraintConfig<Double>(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> DecimalConstraintConfig.<Double>unconstrained().withBetweenOrEqual(10.0, 5.0));
	}

	@Test
	void constructWithEqualMinMaxExclusiveBound() {
		assertThrows(IllegalArgumentException.class, () -> DecimalConstraintConfig.<Double>unconstrained().withBetween(5.0, 5.0));
	}

	@Test
	void withEqualTo() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withEqualTo(3.14);
		assertTrue(config.equalTo().isPresent());
		assertEquals(3.14, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualTo() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotEqualTo(3.14);
		assertTrue(config.equalTo().isPresent());
		assertEquals(3.14, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withIn() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIn(List.of(1.0, 2.0, 3.0));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(1.0, 2.0, 3.0), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withNotIn() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotIn(List.of(4.0, 5.0));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(4.0, 5.0), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withGreaterThan() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withGreaterThan(5.0);
		assertTrue(config.min().isPresent());
		assertEquals(5.0, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
	}

	@Test
	void withGreaterThanOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withGreaterThanOrEqual(5.0);
		assertTrue(config.min().isPresent());
		assertEquals(5.0, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
	}

	@Test
	void withLessThan() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withLessThan(10.0);
		assertTrue(config.max().isPresent());
		assertEquals(10.0, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
	}

	@Test
	void withLessThanOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withLessThanOrEqual(10.0);
		assertTrue(config.max().isPresent());
		assertEquals(10.0, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
	}

	@Test
	void withBetween() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withBetween(1.0, 10.0);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1.0, config.min().get().getFirst());
		assertEquals(10.0, config.max().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertFalse(config.max().get().getSecond());
	}

	@Test
	void withBetweenOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withBetweenOrEqual(1.0, 10.0);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(1.0, config.min().get().getFirst());
		assertEquals(10.0, config.max().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().get().getSecond());
	}

	@Test
	void withPositive() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withPositive();
		assertTrue(config.positive().isPresent());
		assertFalse(config.positive().get());
	}

	@Test
	void withNonPositive() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonPositive();
		assertTrue(config.positive().isPresent());
		assertTrue(config.positive().get());
	}

	@Test
	void withNegative() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNegative();
		assertTrue(config.negative().isPresent());
		assertFalse(config.negative().get());
	}

	@Test
	void withNonNegative() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonNegative();
		assertTrue(config.negative().isPresent());
		assertTrue(config.negative().get());
	}

	@Test
	void withZero() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withZero();
		assertTrue(config.zero().isPresent());
		assertFalse(config.zero().get());
	}

	@Test
	void withNonZero() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonZero();
		assertTrue(config.zero().isPresent());
		assertTrue(config.zero().get());
	}

	@Test
	void withPercentage() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withPercentage();
		assertTrue(config.percentage().isPresent());
		assertEquals(Unit.INSTANCE, config.percentage().get());
	}

	@Test
	void withFinite() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		assertTrue(config.finite().isPresent());
		assertEquals(Unit.INSTANCE, config.finite().get());
	}

	@Test
	void withNotNaN() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotNaN();
		assertTrue(config.notNaN().isPresent());
		assertEquals(Unit.INSTANCE, config.notNaN().get());
	}

	@Test
	void withIntegral() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIntegral();
		assertTrue(config.integral().isPresent());
		assertEquals(Unit.INSTANCE, config.integral().get());
	}

	@Test
	void withNormalized() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNormalized();
		assertTrue(config.normalized().isPresent());
		assertEquals(Unit.INSTANCE, config.normalized().get());
	}

	@Test
	void matchesWithEqualTo() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withEqualTo(3.14);
		assertTrue(config.matches(3.14).isSuccess());
		assertTrue(config.matches(3.15).isError());
	}

	@Test
	void matchesWithNotEqualTo() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotEqualTo(3.14);
		assertTrue(config.matches(3.15).isSuccess());
		assertTrue(config.matches(3.14).isError());
	}

	@Test
	void matchesWithIn() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIn(List.of(1.0, 2.0, 3.0));
		assertTrue(config.matches(1.0).isSuccess());
		assertTrue(config.matches(2.0).isSuccess());
		assertTrue(config.matches(4.0).isError());
	}

	@Test
	void matchesWithNotIn() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotIn(List.of(1.0, 2.0, 3.0));
		assertTrue(config.matches(4.0).isSuccess());
		assertTrue(config.matches(1.0).isError());
	}

	@Test
	void matchesWithGreaterThan() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withGreaterThan(5.0);
		assertTrue(config.matches(5.1).isSuccess());
		assertTrue(config.matches(5.0).isError());
		assertTrue(config.matches(4.9).isError());
	}

	@Test
	void matchesWithGreaterThanOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withGreaterThanOrEqual(5.0);
		assertTrue(config.matches(5.0).isSuccess());
		assertTrue(config.matches(5.1).isSuccess());
		assertTrue(config.matches(4.9).isError());
	}

	@Test
	void matchesWithLessThan() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withLessThan(10.0);
		assertTrue(config.matches(9.9).isSuccess());
		assertTrue(config.matches(10.0).isError());
		assertTrue(config.matches(10.1).isError());
	}

	@Test
	void matchesWithLessThanOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withLessThanOrEqual(10.0);
		assertTrue(config.matches(10.0).isSuccess());
		assertTrue(config.matches(9.9).isSuccess());
		assertTrue(config.matches(10.1).isError());
	}

	@Test
	void matchesWithBetween() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withBetween(1.0, 10.0);
		assertTrue(config.matches(5.0).isSuccess());
		assertTrue(config.matches(1.0).isError());
		assertTrue(config.matches(10.0).isError());
	}

	@Test
	void matchesWithBetweenOrEqual() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withBetweenOrEqual(1.0, 10.0);
		assertTrue(config.matches(1.0).isSuccess());
		assertTrue(config.matches(10.0).isSuccess());
		assertTrue(config.matches(5.0).isSuccess());
		assertTrue(config.matches(0.9).isError());
		assertTrue(config.matches(10.1).isError());
	}

	@Test
	void matchesWithPositive() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withPositive();
		assertTrue(config.matches(0.1).isSuccess());
		assertTrue(config.matches(100.0).isSuccess());
		assertTrue(config.matches(0.0).isError());
		assertTrue(config.matches(-0.1).isError());
	}

	@Test
	void matchesWithNonPositive() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonPositive();
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(-0.1).isSuccess());
		assertTrue(config.matches(0.1).isError());
	}

	@Test
	void matchesWithNegative() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNegative();
		assertTrue(config.matches(-0.1).isSuccess());
		assertTrue(config.matches(-100.0).isSuccess());
		assertTrue(config.matches(0.0).isError());
		assertTrue(config.matches(0.1).isError());
	}

	@Test
	void matchesWithNonNegative() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonNegative();
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(0.1).isSuccess());
		assertTrue(config.matches(-0.1).isError());
	}

	@Test
	void matchesWithZero() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withZero();
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(0.1).isError());
		assertTrue(config.matches(-0.1).isError());
	}

	@Test
	void matchesWithNonZero() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNonZero();
		assertTrue(config.matches(0.1).isSuccess());
		assertTrue(config.matches(-0.1).isSuccess());
		assertTrue(config.matches(0.0).isError());
	}

	@Test
	void matchesWithPercentage() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withPercentage();
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(50.0).isSuccess());
		assertTrue(config.matches(100.0).isSuccess());
		assertTrue(config.matches(-0.1).isError());
		assertTrue(config.matches(100.1).isError());
	}

	@Test
	void matchesWithFinite() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withFinite();
		assertTrue(config.matches(3.14).isSuccess());
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(Double.POSITIVE_INFINITY).isError());
		assertTrue(config.matches(Double.NEGATIVE_INFINITY).isError());
		assertTrue(config.matches(Double.NaN).isError());
	}

	@Test
	void matchesWithNotNaN() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNotNaN();
		assertTrue(config.matches(3.14).isSuccess());
		assertTrue(config.matches(Double.POSITIVE_INFINITY).isSuccess());
		assertTrue(config.matches(Double.NaN).isError());
	}

	@Test
	void matchesWithIntegral() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withIntegral();
		assertTrue(config.matches(1.0).isSuccess());
		assertTrue(config.matches(42.0).isSuccess());
		assertTrue(config.matches(-5.0).isSuccess());
		assertTrue(config.matches(1.5).isError());
		assertTrue(config.matches(3.14).isError());
	}

	@Test
	void matchesWithNormalized() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained().withNormalized();
		assertTrue(config.matches(0.0).isSuccess());
		assertTrue(config.matches(0.5).isSuccess());
		assertTrue(config.matches(1.0).isSuccess());
		assertTrue(config.matches(-0.1).isError());
		assertTrue(config.matches(1.1).isError());
	}

	@Test
	void matchesWithMultipleConstraints() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.<Double>unconstrained()
			.withPositive()
			.withFinite()
			.withLessThanOrEqual(100.0);

		assertTrue(config.matches(0.1).isSuccess());
		assertTrue(config.matches(50.0).isSuccess());
		assertTrue(config.matches(100.0).isSuccess());
		assertTrue(config.matches(0.0).isError());
		assertTrue(config.matches(-1.0).isError());
		assertTrue(config.matches(Double.POSITIVE_INFINITY).isError());
	}

	@Test
	void matchesWithNullValue() {
		DecimalConstraintConfig<Double> config = DecimalConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
