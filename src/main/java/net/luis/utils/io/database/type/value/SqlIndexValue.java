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

record SqlIndexValue(
	int columnIndex,
	@NonNull ResultSet resultSet
) implements SqlValue {
	
	SqlIndexValue {
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
	public @NonNull ResultSet getRawResult() {
		return this.resultSet;
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
		return this.get("local date", (result, index) -> result.getObject(index, LocalDate.class));
	}
	
	@Override
	public @Nullable LocalTime getLocalTime() throws SqlValueRetrievalException {
		return this.get("local time", (result, index) -> result.getObject(index, LocalTime.class));
	}
	
	@Override
	public @Nullable LocalDateTime getLocalDateTime() throws SqlValueRetrievalException {
		return this.get("local date time", (result, index) -> result.getObject(index, LocalDateTime.class));
	}
	
	@Override
	public @Nullable OffsetTime getOffsetTime() throws SqlValueRetrievalException {
		return this.get("offset time", (result, index) -> result.getObject(index, OffsetTime.class));
	}
	
	@Override
	public @Nullable OffsetDateTime getOffsetDateTime() throws SqlValueRetrievalException {
		return this.get("offset date time", (result, index) -> result.getObject(index, OffsetDateTime.class));
	}
}
