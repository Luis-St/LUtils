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
import net.luis.utils.io.codec.constraint.config.io.HostConstraintConfig;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HostConstraintBuilder}.<br>
 *
 * @author Luis-St
 */
class HostConstraintBuilderTest {
	
	@Test
	void constructEmpty() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		HostConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(HostConstraintConfig.UNCONSTRAINED, config);
	}
	
	@Test
	void constructWithInitialConfig() {
		HostConstraintConfig initialConfig = HostConstraintConfig.UNCONSTRAINED.withEqualTo("localhost");
		HostConstraintBuilder builder = new HostConstraintBuilder(initialConfig);
		HostConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertEquals(initialConfig, config);
		assertTrue(config.equalTo().isPresent());
	}
	
	@Test
	void constructWithNullInitialConfig() {
		assertThrows(NullPointerException.class, () -> new HostConstraintBuilder(null));
	}
	
	@Test
	void equalToReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.equalTo("localhost"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void equalToWithNullValue() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.equalTo(null));
	}
	
	@Test
	void notEqualToReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.notEqualTo("localhost"));
		assertTrue(builder.build().equalTo().isPresent());
	}
	
	@Test
	void notEqualToWithNullValue() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notEqualTo(null));
	}
	
	@Test
	void inReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.in(List.of("localhost", "127.0.0.1")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void inWithNullValues() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.in(null));
	}
	
	@Test
	void notInReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.notIn(List.of("localhost", "127.0.0.1")));
		assertTrue(builder.build().in().isPresent());
	}
	
	@Test
	void notInWithNullValues() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.notIn(null));
	}
	
	@Test
	void customReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		Constraint<String> constraint = value -> Result.success(null);
		assertSame(builder, builder.custom(constraint));
		assertTrue(builder.build().custom().isPresent());
	}
	
	@Test
	void customWithNullConstraint() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.custom(null));
	}
	
	@Test
	void ipReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.ip(b -> b.startsWith("192.")));
		assertTrue(builder.build().ip().isPresent());
	}
	
	@Test
	void ipWithNullBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.ip(null));
	}
	
	@Test
	void domainReturnsBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertSame(builder, builder.domain(b -> b.endsWith(".com")));
		assertTrue(builder.build().domain().isPresent());
	}
	
	@Test
	void domainWithNullBuilder() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		assertThrows(NullPointerException.class, () -> builder.domain(null));
	}
	
	@Test
	void buildReturnsCorrectConfig() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		builder.equalTo("localhost").ip(b -> b.length(ib -> ib.minLength(7)));
		
		HostConstraintConfig config = builder.build();
		
		assertNotNull(config);
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.ip().isPresent());
	}
	
	@Test
	void methodChainingWorks() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		
		HostConstraintConfig config = builder
			.notEqualTo("blocked-host")
			.ip(b -> b.length(ib -> ib.minLength(7).maxLength(15)))
			.build();
		
		assertNotNull(config);
		assertTrue(config.equalTo().isPresent());
		assertTrue(config.ip().isPresent());
	}
	
	@Test
	void ipClearsDomain() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		builder.domain(b -> b.endsWith(".com"));
		builder.ip(b -> b.startsWith("192."));
		
		HostConstraintConfig config = builder.build();
		assertTrue(config.ip().isPresent());
		assertFalse(config.domain().isPresent());
	}
	
	@Test
	void domainClearsIp() {
		HostConstraintBuilder builder = new HostConstraintBuilder();
		builder.ip(b -> b.startsWith("192."));
		builder.domain(b -> b.endsWith(".com"));
		
		HostConstraintConfig config = builder.build();
		assertFalse(config.ip().isPresent());
		assertTrue(config.domain().isPresent());
	}
}
