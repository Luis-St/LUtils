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
import net.luis.utils.io.database.dialect.SqlDialects;
import net.luis.utils.io.database.exception.SqlClientException;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
	
	@Test
	void constructWithIdentifier() {
		SqlType<Integer> type = SOURCE.map("test_id", Integer.class, TO_SOURCE, TO_TARGET);
		assertInstanceOf(MappedSqlType.class, type);
		assertEquals("test_id", ((MappedSqlType<?, ?>) type).identifier());
		assertEquals(SOURCE.jdbcType(), type.jdbcType());
		assertEquals(Integer.class, type.javaType());
	}
	
	@Test
	void constructWithoutIdentifier() {
		MappedSqlType<String, Integer> type = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		assertNull(type.identifier());
		assertEquals(Integer.class, type.javaType());
	}
	
	@Test
	void constructIdentifiedWithNullSourceType() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>("id", null, Integer.class, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructIdentifiedWithNullJavaType() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>("id", SOURCE, null, TO_SOURCE, TO_TARGET));
	}
	
	@Test
	void constructIdentifiedWithNullFromTargetToSource() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>("id", SOURCE, Integer.class, null, TO_TARGET));
	}
	
	@Test
	void constructIdentifiedWithNullFromSourceToTarget() {
		assertThrows(NullPointerException.class, () -> new MappedSqlType<>("id", SOURCE, Integer.class, TO_SOURCE, null));
	}
	
	@Test
	void equalsWithSameIdentifier() {
		SqlType<Integer> first = SOURCE.map("uuid", Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Integer> second = SOURCE.map("uuid", Integer.class, TO_SOURCE_OTHER, TO_TARGET_OTHER);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsWithDifferentIdentifier() {
		SqlType<Integer> first = SOURCE.map("uuid", Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Integer> second = SOURCE.map("guid", Integer.class, TO_SOURCE, TO_TARGET);
		assertNotEquals(first, second);
	}
	
	@Test
	void equalsIdentifiedAgainstAnonymous() {
		SqlType<java.util.UUID> anonymous = SqlTypes.FIXED_STRING.configure(SqlParameter.length(36))
			.map(java.util.UUID.class, value -> value == null ? null : value.toString(), java.util.UUID::fromString);
		assertNotEquals(SqlTypes.UUID, anonymous);
		assertNotEquals(anonymous, SqlTypes.UUID);
	}
	
	@Test
	void equalsAnonymousWithSameSourceAndJavaType() {
		MappedSqlType<String, Integer> first = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET);
		MappedSqlType<String, Integer> second = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE_OTHER, TO_TARGET_OTHER);
		assertEquals(first, second);
	}
	
	@Test
	void hashCodeDiffersForDifferentIdentifier() {
		SqlType<Integer> first = SOURCE.map("uuid", Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Integer> second = SOURCE.map("guid", Integer.class, TO_SOURCE, TO_TARGET);
		assertNotEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void toStringContainsIdentifier() {
		String string = SOURCE.map("uuid", Integer.class, TO_SOURCE, TO_TARGET).toString();
		assertTrue(string.contains("identifier=uuid"));
		assertTrue(string.contains("java.lang.Integer"));
	}
	
	@Test
	void toStringForAnonymousType() {
		String string = new MappedSqlType<>(SOURCE, Integer.class, TO_SOURCE, TO_TARGET).toString();
		assertTrue(string.contains("identifier=null"));
	}
	
	@Test
	void identifiedTypeResolvesInRegistryWhileAnonymousDoesNot() {
		SqlTypeRegistry registry = SqlTypeRegistry.builder().register(SqlTypes.UUID, "UUID").build();
		SqlType<java.util.UUID> anonymous = SqlTypes.FIXED_STRING.configure(SqlParameter.length(36))
			.map(java.util.UUID.class, value -> value == null ? null : value.toString(), java.util.UUID::fromString);
		assertTrue(registry.resolve(SqlTypes.UUID).isPresent());
		assertTrue(registry.resolve(anonymous).isEmpty());
	}
	
	@Test
	void identifiedTypeRendersDialectNameWhileAnonymousRendersBaseType() throws SqlException {
		SqlType<java.util.UUID> anonymous = SqlTypes.FIXED_STRING.configure(SqlParameter.length(36))
			.map(java.util.UUID.class, value -> value == null ? null : value.toString(), java.util.UUID::fromString);
		assertEquals("UUID", SqlDialects.POSTGRESQL.getTypeName(SqlTypes.UUID));
		assertEquals("CHAR(36)", SqlDialects.POSTGRESQL.getTypeName(anonymous));
	}
	
	@Test
	void identifiedTypeUsedAsMapKey() {
		SqlType<java.util.UUID> anonymous = SqlTypes.FIXED_STRING.configure(SqlParameter.length(36))
			.map(java.util.UUID.class, value -> value == null ? null : value.toString(), java.util.UUID::fromString);
		Map<SqlType<?>, String> map = new HashMap<>();
		map.put(SqlTypes.UUID, "identified");
		map.put(anonymous, "anonymous");
		assertEquals(2, map.size());
		assertEquals("identified", map.get(SqlTypes.UUID));
		assertEquals("anonymous", map.get(anonymous));
	}
	
	@Test
	void getWithDialectAndNullAccessDelegatesToSourceGuard() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertThrows(IllegalCallerException.class, () -> DATE_MAPPED.get(null, DIALECT, rowSet, 1));
	}
	
	@Test
	void getWithDialectAndNullDialectDelegatesToSourceGuard() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertThrows(NullPointerException.class, () -> DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, null, rowSet, 1));
	}
	
	@Test
	void getWithDialectAndNullResultSetDelegatesToSourceGuard() {
		assertThrows(NullPointerException.class, () -> DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, DIALECT, null, 1));
	}
	
	@Test
	void getWithDialectAndFailingConverter() {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, value -> value == null ? null : LocalDate.parse(value), value -> {
			throw new SqlClientException("Conversion failed in tests");
		});
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		assertThrows(SqlClientException.class, () -> mapped.get(SqlTypeInternalAccess.INSTANCE, DIALECT, rowSet, 1));
	}
	
	@Test
	void getWithDialectReturnsNullWhenSourceNull() throws Exception {
		AtomicInteger conversions = new AtomicInteger(0);
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, value -> value == null ? null : LocalDate.parse(value), value -> {
			conversions.incrementAndGet();
			return value.toString();
		});
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertNull(mapped.get(SqlTypeInternalAccess.INSTANCE, DIALECT, rowSet, 1));
		assertEquals(0, conversions.get());
	}
	
	@Test
	void getWithDialectAppliesConversionWhenSourceNonNull() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		assertEquals("2020-01-15", DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, DIALECT, rowSet, 1));
	}
	
	@Test
	void getWithDialectUsesDialectAwareSourceRead() throws Exception {
		AtomicReference<OffsetDateTime> captured = new AtomicReference<>();
		SqlType<String> mapped = SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)).map(String.class, value -> value == null ? null : OffsetDateTime.parse(value), value -> {
			captured.set(value);
			return value.toString();
		});
		String value = mapped.get(SqlTypeInternalAccess.INSTANCE, SqlDialects.SQLITE, resultRow(LocalDateTime.of(2020, 1, 15, 10, 15, 30)), 1);
		assertNotNull(captured.get());
		assertEquals(ZoneOffset.UTC, captured.get().getOffset());
		assertEquals("2020-01-15T10:15:30Z", value);
	}
	
	@Test
	void getWithDialectMatchesPlainGetOnOffsetSupportingDialect() throws Exception {
		CachedRowSet plainSource = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		CachedRowSet dialectSource = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		String expected = DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, plainSource, 1);
		assertEquals(expected, DATE_MAPPED.get(SqlTypeInternalAccess.INSTANCE, DIALECT, dialectSource, 1));
	}
	
	@Test
	void getWithDialectOnIdentifiedMappedType() throws Exception {
		MappedSqlType<Integer, String> mapped = new MappedSqlType<>("counter", SqlTypes.INTEGER, String.class, Integer::parseInt, value -> Integer.toString(value));
		assertEquals("counter", mapped.identifier());
		assertEquals("42", mapped.get(SqlTypeInternalAccess.INSTANCE, DIALECT, resultRow(42), 1));
	}
	
	@Test
	void getWithDialectOnNestedMappedTypes() throws Exception {
		SqlType<Integer> inner = SOURCE.map(Integer.class, TO_SOURCE, TO_TARGET);
		SqlType<Long> outer = inner.map(Long.class, value -> value == null ? null : Math.toIntExact(value), Integer::longValue);
		assertEquals(42L, outer.get(SqlTypeInternalAccess.INSTANCE, SqlDialects.SQLITE, resultRow("42"), 1));
	}
	
	@Test
	void setThenGetWithDialectRoundTripsMappedValue() throws Exception {
		SqlType<String> mapped = SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)).map(String.class, value -> value == null ? null : OffsetDateTime.parse(value), OffsetDateTime::toString);
		RecordingStatement statement = new RecordingStatement();
		mapped.set(SqlTypeInternalAccess.INSTANCE, SqlDialects.SQLITE, statement, 1, "2020-01-15T10:15:30+02:00");
		assertEquals(Types.TIMESTAMP, statement.capturedJdbcType);
		assertEquals(LocalDateTime.of(2020, 1, 15, 8, 15, 30), statement.capturedValue);
		assertEquals("2020-01-15T08:15:30Z", mapped.get(SqlTypeInternalAccess.INSTANCE, SqlDialects.SQLITE, resultRow(statement.capturedValue), 1));
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
