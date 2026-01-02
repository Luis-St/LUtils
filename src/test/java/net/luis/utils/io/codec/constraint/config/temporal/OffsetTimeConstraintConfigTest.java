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

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class OffsetTimeConstraintConfigTest {

	private static final ZoneOffset OFFSET = ZoneOffset.ofHours(2);

	// Constructor Tests

	@Test
	void constructorNullConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetTimeConstraintConfig(null, SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullSpanConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), null, TimeFieldConstraintConfig.UNCONSTRAINED)
		);
	}

	@Test
	void constructorNullTimeFieldConfigThrows() {
		assertThrows(NullPointerException.class, () ->
			new OffsetTimeConstraintConfig(TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, null)
		);
	}

	@Test
	void constructorValidParameters() {
		OffsetTimeConstraintConfig config = new OffsetTimeConstraintConfig(
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
		assertNotNull(OffsetTimeConstraintConfig.UNCONSTRAINED);
		assertTrue(OffsetTimeConstraintConfig.UNCONSTRAINED.config().isUnconstrained());
		assertTrue(OffsetTimeConstraintConfig.UNCONSTRAINED.spanConfig().isUnconstrained());
		assertTrue(OffsetTimeConstraintConfig.UNCONSTRAINED.timeFieldConfig().isUnconstrained());
	}

	// isUnconstrained Tests

	@Test
	void isUnconstrainedForConstant() {
		assertTrue(OffsetTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}

	@Test
	void isUnconstrainedForNew() {
		OffsetTimeConstraintConfig config = new OffsetTimeConstraintConfig(
			TemporalConstraintConfig.unconstrained(),
			SpanConstraintConfig.UNCONSTRAINED,
			TimeFieldConstraintConfig.UNCONSTRAINED
		);
		assertTrue(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTemporalConstraint() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMin(OffsetTime.of(10, 0, 0, 0, OFFSET), true);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithSpanConstraint() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		assertFalse(config.isUnconstrained());
	}

	@Test
	void isUnconstrainedWithTimeFieldConstraint() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertFalse(config.isUnconstrained());
	}

	// Temporal Constraint Builder Tests

	@Test
	void withEquals() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMin() {
		OffsetTime min = OffsetTime.of(10, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMax() {
		OffsetTime max = OffsetTime.of(18, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMax(max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withRange() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Span Constraint Builder Tests

	@Test
	void withWithinLast() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withWithinNext() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// Time Field Constraint Builder Tests

	@Test
	void withHour() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMinute() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withSecond() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withSecond(FieldConstraintConfig.UNCONSTRAINED.withMin(15, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withMillisecond() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMillisecond(FieldConstraintConfig.UNCONSTRAINED.withMin(500, true));
		assertNotNull(config);
		assertFalse(config.isUnconstrained());
	}

	// matches Tests - Unconstrained

	@Test
	void matchesUnconstrained() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED;
		assertTrue(config.matches(OffsetTime.of(10, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(0, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(23, 59, 0, 0, OFFSET)).isSuccess());
	}

	// matches Tests - Temporal Constraints

	@Test
	void matchesEqualsSuccess() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertTrue(config.matches(value).isSuccess());
	}

	@Test
	void matchesEqualsFailure() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		Result<Void> result = config.matches(OffsetTime.of(10, 31, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinSuccess() {
		OffsetTime min = OffsetTime.of(10, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		assertTrue(config.matches(min).isSuccess());
		assertTrue(config.matches(OffsetTime.of(15, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMinFailure() {
		OffsetTime min = OffsetTime.of(10, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMin(min, true);
		Result<Void> result = config.matches(OffsetTime.of(9, 59, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesRangeSuccess() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		assertTrue(config.matches(OffsetTime.of(12, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesRangeFailure() {
		OffsetTime min = OffsetTime.of(9, 0, 0, 0, OFFSET);
		OffsetTime max = OffsetTime.of(17, 0, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withRange(min, max, true);
		Result<Void> result = config.matches(OffsetTime.of(18, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// matches Tests - Time Field Constraints

	@Test
	void matchesHourSuccess() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true));
		assertTrue(config.matches(OffsetTime.of(10, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(12, 30, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(15, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesHourFailure() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		Result<Void> result = config.matches(OffsetTime.of(9, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMinuteSuccess() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		assertTrue(config.matches(OffsetTime.of(10, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(10, 30, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMinuteFailure() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));
		Result<Void> result = config.matches(OffsetTime.of(10, 31, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// matches Tests - Combined Constraints

	@Test
	void matchesCombinedTemporalAndFieldSuccess() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED
			.withMin(OffsetTime.of(9, 0, 0, 0, OFFSET), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMax(17, true));

		assertTrue(config.matches(OffsetTime.of(12, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(9, 0, 0, 0, OFFSET)).isSuccess());
		assertTrue(config.matches(OffsetTime.of(17, 0, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesCombinedTemporalAndFieldFailure() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED
			.withMin(OffsetTime.of(9, 0, 0, 0, OFFSET), true)
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMax(17, true));

		Result<Void> result = config.matches(OffsetTime.of(18, 0, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	@Test
	void matchesMultipleFieldConstraintsSuccess() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		assertTrue(config.matches(OffsetTime.of(12, 15, 0, 0, OFFSET)).isSuccess());
	}

	@Test
	void matchesMultipleFieldConstraintsFailure() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED
			.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true).withMax(15, true))
			.withMinute(FieldConstraintConfig.UNCONSTRAINED.withMax(30, true));

		Result<Void> result = config.matches(OffsetTime.of(16, 15, 0, 0, OFFSET));
		assertTrue(result.isError());
	}

	// toString Tests

	@Test
	void toStringUnconstrained() {
		assertEquals("OffsetTimeConstraintConfig[unconstrained]", OffsetTimeConstraintConfig.UNCONSTRAINED.toString());
	}

	@Test
	void toStringWithTemporalConstraint() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		String str = config.toString();
		assertTrue(str.contains("OffsetTimeConstraintConfig"));
		assertTrue(str.contains("equals"));
	}

	@Test
	void toStringWithFieldConstraint() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(FieldConstraintConfig.UNCONSTRAINED.withMin(10, true));
		String str = config.toString();
		assertTrue(str.contains("hour"));
	}

	// Equality Tests

	@Test
	void equalsSameInstance() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED;
		assertEquals(config, config);
	}

	@Test
	void equalsSameValues() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config1 = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		OffsetTimeConstraintConfig config2 = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1, config2);
	}

	@Test
	void notEqualsDifferentConstraint() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config1 = OffsetTimeConstraintConfig.UNCONSTRAINED.withMin(value, true);
		OffsetTimeConstraintConfig config2 = OffsetTimeConstraintConfig.UNCONSTRAINED.withMax(value, true);
		assertNotEquals(config1, config2);
	}

	@Test
	void hashCodeConsistency() {
		OffsetTime value = OffsetTime.of(10, 30, 0, 0, OFFSET);
		OffsetTimeConstraintConfig config1 = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		OffsetTimeConstraintConfig config2 = OffsetTimeConstraintConfig.UNCONSTRAINED.withEquals(value, false);
		assertEquals(config1.hashCode(), config2.hashCode());
	}
}
