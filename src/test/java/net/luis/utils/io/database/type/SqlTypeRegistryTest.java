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

import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;
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
	
	@Test
	void constructBuildsReverseIndexFromMappings() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder()
			.register(SqlTypes.UUID, "UUID")
			.register(SqlTypes.JSON, "JSONB")
			.build();
		assertEquals(Optional.of(SqlTypes.UUID), registry.resolveNative("uuid"));
		assertEquals(Optional.of(SqlTypes.JSON), registry.resolveNative("jsonb"));
	}
	
	@Test
	void resolveNativeWithNullTypeName() {
		SqlTypeRegistry registry = SqlTypeRegistry.empty();
		assertThrows(NullPointerException.class, () -> registry.resolveNative(null));
	}
	
	@Test
	void resolveNativeReturnsTypeWhenPresent() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.UUID, "UUID").build();
		Optional<SqlType<?>> resolved = registry.resolveNative("uuid");
		assertTrue(resolved.isPresent());
		assertSame(SqlTypes.UUID, resolved.get());
	}
	
	@Test
	void resolveNativeReturnsEmptyWhenAbsent() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.UUID, "UUID").build();
		assertEquals(Optional.empty(), registry.resolveNative("inet"));
	}
	
	@Test
	void resolveNativeOnEmptyRegistry() {
		SqlTypeRegistry registry = SqlTypeRegistry.empty();
		assertEquals(Optional.empty(), registry.resolveNative("uuid"));
		assertEquals(Optional.empty(), registry.resolveNative("varchar"));
	}
	
	@Test
	void constructWithDuplicateNativeTypeNameKeepsFirst() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder()
			.register(SqlTypes.JSON, "JSON")
			.register(SqlTypes.XML, "json")
			.build();
		assertEquals(Optional.of(SqlTypes.JSON), registry.resolveNative("JSON"));
		assertTrue(registry.resolve(SqlTypes.XML).isPresent());
	}
	
	@Test
	void resolveNativeIgnoresCase() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.UUID, "UUID").build();
		assertEquals(Optional.of(SqlTypes.UUID), registry.resolveNative("UUID"));
		assertEquals(Optional.of(SqlTypes.UUID), registry.resolveNative("uuid"));
		assertEquals(Optional.of(SqlTypes.UUID), registry.resolveNative("UuId"));
	}
	
	@Test
	void resolveNativeIgnoresTypeArguments() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.BYTES.configure(SqlParameter.length(64)), "VARBINARY(64)").build();
		assertTrue(registry.resolveNative("varbinary").isPresent());
		assertTrue(registry.resolveNative("VARBINARY(64)").isPresent());
	}
	
	@Test
	void resolveNativeWithBlankTypeName() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.UUID, "UUID").build();
		assertEquals(Optional.empty(), assertDoesNotThrow(() -> registry.resolveNative("")));
		assertEquals(Optional.empty(), assertDoesNotThrow(() -> registry.resolveNative("   ")));
	}
	
	@Test
	void resolveNativeWithNameNormalizingToBlank() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.TEXT, "(64)").build();
		assertEquals(Optional.of(SqlTypes.TEXT), registry.resolveNative(""));
		assertEquals(Optional.of(SqlTypes.TEXT), registry.resolveNative("(64)"));
	}
	
	@Test
	void resolveNativeMirrorsResolveForEveryMapping() {
		List<SqlType<?>> types = List.of(SqlTypes.UUID, SqlTypes.JSON, SqlTypes.XML, SqlTypes.IP_ADDRESS, SqlTypes.IP_NETWORK);
		SqlTypeRegistry registry = SqlTypeRegistry.builder()
			.register(SqlTypes.UUID, "UUID")
			.register(SqlTypes.JSON, "JSONB")
			.register(SqlTypes.XML, "XML")
			.register(SqlTypes.IP_ADDRESS, "INET")
			.register(SqlTypes.IP_NETWORK, "CIDR")
			.build();
		
		for (SqlType<?> type : types) {
			String nativeName = registry.resolve(type).orElseThrow().nativeTypeName();
			assertSame(type, registry.resolveNative(nativeName).orElseThrow(), "No reverse mapping for " + nativeName);
		}
	}
	
	@Test
	void resolveNativeForDialectRegistries() {
		assertEquals(Optional.of(SqlTypes.UUID), SqlDialects.POSTGRESQL.resolveType(new SqlNativeType(Types.OTHER, "uuid", 0, 0)));
		assertEquals(Optional.of(SqlTypes.JSON), SqlDialects.POSTGRESQL.resolveType(new SqlNativeType(Types.OTHER, "jsonb", 0, 0)));
		assertEquals(Optional.of(SqlTypes.IP_ADDRESS), SqlDialects.POSTGRESQL.resolveType(new SqlNativeType(Types.OTHER, "inet", 0, 0)));
		assertEquals(Optional.of(SqlTypes.IP_NETWORK), SqlDialects.POSTGRESQL.resolveType(new SqlNativeType(Types.OTHER, "cidr", 0, 0)));
		assertEquals(Optional.of(SqlTypes.XML), SqlDialects.POSTGRESQL.resolveType(new SqlNativeType(Types.SQLXML, "xml", 0, 0)));
		assertEquals(Optional.of(SqlTypes.UUID), SqlDialects.MARIA_DB.resolveType(new SqlNativeType(Types.OTHER, "UUID", 0, 0)));
		assertEquals(Optional.of(SqlTypes.UUID), SqlDialects.H2.resolveType(new SqlNativeType(Types.BINARY, "UUID", 16, 0)));
		assertEquals(Optional.of(SqlTypes.UUID), SqlDialects.SQL_SERVER.resolveType(new SqlNativeType(Types.CHAR, "uniqueidentifier", 36, 0)));
	}
	
	@Test
	void equalsIgnoresDerivedReverseIndex() {
		Map<SqlType<?>, SqlTypeMapping> mappings = Map.of(SqlTypes.UUID, new SqlTypeMapping("UUID"));
		SqlTypeRegistry first = new SqlTypeRegistry(mappings);
		SqlTypeRegistry second = new SqlTypeRegistry(Map.copyOf(mappings));
		assertEquals(first, second);
		assertEquals(second, first);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
