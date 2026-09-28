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

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNativeType}.<br>
 *
 * @author Luis-St
 */
class SqlNativeTypeTest {
	
	@Test
	void constructWithAllComponents() {
		SqlNativeType nativeType = new SqlNativeType(Types.VARBINARY, "varbinary", 64, 0);
		assertEquals(Types.VARBINARY, nativeType.jdbcType());
		assertEquals("varbinary", nativeType.typeName());
		assertEquals(64, nativeType.columnSize());
		assertEquals(0, nativeType.decimalDigits());
	}
	
	@Test
	void constructWithJdbcTypeAndTypeName() {
		SqlNativeType nativeType = new SqlNativeType(Types.OTHER, "uuid");
		assertEquals(Types.OTHER, nativeType.jdbcType());
		assertEquals("uuid", nativeType.typeName());
		assertEquals(0, nativeType.columnSize());
		assertEquals(0, nativeType.decimalDigits());
	}
	
	@Test
	void constructWithNullTypeName() {
		assertThrows(NullPointerException.class, () -> new SqlNativeType(Types.VARCHAR, null, 0, 0));
	}
	
	@Test
	void constructWithNullTypeNameInShortConstructor() {
		assertThrows(NullPointerException.class, () -> new SqlNativeType(Types.VARCHAR, null));
	}
	
	@Test
	void constructWithVendorSpecificJdbcType() {
		SqlNativeType nativeType = assertDoesNotThrow(() -> new SqlNativeType(-155, "datetimeoffset", 33, 6));
		assertEquals(-155, nativeType.jdbcType());
		assertEquals("datetimeoffset", nativeType.typeName());
		assertEquals(6, nativeType.decimalDigits());
	}
	
	@Test
	void normalizeWithNullTypeName() {
		assertThrows(NullPointerException.class, () -> SqlNativeType.normalize(null));
	}
	
	@Test
	void normalizeStripsTypeArguments() {
		assertEquals("varbinary", SqlNativeType.normalize("VARBINARY(64)"));
		assertEquals("numeric", SqlNativeType.normalize("NUMERIC(20, 0)"));
		assertEquals("char", SqlNativeType.normalize("CHAR(1)"));
	}
	
	@Test
	void normalizeWithoutTypeArguments() {
		assertEquals("bytea", SqlNativeType.normalize("BYTEA"));
		assertEquals("uuid", SqlNativeType.normalize("uuid"));
	}
	
	@Test
	void normalizeWithWhitespaceBeforeArguments() {
		assertEquals("varbinary", SqlNativeType.normalize("varbinary (64)"));
		assertEquals("timestamp", SqlNativeType.normalize("  TIMESTAMP (6)  "));
	}
	
	@Test
	void normalizedTypeNameDelegatesToNormalize() {
		SqlNativeType nativeType = new SqlNativeType(Types.VARBINARY, "VARBINARY(64)", 64, 0);
		assertEquals("varbinary", nativeType.normalizedTypeName());
		assertEquals("VARBINARY(64)", nativeType.typeName());
	}
	
	@Test
	void normalizeLowercasesMixedCaseName() {
		assertEquals("uniqueidentifier", SqlNativeType.normalize("UnIqUeIdEnTiFiEr"));
	}
	
	@Test
	void normalizeAlreadyNormalizedName() {
		assertEquals("jsonb", SqlNativeType.normalize("JSONB"));
		assertEquals("jsonb", SqlNativeType.normalize(SqlNativeType.normalize("JSONB")));
	}
	
	@Test
	void normalizeEmptyTypeName() {
		assertEquals("", SqlNativeType.normalize(""));
	}
	
	@Test
	void normalizeWithLeadingTypeArgument() {
		assertEquals("", SqlNativeType.normalize("(64)"));
		assertEquals("", SqlNativeType.normalize("("));
	}
	
	@Test
	void normalizeMultiWordTypeName() {
		assertEquals("binary large object", SqlNativeType.normalize("BINARY LARGE OBJECT"));
		assertEquals("timestamp with time zone", SqlNativeType.normalize("TIMESTAMP WITH TIME ZONE"));
		assertEquals("double precision", SqlNativeType.normalize("double precision"));
	}
	
	@Test
	void normalizeNameWithArgumentsAndModifier() {
		assertEquals("varbinary", SqlNativeType.normalize("VARBINARY(MAX)"));
		assertEquals("decimal", SqlNativeType.normalize("decimal(10,2) unsigned"));
	}
	
	@Test
	void equalsAndHashCodeAcrossComponents() {
		SqlNativeType first = new SqlNativeType(Types.OTHER, "uuid", 0, 0);
		SqlNativeType second = new SqlNativeType(Types.OTHER, "uuid", 0, 0);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SqlNativeType(Types.OTHER, "UUID", 0, 0));
	}
	
	@Test
	void equalsDistinguishesNumericComponents() {
		SqlNativeType reference = new SqlNativeType(Types.NUMERIC, "numeric", 20, 4);
		assertNotEquals(reference, new SqlNativeType(Types.DECIMAL, "numeric", 20, 4));
		assertNotEquals(reference, new SqlNativeType(Types.NUMERIC, "numeric", 10, 4));
		assertNotEquals(reference, new SqlNativeType(Types.NUMERIC, "numeric", 20, 0));
	}
	
	@Test
	void toStringContainsAllComponents() {
		String string = new SqlNativeType(Types.VARBINARY, "varbinary", 64, 2).toString();
		assertTrue(string.contains(String.valueOf(Types.VARBINARY)));
		assertTrue(string.contains("varbinary"));
		assertTrue(string.contains("64"));
		assertTrue(string.contains("2"));
	}
}
