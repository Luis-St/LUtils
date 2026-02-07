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

package net.luis.utils.io.codec.constraint.config.io;

import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.PortRange;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PortConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class PortConstraintConfigTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> PortConstraintConfig.UNCONSTRAINED);
		assertDoesNotThrow(() -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorNullChecks() {
		assertThrows(NullPointerException.class, () -> new PortConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new PortConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
		assertThrows(NullPointerException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}
	
	@Test
	void constructorEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorEqualToPortRange() {
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.of(Pair.of(-1, false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.of(Pair.of(65536, false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorInPortRange() {
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(-1), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(65536), false)), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructorInRangeValidation() {
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(Pair.of(-1, 100), false)), Optional.empty(), Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(Pair.of(0, 65536), false)), Optional.empty(), Optional.empty()
		));
		assertThrows(IllegalArgumentException.class, () -> new PortConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.of(Pair.of(Pair.of(1000, 500), false)), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.inRange().isEmpty());
		assertTrue(config.type().isEmpty());
		assertTrue(config.custom().isEmpty());
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(PortConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(80);
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(8080, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotEqualTo(22);
		
		assertTrue(config.equalTo().isPresent());
		assertEquals(22, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withIn(List.of(80, 443, 8080));
		
		assertTrue(config.in().isPresent());
		assertEquals(3, config.in().get().getFirst().size());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotIn(List.of(22, 23));
		
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withInRange() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 65535);
		
		assertTrue(config.inRange().isPresent());
		assertEquals(1024, config.inRange().get().getFirst().getFirst());
		assertEquals(65535, config.inRange().get().getFirst().getSecond());
		assertFalse(config.inRange().get().getSecond());
	}
	
	@Test
	void withNotInRange() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotInRange(0, 1023);
		
		assertTrue(config.inRange().isPresent());
		assertEquals(0, config.inRange().get().getFirst().getFirst());
		assertEquals(1023, config.inRange().get().getFirst().getSecond());
		assertTrue(config.inRange().get().getSecond());
	}
	
	@Test
	void withType() {
		EnumConstraintConfig<PortRange> typeConfig = EnumConstraintConfig.<PortRange>unconstrained().withEqualTo(PortRange.REGISTERED);
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withType(typeConfig);
		
		assertTrue(config.type().isPresent());
	}
	
	@Test
	void withTypeNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.withType(null));
	}
	
	@Test
	void withCustom() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withCustom(value -> {});
		
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateUnconstrained() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED;
		
		assertDoesNotThrow(() -> config.validate(0));
		assertDoesNotThrow(() -> config.validate(80));
		assertDoesNotThrow(() -> config.validate(8080));
		assertDoesNotThrow(() -> config.validate(65535));
	}
	
	@Test
	void validateWithNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.validate(null));
	}
	
	@Test
	void validateEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);
		
		assertDoesNotThrow(() -> config.validate(8080));
		assertThrows(ConstraintViolateException.class, () -> config.validate(80));
	}
	
	@Test
	void validateNotEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotEqualTo(22);
		
		assertDoesNotThrow(() -> config.validate(80));
		assertThrows(ConstraintViolateException.class, () -> config.validate(22));
	}
	
	@Test
	void validateIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withIn(List.of(80, 443, 8080));
		
		assertDoesNotThrow(() -> config.validate(80));
		assertDoesNotThrow(() -> config.validate(443));
		assertDoesNotThrow(() -> config.validate(8080));
		assertThrows(ConstraintViolateException.class, () -> config.validate(22));
	}
	
	@Test
	void validateNotIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotIn(List.of(22, 23));
		
		assertDoesNotThrow(() -> config.validate(80));
		assertThrows(ConstraintViolateException.class, () -> config.validate(22));
		assertThrows(ConstraintViolateException.class, () -> config.validate(23));
	}
}
