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

import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LengthConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class LengthConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		LengthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(LengthConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		LengthConstraintConfig initialConfig = LengthConstraintConfig.UNCONSTRAINED.withMinLength(1);
		LengthConstraintBuilder builder = new LengthConstraintBuilder(initialConfig);
		LengthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.min().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new LengthConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.equalTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.notEqualTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.in(List.of(1, 2, 3)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(0, 100)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		Constraint<Integer> constraint = value -> {};
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void minLengthReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.minLength(1));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void maxLengthReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.maxLength(100));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void exactLengthReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.exactLength(10));
		
		LengthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void lengthBetweenReturnsBuilder() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		assertSame(builder, builder.lengthBetween(5, 20));
		
		LengthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		builder.minLength(1).maxLength(100);
		
		LengthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		
		LengthConstraintConfig config = builder
			.minLength(1)
			.maxLength(50)
			.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void exactLengthSetsMinAndMax() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		builder.exactLength(25);
		
		LengthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(25, config.min().get().getFirst());
		assertEquals(25, config.max().get().getFirst());
	}
	
	@Test
	void lengthBetweenSetsMinAndMax() {
		LengthConstraintBuilder builder = new LengthConstraintBuilder();
		builder.lengthBetween(10, 50);
		
		LengthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertEquals(50, config.max().get().getFirst());
	}
}
