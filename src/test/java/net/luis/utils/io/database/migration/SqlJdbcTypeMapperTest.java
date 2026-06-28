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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.exception.database.SqlSchemaIntrospectionException;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static net.luis.utils.io.database.migration.SqlJdbcTypeMapper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlJdbcTypeMapper}.<br>
 *
 * @author Luis-St
 */
class SqlJdbcTypeMapperTest {
	
	@Test
	void mapUnsupportedJdbcTypeThrows() throws SqlSchemaIntrospectionException {
		assertThrows(SqlSchemaIntrospectionException.class, () -> mapJdbcType(Types.ARRAY, 0, 0));
	}
	
	@Test
	void mapBitAndBooleanToBoolean() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BOOLEAN, mapJdbcType(Types.BIT, 0, 0));
		assertEquals(SqlTypes.BOOLEAN, mapJdbcType(Types.BOOLEAN, 0, 0));
	}
	
	@Test
	void mapTinyIntToByte() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BYTE, mapJdbcType(Types.TINYINT, 0, 0));
	}
	
	@Test
	void mapSmallIntToShort() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.SHORT, mapJdbcType(Types.SMALLINT, 0, 0));
	}
	
	@Test
	void mapIntegerToInteger() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.INTEGER, mapJdbcType(Types.INTEGER, 0, 0));
	}
	
	@Test
	void mapBigIntToLong() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LONG, mapJdbcType(Types.BIGINT, 0, 0));
	}
	
	@Test
	void mapRealToReal() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.REAL, mapJdbcType(Types.REAL, 0, 0));
	}
	
	@Test
	void mapFloatToFloat() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FLOAT, mapJdbcType(Types.FLOAT, 0, 0));
	}
	
	@Test
	void mapDoubleToDouble() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.DOUBLE, mapJdbcType(Types.DOUBLE, 0, 0));
	}
	
	@Test
	void mapLongVarcharToText() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.TEXT, mapJdbcType(Types.LONGVARCHAR, 0, 0));
	}
	
	@Test
	void mapLongNVarcharToUnicodeText() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_TEXT, mapJdbcType(Types.LONGNVARCHAR, 0, 0));
	}
	
	@Test
	void mapLongVarbinaryToLargeBytes() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LARGE_BYTES, mapJdbcType(Types.LONGVARBINARY, 0, 0));
	}
	
	@Test
	void mapClobToClob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.CLOB, mapJdbcType(Types.CLOB, 0, 0));
	}
	
	@Test
	void mapNClobToNClob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NCLOB, mapJdbcType(Types.NCLOB, 0, 0));
	}
	
	@Test
	void mapBlobToBlob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BLOB, mapJdbcType(Types.BLOB, 0, 0));
	}
	
	@Test
	void mapDateToLocalDate() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_DATE, mapJdbcType(Types.DATE, 0, 0));
	}
	
	@Test
	void mapNumericToNumericWithPrecision() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(10, 2)), mapJdbcType(Types.NUMERIC, 10, 2));
	}
	
	@Test
	void mapDecimalToDecimalWithPrecision() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.DECIMAL.configure(SqlParameter.precision(18, 4)), mapJdbcType(Types.DECIMAL, 18, 4));
	}
	
	@Test
	void mapCharToFixedString() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(8)), mapJdbcType(Types.CHAR, 8, 0));
	}
	
	@Test
	void mapNCharToUnicodeFixedString() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_FIXED_STRING.configure(SqlParameter.length(8)), mapJdbcType(Types.NCHAR, 8, 0));
	}
	
	@Test
	void mapVarcharToStringWithLength() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(255)), mapJdbcType(Types.VARCHAR, 255, 0));
	}
	
	@Test
	void mapNVarcharToUnicodeStringWithLength() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_STRING.configure(SqlParameter.length(255)), mapJdbcType(Types.NVARCHAR, 255, 0));
	}
	
	@Test
	void mapBinaryToFixedBytesWithLength() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FIXED_BYTES.configure(SqlParameter.length(16)), mapJdbcType(Types.BINARY, 16, 0));
	}
	
	@Test
	void mapVarbinaryToBytesWithLength() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BYTES.configure(SqlParameter.length(64)), mapJdbcType(Types.VARBINARY, 64, 0));
	}
	
	@Test
	void mapTimeToLocalTimeWithFractional() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(3)), mapJdbcType(Types.TIME, 0, 3));
	}
	
	@Test
	void mapTimestampToLocalDateTimeWithFractional() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)), mapJdbcType(Types.TIMESTAMP, 0, 6));
	}
	
	@Test
	void mapTimeWithTimezoneToOffsetTime() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(3)), mapJdbcType(Types.TIME_WITH_TIMEZONE, 0, 3));
	}
	
	@Test
	void mapTimestampWithTimezoneToOffsetDateTime() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)), mapJdbcType(Types.TIMESTAMP_WITH_TIMEZONE, 0, 6));
	}
	
	@Test
	void mapVarcharClampsNonPositiveLengthToOne() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(1)), mapJdbcType(Types.VARCHAR, 0, 0));
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(1)), mapJdbcType(Types.VARCHAR, -5, 0));
	}
	
	@Test
	void mapNumericClampsNonPositivePrecisionAndScale() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(1, 0)), mapJdbcType(Types.NUMERIC, 0, -1));
	}
	
	@Test
	void mapTimeClampsNegativeFractionalToZero() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(0)), mapJdbcType(Types.TIME, 0, -5));
	}
	
	@Test
	void reconstructScalarUnsupportedTypeThrows() throws SqlSchemaIntrospectionException {
		SqlSchemaIntrospectionException exception = assertThrows(SqlSchemaIntrospectionException.class, () -> reconstructType(Types.VARCHAR, null));
		assertTrue(exception.getMessage().startsWith("Unsupported scalar"));
	}
	
	@Test
	void reconstructParameterizedUnsupportedTypeThrows() throws SqlSchemaIntrospectionException {
		SqlSchemaIntrospectionException exception = assertThrows(SqlSchemaIntrospectionException.class, () -> reconstructType(Types.INTEGER, SqlParameter.length(10)));
		assertTrue(exception.getMessage().startsWith("Unsupported parameterized"));
	}
	
	@Test
	void reconstructScalarTypeWithNullParameter() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.INTEGER, reconstructType(Types.INTEGER, null));
	}
	
	@Test
	void reconstructParameterizedTypeWithParameter() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(50)), reconstructType(Types.VARCHAR, SqlParameter.length(50)));
	}
	
	@Test
	void reconstructScalarBitAndBoolean() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BOOLEAN, reconstructType(Types.BIT, null));
		assertEquals(SqlTypes.BOOLEAN, reconstructType(Types.BOOLEAN, null));
	}
	
	@Test
	void reconstructScalarTinyInt() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BYTE, reconstructType(Types.TINYINT, null));
	}
	
	@Test
	void reconstructScalarSmallInt() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.SHORT, reconstructType(Types.SMALLINT, null));
	}
	
	@Test
	void reconstructScalarBigInt() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LONG, reconstructType(Types.BIGINT, null));
	}
	
	@Test
	void reconstructScalarReal() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.REAL, reconstructType(Types.REAL, null));
	}
	
	@Test
	void reconstructScalarFloat() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FLOAT, reconstructType(Types.FLOAT, null));
	}
	
	@Test
	void reconstructScalarDouble() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.DOUBLE, reconstructType(Types.DOUBLE, null));
	}
	
	@Test
	void reconstructScalarLongVarchar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.TEXT, reconstructType(Types.LONGVARCHAR, null));
	}
	
	@Test
	void reconstructScalarLongNVarchar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_TEXT, reconstructType(Types.LONGNVARCHAR, null));
	}
	
	@Test
	void reconstructScalarLongVarbinary() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LARGE_BYTES, reconstructType(Types.LONGVARBINARY, null));
	}
	
	@Test
	void reconstructScalarClob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.CLOB, reconstructType(Types.CLOB, null));
	}
	
	@Test
	void reconstructScalarNClob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NCLOB, reconstructType(Types.NCLOB, null));
	}
	
	@Test
	void reconstructScalarBlob() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BLOB, reconstructType(Types.BLOB, null));
	}
	
	@Test
	void reconstructScalarDate() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_DATE, reconstructType(Types.DATE, null));
	}
	
	@Test
	void reconstructParameterizedNumeric() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(10, 2)), reconstructType(Types.NUMERIC, SqlParameter.precision(10, 2)));
	}
	
	@Test
	void reconstructParameterizedDecimal() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.DECIMAL.configure(SqlParameter.precision(18, 4)), reconstructType(Types.DECIMAL, SqlParameter.precision(18, 4)));
	}
	
	@Test
	void reconstructParameterizedChar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FIXED_STRING.configure(SqlParameter.length(8)), reconstructType(Types.CHAR, SqlParameter.length(8)));
	}
	
	@Test
	void reconstructParameterizedNChar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_FIXED_STRING.configure(SqlParameter.length(8)), reconstructType(Types.NCHAR, SqlParameter.length(8)));
	}
	
	@Test
	void reconstructParameterizedNVarchar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.UNICODE_STRING.configure(SqlParameter.length(50)), reconstructType(Types.NVARCHAR, SqlParameter.length(50)));
	}
	
	@Test
	void reconstructParameterizedBinary() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.FIXED_BYTES.configure(SqlParameter.length(16)), reconstructType(Types.BINARY, SqlParameter.length(16)));
	}
	
	@Test
	void reconstructParameterizedVarbinary() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.BYTES.configure(SqlParameter.length(64)), reconstructType(Types.VARBINARY, SqlParameter.length(64)));
	}
	
	@Test
	void reconstructParameterizedTime() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_TIME.configure(SqlParameter.fractional(3)), reconstructType(Types.TIME, SqlParameter.fractional(3)));
	}
	
	@Test
	void reconstructParameterizedTimestamp() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6)), reconstructType(Types.TIMESTAMP, SqlParameter.fractional(6)));
	}
	
	@Test
	void reconstructParameterizedTimeWithTimezone() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.OFFSET_TIME.configure(SqlParameter.fractional(3)), reconstructType(Types.TIME_WITH_TIMEZONE, SqlParameter.fractional(3)));
	}
	
	@Test
	void reconstructParameterizedTimestampWithTimezone() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.OFFSET_DATE_TIME.configure(SqlParameter.fractional(6)), reconstructType(Types.TIMESTAMP_WITH_TIMEZONE, SqlParameter.fractional(6)));
	}
	
	@Test
	void mapRoundTripThroughReconstructForScalar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.INTEGER, mapJdbcType(Types.INTEGER, 0, 0));
		assertEquals(SqlTypes.INTEGER, reconstructType(Types.INTEGER, null));
	}
	
	@Test
	void mapAndReconstructAgreeForParameterizedVarchar() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.STRING.configure(SqlParameter.length(50)), mapJdbcType(Types.VARCHAR, 50, 0));
		assertEquals(mapJdbcType(Types.VARCHAR, 50, 0), reconstructType(Types.VARCHAR, SqlParameter.length(50)));
	}
	
	@Test
	void mapNumericLargePrecisionPassesThroughUnclamped() throws SqlSchemaIntrospectionException {
		assertEquals(SqlTypes.NUMERIC.configure(SqlParameter.precision(38, 10)), mapJdbcType(Types.NUMERIC, 38, 10));
	}
}
