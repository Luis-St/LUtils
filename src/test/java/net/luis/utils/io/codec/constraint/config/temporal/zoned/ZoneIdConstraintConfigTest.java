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

package net.luis.utils.io.codec.constraint.config.temporal.zoned;

import net.luis.utils.io.codec.constraint.config.StringConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintViolateException;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneIdConstraintConfig}.<br>
 *
 * @author Luis-St
 */
class ZoneIdConstraintConfigTest {
	
	private static final ZoneId UTC = ZoneId.of("UTC");
	private static final ZoneId EUROPE_BERLIN = ZoneId.of("Europe/Berlin");
	private static final ZoneId AMERICA_NEW_YORK = ZoneId.of("America/New_York");
	private static final ZoneId ASIA_TOKYO = ZoneId.of("Asia/Tokyo");
	
	@Test
	void constructWithNullEqualTo() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullNormalized() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullRegionBased() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullOffsetBased() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullFixedOffset() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullUtc() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullSystemDefault() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullAvailable() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()));
	}
	
	@Test
	void constructWithNullRegion() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null));
	}
	
	@Test
	void constructWithEmptyInSet() {
		assertThrows(IllegalArgumentException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.of(Pair.of(Set.of(), false)), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithBothRegionBasedAndOffsetBased() {
		assertThrows(IllegalArgumentException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(Unit.INSTANCE), Optional.of(Unit.INSTANCE), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void unconstrained() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED;
		assertNotNull(config);
		assertTrue(config.equalTo().isEmpty());
		assertTrue(config.in().isEmpty());
		assertTrue(config.normalized().isEmpty());
		assertTrue(config.regionBased().isEmpty());
		assertTrue(config.offsetBased().isEmpty());
		assertTrue(config.fixedOffset().isEmpty());
		assertTrue(config.utc().isEmpty());
		assertTrue(config.systemDefault().isEmpty());
		assertTrue(config.available().isEmpty());
		assertTrue(config.region().isEmpty());
		assertTrue(config.custom().isEmpty());
		assertDoesNotThrow(() -> config.validate(UTC));
	}
	
	@Test
	void isUnconstrainedWithUnconstrained() {
		assertTrue(ZoneIdConstraintConfig.UNCONSTRAINED.isUnconstrained());
	}
	
	@Test
	void isUnconstrainedWithConstraint() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNormalized();
		assertFalse(config.isUnconstrained());
	}
	
	@Test
	void withEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withEqualTo(EUROPE_BERLIN);
		assertTrue(config.equalTo().isPresent());
		assertEquals(EUROPE_BERLIN, config.equalTo().get().getFirst());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void withEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withEqualTo(null));
	}
	
	@Test
	void withNotEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotEqualTo(EUROPE_BERLIN);
		assertTrue(config.equalTo().isPresent());
		assertEquals(EUROPE_BERLIN, config.equalTo().get().getFirst());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void withNotEqualToNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withNotEqualTo(null));
	}
	
	@Test
	void withIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(EUROPE_BERLIN, AMERICA_NEW_YORK), config.in().get().getFirst());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void withInNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withIn(null));
	}
	
	@Test
	void withNotIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertTrue(config.in().isPresent());
		assertEquals(Set.of(EUROPE_BERLIN, AMERICA_NEW_YORK), config.in().get().getFirst());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void withNotInNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withNotIn(null));
	}
	
	@Test
	void withNormalized() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNormalized();
		assertTrue(config.normalized().isPresent());
		assertEquals(Unit.INSTANCE, config.normalized().get());
	}
	
	@Test
	void withRegionBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegionBased();
		assertTrue(config.regionBased().isPresent());
		assertEquals(Unit.INSTANCE, config.regionBased().get());
	}
	
	@Test
	void withOffsetBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withOffsetBased();
		assertTrue(config.offsetBased().isPresent());
		assertEquals(Unit.INSTANCE, config.offsetBased().get());
	}
	
	@Test
	void withFixedOffset() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withFixedOffset();
		assertTrue(config.fixedOffset().isPresent());
		assertEquals(Unit.INSTANCE, config.fixedOffset().get());
	}
	
	@Test
	void withUtc() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		assertTrue(config.utc().isPresent());
		assertEquals(Unit.INSTANCE, config.utc().get());
	}
	
	@Test
	void withSystemDefault() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withSystemDefault();
		assertTrue(config.systemDefault().isPresent());
		assertEquals(Unit.INSTANCE, config.systemDefault().get());
	}
	
	@Test
	void withAvailable() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withAvailable();
		assertTrue(config.available().isPresent());
		assertEquals(Unit.INSTANCE, config.available().get());
	}
	
	@Test
	void withRegion() {
		StringConstraintConfig regionConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("Europe/");
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegion(regionConfig);
		assertTrue(config.region().isPresent());
		assertEquals(regionConfig, config.region().get());
	}
	
	@Test
	void withRegionNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withRegion(null));
	}
	
	@Test
	void withCustom() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(zone -> {
			if (!zone.getId().contains("Europe")) throw new ConstraintViolateException("Zone must be European");
		});
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void validateWithEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withEqualTo(EUROPE_BERLIN);
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
	}
	
	@Test
	void validateWithNotEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotEqualTo(EUROPE_BERLIN);
		assertDoesNotThrow(() -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(EUROPE_BERLIN));
	}
	
	@Test
	void validateWithIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertDoesNotThrow(() -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ASIA_TOKYO));
	}
	
	@Test
	void validateWithNotIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertDoesNotThrow(() -> config.validate(ASIA_TOKYO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
	}
	
	@Test
	void validateWithRegionBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegionBased();
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertDoesNotThrow(() -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ZoneOffset.UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ZoneOffset.ofHours(2)));
	}
	
	@Test
	void validateWithOffsetBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withOffsetBased();
		assertDoesNotThrow(() -> config.validate(ZoneOffset.UTC));
		assertDoesNotThrow(() -> config.validate(ZoneOffset.ofHours(2)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
	}
	
	@Test
	void validateWithFixedOffset() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withFixedOffset();
		assertDoesNotThrow(() -> config.validate(ZoneOffset.UTC));
		assertDoesNotThrow(() -> config.validate(ZoneOffset.ofHours(5)));
		assertThrows(ConstraintViolateException.class, () -> config.validate(EUROPE_BERLIN));
	}
	
	@Test
	void validateWithUtc() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		assertDoesNotThrow(() -> config.validate(ZoneOffset.UTC));
		assertDoesNotThrow(() -> config.validate(UTC));
		assertThrows(ConstraintViolateException.class, () -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ZoneOffset.ofHours(2)));
	}
	
	@Test
	void validateWithSystemDefault() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withSystemDefault();
		assertDoesNotThrow(() -> config.validate(ZoneId.systemDefault()));
		if (!ZoneId.systemDefault().equals(UTC)) {
			assertThrows(ConstraintViolateException.class, () -> config.validate(UTC));
		}
	}
	
	@Test
	void validateWithAvailable() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withAvailable();
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertDoesNotThrow(() -> config.validate(AMERICA_NEW_YORK));
		assertDoesNotThrow(() -> config.validate(ASIA_TOKYO));
	}
	
	@Test
	void validateWithRegionConstraint() {
		StringConstraintConfig regionConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("Europe/");
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegion(regionConfig);
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ASIA_TOKYO));
	}
	
	@Test
	void validateWithCustomConstraint() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(zone -> {
			if (!zone.getId().contains("Europe")) throw new ConstraintViolateException("Zone must be European");
		});
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ASIA_TOKYO));
	}
	
	@Test
	void validateWithMultipleConstraints() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED
			.withRegionBased()
			.withAvailable()
			.withNotIn(List.of(AMERICA_NEW_YORK));
		
		assertDoesNotThrow(() -> config.validate(EUROPE_BERLIN));
		assertDoesNotThrow(() -> config.validate(ASIA_TOKYO));
		assertThrows(ConstraintViolateException.class, () -> config.validate(AMERICA_NEW_YORK));
		assertThrows(ConstraintViolateException.class, () -> config.validate(ZoneOffset.UTC));
	}
	
	@Test
	void validateWithNullValue() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.validate(null));
	}
}
