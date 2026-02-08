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

import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.UUIDVariant;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
	void constructWithNilAndNotNil() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNil().withNotNil());
	}
	
	@Test
	void constructWithNilAndMax() {
		assertThrows(IllegalArgumentException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withNil().withMax());
	}
	
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
		assertDoesNotThrow(() -> config.validate(RANDOM_UUID));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(UUIDConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotNil();
		assertFalse(config.isUnconstrained());
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
		NumericConstraintConfig versionConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(4);
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVersion(versionConfig);
		assertTrue(config.version().isPresent());
		assertEquals(versionConfig, config.version().get());
	}
	
	@Test
	void withVersionNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withVersion(null));
	}
	
	@Test
	void withVariant() {
		EnumConstraintConfig<UUIDVariant> variantConfig = EnumConstraintConfig.<UUIDVariant>unconstrained().withEqualTo(UUIDVariant.RFC_4122);
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVariant(variantConfig);
		assertTrue(config.variant().isPresent());
		assertEquals(variantConfig, config.variant().get());
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
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withCustom(u -> {
			if (u.version() != 4) throw new ConstraintViolateException("Must be version 4");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> UUIDConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		UUID uuid = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withEqualTo(uuid);
		assertDoesNotThrow(() -> config.validate(uuid));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UUID.randomUUID()));
	}
	
	@Test
	void validateWithNotEqualTo() {
		UUID uuid = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotEqualTo(uuid);
		assertDoesNotThrow(() -> config.validate(UUID.randomUUID()));
		assertThrows(ConstraintViolateException.class, () -> config.validate(uuid));
	}
	
	@Test
	void validateWithIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withIn(List.of(uuid1, uuid2));
		assertDoesNotThrow(() -> config.validate(uuid1));
		assertDoesNotThrow(() -> config.validate(uuid2));
		assertThrows(ConstraintViolateException.class, () -> config.validate(UUID.randomUUID()));
	}
	
	@Test
	void validateWithNotIn() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotIn(List.of(uuid1, uuid2));
		assertDoesNotThrow(() -> config.validate(UUID.randomUUID()));
		assertThrows(ConstraintViolateException.class, () -> config.validate(uuid1));
		assertThrows(ConstraintViolateException.class, () -> config.validate(uuid2));
	}
	
	@Test
	void validateWithVersion() {
		NumericConstraintConfig versionConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(4);
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVersion(versionConfig);
		assertDoesNotThrow(() -> config.validate(RANDOM_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(NIL_UUID));
	}
	
	@Test
	void validateWithVariant() {
		EnumConstraintConfig<UUIDVariant> variantConfig = EnumConstraintConfig.<UUIDVariant>unconstrained().withEqualTo(UUIDVariant.RFC_4122);
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withVariant(variantConfig);
		assertDoesNotThrow(() -> config.validate(RANDOM_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(NIL_UUID));
	}
	
	@Test
	void validateWithNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNil();
		assertDoesNotThrow(() -> config.validate(NIL_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(RANDOM_UUID));
	}
	
	@Test
	void validateWithNotNil() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withNotNil();
		assertDoesNotThrow(() -> config.validate(RANDOM_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(NIL_UUID));
	}
	
	@Test
	void validateWithMax() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED.withMax();
		assertDoesNotThrow(() -> config.validate(MAX_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(RANDOM_UUID));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		NumericConstraintConfig versionConfig = NumericConstraintConfig.UNCONSTRAINED.withEqualTo(4);
		EnumConstraintConfig<UUIDVariant> variantConfig = EnumConstraintConfig.<UUIDVariant>unconstrained().withEqualTo(UUIDVariant.RFC_4122);
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED
			.withVersion(versionConfig)
			.withVariant(variantConfig)
			.withNotNil();
		
		assertDoesNotThrow(() -> config.validate(RANDOM_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(NIL_UUID));
		assertThrows(ConstraintViolateException.class, () -> config.validate(MAX_UUID));
	}
	
	@Test
	void validateWithNullValue() {
		UUIDConstraintConfig config = UUIDConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
