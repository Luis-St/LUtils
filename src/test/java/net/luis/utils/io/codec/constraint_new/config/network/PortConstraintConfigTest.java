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

package net.luis.utils.io.codec.constraint_new.config.network;

import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint_new.core.PortRange;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
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
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withCustom(value -> Result.success());

		assertTrue(config.custom().isPresent());
	}

	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.withCustom(null));
	}

	@Test
	void matchesUnconstrained() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED;

		assertTrue(config.matches(0).isSuccess());
		assertTrue(config.matches(80).isSuccess());
		assertTrue(config.matches(8080).isSuccess());
		assertTrue(config.matches(65535).isSuccess());
	}

	@Test
	void matchesWithNull() {
		assertThrows(NullPointerException.class, () -> PortConstraintConfig.UNCONSTRAINED.matches(null));
	}

	@Test
	void matchesEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withEqualTo(8080);

		assertTrue(config.matches(8080).isSuccess());
		assertTrue(config.matches(80).isError());
	}

	@Test
	void matchesNotEqualTo() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotEqualTo(22);

		assertTrue(config.matches(80).isSuccess());
		assertTrue(config.matches(22).isError());
	}

	@Test
	void matchesIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withIn(List.of(80, 443, 8080));

		assertTrue(config.matches(80).isSuccess());
		assertTrue(config.matches(443).isSuccess());
		assertTrue(config.matches(8080).isSuccess());
		assertTrue(config.matches(22).isError());
	}

	@Test
	void matchesNotIn() {
		PortConstraintConfig config = PortConstraintConfig.UNCONSTRAINED.withNotIn(List.of(22, 23));

		assertTrue(config.matches(80).isSuccess());
		assertTrue(config.matches(22).isError());
		assertTrue(config.matches(23).isError());
	}
}
