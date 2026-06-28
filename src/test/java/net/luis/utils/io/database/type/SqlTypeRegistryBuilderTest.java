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

package net.luis.utils.io.database.type;

import org.junit.jupiter.api.Test;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypeRegistryBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlTypeRegistryBuilderTest {
	
	private static final SqlValueBinder BINDER = (statement, index, value) -> {};
	private static final SqlValueReader READER = (resultSet, index) -> null;
	
	@Test
	void registerMappingWithNullType() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(NullPointerException.class, () -> builder.register(null, new SqlTypeMapping("VARCHAR")));
	}
	
	@Test
	void registerMappingWithNullMapping() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(NullPointerException.class, () -> builder.register(STRING_TYPE, (SqlTypeMapping) null));
	}
	
	@Test
	void registerWithNullBinder() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(NullPointerException.class, () -> builder.register(STRING_TYPE, "VARCHAR", null));
	}
	
	@Test
	void registerWithBinderAndReaderNullBinder() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(NullPointerException.class, () -> builder.register(STRING_TYPE, "VARCHAR", null, READER));
	}
	
	@Test
	void registerWithNullReader() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(NullPointerException.class, () -> builder.register(STRING_TYPE, "VARCHAR", BINDER, null));
	}
	
	@Test
	void registerNameWithBlankNameThrows() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertThrows(IllegalArgumentException.class, () -> builder.register(STRING_TYPE, ""));
	}
	
	@Test
	void registerWithNameReturnsThis() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertSame(builder, builder.register(STRING_TYPE, "VARCHAR"));
	}
	
	@Test
	void registerWithBinderReturnsThis() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertSame(builder, builder.register(STRING_TYPE, "VARCHAR", BINDER));
		SqlTypeMapping mapping = builder.build().resolve(STRING_TYPE).orElseThrow();
		assertSame(BINDER, mapping.binder());
	}
	
	@Test
	void registerWithBinderAndReaderReturnsThis() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertSame(builder, builder.register(STRING_TYPE, "VARCHAR", BINDER, READER));
		SqlTypeMapping mapping = builder.build().resolve(STRING_TYPE).orElseThrow();
		assertSame(BINDER, mapping.binder());
		assertSame(READER, mapping.reader());
	}
	
	@Test
	void registerWithMappingReturnsThis() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder();
		assertSame(builder, builder.register(STRING_TYPE, new SqlTypeMapping("VARCHAR")));
	}
	
	@Test
	void buildProducesRegistryWithMapping() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(STRING_TYPE, "VARCHAR").build();
		SqlTypeMapping mapping = registry.resolve(STRING_TYPE).orElseThrow();
		assertEquals("VARCHAR", mapping.nativeTypeName());
	}
	
	@Test
	void buildEmptyProducesEmptyResolution() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().build();
		assertTrue(registry.resolve(STRING_TYPE).isEmpty());
	}
	
	@Test
	void registerOverwritesExistingTypeMapping() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(STRING_TYPE, "VARCHAR").register(STRING_TYPE, "TEXT").build();
		assertEquals("TEXT", registry.resolve(STRING_TYPE).orElseThrow().nativeTypeName());
	}
	
	@Test
	void methodChainingBuildsMultipleMappings() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(STRING_TYPE, "VARCHAR").register(INTEGER_TYPE, "INT").build();
		assertEquals("VARCHAR", registry.resolve(STRING_TYPE).orElseThrow().nativeTypeName());
		assertEquals("INT", registry.resolve(INTEGER_TYPE).orElseThrow().nativeTypeName());
	}
	
	@Test
	void builderReuseAfterBuild() {
		SqlTypeRegistryBuilder builder = SqlTypeRegistry.builder().register(STRING_TYPE, "VARCHAR");
		SqlTypeRegistry first = builder.build();
		
		builder.register(INTEGER_TYPE, "INT");
		SqlTypeRegistry second = builder.build();
		
		assertTrue(second.resolve(STRING_TYPE).isPresent());
		assertTrue(second.resolve(INTEGER_TYPE).isPresent());
		assertTrue(first.resolve(INTEGER_TYPE).isEmpty());
	}
}
