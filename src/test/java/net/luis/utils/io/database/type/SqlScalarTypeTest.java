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
 * Test class for {@link SqlScalarType}.<br>
 *
 * @author Luis-St
 */
class SqlScalarTypeTest {
	
	@Test
	void constructWithJdbcTypeAndJavaType() {
		SqlScalarType<Integer> type = new SqlScalarType<>(Types.INTEGER, Integer.class);
		assertEquals(Types.INTEGER, type.jdbcType());
		assertEquals(Integer.class, type.javaType());
	}
	
	@Test
	void constructWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new SqlScalarType<>(Types.INTEGER, null));
	}
	
	@Test
	void arrayWrapsScalarType() {
		SqlScalarType<Integer> type = new SqlScalarType<>(Types.INTEGER, Integer.class);
		SqlArrayType<Integer> array = type.array();
		assertNotNull(array);
		assertEquals(type, array.elementType());
	}
	
	@Test
	void equalsSameJdbcAndJavaType() {
		assertEquals(new SqlScalarType<>(Types.INTEGER, Integer.class), new SqlScalarType<>(Types.INTEGER, Integer.class));
	}
	
	@Test
	void equalsDifferentJdbcType() {
		assertNotEquals(new SqlScalarType<>(Types.INTEGER, Integer.class), new SqlScalarType<>(Types.BIGINT, Integer.class));
	}
	
	@Test
	void equalsDifferentJavaType() {
		assertNotEquals(new SqlScalarType<>(Types.INTEGER, Integer.class), new SqlScalarType<>(Types.INTEGER, Long.class));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new SqlScalarType<>(Types.INTEGER, Integer.class));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new SqlScalarType<>(Types.INTEGER, Integer.class));
	}
	
	@Test
	void jdbcTypeReturnsConfiguredValue() {
		assertEquals(Types.VARCHAR, new SqlScalarType<>(Types.VARCHAR, String.class).jdbcType());
	}
	
	@Test
	void javaTypeReturnsConfiguredValue() {
		assertEquals(String.class, new SqlScalarType<>(Types.VARCHAR, String.class).javaType());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(new SqlScalarType<>(Types.INTEGER, Integer.class).hashCode(), new SqlScalarType<>(Types.INTEGER, Integer.class).hashCode());
	}
	
	@Test
	void toStringContainsJdbcAndJavaType() {
		String string = new SqlScalarType<>(Types.INTEGER, Integer.class).toString();
		assertTrue(string.contains("jdbcType="));
		assertTrue(string.contains("javaType="));
	}
	
	@Test
	void arrayTypeRoundTripsElementType() {
		SqlScalarType<Integer> type = new SqlScalarType<>(Types.INTEGER, Integer.class);
		SqlArrayType<Integer> array = type.array();
		assertEquals(type, array.elementType());
		assertEquals(Integer[].class, array.javaType());
	}
}
