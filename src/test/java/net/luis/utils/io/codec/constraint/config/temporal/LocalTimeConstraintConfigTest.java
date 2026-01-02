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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalTimeConstraintConfigTest {

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalTimeConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), null, TimeFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullTimeFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new LocalTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		LocalTimeConstraintConfig config = new LocalTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED
		);
		assertNotNull(config);
		assertNotNull(config.config());
		assertNotNull(config.spanConfig());
		assertNotNull(config.timeFieldConfig());
	}

	// UNCONSTRAINED Tests

	@Test
	void unconstrainedConstant() {
		assertNotNull(LocalTimeConstraintConfig.UNCONSTRAINED);
		assertTrue(LocalTimeConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(LocalTimeConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(LocalTimeConstraintConfig.UNCONSTRAINED.timeFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(LocalTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		LocalTimeConstraintConfig config = new LocalTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMin(LocalTime.of(10, 0), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTimeFieldConstraint() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		LocalTime min = LocalTime.of(10, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		LocalTime max = LocalTime.of(18, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Time Field Constraint Builder Tests

	@Test
	void withHour() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinute() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withSecond() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMillisecond() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(LocalTime.of(10, 30)).isSuccess());
		assertTrue(config.matches(LocalTime.of(0, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(23, 59)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(LocalTime.of(10, 31));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		LocalTime min = LocalTime.of(10, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(LocalTime.of(15, 0)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		LocalTime min = LocalTime.of(10, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(LocalTime.of(9, 59));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(LocalTime.of(12, 0)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		LocalTime min = LocalTime.of(9, 0);
		LocalTime max = LocalTime.of(17, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(LocalTime.of(18, 0));
		assertTrue(result.isError());
	}

	// matches Tests - Time Field Constraints

	@Test
	void matchesHourSuccess() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true));
		assertTrue(config.matches(LocalTime.of(10, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(12, 30)).isSuccess());
		assertTrue(config.matches(LocalTime.of(15, 0)).isSuccess());
	}

	@Test
	void matchesHourFailure() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		Result<Void> result = config.matches(LocalTime.of(9, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinuteSuccess() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertTrue(config.matches(LocalTime.of(10, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(10, 30)).isSuccess());
	}

	@Test
	void matchesMinuteFailure() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		Result<Void> result = config.matches(LocalTime.of(10, 31));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTemporalAndFieldSuccess() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED
			.withMin(LocalTime.of(9, 0), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMax(17, true));

		assertTrue(config.matches(LocalTime.of(12, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(9, 0)).isSuccess());
		assertTrue(config.matches(LocalTime.of(17, 0)).isSuccess());
	}

	@Test
	void matchesCombinedTemporalAndFieldFailure() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED
			.withMin(LocalTime.of(9, 0), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMax(17, true));

		Result<Void> result = config.matches(LocalTime.of(18, 0));
		assertTrue(result.isError());
	}

	@Test
	void matchesMultipleFieldConstraintsSuccess() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		assertTrue(config.matches(LocalTime.of(12, 15)).isSuccess());
	}

	@Test
	void matchesMultipleFieldConstraintsFailure() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		Result<Void> result = config.matches(LocalTime.of(16, 15));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("LocalTimeConstraintConfig[unconstrained]", LocalTimeConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("LocalTimeConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithFieldConstraint() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config1 = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalTimeConstraintConfig config2 = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config1 = LocalTimeConstraintConfig.UNCONSTRAINED.withMin(value, true);
		LocalTimeConstraintConfig config2 = LocalTimeConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		LocalTime value = LocalTime.of(10, 30);
		LocalTimeConstraintConfig config1 = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		LocalTimeConstraintConfig config2 = LocalTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
