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

package net.luis.utils.io.codec.constraint.config.temporal.offset;

import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OffsetTimeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class OffsetTimeConstraintConfigTest {
	
	private static final OffsetTime TIME_10_30 = OffsetTime.of(10, 30, 0, 0, ZoneOffset.UTC);
	private static final OffsetTime TIME_12_00 = OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC);
	private static final OffsetTime TIME_14_30 = OffsetTime.of(14, 30, 0, 0, ZoneOffset.UTC);
	private static final OffsetTime TIME_18_00 = OffsetTime.of(18, 0, 0, 0, ZoneOffset.UTC);
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullOffset() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new OffsetTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.after().isEmpty());
		assertTrue(config.before().isEmpty());
		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isEmpty());
		assertTrue(config.hour().isEmpty());
		assertTrue(config.minute().isEmpty());
		assertTrue(config.second().isEmpty());
		assertTrue(config.millisecond().isEmpty());
		assertTrue(config.nanosecond().isEmpty());
		assertTrue(config.offset().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(TIME_12_00).isSuccess());
	}
	
	@Test
	void withEqualTo() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEqualTo(TIME_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(TIME_12_00, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(TIME_12_00);
		assertTrue(config.equalTo().isPresent());
		assertEquals(TIME_12_00, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(TIME_10_30, TIME_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(TIME_10_30, TIME_12_00), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(TIME_10_30, TIME_12_00));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(TIME_10_30, TIME_12_00), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withAfter() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withAfter(TIME_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(TIME_10_30, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withAfter(null));
	}
	
	@Test
	void withAfterOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(TIME_10_30);
		assertTrue(config.after().isPresent());
		assertEquals(TIME_10_30, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqualNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(null));
	}
	
	@Test
	void withBefore() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBefore(TIME_18_00);
		assertTrue(config.before().isPresent());
		assertEquals(TIME_18_00, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBefore(null));
	}
	
	@Test
	void withBeforeOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(TIME_18_00);
		assertTrue(config.before().isPresent());
		assertEquals(TIME_18_00, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqualNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(null));
	}
	
	@Test
	void withBetween() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBetween(TIME_10_30, TIME_18_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(TIME_10_30, config.after().get().getFirst());
		assertEquals(TIME_18_00, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenNullAfter() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBetween(null, TIME_18_00));
	}
	
	@Test
	void withBetweenNullBefore() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBetween(TIME_10_30, null));
	}
	
	@Test
	void withBetweenOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(TIME_10_30, TIME_18_00);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(TIME_10_30, config.after().get().getFirst());
		assertEquals(TIME_18_00, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqualNullAfter() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(null, TIME_18_00));
	}
	
	@Test
	void withBetweenOrEqualNullBefore() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(TIME_10_30, null));
	}
	
	@Test
	void withWithinLast() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(1));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofHours(1), config.withinLast().get());
	}
	
	@Test
	void withWithinLastNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinLast(null));
	}
	
	@Test
	void withWithinNext() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofHours(2), config.withinNext().get());
	}
	
	@Test
	void withWithinNextNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withWithinNext(null));
	}
	
	@Test
	void withHour() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}
	
	@Test
	void withHourNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(null));
	}
	
	@Test
	void withMinute() {
		NumericFieldConstraintConfig minuteConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withIn(List.of(0, 15, 30, 45));
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}
	
	@Test
	void withMinuteNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(null));
	}
	
	@Test
	void withSecond() {
		NumericFieldConstraintConfig secondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}
	
	@Test
	void withSecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withSecond(null));
	}
	
	@Test
	void withMillisecond() {
		NumericFieldConstraintConfig millisecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}
	
	@Test
	void withMillisecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withMillisecond(null));
	}
	
	@Test
	void withNanosecond() {
		NumericFieldConstraintConfig nanosecondConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}
	
	@Test
	void withNanosecondNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withNanosecond(null));
	}
	
	@Test
	void withOffset() {
		ZoneOffsetConstraintConfig offsetConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withOffset(offsetConfig);
		assertTrue(config.offset().isPresent());
		assertEquals(offsetConfig, config.offset().get());
	}
	
	@Test
	void withOffsetNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withOffset(null));
	}
	
	@Test
	void withCustom() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withCustom(time -> time.getHour() < 12 ? Result.success() : Result.error("Time must be before noon"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> OffsetTimeConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withEqualTo(TIME_12_00);
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(TIME_10_30).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(TIME_12_00);
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isError());
	}
	
	@Test
	void matchesWithIn() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(TIME_10_30, TIME_12_00));
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(TIME_14_30).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(TIME_10_30, TIME_12_00));
		assertTrue(config.matches(TIME_14_30).isSuccess());
		assertTrue(config.matches(TIME_10_30).isError());
		assertTrue(config.matches(TIME_12_00).isError());
	}
	
	@Test
	void matchesWithAfter() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withAfter(TIME_10_30);
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(TIME_10_30).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(TIME_10_30);
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isSuccess());
	}
	
	@Test
	void matchesWithBefore() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBefore(TIME_18_00);
		assertTrue(config.matches(TIME_14_30).isSuccess());
		assertTrue(config.matches(TIME_18_00).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(TIME_18_00);
		assertTrue(config.matches(TIME_18_00).isSuccess());
		assertTrue(config.matches(TIME_14_30).isSuccess());
	}
	
	@Test
	void matchesWithBetween() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBetween(TIME_10_30, TIME_18_00);
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(TIME_14_30).isSuccess());
		assertTrue(config.matches(TIME_10_30).isError());
		assertTrue(config.matches(TIME_18_00).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(TIME_10_30, TIME_18_00);
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_18_00).isSuccess());
		assertTrue(config.matches(TIME_12_00).isSuccess());
	}
	
	@Test
	void matchesWithHourConstraint() {
		NumericFieldConstraintConfig hourConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(TIME_14_30).isSuccess());
		assertTrue(config.matches(TIME_18_00).isError());
	}
	
	@Test
	void matchesWithMinuteConstraint() {
		NumericFieldConstraintConfig minuteConfig = NumericFieldConstraintConfig.UNCONSTRAINED.withIn(List.of(0, 30));
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(OffsetTime.of(12, 15, 0, 0, ZoneOffset.UTC)).isError());
	}
	
	@Test
	void matchesWithOffsetConstraint() {
		ZoneOffsetConstraintConfig offsetConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withZero();
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED.withOffset(offsetConfig);
		assertTrue(config.matches(TIME_12_00).isSuccess());
		assertTrue(config.matches(OffsetTime.of(12, 0, 0, 0, ZoneOffset.ofHours(2))).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED
			.withAfterOrEqual(TIME_10_30)
			.withBeforeOrEqual(TIME_18_00)
			.withNotIn(List.of(TIME_12_00));
		
		assertTrue(config.matches(TIME_10_30).isSuccess());
		assertTrue(config.matches(TIME_14_30).isSuccess());
		assertTrue(config.matches(TIME_12_00).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		OffsetTimeConstraintConfig config = OffsetTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
