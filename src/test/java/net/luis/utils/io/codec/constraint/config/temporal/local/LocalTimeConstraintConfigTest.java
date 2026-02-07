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

package net.luis.utils.io.codec.constraint.config.temporal.local;

import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LocalTimeConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class LocalTimeConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullHour() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMinute() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSecond() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullMillisecond() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNanosecond() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new LocalTimeConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED;
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
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(LocalTime.now()));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(LocalTimeConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withAfter(LocalTime.now());
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		LocalTime time = LocalTime.of(10, 30, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEqualTo(time);
		assertTrue(config.equalTo().isPresent());
		assertEquals(time, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		LocalTime time = LocalTime.of(10, 30, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(time);
		assertTrue(config.equalTo().isPresent());
		assertEquals(time, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		LocalTime time1 = LocalTime.of(10, 0, 0);
		LocalTime time2 = LocalTime.of(12, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(time1, time2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(time1, time2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		LocalTime time1 = LocalTime.of(10, 0, 0);
		LocalTime time2 = LocalTime.of(12, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(time1, time2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(time1, time2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withAfter() {
		LocalTime time = LocalTime.of(9, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withAfter(time);
		assertTrue(config.after().isPresent());
		assertEquals(time, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqual() {
		LocalTime time = LocalTime.of(9, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(time);
		assertTrue(config.after().isPresent());
		assertEquals(time, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withBefore() {
		LocalTime time = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBefore(time);
		assertTrue(config.before().isPresent());
		assertEquals(time, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqual() {
		LocalTime time = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(time);
		assertTrue(config.before().isPresent());
		assertEquals(time, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetween() {
		LocalTime after = LocalTime.of(9, 0, 0);
		LocalTime before = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		LocalTime after = LocalTime.of(9, 0, 0);
		LocalTime before = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withWithinLast() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(2));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofHours(2), config.withinLast().get());
	}
	
	@Test
	void withWithinNext() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(2));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofHours(2), config.withinNext().get());
	}
	
	@Test
	void withHour() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertTrue(config.hour().isPresent());
		assertEquals(hourConfig, config.hour().get());
	}
	
	@Test
	void withMinute() {
		NumericConstraintConfig minuteConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertTrue(config.minute().isPresent());
		assertEquals(minuteConfig, config.minute().get());
	}
	
	@Test
	void withSecond() {
		NumericConstraintConfig secondConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 59);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertTrue(config.second().isPresent());
		assertEquals(secondConfig, config.second().get());
	}
	
	@Test
	void withMillisecond() {
		NumericConstraintConfig millisecondConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(0, 999);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMillisecond(millisecondConfig);
		assertTrue(config.millisecond().isPresent());
		assertEquals(millisecondConfig, config.millisecond().get());
	}
	
	@Test
	void withNanosecond() {
		NumericConstraintConfig nanosecondConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThanOrEqual(0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withNanosecond(nanosecondConfig);
		assertTrue(config.nanosecond().isPresent());
		assertEquals(nanosecondConfig, config.nanosecond().get());
	}
	
	@Test
	void withCustom() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withCustom(t -> {
			if (t.getHour() < 9 || t.getHour() > 17) throw new ConstraintViolateException("Time must be between 9 and 17");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void validateWithEqualTo() {
		LocalTime time = LocalTime.of(10, 30, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withEqualTo(time);
		assertDoesNotThrow(() -> config.validate(time));
		assertThrows(ConstraintViolateException.class, () -> config.validate(time.plusSeconds(1)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		LocalTime time = LocalTime.of(10, 30, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withNotEqualTo(time);
		assertDoesNotThrow(() -> config.validate(time.plusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(time));
	}
	
	@Test
	void validateWithIn() {
		LocalTime time1 = LocalTime.of(10, 0, 0);
		LocalTime time2 = LocalTime.of(12, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withIn(List.of(time1, time2));
		assertDoesNotThrow(() -> config.validate(time1));
		assertDoesNotThrow(() -> config.validate(time2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(11, 0, 0)));
	}
	
	@Test
	void validateWithNotIn() {
		LocalTime time1 = LocalTime.of(10, 0, 0);
		LocalTime time2 = LocalTime.of(12, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withNotIn(List.of(time1, time2));
		assertDoesNotThrow(() -> config.validate(LocalTime.of(11, 0, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(time1));
	}
	
	@Test
	void validateWithAfter() {
		LocalTime threshold = LocalTime.of(9, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(9, 0, 1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(8, 59, 59)));
	}
	
	@Test
	void validateWithAfterOrEqual() {
		LocalTime threshold = LocalTime.of(9, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(LocalTime.of(9, 0, 1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(8, 59, 59)));
	}
	
	@Test
	void validateWithBefore() {
		LocalTime threshold = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(16, 59, 59)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(17, 0, 1)));
	}
	
	@Test
	void validateWithBeforeOrEqual() {
		LocalTime threshold = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(LocalTime.of(16, 59, 59)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(17, 0, 1)));
	}
	
	@Test
	void validateWithBetween() {
		LocalTime after = LocalTime.of(9, 0, 0);
		LocalTime before = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(12, 0, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(after));
		assertThrows(ConstraintViolateException.class, () -> config.validate(before));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		LocalTime after = LocalTime.of(9, 0, 0);
		LocalTime before = LocalTime.of(17, 0, 0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertDoesNotThrow(() -> config.validate(after));
		assertDoesNotThrow(() -> config.validate(before));
		assertDoesNotThrow(() -> config.validate(LocalTime.of(12, 0, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(8, 59, 59)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(17, 0, 1)));
	}
	
	@Test
	void validateWithHour() {
		NumericConstraintConfig hourConfig = NumericConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(9, 17);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withHour(hourConfig);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(10, 0, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(8, 0, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(18, 0, 0)));
	}
	
	@Test
	void validateWithMinute() {
		NumericConstraintConfig minuteConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(30);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withMinute(minuteConfig);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(10, 30, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(10, 0, 0)));
	}
	
	@Test
	void validateWithSecond() {
		NumericConstraintConfig secondConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(0);
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED.withSecond(secondConfig);
		assertDoesNotThrow(() -> config.validate(LocalTime.of(10, 30, 0)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(LocalTime.of(10, 30, 1)));
	}
	
	@Test
	void validateWithNullValue() {
		LocalTimeConstraintConfig config = LocalTimeConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
