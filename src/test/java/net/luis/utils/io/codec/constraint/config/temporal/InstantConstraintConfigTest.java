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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
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
		assertTrue(config.matches(Instant.now()).isSuccess());
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
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withCustom(i -> i.isBefore(Instant.MAX) ? Result.success() : Result.error("Instant must be before MAX"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void matchesWithEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withEqualTo(instant);
		assertTrue(config.matches(instant).isSuccess());
		assertTrue(config.matches(instant.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		Instant instant = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotEqualTo(instant);
		assertTrue(config.matches(instant.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(instant).isError());
	}
	
	@Test
	void matchesWithIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withIn(List.of(instant1, instant2));
		assertTrue(config.matches(instant1).isSuccess());
		assertTrue(config.matches(instant2).isSuccess());
		assertTrue(config.matches(instant1.plusSeconds(50)).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		Instant instant1 = Instant.now();
		Instant instant2 = instant1.plusSeconds(100);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withNotIn(List.of(instant1, instant2));
		assertTrue(config.matches(instant1.plusSeconds(50)).isSuccess());
		assertTrue(config.matches(instant1).isError());
	}
	
	@Test
	void matchesWithAfter() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfter(threshold);
		assertTrue(config.matches(threshold.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.minusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithAfterOrEqual() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withAfterOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.plusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold.minusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithBefore() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBefore(threshold);
		assertTrue(config.matches(threshold.minusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold).isError());
		assertTrue(config.matches(threshold.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithBeforeOrEqual() {
		Instant threshold = Instant.now();
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBeforeOrEqual(threshold);
		assertTrue(config.matches(threshold).isSuccess());
		assertTrue(config.matches(threshold.minusSeconds(1)).isSuccess());
		assertTrue(config.matches(threshold.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithBetween() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetween(after, before);
		assertTrue(config.matches(after.plusSeconds(1800)).isSuccess());
		assertTrue(config.matches(after).isError());
		assertTrue(config.matches(before).isError());
	}
	
	@Test
	void matchesWithBetweenOrEqual() {
		Instant after = Instant.now();
		Instant before = after.plusSeconds(3600);
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withBetweenOrEqual(after, before);
		assertTrue(config.matches(after).isSuccess());
		assertTrue(config.matches(before).isSuccess());
		assertTrue(config.matches(after.plusSeconds(1800)).isSuccess());
		assertTrue(config.matches(after.minusSeconds(1)).isError());
		assertTrue(config.matches(before.plusSeconds(1)).isError());
	}
	
	@Test
	void matchesWithWithinLast() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinLast(Duration.ofHours(1));
		assertTrue(config.matches(Instant.now().minusSeconds(1800)).isSuccess());
		assertTrue(config.matches(Instant.now().minusSeconds(7200)).isError());
	}
	
	@Test
	void matchesWithWithinNext() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED.withWithinNext(Duration.ofHours(1));
		assertTrue(config.matches(Instant.now().plusSeconds(1800)).isSuccess());
		assertTrue(config.matches(Instant.now().plusSeconds(7200)).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		InstantConstraintConfig config = InstantConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
