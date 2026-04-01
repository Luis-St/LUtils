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

import net.luis.utils.io.database.exception.SqlMappingException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;

/**
 *
 * @author Luis-St
 *
 */

public sealed interface SqlDataType<T> permits SqlBuiltinDataType, SqlCustomDataType {
	
	SqlDataType<Boolean> BOOLEAN = new SqlBuiltinDataType<>(Boolean.class, Types.BOOLEAN, ResultSet::getBoolean, ResultSet::getBoolean);
	SqlDataType<Short> SHORT = new SqlBuiltinDataType<>(Short.class, Types.SMALLINT, ResultSet::getShort, ResultSet::getShort);
	SqlDataType<Integer> INTEGER = new SqlBuiltinDataType<>(Integer.class, Types.INTEGER, ResultSet::getInt, ResultSet::getInt);
	SqlDataType<Long> LONG = new SqlBuiltinDataType<>(Long.class, Types.BIGINT, ResultSet::getLong, ResultSet::getLong);
	SqlDataType<Float> FLOAT = new SqlBuiltinDataType<>(Float.class, Types.REAL, ResultSet::getFloat, ResultSet::getFloat);
	SqlDataType<Double> DOUBLE = new SqlBuiltinDataType<>(Double.class, Types.DOUBLE, ResultSet::getDouble, ResultSet::getDouble);
	SqlDataType<BigDecimal> BIG_DECIMAL = new SqlBuiltinDataType<>(BigDecimal.class, Types.DECIMAL, ResultSet::getBigDecimal, ResultSet::getBigDecimal);
	
	SqlDataType<String> FIXED_STRING = new SqlBuiltinDataType<>(String.class, Types.CHAR, ResultSet::getString, ResultSet::getString);
	SqlDataType<String> STRING = new SqlBuiltinDataType<>(String.class, Types.VARCHAR, ResultSet::getString, ResultSet::getString);
	SqlDataType<String> TEXT = new SqlBuiltinDataType<>(String.class, Types.LONGNVARCHAR, ResultSet::getString, ResultSet::getString);
	SqlDataType<Reader> TEXT_READER = new SqlBuiltinDataType<>(Reader.class, Types.LONGNVARCHAR, ResultSet::getCharacterStream, ResultSet::getCharacterStream);
	SqlDataType<Clob> CLOB = new SqlBuiltinDataType<>(Clob.class, Types.CLOB, ResultSet::getClob, ResultSet::getClob);
	SqlDataType<Reader> CLOB_READER = new SqlBuiltinDataType<>(Reader.class, Types.CLOB, ResultSet::getCharacterStream, ResultSet::getCharacterStream);
	SqlDataType<NClob> NCLOB = new SqlBuiltinDataType<>(NClob.class, Types.NCLOB, ResultSet::getNClob, ResultSet::getNClob);
	SqlDataType<Reader> NCLOB_READER = new SqlBuiltinDataType<>(Reader.class, Types.NCLOB, ResultSet::getNCharacterStream, ResultSet::getNCharacterStream);
	
	SqlDataType<byte[]> BYTES = new SqlBuiltinDataType<>(byte[].class, Types.BINARY, ResultSet::getBytes, ResultSet::getBytes);
	SqlDataType<byte[]> VAR_BYTES = new SqlBuiltinDataType<>(byte[].class, Types.VARBINARY, ResultSet::getBytes, ResultSet::getBytes);
	SqlDataType<byte[]> LONG_BYTES = new SqlBuiltinDataType<>(byte[].class, Types.LONGVARBINARY, ResultSet::getBytes, ResultSet::getBytes);
	SqlDataType<InputStream> LONG_BYTES_STREAM = new SqlBuiltinDataType<>(InputStream.class, Types.LONGVARBINARY, ResultSet::getBinaryStream, ResultSet::getBinaryStream);
	SqlDataType<Blob> BLOB = new SqlBuiltinDataType<>(Blob.class, Types.BLOB, ResultSet::getBlob, ResultSet::getBlob);
	SqlDataType<InputStream> BLOB_STREAM = new SqlBuiltinDataType<>(InputStream.class, Types.BLOB, ResultSet::getBinaryStream, ResultSet::getBinaryStream);
	
	SqlDataType<LocalDate> LOCAL_DATE = new SqlBuiltinDataType<>(LocalDate.class, Types.DATE, (set, index) -> set.getObject(index, LocalDate.class), (set, name) -> set.getObject(name, LocalDate.class));
	SqlDataType<LocalTime> LOCAL_TIME = new SqlBuiltinDataType<>(LocalTime.class, Types.TIME, (set, index) -> set.getObject(index, LocalTime.class), (set, name) -> set.getObject(name, LocalTime.class));
	SqlDataType<LocalDateTime> LOCAL_DATE_TIME = new SqlBuiltinDataType<>(LocalDateTime.class, Types.TIMESTAMP, (set, index) -> set.getObject(index, LocalDateTime.class), (set, name) -> set.getObject(name, LocalDateTime.class));
	SqlDataType<OffsetTime> OFFSET_TIME = new SqlBuiltinDataType<>(OffsetTime.class, Types.TIME_WITH_TIMEZONE, (set, index) -> set.getObject(index, OffsetTime.class), (set, name) -> set.getObject(name, OffsetTime.class));
	SqlDataType<OffsetDateTime> OFFSET_DATE_TIME = new SqlBuiltinDataType<>(OffsetDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE, (set, index) -> set.getObject(index, OffsetDateTime.class), (set, name) -> set.getObject(name, OffsetDateTime.class));
	
	@NonNull Class<T> javaType();
	
	int jdbcType();
	
	@Nullable T get(@NonNull ResultSet result, int columnIndex) throws SqlMappingException;
	
	@Nullable T get(@NonNull ResultSet result, @NonNull String columnName) throws SqlMappingException;
}
