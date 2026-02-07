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

import net.luis.utils.io.codec.constraint.config.DepthConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DepthConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class DepthConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		DepthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(DepthConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		DepthConstraintConfig initialConfig = DepthConstraintConfig.UNCONSTRAINED.withMinDepth(1);
		DepthConstraintBuilder builder = new DepthConstraintBuilder(initialConfig);
		DepthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.min().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new DepthConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.equalTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.notEqualTo(10));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.in(List.of(1, 2, 3)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(0, 100)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		Constraint<Integer> constraint = value -> {};
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void minDepthReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.minDepth(1));
		assertTrue(builder.build().min().isPresent());
	}
	
	@Test
	void maxDepthReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.maxDepth(100));
		assertTrue(builder.build().max().isPresent());
	}
	
	@Test
	void exactDepthReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.exactDepth(10));
		
		DepthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void depthBetweenReturnsBuilder() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		assertSame(builder, builder.depthBetween(5, 20));
		
		DepthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		builder.minDepth(1).maxDepth(100);
		
		DepthConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		
		DepthConstraintConfig config = builder
			.minDepth(1)
			.maxDepth(50)
			.build();
		
		assertNotNull(config);
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void exactDepthSetsMinAndMax() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		builder.exactDepth(25);
		
		DepthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(25, config.min().get().getFirst());
		assertEquals(25, config.max().get().getFirst());
	}
	
	@Test
	void depthBetweenSetsMinAndMax() {
		DepthConstraintBuilder builder = new DepthConstraintBuilder();
		builder.depthBetween(10, 50);
		
		DepthConstraintConfig config = builder.build();
		assertTrue(config.min().isPresent());
		assertTrue(config.max().isPresent());
		assertEquals(10, config.min().get().getFirst());
		assertEquals(50, config.max().get().getFirst());
	}
}
