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

import net.luis.utils.io.database.type.parameter.SqlLengthParameter;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParameterizedSqlType}.<br>
 *
 * @author Luis-St
 */
class ParameterizedSqlTypeTest {
	
	private static final SqlLengthParameter LENGTH = SqlParameter.length(255);
	
	@Test
	void constructWithJdbcJavaAndParameter() {
		ParameterizedSqlType<String, SqlLengthParameter> type = new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH);
		assertEquals(Types.VARCHAR, type.jdbcType());
		assertEquals(String.class, type.javaType());
		assertEquals(LENGTH, type.parameter());
	}
	
	@Test
	void constructWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new ParameterizedSqlType<>(Types.VARCHAR, null, LENGTH));
	}
	
	@Test
	void constructWithNullParameter() {
		assertThrows(NullPointerException.class, () -> new ParameterizedSqlType<>(Types.VARCHAR, String.class, null));
	}
	
	@Test
	void arrayWrapsParameterizedType() {
		ParameterizedSqlType<String, SqlLengthParameter> type = new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH);
		SqlArrayType<String> array = type.array();
		assertNotNull(array);
		assertEquals(type, array.elementType());
	}
	
	@Test
	void equalsSameFields() {
		assertEquals(new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH), new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH));
	}
	
	@Test
	void equalsDifferentJdbcType() {
		assertNotEquals(new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH), new ParameterizedSqlType<>(Types.NVARCHAR, String.class, LENGTH));
	}
	
	@Test
	void equalsDifferentJavaType() {
		assertNotEquals(new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH), new ParameterizedSqlType<>(Types.VARCHAR, CharSequence.class, LENGTH));
	}
	
	@Test
	void equalsDifferentParameter() {
		assertNotEquals(new ParameterizedSqlType<>(Types.VARCHAR, String.class, SqlParameter.length(255)), new ParameterizedSqlType<>(Types.VARCHAR, String.class, SqlParameter.length(10)));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH));
	}
	
	@Test
	void parameterReturnsConfiguredValue() {
		assertEquals(LENGTH, new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH).parameter());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH).hashCode(), new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH).hashCode());
	}
	
	@Test
	void toStringContainsAllFields() {
		String string = new ParameterizedSqlType<>(Types.VARCHAR, String.class, LENGTH).toString();
		assertTrue(string.contains("jdbcType="));
		assertTrue(string.contains("javaType="));
		assertTrue(string.contains("parameter="));
	}
}
