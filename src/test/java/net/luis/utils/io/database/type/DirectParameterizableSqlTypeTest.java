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

import net.luis.utils.io.database.type.parameter.*;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DirectParameterizableSqlType}.<br>
 *
 * @author Luis-St
 */
class DirectParameterizableSqlTypeTest {
	
	@Test
	void constructWithJdbcJavaAndParameterType() {
		DirectParameterizableSqlType<String, SqlLengthParameter> type = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
		assertEquals(Types.VARCHAR, type.jdbcType());
		assertEquals(String.class, type.javaType());
		assertEquals(SqlLengthParameter.class, type.parameterType());
	}
	
	@Test
	void constructWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new DirectParameterizableSqlType<>(Types.VARCHAR, null, SqlLengthParameter.class));
	}
	
	@Test
	void constructWithNullParameterType() {
		assertThrows(NullPointerException.class, () -> new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, null));
	}
	
	@Test
	void configureWithNullParameter() {
		DirectParameterizableSqlType<String, SqlLengthParameter> type = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
		assertThrows(NullPointerException.class, () -> type.configure(null));
	}
	
	@Test
	void configureProducesParameterizedType() {
		DirectParameterizableSqlType<String, SqlLengthParameter> type = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
		ParameterizedSqlType<String, SqlLengthParameter> configured = type.configure(SqlParameter.length(10));
		assertEquals(Types.VARCHAR, configured.jdbcType());
		assertEquals(String.class, configured.javaType());
		assertEquals(SqlParameter.length(10), configured.parameter());
	}
	
	@Test
	void equalsSameFields() {
		assertEquals(new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class), new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class));
	}
	
	@Test
	void equalsDifferentJdbcType() {
		assertNotEquals(new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class), new DirectParameterizableSqlType<>(Types.NVARCHAR, String.class, SqlLengthParameter.class));
	}
	
	@Test
	void equalsDifferentJavaType() {
		assertNotEquals(new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class), new DirectParameterizableSqlType<>(Types.VARCHAR, CharSequence.class, SqlLengthParameter.class));
	}
	
	@Test
	void equalsDifferentParameterType() {
		assertNotEquals(new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class), new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlPrecisionParameter.class));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class));
	}
	
	@Test
	void parameterTypeReturnsConfiguredValue() {
		assertEquals(SqlLengthParameter.class, new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class).parameterType());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class).hashCode(), new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class).hashCode());
	}
	
	@Test
	void toStringContainsAllFields() {
		String string = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class).toString();
		assertTrue(string.contains("jdbcType="));
		assertTrue(string.contains("javaType="));
		assertTrue(string.contains("parameterType="));
	}
	
	@Test
	void mapProducesMappedParameterizableType() {
		DirectParameterizableSqlType<String, SqlLengthParameter> type = new DirectParameterizableSqlType<>(Types.VARCHAR, String.class, SqlLengthParameter.class);
		ParameterizableSqlType<Integer, SqlLengthParameter> mapped = type.map(Integer.class, i -> String.valueOf(i), Integer::parseInt);
		assertInstanceOf(MappedParameterizableSqlType.class, mapped);
		assertEquals(SqlLengthParameter.class, mapped.parameterType());
	}
}
