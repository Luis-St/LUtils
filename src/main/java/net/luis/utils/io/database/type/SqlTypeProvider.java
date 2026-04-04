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

import net.luis.utils.annotation.type.Singleton;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.database.exception.SqlValueReadOnlyException;
import net.luis.utils.io.database.exception.SqlValueRetrievalException;
import net.luis.utils.io.database.type.value.SqlValue;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.function.Function;

/**
 *
 * @author Luis-St
 *
 */

@Singleton
public final class SqlTypeProvider implements TypeProvider<SqlValue> {
	
	public static final SqlTypeProvider INSTANCE = new SqlTypeProvider();
	
	private SqlTypeProvider() {}
	
	@Override
	public @NonNull SqlValue empty() {
		return SqlValue.ofWrite();
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createNull(@NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		return SqlValue.ofNull();
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createBoolean(boolean value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setBoolean(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create boolean sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createByte(byte value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setByte(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create byte sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createShort(short value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setShort(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create short sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createInteger(int value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setInteger(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create integer sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createLong(long value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setLong(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create long sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createFloat(float value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setFloat(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create float sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createDouble(double value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setDouble(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create double sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createBigDecimal(@Nullable BigDecimal value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a big decimal");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setBigDecimal(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create big decimal sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createString(@Nullable String value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setString(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create string sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createClob(@Nullable Clob value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a clob");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setClob(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create clob sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createCharacterStream(@Nullable Reader value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a character stream");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setCharacterStream(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create character stream sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createNClob(@Nullable NClob value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a nclob");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setNClob(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create nclob sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createNCharacterStream(@Nullable Reader value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a n character stream");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setNCharacterStream(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create n character stream sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createBytes(byte @Nullable [] value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte array");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setBytes(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create byte array sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createBlob(@Nullable Blob value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a blob");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setBlob(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create blob sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createBinaryStream(@Nullable InputStream value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a binary stream");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setBinaryStream(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create binary stream sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createLocalDate(@Nullable LocalDate value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local date");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setLocalDate(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create local date sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createLocalTime(@Nullable LocalTime value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local time");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setLocalTime(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create local time sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createLocalDateTime(@Nullable LocalDateTime value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local date time");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setLocalDateTime(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create local date time sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createOffsetTime(@Nullable OffsetTime value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not an offset time");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setOffsetTime(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create offset time sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull SqlValue createOffsetDateTime(@Nullable OffsetDateTime value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			throw exceptionConstructor.apply("Value 'null' is not an offset date time");
		}
		
		try {
			SqlValue sqlValue = SqlValue.ofWrite();
			sqlValue.setOffsetDateTime(value);
			return sqlValue;
		} catch (SqlValueReadOnlyException e) {
			throw exceptionConstructor.apply("Failed to create offset date time sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createList(@Nullable List<? extends SqlValue> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support list values");
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createMap(@NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> @NonNull SqlValue createMap(@Nullable Map<String, ? extends SqlValue> values, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> boolean isEmpty(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid sql value");
		}
		
		return type.isEmpty();
	}
	
	@Override
	public <X extends Exception> boolean isNull(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a valid sql value");
		}
		
		return !type.isEmpty() && type.getRawObject() == null;
	}
	
	@Override
	public <X extends Exception> @NonNull Boolean getBoolean(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a boolean");
		}
		
		try {
			Boolean value = type.getBoolean();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a boolean");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve boolean from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Byte getByte(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte");
		}
		
		try {
			Byte value = type.getByte();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a byte");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve byte from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Short getShort(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a short");
		}
		
		try {
			Short value = type.getShort();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a short");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve short from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Integer getInteger(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an integer");
		}
		
		try {
			Integer value = type.getInteger();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not an integer");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve integer from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Long getLong(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a long");
		}
		
		try {
			Long value = type.getLong();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a long");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve long from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Float getFloat(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a float");
		}
		
		try {
			Float value = type.getFloat();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a float");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve float from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull Double getDouble(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a double");
		}
		
		try {
			Double value = type.getDouble();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a double");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve double from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull BigDecimal getBigDecimal(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a big decimal");
		}
		
		try {
			BigDecimal value = type.getBigDecimal();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a big decimal");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve big decimal from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull String getString(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a string");
		}
		
		try {
			String value = type.getString();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a string");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve string from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull Clob getClob(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a clob");
		}
		
		try {
			Clob value = type.getClob();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a clob");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve clob from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull Reader getCharacterStream(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a character stream");
		}
		
		try {
			Reader value = type.getCharacterStream();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a character stream");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve character stream from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull NClob getNClob(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a nclob");
		}
		
		try {
			NClob value = type.getNClob();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a nclob");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve nclob from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull Reader getNCharacterStream(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a n character stream");
		}
		
		try {
			Reader value = type.getNCharacterStream();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a n character stream");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve n character stream from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> byte @NonNull [] getBytes(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a byte array");
		}
		
		try {
			byte[] value = type.getBytes();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a byte array");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve byte array from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull Blob getBlob(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a blob");
		}
		
		try {
			Blob value = type.getBlob();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a blob");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve blob from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull InputStream getBinaryStream(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a binary stream");
		}
		
		try {
			InputStream value = type.getBinaryStream();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a binary stream");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve binary stream from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull LocalDate getLocalDate(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local date");
		}
		
		try {
			LocalDate value = type.getLocalDate();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a local date");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve local date from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull LocalTime getLocalTime(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local time");
		}
		
		try {
			LocalTime value = type.getLocalTime();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a local time");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve local time from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull LocalDateTime getLocalDateTime(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not a local date time");
		}
		
		try {
			LocalDateTime value = type.getLocalDateTime();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not a local date time");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve local date time from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull OffsetTime getOffsetTime(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an offset time");
		}
		
		try {
			OffsetTime value = type.getOffsetTime();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not an offset time");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve offset time from sql value: " + e.getMessage());
		}
	}
	
	public <X extends Exception> @NonNull OffsetDateTime getOffsetDateTime(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (type == null) {
			throw exceptionConstructor.apply("Value 'null' is not an offset date time");
		}
		
		try {
			OffsetDateTime value = type.getOffsetDateTime();
			if (value == null) {
				throw exceptionConstructor.apply("Sql value is null, not an offset date time");
			}
			return value;
		} catch (SqlValueRetrievalException e) {
			throw exceptionConstructor.apply("Failed to retrieve offset date time from sql value: " + e.getMessage());
		}
	}
	
	@Override
	public <X extends Exception> @NonNull List<SqlValue> getList(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support list values");
	}
	
	@Override
	public <X extends Exception> @NonNull Map<String, SqlValue> getMap(@Nullable SqlValue type, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> boolean has(@Nullable SqlValue type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> @Nullable SqlValue get(@Nullable SqlValue type, @Nullable String key, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> void set(@Nullable SqlValue type, @Nullable String key, @Nullable SqlValue value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		throw exceptionConstructor.apply("Sql type provider does not support map values");
	}
	
	@Override
	public <X extends Exception> @UnknownNullability SqlValue merge(@Nullable SqlValue current, @Nullable SqlValue value, @NonNull Function<String, X> exceptionConstructor) throws X {
		Objects.requireNonNull(exceptionConstructor, "Exception constructor must not be null");
		if (value == null) {
			return current;
		}
		return value;
	}
}
