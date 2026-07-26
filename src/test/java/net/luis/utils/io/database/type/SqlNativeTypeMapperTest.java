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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlNativeTypeMapper}.<br>
 *
 * @author Luis-St
 */
class SqlNativeTypeMapperTest {
	
	private static SqlType<?> map(int jdbcType, int columnSize, int decimalDigits) {
		Optional<SqlType<?>> mapped = SqlNativeTypeMapper.mapNativeType(new SqlNativeType(jdbcType, "", columnSize, decimalDigits));
		assertTrue(mapped.isPresent(), "No type mapped for jdbc type code " + jdbcType);
		return mapped.get();
	}
	
	@Test
	void mapNativeTypeWithNullNativeType() {
		assertThrows(NullPointerException.class, () -> SqlNativeTypeMapper.mapNativeType(null));
	}
	
	@Test
	void mapBitAndBooleanToBoolean() {
		assertEquals(SqlTypes.BOOLEAN, map(Types.BIT, 0, 0));
		assertEquals(SqlTypes.BOOLEAN, map(Types.BOOLEAN, 0, 0));
	}
	
	@Test
	void mapTinyIntToByte() {
		assertEquals(SqlTypes.BYTE, map(Types.TINYINT, 0, 0));
	}
	
	@Test
	void mapSmallIntToShort() {
		assertEquals(SqlTypes.SHORT, map(Types.SMALLINT, 0, 0));
	}
	
	@Test
	void mapIntegerToInteger() {
		assertEquals(SqlTypes.INTEGER, map(Types.INTEGER, 10, 0));
	}
	
	@Test
	void mapBigIntToLong() {
		assertEquals(SqlTypes.LONG, map(Types.BIGINT, 19, 0));
	}
	
	@Test
	void mapRealToReal() {
		assertEquals(SqlTypes.REAL, map(Types.REAL, 0, 0));
	}
	
	@Test
	void mapFloatToFloat() {
		assertEquals(SqlTypes.FLOAT, map(Types.FLOAT, 0, 0));
	}
	
	@Test
	void mapDoubleToDouble() {
		assertEquals(SqlTypes.DOUBLE, map(Types.DOUBLE, 0, 0));
	}
	
