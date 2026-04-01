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

record SqlColumnValue(
	@NonNull String columnName,
	@NonNull ResultSet resultSet
) implements SqlValue {
	
	SqlColumnValue {
		Objects.requireNonNull(columnName, "Column name must not be null");
		Objects.requireNonNull(resultSet, "Result set must not be null");
		
		if (columnName.isBlank()) {
			throw new IllegalArgumentException("Column name must not be empty or blank");
		}
		try {
			resultSet.findColumn(columnName);
		} catch (SQLException e) {
			throw new IllegalArgumentException("Column name must exist in the result set", e);
		}
	}
	
	@Override
	public @NonNull ResultSet getRawResult() {
		return this.resultSet;
	}
	
	private <T> @Nullable T get(@NonNull String dataType, @NonNull ThrowableBiFunction<ResultSet, String, T, SQLException> getter) throws SqlValueRetrievalException {
		Objects.requireNonNull(dataType, "Data type description must not be null");
		Objects.requireNonNull(getter, "Getter function must not be null");
		
		try {
			T value = getter.apply(this.resultSet, this.columnName);
			return this.resultSet.wasNull() ? null : value;
		} catch (SQLException e) {
			throw new SqlValueRetrievalException("Failed to retrieve value of type " + dataType + " from column '" + this.columnName + "'", e);
		}
	}
	
	@Override
	public @Nullable Boolean getBoolean() throws SqlValueRetrievalException {
		return this.get("boolean", ResultSet::getBoolean);
	}
	
	@Override
	public @Nullable Byte getByte() throws SqlValueRetrievalException {
		return this.get("byte", ResultSet::getByte);
	}
	
	@Override
	public @Nullable Short getShort() throws SqlValueRetrievalException {
		return this.get("short", ResultSet::getShort);
	}
	
	@Override
	public @Nullable Integer getInteger() throws SqlValueRetrievalException {
		return this.get("integer", ResultSet::getInt);
	}
	
	@Override
	public @Nullable Long getLong() throws SqlValueRetrievalException {
		return this.get("long", ResultSet::getLong);
	}
	
	@Override
	public @Nullable Float getFloat() throws SqlValueRetrievalException {
		return this.get("float", ResultSet::getFloat);
	}
	
	@Override
	public @Nullable Double getDouble() throws SqlValueRetrievalException {
		return this.get("double", ResultSet::getDouble);
	}
	
	@Override
	public @Nullable BigDecimal getBigDecimal() throws SqlValueRetrievalException {
		return this.get("big decimal", ResultSet::getBigDecimal);
	}
	
	@Override
	public @Nullable String getString() throws SqlValueRetrievalException {
		return this.get("string", ResultSet::getString);
	}
	
	@Override
	public @Nullable Clob getClob() throws SqlValueRetrievalException {
		return this.get("clob", ResultSet::getClob);
	}
	
	@Override
	public @Nullable Reader getCharacterStream() throws SqlValueRetrievalException {
		return this.get("character stream", ResultSet::getCharacterStream);
	}
	
	@Override
	public @Nullable NClob getNClob() throws SqlValueRetrievalException {
		return this.get("nclob", ResultSet::getNClob);
	}
	
	@Override
	public @Nullable Reader getNCharacterStream() throws SqlValueRetrievalException {
		return this.get("n character stream", ResultSet::getNCharacterStream);
	}
	
	@Override
	public byte @Nullable [] getBytes() throws SqlValueRetrievalException {
		return this.get("bytes", ResultSet::getBytes);
	}
	
	@Override
	public @Nullable Blob getBlob() throws SqlValueRetrievalException {
		return this.get("blob", ResultSet::getBlob);
	}
	
	@Override
	public @Nullable InputStream getBinaryStream() throws SqlValueRetrievalException {
		return this.get("binary stream", ResultSet::getBinaryStream);
	}
	
	@Override
	public @Nullable LocalDate getLocalDate() throws SqlValueRetrievalException {
		return this.get("local date", (result, name) -> result.getObject(name, LocalDate.class));
	}
	
	@Override
	public @Nullable LocalTime getLocalTime() throws SqlValueRetrievalException {
		return this.get("local time", (result, name) -> result.getObject(name, LocalTime.class));
	}
	
	@Override
	public @Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException {
		return this.get("local date time", (result, name) -> result.getObject(name, LocalDateTime.class));
	}
	
	@Override
	public @Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException {
		return this.get("offset time", (result, name) -> result.getObject(name, OffsetTime.class));
	}
	
	@Override
	public @Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException {
		return this.get("offset date time", (result, name) -> result.getObject(name, OffsetDateTime.class));
	}
}
