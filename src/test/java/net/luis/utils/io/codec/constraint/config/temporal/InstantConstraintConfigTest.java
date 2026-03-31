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

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link InstantConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class InstantConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAfter() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullBefore() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinLast() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullWithinNext() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinLast() {
		assertThrows(IllegalArgumentException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNegativeWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ofHours(-1)), Optional.empty()
		));
	}
	
	@Test
	void constructWithZeroWithinNext() {
		assertThrows(IllegalArgumentException.class, () -> new InstantConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Duration.ZERO), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.after().isEmpty());
		assertTrue(config.before().isEmpty());
		assertTrue(config.withinLast().isEmpty());
		assertTrue(config.withinNext().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(Instant.now()));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(InstantConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfter(Instant.now());
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEqualTo(instant);
		assertTrue(config.equalTo().isPresent());
		assertEquals(instant, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotEqualTo(instant);
		assertTrue(config.equalTo().isPresent());
		assertEquals(instant, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withIn(List.of(instant1, instant2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(instant1, instant2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withNotIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotIn(List.of(instant1, instant2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(instant1, instant2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withAfter() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfter(instant);
		assertTrue(config.after().isPresent());
		assertEquals(instant, config.after().get().getFirst());
		assertFalse(config.after().get().getSecond());
	}
	
	@Test
	void withAfterOrEqual() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfterOrEqual(instant);
		assertTrue(config.after().isPresent());
		assertEquals(instant, config.after().get().getFirst());
		assertTrue(config.after().get().getSecond());
	}
	
	@Test
	void withBefore() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBefore(instant);
		assertTrue(config.before().isPresent());
		assertEquals(instant, config.before().get().getFirst());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBeforeOrEqual() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(instant);
		assertTrue(config.before().isPresent());
		assertEquals(instant, config.before().get().getFirst());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withBetween() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertFalse(config.after().get().getSecond());
		assertFalse(config.before().get().getSecond());
	}
	
	@Test
	void withBetweenOrEqual() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertTrue(config.after().isPresent());
		assertTrue(config.before().isPresent());
		assertEquals(after, config.after().get().getFirst());
		assertEquals(before, config.before().get().getFirst());
		assertTrue(config.after().get().getSecond());
		assertTrue(config.before().get().getSecond());
	}
	
	@Test
	void withWithinLast() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(24));
		assertTrue(config.withinLast().isPresent());
		assertEquals(Duration.ofHours(24), config.withinLast().get());
	}
	
	@Test
	void withWithinNext() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(24));
		assertTrue(config.withinNext().isPresent());
		assertEquals(Duration.ofHours(24), config.withinNext().get());
	}
	
	@Test
	void withCustom() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withCustom(i -> {
			if (!i.isBefore(Instant.MAX)) throw new ConstraintViolateException("Instant must be before MAX");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void validateWithEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEqualTo(instant);
		assertDoesNotThrow(() -> config.validate(instant));
		assertThrows(ConstraintViolateException.class, () -> config.validate(instant.plusSeconds(1)));
	}
	
	@Test
	void validateWithNotEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotEqualTo(instant);
		assertDoesNotThrow(() -> config.validate(instant.plusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(instant));
	}
	
	@Test
	void validateWithIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withIn(List.of(instant1, instant2));
		assertDoesNotThrow(() -> config.validate(instant1));
		assertDoesNotThrow(() -> config.validate(instant2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(instant1.plusSeconds(50)));
	}
	
	@Test
	void validateWithNotIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotIn(List.of(instant1, instant2));
		assertDoesNotThrow(() -> config.validate(instant1.plusSeconds(50)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(instant1));
	}
	
	@Test
	void validateWithAfter() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertDoesNotThrow(() -> config.validate(threshold.plusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.minusSeconds(1)));
	}
	
	@Test
	void validateWithAfterOrEqual() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(threshold.plusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.minusSeconds(1)));
	}
	
	@Test
	void validateWithBefore() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertDoesNotThrow(() -> config.validate(threshold.minusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.plusSeconds(1)));
	}
	
	@Test
	void validateWithBeforeOrEqual() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertDoesNotThrow(() -> config.validate(threshold));
		assertDoesNotThrow(() -> config.validate(threshold.minusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(threshold.plusSeconds(1)));
	}
	
	@Test
	void validateWithBetween() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertDoesNotThrow(() -> config.validate(after.plusSeconds(1800)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(after));
		assertThrows(ConstraintViolateException.class, () -> config.validate(before));
	}
	
	@Test
	void validateWithBetweenOrEqual() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertDoesNotThrow(() -> config.validate(after));
		assertDoesNotThrow(() -> config.validate(before));
		assertDoesNotThrow(() -> config.validate(after.plusSeconds(1800)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(after.minusSeconds(1)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(before.plusSeconds(1)));
	}
	
	@Test
	void validateWithWithinLast() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(1));
		assertDoesNotThrow(() -> config.validate(Instant.now().minusSeconds(1800)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Instant.now().minusSeconds(7200)));
	}
	
	@Test
	void validateWithWithinNext() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(1));
		assertDoesNotThrow(() -> config.validate(Instant.now().plusSeconds(1800)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(Instant.now().plusSeconds(7200)));
	}
	
	@Test
	void validateWithNullValue() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
