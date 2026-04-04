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

import net.luis.utils.function.throwable.ThrowableBiFunction;
import net.luis.utils.io.database.exception.SqlValueReadOnlyException;
import net.luis.utils.io.database.exception.SqlValueRetrievalException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

record SqlReadValue(
	int columnIndex,
	@NonNull ResultSet resultSet
) implements SqlValue {
	
	SqlReadValue {
		Objects.requireNonNull(resultSet, "Result set must not be null");
		
		try {
			if (resultSet.getMetaData().getColumnCount() >= columnIndex || columnIndex < 0) {
				throw new IllegalArgumentException("Column index must be between 0 and the column count of the result set");
			}
		} catch (SQLException e) {
			throw new IllegalStateException("Failed to retrieve metadata information from the result set", e);
		}
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public @Nullable Object getRawObject() {
		throw new UnsupportedOperationException("Raw object retrieval is not supported for read values");
	}
	
	@Override
	public int getJdbcType() {
		throw new UnsupportedOperationException("JDBC type retrieval is not supported for read values");
	}
	
	private <T> @Nullable T get(@NonNull String dataType, @NonNull ThrowableBiFunction<ResultSet, Integer, T, SQLException> getter) throws SqlValueRetrievalException {
		Objects.requireNonNull(dataType, "Data type description must not be null");
		Objects.requireNonNull(getter, "Getter function must not be null");
		
		try {
			T value = getter.apply(this.resultSet, this.columnIndex);
			return this.resultSet.wasNull() ? null : value;
		} catch (SQLException e) {
			throw new SqlValueRetrievalException("Failed to retrieve value of type " + dataType + " from column index " + this.columnIndex, e);
		}
	}
	
	@Override
	public @Nullable Boolean getBoolean() throws SqlValueRetrievalException {
		return this.get("boolean", ResultSet::getBoolean);
	}
	
	@Override
	public void setBoolean(@Nullable Boolean value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Byte getByte() throws SqlValueRetrievalException {
		return this.get("byte", ResultSet::getByte);
	}
	
	@Override
	public void setByte(@Nullable Byte value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Short getShort() throws SqlValueRetrievalException {
		return this.get("short", ResultSet::getShort);
	}
	
	@Override
	public void setShort(@Nullable Short value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Integer getInteger() throws SqlValueRetrievalException {
		return this.get("integer", ResultSet::getInt);
	}
	
	@Override
	public void setInteger(@Nullable Integer value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Long getLong() throws SqlValueRetrievalException {
		return this.get("long", ResultSet::getLong);
	}
	
	@Override
	public void setLong(@Nullable Long value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Float getFloat() throws SqlValueRetrievalException {
		return this.get("float", ResultSet::getFloat);
	}
	
	@Override
	public void setFloat(@Nullable Float value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Double getDouble() throws SqlValueRetrievalException {
		return this.get("double", ResultSet::getDouble);
	}
	
	@Override
	public void setDouble(@Nullable Double value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable BigDecimal getBigDecimal() throws SqlValueRetrievalException {
		return this.get("big decimal", ResultSet::getBigDecimal);
	}
	
	@Override
	public void setBigDecimal(@Nullable BigDecimal value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable String getString() throws SqlValueRetrievalException {
		return this.get("string", ResultSet::getString);
	}
	
	@Override
	public void setString(@Nullable String value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Clob getClob() throws SqlValueRetrievalException {
		return this.get("clob", ResultSet::getClob);
	}
	
	@Override
	public void setClob(@Nullable Clob value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Reader getCharacterStream() throws SqlValueRetrievalException {
		return this.get("character stream", ResultSet::getCharacterStream);
	}
	
	@Override
	public void setCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable NClob getNClob() throws SqlValueRetrievalException {
		return this.get("nclob", ResultSet::getNClob);
	}
	
	@Override
	public void setNClob(@Nullable NClob value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Reader getNCharacterStream() throws SqlValueRetrievalException {
		return this.get("n character stream", ResultSet::getNCharacterStream);
	}
	
	@Override
	public void setNCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public byte @Nullable [] getBytes() throws SqlValueRetrievalException {
		return this.get("bytes", ResultSet::getBytes);
	}
	
	@Override
	public void setBytes(byte @Nullable [] value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable Blob getBlob() throws SqlValueRetrievalException {
		return this.get("blob", ResultSet::getBlob);
	}
	
	@Override
	public void setBlob(@Nullable Blob value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable InputStream getBinaryStream() throws SqlValueRetrievalException {
		return this.get("binary stream", ResultSet::getBinaryStream);
	}
	
	@Override
	public void setBinaryStream(@Nullable InputStream value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable LocalDate getLocalDate() throws SqlValueRetrievalException {
		return this.get("local date", (result, index) -> result.getObject(index, LocalDate.class));
	}
	
	@Override
	public void setLocalDate(@Nullable LocalDate value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable LocalTime getLocalTime() throws SqlValueRetrievalException {
		return this.get("local time", (result, index) -> result.getObject(index, LocalTime.class));
	}
	
	@Override
	public void setLocalTime(@Nullable LocalTime value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException {
		return this.get("local date time", (result, index) -> result.getObject(index, LocalDateTime.class));
	}
	
	@Override
	public void setLocalDateTime(@Nullable LocalDateTime value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException {
		return this.get("offset time", (result, index) -> result.getObject(index, OffsetTime.class));
	}
	
	@Override
	public void setOffsetTime(@Nullable OffsetTime value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
	
	@Override
	public @Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException {
		return this.get("offset date time", (result, index) -> result.getObject(index, OffsetDateTime.class));
	}
	
	@Override
	public void setOffsetDateTime(@Nullable OffsetDateTime value) throws SqlValueReadOnlyException {
		throw new SqlValueReadOnlyException("SqlReadValue is read-only");
	}
}
