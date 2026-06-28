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

import java.util.*;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTypeRegistry}.<br>
 *
 * @author Luis-St
 */
class SqlTypeRegistryTest {
	
	@Test
	void constructWithMappings() {
		SqlTypeMapping mapping = new SqlTypeMapping("VARCHAR");
		Map<SqlType<?>, SqlTypeMapping> mappings = Map.of(STRING_TYPE, mapping);
		SqlTypeRegistry registry = new SqlTypeRegistry(mappings);
		assertEquals(Optional.of(mapping), registry.resolve(STRING_TYPE));
	}
	
	@Test
	void constructWithNullMappings() {
		assertThrows(NullPointerException.class, () -> new SqlTypeRegistry(null));
	}
	
	@Test
	void resolveWithNullType() {
		SqlTypeRegistry registry = SqlTypeRegistry.empty();
		assertThrows(NullPointerException.class, () -> registry.resolve(null));
	}
	
	@Test
	void resolveReturnsMappingWhenPresent() {
		SqlTypeMapping mapping = new SqlTypeMapping("VARCHAR");
		SqlTypeRegistry registry = new SqlTypeRegistry(Map.of(STRING_TYPE, mapping));
		Optional<SqlTypeMapping> resolved = registry.resolve(STRING_TYPE);
		assertTrue(resolved.isPresent());
		assertSame(mapping, resolved.get());
	}
	
	@Test
	void resolveReturnsEmptyWhenAbsent() {
		SqlTypeRegistry registry = new SqlTypeRegistry(Map.of(STRING_TYPE, new SqlTypeMapping("VARCHAR")));
		assertEquals(Optional.empty(), registry.resolve(INTEGER_TYPE));
	}
	
	@Test
	void emptyReturnsSingleton() {
		assertSame(SqlTypeRegistry.empty(), SqlTypeRegistry.empty());
		assertTrue(SqlTypeRegistry.empty().resolve(STRING_TYPE).isEmpty());
	}
	
	@Test
	void builderReturnsNewBuilder() {
		assertNotNull(SqlTypeRegistry.builder());
		assertNotSame(SqlTypeRegistry.builder(), SqlTypeRegistry.builder());
	}
	
	@Test
	void equalsSameMappings() {
		Map<SqlType<?>, SqlTypeMapping> mappings = Map.of(STRING_TYPE, new SqlTypeMapping("VARCHAR"));
		assertEquals(new SqlTypeRegistry(mappings), new SqlTypeRegistry(mappings));
	}
	
	@Test
	void equalsDifferentMappings() {
		SqlTypeRegistry first = new SqlTypeRegistry(Map.of(STRING_TYPE, new SqlTypeMapping("VARCHAR")));
		SqlTypeRegistry second = new SqlTypeRegistry(Map.of(STRING_TYPE, new SqlTypeMapping("TEXT")));
		assertNotEquals(first, second);
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, SqlTypeRegistry.empty());
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", SqlTypeRegistry.empty());
	}
	
	@Test
	void hashCodeConsistentForEqualRegistries() {
		Map<SqlType<?>, SqlTypeMapping> mappings = Map.of(STRING_TYPE, new SqlTypeMapping("VARCHAR"));
		assertEquals(new SqlTypeRegistry(mappings).hashCode(), new SqlTypeRegistry(mappings).hashCode());
	}
	
	@Test
	void toStringContainsMappings() {
		assertTrue(SqlTypeRegistry.empty().toString().startsWith("SqlTypeRegistry"));
	}
	
	@Test
	void constructorCopiesMappingsDefensively() {
		SqlTypeMapping mapping = new SqlTypeMapping("VARCHAR");
		Map<SqlType<?>, SqlTypeMapping> source = new HashMap<>();
		source.put(STRING_TYPE, mapping);
		SqlTypeRegistry registry = new SqlTypeRegistry(source);
		
		source.put(INTEGER_TYPE, new SqlTypeMapping("INT"));
		source.clear();
		
		assertEquals(Optional.of(mapping), registry.resolve(STRING_TYPE));
		assertTrue(registry.resolve(INTEGER_TYPE).isEmpty());
	}
}
