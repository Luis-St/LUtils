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

import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.util.Pair;
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
		assertDoesNotThrow(() -> config.validate(TimeUnit.SECONDS));
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
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withCustom(v -> {
			if (v.ordinal() <= 2) throw new ConstraintViolateException("Ordinal must be > 2");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> EnumConstraintConfig.<TimeUnit>unconstrained().withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withEqualTo(TimeUnit.SECONDS);
		assertDoesNotThrow(() -> config.validate(TimeUnit.SECONDS));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.MINUTES));
	}
	
	@Test
	void validateWithNotEqualTo() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotEqualTo(TimeUnit.SECONDS);
		assertDoesNotThrow(() -> config.validate(TimeUnit.MINUTES));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.SECONDS));
	}
	
	@Test
	void validateWithIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withIn(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES));
		assertDoesNotThrow(() -> config.validate(TimeUnit.SECONDS));
		assertDoesNotThrow(() -> config.validate(TimeUnit.MINUTES));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.HOURS));
	}
	
	@Test
	void validateWithNotIn() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained().withNotIn(List.of(TimeUnit.HOURS, TimeUnit.DAYS));
		assertDoesNotThrow(() -> config.validate(TimeUnit.SECONDS));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.HOURS));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.DAYS));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.<TimeUnit>unconstrained()
			.withIn(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS))
			.withNotEqualTo(TimeUnit.HOURS);
		
		assertDoesNotThrow(() -> config.validate(TimeUnit.SECONDS));
		assertDoesNotThrow(() -> config.validate(TimeUnit.MINUTES));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.HOURS));
		assertThrows(ConstraintViolateException.class, () -> config.validate(TimeUnit.DAYS));
	}
	
	@Test
	void validateWithNullValue() {
		EnumConstraintConfig<TimeUnit> config = EnumConstraintConfig.unconstrained();
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
