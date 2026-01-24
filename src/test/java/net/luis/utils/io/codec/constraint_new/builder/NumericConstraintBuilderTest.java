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

package net.luis.utils.io.codec.constraint_new.builder;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NumericConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class NumericConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		NumericConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(NumericConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		NumericConstraintConfig initialConfig = NumericConstraintConfig.UNCONSTRAINED.withGreaterThan(0);
		NumericConstraintBuilder builder = new NumericConstraintBuilder(initialConfig);
		NumericConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.min().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new NumericConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.equalTo(42));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.notEqualTo(42));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.in(List.of(1, 2, 3)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(1, 2, 3)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		Constraint<Integer> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void greaterThanReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.greaterThan(10));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void greaterThanWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.greaterThan(null));
	}
	
	@Test
	void greaterThanOrEqualReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.greaterThanOrEqual(10));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void greaterThanOrEqualWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.greaterThanOrEqual(null));
	}
	
	@Test
	void lessThanReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.lessThan(100));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void lessThanWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.lessThan(null));
	}
	
	@Test
	void lessThanOrEqualReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.lessThanOrEqual(100));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void lessThanOrEqualWithNullValue() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.lessThanOrEqual(null));
	}
	
	@Test
	void betweenReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.between(10, 100));
		
		NumericConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void betweenWithNullMin() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.between(null, 100));
	}
	
	@Test
	void betweenWithNullMax() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.between(10, null));
	}
	
	@Test
	void betweenOrEqualReturnsBuilder() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertSame(builder, builder.betweenOrEqual(10, 100));
		
		NumericConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void betweenOrEqualWithNullMin() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.betweenOrEqual(null, 100));
	}
	
	@Test
	void betweenOrEqualWithNullMax() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.betweenOrEqual(10, null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		builder.greaterThanOrEqual(0).lessThanOrEqual(100);
		
		NumericConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		NumericConstraintBuilder builder = new NumericConstraintBuilder();
		
		NumericConstraintConfig config = builder
			.greaterThanOrEqual(0)
			.lessThanOrEqual(1000)
			.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
}
