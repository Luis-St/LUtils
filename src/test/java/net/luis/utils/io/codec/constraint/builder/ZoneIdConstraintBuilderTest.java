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

package net.luis.utils.io.codec.constraint.builder;

import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneIdConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneIdConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class ZoneIdConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		ZoneIdConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(ZoneIdConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		ZoneIdConstraintConfig initialConfig = ZoneIdConstraintConfig.UNCONSTRAINED.withUtc();
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder(initialConfig);
		ZoneIdConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.utc().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new ZoneIdConstraintBuilder(null));
	}
	
	@Test
	void applyThrowsUnsupportedOperationException() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(UnsupportedOperationException.class, () -> builder.apply(config -> config));
	}
	
	@Test
	void equalToReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.equalTo(ZoneId.of("UTC")));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.notEqualTo(ZoneId.of("UTC")));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.in(List.of(ZoneId.of("UTC"), ZoneId.of("Europe/Berlin"), ZoneId.of("America/New_York"))));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(ZoneId.of("Asia/Tokyo"), ZoneId.of("Australia/Sydney"))));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		Constraint<ZoneId> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void normalizedReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.normalized());
		assertTrue(builder.build().normalized().isPresent());
	}
	
	@Test
	void regionBasedReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.regionBased());
		assertTrue(builder.build().regionBased().isPresent());
	}
	
	@Test
	void offsetBasedReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.offsetBased());
		assertTrue(builder.build().offsetBased().isPresent());
	}
	
	@Test
	void fixedOffsetReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.fixedOffset());
		assertTrue(builder.build().fixedOffset().isPresent());
	}
	
	@Test
	void utcReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.utc());
		assertTrue(builder.build().utc().isPresent());
	}
	
	@Test
	void systemDefaultReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.systemDefault());
		assertTrue(builder.build().systemDefault().isPresent());
	}
	
	@Test
	void availableReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.available());
		assertTrue(builder.build().available().isPresent());
	}
	
	@Test
	void regionReturnsBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.region(b -> b.startsWith("Europe/")));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.region().isPresent());
		assertTrue(config.region().get().startsWith().isPresent());
	}
	
	@Test
	void regionWithNullBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.region(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.regionBased().available();
		
		ZoneIdConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.regionBased().isPresent());
		assertTrue(config.available().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		
		ZoneIdConstraintConfig config = builder
			.regionBased()
			.available()
			.normalized()
			.region(b -> b.startsWith("Europe/"))
			.build();
		
		assertNotNull(config);
		assertTrue(config.regionBased().isPresent());
		assertTrue(config.available().isPresent());
		assertTrue(config.normalized().isPresent());
		assertTrue(config.region().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void equalToSetsNegatedFalse() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.equalTo(ZoneId.of("Europe/Berlin"));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void notEqualToSetsNegatedTrue() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.notEqualTo(ZoneId.of("Europe/Berlin"));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void inSetsNegatedFalse() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.in(List.of(ZoneId.of("UTC"), ZoneId.of("Europe/Berlin")));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void notInSetsNegatedTrue() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.notIn(List.of(ZoneId.of("UTC"), ZoneId.of("Europe/Berlin")));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void worksWithZoneOffset() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.equalTo(ZoneOffset.ofHours(2)));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertEquals(ZoneOffset.ofHours(2), config.equalTo().get().getFirst());
	}
	
	@Test
	void worksWithRegionZoneId() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		assertSame(builder, builder.in(List.of(ZoneId.of("Europe/Berlin"), ZoneId.of("America/New_York"))));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.in().isPresent());
		assertEquals(2, config.in().get().getFirst().size());
	}
	
	@Test
	void regionConstraintWithComplexBuilder() {
		ZoneIdConstraintBuilder builder = new ZoneIdConstraintBuilder();
		builder.region(b -> b.startsWith("Europe/").notContains("Moscow").length(l -> l.minLength(5)));
		
		ZoneIdConstraintConfig config = builder.build();
		assertTrue(config.region().isPresent());
		assertTrue(config.region().get().startsWith().isPresent());
		assertTrue(config.region().get().contains().isPresent());
		assertTrue(config.region().get().length().isPresent());
	}
}
