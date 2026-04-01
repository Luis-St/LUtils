package net.luis.utils.io.database.type.value;

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

public sealed interface SqlValue permits SqlColumnValue, SqlIndexValue {
	
	static @NonNull SqlValue ofIndexed(int columnIndex, @NonNull ResultSet resultSet) {
		return new SqlIndexValue(columnIndex, resultSet);
	}
	
	static @NonNull SqlValue ofNamed(@NonNull String columnName, @NonNull ResultSet resultSet) {
		return new SqlColumnValue(columnName, resultSet);
	}
	
	@NonNull ResultSet getRawResult();
	
	@Nullable Boolean getBoolean() throws SqlValueRetrievalException;
	
	@Nullable Byte getByte() throws SqlValueRetrievalException;
	
	@Nullable Short getShort() throws SqlValueRetrievalException;
	
	@Nullable Integer getInteger() throws SqlValueRetrievalException;
	
	@Nullable Long getLong() throws SqlValueRetrievalException;
	
	@Nullable Float getFloat() throws SqlValueRetrievalException;
	
	@Nullable Double getDouble() throws SqlValueRetrievalException;
	
	@Nullable BigDecimal getBigDecimal() throws SqlValueRetrievalException;
	
	@Nullable String getString() throws SqlValueRetrievalException;
	
	@Nullable Clob getClob() throws SqlValueRetrievalException;
	
	@Nullable Reader getCharacterStream() throws SqlValueRetrievalException;
	
	@Nullable NClob getNClob() throws SqlValueRetrievalException;
	
	@Nullable Reader getNCharacterStream() throws SqlValueRetrievalException;
	
	byte @Nullable [] getBytes() throws SqlValueRetrievalException;
	
	@Nullable Blob getBlob() throws SqlValueRetrievalException;
	
	@Nullable InputStream getBinaryStream() throws SqlValueRetrievalException;
	
	@Nullable LocalDate getLocalDate() throws SqlValueRetrievalException;
	
	@Nullable LocalTime getLocalTime() throws SqlValueRetrievalException;
	
	@Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException;
	
	@Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException;
	
	@Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException;
}
