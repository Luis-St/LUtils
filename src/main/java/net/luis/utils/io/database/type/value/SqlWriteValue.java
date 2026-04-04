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

package net.luis.utils.io.database.type.value;

import net.luis.utils.io.database.exception.SqlValueReadOnlyException;
import net.luis.utils.io.database.exception.SqlValueRetrievalException;
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

final class SqlWriteValue implements SqlValue {
	
	private @Nullable Object value;
	private int jdbcType;
	private boolean empty = true;
	
	SqlWriteValue() {}
	
	SqlWriteValue(boolean empty) {
		this.empty = empty;
	}
	
	@Override
	public boolean isEmpty() {
		return this.empty;
	}
	
	@Override
	public @Nullable Object getRawObject() {
		return this.value;
	}
	
	@Override
	public int getJdbcType() {
		return this.jdbcType;
	}
	
	private <T> @Nullable T cast(@NonNull String typeName, @NonNull Class<T> type) throws SqlValueRetrievalException {
		if (this.value == null) {
			return null;
		}
		
		if (type.isInstance(this.value)) {
			return type.cast(this.value);
		}
		throw new SqlValueRetrievalException("Unable to retrieve value as " + typeName + ", actual type is " + this.value.getClass().getName());
	}
	
	@Override
	public @Nullable Boolean getBoolean() throws SqlValueRetrievalException {
		return this.cast("boolean", Boolean.class);
	}
	
	@Override
	public void setBoolean(@Nullable Boolean value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.BOOLEAN;
	}
	
	@Override
	public @Nullable Byte getByte() throws SqlValueRetrievalException {
		return this.cast("byte", Byte.class);
	}
	
	@Override
	public void setByte(@Nullable Byte value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.TINYINT;
	}
	
	@Override
	public @Nullable Short getShort() throws SqlValueRetrievalException {
		return this.cast("short", Short.class);
	}
	
	@Override
	public void setShort(@Nullable Short value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.SMALLINT;
	}
	
	@Override
	public @Nullable Integer getInteger() throws SqlValueRetrievalException {
		return this.cast("integer", Integer.class);
	}
	
	@Override
	public void setInteger(@Nullable Integer value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.INTEGER;
	}
	
	@Override
	public @Nullable Long getLong() throws SqlValueRetrievalException {
		return this.cast("long", Long.class);
	}
	
	@Override
	public void setLong(@Nullable Long value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.BIGINT;
	}
	
	@Override
	public @Nullable Float getFloat() throws SqlValueRetrievalException {
		return this.cast("float", Float.class);
	}
	
	@Override
	public void setFloat(@Nullable Float value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.REAL;
	}
	
	@Override
	public @Nullable Double getDouble() throws SqlValueRetrievalException {
		return this.cast("double", Double.class);
	}
	
	@Override
	public void setDouble(@Nullable Double value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.DOUBLE;
	}
	
	@Override
	public @Nullable BigDecimal getBigDecimal() throws SqlValueRetrievalException {
		return this.cast("big decimal", BigDecimal.class);
	}
	
	@Override
	public void setBigDecimal(@Nullable BigDecimal value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.DECIMAL;
	}
	
	@Override
	public @Nullable String getString() throws SqlValueRetrievalException {
		return this.cast("string", String.class);
	}
	
	@Override
	public void setString(@Nullable String value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.VARCHAR;
	}
	
	@Override
	public @Nullable Clob getClob() throws SqlValueRetrievalException {
		return this.cast("clob", Clob.class);
	}
	
	@Override
	public void setClob(@Nullable Clob value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.CLOB;
	}
	
	@Override
	public @Nullable Reader getCharacterStream() throws SqlValueRetrievalException {
		return this.cast("character stream", Reader.class);
	}
	
	@Override
	public void setCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.LONGNVARCHAR;
	}
	
	@Override
	public @Nullable NClob getNClob() throws SqlValueRetrievalException {
		return this.cast("nclob", NClob.class);
	}
	
	@Override
	public void setNClob(@Nullable NClob value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.NCLOB;
	}
	
	@Override
	public @Nullable Reader getNCharacterStream() throws SqlValueRetrievalException {
		return this.cast("n character stream", Reader.class);
	}
	
	@Override
	public void setNCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.NCLOB;
	}
	
	@Override
	public byte @Nullable [] getBytes() throws SqlValueRetrievalException {
		return this.cast("bytes", byte[].class);
	}
	
	@Override
	public void setBytes(byte @Nullable [] value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.BINARY;
	}
	
	@Override
	public @Nullable Blob getBlob() throws SqlValueRetrievalException {
		return this.cast("blob", Blob.class);
	}
	
	@Override
	public void setBlob(@Nullable Blob value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.BLOB;
	}
	
	@Override
	public @Nullable InputStream getBinaryStream() throws SqlValueRetrievalException {
		return this.cast("binary stream", InputStream.class);
	}
	
	@Override
	public void setBinaryStream(@Nullable InputStream value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.LONGVARBINARY;
	}
	
	@Override
	public @Nullable LocalDate getLocalDate() throws SqlValueRetrievalException {
		return this.cast("local date", LocalDate.class);
	}
	
	@Override
	public void setLocalDate(@Nullable LocalDate value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.DATE;
	}
	
	@Override
	public @Nullable LocalTime getLocalTime() throws SqlValueRetrievalException {
		return this.cast("local time", LocalTime.class);
	}
	
	@Override
	public void setLocalTime(@Nullable LocalTime value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.TIME;
	}
	
	@Override
	public @Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException {
		return this.cast("local date time", LocalDateTime.class);
	}
	
	@Override
	public void setLocalDateTime(@Nullable LocalDateTime value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.TIMESTAMP;
	}
	
	@Override
	public @Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException {
		return this.cast("offset time", OffsetTime.class);
	}
	
	@Override
	public void setOffsetTime(@Nullable OffsetTime value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.TIME_WITH_TIMEZONE;
	}
	
	@Override
	public @Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException {
		return this.cast("offset date time", OffsetDateTime.class);
	}
	
	@Override
	public void setOffsetDateTime(@Nullable OffsetDateTime value) throws SqlValueReadOnlyException {
		this.value = value;
		this.empty = false;
		this.jdbcType = Types.TIMESTAMP_WITH_TIMEZONE;
	}
}
