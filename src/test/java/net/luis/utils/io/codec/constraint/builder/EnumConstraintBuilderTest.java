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

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.util.IpVersion;
import net.luis.utils.io.codec.constraint.util.PortRange;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EnumConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class EnumConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		EnumConstraintConfig<TimeUnit> config = builder.build();
		
		assertNotNull(config);
		assertEquals(EnumConstraintConfig.<TimeUnit>unconstrained(), config);
	}
	
	@Test
	void constructWithInitialConfig() {
		EnumConstraintConfig<TimeUnit> initialConfig = EnumConstraintConfig.<TimeUnit>unconstrained()
			.withEqualTo(TimeUnit.SECONDS);
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>(initialConfig);
		EnumConstraintConfig<TimeUnit> config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.equalTo().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new EnumConstraintBuilder<TimeUnit>(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.equalTo(TimeUnit.SECONDS));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.notEqualTo(TimeUnit.MILLISECONDS));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.in(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.notIn(List.of(TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS)));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		Constraint<TimeUnit> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		builder.in(List.of(TimeUnit.SECONDS, TimeUnit.MINUTES));
		
		EnumConstraintConfig<TimeUnit> config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.in().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		
		EnumConstraintConfig<TimeUnit> config = builder
			.notIn(List.of(TimeUnit.NANOSECONDS, TimeUnit.MICROSECONDS))
			.build();
		
		assertNotNull(config);
		assertTrue(config.in().isPresent());
		assertFalse(config.equalTo().isPresent());
	}
	
	@Test
	void worksWithIpVersion() {
		EnumConstraintBuilder<IpVersion> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.equalTo(IpVersion.IPV4));
		
		EnumConstraintConfig<IpVersion> config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertEquals(IpVersion.IPV4, config.equalTo().get().getFirst());
	}
	
	@Test
	void worksWithPortRange() {
		EnumConstraintBuilder<PortRange> builder = new EnumConstraintBuilder<>();
		assertSame(builder, builder.in(List.of(PortRange.REGISTERED, PortRange.DYNAMIC)));
		
		EnumConstraintConfig<PortRange> config = builder.build();
		assertTrue(config.in().isPresent());
	}
	
	@Test
	void equalToSetsNegatedFalse() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		builder.equalTo(TimeUnit.HOURS);
		
		EnumConstraintConfig<TimeUnit> config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertFalse(config.equalTo().get().getSecond());
	}
	
	@Test
	void notEqualToSetsNegatedTrue() {
		EnumConstraintBuilder<TimeUnit> builder = new EnumConstraintBuilder<>();
		builder.notEqualTo(TimeUnit.HOURS);
		
		EnumConstraintConfig<TimeUnit> config = builder.build();
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.equalTo().get().getSecond());
	}
}
