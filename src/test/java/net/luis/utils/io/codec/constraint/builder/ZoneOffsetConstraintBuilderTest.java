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

import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ZoneOffsetConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class ZoneOffsetConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		ZoneOffsetConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(ZoneOffsetConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		ZoneOffsetConstraintConfig initialConfig = ZoneOffsetConstraintConfig.UNCONSTRAINED.withPositive();
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder(initialConfig);
		ZoneOffsetConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.positive().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new ZoneOffsetConstraintBuilder(null));
	}
	
	@Test
	void applyThrowsUnsupportedOperationException() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(UnsupportedOperationException.class, () -> builder.apply(config -> config));
	}
	
	@Test
	void equalToReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.equalTo(ZoneOffset.UTC));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.notEqualTo(ZoneOffset.UTC));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.in(List.of(ZoneOffset.UTC, ZoneOffset.ofHours(1), ZoneOffset.ofHours(-1))));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(ZoneOffset.ofHours(12), ZoneOffset.ofHours(-12))));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		Constraint<ZoneOffset> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void greaterThanReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.greaterThan(ZoneOffset.ofHours(-5)));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void greaterThanWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.greaterThan(null));
	}
	
	@Test
	void greaterThanOrEqualReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.greaterThanOrEqual(ZoneOffset.ofHours(-5)));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void greaterThanOrEqualWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.greaterThanOrEqual(null));
	}
	
	@Test
	void lessThanReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.lessThan(ZoneOffset.ofHours(5)));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void lessThanWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.lessThan(null));
	}
	
	@Test
	void lessThanOrEqualReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.lessThanOrEqual(ZoneOffset.ofHours(5)));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void lessThanOrEqualWithNullValue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.lessThanOrEqual(null));
	}
	
	@Test
	void betweenReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.between(ZoneOffset.ofHours(-5), ZoneOffset.ofHours(5)));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void betweenWithNullMin() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.between(null, ZoneOffset.ofHours(5)));
	}
	
	@Test
	void betweenWithNullMax() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.between(ZoneOffset.ofHours(-5), null));
	}
	
	@Test
	void betweenOrEqualReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.betweenOrEqual(ZoneOffset.ofHours(-5), ZoneOffset.ofHours(5)));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void betweenOrEqualWithNullMin() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.betweenOrEqual(null, ZoneOffset.ofHours(5)));
	}
	
	@Test
	void betweenOrEqualWithNullMax() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.betweenOrEqual(ZoneOffset.ofHours(-5), null));
	}
	
	@Test
	void positiveReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.positive());
		assertTrue(builder.build().positive().isPresent());
		assertFalse(builder.build().positive().get());
	}
	
	@Test
	void negativeReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.negative());
		assertTrue(builder.build().negative().isPresent());
		assertFalse(builder.build().negative().get());
	}
	
	@Test
	void nonPositiveReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.nonPositive());
		assertTrue(builder.build().positive().isPresent());
		assertTrue(builder.build().positive().get());
	}
	
	@Test
	void nonNegativeReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.nonNegative());
		assertTrue(builder.build().negative().isPresent());
		assertTrue(builder.build().negative().get());
	}
	
	@Test
	void zeroReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.zero());
		assertTrue(builder.build().zero().isPresent());
		assertFalse(builder.build().zero().get());
	}
	
	@Test
	void nonZeroReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.nonZero());
		assertTrue(builder.build().zero().isPresent());
		assertTrue(builder.build().zero().get());
	}
	
	@Test
	void utcReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.utc());
		assertTrue(builder.build().zero().isPresent());
		assertFalse(builder.build().zero().get());
	}
	
	@Test
	void utcIsSameAsZero() {
		ZoneOffsetConstraintBuilder zeroBuilder = new ZoneOffsetConstraintBuilder();
		ZoneOffsetConstraintBuilder utcBuilder = new ZoneOffsetConstraintBuilder();
		
		zeroBuilder.zero();
		utcBuilder.utc();
		
		assertEquals(zeroBuilder.build(), utcBuilder.build());
	}
	
	@Test
	void hourReturnsBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertSame(builder, builder.hour(b -> b.greaterThanOrEqual(-5).lessThanOrEqual(5)));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.hour().isPresent());
		assertTrue(config.hour().get().min().isPresent());
		assertTrue(config.hour().get().max().isPresent());
	}
	
	@Test
	void hourWithNullBuilder() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.hour(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.greaterThanOrEqual(ZoneOffset.ofHours(-5)).lessThanOrEqual(ZoneOffset.ofHours(5));
		
		ZoneOffsetConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		
		ZoneOffsetConstraintConfig config = builder
			.greaterThanOrEqual(ZoneOffset.ofHours(-5))
			.lessThanOrEqual(ZoneOffset.ofHours(5))
			.notIn(List.of(ZoneOffset.ofHours(3)))
			.hour(b -> b.notEqualTo(0))
			.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertTrue(config.in().isPresent());
		assertTrue(config.hour().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void equalToSetsNegatedFalse() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.equalTo(ZoneOffset.ofHours(2));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void notEqualToSetsNegatedTrue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.notEqualTo(ZoneOffset.ofHours(2));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.equalTo().get().getSecond());
	}
	
	@Test
	void inSetsNegatedFalse() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.in(List.of(ZoneOffset.UTC, ZoneOffset.ofHours(1)));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.in().isPresent());
		assertFalse(config.in().get().getSecond());
	}
	
	@Test
	void notInSetsNegatedTrue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.notIn(List.of(ZoneOffset.UTC, ZoneOffset.ofHours(1)));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.in().isPresent());
		assertTrue(config.in().get().getSecond());
	}
	
	@Test
	void greaterThanSetsInclusiveFalse() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.greaterThan(ZoneOffset.ofHours(1));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertFalse(config.min().get().getSecond());
	}
	
	@Test
	void greaterThanOrEqualSetsInclusiveTrue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.greaterThanOrEqual(ZoneOffset.ofHours(1));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.min().get().getSecond());
	}
	
	@Test
	void lessThanSetsInclusiveFalse() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.lessThan(ZoneOffset.ofHours(1));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.max().isPresent());
		assertFalse(config.max().get().getSecond());
	}
	
	@Test
	void lessThanOrEqualSetsInclusiveTrue() {
		ZoneOffsetConstraintBuilder builder = new ZoneOffsetConstraintBuilder();
		builder.lessThanOrEqual(ZoneOffset.ofHours(1));
		
		ZoneOffsetConstraintConfig config = builder.build();
		assertTrue(config.max().isPresent());
		assertTrue(config.max().get().getSecond());
	}
}
