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

public sealed interface SqlValue permits SqlReadValue, SqlWriteValue {
	
	static @NonNull SqlValue ofRead(int columnIndex, @NonNull ResultSet resultSet) {
		return new SqlReadValue(columnIndex, resultSet);
	}
	
	static @NonNull SqlValue ofWrite() {
		return new SqlWriteValue();
	}
	
	static @NonNull SqlValue ofNull() {
		return new SqlWriteValue(false);
	}
	
	boolean isEmpty();
	
	@Nullable Object getRawObject();
	
	int getJdbcType();
	
	@Nullable Boolean getBoolean() throws SqlValueRetrievalException;
	
	void setBoolean(@Nullable Boolean value) throws SqlValueReadOnlyException;
	
	@Nullable Byte getByte() throws SqlValueRetrievalException;
	
	void setByte(@Nullable Byte value) throws SqlValueReadOnlyException;
	
	@Nullable Short getShort() throws SqlValueRetrievalException;
	
	void setShort(@Nullable Short value) throws SqlValueReadOnlyException;
	
	@Nullable Integer getInteger() throws SqlValueRetrievalException;
	
	void setInteger(@Nullable Integer value) throws SqlValueReadOnlyException;
	
	@Nullable Long getLong() throws SqlValueRetrievalException;
	
	void setLong(@Nullable Long value) throws SqlValueReadOnlyException;
	
	@Nullable Float getFloat() throws SqlValueRetrievalException;
	
	void setFloat(@Nullable Float value) throws SqlValueReadOnlyException;
	
	@Nullable Double getDouble() throws SqlValueRetrievalException;
	
	void setDouble(@Nullable Double value) throws SqlValueReadOnlyException;
	
	@Nullable BigDecimal getBigDecimal() throws SqlValueRetrievalException;
	
	void setBigDecimal(@Nullable BigDecimal value) throws SqlValueReadOnlyException;
	
	@Nullable String getString() throws SqlValueRetrievalException;
	
	void setString(@Nullable String value) throws SqlValueReadOnlyException;
	
	@Nullable Clob getClob() throws SqlValueRetrievalException;
	
	void setClob(@Nullable Clob value) throws SqlValueReadOnlyException;
	
	@Nullable Reader getCharacterStream() throws SqlValueRetrievalException;
	
	void setCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException;
	
	@Nullable NClob getNClob() throws SqlValueRetrievalException;
	
	void setNClob(@Nullable NClob value) throws SqlValueReadOnlyException;
	
	@Nullable Reader getNCharacterStream() throws SqlValueRetrievalException;
	
	void setNCharacterStream(@Nullable Reader value) throws SqlValueReadOnlyException;
	
	byte @Nullable [] getBytes() throws SqlValueRetrievalException;
	
	void setBytes(byte @Nullable [] value) throws SqlValueReadOnlyException;
	
	@Nullable Blob getBlob() throws SqlValueRetrievalException;
	
	void setBlob(@Nullable Blob value) throws SqlValueReadOnlyException;
	
	@Nullable InputStream getBinaryStream() throws SqlValueRetrievalException;
	
	void setBinaryStream(@Nullable InputStream value) throws SqlValueReadOnlyException;
	
	@Nullable LocalDate getLocalDate() throws SqlValueRetrievalException;
	
	void setLocalDate(@Nullable LocalDate value) throws SqlValueReadOnlyException;
	
	@Nullable LocalTime getLocalTime() throws SqlValueRetrievalException;
	
	void setLocalTime(@Nullable LocalTime value) throws SqlValueReadOnlyException;
	
	@Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException;
	
	void setLocalDateTime(@Nullable LocalDateTime value) throws SqlValueReadOnlyException;
	
	@Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException;
	
	void setOffsetTime(@Nullable OffsetTime value) throws SqlValueReadOnlyException;
	
	@Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException;
	
	void setOffsetDateTime(@Nullable OffsetDateTime value) throws SqlValueReadOnlyException;
}
