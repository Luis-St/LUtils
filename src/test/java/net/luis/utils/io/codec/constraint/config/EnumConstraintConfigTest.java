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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EnumConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class EnumConstraintConfigTest {
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new EnumConstraintConfig<TimeUnit>(
			null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new EnumConstraintConfig<TimeUnit>(
			Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new EnumConstraintConfig<TimeUnit>(
			Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new EnumConstraintConfig<TimeUnit>(
			Optional.empty(), Optional.of(Pair.of(EnumSet.noneOf(TimeUnit.class), false)), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.unconstrained();
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(TimeUnit.SECONDS).isSuccess());
	}

	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(EnumConstraintConfig.<TimeUnit>unconstrained().isUnconstrained());
	}

	@Test
	void isUnconstrainedWithConstraint() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withEqualTo(TimeUnit.SECONDS);
		assertFalse(config.isUnconstrained());
	}

	@Test
	void withEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withEqualTo(TimeUnit.SECONDS);
		assertTrue(config.equalTo().isPresent());
		assertEquals(TimeUnit.SECONDS, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotEqualTo(TimeUnit.SECONDS);
		assertTrue(config.equalTo().isPresent());
		assertEquals(TimeUnit.SECONDS, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withIn(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES));
		assertTrue(config.in().isPresent());
		assertEquals(EnumSet.of(TimeUnit.SECONDS, TimeUnit.MINUTES), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withIn(null));
	}
	
	@Test
	void withNotIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotIn(List.of(TimeUnit.HOURS, TimeUnit.DAYS));
		assertTrue(config.in().isPresent());
		assertEquals(EnumSet.of(TimeUnit.HOURS, TimeUnit.DAYS), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withNotIn(null));
	}
	
	@Test
	void withCustom() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withCustom(v -> v.ordinal() <= 2 ? Result.error("Ordinal must be > 2") : Result.success());
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withEqualTo(TimeUnit.SECONDS);
		assertTrue(config.matches(TimeUnit.SECONDS).isSuccess());
		assertTrue(config.matches(TimeUnit.MINUTES).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotEqualTo(TimeUnit.SECONDS);
		assertTrue(config.matches(TimeUnit.MINUTES).isSuccess());
		assertTrue(config.matches(TimeUnit.SECONDS).isError());
	}
	
	@Test
	void matchesWithIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withIn(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES));
		assertTrue(config.matches(TimeUnit.SECONDS).isSuccess());
		assertTrue(config.matches(TimeUnit.MINUTES).isSuccess());
		assertTrue(config.matches(TimeUnit.HOURS).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotIn(List.of(TimeUnit.HOURS, TimeUnit.DAYS));
		assertTrue(config.matches(TimeUnit.SECONDS).isSuccess());
		assertTrue(config.matches(TimeUnit.HOURS).isError());
		assertTrue(config.matches(TimeUnit.DAYS).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained()
			.withIn(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS))
			.withNotEqualTo(TimeUnit.HOURS);
		
		assertTrue(config.matches(TimeUnit.SECONDS).isSuccess());
		assertTrue(config.matches(TimeUnit.MINUTES).isSuccess());
		assertTrue(config.matches(TimeUnit.HOURS).isError());
		assertTrue(config.matches(TimeUnit.DAYS).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
