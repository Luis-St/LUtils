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

import net.luis.utils.io.database.dialect.SqlDefaultDialect;
import net.luis.utils.io.database.dialect.SqlDialect;
import net.luis.utils.io.database.exception.database.SqlResultMappingException;
import net.luis.utils.io.database.exception.database.statement.SqlStatementBindException;
import net.luis.utils.io.database.type.infer.SqlTypeInferrer;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlType}.<br>
 *
 * @author Luis-St
 */
class SqlTypeTest {
	
	@Test
	void inferTypeWithNullValue() {
		assertThrows(NullPointerException.class, () -> SqlType.inferType(null));
	}
	
	@Test
	void inferTypeWithInferrerNullValue() {
		assertThrows(NullPointerException.class, () -> SqlType.inferType(null, SqlTypeInferrer.standard()));
	}
	
	@Test
	void inferTypeWithNullInferrer() {
		assertThrows(NullPointerException.class, () -> SqlType.inferType(5, null));
	}
	
	@Test
	void getValueWithNullType() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertThrows(NullPointerException.class, () -> SqlType.getValue(null, DIALECT, rowSet, 1));
	}
	
	@Test
	void getValueWithNullDialect() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertThrows(NullPointerException.class, () -> SqlType.getValue(STRING_TYPE, null, rowSet, 1));
	}
	
	@Test
	void getValueWithNullResultSet() {
		assertThrows(NullPointerException.class, () -> SqlType.getValue(STRING_TYPE, DIALECT, null, 1));
	}
	
	@Test
	void setValueWithNullType() {
		assertThrows(NullPointerException.class, () -> SqlType.setValue(null, DIALECT, null, 1, "x"));
	}
	
	@Test
	void setValueWithNullDialect() {
		assertThrows(NullPointerException.class, () -> SqlType.setValue(STRING_TYPE, null, null, 1, "x"));
	}
	
	@Test
	void inferTypeReturnsInferredType() throws Exception {
		assertEquals(SqlTypes.INTEGER, SqlType.inferType(5));
	}
	
	@Test
	void inferTypeWithInferrerDelegates() throws Exception {
		Map<Class<?>, SqlType<?>> lookup = Map.of(String.class, STRING_TYPE);
		assertSame(STRING_TYPE, SqlType.inferType("x", SqlTypeInferrer.of(lookup)));
	}
	
	@Test
	void getValueUsesReadingOverrideWhenPresent() throws Exception {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
				return Optional.of((resultSet, index) -> "override");
			}
		};
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertEquals("override", SqlType.getValue(STRING_TYPE, dialect, rowSet, 1));
	}
	
	@Test
	void getValueReadingOverrideReturnsNull() throws Exception {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
				return Optional.of((resultSet, index) -> null);
			}
		};
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertNull(SqlType.getValue(STRING_TYPE, dialect, rowSet, 1));
	}
	
	@Test
	void getValueReadingOverrideThrowsSqlException() {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
				return Optional.of((resultSet, index) -> {
					throw new SQLException("fail");
				});
			}
		};
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertThrows(SqlResultMappingException.class, () -> SqlType.getValue(STRING_TYPE, dialect, rowSet, 1));
	}
	
	@Test
	void getValueReadingOverrideIncompatibleType() {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
				return Optional.of((resultSet, index) -> 42);
			}
		};
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertThrows(SqlResultMappingException.class, () -> SqlType.getValue(STRING_TYPE, dialect, rowSet, 1));
	}
	
	@Test
	void getValueWithoutOverrideDelegatesToType() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		assertEquals(LocalDate.of(2020, 1, 15), SqlType.getValue(SqlTypes.LOCAL_DATE, DIALECT, rowSet, 1));
	}
	
	@Test
	void setValueUsesBindingOverrideWhenPresent() throws Exception {
		AtomicReference<Object> captured = new AtomicReference<>();
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueBinder> bindingOverride(@NonNull SqlType<?> type) {
				return Optional.of((statement, index, value) -> captured.set(value));
			}
		};
		SqlType.setValue(STRING_TYPE, dialect, null, 1, "bound");
		assertEquals("bound", captured.get());
	}
	
	@Test
	void setValueBindingOverrideThrowsSqlException() {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueBinder> bindingOverride(@NonNull SqlType<?> type) {
				return Optional.of((statement, index, value) -> {
					throw new SQLException("fail");
				});
			}
		};
		assertThrows(SqlStatementBindException.class, () -> SqlType.setValue(STRING_TYPE, dialect, null, 1, "x"));
	}
	
	@Test
	void baseTypeOfScalarReturnsSelf() {
		assertSame(SqlTypes.INTEGER, SqlTypes.INTEGER.baseType());
	}
	
	@Test
	void baseTypeOfMappedReturnsUnderlyingType() {
		SqlType<String> mapped = SqlTypes.LOCAL_DATE.map(String.class, LocalDate::parse, LocalDate::toString);
		assertEquals(SqlTypes.LOCAL_DATE, mapped.baseType());
	}
	
	@Test
	void baseTypeOfArrayReturnsSelf() {
		SqlType<Integer[]> array = SqlTypes.INTEGER.array();
		assertSame(array, array.baseType());
	}
	
	@Test
	void arrayDefaultThrowsForArrayType() {
		assertThrows(UnsupportedOperationException.class, () -> SqlTypes.INTEGER.array().array());
	}
	
	@Test
	void getWithNullAccess() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.INTEGER, 42);
		assertThrows(IllegalCallerException.class, () -> SqlTypes.INTEGER.get(null, rowSet, 1));
	}
	
	@Test
	void getWithNullResultSet() {
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.get(SqlTypeInternalAccess.INSTANCE, null, 1));
	}
	
	@Test
	void getWithColumnIndexBelowOne() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.INTEGER, 42);
		assertThrows(IllegalArgumentException.class, () -> SqlTypes.INTEGER.get(SqlTypeInternalAccess.INSTANCE, rowSet, 0));
	}
	
	@Test
	void getTemporalTypeViaFallbackReturnsValue() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, Date.valueOf("2020-01-15"));
		assertEquals(LocalDate.of(2020, 1, 15), SqlTypes.LOCAL_DATE.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getTemporalTypeReturnsNullForNullColumn() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DATE, null);
		assertNull(SqlTypes.LOCAL_DATE.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getNumericFallbackCoercesInteger() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.INTEGER, 42);
		Object value = SqlTypes.INTEGER.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals(42, value);
		assertInstanceOf(Integer.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesLong() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.BIGINT, 42L);
		Object value = SqlTypes.LONG.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals(42L, value);
		assertInstanceOf(Long.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesDouble() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.DOUBLE, 1.5d);
		Object value = SqlTypes.DOUBLE.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals(1.5d, value);
		assertInstanceOf(Double.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesFloat() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.FLOAT, 1.5d);
		Object value = SqlTypes.FLOAT.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals(1.5d, value);
		assertInstanceOf(Double.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesFloatViaReal() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.REAL, 1.5f);
		Object value = SqlTypes.REAL.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals(1.5f, value);
		assertInstanceOf(Float.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesShort() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.SMALLINT, (short) 7);
		Object value = SqlTypes.SHORT.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals((short) 7, value);
		assertInstanceOf(Short.class, value);
	}
	
	@Test
	void getNumericFallbackCoercesByte() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TINYINT, (byte) 3);
		Object value = SqlTypes.BYTE.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertEquals((byte) 3, value);
		assertInstanceOf(Byte.class, value);
	}
	
	@Test
	void getNumericFallbackReturnsNullForNullColumn() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.INTEGER, null);
		assertNull(SqlTypes.INTEGER.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getNumericFallbackNonNumericValueThrowsMappingException() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.INTEGER, "not-a-number");
		assertThrows(SqlResultMappingException.class, () -> SqlTypes.INTEGER.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getNonNumericNonTemporalTypeThrowsMappingException() {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.BOOLEAN, true);
		assertThrows(SqlResultMappingException.class, () -> SqlTypes.BOOLEAN.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void setWithNullAccess() {
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(IllegalCallerException.class, () -> SqlTypes.INTEGER.set(null, DIALECT, statement, 1, 5));
	}
	
	@Test
	void setWithNullDialect() {
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.set(SqlTypeInternalAccess.INSTANCE, null, statement, 1, 5));
	}
	
	@Test
	void setWithNullPreparedStatement() {
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.set(SqlTypeInternalAccess.INSTANCE, DIALECT, null, 1, 5));
	}
	
	@Test
	void setWithColumnIndexBelowOne() {
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(IllegalArgumentException.class, () -> SqlTypes.INTEGER.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 0, 5));
	}
	
	@Test
	void mapWithNullTargetType() {
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.map(null, Integer::parseInt, value -> Integer.toString(value)));
	}
	
	@Test
	void mapWithNullFromTargetToSource() {
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.map(String.class, null, value -> Integer.toString(value)));
	}
	
	@Test
	void mapWithNullFromSourceToTarget() {
		assertThrows(NullPointerException.class, () -> SqlTypes.INTEGER.map(String.class, Integer::parseInt, null));
	}
	
	@Test
	void mapProducesMappedSqlType() {
		SqlType<String> mapped = SqlTypes.INTEGER.map(String.class, Integer::parseInt, value -> Integer.toString(value));
		assertInstanceOf(MappedSqlType.class, mapped);
		assertEquals(String.class, mapped.javaType());
		assertEquals(SqlTypes.INTEGER.jdbcType(), mapped.jdbcType());
	}
	
	@Test
	void getTemporalFallbackCoversTimestampType() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIMESTAMP, Timestamp.valueOf("2020-01-15 10:15:30"));
		assertEquals(LocalDateTime.of(2020, 1, 15, 10, 15, 30), SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getTemporalFallbackCoversLocalTime() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIME, Time.valueOf("10:15:30"));
		assertEquals(LocalTime.of(10, 15, 30), SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getOffsetDateTimeUsesTimestampWithTimezoneBranch() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIMESTAMP, Timestamp.valueOf("2020-01-15 10:15:30"));
		CachedRowSet expectedSource = SqlRowSets.singleColumn(Types.TIMESTAMP, Timestamp.valueOf("2020-01-15 10:15:30"));
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		OffsetDateTime expected = OffsetDateTime.ofInstant(expectedSource.getTimestamp(1, utc).toInstant(), ZoneOffset.UTC);
		OffsetDateTime actual = SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertNotNull(actual);
		assertEquals(ZoneOffset.UTC, actual.getOffset());
		assertEquals(expected.truncatedTo(ChronoUnit.SECONDS), actual.truncatedTo(ChronoUnit.SECONDS));
	}
	
	@Test
	void getOffsetDateTimeWithTimezoneBranchReturnsNullForNullColumn() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIMESTAMP, null);
		assertNull(SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getOffsetTimeUsesTimeWithTimezoneBranch() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIME, Time.valueOf("10:15:30"));
		CachedRowSet expectedSource = SqlRowSets.singleColumn(Types.TIME, Time.valueOf("10:15:30"));
		Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		OffsetTime expected = expectedSource.getTime(1, utc).toLocalTime().atOffset(ZoneOffset.UTC);
		OffsetTime actual = SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1);
		assertNotNull(actual);
		assertEquals(ZoneOffset.UTC, actual.getOffset());
		assertEquals(expected.truncatedTo(ChronoUnit.SECONDS), actual.truncatedTo(ChronoUnit.SECONDS));
	}
	
	@Test
	void getOffsetTimeWithTimezoneBranchReturnsNullForNullColumn() throws Exception {
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.TIME, null);
		assertNull(SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(6)).get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void getValueOverridePrecedesTypeGet() throws Exception {
		SqlDialect dialect = new SqlDefaultDialect() {
			@Override
			public @NonNull Optional<net.luis.utils.io.database.type.SqlValueReader> readingOverride(@NonNull SqlType<?> type) {
				return Optional.of((resultSet, index) -> "fixed");
			}
		};
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.VARCHAR, null);
		assertEquals("fixed", SqlType.getValue(STRING_TYPE, dialect, rowSet, 1));
	}
}
