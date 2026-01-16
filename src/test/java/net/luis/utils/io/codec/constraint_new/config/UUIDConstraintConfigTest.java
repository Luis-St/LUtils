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

package net.luis.utils.io.codec.constraint_new.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.luis.utils.io.codec.constraint_new.core.UUIDVariant;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;

import java.util.*;

/**
 * Test class for {@link UUIDConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class UUIDConstraintConfigTest {

	private static final UUID NIL_UUID = new UUID(0L, 0L);
	private static final UUID MAX_UUID = new UUID(-1L, -1L);
	private static final UUID RANDOM_UUID = UUID.randomUUID();

	@Test
	void unconstrained() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.version().isEmpty());
		assertTrue(config.variant().isEmpty());
		assertTrue(config.nil().isEmpty());
		assertTrue(config.notNil().isEmpty());
		assertTrue(config.max().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertTrue(config.matches(RANDOM_UUID).isSuccess());
	}

	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullVersion() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullVariant() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNil() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullNotNil() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithNullMax() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}

	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
	}

	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new UUIDConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}

	@Test
	void constructWithInvalidVersionNegative() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withVersion(-1));
	}

	@Test
	void constructWithInvalidVersionTooHigh() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withVersion(6));
	}

	@Test
	void constructWithNilAndNotNil() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNil().withNotNil());
	}

	@Test
	void constructWithNilAndMax() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNil().withMax());
	}

	@Test
	void withEqualTo() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withEqualTo(RANDOM_UUID);
		assertTrue(config.equalTo().isPresent());
		assertEquals(RANDOM_UUID, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}

	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}

	@Test
	void withNotEqualTo() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotEqualTo(RANDOM_UUID);
		assertTrue(config.equalTo().isPresent());
		assertEquals(RANDOM_UUID, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}

	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}

	@Test
	void withIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withIn(List.of(uuid1, uuid2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(uuid1, uuid2), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}

	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withIn(null));
	}

	@Test
	void withNotIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotIn(List.of(uuid1, uuid2));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(uuid1, uuid2), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}

	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}

	@Test
	void withVersion() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVersion(4);
		assertTrue(config.version().isPresent());
		assertEquals(4, config.version().get());
	}

	@Test
	void withVariant() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVariant(UUIDVariant.RFC_4122);
		assertTrue(config.variant().isPresent());
		assertEquals(UUIDVariant.RFC_4122, config.variant().get());
	}

	@Test
	void withVariantNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withVariant(null));
	}

	@Test
	void withNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNil();
		assertTrue(config.nil().isPresent());
	}

	@Test
	void withNotNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotNil();
		assertTrue(config.notNil().isPresent());
	}

	@Test
	void withMax() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withMax();
		assertTrue(config.max().isPresent());
	}

	@Test
	void withCustom() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withCustom(u -> u.version() != 4 ? Result.error("Must be version 4") : Result.success());
		assertTrue(config.custom().isPresent());
	}

	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withCustom(null));
	}

	@Test
	void matchesWithEqualTo() {
		UUID uuid = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withEqualTo(uuid);
		assertTrue(config.matches(uuid).isSuccess());
		assertTrue(config.matches(UUID.randomUUID()).isError());
	}

	@Test
	void matchesWithNotEqualTo() {
		UUID uuid = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotEqualTo(uuid);
		assertTrue(config.matches(UUID.randomUUID()).isSuccess());
		assertTrue(config.matches(uuid).isError());
	}

	@Test
	void matchesWithIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withIn(List.of(uuid1, uuid2));
		assertTrue(config.matches(uuid1).isSuccess());
		assertTrue(config.matches(uuid2).isSuccess());
		assertTrue(config.matches(UUID.randomUUID()).isError());
	}

	@Test
	void matchesWithNotIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotIn(List.of(uuid1, uuid2));
		assertTrue(config.matches(UUID.randomUUID()).isSuccess());
		assertTrue(config.matches(uuid1).isError());
		assertTrue(config.matches(uuid2).isError());
	}

	@Test
	void matchesWithVersion() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVersion(4);
		assertTrue(config.matches(RANDOM_UUID).isSuccess());
		assertTrue(config.matches(NIL_UUID).isError());
	}

	@Test
	void matchesWithVariant() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVariant(UUIDVariant.RFC_4122);
		assertTrue(config.matches(RANDOM_UUID).isSuccess());
		assertTrue(config.matches(NIL_UUID).isError());
	}

	@Test
	void matchesWithNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNil();
		assertTrue(config.matches(NIL_UUID).isSuccess());
		assertTrue(config.matches(RANDOM_UUID).isError());
	}

	@Test
	void matchesWithNotNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotNil();
		assertTrue(config.matches(RANDOM_UUID).isSuccess());
		assertTrue(config.matches(NIL_UUID).isError());
	}

	@Test
	void matchesWithMax() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withMax();
		assertTrue(config.matches(MAX_UUID).isSuccess());
		assertTrue(config.matches(RANDOM_UUID).isError());
	}

	@Test
	void matchesWithMultipleConstraints() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED
			.withVersion(4)
			.withVariant(UUIDVariant.RFC_4122)
			.withNotNil();

		assertTrue(config.matches(RANDOM_UUID).isSuccess());
		assertTrue(config.matches(NIL_UUID).isError());
		assertTrue(config.matches(MAX_UUID).isError());
	}

	@Test
	void matchesWithNullValue() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
