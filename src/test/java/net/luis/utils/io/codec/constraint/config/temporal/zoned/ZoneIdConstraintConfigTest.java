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
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
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
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullIn() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullNormalized() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullRegionBased() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullOffsetBased() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullFixedOffset() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null,
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullUtc() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			null, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullSystemDefault() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), null, Optional.empty(), Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullAvailable() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), null, Optional.empty(), Optional.empty()
		));
	}
	
	@Test
	void constructWithNullRegion() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), null, Optional.empty()
		));
	}
	
	@Test
	void constructWithNullCustom() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintConfig(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), null
		));
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
		assertTrue(config.matches(UTC).isSuccess());
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
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(zone -> zone.getId().startsWith("Europe") ? Result.success() : Result.error("Zone must be European"));
		assertTrue(config.custom().isPresent());
	}
	
	@Test
	void withCustomNull() {
		assertThrows(NullPointerException.class, () -> ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(null));
	}
	
	@Test
	void matchesWithEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withEqualTo(EUROPE_BERLIN);
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
	}
	
	@Test
	void matchesWithNotEqualTo() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotEqualTo(EUROPE_BERLIN);
		assertTrue(config.matches(AMERICA_NEW_YORK).isSuccess());
		assertTrue(config.matches(EUROPE_BERLIN).isError());
	}
	
	@Test
	void matchesWithIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isSuccess());
		assertTrue(config.matches(ASIA_TOKYO).isError());
	}
	
	@Test
	void matchesWithNotIn() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withNotIn(List.of(EUROPE_BERLIN, AMERICA_NEW_YORK));
		assertTrue(config.matches(ASIA_TOKYO).isSuccess());
		assertTrue(config.matches(EUROPE_BERLIN).isError());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
	}
	
	@Test
	void matchesWithRegionBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegionBased();
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isSuccess());
		assertTrue(config.matches(ZoneOffset.UTC).isError());
		assertTrue(config.matches(ZoneOffset.ofHours(2)).isError());
	}
	
	@Test
	void matchesWithOffsetBased() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withOffsetBased();
		assertTrue(config.matches(ZoneOffset.UTC).isSuccess());
		assertTrue(config.matches(ZoneOffset.ofHours(2)).isSuccess());
		assertTrue(config.matches(EUROPE_BERLIN).isError());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
	}
	
	@Test
	void matchesWithFixedOffset() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withFixedOffset();
		assertTrue(config.matches(ZoneOffset.UTC).isSuccess());
		assertTrue(config.matches(ZoneOffset.ofHours(5)).isSuccess());
		assertTrue(config.matches(EUROPE_BERLIN).isError());
	}
	
	@Test
	void matchesWithUtc() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		assertTrue(config.matches(ZoneOffset.UTC).isSuccess());
		assertTrue(config.matches(UTC).isSuccess());
		assertTrue(config.matches(EUROPE_BERLIN).isError());
		assertTrue(config.matches(ZoneOffset.ofHours(2)).isError());
	}
	
	@Test
	void matchesWithSystemDefault() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withSystemDefault();
		assertTrue(config.matches(ZoneId.systemDefault()).isSuccess());
		if (!ZoneId.systemDefault().equals(UTC)) {
			assertTrue(config.matches(UTC).isError());
		}
	}
	
	@Test
	void matchesWithAvailable() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withAvailable();
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isSuccess());
		assertTrue(config.matches(ASIA_TOKYO).isSuccess());
	}
	
	@Test
	void matchesWithRegionConstraint() {
		StringConstraintConfig regionConfig = StringConstraintConfig.UNCONSTRAINED.withStartsWith("Europe/");
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withRegion(regionConfig);
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
		assertTrue(config.matches(ASIA_TOKYO).isError());
	}
	
	@Test
	void matchesWithCustomConstraint() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED.withCustom(zone -> zone.getId().contains("Europe") ? Result.success() : Result.error("Zone must be European"));
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
		assertTrue(config.matches(ASIA_TOKYO).isError());
	}
	
	@Test
	void matchesWithMultipleConstraints() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED
			.withRegionBased()
			.withAvailable()
			.withNotIn(List.of(AMERICA_NEW_YORK));
		
		assertTrue(config.matches(EUROPE_BERLIN).isSuccess());
		assertTrue(config.matches(ASIA_TOKYO).isSuccess());
		assertTrue(config.matches(AMERICA_NEW_YORK).isError());
		assertTrue(config.matches(ZoneOffset.UTC).isError());
	}
	
	@Test
	void matchesWithNullValue() {
		ZoneIdConstraintConfig config = ZoneIdConstraintConfig.UNCONSTRAINED;
		assertThrows(NullPointerException.class, () -> config.matches(null));
	}
}
