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

import net.luis.utils.io.database.dialect.*;
import net.luis.utils.io.database.exception.client.dialect.SqlDialectUnsupportedRenderingException;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.time.LocalDate;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlArrayType}.<br>
 *
 * @author Luis-St
 */
class SqlArrayTypeTest {
	
	private static final PostgresSqlDialect POSTGRES = new PostgresSqlDialect();
	
	@Test
	void constructWithElementType() {
		SqlArrayType<Integer> type = new SqlArrayType<>(SqlTypes.INTEGER);
		assertEquals(SqlTypes.INTEGER, type.elementType());
		assertEquals(Types.ARRAY, type.jdbcType());
	}
	
	@Test
	void constructWithNullElementType() {
		assertThrows(NullPointerException.class, () -> new SqlArrayType<>(null));
	}
	
	@Test
	void constructWithNestedArrayType() {
		assertThrows(IllegalArgumentException.class, () -> new SqlArrayType<>(SqlTypes.INTEGER.array()));
	}
	
	@Test
	void getWithNullAccess() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertThrows(IllegalCallerException.class, () -> type.get(null, rowSet, 1));
	}
	
	@Test
	void getWithNullResultSet() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.get(SqlTypeInternalAccess.INSTANCE, null, 1));
	}
	
	@Test
	void getWithColumnIndexBelowOne() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertThrows(IllegalArgumentException.class, () -> type.get(SqlTypeInternalAccess.INSTANCE, rowSet, 0));
	}
	
	@Test
	void setWithNullAccess() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(IllegalCallerException.class, () -> type.set(null, DIALECT, new FakePreparedStatement(), 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithNullDialect() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, null, new FakePreparedStatement(), 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithNullPreparedStatement() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, DIALECT, null, 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithColumnIndexBelowOne() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(IllegalArgumentException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 0, new Integer[] { 1 }));
	}
	
	@Test
	void getReturnsNullForNullArrayColumn() throws Exception {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertNull(type.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void jdbcTypeIsArray() {
		assertEquals(Types.ARRAY, SqlTypes.INTEGER.array().jdbcType());
	}
	
	@Test
	void javaTypeIsElementArrayClass() {
		assertEquals(Integer[].class, SqlTypes.INTEGER.array().javaType());
	}
	
	@Test
	void equalsSameElementType() {
		assertEquals(SqlTypes.INTEGER.array(), SqlTypes.INTEGER.array());
	}
	
	@Test
	void equalsDifferentElementType() {
		assertNotEquals(SqlTypes.INTEGER.array(), STRING_TYPE.array());
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, SqlTypes.INTEGER.array());
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", SqlTypes.INTEGER.array());
	}
	
	@Test
	void elementTypeReturnsConfiguredValue() {
		assertEquals(SqlTypes.INTEGER, SqlTypes.INTEGER.array().elementType());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(SqlTypes.INTEGER.array().hashCode(), SqlTypes.INTEGER.array().hashCode());
	}
	
	@Test
	void toStringContainsElementType() {
		assertTrue(SqlTypes.INTEGER.array().toString().contains("elementType="));
	}
	
	@Test
	void arrayOfParameterizedElementType() {
		SqlArrayType<String> type = STRING_TYPE.array();
		assertEquals(STRING_TYPE, type.elementType());
		assertEquals(String[].class, type.javaType());
	}
	
	@Test
	void getWrapsSqlExceptionAsMappingException() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		ResultSet resultSet = throwingArrayResultSet();
		assertThrows(SqlResultMappingException.class, () -> type.get(SqlTypeInternalAccess.INSTANCE, resultSet, 1));
	}
	
	@Test
	void setUnsupportedElementTypeThrows() {
		SqlDialect unsupporting = new SqlDefaultDialect() {
			@Override
			public boolean isTypeSupported(SqlType<?> type) {
				return false;
			}
		};
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(SqlDialectUnsupportedRenderingException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, unsupporting, statement, 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWrapsSqlExceptionAsBindException() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		RecordingStatement statement = new RecordingStatement(true);
		assertThrows(SqlStatementBindException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, new Integer[] { 1 }));
	}
	
	@Test
	void getReadsScalarArrayValues() throws Exception {
		boolean[] freed = { false };
		Array array = fakeArray(new Object[] { 1, 2, 3 }, freed);
		ResultSet resultSet = arrayResultSet(array);
		
		Integer[] result = SqlTypes.INTEGER.array().get(SqlTypeInternalAccess.INSTANCE, resultSet, 1);
		assertArrayEquals(new Integer[] { 1, 2, 3 }, result);
		assertTrue(freed[0]);
	}
	
	@Test
	void getConvertsMappedArrayElements() throws Exception {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, LocalDate::parse, LocalDate::toString);
		boolean[] freed = { false };
		Array array = fakeArray(new Object[] { LocalDate.of(2020, 1, 15) }, freed);
		ResultSet resultSet = arrayResultSet(array);
		
		String[] result = mapped.array().get(SqlTypeInternalAccess.INSTANCE, resultSet, 1);
		assertArrayEquals(new String[] { "2020-01-15" }, result);
		assertTrue(freed[0]);
	}
	
	@Test
	void getConvertsNullMappedArrayElementToNull() throws Exception {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, LocalDate::parse, LocalDate::toString);
		Array array = fakeArray(new Object[] { null }, new boolean[] { false });
		ResultSet resultSet = arrayResultSet(array);
		
		String[] result = mapped.array().get(SqlTypeInternalAccess.INSTANCE, resultSet, 1);
		assertEquals(1, result.length);
		assertNull(result[0]);
	}
	
	@Test
	void setNullValueBindsSqlNull() throws Exception {
		RecordingStatement statement = new RecordingStatement(false);
		SqlTypes.INTEGER.array().set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, null);
		
		assertEquals(1, statement.setNullIndex);
		assertEquals(Types.ARRAY, statement.setNullType);
		assertNull(statement.createArrayTypeName);
		assertNull(statement.setArrayIndex);
	}
	
	@Test
	void setBindsScalarArray() throws Exception {
		RecordingStatement statement = new RecordingStatement(false);
		SqlTypes.INTEGER.array().set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, new Integer[] { 1, 2 });
		
		assertEquals("INTEGER", statement.createArrayTypeName);
		assertArrayEquals(new Object[] { 1, 2 }, statement.createArrayElements);
		assertEquals(1, statement.setArrayIndex);
		assertSame(statement.createdArray, statement.setArrayValue);
	}
	
	@Test
	void setConvertsMappedArrayElements() throws Exception {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, LocalDate::parse, LocalDate::toString);
		RecordingStatement statement = new RecordingStatement(false);
		mapped.array().set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, new String[] { "2020-01-15" });
		
		assertEquals(1, statement.createArrayElements.length);
		assertEquals(LocalDate.of(2020, 1, 15), statement.createArrayElements[0]);
		assertEquals(1, statement.setArrayIndex);
	}
	
	@Test
	void getReturnsEmptyArrayForEmptySqlArray() throws Exception {
		boolean[] freed = { false };
		Array array = fakeArray(new Object[0], freed);
		ResultSet resultSet = arrayResultSet(array);
		
		Integer[] result = SqlTypes.INTEGER.array().get(SqlTypeInternalAccess.INSTANCE, resultSet, 1);
		assertNotNull(result);
		assertEquals(0, result.length);
		assertTrue(freed[0]);
	}
	
	@Test
	void javaTypeOfMappedElementArray() {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, LocalDate::parse, LocalDate::toString);
		assertEquals(String[].class, mapped.array().javaType());
	}
	
	@Test
	void setBindsEmptyArray() throws Exception {
		RecordingStatement statement = new RecordingStatement(false);
		SqlTypes.INTEGER.array().set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, new Integer[0]);
		
		assertEquals("INTEGER", statement.createArrayTypeName);
		assertEquals(0, statement.createArrayElements.length);
		assertEquals(1, statement.setArrayIndex);
		assertSame(statement.createdArray, statement.setArrayValue);
	}
	
	@Test
	void setNullMappedElementToSourceNull() throws Exception {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, value -> value == null ? null : LocalDate.parse(value), LocalDate::toString);
		RecordingStatement statement = new RecordingStatement(false);
		mapped.array().set(SqlTypeInternalAccess.INSTANCE, POSTGRES, statement, 1, new String[] { null });
		
		assertEquals(1, statement.createArrayElements.length);
		assertNull(statement.createArrayElements[0]);
	}
	
	private static @org.jspecify.annotations.NonNull Array fakeArray(Object[] data, boolean[] freed) {
		return (Array) Proxy.newProxyInstance(
			Array.class.getClassLoader(),
			new Class<?>[] { Array.class },
			(proxy, method, args) -> switch (method.getName()) {
				case "getArray" -> data;
				case "free" -> {
					freed[0] = true;
					yield null;
				}
				case "toString" -> "FakeArray";
				default -> throw new UnsupportedOperationException("Array method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	private static @org.jspecify.annotations.NonNull ResultSet arrayResultSet(Array array) {
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				if ("getArray".equals(method.getName())) {
					return array;
				}
				if ("toString".equals(method.getName())) {
					return "ArrayResultSet";
				}
				throw new UnsupportedOperationException("Result set method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	private static @org.jspecify.annotations.NonNull ResultSet throwingArrayResultSet() {
		return (ResultSet) Proxy.newProxyInstance(
			ResultSet.class.getClassLoader(),
			new Class<?>[] { ResultSet.class },
			(proxy, method, args) -> {
				if ("getArray".equals(method.getName())) {
					throw new SQLException("Array retrieval failed in tests");
				}
				if ("toString".equals(method.getName())) {
					return "ThrowingArrayResultSet";
				}
				throw new UnsupportedOperationException("Result set method '" + method.getName() + "' must not be invoked in tests");
			}
		);
	}
	
	private static final class RecordingStatement extends FakePreparedStatement {
		
		private final boolean throwOnCreateArray;
		private final boolean[] freed = { false };
		private String createArrayTypeName;
		private Object[] createArrayElements;
		private Array createdArray;
		private Integer setArrayIndex;
		private Array setArrayValue;
		private Integer setNullIndex;
		private Integer setNullType;
		
		private RecordingStatement(boolean throwOnCreateArray) {
			this.throwOnCreateArray = throwOnCreateArray;
		}
		
		@Override
		public Connection getConnection() {
			return (Connection) Proxy.newProxyInstance(
				Connection.class.getClassLoader(),
				new Class<?>[] { Connection.class },
				(proxy, method, args) -> {
					if ("createArrayOf".equals(method.getName())) {
						if (this.throwOnCreateArray) {
							throw new SQLException("Array creation failed in tests");
						}
						this.createArrayTypeName = (String) args[0];
						this.createArrayElements = (Object[]) args[1];
						this.createdArray = fakeArray(this.createArrayElements, this.freed);
						return this.createdArray;
					}
					if ("toString".equals(method.getName())) {
						return "RecordingConnection";
					}
					throw new UnsupportedOperationException("Connection method '" + method.getName() + "' must not be invoked in tests");
				}
			);
		}
		
		@Override
		public void setArray(int index, Array array) {
			this.setArrayIndex = index;
			this.setArrayValue = array;
		}
		
		@Override
		public void setNull(int index, int sqlType) {
			this.setNullIndex = index;
			this.setNullType = sqlType;
		}
	}
}
