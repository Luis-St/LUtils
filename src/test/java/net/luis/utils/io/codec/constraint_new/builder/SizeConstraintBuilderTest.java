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

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.SizeConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SizeConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class SizeConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		SizeConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(SizeConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		SizeConstraintConfig initialConfig = SizeConstraintConfig.UNCONSTRAINED.withMinSize(1);
		SizeConstraintBuilder builder = new SizeConstraintBuilder(initialConfig);
		SizeConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.min().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new SizeConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.equalTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.notEqualTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.in(List.of(1, 2, 3)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(0, 100)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		Constraint<Integer> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void minSizeReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.minSize(1));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void maxSizeReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.maxSize(100));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void exactSizeReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.exactSize(10));
		
		SizeConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void sizeBetweenReturnsBuilder() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		assertSame(builder, builder.sizeBetween(5, 20));
		
		SizeConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		builder.minSize(1).maxSize(100);
		
		SizeConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		
		SizeConstraintConfig config = builder
			.minSize(1)
			.maxSize(50)
			.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void exactSizeSetsMinAndMax() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		builder.exactSize(25);
		
		SizeConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(25, config.min().get().getFirst());
		assertEquals(25, config.max().get().getFirst());
	}
	
	@Test
	void sizeBetweenSetsMinAndMax() {
		SizeConstraintBuilder builder = new SizeConstraintBuilder();
		builder.sizeBetween(10, 50);
		
		SizeConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertEquals(50, config.max().get().getFirst());
	}
}
