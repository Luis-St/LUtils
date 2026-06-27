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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.time.LocalDate;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MappedSqlType}.<br>
 *
 * @author Luis-St
 */
class MappedSqlTypeTest {
	
	private static final SqlType<String> SOURCE = SqlTypes.TEXT;
	private static final ThrowableFunction<Integer, String, SqlStatementBindException> TO_SOURCE = value -> value == null ? null : String.valueOf(value);
	private static final ThrowableFunction<String, Integer, SqlClientException> TO_TARGET = Integer::parseInt;
	private static final ThrowableFunction<Integer, String, SqlStatementBindException> TO_SOURCE_OTHER = value -> value == null ? null : Integer.toString(value);
	private static final ThrowableFunction<String, Integer, SqlClientException> TO_TARGET_OTHER = value -> Integer.valueOf(value);
	private static final ThrowableFunction<Long, String, SqlStatementBindException> TO_SOURCE_LONG = value -> value == null ? null : String.valueOf(value);
	private static final ThrowableFunction<String, Long, SqlClientException> TO_TARGET_LONG = Long::parseLong;
	private static final SqlType<String> DATE_MAPPED = SqlTypes.LOCAL_DATE.map(String.class, value -> value == null ? null : LocalDate.parse(value), LocalDate::toString);
	
	@Test
	void constructWithAllArguments() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertEquals(SOURCE, mapped.sourceType());
		assertEquals(Integer.class, mapped.javaType());
		assertSame(TO_SOURCE, mapped.fromTargetToSource());
		assertSame(TO_TARGET, mapped.fromSourceToTarget());
	}
	
	@Test
	void constructWithNullSourceType() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>(null, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>(SOURCE, null, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructWithNullFromTargetToSource() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>(SOURCE, Integer.class, null, TO_TARGET));
	}
	
	@Test
	void constructWithNullFromSourceToTarget() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, null));
	}
	
	@Test
	void getWithNullAccessDelegatesToSourceGuard() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertThrows(IllegalCallerException.class, () -> DATE_MAPPED.get(null, rowSet, 1));
	}
	
	@Test
	void getWithColumnIndexBelowOneDelegatesToSourceGuard() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertThrows(IllegalArgumentException.class, () -> DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, rowSet, 0));
	}
	
	@Test
	void getReturnsNullWhenSourceNull() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertNull(DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getAppliesConversionWhenSourceNonNull() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		assertEquals("2020-01-15", DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void jdbcTypeDelegatesToSource() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertEquals(SOURCE.jdbcType(), mapped.jdbcType());
	}
	
	@Test
	void arrayWrapsMappedType() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		SqlArrayType<Integer> array = mapped.array();
		assertNotNull(array);
		assertEquals(mapped, array.elementType());
	}
	
	@Test
	void equalsSameSourceAndJavaType() {
		assertEquals(new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE_OTHER, TO_TARGET_OTHER));
	}
	
	@Test
	void equalsDifferentSourceType() {
		assertNotEquals(new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedSqlType<>(SqlTypes.UNICODE_TEXT, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void equalsDifferentJavaType() {
		assertNotEquals(new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET), new MappedSqlType<>(SOURCE, Long.class, TO_SOURCE_LONG, TO_TARGET_LONG));
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void gettersReturnConfiguredFunctions() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertSame(TO_SOURCE, mapped.fromTargetToSource());
		assertSame(TO_TARGET, mapped.fromSourceToTarget());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).hashCode(), new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE_OTHER, TO_TARGET_OTHER).hashCode());
	}
	
	@Test
	void toStringContainsSourceAndJavaType() {
		String string = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).toString();
		assertTrue(string.contains("sourceType="));
		assertTrue(string.contains("javaType="));
	}
	
	@Test
	void nestedMappedTypeChain() {
		SqlType<Integer> inner = SOURCE.map(Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<String> outer = inner.map(String.class, value -> value == null ? null : Integer.parseInt(value), value -> Integer.toString(value));
		assertEquals(SOURCE, outer.baseType());
		assertEquals(SOURCE.jdbcType(), outer.jdbcType());
	}
	
	@Test
	void setWithNullAccessDelegatesToSourceGuard() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		assertThrows(IllegalCallerException.class, () -> mapped.set(null, DIALECT, statement, 1, 42));
	}
	
	@Test
	void setWithNullDialectDelegatesToSourceGuard() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		assertThrows(NullPointerException.class, () -> mapped.set(SqlTypeInternalAccess.INSTANCE, null, statement, 1, 42));
	}
	
	@Test
	void setWithNullPreparedStatementDelegatesToSourceGuard() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertThrows(NullPointerException.class, () -> mapped.set(SqlTypeInternalAccess.INSTANCE, DIALECT, null, 1, 42));
	}
	
	@Test
	void setWithColumnIndexBelowOneDelegatesToSourceGuard() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		assertThrows(IllegalArgumentException.class, () -> mapped.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 0, 42));
	}
	
	@Test
	void setPropagatesConversionFailure() {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, value -> {
			throw new SqlStatementBindException("boom", new SQLException("boom"), 1);
		}, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		assertThrows(SqlStatementBindException.class, () -> mapped.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 1, 42));
	}
	
	@Test
	void setAppliesConversionAndDelegatesToSource() throws Exception {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		mapped.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 1, 42);
		assertEquals("42", statement.capturedValue);
		assertEquals(1, statement.capturedIndex);
		assertEquals(SOURCE.jdbcType(), statement.capturedJdbcType);
	}
	
	@Test
	void setWithNullValueBindsNull() throws Exception {
		MappedSqlType<String, Integer> mapped = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		RecordingStatement statement = new RecordingStatement();
		mapped.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 1, null);
		assertNull(statement.capturedValue);
		assertEquals(1, statement.calls);
	}
	
	@Test
	void setOnNestedMappedTypeChainsConversions() throws Exception {
		SqlType<Integer> inner = SOURCE.map(Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<String> outer = inner.map(String.class, value -> value == null ? null : Integer.parseInt(value), value -> Integer.toString(value));
		RecordingStatement statement = new RecordingStatement();
		outer.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 1, "42");
		assertEquals("42", statement.capturedValue);
		assertEquals(1, statement.capturedIndex);
	}
	
	private static final class RecordingStatement extends FakePreparedStatement {
		
		private int calls;
		private int capturedIndex;
		private Object capturedValue;
		private int capturedJdbcType;
		
		@Override
		public void setObject(int columnIndex, Object value, int jdbcType) {
			this.calls++;
			this.capturedIndex = columnIndex;
			this.capturedValue = value;
			this.capturedJdbcType = jdbcType;
		}
	}
}
