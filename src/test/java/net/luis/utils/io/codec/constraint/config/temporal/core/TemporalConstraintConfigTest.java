/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TemporalConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class TemporalConstraintConfigTest {

	@Test
	void constructor() {
		assertDoesNotThrow(() -> new TemporalConstraintConfig<LocalDate>(Optional.empty(), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new TemporalConstraintConfig<>(Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)), Optional.empty(), Optional.empty()));
		assertDoesNotThrow(() -> new TemporalConstraintConfig<>(Optional.empty(), Optional.of(Pair.of(LocalDate.of(2030, 12, 31), true)), Optional.empty()));
		assertDoesNotThrow(() -> new TemporalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.of(Pair.of(LocalDate.of(2024, 6, 15), false))));
		assertDoesNotThrow(() -> new TemporalConstraintConfig<>(Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)), Optional.of(Pair.of(LocalDate.of(2030, 12, 31), true)), Optional.empty()));
		assertDoesNotThrow(() -> new TemporalConstraintConfig<>(Optional.of(Pair.of(LocalDate.of(2024, 1, 1), true)), Optional.of(Pair.of(LocalDate.of(2024, 1, 1), true)), Optional.empty()));
	}

	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new TemporalConstraintConfig<LocalDate>(null, Optional.empty(), Optional.empty()));
		assertThrows(NullPointerException.class, () -> new TemporalConstraintConfig<LocalDate>(Optional.empty(), null, Optional.empty()));
		assertThrows(NullPointerException.class, () -> new TemporalConstraintConfig<LocalDate>(Optional.empty(), Optional.empty(), null));
		assertThrows(NullPointerException.class, () -> new TemporalConstraintConfig<LocalDate>(null, null, null));
	}

	@Test
	void constructorMinGreaterThanMax() {
		assertThrows(IllegalArgumentException.class, () -> new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalDate.of(2030, 1, 1), true)),
			Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)),
			Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalTime.of(18, 0), true)),
			Optional.of(Pair.of(LocalTime.of(9, 0), true)),
			Optional.empty()
		));
	}

	@Test
	void constructorMinEqualMaxExclusiveFails() {
		LocalDate date = LocalDate.of(2024, 1, 1);
		assertThrows(IllegalArgumentException.class, () -> new TemporalConstraintConfig<>(
			Optional.of(Pair.of(date, false)),
			Optional.of(Pair.of(date, true)),
			Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new TemporalConstraintConfig<>(
			Optional.of(Pair.of(date, true)),
			Optional.of(Pair.of(date, false)),
			Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new TemporalConstraintConfig<>(
			Optional.of(Pair.of(date, false)),
			Optional.of(Pair.of(date, false)),
			Optional.empty()
		));
	}

	@Test
	void unconstrainedMethod() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void isUnconstrained() {
		assertTrue(TemporalConstraintConfig.<LocalDate>unconstrained().isUnconstrained());
		assertTrue(new TemporalConstraintConfig<LocalDate>(Optional.empty(), Optional.empty(), Optional.empty()).isUnconstrained());
		assertFalse(TemporalConstraintConfig.<LocalDate>unconstrained().withMin(LocalDate.of(2020, 1, 1), true).isUnconstrained());
		assertFalse(TemporalConstraintConfig.<LocalDate>unconstrained().withMax(LocalDate.of(2030, 1, 1), true).isUnconstrained());
		assertFalse(TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(LocalDate.of(2024, 1, 1), false).isUnconstrained());
	}

	@Test
	void withMinInclusive() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, true);

		assertTrue(config.min().isPresent());
		assertEquals(min, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMinExclusive() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, false);

		assertTrue(config.min().isPresent());
		assertEquals(min, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMinPreservesMax() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(Optional.empty(), Optional.of(Pair.of(max, true)), Optional.empty());
		TemporalConstraintConfig<LocalDate> config = initial.withMin(LocalDate.of(2020, 1, 1), true);

		assertTrue(config.min().isPresent());
		assertEquals(LocalDate.of(2020, 1, 1), config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(max, config.max().get().getFirst());
	}

	@Test
	void withMinPreservesEquals() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.of(Pair.of(equals, false)));
		TemporalConstraintConfig<LocalDate> config = initial.withMin(LocalDate.of(2020, 1, 1), true);

		assertTrue(config.min().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(equals, config.equals().get().getFirst());
	}

	@Test
	void withMaxInclusive() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, true);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isPresent());
		assertEquals(max, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMaxExclusive() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, false);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isPresent());
		assertEquals(max, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withMaxPreservesMin() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(Optional.of(Pair.of(min, true)), Optional.empty(), Optional.empty());
		TemporalConstraintConfig<LocalDate> config = initial.withMax(LocalDate.of(2030, 12, 31), true);

		assertTrue(config.min().isPresent());
		assertEquals(min, config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(LocalDate.of(2030, 12, 31), config.max().get().getFirst());
	}

	@Test
	void withMaxPreservesEquals() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.of(Pair.of(equals, false)));
		TemporalConstraintConfig<LocalDate> config = initial.withMax(LocalDate.of(2030, 12, 31), true);

		assertTrue(config.max().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(equals, config.equals().get().getFirst());
	}

	@Test
	void withRangeInclusive() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withRange(min, max, true);

		assertTrue(config.min().isPresent());
		assertEquals(min, config.min().get().getFirst());
		assertTrue(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(max, config.max().get().getFirst());
		assertTrue(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withRangeExclusive() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withRange(min, max, false);

		assertTrue(config.min().isPresent());
		assertEquals(min, config.min().get().getFirst());
		assertFalse(config.min().get().getSecond());
		assertTrue(config.max().isPresent());
		assertEquals(max, config.max().get().getFirst());
		assertFalse(config.max().get().getSecond());
		assertTrue(config.equals().isEmpty());
	}

	@Test
	void withRangeReplacesExisting() {
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalDate.of(2010, 1, 1), true)),
			Optional.of(Pair.of(LocalDate.of(2040, 12, 31), true)),
			Optional.empty()
		);
		TemporalConstraintConfig<LocalDate> config = initial.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		assertTrue(config.min().isPresent());
		assertEquals(LocalDate.of(2020, 1, 1), config.min().get().getFirst());
		assertTrue(config.max().isPresent());
		assertEquals(LocalDate.of(2030, 12, 31), config.max().get().getFirst());
	}

	@Test
	void withRangePreservesEquals() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(Optional.empty(), Optional.empty(), Optional.of(Pair.of(equals, false)));
		TemporalConstraintConfig<LocalDate> config = initial.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		assertTrue(config.equals().isPresent());
		assertEquals(equals, config.equals().get().getFirst());
	}

	@Test
	void withEqualsNormal() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, false);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isPresent());
		assertEquals(equals, config.equals().get().getFirst());
		assertFalse(config.equals().get().getSecond());
	}

	@Test
	void withEqualsNegated() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, true);

		assertTrue(config.min().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.equals().isPresent());
		assertEquals(equals, config.equals().get().getFirst());
		assertTrue(config.equals().get().getSecond());
	}

	@Test
	void withEqualsPreservesMinMax() {
		TemporalConstraintConfig<LocalDate> initial = new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)),
			Optional.of(Pair.of(LocalDate.of(2030, 12, 31), true)),
			Optional.empty()
		);
		TemporalConstraintConfig<LocalDate> config = initial.withEquals(LocalDate.of(2024, 6, 15), false);

		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertTrue(config.equals().isPresent());
		assertEquals(LocalDate.of(2024, 6, 15), config.equals().get().getFirst());
	}

	@Test
	void matchesUnconstrained() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.unconstrained();

		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
		assertTrue(config.matches(LocalDate.MIN).isSuccess());
		assertTrue(config.matches(LocalDate.MAX).isSuccess());
	}

	@Test
	void matchesMinInclusiveSuccess() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, true);

		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesMinInclusiveFailure() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, true);

		Result<Void> result = config.matches(LocalDate.of(2019, 12, 31));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum constraint"));
		assertTrue(result.errorOrThrow().contains("2019-12-31"));
		assertTrue(result.errorOrThrow().contains("2020-01-01"));
	}

	@Test
	void matchesMinExclusiveSuccess() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, false);

		assertTrue(config.matches(LocalDate.of(2020, 1, 2)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesMinExclusiveFailure() {
		LocalDate min = LocalDate.of(2020, 1, 1);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(min, false);

		Result<Void> resultEqual = config.matches(LocalDate.of(2020, 1, 1));
		assertTrue(resultEqual.isError());
		assertTrue(resultEqual.errorOrThrow().contains("Violated minimum constraint (exclusive)"));

		Result<Void> resultBefore = config.matches(LocalDate.of(2019, 12, 31));
		assertTrue(resultBefore.isError());
		assertTrue(resultBefore.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesMaxInclusiveSuccess() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, true);

		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesMaxInclusiveFailure() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, true);

		Result<Void> result = config.matches(LocalDate.of(2031, 1, 1));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum constraint"));
		assertTrue(result.errorOrThrow().contains("2031-01-01"));
		assertTrue(result.errorOrThrow().contains("2030-12-31"));
	}

	@Test
	void matchesMaxExclusiveSuccess() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, false);

		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 30)).isSuccess());
	}

	@Test
	void matchesMaxExclusiveFailure() {
		LocalDate max = LocalDate.of(2030, 12, 31);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMax(max, false);

		Result<Void> resultEqual = config.matches(LocalDate.of(2030, 12, 31));
		assertTrue(resultEqual.isError());
		assertTrue(resultEqual.errorOrThrow().contains("Violated maximum constraint (exclusive)"));

		Result<Void> resultAfter = config.matches(LocalDate.of(2031, 1, 1));
		assertTrue(resultAfter.isError());
		assertTrue(resultAfter.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesRangeInclusiveSuccess() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesRangeInclusiveFailureBelow() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		Result<Void> result = config.matches(LocalDate.of(2019, 12, 31));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated minimum constraint"));
	}

	@Test
	void matchesRangeInclusiveFailureAbove() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		Result<Void> result = config.matches(LocalDate.of(2031, 1, 1));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated maximum constraint"));
	}

	@Test
	void matchesRangeExclusiveSuccess() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), false);

		assertTrue(config.matches(LocalDate.of(2020, 1, 2)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 30)).isSuccess());
	}

	@Test
	void matchesRangeExclusiveFailureBoundaries() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), false);

		Result<Void> resultMin = config.matches(LocalDate.of(2020, 1, 1));
		assertTrue(resultMin.isError());
		assertTrue(resultMin.errorOrThrow().contains("exclusive"));

		Result<Void> resultMax = config.matches(LocalDate.of(2030, 12, 31));
		assertTrue(resultMax.isError());
		assertTrue(resultMax.errorOrThrow().contains("exclusive"));
	}

	@Test
	void matchesEqualsSuccess() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, false);

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, false);

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 14));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(result.errorOrThrow().contains("not equal"));
		assertTrue(result.errorOrThrow().contains("2024-06-14"));
		assertTrue(result.errorOrThrow().contains("2024-06-15"));
	}

	@Test
	void matchesNotEqualsSuccess() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, true);

		assertTrue(config.matches(LocalDate.of(2024, 6, 14)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2024, 6, 16)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2020, 1, 1)).isSuccess());
		assertTrue(config.matches(LocalDate.of(2030, 12, 31)).isSuccess());
	}

	@Test
	void matchesNotEqualsFailure() {
		LocalDate equals = LocalDate.of(2024, 6, 15);
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withEquals(equals, true);

		Result<Void> result = config.matches(LocalDate.of(2024, 6, 15));
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().contains("Violated equals constraint"));
		assertTrue(result.errorOrThrow().contains("is equal"));
		assertTrue(result.errorOrThrow().contains("should not be"));
		assertTrue(result.errorOrThrow().contains("2024-06-15"));
	}

	@Test
	void matchesEqualsOverridesMinMax() {
		TemporalConstraintConfig<LocalDate> config = new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)),
			Optional.of(Pair.of(LocalDate.of(2030, 12, 31), true)),
			Optional.of(Pair.of(LocalDate.of(2024, 6, 15), false))
		);

		assertTrue(config.matches(LocalDate.of(2024, 6, 15)).isSuccess());

		Result<Void> resultOther = config.matches(LocalDate.of(2024, 6, 14));
		assertTrue(resultOther.isError());
		assertTrue(resultOther.errorOrThrow().contains("Violated equals constraint"));
	}

	@Test
	void matchesValueRequired() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained().withMin(LocalDate.of(2020, 1, 1), true);
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}

	// Tests with other temporal types

	@Test
	void matchesLocalTime() {
		TemporalConstraintConfig<LocalTime> config = TemporalConstraintConfig.<LocalTime>unconstrained()
			.withRange(LocalTime.of(9, 0), LocalTime.of(17, 0), true);

		assertTrue(config.matches(LocalTime.of(9, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(12, 30)).isSuccess());
		assertTrue(config.matches(LocalTime.of(17, 0)).isSuccess());

		Result<Void> resultBefore = config.matches(LocalTime.of(8, 59));
		assertTrue(resultBefore.isError());

		Result<Void> resultAfter = config.matches(LocalTime.of(17, 1));
		assertTrue(resultAfter.isError());
	}

	@Test
	void matchesInstant() {
		Instant now = Instant.now();
		Instant past = now.minusSeconds(3600);
		Instant future = now.plusSeconds(3600);

		TemporalConstraintConfig<Instant> config = TemporalConstraintConfig.<Instant>unconstrained()
			.withRange(past, future, true);

		assertTrue(config.matches(past).isSuccess());
		assertTrue(config.matches(now).isSuccess());
		assertTrue(config.matches(future).isSuccess());

		Result<Void> resultBefore = config.matches(past.minusSeconds(1));
		assertTrue(resultBefore.isError());

		Result<Void> resultAfter = config.matches(future.plusSeconds(1));
		assertTrue(resultAfter.isError());
	}

	@Test
	void matchesYear() {
		TemporalConstraintConfig<Year> config = TemporalConstraintConfig.<Year>unconstrained()
			.withRange(Year.of(2000), Year.of(2100), true);

		assertTrue(config.matches(Year.of(2000)).isSuccess());
		assertTrue(config.matches(Year.of(2024)).isSuccess());
		assertTrue(config.matches(Year.of(2100)).isSuccess());

		Result<Void> resultBefore = config.matches(Year.of(1999));
		assertTrue(resultBefore.isError());

		Result<Void> resultAfter = config.matches(Year.of(2101));
		assertTrue(resultAfter.isError());
	}

	@Test
	void toStringUnconstrained() {
		String str = TemporalConstraintConfig.<LocalDate>unconstrained().toString();
		assertEquals("TemporalConstraintConfig[unconstrained]", str);
	}

	@Test
	void toStringMinInclusive() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMin(LocalDate.of(2020, 1, 1), true);
		String str = config.toString();
		assertTrue(str.contains("TemporalConstraintConfig["));
		assertTrue(str.contains("min=2020-01-01"));
		assertTrue(str.contains("(inclusive)"));
	}

	@Test
	void toStringMinExclusive() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMin(LocalDate.of(2020, 1, 1), false);
		String str = config.toString();
		assertTrue(str.contains("min=2020-01-01"));
		assertTrue(str.contains("(exclusive)"));
	}

	@Test
	void toStringMaxInclusive() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMax(LocalDate.of(2030, 12, 31), true);
		String str = config.toString();
		assertTrue(str.contains("max=2030-12-31"));
		assertTrue(str.contains("(inclusive)"));
	}

	@Test
	void toStringMaxExclusive() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMax(LocalDate.of(2030, 12, 31), false);
		String str = config.toString();
		assertTrue(str.contains("max=2030-12-31"));
		assertTrue(str.contains("(exclusive)"));
	}

	@Test
	void toStringRange() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);
		String str = config.toString();
		assertTrue(str.contains("min=2020-01-01"));
		assertTrue(str.contains("max=2030-12-31"));
	}

	@Test
	void toStringEquals() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withEquals(LocalDate.of(2024, 6, 15), false);
		String str = config.toString();
		assertTrue(str.contains("equals=2024-06-15"));
		assertFalse(str.contains("(negated)"));
	}

	@Test
	void toStringNotEquals() {
		TemporalConstraintConfig<LocalDate> config = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withEquals(LocalDate.of(2024, 6, 15), true);
		String str = config.toString();
		assertTrue(str.contains("equals=2024-06-15"));
		assertTrue(str.contains("(negated)"));
	}

	@Test
	void toStringMultipleConstraints() {
		TemporalConstraintConfig<LocalDate> config = new TemporalConstraintConfig<>(
			Optional.of(Pair.of(LocalDate.of(2020, 1, 1), true)),
			Optional.of(Pair.of(LocalDate.of(2030, 12, 31), false)),
			Optional.of(Pair.of(LocalDate.of(2024, 6, 15), true))
		);
		String str = config.toString();
		assertTrue(str.contains("min=2020-01-01"));
		assertTrue(str.contains("max=2030-12-31"));
		assertTrue(str.contains("equals=2024-06-15"));
	}

	@Test
	void equality() {
		TemporalConstraintConfig<LocalDate> config1 = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMin(LocalDate.of(2020, 1, 1), true);
		TemporalConstraintConfig<LocalDate> config2 = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMin(LocalDate.of(2020, 1, 1), true);
		TemporalConstraintConfig<LocalDate> config3 = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withMin(LocalDate.of(2020, 1, 2), true);

		assertEquals(config1, config2);
		assertNotEquals(config1, config3);
	}

	@Test
	void hashCodeConsistency() {
		TemporalConstraintConfig<LocalDate> config1 = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);
		TemporalConstraintConfig<LocalDate> config2 = TemporalConstraintConfig.<LocalDate>unconstrained()
			.withRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31), true);

		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
