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
import net.luis.utils.io.codec.constraint.config.io.PortConstraintConfig;
import net.luis.utils.io.codec.constraint.util.PortRange;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PortConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class PortConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		PortConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(PortConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		PortConstraintConfig initialConfig = PortConstraintConfig.UNCONSTRAINED.withInRange(1024, 49151);
		PortConstraintBuilder builder = new PortConstraintBuilder(initialConfig);
		PortConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.inRange().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new PortConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.equalTo(8080));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.notEqualTo(8080));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.in(List.of(80, 443, 8080)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.notIn(List.of(80, 443, 8080)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		Constraint<Integer> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void inRangeReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.inRange(1024, 49151));
		assertTrue(builder.build().inRange().isPresent());
	}
	
	@Test
	void notInRangeReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.notInRange(0, 1023));
		assertTrue(builder.build().inRange().isPresent());
	}
	
	@Test
	void typeReturnsBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertSame(builder, builder.type(b -> b.equalTo(PortRange.REGISTERED)));
		assertTrue(builder.build().type().isPresent());
	}
	
	@Test
	void typeWithNullBuilder() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.type(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		builder.inRange(1024, 65535).notIn(List.of(3306, 5432));
		
		PortConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.inRange().isPresent());
		assertTrue(config.in().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		PortConstraintBuilder builder = new PortConstraintBuilder();
		
		PortConstraintConfig config = builder
			.inRange(1024, 65535)
			.type(b -> b.equalTo(PortRange.REGISTERED))
			.notIn(List.of(3306, 5432))
			.build();
		
		assertNotNull(config);
		assertTrue(config.inRange().isPresent());
		assertTrue(config.type().isPresent());
		assertTrue(config.in().isPresent());
	}
}