	@Test
	void mapNumericToNumericWithPrecision() {
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(20, 4)), map(Types.NUMERIC, 20, 4));
	}
	
	@Test
	void mapDecimalToDecimalWithPrecision() {
		assertEquals(SqlTypes.DECIMAL.configure(SqlParameter.precision(10, 2)), map(Types.DECIMAL, 10, 2));
	}
	
	@Test
	void mapCharToFixedString() {
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(36)), map(Types.CHAR, 36, 0));
	}
	
	@Test
	void mapNCharToUnicodeFixedString() {
		assertEquals(SqlTypes.UNICODE_FIXED_STRING.configure(SqlParameter.length(8)), map(Types.NCHAR, 8, 0));
	}
	
	@Test
	void mapVarCharToString() {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(64)), map(Types.VARCHAR, 64, 0));
	}
	
	@Test
	void mapNVarCharToUnicodeString() {
		assertEquals(SqlTypes.UNICODE_STRING.configure(SqlParameter.length(64)), map(Types.NVARCHAR, 64, 0));
	}
	
	@Test
	void mapLongVarCharToText() {
		assertEquals(SqlTypes.TEXT, map(Types.LONGVARCHAR, 0, 0));
	}
	
	@Test
	void mapLongNVarCharToUnicodeText() {
		assertEquals(SqlTypes.UNICODE_TEXT, map(Types.LONGNVARCHAR, 0, 0));
	}
	
	@Test
	void mapBinaryToFixedBytes() {
		assertEquals(SqlTypes.FIXED_BYTES.configure(SqlParameter.length(16)), map(Types.BINARY, 16, 0));
	}
	
	@Test
	void mapVarBinaryToBytes() {
		assertEquals(SqlTypes.BYTES.configure(SqlParameter.length(64)), map(Types.VARBINARY, 64, 0));
	}
	
	@Test
	void mapLongVarBinaryToLargeBytes() {
		assertEquals(SqlTypes.LARGE_BYTES, map(Types.LONGVARBINARY, 0, 0));
	}
	
	@Test
	void mapClobToClob() {
		assertEquals(SqlTypes.CLOB, map(Types.CLOB, 0, 0));
	}
	
	@Test
	void mapNClobToNClob() {
		assertEquals(SqlTypes.NCLOB, map(Types.NCLOB, 0, 0));
	}
	
	@Test
	void mapBlobToBlob() {
		assertEquals(SqlTypes.BLOB, map(Types.BLOB, 0, 0));
	}
	
	@Test
	void mapSqlXmlToXml() {
		assertEquals(SqlTypes.XML, map(Types.SQLXML, 0, 0));
	}
	
	@Test
	void mapDateToLocalDate() {
		assertEquals(SqlTypes.LOCAL_DATE, map(Types.DATE, 0, 0));
	}
	
	@Test
	void mapTimeToLocalTimeWithFractional() {
		assertEquals(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(3)), map(Types.TIME, 0, 3));
	}
	
	@Test
	void mapTimestampToLocalDateTimeWithFractional() {
		assertEquals(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)), map(Types.TIMESTAMP, 0, 6));
	}
	
	@Test
	void mapTimeWithTimezoneToOffsetTime() {
		assertEquals(SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(0)), map(Types.TIME_WITH_TIMEZONE, 0, 0));
	}
	
	@Test
	void mapTimestampWithTimezoneToOffsetDateTime() {
		assertEquals(SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)), map(Types.TIMESTAMP_WITH_TIMEZONE, 0, 6));
	}
	
	@Test
	void mapUnsupportedJdbcTypeReturnsEmpty() {
		assertTrue(assertDoesNotThrow(() -> SqlNativeTypeMapper.mapNativeType(new SqlNativeType(Types.OTHER, "uuid", 0, 0))).isEmpty());
		assertTrue(assertDoesNotThrow(() -> SqlNativeTypeMapper.mapNativeType(new SqlNativeType(-155, "datetimeoffset", 33, 6))).isEmpty());
	}
	
	@Test
	void mapNativeTypeIgnoresTypeName() {
		Optional<SqlType<?>> first = SqlNativeTypeMapper.mapNativeType(new SqlNativeType(Types.INTEGER, "int4", 10, 0));
		Optional<SqlType<?>> second = SqlNativeTypeMapper.mapNativeType(new SqlNativeType(Types.INTEGER, "INT", 10, 0));
		assertEquals(Optional.of(SqlTypes.INTEGER), first);
		assertEquals(first, second);
	}
	
	@Test
	void mapNativeTypeWithZeroColumnSizeClampsLength() {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(1)), map(Types.VARCHAR, 0, 0));
	}
	
	@Test
	void mapNativeTypeWithNegativeDecimalDigitsClampsScale() {
		assertEquals(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(0)), map(Types.TIMESTAMP, 0, -1));
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(10, 0)), map(Types.NUMERIC, 10, -5));
	}
	
	@Test
	void mapNativeTypeWithSaturatedColumnSize() {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(Integer.MAX_VALUE)), map(Types.VARCHAR, Integer.MAX_VALUE, 0));
		assertEquals(SqlTypes.FIXED_BYTES.configure(SqlParameter.length(Integer.MAX_VALUE)), map(Types.BINARY, Integer.MAX_VALUE, 0));
	}
	
	@Test
	void mapNativeTypeRoundTripsThroughDialectTypeName() {
		List<SqlType<?>> types = List.of(
			map(Types.BOOLEAN, 0, 0), map(Types.INTEGER, 10, 0), map(Types.BIGINT, 19, 0),
			map(Types.VARCHAR, 64, 0), map(Types.LONGVARCHAR, 0, 0), map(Types.BINARY, 16, 0),
			map(Types.VARBINARY, 64, 0), map(Types.LONGVARBINARY, 0, 0), map(Types.NUMERIC, 10, 2),
			map(Types.DATE, 0, 0), map(Types.TIMESTAMP, 0, 6)
		);
		for (SqlType<?> type : types) {
			assertDoesNotThrow(() -> SqlDialects.DEFAULT.getTypeName(type), "No type name for " + type);
		}
	}
}
