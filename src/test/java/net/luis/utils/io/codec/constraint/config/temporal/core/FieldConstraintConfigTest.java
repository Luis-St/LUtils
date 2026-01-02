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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FieldConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class FieldConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.of(Pair.of(0, true)), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.empty(), Optional.of(Pair.of(23, true)), Optional.empty()));
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(Pair.of(12, false))));
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(10, true)), Optional.empty()));
		assertDoesNotThrow(() -> new FieldConstraintConfig(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(5, true)), Optional.empty()));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new FieldConstraintConfig(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new FieldConstraintConfig(Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> new FieldConstraintConfig(Optional.empty(), Optional.empty(), null));
		assertThrows(NullPointerException.class, () -> new FieldConstraintConfig(null, null, null));
	}

	@Test
	void constructorMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> new FieldConstraintConfig(Optional.of(Pair.of(10, true)), Optional.of(Pair.of(5, true)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new FieldConstraintConfig(Optional.of(Pair.of(100, true)), Optional.of(Pair.of(1, true)), Optional.empty()));
	}

	@Test
	void constructorMinEqualMaxExclusiveFails() {
		assertThrows(IllegalArgumentException.class, () -> new FieldConstraintConfig(Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, true)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new FieldConstraintConfig(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(5, false)), Optional.empty()));
		assertThrows(IllegalArgumentException.class, () -> new FieldConstraintConfig(Optional.of(Pair.of(5, false)), Optional.of(Pair.of(5, false)), Optional.empty()));
	}

	@Test
	void unconstrainedConstant() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void isUnconstrained() {
		assertTrue(FieldConstraintConfig.UNCONSTRAINED.isUnconstrained());
		assertTrue(new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty()).isUnconstrained());
		assertFalse(FieldConstraintConfig.UNCONSTRAINED.withMin(0, true).isUnconstrained());
		assertFalse(FieldConstraintConfig.UNCONSTRAINED.withMax(23, true).isUnconstrained());
		assertFalse(FieldConstraintConfig.UNCONSTRAINED.withEquals(12, false).isUnconstrained());
	}

	@Test
	void withMinInclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(5, true);

		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMinExclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, false);

		assertTrue(config.min().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMinPreservesMax() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.empty(), Optional.of(Pair.of(20, true)), Optional.empty());
		FieldConstraintConfig config = initial.withMin(10, true);

		assertTrue(config.min().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(20, config.max().get().getFirst());
	}

	@Test
	void withMinPreservesEquals() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(Pair.of(12, false)));
		FieldConstraintConfig config = initial.withMin(5, true);

		assertTrue(config.min().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(12, config.equals().get().getFirst());
	}

	@Test
	void withMaxInclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(23, true);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isPresent());
		assertEquals(23, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMaxExclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(15, false);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isPresent());
		assertEquals(15, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMaxPreservesMin() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.of(Pair.of(5, true)), Optional.empty(), Optional.empty());
		FieldConstraintConfig config = initial.withMax(20, true);

		assertTrue(config.min().isPresent());
		assertEquals(5, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(20, config.max().get().getFirst());
	}

	@Test
	void withMaxPreservesEquals() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(Pair.of(12, false)));
		FieldConstraintConfig config = initial.withMax(23, true);

		assertTrue(config.max().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(12, config.equals().get().getFirst());
	}

	@Test
	void withRangeInclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);

		assertTrue(config.min().isPresent());
		assertEquals(9, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(17, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withRangeExclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(0, 60, false);

		assertTrue(config.min().isPresent());
		assertEquals(0, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(60, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withRangeReplacesExisting() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.of(Pair.of(1, true)), Optional.of(Pair.of(100, true)), Optional.empty());
		FieldConstraintConfig config = initial.withRange(10, 20, true);

		assertTrue(config.min().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(20, config.max().get().getFirst());
	}

	@Test
	void withRangePreservesEquals() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.empty(), Optional.empty(), Optional.of(Pair.of(15, false)));
		FieldConstraintConfig config = initial.withRange(10, 20, true);

		assertTrue(config.equals().isPresent());
		assertEquals(15, config.equals().get().getFirst());
	}

	@Test
	void withEqualsNormal() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, false);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isPresent());
		assertEquals(12, config.equals().get().getFirst());
		assertFalse(config.equals().get().getSecond());
	}

	@Test
	void withEqualsNegated() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(0, true);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isPresent());
		assertEquals(0, config.equals().get().getFirst());
		assertTrue(config.equals().get().getSecond());
	}

	@Test
	void withEqualsPreservesMinMax() {
		FieldConstraintConfig initial = new FieldConstraintConfig(Optional.of(Pair.of(5, true)), Optional.of(Pair.of(20, true)), Optional.empty());
		FieldConstraintConfig config = initial.withEquals(10, false);

		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(10, config.equals().get().getFirst());
	}

	@Test
	void matchesUnconstrained() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches("hour", 0).isSuccess());
		assertTrue(config.matches("hour", 12).isSuccess());
		assertTrue(config.matches("hour", 23).isSuccess());
		assertTrue(config.matches("minute", 30).isSuccess());
		assertTrue(config.matches("second", 59).isSuccess());
	}

	@Test
	void matchesMinInclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);

		assertTrue(config.matches("hour", 10).isSuccess());
		assertTrue(config.matches("hour", 11).isSuccess());
		assertTrue(config.matches("hour", 23).isSuccess());
	}

	@Test
	void matchesMinInclusiveFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);

		Result<Void> result = config.matches("hour", 9);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum constraint"));
		assertTrue(result.errorOrThrow().contains("hour"));
		assertTrue(result.errorOrThrow().contains("9"));
		assertTrue(result.errorOrThrow().contains("10"));
	}

	@Test
	void matchesMinExclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, false);

		assertTrue(config.matches("hour", 11).isSuccess());
		assertTrue(config.matches("hour", 15).isSuccess());
		assertTrue(config.matches("hour", 23).isSuccess());
	}

	@Test
	void matchesMinExclusiveFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, false);

		Result<Void> resultEqual = config.matches("hour", 10);
		assertTrue(resultEqual.isError());
		assertTrue(resultEqual.errorOrThrow().contains("Violated minimum constraint (exclusive)"));
		assertTrue(resultEqual.errorOrThrow().contains("hour"));

		Result<Void> resultBelow = config.matches("hour", 9);
		assertTrue(resultBelow.isError());
		assertTrue(resultBelow.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesMaxInclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, true);

		assertTrue(config.matches("hour", 0).isSuccess());
		assertTrue(config.matches("hour", 15).isSuccess());
		assertTrue(config.matches("hour", 20).isSuccess());
	}

	@Test
	void matchesMaxInclusiveFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, true);

		Result<Void> result = config.matches("hour", 21);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum constraint"));
		assertTrue(result.errorOrThrow().contains("hour"));
		assertTrue(result.errorOrThrow().contains("21"));
		assertTrue(result.errorOrThrow().contains("20"));
	}

	@Test
	void matchesMaxExclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, false);

		assertTrue(config.matches("hour", 0).isSuccess());
		assertTrue(config.matches("hour", 15).isSuccess());
		assertTrue(config.matches("hour", 19).isSuccess());
	}

	@Test
	void matchesMaxExclusiveFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, false);

		Result<Void> resultEqual = config.matches("hour", 20);
		assertTrue(resultEqual.isError());
		assertTrue(resultEqual.errorOrThrow().contains("Violated maximum constraint (exclusive)"));
		assertTrue(resultEqual.errorOrThrow().contains("hour"));

		Result<Void> resultAbove = config.matches("hour", 21);
		assertTrue(resultAbove.isError());
		assertTrue(resultAbove.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesRangeInclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);

		assertTrue(config.matches("hour", 9).isSuccess());
		assertTrue(config.matches("hour", 12).isSuccess());
		assertTrue(config.matches("hour", 17).isSuccess());
	}

	@Test
	void matchesRangeInclusiveFailureBelow() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);

		Result<Void> result = config.matches("hour", 8);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum constraint"));
	}

	@Test
	void matchesRangeInclusiveFailureAbove() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);

		Result<Void> result = config.matches("hour", 18);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum constraint"));
	}

	@Test
	void matchesRangeExclusiveSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, false);

		assertTrue(config.matches("hour", 10).isSuccess());
		assertTrue(config.matches("hour", 12).isSuccess());
		assertTrue(config.matches("hour", 16).isSuccess());
	}

	@Test
	void matchesRangeExclusiveFailureBoundaries() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, false);

		Result<Void> resultMin = config.matches("hour", 9);
		assertTrue(resultMin.isError());
		assertTrue(resultMin.errorOrThrow().contains("exclusive"));

		Result<Void> resultMax = config.matches("hour", 17);
		assertTrue(resultMax.isError());
		assertTrue(resultMax.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesEqualsSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, false);

		assertTrue(config.matches("hour", 12).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, false);

		Result<Void> result = config.matches("hour", 11);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(result.errorOrThrow().contains("not equal"));
		assertTrue(result.errorOrThrow().contains("hour"));
		assertTrue(result.errorOrThrow().contains("11"));
		assertTrue(result.errorOrThrow().contains("12"));
	}

	@Test
	void matchesNotEqualsSuccess() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, true);

		assertTrue(config.matches("hour", 0).isSuccess());
		assertTrue(config.matches("hour", 11).isSuccess());
		assertTrue(config.matches("hour", 13).isSuccess());
		assertTrue(config.matches("hour", 23).isSuccess());
	}

	@Test
	void matchesNotEqualsFailure() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, true);

		Result<Void> result = config.matches("hour", 12);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(result.errorOrThrow().contains("is equal"));
		assertTrue(result.errorOrThrow().contains("should not be"));
		assertTrue(result.errorOrThrow().contains("hour"));
		assertTrue(result.errorOrThrow().contains("12"));
	}

	@Test
	void matchesEqualsOverridesMinMax() {
		FieldConstraintConfig config = new FieldConstraintConfig(
			Optional.of(Pair.of(10, true)),
			Optional.of(Pair.of(20, true)),
			Optional.of(Pair.of(15, false))
		);

		assertTrue(config.matches("hour", 15).isSuccess());

		Result<Void> resultOther = config.matches("hour", 12);
		assertTrue(resultOther.isError());
		assertTrue(resultOther.errorOrThrow().contains("Violated equals constraint"));
	}

	@Test
	void matchesFieldNameRequired() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(5, true);
		assertThrows(NullPointerException.class, () -> config.matches(null, 10));
	}

	@Test
	void toStringUnconstrained() {
		String str = FieldConstraintConfig.UNCONSTRAINED.toString();
		assertEquals("FieldConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringMinInclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		String str = config.toString();
		assertTrue(str.contains("FieldConstraintConfig["));
		assertTrue(str.contains("min=10"));
		assertTrue(str.contains("(inclusive)"));
	}

	@Test
	void toStringMinExclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMin(10, false);
		String str = config.toString();
		assertTrue(str.contains("min=10"));
		assertTrue(str.contains("(exclusive)"));
	}

	@Test
	void toStringMaxInclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, true);
		String str = config.toString();
		assertTrue(str.contains("max=20"));
		assertTrue(str.contains("(inclusive)"));
	}

	@Test
	void toStringMaxExclusive() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withMax(20, false);
		String str = config.toString();
		assertTrue(str.contains("max=20"));
		assertTrue(str.contains("(exclusive)"));
	}

	@Test
	void toStringRange() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);
		String str = config.toString();
		assertTrue(str.contains("min=9"));
		assertTrue(str.contains("max=17"));
	}

	@Test
	void toStringEquals() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(12, false);
		String str = config.toString();
		assertTrue(str.contains("equals=12"));
		assertFalse(str.contains("(negated)"));
	}

	@Test
	void toStringNotEquals() {
		FieldConstraintConfig config = FieldConstraintConfig.UNCONSTRAINED.withEquals(0, true);
		String str = config.toString();
		assertTrue(str.contains("equals=0"));
		assertTrue(str.contains("(negated)"));
	}

	@Test
	void toStringMultipleConstraints() {
		FieldConstraintConfig config = new FieldConstraintConfig(
			Optional.of(Pair.of(5, true)),
			Optional.of(Pair.of(20, false)),
			Optional.of(Pair.of(10, true))
		);
		String str = config.toString();
		assertTrue(str.contains("min=5"));
		assertTrue(str.contains("max=20"));
		assertTrue(str.contains("equals=10"));
	}

	@Test
	void equality() {
		FieldConstraintConfig config1 = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		FieldConstraintConfig config2 = FieldConstraintConfig.UNCONSTRAINED.withMin(10, true);
		FieldConstraintConfig config3 = FieldConstraintConfig.UNCONSTRAINED.withMin(15, true);

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		FieldConstraintConfig config1 = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);
		FieldConstraintConfig config2 = FieldConstraintConfig.UNCONSTRAINED.withRange(9, 17, true);

		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
